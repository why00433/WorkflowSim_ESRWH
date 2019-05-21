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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.workflowsim.ClusterStorage;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.DeadlineLevel;
import org.workflowsim.utils.Parameters.RankMethod;
import org.workflowsim.utils.Parameters.WorkflowSequeningMethod;
import org.workflowsim.utils.ReplicaCatalog;

import com.weiyu.experiment.domain.SimulationResult;
import com.weiyu.experiment.domain.SortedVM;
import com.weiyu.experiment.utils.Print;
import com.weiyu.experiment.utils.VMAllocationPolicyImpl;

/**
 * 多数据中心场景下多工作流实例
 * 
 * @author Wei Yu
 *
 */
public class EECEPlannerAlgorithmExecutor {

	public static void main(String[] args) throws Exception {

		// First step: Initialize the WorkflowSim package.
		List<String> daxPaths = null;
		String prefixPath = "F:/毕业论文!/Experiment/Montage/";
		int[] workflowNumbers = { 20, 40, 60, 80, 100 };
		int[] taskNumbers = { 50, 100, 200, 500 };

		// 工作流应用排序方法
		Parameters.WorkflowSequeningMethod[] workflowMethods = { Parameters.WorkflowSequeningMethod.EDF,
				Parameters.WorkflowSequeningMethod.SSF, Parameters.WorkflowSequeningMethod.SWF };
		// 任务排序方法
		Parameters.RankMethod[] rankMethods = { Parameters.RankMethod.UPWARDRANK, Parameters.RankMethod.DOWNWARDRANK,
				Parameters.RankMethod.MERGEDRANK };
		// 截止时间的松紧
		Parameters.DeadlineLevel[] deadlinelevels = { Parameters.DeadlineLevel.D1, Parameters.DeadlineLevel.D2,
				Parameters.DeadlineLevel.D3, Parameters.DeadlineLevel.D4, Parameters.DeadlineLevel.D5 };
		double[] kmaxs = { 0.2, 0.4, 0.6, 0.8, 1 };

		// String xmlPath = prefixPath;
		for (int i = 0; i < workflowNumbers.length; i++) {
			for (int j = 0; j < taskNumbers.length; j++) {
				List<SimulationResult> results = new ArrayList<>();
				for (int k = 0; k < 10; k++) {
					daxPaths = new ArrayList<>();
					for (int s = 0; s < workflowNumbers[i]; s++) {
						String xmlPath = prefixPath + "Montage_" + workflowNumbers[i] + "_" + taskNumbers[j] + "_" + k
								+ "_" + s + ".xml";
						daxPaths.add(xmlPath);
					}

					// 判断该文件是否存在
					File daxFile = null;
					for (String daxPath : daxPaths) {
						daxFile = new File(daxPath);
						if (!daxFile.exists()) {
							Log.printLine(
									"Warning: Please replace daxPath with the physical path in your working environment!");
							return;
						}
					}

					Parameters.clear();

					// 给每个工作流应用初始化一个到达时刻
					Random random = new Random();
					for (int item = 0; item < daxPaths.size(); item++) {
						int timePoint = random.nextInt(Parameters.elecPrices.length);
						Parameters.timePoints.add(timePoint);
					}

					// 给每个工作流应用初始化一个本地数据的存放位置
					for (int item = 0; item < daxPaths.size(); item++) {
						// 虚拟机的数量预设置为5000个
						int randomVMIndex = random.nextInt(5000);
						Parameters.inputDataLocations.add(randomVMIndex);
					}

					// 给每个工作流应用生成一个随机的松弛时间
					for (int item = 0; item < daxPaths.size(); item++) {
						// 虚拟机的数量预设置为5000个
						int randomWst = 1 + random.nextInt(10);
						Parameters.randomWsts.add(randomWst);
					}
					// List<SimulationResult> results = new ArrayList<>();
					// 针对不同的参数进行实验
					for (int l = 0; l < workflowMethods.length; l++) {
						for (int m = 0; m < rankMethods.length; m++) {
							// for(int n = 0; n < deadlinelevels.length; n++){
							// for(int t = 0; t < kmaxs.length; t++){
							SimulationResult result = new SimulationResult();
							long beginTime = System.currentTimeMillis();
							// 调用仿真函数
							doSimulations(daxPaths, workflowMethods[l], rankMethods[m], deadlinelevels[0], kmaxs[0]);
							long currentTime = System.currentTimeMillis();

							// 秒为单位
							long runtime = (currentTime - beginTime) / 1000;
							result.setWorkflowNumber(workflowNumbers[i]);
							result.setTaskNumber(taskNumbers[j]);
							result.setInstanceNumber(k);
							result.setWorkflowMethod(workflowMethods[l].toString());
							result.setRankMethod(rankMethods[m].toString());
							// result.setDeadlinelevel(deadlinelevels[n].toString());
							// result.setKmax(kmaxs[t]);
							result.setElecCostForAllWorkflowsBeforeVND(Parameters.elecCostForAllWorkflowsBeforeVND);
							// result.setElecCostForAllWorkflowsAfterVND(Parameters.elecCostForAllWorkflowsAfterVND);
							result.setRuntime(runtime);
							results.add(result);
						}
						// }
						// }
					}
				}
				// 求出工作流应用数量跟任务数量的组合下的各参数的RPD值
				calculateRPD(results);
				exportToTxt(results);
			}
		}
		// System.out.println("运行时间：" + (currentTime - beginTime) / 1000 + "秒");
		// Print.printJobList(outputList);

	}

