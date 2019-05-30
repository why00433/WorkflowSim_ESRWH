package com.weiyu.experiment.resourceAllocating;

import com.weiyu.experiment.ESRWHAlgorithm;
import com.weiyu.experiment.domain.Event;
import com.weiyu.experiment.domain.TaskRank;
import com.weiyu.experiment.taskassigning.TaskAssigningUtils;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;

import java.util.*;

/**
 * Created by why on 2019/5/29.
 */
public class SerachPMUtils {


    protected ESRWHAlgorithm planner;

    //记录消耗能耗
    protected double[][] energyTable = null;
    protected double totalEnergy = 0.0;

    //记录任务分配后的每个物理机所有的运行时间段
    protected Map<CondorVM, ArrayList<Event>> allocatedMap = new HashMap<>();

    //记录每个任务的可靠性
    protected Map<Task,Double> reliaMap = new HashMap<>();

    public SerachPMUtils(ESRWHAlgorithm planner) {
        this.planner = planner;
    }

    public double searchPM(Parameters.AllocatingMethod allocatingMethod, Map<Task, Double> subdeadlines){
        //记录EFT,方便下面算子截止期

        //可靠性约束
        double target = planner.getReliability();

        List<TaskRank> ranks = planner.getRanks();

        //获取物理机列表
        List<CondorVM> vmList = planner.getVmList();

        double[][] EST = new double[ranks.size()][vmList.size()];
        double[][] EFT = new double[ranks.size()][vmList.size()];
        HashMap<Task,Double> AFT = new HashMap<>();

        double[] avail = new double[vmList.size()];

        double[][] R = new double[ranks.size()][vmList.size()];
        //记录消耗能耗
        energyTable = new double[ranks.size()][vmList.size()];

        //初始化
        for (int i = 0 ; i < ranks.size(); i++) {

            Task task = ranks.get(i).getTask();
            AFT.put(task,0.0);

            for(int j = 0;j < vmList.size();j++){
                avail[j] = 0.0;
                EST[i][j] = 0.0;
                EST[i][j] = 0.0;
                R[i][j] =0.0;
                energyTable[i][j] = 0.0;
            }
        }





        for (int i = 0 ; i < ranks.size(); i++) {

            TaskRank rank = ranks.get(i);
            Task task = rank.getTask();
            Map<Double ,Integer> EFTMap = new TreeMap<>();

            //用于排序的供调度的物理机列表
            Map<CondorVM, Double> PM = new LinkedHashMap<>();

            //错误到达率
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

                double energy = vm.getPower() * T;

                energyTable[i][j] = energy;


                if(R[i][j] >= target){
                    if(Parameters.AllocatingMethod.VS1 == allocatingMethod){
                        PM.put(vm, energy);
                    }
                    else if(Parameters.AllocatingMethod.VS1 == allocatingMethod){
                        PM.put(vm, vm.getPower()/vm.getMips());
                    }
                    else{
                        PM.put(vm, new Random().nextDouble());
                    }
                }
            }

            boolean flag = false;

            //如果PM不为空
            if(PM != null && !PM.isEmpty()){
                //对物理机进行排序
                PM = sort(PM);
                Set<CondorVM> keySet = PM.keySet();
                Iterator<CondorVM> iter = keySet.iterator();
                while (iter.hasNext()){
                    CondorVM m = iter.next();
                    int p = m.getId();
                    if (EFT[i][p] < subdeadlines.get(task)) {
                        AFT.put(task,EFT[i][p]);
                        avail[p] = EFT[i][p];
                        totalEnergy += energyTable[i][p];

                        if(allocatedMap.get(m) == null || allocatedMap.isEmpty()){
                            ArrayList<Event> list = new ArrayList<>();
                            list.add(new Event(task, EST[i][p], EFT[i][p]));
                            allocatedMap.put(m, list);
                            reliaMap.put(task, R[i][p]);
                        }
                        else {
                            allocatedMap.get(m).add(new Event(task, EST[i][p], EFT[i][p]));
                        }

                        flag = true;
                        break;
                    }
                }

            }

            //没有满足的PM或是副本数不足
            if(PM == null || PM.isEmpty() || !flag){
                //低于目标可靠性，增加副本并分配
                Set<Double> keySet = EFTMap.keySet();
                Iterator<Double> iter = keySet.iterator();
                while (iter.hasNext()) {
                    Double key = iter.next();

                    int p = EFTMap.get(key);
                    CondorVM m = vmList.get(p);

                    //分配任务到最小EFT的物理机，更新完成时间
                    AFT.put(task,EFT[i][p]);
                    avail[p] = EFT[i][p];


                    if(allocatedMap.get(m) == null || allocatedMap.isEmpty()){
                        ArrayList<Event> list = new ArrayList<>();
                        list.add(new Event(task, EST[i][p], EFT[i][p]));
                        allocatedMap.put(m, list);
                    }
                    else {
                        allocatedMap.get(m).add(new Event(task, EST[i][p], EFT[i][p]));
                    }

                    totalEnergy += energyTable[i][p];
                    relia = 1-(1-relia)*(1-R[i][p]);
                    reliaMap.put(task,relia);

                    if(relia >= target)
                        break;

                }
            }



        }

