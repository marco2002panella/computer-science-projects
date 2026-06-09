package view;

import java.util.ArrayList;
import java.util.Iterator;

import controller.Controller;
import dataModel.Saves;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

public class MenuView {
	private Scene scene;
	private Pane root;
	private ArrayList nodes;
	private boolean showing=true;
	boolean destra,sinistra,su,giu;
	private PlayerView player;
	private Pane buttonRoot;
	private ArrayList buttons;
	private ListView<String> savesView;
	private int color;
	private String nick;
	
	public MenuView(int l, int a) {
		root=new Pane();
		scene=new Scene(root,l,a);
		player=new PlayerView(24, 36, 0, 0, null);
		nodes=new ArrayList<>();
		buttons=new ArrayList<>();
		Rectangle white=new Rectangle(800,800,Color.WHITE);
		root.getChildren().add(white);
		scene.setOnKeyPressed(event->handleKeyPressed(event));
		scene.setOnKeyReleased(event->handleKeyReleased(event));
	}
	
	
	public Scene getScene() {
		return scene;
	}
	
	/**mostra il menu*/
	public void show(){
		showing=true;
		if(nodes.size()!=0)
			root.getChildren().removeAll(nodes);
		new AnimationTimer() {
			int counter=0;
			int s=0;
			long last=0;
			String t="JDYNABLASTER     created by";
			String n="              Marco Panella";
			@Override
			public void handle(long arg0) {
				if(arg0-last>100000000l ) {
					last=arg0;
					if(counter<t.length()) {
						Text text=new Text();
						text.setText(""+t.charAt(counter));
						root.getChildren().add(text);
						if(counter<=11)
							text.setFont(Font.font(30));
						Text name=new Text();
						name.setText(""+n.charAt(counter));
						name.setX(100+counter*20);
						name.setY(300+20);
						root.getChildren().add(name);
						text.setY(300);
						text.setX(100+counter*20);
						nodes.add(text);
						nodes.add(name);
						counter++;
					}
					
					if(counter==(t.length())){
				        player.translate(448, 380, "fermo", 0, true,1);
						createMenuButtons();
						nodes.add(player);
						root.getChildren().add(player);
				        this.stop();
					}

				}

			}
			
		}.start();
		
	}
	
	/**rimuove tutti i bottoni*/
	public void removeButtons() {
		if(buttons.size()!=0) {
			root.getChildren().removeAll(buttons);
			nodes.removeIf(e->buttons.contains(e));
			buttons.removeIf(e->buttons.contains(e));
		}
	}
	
