package com.weiyu.experiment.domain;

public class WorkflowAttribute implements Comparable<WorkflowAttribute>{
	private Integer index;
	private Double value;
	
	public WorkflowAttribute(int index, Double value){
		this.index = index;
		this.value = value;
	}

	@Override
	public int compareTo(WorkflowAttribute deadline) {
		return value.compareTo(deadline.getValue());
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
