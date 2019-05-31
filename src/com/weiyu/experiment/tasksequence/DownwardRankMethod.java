package com.weiyu.experiment.tasksequence;

import java.util.*;

import com.weiyu.experiment.ESRWHAlgorithm;
import com.weiyu.experiment.domain.SortedVM;
import com.weiyu.experiment.taskassigning.TaskAssigningUtils;
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.utils.Parameters;

import com.weiyu.experiment.EECEPlanningAlgorithm;
import com.weiyu.experiment.domain.TaskRank;

public class DownwardRankMethod extends RankMethod{
	
	public DownwardRankMethod(ESRWHAlgorithm planner){
		super(planner);
	}
    
    /**
     * 通过upward rank的方式来给任务排序
     * @return: 返回排序后任务的值
     */
	@Override
    public List<TaskRank> calculateRanks() {
    	List<Task> taskList = planner.getTaskList();
    	for(int i = taskList.size() - 1;i >= 0; i--){
    		Task task = taskList.get(i);
    		calculateRank(task);
    	}
        
        List<TaskRank> downwardRankList = new ArrayList<>();
		for (Task task : rank.keySet()) {
			downwardRankList.add(new TaskRank(task, rank.get(task), 0));
		}
        
//        List<TaskRank> upwardRankList = new ArrayList<>();
//        
//        for (Task task : planner.getTaskList()) {
//			TaskRank taskRank = new TaskRank(task, rank.get(task), 1);
//			upwardRankList.add(taskRank);
//		}
		Collections.sort(downwardRankList);
		return downwardRankList;
    }

    private int getDCIndex(WorkflowDatacenter dc) {
        List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
        for (int i = 0; i < dcList.size(); i++) {
            if (dc.getId() == dcList.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Populates rank.get(task) with the rank of task as defined in the HEFT
     * paper.
     *
     * @param task The task have the rank calculates
     * @return The rank
     */
    private double calculateRank(Task task) {
    	//Map<Task, Double> rank = planner.getRank();
        if (rank.containsKey(task)) {
            return rank.get(task);
        }

        double averageComputationCost = 0.0;
//        double avgOriginalDataTransTime = 0.0;
        double avgGeneratedDataTransmissionTimes = 0.0;
        int workflowId = planner.getWorkflowId();
        Map<Task, Double> computationTimes = planner.getComputationTimes();
//        Map<Task, Double> originalDataTransmissionTimes = planner.getOriginalDataTransmissionTimesList().get(workflowId);
        Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = planner.getGeneratedDataTransmissionTimes();
//        Map<Task, Double> computationTimes = planner.getComputationTimes();
//        Map<Task, Double> originalDataTransmissionTimes = planner.getOriginalDataTransmissionTimes();
//        Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = planner.getGeneratedDataTransmissionTimes();
        double max = 0.0;
        for (Task parent : task.getParentList()) {
        	averageComputationCost = computationTimes.get(parent);
//            avgOriginalDataTransTime = originalDataTransmissionTimes.get(parent);
            avgGeneratedDataTransmissionTimes = generatedDataTransmissionTimes.get(parent).get(task);
//            averageComputationCost = Parameters.getComputationTimesList().get(workflowId).get(parent);
//            avgOriginalDataTransTime = Parameters.getOriginalDataTransmissionTimesList().get(workflowId).get(parent);
//            avgGeneratedDataTransmissionTimes = Parameters.getGeneratedDataTransmissionTimesList().get(workflowId).get(parent).get(task);
            double parentCost = averageComputationCost  + avgGeneratedDataTransmissionTimes
                    + calculateRank(parent);
            max = Math.max(max, parentCost);
        }

        rank.put(task, max);
        return rank.get(task);
    }

}
