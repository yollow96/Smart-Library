package tesapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tesapp.config.Dbconnect;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AppScene {
    private final App app;
    private final ObservableList<Book> availableBooks;
    private final ObservableList<Book> borrowedBooks;
    private final Stage primaryStage;
    private ListView<Book> bookListView;
    private TextField titleField;
    private TextField authorField;
    private ComboBox<String> categoryComboBox;
    private TextField searchField;

    public AppScene(App app, ObservableList<Book> availableBooks, ObservableList<Book> borrowedBooks, Stage primaryStage) {
        this.app = app;
        this.availableBooks = availableBooks;
        this.borrowedBooks = borrowedBooks;
        this.primaryStage = primaryStage;
    }

    public Scene createAppScene() {
        BorderPane appRoot = new BorderPane();
        appRoot.setPadding(new Insets(20));

        // Daftar buku tersedia
        bookListView = new ListView<>(availableBooks);
        bookListView.setPrefHeight(700);
        bookListView.setPrefWidth(700);
        bookListView.setCellFactory(param -> new BookListCell());

        // Membungkus ListView dalam ScrollPane untuk mendukung scrolling
        ScrollPane scrollPane = new ScrollPane(bookListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Field input judul buku
        titleField = new TextField();
        titleField.setPromptText("Judul");
        titleField.getStyleClass().add("judul-field");
        titleField.setMaxWidth(300);

        // Field input penulis buku
        authorField = new TextField();
        authorField.setPromptText("Penulis");
        authorField.getStyleClass().add("penulis-field");
        authorField.setMaxWidth(300);

        // ComboBox untuk kategori buku
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Kategori");
        categoryComboBox.setMaxWidth(300);
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
                "Horror"
        );

        // Field pencarian buku
        searchField = new TextField();
        searchField.setPromptText("Cari berdasarkan Judul/Penulis/Kategori");
        searchField.getStyleClass().add("text-field");
        searchField.setMaxWidth(300);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBooks(newValue);
        });

        // Membuat tombol untuk menambah buku
        Button addButton = new Button("Tambah");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(e -> addBook());

        // Membuat tombol untuk menghapus buku
        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("button");
        deleteButton.setOnAction(e -> deleteBook());

        // Membuat tombol untuk meminjam buku
        Button borrowButton = new Button("Pinjam");
        borrowButton.getStyleClass().add("button");
        borrowButton.setOnAction(e -> borrowBook());

        // Membuat tombol untuk mengembalikan buku
        Button returnButton = new Button("Kembalikan");
        returnButton.getStyleClass().add("button");
        returnButton.setOnAction(e -> returnBook());

        // Membuat tombol untuk melihat riwayat peminjaman
        Button historyButton = new Button("Riwayat Peminjaman");
        historyButton.getStyleClass().add("button");
        historyButton.setOnAction(e -> showHistoryScene(primaryStage));

        // Membuat tombol untuk keluar aplikasi
        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("button");
        exitButton.setOnAction(e -> {
            showAlert("Terima kasih telah menggunakan aplikasi.");
            app.showLoginScene();
        });
        
        // Membuat elemen-elemen HBox dan VBox
        HBox buttonBox = new HBox(10, borrowButton, returnButton, historyButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox adminBox = new HBox(10, addButton, deleteButton);
        adminBox.setAlignment(Pos.CENTER);

        VBox inputBox = new VBox(10, titleField, authorField, categoryComboBox);
        inputBox.setAlignment(Pos.CENTER);

        VBox leftBox = new VBox(10, new Label("Daftar Buku Tersedia"), scrollPane, searchField);
        leftBox.setSpacing(10);
        leftBox.setAlignment(Pos.CENTER);

        // Membuat VBox untuk menggabungkan inputBox dan adminBox di bawah buttonBox
        VBox rightBox = new VBox(10);
        rightBox.getChildren().addAll(buttonBox, inputBox, adminBox);
        rightBox.setAlignment(Pos.CENTER);

        // Mengatur tata letak menggunakan BorderPane
        appRoot.setLeft(leftBox);
        appRoot.setCenter(rightBox);

        appRoot.setStyle("-fx-background-color: #f7f7f7;");
        BorderPane.setAlignment(leftBox, Pos.CENTER);
        BorderPane.setAlignment(rightBox, Pos.CENTER);
        
        // Menampilkan tombol dan field hanya jika pengguna adalah admin
        if (!app.getCurrentUser().equals("admin")) {
            addButton.setVisible(false);
            deleteButton.setVisible(false);
            titleField.setVisible(false);
            authorField.setVisible(false);
            categoryComboBox.setVisible(false);
        }


        Scene scene = new Scene(appRoot, 1440, 800);
        scene.getStylesheets().add(getClass().getResource("/style_appscene.css").toExternalForm()); // Apply CSS file

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        Image image = new Image("gambar/backgroundAppScene.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        appRoot.setBackground(new Background(backgroundImage));

        return scene;
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

            setBackground(dialog.getDialogPane(), "/gambar/alert.jpg");
        

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String inputDate = result.get();
                try {
                    LocalDate borrowDate = LocalDate.parse(inputDate, DateTimeFormatter.ISO_DATE);
                    LocalDate dueDate = borrowDate.plusDays(7); // Tenggat waktu peminjaman adalah 7 hari
                    setBackground(dialog.getDialogPane(), "/gambar/alert.jpg");

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

        setBackground(dialog.getDialogPane(), "/gambar/alert.jpg");



        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            // Lakukan pengembalian buku dan simpan perubahan ke database
            if (Dbconnect.returnBook(book.getTitle(), app.getCurrentUser())) {
                borrowedBooks.remove(book);
                availableBooks.add(book);
                showAlert("Buku '" + book.getTitle() + "' berhasil dikembalikan.");
            } else {
                showAlert("User bukan yang meminjam buku tersebut.");
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
        categoryComboBox.setValue(null);
    }

    // Metode untuk memfilter daftar buku berdasarkan input pencarian
    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            bookListView.setItems(availableBooks);
        } else {
            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
            for (Book book : availableBooks) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                        book.getCategory().toLowerCase().contains(query.toLowerCase())) {
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
        return new Scene(loginRoot, 1440, 800);
    }

    // Metode untuk menampilkan pesan peringatan/informasi
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Image image = new Image("gambar/alert.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        alert.getDialogPane().setBackground(new Background(backgroundImage));
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    // Metode untuk menampilkan background
    private void setBackground(Node node, String imagePath) {
        // Memuat gambar
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        // Membuat BackgroundImage menggunakan gambar
        BackgroundImage backgroundImage = new BackgroundImage(image, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            new BackgroundSize(1, 1, true, true, false, false));
        // Mengatur gambar latar belakang untuk node
        if (node instanceof Region) {
            ((Region) node).setBackground(new Background(backgroundImage));
        }

        // Mengatur latar belakang untuk header label jika ada
        if (node instanceof DialogPane) {
            Node header = ((DialogPane) node).lookup(".header-panel");
            if (header instanceof Region) {
                ((Region) header).setBackground(new Background(backgroundImage));
            }
        }
    }
}