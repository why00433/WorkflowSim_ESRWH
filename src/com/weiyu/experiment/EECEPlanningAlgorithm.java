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
 * ���������ĳ����»���������֪�Ĺ����������㷨�������������ݴ���ʱ�䣨ǰ�����ݴ���ʱ�䡢�������ݴ���ʱ�䣩����С�������ṩ�̵ĵ�ѳɱ�
 * 
 * @author Wei Yu
 *
 */
public class EECEPlanningAlgorithm extends BasePlanningAlgorithm {

	/**
	 * ������Ӧ�õ�ID��
	 */
	private int workflowId;
	/**
	 * ÿ������ļ���ʱ��
	 */
	// private Map<Task, Map<CondorVM, Double>> computationCosts = null;

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

	public EECEPlanningAlgorithm() {
		rank = new HashMap<>();
		schedules = new HashMap<>();
		// System.out.println("=====��ӡ��ӡ=======");
		upwardRankList = new ArrayList<>();
		computationTimesList = new ArrayList<>();
		originalDataTransmissionTimesList = new ArrayList<>();
		generatedDataTransmissionTimesList = new ArrayList<>();
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

	/**
	 * The main function
	 */
	@Override
	public void run() {
		List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
		List<List<Task>> workflowList = Parameters.getWorkflowList();
		List<CondorVM> vmList = Parameters.getVmList();
		Map<Integer, List<CondorVM>> dcToVMs = Parameters.getDcToVMs();
		int vmNum = 0;

		for (int i = 0; i < workflowList.size(); i++) {
			Map<Integer, List<Event>> schedules = initializeSchedules();
			Parameters.getSchedulesList().add(schedules);
		}

		// Parameters.setSchedulesList(schedules);

		// System.out.println("������������ܸ�����" + vmList.size() + "; �ɹ�������������ĸ�����" +
		// vmNum);

		// Log.printLine("EECE planner ��ʼ�� " + workflowList.size() + "
		// ������Ӧ�ý��е��ȹ���");
		// Log.printLine("EECE planner running with " + getTaskList().size() + "
		// tasks.");

		// ��һ������ʼ��ÿ��������Ӧ�õ�deadline

		List<Double> deadlineList = null;
		deadlineList = Parameters.getDeadlineList();
		// �ж����deadlineListΪ�գ�˵��Parameters���л�û�б���deadlineֵ����ʱ��Ҫ���м���
		if (null == deadlineList || deadlineList.isEmpty()) {
			deadlineList = new ArrayList<>();
			List<Double> wstList = new ArrayList<>();
			// List<List<Task>> workflowList = getWorkflowList();
			// List<Map<Task, Double>> computationTimesList = new ArrayList<>();
			// List<Map<Task, Map<CondorVM, Double>>> computationCostsList = new
			// ArrayList<>();
			// List<Map<Task, Double>> originalDataTransmissionTimesList = new
			// ArrayList<>();
			// List<Map<Task, Map<Task, Double>>>
			// generatedDataTransmissionTimesList = new ArrayList<>();

			List<Map<Task, Double>> estsForWSTList = new ArrayList<>();
			List<Map<Task, Double>> eftsForWSTList = new ArrayList<>();
			List<Double> lastEFTForWSTList = new ArrayList<>();

			for (int i = 0; i < workflowList.size(); i++) {
				List<Task> taskList = workflowList.get(i);
				setTaskList(taskList);
				// computationCosts = new HashMap<>();
				computationTimes = new HashMap<>();
				originalDataTransmissionTimes = new HashMap<>();
				generatedDataTransmissionTimes = new HashMap<>();
				estsForWST = new HashMap<>();
				eftsForWST = new HashMap<>();
				lastEFTForWST = 0.0;

				RankMethod rankMethod = new UpwardRankMethod(this);

				// ����ÿ������������������ϵļ���ɱ�
				rankMethod.calculateComputationTime();
				rankMethod.calculateGeneratedDataTransmissionTime();
				rankMethod.calculateOriginalDataTransmissionTime();

				// ��ÿ��������Ӧ�õ����ݱ�����Parameters����
				computationTimesList.add(computationTimes);
				// computationCostsList.add(computationCosts);
				generatedDataTransmissionTimesList.add(generatedDataTransmissionTimes);
				originalDataTransmissionTimesList.add(originalDataTransmissionTimes);

				calculateNormalEFT();
				estsForWSTList.add(estsForWST);
				eftsForWSTList.add(eftsForWST);
				lastEFTForWSTList.add(lastEFTForWST);

				// System.out.println("������Ӧ��#" + (i + 1) + "��WSTΪ��" +
				// lastEFTForWST);

//				double ratio = (double) Parameters.randomWsts.get(i) / 10;
//				double wst = ratio * lastEFTForWST;
//				double deadline = lastEFTForWST + wst;
				// ԭʼ��ͨ�����ò�ͬ�ȼ��Ľ�ֹ�������������ֹʱ��
				 int sand = Parameters.getDeadlineLevel().value;
				 
				 //��CyberShake��ֹ��Ҫ���õĿ���һЩ
				 double ratio = (double) sand / 2;
				 
				 //��Montage��ֹ�ڿ���������΢��һЩ
//				 double ratio = (double) sand / 10;
				 double wst = ratio * lastEFTForWST;
				 double deadline = lastEFTForWST + wst;

				deadlineList.add(deadline);
				wstList.add(wst);
			}
			// System.out.println("deadline���ϵĳ��ȣ�" + deadlineList.size());
			Parameters.setDeadlineList(deadlineList);
			Parameters.setWstList(wstList);
			// Parameters.setDeadlineRatioList(deadlineRatioList);

			Parameters.setComputationTimesList(computationTimesList);
			// Parameters.setComputationCostsList(computationCostsList);
			Parameters.setGeneratedDataTransmissionTimesList(generatedDataTransmissionTimesList);
			Parameters.setOriginalDataTransmissionTimesList(originalDataTransmissionTimesList);
			Parameters.setEstsForWSTList(estsForWSTList);
			Parameters.setEftsForWSTList(eftsForWSTList);
			Parameters.setLastEFTForWSTList(lastEFTForWSTList);
		} else {
			computationTimesList = Parameters.getComputationTimesList();
			generatedDataTransmissionTimesList = Parameters.getGeneratedDataTransmissionTimesList();
			originalDataTransmissionTimesList = Parameters.getOriginalDataTransmissionTimesList();
		}

		// �ڶ������Թ�����Ӧ�ý������� 1������Ӧ�ý�ֹʱ������ 2������Ӧ���ɳ�ʱ������3������Ӧ�ô�С����
		List<WorkflowAttribute> returnedWorkflows = null;
		if (Parameters.WorkflowSequeningMethod.EDF == Parameters.getWorkflowSequeningMethod()) {
			returnedWorkflows = sortWorkflowsByDeadline(workflowList);
		} else if (Parameters.WorkflowSequeningMethod.SSF == Parameters.getWorkflowSequeningMethod()) {
			returnedWorkflows = sortWorkflowsByWst(workflowList);
		} else if (Parameters.WorkflowSequeningMethod.SWF == Parameters.getWorkflowSequeningMethod()) {
			returnedWorkflows = sortWorkflowsBySize(workflowList);
		}

		// �ٴ����³�ʼ��eftsForWST
		eftsForWST = new HashMap<>();
		for (Task task : getTaskList()) {
			eftsForWST.put(task, 0.0);
		}

		double elecCostForAllWorkflowsAfterVND = 0.0;
		double elecCostForAllWorkflowsBeforeVND = 0.0;
		// �ֱ��ÿ��������е���
		for (WorkflowAttribute wst : returnedWorkflows) {
			schedules = new HashMap<>();

			int index = wst.getIndex();
			setWorkflowId(index);
			List<Task> taskList = Parameters.getWorkflowList().get(index);
			setTaskList(taskList);

			// ���������������������ȸ��ݹ�����WST�Ĵ�С�Թ�����Ӧ�ý�������Ȼ�������������
			List<TaskRank> ranks = null;
			RankMethod rankMethod = null;
			if (Parameters.RankMethod.UPWARDRANK == Parameters.getRankMethod()) {
				// upward rank�ļ��㷽ʽ
				rankMethod = new UpwardRankMethod(this);
				ranks = rankMethod.calculateRanks();
			} else if (Parameters.RankMethod.DOWNWARDRANK == Parameters.getRankMethod()) {
				// downward rank�ļ��㷽ʽ
				// List<Map<Task, Double>> computationTimesList2 =
				// getComputationTimesList();
				rankMethod = new DownwardRankMethod(this);
				ranks = rankMethod.calculateRanks();
			} else if (Parameters.RankMethod.MERGEDRANK == Parameters.getRankMethod()) {
				// merged rank�ļ��㷽ʽ
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

			// for (TaskRank rank : ranks) {
			// System.out.println("����#" + rank.getTask().getCloudletId() +
			// "---------��������" + rank.getRank());
			// }
			// System.out.println("WSTֵ��" + wst.getValue() + " ˳��" +
			// wst.getIndex());

			// ���Ĳ��������ӽ�ֹʱ�仮��
			Map<Task, Double> subdeadlines = calculateSubdeadlines(index, ranks);
			Parameters.getSubdeadlineList().put(index, subdeadlines);

			// System.out.println("=================�����ӽ�ֹʱ��================");
			// for (Task task : getTaskList()) {
			// System.out.println("����#" + task.getCloudletId() + "; �ӽ�ֹʱ�䣺 " +
			// subdeadlines.get(task));
			// }
			// System.out.println("=================�����ӽ�ֹʱ�����================");

			// ���岽���������ɵĳ�ʼ����������У��������������
			double totalElectricityCost = 0.0;
			double electricityCost = 0.0;
			int timePoint = Parameters.timePoints.get(index);
			for (TaskRank rank : ranks) {
				// ��һ�֣����ѡ��һ����������
				Task task = rank.getTask();
				electricityCost = searchVM(task, dcList, timePoint, index, true);
				totalElectricityCost += electricityCost;
			}
			// System.out.println("VND֮ǰ�ļ۸�" + totalElectricityCost);
			//elecCostForAllWorkflowsBeforeVND += totalElectricityCost;
			// System.out.print("VND��Ѽ۸�Ϊ��" + totalElectricityCost + " - ");

			
			//�������VND�ͻ�׼�ı����������㷨�����������������е�����
			if (Parameters.VNDMethod.VND == Parameters.vndMethod || Parameters.VNDMethod.LS == Parameters.vndMethod) {
				// ������У��ʵ���ʱ����VND
				// ����VND�����������е����Լ�������������
				totalElectricityCost = taskSequenceUpdating(totalElectricityCost, ranks, dcList, timePoint, index, Parameters.vndMethod);
				// System.out.println("VND֮��ļ۸�" + totalElectricityCost);
				Parameters.getTotalElectricityCostList().put(index, totalElectricityCost);
			}
			elecCostForAllWorkflowsAfterVND += totalElectricityCost;

			// ���굱ǰ������Ӧ��֮��Ҫ����ǰӦ����taskռ�õ�VM���µ���һ��������Ӧ����
			if (index + 1 < workflowList.size()) {
				Map<Integer, List<Event>> schedulesList = Parameters.getSchedulesList().get(index);
				Map<Integer, List<Event>> currentSchedulesList = CloneUtils.clone(schedulesList);
				// Map<Integer, List<Event>> currentSchedulesList = new
				// HashMap<>();
				// currentSchedulesList.putAll(schedulesList);
				Parameters.getSchedulesList().set(index + 1, currentSchedulesList);
			}

			// System.out.println("VND֮���Ѽ۸�Ϊ��" + totalElectricityCost);
		}

		System.out.println("VND���ܵ�Ѽ۸�Ϊ��" + elecCostForAllWorkflowsAfterVND);
		// System.out.println("VND֮ǰ�ܵ�Ѽ۸�Ϊ��" + elecCostForAllWorkflowsBeforeVND
		// + " VND֮���ܵ�Ѽ۸�Ϊ��" + elecCostForAllWorkflowsAfterVND);
//		Parameters.elecCostForAllWorkflowsBeforeVND = elecCostForAllWorkflowsBeforeVND;
		Parameters.elecCostForAllWorkflowsAfterVND = elecCostForAllWorkflowsAfterVND;

	}

	/**
	 * ��ʼ��һ�������б�
	 * 
	 * @return
	 */
	private Map<Integer, List<Event>> initializeSchedules() {
		List<WorkflowDatacenter> dcList = Parameters.getDatacenterList();
		Map<Integer, List<Event>> schedules = new HashMap<>();
		for (Datacenter dc : dcList) {
			List<CondorVM> vmObjects = Parameters.getDcToVMs().get(dc.getId());
			for (CondorVM vm : vmObjects) {
				schedules.put(vm.getId(), new ArrayList<>());
			}
		}
		return schedules;
	}

	/**
	 * ����VND���б���������
	 * @param electricityCost
	 * @param ranks
	 * @param dcList
	 * @param timePoint
	 * @param index
	 * @param vndMethod
	 * @return
	 */
	private double taskSequenceUpdating(double electricityCost, List<TaskRank> ranks, List<WorkflowDatacenter> dcList,
			int timePoint, int index, VNDMethod vndMethod) {
		// ��ʼ��eftsForWST�Լ�schedules
		eftsForWST = new HashMap<>();
		for (Task task : getTaskList()) {
			eftsForWST.put(task, 0.0);
		}
		Map<Integer, List<Event>> currentSchedulesList = null;
		if (0 == index) {
			// ����ǵ�һ��������Ӧ�ã�ֱ�Ӹ���Ӧ�ý��г�ʼ��
			currentSchedulesList = initializeSchedules();
		} else {
			// HashMap��Ҫͨ�����¡�ķ�ʽ���и��ƣ������޸ĸ��ƺ�Ķ���Ҳ���������޸�֮ǰ�Ķ���
			Map<Integer, List<Event>> schedulesList = Parameters.getSchedulesList().get(index - 1);
			// currentSchedulesList = new HashMap<>();
			// currentSchedulesList.putAll(schedulesList);
			currentSchedulesList = CloneUtils.clone(schedulesList);
		}
		Parameters.getSchedulesList().set(index, currentSchedulesList);
		// List<Event> schedule = Parameters.getSchedulesList().get(index);
		// schedule.clear();
		
		// Map<Task, Integer> taskMap = new HashMap<>();
		// // List<TaskAndIndex> tasks = new ArrayList<>();
		// for (int i = 0; i < ranks.size(); i++) {
		// TaskRank rank = ranks.get(i);
		// taskMap.put(rank.getTask(), i);
		// // tasks.add(new TaskAndIndex(rank.getTask(), i));
		// }
		
		// ��Ҫ�������������еĸ���
		int kmax = (int) (ranks.size() / 2 * Parameters.kmax);
		int k = 1;
		// double newTotalElecCost = 0.0;
		List<TaskRank> newRanks = new ArrayList<>();
		newRanks.addAll(ranks);
		while (k <= kmax) {
			boolean flag = true;
			while (flag) {
				// ����һ���µ���������
				int pairSize = kmax - k + 1;
				if(Parameters.VNDMethod.LS == vndMethod){
					pairSize = k;
				}
				
				Set<Task> randomSelectedTasks = getRandomSelectedTasks(ranks, pairSize);
				Iterator<Task> iterator = randomSelectedTasks.iterator();
				while (iterator.hasNext()) {
					Task randomedTask = iterator.next();
					List<Task> childList = randomedTask.getChildList();
					if (childList.size() >= 2) {
						// ������ĺ��������ڵ���2
						// int size = childList.size();
						// Random random = new Random();
						// int firstRandom = 0;
						// int secondRandom = 0;
						// //���ѡ����������ͬ�ĺ��ӽڵ�
						// if(2 == size){
						// firstRandom = 0;
						// secondRandom = childList.size() - 1;
						// }else{
						// firstRandom = random.nextInt(size);
						// secondRandom = random.nextInt(size);
						// while(firstRandom == secondRandom){
						// secondRandom = random.nextInt(size);
						// }
						// }
						Task firstTask = childList.get(0);
						Task secondTask = childList.get(childList.size() - 1);
						int firstIndex = childList.indexOf(firstTask);
						int secondIndex = childList.indexOf(secondTask);
						// int firstIndex = taskMap.get(firstTask);
						// int secondIndex = taskMap.get(secondTask);
						TaskRank firstRank = newRanks.get(firstIndex);
						TaskRank secondRank = newRanks.get(secondIndex);
						newRanks.set(firstIndex, secondRank);
						newRanks.set(secondIndex, firstRank);
					}
				}
				
				// �Ե��������������������Դ����
				double newTotalElecCost = 0.0;
				for (TaskRank rank : newRanks) {
					double singleElecCost = searchVM(rank.getTask(), dcList, timePoint, index, false);
					newTotalElecCost += singleElecCost;
				}
				
				if (newTotalElecCost < electricityCost) {
					electricityCost = newTotalElecCost;
					ranks = newRanks;
				} else {
					newRanks = ranks;
					flag = false;
				}
			}
			k++;
		}
		
		// double conservedTotalElecCost = 0;
		for (TaskRank rank : ranks) {
			makeVMUnavaiable(rank.getTask(), index);
			// double singleElecCost = searchVM(rank.getTask(), dcList,
			// timePoint, index, true);
			// conservedTotalElecCost += singleElecCost;
		}
		// System.out.println("֮ǰ��Cost:" + electricityCost + "====֮���Cost��" +
		// conservedTotalElecCost);
		return electricityCost;
	}

	/**
	 * �ҵ���ѵ�task sequence���֮��Ҫ��ÿ��task���ڵ��������ʱ�������Ϊ������
	 * 
	 * @param task
	 * @param index
	 */
	public void makeVMUnavaiable(Task task, int index) {
		// task, vm, minReadyTime, index, 1, false
		CondorVM vm = VmList.getById(Parameters.getVmList(), task.getVmId());
		double minReadyTime = 0.0;
		// ���㵱ǰ�����ǰ����������ݴ���ʱ��
		for (Task parent : task.getParentList()) {
			if (null == parent)
				continue;
			double beginningTime = eftsForWST.get(parent);
			double readyTime = beginningTime;
			CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
			if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
				// �����ǰ���������������ͬһ����������
				if (vm.getHost().getId() != parentVM.getHost().getId()) {
					readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
							Parameters.getBandwidthInDC());
				}
			} else {
				readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
						Parameters.getBandwidthBetweenDC());
			}

			if (readyTime > minReadyTime) {
				minReadyTime = readyTime;
			}
			// minReadyTime = Math.max(minReadyTime, readyTime);
		}

