package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LayoutController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private Button btnPlay;

    @FXML
    private Button btnExit;

    @FXML
    private Spinner<Double> spinWind;

    @FXML
    private Spinner<Double> spinAngle;

    @FXML
    private StackPane spAnimation;

    @FXML
    private Canvas canvasStart;
    
    private double startTime = 0;
    final int SCALE_X = 20;
    final int SCALE_Y = 22;
    double wind = 0;
	double angle = 0;
    ParticleManager manager = null;
    Timeline timeline = null;
    GraphicsContext gc;
    
    @FXML
    void initialize() {
        assert btnPlay != null : "fx:id=\"btnPlay\" was not injected: check your FXML file 'Layout.fxml'.";
        assert btnExit != null : "fx:id=\"btnExit\" was not injected: check your FXML file 'Layout.fxml'.";
        assert spinWind != null : "fx:id=\"spinWind\" was not injected: check your FXML file 'Layout.fxml'.";
        assert spinAngle != null : "fx:id=\"spinAngle\" was not injected: check your FXML file 'Layout.fxml'.";
        assert spAnimation != null : "fx:id=\"spAnimation\" was not injected: check your FXML file 'Layout.fxml'.";
        assert canvasStart != null : "fx:id=\"canvasStart\" was not injected: check your FXML file 'Layout.fxml'.";
        
        canvasStart.setWidth(spAnimation.getMinWidth());
        canvasStart.setHeight(spAnimation.getMinHeight());
        gc = canvasStart.getGraphicsContext2D();
        gc.setFill(Color.rgb(20, 20, 36));
        gc.fillRect(0, 0, canvasStart.getWidth(), canvasStart.getHeight());
        
        spinAngle.valueProperty().addListener((observableValue, oldVal, newVal) -> 
        {
        	angle = spinAngle.getValue();
        });
        spinWind.valueProperty().addListener((observableValue, oldVal, newVal) -> 
        {
        	wind = spinWind.getValue();
        });
    }
    
    @FXML
    void playAnimation(ActionEvent event) {
    	if (timeline != null) {
    		timeline.stop();
    	}
    	startTime = System.currentTimeMillis() / 1000.0;
    	angle = spinAngle.getValue();
    	wind = spinWind.getValue();
    	try {
    		manager = new ParticleManager(wind, angle);
    		manager.start(0);
    	} catch (EnvironmentException except) {
    		System.out.println(except.getMessage());
    		return;
    	} catch (EmitterException except) {
    		System.out.println(except.getMessage());		
    		return;
    	}
    	timeline = new Timeline(
    			new KeyFrame(Duration.ZERO, actionEvent -> drawFireworks()),
    			new KeyFrame(Duration.millis(1000 / 24)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();
    }
    
    void drawFireworks() {
    	double time = System.currentTimeMillis() / 1000.0 - startTime;
    	try {
			manager.setLaunchAngle(angle);
			manager.setWindVelocity(wind);
		} catch (EmitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EnvironmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	double angleR = Math.PI * angle / 180.0;
    	double[] position = {Math.sin(angleR), Math.cos(angleR)};
    	manager.setTipPosition(position);
    	ArrayList<Particle> fireworks;
    	fireworks = manager.getFireworks(time);
    	gc.setFill(Color.rgb(20, 20, 36));
		gc.fillRect(0, 0, canvasStart.getWidth(), canvasStart.getHeight());
		gc.setLineWidth(6);
		gc.setStroke(Color.WHITE);
		gc.strokeLine(0.5 * canvasStart.getWidth(), canvasStart.getHeight(), 
				0.5 * canvasStart.getWidth() + Math.sin(angleR) * SCALE_X, 
				canvasStart.getHeight() - Math.cos(angleR) * SCALE_Y);
		Glow glow = new Glow();
		glow.setLevel(0.3);
		MotionBlur blur = new MotionBlur();
    	for (Particle firework : fireworks) {
    		if (firework instanceof BurningParticle) {
    			blur.setRadius(60);
    			blur.setAngle(180);
    			gc.setEffect(blur);
    			gc.setFill(firework.getColour());
    			gc.setFill(firework.getColour());
    			gc.fillOval(0.5 * canvasStart.getWidth() + firework.getPosition()[0] * SCALE_X - 3, 
    					canvasStart.getHeight() - firework.getPosition()[1] * SCALE_Y - 3, 6, 6);
    		} else if (firework instanceof Streak) {
    			gc.setEffect(glow);
    			gc.setLineWidth(3);
    			gc.setStroke(firework.getColour());
    			gc.strokeLine(0.5 * canvasStart.getWidth() + ((Streak) firework).getOrigin()[0] * SCALE_X, 
    					canvasStart.getHeight() - ((Streak) firework).getOrigin()[1] * SCALE_Y, 
    					0.5 * canvasStart.getWidth() + firework.getPosition()[0] * SCALE_X, 
    					canvasStart.getHeight() - firework.getPosition()[1] * SCALE_Y);
    			
    		} else {
    			gc.setEffect(glow);
    			gc.fillOval(0.5 * canvasStart.getWidth() + firework.getPosition()[0] * SCALE_X - 1, 
    					canvasStart.getHeight() - firework.getPosition()[1] * SCALE_Y - 1, 2, 2);
    		}
    	}
        if (fireworks.size() <= 0) {
        	timeline.stop();
        }
	}
    
    @FXML
    void updateAngle(KeyEvent event) { 
    	switch (event.getCode()) {
    	case UP: 
    		spinAngle.increment(1);
    		angle = spinAngle.getValue();
    		break;
    	case DOWN:
    		spinAngle.decrement(1);
    		angle = spinAngle.getValue();
    		break;
    	default:
    		break;
    	}
    }

    @FXML
    void updateWind(KeyEvent event) {
    	switch (event.getCode()) {
    	case UP: 
    		spinWind.increment(1);
    		wind = spinWind.getValue();
    		break;
    	case DOWN:
    		spinWind.decrement(1);
    		wind = spinWind.getValue();
    		break;
    	default:
    		break;
    	}
    }

    @FXML
    void exitProgram(ActionEvent event) {
    	if (timeline != null) {
    		timeline.stop();
    	}
    	Platform.exit();
    }
}
