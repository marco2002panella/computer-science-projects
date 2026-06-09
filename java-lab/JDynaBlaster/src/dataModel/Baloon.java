package dataModel;

public class Baloon extends Entity{
	
	private AreaSolida solida;
	private Movement<Baloon> movement;
	private Action<Baloon> action;
	private CollisionChecker<Baloon> checker;
	private boolean dead,none;
	private CheckerPlayer checkerPlayer;
	private int number;
	
	public Baloon(int x, int y, int w, int h, boolean collide, String direzione, int blocco, GameModel cd, int number) {
		
		super(x, y, w, h, collide, direzione, blocco, cd);
		this.number=number;
		solida=new AreaSolida(30,30);
		setDirezione("destra");
		checkerPlayer=new CheckerPlayer();
		movement=new MovementBaloon();
		Baloon s=this;
		none=false;
		action=new Action<Baloon>() {
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
				if(counter==1000)
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
		dead=false;
		checker=new CollisionCheckerBaloon();
	}
	
	/**imposta none con il flag in input*/
	public void setNone(boolean flag){
		none=flag;
	}
	/**restituisce none*/
	public boolean getNone() {
		return none;
	}
	
	/**ritorna il numero di baloon*/
	public int getNumber() {
		return number;
	}
	/**ritorna l'area solida del baloon*/
	public AreaSolida getBaloonSolid() {
		return solida;
	}
	
	/**imposta come si muove un baloon*/
	public void setMovement(Movement<Baloon> mov){
		movement=mov;
	}
	/**imposta l'azione*/
	public void setAction(Action<Baloon> act) {
		action=act;
	}
	/**imposta il collider*/
	public void setCollider(CollisionChecker<Baloon> cc) {
		checker=cc;
	}
	
	/**aggiorna lo stato del baloon*/
	public void update(){
		Action();
	}
	
	/**effettua l azione del baloon*/
	private void Action() {
		action.play();
	}
	/**imposta il numero*/
	public void setNumber(int i) {
		number=i;
	}
	
	/**imposta l'attributo dead a true*/
	public void dead() {
		dead=true;
		GameModel.getInstance().updateController(this);
	}
	
	/**ritorna se è morto*/
	public boolean isDead(){
		return dead;
	}

}
