package com.weiyu.experiment.domain;

import org.workflowsim.Task;

public class Event implements Cloneable{
	public Task task;
	public double start;
	public double finish;

	public Event(double start, double finish) {
		this.start = start;
		this.finish = finish;
	}

	public Event(Task task, double start, double finish) {
		this.task = task;
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