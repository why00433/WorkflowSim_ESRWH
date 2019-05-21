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
package org.workflowsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.FileType;
import org.workflowsim.utils.ReplicaCatalog;

/**
 * WorkflowParser parse a DAX into tasks so that WorkflowSim can manage them
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Aug 23, 2013
 * @date Nov 9, 2014
 */
public final class WorkflowParser {

    /**
     * The path to DAX file.
     */
    private final String daxPath;
    /**
     * The path to DAX files.
     */
    private final List<String> daxPaths;
    /**
     * All tasks.
     */
    private List<Task> taskList;
    
    private List<List<Task>> workflowList;
    /**
     * User id. used to create a new task.
     */
    private final int userId;

    /**
     * current job id. In case multiple workflow submission
     */
    private int jobIdStartsFrom;
    
    private int initialDepth = -1;

    /**
     * Gets the task list
     *
     * @return the task list
     */
    @SuppressWarnings("unchecked")
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * Sets the task list
     *
     * @param taskList the task list
     */
    protected void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
    
    
    public List<List<Task>> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<List<Task>> workflowList) {
		this.workflowList = workflowList;
	}





	/**
     * Map from task name to task.
     */
    protected Map<String, Task> mName2Task;
    
    private int randomVMIndex;

    /**
     * Initialize a WorkflowParser
     *
     * @param userId the user id. Currently we have just checked single user
     * mode
     */
    public WorkflowParser(int userId) {
        this.userId = userId;
        this.mName2Task = new HashMap<>();
        this.daxPath = Parameters.getDaxPath();
        this.daxPaths = Parameters.getDAXPaths();
        this.jobIdStartsFrom = 1;

        setTaskList(new ArrayList<>());
        
        workflowList = new ArrayList<>();
        
        //����һ����������Input�������������������VM�ĳ���Ϊ����
//        Random random = new Random();
//        randomVMIndex = random.nextInt(Parameters.getVmNum());
    }

    /**
     * Start to parse a workflow which is a xml file(s).
     */
    public void parse() {
        if (this.daxPath != null) {
            taskList = parseXmlFile(this.daxPath);
        } else if (this.daxPaths != null) {
        	List<Task> taskList = null;
        	List<Double> workflowSizeList = new ArrayList<>();
        	List<Integer> arriveTimeList = new ArrayList<>();
        	Random random = new Random();
        	int workflowSize = daxPaths.size();
        	
        	for(int i = 0;i < daxPaths.size();i++){
        		String path = daxPaths.get(i);
        		randomVMIndex = Parameters.inputDataLocations.get(i);
//        	}
//            for (String path : this.daxPaths) {
            	initialDepth = -1;
                taskList = parseXmlFile(path);
                double totalWorkflowSize = 0.0;
                for(Task task : taskList){
                	totalWorkflowSize += task.getCloudletLength();
                }
                
                //����ÿ��������Ӧ�õĴ�С
                workflowSizeList.add(totalWorkflowSize);
                workflowList.add(taskList);
                
                //��ÿ��������Ӧ�ó�ʼ��һ������ʱ��
                int arriveTime = random.nextInt(workflowSize);
                arriveTimeList.add(arriveTime);
            }
            
            //����ÿ��������Ӧ�õĴ�С�͹�����Ӧ�ü���
            Parameters.setWorkflowSizes(workflowSizeList);
            Parameters.setWorkflowList(workflowList);
            Parameters.setArriveTimeList(arriveTimeList);
        }
    }

    /**
     * Sets the depth of a task
     *
     * @param task the task
     * @param depth the depth
     */
    private void setDepth(Task task, int depth) {
        if (depth > task.getDepth()) {
        	if(depth > initialDepth){
        		initialDepth = depth;
        	}
            task.setDepth(depth);
        }
        for (Task cTask : task.getChildList()) {
            setDepth(cTask, task.getDepth() + 1);
        }
    }

    /**
     * Parse a DAX file with jdom
     */
    private List<Task> parseXmlFile(String path) {
    	List<Task> parsedTaskList = null;
//    	double totalWorkflowSize = 0.0;
        try {
        	parsedTaskList = new ArrayList<>();
            SAXBuilder builder = new SAXBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = builder.build(new File(path));
            Element root = dom.getRootElement();
            List<Element> list = root.getChildren();
            for (Element node : list) {
                switch (node.getName().toLowerCase()) {
                    case "job":
                        long length = 0;
                        String nodeName = node.getAttributeValue("id");
                        String nodeType = node.getAttributeValue("name");
                        /**
                         * capture runtime. If not exist, by default the runtime
                         * is 0.1. Otherwise CloudSim would ignore this task.
                         * BUG/#11
                         */
                        double runtime;
                        if (node.getAttributeValue("runtime") != null) {
                            String nodeTime = node.getAttributeValue("runtime");
                            runtime = 1000 * Double.parseDouble(nodeTime);
                            if (runtime < 100) {
                                runtime = 100;
                            }
                            length = (long) runtime;
                        } else {
                            Log.printLine("Cannot find runtime for " + nodeName + ",set it to be 0");
                        }   //multiple the scale, by default it is 1.0
                        length *= Parameters.getRuntimeScale();
                        List<Element> fileList = node.getChildren();
                        List<FileItem> mFileList = new ArrayList<>();
                        for (Element file : fileList) {
                            if (file.getName().toLowerCase().equals("uses")) {
                                String fileName = file.getAttributeValue("name");//DAX version 3.3
                                if (fileName == null) {
                                    fileName = file.getAttributeValue("file");//DAX version 3.0
                                }
                                if (fileName == null) {
                                    Log.print("Error in parsing xml");
                                }

                                String inout = file.getAttributeValue("link");
                                double size = 0.0;

                                String fileSize = file.getAttributeValue("size");
                                if (fileSize != null) {
                                    size = Double.parseDouble(fileSize) /*/ 1024*/;
                                } else {
                                    Log.printLine("File Size not found for " + fileName);
                                }

                                /**
                                 * a bug of cloudsim, size 0 causes a problem. 1
                                 * is ok.
                                 */
                                if (size == 0) {
                                    size++;
                                }
                                /**
                                 * Sets the file type 1 is input 2 is output
                                 */
                                FileType type = FileType.NONE;
                                switch (inout) {
                                    case "input":
                                        type = FileType.INPUT;
                                        break;
                                    case "output":
                                        type = FileType.OUTPUT;
                                        break;
                                    default:
                                        Log.printLine("Parsing Error");
                                        break;
                                }
                                FileItem tFile;
                                /*
                                 * Already exists an input file (forget output file)
                                 */
                                if (size < 0) {
                                    /*
                                     * Assuming it is a parsing error
                                     */
                                    size = 0 - size;
                                    Log.printLine("Size is negative, I assume it is a parser error");
                                }
                                /*
                                 * Note that CloudSim use size as MB, in this case we use it as Byte
                                 */
                                if (type == FileType.OUTPUT) {
                                    /**
                                     * It is good that CloudSim does tell
                                     * whether a size is zero
                                     */
                                    tFile = new FileItem(fileName, size);
                                } else if (ReplicaCatalog.containsFile(fileName)) {
                                    tFile = ReplicaCatalog.getFile(fileName);
                                } else {

                                    tFile = new FileItem(fileName, size);
                                    ReplicaCatalog.setFile(fileName, tFile);
                                }
                                
                                if(type == FileType.INPUT){
	                                //�����ǰ�ļ���һ��Input�ļ�����������������һ���������ĵ�������С�
                                	List<CondorVM> vmList = Parameters.getVmList();
                                	if(null != vmList && vmList.size() > 0){
		                                CondorVM vm = Parameters.getVmList().get(randomVMIndex);
		                                tFile.setHost(vm.getHost());
                                	}
                                }
                                tFile.setType(type);
                                mFileList.add(tFile);
                            }
                        }
                        Task task;
                        //In case of multiple workflow submission. Make sure the jobIdStartsFrom is consistent.
                        synchronized (this) {
                            task = new Task(this.jobIdStartsFrom, length);
                            this.jobIdStartsFrom++;
                        }
                        //totalWorkflowSize += length;
                        task.setType(nodeType);
                        task.setUserId(userId);
                        mName2Task.put(nodeName, task);
                        for (FileItem file : mFileList) {
                            task.addRequiredFile(file.getName());
                        }
                        task.setFileList(mFileList);
                        parsedTaskList.add(task);
                        //this.getTaskList().add(task);

                        /**
                         * Add dependencies info.
                         */
                        break;
                    case "child":
                        List<Element> pList = node.getChildren();
                        String childName = node.getAttributeValue("ref");
                        if (mName2Task.containsKey(childName)) {

                            Task childTask = (Task) mName2Task.get(childName);

                            for (Element parent : pList) {
                                String parentName = parent.getAttributeValue("ref");
                                if (mName2Task.containsKey(parentName)) {
                                    Task parentTask = (Task) mName2Task.get(parentName);
                                    parentTask.addChild(childTask);
                                    childTask.addParent(parentTask);
                                }
                            }
                        }
                        break;
                }
            }
            /**
             * If a task has no parent, then it is root task.
             */
            ArrayList<Task> roots = new ArrayList<>();
            for (Task task : mName2Task.values()) {
                task.setDepth(0);
                if (task.getParentList().isEmpty()) {
                    roots.add(task);
                }
            }

            /**
             * Add depth from top to bottom.
             */
            for (Iterator it = roots.iterator(); it.hasNext();) {
                Task task = (Task) it.next();
                setDepth(task, 1);
            }
            
            Parameters.getMaximalDepthList().add(initialDepth);
            /**
             * Clean them so as to save memory. Parsing workflow may take much
             * memory
             */
            this.mName2Task.clear();
            
            return parsedTaskList;
        } catch (JDOMException jde) {
            Log.printLine("JDOM Exception;Please make sure your dax file is valid");

        } catch (IOException ioe) {
            Log.printLine("IO Exception;Please make sure dax.path is correctly set in your config file");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Parsing Exception");
        }
        
        printWorkflow();
        return null;
    }

    
    /**
     * ��ӡ��������Ӧ�����������
     */
	private void printWorkflow() {
	/*	for(Task task : this.getTaskList()){
			System.out.println("����ID:" + task.getCloudletId() + " �������ƣ�" + task.getType() + " �����С:" + task.getCloudletTotalLength()
			+ " ������Ҫ�Ĵ�����������" + task.getNumberOfPes()
			+ " �������:" + task.getDepth() + " ��Ҫ���ļ��б�:" + task.getRequiredFiles() + " ��Ҫ���ļ���������:" + task.getFileList());
			
			for(FileItem file : task.getFileList()){
				System.out.println("�ļ����ƣ�" + file.getName()+"�ļ����ͣ�" + file.getType() + "�ļ���С��" + file.getSize());
			}
		
		}*/
	}
}
