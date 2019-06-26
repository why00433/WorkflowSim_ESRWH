package com.weiyu.experiment;

import com.weiyu.experiment.calculateSL.RHEFT;
import com.weiyu.experiment.domain.*;
import com.weiyu.experiment.resourceAllocating.SerachPMUtils;
import com.weiyu.experiment.taskassigning.TaskAssigningUtils;
import com.weiyu.experiment.tasksequence.DownwardRankMethod;
import com.weiyu.experiment.tasksequence.RankMethod;
import com.weiyu.experiment.tasksequence.UpwardRankMethod;
import com.weiyu.experiment.utils.CloneUtils;
import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.planning.BasePlanningAlgorithm;
import org.workflowsim.utils.Parameters;

import java.util.*;

/**
 * Created by Administrator on 2019/5/27.
 */
public class QFEC extends ESRWHAlgorithm {
    /**
     * 工作流应用的ID；
     */
    private int workflowId;

    /**
     * 可靠性约束；
     */
    private double reliability = 0.0;

    /**
     * 每个任务的平均计算时间
     */
    private Map<Task, Double> computationTimes = null;

    /**
     * 每个任务的数据传输时间
     */
    private Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = null;

    /**
     * 每个任务的数据传输时间
     */
    private Map<Task, Double> originalDataTransmissionTimes = null;
    /**
     * 任务的upward rank
     */
    private Map<Task, Double> rank = null;

    /**
     * 任务排序 ---why
     */
    private List<TaskRank> ranks = null;

    /**
     * 每个VM的调度列表
     */
    private Map<CondorVM, List<Event>> schedules = null;

    /**
     * 工作流应用划分时对应的最早开始时间
     */
    private Map<Task, Double> estsForWST = null;
    /**
     * 工作流应用划分时对应的最早完成时间列表
     */
    private Map<Task, Double> eftsForWST = null;
    /**
     * 子截止时间划分时对应的最早开始时间
     */
    private Map<Task, Double> estsForSubdeadline = null;
    /**
     * 子截止时间划分时对应的最早完成时间列表
     */
    private Map<Task, Double> eftsForSubdeadline = null;

    /**
     * 每个任务的子截止时间列表
     */
    private Map<Task, Double> subdeadlines = null;

    private List<UpwardTaskRank> upwardRankList = null;

    /**
     * 计算工作流应用排序时的最早完成时间，这个最早完成时间中，通过任务的平均时间来预估任务的计算时间
     */
    private double lastEFTForWST;

    /**
     * 为每个任务划分子截止时间，以最快的计算时间来作为任务的平均预估时间
     */
    private double lastEFTForSubdeadline;

    List<Map<Task, Double>> computationTimesList = null;
    List<Map<Task, Double>> originalDataTransmissionTimesList = null;
    List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList = null;

	/*
	 * private class Event { public double start; public double finish;
	 *
	 * public Event(double start, double finish) { this.start = start;
	 * this.finish = finish; } }
	 */

	/*
	 * private class TaskRank implements Comparable<TaskRank> { public Task
	 * task; public Double rank;
	 *
	 * public TaskRank(Task task, Double rank) { this.task = task; this.rank =
	 * rank; }
	 *
	 * @Override public int compareTo(TaskRank o) { return
	 * o.rank.compareTo(rank); } }
	 */

    public QFEC() {
        rank = new HashMap<>();
        schedules = new HashMap<>();
        upwardRankList = new ArrayList<>();
        computationTimesList = new ArrayList<>();
        originalDataTransmissionTimesList = new ArrayList<>();
        generatedDataTransmissionTimesList = new ArrayList<>();
        generatedDataTransmissionTimes = new HashMap<>();
        computationTimes = new HashMap<>();
        // computationCosts = new HashMap<>();
        // computationTimes = new HashMap<>();
        // generatedDataTransmissionTimes = new HashMap<>();
        // originalDataTransmissionTimes = new HashMap<>();
        // estsForWST = new HashMap<>();
        // eftsForWST = new HashMap<>();
        // estsForSubdeadline = new HashMap<>();
        // eftsForSubdeadline = new HashMap<>();
        // subdeadlines = new HashMap<>();
        // lastEFTForWST = 0.0;
        // lastEFTForSubdeadline = 0.0;
    }

