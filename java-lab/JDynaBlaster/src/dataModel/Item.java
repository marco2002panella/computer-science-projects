package dataModel;

public class Item {
	
	protected boolean spawned;
	protected AreaSolida area;
	protected int x,y;
	protected double probability;
	protected boolean used,pressed;
	
	public Item(double prob) {
		spawned=false;
		area=new AreaSolida(10, 10);
		x=0;
		y=0;
		probability=prob;
		used=false;
	}
	
	/**imposta l'attributo spawned*/
	public void setSpawned(boolean flag){
		spawned=flag;
	}
	
	/**ritorna se è spawnato*/
	public boolean getSpawned(){
		return spawned;
	}
	
	/**imposta la probabilità di spawn*/
	public void setProbabilty(double prob) {
		probability=prob;
	}
	/**ritorna la probabilità di spawn*/
	public double getProbabilty() {
		return probability;
	}
	
	/**metodo che spawna l'oggetto con una probabilità di 1 su 10*/
	public void spawn(int x, int y) {
		double n=(Math.random());
		System.out.println("n " +n);
		System.out.println("prob " +probability);
		if(n<=probability) {
			spawned=true;
		}
		if(spawned){
			GameModel.getInstance().getFireItem().despawn();
			GameModel.getInstance().getSpeedItem().despawn();
			used=true;
			setPosX(x);
			setPosY(y);
			area.setXY(this.x,this.y,0,0);
			GameModel.getInstance().updateController(this);
		}
		
	}
	
	/**despawna un oggetto*/
	public void despawn() {
		spawned=false;
		used=false;
		area.setXY(-100, -100, x, y);
	}
	
	/**imposta se è gia stato utilizzato*/
	public void setUsed(boolean flag){
		used=flag;
	}
	
	/**ritorna utilizzato*/
	public boolean getUsed() {
		return used;
	}
	
	/**imposta la posizione x*/
	public void setPosX(int x) {
		this.x=x;
	}
	
	/**imposta la posizione y*/
	public void setPosY(int y) {
		this.y=y;
	}
	
	/**ritorna la posizione x*/
	public int getPosX() {
		return x;
	}
	
	/**ritorna la posizione y*/
	public int getPosY() {
		return y;
	}
	/**ritorna l'area solida*/
	public AreaSolida getAreaSolida() {
		return area;
	}
	
}
