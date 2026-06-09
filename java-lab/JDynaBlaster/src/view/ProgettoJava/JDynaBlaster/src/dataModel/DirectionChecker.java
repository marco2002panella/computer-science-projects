package dataModel;

import java.util.function.Function;

public class DirectionChecker {
	
	protected boolean su,giu,sinistra,destra;
	
	Function<String,Boolean> checkSu=(stringa)->{return stringa.equals("su");};
	Function<String,Boolean> checkGiu=(stringa)->{return stringa.equals("giu");};
	Function<String,Boolean> checkSinistra=(stringa)->{return stringa.equals("sinistra");};
	Function<String,Boolean> checkDestra=(stringa)->{return stringa.equals("destra");};
	
	
	/**asserisce l'attributo con la direzione*/
	public void check(String direction){
		su=checkSu.apply(direction);
		giu=checkGiu.apply(direction);
		sinistra=checkSinistra.apply(direction);
		destra=checkDestra.apply(direction);
	}
	
	/**disasserisce tutti i segnali*/
	public void setFalse() {
		su=false;
		giu=false;
		sinistra=false;
		destra=false;
	}
}
