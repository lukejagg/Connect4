// © 2018 Luke Jagg
// MIT License

package Connect4;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;

// Includes Drawing
public class Main extends Application {

	final int cellSize = 120;
	final int boardWidth = 7;
	final int boardHeight = 6;
	final int inARow = 4;
	
	// AI SETTINGS:
	boolean useAI = false;
	int offset = 3;
	boolean defendFours = true;
	boolean attackFours = true;
	boolean checkStepAfter = true;
	int divisor = 1;
	
	int[] automoves = new int[] {
			//3,3,3,2,1,4,3,2,2,1,1,6,6,6,4
	};
	int moves = 0;
	
	int games = 0;
	
	final int width = boardWidth * cellSize;
	final int height = boardHeight * cellSize;
	
	int redWins = 0;
	int yellowWins = 0;
	
	int selectedRow = 0;

	boolean mouseDown = false;
	
	int lastX = -1;
	int lastY = -1;
	
	boolean boardFall = false;
	
	// So the stuff looks like it's falling
	float gravityEffect = 0;
	float gravityAccel = 0;
	
	boolean yellowMove = false;
	
	Board board = new Board(boardWidth, boardHeight);
	
	Media click = new Media(new File("res/Click.mp3").toURI().toString());
	Media sphaget = new Media(new File("res/Toucha.mp3").toURI().toString());
	Media bawana = new Media(new File("res/Bawana.mp3").toURI().toString());
	Media tie = new Media(new File("res/Ties.mp3").toURI().toString());
	Media tie2 = new Media(new File("res/CPU_WON.mp3").toURI().toString());
	MediaPlayer clickPlayer = new MediaPlayer(click);
	
	Media music = new Media(new File("res/Ties.mp3").toURI().toString());
	MediaPlayer musicPlayer = new MediaPlayer(music);
	
	Media win = new Media(new File("res/Win.mp3").toURI().toString());
	Media lose = new Media(new File("res/Lose.mp3").toURI().toString());
	MediaPlayer winPlayer = new MediaPlayer(win);
	
	final Image cell = new Image("file:res/Connect4Cell.png");
	final Image cellBackground = new Image("file:res/CellBackground.png");
	
	Random random = new Random();
	
	GraphicsContext gc;
	
	int[] minmax = new int[boardWidth];
	
	int[][] evalTable = new int[][] {
		{3, 4, 5, 7, 5, 4, 3}, 
        {4, 6, 8, 10, 8, 6, 4},
        {5, 8, 11, 13, 11, 8, 5}, 
        {5, 8, 11, 13, 11, 8, 5},
        {4, 6, 8, 10, 8, 6, 4},
        {2, 4, 5, 6, 5, 4, 2}
        };
	
