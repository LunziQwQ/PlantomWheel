package sample;

import javafx.application.Platform;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFormController {
	static StringProperty stepCount = new SimpleStringProperty("0");
	private Strategies strategies = new Strategies();
	private Coord stepCache;
	
	private GraphicsContext mainGC;
	private GraphicsContext captureGC;
	
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
	
	private Coord mousePos = new Coord(0, 0);
	
	@FXML	//Always get the mouse position
	void getMousePos(MouseEvent event) {
		mousePos = new Coord((int) event.getX(), (int) event.getY());
		Main.gameStage.setTitle("PlantomWheel  --> " + (Main.isBlackPlayer ? "Black" : "White") + " player" +
				"   --> MousePos: " + mousePos.toString());
	}
	
	@FXML
	void getStepOnClick() {
		stepCache = strategies.getStep();
		if (stepCache == null) console.appendText("--> Pass!");
		drawChessShape(stepCache, 't');
		
		console.appendText(String.format("--> Try (%d, %d): ", stepCache.x, stepCache.y));
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
		console.appendText("legal\n");
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
		console.appendText("illegal\n");
		getStepOnClick();
		
	}
	
	
	
	@FXML
	void captureOnClick() {
		if (!runningCapture.get()) {
			captureBtn.setText("Finish");
			getStepBtn.setDisable(true);
			runningCapture.set(true);
			captureThread.start();
		} else {
			captureBtn.setText("Capture");
			getStepBtn.setDisable(false);
			runningCapture.set(false);
		}
	}
	
	public void initialize() {
		//Make the console always scroll to the bottom
		this.console.textProperty().addListener((observableValue, oldValue, newValue) -> {
			this.console.setScrollTop(1.7976931348623157E308D);
		});
		
		new ChessBoard();
		drawChessBoardBase();
		stepCountLabel.textProperty().bind(stepCount);
		console.appendText("Game is start. I'm " + (Main.isBlackPlayer ? "black player" : "white player") + "\n");

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
		Canvas chessCanvas = new Canvas();
		chessCanvas.setHeight(600);
		chessCanvas.setWidth(600);
		chessboardUI.getChildren().add(chessCanvas);
		mainGC = chessCanvas.getGraphicsContext2D();
		mainGC.setLineWidth(3);
		mainGC.setFont(Font.font("console", FontWeight.BOLD, 50));
		
		Canvas captureCanvas = new Canvas();
		captureCanvas.setHeight(600);
		captureCanvas.setWidth(600);
		chessboardUI.getChildren().add(captureCanvas);
		captureGC = captureCanvas.getGraphicsContext2D();
		captureGC.setLineWidth(5);
	}
	
	private void drawChessBoard() {
		mainGC.clearRect(0,0,600,600);
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
		
		mainGC.clearRect(centerX-1, centerY-1, 63, 63);
		switch (status) {
			case 'b':
				mainGC.setFill(Color.BLACK);
				break;
			case 'w':
				mainGC.setFill(Color.WHITE);
				break;
			case '?':
				drawMark = true;
				mainGC.setFill(Main.isBlackPlayer ? Color.WHITE : Color.BLACK);
				break;
			case 't':
				mainGC.setStroke(Color.rgb(102, 204, 255));
				mainGC.strokeRect(centerX, centerY, 60, 60);
				mainGC.setFill(Main.isBlackPlayer ? Color.BLACK : Color.WHITE);
				break;
			case 'e':
				return;
		}
		mainGC.fillRoundRect(centerX, centerY, 60, 60, 60, 60);
		if(drawMark){
			mainGC.setFill(Color.RED);
			mainGC.fillText("?", centerX + 20, centerY + 47);
		}
	}
	
	private void drawChessBorder(Coord coord, GraphicsContext gc) {
	
	}
	
	private AtomicBoolean runningCapture = new AtomicBoolean(false);
	private volatile List<Coord> captureCoords = new ArrayList<>();
	private Thread captureThread = new Thread(()->{
		while (true) {
			if (runningCapture.get()) {
				captureGC.clearRect(0, 0, 600, 600);
				if (mousePos.x <= 600) {
				
				}
			} else {
				//输出信息，输出坐标集合
				Platform.runLater(() -> console.appendText("Capture Finish! \n"));
				break;
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	});
}