        return totalEnergy;



    }

    /**
     * 对物理机进行排序进行排序
     *
     */
    private Map<CondorVM,Double> sort(Map<CondorVM, Double> pm) {
        List<Map.Entry<CondorVM,Double>> entryList = new ArrayList<Map.Entry<CondorVM,Double>>(
                pm.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<CondorVM,Double>> (){

            @Override
            public int compare(Map.Entry<CondorVM,Double> me1, Map.Entry<CondorVM,Double> me2) {

                return me1.getValue().compareTo(me2.getValue());
            }
        });

        Map<CondorVM,Double> sortedMap = new LinkedHashMap<>();

        Iterator<Map.Entry<CondorVM,Double>> iter = entryList.iterator();
        while (iter.hasNext()) {
            Map.Entry<CondorVM,Double> tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    /**
     * 对物理机DVFS变频计算
     *
     */
    public double afterDVFS(Map<Task, Double> subdeadlines){

        //可靠性约束
        double target = planner.getReliability();

        Set<CondorVM> keySet = allocatedMap.keySet();
        Iterator<CondorVM> iter = keySet.iterator();
        while (iter.hasNext()) {
            CondorVM m = iter.next();
            List<Event> list = allocatedMap.get(m);

            for(Event event : list){
                double subdead = subdeadlines.get(event.task);
                double R = 0.0;

                if(event.finish < subdead){
                    double f = (subdead - event.start)/(event.finish - event.start);
                    List<Double> frequencyTable = m.getFrequency();
                    double frequencyMin = frequencyTable.get(frequencyTable.size()-1);
                    double frequency = f;
                    for(double f1 : frequencyTable){

                        double relia = reliaMap.get(event.task);
                        R = calculateRelia(relia,event.finish - event.start, frequency, frequencyMin);

                        //可靠性约束
                        if(R < target)
                            break;

                        if(f1 < f)
                            break;

                        frequency = f1;
                    }

                    //简单的计算一下减少的能耗

                    totalEnergy -= (1-frequency*frequency)*m.getPower()*TaskAssigningUtils.calculateComputationTime(event.task, m);

                    //更新任务可靠性计算
                    reliaMap.put(event.task, R);
                }
            }
        }

        return totalEnergy;
    }

    /**
     * 对可靠性简单计算
     *
     */
    public double calculateRelia(double relia, double T, double frequency, double frequencyMin){

        //错误到达率
        double lamda = 10e-6;

        double lamdaFrequency = lamda*Math.pow(10.0, 4.0*(1.0-frequency)/(1.0-frequency));
        double rf = Math.exp(-lamdaFrequency*T/frequency);

        relia = 1-(1-relia)/(1-Math.exp(-lamda * T))*(1-rf);

        return relia;
    }

}