	/**crea la view new game*/
	public void createNewGameButtons() {
		
		removeButtons();
		TextField textField = new TextField();
		Button start=new Button();
		Button back=new Button();
		Text nickname=new Text();
		
		buttons.add(nickname);
		buttons.add(start);
		buttons.add(textField);
		buttons.add(back);
		
		nodes.add(nickname);
		nodes.add(start);
		nodes.add(textField);
		nodes.add(back);
		
		Button black = new Button();
        black.setMinSize(20, 20);
        black.setMaxSize(20, 20);
        black.setStyle("-fx-border-color: black;");
        black.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        black.setOnMouseEntered(e -> {
        	black.setMinSize(22, 22);
        	black.setMaxSize(22, 22);
        });
        black.setOnMouseExited(e -> {
        	black.setMinSize(20, 20);
        	black.setMaxSize(20, 20);
        });
        black.setOnMouseClicked(event->{
        	player.loadImagesBlack();
        });
        Button red = new Button();
        red.setMinSize(20, 20);
        red.setMaxSize(20, 20);
        red.setStyle("-fx-border-color: red;");
        red.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        red.setOnMouseEntered(e -> {
        	red.setMinSize(22, 22);
            red.setMaxSize(22, 22);
        });
        red.setOnMouseExited(e -> {
        	red.setMinSize(20, 20);
        	red.setMaxSize(20, 20);
        });
        red.setOnMouseClicked(event->{
        	player.loadImagesRed();
        });
        Button white = new Button();
        white.setMinSize(20, 20);
        white.setMaxSize(20, 20);
        white.setStyle("-fx-background-color: White; -fx-border-color: black;");
        white.setOnMouseEntered(e -> {
        	white.setMinSize(22, 22);
        	white.setMaxSize(22, 22);
        });
        white.setOnMouseExited(e -> {
        	white.setMinSize(20, 20);
            white.setMaxSize(20, 20);
        });
        
        black.setLayoutX(400);
        black.setLayoutY(430);
        red.setLayoutX(450);
        red.setLayoutY(430);
        white.setLayoutX(500);
        white.setLayoutY(430);
        
        nodes.add(black);
        nodes.add(red);
        nodes.add(white);
        
        buttons.add(black);
        buttons.add(red);
        buttons.add(white);
        
        root.getChildren().add(black);
        root.getChildren().add(red);
        root.getChildren().add(white);
        
        black.setOnMouseClicked(event->{
        	player.loadImagesBlack();
        	GameView.getInstance().getGamePanel().setPlayerColor(2);
        	color=2;
        });
        
        red.setOnMouseClicked(event->{
        	player.loadImagesRed();
        	GameView.getInstance().getGamePanel().setPlayerColor(1);
        	color=1;
        });
        
        white.setOnMouseClicked(event->{
        	player.loadImagesWhite();
        	GameView.getInstance().getGamePanel().setPlayerColor(0);
        	color=0;	
        });
		back.setLayoutX(100);
		back.setLayoutY(410);
		back.setText("b a c k");
		back.setStyle("-fx-background-color: white;");
		back.setTextFill(Color.BLACK);
		back.setOnMouseEntered(event->{
			back.setText("B A C K");
		});
		back.setOnMouseExited(event->{
			back.setText("b a c k");});
		
		back.setOnMouseClicked(event->{
			createMenuButtons();
		});
		
		start.setLayoutX(100);
		
		start.setLayoutY(450);
		
		start.setText("s t a r t");
		
		start.setStyle("-fx-background-color: white;");
		
		start.setTextFill(Color.BLACK);
		
		start.setOnMouseEntered(event->{
			start.setText("S T A R T");});
		
		start.setOnMouseExited(event->{
			start.setText("s t a r t");});
		
		start.setOnMouseClicked(event->{
			nick=textField.getText();
			Iterator<String> iter=savesView.getItems().iterator();
			boolean ok=true;
			while(iter.hasNext()){
				String t=iter.next();
				if(t.split("\\|")[0].equals(nick+""))
					ok=false;
			}
			if(!nick.isEmpty() && ok){
				removeAll();
				Controller.getInstance().newSave(nick,color);
				Controller.getInstance().cycle();
				GameView.getInstance().game();
			}		
		});
		
		
		//textField.setStyle("-fx-background-color: white;");
		nickname.setText("add nickName");
		nickname.setLayoutX(200);
		nickname.setLayoutY(430);
		nickname.setFill(Color.BLACK);
		textField.setLayoutX(200);
		textField.setLayoutY(450);
        
		root.getChildren().addAll(start,back,textField,nickname);
	}
	
