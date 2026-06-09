package dataModel;

public interface Movement<T extends Entity>{
	
	public void muovi(T in,String direction);
	public int getSpeed();
}