    @Override
    public void run() {
        WorkflowDatacenter datacenter = Parameters.getDatacenter();
//        List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
//        List<List<Task>> workflowList = Parameters.getWorkflowList();

        List<CondorVM> vmList = Parameters.getVmList();
//        Map<Integer, List<CondorVM>> dcToVMs = Parameters.getDcToVMs();
        int vmNum = 0;

//        for (int i = 0; i < workflowList.size(); i++) {
//            Map<Integer, List<Event>> schedules = initializeSchedules();
//            Parameters.getSchedulesList().add(schedules);
//        }

        // Parameters.setSchedulesList(schedules);

        //初始化一些变量,
        RankMethod rankMethod = null;
        Double deadline = Parameters.getDeadline();
        Map<Task, Double> estMap = new HashMap<>();
        Map<Task, Double> eftMap = new HashMap<>();

        List<Task> taskList = Parameters.getTaskList();
        setTaskList(taskList);
        setReliability(Parameters.getReliabilityLevel().value);


        // 计算(1)任务平均执行时间、(2)数据传输时间
        rankMethod = new UpwardRankMethod(this);
        computationTimes = new HashMap<>();
        generatedDataTransmissionTimes = new HashMap<>();
        lastEFTForWST = 0.0;

        // 计算每个任务在所有虚拟机上的计算成本
        rankMethod.calculateComputationTime();
        rankMethod.calculateGeneratedDataTransmissionTime();

//        calculateNormalEFT();

        Parameters.setComputationTimes(computationTimes);
        Parameters.setGeneratedDataTransmissionTimesList(generatedDataTransmissionTimesList);


        // 算法第一步：任务排序
        ranks = rankMethod.calculateRanks();



        // 算法第二步：计算最短路径
        RHEFT rheft = new RHEFT(this);
        double SL = rheft.calculateSL();


        // 算法第二步：初始化工作流应用的deadline
        // 原始的通过设置不同等级的截止期来设置任务截止时间
        int sand = Parameters.getDeadlineLevel().value;

        //用CyberShake截止期要设置的宽松一些
        double ratio = (double) sand * 0.2;

        //用Montage截止期可以设置稍微紧一些
//				 double ratio = (double) sand / 10;
        double wst = ratio * SL;
        deadline = SL + wst;

        Parameters.setDeadline(deadline);

        // 第三步：进行子截止时间划分
        Map<Task, Double> eftsForSubdeadline = rheft.getEftsForSubdeadline();
        Map<Task, Double> subdeadlines = calculateQFECSubdeadlines(ranks, eftsForSubdeadline, SL );


        // 第四步：分配资源
        double totalEnergy = 0.0;
        SerachPMUtils serachPMUtils = new SerachPMUtils(this);
        totalEnergy = serachPMUtils.searchPM(Parameters.AllocatingMethod.VS1, subdeadlines);

        System.out.println("总能耗为：" + totalEnergy);

        Parameters.setTotalEnergy(totalEnergy);


    }




    /**
     * 设置每个任务的子截止时间，这时候需要假设将任务分配到最快的物理机上去
     */
    private Map<Task, Double> calculateQFECSubdeadlines(List<TaskRank> ranks, Map<Task, Double> eftsForSubdeadline, double SL) {
        Map<Task, Double> subdeadlines = new HashMap<>();
        // 计算每个任务的最早开始时间、最早完成时间
        double deadline = Parameters.getDeadline();
        double DS = deadline - SL;
        double subdealine = 0.0;



        for (TaskRank rank : ranks) {
            Task task = rank.getTask();
            // 将工作流松弛时间跟当前任务执行时间成比例的方式分配给每个任务
            subdealine = eftsForSubdeadline.get(task) + DS;
            subdeadlines.put(task, subdealine);
        }

        return subdeadlines;
    }




    public Map<Task, Double> getComputationTimes() {
        return computationTimes;
    }

    public void setComputationTimes(Map<Task, Double> computationTimes) {
        this.computationTimes = computationTimes;
    }

    public Map<Task, Map<Task, Double>>  getGeneratedDataTransmissionTimes() {
        return generatedDataTransmissionTimes;
    }

    public void setGeneratedDataTransmissionTimes(Map<Task, Map<Task, Double>> generatedDataTransmissionTimes) {
        this.generatedDataTransmissionTimes = generatedDataTransmissionTimes;
    }

    public Map<Task, Double> getOriginalDataTransmissionTimes() {
        return originalDataTransmissionTimes;
    }

    public void setOriginalDataTransmissionTimes(Map<Task, Double> originalDataTransmissionTimes) {
        this.originalDataTransmissionTimes = originalDataTransmissionTimes;
    }

    public Map<Task, Double> getRank() {
        return rank;
    }

    public void setRank(Map<Task, Double> rank) {
        this.rank = rank;
    }

    // public Map<Task, Map<CondorVM, Double>> getComputationCosts() {
    // return computationCosts;
    // }
    //
    // public void setComputationCosts(Map<Task, Map<CondorVM, Double>>
    // computationCosts) {
    // this.computationCosts = computationCosts;
    // }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public List<Map<Task, Double>> getComputationTimesList() {
        return computationTimesList;
    }

    public void setComputationTimesList(List<Map<Task, Double>> computationTimesList) {
        this.computationTimesList = computationTimesList;
    }

    public List<Map<Task, Double>> getOriginalDataTransmissionTimesList() {
        return originalDataTransmissionTimesList;
    }

    public void setOriginalDataTransmissionTimesList(List<Map<Task, Double>> originalDataTransmissionTimesList) {
        this.originalDataTransmissionTimesList = originalDataTransmissionTimesList;
    }

    public List<Map<Task, Map<Task, Double>>> getGeneratedDataTransmissionTimesList() {
        return generatedDataTransmissionTimesList;
    }

    public void setGeneratedDataTransmissionTimesList(
            List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList) {
        this.generatedDataTransmissionTimesList = generatedDataTransmissionTimesList;
    }

    public List<TaskRank> getRanks() {
        return ranks;
    }

    public void setRanks(List<TaskRank> ranks) {
        this.ranks = ranks;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }
}


