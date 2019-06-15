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
 * ��С����Ƶ��Դ�ܺĵĹ�����ʵ�����в���У��
 * 1����VND���㷨�Ͳ���VND�㷨֮��Ľ��жԱȡ� 2���ڲ�ͬ�Ľ�ֹ������½��бȽ�
 * 3���ڲ�ͬ�Ĺ�����ʵ����ģ������½��бȽ� 4���ڲ�ͬ������������ģ�Խ��бȽ�
 *
 * @author Wei Yu
 *
 */
public class ESRWHAllocatingCalibration {

    public static void main(String[] args) throws Exception {

        // ���ݴ��·��
        List<String> daxPaths = null;
        String prefixPath = "F:/Experiment/Montage/";

//      int[] workflowNumbers = { 20, 40};
        int[] taskNumbers = {50, 100, 150, 200, 250};

        // �������򷽷�
        Parameters.RankMethod[] rankMethods = { Parameters.RankMethod.UPWARDRANK, Parameters.RankMethod.DOWNWARDRANK,
                Parameters.RankMethod.HybridRank };

        // ��Դ���䷽��
        Parameters.AllocatingMethod[] allocatingMethods = { Parameters.AllocatingMethod.VS1, Parameters.AllocatingMethod.VS2,
                Parameters.AllocatingMethod.Random};

        // �����ͬ�ȼ����ɳ�ʱ��
        Parameters.DeadlineLevel[] deadlinelevels = { Parameters.DeadlineLevel.D1, Parameters.DeadlineLevel.D2,
                Parameters.DeadlineLevel.D3, Parameters.DeadlineLevel.D4, Parameters.DeadlineLevel.D5};

        // ������ͬ�ɿ���Լ��
        Parameters.ReliabilityLevel[] reliabilityLevels = {Parameters.ReliabilityLevel.R1, Parameters.ReliabilityLevel.R2,
                Parameters.ReliabilityLevel.R3};


        for (int  i = 0; i < taskNumbers.length; i++) {
            for (int k = 0; k < 10; k++) {
                //ʵ�����·��
                daxPaths = new ArrayList<>();
                String xmlPath = prefixPath + "Montage" + "_" + taskNumbers[i] + "_" + k + ".xml";
                daxPaths.add(xmlPath);


                // �жϸ��ļ��Ƿ����
                File daxFile = null;
                for (String daxPath : daxPaths) {
                    daxFile = new File(daxPath);
                    if (!daxFile.exists()) {
                        Log.printLine(
                                "Warning: Please replace daxPath with the physical path in your working environment!");
                    }
                }

                Parameters.clear();


                // ��ÿ��������Ӧ�ó�ʼ��һ���������ݵĴ��λ��
//                for (int item = 0; item < daxPaths.size(); item++) {
//                    // �����������Ԥ����Ϊ5000��
//                    int randomVMIndex = random.nextInt(500);
//                    Parameters.inputDataLocations.add(randomVMIndex);
//                }

                // ��ÿ��������Ӧ������һ��������ɳ�ʱ��
//                for (int item = 0; item < daxPaths.size(); item++) {
//                    // �����������Ԥ����Ϊ5000��
//                    int randomWst = 1 + random.nextInt(10);
//                    Parameters.randomWsts.add(randomWst);
//                }

                List<SimulationResult> results = new ArrayList<>();

                // ��Բ�ͬ�Ĳ�������ʵ��
                for(int a = 0; a < deadlinelevels.length; a++){
                    for(int b = 0; b < reliabilityLevels.length; b++){
                        for (int l = 0; l < rankMethods.length; l++) {

                            List<SimulationResult> simulationResults = new ArrayList<>();

                            for (int m = 0; m <allocatingMethods.length; m++) {

                                SimulationResult result = new SimulationResult();
                                long beginTime = System.currentTimeMillis();
                                // ���÷��溯��
                                doSimulations(daxPaths, rankMethods[l], allocatingMethods[m], deadlinelevels[a], reliabilityLevels[b]);
                                long currentTime = System.currentTimeMillis();

                                // ��Ϊ��λ
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

                            // ���������Ӧ����������������������µĸ�������RPDֵ
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
     * ���õ��Ľ��д�뵽TXT�ĵ���
     *
     * @param results
     * @throws IOException
     */
    private static void exportToTxt(List<SimulationResult> results) throws IOException {
        String filePath = "F:/Experiment/ESRWH_AllocatingCalibration_20190602.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();// �������򴴽�
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));// true,��׷��д��text�ı�
        for (SimulationResult result : results) {
            writer.write(result.toString());
            writer.write("\r\n");// ����
        }

        writer.flush();
        if (null != writer) {
            writer.close();
        }
    }

    /**
     * ��Լ���õ��Ľ������ý����RPDֵ
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
     * ����ʵ������庯��
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

        // �������ĵ����� int dcNum = 10;
        // �������������
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
         * ����������
         */
        wfEngine.bindSchedulerDatacenter(1, 0);



        // ����һ��ͳһ�����������ڵ�PM���д���Ĵ���
        double interBandwidth = 1.0e8;
        Parameters.setBandwidthInDC(interBandwidth);

        CloudSim.startSimulation();
        List<Job> outputList = wfEngine.getJobsReceivedList();
        CloudSim.stopSimulation();
    }



    /**
     * ���������VM������������Ӧ�����������
     *
     * @param userId
     * @param vms
     * @return
     */
    protected static List<CondorVM> createVM(int userId, int vms) {
        // Creates a container to store VMs. This list is passed to the broker
        // later
        LinkedList<CondorVM> list = new LinkedList<>();

        // VM Parameters ûʲô��
        long size = 1000; // image size (MB)
        int ram = 512; // vm memory (MB)
        double mips = 1000.0;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        CondorVM vm = null;


        double fastestMips = 0.0;
        for (int i = 0; i < vms; i++) {
            // �������������������ٶ�
            Random random = new Random();
            int j = random.nextInt(3);

            // ����ÿ̨������Ĺ���
            double powers[] = {62.0,25.0,95.0};
            double generatedMips[] = {3000, 2000, 5200};

            Double frequency1[] = {1.0, 1.8/2.0, 1.6/2.0, 1.4/2.0, 1.2/2.0, 1.0/2.0, 0.8/2.0};
            Double frequency2[] = {1.0, 1.6/1.8 ,1.4/1.8, 1.2/1.8, 1.0/1.8, 0.8/1.8};
            Double frequency3[] = {1.0, 2.4/2.6, 2.2/2.6, 2.0/2.6, 1.8/2.6, 1.0/2.6};

            List<List<Double>> frequencyList = new ArrayList<>();
            frequencyList.add(Arrays.asList(frequency1));
            frequencyList.add(Arrays.asList(frequency2));
            frequencyList.add(Arrays.asList(frequency3));

            // System.out.println("������" + ratio + " �ٶȣ�" + generatedMips + "
            // ����Ϊ��" + power);
            vm = new CondorVM(i, userId, generatedMips[j], pesNumber, ram, bw, size, powers[j], vmm + i,
                    new CloudletSchedulerSpaceShared());

            vm.setFrequency(frequencyList.get(j));

            list.add(vm);
        }


        return list;
    }

}

