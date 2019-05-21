package com.weiyu.experiment.domain;

import org.workflowsim.CondorVM;

/**
 * 定义一个实体类，用来根据VM性能能耗比排序
 * @author Wei Yu
 *
 */
public class SortedVM implements Comparable<SortedVM>{
	private CondorVM vm;
	private Double ppW;
	public SortedVM(CondorVM vm, Double ppW){
		this.vm = vm;
		this.ppW = ppW;
	}
	
	public CondorVM getVm() {
		return vm;
	}

	public void setVm(CondorVM vm) {
		this.vm = vm;
	}

	public Double getPpW() {
		return ppW;
	}
	
	public void setPpW(Double ppW) {
		this.ppW = ppW;
	}
	
	@Override
	public int compareTo(SortedVM vm) {
		return vm.getPpW().compareTo(ppW);
	}
}
