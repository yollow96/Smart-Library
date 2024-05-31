package tesapp;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import tesapp.config.Dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HistoryBorrowed {
    private ObservableList<BorrowHistory> borrowHistoryList = FXCollections.observableArrayList();
    private String borrower;
    private final App app;
    private static final Logger logger = Logger.getLogger(HistoryBorrowed.class.getName());

    // Konstruktor untuk kelas HistoryBorrowed
    public HistoryBorrowed(App app) {
        this.app = app;
        // Inisialisasi daftar riwayat peminjaman dengan daftar kosong
        borrowHistoryList = FXCollections.observableArrayList();
    }

    // Metode untuk membuat Scene riwayat peminjaman
    @SuppressWarnings("unchecked")
    public Scene createScene() {
        TableView<BorrowHistory> borrowHistoryTable = new TableView<>(borrowHistoryList);

        TableColumn<BorrowHistory, String> borrowerColumn = new TableColumn<>("Peminjam");
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrower"));
        borrowerColumn.setPrefWidth(150);

        TableColumn<BorrowHistory, String> bookTitleColumn = new TableColumn<>("Nama Buku");
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookTitleColumn.setPrefWidth(250);

        TableColumn<BorrowHistory, String> borrowDateColumn = new TableColumn<>("Tanggal Peminjaman");
        borrowDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getBorrowDate().format(DateTimeFormatter.ISO_DATE)));
        borrowDateColumn.setPrefWidth(150);

        TableColumn<BorrowHistory, String> dueDateColumn = new TableColumn<>("Tanggal Pengembalian");
        dueDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDueDate().format(DateTimeFormatter.ISO_DATE)));
        dueDateColumn.setPrefWidth(150);

        borrowHistoryTable.getColumns().addAll(borrowerColumn, bookTitleColumn, borrowDateColumn, dueDateColumn);
        borrowHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //Pesan tersebut menunjukkan bahwa TableView.CONSTRAINED_RESIZE_POLICY telah 
        //tidak direkomendasikan untuk digunakan (deprecated) sejak versi JavaFX 20. 

        

        BorderPane root = new BorderPane();
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.getChildren().addAll(borrowHistoryTable);

        // Membuat tombol kembali
        Button btnBack = new Button("Kembali");
        btnBack.setId("btnBack"); // Mengatur ID untuk tombol
        btnBack.setOnAction((ActionEvent event) -> app.showAppScene());

        vbox.getChildren().add(btnBack);

        root.setCenter(vbox);

        // Set the background image for the VBox
        String imagePath = getClass().getResource("/gambar/background3.jpg").toExternalForm();
        BackgroundImage backgroundImage = new BackgroundImage(new Image(imagePath), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        vbox.setBackground(new Background(backgroundImage));

        // Mendapatkan ukuran layar penuh
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        Scene scene = new Scene(root, screenWidth, screenHeight);
        scene.getStylesheets().add(getClass().getResource("/style_history.css").toExternalForm()); // Apply CSS file
        
        return scene;
    }

    // Metode untuk menetapkan peminjam dan memperbarui riwayat peminjaman
    public void setBorrower(String borrower) {
        this.borrower = borrower;
        refreshHistory();
    }

    // Metode untuk memperbarui daftar riwayat peminjaman
    private void refreshHistory() {
        borrowHistoryList.clear();
        List<BorrowHistory> history = fetchAllBorrowHistory(borrower);
        if (history != null) {
            borrowHistoryList.addAll(history);
        }
    }

    // Metode untuk mengambil semua riwayat peminjaman dari database
    private List<BorrowHistory> fetchAllBorrowHistory(String borrower) {
        List<BorrowHistory> historyList = new ArrayList<>();
        String query = "SELECT borrower, book_title, borrow_date, due_date FROM borrow_history WHERE borrower = ?";
        try (Connection connection = Dbconnect.connectBorrowHistory();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, borrower);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String borrowerName = resultSet.getString("borrower");
                    String bookTitle = resultSet.getString("book_title");

                    long borrowDateMillis = resultSet.getLong("borrow_date");
                    long dueDateMillis = resultSet.getLong("due_date");

                    LocalDate borrowLocalDate = Instant.ofEpochMilli(borrowDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate dueLocalDate = Instant.ofEpochMilli(dueDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();

                    logger.info("Retrieved: borrower=" + borrowerName + ", book_title=" + bookTitle +
                            ", borrow_date=" + borrowLocalDate + ", due_date=" + dueLocalDate);

                    historyList.add(new BorrowHistory(borrowerName, bookTitle, borrowLocalDate, dueLocalDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList;
    }
}