	@Override
    public void start(Stage primaryStage) {
		
	    Group root = new Group();
	    	Scene s = new Scene(root, width, height + cellSize, Color.BLACK);

	    	final Canvas canvas = new Canvas(width, height + cellSize);
	    	gc = canvas.getGraphicsContext2D();
	    	 
	    	root.getChildren().add(canvas);
	    
	    	primaryStage.setTitle("4 in a Row");
	    	primaryStage.setScene(s);
	    	primaryStage.setResizable(false);
	    	primaryStage.show();
	    	
	    	canvas.setFocusTraversable(true);
	    	
	    	canvas.setOnMouseReleased(e -> {
	    		
	    		mouseDown = false;
	    		
	    		if (e.getY() < height && board.winner == 0) {
	    			
	    			int place = GetLowest();
	    			
	    			if (yellowMove && board.winner == 0 && useAI) {
	    				selectedRow = (EvaluateColumn(0) / divisor) % boardWidth;
		    			while (GetLowest() == -1) {
		    				selectedRow = ((++selectedRow) / divisor) % boardWidth;
		    			}
		    			lastX = selectedRow;
		    			lastY = GetLowest();
		    			board.PlacePiece(selectedRow, GetLowest(), (!yellowMove) ? 1 : 2);
		    			yellowMove = !yellowMove;
		    			
		    			clickPlayer.stop();
	    				clickPlayer = new MediaPlayer(click);
	    				clickPlayer.play();
	    				
	    				gravityEffect = 0;
	    				gravityAccel = 1f;
	    			}
	    			else if (!yellowMove && board.winner == 0 && moves < automoves.length) {
	    				selectedRow = automoves[moves];
	    				
	    				lastX = selectedRow;
		    			lastY = GetLowest();
		    			board.PlacePiece(selectedRow, GetLowest(), (!yellowMove) ? 1 : 2);
		    			yellowMove = !yellowMove;
		    			
		    			clickPlayer.stop();
	    				clickPlayer = new MediaPlayer(click);
	    				clickPlayer.play();
	    				
	    				gravityEffect = 0;
	    				gravityAccel = 1f;
	    				
	    				moves++;
	    			}
	    			else if (place != -1) {
	    				lastX = selectedRow;
	    				lastY = place;
	    				
	    				board.PlacePiece(selectedRow, place, (!yellowMove) ? 1 : 2);
	    				yellowMove = !yellowMove;
	    				
	    				gravityEffect = 0;
	    				gravityAccel = 1f;
	    				
	    				if (board.winner != 0) {
	    					games++;
	    					
	    					musicPlayer.stop();
	    					winPlayer.stop();
	    					winPlayer = new MediaPlayer(lose);
	    					
	    					if (board.winner == -1) {
	    						if (random.nextBoolean()) {
	    							winPlayer = new MediaPlayer(tie);
	    						}
	    						else {
	    							winPlayer = new MediaPlayer(tie2);
	    						}
		    					redWins++;
		    				}
	    					else if (board.winner == 1) {
		    					winPlayer = new MediaPlayer(win);
		    					redWins++;
		    				}
		    				else if (board.winner == 2) {
		    					winPlayer = new MediaPlayer(win);
		    					yellowWins++;
		    				}
		    				if (e.getY() < height && board.winner > 0) {
			    				if (board.board[selectedRow][boardHeight - (int)e.getY() / cellSize] == 1) {
			    					winPlayer = new MediaPlayer(sphaget);
			    				}
			    				else if (board.board[selectedRow][boardHeight - (int)e.getY() / cellSize] == 2) {
			    					winPlayer = new MediaPlayer(bawana);
			    				}
		    				}
		    				
		    				winPlayer.play();

	    				}
	    				
	    				clickPlayer.stop();
	    				clickPlayer = new MediaPlayer(click);
	    				clickPlayer.play();
	    				
	    				Draw();
	    			}
	    			
	    		}
	    		else {
	    			selectedRow = -1;
	    			
	    			if (board.winner != 0) {
	    				if (boardFall) {
		    				winPlayer.stop();
			    			Start();
	    				}
	    				else {
	    					boardFall = true;
	    					gravityEffect = 0;
	    					gravityAccel = 1;
	    					lastY = -1;
	    				}
		    		}
	    			
	    			Draw();
	    		}
	    		
	    	});
	    	
	    	canvas.setOnMousePressed(e -> {
	    		
	    		mouseDown = true;
	    		
	    	});
	    	
	    	canvas.setOnMouseMoved(e -> {
	    		
	    		selectedRow = (int)e.getX() / cellSize;
	    		
	    	});
	    	
	    	canvas.setOnMouseDragged(e -> {
	    		
	    		int selectedRow = (int)e.getX() / cellSize;
	    		
	    		if (selectedRow != this.selectedRow) {
	    			this.selectedRow = selectedRow;
	    			Draw();
	    		}
	    		
	    	});
				    	
	    	primaryStage.setOnCloseRequest(e -> {
		    	
		    	System.exit(0);
		    	
	    	});
	    	
	    	final LongProperty lastUpdateTime = new SimpleLongProperty(0);
	    	// LOL I FINALLY FOUND OUT HOW TO SYNC THIS WITH THE DRAW THINGY LOL!!!
	    	// <3 AnimationTimer
	    	AnimationTimer timer = new AnimationTimer() {
	    		
	    	    @Override
	    	    public void handle(long dt) {
	    	    	
	    	        if (lastUpdateTime.get() > 0) {
	    	        	
	    	            double deltaTime = (dt - lastUpdateTime.get()) / 1000000000.0;
	    	            
	    	            if (gravityEffect <= boardHeight - lastY) { 
	    	            		gravityAccel += deltaTime * 36;
	    	   	 			gravityEffect += deltaTime * gravityAccel;
	    	   	 			
	    	   	 			if (gravityEffect > boardHeight - lastY) {
	    	   	 				gravityEffect = boardHeight - lastY;
	    	   	 			}
	    	   	 			
	    		   	 		Draw();
	    		   	 	}
	    	            
	    	        }
	    	        
	    	        lastUpdateTime.set(dt);
	    	        
	    	    }
	    	    
	    	};
	    	
	    	timer.start();
	    	
	    	Start();
	    	
    }
	
