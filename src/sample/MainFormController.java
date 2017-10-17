package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainFormController {
	static StringProperty stepCount = new SimpleStringProperty("0");
	private Strategies strategies = new Strategies();
	private Coord stepCache;
	
	private GraphicsContext gc;
	
	@FXML
	private Button getStepBtn;
	
	@FXML
	private Button legalBtn;
	
	@FXML
	private Button illegalBtn;
	
	@FXML
	private Button captureBtn;
	
	@FXML
	private AnchorPane chessboardUI;
	
	@FXML
	private Label stepCountLabel;
	
	@FXML
	private TextArea console;
	
	
	@FXML
	void getStepOnClick() {
		stepCache = strategies.getStep();
		if (stepCache == null) console.appendText("--> Pass!");
		drawChessShape(stepCache, 't');
		
		getStepBtn.setDisable(true);
		captureBtn.setDisable(true);
		legalBtn.setDisable(false);
		illegalBtn.setDisable(false);
	}
	
	@FXML
	void legalOnClick() {
		ChessBoard.getChess(stepCache).setChess(Main.isBlackPlayer ? 'b' : 'w');
		drawChessShape(stepCache, Main.isBlackPlayer ? 'b' : 'w');
		drawChessBoard();
		
		stepCache = null;
		stepCount.setValue(String.valueOf(Integer.valueOf(stepCount.getValue()) + 1));
		captureBtn.setDisable(false);
		getStepBtn.setDisable(false);
		legalBtn.setDisable(true);
		illegalBtn.setDisable(true);
	}
	
	@FXML
	void illegalOnClick(MouseEvent event) {
		MouseButton mouseButton = event.getButton();
		if (mouseButton == MouseButton.PRIMARY) {
			ChessBoard.getChess(stepCache).setChess('?');
			drawChessShape(stepCache, '?');
		} else {
			ChessBoard.getChess(stepCache).setChess(Main.isBlackPlayer ? 'w' : 'b');
			drawChessShape(stepCache, Main.isBlackPlayer ? 'w' : 'b');
		}
		strategies.offensiveFlag = true;
		drawChessBoard();
		getStepOnClick();
	}
	
	@FXML
	void captureOnClick() {
	}
	
	public void initialize() {
		new ChessBoard();
		drawChessBoardBase();
		stepCountLabel.textProperty().bind(stepCount);
		console.appendText("Game is start. I'm " + (Main.isBlackPlayer ? "black player" : "white player"));
	}
	
	private void drawChessBoardBase() {
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
		
		//Initialize Canvas
		Canvas canvas = new Canvas();
		canvas.setHeight(600);
		canvas.setWidth(600);
		chessboardUI.getChildren().add(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(3);
		gc.setFont(Font.font("console", FontWeight.BOLD, 50));
	}
	
	private void drawChessBoard() {
		gc.clearRect(0,0,600,600);
		for (Chess[] chesses : ChessBoard.board) {
			for (Chess chess : chesses) {
				drawChessShape(chess.coord, chess.status);
			}
		}
	}
	
	private void drawChessShape(Coord coord, char status) {
		double centerX = 20 + 62.5 * (coord.x);
		double centerY = 20 + 62.5 * (coord.y);
		boolean drawMark = false;
		
		gc.clearRect(centerX-1, centerY-1, 63, 63);
		switch (status) {
			case 'b':
				gc.setFill(Color.BLACK);
				break;
			case 'w':
				gc.setFill(Color.WHITE);
				break;
			case '?':
				drawMark = true;
				gc.setFill(Main.isBlackPlayer ? Color.WHITE : Color.BLACK);
				break;
			case 't':
				gc.setStroke(Color.rgb(102, 204, 255));
				gc.strokeRect(centerX, centerY, 60, 60);
				gc.setFill(Main.isBlackPlayer ? Color.BLACK : Color.WHITE);
				break;
			case 'e':
				return;
		}
		gc.fillRoundRect(centerX, centerY, 60, 60, 60, 60);
		if(drawMark){
			gc.setFill(Color.RED);
			gc.fillText("?", centerX + 20, centerY + 47);
		}
	}
}