	/**crea le opzioni del menu principale*/
	public void createMenuButtons() {
		
		removeButtons();
			
		String option1=" s t a r t  n e w  g a m e";
		String option2=" c o n t i n u e    ";
		String option3=" e x i t      ";
		createSavesView(Controller.getInstance().getSaves());
		Button button = new Button();
        button.setStyle("-fx-background-color: white;");
        button.setTextFill(Color.BLACK);
        button.setFont(Font.font("Arial", 12));
        button.setText(option1);
        button.setLayoutX(100);
        button.setLayoutY(400);
        button.setOnMouseEntered(event->{
        	button.setText(">"+option1);
        	});
        button.setOnMouseExited(event->{
        	button.setText(option1);
        	});
        Button button1 = new Button();
        button1.setStyle("-fx-background-color: white;");
        button1.setTextFill(Color.BLACK);
        button1.setFont(Font.font("Arial", 12));
        button1.setText(option2);
        button1.setLayoutX(100);
        button1.setLayoutY(430);
        button1.setOnMouseEntered(event->{
        	button1.setText(">"+option2);
        	});
        button1.setOnMouseExited(event->{
        	button1.setText(option2);
        	});
        Button button2 = new Button();
        button2.setStyle("-fx-background-color: white;");
        button2.setTextFill(Color.BLACK);
        button2.setFont(Font.font("Arial", 12));
        button2.setText(option3);
        button2.setLayoutX(100);
        button2.setLayoutY(460);
        button2.setOnMouseEntered(event->{
        	button2.setText(">"+option3);
        	});
        button2.setOnMouseExited(event->{
        	button2.setText(option3);
        	});
       
        root.getChildren().addAll(button,button1,button2);
        
        nodes.add(button2);
        nodes.add(button1);
        nodes.add(button);
 
        buttons.add(button);
        buttons.add(button1);
        buttons.add(button2);
        
        button.setOnMouseClicked(event->createNewGameButtons());
        button1.setOnMouseClicked(event->{loadSaves();});
        button2.setOnMouseClicked(event->{GameView.getInstance().stop();});
	}
	
	public void createSavesView(ArrayList<String> in) {
		savesView = new ListView<>();
        // Imposta uno sfondo bianco per la lista
        savesView.setStyle("-fx-background-color: white; -fx-border-color:white;");
        
        // Imposta l'elemento della lista personalizzata
        savesView.setCellFactory(param -> new CustomListCell());
        // Aggiungi elementi alla lista
        
        if(in!=null) {
        	for(String s:in) {
            	savesView.getItems().add(s);
            }
        }
        
        savesView.setStyle("-fx-background-color:white;fx-border-color:white;-fx-selection-bar: white;");
        savesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        savesView.setPrefSize(500, 300);
	}
	
	/**chiede al controller di mandargli i salvataggi e li stampa*/
	public void loadSaves() {
		removeButtons();
		Button start=new Button();
		Button back=new Button();
        ScrollPane scrollPane = new ScrollPane(savesView);
        scrollPane.setStyle("-fx-border-color: white; -fx-background-color:white;");
        scrollPane.setPrefSize(500,300);
        
        scrollPane.setLayoutX(100);
        scrollPane.setLayoutY(340);
        scrollPane.setMaxHeight(100);
        // Nascondi la scrollbar orizzontale
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Nascondi la scrollbar verticale
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        back.setLayoutX(100);
		back.setLayoutY(460);
		back.setText("b a c k");
		back.setStyle("-fx-background-color: white;");
		back.setTextFill(Color.BLACK);
		back.setOnMouseEntered(event->{
			back.setText("B A C K");
		});
		back.setOnMouseExited(event->{
			back.setText("b a c k");});
		
		back.setOnMouseClicked(event->{
			createMenuButtons();
		});
		
		start.setLayoutX(200);
		
		start.setLayoutY(460);
		
		start.setText("s t a r t");
		
		start.setStyle("-fx-background-color: white;");
		
		start.setTextFill(Color.BLACK);
		
		start.setOnMouseEntered(event->{
			start.setText("S T A R T");});
		
		start.setOnMouseExited(event->{
			start.setText("s t a r t");});
		
		start.setOnMouseClicked(event->{if(!savesView.getSelectionModel().isEmpty())
											GameView.getInstance().game();});
        
        buttons.add(back);
		buttons.add(start);
		nodes.add(back);
		nodes.add(start);
        buttons.add(scrollPane);
        nodes.add(scrollPane);
        
        root.getChildren().add(back);
        root.getChildren().add(start);
        root.getChildren().add(scrollPane);
        
	}
	
