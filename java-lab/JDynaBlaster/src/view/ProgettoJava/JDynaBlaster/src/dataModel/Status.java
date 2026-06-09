package dataModel;


public class Status {
	
	private int stato;
	private long timer;
	
	public Status(int stato){
		this.stato=stato;
	}
	
	public void setStato(int in) {
		stato=in;	
	}
	
	public int getStato(){
		return stato;
	}
	
	
}
