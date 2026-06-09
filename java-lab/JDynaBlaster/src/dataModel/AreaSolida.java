package dataModel;

public class AreaSolida implements Cloneable{
	
	private Punto xy,xy0,xy1,xy2;
	private int latox,latoy;
	public AreaSolida(int latox,int latoy) {
		
		xy=new Punto(0,0);
		xy0=new Punto(0,0);
		xy1=new Punto(0,0);
		xy2=new Punto(0,0);
		this.latox=latox;
		this.latoy=latoy;
	}
	
	public void setXY(int x,int y,int offSetx,int offSety) {
		xy.setX(x+offSetx);
		xy.setY(y+offSety);
		xy0.setX(x+latox+offSetx);
		xy0.setY(y+offSety);
		xy1.setX(x+offSetx);
		xy1.setY(y+latoy+offSety);
		xy2.setX(x+latox+offSetx);
		xy2.setY(y+latoy+offSety);
	}
	
	/**ritorna il lato orizzontale dell'area solida*/
	public int getLatoX() {
		return latox;
	}
	/**ritorna il lato verticale dell'area solida*/
	public int getLatoY() {
		return latoy;
	}
	/**imposta il punto in alto a sinistra*/
	public void setXY(Punto xy){
		this.xy=xy;
	}
	/**imposta il punto in alto a destra*/
	public void setXY0(Punto xy0){
		this.xy0=xy0;
	}
	/**imposta il punto in basso a sinistra*/
	public void setXY1(Punto xy1){
		this.xy1=xy1;
	}
	/**imposta il punto in basso a destra*/
	public void setXY2(Punto xy2){
		this.xy2=xy2;
	}
	
	/*metodo che controlla se questa area solida viene intersecata da quella in input*/
	public boolean interseca(AreaSolida in){
		boolean flag=false;
		
		if(in!=null) {
			int x,y,x1,y1;
			x=in.getXY().getX();
			y=in.getXY().getY();
			x1=in.getXY0().getX();
			y1=in.getXY1().getY();
			flag=(compreso(x,xy.getX(),xy0.getX()) || compreso(x1,xy.getX(),xy0.getX()) || compreso(xy.getX(),x,x1) || compreso(xy0.getX(),x,x1)) 
					&& (compreso(y,xy.getY(),xy1.getY()) || compreso(y1,xy.getY(),xy1.getY()) || compreso(xy.getY(),y,y1) || compreso(xy1.getY(),y,y1)) ;
		}
		
		//essendo un'area solida al massimo un rettangolo dobbiamo controllare se uno dei quattro angoli è compreso nella nostra figura
		return flag;
	}
	
	/**controlla se un punto è compreso tra due punti*/
	public boolean compreso(int i,int x,int x1){
		boolean flag=i<=x1 && i>=x;
		return flag;
	}

	/**restituisce una stringa dei 4 punti*/
	public String toString() {
		return "xy "+this.getXY().getX()+" "+this.getXY().getY()+"\nxy0 "+this.getXY0().getX()+" "+this.getXY0().getY()+
				"\nxy1 "+this.getXY1().getX()+" "+this.getXY1().getY()+"\nxy2 "+this.getXY2().getX()+" "+this.getXY2().getY();
		
	}
	/**clona l'area solida*/
	public Object clone() {
		try {
			AreaSolida clone=(AreaSolida)super.clone();
			clone.setXY((Punto)xy.clone());
			clone.setXY0((Punto)xy0.clone());
			clone.setXY1((Punto)xy1.clone());
			clone.setXY2((Punto)xy2.clone());
			
		} catch (CloneNotSupportedException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	/**ritorna il punto in alto a sinistra*/
	public Punto getXY() {
		return xy;
	}
	/**ritorna il punto in alto a destra*/
	public Punto getXY0() {
		return xy0;
	}
	/**ritorna il punto in basso a sinistra*/
	public Punto getXY1() {
		return xy1;
	}
	/**ritorna il punto in basso a destra*/
	public Punto getXY2() {
		return xy2;
	}
	
}
