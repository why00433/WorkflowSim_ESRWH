package com.weiyu.experiment.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;

/**
 * 将创建的VM跟数据中心进行绑定
 * @author Wei Yu
 *
 */
public class VMAllocationPolicyImpl {
	/**
	 * 将VM跟数据中心在创建的时候就进行绑定
	 * 
	 * @param dcList
	 * @param vmList
	 */
	public static Map<Integer, List<CondorVM>> bindVMToDatacenter(List<WorkflowDatacenter> dcList,
			List<CondorVM> vmList) {
		Map<Integer, List<CondorVM>> vmsToDatacenterMap = new HashMap<>();
		for (int i = 0; i < vmList.size(); i++) {
			CondorVM vm = vmList.get(i);
			int dcIndex = i % dcList.size();
			WorkflowDatacenter preSelectedDC = dcList.get(dcIndex);
			List<CondorVM> createdVMList = vmsToDatacenterMap.get(preSelectedDC.getId());
			if (null == createdVMList)
				createdVMList = new ArrayList<>();

			List<Host> hostListInPreselectedDC = preSelectedDC.getHostList();
			boolean isSuccess = false;
			for (Host host : hostListInPreselectedDC) {
				isSuccess = host.vmCreate(vm);
				if (isSuccess) {
					// 如果分配成功，则跳出当前循环
					break;
				}
			}
			if (!isSuccess) {
				// 如果当前数据中心中主机的配置都不能满足该虚拟机的要求，则需要将当前VM分配到其他数据中心。
				vmsToDatacenterMap = assignVMtoOtherDatacenter(dcList, vm, vmsToDatacenterMap);
				/*if (-1 != assignedDCId) {
					vmsToDatacenterMap.put(assignedDCId, createdVMList);
				}*/
			} else {
				createdVMList.add(vm);
				vmsToDatacenterMap.put(preSelectedDC.getId(), createdVMList);
			}
		}
		return vmsToDatacenterMap;
	}

	/**
	 * 如果返回失败，对数据中心重头到尾进行遍历，直到满足当前VM要求的主机
	 * 
	 * @param dcList
	 * @param vm
	 * @param vmsToDatacenterMap
	 * @return：返回VM分配到的数据中心ID； -1：表示分配失败；
	 */
	public static Map<Integer, List<CondorVM>> assignVMtoOtherDatacenter(List<WorkflowDatacenter> dcList, CondorVM vm,
			Map<Integer, List<CondorVM>> vmsToDatacenterMap) {
		System.out.println("虚拟机#" + vm.getId() + "重新搜索来了。。。");
		for (WorkflowDatacenter dc : dcList) {
			List<CondorVM> createdVMList = vmsToDatacenterMap.get(dc.getId());
			// 遍历数据中心里的每个主机
			boolean isSuccess = false;
			for (Host host : dc.getHostList()) {
				isSuccess = host.vmCreate(vm);
				if (isSuccess) {
					// 如果分配成功，则跳出当前循环
					createdVMList.add(vm);
					vmsToDatacenterMap.put(dc.getId(), createdVMList);
					System.out.println("虚拟机#" + vm.getId() + "最终被分在数据中心#" + dc.getId() + "中的主机#" + host.getId() + "上!!!");
					return vmsToDatacenterMap;
				}
			}
		}
		return vmsToDatacenterMap;
	}
}
