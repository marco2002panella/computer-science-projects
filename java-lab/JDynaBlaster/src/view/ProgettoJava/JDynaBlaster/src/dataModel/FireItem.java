package dataModel;

public class FireItem extends Item{

	public FireItem(double prob) {
		super(prob);
	}
	
	/**metodo che spawna l'oggetto*/
	public void spawn(int x, int y) {
		double n=(Math.random());
		System.out.println("n " +n);
		System.out.println("prob " +probability);
		
		if(n<=probability) {
			spawned=true;
		}
		
		if(spawned){
			GameModel.getInstance().getSkullItem().despawn();
			GameModel.getInstance().getSpeedItem().despawn();
			used=true;
			setPosX(x);
			setPosY(y);
			area.setXY(this.x,this.y,0,0);
			GameModel.getInstance().updateController(this);
		}
		
	}
}
