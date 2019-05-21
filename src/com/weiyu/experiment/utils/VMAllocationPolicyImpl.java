package com.weiyu.experiment.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;

/**
 * ��������VM���������Ľ��а�
 * @author Wei Yu
 *
 */
public class VMAllocationPolicyImpl {
	/**
	 * ��VM�����������ڴ�����ʱ��ͽ��а�
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
					// �������ɹ�����������ǰѭ��
					break;
				}
			}
			if (!isSuccess) {
				// �����ǰ�������������������ö�����������������Ҫ������Ҫ����ǰVM���䵽�����������ġ�
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
	 * �������ʧ�ܣ�������������ͷ��β���б�����ֱ�����㵱ǰVMҪ�������
	 * 
	 * @param dcList
	 * @param vm
	 * @param vmsToDatacenterMap
	 * @return������VM���䵽����������ID�� -1����ʾ����ʧ�ܣ�
	 */
	public static Map<Integer, List<CondorVM>> assignVMtoOtherDatacenter(List<WorkflowDatacenter> dcList, CondorVM vm,
			Map<Integer, List<CondorVM>> vmsToDatacenterMap) {
		System.out.println("�����#" + vm.getId() + "�����������ˡ�����");
		for (WorkflowDatacenter dc : dcList) {
			List<CondorVM> createdVMList = vmsToDatacenterMap.get(dc.getId());
			// ���������������ÿ������
			boolean isSuccess = false;
			for (Host host : dc.getHostList()) {
				isSuccess = host.vmCreate(vm);
				if (isSuccess) {
					// �������ɹ�����������ǰѭ��
					createdVMList.add(vm);
					vmsToDatacenterMap.put(dc.getId(), createdVMList);
					System.out.println("�����#" + vm.getId() + "���ձ�������������#" + dc.getId() + "�е�����#" + host.getId() + "��!!!");
					return vmsToDatacenterMap;
				}
			}
		}
		return vmsToDatacenterMap;
	}
}
