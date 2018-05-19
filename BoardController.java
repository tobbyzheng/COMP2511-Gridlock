import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;


import java.util.ArrayList;


public class BoardController extends Controller {
	// public static final int  VERT=0;
	// public static final int HORIZ=1;
	private static final int  GOALCAR =5;
    private static final String GAME_OVER = "GAME OVER";
    private static final String GAME_WON = "YOU WIN!";
    private static final int animTime = 500;

    private GameEngine engine;

	@FXML
    private Pane boardPane;
    @FXML
    private Label totalTime;
    @FXML
    private Button buttonPause;
    @FXML
    private Button buttonRestart;
    
    @FXML
    private Button buttonNewGame;
    
    @FXML
    private Button buttonHint;

    private Rectangle curtain;
    private Label message;
    private double squareWidth;
    private int nSquares;
    private Timeline countDown;
    private Timeline winCountDown;
    private final int totalSeconds;  // The duration of game, should not changed
    private int currSeconds;
    private boolean running;
    private ArrayList<Car> workload;
    private boolean GameWon = false;
    private boolean animating = false;
    private Car goalCar;

    private final Color boardColor = Color.ORANGE;

    public BoardController(GameEngine engine) {
        this.engine = engine;
        nSquares = 6; //this will be replaced dynamically.
        totalSeconds = currSeconds = 3600;
        workload = new ArrayList<>();
        running = true; // this is to check whether the game is paused. Initially, it's running.
    }

