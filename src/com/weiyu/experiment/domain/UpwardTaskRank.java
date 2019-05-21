package com.weiyu.experiment.domain;

import org.workflowsim.Task;

public class UpwardTaskRank implements Comparable<UpwardTaskRank> {
	private Task task;
	private Double rank;
	/**
	 * 排序标志，如果flag=0，表示递增排序，如果为1表示递减排序
	 */
	private int flag;

	public UpwardTaskRank(Task task, Double rank) {
		this.task = task;
		this.rank = rank;
	}
	
	public UpwardTaskRank(Task task, Double rank, int flag) {
		this.task = task;
		this.rank = rank;
		this.flag = flag;
	}

	@Override
	public int compareTo(UpwardTaskRank o) {
		if(1 == flag)
			return o.getRank().compareTo(rank);
		else
			return rank.compareTo(o.getRank());
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
}