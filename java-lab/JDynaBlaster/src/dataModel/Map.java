package dataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.scene.image.Image;

public class Map {
	private GameModel cd;
	private int larghezza,altezza,blocco;
	private int[][] matrice;
	
	public Map(int larghezza,int altezza, int blocco, GameModel cd,String path){
		this.larghezza=larghezza;
		this.altezza=altezza;
		this.blocco=blocco;
		this.cd=cd;
		matrice=new int[altezza/blocco][larghezza/blocco];
		loadMatrice(path);
	}
	
	/*metodo che cambia una riga e una colonna se possono essere cambiati*/
	public void changeRC(int row,int col){
		
		if(matrice[row][col]==2) {
			
			matrice[row][col]=0;
			
			System.out.println("cambiata "+row+" "+col);
			
			if(!cd.getSpeedItem().getSpawned())
				
				cd.getSpeedItem().spawn(col*40+10,row*40+10+80);
			
			 if(!cd.getSkullItem().getSpawned())
				
				cd.getSkullItem().spawn(col*40+10,row*40+10+80);
			
			 if(!cd.getFireItem().getSpawned())
				
				cd.getFireItem().spawn(col*40+10,row*40+10+80);
			
		}
			
		if(matrice[row][col]==4) {
			
			matrice[row][col]=5;
			System.out.println("cambiato");
		}
		
		cd.updateController(this);
		
	}
	
	/**carica la matrice dal file*/
	public void loadMatrice(String path) {
		if(path!=null) {
			try {
				BufferedReader a=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
				
				String[] bufferedString;
				
				int i=0,j;
				
				while(i<altezza/blocco){
					
					j=0;
					
					bufferedString=a.readLine().split(" ");
					
					while(j<larghezza/blocco){
						
						matrice[i][j]=Integer.parseInt(bufferedString[j]);
						
						System.out.print(matrice[i][j]);
						
						j++;
						
						}
					
					System.out.println();
					
					i++;
				}
				//ok
				System.out.println("mappa caricata");
				
			} catch (FileNotFoundException fileNonTrovato) {
				
				fileNonTrovato.printStackTrace();
				
			}catch(IOException IONonRiuscito) {
				
				IONonRiuscito.printStackTrace();
			}		
		}		
	}
	
	/**ritorna la matrice di interi rappresentante la mappa*/
	public int[][] getMatrix() {
		return matrice;
	}
	
	public int getLarghezza() {
		return larghezza;
	}
	
	/**aggiorna il controller*/
	public void update(){
		cd.updateController(this);
		
	}
	/**controlla la riga*/
	public int checkRow(int y) {
		int r=-1;
		r=y/blocco;
		return r;
	}
	/**controlla la colonna*/
	public int checkCol(int x) {
		int r=-1;
		r=x/blocco;
		return r;
	}
	
	//non ho bisogno di copiare gli oggetti di riferimento per la stampa ma soltanto la matrice 
	/**restituisce un clone della mappa*/
	public Map clone(){
		
		Map clone=new Map(this.larghezza,this.getAltezza(),this.blocco,null,null);
		
		int[][] clone0=clone.getMatrix();
		
		for(int i=0;i<altezza/blocco;i++) {
			for(int j=0;j<larghezza/blocco;j++) {
				clone0[i][j]=this.matrice[i][j];
			}
			System.out.println();
		}
		return clone;
		
	}
	
	public int getAltezza() {
		return altezza;
	}
	
}