		// �������񱾵����ݴ���ʱ��
		minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);
		findFinishTime(task, vm, minReadyTime, index, 1, true);
	}

	/**
	 * ���ȡ������������TASK
	 * 
	 * @param tasks
	 * @param pairSize
	 * @return
	 */
	private Set<Task> getRandomSelectedTasks(List<TaskRank> tasks, int pairSize) {
		Random random = new Random();
		Set<Task> result = new LinkedHashSet<Task>();
		while (result.size() < pairSize) {
			int randomIndex = random.nextInt(tasks.size());
			// TaskAndIndex rank = new TaskAndIndex(tasks.get(randomIndex),
			// randomIndex);
			result.add(tasks.get(randomIndex).getTask());
		}
		return result;
	}

	/**
	 * Ϊ������Ӧ���е�ÿ���������������
	 * 
	 * @param task
	 * @param dcList
	 * @param timePoint
	 */
	private double searchVM(Task task, List<WorkflowDatacenter> dcList, int timePoint, int index, boolean isVND) {
		// Random random = new Random();
		CondorVM chosenVM = null;
		double earliestFinishTime = Double.MAX_VALUE;
		double bestReadyTime = 0.0;
		double finishTime = 0.0;
		double bestFrequency = 0.0;
		double electricityCost = Double.MAX_VALUE;
		double[] elecPrice = Parameters.elecPrices[timePoint];

		// �����������е�ÿ����������б������ҳ�����ʵ������
		for (WorkflowDatacenter dc : dcList) {

			List<SortedVM> sortedVMs = Parameters.dcToSortedVMs.get(dc.getId());

			// List<CondorVM> vmList = Parameters.getDcToVMs().get(dc.getId());
			// List<SortedVM> sortedVMs = new ArrayList<>();
			// // ��һ�֣��ȸ��������Ż��ķ�ʽ������
			// for (CondorVM vm : vmList) {
			// double ppW = vm.getMips() / vm.getPower();
			// SortedVM sortedVM = new SortedVM(vm, ppW);
			// sortedVMs.add(sortedVM);
			// }
			// Collections.sort(sortedVMs);

			for (SortedVM sortedVM : sortedVMs) {
				CondorVM vm = sortedVM.getVm();
				double minReadyTime = 0.0;
				double bestBeginningTime = 0.0;

				// ���㵱ǰ�����ǰ����������ݴ���ʱ��
				for (Task parent : task.getParentList()) {
					if (null == parent)
						continue;
					double beginningTime = eftsForWST.get(parent);
					double readyTime = beginningTime;
					CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
					if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
						// �����ǰ���������������ͬһ����������
						if (vm.getHost().getId() != parentVM.getHost().getId()) {
							readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent,
									task, Parameters.getBandwidthInDC());
						}
					} else {
						readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
								Parameters.getBandwidthBetweenDC());
					}

					if (readyTime > minReadyTime) {
						minReadyTime = readyTime;
						bestBeginningTime = beginningTime;
					}
					// minReadyTime = Math.max(minReadyTime, readyTime);
				}

				// �������񱾵����ݴ���ʱ��
				minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);

				double tmpMinReadyTime = minReadyTime;

				// ���������������ʱ��
				finishTime = findFinishTime(task, vm, minReadyTime, index, 1, false);

				Map<Task, Double> subdeadlines = Parameters.getSubdeadlineList().get(index);
				double subdeadline = subdeadlines.get(task);

				if (finishTime <= subdeadline) {
					if (Parameters.TSTMethod.NOTST == Parameters.tstMethod) {
						// // ��ǰ�����������������ӽ�ֹʱ�䣬�����Ѽ۸�without DVFS��
						double power = vm.getPower() / 1000;
						double durationTime = finishTime - bestBeginningTime;
						double tmpElectricityCost = power * durationTime / 3600
								* elecPrice[Parameters.dcToIndex.get(dc.getId())];

						if (tmpElectricityCost < electricityCost) {
							electricityCost = tmpElectricityCost;
							bestReadyTime = minReadyTime;
							earliestFinishTime = finishTime;
							chosenVM = vm;
						}
					} else if (Parameters.TSTMethod.TST == Parameters.tstMethod) {
						// ��ǰ�����������������ӽ�ֹʱ�䣬�����Ѽ۸�ͨ�� DVFS����������
						double power = vm.getPower() / 1000;
						// ͨ��DVFS��Ƶ��������VM�Ĺ����Լ������ٶ�
						double tst = subdeadline - finishTime;
						double frequency = (subdeadline - bestBeginningTime - Parameters.beta * tst)
								/ (subdeadline - bestBeginningTime);
						// Map<Task, Map<CondorVM, Double>> computationCosts =
						// Parameters.getComputationCostsList().get(index);
						// double computationCost =
						// computationCosts.get(task).get(vm);
						double computationCost = TaskAssigningUtils.calculateComputationTime(task, vm, frequency);
						double improvedComputationCost = computationCost / frequency;
						// computationCosts.get(task).put(vm,
						// improvedComputationCost);
						double tmpFinishTime = tmpMinReadyTime + improvedComputationCost;
						double durationTime = tmpFinishTime - bestBeginningTime;

						double tmpElectricityCost = power * frequency * durationTime / 3600
								* elecPrice[Parameters.dcToIndex.get(dc.getId())];

						// System.out.println("��Ƶǰ�ĵ�ѣ�" + originalTmpElecCost +
						// ":
						// ��Ƶ��ĵ�ѣ�" + tmpElectricityCost);
						if (tmpElectricityCost < electricityCost) {
							electricityCost = tmpElectricityCost;
							bestReadyTime = tmpMinReadyTime;
							earliestFinishTime = tmpFinishTime;
							bestFrequency = frequency;
							chosenVM = vm;
						}
					}

				}
			}
			// System.out.println("��������#" + dc.getId() + "�ϵĵ�Ѽ۸�Ϊ��" +
			// electricityCost);
		}
		// WorkflowDatacenter dc = dcList.get(random.nextInt(dcList.size()));
		// System.out.println("���˵ڶ���findFinishTime");

		// ����������ʧ�ܵ����������ķ���һ��VM
		if (null == chosenVM) {
			System.out.println("�����û�з���ɹ������");
			// List<CondorVM> vmList = Parameters.getVmList();
			Random random = new Random();
			int dcIndex = random.nextInt(dcList.size());
			WorkflowDatacenter dc = dcList.get(dcIndex);
			List<CondorVM> vmListForGivenDC = Parameters.getDcToVMs().get(dc.getId());

			int vmIndex = random.nextInt(vmListForGivenDC.size());
			// SortedVM sortedVM =
			// Parameters.dcToSortedVMs.get(dc.getId()).get(0);
			// CondorVM vm = sortedVM.getVm();
			CondorVM vm = vmListForGivenDC.get(vmIndex);

			double minReadyTime = 0.0;
			double bestBeginningTime = 0.0;

			// ���㵱ǰ�����ǰ����������ݴ���ʱ��
			for (Task parent : task.getParentList()) {
				if (null == parent)
					continue;
				double beginningTime = eftsForWST.get(parent);
				double readyTime = beginningTime;
				CondorVM parentVM = VmList.getById(Parameters.getVmList(), parent.getVmId());
				if (vm.getHost().getDatacenter().getId() == parentVM.getHost().getDatacenter().getId()) {
					// �����ǰ���������������ͬһ����������
					if (vm.getHost().getId() != parentVM.getHost().getId()) {
						readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
								Parameters.getBandwidthInDC());
					}
				} else {
					readyTime += TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task,
							Parameters.getBandwidthBetweenDC());
				}
				if (readyTime > minReadyTime) {
					minReadyTime = readyTime;
					bestBeginningTime = beginningTime;
				}
				// minReadyTime = Math.max(minReadyTime, readyTime);
			}

			// �������񱾵����ݴ���ʱ��
			minReadyTime += TaskAssigningUtils.calculateOriginalDataTransmissionTime(task, vm);

			// ���������������ʱ��
			finishTime = minReadyTime + task.getCloudletLength() / vm.getMips();
			// finishTime = findFinishTime(task, vm, minReadyTime, index, 1,
			// false);

			double power = vm.getPower() / 1000;
			double durationTime = finishTime - bestBeginningTime;
			double tmpElectricityCost = power * durationTime / 3600
					* elecPrice[Parameters.dcToIndex.get(vm.getHost().getDatacenter().getId())];

			// System.out.println("��Ƶǰ�ĵ�ѣ�" + originalTmpElecCost + ": ��Ƶ��ĵ�ѣ�"
			// + tmpElectricityCost);
			electricityCost = tmpElectricityCost;
			bestReadyTime = minReadyTime;
			chosenVM = vm;
			earliestFinishTime = finishTime;
		}

		if (isVND) {
			findFinishTime(task, chosenVM, bestReadyTime, index, bestFrequency, true);
		}
		eftsForWST.put(task, earliestFinishTime);

		task.setVmId(chosenVM.getId());
		return electricityCost;
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
	 * ����������Ӧ�õĴ�С������
	 * 
	 * @param workflowList
	 * @return
	 */
	private List<WorkflowAttribute> sortWorkflowsBySize(List<List<Task>> workflowList) {
		List<Double> workflowSizeList = Parameters.getWorkflowSizes();
		List<WorkflowAttribute> workflowSizes = new ArrayList<>();
		for (int i = 0; i < workflowSizeList.size(); i++) {
			double size = workflowSizeList.get(i);
			WorkflowAttribute workflowSize = new WorkflowAttribute(i, size);
			workflowSizes.add(workflowSize);
		}
		Collections.sort(workflowSizes);
		return workflowSizes;
	}

	/**
	 * ���ݹ�����Ӧ�õ��ɳ�ʱ��������
	 * 
	 * @param workflowList
	 * @return
	 */
	private List<WorkflowAttribute> sortWorkflowsByWst(List<List<Task>> workflowList) {
		List<Double> wstList = Parameters.getWstList();
		List<WorkflowAttribute> wsts = new ArrayList<>();
		for (int i = 0; i < wstList.size(); i++) {
			double wstItem = wstList.get(i);
			WorkflowAttribute wst = new WorkflowAttribute(i, wstItem);
			wsts.add(wst);
		}
		Collections.sort(wsts);
		return wsts;
	}

	/**
	 * ����Ӧ�ý�ֹʱ���������
	 * 
	 * @param workflowList
	 * @return
	 */
	private List<WorkflowAttribute> sortWorkflowsByDeadline(List<List<Task>> workflowList) {
		List<Double> deadlineList = Parameters.getDeadlineList();
		List<WorkflowAttribute> deadlines = new ArrayList<>();
		for (int i = 0; i < deadlineList.size(); i++) {
			double deadlineItem = deadlineList.get(i);
			WorkflowAttribute deadline = new WorkflowAttribute(i, deadlineItem);
			deadlines.add(deadline);
		}
		Collections.sort(deadlines);
		return deadlines;
	}

	/**
	 * ����ÿ��������ӽ�ֹʱ�䣬��ʱ����Ҫ���轫������䵽�����������ȥ��
	 */
	private Map<Task, Double> calculateSubdeadlines(int index, List<TaskRank> ranks) {
		Map<Task, Double> subdeadlines = new HashMap<>();
		// ����ÿ����������翪ʼʱ�䡢�������ʱ��
		calculateEFTForSubdeadline(index);
		double deadline = Parameters.getDeadlineList().get(index);
		// System.out.println("�������ʱ��for subdealine��" +
		// Parameters.getLastEFTForSubdeadlineList().get(index)
		// + " --Dealine for SubdealineΪ��" + deadline);
		// �ȼ��㹤�����ɳ�ʱ��
		double lastEFTForSubdeadline = Parameters.getLastEFTForSubdeadlineList().get(index);
		double wst = deadline - lastEFTForSubdeadline;
		double subdealine = 0.0;
		double totalcomputationTime = 0.0;
		Map<Task, Double> computationTimes = computationTimesList.get(index);
		// Map<Task, Double> computationTimes =
		// Parameters.getComputationTimesList().get(index);
		Map<Task, Double> eftsForSubdeadline = Parameters.getEftsForSubdeadlineList().get(index);

		for (Task task : getTaskList()) {
			totalcomputationTime += computationTimes.get(task);
		}

		int maximalDepth = Parameters.getMaximalDepthList().get(index);
		double computationTime = 0.0;
		for (TaskRank rank : ranks) {
			Task task = rank.getTask();
			computationTime += computationTimes.get(task);
			// ���������ɳ�ʱ�����ǰ����ִ��ʱ��ɱ����ķ�ʽ�����ÿ������
			subdealine = eftsForSubdeadline.get(task) + wst * task.getDepth() / maximalDepth;
			// subdealine = eftsForSubdeadline.get(task) + wst * computationTime
			// / totalcomputationTime;
			subdeadlines.put(task, subdealine);
		}

		// Parameters.setSubdeadlineList(subdeadlines);
		return subdeadlines;
	}

	/**
	 * ��������ÿ����������翪ʼʱ�䡢�������ʱ��
	 */
	public Map<Task, Double> calculateNormalEFT() {
		// Map<Task, Double> eftsForWST = new HashMap<>();
		// �ȸ�ÿ��������������ʱ���ʼ��Ϊ0
		for (Task task : getTaskList()) {
			eftsForWST.put(task, 0.0);
		}

		for (Task task : getTaskList()) {
			estsForWST.put(task, 0.0);
		}

		for (Task task : getTaskList()) {
			double averageComputationCost = 0.0;
			averageComputationCost = computationTimes.get(task);

			double earliestStartTime = 0.0;
			double max = 0.0;
			for (Task parent : task.getParentList()) {
				// ������������翪ʼʱ��
				earliestStartTime = eftsForWST.get(parent) + generatedDataTransmissionTimes.get(parent).get(task)
						+ computationTimes.get(parent);
				max = Math.max(max, earliestStartTime);
			}

			estsForWST.put(task, max + originalDataTransmissionTimes.get(task));
			double initialEarliestFinishTime = estsForWST.get(task) + averageComputationCost;

			if (initialEarliestFinishTime > lastEFTForWST)
				lastEFTForWST = initialEarliestFinishTime;
			eftsForWST.put(task, initialEarliestFinishTime);
		}

		// System.out.println("===========��ӡÿ����������翪ʼʱ�䡢�������ʱ��==============");
		// for (Task task : getTaskList()) {
		// System.out.println(estsForWST.get(task) + " : " +
		// eftsForWST.get(task));
		// }
		// System.out.println("========================��ӡ����=========================");

		return eftsForWST;
	}

	/**
	 * �����ӽ�ֹʱ����������ʱ��
	 * 
	 * @param index
	 * 
	 * @return
	 */
	public Map<Task, Double> calculateEFTForSubdeadline(int index) {
		Map<Task, Map<Task, Double>> generatedDataTransmissionTimes = generatedDataTransmissionTimesList.get(index);
		Map<Task, Double> originalDataTransmissionTimes = originalDataTransmissionTimesList.get(index);
		Map<Task, Double> estsForSubdeadline = new HashMap<>();
		Map<Task, Double> eftsForSubdeadline = new HashMap<>();
		double lastEFTForSubdeadline = 0.0;
		// double est = 0.0;
		// �ȸ�ÿ��������������ʱ���ʼ��Ϊ0
		for (Task task : getTaskList()) {
			eftsForSubdeadline.put(task, 0.0);
		}

		// ��ÿ����������翪ʼʱ���ʼ��Ϊ0
		for (Task task : getTaskList()) {
			estsForSubdeadline.put(task, 0.0);
		}

		for (Task task : getTaskList()) {
			double averageComputationCost = 0.0;
			double fastestSpeed = Parameters.getFastestVM().getMips();
			averageComputationCost = task.getCloudletLength() / fastestSpeed;

			double earliestStartTime = 0.0;
			double max = 0.0;
			for (Task parent : task.getParentList()) {
				// ������������翪ʼʱ��
				earliestStartTime = eftsForSubdeadline.get(parent)
						+ generatedDataTransmissionTimes.get(parent).get(task)
						+ parent.getCloudletLength() / fastestSpeed;
				// earliestStartTime = eftsForSubdeadline.get(parent)
				// +
				// Parameters.getGeneratedDataTransmissionTimesList().get(index).get(parent).get(task)
				// + parent.getCloudletLength() / fastestSpeed;
				max = Math.max(max, earliestStartTime);
			}
			// est = max + originalDataTransmissionTimes.get(task);

			estsForSubdeadline.put(task, max + originalDataTransmissionTimes.get(task));
			// estsForSubdeadline.put(task, max +
			// Parameters.getOriginalDataTransmissionTimesList().get(index).get(task));
			double initialEarliestFinishTime = estsForSubdeadline.get(task) + averageComputationCost;

			if (initialEarliestFinishTime > lastEFTForSubdeadline)
				lastEFTForSubdeadline = initialEarliestFinishTime;
			eftsForSubdeadline.put(task, initialEarliestFinishTime);
		}

		Parameters.getEstsForSubdeadlineList().put(index, estsForSubdeadline);
		Parameters.getEftsForSubdeadlineList().put(index, eftsForSubdeadline);
		Parameters.getLastEFTForSubdeadlineList().put(index, lastEFTForSubdeadline);

		// System.out.println("===========��ӡÿ����������翪ʼʱ�䡢�������ʱ��==============");
		// for (Task task : getTaskList()) {
		// System.out.println(estsForSubdeadline.get(task) + " : " +
		// eftsForSubdeadline.get(task));
		// }
		// System.out.println("========================��ӡ����=========================");

		return eftsForSubdeadline;
	}

	/**
	 * Finds the best time slot available to minimize the finish time of the
	 * given task in the vm with the constraint of not scheduling it before
	 * readyTime. If occupySlot is true, reserves the time slot in the schedule.
	 *
	 * @param task
	 *            The task to have the time slot reserved
	 * @param vm
	 *            The vm that will execute the task
	 * @param readyTime
	 *            The first moment that the task is available to be scheduled
	 * @param frequency
	 * @param occupySlot
	 *            If true, reserves the time slot in the schedule.
	 * @return The minimal finish time of the task in the vmn
	 */
	private double findFinishTime(Task task, CondorVM vm, double readyTime, int index, double frequency,
			boolean occupySlot) {
		if (null == vm)
			System.out.println("VMΪ��");
		/**
		 * ��������ÿ��VM��task�����б�
		 */
		Map<Integer, List<Event>> schedules = Parameters.getSchedulesList().get(index);
		List<Event> sched = schedules.get(vm.getId());
		// Map<Task, Map<CondorVM, Double>> computationCosts =
		// Parameters.getComputationCostsList().get(index);
		// double computationCost = computationCosts.get(task).get(vm);
		double computationCost = TaskAssigningUtils.calculateComputationTime(task, vm, frequency);
		double start, finish;
		int pos;

		/**
		 * �����ǰVM�ĵ����б��ǿյĻ�
		 */
		if (sched.isEmpty()) {
			if (occupySlot) {
				sched.add(new Event(readyTime, readyTime + computationCost));
			}
			return readyTime + computationCost;
		}

		/**
		 * �����ǰVM�ĵ����б�ֻ��һ��TASK
		 */
		if (sched.size() == 1) {
			if (readyTime >= sched.get(0).finish) {
				pos = 1;
				start = readyTime;
			} else if (readyTime + computationCost <= sched.get(0).start) {
				pos = 0;
				start = readyTime;
			} else {
				pos = 1;
				start = sched.get(0).finish;
			}

			if (occupySlot) {
				sched.add(pos, new Event(start, start + computationCost));
			}
			return start + computationCost;
		}

		/**
		 * ��ǰVM�ĵ����������ж�������ȵ�TASK
		 */
		// Trivial case: Start after the latest task scheduled
		start = Math.max(readyTime, sched.get(sched.size() - 1).finish);
		finish = start + computationCost;
		int i = sched.size() - 1;
		int j = sched.size() - 2;
		pos = i + 1;
		/**
		 * ��������ʱ���
		 */
		while (j >= 0) {
			Event current = sched.get(i);
			Event previous = sched.get(j);

			if (readyTime > previous.finish) {
				if (readyTime + computationCost <= current.start) {
					start = readyTime;
					finish = readyTime + computationCost;
				}

				break;
			}
			if (previous.finish + computationCost <= current.start) {
				start = previous.finish;
				finish = previous.finish + computationCost;
				pos = i;
			}
			i--;
			j--;
		}

		if (readyTime + computationCost <= sched.get(0).start) {
			pos = 0;
			start = readyTime;

			if (occupySlot) {
				sched.add(pos, new Event(start, start + computationCost));
			}
			return start + computationCost;
		}
		if (occupySlot) {
			sched.add(pos, new Event(start, finish));
		}
		return finish;
	}

	public Map<Task, Double> getComputationTimes() {
		return computationTimes;
	}

	public void setComputationTimes(Map<Task, Double> computationTimes) {
		this.computationTimes = computationTimes;
	}

	public Map<Task, Map<Task, Double>> getGeneratedDataTransmissionTimes() {
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

}
