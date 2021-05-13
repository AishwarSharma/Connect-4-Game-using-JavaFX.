package connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;  // reference to the controller object

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));     // Connects Main.java with game.fxml
        GridPane rootGridPane = loader.load();   //This simply loads our root node (or) Parent node

        controller= loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());  // cover the whole menu
        // Whatever the width of primaryStage will be the width of our menu bar

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);  // We assigned first child(0) as Pane
        menuPane.getChildren().add(menuBar);  // Our menu is placed at top i.e index 0.

        Scene scene = new Scene(rootGridPane);  // this will do statically arrangement screen  // Here we can adjust the scene width & height

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.show();
    }



    private MenuBar createMenu(){
        //FileMenu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> controller.resetGame());  // Lambda Expression  (event is tha parameter)

        MenuItem resetGame = new MenuItem("Reset game");
        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separator = new SeparatorMenuItem();   // This will separate our menu items with a horizontal line
        MenuItem exitGame = new MenuItem("Exit game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame,resetGame,separator,exitGame);

        //HelpMenu

        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About game");
        aboutGame.setOnAction(event -> aboutGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame,separatorMenuItem,aboutMe);

        MenuBar menuBar = new MenuBar();                // This will Create the menu bar
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
    }

    private void aboutMe(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Aishwar & Abhieshek");    // Sub Title
        alert.setContentText("We love to play around with code and create games.\n" +
                "Connect 4 is one of them.In free time\n" +
                "We like to spend time with nears and dears\n");
        alert.show();
    }

    private void aboutGame() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players\n" +
                "first choose a color and then take turns dropping colored discs from the top \n " +
                "into a seven-column, six-row vertically suspended grid. The pieces fall straight  \n" +
                "down, occupying the next available space within the column. \n" +
                "The objective of the game is to be the first to form a horizontal, vertical, \n" +
                "or diagonal line of four of one's own discs. \n" +
                "Connect Four is a solved game. \n" +
                "The first player can always win by playing the right moves.\n");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();  // This will exit the application
        System.exit(0);  // This will stop the virtual machine
    }







    public static void main(String[] args) {
        launch(args);
    }
}
