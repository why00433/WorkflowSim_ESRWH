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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
import org.workflowsim.utils.ReplicaCatalog;

import com.weiyu.experiment.utils.Print;

/**
 * This WorkflowSimExample creates a workflow planner, a workflow engine, and
 * two schedulers, two data centers and 20 vms. All the configuration of
 * CloudSim is done in WorkflowSimExamplex.java All the configuration of
 * WorkflowSim is done in the config.txt that must be specified in argument of
 * this WorkflowSimExample. The argument should have at least: "-p
 * path_to_config.txt"
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class WorkflowSimMultipleClusterExample1{
	
	protected static List<CondorVM> createVM(int userId, int vms) {

		// Creates a container to store VMs. This list is passed to the broker
		// later
		LinkedList<CondorVM> list = new LinkedList<>();

		// VM Parameters
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VMs
		// CondorVM[] vm = new CondorVM[vms];
		CondorVM vm = null;
		for (int i = 0; i < vms; i++) {
			// 生成随机的虚拟机处理速度
			Random random = new Random();
			int ratio = 1 + random.nextInt(10);

			int generatedMips = (int) (ratio * mips / 10);
			System.out.println("比例：" + ratio + " 速度：" + generatedMips);
			vm = new CondorVM(i, userId, generatedMips, pesNumber, ram, bw, size, vmm + i,
					new CloudletSchedulerSpaceShared());
			list.add(vm);
		}
		return list;
	}

	protected static List<CondorVM> createVM(int userId, int vms, int vmIdBase) {

		// Creates a container to store VMs. This list is passed to the broker
		// later
		LinkedList<CondorVM> list = new LinkedList<>();

		// VM Parameters
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VMs
		// CondorVM[] vm = new CondorVM[vms];
		CondorVM vm = null;
		for (int i = 0; i < vms; i++) {
			// 生成随机的虚拟机处理速度
			Random random = new Random();
			int ratio = 1 + random.nextInt(10);

			int generatedMips = (int) (ratio * mips / 10);
			System.out.println("比例：" + ratio + " 速度：" + generatedMips);
			vm = new CondorVM(vmIdBase + i, userId, generatedMips, pesNumber, ram, bw, size, vmm + i,
					new CloudletSchedulerSpaceShared());
			list.add(vm);
		}
		return list;
	}

	////////////////////////// STATIC METHODS ///////////////////////
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		try {
			// First step: Initialize the WorkflowSim package.

			/**
			 * However, the exact number of vms may not necessarily be vmNum If
			 * the data center or the host doesn't have sufficient resources the
			 * exact vmNum would be smaller than that. Take care.
			 */
			int vmNum = 20;// number of vms;
			/**
			 * Should change this based on real physical path
			 */
			String daxPath = "E:/GitHub/WorkflowSim-1.0/config/dax/Montage_25.xml";
			File daxFile = new File(daxPath);
			if (!daxFile.exists()) {
				Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
				return;
			}

			/**
			 * Since we are using MINMIN scheduling algorithm, the planning
			 * algorithm should be INVALID such that the planner would not
			 * override the result of the scheduler
			 */
			Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.INVALID;
			Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.HEFT;
			ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

			/**
			 * No overheads
			 */
			OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);

			/**
			 * No Clustering
			 */
			ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
			ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

			/**
			 * Initialize static parameters
			 */
			Parameters.init(vmNum, daxPath, null, null, op, cp, sch_method, pln_method, null, 0);
			ReplicaCatalog.init(file_system);

			// before creating any entities.
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// 创建数据中心，分别创建5，10,15,30,50个数据中心
			List<WorkflowDatacenter> dcList = createDatacenters(2);
