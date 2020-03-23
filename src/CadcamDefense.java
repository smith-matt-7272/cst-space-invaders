import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CadcamDefense extends Application
{
	private GraphicsContext gc;
	private Canvas obCanvas;
	
	private Scene obScene;
	private final double DC = 500;
	private BorderPane obPane;
	
	//--Labels and Text--//
	private Color obColor = Color.rgb(255,240,0);  //Standard label and text color
	private HBox obBottom;

	
	//--Player stats--//
	private Player obPlayer;
	private int nLevel = 1;
	private ArrayList<Player> highScores;
	private RandomAccessFile obRaf;
	private final String FILE_LOCATION = "binFiles/scores.dat";
	
	//--Sprites--//
	private ArrayList<Image> obUFOImages;
	private ArrayList<Sprite> ufoList;

	private Sprite obBoom;
	private Sprite obLauncher;
	private Sprite obMissile;
	
	//--Level Functions--//
	private int nUfoCount;
	private double dMissileSpeed = -1;
	private double dGravity;
	private int nRoundKills;
	private boolean bGameOver = false;
	private boolean bScoreScreen = false;
	private boolean bCanLaunch = true;
	private double dShimmy;
	private boolean bShimmyToggle;
	private int nLaunchDelay = 1000;
	

	@Override
	public void start(Stage obStage) throws Exception
	{
		obRaf = new RandomAccessFile(FILE_LOCATION,"rw");
		welcomeScreen(obStage);
		loadImages("images/ufo");
		this.highScores = RafUtils.loadHighScores(obRaf);
	}

	/**
	 * 
	 * Splash screen; user enters their player name here
	 * 
	 * @param obStage
	 */
	public void welcomeScreen(Stage obStage)
	{
		obPane = setDefaultBackdrop();
		obScene = new Scene(obPane, DC, DC);
		
		StackPane obFront = new StackPane();
		HBox obTitle = new HBox();
		obTitle.setAlignment(Pos.CENTER);
		Label lblTitle = new Label("CADCAM Invasion Defence");
		lblTitle.setTextFill(obColor);
		lblTitle.setStyle("-fx-font-size: 36;");
		obTitle.getChildren().addAll(lblTitle);
		
		HBox obInput = new HBox(15);
		obInput.setAlignment(Pos.BOTTOM_CENTER);
		obInput.setPadding(new Insets(15));
		Label lblPlayer = new Label("Enter Your Name:");
		lblPlayer.setPrefWidth(125);
		lblPlayer.setTextFill(obColor);
		lblPlayer.setStyle("-fx-font-size: 14");
		TextField txtPlayer = new TextField();
		txtPlayer.setMaxWidth(150);
		obInput.getChildren().addAll(lblPlayer,txtPlayer);
		
		obFront.getChildren().addAll(obTitle,obInput);
		
		obBottom = new HBox(35);
		Button btnStart = new Button("Start Game");
		btnStart.setDisable(true);
		btnStart.setOnAction(e-> 
		{
			obBottom.getChildren().clear();
			obPane.getChildren().clear();
			this.obPlayer = new Player(txtPlayer.getText());
			transitionScreen(obStage, "Welcome " + this.obPlayer.getName(), "Begin");
		});
		
		txtPlayer.setOnKeyReleased(e-> {
			if(txtPlayer.getText().length()>=4)
			{
				btnStart.setDisable(false);
			}
			else 
			{
				btnStart.setDisable(true);
			}
		});
		
		obBottom.getChildren().add(btnStart);
		obBottom.setAlignment(Pos.CENTER);
		obBottom.setPadding(new Insets(15));
		
		obPane.setCenter(obFront);
		obPane.setBottom(obBottom);
		
		obStage.setTitle("UFO Hyper Defence Deluxe");
		obStage.setScene(obScene);
		obStage.show();
	}
	
	/**
	 * 
	 * Transition screen used during all transitions between Welcome, game Screens,
	 * and high score screen.
	 * 
	 * @param obStage
	 * @param sTransition
	 * @param sButton
	 */
	public void transitionScreen(Stage obStage, String sTransition, String sButton)
	{
		obPane = setDefaultBackdrop();
		obScene.setRoot(obPane);
		
		StackPane obIntro =  new StackPane();
		obIntro.setAlignment(Pos.CENTER);
		Label lblIntro = new Label(sTransition);
		lblIntro.setTextFill(obColor);
		lblIntro.setStyle("-fx-font-size: 32;");
		
		obIntro.getChildren().add(lblIntro);
		
		Button btn = new Button(sButton);
		obBottom.getChildren().add(btn);
		if(!bGameOver && !bScoreScreen)
		{
			btn.setOnAction(e-> {
				if(this.ufoList != null)
				{
					this.ufoList.clear();
				}
				if(this.nLevel > 1)
				{
					gc.restore();
				}
				obBottom.getChildren().clear();
				obPane.getChildren().clear();
				playGame(obStage);
			});
			
			if(this.nLevel==1)
			{
				Label lblInstruct = new Label("\n\n\n\n\n\n\n\nKeyboard:  Left/Right to Move, Up to Launch Missile");
				lblInstruct.setTextFill(obColor);
				lblInstruct.setStyle("-fx-font-size: 16;");
				lblInstruct.setAlignment(Pos.BOTTOM_CENTER);
				obIntro.getChildren().add(lblInstruct);
			}

			FadeTransition fadeIntro = new FadeTransition(Duration.millis(3000), lblIntro);
			fadeIntro.setFromValue(0);
			fadeIntro.setToValue(1);
			fadeIntro.setCycleCount(2);
			fadeIntro.setAutoReverse(true);
			fadeIntro.play();
			
			if(this.nLevel > 1)
			{
				Button btnStop = new Button("Stop Playing");
				obBottom.getChildren().add(btnStop);
				btnStop.setOnAction(e-> {
					obBottom.getChildren().clear();
					obPane.getChildren().clear();
					gameOverScreen(obStage);
				});
			}
		}
		else
		{
			btn.setOnAction(e-> {
				obBottom.getChildren().clear();
				obPane.getChildren().clear();
				gameOverScreen(obStage);
			});
		}
		obPane.setCenter(obIntro);
		obPane.setBottom(obBottom);

		obStage.setScene(obScene);
		obStage.show();
	}
	
	/*
	 * 
	 * Game screen generated here, as well as game play threads started
	 * 
	 * 
	 */
	public void playGame(Stage obStage)
	{
		setLevelStats();
		prepareUFOs();
		obPane = setDefaultBackdrop();
		obScene.setRoot(obPane);
		
		obBottom.setAlignment(Pos.CENTER);
		
		//Create a canvas to draw on
		obCanvas = new Canvas(DC, DC);
		this.gc = this.obCanvas.getGraphicsContext2D();

		//Event handlers for launching missiles; can't interact with the launcher
		//or launch any missiles until bCanLaunch is reset
		obScene.setOnKeyPressed(e->
		{
			switch(e.getCode())
			{
			case LEFT:
				if(bCanLaunch) {
					Platform.runLater(()->moveLaunch(-8));
					break;
				}
			case RIGHT:
				if(bCanLaunch) {
					Platform.runLater(()->moveLaunch(8));
					break;
				}				
			case UP:
				if(bCanLaunch)
				{
					this.bCanLaunch = false;
					Platform.runLater(()->checkCanLaunch());
					Platform.runLater(()->launchMissile());
				}
				break;
			default:
				break;
			}
		});
		
		obLauncher = new Sprite(new Image("file:images/missileLauncher.png"), 250,465);
		obLauncher.render(gc);
		
		for(Sprite ob : ufoList)
		{
			startTask(ob);
		}
		
		//Runs a thread which checks for bGameOver to flip
		Platform.runLater(()-> {
			checkGameOver(obStage);
		});
		
		//Runs thread to determine when player finishes a level
		Platform.runLater(()-> {
			checkWinStatus(obStage);
		});
		
		//Runs a thread which helps regulate the UFO x-axis movement
		Platform.runLater(()-> {
			runShimmy();
		});
		
		obPane.setCenter(obCanvas);
		obPane.setBottom(obBottom);

		obStage.setScene(obScene);
		obStage.show();
	}
	
	/*
	 * Starts thread to toggle the bShimmy at
	 * regular intervals
	 * 
	 */
	private void runShimmy()
	{
		Thread obThread = new Thread(()->toggleShimmy());
		obThread.setDaemon(true);
		obThread.start();
	}
	
	/**
	 * Flips bShimmy, which changes the x-axis draw direction
	 * of the UFOS
	 */
	private void toggleShimmy()
	{
		while(true)
		{
			if(ufoList.size()>0)
			{
				if(this.bShimmyToggle || ufoList.get(ufoList.size()-1).getCoors().getX() >= 450 )
				{
					this.bShimmyToggle = false;
				}
				else if(!this.bShimmyToggle || ufoList.get(0).getCoors().getX() <= 10)
				{
					this.bShimmyToggle = true;
				}
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Loads the UFOS into arrayList, and spaces out their spawn location
	 * based on how many will spawn
	 */
	private void prepareUFOs()
	{
		ufoList = new ArrayList<>();
		Double dPoint = (DC/(double)(nUfoCount+1));
		for(int i=0; i < nUfoCount;i++)
		{
			ufoList.add(new Sprite(obUFOImages.get(0), dPoint*(i+1)-20, 20));
		}
	}
		
	/*
	 * Checked at the start of each playGame method
	 * Determines the variables involved in the difficulty of each level
	 * 
	 */
	private void setLevelStats()
	{
		switch(nLevel)
		{
			case 1:
				this.dGravity = 1;
				this.nUfoCount = 4;
				this.dShimmy = 1;
				break;
			
			case 2:
				this.dGravity = 1;
				this.nUfoCount = 5;
				this.dShimmy = 1;
				break;
				
			case 3:
				this.dGravity = 1;
				this.nUfoCount = 6;
				this.dShimmy = 1;
				break;
				
			case 4:
				this.dGravity = 1.5;
				this.nUfoCount = 4;
				this.dShimmy = 1;
				break;
				
			case 5:
				this.dGravity = 1.5;
				this.nUfoCount = 5;
				this.dShimmy = 1;
				break;
				
			case 6:
				this.dGravity = 1.5;
				this.nUfoCount = 6;
				this.dShimmy = 1;
				break;
				
			case 7:
				this.dGravity = 2;
				this.nUfoCount = 5;
				this.dShimmy = 1.5;
				break;
			case 8:
				this.dGravity = 2;
				this.nUfoCount = 5;
				this.dShimmy = 1.5;
				break;
			case 9:
				this.dGravity = 2;
				this.nUfoCount = 6;
				this.dShimmy = 1.5;
				break;
			case 10:
				this.dGravity = 2.5;
				this.nUfoCount = 6;
				this.dShimmy = 2;
				break;
		}
	}
	
	/**
	 * 
	 * Follows the transitionScreen when a game is lost
	 * (that is when bGameOver is true)
	 * Displays high scores based off the RAF file scores.dat
	 * and current play through
	 * What a MONSTROSITY!
	 * @param obStage
	 */
	public void gameOverScreen(Stage obStage)
	{
		obPane = setDefaultBackdrop();
		obScene.setRoot(obPane);
		
		highScores.add(this.obPlayer);
		highScores.sort((x,y)->y.getLevel() - x.getLevel());
		RafUtils.generateRAF(highScores);
		
		HBox obTop = new HBox(30);
		obTop.setAlignment(Pos.CENTER);
		obTop.setPadding(new Insets(20));
		Label lblYour = new Label("Your score:");
		lblYour.setTextFill(obColor);
		lblYour.setStyle("-fx-font-size: 24;");
		Label lblYourScore = new Label("Level "+Integer.toString(this.obPlayer.getLevel()));
		lblYourScore.setTextFill(obColor);
		lblYourScore.setStyle("-fx-font-size: 24;");
		obTop.getChildren().addAll(lblYour, lblYourScore);
		
		HBox obBottom = new HBox(30);
		obBottom.setAlignment(Pos.CENTER);
		obBottom.setPadding(new Insets(15));
		GridPane obGrid = new GridPane();
		obGrid.setAlignment(Pos.CENTER);
		obGrid.setVgap(10);
		obGrid.setHgap(30);
		Label lblName = new Label("Player Name");
		lblName.setTextFill(obColor);
		lblName.setStyle("-fx-font-size: 24;");
		Label lblLevel = new Label("Level Reached");
		lblLevel.setTextFill(obColor);
		lblLevel.setStyle("-fx-font-size: 24;");
		Label lblFirst = new Label("First");
		lblFirst.setTextFill(obColor);
		lblFirst.setStyle("-fx-font-size: 24;");
		Label lblSecond = new Label("Second");
		lblSecond.setTextFill(obColor);
		lblSecond.setStyle("-fx-font-size: 24;");
		Label lblThird = new Label("Third");
		lblThird.setTextFill(obColor);
		lblThird.setStyle("-fx-font-size: 24;");
		Label lblFirstName = new Label(highScores.get(0).getName());
		lblFirstName.setTextFill(obColor);
		lblFirstName.setStyle("-fx-font-size: 24;");
		Label lblFirstLvl = new Label(Integer.toString(highScores.get(0).getLevel()));
		lblFirstLvl.setTextFill(obColor);
		lblFirstLvl.setStyle("-fx-font-size: 24;");
	
		obGrid.add(lblName, 1, 0);
		obGrid.add(lblLevel, 2, 0);
		obGrid.add(lblFirst, 0, 1);
		obGrid.add(lblFirstName, 1, 1);
		obGrid.add(lblFirstLvl, 2, 1);
		obGrid.add(lblSecond, 0, 2);
		obGrid.add(lblThird, 0, 3);
		
		if(this.highScores.size()>1)
		{
			Label lblSecondName = new Label(highScores.get(1).getName());
			lblSecondName.setTextFill(obColor);
			lblSecondName.setStyle("-fx-font-size: 24;");
			Label lblSecondLvl = new Label(Integer.toString(highScores.get(1).getLevel()));
			lblSecondLvl.setTextFill(obColor);
			lblSecondLvl.setStyle("-fx-font-size: 24;");
			obGrid.add(lblSecondName, 1, 2);
			obGrid.add(lblSecondLvl, 2, 2);
		}
		if(this.highScores.size()>2) {
			Label lblThirdName = new Label(highScores.get(2).getName());
			lblThirdName.setTextFill(obColor);
			lblThirdName.setStyle("-fx-font-size: 24;");
			Label lblThirdLvl = new Label(Integer.toString(highScores.get(2).getLevel()));
			lblThirdLvl.setTextFill(obColor);
			lblThirdLvl.setStyle("-fx-font-size: 24;");
			obGrid.add(lblThirdName, 1, 3);
			obGrid.add(lblThirdLvl, 2, 3);
		}
		Button btnStartOver = new Button("Try Again");
		btnStartOver.setOnAction(e-> {
			obBottom.getChildren().clear();
			obPane.getChildren().clear();
			this.ufoList.clear();
			this.bGameOver = false;
			this.bScoreScreen = false;
			this.nLevel = 1;
			this.nRoundKills = 0;
			obPlayer.setScore(0);
			welcomeScreen(obStage);
		});
		
		Button btnExit = new Button("Exit");
		btnExit.setOnAction(e-> System.exit(0));
		
		obBottom.getChildren().addAll(btnStartOver,btnExit);
		obPane.setTop(obTop);
		obPane.setBottom(obBottom);
		obPane.setCenter(obGrid);
		
		obStage.setScene(obScene);
		obStage.show();
	}
	
	/*
	 * Generates the background image for each scene
	 * 
	 */
	private BorderPane setDefaultBackdrop()
	{
		BorderPane obPane = new BorderPane();
		obPane.setStyle("-fx-background-color: linear-gradient(#000040,#000036,#000028,#000014);");
		
		Rectangle[] obStars = new Rectangle[100];
		
		for(Rectangle star : obStars)
		{
			star = new Rectangle();
			star.setHeight(2);
			star.setWidth(2);
			star.setLayoutX(Math.random()*475+10);
			star.setLayoutY(Math.random()*400+10);
			star.setFill(Color.WHITE);
			obPane.getChildren().add(star);
		}
		return obPane;
	}
	
	/**
	 * Starts the thread which monitors if a
	 * win scenario occurs, in which case it will
	 * initiate the TransitionScreen
	 * 
	 * @param obStage
	 */
	private void checkWinStatus(Stage obStage)
	{
		Thread obThread = new Thread(()-> runGameStatus(obStage));
		obThread.setDaemon(true);
		obThread.start();
	}
	
	/**
	 * If the count of UFOs destroyed matches
	 * the UFO count spawned in the level,
	 * initiates a transitionScreen
	 * 
	 * Occasionally glitches, and initiates
	 * 
	 * @param obStage
	 */
	private void runGameStatus(Stage obStage)
	{
		try
		{
			while(true)
			{
				if(nRoundKills == nUfoCount)
				{
					Platform.runLater(()-> {
						this.nRoundKills = 0;
						this.nLevel++;
						obPlayer.setLevel(this.nLevel);
						
						transitionScreen(obStage, "Level "+this.nLevel, "Continue");
					});
					break;
				}
				Thread.sleep(25);
			}
		}
		catch(InterruptedException exp)
		{
			exp.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Starts thread which initiates  transition to GameOverScreen
	 * @param obStage
	 */
	private void checkGameOver(Stage obStage)
	{
		Thread obThread = new Thread(()-> runGameOver(obStage));
		obThread.setDaemon(true);
		obThread.start();
	}
	/**
	 * 
	 * Initiates transitionScreen to the GameOver screen when
	 * bGameOver is true (a UFO reaches bottom of screen)
	 * 
	 * @param obStage
	 */
	private void runGameOver(Stage obStage)
	{
		try
		{
			while(true)
			{
				if(bGameOver)
				{
					Platform.runLater(()-> {

						this.obPane.getChildren().clear();
						this.obBottom.getChildren().clear();
						transitionScreen(obStage, "Game Over","See Scores");
					});
					break;
				}				
				Thread.sleep(25);
			}
		}
		catch(InterruptedException exp)
		{
			
		}
	}
	
	public void launchMissile()
	{
		obLauncher.renderNull(this.gc);
		this.obMissile = new Sprite(new Image("file:images/newMissile.png"),
				obLauncher.getCoors().getX(), obLauncher.getCoors().getY());
		
		startMissile(obMissile);
	}
	
	public void checkCanLaunch()
	{
		Thread obThread = new Thread(()-> runLaunchCheck());
		obThread.setDaemon(true);
		obThread.start();
	}
	
	/**
	 * 
	 * After a missile is launched, this uses the timer to determine
	 * when the missile launcher is rendered again.
	 * 
	 */
	public void runLaunchCheck()
	{
		try
		{
			Thread.sleep(this.nLaunchDelay);
			this.bCanLaunch=true;
		}
		catch(InterruptedException exp)
		{
			exp.printStackTrace();
		}
	}

	/**
	 * Handles the position and render of the Missile Launcher
	 * 
	 * @param dInc
	 */
	public void moveLaunch(double dInc)
	{
		obLauncher.moveX(dInc, this.gc);
	}

	/**
	 * Handles the position and render of the UFO
	 * sprite.
	 * 
	 * @param obUFO
	 */
	public void gravity(Sprite obUFO)
	{
		obUFO.moveY(this.dGravity, this.gc);
		if(bShimmyToggle)
		{
			obUFO.moveX(this.dShimmy, this.gc);
		}
		else
		{
			obUFO.moveX(-1*this.dShimmy, this.gc);
		}

		
		if(obUFO.getCoors().getY() >= 460)
		{
			this.bGameOver = true;
		}
	}
	
	/**
	 * After missile is launched, renders the image as it moves,
	 * "accelerating" it as it climbs the canvas
	 * 
	 * @param obSprite
	 */
	private void track(Sprite obSprite)
	{
		if(obSprite.getCoors().getY()>425)
		{
			obSprite.moveY(this.dMissileSpeed, this.gc);
		}
		else if(obSprite.getCoors().getY() <= 425 && obSprite.getCoors().getY() > 350)
		{
			obSprite.moveY(this.dMissileSpeed*2, this.gc);
		}
		else if(obSprite.getCoors().getY() <= 350 && obSprite.getCoors().getY() > 200)
		{
			obSprite.moveY(this.dMissileSpeed*3, this.gc);
		}
		else if(obSprite.getCoors().getY() <= 200)
		{
			obSprite.moveY(this.dMissileSpeed*4, this.gc);
		}
		
		for(Sprite ob : ufoList)
			{
				if(obSprite.intersects(ob))
				{
					obBoom = new Sprite(new Image("file:images/newBoom2.png"), obSprite.getCoors().getX()-25, obSprite.getCoors().getY()-25);
					this.nRoundKills++;
					obPlayer.setScore(obPlayer.getScore()+1);
					System.out.printf("Round Kills: %d UFO Count: %d\n",this.nRoundKills, this.nUfoCount);
					Thread obThread = new Thread(()-> goBoom(obBoom));
					obThread.setDaemon(true);
					obThread.start();
				}
			}
		}

	/**
	 * When missile sprite intercepts UFO sprite, this handles the
	 * animation of the explosion
	 * 
	 * @param obSprite
	 */
	private void goBoom(Sprite obSprite)
	{
		try
		{
			double dOpacity = 1.0;
			while (dOpacity > 0)
			{
				obSprite.renderNull(this.gc);
				obSprite.setOpacity(dOpacity);
				dOpacity -= .1;
				obSprite.render(this.gc);
				Thread.sleep(100);
			}
		
			obSprite.renderNull(this.gc);
		}
		catch(InterruptedException exp)
		{
			exp.printStackTrace();
		}
	}
	
	/**
	 * Starts the...start of the missile track
	 * 
	 * @param obSprite
	 */
	private void startMissile(Sprite obSprite)
	{
		Thread obThread = new Thread(()-> runMissileTrack(obSprite));
		obThread.setDaemon(true);
		obThread.start();
	}
	
	/**
	 * Starts the missile track thread
	 * @param obSprite
	 */
	private void runMissileTrack(Sprite obSprite)
	{
		try
		{
			while(true)
			{
				if (obSprite.isDead())
				{
					obSprite.renderNull(this.gc);
					break;
				}
				Platform.runLater(() -> track(obSprite));
				Thread.sleep(25);
			}
		}
		catch(InterruptedException exp)
		{
			exp.printStackTrace();
		}
	}
	
	/*
	 * Starts the UFO animation
	 */
	private void startTask(Sprite obUFO)
	{
		Thread obThread = new Thread(() -> runTask(obUFO));
		obThread.setDaemon(true);
		obThread.start();
	}
	
	/**
	 * Handles the "animation" of the
	 * UFO based on its position.
	 * 
	 * @param obUFO
	 */
	private void runTask(Sprite obUFO)
	{
		//handle the different images
		try
		{
			Thread.sleep((int) (Math.random()* 4000));
			int nPos = 0;
			while (true)
			{
				if (obUFO.isDead())
				{
					obUFO.renderNull(this.gc);
					ufoList.remove(obUFO);
					break;
				}
				if(this.bGameOver)
				{
					break;
				}
				obUFO.setImage(this.obUFOImages.get(nPos % this.obUFOImages.size()));
				nPos++;
				Platform.runLater(() -> gravity(obUFO));
				Thread.sleep(25);
			}

		}
		catch(InterruptedException exp)
		{
			exp.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * Loads images used for UFOs
	 * 
	 * @param sPath
	 */
	private void loadImages(String sPath)
	{
		File obDir = new File(sPath);
		if (!obDir.isDirectory())
		{
			return;
		}
		this.obUFOImages = new ArrayList<>();
		try
		{
			for(String sVal: obDir.list())
			{
				if (sVal.matches(".*png"))
				{
					File obFile = new File(obDir.getAbsolutePath() + "/" + sVal);
					this.obUFOImages.add(new Image(new FileInputStream(obFile)));
				}
			}
		}
		catch (FileNotFoundException exp)
		{
			exp.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Application.launch(args);
	}
}
