package gui.component;

import gui.observers.EventChange;
import gui.observers.EventListener;
import gui.observers.EventSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import ast.evaluate.Value;

public abstract class Control implements EventSource{

	private List<EventListener> eventListeners;

	public abstract JComponent getComponent();
	public abstract Value getValue();

	public Control() {
		eventListeners = new ArrayList<EventListener>();
	}

	protected synchronized void publishEventChange(){
		EventChange event = new EventChange(this);
		//event.getSource()
		Iterator<EventListener> itr = eventListeners.iterator();
		while(itr.hasNext()){
			itr.next().handleEvent(event);
		}
	}

	public synchronized void addListener(EventListener e){
		eventListeners.add(e);
	}
	public synchronized void removeListener(EventListener e){
		eventListeners.remove(e);
	}
}