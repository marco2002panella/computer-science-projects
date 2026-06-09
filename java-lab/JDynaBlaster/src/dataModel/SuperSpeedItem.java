package dataModel;

public class SuperSpeedItem extends Item{
	
	public SuperSpeedItem(double prob) {
		super(prob);
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
			GameModel.getInstance().getSkullItem().despawn();
			used=true;
			setPosX(x);
			setPosY(y);
			area.setXY(this.x,this.y,0,0);
			GameModel.getInstance().updateController(this);
		}
		
	}
	
	
}
