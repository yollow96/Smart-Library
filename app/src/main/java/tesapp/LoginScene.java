package tesapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tesapp.config.Dbconnect;

public class LoginScene {
    private final App app;

    // Konstruktor untuk kelas LoginScene
    public LoginScene(App app) {
        this.app = app;
    }

    // Metode untuk membuat Scene login
    public Scene createLoginScene(Stage primaryStage) {
        // Membuat root pane dengan padding
        BorderPane loginRoot = new BorderPane();
        loginRoot.setPadding(new Insets(20));

        // Membuat TextField untuk username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(500);
        usernameField.setPrefHeight(50);
        usernameField.setAlignment(Pos.CENTER);
        usernameField.getStyleClass().add("text-field");

        // Membuat PasswordField untuk password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(500);
        passwordField.setPrefHeight(50);
        passwordField.setAlignment(Pos.CENTER);

        passwordField.getStyleClass().add("password-field");

        // Set the background image
        Image image = new Image("gambar/backgroundlogin.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        loginRoot.setBackground(new Background(backgroundImage));

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
                showAlert(Alert.AlertType.ERROR, "Login gagal." +  "\nSilahkan Cek Username dan Password.");
            }
        });
        loginButton.getStyleClass().add("login-button");

        // Membuat tombol register
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> app.showRegistrationScene());
        registerButton.getStyleClass().add("register-button");

        // Membuat HBox untuk tombol dan mengatur ke tengah
        HBox buttonBox = new HBox(100, loginButton, registerButton);
        VBox spacing = new VBox(300, passwordField, buttonBox);
        buttonBox.setAlignment(Pos.CENTER);


        // Membuat tulisan SMART LIBRARY diatas login to smart library dan buat besar seperti judul welcome yang cantik
        VBox titleBox = new VBox(40);
        titleBox.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("SMART LIBRARY");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 50px; -fx-text-fill: #0073e6; -fx-text-alignment: center;");
        titleBox.getChildren().add(titleLabel);
        loginRoot.setTop(titleBox);

        

        // Membuat VBox dan menambahkan elemen-elemen ke dalamnya
        VBox loginBox = new VBox(20); // Mengubah jarak antar elemen dalam VBox
        // Membuat label login
        Label loginLabel = new Label("LOGIN TO SMART LIBRARY");
        loginLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");

        // Menambahkan margin elemen untuk jarak dari atas
        VBox.setMargin(loginLabel, new Insets(10, 0, 5, 0)); // Margin top untuk label login
        VBox.setMargin(usernameField, new Insets(0, 0, 20, 0)); // Margin top untuk usernameField
        VBox.setMargin(passwordField, new Insets(0, 0, 20, 0)); // Margin top untuk passwordField

        loginBox.getChildren().addAll(
                titleLabel,
                loginLabel,
                usernameField,
                passwordField,
                buttonBox,
                spacing);
        loginBox.setAlignment(Pos.CENTER);

        // Menambahkan VBox ke dalam root pane
        loginRoot.setCenter(loginBox);
        BorderPane.setAlignment(loginBox, Pos.CENTER);

        // Membuat scene dan mengatur ukuran menjadi layar penuh
        Scene scene = new Scene(loginRoot, 1440, 800);

        // Menambahkan file CSS ke dalam scene
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        return scene;
    }

    // Metode untuk menampilkan alert
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Image image = new Image("gambar/alert.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
        alert.getDialogPane().setBackground(new Background(backgroundImage));
        alert.initStyle(StageStyle.UTILITY); // Mengubah tampilan alert menjadi non-fullscreen
        alert.showAndWait();
    }
}
