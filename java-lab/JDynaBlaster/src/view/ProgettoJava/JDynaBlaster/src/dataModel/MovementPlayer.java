package dataModel;

public class MovementPlayer extends DirectionChecker implements Movement<Player>{
	private int speed=2;
	/**muove l'oggetto di tipo player nella direzione direction*/
	@Override
	public void muovi(Player in,String direzione) {
		check(direzione);
		if(giu) {
			
			in.setY(in.getY()+speed);
			
		}
		if(su) {
			
			in.setY(in.getY()-speed);

		}
		if(sinistra) {
			
			in.setX(in.getX()-speed);
			
		}
		if(destra) {
			
			in.setX(in.getX()+speed);
			
		}
		in.setDirezione(direzione);
		
		setFalse();
		
	}
	
	public void setSpeed(int i) {
		speed=i;
	}
	
	public int getSpeed(){
		return speed;
	}

}
