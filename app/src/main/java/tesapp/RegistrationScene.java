package tesapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import tesapp.config.Dbconnect;

public class RegistrationScene {
    private final App app;

    // Konstruktor untuk kelas RegistrationScene
    public RegistrationScene(App app) {
        this.app = app;
    }

    // Metode untuk membuat Scene registrasi
    public Scene createRegistrationScene() {
        // Membuat root pane dengan padding
        BorderPane registerRoot = new BorderPane();
        registerRoot.setPadding(new Insets(20));

        // Membuat TextField untuk username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        // Membuat PasswordField untuk password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Membuat tombol register
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            // Mengambil teks dari field username dan password
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // Melakukan registrasi pengguna baru
            if (!username.isEmpty() && !password.isEmpty()) {
                if (Dbconnect.registerUser(username, password)) {
                    // Menampilkan pesan sukses jika registrasi berhasil
                    showAlert(Alert.AlertType.INFORMATION, "Registration successful. Please login with your new account.");
                    // Menampilkan scene login
                    app.showLoginScene();
                } else {
                    // Menampilkan pesan error jika registrasi gagal
                    showAlert(Alert.AlertType.ERROR, "Registration failed. Please try again.");
                }
            } else {
                // Menampilkan pesan peringatan jika field kosong
                showAlert(Alert.AlertType.WARNING, "Please enter a valid username and password.");
            }
        });

        // Membuat label judul
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Membuat VBox dan menambahkan elemen-elemen ke dalamnya
        VBox registerBox = new VBox(10);
        registerBox.getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                registerButton);

        // Menambahkan VBox ke dalam root pane
        registerRoot.setCenter(registerBox);
        return new Scene(registerRoot, 300, 200);
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
