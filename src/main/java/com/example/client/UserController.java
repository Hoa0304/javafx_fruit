package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private VBox chosenFruitCard;
    @FXML
    private Label fruitNameLable;
    @FXML
    private Label fruitPriceLabel;
    @FXML
    private ImageView fruitImg;
    @FXML
    private GridPane grid;
    @FXML
    private Button backButton;

    private String chosenFruitId;
    private Integer chosenFruitQuantity;
    private String chosenImage;
    private String chosenCost;
    private String chosenColor;
    private String chosenName;

    @FXML
    private ComboBox<Integer> quantityLabel;


    private List<Fruit> fruits = new ArrayList<>();
    private Image image;
    private MyListener myListener;

    private List<Fruit> getData() {
        List<Fruit> fruits = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://springboot-be-fruit.onrender.com/api/fruits"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                Fruit[] fruitArray = objectMapper.readValue(response.body(), Fruit[].class);
                for (Fruit fruit : fruitArray) {
                    Fruit fruitss = new Fruit();
                    fruitss.setId(fruit.getId());
                    fruitss.setName(fruit.getName());
                    fruitss.setCost(fruit.getCost());
                    fruitss.setImageUrl(fruit.getImageUrl());
                    fruitss.setQuantity(fruit.getQuantity());
                    fruitss.setColor("6A7324");
                    fruits.add(fruitss);
                }
            } else {
                System.out.println("Error: " + response.statusCode());
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return fruits;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fruits.addAll(getData());
        ObservableList<Integer> quantities = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        quantityLabel.setItems(quantities);
        quantityLabel.setValue(1);
        if (fruits.size() > 0) {
            setChosenFruit(fruits.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Fruit fruit) {
                    setChosenFruit(fruit);
                }
            };
        }
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < fruits.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(fruits.get(i),myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setChosenFruit(Fruit fruit) {
        fruitNameLable.setText(fruit.getName());
        fruitPriceLabel.setText(fruit.getCost());
        image = new Image(fruit.getImageUrl());
        fruitImg.setImage(image);
        System.out.printf(""+fruit.getQuantity());
        quantityLabel.setValue(fruit.getQuantity());
        chosenFruitCard.setStyle("-fx-background-color: #" + fruit.getColor() + ";\n" +
                "    -fx-background-radius: 30;");
        chosenFruitId = fruit.getId();
        chosenColor = fruit.getColor();
        chosenCost = fruit.getCost();
        chosenImage = fruit.getImageUrl();
        chosenName = fruit.getName();
    }

    @FXML
    private void goBackToMarket() {
        try {
            // Tải lại giao diện Market (market.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/market.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load the Market view.");
            alert.showAndWait();
        }
    }

    @FXML
    public void updateFruit() {
        String id = chosenFruitId;
        Integer quantity = quantityLabel.getValue();
        String name = chosenName;
        String cost = chosenCost;
        String color = chosenColor;
        String imageUrl = chosenImage;
        System.out.println("up"+quantity);
        String API_URL = "http://localhost:8081/api/fruits";
        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();
            JsonObject fruit = new JsonObject();
            fruit.addProperty("name", name);
            fruit.addProperty("color", color);
            fruit.addProperty("quantity", quantity);
            fruit.addProperty("cost", cost);
            fruit.addProperty("imageUrl", imageUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(fruit.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update Successful");
                alert.setHeaderText(null);
                alert.setContentText("Fruit updated successfully.");
                alert.showAndWait();
                fruits.clear();
                fruits.addAll(getData());
                refreshGrid();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update fruit.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void deleteFruit() {
        String id = chosenFruitId;
        String API_URL = "https://springboot-be-fruit.onrender.com/api/fruits";
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Successful");
                alert.setHeaderText(null);
                alert.setContentText("Fruit deleted successfully.");
                alert.showAndWait();
                fruits.clear();
                fruits.addAll(getData());
                refreshGrid();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Delete Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to delete fruit.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshGrid() {
        grid.getChildren().clear(); // Xóa tất cả các phần tử trong grid

        int column = 0;
        int row = 1;

        try {
            for (int i = 0; i < fruits.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(fruits.get(i), myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
