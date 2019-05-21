package com.weiyu.experiment.taskassigning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.workflowsim.CondorVM;
import org.workflowsim.FileItem;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.FileType;

public class TaskAssigningUtils {
	/**
	 * ���㱾�����ݵĴ���ʱ��
	 */
	public static double calculateOriginalDataTransmissionTime(Task task, CondorVM vm) {
		double totalSize = 0.0;
		Host localDataHost = null;
		List<FileItem> files = task.getFileList();
		for (FileItem fileItem : files) {
			if (FileType.INPUT == fileItem.getType()) {
				if (null == localDataHost) {
					localDataHost = fileItem.getHost();
				}
				double size = fileItem.getSize();
				totalSize += size;
			}
		}

		totalSize *= 8;
		double totalOriginalTransTime = 0.0;

		if (null != files && files.size() > 0) {
			if (totalSize > 0) {
				if (vm.getHost().getDatacenter().getId() == localDataHost.getDatacenter().getId()) {
					// �����ͬһ��������
					if (vm.getHost().getId() == localDataHost.getId()) {
						// �����ͬһ����
						totalOriginalTransTime += 0;
					} else {
						totalOriginalTransTime += (totalSize / Parameters.getBandwidthInDC());
					}
				} else {
					totalOriginalTransTime += (totalSize / Parameters.getBandwidthBetweenDC());
				}
			}
		}
		// System.out.println("����ID��#" + task.getCloudletId() + " Task��������ƣ�" +
		// task.getType() + " -��������ƽ������ʱ�䣺"
		// + totalOriginalTransTime);
		return totalOriginalTransTime;
	}

	/**
	 * ���㸸�ڵ���ӽڵ�֮������ݴ���ʱ��
	 * 
	 * @param parent
	 * @param child
	 * @param bandwidthSpeed
	 * @return
	 */
	public static double calculateDataTransmissionTimeBetweenParentAndChild(Task parent, Task child,
			double bandwidthSpeed) {
		List<FileItem> parentFiles = parent.getFileList();
		List<FileItem> childFiles = child.getFileList();

		double totalTransmissionTime = 0.0;
		for (FileItem parentFile : parentFiles) {
			if (parentFile.getType() != Parameters.FileType.OUTPUT) {
				continue;
			}

			for (FileItem childFile : childFiles) {
				if (childFile.getType() == Parameters.FileType.INPUT
						&& childFile.getName().equals(parentFile.getName())) {
					double childSize = childFile.getSize();
					// �����ͬһ�������ģ��������������ڵĴ���
					totalTransmissionTime += (childSize * 8 / bandwidthSpeed);
				}
				break;
			}
		}
		// acc in MB, averageBandwidth in Mb/s
		return totalTransmissionTime;
	}

	//����ĳ����������̨������ϵ�ִ��ʱ��
	public static double calculateComputationTime(Task task, CondorVM vm, double frequency) {
		double totalComputationTime = task.getCloudletLength() / (vm.getMips() * frequency);
		return totalComputationTime;
	}

}