	void CreateTheme() {
		
		musicPlayer.stop();
	    	musicPlayer = new MediaPlayer(music);
	    	musicPlayer.setVolume(0.5);
	    	
	 	musicPlayer.setOnReady(new Runnable() {
	 		public void run() {
	 			musicPlayer.play();
	 		}
	 	});
	 	
	 	musicPlayer.setOnEndOfMedia(new Runnable() {
	   	    public void run() {
	   	    		CreateTheme();
	   	    }
	   	});
	 	
	}
	
	int GetLowest() {
		
		if (selectedRow >= 0 && selectedRow < boardWidth) {
			for (int i = 0; i < boardHeight; i++) {
				
				if (board.board[selectedRow][i] == 0)
					return i;
				
			}
		}
		
		return -1;
		
	}
	
	void Draw() {
		
		gc.setFont(new Font(cellSize / 3));
		gc.setTextAlign(TextAlignment.LEFT);
		gc.setTextBaseline(VPos.BOTTOM);

		gc.setEffect(new ColorAdjust());
		gc.setTransform(new Affine());
		
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, width, height + cellSize);
		
		gc.setFill(Color.rgb(61, 164, 255, 0.6));
		gc.fillRect(0, height, width, cellSize);
		
		gc.setFill(Color.WHITE);
		
		gc.fillText("" + redWins, cellSize * 0.8, height + cellSize * 0.9);
		
		gc.setTextAlign(TextAlignment.RIGHT);
		gc.fillText("" + yellowWins, width - cellSize * 0.8, height + cellSize * 0.9);
		
		if (!yellowMove) {
			gc.setFill(Color.RED);
			gc.fillRoundRect(cellSize * .05, height + cellSize * .25, cellSize * .7, cellSize * .7, cellSize, cellSize);
			
			gc.setFill(Color.YELLOW);
			gc.fillRoundRect(width - cellSize * .55, height + cellSize * .45, cellSize * .5, cellSize * .5, cellSize, cellSize);
		}
		else {
			gc.setFill(Color.RED);
			gc.fillRoundRect(cellSize * .05, height + cellSize * .45, cellSize * .5, cellSize * .5, cellSize, cellSize);
			
			gc.setFill(Color.YELLOW);
			gc.fillRoundRect(width - cellSize * .75, height + cellSize * .25, cellSize * .7, cellSize * .7, cellSize, cellSize);
		}
		
		if (board.winner == 0) {
			//gc.fillText(((!yellowMove) ? "Player 1" : "Player 2") + "'s Turn", width / 2, height + cellSize - 10);
			if (yellowMove) {
				gc.setFill(Color.rgb(255, 255, 0, 0.35));
			}
			else {
				gc.setFill(Color.rgb(255, 0, 0, 0.35));
			}
			
			if (GetLowest() >= 0) 
				gc.fillRoundRect(selectedRow * cellSize, height - (GetLowest() + 1) * cellSize, cellSize, cellSize, cellSize, cellSize);
		}
		else if (board.winner == 1) {
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText("Player 1 Wins", width / 2, height + cellSize - 10);
		}
		else if (board.winner == 2) {
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText("Player 2 Wins", width / 2, height + cellSize - 10);
		}
		else if (board.winner == -1) {
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText("Tie!", width / 2, height + cellSize - 10);
		}
		
		float xMult = (float)width / boardWidth;
		float yMult = (float)height / boardHeight;
		
