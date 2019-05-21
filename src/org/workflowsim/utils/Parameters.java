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
package org.workflowsim.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;

import com.weiyu.experiment.domain.Event;
import com.weiyu.experiment.domain.SortedVM;

/**
 * This class includes most parameters a user can specify in a configuration
 * file
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class Parameters {
	
	public enum DeadlineLevel{
		D1(1),D2(2),D3(3),D4(4),D5(5),D6(6),D7(7),D8(8),D9(9),D10(10);
		public final int value;
		private DeadlineLevel(int value){
			this.value = value;
		}
	}
	
	public enum WorkflowSequeningMethod{
		EDF, SSF, SWF
	}
	
	public enum RankMethod{
		UPWARDRANK, DOWNWARDRANK, MERGEDRANK
	}
	
	public enum TSTMethod{
		TST, NOTST
	}
	
	public enum VNDMethod{
		HEFT, LS, VND, NOVND
	}

    
    /*
     * Scheduling Algorithm (Local Scheduling Algorithm)
     */

    public enum SchedulingAlgorithm {

        MAXMIN, MINMIN, MCT, DATA, 
        STATIC, FCFS, ROUNDROBIN, INVALID
    }
    
    /**
     * Planning Algorithm (Global Scheduling Algorithm)
     * 
     */
    public enum PlanningAlgorithm{
        INVALID, RANDOM, HEFT, DHEFT,EECE
    }
    
    /**
     * File Type
     */
    public enum FileType{
        NONE(0), INPUT(1), OUTPUT(2);
        public final int value;
        private FileType(int fType){
            this.value = fType;
        }
    }
    
    /**
     * File Type
     */
    public enum ClassType{
        STAGE_IN(1), COMPUTE(2), STAGE_OUT(3), CLEAN_UP(4);
        public final int value;
        private ClassType(int cType){
            this.value = cType;
        }
    }
    
    /**
     * The cost model
     * DATACENTER: specify the cost per data center
     * VM: specify the cost per VM
     */
    public enum CostModel{
        DATACENTER(1), VM(2);
        public final int value;
        private CostModel(int model){
            this.value = model;
        }
    }
    
    /** 
     * Source Host (submit host)
     */
    public static String SOURCE = "source";
    
    public static final int BASE = 0;
    
    private static RankMethod rankMethod;
    
    private static WorkflowSequeningMethod workflowSequeningMethod;
    
    private static DeadlineLevel deadlineLevel;
    /**
     * Scheduling mode
     */
    private static SchedulingAlgorithm schedulingAlgorithm;
    
    /**
     * Planning mode
     */
    private static PlanningAlgorithm planningAlgorithm;
    
    
    public static TSTMethod tstMethod;
    
    public static VNDMethod vndMethod;
    
    /**
     * Reducer mode
     */
    private static String reduceMethod;
    /**
     * Number of vms available
     */
    private static int vmNum;
    
    /**
     * The physical path to DAX file
     */
    private static String daxPath;
    
    /**
     * The physical path to DAX files
     */
    private static List<String> daxPaths;
    /**
     * The physical path to runtime file In the runtime file, please use format
     * as below ID1 1.0 ID2 2.0 ... This is optional, if you have specified task
     * runtime in DAX then you don't need to specify this file
     */
    private static String runtimePath;
    /**
     * The physical path to datasize file In the datasize file, please use
     * format as below DATA1 1000 DATA2 2000 ... This is optional, if you have
     * specified datasize in DAX then you don't need to specify this file
     */
    private static String datasizePath;
    /**
     * Version number
     */
    private static final String version = "1.1.0";
    /**
     * Note information
     */
    private static final String note = " supports planning algorithm at Nov 9, 2013";
    /**
     * Overhead parameters
     */
    private static OverheadParameters oParams;
    /**
     * Clustering parameters
     */
    private static ClusteringParameters cParams;
    /**
     * Deadline of a workflow
     */
    private static double deadline;
    
    /**
     * the bandwidth from one vm to one vm
     */
    private static double[][] bandwidths;
    
    public static double[][] elecPrices = {
    		{0.15, 0.07, 0.10, 0.11, 0.13, 0.18, 0.24, 0.21, 0.19, 0.08},
    		{0.10, 0.08, 0.09, 0.12, 0.13, 0.19, 0.24, 0.21, 0.18, 0.08},
    		{0.11, 0.09, 0.11, 0.12, 0.15, 0.18, 0.19, 0.21, 0.18, 0.09},
    		{0.11, 0.07, 0.11, 0.12, 0.15, 0.18, 0.15, 0.21, 0.18, 0.09},
    		{0.19, 0.13, 0.09, 0.12, 0.17, 0.18, 0.15, 0.16, 0.19, 0.13},
    		{0.22, 0.14, 0.09, 0.22, 0.19, 0.21, 0.16, 0.16, 0.19, 0.13},
    		{0.19, 0.13, 0.10, 0.22, 0.21, 0.22, 0.16, 0.21, 0.18, 0.13},
    		{0.11, 0.13, 0.10, 0.22, 0.21, 0.25, 0.08, 0.21, 0.18, 0.13},
    		{0.12, 0.12, 0.15, 0.22, 0.21, 0.25, 0.08, 0.21, 0.18, 0.11},
    		{0.07, 0.12, 0.15, 0.22, 0.18, 0.16, 0.08, 0.21, 0.18, 0.11},
    		{0.08, 0.09, 0.15, 0.19, 0.18, 0.16, 0.08, 0.21, 0.19, 0.09},
    		{0.10, 0.09, 0.13, 0.18, 0.17, 0.17, 0.16, 0.19, 0.19, 0.09},
    		{0.14, 0.09, 0.12, 0.15, 0.13, 0.18, 0.16, 0.19, 0.19, 0.09,}
	};
    
    public static int[] hostNumberList = {150, 200, 90, 135, 175, 85, 110, 70, 200, 180};
    
    /**
     * 每个工作流应用到达的时间点
     */
    public static List<Integer> timePoints = new ArrayList<>();
    
    /**
     * 每个工作流应用本地数据的位置
     */
    public static List<Integer> inputDataLocations = new ArrayList<>();
    
    /**
     * 每个工作流应用对应的松弛时间，在0-1之间，跟每个应用的最早完成时间成比例
     */
    public static List<Integer> randomWsts = new ArrayList<>();
    /**
     * 每个数据中心对象对应的下标
     */
    public static Map<Integer, Integer> dcToIndex = new HashMap<>();
    
    /**
     * 每个数据中心对应的排序后的虚拟机
     */
    public static Map<Integer, List<SortedVM>> dcToSortedVMs = new HashMap<>();
    
    /**
     * VND之前的电费价格
     */
    public static double elecCostForAllWorkflowsBeforeVND;
    
    /**
     * VND之后的电费价格
     */
    public static double elecCostForAllWorkflowsAfterVND;
    
    /**
     * 数据中心内的带宽
     */
    private static double bandwidthInDC;
    
    /**
     * 数据中心之间的带宽
     */
    private static double bandwidthBetweenDC;
    
    /**
     * The maximum depth. It is inited manually and used in FailureGenerator
     */
    private static int maxDepth;
    
    /**
     * Invalid String
     */
    private static final String INVALID = "Invalid";
    
    /**
     * The scale of runtime. Multiple runtime by this
     */
    private static double runtime_scale = 1.0;
    
    /**
     * The default cost model is based on datacenter, similar to CloudSim
     */
    private static CostModel costModel = CostModel.DATACENTER;
    
    /**
     * 保存数据中心列表
     */
    private static List<WorkflowDatacenter> datacenterList = null;
    
    /**
     * 数据中心ID跟数据中心的Host的一一对应关系
     */
    private static Map<Integer, List<CondorVM>> dcToVMs = null;
    
    
    /**
     * 保存新建的VM列表
     */
    private static List<CondorVM> vmList = null;
    
    /**
     * 设置工作流应用截止时间的比例
     */
    private static double deadlineRatio = 0.0;
    
    
    
    private static CondorVM fastestVM = null;
    
    private static List<Integer> arriveTimeList = null;
    
    private static List<Double> workflowSizes = null;
    
    private static List<List<Task>> workflowList = null;
    
    private static List<List<Double>> deadlineRatioList = null;
    
    private static List<Double> wstList = null;
    
    private static List<Double> deadlineList = null;
    
    private static List<Map<Task, Double>> computationTimesList = null;
    
    private static List<Map<Task, Map<CondorVM, Double>>> computationCostsList = null;
    
    private static List<Map<Task, Double>> originalDataTransmissionTimesList = null;
    
    private static List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList = null;
    
    private static List<Map<Task, Double>> estsForWSTList = null;
    private static List<Map<Task, Double>> eftsForWSTList = null;
    private static Map<Integer,Map<Task, Double>> estsForSubdeadlineList = new HashMap<>();
    private static Map<Integer,Map<Task, Double>> eftsForSubdeadlineList = new HashMap<>();
    
    private static Map<Integer, Map<Task, Double>> subdeadlineList = new HashMap<>();
    
    private static List<Double> lastEFTForWSTList = null;
    
    private static Map<Integer, Double> lastEFTForSubdeadlineList = new HashMap<>();
    
    private static Map<Integer, Double> totalElectricityCostList = new HashMap<>();
    
    private static List<Map<Integer, List<Event>>> schedulesList = new ArrayList<>();
    
    public static List<Integer> maximalDepthList = new ArrayList<Integer>();
    
    public static double kmax;
    
    public static double beta;
    
    
    
    //private static List<List<TaskRank>> taskRankList = null;
    
    public static void clear(){
    	schedulesList.clear();
    	timePoints.clear();
    	inputDataLocations.clear();
    	dcToSortedVMs.clear();
    	dcToIndex.clear();
    	randomWsts.clear();
    	//dcToVMs这个变量赋值为null，便于垃圾回收器回收
    	dcToVMs = null;
    	if(deadlineList != null && !deadlineList.isEmpty()){
    		deadlineList.clear();
    	}
    	computationTimesList = null;
    	originalDataTransmissionTimesList = null;
    	generatedDataTransmissionTimesList = null;
    }
    
    
    
    /**
     * A static function so that you can specify them in any place
     *
     * @param vm, the number of vms
     * @param dax, the DAX path
     * @param runtime, optional, the runtime file path
     * @param datasize, optional, the datasize file path
     * @param op, overhead parameters
     * @param cp, clustering parameters
     * @param scheduler, scheduling mode
     * @param planner, planning mode
     * @param rMethod , reducer mode
     * @param dl, deadline
     */
    public static void init(
            int vm, String dax, String runtime, String datasize,
            OverheadParameters op, ClusteringParameters cp,
            SchedulingAlgorithm scheduler, PlanningAlgorithm planner, String rMethod,
            long dl) {

        cParams = cp;
        vmNum = vm;
        daxPath = dax;
        runtimePath = runtime;
        datasizePath = datasize;

        oParams = op;
        schedulingAlgorithm = scheduler;
        planningAlgorithm = planner;
        reduceMethod = rMethod;
        deadline = dl;
        maxDepth = 0;
    }
    
    /**
     * A static function so that you can specify them in any place
     *
     * @param vm, the number of vms
     * @param dax, the list of DAX paths 
     * @param runtime, optional, the runtime file path
     * @param datasize, optional, the datasize file path
     * @param op, overhead parameters
     * @param cp, clustering parameters
     * @param scheduler, scheduling mode
     * @param planner, planning mode
     * @param rMethod , reducer mode
     * @param dl, deadline of a workflow
     */
    public static void init(
            int vm, List<String> dax, String runtime, String datasize,
            OverheadParameters op, ClusteringParameters cp,
            SchedulingAlgorithm scheduler, PlanningAlgorithm planner, String rMethod,
            long dl) {

        cParams = cp;
        vmNum = vm;
        daxPaths = dax;
        runtimePath = runtime;
        datasizePath = datasize;

        oParams = op;
        schedulingAlgorithm = scheduler;
        planningAlgorithm = planner;
        reduceMethod = rMethod;
        deadline = dl;
        maxDepth = 0;
    }

    /**
     * Gets the overhead parameters
     *
     * @return the overhead parameters
     * @pre $none
     * @post $none
     */
    public static OverheadParameters getOverheadParams() {
        return oParams;
    }

    

    /**
     * Gets the reducer mode
     *
     * @return the reducer
     * @pre $none
     * @post $none
     */
    public static String getReduceMethod() {
        if(reduceMethod!=null){
            return reduceMethod;
        }else{
            return INVALID;
        }
    }

   

    /**
     * Gets the DAX path
     *
     * @return the DAX path
     * @pre $none
     * @post $none
     */
    public static String getDaxPath() {
        return daxPath;
    }

    /**
     * Gets the runtime file path
     *
     * @return the runtime file path
     * @pre $none
     * @post $none
     */
    public static String getRuntimePath() {
        return runtimePath;
    }

    /**
     * Gets the data size path
     *
     * @return the datasize file path
     * @pre $none
     * @post $none
     */
    public static String getDatasizePath() {
        return datasizePath;
    }

    
    /**
     * Gets the vm number
     *
     * @return the vm number
     * @pre $none
     * @post $none
     */
    public static int getVmNum() {
        return vmNum;
    }

    
    /**
     * Gets the cost model
     * 
     * @return costModel
     */
    public static CostModel getCostModel(){
        return costModel;
    }
    
    /**
     * Sets the vm number
     *
     * @param num
     */
    public static void setVmNum(int num) {
        vmNum = num;
    }

    /**
     * Gets the clustering parameters
     *
     * @return the clustering parameters
     */
    public static ClusteringParameters getClusteringParameters() {
        return cParams;
    }

    /**
     * Gets the scheduling method
     *
     * @return the scheduling method
     */
    public static SchedulingAlgorithm getSchedulingAlgorithm() {
        return schedulingAlgorithm;
    }
    
    /**
     * Gets the planning method
     * @return the planning method
     * 
     */
    public static PlanningAlgorithm getPlanningAlgorithm() {
        return planningAlgorithm;
    }
    /**
     * Gets the version
     * @return version
     */
    public static String getVersion(){
        return version;
    }

    public static void printVersion() {
        Log.printLine("WorkflowSim Version: " + version);
        Log.printLine("Change Note: " + note);
    }
    /*
     * Gets the deadline
     */
    public static double getDeadline(){
    	return deadline;
    }
    
    public static void setDeadline(double dd){
    	deadline = dd;
    }
    
    /**
     * Gets the maximum depth
     * @return the maxDepth
     */
    public static int getMaxDepth(){
        return maxDepth;
    }
    
    /**
     * Sets the maximum depth
     * @param depth the maxDepth
     */
    public static void setMaxDepth(int depth){
        maxDepth = depth;
    }
    
    /**
     * Sets the runtime scale
     * @param scale 
     */
    public static void setRuntimeScale(double scale){
        runtime_scale = scale;
    }
    
    /**
     * Sets the cost model
     * @param model
     */
    public static void setCostModel(CostModel model){
        costModel = model;
    }
    
    /**
     * Gets the runtime scale
     * @return 
     */
    public static double getRuntimeScale(){
        return runtime_scale;
    }
    
    /**
     * Gets the dax paths
     * @return 
     */
    public static List<String> getDAXPaths() {
        return daxPaths;
    }

	public static Map<Integer, List<CondorVM>> getDcToVMs() {
		return dcToVMs;
	}

	public static void setDcToVMs(Map<Integer, List<CondorVM>> dcToVMs) {
		Parameters.dcToVMs = dcToVMs;
	}

	public static double getBandwidthInDC() {
		return bandwidthInDC;
	}

	public static void setBandwidthInDC(double bandwidthInDC) {
		Parameters.bandwidthInDC = bandwidthInDC;
	}

	public static double getBandwidthBetweenDC() {
		return bandwidthBetweenDC;
	}

	public static void setBandwidthBetweenDC(double bandwidthBetweenDC) {
		Parameters.bandwidthBetweenDC = bandwidthBetweenDC;
	}

	public static List<CondorVM> getVmList() {
		return vmList;
	}

	public static void setVmList(List<CondorVM> vmList) {
		Parameters.vmList = vmList;
	}

	public static double getDeadlineRatio() {
		return deadlineRatio;
	}

	public static void setDeadlineRatio(double deadlineRatio) {
		Parameters.deadlineRatio = deadlineRatio;
	}

	public static CondorVM getFastestVM() {
		return fastestVM;
	}

	public static void setFastestVM(CondorVM fastestVM) {
		Parameters.fastestVM = fastestVM;
	}

	public static List<Double> getWorkflowSizes() {
		return workflowSizes;
	}

	public static void setWorkflowSizes(List<Double> workflowSizes) {
		Parameters.workflowSizes = workflowSizes;
	}

	public static List<List<Task>> getWorkflowList() {
		return workflowList;
	}

	public static void setWorkflowList(List<List<Task>> workflowList) {
		Parameters.workflowList = workflowList;
	}

	public static List<Integer> getArriveTimeList() {
		return arriveTimeList;
	}

	public static void setArriveTimeList(List<Integer> arriveTimeList) {
		Parameters.arriveTimeList = arriveTimeList;
	}
	
	

