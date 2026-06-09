package dataModel;

import java.util.Iterator;
import java.util.List;

public class CollisionCheckerBaloon extends DirectionChecker implements CollisionChecker<Baloon>{
	
	/**controlla se il baloon collide con gli oggetti della mappa e gli altri enemies*/
	@Override
	public boolean collide(Baloon in, Movement<Baloon> in0, GameModel in1, String direction) {
		int i,j;
		int x,y;
		int row,col;
		AreaSolida baloon=in.getBaloonSolid();
		Iterator<AreaSolida> solids=in1.getSolids().iterator();
		boolean flag=false;	
		
		check(direction);
		
		 if(su){
			 y=in.getY()-5;
			 x=in.getX();
			 baloon.setXY(x,y,0,0);
			 in.setDirezione("su");
		 }
		 
		else if(giu){
			y=in.getY()+5;
			 x=in.getX();
			 baloon.setXY(x,y,0,0);
			 in.setDirezione("giu");
			
		}
		
		else if(sinistra){
			 y=in.getY();
			 x=in.getX()-5;
			 baloon.setXY(x,y,0,0);
			 in.setDirezione("sinistra");
		}
		
		else if(destra){
			y=in.getY();
			 x=in.getX()+5;
			 baloon.setXY(x,y,0,0);
			 in.setDirezione("destra");
		}
		 
		 while(solids.hasNext()) {
			 
			 AreaSolida check=solids.next();
			 
			 if(baloon.interseca(check))
				 flag=true;
		 }
		 
		 List<Entity> enemies=in1.getEnemies();
		 for(Entity s:enemies) {
			 if(!s.equals(in)) {
				 if(s instanceof Baloon &&((Baloon)s).getBaloonSolid().interseca(baloon))
					 flag=true;
				 if(s instanceof Ghost && ((Ghost)s).getGhostSolid().interseca(baloon))
					 flag=true;
			 } 
		 }	 
		 
		 this.setFalse();
		
		 return flag;
	}

}
