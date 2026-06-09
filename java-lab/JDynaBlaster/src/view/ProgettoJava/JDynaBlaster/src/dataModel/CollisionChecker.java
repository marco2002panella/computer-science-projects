package dataModel;
@FunctionalInterface

public interface CollisionChecker<T extends Entity>{
	
	public boolean collide(T in,Movement<T> in0,GameModel in1, String direction);
	
}
