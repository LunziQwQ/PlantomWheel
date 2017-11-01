package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;

/**
 * ***********************************************
 * Created by Lunzi on 10/10/2017.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class WelcomeFormController {
	
	@FXML
	private Text welcome;
	
	@FXML
	void githubRepoOnClick(){
		openUrlWithBrowse("https://github.com/LunziQwQ/PlantomWheel");
	}
	
	@FXML
	void authorOnClick(){
		openUrlWithBrowse("http://www.lunzi.pw/");
	}
	
	@FXML
	void blackPlayerOnClick(){
		gameStart(true);
	}
	
	@FXML
	void whitePlayerOnClick(){
		gameStart(false);
	}
	
	private void openUrlWithBrowse(String url) {
		try {
			Desktop.getDesktop().browse(URI.create(url));
		} catch (IOException ioe) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText("Can't open your browse. URL already copy to clipboard.");
			alert.showAndWait();
		}
	}
	
	private void gameStart(boolean isBlackPlayer) {
		try {
			Main.isBlackPlayer = isBlackPlayer;
			Main.gameStage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("MainForm.fxml"));
			Main.gameStage.setScene(new Scene(root, 800, 600));
			Main.gameStage.setTitle("PlantomWheel --" + (isBlackPlayer ? "black" : "white") + " player");
			Main.gameStage.setResizable(false);   //禁止用户更改窗口大小
			Main.gameStage.show();
			Stage welcomeStage = (Stage) welcome.getScene().getWindow();
			welcomeStage.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setHeaderText("IOException:");
			alert.setContentText(ioe.getMessage());
			alert.showAndWait();
		}
	}
}
