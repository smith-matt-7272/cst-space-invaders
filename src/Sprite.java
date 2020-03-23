import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {

    private Image image;
    private Coors obCoor;
   
    private double width;
    private double height;
    public static Lock obLock = new ReentrantLock();
    
    private boolean bDead;

    public double dOpacity;
    
    public Sprite(Image image,  double nXPos, double nYPos) 
    {
      setImage(image);
       obCoor = new Coors(nXPos,nYPos);
       this.bDead = false;
       this.dOpacity = 1.0;
       
    }
    
    
    public void setImage(Image image)
    {
    	this.image = image;
        width = image.getWidth();
        height = image.getHeight();
    }
    
    public void setOpacity(double dVal)
    {
    	this.dOpacity = dVal;
    }
    
    public boolean isDead()
    {
    	return this.bDead;
    }
    
    public double getWidth()
    {
    	return this.width;
    }
    public double getHeight()
    {
    	return this.height;
    }

    public void setPosition(double x, double y) {
       this.obCoor.setX(x);
        this.obCoor.setY(y);
        
    }

    public Coors getCoors()
    {
    	return this.obCoor;
    	
    }
    
    public void moveX(double dInc, GraphicsContext gc)
    {
    	renderNull(gc);
		
		getCoors().incX(dInc);
		render(gc);
    }
    
    /**
     * This routine will cause our image to be displayed on the given canvas offset by the indicated value.
     * @param dInc
     */
    public void moveY(double dInc, GraphicsContext gc)
    {
    	renderNull(gc);
		
		getCoors().incY(dInc);
		render(gc);
    }
    
    /**
     * This routine will cause the existing image to be wiped from the canvas
     * @param gc
     */
    public void renderNull(GraphicsContext gc)
    {
    	Coors obCor = getCoors();
    	obLock.lock();
		gc.clearRect(obCor.getX(), obCor.getY(), getWidth(), getHeight());
		obLock.unlock();
    }
    
    
    public void render(GraphicsContext gc)
    {
    	obLock.lock();
    	gc.setGlobalAlpha(this.dOpacity);
        gc.drawImage(image, obCoor.getX(), obCoor.getY());
        obLock.unlock();
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(obCoor.getX(), obCoor.getY(), width, height);
    }

    public boolean intersects(Sprite spr) 
    {
        if (spr.getBoundary().intersects(this.getBoundary()))
        {
        	this.bDead = true;
        	spr.bDead = true;
        	return true;
        }
        return false;
    }

}
