package dataModel;

public class CollisionCheckerGhost implements CollisionChecker<Ghost> {

	@Override
	public boolean collide(Ghost in, Movement<Ghost> in0, GameModel in1, String direction) {
		
		return false;
	}

}
