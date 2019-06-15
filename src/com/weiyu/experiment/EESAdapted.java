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
public class EESAdapted extends ESRWHAlgorithm {
    /**
     * ������Ӧ�õ�ID��
     */
    private int workflowId;

    /**
     * �ɿ���Լ����
     */
    private double reliability = 0.0;

    /**
     * ÿ�������ƽ������ʱ��
     */
    private Map<Task, Double> computationTimes = null;

    /**
     * ÿ����������ݴ���ʱ��
     */
    private Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = null;

    /**
     * ÿ����������ݴ���ʱ��
     */
    private Map<Task, Double> originalDataTransmissionTimes = null;
    /**
     * �����upward rank
     */
    private Map<Task, Double> rank = null;

    /**
     * �������� ---why
     */
    private List<TaskRank> ranks = null;

    /**
     * ÿ��VM�ĵ����б�
     */
    private Map<CondorVM, List<Event>> schedules = null;

    /**
     * ������Ӧ�û���ʱ��Ӧ�����翪ʼʱ��
     */
    private Map<Task, Double> estsForWST = null;
    /**
     * ������Ӧ�û���ʱ��Ӧ���������ʱ���б�
     */
    private Map<Task, Double> eftsForWST = null;
    /**
     * �ӽ�ֹʱ�仮��ʱ��Ӧ�����翪ʼʱ��
     */
    private Map<Task, Double> estsForSubdeadline = null;
    /**
     * �ӽ�ֹʱ�仮��ʱ��Ӧ���������ʱ���б�
     */
    private Map<Task, Double> eftsForSubdeadline = null;

    /**
     * ÿ��������ӽ�ֹʱ���б�
     */
    private Map<Task, Double> subdeadlines = null;

    private List<UpwardTaskRank> upwardRankList = null;

    /**
     * ���㹤����Ӧ������ʱ���������ʱ�䣬����������ʱ���У�ͨ�������ƽ��ʱ����Ԥ������ļ���ʱ��
     */
    private double lastEFTForWST;

