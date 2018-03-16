package uebungen.uebung5.aufgabe1;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.scene.shape.Ellipse;
import javafx.event.ActionEvent;

import java.util.concurrent.LinkedBlockingQueue;

import javafx.animation.AnimationTimer;

public class Ampel extends Application {
	
	Pane pane;
	Button red, green, blink;
	Ellipse redLight, yellowLight, greenLight;
	BorderPane bp;
	AnimationTimer atBlink, atGreen, atRed;
	State state = State.RED;
	boolean busy=false;
	Thread worker;
	LinkedBlockingQueue<State> queue;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		queue = new LinkedBlockingQueue<>();
		primaryStage.setTitle("Ampel");
		this.pane = createPane();
		this.bp= new BorderPane();
		this.red = new Button("Red");
		this.red.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent event) {
				queue.add(State.RED);
			}
		});
		this.green = new Button("Green");
		this.green.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				queue.add(State.GREEN);
			}
			
		});
		this.blink = new Button("Blink");
		this.blink.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				state = State.BLINK;
				atBlink = new AnimationTimer(){
					boolean yellow = true;
					@Override
					public void handle(long now) {
						if(yellow){
							yellow();
							yellow = false;
						}
						else{
							black();
							yellow = true;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							
						}
					}
				};
				atBlink.start();
			}
			
		});
		worker = new Thread(){
			@Override
			public void run(){
				while(true){
					uebungen.uebung5.aufgabe1.State current = queue.poll();
					if(current != null){
						if(current == uebungen.uebung5.aufgabe1.State.GREEN){
							switchToGreen();
						}
						else if(current == uebungen.uebung5.aufgabe1.State.RED){
							switchToRed();
						}
					}
					else{
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		worker.start();
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(10, 40, 0, 170));
		hbox.getChildren().addAll(red, green, blink);
		bp.setBottom(hbox);
		bp.setCenter(pane);
		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private Pane createPane(){
		Pane pane= new Pane();
		pane.setPrefSize(500.0, 300.0);
		Rectangle ampel = new Rectangle(60, 160, Color.BLACK);
		ampel.setX(220);
		ampel.setY(60);
		redLight = new Ellipse(20,20);
		redLight.setFill(Color.RED);
		redLight.setCenterX(ampel.getX()+30);
		redLight.setCenterY(ampel.getY()+30);
		yellowLight = new Ellipse(20,20);
		yellowLight.setFill(Color.BLACK);
		yellowLight.setCenterX(ampel.getX()+30);
		yellowLight.setCenterY(ampel.getY()+80);
		greenLight = new Ellipse(20,20);
		greenLight.setFill(Color.BLACK);
		greenLight.setCenterX(ampel.getX()+30);
		greenLight.setCenterY(ampel.getY()+130);
		pane.getChildren().addAll(ampel, redLight, yellowLight, greenLight);
		return pane;
	}
	
	private void green(){
		this.greenLight.setFill(Color.GREEN);
		this.redLight.setFill(Color.BLACK);
		this.yellowLight.setFill(Color.BLACK);
	}
	
	private void red(){
		this.greenLight.setFill(Color.BLACK);
		this.redLight.setFill(Color.RED);
		this.yellowLight.setFill(Color.BLACK);
	}
	
	private void yellow(){
		this.greenLight.setFill(Color.BLACK);
		this.redLight.setFill(Color.BLACK);
		this.yellowLight.setFill(Color.YELLOW);
	}
	
	private void redYellow(){
		this.greenLight.setFill(Color.BLACK);
		this.redLight.setFill(Color.RED);
		this.yellowLight.setFill(Color.YELLOW);
	}
	
	private void black(){
		this.greenLight.setFill(Color.BLACK);
		this.yellowLight.setFill(Color.BLACK);
		this.redLight.setFill(Color.BLACK);
	}
	
	public void switchToRed(){
		if(atBlink != null){
			atBlink.stop();
		}
		if(state == State.BLINK){
			red();
			state = State.RED;
		}
		if(!(state == State.RED)){
			state = State.RED;
			atRed = new AnimationTimer(){
				boolean yellowPhase = true;
				@Override
				public void handle(long now) {
					if(yellowPhase){
						yellow();
						yellowPhase=false;
					}
					else{
						red();
						this.stop();
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						
					}
				}
				
			};
			atRed.start();
		}
	}
	
	public void switchToGreen(){
		if(atBlink != null){
			atBlink.stop();
		}
		if(state == State.BLINK){
			green();
			state = State.GREEN;
		}
		if(!(state == State.GREEN)){
			state = State.GREEN;
			atGreen = new AnimationTimer(){
				boolean yellowRedPhase = true;
				@Override
				public void handle(long now) {
					if(yellowRedPhase){
						redYellow();
						yellowRedPhase=false;
					}
					else{
						green();
						this.stop();
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						
					}
				}
			};
			atGreen.start();
		}
	}
}