//			WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

			/**
			 * Create a WorkflowPlanner with one scheduler.
			 */
			WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
			/**
			 * Create a WorkflowEngine. Attach it to the workflow planner
			 */
			WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
			/**
			 * Create two list of VMs. The trick is that make sure all vmId is
			 * unique so we need to index vm from a base (in this case
			 * Parameters.getVmNum/2 for the second vmlist1).
			 */
			List<CondorVM> vmList = createVM(wfEngine.getSchedulerId(0), vmNum);

			/**
			 * Submits these lists of vms to this WorkflowEngine.
			 */
			wfEngine.submitVmList(vmList, 0);

			/**
			 * Binds the data centers with the scheduler id. This scheduler
			 * controls two data centers. Make sure your data center is not very
			 * big otherwise all the vms will be allocated to the first
			 * available data center In the future, the vm allocation algorithm
			 * should be improved.
			 */
			for(int i = 0; i < dcList.size();i++){
				wfEngine.bindSchedulerDatacenter(dcList.get(i).getId(), 0);
			}
			
			for(int i = 0; i < dcList.size();i++){
				wfEngine.submitDatacenters(dcList.get(i),0);
			}

			CloudSim.startSimulation();
			List<Job> outputList0 = wfEngine.getJobsReceivedList();
			CloudSim.stopSimulation();
			Print.printJobList(outputList0);

		} catch (Exception e) {
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}


	/*protected static WorkflowDatacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 第一步：先创建CPU核的数量We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        //第二步：创建主机Host，每个数据中心假设由1-10个主机
        int hostNumber = 0;
        Random random = new Random();
        hostNumber = 1 + random.nextInt(10);
        for (int i = 1; i <= hostNumber; i++) {
            List<Pe> peList1 = new ArrayList<>();
            
            //由于是异构的，主机的处理速度也是不一样的，因此以0-1之内的随机数来生成随机的速度
            int mips = 2000;
            random = new Random();
            int ratio = 1 + random.nextInt(10);
            
            mips = mips * ratio / 10;  
            System.out.println("主机ID：" + i + "- 处理速度：" + mips);
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            peList1.add(new Pe(1, new PeProvisionerSimple(mips)));

            int ram = 2048; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 10000;
            Host host = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1,
                    new VmSchedulerTimeShared(peList1));
            hostList.add(host); // This is our first machine
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a cluster storage object.
        *//**
         * The bandwidth between data centers.
         *//*
        double interBandwidth = 1.5e7;// the number comes from the futuregrid site, you can specify your bw
        *//**
         * The bandwidth within a data center.
         *//*
        double intraBandwidth = interBandwidth * 2;
        try {
            ClusterStorage s1 = new ClusterStorage(name, 1e12);
            // The bandwidth within a data center
            s1.setBandwidth("in", intraBandwidth);
            // The bandwidth to the source site 
            s1.setBandwidth("between", interBandwidth);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }*/
	
	
	protected static List<WorkflowDatacenter> createDatacenters(int dcNum) {
		List<WorkflowDatacenter> datacenters = new ArrayList<>();
		
		WorkflowDatacenter datacenter = null;
		for(int i = 0; i < dcNum;i++){
			datacenter = createDatacenter("DC" + (i+1));
			datacenters.add(datacenter);
		}
        return datacenters;
    }

	protected static WorkflowDatacenter createDatacenter(String name) {
		// Here are the steps needed to create a PowerDatacenter:
        // 第一步：先创建CPU核的数量We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        //第二步：创建主机Host，每个数据中心假设由1-10个主机
        int hostNumber = 0;
        Random random = new Random();
        hostNumber = 1 + random.nextInt(10);
        for (int i = 1; i <= hostNumber; i++) {
            List<Pe> peList1 = new ArrayList<>();
            
            //由于是异构的，主机的处理速度也是不一样的，因此以0-1之内的随机数来生成随机的速度
            int mips = 2000;
            random = new Random();
            int ratio = 1 + random.nextInt(10);
            
            mips = mips * ratio / 10;  
            System.out.println("主机ID：" + i + "- 处理速度：" + mips);
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            peList1.add(new Pe(1, new PeProvisionerSimple(mips)));

            int ram = 8192; //host memory (MB)
            long storage = 500000; //host storage
            int bw = 10000;
            Host host = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1,
                    new VmSchedulerTimeShared(peList1));
            hostList.add(host); // This is our first machine
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a cluster storage object.
        /**
         * The bandwidth within a data center.
         */
        double intraBandwidth = 1.5e7;
        
        /**
         * The bandwidth between data centers.
         */
        double interBandwidth = intraBandwidth * 10;// the number comes from the futuregrid site, you can specify your bw
        try {
            ClusterStorage s1 = new ClusterStorage(name, 1e12);
            // The bandwidth within a data center
            s1.setBandwidth("in", intraBandwidth);
            // The bandwidth to the source site 
            s1.setBandwidth("between", interBandwidth);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
            List<Storage> returnedStorageList = datacenter.getStorageList();
            for (Storage storage : returnedStorageList) {
            	System.out.println(storage);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
	}
}
