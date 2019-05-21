package com.weiyu.experiment.domain;

/**
 * 新建一个保存仿真结果的实体类
 * @author Wei Yu
 *
 */
public class SimulationResult {
	/**
	 * 工作流应用的数量
	 */
	private int workflowNumber;
	/**
	 * 任务数量
	 */
	private int taskNumber;
	
	/**
	 * 工作流数量跟任务数量组合对应的实例
	 */
	private int instanceNumber;
	
	/**
	 * 重复做的次数
	 */
	private int repeatTime;
	
	/**
	 * 工作流应用的排序方法
	 */
	private String workflowMethod;
	
	/**
	 * 任务排序方法
	 */
	private String rankMethod;
	
	/**
	 * 截止时间的级别
	 */
	private String deadlinelevel;
	
	private String vndMethod;
	
	
	/**
	 * VND中kmax的大小
	 */
	private double kmax; 
	
	/**
	 * VND之前的总电费成本
	 */
	private double elecCostForAllWorkflowsBeforeVND;
	
	/**
	 * VND之后的总电费成本
	 */
	private double elecCostForAllWorkflowsAfterVND;
	
	/**
	 * 单个实例的运行时间
	 */
	private long runtime;
	
	/**
	 * VND之前单个实例对应的RPD值
	 */
	private double rpdBeforeVND;
	
	/**
	 * VND之后单个实例对应的RPD值
	 */
	private double rpdAfterVND;
	
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

	@Override
	public String toString() {
		return workflowNumber 
				+ "\t" + taskNumber 
				+ "\t" + instanceNumber 
//				+ "\t" + repeatTime 
				+ "\t" + workflowMethod 
				+ "\t" + rankMethod
				+ "\t" + deadlinelevel 
				+ "\t" + vndMethod 
				+ "\t" + kmax 
//				+ "\t" + elecCostForAllWorkflowsBeforeVND 
				+ "\t" + elecCostForAllWorkflowsAfterVND 
				+ "\t" + runtime 
//				+ "\t" + rpdBeforeVND
				+ "\t" + rpdAfterVND;
	}
	
	
	
	
}
