package com.weiyu.experiment;

/**
 * Created by why on 2019/6/12.
 */
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

import com.weiyu.experiment.domain.SimulationResult;
import com.weiyu.experiment.utils.Print;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.CondorVM;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.*;

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
import org.workflowsim.utils.Parameters.VNDMethod;
import org.workflowsim.utils.Parameters.WorkflowSequeningMethod;
import org.workflowsim.utils.ReplicaCatalog;

import com.weiyu.experiment.domain.SimulationResult;
import com.weiyu.experiment.domain.SortedVM;
import com.weiyu.experiment.utils.Print;
import com.weiyu.experiment.utils.VMAllocationPolicyImpl;

/**
 * 最小化变频资源能耗的工作流实例进行参数校正
 * 1）用VND的算法和不用VND算法之间的进行对比。 2）在不同的截止期情况下进行比较
 * 3）在不同的工作流实例规模的情况下进行比较 4）在不同的任务数量规模性进行比较
 *
 * @author Wei Yu
 *
 */
public class ESRWHAllocatingCalibration {

    public static void main(String[] args) throws Exception {

        // 数据存放路径
        List<String> daxPaths = null;
        String prefixPath = "F:/Experiment/Montage/";

//      int[] workflowNumbers = { 20, 40};
        int[] taskNumbers = {50, 100, 150, 200, 250};

        // 任务排序方法
        Parameters.RankMethod[] rankMethods = { Parameters.RankMethod.UPWARDRANK, Parameters.RankMethod.DOWNWARDRANK,
                Parameters.RankMethod.HybridRank };

        // 资源分配方法
        Parameters.AllocatingMethod[] allocatingMethods = { Parameters.AllocatingMethod.VS1, Parameters.AllocatingMethod.VS2,
                Parameters.AllocatingMethod.Random};

        // 五个不同等级的松弛时间
        Parameters.DeadlineLevel[] deadlinelevels = { Parameters.DeadlineLevel.D1, Parameters.DeadlineLevel.D2,
                Parameters.DeadlineLevel.D3, Parameters.DeadlineLevel.D4, Parameters.DeadlineLevel.D5};

        // 三个不同可靠性约束
        Parameters.ReliabilityLevel[] reliabilityLevels = {Parameters.ReliabilityLevel.R1, Parameters.ReliabilityLevel.R2,
                Parameters.ReliabilityLevel.R3};


        for (int  i = 0; i < taskNumbers.length; i++) {
            for (int k = 0; k < 10; k++) {
                //实例存放路径
                daxPaths = new ArrayList<>();
                String xmlPath = prefixPath + "Montage" + "_" + taskNumbers[i] + "_" + k + ".xml";
                daxPaths.add(xmlPath);


                // 判断该文件是否存在
                File daxFile = null;
                for (String daxPath : daxPaths) {
                    daxFile = new File(daxPath);
                    if (!daxFile.exists()) {
                        Log.printLine(
                                "Warning: Please replace daxPath with the physical path in your working environment!");
                    }
                }

                Parameters.clear();


                // 给每个工作流应用初始化一个本地数据的存放位置
//                for (int item = 0; item < daxPaths.size(); item++) {
//                    // 虚拟机的数量预设置为5000个
//                    int randomVMIndex = random.nextInt(500);
//                    Parameters.inputDataLocations.add(randomVMIndex);
//                }

                // 给每个工作流应用生成一个随机的松弛时间
//                for (int item = 0; item < daxPaths.size(); item++) {
//                    // 虚拟机的数量预设置为5000个
//                    int randomWst = 1 + random.nextInt(10);
//                    Parameters.randomWsts.add(randomWst);
//                }

                List<SimulationResult> results = new ArrayList<>();

                // 针对不同的参数进行实验
                for(int a = 0; a < deadlinelevels.length; a++){
                    for(int b = 0; b < reliabilityLevels.length; b++){
                        for (int l = 0; l < rankMethods.length; l++) {

                            List<SimulationResult> simulationResults = new ArrayList<>();

                            for (int m = 0; m <allocatingMethods.length; m++) {

                                SimulationResult result = new SimulationResult();
                                long beginTime = System.currentTimeMillis();
                                // 调用仿真函数
                                doSimulations(daxPaths, rankMethods[l], allocatingMethods[m], deadlinelevels[a], reliabilityLevels[1]);
                                long currentTime = System.currentTimeMillis();

                                // 秒为单位
                                long runtime = currentTime - beginTime ;
//                        result.setWorkflowNumber(workflowNumbers[i]);
                                result.setTaskNumber(taskNumbers[i]);
                                result.setInstanceNumber(k);
//						result.setRepeatTime(o);
                                result.setRankMethod(rankMethods[l].toString());
                                result.setAllocatingMethod(allocatingMethods[m].toString());
                                result.setDeadlinelevel(deadlinelevels[a].toString());
                                result.setReliabilityLevel(reliabilityLevels[b].toString());
                                result.setTotalEnergy(Parameters.getTotalEnergy());
                                result.setRuntime(runtime);
                                simulationResults.add(result);
                                results.add(result);
                            }

                            // 求出工作流应用数量跟任务数量的组合下的各参数的RPD值
                            calculateRPD(simulationResults);


                        }
                    }
                }

                exportToTxt(results);

            }
            // }
            // }
            // }

        }
    }



