package com.weiyu.experiment.domain;

import org.workflowsim.Task;

public class DownwardTaskRank implements Comparable<DownwardTaskRank> {
	private Task task;
	private Double rank;
	/**
	 * �����־�����flag=0����ʾ�����������Ϊ1��ʾ�ݼ�����
	 */
	private int flag;

	public DownwardTaskRank(Task task, Double rank) {
		this.task = task;
		this.rank = rank;
	}

	public DownwardTaskRank(Task task, Double rank, int flag) {
		this.task = task;
		this.rank = rank;
	}

	@Override
	public int compareTo(DownwardTaskRank o) {
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