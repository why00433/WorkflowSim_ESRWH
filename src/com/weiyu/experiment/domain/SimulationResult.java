package com.weiyu.experiment.domain;

import org.workflowsim.utils.Parameters;

/**
 * �½�һ�������������ʵ����
 * @author Wei Yu
 *
 */
public class SimulationResult {
	/**
	 * ������Ӧ�õ�����
	 */
	private int workflowNumber;
	/**
	 * ��������
	 */
	private int taskNumber;
	
	/**
	 * ����������������������϶�Ӧ��ʵ��
	 */
	private int instanceNumber;
	
	/**
	 * �ظ����Ĵ���
	 */
	private int repeatTime;
	
	/**
	 * ������Ӧ�õ����򷽷�
	 */
	private String workflowMethod;
	
	/**
	 * �������򷽷�
	 */
	private String rankMethod;

	private  String allocatingMethod;

	private  String reliabilityLevel;
	
	/**
	 * ��ֹʱ��ļ���
	 */
	private String deadlinelevel;
	
	private String vndMethod;

	private String experimentAlgorithm;
	
	/**
	 * VND��kmax�Ĵ�С
	 */
	private double kmax; 
	
	/**
	 * VND֮ǰ���ܵ�ѳɱ�
	 */
	private double elecCostForAllWorkflowsBeforeVND;
	
	/**
	 * VND֮����ܵ�ѳɱ�
	 */
	private double elecCostForAllWorkflowsAfterVND;
	
	/**
	 * ����ʵ��������ʱ��
	 */
	private long runtime;
	
	/**
	 * VND֮ǰ����ʵ����Ӧ��RPDֵ
	 */
	private double rpdBeforeVND;
	
	/**
	 * VND֮�󵥸�ʵ����Ӧ��RPDֵ
	 */
	private double rpdAfterVND;

	private double rpd;


	/**
	 * �������������ܺ�
	 */
	private double totalEnergy;
	
	public SimulationResult(){}
	
	public SimulationResult(int workflowNumber, int taskNumber, int instanceNumber, String workflowMethod,
			String rankMethod, String deadlinelevel, double kmax, double elecCostForAllWorkflowsBeforeVND,
			double elecCostForAllWorkflowsAfterVND, long runtime, double rpdBeforeVND, double rpdAfterVND) {
		super();
		this.workflowNumber = workflowNumber;
		this.taskNumber = taskNumber;
		this.instanceNumber = instanceNumber;
		this.workflowMethod = workflowMethod;
		this.rankMethod = rankMethod;
		this.deadlinelevel = deadlinelevel;
		this.kmax = kmax;
		this.elecCostForAllWorkflowsBeforeVND = elecCostForAllWorkflowsBeforeVND;
		this.elecCostForAllWorkflowsAfterVND = elecCostForAllWorkflowsAfterVND;
		this.runtime = runtime;
		this.rpdBeforeVND = rpdBeforeVND;
		this.rpdAfterVND = rpdAfterVND;
	}

	public int getWorkflowNumber() {
		return workflowNumber;
	}
	
	public void setWorkflowNumber(int workflowNumber) {
		this.workflowNumber = workflowNumber;
	}
	
	public int getTaskNumber() {
		return taskNumber;
	}
	
	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}
	
	public int getInstanceNumber() {
		return instanceNumber;
	}
	
	public void setInstanceNumber(int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	
	public double getElecCostForAllWorkflowsBeforeVND() {
		return elecCostForAllWorkflowsBeforeVND;
	}
	
	public void setElecCostForAllWorkflowsBeforeVND(double elecCostForAllWorkflowsBeforeVND) {
		this.elecCostForAllWorkflowsBeforeVND = elecCostForAllWorkflowsBeforeVND;
	}
	
	public double getElecCostForAllWorkflowsAfterVND() {
		return elecCostForAllWorkflowsAfterVND;
	}
	
	public void setElecCostForAllWorkflowsAfterVND(double elecCostForAllWorkflowsAfterVND) {
		this.elecCostForAllWorkflowsAfterVND = elecCostForAllWorkflowsAfterVND;
	}
	
	public long getRuntime() {
		return runtime;
	}
	
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}
	
	public String getWorkflowMethod() {
		return workflowMethod;
	}
	
	public void setWorkflowMethod(String workflowMethod) {
		this.workflowMethod = workflowMethod;
	}
	
	public String getRankMethod() {
		return rankMethod;
	}
	
	public void setRankMethod(String rankMethod) {
		this.rankMethod = rankMethod;
	}
	
	public String getDeadlinelevel() {
		return deadlinelevel;
	}
	
	public void setDeadlinelevel(String deadlinelevel) {
		this.deadlinelevel = deadlinelevel;
	}
	
	public double getKmax() {
		return kmax;
	}
	
	public void setKmax(double kmax) {
		this.kmax = kmax;
	}

	public double getRpdBeforeVND() {
		return rpdBeforeVND;
	}

	public void setRpdBeforeVND(double rpdBeforeVND) {
		this.rpdBeforeVND = rpdBeforeVND;
	}

	public double getRpdAfterVND() {
		return rpdAfterVND;
	}

	public void setRpdAfterVND(double rpdAfterVND) {
		this.rpdAfterVND = rpdAfterVND;
	}
	
	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}
	
	public String getVndMethod() {
		return vndMethod;
	}

	public void setVndMethod(String vndMethod) {
		this.vndMethod = vndMethod;
	}


	public String getAllocatingMethod() {
		return allocatingMethod;
	}

	public void setAllocatingMethod(String allocatingMethod) {
		this.allocatingMethod = allocatingMethod;
	}

	public String getReliabilityLevel() {
		return reliabilityLevel;
	}

	public void setReliabilityLevel(String reliabilityLevel) {
		this.reliabilityLevel = reliabilityLevel;
	}

	public double getTotalEnergy() {
		return totalEnergy;
	}

	public void setTotalEnergy(double totalEnergy) {
		this.totalEnergy = totalEnergy;
	}

	public double getRpd() {
		return rpd;
	}

	public void setRpd(double rpd) {
		this.rpd = rpd;
	}


	public String getExperimentAlgorithm() {
		return experimentAlgorithm;
	}

	public void setExperimentAlgorithm(String experimentAlgorithm) {
		this.experimentAlgorithm = experimentAlgorithm;
	}

	@Override
	public String toString() {
		return  taskNumber
				+ "\t" + instanceNumber 
//				+ "\t" + repeatTime
				+ "\t" + rankMethod
				+ "\t" + allocatingMethod
				+ "\t" + deadlinelevel 
				+ "\t" + reliabilityLevel
				+ "\t" + totalEnergy
				+ "\t" + rpd
				+ "\t" + runtime;
	}

	public String toString2() {
		return  taskNumber
				+ "\t" + instanceNumber
//				+ "\t" + repeatTime
				+ "\t" + deadlinelevel
				+ "\t" + reliabilityLevel
				+ "\t" + experimentAlgorithm
				+ "\t" + totalEnergy
				+ "\t" + rpd
				+ "\t" + runtime;
	}




}
