package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

public class MarketController implements Initializable {
    @FXML
    private VBox chosenFruitCard;

    @FXML
    private Label fruitNameLable;

    @FXML
    private Label fruitPriceLabel;

    @FXML
    private ComboBox<Integer> quantityLabel;

    @FXML
    private ImageView fruitImg;
    @FXML
    private ImageView cartIcon;

    @FXML
    private ScrollPane scroll;
    @FXML
    private Button add;

    @FXML
    private GridPane grid;

    private List<Fruit> fruits = new ArrayList<>();
    private Image image;
    private MyListener myListener;

    private List<Fruit> getData() {
        List<Fruit> fruits = new ArrayList<>();
       try {
           HttpRequest request = HttpRequest.newBuilder()
                   .uri(URI.create("https://springboot-be-fruit.onrender.com/api/fruits/all"))
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
                   fruitss.setName(fruit.getName());
                   fruitss.setCost(fruit.getCost());
                   fruitss.setImageUrl(fruit.getImageUrl());
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

    private void setChosenFruit(Fruit fruit) {
        fruitNameLable.setText(fruit.getName());
        fruitPriceLabel.setText(fruit.getCost());
        image = new Image(fruit.getImageUrl());
        fruitImg.setImage(image);
        chosenFruitCard.setStyle("-fx-background-color: #" + fruit.getColor() + ";\n" +
                "    -fx-background-radius: 30;");
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
    @FXML
    private void add() {
        String jsonServerUrl = "https://json-fruit.onrender.com/fruits";
        try {
            // Lấy giá trị từ các trường trong FXML
            String name = fruitNameLable.getText();
            String price = fruitPriceLabel.getText();
            Integer quantity = quantityLabel.getValue();
            String image = fruitImg.getImage().getUrl();

            // Tạo đối tượng Fruit
            Fruit fruit = new Fruit(name, price, quantity, image);

            // Chuyển đối tượng Fruit thành JSON
            Gson gson = new Gson();
            String json = gson.toJson(fruit);

            // Tạo HttpClient và yêu cầu POST
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jsonServerUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Gửi yêu cầu và nhận phản hồi
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra phản hồi
            if (response.statusCode() == 201) {
                // Nếu thành công, hiển thị thông báo
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Fruit Added Successfully");
                alert.setContentText("The fruit has been added to the market.");
                alert.showAndWait();
            } else {
                // Nếu thất bại, hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Add Fruit");
                alert.setContentText("An error occurred while adding the fruit.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please check the inputs.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/user.fxml"));
            Scene cartScene = new Scene(loader.load());

            // Tạo một Stage mới cho trang Cart
            Stage cartStage = new Stage();
            cartStage.setScene(cartScene);
            cartStage.setTitle("Your Cart");

            // Hiển thị cửa sổ Cart
            cartStage.show();

            // Đóng cửa sổ hiện tại nếu cần
            Stage currentStage = (Stage) cartIcon.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
