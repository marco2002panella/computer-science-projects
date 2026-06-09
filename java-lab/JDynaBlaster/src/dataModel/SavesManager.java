package dataModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SavesManager{
	
	private static SavesManager saves;
	private ArrayList<Saves> list;
	private MemLoader<ArrayList<Saves>> loader;
	private SavesManager() {
		
		list=new ArrayList<Saves>();
		
		loader=list->{
			if(list.size()!=0) {
				list.removeAll(list);
			}
			File directory=new File("C:\\Users\\david\\eclipse-workspace\\JDynaBlaster\\src\\saves");
			File[] l=directory.listFiles();
			for(File f:l) {		
				list.add(new Saves(f.getAbsolutePath()));
			}
			for(Saves a:list) {
				System.out.println(a);
			}
		};
		loader.load(list);
	}

	/**ritorna l instanza di savesManager*/
	public static SavesManager getInstance(){
		if(saves==null) {
			saves=new SavesManager();
		} 
		return saves;
		
	}
	
	/**crea un salvataggio,lo aggiunge in memoria e lo aggiunge alla lista dei salvataggi correnti*/
	public void addSaves(String name,int color){
		Saves nuovo=new Saves(name,color);
		list.add(nuovo);
	}
	
	public Saves getLastInsert() {
		return list.get(list.size()-1);
	}
	
	/**imposta il loader*/
	public void setLoader(MemLoader<ArrayList<Saves>> in) {
		loader=in;
	}
	
	/**carica i salvataggi*/
	public void load() {
		loader.load(list);
	}
	/**ritorna i saltavataggi sotto forma di arryList di salvataggi in stringa*/
	public ArrayList<String> getSaves(){
		ArrayList<String> s=new ArrayList<String>();
		for(Saves se:list) {
			s.add(se.toString());
		}
		return s;
	}


	
}
