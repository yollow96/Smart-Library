package tesapp.config;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import tesapp.Book;
import tesapp.BorrowHistory;

public class Dbconnect {
    private static final String DB_URL_USER = "jdbc:sqlite:db/db_user.db";
    private static final String DB_URL_BOOK = "jdbc:sqlite:db/db_book.db";
    private static final String DB_URL_BORROW_HISTORY = "jdbc:sqlite:db/db_borrow_history.db";

    // Fungsi untuk menghubungkan ke database pengguna
    public static Connection connectUser() throws SQLException {
        return DriverManager.getConnection(DB_URL_USER);
    }

    // Fungsi untuk menghubungkan ke database buku
    public static Connection connectBook() throws SQLException {
        return DriverManager.getConnection(DB_URL_BOOK);
    }

    // Fungsi untuk menghubungkan ke database riwayat peminjaman
    public static Connection connectBorrowHistory() throws SQLException {
        return DriverManager.getConnection(DB_URL_BORROW_HISTORY);
    }

    // Fungsi untuk validasi login
    public static boolean validateLogin(String userName, String password) {
        String query = "SELECT user_name, password FROM user WHERE user_name = ? AND password = ?";
        try (Connection connection = connectUser();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk registrasi pengguna baru
    public static boolean registerUser(String userName, String password) {
        String query = "INSERT INTO user (user_name, password) VALUES (?, ?)";
        try (Connection connection = connectUser();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk menambahkan buku baru
    public static boolean addBook(String title, String author, String category) {
        String query = "INSERT INTO book (title, author, category) VALUES (?, ?, ?)";
        try (Connection connection = connectBook();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, category);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk menghapus buku
    public static boolean deleteBook(String title) {
        String query = "DELETE FROM book WHERE title = ?";
        try (Connection connection = connectBook();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk meminjam buku
    public static boolean borrowBook(String title, String borrower, LocalDate borrowDate, LocalDate dueDate) {
        String query = "UPDATE book SET is_borrowed = 1, borrower = ?, borrow_date = ?, due_date = ? WHERE title = ?";
        try (Connection connection = connectBook();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, borrower);
            preparedStatement.setDate(2, Date.valueOf(borrowDate));
            preparedStatement.setDate(3, Date.valueOf(dueDate));
            preparedStatement.setString(4, title);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk mengembalikan buku
    public static boolean returnBook(String title, String borrower) {
        String query = "UPDATE book SET is_borrowed = 0, borrower = NULL, borrow_date = NULL, due_date = NULL WHERE title = ? AND borrower = ?";
        try (Connection connection = connectBook();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, borrower);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi untuk mengambil riwayat peminjaman
    public static List<BorrowHistory> fetchAllBorrowHistory(String borrower) {
        String query = "SELECT * FROM book WHERE borrower = ?";
        List<BorrowHistory> borrowHistoryList = new ArrayList<>();
        try (Connection connection = connectBook();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, borrower);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("title");
                    LocalDate borrowDate = resultSet.getDate("borrow_date").toLocalDate();
                    LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                    borrowHistoryList.add(new BorrowHistory(borrower, bookTitle, borrowDate, dueDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistoryList;
    }

    // Fungsi untuk mengambil daftar buku yang tersedia
    public static List<Book> getAvailableBooks() {
        String query = "SELECT * FROM book WHERE is_borrowed = 0";
        List<Book> bookList = new ArrayList<>();
        try (Connection connection = connectBook();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String category = resultSet.getString("category");
                bookList.add(new Book(title, author, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    // Fungsi untuk menambahkan informasi peminjaman buku ke riwayat
    public static void addBorrowHistory(String borrower, String bookTitle, String borrowDate, String dueDate) {
        String query = "INSERT INTO borrow_history (borrower, book_title, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectBorrowHistory();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, borrower);
            preparedStatement.setString(2, bookTitle);
            preparedStatement.setString(3, borrowDate.toString());
            preparedStatement.setString(4, dueDate.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