//	public static List<List<Double>> getWstList() {
//		return wstList;
//	}
//
//	public static void setWstList(List<List<Double>> wstList) {
//		Parameters.wstList = wstList;
//	}
//
//	public static List<List<Double>> getDeadlineList() {
//		return deadlineList;
//	}
//
//	public static void setDeadlineList(List<List<Double>> deadlineList) {
//		Parameters.deadlineList = deadlineList;
//	}

	public static List<Double> getWstList() {
		return wstList;
	}

	public static void setWstList(List<Double> wstList) {
		Parameters.wstList = wstList;
	}

	public static List<Double> getDeadlineList() {
		return deadlineList;
	}

	public static void setDeadlineList(List<Double> deadlineList) {
		Parameters.deadlineList = deadlineList;
	}

	public static List<Map<Task, Double>> getComputationTimesList() {
		return computationTimesList;
	}

	public static void setComputationTimesList(List<Map<Task, Double>> computationTimesList) {
		Parameters.computationTimesList = computationTimesList;
	}

	public static List<Map<Task, Double>> getOriginalDataTransmissionTimesList() {
		return originalDataTransmissionTimesList;
	}

	public static void setOriginalDataTransmissionTimesList(List<Map<Task, Double>> originalDataTransmissionTimesList) {
		Parameters.originalDataTransmissionTimesList = originalDataTransmissionTimesList;
	}

	public static List<Map<Task, Map<Task, Double>>> getGeneratedDataTransmissionTimesList() {
		return generatedDataTransmissionTimesList;
	}

	public static void setGeneratedDataTransmissionTimesList(
			List<Map<Task, Map<Task, Double>>> generatedDataTransmissionTimesList) {
		Parameters.generatedDataTransmissionTimesList = generatedDataTransmissionTimesList;
	}

	public static List<Map<Task, Map<CondorVM, Double>>> getComputationCostsList() {
		return computationCostsList;
	}

	public static void setComputationCostsList(List<Map<Task, Map<CondorVM, Double>>> computationCostsList) {
		Parameters.computationCostsList = computationCostsList;
	}

	public static List<Map<Task, Double>> getEstsForWSTList() {
		return estsForWSTList;
	}

	public static void setEstsForWSTList(List<Map<Task, Double>> estsForWSTList) {
		Parameters.estsForWSTList = estsForWSTList;
	}

	public static List<Map<Task, Double>> getEftsForWSTList() {
		return eftsForWSTList;
	}

	public static void setEftsForWSTList(List<Map<Task, Double>> eftsForWSTList) {
		Parameters.eftsForWSTList = eftsForWSTList;
	}
	
	public static Map<Integer, Map<Task, Double>> getEstsForSubdeadlineList() {
		return estsForSubdeadlineList;
	}

	public static void setEstsForSubdeadlineList(Map<Integer, Map<Task, Double>> estsForSubdeadlineList) {
		Parameters.estsForSubdeadlineList = estsForSubdeadlineList;
	}

	public static Map<Integer, Map<Task, Double>> getEftsForSubdeadlineList() {
		return eftsForSubdeadlineList;
	}

	public static void setEftsForSubdeadlineList(Map<Integer, Map<Task, Double>> eftsForSubdeadlineList) {
		Parameters.eftsForSubdeadlineList = eftsForSubdeadlineList;
	}

	public static List<Double> getLastEFTForWSTList() {
		return lastEFTForWSTList;
	}

	public static void setLastEFTForWSTList(List<Double> lastEFTForWSTList) {
		Parameters.lastEFTForWSTList = lastEFTForWSTList;
	}
	
	public static Map<Integer, Double> getLastEFTForSubdeadlineList() {
		return lastEFTForSubdeadlineList;
	}

	public static void setLastEFTForSubdeadlineList(Map<Integer, Double> lastEFTForSubdeadlineList) {
		Parameters.lastEFTForSubdeadlineList = lastEFTForSubdeadlineList;
	}

	public static List<List<Double>> getDeadlineRatioList() {
		return deadlineRatioList;
	}

	public static void setDeadlineRatioList(List<List<Double>> deadlineRatioList) {
		Parameters.deadlineRatioList = deadlineRatioList;
	}
	
	public static Map<Integer, Map<Task, Double>> getSubdeadlineList() {
		return subdeadlineList;
	}

	public static void setSubdeadlineList(Map<Integer, Map<Task, Double>> subdeadlineList) {
		Parameters.subdeadlineList = subdeadlineList;
	}

	public static RankMethod getRankMethod() {
		return rankMethod;
	}

	public static void setRankMethod(RankMethod rankMethod) {
		Parameters.rankMethod = rankMethod;
	}

	public static WorkflowSequeningMethod getWorkflowSequeningMethod() {
		return workflowSequeningMethod;
	}

	public static void setWorkflowSequeningMethod(WorkflowSequeningMethod workflowSequeningMethod) {
		Parameters.workflowSequeningMethod = workflowSequeningMethod;
	}

	public static DeadlineLevel getDeadlineLevel() {
		return deadlineLevel;
	}

	public static void setDeadlineLevel(DeadlineLevel deadlineLevel) {
		Parameters.deadlineLevel = deadlineLevel;
	}

	public static List<WorkflowDatacenter> getDatacenterList() {
		return datacenterList;
	}

	public static void setDatacenterList(List<WorkflowDatacenter> dcList) {
		Parameters.datacenterList = dcList;
	}

	public static Map<Integer, Double> getTotalElectricityCostList() {
		return totalElectricityCostList;
	}

	public static void setTotalElectricityCostList(Map<Integer, Double> totalElectricityCostList) {
		Parameters.totalElectricityCostList = totalElectricityCostList;
	}

	public static List<Map<Integer, List<Event>>> getSchedulesList() {
		return schedulesList;
	}

	public static void setSchedulesList(List<Map<Integer, List<Event>>> schedulesList) {
		Parameters.schedulesList = schedulesList;
	}

	public static List<Integer> getMaximalDepthList() {
		return maximalDepthList;
	}

	public static void setMaximalDepthList(List<Integer> maximalDepthList) {
		Parameters.maximalDepthList = maximalDepthList;
	}
	
}
