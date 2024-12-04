package com.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RoleSelectionController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSubmit() {

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Notification", "Please enter full email and password!", AlertType.WARNING);
            return;
        }

        try {
            URL url = new URL("https://springboot-be-fruit.onrender.com/api/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                showAlert("Success", "Successful login!", AlertType.INFORMATION);
                goToHomePage();
            } else {
                showAlert("Failed", "Login failed. Please try again!", AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while connecting to the server!", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterClick() {
        try {
            // Tải giao diện register.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/register.fxml"));
            Parent root = loader.load();

            // Tạo một cửa sổ mới (Stage) để hiển thị màn hình đăng ký
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ hiện tại nếu cần (tùy chọn)
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load registration interface!", AlertType.ERROR);
        }
    }

    private void goToHomePage() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/market.fxml"));
        AnchorPane homePage = loader.load();;
        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene scene = new Scene(homePage);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
