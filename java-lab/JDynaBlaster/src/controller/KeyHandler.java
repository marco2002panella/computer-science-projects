package controller;

import java.util.function.Function;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class KeyHandler implements EventHandler<KeyEvent>{
	
	private boolean w,a,s,d,qb,spac,entr,any;
	
	private Function<KeyCode,Boolean> up=keycode->keycode.equals(keycode.W);
	private Function<KeyCode,Boolean> left=keycode->keycode.equals(keycode.A);
	private Function<KeyCode,Boolean> down=keycode->keycode.equals(keycode.S);
	private Function<KeyCode,Boolean> rigth=keycode->keycode.equals(keycode.D);
	private Function<KeyCode,Boolean> space=keycode->keycode.equals(keycode.SPACE);
	private Function<KeyCode,Boolean> enter=keycode->keycode.equals(keycode.ENTER);
	private Function<KeyCode,Boolean> q=keycode->keycode.equals(keycode.Q);
	private Function<KeyCode,Boolean> e=keycode->keycode.equals(keycode.E);
	private Function<KeyCode,Boolean> r=keycode->keycode.equals(keycode.R);
	private Function<KeyCode,Boolean> t=keycode->keycode.equals(keycode.T);
	private Function<KeyCode,Boolean> y=keycode->keycode.equals(keycode.Y);
	private Function<KeyCode,Boolean> u=keycode->keycode.equals(keycode.U);
	private Function<KeyCode,Boolean> i=keycode->keycode.equals(keycode.I);
	private Function<KeyCode,Boolean> o=keycode->keycode.equals(keycode.O);
	private Function<KeyCode,Boolean> p=keycode->keycode.equals(keycode.P);
	private int func=0;	
	@Override
	
	public void handle(KeyEvent arg0) {
		KeyCode t=arg0.getCode();
		if(arg0.getEventType()==KeyEvent.KEY_PRESSED) {
			w=up.apply(t);
			a=left.apply(t);
			s=down.apply(t);
			d=rigth.apply(t);
			spac=space.apply(t);
			entr=enter.apply(t);
			qb=q.apply(t);
			any=w || a || s || d ||spac ||entr;
		}else if(arg0.getEventType()==KeyEvent.KEY_RELEASED) {
			if(w)
				pressedW();
			if(d)
				pressedD();
			if(a)
				pressedA();
			if(s)
				pressedS();
			if(spac)
				pressedSpace();
			if(entr)
				pressedEnter();
			if(qb)
				qb=false;
			any=false;
		}
			
	}
	
	public void pressedAny() {
		any=false;
	}
	
	public void pressedW() {
		w=false;
	}
	
	public void pressedS() {
		s=false;
	}
	
	public void pressedD() {
		d=false;
	}
	
	public void pressedA() {
		a=false;
	}
	
	public boolean getW(){
		return w;
	}
	public void pressedSpace() {
		spac=false;
	}
	public void pressedEnter() {
		entr=false;
	}
	
	public boolean getS(){
		return s;
	}
	public boolean getA(){
		return a;
	}
	public boolean getD(){
		return d;
	}
	
	public boolean getAny(){
		return any;
	}
	
	public boolean getQ(){
		return qb;
	}

}