    /**
     * Ϊÿ�����񻮷��ӽ�ֹʱ�䣬�����ļ���ʱ������Ϊ�����ƽ��Ԥ��ʱ��
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

    public EESAdapted() {
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

        //��ʼ��һЩ����,
        RankMethod rankMethod = null;
        Double deadline = Parameters.getDeadline();
        Map<Task, Double> estMap = new HashMap<>();
        Map<Task, Double> eftMap = new HashMap<>();

        List<Task> taskList = Parameters.getTaskList();
        setTaskList(taskList);
        setReliability(Parameters.getReliabilityLevel().value);


        // ����(1)����ƽ��ִ��ʱ�䡢(2)���ݴ���ʱ��
        rankMethod = new UpwardRankMethod(this);
        computationTimes = new HashMap<>();
        generatedDataTransmissionTimes = new HashMap<>();
        lastEFTForWST = 0.0;

        // ����ÿ������������������ϵļ���ɱ�
        rankMethod.calculateComputationTime();
        rankMethod.calculateGeneratedDataTransmissionTime();

//        calculateNormalEFT();

        Parameters.setComputationTimes(computationTimes);
        Parameters.setGeneratedDataTransmissionTimesList(generatedDataTransmissionTimesList);


        // �㷨��һ������������
        if (Parameters.RankMethod.UPWARDRANK == Parameters.getRankMethod()) {
            // upward rank�ļ��㷽ʽ
            rankMethod = new UpwardRankMethod(this);
            ranks = rankMethod.calculateRanks();
        } else if (Parameters.RankMethod.DOWNWARDRANK == Parameters.getRankMethod()) {
            // downward rank�ļ��㷽ʽ
            rankMethod = new DownwardRankMethod(this);
            ranks = rankMethod.calculateRanks();
        } else if (Parameters.RankMethod.HybridRank == Parameters.getRankMethod()) {
            // hybrid rank�ļ��㷽ʽ
            ranks = new ArrayList<>();
            RankMethod upwardRankMethod = new UpwardRankMethod(this);
            List<TaskRank> upwardRanks = upwardRankMethod.calculateRanks();
            Map<Task, Double> upwardRankValues = upwardRankMethod.getRank();

            RankMethod downwardRankMethod = new DownwardRankMethod(this);
            List<TaskRank> downwardRanks = downwardRankMethod.calculateRanks();
            Map<Task, Double> downwardRankValues = downwardRankMethod.getRank();

            double lastRankValue = downwardRanks.get(downwardRanks.size() - 1).getRank();
            for (Task task : taskList) {
                double rankValue = upwardRankValues.get(task) + lastRankValue - downwardRankValues.get(task);
                // ��������
                ranks.add(new TaskRank(task, rankValue, 1));
            }
            Collections.sort(ranks);
        }


        // �㷨�ڶ������������·��
        RHEFT rheft = new RHEFT(this);
        double SL = rheft.calculateSL();


        // �㷨�ڶ�������ʼ��������Ӧ�õ�deadline
        // ԭʼ��ͨ�����ò�ͬ�ȼ��Ľ�ֹ�������������ֹʱ��
        int sand = Parameters.getDeadlineLevel().value;

        //��CyberShake��ֹ��Ҫ���õĿ���һЩ
        double ratio = (double) sand * 0.2;

        //��Montage��ֹ�ڿ���������΢��һЩ
//				 double ratio = (double) sand / 10;
        double wst = ratio * SL;
        deadline = SL + wst;

        Parameters.setDeadline(deadline);

        // �������������ӽ�ֹʱ�仮��
        Map<Task, Double> eftsForSubdeadline = rheft.getEftsForSubdeadline();
        Map<Task, Double> subdeadlines = calculateEESSubdeadlines(ranks, eftsForSubdeadline, SL );


        // ���Ĳ���������Դ
        double totalEnergy = 0.0;
        SerachPMUtils serachPMUtils = new SerachPMUtils(this);
        totalEnergy = serachPMUtils.searchPM(Parameters.AllocatingMethod.VS2, subdeadlines);


        System.out.println("���ܺ�Ϊ��" + totalEnergy);

        // ���岽��DVFS��Ƶ
        totalEnergy = serachPMUtils.afterDVFS(subdeadlines);

        System.out.println("DVFS�����ܺ�Ϊ��" + totalEnergy);

        Parameters.setTotalEnergy(totalEnergy);


    }




    /**
     * ����ÿ��������ӽ�ֹʱ�䣬��ʱ����Ҫ���轫������䵽�����������ȥ
     */
    private Map<Task, Double> calculateSubdeadlines(List<TaskRank> ranks, Map<Task, Double> eftsForSubdeadline, double SL) {
        Map<Task, Double> subdeadlines = new HashMap<>();
        // ����ÿ����������翪ʼʱ�䡢�������ʱ��
        double deadline = Parameters.getDeadline();
        double DS = deadline - SL;
        double subdealine = 0.0;

        int maximalDepth = 1;
        for (TaskRank rank : ranks){
            Task task = rank.getTask();
            if(task.getDepth() >maximalDepth)
                maximalDepth = task.getDepth();
        }

        for (TaskRank rank : ranks) {
            Task task = rank.getTask();
            // ���������ɳ�ʱ�����ǰ����ִ��ʱ��ɱ����ķ�ʽ�����ÿ������
            subdealine = eftsForSubdeadline.get(task) + DS * task.getDepth() / maximalDepth;
            subdeadlines.put(task, subdealine);
        }

        return subdeadlines;
    }

    /**
     * ����ÿ��������ӽ�ֹʱ�䣬��ʱ����Ҫ���轫������䵽�����������ȥ
     */
    private Map<Task, Double> calculateEESSubdeadlines(List<TaskRank> ranks, Map<Task, Double> eftsForSubdeadline, double SL) {
        Map<Task, Double> subdeadlines = new HashMap<>();
        // ����ÿ����������翪ʼʱ�䡢�������ʱ��
        double deadline = Parameters.getDeadline();
        double DS = deadline - SL;
        double subdealine = 0.0;



        for (TaskRank rank : ranks) {
            Task task = rank.getTask();
            // ���������ɳ�ʱ�����ǰ����ִ��ʱ��ɱ����ķ�ʽ�����ÿ������
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