	/**una classe cella customizzata per la mia listView */
	public class CustomListCell extends ListCell<String>{
		private static final Background WHITE_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
		private static final Background GRAY_BACKGROUND = new Background(new BackgroundFill(Color.GRAY, null, null));
		private String originalItem;
		public CustomListCell() {
            // Imposta l'effetto di scurimento del testo quando il mouse entra nella cella
            setOnMouseEntered(event -> setBackground(GRAY_BACKGROUND));
            // Ripristina il colore del testo quando il mouse esce dalla cella
            setOnMouseExited(event -> setBackground(WHITE_BACKGROUND));
          //quando clicco la mia opzione devo aggionare il controller con il file selezionato
            setOnMouseClicked(event->{
            	if(!this.isEmpty()) {
            		Controller.getInstance().Charge(originalItem);
                	GameView.getInstance().getGamePanel().setPlayerColor(Integer.parseInt(originalItem.split("\\|")[1]));
            	}
            });
            		
        }
		
		public String getOriginalItem() {
			return originalItem;
		}
		
		@Override
	    protected void updateItem(String item, boolean empty) {
	          	super.updateItem(item, empty);
	            if (empty || item == null) {
	                setText(null);
	                setBackground(Background.EMPTY); // Rimuovi lo sfondo se l'elemento è vuoto
	            } else {
	            	originalItem=item;
	                setTextFill(Color.BLACK); // Imposta il colore del testo su nero
	                String s=item;
	                String colore=" |color: ";
	        		if(s.split("\\|")[1].equals("0")){
	        			colore+="white";
	        		}
	        		if(s.split("\\|")[1].equals("1")){
	        			colore+="red";  			
	        			}
	        		if(s.split("\\|")[1].equals("2")){
	        			
	        			colore+="black";	
	    			}
	        		String vittorie=" |win: "+s.split("\\|")[2];
	        		String sconfitte=" |loose: "+s.split("\\|")[3];
	        		String livello=" |Level: "+s.split("\\|")[4];
	        		String vite=" |Vite: "+s.split("\\|")[5];
	        		s=s.split("\\|")[0]+colore+vittorie+sconfitte+livello+vite;
	        		setText(s);
	                setBackground(WHITE_BACKGROUND); // Imposta uno sfondo bianco per ogni elemento della lista
	            }
	        }
	}
	
	/**aggiorna la posizione del player nel menu*/
	public void updateMenu(){
		if(destra) {
			player.translate((int)player.getLayoutX()+2, (int)player.getLayoutY(), "destra", 0, true,1);
		}
		if(sinistra) {
			player.translate((int)player.getLayoutX()-2,(int)player.getLayoutY(), "sinistra", 0, true,1);
		}
		if(su) {
			player.translate((int)player.getLayoutX(),(int)player.getLayoutY()-2, "su", 0, true,1);
		}
		if(giu) {
			player.translate((int)player.getLayoutX(), (int)player.getLayoutY()+2, "giu", 0, true,1);
		}
	}
	
	/**metodo che imposta se un tasto viene premuto durante un livello*/
	private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if(keyCode==KeyCode.D) {
        	destra=true;
        }if(keyCode==KeyCode.S) {
        	giu=true;
        }
         if(keyCode==KeyCode.A) {
        	sinistra=true;
        }
         if(keyCode==KeyCode.W) {
        	su=true;
         }
    }
	
	/**metodo che imposta se un tasto viene rilasciato durante il menu*/
	private void handleKeyReleased(KeyEvent event){
		KeyCode keyCode = event.getCode();
		if(keyCode==KeyCode.D) {
        	destra=false;
        }if(keyCode==KeyCode.S) {
        	giu=false;
        }
         if(keyCode==KeyCode.A) {
        	sinistra=false;
        }
         if(keyCode==KeyCode.W) {
        	su=false;
         }
	}
	
	/**rimuovi tutti gli elementi della scena*/
	public void removeAll() {
		removeButtons();
		root.getChildren().removeAll(nodes);
	}
	
	
}
