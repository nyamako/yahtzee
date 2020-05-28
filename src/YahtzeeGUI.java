import javafx.application.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class YahtzeeGUI extends Application {

    Player player = new Player();
    boolean[] reroll = {true, true, true, true, true};
    Image lockImage = new Image("lock.png");
    Image unlockImage = new Image("unlock.png");
    Image[] diceImage = {new Image("0.png"), new Image("1.png"), new Image("2.png"), new Image("3.png"),
            new Image("4.png"), new Image("5.png"), new Image("6.png")};
    int currentScoreBox = -1;
    List<Integer> validBoxes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13, 14, 16));

    // SCORES
    Text[] scoreType;
    Text[] score;
    Rectangle[] typeBorder;
    Rectangle[] scoreBorder;
    HBox[] scoreRow;
    StackPane[] typePane;
    StackPane[] scorePane;
    Button[] scoreButton;
    StackPane[] scoreRowPane;

    VBox UpperScores;
    VBox LowerScores;
    HBox upperLower;
    VBox scoreSheet;

    // DICE
    VBox[] dieVBox;
    StackPane[] diePane;
    ImageView[] diePic;
    ImageView[] lockPic;
    Rectangle[] dieBorder;
    Button[] rerollDie;

    HBox dice;
    Button rollButton;
    Button scoreRollButton;
    HBox buttonBox;
    VBox game;

    Button playAgain;
    StackPane buttonPane;

    public void start(Stage primaryStage) {

        // SCORES
        scoreType = new Text[19];
        score = new Text[19];
        typeBorder = new Rectangle[19];
        scoreBorder = new Rectangle[19];
        scoreRow = new HBox[19];
        typePane = new StackPane[19];
        scorePane = new StackPane[19];
        scoreButton = new Button[19];
        scoreRowPane = new StackPane[19];

        for(int i = 0; i < 19; i++) {
            scoreType[i] = new Text(" " + Player.getType()[i]);
            scoreType[i].setFont(new Font(24));
            typeBorder[i] = new Rectangle(225, 40, Color.TRANSPARENT);
            typeBorder[i].setStroke(Color.BLACK);
            typeBorder[i].setStrokeWidth(2);
            typePane[i] = new StackPane(typeBorder[i], scoreType[i]);
            typePane[i].setAlignment(Pos.CENTER_LEFT);
            score[i] = new Text();
            score[i].setFont(new Font(24));
            scoreBorder[i] = new Rectangle(100, 40, Color.TRANSPARENT);
            scoreBorder[i].setStroke(Color.BLACK);
            scoreBorder[i].setStrokeWidth(2);
            scorePane[i] = new StackPane();
            scorePane[i].setAlignment(Pos.CENTER);
            scorePane[i].getChildren().add(scoreBorder[i]);
            scorePane[i].getChildren().add(score[i]);

            scoreRow[i] = new HBox(typePane[i], scorePane[i]);
            scoreButton[i] = new Button();
            scoreButton[i].setDisable(true);
            scoreButton[i].setPrefSize(325, 40);
            scoreButton[i].setStyle("-fx-background-color: transparent;");
            scoreButton[i].setOnAction(this::handleScoreBox);
            scoreRowPane[i] = new StackPane(scoreRow[i], scoreButton[i]);
        }

        UpperScores = new VBox();
        for(int i = 0; i < 9; i++) {
            UpperScores.getChildren().add(scoreRowPane[i]);
        }

        LowerScores = new VBox();
        for(int i = 9; i < 18; i++) {
            LowerScores.getChildren().add(scoreRowPane[i]);
        }

        upperLower = new HBox(UpperScores, LowerScores);
        upperLower.setAlignment(Pos.CENTER);
        upperLower.setSpacing(20);

        scoreRow[18].setAlignment(Pos.CENTER);
        scoreSheet = new VBox(upperLower, scoreRowPane[18]);
        scoreSheet.setSpacing(2);


        // DICE
        dieVBox = new VBox[5];
        diePane = new StackPane[5];
        diePic = new ImageView[5];
        lockPic = new ImageView[5];
        dieBorder = new Rectangle[5];
        rerollDie = new Button[5];
        for(int i = 0; i < 5; i++) {
            lockPic[i] = new ImageView(unlockImage);
            lockPic[i].setPreserveRatio(true);
            lockPic[i].setFitHeight(50);
            diePic[i] = new ImageView(diceImage[0]);
            diePic[i].setPreserveRatio(true);
            diePic[i].setFitHeight(100);
            dieBorder[i] = new Rectangle(100, 100, Color.TRANSPARENT);
            dieBorder[i].setStroke(Color.TRANSPARENT);
            dieBorder[i].setStrokeWidth(10);
            //rounded corners
            dieBorder[i].setArcHeight(10);
            dieBorder[i].setArcWidth(10);
            rerollDie[i] = new Button();
            rerollDie[i].setPrefSize(100, 100);
            rerollDie[i].setStyle("-fx-background-color: transparent;");
            rerollDie[i].setDisable(true);
            rerollDie[i].setOnAction(this::handleLockButton);
            diePane[i] = new StackPane();
            diePane[i].getChildren().add(diePic[i]);
            diePane[i].getChildren().add(dieBorder[i]);
            diePane[i].getChildren().add(rerollDie[i]);
            dieVBox[i] = new VBox(lockPic[i], diePane[i]);
            dieVBox[i].setAlignment(Pos.CENTER);
            dieVBox[i].setSpacing(5);
        }
        dice = new HBox(dieVBox[0], dieVBox[1], dieVBox[2], dieVBox[3], dieVBox[4]);
        dice.setAlignment(Pos.CENTER);
        dice.setSpacing(5);


        rollButton = new Button();
        rollButton.setText("Roll!");
        rollButton.setPrefSize(150, 40);
        rollButton.setFont(new Font(24));
        rollButton.setOnAction(this::handleRollButton);

        scoreRollButton = new Button();
        scoreRollButton.setText("Score");
        scoreRollButton.setPrefSize(150, 40);
        scoreRollButton.setFont(new Font(24));
        scoreRollButton.setDisable(true);
        scoreRollButton.setOnAction(this::handleScoreButton);

        buttonBox = new HBox(rollButton, scoreRollButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(50);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));

        playAgain = new Button();
        playAgain.setText("Play Again?");
        playAgain.setFont(new Font(24));
        playAgain.setPrefSize(350, 40);
        playAgain.setVisible(false);
        playAgain.setOnAction(this::handleAgainButton);

        buttonPane = new StackPane(buttonBox, playAgain);
        buttonPane.setAlignment(Pos.BOTTOM_CENTER);

        game = new VBox(scoreSheet, dice, buttonPane);
        game.setSpacing(10);
        game.setAlignment(Pos.CENTER);


        // scale game with window size
        game.setMinSize(700, 750);
        game.setMaxSize(700, 750);
        StackPane scaledGame = new StackPane(game);
        NumberBinding maxScale = Bindings.min(scaledGame.widthProperty().divide(700),
                                                scaledGame.heightProperty().divide(750));
        game.scaleXProperty().bind(maxScale);
        game.scaleYProperty().bind(maxScale);

        Scene scene = new Scene(scaledGame, 700, 750, Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yahtzee!");
        primaryStage.show();
    }


    private void handleLockButton(ActionEvent event) {
        for(int i = 0; i < 5; i++) {
            if(event.getSource() == rerollDie[i]) {
                reroll[i] = !reroll[i];
                if(reroll[i]) {
                    lockPic[i].setImage(unlockImage);
                    dieBorder[i].setStroke(Color.TRANSPARENT);
                }
                else {
                    lockPic[i].setImage(lockImage);
                    dieBorder[i].setStroke(Color.FIREBRICK);
                }
                break;
            }
        }

        // Prevent rolling while all dice locked
        boolean allLocked = true;
        for(int i = 0; i < 5; i++) {
            if(reroll[i]) {
                allLocked = false;
            }
        }
        if(allLocked) {
            rollButton.setDisable(true);
        }
        else {
            rollButton.setDisable(false);
        }
    }


    private void handleRollButton(ActionEvent event) {
        player.roll(reroll);
        for(int i = 0; i < 5; i++) {
            diePic[i].setImage(diceImage[player.getDice()[i]]);
        }
        if(player.outOfRolls()) {
            for(int i = 0; i < 5; i++) {
                rerollDie[i].setDisable(true);
                rollButton.setDisable(true);
            }
        }
        else {
            for(int i = 0; i < 5; i++) {
                rerollDie[i].setDisable(false);
            }
        }
        for(int i : validBoxes) {
            scoreButton[i].setDisable(false);
        }
        if(currentScoreBox != -1) {
            score[currentScoreBox].setText(String.valueOf(player.scoreValue(currentScoreBox)));
        }
    }


    public void handleScoreBox(ActionEvent event) {
        if(currentScoreBox != -1) {
            scoreBorder[currentScoreBox].setFill(Color.TRANSPARENT);
            score[currentScoreBox].setText("");
        }
        for(int i = 0; i < 19; i++) {
            if(event.getSource() == scoreButton[i]) {
                if(i != currentScoreBox) {
                    currentScoreBox = i;
                    scoreBorder[i].setFill(Color.PALEGOLDENROD);
                    score[i].setText(Integer.toString(player.scoreValue(i)));
                    scoreRollButton.setDisable(false);
                    break;
                }
                else {
                    scoreRollButton.setDisable(true);
                    currentScoreBox = -1;
                }
            }
        }

    }


    public void handleScoreButton(ActionEvent event) {
        player.enterScore(currentScoreBox);
        scoreBorder[currentScoreBox].setFill(Color.TRANSPARENT);
        scoreButton[currentScoreBox].setDisable(true);
        validBoxes.remove(Integer.valueOf(currentScoreBox));
        currentScoreBox = -1;
        for(int i : Arrays.asList(6, 7, 8, 15, 17, 18)) {
            score[i].setText(Objects.toString(player.getScore()[i], ""));
        }
        scoreRollButton.setDisable(true);
        if(!player.gameOver()) {
            rollButton.setDisable(false);
        }
        else {
            rollButton.setVisible(false);
            scoreRollButton.setVisible(false);
            playAgain.setVisible(true);
        }
        for(int i = 0; i < 5; i++) {
            reroll[i] = true;
            lockPic[i].setImage(unlockImage);
            dieBorder[i].setStroke(Color.TRANSPARENT);
            diePic[i].setImage(diceImage[0]);
        }
        for(int i : validBoxes) {
            scoreButton[i].setDisable(true);
        }
    }


    public void handleAgainButton(ActionEvent event) {
        player.reset();
        reroll = new boolean[]{true, true, true, true, true};
        currentScoreBox = -1;
        validBoxes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13, 14, 16));
        for(int i = 0; i < 19; i++) {
            score[i].setText("");
        }
        rollButton.setVisible(true);
        rollButton.setDisable(false);
        scoreRollButton.setVisible(true);
        playAgain.setVisible(false);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
