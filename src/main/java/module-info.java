module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.google.gson;


    opens com.example.client to javafx.fxml, com.google.gson;
    exports com.example.client;
    exports com.example.client.controller;
    opens com.example.client.controller to com.google.gson, javafx.fxml;
}