    /**
     * 将得到的结果写入到TXT文档中
     *
     * @param results
     * @throws IOException
     */
    private static void exportToTxt(List<SimulationResult> results) throws IOException {
        String filePath = "F:/Experiment/ESRWH_AllocatingCalibration_20190530.txt";
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
        double min = Double.MAX_VALUE;

        for (SimulationResult result : results) {
            if (result.getTotalEnergy() < min)
                min = result.getTotalEnergy();

        }

        for (SimulationResult result : results) {
            double totalE = result.getTotalEnergy();

            result.setRpd((totalE - min) / min * 100);

        }
    }

    /**
     * 仿真实验的主体函数
     *
     * @param daxPaths
     * @param rankMethod
     * @param allocatingMethod
     * @param deadlinelevel
     * @param reliabilityLevel
     * @throws Exception
     */
    protected static void doSimulations(List<String> daxPaths, Parameters.RankMethod rankMethod, Parameters.AllocatingMethod allocatingMethod,
                                        Parameters.DeadlineLevel deadlinelevel, Parameters.ReliabilityLevel reliabilityLevel) throws Exception {
        Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.INVALID;
        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.ESRWH;
        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

        Parameters.setRankMethod(rankMethod);
        Parameters.setAllocatingMethod(allocatingMethod);
        Parameters.setDeadlineLevel(deadlinelevel);
        Parameters.setReliabilityLevel(reliabilityLevel);

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

        // 数据中心的数量 int dcNum = 10;
        // 物理机的总数量
        int vmNum = 3 * 50;


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



        /**
         * Create a WorkflowPlanner with one scheduler.
         */
        WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
        /**
         * Create a WorkflowEngine. Attach it to the workflow planner
         */
        WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
        List<CondorVM> vmList = Parameters.getVmList();

        if (null == vmList || vmList.size() == 0) {
            String vmListPath = "F:/Experiment/VM_20190530.xls";
            vmList = Print.readVMListFromExcel(vmListPath);

            if (null != vmList && vmList.size() != 0){
                for(CondorVM vm : vmList){
                    Double frequency1[] = {1.0, 1.8/2.0, 1.6/2.0, 1.4/2.0, 1.2/2.0, 1.0/2.0, 0.8/2.0};
                    Double frequency2[] = {1.0, 1.6/1.8 ,1.4/1.8, 1.2/1.8, 1.0/1.8, 0.8/1.8};
                    Double frequency3[] = {1.0, 2.4/2.6, 2.2/2.6, 2.0/2.6, 1.8/2.6, 1.0/2.6};

                    List<List<Double>> frequencyList = new ArrayList<>();
                    frequencyList.add(Arrays.asList(frequency1));
                    frequencyList.add(Arrays.asList(frequency2));
                    frequencyList.add(Arrays.asList(frequency3));

                    if(vm.getPower() == 62.0)
                        vm.setFrequency(frequencyList.get(0));
                    else if(vm.getPower() == 25.0)
                        vm.setFrequency(frequencyList.get(1));
                    else
                        vm.setFrequency(frequencyList.get(2));

                }
            }


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


        /**
         * 绑定数据中心
         */
        wfEngine.bindSchedulerDatacenter(1, 0);



        // 设置一个统一的数据中心内的PM进行传输的带宽
        double interBandwidth = 1.0e7;
        Parameters.setBandwidthInDC(interBandwidth);

        CloudSim.startSimulation();
        List<Job> outputList = wfEngine.getJobsReceivedList();
        CloudSim.stopSimulation();
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

        // VM Parameters 没什么用
        long size = 1000; // image size (MB)
        int ram = 512; // vm memory (MB)
        double mips = 1000.0;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        CondorVM vm = null;


        double fastestMips = 0.0;
        for (int i = 0; i < vms; i++) {
            // 生成随机的虚拟机处理速度
            Random random = new Random();
            int j = random.nextInt(3);

            // 设置每台物理机的功率
            double powers[] = {62.0,25.0,95.0};
            double generatedMips[] = {3000, 2000, 5200};

            Double frequency1[] = {1.0, 1.8/2.0, 1.6/2.0, 1.4/2.0, 1.2/2.0, 1.0/2.0, 0.8/2.0};
            Double frequency2[] = {1.0, 1.6/1.8 ,1.4/1.8, 1.2/1.8, 1.0/1.8, 0.8/1.8};
            Double frequency3[] = {1.0, 2.4/2.6, 2.2/2.6, 2.0/2.6, 1.8/2.6, 1.0/2.6};

            List<List<Double>> frequencyList = new ArrayList<>();
            frequencyList.add(Arrays.asList(frequency1));
            frequencyList.add(Arrays.asList(frequency2));
            frequencyList.add(Arrays.asList(frequency3));

            // System.out.println("比例：" + ratio + " 速度：" + generatedMips + "
            // 功率为：" + power);
            vm = new CondorVM(i, userId, generatedMips[j], pesNumber, ram, bw, size, powers[j], vmm + i,
                    new CloudletSchedulerSpaceShared());

            vm.setFrequency(frequencyList.get(j));

            list.add(vm);
        }


        return list;
    }

}

