package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class MainFormController {
	static StringProperty stepCount = new SimpleStringProperty();
	
	
	@FXML
	private AnchorPane chessboard;
	
	@FXML
	private Label stepCountLabel;
	
	@FXML
	private TextArea console;
	
	@FXML
	void getStepOnClick(){
	}
	
	public void initialize() {
		drawChessBoard();
		stepCountLabel.textProperty().bind(stepCount);
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
			chessboard.getChildren().addAll(line, text);
		}
		
		//Draw Columns
		for (int i = 0; i < 9; i++) {
			Line line = new Line(margin + lineSpacing * i, margin, margin + lineSpacing * i, margin + 500);
			Text text = new Text(margin + lineSpacing * i - textOffset, textMargin + 2 * textOffset, String.valueOf((char) (i + 'A')));
			chessboard.getChildren().addAll(line, text);
		}
	}
	
	
}
