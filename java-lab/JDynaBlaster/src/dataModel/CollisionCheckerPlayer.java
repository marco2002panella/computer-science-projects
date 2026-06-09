package dataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public  class CollisionCheckerPlayer extends DirectionChecker implements CollisionChecker<Player>{
	
	private int x,y,g,h;
	/**controlla se il player collide con la mappa, con le enemies e i punti */
	@Override
	public boolean collide(Player in, Movement<Player> in0, GameModel in1,String direction) {
		int i,j;
		int x,y;
		int row,col;
		AreaSolida player=in.getPlayerSolid();
		Iterator<AreaSolida> solids=in1.getSolids().iterator();
		boolean flag=false;	
		check(direction);
		
		 if(su){
			 y=in.getY()-in0.getSpeed();
			 x=in.getX();
			 player.setXY(x,y,5,16);
			 in.setDirezione("su");
		 }
		 
		else if(giu){
			y=in.getY()+in0.getSpeed();
			 x=in.getX();
			 player.setXY(x,y,5,16);
			 in.setDirezione("giu");
			
		}
		
		else if(sinistra){
			 y=in.getY();
			 x=in.getX()-in0.getSpeed()-1;
			 player.setXY(x,y,5,16);
			 in.setDirezione("sinistra");
		}
		
		else if(destra){
			y=in.getY();
			 x=in.getX()+in0.getSpeed();
			 player.setXY(x,y,5,16);
			 in.setDirezione("destra");
		}
		 
		 while(solids.hasNext()) {
			 
			 AreaSolida check=solids.next();
			 
			 if(player.interseca(check) || check.interseca(player))
				 flag=true;
		 }
		 
		 List<Entity> enemies=in1.getEnemies();
		 
		 for(Entity s:enemies) {
			 if(!s.equals(in)) {
				 if(s instanceof Baloon &&((Baloon)s).getBaloonSolid().interseca(player) && !((Baloon)s).isDead()) {
					 in.dead();
				 }else if(s instanceof Baloon && ((Baloon)s).getBaloonSolid().interseca(player) && ((Baloon)s).isDead() && !((Baloon)s).getNone()) {
					 in1.setPoints(in1.getPoints()+100);
					 ((Baloon)s).setNone(true);
					 in1.updateController((Baloon)s);
				 }
					 
				 if(s instanceof Ghost && ((Ghost)s).getGhostSolid().interseca(player)&& !((Ghost)s).isDead()) {
					 in.dead();
				 }else if(s instanceof Ghost && ((Ghost)s).getGhostSolid().interseca(player)&& ((Ghost)s).isDead() && !((Ghost)s).getNone()) {
					 in1.setPoints(in1.getPoints()+150);
					 ((Ghost)s).setNone(true);
					 in1.updateController((Ghost)s);
				 }
			 } 
		 }
		 
		 
		this.setFalse();
		return flag;
	}


}
