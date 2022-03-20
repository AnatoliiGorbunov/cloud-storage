package ru.geekbrains.cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

public class CloudClientApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        BasicConfigurator.configure();
        Parent parent = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
