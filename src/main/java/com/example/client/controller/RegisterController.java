package com.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegisterController {

    @FXML
    private TextField emailField1; // Email

    @FXML
    private TextField emailField; // Name

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignUp() {
        String name = emailField.getText();
        String email = emailField1.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Notification", "Please enter complete information!", AlertType.WARNING);
            return;
        }

        try {
            URL url = new URL("https://springboot-be-fruit.onrender.com/api/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = String.format("{\"name\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", name, email, password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                showAlert("Success", "Successful registration!", AlertType.INFORMATION);
            } else {
                showAlert("Failed", "Registration failed. Email may already exist!", AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while connecting to the server!", AlertType.ERROR);
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/role.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ hiện tại nếu cần (tùy chọn)
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load login interface!", AlertType.ERROR);
        }
    }
    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
