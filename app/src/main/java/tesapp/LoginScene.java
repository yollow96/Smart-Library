package tesapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tesapp.config.Dbconnect;

public class LoginScene {
    private final App app;

    // Konstruktor untuk kelas LoginScene
    public LoginScene(App app) {
        this.app = app;
    }

    // Metode untuk membuat Scene login
    public Scene createLoginScene() {
        // Membuat root pane dengan padding
        BorderPane loginRoot = new BorderPane();
        loginRoot.setPadding(new Insets(20));

        // Membuat TextField untuk username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        // Membuat PasswordField untuk password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Membuat tombol login
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            // Mengambil teks dari field username dan password
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // Melakukan autentikasi pengguna
            if (Dbconnect.validateLogin(username, password)) {
                // Menetapkan pengguna saat ini jika login berhasil
                app.setCurrentUser(username);
                // Menampilkan pesan sukses jika login berhasil
                showAlert(Alert.AlertType.INFORMATION, "Login successful.");
                // Menampilkan scene utama aplikasi
                app.showAppScene();
            } else {
                // Menampilkan pesan error jika login gagal
                showAlert(Alert.AlertType.ERROR, "Login failed. Please check your credentials.");
            }
        });

        // Membuat tombol register
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> app.showRegistrationScene());

        // Membuat VBox dan menambahkan elemen-elemen ke dalamnya
        VBox loginBox = new VBox(10);
        // Membuat label login
        Label loginLabel = new Label("LOGIN TO SMART LIBRARY");
        loginLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
        loginBox.getChildren().addAll(
                loginLabel,
                usernameField,
                passwordField,
                new HBox(10, loginButton, registerButton));

        // Menambahkan VBox ke dalam root pane
        loginRoot.setCenter(loginBox);
        return new Scene(loginRoot, 300, 200);
    }

    // Metode untuk menampilkan alert
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
