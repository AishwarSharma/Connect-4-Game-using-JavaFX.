package connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "24304E";
	private static final String discColor2 = "#FF0000";

	private boolean isPlayerOneTurn = true;            // at beginning player one will insert // when it becomes false player two will play his turn
	// so this value will toggle between true and false

	private boolean isAllowedToInsert = true;

	public String PLAYER_ONE;
	public String PLAYER_TWO ;

	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];// for structural changes , for developer
	// This array will contain all the inserted disc objects.

	public void createPlayground(){
		Platform.runLater(() -> setNamesButton.requestFocus());

		Shape rectangleWithHoles = createGameStructureGrid();

		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList = createClickableColumns();

		for(Rectangle rectangle: rectangleList){
			rootGridPane.add(rectangle,0,1);     // This will add our Hower Over Rectangles to our Grid Pane.
		}
		setNamesButton.setOnAction(event -> {
			PLAYER_ONE = playerOneTextField.getText();
			PLAYER_TWO = playerTwoTextField.getText();
			playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
		});
	}


	private List<Rectangle> createClickableColumns() {
		List<Rectangle> rectangleList = new ArrayList<>();      // Data Collection Framework

		for(int col = 0; col<COLUMNS;col++){
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS +1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER/4));

			rectangle.setOnMouseEntered(event ->rectangle.setFill(Color.valueOf("eeeeee26")));
			// When we Hover Over on Each reactangle it will become light grey
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			// The rectangle will again come back to its original color when we remove our cursor

			final int column = col;
			rectangle.setOnMouseClicked(event ->{
				if(isAllowedToInsert) {
					isAllowedToInsert = false;      // when disc is dropping down then no more disc will be inserted
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}



	private void insertDisc(Disc disc, int column) {
		// This will determine the position of our new disk
		int row = ROWS -1;  // Array index start from 0 . So we decremented 1 from ROWS.
		while(row>=0) {
			if (insertedDiscArray[row][column] == null) {
				break;
			}
			row--;
		}

		if(row<0)         // If it is full , we cannot insert anymore disc
			return;

		insertedDiscArray[row][column] = disc; // for structural changes
		insertedDiscPane.getChildren().add(disc); // for visual changes

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER/4));

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);  // Duration speed or time

		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);
		translateTransition.setOnFinished(event -> {

			isAllowedToInsert = true; // Finally , when the disc is dropped then it will allow the next player to insert disc
			if (gameEnded(currentRow,column)){
				gameOver();
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? playerOneTextField.getText() : PLAYER_TWO);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row, int column){
		// vertical Points. A small example: Player has inserted his last disc at row = 2, column = 3.

		// index of each element present in column [row][column] : 0,3  1,3  2,3  3,3  4,3  5,3

		// Point2D class hold the value of x and y in term of coordinate
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3)        // This will help us to get the range of row values
				.mapToObj(r ->new Point2D(r,column))                   // range of row values = 0,1,2,3,4,5
				.collect(Collectors.toList());                   // This whole will check for vertical win.

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column+3)        // Horizontal win.
				.mapToObj(col ->new Point2D(row,col))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3,column+3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3,column-3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point:points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

			if(disc != null&& disc.isPlayerOneTurn == isPlayerOneTurn){ // if the last inserted disc belong to the current player.
				chain++;
				if(chain==4){
					return true;
				}
			}
			else{
				chain = 0;
			}

		}
		return false;
	}

	// Below Method will help to resolve ArrayIndexOutOfBound Exception
	private Disc getDiscIfPresent(int row, int column) {
		if(row>=ROWS || row<0 || column>=COLUMNS || column<0){
			return null;
		}
		return insertedDiscArray[row][column];
	}



	private void gameOver(){
		String winner = isPlayerOneTurn? playerOneTextField.getText():PLAYER_TWO;
		System.out.println("Winner is" + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner is" + winner);
		alert.setContentText("Want to play again?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(() ->{
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get() == yesBtn){
				resetGame();
			}
			else{
				Platform.exit();
				System.exit(0);
			}
		});
	}
	public void resetGame() {
		insertedDiscPane.getChildren().clear(); // removes all inserted disc from pane
		for(int row = 0;row<insertedDiscArray.length;row++){
			for(int col = 0;col<insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(playerOneTextField.getText());

		createPlayground(); // prepares a fresh playground
	}

    // Disc class will help us to determine the color of the disc according to the player.
	private static class Disc extends Circle{    // methods of Circle class can be used now
		private final boolean isPlayerOneTurn;
		public Disc(boolean isPlayerOneTurn){
			this.isPlayerOneTurn = isPlayerOneTurn;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneTurn?Color.valueOf(discColor1):Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}

	private Shape createGameStructureGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
		// columns + 1 and rows + 1 will generate extra space in both row and column
		// if we do not add 1 there will will no space between the disks

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {

				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);  // the horizontal position of the center of the circle in pixels
				circle.setCenterY(CIRCLE_DIAMETER / 2);  //the vertical position of the center of the circle in pixels
				circle.setSmooth(true);     // this will make the circle edges smoother

				// Below 2 lines will set the coordinate for each circle
				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				// CIRCLE_DIAMETER / 4 = 20 pixels

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);   // When we subtract circle from rectangle we will get a hole.
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField, playerTwoTextField;

	@FXML
	public Button setNamesButton;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
