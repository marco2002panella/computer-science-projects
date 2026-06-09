package dataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashSet;

public class Saves implements Cloneable{
	
	private int colorePersonaggio;
	private String nickname;
	private int partiteVinte;
	private int partitePerse;
	private int livello;
	private int vite;
	
	private static final String basePath="C:\\Users\\david\\eclipse-workspace\\JDynaBlaster\\src\\saves";
	private String fullPath;
	
	private MemStorer<Saves> storer=in -> {
		if(in instanceof Saves) {
			File file=new File(fullPath);
			Saves o=(Saves)in;
			if(file.exists()) {
				try {
					FileWriter writer = new FileWriter(file);
		            writer.write(o.toString());
		            writer.close();
		            System.out.println(this);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}else {
				try {
					FileWriter writer = new FileWriter(file,false);
		            writer.write(o.toString());
		            writer.close();
		            System.out.println("non esiste "+o);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private MemLoader<String> loader=in->{
		try {
			File file=new File(in);
			FileInputStream stream=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(stream));
			try {
				String s=""+br.readLine();
				String[] arr=s.split("\\|");
				nickname=arr[0];
				colorePersonaggio=Integer.parseInt(arr[1]);
				partiteVinte=Integer.parseInt(arr[2]);
				partitePerse=Integer.parseInt(arr[3]);
				livello=Integer.parseInt(arr[4]);
				vite=Integer.parseInt(arr[5]);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	};
	
	private MemStorer<Saves> realStorer=storer;
	private MemLoader<String> realLoader=loader;
	
	public Saves(String nickname,int colore) {
		this.nickname=""+nickname;
		colorePersonaggio=colore;
		partiteVinte=0;
		partitePerse=0;
		livello=0;
		vite=3;
		fullPath=basePath+"/"+nickname+".txt";
		realStorer.store(this);
	}
	
	public Saves(String path){
		fullPath=path;
		realLoader.load(path);
	}
	
	/**crea un salvataggio da una stringa di formato nomeprofilo|ncolore|nvittore|nsconfitte|nlivello|nvite*/
	public static Saves getSave(String save) {
		String s=save.split("\\|")[0];
		Saves r=new Saves(basePath+"/"+s+".txt");
		return r;
	}
	
	/**restituisce un clone dell oggetto*/
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**imposta come viene fatto lo store*/
	public void setStorer(MemStorer<Saves> st){
		this.realStorer=storer;
	}
	/**incrementa le vittorie*/
	public void increaseWin() {
		partiteVinte+=1;
	}
	/**incrementa le partite perse*/
	public void increaseGameOver(){
		partitePerse+=1;
	}
	/**fa lo store*/
	public void store(){
		storer.store(this);
		SavesManager.getInstance().load();
	}
	/**aumenta il livello*/
	public void increaseLevel(){
		livello+=1;
	}
	/**diminuisce il numero di vite*/
	public void decreaseLife(){
		vite-=1;
	}
	/**imposta il numero di vite*/
	public void setVite(int i){
		vite=i;
	}
	/**impsota il livello*/
	public void setLivello(int i){
		livello=i;
	}
	/**ritorna il livello*/
	public int getLivello(){
		return livello;
	}
	/**ritorna le vite*/
	public int getVite(){
		return vite;
	}

	/**ne restituisce un formato stringa*/
	public String toString() {
		return nickname+"|"+colorePersonaggio+"|"+partiteVinte+"|"+partitePerse+"|"+livello+"|"+vite;
	}
	
}
