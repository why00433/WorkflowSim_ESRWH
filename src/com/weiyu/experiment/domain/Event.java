package com.weiyu.experiment.domain;

public class Event implements Cloneable{
	public double start;
	public double finish;

	public Event(double start, double finish) {
		this.start = start;
		this.finish = finish;
	}

	@Override
	public Event clone(){
		Event event = null;
		try{
			event = (Event)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return event;
	}

	@Override
	public String toString() {
		return "Event [start=" + start + ", finish=" + finish + "]";
	}
	
}