    @FXML
    public void initialize() {
        this.squareWidth = boardPane.getPrefWidth()/nSquares;

        // init curtain
        curtain = new Rectangle(boardPane.getPrefWidth(), boardPane.getPrefHeight(), boardColor);
        curtain.setX(0);
        curtain.setY(0);

        // init game over message
        message = new Label("");
        message.setFont(new Font("DejaVu Sans Mono for Powerline Bold", 40));
        message.setTextFill(Color.WHITESMOKE);
        // place the label in the centre
        message.layoutXProperty().bind(boardPane.widthProperty().subtract(message.widthProperty()).divide(2));
        message.layoutYProperty().bind(boardPane.heightProperty().subtract(message.heightProperty()).divide(2));

        //GenNewPuzzle();

        // must call drawBoard after curtain and message are init'd, and after GenNewPuzzle
        drawBoard();

        // init buttons
        buttonPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (running) {
                    countDown.pause();
                    running = false;
                    buttonPause.setText("Resume");
                    curtain.setVisible(true);
                    curtain.toFront();
                } else {
                    countDown.play();
                    running = true;
                    buttonPause.setText("Pause");
                    curtain.setVisible(false);
                    //curtain.toFront();
                }
            }
        });
        
        buttonNewGame.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
            	//workload = engine.GetCarList();
            	engine.getNewPuzzle();
                boardPane.getChildren().clear();
                currSeconds = totalSeconds;
                drawBoard();
                GameWon=false;
                animating=false;
                countDown.playFromStart();
            }

        });

        buttonRestart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	//workload = engine.GetCarList();
            	
            	engine.RestartPuzzle();
                boardPane.getChildren().clear();
                currSeconds = totalSeconds;
                GameWon=false;
                animating=false;
                drawBoard();
                countDown.playFromStart();
            }
        });
        

        buttonHint.setOnMouseClicked(new EventHandler <MouseEvent>() {
        	@Override
        	public void handle (MouseEvent event) {
        		System.out.println("HINT");
        		int[] arr =engine.getNextMove();
        		//System.out.println(arr);
        		Car car =findCar(arr[0], arr[1]);
        		if(car!=null) {
        			animating=true;
        			car.CarMakeAnimatingMove(arr[2], arr[3], 2000);
        		}
        		else {
        			//System.out.println("R"+ arr[0] + " " + "C"+arr[1]);
        		}
        	}
        });

        // init timer
        countDown = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currSeconds--;
                totalTime.setText(convertTime(currSeconds));
                if (currSeconds <= 0) {
                    stopGame(GAME_OVER);
                }
            }
        }));
        countDown.setCycleCount(Animation.INDEFINITE);
        countDown.playFromStart(); // initialise the timer at the first time

        winCountDown = new Timeline(new KeyFrame(Duration.millis(animTime-43), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopGame(GAME_WON);
            }
        }));
        winCountDown.setCycleCount(1);

    }

    private void drawBoard() {
        running = true;
        buttonPause.setDisable(false);
        buttonPause.setText("Pause");
        buttonHint.setDisable(false);
        totalTime.setText(convertTime(totalSeconds));
        curtain.setVisible(false);
        message.setVisible(false);

        Rectangle[][] rec = new Rectangle[nSquares][nSquares];

        for (int i = 0; i < nSquares; i ++) {
            for (int j = 0; j < nSquares; j ++) {
                rec[i][j] = new Rectangle();
                rec[i][j].setX(i * squareWidth);
                rec[i][j].setY(j * squareWidth);
                rec[i][j].setWidth(squareWidth);
                rec[i][j].setHeight(squareWidth);
                rec[i][j].setFill(boardColor);
                rec[i][j].setStroke(Color.BLUE);
                boardPane.getChildren().add(rec[i][j]);
            }
        }
        drawBorder();
        drawCars();
        boardPane.getChildren().add(curtain);
        boardPane.getChildren().add(message);
    }


    // draw border for the board
    private void drawBorder() {
        Line l= new Line();
        l.setEndY(nSquares*squareWidth);
        boardPane.getChildren().add(l);
        l=new Line();
        l.setEndX(nSquares*squareWidth);
        boardPane.getChildren().add(l);
        l=new Line();
        l.setStartY(nSquares*squareWidth);
        l.setEndY(nSquares*squareWidth);
        l.setEndX(nSquares*squareWidth);
        boardPane.getChildren().add(l);
        l=new Line();
        l.setStartX(nSquares*squareWidth);
        l.setEndX(nSquares*squareWidth);
        l.setEndY(nSquares*squareWidth);
        boardPane.getChildren().add(l);
    }

    private void drawCars() {
        workload.clear();
    	workload = engine.GetCarList();
        for(Car c: workload) {
            c.frontEndCarConstructor(squareWidth, boardPane.getBoundsInLocal(),this);
            Node car = c.getCar();
            boardPane.getChildren().add(car);
            car.toFront();
            if (c.getCarType() == GOALCAR) {
                goalCar = c;
            }
        }

    }

    public boolean checkIntersection(Car car) {
        Bounds bounds = car.getCar().getBoundsInParent();
        //Bounds bounds = new BoundingBox(tmpBounds.getMinX()-0.5, tmpBounds.getMinY()-0.5,
        //        tmpBounds.getWidth()+1, tmpBounds.getHeight()+1);
        for (Car c: workload) {
            if (c == car) {
                continue;
            }
            Bounds cbounds = c.getCar().getBoundsInParent();
            if(cbounds.intersects(bounds)) {
                return true;
            }
        }
        return false;
    }

    private void stopGame(String msg) {
        countDown.stop();
        buttonPause.setDisable(true);
        buttonHint.setDisable(true);
        curtain.setVisible(true);
        curtain.toFront();
        message.setText(msg);
        message.setVisible(true);
        message.toFront();
    }
    /*
    public Bounds getFreeZone(Car car) {
        Bounds bounds = car.getCar().getBoundsInParent();
        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();
        for (Car c: workload) {
            if (c == car) {
                continue;
            }
            Bounds cbounds = c.getCar().getBoundsInParent();
            if (cbounds.getMaxX() >= minX &&) {
                minX = cbounds.getMaxX();
            }

            if(cbounds.intersects(bounds)) {
            }
        }

    }
    */

    private String convertTime(long secondDelta) {
        // this snippet taken from https://stackoverflow.com/questions/43892644
        long seconds = 1;
        long minutes = seconds * 60;
        long hours = minutes * 60;

        long elapsedHours = secondDelta / hours;
        secondDelta = secondDelta % hours;

        long elapsedMinutes = secondDelta / minutes;
        secondDelta = secondDelta % minutes;

        long elapsedSeconds = secondDelta;
        return String.format("%02d", elapsedHours) + ":" +
                String.format("%02d", elapsedMinutes) + ":" +
                String.format("%02d", elapsedSeconds);
    }

    
    public void MakeMove(int oldR, int oldC, int r, int c) {
    	if(!GameWon) {
			if(engine.MakeMove(oldR, oldC, r,c)) {
				//Game has finished
	    		animating=true;
				goalCar.CarMakeAnimatingMove(goalCar.getR(), engine.getBoardSize()-2, animTime);
                GameWon=true;
				//return true;
			}
			//return false;
    	}
    }
    

    
    public boolean GetAnimating() {
    	return animating;
    }
    
    public void AnimatingFin() {
    	animating=false;
    	if (GameWon) {
            winCountDown.playFromStart();
        }
    }

    /*
    private Car findGoalCar() {
    	for(Car car:workload) {
    		if(car.getCarType()==GOALCAR) {
    			return car;
    		}
    	}
    	return null;
    }
    */

    private Car findCar(int r,int c) {
    	for(Car car: workload) {
    		if(r==car.getR() && c==car.getC()) {
    			return car;
    		}
    	}
    	return null;
    }
    
    public void AddCartoPane(Node c) {
    	boardPane.getChildren().remove(c);
        boardPane.getChildren().add(c);
        c.toFront();
    }
    
    
	public int[] FindMoves(int r, int c) {
		return engine.FindMoves(r, c);
	}

    
   /* private void GenNewPuzzle(){
    	//puzzle = engine.getNewPuzzle();
        //puzzle = new Puzzle(6,6);
        //puzzle.printBoard();
    }*/
}
