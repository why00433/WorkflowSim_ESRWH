package com.weiyu.experiment.tasksequence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weiyu.experiment.ESRWHAlgorithm;
import org.cloudbus.cloudsim.Host;
import org.workflowsim.CondorVM;
import org.workflowsim.FileItem;
import org.workflowsim.Task;
import org.workflowsim.planning.BasePlanningAlgorithm;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.FileType;

import com.weiyu.experiment.EECEPlanningAlgorithm;
import com.weiyu.experiment.domain.DownwardTaskRank;
import com.weiyu.experiment.domain.TaskRank;
import com.weiyu.experiment.domain.UpwardTaskRank;

public abstract class RankMethod {
	/**
	 * �����upward rank
	 */
	protected Map<Task, Double> rank;
	
	protected ESRWHAlgorithm planner;
	
	public RankMethod(ESRWHAlgorithm planner){
		this.planner = planner;
		rank = new HashMap<>();
	}
	
	/**
     * ���㱾�����ݵĴ���ʱ��
     */
	public void calculateOriginalDataTransmissionTime() {
    	List<Task> taskList = planner.getTaskList();
    	//�ȳ�ʼ��ÿ������ı������ݴ���ʱ��
        for (Task task : taskList) {
        	planner.getOriginalDataTransmissionTimes().put(task, 0.0);
        }

        //
        for (Task task : taskList) {
        	double totalSize = 0.0;
        	Host localDataHost = null;
        	List<FileItem> files = task.getFileList();
        	for (FileItem fileItem : files) {
				if(FileType.INPUT == fileItem.getType()){
					if(null == localDataHost){
						localDataHost = fileItem.getHost();
					}
					double size = fileItem.getSize();
					totalSize += size;
				}
			}
        	
        	totalSize *= 8;
        	double avgOriginalTransTime = 0.0;
        	double totalOriginalTransTime = 0.0;
        	
        	if(null != files && files.size() > 0){
        		if(totalSize > 0){
	        		//Host host = files.get(0).getHost();
	        		List<CondorVM> vmList = Parameters.getVmList();
	        		for(CondorVM vm : vmList){
	        			if(vm.getHost().getDatacenter().getId() == localDataHost.getDatacenter().getId()){
	        				//�����ͬһ��������
	        				if(vm.getHost().getId() == localDataHost.getId()){
	        					//�����ͬһ����
	        					totalOriginalTransTime += 0;
	        				}else{
	        					totalOriginalTransTime += (totalSize / Parameters.getBandwidthInDC());
	        				}
	        			}else{
	        				totalOriginalTransTime += (totalSize / Parameters.getBandwidthBetweenDC());
	        			}
	        		}
	        		avgOriginalTransTime = totalOriginalTransTime / vmList.size();
        		}
        	}else{
        		avgOriginalTransTime = 0;
        	}
//        	System.out.println("����ID��#" + task.getCloudletId() + " Task��������ƣ�" + task.getType() + " -��������ƽ������ʱ�䣺" + avgOriginalTransTime);
        	planner.getOriginalDataTransmissionTimes().put(task, avgOriginalTransTime);
        }
    }
	
	/**
	 * ����ǰ�����ݴ���ʱ��
	 */
    public void calculateGeneratedDataTransmissionTime() {
    	List<Task> taskList = planner.getTaskList();
    	Map<Task, Double> taskTransferCosts = null;
        // Initializing the matrix
    	for (Task task1 : taskList) {
            taskTransferCosts = new HashMap<>();
            for (Task task2 : task1.getChildList()) {
                taskTransferCosts.put(task2, 0.0);
            }
            planner.getGeneratedDataTransmissionTimes().put(task1, taskTransferCosts);
        }

        // Calculating the actual values
        for (Task parent : taskList) {
            for (Task child : parent.getChildList()) {
            	planner.getGeneratedDataTransmissionTimes().get(parent).put(child,
                        calculateTransferCost(parent, child));
            }
        }
    }
	
	
    public double calculateTransferCost(Task parent, Task child) {
        List<FileItem> parentFiles = parent.getFileList();
        List<FileItem> childFiles = child.getFileList();

        double avgGeneratedTransTime = 0.0;
        double totalTransmissionTime = 0.0;
        List<CondorVM> vmList = planner.getVmList();
        for (FileItem parentFile : parentFiles) {
            if (parentFile.getType() != Parameters.FileType.OUTPUT) {
                continue;
            }

            
            for (FileItem childFile : childFiles) {
                if (childFile.getType() == Parameters.FileType.INPUT
                        && childFile.getName().equals(parentFile.getName())) {
                	double childSize = childFile.getSize();
                    int vmLength = vmList.size();

					//����GeneratedDatatransmissionTime
					totalTransmissionTime = (childSize * 8 / Parameters.getBandwidthInDC());


                    //����ƽ����GeneratedDatatransmissionTime���ȼ��踸�ڵ���ӽڵ㱻���������е��������Դ��
//                    for(int i = 0; i < vmLength;i++) {
//                    	CondorVM vmForParent = vmList.get(i);
//                    	for(int j = 0;j < vmLength;j++){
//                    		CondorVM vmForChild = vmList.get(j);
//							if(vmForParent.getHost().getId() == vmForChild.getHost().getId()){
//								//�����ͬһ�����������ʱ��Ϊ0
//								totalTransmissionTime += 0;
//							}else{
//								totalTransmissionTime += (childSize * 8 / Parameters.getBandwidthInDC());
//							}
//                    	}
//                    }
                    break;
                }
            }
        }
//        avgGeneratedTransTime = totalTransmissionTime / (vmList.size() * vmList.size());
        // acc in MB, averageBandwidth in Mb/s
        return totalTransmissionTime;
    }
    
    
    /**
     * Populates the computationCosts field with the time in seconds to compute
     * a task in a vm.
     * ����ÿ�������ƽ������ɱ�
     */
    public void calculateComputationTime() {
    	Map<CondorVM, Double> costsVm = null;
        for (Task task : planner.getTaskList()) {
            //costsVm = new HashMap<>();
            double totalComputationTime = 0.0;
            double avgComputationTime = 0.0;
            for (Object vmObject : planner.getVmList()) {
                CondorVM vm = (CondorVM) vmObject;
                totalComputationTime += task.getCloudletLength() / vm.getMips();
                
                
//                if (vm.getNumberOfPes() < task.getNumberOfPes()) {
//                    costsVm.put(vm, Double.MAX_VALUE);
//                } else {
//                    costsVm.put(vm,
//                            task.getCloudletTotalLength() / vm.getMips());
//                }
                
                
            }
            avgComputationTime = totalComputationTime / planner.getVmList().size();
            planner.getComputationTimes().put(task, avgComputationTime);
//            planner.getComputationCosts().put(task, costsVm);
        }
    }
    
    public abstract List<TaskRank> calculateRanks();
    

	public Map<Task, Double> getRank() {
		return rank;
	}

	public void setRank(Map<Task, Double> rank) {
		this.rank = rank;
	}
    
}
