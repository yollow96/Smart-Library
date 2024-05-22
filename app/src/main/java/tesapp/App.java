package tesapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import tesapp.config.Dbconnect;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private ObservableList<Book> availableBooks;
    private ObservableList<Book> borrowedBooks;

    private Stage primaryStage;
    private String currentUser;
    private Map<String, String> userCredentials = new HashMap<>();

    // Metode utama yang dijalankan saat aplikasi dimulai
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inisialisasi data buku
        availableBooks = FXCollections.observableArrayList();
        refreshAvailableBooks();

        borrowedBooks = FXCollections.observableArrayList();

        // Membuat scene login
        primaryStage.setTitle("Smart Library");
        showLoginScene();
        primaryStage.show();
    }

    // Metode untuk memperbarui daftar buku yang tersedia
    public void refreshAvailableBooks() {
        availableBooks.clear();
        availableBooks.addAll(Dbconnect.getAvailableBooks());
    }

    // Metode untuk mengautentikasi pengguna
    public boolean authenticateUser(String username, String password) {
        return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
    }

    // Metode untuk mendaftarkan pengguna baru
    public void registerUser(String username, String password) {
        userCredentials.put(username, password);
    }

    // Metode untuk menampilkan scene kembali ke login
    public void showReturnToLoginScene() {
        AppScene appScene = new AppScene(this, availableBooks, borrowedBooks);
        appScene.createReturnToLoginScene();
    }

    // Metode untuk menampilkan scene login
    public void showLoginScene() {
        LoginScene loginScene = new LoginScene(this);
        primaryStage.setScene(loginScene.createLoginScene());
    }

    // Metode untuk menampilkan scene registrasi
    public void showRegistrationScene() {
        RegistrationScene registrationScene = new RegistrationScene(this);
        primaryStage.setScene(registrationScene.createRegistrationScene());
    }

    // Metode untuk menampilkan scene utama aplikasi
    public void showAppScene() {
        AppScene appScene = new AppScene(this, availableBooks, borrowedBooks);
        primaryStage.setScene(appScene.createAppScene());
    }

    // Metode untuk menampilkan scene riwayat peminjaman
    public void showHistoryScene() {
        HistoryBorrowed historyScene = new HistoryBorrowed(this);
        primaryStage.setScene(historyScene.createScene());
    }

    // Metode untuk mendapatkan pengguna saat ini
    public String getCurrentUser() {
        return currentUser;
    }

    // Metode untuk menetapkan pengguna saat ini
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    // Metode utama untuk meluncurkan aplikasi
    public static void main(String[] args) {
        launch(args);
    }
}
