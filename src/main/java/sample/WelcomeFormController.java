package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

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
	private Button startAiBtn;
	
	@FXML
	private Button loadRepBtn;
	
	@FXML
	void githubRepoOnClick() {
		openUrlWithBrowse("https://github.com/LunziQwQ/PlantomWheel");
	}
	
	@FXML
	void authorOnClick() {
		openUrlWithBrowse("http://www.lunzi.pw/");
	}
	
	@FXML
	void blackPlayerOnClick() {
		gameStart(true);
	}
	
	@FXML
	void whitePlayerOnClick() {
		gameStart(false);
	}
	
	@FXML
	void startAiOnClick() {
		startAiBtn.setVisible(false);
		loadRepBtn.setVisible(false);
		Main.isAiMode = true;
	}
	
	@FXML
	void loadRepOnClick() {
		Main.isAiMode = false;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Replay file");
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("REP file(*.rep)", "*.rep"));
		File file = fileChooser.showOpenDialog(new Stage());
		if (file != null) {
			if (Main.history.load(file.getAbsolutePath())) {
				gameStart(true);
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Load rep file failed:");
				alert.setContentText("Can't load the rep failed");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Load rep file failed:");
			alert.setContentText("Choose a invaild rep file.");
			alert.showAndWait();
		}
		
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
			Parent root = FXMLLoader.load(getClass().getResource("/MainForm.fxml"));
			Main.gameStage.setScene(new Scene(root, 800, 600));
			Main.gameStage.setTitle(Main.isAiMode ? "PlantomWheel --> " + (isBlackPlayer ? "black" : "white") + " player"
					: "PlantomWheel --> " + "Replay");
			Main.gameStage.setResizable(false);   //禁止用户更改窗口大小
			Main.gameStage.show();
			Stage welcomeStage = (Stage) welcome.getScene().getWindow();
			welcomeStage.close();
			
			Main.gameStage.setOnCloseRequest(event -> {
				if (Main.isAiMode) {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Save rep");
					alert.setHeaderText("Do you want to save replay?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						DirectoryChooser chooser = new DirectoryChooser();
						chooser.setTitle("Select replay save path");
						File path = chooser.showDialog(new Stage());
						Main.history.save(path.getAbsolutePath());
						System.out.println(path.getAbsoluteFile());
					}
				}
			});
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setHeaderText("IOException:");
			alert.setContentText(ioe.getMessage());
			alert.showAndWait();
		}
	}
}
