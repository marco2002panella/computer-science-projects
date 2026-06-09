package dataModel;

public class Ghost extends Entity{
		
		private AreaSolida solida;
		private Movement<Ghost> movement;
		private Action<Ghost> action;
		private CollisionChecker<Ghost> checker;
		private CheckerPlayer checkerPlayer;
		private boolean dead,none;
		private int number;
		
		public Ghost(int x, int y, int w, int h, boolean collide, String direzione, int blocco, GameModel cd, int n) {
			
			super(x, y, w, h, collide, direzione, blocco, cd);
			solida=new AreaSolida(30,30);
			number=n;
			dead=false;
			Ghost s=this;
			checkerPlayer=new CheckerPlayer();
			none=false;
			
			movement=new Movement<Ghost>() {
				int speed=2;
				@Override
				public void muovi(Ghost in, String direction) {
					switch(direction) {
					case "giu":in.setY(in.getY()+speed);	break;
					case "su" :in.setY(in.getY()-speed);	break;
					case "sinistra":in.setX(in.getX()-speed);	break;
					case "destra":in.setX(in.getX()+speed);	break;
					}
				}

				@Override
				public int getSpeed() {
					return speed;
				}
				
			};
			
			action=new Action<Ghost>() {
				int counter=0;
				@Override
				public void play() {
					if(!dead) {
						if(counter==0) {
							int random=(int)(Math.random()*4);
							if(random==3) {
								setDirezione("destra");
							}
							if(random==2) {
								setDirezione("sinistra");
							}
							if(random==1) {
								setDirezione("giu");
							}
							if(random==0) {
								setDirezione("su");
							}
						}
						counter++;
						if(counter==500)
							counter=0;
						
						if(!checker.collide(s, movement, cd, getDirezione())) {
							movement.muovi(s, getDirezione());
							solida.setXY(getX(), getY(), 0, 0);
							checkerPlayer.check(solida);
							cd.updateController(s);
						}else {
							String direzione=getDirezione();
							do {
								int random=(int)(Math.random()*4);
								if(random==3) {
									setDirezione("destra");
								}
								if(random==2) {
									setDirezione("sinistra");
								}
								if(random==1) {
									setDirezione("giu");
								}
								if(random==0) {
									setDirezione("su");
								}
							}while(getDirezione().equals(direzione));
							
						}
					}
				}
			};
			
			checker=new CollisionChecker<Ghost>(){
				@Override
				public boolean collide(Ghost in, Movement<Ghost> in0, GameModel in1, String direction) {
					int x,y;
					x=in.getX();
					y=in.getY();
					switch(direction) {
					case "giu": y+=in0.getSpeed(); break;
					case "su": y-=in0.getSpeed();break;
					case "destra": x+=in0.getSpeed(); break;
					case "sinistra":x-=in0.getSpeed(); break;
					}
					boolean flag=false;
					if(x <=0 || x>=760)
						flag=true;
					if(y<=80 || y>=760)
						flag=true;
					
					return flag;
				}
				
			};
		}
		
		public void setNone(boolean flag){
			none=flag;
		}
		
		public boolean getNone() {
			return none;
		}
		
		/**ritorna l'area solida del ghost*/
		public AreaSolida getGhostSolid() {
			return solida;
		}
		
		/**imposta come si muove un baloon*/
		public void setMovement(Movement<Ghost> mov){
			movement=mov;
		}
		
		/**imposta l'azione dell'entità*/
		public void setAction(Action<Ghost> act) {
			action=act;
		}
		
		/**imposta il collider*/
		public void setCollider(CollisionChecker<Ghost> cc) {
			checker=cc;
		}
		
		/**aggiorna lo stato del baloon*/
		public void update(){
			Action();
		}
		
		/**imposta l azione del baloon*/
		private void Action() {
			action.play();
		}
		
		/**imposta l'attributo dead a true*/
		public void dead() {
			dead=true;
			GameModel.getInstance().updateController(this);
		}
		
		public void setNumber(int i) {
			number=i;
		}
		
		public int getNumber() {
			return number;
		}
		
		public boolean isDead() {
			return dead;
		}

	}

	