		gc.setFill(Color.rgb(0, 255, 0, 0.5));
		
		if (selectedRow != -1 && mouseDown)
			gc.fillRect(selectedRow * cellSize, 0, cellSize, height - (GetLowest() + 1) * cellSize);
		
		for (int i = 0; i < boardWidth; i++) {
			for (int j = 0; j < boardHeight; j++) {
				
				gc.drawImage(cellBackground, xMult * i, height - yMult * (j + 1), cellSize, cellSize);
				
				if (board.board[i][j] > 0) {
					if (board.board[i][j] == 1) {
						gc.setFill(Color.RED);
					}
					else {
						gc.setFill(Color.YELLOW);
					}
					if (!boardFall) {
						if (i == lastX && j == lastY) {
							gc.fillRoundRect(xMult * i + cellSize * .05, yMult * (gravityEffect) - cellSize * .95, cellSize * .9, cellSize * .875, cellSize, cellSize);
						}
						else {
							gc.fillRoundRect(xMult * i + cellSize * .05, height - yMult * (j + 1) + cellSize * .05, cellSize * .9, cellSize * .875, cellSize, cellSize);
						}
					}
				}
				
				if (!boardFall) 
					gc.drawImage(cell, xMult * i, height - yMult * (j + 1), cellSize, cellSize);
				
			}
		}
		
		if (boardFall) {
			
			for (int i = 0; i < boardWidth; i++) {
				for (int j = 0; j < boardHeight; j++) {
					if (board.board[i][j] > 0) {
						
						if (board.board[i][j] == 1) {
							gc.setFill(Color.RED);
						}
						else {
							gc.setFill(Color.YELLOW);
						}
						gc.fillRoundRect(xMult * i + cellSize * .05, height - yMult * (j+1) + yMult * gravityEffect, cellSize * .9, cellSize * .875, cellSize, cellSize);
					
					}
				}
			}
			
			for (int i = 0; i < boardWidth; i++) {
				for (int j = 0; j < boardHeight; j++) {
					gc.drawImage(cell, xMult * i, height - yMult * (j + 1), cellSize, cellSize);
				}
			}
			
		}
		
	}
	
	int EvaluateColumn(int column) {
		int evaluation = offset;
		for (int i = 0; i < boardWidth; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (board.board[i][j] == 1) {
					evaluation += evalTable[j][i];
				}
				else if (board.board[i][j] == 2) {
					evaluation -= evalTable[j][i];
				}
			}
		}
		
		if (checkStepAfter) {
			Board newBoard = new Board(board);
			
			selectedRow = (evaluation / divisor) % boardWidth;
			while (GetLowest() == -1) {
				selectedRow = ((++selectedRow) / divisor) % boardWidth;
			}
			newBoard.PlacePiece(selectedRow, GetLowest(), 2);
			if (newBoard.winner == 0) {
				newBoard.PlacePiece(selectedRow, GetLowest() + 1, 1);
				if (newBoard.winner == 1) {
					evaluation += divisor;
				}
			}
		}
		
		for (int i = 0; i < boardWidth; i++) {
			
			selectedRow = i;
			if (GetLowest() == -1) {
				continue;
			}
			else {
				
				Board newBoard = new Board(board);
				
				if (attackFours) {
					newBoard = new Board(board);
					newBoard.PlacePiece(i, GetLowest(), 2);
					if (newBoard.winner == 2) {
						evaluation = i * divisor;
						break;
					}
				}
				
				if (defendFours) {
					newBoard = new Board(board);
					
					newBoard.PlacePiece(i, GetLowest(), 1);
					if (newBoard.winner == 1) {
						evaluation = i * divisor;
						break;
					}
				}
				
			}
			
		}
		
		return evaluation;
	}
	
	void Start() {
		
		yellowMove = (games%2 == 0) ? false : true;
		
		
		board = new Board(boardWidth, boardHeight);
		board.winLength = inARow;
		
		lastX = -1;
		lastY = -1;
		
		mouseDown = false;
		
		boardFall = false;
		
		Draw();
		
		CreateTheme();
		
		moves = 0;
		
	}
	
	public static void main(String[] args) {
		
		launch(args);
		
	}
	
}
