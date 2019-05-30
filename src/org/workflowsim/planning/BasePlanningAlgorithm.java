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
package org.workflowsim.planning;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.workflowsim.Task;

/**
 * The base planner has implemented the basic features. Every other planning method
 * should extend from BasePlanningAlgorithm but should not directly use it. 
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Jun 17, 2013
 */
public abstract class BasePlanningAlgorithm implements PlanningAlgorithmInterface{

    /**
     * the task list.
     */
    private List<Task> tasktList;

    /**
     * 工作流应用列表
     */
    private List<List<Task>> workflowList;
    /**
     * the vm list.
     */
    private List<? extends Vm> vmList;

    /**
     * the datacenter list
     */
    private List<Datacenter> datacenterList;
    
    protected List<Integer> datacenterIdsList;
    /**
     * Initialize a BaseScheduler
     */
    public BasePlanningAlgorithm() {
    }

    /**
     * Sets the job list.
     *
     * @param list
     */
    @Override
    public void setTaskList(List list) {
        this.tasktList = list;
    }

    /**
     * Sets the vm list
     *
     * @param list
     */
    @Override
    public void setVmList(List list) {
        this.vmList = new ArrayList(list);
    }

    /**
     * Gets the task list.
     *
     * @return the task list
     */
    @Override
    public List<Task> getTaskList() {
        return this.tasktList;
    }

    /**
     * Gets the vm list
     *
     * @return the vm list
     */
    @Override
    public List getVmList() {
        return this.vmList;
    }

    /**
     * Gets the datacenter list
     * @return the datacenter list
     */
    public List<Datacenter> getDatacenterList(){
        return this.datacenterList;
    }
    
    /**
     * Sets the datacenter list
     * @param list the datacenter list
     */
    public void setDatacenterList(List<Datacenter> list){
        this.datacenterList = list;
    }
    
    /**
     * The main function
     */
    public abstract void run() throws Exception;

	public List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	public void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	public List<List<Task>> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<List<Task>> workflowList) {
		this.workflowList = workflowList;
	}

	
	

    
}
