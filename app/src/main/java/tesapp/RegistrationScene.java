package tesapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
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
        usernameField.getStyleClass().add("text-field");
        usernameField.setMaxWidth(300);

        // Membuat PasswordField untuk password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(300);

        // Membuat tombol register
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("register-button");
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
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");

        // Membuat VBox dan menambahkan elemen-elemen ke dalamnya
        VBox registerBox = new VBox(10);
        registerBox.getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                registerButton);
        registerBox.setAlignment(Pos.CENTER);

        // Menambahkan VBox ke dalam root pane
        registerRoot.setCenter(registerBox);
        BorderPane.setAlignment(registerBox, Pos.CENTER);

        


        // Membuat scene dan mengatur ukuran menjadi layar penuh
        Scene scene = new Scene(registerRoot, 1440, 800);
        Image image = new Image("gambar/backgroundregis.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        registerRoot.setBackground(new Background(backgroundImage));

        scene.getStylesheets().add(getClass().getResource("/styleregister.css").toExternalForm()); // Apply CSS file
        return scene;
    }

    // Metode untuk menampilkan alert
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);

        Image image = new Image("gambar/alert.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        alert.getDialogPane().setBackground(new Background(backgroundImage));

        alert.showAndWait();
    }
}
