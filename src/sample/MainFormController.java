package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class MainFormController {
	static StringProperty stepCount = new SimpleStringProperty();
	private Strategies strategies;
	private Coord stepCache;
	
	private GraphicsContext gc;
	
	@FXML
	private AnchorPane chessboardUI;
	
	@FXML
	private Label stepCountLabel;
	
	@FXML
	private TextArea console;
	
	@FXML
	private Canvas canvas;
	
	@FXML
	void getStepOnClick() {
		stepCache = strategies.getStep();
		drawChessShape();
	}
	
	@FXML
	void legalOnClick() {
	
	}
	
	@FXML
	void illegalOnClick() {
	
	}
	
	@FXML
	void captureOnClick() {
	
	}
	
	public void initialize() {
		drawChessBoard();
		stepCountLabel.textProperty().bind(stepCount);
		strategies = new Strategies();
		gc = canvas.getGraphicsContext2D();
		//TODO：重构原先的GameEngine类以及交互方法
	}
	
	private void drawChessBoard() {
		double
				margin = 50,            //chessboard line margin
				lineSpacing = 62.5,     //spacing between two lines
				textMargin = 10,        //text margin
				textOffset = 3;         //make text be on line center
		
		//Draw Rows
		for (int i = 0; i < 9; i++) {
			Line line = new Line(margin, margin + lineSpacing * i, margin + 500, margin + lineSpacing * i);
			Text text = new Text(textMargin, margin + lineSpacing * i + textOffset, String.valueOf(i + 1));
			chessboardUI.getChildren().addAll(line, text);
		}
		
		//Draw Columns
		for (int i = 0; i < 9; i++) {
			Line line = new Line(margin + lineSpacing * i, margin, margin + lineSpacing * i, margin + 500);
			Text text = new Text(margin + lineSpacing * i - textOffset, textMargin + 2 * textOffset, String.valueOf((char) (i + 'A')));
			chessboardUI.getChildren().addAll(line, text);
		}
	}
	
	
	private void drawChessShape(/*Coord coord, char status*/) {
	
	}
}
