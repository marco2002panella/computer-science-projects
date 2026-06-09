package view;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BombView extends ImageView{
	
	private int imageCounter=0;
	private int imageNum=0;
	private Image bomb1,bomb2,bomb3;
	private Image es,es1,es2,es3,es4,es5,es6,es7;
	private boolean nowFlag;
	private int explosed;
	
	public BombView(){
		getImages();
	}
	
	/**disegna la bomba 
	 * @param x 
	 * @param y 
	 * @param piazzabile 
	 * @param esplosa
	 * @return void*/
	public void drawSingle(int x, int y,boolean piazzabile, boolean esplosa) {
		setImage(null);
		AnimationTimer bombAnimation = new AnimationTimer() {
            int counter = 0;
            long lastRender = 0;
            @Override
            public void handle(long now) {
                if (now - lastRender > 100000000) {
                    switch (counter) {
                        case 0:
                            setImage(bomb1);
                            break;
                        case 1:
                            setImage(bomb2);
                            break;
                        case 2:
                            setImage(bomb3);
                            break;
                    }
                    counter++;
                    lastRender = now;
                }
                if (counter == 3)
                    stop();
            }
        };
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), this);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(2);
        scaleTransition.setToY(2);

        AnimationTimer explosionAnimation = new AnimationTimer() {
            int counter = 0;
            long lastRender = 0;
            
            @Override
            public void handle(long now) {
                if (now - lastRender > 50000000) {
                    switch (counter) {
                        case 0:
                        	AudioManager.getInstance().Play(AudioBomb.getInstance());
                            setImage(es);
                            break;
                        case 1:
                            setImage(es1);
                            break;
                        case 2:
                            setImage(es2);
                            break;
                        case 3:
                            setImage(es3);
                            break;
                        case 4:
                            setImage(es4);
                            break;
                        case 5:
                            setImage(es5);
                            break;
                        case 6:
                            setImage(es6);
                            break;
                        case 7:
                            setImage(es7);
                            break;
                        
                    }
                    counter++;
                    lastRender = now;
                }
                if (counter == 7) {
                    setImage(null);
                    stop();
                }
            }
        };

        if (!piazzabile && !esplosa) {
        	explosed=0;
        	setX(x-5);
    		setY(y-5);
            bombAnimation.start();
            scaleTransition.play();
            
        } else if (esplosa) {
        	
        	setX(x-30);
    		setY(y-30);
    		if(explosed==0) 
    			explosionAnimation.start();
    		explosed=1;
        }
		
	}
	
	private Timeline createImageTimeline(Image[] frames, double duration) {
	    Timeline timeline = new Timeline();
	    
	    for (int i = 0; i < frames.length; i++) {
	    	final int index=i;
	        timeline.getKeyFrames().add(new KeyFrame(
	            Duration.seconds(i * duration),
	            event -> setImage(frames[index])
	        ));
	    }
	        
	    return timeline;
	}
	
	/**disegna la fireBomb*/
	public void drawFire(int x, int y,boolean piazzabile, boolean esplosa){
		drawSingle(x,y,piazzabile,esplosa);
	}
		
	
	/**metodo che acquisisce le immagini per la visualizzazione della bomba e delle esplosioni
	 * @param
	 * @return void*/
	public void getImages(){
		bomb1=new Image(getClass().getResourceAsStream("/resources/bomb/bomb5.png"));
		bomb2=new Image(getClass().getResourceAsStream("/resources/bomb/bomb6.png"));
		bomb3=new Image(getClass().getResourceAsStream("/resources/bomb/bomb7.png"));
		es=new Image(getClass().getResourceAsStream("/resources/bomb/explosion1.png"));
		es1=new Image(getClass().getResourceAsStream("/resources/bomb/explosion2.png"));
		es2=new Image(getClass().getResourceAsStream("/resources/bomb/explosion3.png"));
		es3=new Image(getClass().getResourceAsStream("/resources/bomb/explosion4.png"));
		es4=new Image(getClass().getResourceAsStream("/resources/bomb/explosion5.png"));
		es5=new Image(getClass().getResourceAsStream("/resources/bomb/explosion6.png"));
		es6=new Image(getClass().getResourceAsStream("/resources/bomb/explosion7.png"));

	}

	
}
