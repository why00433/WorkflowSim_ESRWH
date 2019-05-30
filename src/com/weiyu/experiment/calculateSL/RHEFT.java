package com.weiyu.experiment.calculateSL;

import com.weiyu.experiment.ESRWHAlgorithm;
import com.weiyu.experiment.domain.TaskRank;
import com.weiyu.experiment.taskassigning.TaskAssigningUtils;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;

import java.util.*;

/**
 * 用于算法第二步计算最短路径ShortestLength
 */
public class RHEFT {

    protected ESRWHAlgorithm planner;
    protected Map<Task,Double> eftsForSubdeadline;

    public RHEFT(ESRWHAlgorithm planner) {
        this.planner = planner;
    }

    public double calculateSL(){
        //记录EFT,方便下面算子截止期
        eftsForSubdeadline = new HashMap<>();

        List<TaskRank> ranks = planner.getRanks();

        //获取物理机列表
        List<CondorVM> vmList = planner.getVmList();

        double[][] EST = new double[ranks.size()][vmList.size()];
        double[][] EFT = new double[ranks.size()][vmList.size()];
        HashMap<Task,Double> AFT = new HashMap<>();

        double[] avail = new double[vmList.size()];

        double[][] R = new double[ranks.size()][vmList.size()];


        double SL = 0.0;

        //初始化
        for (int i = 0 ; i < ranks.size(); i++) {

            Task task = ranks.get(i).getTask();
            AFT.put(task,0.0);

            for(int j = 0;j < vmList.size();j++){
                avail[j] = 0.0;
                EST[i][j] = 0.0;
                EST[i][j] = 0.0;
                R[i][j] =0.0;
            }
        }

        //可靠性约束
        double target = planner.getReliability();


        for (int i = 0 ; i < ranks.size(); i++) {

            TaskRank rank = ranks.get(i);
            Task task = rank.getTask();
            Map<Double ,Integer> EFTMap = new TreeMap<>();


            //计算可靠性
            double lamda = 10e-6;
            double relia = 0.0;


            //记录最小EFT的物理机
            double min = 10000000;

            for(int j = 0;j < vmList.size();j++){
                CondorVM vm = vmList.get(j);

                //计算EFT
                double T = TaskAssigningUtils.calculateComputationTime(task,vm);

                double max = 0.0;
                for(Task parent: task.getParentList()){
                    //Transmission Time
                    double transmissionTime = TaskAssigningUtils.calculateDataTransmissionTimeBetweenParentAndChild(parent, task, Parameters.getBandwidthInDC());
                    if(AFT.get(parent) + transmissionTime > max)
                        max = AFT.get(parent) + transmissionTime;
                }
                EST[i][j] = Math.max(max,avail[j]);
                EFT[i][j] = EST[i][j] + T;
                EFTMap.put(EFT[i][j], i);


                R[i][j] = Math.exp(-lamda * T);
            }



            //低于目标可靠性，增加副本并分配
            Set<Double> keySet = EFTMap.keySet();
            Iterator<Double> iter = keySet.iterator();
            while (iter.hasNext()) {
                Double key = iter.next();
                int p = EFTMap.get(key);
                //分配任务到最小EFT的物理机，更新完成时间
                AFT.put(task,EFT[i][p]);
                avail[p] = EFT[i][p];

                relia = 1-(1-relia)*(1-R[i][p]);

                if(relia >= target)
                    break;

            }

            eftsForSubdeadline.put(task,AFT.get(task));
            SL = Math.max(AFT.get(task), SL);


        }

        return SL;



    }


    public Map<Task,Double> getEftsForSubdeadline() {
        return eftsForSubdeadline;
    }
}
