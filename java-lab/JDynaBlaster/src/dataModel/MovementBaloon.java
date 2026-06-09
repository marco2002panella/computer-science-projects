package dataModel;

public class MovementBaloon extends DirectionChecker implements Movement<Baloon> {
	private final int speed=1;
	/**muove l'oggetto di tipo baloon della direzione direction*/
	@Override
	public void muovi(Baloon in, String direction) {
		check(direction);
		if(giu) {
			in.setY(in.getY()+speed);
		}
		if(destra) {
			in.setX(in.getX()+speed);
		}
		if(sinistra) {
			in.setX(in.getX()-speed);
		}
		if(su) {
			in.setY(in.getY()-speed);
		}
		in.setDirezione(direction);
		setFalse();
	}

	@Override
	public int getSpeed() {
		return speed;
	}

}
