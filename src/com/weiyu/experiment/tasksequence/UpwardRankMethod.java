package com.weiyu.experiment.tasksequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;
import com.weiyu.experiment.EECEPlanningAlgorithm;
import com.weiyu.experiment.domain.TaskRank;

public class UpwardRankMethod extends RankMethod{
	
	public UpwardRankMethod(EECEPlanningAlgorithm planner){
		super(planner);
	}
    
    /**
     * 通过upward rank的方式来给任务排序
     * @return: 返回排序后任务的值
     */
	@Override
    public List<TaskRank> calculateRanks() {
        for (Task task : planner.getTaskList()) {
            calculateRank(task);
        }
        
        List<TaskRank> upwardRankList = new ArrayList<>();
		for (Task task : rank.keySet()) {
			upwardRankList.add(new TaskRank(task, rank.get(task), 1));
		}
        
//        List<TaskRank> upwardRankList = new ArrayList<>();
//        
//        for (Task task : planner.getTaskList()) {
//			TaskRank taskRank = new TaskRank(task, rank.get(task), 1);
//			upwardRankList.add(taskRank);
//		}
		Collections.sort(upwardRankList);
		return upwardRankList;
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
        int workflowId = planner.getWorkflowId();
        Map<Task, Double> computationTimes = planner.getComputationTimesList().get(workflowId);
        Map<Task, Double> originalDataTransmissionTimes = planner.getOriginalDataTransmissionTimesList().get(workflowId);
        Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = planner.getGeneratedDataTransmissionTimesList().get(workflowId);
//        Map<Task, Double> computationTimes = planner.getComputationTimes();
//        Map<Task, Double> originalDataTransmissionTimes = planner.getOriginalDataTransmissionTimes();
//        Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = planner.getGeneratedDataTransmissionTimes();
//        averageComputationCost = Parameters.getComputationTimesList().get(workflowId).get(task);
        
        averageComputationCost = computationTimes.get(task);
        
        double avgOriginalDataTransTime = originalDataTransmissionTimes.get(task);
//        double avgOriginalDataTransTime = Parameters.getOriginalDataTransmissionTimesList().get(workflowId).get(task);

        double max = 0.0;
        for (Task child : task.getChildList()) {
            double childCost = generatedDataTransmissionTimes.get(task).get(child)
                    + calculateRank(child);
//            double childCost = Parameters.getGeneratedDataTransmissionTimesList().get(workflowId).get(task).get(child)
//            		+ calculateRank(child);
            max = Math.max(max, childCost);
        }

        rank.put(task, averageComputationCost + avgOriginalDataTransTime + max);

        return rank.get(task);
    }

}
