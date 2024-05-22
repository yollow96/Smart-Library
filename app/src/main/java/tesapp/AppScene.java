package tesapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tesapp.config.Dbconnect;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AppScene {
    private final App app;
    private final ObservableList<Book> availableBooks;
    private final ObservableList<Book> borrowedBooks;
    private ListView<Book> bookListView;
    private TextField titleField;
    private TextField authorField;
    private ComboBox<String> categoryComboBox;
    private TextField searchField;

    public AppScene(App app, ObservableList<Book> availableBooks, ObservableList<Book> borrowedBooks) {
        this.app = app;
        this.availableBooks = availableBooks;
        this.borrowedBooks = borrowedBooks;
    }

    public Scene createAppScene() {
        BorderPane appRoot = new BorderPane();
        appRoot.setPadding(new Insets(20));

        // Daftar buku tersedia
        bookListView = new ListView<>(availableBooks);
        bookListView.setPrefHeight(300);
        bookListView.setCellFactory(param -> new BookListCell());

        // Membungkus ListView dalam ScrollPane untuk mendukung scrolling
        ScrollPane scrollPane = new ScrollPane(bookListView);
        scrollPane.setFitToWidth(true);

        // Field input judul buku
        titleField = new TextField();
        titleField.setPromptText("Judul");

        // Field input penulis buku
        authorField = new TextField();
        authorField.setPromptText("Penulis");

        // ComboBox untuk kategori buku
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Kategori");
        categoryComboBox.getItems().addAll(
                "Programming",
                "Computer Science",
                "Fiction",
                "Non-fiction",
                "History",
                "Biography",
                "Romance",
                "Action",
                "Comedy",
                "Thriller",
                "Science",
                "Fantasy",
                "Mystery",
                "Horror");

        // Field pencarian buku
        searchField = new TextField();
        searchField.setPromptText("Cari berdasarkan Judul/Penulis/Kategori");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBooks(newValue);
        });

        // Tombol untuk menambah buku
        Button addButton = new Button("Tambah");
        addButton.setOnAction(e -> addBook());

        // Tombol untuk menghapus buku
        Button deleteButton = new Button("Hapus");
        deleteButton.setOnAction(e -> deleteBook());

        // Tombol untuk meminjam buku
        Button borrowButton = new Button("Pinjam");
        borrowButton.setOnAction(e -> borrowBook());

        // Tombol untuk mengembalikan buku
        Button returnButton = new Button("Kembalikan");
        returnButton.setOnAction(e -> returnBook());

        // Tombol untuk melihat riwayat peminjaman
        Button historyButton = new Button("Riwayat Peminjaman");
        historyButton.setOnAction(e -> showHistoryScene((Stage) appRoot.getScene().getWindow()));

        // Tombol untuk keluar aplikasi
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            showAlert("Terima kasih telah menggunakan aplikasi.");
            app.showLoginScene();
        });

        // Gambar buku
        ImageView imageView = new ImageView("gambar/gambarbuku1.jpg");
        imageView.setFitWidth(475);
        imageView.setFitHeight(215);

        VBox button2box = new VBox(10);
        button2box.getChildren().addAll(imageView, new HBox(10, addButton, deleteButton, borrowButton, returnButton, historyButton , exitButton));

        VBox inputBox = new VBox(10);
        inputBox.getChildren().addAll(titleField, authorField, categoryComboBox, button2box);

        appRoot.setLeft(new VBox(10, new Label("Daftar Buku Tersedia"), scrollPane, searchField));
        appRoot.setCenter(inputBox);
        appRoot.setStyle("-fx-background-color: #f7f7f7;");

        return new Scene(appRoot, 800, 400);
    }

    // Metode untuk menambahkan buku baru
    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryComboBox.getValue();

        if (!title.isEmpty() && !author.isEmpty() && category != null) {
            // Tambahkan buku ke database
            if (Dbconnect.addBook(title, author, category)) {
                Book newBook = new Book(title, author, category);
                availableBooks.add(newBook);
                clearFields();
            } else {
                showAlert("Gagal menambahkan buku ke database.");
            }
        } else {
            showAlert("Harap lengkapi semua informasi buku.");
        }
    }

    // Metode untuk menghapus buku yang dipilih
    private void deleteBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Hapus buku dari database
            if (Dbconnect.deleteBook(selectedBook.getTitle())) {
                availableBooks.remove(selectedBook);
            } else {
                showAlert("Gagal menghapus buku dari database.");
            }
        } else {
            showAlert("Pilih buku yang ingin dihapus.");
        }
    }

    // Metode untuk meminjam buku yang dipilih
    private void borrowBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook != null && !selectedBook.isBorrowed()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tanggal Peminjaman");
            dialog.setHeaderText("Masukkan tanggal peminjaman (Format: YYYY-MM-DD)");
            dialog.setContentText("Tanggal Peminjaman:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String inputDate = result.get();
                try {
                    LocalDate borrowDate = LocalDate.parse(inputDate, DateTimeFormatter.ISO_DATE);
                    LocalDate dueDate = borrowDate.plusDays(7); // Tenggat waktu peminjaman adalah 7 hari

                    // Lakukan peminjaman buku dan simpan perubahan ke database
                    if (Dbconnect.borrowBook(selectedBook.getTitle(), app.getCurrentUser(), borrowDate, dueDate)) {
                        selectedBook.setBorrowed(true);
                        selectedBook.setBorrowDate(borrowDate);
                        selectedBook.setBorrower(app.getCurrentUser()); // Atur peminjam dengan pengguna saat ini
                        showAlert("Buku '" + selectedBook.getTitle() + "' berhasil dipinjam.\nTenggat waktu peminjaman: " + dueDate);

                        availableBooks.remove(selectedBook);
                        borrowedBooks.add(selectedBook);
                        Dbconnect.addBorrowHistory(selectedBook.getBorrower(), selectedBook.getTitle(), borrowDate.toString(), dueDate.toString());
                    } else {
                        showAlert("Gagal melakukan peminjaman buku.");
                    }
                } catch (Exception ex) {
                    showAlert("Format tanggal tidak valid. Harap masukkan tanggal dengan format YYYY-MM-DD.");
                }
            }
        } else if (selectedBook == null) {
            showAlert("Pilih buku yang tersedia untuk dipinjam.");
        } else {
            showAlert("Buku sudah dipinjam.");
        }
    }

    // Metode untuk mengembalikan buku yang dipinjam
    private void returnBook() {
        if (!borrowedBooks.isEmpty()) {
            // Tampilkan dialog untuk memilih buku yang ingin dikembalikan
            ChoiceDialog<Book> dialog = new ChoiceDialog<>(borrowedBooks.get(0), borrowedBooks);
            dialog.setTitle("Pengembalian Buku");
            dialog.setHeaderText("Pilih buku yang ingin dikembalikan:");

            Optional<Book> result = dialog.showAndWait();
            result.ifPresent(book -> {
                // Lakukan pengembalian buku dan simpan perubahan ke database
                if (Dbconnect.returnBook(book.getTitle(), app.getCurrentUser())) {
                    borrowedBooks.remove(book);
                    availableBooks.add(book);
                    showAlert("Buku '" + book.getTitle() + "' berhasil dikembalikan.");
                } else {
                    showAlert("Gagal melakukan pengembalian buku.");
                }
            });
        } else {
            showAlert("Belum ada buku yang dipinjam.");
        }
    }

    // Metode untuk membersihkan field input
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    // Metode untuk memfilter buku berdasarkan kata kunci pencarian
    private void filterBooks(String keyword) {
        if (keyword.isEmpty()) {
            bookListView.setItems(availableBooks);
        } else {
            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
            for (Book book : availableBooks) {
                if (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) || book.getCategory().toLowerCase().contains(keyword.toLowerCase()) || book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
            bookListView.setItems(filteredBooks);
        }
    }

    // Metode untuk menampilkan halaman riwayat peminjaman
    private void showHistoryScene(Stage stage) {
        HistoryBorrowed historyBorrowed = new HistoryBorrowed(app);
        historyBorrowed.setBorrower(app.getCurrentUser());
        Scene historyScene = historyBorrowed.createScene();
        stage.setScene(historyScene);
        stage.setTitle("Smart Library");
        stage.show();
    }

    // Metode untuk menampilkan halaman kembali ke login
    public Scene createReturnToLoginScene() {
        BorderPane loginRoot = new BorderPane();
        loginRoot.setPadding(new Insets(20));

        Button loginButton = new Button("Kembali");
        loginButton.setOnAction(e -> app.showLoginScene());

        VBox loginBox = new VBox(10);
        loginBox.getChildren().addAll(new HBox(10, loginButton));

        loginRoot.setCenter(loginBox);
        return new Scene(loginRoot, 300, 200);
    }

    // Metode untuk menampilkan pesan peringatan/informasi
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
