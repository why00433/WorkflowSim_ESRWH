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
	 * 计算本地数据的传输时间
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
					// 如果在同一数据中心
					if (vm.getHost().getId() == localDataHost.getId()) {
						// 如果在同一主机
						totalOriginalTransTime += 0;
					} else {
						totalOriginalTransTime += (totalSize / Parameters.getBandwidthInDC());
					}
				} else {
					totalOriginalTransTime += (totalSize / Parameters.getBandwidthBetweenDC());
				}
			}
		}
		// System.out.println("任务ID：#" + task.getCloudletId() + " Task任务的名称：" +
		// task.getType() + " -本地数据平均传输时间："
		// + totalOriginalTransTime);
		return totalOriginalTransTime;
	}

	/**
	 * 计算父节点跟子节点之间的数据传输时间
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
			if (parentFile.getType() != FileType.OUTPUT) {
				continue;
			}

			for (FileItem childFile : childFiles) {
				if (childFile.getType() == Parameters.FileType.INPUT
						&& childFile.getName().equals(parentFile.getName())) {
					double childSize = childFile.getSize();
					// 如果在同一数据中心，则用数据中心内的带宽
					totalTransmissionTime += (childSize * 8 / bandwidthSpeed);
					break;
				}
			}
		}
		// acc in MB, averageBandwidth in Mb/s
		return totalTransmissionTime;
	}

	//计算某个任务在哪台计算机上的执行时间
	public static double calculateComputationTime(Task task, CondorVM vm) {
		double totalComputationTime = task.getCloudletLength() / (vm.getMips());
		return totalComputationTime;
	}

	//计算某个任务在哪台计算机上的执行时间
	public static double calculateComputationTime(Task task, CondorVM vm, double frequency) {
		double totalComputationTime = task.getCloudletLength() / (vm.getMips() * frequency);
		return totalComputationTime;
	}

}
