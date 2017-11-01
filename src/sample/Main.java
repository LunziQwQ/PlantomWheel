package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    static boolean isBlackPlayer;        //存储选定的黑方白方
    static Stage gameStage;
    
    @Override
    public void start(Stage welcomeStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("WelcomeForm.fxml"));
        welcomeStage.setTitle("Welcome to PlantomWheel v1.0");
        welcomeStage.setScene(new Scene(root, 600, 400));
        welcomeStage.setResizable(false);       //禁止用户更改窗口大小
        welcomeStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
