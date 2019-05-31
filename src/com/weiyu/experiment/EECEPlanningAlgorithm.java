/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weiyu.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.planning.BasePlanningAlgorithm;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.VNDMethod;

import com.weiyu.experiment.domain.Event;
import com.weiyu.experiment.domain.SortedVM;
import com.weiyu.experiment.domain.TaskAndIndex;
import com.weiyu.experiment.domain.TaskRank;
import com.weiyu.experiment.domain.UpwardTaskRank;
import com.weiyu.experiment.domain.WorkflowAttribute;
import com.weiyu.experiment.taskassigning.TaskAssigningUtils;
import com.weiyu.experiment.tasksequence.DownwardRankMethod;
import com.weiyu.experiment.tasksequence.RankMethod;
import com.weiyu.experiment.tasksequence.UpwardRankMethod;
import com.weiyu.experiment.utils.CloneUtils;

/**
 * 多数据中心场景下基于能量感知的工作流调度算法，考虑两种数据传输时间（前驱数据传输时间、本地数据传输时间）。最小化服务提供商的电费成本
 *
 * @author Wei Yu
 *
 */
public class EECEPlanningAlgorithm extends BasePlanningAlgorithm {
	@Override
	public void run() throws Exception {

	}

//	/**
//	 * 工作流应用的ID；
//	 */
//	private int workflowId;
//	/**
//	 * 每个任务的计算时间
//	 */
//	// private Map<Task, Map<CondorVM, Double>> computationCosts = null;
//
//	/**
//	 * 每个任务的平均计算时间
//	 */
//	private Map<Task, Double> computationTimes = null;
//
//	/**
//	 * 每个任务的数据传输时间
//	 */
//	private Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = null;
//
//	/**
//	 * 每个任务的数据传输时间
//	 */
//	private Map<Task, Double> originalDataTransmissionTimes = null;
//	/**
//	 * 任务的upward rank
//	 */
//	private Map<Task, Double> rank = null;
//
//	/**
//	 * 每个VM的调度列表
//	 */
//	private Map<CondorVM, List<Event>> schedules = null;
//
//	/**
//	 * 工作流应用划分时对应的最早开始时间
//	 */
//	private Map<Task, Double> estsForWST = null;
//	/**
//	 * 工作流应用划分时对应的最早完成时间列表
//	 */
//	private Map<Task, Double> eftsForWST = null;
//	/**
//	 * 子截止时间划分时对应的最早开始时间
//	 */
//	private Map<Task, Double> estsForSubdeadline = null;
//	/**
//	 * 子截止时间划分时对应的最早完成时间列表
//	 */
//	private Map<Task, Double> eftsForSubdeadline = null;
//
//	/**
//	 * 每个任务的子截止时间列表
//	 */
//	private Map<Task, Double> subdeadlines = null;
//
//	private List<UpwardTaskRank> upwardRankList = null;
//
//	/**
//	 * 计算工作流应用排序时的最早完成时间，这个最早完成时间中，通过任务的平均时间来预估任务的计算时间
//	 */
//	private double lastEFTForWST;
//
//	/**
//	 * 为每个任务划分子截止时间，以最快的计算时间来作为任务的平均预估时间
//	 */
//	private double lastEFTForSubdeadline;
//
//	List<Map<Task, Double>> computationTimesList = null;
//	List<Map<Task, Double>> originalDataTransmissionTimesList = null;
//	List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList = null;
//
//	/*
//	 * private class Event { public double start; public double finish;
//	 *
//	 * public Event(double start, double finish) { this.start = start;
//	 * this.finish = finish; } }
//	 */
//
//	/*
//	 * private class TaskRank implements Comparable<TaskRank> { public Task
//	 * task; public Double rank;
//	 *
//	 * public TaskRank(Task task, Double rank) { this.task = task; this.rank =
//	 * rank; }
//	 *
//	 * @Override public int compareTo(TaskRank o) { return
//	 * o.rank.compareTo(rank); } }
//	 */
//
//	public EECEPlanningAlgorithm() {
//		rank = new HashMap<>();
//		schedules = new HashMap<>();
//		// System.out.println("=====打印打印=======");
//		upwardRankList = new ArrayList<>();
//		computationTimesList = new ArrayList<>();
//		originalDataTransmissionTimesList = new ArrayList<>();
//		generatedDataTransmissionTimesList = new ArrayList<>();
//		// computationCosts = new HashMap<>();
//		// computationTimes = new HashMap<>();
//		// generatedDataTransmissionTimes = new HashMap<>();
//		// originalDataTransmissionTimes = new HashMap<>();
//		// estsForWST = new HashMap<>();
//		// eftsForWST = new HashMap<>();
//		// estsForSubdeadline = new HashMap<>();
//		// eftsForSubdeadline = new HashMap<>();
//		// subdeadlines = new HashMap<>();
//		// lastEFTForWST = 0.0;
//		// lastEFTForSubdeadline = 0.0;
//	}
//
//	/**
//	 * The main function
//	 */
//	@Override
//	public void run() {
//		List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
//		List<List<Task>> workflowList = Parameters.getWorkflowList();
//		List<CondorVM> vmList = Parameters.getVmList();
//		Map<Integer, List<CondorVM>> dcToVMs = Parameters.getDcToVMs();
//		int vmNum = 0;
//
//		for (int i = 0; i < workflowList.size(); i++) {
//			Map<Integer, List<Event>> schedules = initializeSchedules();
//			Parameters.getSchedulesList().add(schedules);
//		}
//
//		// Parameters.setSchedulesList(schedules);
//
//		// System.out.println("创建的虚拟机总个数：" + vmList.size() + "; 成功创建的虚拟机的个数：" +
//		// vmNum);
//
//		// Log.printLine("EECE planner 开始对 " + workflowList.size() + "
//		// 工作流应用进行调度工作");
//		// Log.printLine("EECE planner running with " + getTaskList().size() + "
//		// tasks.");
//
//		// 第一步：计算每个工作流应用的deadline
//
//		List<Double> deadlineList = null;
//		deadlineList = Parameters.getDeadlineList();
//		// 判断如果deadlineList为空，说明Parameters类中还没有保存deadline值，这时需要进行计算
//		if (null == deadlineList || deadlineList.isEmpty()) {
//			deadlineList = new ArrayList<>();
//			List<Double> wstList = new ArrayList<>();
//			// List<List<Task>> workflowList = getWorkflowList();
//			// List<Map<Task, Double>> computationTimesList = new ArrayList<>();
//			// List<Map<Task, Map<CondorVM, Double>>> computationCostsList = new
//			// ArrayList<>();
//			// List<Map<Task, Double>> originalDataTransmissionTimesList = new
//			// ArrayList<>();
//			// List<Map<Task, Map<Task, Double>>>
//			// generatedDataTransmissionTimesList = new ArrayList<>();
//
//			List<Map<Task, Double>> estsForWSTList = new ArrayList<>();
//			List<Map<Task, Double>> eftsForWSTList = new ArrayList<>();
//			List<Double> lastEFTForWSTList = new ArrayList<>();
//
//			for (int i = 0; i < workflowList.size(); i++) {
//				List<Task> taskList = workflowList.get(i);
//				setTaskList(taskList);
//				// computationCosts = new HashMap<>();
//				computationTimes = new HashMap<>();
//				originalDataTransmissionTimes = new HashMap<>();
//				generatedDataTransmissionTimes = new HashMap<>();
//				estsForWST = new HashMap<>();
//				eftsForWST = new HashMap<>();
//				lastEFTForWST = 0.0;
//
//				RankMethod rankMethod = new UpwardRankMethod(this);
//
//				// 计算每个任务在所有虚拟机上的计算成本
//				rankMethod.calculateComputationTime();
//				rankMethod.calculateGeneratedDataTransmissionTime();
//				rankMethod.calculateOriginalDataTransmissionTime();
//
//				// 将每个工作流应用的数据保存在Parameters类中
//				computationTimesList.add(computationTimes);
//				// computationCostsList.add(computationCosts);
//				generatedDataTransmissionTimesList.add(generatedDataTransmissionTimes);
//				originalDataTransmissionTimesList.add(originalDataTransmissionTimes);
//
//				calculateNormalEFT();
//				estsForWSTList.add(estsForWST);
//				eftsForWSTList.add(eftsForWST);
//				lastEFTForWSTList.add(lastEFTForWST);
//
//				// System.out.println("工作流应用#" + (i + 1) + "的WST为：" +
//				// lastEFTForWST);
//
////				double ratio = (double) Parameters.randomWsts.get(i) / 10;
////				double wst = ratio * lastEFTForWST;
////				double deadline = lastEFTForWST + wst;
//				// 原始的通过设置不同等级的截止期来设置任务截止时间
//				 int sand = Parameters.getDeadlineLevel().value;
//
//				 //用CyberShake截止期要设置的宽松一些
//				 double ratio = (double) sand / 2;
//
//				 //用Montage截止期可以设置稍微紧一些
////				 double ratio = (double) sand / 10;
//				 double wst = ratio * lastEFTForWST;
//				 double deadline = lastEFTForWST + wst;
//
//				deadlineList.add(deadline);
//				wstList.add(wst);
//			}
//
//
//			Parameters.setDeadlineList(deadlineList);
//			Parameters.setWstList(wstList);
//
//
//			Parameters.setComputationTimesList(computationTimesList);
//			// Parameters.setComputationCostsList(computationCostsList);
//			Parameters.setGeneratedDataTransmissionTimesList(generatedDataTransmissionTimesList);
//			Parameters.setOriginalDataTransmissionTimesList(originalDataTransmissionTimesList);
//			Parameters.setEstsForWSTList(estsForWSTList);
//			Parameters.setEftsForWSTList(eftsForWSTList);
//			Parameters.setLastEFTForWSTList(lastEFTForWSTList);
//		} else {
//			computationTimesList = Parameters.getComputationTimesList();
//			generatedDataTransmissionTimesList = Parameters.getGeneratedDataTransmissionTimesList();
//			originalDataTransmissionTimesList = Parameters.getOriginalDataTransmissionTimesList();
//		}
//
//		// 第二步：对工作流应用进行排序 1）按照应用截止时间排序 2）按照应用松弛时间排序；3）按照应用大小排序
//		List<WorkflowAttribute> returnedWorkflows = null;
//		if (Parameters.WorkflowSequeningMethod.EDF == Parameters.getWorkflowSequeningMethod()) {
//			returnedWorkflows = sortWorkflowsByDeadline(workflowList);
//		} else if (Parameters.WorkflowSequeningMethod.SSF == Parameters.getWorkflowSequeningMethod()) {
//			returnedWorkflows = sortWorkflowsByWst(workflowList);
//		} else if (Parameters.WorkflowSequeningMethod.SWF == Parameters.getWorkflowSequeningMethod()) {
//			returnedWorkflows = sortWorkflowsBySize(workflowList);
//		}
//
//		// 再次重新初始化eftsForWST
//		eftsForWST = new HashMap<>();
//		for (Task task : getTaskList()) {
//			eftsForWST.put(task, 0.0);
//		}
//
//		double elecCostForAllWorkflowsAfterVND = 0.0;
//		double elecCostForAllWorkflowsBeforeVND = 0.0;
//		// 分别对每个任务进行调度
//		for (WorkflowAttribute wst : returnedWorkflows) {
//			schedules = new HashMap<>();
//
//			int index = wst.getIndex();
//			setWorkflowId(index);
//			List<Task> taskList = Parameters.getWorkflowList().get(index);
//			setTaskList(taskList);
//
//			// 第三步：进行任务排序：先根据工作流WST的大小对工作流应用进行排序，然后进行任务排序。
//			List<TaskRank> ranks = null;
//			RankMethod rankMethod = null;
//			if (Parameters.RankMethod.UPWARDRANK == Parameters.getRankMethod()) {
//				// upward rank的计算方式
//				rankMethod = new UpwardRankMethod(this);
//				ranks = rankMethod.calculateRanks();
//			} else if (Parameters.RankMethod.DOWNWARDRANK == Parameters.getRankMethod()) {
//				// downward rank的计算方式
//				// List<Map<Task, Double>> computationTimesList2 =
//				// getComputationTimesList();
//				rankMethod = new DownwardRankMethod(this);
//				ranks = rankMethod.calculateRanks();
//			} else if (Parameters.RankMethod.MERGEDRANK == Parameters.getRankMethod()) {
//				// merged rank的计算方式
//				ranks = new ArrayList<>();
//				RankMethod upwardRankMethod = new UpwardRankMethod(this);
//				List<TaskRank> upwardRanks = upwardRankMethod.calculateRanks();
//				Map<Task, Double> upwardRankValues = upwardRankMethod.getRank();
//
//				RankMethod downwardRankMethod = new DownwardRankMethod(this);
//				List<TaskRank> downwardRanks = downwardRankMethod.calculateRanks();
//				Map<Task, Double> downwardRankValues = downwardRankMethod.getRank();
//
//				double lastRankValue = downwardRanks.get(downwardRanks.size() - 1).getRank();
//				for (Task task : taskList) {
//					double rankValue = upwardRankValues.get(task) + lastRankValue - downwardRankValues.get(task);
//					// 降序排列
//					ranks.add(new TaskRank(task, rankValue, 1));
//				}
//				Collections.sort(ranks);
//			}
//
//			// for (TaskRank rank : ranks) {
//			// System.out.println("任务#" + rank.getTask().getCloudletId() +
//			// "---------任务排序：" + rank.getRank());
//			// }
//			// System.out.println("WST值：" + wst.getValue() + " 顺序：" +
//			// wst.getIndex());
//
//			// 第四步：进行子截止时间划分
//			Map<Task, Double> subdeadlines = calculateSubdeadlines(index, ranks);
//			Parameters.getSubdeadlineList().put(index, subdeadlines);
//
//			// System.out.println("=================任务子截止时间================");
//			// for (Task task : getTaskList()) {
//			// System.out.println("任务#" + task.getCloudletId() + "; 子截止时间： " +
//			// subdeadlines.get(task));
//			// }
//			// System.out.println("=================任务子截止时间结束================");
//
//			// 第五步：根据生成的初始任务调度序列，对任务进行排序
//			double totalElectricityCost = 0.0;
//			double electricityCost = 0.0;
//			int timePoint = Parameters.timePoints.get(index);
//			for (TaskRank rank : ranks) {
//				// 第一种：随机选择一个数据中心
//				Task task = rank.getTask();
//				electricityCost = searchVM(task, dcList, timePoint, index, true);
//				totalElectricityCost += electricityCost;
//			}
//			// System.out.println("VND之前的价格：" + totalElectricityCost);
//			//elecCostForAllWorkflowsBeforeVND += totalElectricityCost;
//			// System.out.print("VND电费价格为：" + totalElectricityCost + " - ");
//
//
//			//如果用了VND和基准的变邻域搜索算法，则进行任务调度序列调整。
//			if (Parameters.VNDMethod.VND == Parameters.vndMethod || Parameters.VNDMethod.LS == Parameters.vndMethod) {
//				// 做参数校正实验的时候不跑VND
//				// 基于VND进行任务序列调整以及数据中心搜索
//				totalElectricityCost = taskSequenceUpdating(totalElectricityCost, ranks, dcList, timePoint, index, Parameters.vndMethod);
//				// System.out.println("VND之后的价格：" + totalElectricityCost);
//				Parameters.getTotalElectricityCostList().put(index, totalElectricityCost);
//			}
//			elecCostForAllWorkflowsAfterVND += totalElectricityCost;
//
//			// 跑完当前工作流应用之后要将当前应用中task占用的VM更新到下一个工作流应用中
//			if (index + 1 < workflowList.size()) {
//				Map<Integer, List<Event>> schedulesList = Parameters.getSchedulesList().get(index);
//				Map<Integer, List<Event>> currentSchedulesList = CloneUtils.clone(schedulesList);
//				// Map<Integer, List<Event>> currentSchedulesList = new
//				// HashMap<>();
//				// currentSchedulesList.putAll(schedulesList);
//				Parameters.getSchedulesList().set(index + 1, currentSchedulesList);
//			}
//
//			// System.out.println("VND之后电费价格为：" + totalElectricityCost);
//		}
//
//		System.out.println("VND的总电费价格为：" + elecCostForAllWorkflowsAfterVND);
//		// System.out.println("VND之前总电费价格为：" + elecCostForAllWorkflowsBeforeVND
//		// + " VND之后总电费价格为：" + elecCostForAllWorkflowsAfterVND);
////		Parameters.elecCostForAllWorkflowsBeforeVND = elecCostForAllWorkflowsBeforeVND;
//		Parameters.elecCostForAllWorkflowsAfterVND = elecCostForAllWorkflowsAfterVND;
//
//	}
//
//	/**
//	 * 初始化一个调度列表
//	 *
//	 * @return
//	 */
//	private Map<Integer, List<Event>> initializeSchedules() {
//		List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
//		Map<Integer, List<Event>> schedules = new HashMap<>();
//		for (Datacenter dc : dcList) {
//			List<CondorVM> vmObjects = Parameters.getDcToVMs().get(dc.getId());
//			for (CondorVM vm : vmObjects) {
//				schedules.put(vm.getId(), new ArrayList<>());
//			}
//		}
//		return schedules;
//	}
//
//	/**
//	 * 基于VND进行变邻域搜索
//	 * @param electricityCost
//	 * @param ranks
//	 * @param dcList
//	 * @param timePoint
//	 * @param index
//	 * @param vndMethod
//	 * @return
//	 */
//	private double taskSequenceUpdating(double electricityCost, List<TaskRank> ranks, List<WorkflowDatacenter> dcList,
//			int timePoint, int index, VNDMethod vndMethod) {
//		// 初始化eftsForWST以及schedules
//		eftsForWST = new HashMap<>();
//		for (Task task : getTaskList()) {
//			eftsForWST.put(task, 0.0);
//		}
//		Map<Integer, List<Event>> currentSchedulesList = null;
//		if (0 == index) {
//			// 如果是第一个工作流应用，直接给该应用进行初始化
//			currentSchedulesList = initializeSchedules();
//		} else {
//			// HashMap需要通过深克隆的方式进行复制，否则修改复制后的对象也会连带着修改之前的对象
//			Map<Integer, List<Event>> schedulesList = Parameters.getSchedulesList().get(index - 1);
//			// currentSchedulesList = new HashMap<>();
//			// currentSchedulesList.putAll(schedulesList);
//			currentSchedulesList = CloneUtils.clone(schedulesList);
//		}
//		Parameters.getSchedulesList().set(index, currentSchedulesList);
//		// List<Event> schedule = Parameters.getSchedulesList().get(index);
//		// schedule.clear();
//
//		// Map<Task, Integer> taskMap = new HashMap<>();
//		// // List<TaskAndIndex> tasks = new ArrayList<>();
//		// for (int i = 0; i < ranks.size(); i++) {
//		// TaskRank rank = ranks.get(i);
//		// taskMap.put(rank.getTask(), i);
//		// // tasks.add(new TaskAndIndex(rank.getTask(), i));
//		// }
//
//		// 需要调整的任务序列的个数
//		int kmax = (int) (ranks.size() / 2 * Parameters.kmax);
//		int k = 1;
//		// double newTotalElecCost = 0.0;
//		List<TaskRank> newRanks = new ArrayList<>();
//		newRanks.addAll(ranks);
//		while (k <= kmax) {
//			boolean flag = true;
//			while (flag) {
//				// 生成一个新的任务序列
//				int pairSize = kmax - k + 1;
//				if(Parameters.VNDMethod.LS == vndMethod){
//					pairSize = k;
//				}
//
//				Set<Task> randomSelectedTasks = getRandomSelectedTasks(ranks, pairSize);
//				Iterator<Task> iterator = randomSelectedTasks.iterator();
//				while (iterator.hasNext()) {
//					Task randomedTask = iterator.next();
//					List<Task> childList = randomedTask.getChildList();
//					if (childList.size() >= 2) {
//						// 如果他的孩子数大于等于2
//						// int size = childList.size();
//						// Random random = new Random();
//						// int firstRandom = 0;
//						// int secondRandom = 0;
//						// //随机选择两个不相同的孩子节点
//						// if(2 == size){
//						// firstRandom = 0;
//						// secondRandom = childList.size() - 1;
//						// }else{
//						// firstRandom = random.nextInt(size);
//						// secondRandom = random.nextInt(size);
//						// while(firstRandom == secondRandom){
//						// secondRandom = random.nextInt(size);
//						// }
//						// }
//						Task firstTask = childList.get(0);
//						Task secondTask = childList.get(childList.size() - 1);
//						int firstIndex = childList.indexOf(firstTask);
//						int secondIndex = childList.indexOf(secondTask);
//						// int firstIndex = taskMap.get(firstTask);
//						// int secondIndex = taskMap.get(secondTask);
//						TaskRank firstRank = newRanks.get(firstIndex);
//						TaskRank secondRank = newRanks.get(secondIndex);
//						newRanks.set(firstIndex, secondRank);
//						newRanks.set(secondIndex, firstRank);
//					}
//				}
//
//				// 对调整后的任务进行虚拟机资源搜索
//				double newTotalElecCost = 0.0;
//				for (TaskRank rank : newRanks) {
//					double singleElecCost = searchVM(rank.getTask(), dcList, timePoint, index, false);
//					newTotalElecCost += singleElecCost;
//				}
//
//				if (newTotalElecCost < electricityCost) {
//					electricityCost = newTotalElecCost;
//					ranks = newRanks;
//				} else {
//					newRanks = ranks;
//					flag = false;
//				}
//			}
//			k++;
//		}
//
//		// double conservedTotalElecCost = 0;
//		for (TaskRank rank : ranks) {
//			makeVMUnavaiable(rank.getTask(), index);
//			// double singleElecCost = searchVM(rank.getTask(), dcList,
//			// timePoint, index, true);
//			// conservedTotalElecCost += singleElecCost;
//		}
//		// System.out.println("之前的Cost:" + electricityCost + "====之后的Cost：" +
//		// conservedTotalElecCost);
//		return electricityCost;
//	}
//
//	/**
//	 * 找到最佳的task sequence组合之后，要将每个task所在的虚拟机的时间段设置为不可用
//	 *
//	 * @param task
//	 * @param index
//	 */
//	public void makeVMUnavaiable(Task task, int index) {
//		// task, vm, minReadyTime, index, 1, false
//		CondorVM vm = VmList.getById(Parameters.getVmList(), task.getVmId());
//		double minReadyTime = 0.0;
//		// 计算当前任务跟前驱任务的数据传输时间
//		for (Task parent : task.getParentList()) {
//			if (null == parent)
//				continue;
//			double beginningTime = eftsForWST.get(parent);
//			double readyTime = beginningTime;
//			CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
//			if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
//				// 如果当前任务跟父亲任务在同一个数据中心
//				if (vm.getHost().getId() != parentVM.getHost().getId()) {
//					readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
//							Parameters.getBandwidthInDC());
//				}
//			} else {
//				readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
//						Parameters.getBandwidthBetweenDC());
//			}
//
//			if (readyTime > minReadyTime) {
//				minReadyTime = readyTime;
//			}
//			// minReadyTime = Math.max(minReadyTime, readyTime);
//		}
//
//		// 计算任务本地数据传输时间
//		minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);
//		findFinishTime(task, vm, minReadyTime, index, 1, true);
//	}
//
//	/**
//	 * 随机取出给定个数的TASK
//	 *
//	 * @param tasks
//	 * @param pairSize
//	 * @return
//	 */
//	private Set<Task> getRandomSelectedTasks(List<TaskRank> tasks, int pairSize) {
//		Random random = new Random();
//		Set<Task> result = new LinkedHashSet<Task>();
//		while (result.size() < pairSize) {
//			int randomIndex = random.nextInt(tasks.size());
//			// TaskAndIndex rank = new TaskAndIndex(tasks.get(randomIndex),
//			// randomIndex);
//			result.add(tasks.get(randomIndex).getTask());
//		}
//		return result;
//	}
//
//	/**
//	 * 为工作流应用中的每个任务搜索虚拟机
//	 *
//	 * @param task
//	 * @param dcList
//	 * @param timePoint
//	 */
//	private double searchVM(Task task, List<WorkflowDatacenter> dcList, int timePoint, int index, boolean isVND) {
//		// Random random = new Random();
//		CondorVM chosenVM = null;
//		double earliestFinishTime = Double.MAX_VALUE;
//		double bestReadyTime = 0.0;
//		double finishTime = 0.0;
//		double bestFrequency = 0.0;
//		double electricityCost = Double.MAX_VALUE;
//		double[] elecPrice = Parameters.elecPrices[timePoint];
//
//		// 对数据中心中的每个虚拟机进行遍历，找出最合适的虚拟机
//		for (WorkflowDatacenter dc : dcList) {
//
//			List<SortedVM> sortedVMs = Parameters.dcToSortedVMs.get(dc.getId());
//
//			// List<CondorVM> vmList = Parameters.getDcToVMs().get(dc.getId());
//			// List<SortedVM> sortedVMs = new ArrayList<>();
//			// // 第一种：先根据能量优化的方式来排序
//			// for (CondorVM vm : vmList) {
//			// double ppW = vm.getMips() / vm.getPower();
//			// SortedVM sortedVM = new SortedVM(vm, ppW);
//			// sortedVMs.add(sortedVM);
//			// }
//			// Collections.sort(sortedVMs);
//
//			for (SortedVM sortedVM : sortedVMs) {
//				CondorVM vm = sortedVM.getVm();
//				double minReadyTime = 0.0;
//				double bestBeginningTime = 0.0;
//
//				// 计算当前任务跟前驱任务的数据传输时间
//				for (Task parent : task.getParentList()) {
//					if (null == parent)
//						continue;
//					double beginningTime = eftsForWST.get(parent);
//					double readyTime = beginningTime;
//					CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
//					if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
//						// 如果当前任务跟父亲任务在同一个数据中心
//						if (vm.getHost().getId() != parentVM.getHost().getId()) {
//							readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent,
//									task, Parameters.getBandwidthInDC());
//						}
//					} else {
//						readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
//								Parameters.getBandwidthBetweenDC());
//					}
//
//					if (readyTime > minReadyTime) {
//						minReadyTime = readyTime;
//						bestBeginningTime = beginningTime;
//					}
//					// minReadyTime = Math.max(minReadyTime, readyTime);
//				}
//
//				// 计算任务本地数据传输时间
//				minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);
//
//				double tmpMinReadyTime = minReadyTime;
//
//				// 计算任务最晚完成时间
//				finishTime = findFinishTime(task, vm, minReadyTime, index, 1, false);
//
//				Map<Task, Double> subdeadlines = Parameters.getSubdeadlineList().get(index);
//				double subdeadline = subdeadlines.get(task);
//
//				if (finishTime <= subdeadline) {
//					if (Parameters.TSTMethod.NOTST == Parameters.tstMethod) {
//						// // 当前虚拟机需满足任务的子截止时间，计算电费价格（without DVFS）
//						double power = vm.getPower() / 1000;
//						double durationTime = finishTime - bestBeginningTime;
//						double tmpElectricityCost = power * durationTime / 3600
//								* elecPrice[Parameters.dcToIndex.get(dc.getId())];
//
//						if (tmpElectricityCost < electricityCost) {
//							electricityCost = tmpElectricityCost;
//							bestReadyTime = minReadyTime;
//							earliestFinishTime = finishTime;
//							chosenVM = vm;
//						}
//					} else if (Parameters.TSTMethod.TST == Parameters.tstMethod) {
//						// 当前虚拟机需满足任务的子截止时间，计算电费价格（通过 DVFS技术调整）
//						double power = vm.getPower() / 1000;
//						// 通过DVFS调频技术降低VM的功率以及处理速度
//						double tst = subdeadline - finishTime;
//						double frequency = (subdeadline - bestBeginningTime - Parameters.beta * tst)
//								/ (subdeadline - bestBeginningTime);
//						// Map<Task, Map<CondorVM, Double>> computationCosts =
//						// Parameters.getComputationCostsList().get(index);
//						// double computationCost =
//						// computationCosts.get(task).get(vm);
//						double computationCost = TaskAssigningUtils.calculateComputationTime(task, vm, frequency);
//						double improvedComputationCost = computationCost / frequency;
//						// computationCosts.get(task).put(vm,
//						// improvedComputationCost);
//						double tmpFinishTime = tmpMinReadyTime + improvedComputationCost;
//						double durationTime = tmpFinishTime - bestBeginningTime;
//
//						double tmpElectricityCost = power * frequency * durationTime / 3600
//								* elecPrice[Parameters.dcToIndex.get(dc.getId())];
//
//						// System.out.println("调频前的电费：" + originalTmpElecCost +
//						// ":
//						// 调频后的电费：" + tmpElectricityCost);
//						if (tmpElectricityCost < electricityCost) {
//							electricityCost = tmpElectricityCost;
//							bestReadyTime = tmpMinReadyTime;
//							earliestFinishTime = tmpFinishTime;
//							bestFrequency = frequency;
//							chosenVM = vm;
//						}
//					}
//
//				}
//			}
//			// System.out.println("数据中心#" + dc.getId() + "上的电费价格为：" +
//			// electricityCost);
//		}
//		// WorkflowDatacenter dc = dcList.get(random.nextInt(dcList.size()));
//		// System.out.println("到了第二个findFinishTime");
//
//		// 针对任务分配失败的情况，随机的分配一个VM
//		if (null == chosenVM) {
//			System.out.println("虚拟机没有分配成功的情况");
//			// List<CondorVM> vmList = Parameters.getVmList();
//			Random random = new Random();
//			int dcIndex = random.nextInt(dcList.size());
//			WorkflowDatacenter dc = dcList.get(dcIndex);
//			List<CondorVM> vmListForGivenDC = Parameters.getDcToVMs().get(dc.getId());
//
//			int vmIndex = random.nextInt(vmListForGivenDC.size());
//			// SortedVM sortedVM =
//			// Parameters.dcToSortedVMs.get(dc.getId()).get(0);
//			// CondorVM vm = sortedVM.getVm();
//			CondorVM vm = vmListForGivenDC.get(vmIndex);
//
//			double minReadyTime = 0.0;
//			double bestBeginningTime = 0.0;
//
//			// 计算当前任务跟前驱任务的数据传输时间
//			for (Task parent : task.getParentList()) {
//				if (null == parent)
//					continue;
//				double beginningTime = eftsForWST.get(parent);
//				double readyTime = beginningTime;
//				CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
//				if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
//					// 如果当前任务跟父亲任务在同一个数据中心
//					if (vm.getHost().getId() != parentVM.getHost().getId()) {
//						readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
//								Parameters.getBandwidthInDC());
//					}
//				} else {
//					readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
//							Parameters.getBandwidthBetweenDC());
//				}
//				if (readyTime > minReadyTime) {
//					minReadyTime = readyTime;
//					bestBeginningTime = beginningTime;
//				}
//				// minReadyTime = Math.max(minReadyTime, readyTime);
//			}
//
//			// 计算任务本地数据传输时间
//			minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);
//
//			// 计算任务最晚完成时间
//			finishTime = minReadyTime + task.getCloudletLength() / vm.getMips();
//			// finishTime = findFinishTime(task, vm, minReadyTime, index, 1,
//			// false);
//
//			double power = vm.getPower() / 1000;
//			double durationTime = finishTime - bestBeginningTime;
//			double tmpElectricityCost = power * durationTime / 3600
//					* elecPrice[Parameters.dcToIndex.get(vm.getHost().getDatacenter().getId())];
//
//			// System.out.println("调频前的电费：" + originalTmpElecCost + ": 调频后的电费："
//			// + tmpElectricityCost);
//			electricityCost = tmpElectricityCost;
//			bestReadyTime = minReadyTime;
//			chosenVM = vm;
//			earliestFinishTime = finishTime;
//		}
//
//		if (isVND) {
//			findFinishTime(task, chosenVM, bestReadyTime, index, bestFrequency, true);
//		}
//		eftsForWST.put(task, earliestFinishTime);
//
//		task.setVmId(chosenVM.getId());
//		return electricityCost;
//	}
//
//	private int getDCIndex(WorkflowDatacenter dc) {
//		List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
//		for (int i = 0; i < dcList.size(); i++) {
//			if (dc.getId() == dcList.get(i).getId()) {
//				return i;
//			}
//		}
//		return 0;
//	}
//
//	/**
//	 * 工作工作流应用的大小来排序
//	 *
//	 * @param workflowList
//	 * @return
//	 */
//	private List<WorkflowAttribute> sortWorkflowsBySize(List<List<Task>> workflowList) {
//		List<Double> workflowSizeList = Parameters.getWorkflowSizes();
//		List<WorkflowAttribute> workflowSizes = new ArrayList<>();
//		for (int i = 0; i < workflowSizeList.size(); i++) {
//			double size = workflowSizeList.get(i);
//			WorkflowAttribute workflowSize = new WorkflowAttribute(i, size);
//			workflowSizes.add(workflowSize);
//		}
//		Collections.sort(workflowSizes);
//		return workflowSizes;
//	}
//
//	/**
//	 * 根据工作流应用的松弛时间来排序
//	 *
//	 * @param workflowList
//	 * @return
//	 */
//	private List<WorkflowAttribute> sortWorkflowsByWst(List<List<Task>> workflowList) {
//		List<Double> wstList = Parameters.getWstList();
//		List<WorkflowAttribute> wsts = new ArrayList<>();
//		for (int i = 0; i < wstList.size(); i++) {
//			double wstItem = wstList.get(i);
//			WorkflowAttribute wst = new WorkflowAttribute(i, wstItem);
//			wsts.add(wst);
//		}
//		Collections.sort(wsts);
//		return wsts;
//	}
//
//	/**
//	 * 根据应用截止时间进行排序
//	 *
//	 * @param workflowList
//	 * @return
//	 */
//	private List<WorkflowAttribute> sortWorkflowsByDeadline(List<List<Task>> workflowList) {
//		List<Double> deadlineList = Parameters.getDeadlineList();
//		List<WorkflowAttribute> deadlines = new ArrayList<>();
//		for (int i = 0; i < deadlineList.size(); i++) {
//			double deadlineItem = deadlineList.get(i);
//			WorkflowAttribute deadline = new WorkflowAttribute(i, deadlineItem);
//			deadlines.add(deadline);
//		}
//		Collections.sort(deadlines);
//		return deadlines;
//	}
//
//	/**
//	 * 设置每个任务的子截止时间，这时候需要假设将任务分配到最快的虚拟机上去了
//	 */
//	private Map<Task, Double> calculateSubdeadlines(int index, List<TaskRank> ranks) {
//		Map<Task, Double> subdeadlines = new HashMap<>();
//		// 计算每个任务的最早开始时间、最早完成时间
//		calculateEFTForSubdeadline(index);
//		double deadline = Parameters.getDeadlineList().get(index);
//		// System.out.println("最早完成时间for subdealine：" +
//		// Parameters.getLastEFTForSubdeadlineList().get(index)
//		// + " --Dealine for Subdealine为：" + deadline);
//		// 先计算工作流松弛时间
//		double lastEFTForSubdeadline = Parameters.getLastEFTForSubdeadlineList().get(index);
//		double wst = deadline - lastEFTForSubdeadline;
//		double subdealine = 0.0;
//		double totalcomputationTime = 0.0;
//		Map<Task, Double> computationTimes = computationTimesList.get(index);
//		// Map<Task, Double> computationTimes =
//		// Parameters.getComputationTimesList().get(index);
//		Map<Task, Double> eftsForSubdeadline = Parameters.getEftsForSubdeadlineList().get(index);
//
//		for (Task task : getTaskList()) {
//			totalcomputationTime += computationTimes.get(task);
//		}
//
//		int maximalDepth = Parameters.getMaximalDepthList().get(index);
//		double computationTime = 0.0;
//		for (TaskRank rank : ranks) {
//			Task task = rank.getTask();
//			computationTime += computationTimes.get(task);
//			// 将工作流松弛时间跟当前任务执行时间成比例的方式分配给每个任务
//			subdealine = eftsForSubdeadline.get(task) + wst * task.getDepth() / maximalDepth;
//			// subdealine = eftsForSubdeadline.get(task) + wst * computationTime
//			// / totalcomputationTime;
//			subdeadlines.put(task, subdealine);
//		}
//
//		// Parameters.setSubdeadlineList(subdeadlines);
//		return subdeadlines;
//	}
//
//	/**
//	 * 工作流中每个任务的最早开始时间、最早完成时间
//	 */
//	public Map<Task, Double> calculateNormalEFT() {
//		// Map<Task, Double> eftsForWST = new HashMap<>();
//		// 先给每个任务的最早完成时间初始化为0
//		for (Task task : getTaskList()) {
//			eftsForWST.put(task, 0.0);
//		}
//
//		for (Task task : getTaskList()) {
//			estsForWST.put(task, 0.0);
//		}
//
//		for (Task task : getTaskList()) {
//			double averageComputationCost = 0.0;
//			averageComputationCost = computationTimes.get(task);
//
//			double earliestStartTime = 0.0;
//			double max = 0.0;
//			for (Task parent : task.getParentList()) {
//				// 计算任务的最早开始时间
//				earliestStartTime = eftsForWST.get(parent) + generatedDataTransmissionTimes.get(parent).get(task)
//						+ computationTimes.get(parent);
//				max = Math.max(max, earliestStartTime);
//			}
//
//			estsForWST.put(task, max + originalDataTransmissionTimes.get(task));
//			double initialEarliestFinishTime = estsForWST.get(task) + averageComputationCost;
//
//			if (initialEarliestFinishTime > lastEFTForWST)
//				lastEFTForWST = initialEarliestFinishTime;
//			eftsForWST.put(task, initialEarliestFinishTime);
//		}
//
//		// System.out.println("===========打印每个任务的最早开始时间、最早完成时间==============");
//		// for (Task task : getTaskList()) {
//		// System.out.println(estsForWST.get(task) + " : " +
//		// eftsForWST.get(task));
//		// }
//		// System.out.println("========================打印结束=========================");
//
//		return eftsForWST;
//	}
//
//	/**
//	 * 计算子截止时间的最早完成时间
//	 *
//	 * @param index
//	 *
//	 * @return
//	 */
//	public Map<Task, Double> calculateEFTForSubdeadline(int index) {
//		Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = generatedDataTransmissionTimesList.get(index);
//		Map<Task, Double> originalDataTransmissionTimes = originalDataTransmissionTimesList.get(index);
//		Map<Task, Double> estsForSubdeadline = new HashMap<>();
//		Map<Task, Double> eftsForSubdeadline = new HashMap<>();
//		double lastEFTForSubdeadline = 0.0;
//		// double est = 0.0;
//		// 先给每个任务的最早完成时间初始化为0
//		for (Task task : getTaskList()) {
//			eftsForSubdeadline.put(task, 0.0);
//		}
//
//		// 将每个任务的最早开始时间初始化为0
//		for (Task task : getTaskList()) {
//			estsForSubdeadline.put(task, 0.0);
//		}
//
//		for (Task task : getTaskList()) {
//			double averageComputationCost = 0.0;
//			double fastestSpeed = Parameters.getFastestVM().getMips();
//			averageComputationCost = task.getCloudletLength() / fastestSpeed;
//
//			double earliestStartTime = 0.0;
//			double max = 0.0;
//			for (Task parent : task.getParentList()) {
//				// 计算任务的最早开始时间
//				earliestStartTime = eftsForSubdeadline.get(parent)
//						+ generatedDataTransmissionTimes.get(parent).get(task)
//						+ parent.getCloudletLength() / fastestSpeed;
//				// earliestStartTime = eftsForSubdeadline.get(parent)
//				// +
//				// Parameters.getGeneratedDataTransmissionTimesList().get(index).get(parent).get(task)
//				// + parent.getCloudletLength() / fastestSpeed;
//				max = Math.max(max, earliestStartTime);
//			}
//			// est = max + originalDataTransmissionTimes.get(task);
//
//			estsForSubdeadline.put(task, max + originalDataTransmissionTimes.get(task));
//			// estsForSubdeadline.put(task, max +
//			// Parameters.getOriginalDataTransmissionTimesList().get(index).get(task));
//			double initialEarliestFinishTime = estsForSubdeadline.get(task) + averageComputationCost;
//
//			if (initialEarliestFinishTime > lastEFTForSubdeadline)
//				lastEFTForSubdeadline = initialEarliestFinishTime;
//			eftsForSubdeadline.put(task, initialEarliestFinishTime);
//		}
//
//		Parameters.getEstsForSubdeadlineList().put(index, estsForSubdeadline);
//		Parameters.getEftsForSubdeadlineList().put(index, eftsForSubdeadline);
//		Parameters.getLastEFTForSubdeadlineList().put(index, lastEFTForSubdeadline);
//
//		// System.out.println("===========打印每个任务的最早开始时间、最早完成时间==============");
//		// for (Task task : getTaskList()) {
//		// System.out.println(estsForSubdeadline.get(task) + " : " +
//		// eftsForSubdeadline.get(task));
//		// }
//		// System.out.println("========================打印结束=========================");
//
//		return eftsForSubdeadline;
//	}
//
//	/**
//	 * Finds the best time slot available to minimize the finish time of the
//	 * given task in the vm with the constraint of not scheduling it before
//	 * readyTime. If occupySlot is true, reserves the time slot in the schedule.
//	 *
//	 * @param task
//	 *            The task to have the time slot reserved
//	 * @param vm
//	 *            The vm that will execute the task
//	 * @param readyTime
//	 *            The first moment that the task is available to be scheduled
//	 * @param frequency
//	 * @param occupySlot
//	 *            If true, reserves the time slot in the schedule.
//	 * @return The minimal finish time of the task in the vmn
//	 */
//	private double findFinishTime(Task task, CondorVM vm, double readyTime, int index, double frequency,
//			boolean occupySlot) {
//		if (null == vm)
//			System.out.println("VM为空");
//		/**
//		 * 用来保存每个VM的task调度列表
//		 */
//		Map<Integer, List<Event>> schedules = Parameters.getSchedulesList().get(index);
//		List<Event> sched = schedules.get(vm.getId());
//		// Map<Task, Map<CondorVM, Double>> computationCosts =
//		// Parameters.getComputationCostsList().get(index);
//		// double computationCost = computationCosts.get(task).get(vm);
//		double computationCost = TaskAssigningUtils.calculateComputationTime(task, vm, frequency);
//		double start, finish;
//		int pos;
//
//		/**
//		 * 如果当前VM的调度列表是空的话
//		 */
//		if (sched.isEmpty()) {
//			if (occupySlot) {
//				sched.add(new Event(readyTime, readyTime + computationCost));
//			}
//			return readyTime + computationCost;
//		}
//
//		/**
//		 * 如果当前VM的调度列表只有一个TASK
//		 */
//		if (sched.size() == 1) {
//			if (readyTime >= sched.get(0).finish) {
//				pos = 1;
//				start = readyTime;
//			} else if (readyTime + computationCost <= sched.get(0).start) {
//				pos = 0;
//				start = readyTime;
//			} else {
//				pos = 1;
//				start = sched.get(0).finish;
//			}
//
//			if (occupySlot) {
//				sched.add(pos, new Event(start, start + computationCost));
//			}
//			return start + computationCost;
//		}
//
//		/**
//		 * 当前VM的调度序列中有多个待调度的TASK
//		 */
//		// Trivial case: Start after the latest task scheduled
//		start = Math.max(readyTime, sched.get(sched.size() - 1).finish);
//		finish = start + computationCost;
//		int i = sched.size() - 1;
//		int j = sched.size() - 2;
//		pos = i + 1;
//		/**
//		 * 搜索空闲时间块
//		 */
//		while (j >= 0) {
//			Event current = sched.get(i);
//			Event previous = sched.get(j);
//
//			if (readyTime > previous.finish) {
//				if (readyTime + computationCost <= current.start) {
//					start = readyTime;
//					finish = readyTime + computationCost;
//				}
//
//				break;
//			}
//			if (previous.finish + computationCost <= current.start) {
//				start = previous.finish;
//				finish = previous.finish + computationCost;
//				pos = i;
//			}
//			i--;
//			j--;
//		}
//
//		if (readyTime + computationCost <= sched.get(0).start) {
//			pos = 0;
//			start = readyTime;
//
//			if (occupySlot) {
//				sched.add(pos, new Event(start, start + computationCost));
//			}
//			return start + computationCost;
//		}
//		if (occupySlot) {
//			sched.add(pos, new Event(start, finish));
//		}
//		return finish;
//	}
//
//	public Map<Task, Double> getComputationTimes() {
//		return computationTimes;
//	}
//
//	public void setComputationTimes(Map<Task, Double> computationTimes) {
//		this.computationTimes = computationTimes;
//	}
//
//	public Map<Task, Map<Task, Double>> getGeneratedDataTransmissionTimes() {
//		return generatedDataTransmissionTimes;
//	}
//
//	public void setGeneratedDataTransmissionTimes(Map<Task, Map<Task, Double>> generatedDataTransmissionTimes) {
//		this.generatedDataTransmissionTimes = generatedDataTransmissionTimes;
//	}
//
//	public Map<Task, Double> getOriginalDataTransmissionTimes() {
//		return originalDataTransmissionTimes;
//	}
//
//	public void setOriginalDataTransmissionTimes(Map<Task, Double> originalDataTransmissionTimes) {
//		this.originalDataTransmissionTimes = originalDataTransmissionTimes;
//	}
//
//	public Map<Task, Double> getRank() {
//		return rank;
//	}
//
//	public void setRank(Map<Task, Double> rank) {
//		this.rank = rank;
//	}
//
//	// public Map<Task, Map<CondorVM, Double>> getComputationCosts() {
//	// return computationCosts;
//	// }
//	//
//	// public void setComputationCosts(Map<Task, Map<CondorVM, Double>>
//	// computationCosts) {
//	// this.computationCosts = computationCosts;
//	// }
//
//	public int getWorkflowId() {
//		return workflowId;
//	}
//
//	public void setWorkflowId(int workflowId) {
//		this.workflowId = workflowId;
//	}
//
//	public List<Map<Task, Double>> getComputationTimesList() {
//		return computationTimesList;
//	}
//
//	public void setComputationTimesList(List<Map<Task, Double>> computationTimesList) {
//		this.computationTimesList = computationTimesList;
//	}
//
//	public List<Map<Task, Double>> getOriginalDataTransmissionTimesList() {
//		return originalDataTransmissionTimesList;
//	}
//
//	public void setOriginalDataTransmissionTimesList(List<Map<Task, Double>> originalDataTransmissionTimesList) {
//		this.originalDataTransmissionTimesList = originalDataTransmissionTimesList;
//	}
//
//	public List<Map<Task, Map<Task, Double>>> getGeneratedDataTransmissionTimesList() {
//		return generatedDataTransmissionTimesList;
//	}
//
//	public void setGeneratedDataTransmissionTimesList(
//			List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList) {
//		this.generatedDataTransmissionTimesList = generatedDataTransmissionTimesList;
//	}

}
