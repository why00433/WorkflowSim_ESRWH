package com.weiyu.experiment.domain;

import org.workflowsim.Task;

public class TaskRank implements Comparable<TaskRank> {
	protected Task task;
	protected Double rank;
	/**
	 * �����־�����flag=0����ʾ�����������Ϊ1��ʾ�ݼ�����
	 */
	protected int flag;

	public TaskRank(Task task, Double rank) {
		this.task = task;
		this.rank = rank;
	}

	public TaskRank(Task task, Double rank, int flag) {
		this.task = task;
		this.rank = rank;
		this.flag = flag;
	}

	@Override
	public int compareTo(TaskRank o) {
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