	/**
	 * 将得到的结果写入到TXT文档中
	 * 
	 * @param results
	 * @throws IOException
	 */
	private static void exportToTxt(List<SimulationResult> results) throws IOException {
		String filePath = "E:/实验数据/Montage_results.txt";
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();// 不存在则创建
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));// true,则追加写入text文本
		for (SimulationResult result : results) {
			writer.write(result.toString());
			writer.write("\r\n");// 换行
		}

		writer.flush();
		if (null != writer) {
			writer.close();
		}
	}

	/**
	 * 针对计算得到的结果求出该结果的RPD值
	 * 
	 * @param results
	 */
	private static void calculateRPD(List<SimulationResult> results) {
		double minimalElecCostBeforeVND = Double.MAX_VALUE;
		// double minimalElecCostAfterVND = Double.MAX_VALUE;

		for (SimulationResult result : results) {
			if (result.getElecCostForAllWorkflowsBeforeVND() < minimalElecCostBeforeVND)
				minimalElecCostBeforeVND = result.getElecCostForAllWorkflowsBeforeVND();

			// if(result.getElecCostForAllWorkflowsAfterVND() <
			// minimalElecCostAfterVND)
			// minimalElecCostAfterVND =
			// result.getElecCostForAllWorkflowsAfterVND();
		}

		for (SimulationResult result : results) {
			double elecCostForAllWorkflowsBeforeVND = result.getElecCostForAllWorkflowsBeforeVND();
			// double elecCostForAllWorkflowsAfterVND =
			// result.getElecCostForAllWorkflowsAfterVND();
			result.setRpdBeforeVND(
					(elecCostForAllWorkflowsBeforeVND - minimalElecCostBeforeVND) / minimalElecCostBeforeVND * 100);
			// result.setRpdAfterVND((elecCostForAllWorkflowsAfterVND -
			// minimalElecCostAfterVND) / minimalElecCostAfterVND * 100);
		}
	}

	/**
	 * 仿真实验的主体函数
	 * 
	 * @param daxPaths
	 * @param workflowMethod
	 * @param rankMethod
	 * @param deadlinelevel
	 * @param kmax
	 * @throws Exception
	 */
	protected static void doSimulations(List<String> daxPaths, WorkflowSequeningMethod workflowMethod,
			RankMethod rankMethod, DeadlineLevel deadlinelevel, double kmax) throws Exception {
		Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.INVALID;
		Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.EECE;
		ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

		Parameters.setWorkflowSequeningMethod(workflowMethod);
		Parameters.setRankMethod(rankMethod);
		Parameters.setDeadlineLevel(deadlinelevel);
		Parameters.kmax = kmax;
		Parameters.beta = 0.8;
		Parameters.tstMethod = Parameters.TSTMethod.NOTST;

		/**
		 * No overheads
		 */
		OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);

		/**
		 * No Clustering
		 */
		ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
		ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

		// 数据中心的数量
		int dcNum = 10;
		// 虚拟机的总数量，虚拟机的数量一般设置为数据中心总数量的10倍
		int vmNum = dcNum * 500;// number of vms;
		/**
		 * Initialize static parameters
		 */
		Parameters.init(vmNum, daxPaths, null, null, op, cp, sch_method, pln_method, null, 0);
		ReplicaCatalog.init(file_system);

		// before creating any entities.
		int num_user = 1; // number of grid users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);

		// 创建数据中心，分别创建5，10,15,30,50个数据中心
		List<WorkflowDatacenter> dcList = createDatacenters(dcNum);
		Parameters.setDatacenterList(dcList);

		// 将每个DC跟他的位置关系对应好
		for (int i = 0; i < dcList.size(); i++) {
			Parameters.dcToIndex.put(dcList.get(i).getId(), i);
		}

		// System.out.println(Parameters.timePoints);

		/**
		 * Create a WorkflowPlanner with one scheduler.
		 */
		WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
		/**
		 * Create a WorkflowEngine. Attach it to the workflow planner
		 */
		WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
		List<CondorVM> vmList = Parameters.getVmList();
		// Map<Integer, List<CondorVM>> dcToVMs = Parameters.getDcToVMs();
		Map<Integer, List<CondorVM>> dcToVMs = null;
		// List<CondorVM> vmList = null;
		if (null == vmList || vmList.size() == 0) {
			String vmListPath = "E://实验数据/VM配置.xls";
			vmList = Print.readVMListFromExcel(vmListPath);
			// for (CondorVM vm : vmList) {
			// System.out.println(vm.getId() + ":" + vm.getVmm());
			// }

			if (null == vmList || vmList.size() == 0) {
				vmList = createVM(wfEngine.getSchedulerId(0), vmNum);
				Print.exportVMsToExcel(vmList);
			}
			Parameters.setVmList(vmList);
		}
		/**
		 * Submits these lists of vms to this WorkflowEngine.
		 */
		wfEngine.submitVmList(vmList, 0);

		// 将其保存在Parameters中
		// Parameters.setVmList(vmList);
		// wfEngine.submitVmList(vmlist1, 0);
		// 创建一个绑定函数将虚拟机跟数据中心中的Host绑定起来
		dcToVMs = VMAllocationPolicyImpl.bindVMToDatacenter(dcList, vmList);

		// 保存数据中心ID跟该数据中心中部署的虚拟机的一一对应关系
		Parameters.setDcToVMs(dcToVMs);

		/**
		 * 绑定数据中心
		 */
		for (int i = 0; i < dcNum; i++) {
			wfEngine.bindSchedulerDatacenter(dcList.get(i).getId(), 0);
		}

		for (int i = 0; i < dcNum; i++) {
			wfEngine.submitDatacenters(dcList.get(i), 0);
		}

		// 将每个数据中心中的虚拟机按照性能能耗比排好序
		for (WorkflowDatacenter dc : dcList) {
			List<CondorVM> vmListBySpecialDC = Parameters.getDcToVMs().get(dc.getId());
			List<SortedVM> sortedVMs = new ArrayList<>();
			// 第一种：先根据能量优化的方式来排序
			for (CondorVM vm : vmListBySpecialDC) {
				double ppW = vm.getMips() / vm.getPower();
				SortedVM sortedVM = new SortedVM(vm, ppW);
				sortedVMs.add(sortedVM);
			}
			Collections.sort(sortedVMs);
			Parameters.dcToSortedVMs.put(dc.getId(), sortedVMs);
		}

		// 设置一个统一的数据中心内的VM进行传输的带宽，以及数据中心之间的数据传输的带宽
		double interBandwidth = 1.5e7;
		Parameters.setBandwidthBetweenDC(interBandwidth);
		Parameters.setBandwidthInDC(interBandwidth * 10);

		CloudSim.startSimulation();
		List<Job> outputList = wfEngine.getJobsReceivedList();
		CloudSim.stopSimulation();
	}

	protected static List<WorkflowDatacenter> createDatacenters(int dcNum) {
		List<WorkflowDatacenter> datacenters = new ArrayList<>();

		WorkflowDatacenter datacenter = null;
		for (int i = 0; i < dcNum; i++) {
			datacenter = createDatacenter("DC" + (i + 1), i);
			datacenters.add(datacenter);
		}
		return datacenters;
	}

	private static WorkflowDatacenter createDatacenter(String name, int index) {
		// Here are the steps needed to create a PowerDatacenter:
		// 第一步：先创建CPU核的数量We need to create a list to store one or more
		// Machines
		List<Host> hostList = new ArrayList<>();

		// 第二步：创建主机Host，每个数据中心假设由1-10个主机
		int hostNumber = 0;
		// Random random = new Random();
		hostNumber = Parameters.hostNumberList[index];
		// hostNumber = 1 + random.nextInt(200);
		for (int i = 1; i <= hostNumber; i++) {
			List<Pe> peList = new ArrayList<>();

			// 由于是异构的，主机的处理速度也是不一样的，因此以0-1之内的随机数来生成随机的速度
			int mips = 2000;
			// random = new Random();
			// int ratio = 1 + random.nextInt(5);
			int ratio = 1 + i % 5;

			mips = mips * ratio / 5;
			// System.out.println("主机ID：" + i + "- 处理速度：" + mips);
			// 3. Create PEs and add these into the list.
			// for a quad-core machine, a list of 4 PEs is required:
			if (ratio % 2 == 0) {
				for (int j = 0; j < 8; j++) {
					peList.add(new Pe(j, new PeProvisionerSimple(mips)));
				}
			} else {
				for (int j = 0; j < 4; j++) {
					peList.add(new Pe(j, new PeProvisionerSimple(mips)));
				}
			}

			int ram = 8192; // host memory (MB)
			long storage = 500000; // host storage
			int bw = 10000;
			Host host = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList,
					new VmSchedulerTimeShared(peList));
			hostList.add(host); // This is our first machine
		}

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.1; // the cost of using storage in this
										// resource
		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<>(); // we are not
																// adding SAN
																// devices by
																// now
		WorkflowDatacenter datacenter = null;
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a cluster storage object.
		/**
		 * The bandwidth within a data center.
		 */
		// double intraBandwidth = 1.5e7;

		/**
		 * The bandwidth between data centers.
		 */
		// double interBandwidth = intraBandwidth * 10;// the number comes from
		// the futuregrid site, you can specify your bw
		try {
			ClusterStorage s1 = new ClusterStorage(name, 1e12);
			// The bandwidth within a data center
			s1.setBandwidth("in", Parameters.getBandwidthInDC());
			// The bandwidth to the source site
			s1.setBandwidth("between", Parameters.getBandwidthBetweenDC());
			storageList.add(s1);
			datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList),
					storageList, 0);
			// for(Host host : hostList){
			// host.setDatacenter(datacenter);
			// }
			// List<Storage> returnedStorageList = datacenter.getStorageList();
			// for (Storage storage : returnedStorageList) {
			// System.out.println(storage);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
	}

	/**
	 * 根据输入的VM数量，创建对应数量的虚拟机
	 * 
	 * @param userId
	 * @param vms
	 * @return
	 */
	protected static List<CondorVM> createVM(int userId, int vms) {
		// Creates a container to store VMs. This list is passed to the broker
		// later
		LinkedList<CondorVM> list = new LinkedList<>();

		// VM Parameters
		long size = 1000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VMs
		// CondorVM[] vm = new CondorVM[vms];
		CondorVM vm = null;

		CondorVM fastestVm = null;
		double fastestMips = 0.0;
		for (int i = 0; i < vms; i++) {
			// 生成随机的虚拟机处理速度
			Random random = new Random();
			int ratio = 1 + random.nextInt(5);

			int generatedMips = (int) (ratio * mips / 5);

			// 设置每台虚拟机的功率
			double power = generatedMips / 4;
			// System.out.println("比例：" + ratio + " 速度：" + generatedMips + "
			// 功率为：" + power);
			vm = new CondorVM(i, userId, generatedMips, pesNumber, ram, bw, size, power * 10, vmm + i,
					new CloudletSchedulerSpaceShared());
			if (generatedMips > fastestMips) {
				fastestMips = generatedMips;
				fastestVm = vm;
			}
			list.add(vm);
		}

		// 将最快的虚拟机保存下来
		Parameters.setFastestVM(fastestVm);
		return list;
	}

}
