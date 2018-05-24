import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;


public class MenuController extends Controller{
	private Main main;
	private GameEngine engine;
	private Stage stage;
	private boolean showHelp;

	public MenuController(Main main, GameEngine engine, Stage s) {
		this.main = main;
		this.engine = engine;
		this.stage = s;
		showHelp = false;
	}
	@FXML
    public void initialize() {
		ModeGroup = new ToggleGroup();
		labelDifficulty.setText("School Certificate");
		toggleTimed.setToggleGroup(ModeGroup);
		toggleFreePlay.setToggleGroup(ModeGroup);
		toggleStory.setToggleGroup(ModeGroup);

        buttonStartGame.setOnAction(new EventHandler<ActionEvent>() {
        	@Override public void handle(ActionEvent e) {
        		Toggle t = ModeGroup.getSelectedToggle();
        		Mode mode = Mode.TIMED;
        		if(t==toggleFreePlay) {
        			mode = Mode.FREEPLAY;

        		}
        		else if(t==toggleTimed) {
        			mode = Mode.TIMED;

        		}
        		else if(t==toggleStory) {
        			mode = Mode.STORY;
        		}

        		
        			
        		engine.setMode(mode);
        		engine.getNewPuzzle();
        		main.ShowGameScreen();
        	}
        });
        
        ModeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
        	public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle newToggle) {
        		if(newToggle!=toggleStory ) {
        				sliderDifficulty.setDisable(false);
                		double difficulty = sliderDifficulty.getValue();
                		String text = getDiffString(difficulty);
                		labelDifficulty.setText(text);
        				
        			}
        		if(newToggle==toggleStory) {
        			if(newToggle.isSelected())
        			sliderDifficulty.setDisable(true);
        			labelDifficulty.setText("Story Mode");
        		}
        	}
        });
        
        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });

        sliderDifficulty.valueProperty().addListener(new ChangeListener<Number>() {
        	public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
        		//System.out.println(new_val);
        		double difficulty = sliderDifficulty.getValue();
        		String text = getDiffString(difficulty);
        		labelDifficulty.setText(text);

        		engine.SetDifficulty((int)difficulty);
        	}
        });

        initHelp();

        buttonHelp.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (showHelp) {
					helpMsg[0].setVisible(false);
					helpMsg[1].setVisible(false);
					showHelp = false;

				} else {
					helpMsg[0].setVisible(true);
					helpMsg[1].setVisible(true);
					showHelp = true;
				}
			}
		});
	}

	private void initHelp() {
		Rectangle rect = new Rectangle();
		int helpWidth = 300;
		int helpHeight = 300;
		rect.setWidth(helpWidth);
		rect.setHeight(helpHeight);
		rect.setFill(Color.MINTCREAM);
		rect.setX(menuPane.getPrefWidth()-helpWidth-50);
		rect.yProperty().bind(buttonHelp.layoutYProperty());
		rect.setVisible(false);
		Text helpMsgText = new Text("ahahahahaha this is empty hahahahhahaha\n this\n is very looooooong hahahahaha");
		helpMsgText.setFont(new Font("DejaVu Sans Mono for Powerline Bold", 16));
		helpMsgText.setWrappingWidth(helpWidth-30);
		helpMsgText.setFill(Color.SEAGREEN);
		helpMsgText.xProperty().bind(rect.xProperty().add(20));
		helpMsgText.yProperty().bind(rect.yProperty().add(30));
		helpMsgText.setVisible(false);
		helpMsg = new Node[2];
		helpMsg[0] = rect;
		helpMsg[1] = helpMsgText;


		menuPane.getChildren().add(rect);
		menuPane.getChildren().add(helpMsgText);
	}

	private String getDiffString(double difficulty) {
    		int diff = (int) Math.round(difficulty);
    		String text;
    		switch(diff) {
    		case 0:
    			text="School Certificate";
    			break;
    		case 1:
    			text="Higher School Certificate";
    			break;
    		case 2:
    			text="Bachelor Degree";
    			break;
    		case 3:
    			text="Masters Degree";
    			break;
			case 4:
				text="Doctoral Degree";
				break;
			default:
				text="SC";
			}
    		return text;

        }
        
        @FXML
        private Button buttonStartGame;
        @FXML
        private ToggleGroup ModeGroup;
        @FXML
        private Slider sliderDifficulty;
        @FXML
        private ToggleButton toggleTimed;
        @FXML
        private Button buttonMultiplayer;
        @FXML
        private Button buttonExit;
        @FXML
        private Button buttonHelp;
        @FXML
        private ToggleButton toggleFreePlay;
        @FXML
        private ToggleButton toggleStory;
        @FXML
        private Label labelDifficulty;
        @FXML
		private Pane menuPane;
		private Node[] helpMsg;

}
