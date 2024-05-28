package tesapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tesapp.config.Dbconnect;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private ObservableList<Book> availableBooks;
    private ObservableList<Book> borrowedBooks;

    private Stage primaryStage;
    @SuppressWarnings("unused")
    private String currentUserRole;
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
        primaryStage.getIcons().add(
        new Image(
        App.class.getResourceAsStream( "/gambar/iconapk.png" )));
        primaryStage.setResizable(false);
        showLoginScene();
        primaryStage.show();
    }

    // Metode untuk memperbarui daftar buku yang tersedia
    public void refreshAvailableBooks() {
        availableBooks.clear();
        availableBooks.addAll(Dbconnect.getAvailableBooks());
    }

    // // Metode untuk mengautentikasi pengguna
    // public boolean authenticateUser(String username, String password) {
    //     return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
    // }

    // Metode untuk mendaftarkan pengguna baru
    public void registerUser(String username, String password) {
        userCredentials.put(username, password);
    }

    // Metode untuk menampilkan scene kembali ke login
    public void showReturnToLoginScene() {
        AppScene appScene = new AppScene(this, availableBooks, borrowedBooks, primaryStage);
        appScene.createReturnToLoginScene();
        // primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    // Metode untuk menampilkan scene login
    public void showLoginScene() {
        Scene loginScene = new LoginScene(this).createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
        // primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    // Metode untuk menampilkan scene registrasi
    public void showRegistrationScene() {
        RegistrationScene registrationScene = new RegistrationScene(this);
        primaryStage.setScene(registrationScene.createRegistrationScene());
        // primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    // Metode untuk menampilkan scene utama aplikasi
    public void showAppScene() {
        AppScene appScene = new AppScene(this, availableBooks, borrowedBooks, primaryStage);
        primaryStage.setScene(appScene.createAppScene());
        // primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    // Metode untuk menampilkan scene riwayat peminjaman
    public void showHistoryScene() {
        HistoryBorrowed historyScene = new HistoryBorrowed(this);
        primaryStage.setScene(historyScene.createScene());
        // primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    // Metode untuk mendapatkan pengguna saat ini
    public String getCurrentUser() {
        return currentUser;
    }

    // Metode untuk menetapkan pengguna saat ini
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    public boolean authenticateUser(String username, String password) {
    if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
        currentUserRole = (username.equals("admin")) ? "admin" : "user";
        return true;
    }
    return false;
    }


    // Metode utama untuk meluncurkan aplikasi
    public static void main(String[] args) {
        launch(args);
    }
}
