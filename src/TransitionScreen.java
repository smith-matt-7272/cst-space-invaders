import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TransitionScreen extends BorderPane
{
	
	private int nLevel;
	
	
	public TransitionScreen()
	{
		super();
		setDefaultBackdrop();
	}
	
	public void setDefaultBackdrop()
	{

		this.setStyle("-fx-background-color: linear-gradient(#000040,#000036,#000028,#000014);");
		
		Rectangle[] obStars = new Rectangle[100];
		
		for(Rectangle star : obStars)
		{
			star = new Rectangle();
			star.setHeight(2);
			star.setWidth(2);
			star.setLayoutX(Math.random()*475+10);
			star.setLayoutY(Math.random()*400+10);
			star.setFill(Color.WHITE);
			this.getChildren().add(star);
		}
	}

}
