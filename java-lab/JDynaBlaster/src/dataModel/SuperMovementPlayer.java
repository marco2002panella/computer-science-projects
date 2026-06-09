package dataModel;

public class SuperMovementPlayer extends DirectionChecker implements Movement<Player>{
	private int speed=5;
	private long lastTime;
	private int seconds;
	public SuperMovementPlayer() {lastTime=System.nanoTime(); seconds=5;}
		@Override
		public void muovi(Player in,String direzione) {
			if(System.nanoTime()-lastTime>seconds*1000000000l)
				in.setMovement(new MovementPlayer());
			
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
