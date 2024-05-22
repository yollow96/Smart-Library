package tesapp;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import tesapp.config.Dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryBorrowed {
    private ObservableList<BorrowHistory> borrowHistoryList = FXCollections.observableArrayList();
    private String borrower;
    private final App app;

    // Konstruktor untuk kelas HistoryBorrowed
    public HistoryBorrowed(App app) {
        this.app = app;
        // Inisialisasi daftar riwayat peminjaman dengan daftar kosong
        borrowHistoryList = FXCollections.observableArrayList();
    }

    // Metode untuk membuat Scene riwayat peminjaman
    @SuppressWarnings("unchecked")
    public Scene createScene() {
        // Membuat tabel untuk menampilkan riwayat peminjaman
        TableView<BorrowHistory> borrowHistoryTable = new TableView<>(borrowHistoryList);
        borrowHistoryTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        borrowHistoryTable.setPrefWidth(600);

        // Membuat kolom untuk peminjam
        TableColumn<BorrowHistory, String> borrowerColumn = new TableColumn<>("Peminjam");
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrower"));

        // Membuat kolom untuk judul buku
        TableColumn<BorrowHistory, String> bookTitleColumn = new TableColumn<>("Nama Buku");
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));

        // Membuat kolom untuk tanggal peminjaman
        TableColumn<BorrowHistory, String> borrowDateColumn = new TableColumn<>("Tanggal Peminjaman");
        borrowDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getBorrowDate().format(DateTimeFormatter.ISO_DATE)));

        // Membuat kolom untuk tanggal pengembalian
        TableColumn<BorrowHistory, String> dueDateColumn = new TableColumn<>("Tanggal Pengembalian");
        dueDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDueDate().format(DateTimeFormatter.ISO_DATE)));

        // Menambahkan semua kolom ke dalam tabel
        borrowHistoryTable.getColumns().addAll(borrowerColumn, bookTitleColumn, borrowDateColumn, dueDateColumn);

        // Membuat root pane dengan VBox untuk tata letak
        BorderPane root = new BorderPane();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(borrowHistoryTable);

        // Membuat tombol kembali
        Button btnBack = new Button("Kembali");
        btnBack.setOnAction((ActionEvent event) -> app.showAppScene());

        vbox.getChildren().add(btnBack);

        root.setCenter(vbox);

        return new Scene(root, 800, 400);
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
                    LocalDate borrowDate = resultSet.getDate("borrow_date").toLocalDate();
                    LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                    historyList.add(new BorrowHistory(borrowerName, bookTitle, borrowDate, dueDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList;
    }
}
