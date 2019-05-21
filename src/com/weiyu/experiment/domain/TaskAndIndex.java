package com.weiyu.experiment.domain;

import org.workflowsim.Task;

public class TaskAndIndex {
	private Task task;
	private int index;
	
	public TaskAndIndex(Task task, int index){
		this.task = task;
		this.index = index;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
