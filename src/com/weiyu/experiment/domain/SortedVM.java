package com.weiyu.experiment.domain;

import org.workflowsim.CondorVM;

/**
 * ����һ��ʵ���࣬��������VM�����ܺı�����
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
