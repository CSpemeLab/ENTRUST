/*
 * Title:        EdgeCloudSim - Mobile Device Manager
 *
 * Description:
 * Mobile Device Manager is one of the most important component
 * in EdgeCloudSim. It is responsible for creating the tasks,
 * submitting them to the related VM with respect to the
 * Edge Orchestrator decision, and takes proper actions when
 * the execution of the tasks are finished. It also feeds the
 * SimLogger with the relevant results.

 * SampleMobileDeviceManager sends tasks to the edge servers or
 * mobile device processing unit.
 *
 * If you want to use different topology, you should modify
 * the flow implemented in this class.
 *
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.applications.sample_app6;

import edu.boun.edgecloudsim.utils.*;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimManagerEnergy;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;
import edu.boun.edgecloudsim.core.SimSettings.VM_TYPES;
import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
import edu.boun.edgecloudsim.edge_client.MobileDeviceManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_client.mobile_processing_unit.MobileHostEnergy;
import edu.boun.edgecloudsim.network.NetworkModel;

public class SampleMobileDeviceManager extends MobileDeviceManager {
    private static final int BASE = 100000; //start from base in order not to conflict cloudsim tag!

    private static final int REQUEST_RECEIVED_BY_EDGE_DEVICE = BASE + 1;
    private static final int REQUEST_RECEIVED_BY_MOBILE_DEVICE = BASE + 2;
    private static final int RESPONSE_RECEIVED_BY_MOBILE_DEVICE = BASE + 3;

    private DeadHost deadHost;

    private int taskIdCounter = 0;

    public SampleMobileDeviceManager() throws Exception {
        deadHost = DeadHost.getInstance();
        deadHost.reset();
    }

    @Override
    public void initialize() {
    }

    @Override
    public UtilizationModel getCpuUtilizationModel() {
        return new CpuUtilizationModel_Custom();
    }

    @Override
    public void startEntity() {
        super.startEntity();
    }

    /**
     * Submit cloudlets to the created VMs.
     *
     * @pre $none
     * @post $none
     */
    protected void submitCloudlets() {
        //do nothing!
    }

    /**
     * Process a cloudlet return event.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processCloudletReturn(SimEvent ev) {
        NetworkModel networkModel = SimManager.getInstance().getNetworkModel();
        Task task = (Task) ev.getData();

        ((SimManagerEnergy) SimManager.getInstance()).calculateNetConsume(task, SimUtils.RECEPTION);

        SimLogger.getInstance().taskExecuted(task.getCloudletId());

        if (task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID) {

            double delay = networkModel.getDownloadDelay(task.getAssociatedDatacenterId(), task.getMobileDeviceId(), task);

            if (delay > 0) {
                Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock() + delay);
                if (task.getSubmittedLocation().getServingWlanId() == currentLocation.getServingWlanId()) {
                    networkModel.downloadStarted(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID);
                    SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), delay, NETWORK_DELAY_TYPES.WLAN_DELAY);

                    schedule(getId(), delay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
                } else {
                    SimLogger.getInstance().failedDueToMobility(task.getCloudletId(), CloudSim.clock());
                }
            } else {
                SimLogger.getInstance().failedDueToBandwidth(task.getCloudletId(), CloudSim.clock(), NETWORK_DELAY_TYPES.WLAN_DELAY);
//                System.err.println("fallimento per banda - task:" + task.getCloudletId() + " device:" + task.getMobileDeviceId());

            }
        } else if (task.getAssociatedDatacenterId() == SimSettings.MOBILE_DATACENTER_ID) {
            SimLogger.getInstance().taskEnded(task.getCloudletId(), CloudSim.clock());

            /*
             * TODO: In this scenario device to device (D2D) communication is ignored.
             * If you want to consider D2D communication, you should transmit the result
             * of the task to the sender mobile device. Hence, you should calculate
             * D2D_DELAY here and send the following event:
             *
             * schedule(getId(), delay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
             *
             * Please not that you should deal with the mobility and D2D delay calculation.
             * The task can be failed due to the network bandwidth or the nobility.
             */
        } else {
            SimLogger.printLine("Unknown datacenter id! Terminating simulation...");
            System.exit(0);
        }
    }

    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null! Terminating simulation...");
            System.exit(0);
            return;
        }

        NetworkModel networkModel = SimManager.getInstance().getNetworkModel();

        switch (ev.getTag()) {
            case REQUEST_RECEIVED_BY_MOBILE_DEVICE: {
                Task task = (Task) ev.getData();
                submitTaskToVm(task, SimSettings.VM_TYPES.MOBILE_VM);
                break;
            }
            case REQUEST_RECEIVED_BY_EDGE_DEVICE: {
                Task task = (Task) ev.getData();
                networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID);
                submitTaskToVm(task, SimSettings.VM_TYPES.EDGE_VM);
                break;
            }
            case RESPONSE_RECEIVED_BY_MOBILE_DEVICE: {
                Task task = (Task) ev.getData();

                networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID);
                
//                mettere qua l'upload

                SimLogger.getInstance().taskEnded(task.getCloudletId(), CloudSim.clock());
                break;
            }
            default:
                SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - event unknown by this DatacenterBroker. Terminating simulation...");
                System.exit(0);
                break;
        }
    }

    public void submitTask(TaskProperty edgeTask) {
        double delay = 0;
        int nextEvent = 0;
        int nextDeviceForNetworkModel = 0;
        VM_TYPES vmType = null;
        NETWORK_DELAY_TYPES delayType = null;

        NetworkModel networkModel = SimManager.getInstance().getNetworkModel();

        //create a task
        Task task = createTask(edgeTask);

        Location currentLocation = SimManager.getInstance().getMobilityModel().
                getLocation(task.getMobileDeviceId(), CloudSim.clock());

        //set location of the mobile device which generates this task
        task.setSubmittedLocation(currentLocation);

        //add related task to log list
        SimLogger.getInstance().addLog(task.getMobileDeviceId(),
                task.getCloudletId(),
                task.getTaskType(),
                (int) task.getCloudletLength(),
                (int) task.getCloudletFileSize(),
                (int) task.getCloudletOutputSize());

        int nextHopId = SimManager.getInstance().getEdgeOrchestrator().getDeviceToOffload(task);

        if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID) {
            delay = networkModel.getUploadDelay(task.getMobileDeviceId(), nextHopId, task);
            vmType = SimSettings.VM_TYPES.EDGE_VM;
            nextEvent = REQUEST_RECEIVED_BY_EDGE_DEVICE;
            delayType = NETWORK_DELAY_TYPES.WLAN_DELAY;
            nextDeviceForNetworkModel = SimSettings.GENERIC_EDGE_DEVICE_ID;
        } else if (nextHopId == SimSettings.MOBILE_DATACENTER_ID) {
            vmType = VM_TYPES.MOBILE_VM;
            nextEvent = REQUEST_RECEIVED_BY_MOBILE_DEVICE;

            /*
             * TODO: In this scenario device to device (D2D) communication is ignored.
             * If you want to consider D2D communication, you should calculate D2D
             * network delay here.
             *
             * You should also add D2D_DELAY to the following enum in SimSettings
             * public static enum NETWORK_DELAY_TYPES { WLAN_DELAY, MAN_DELAY, WAN_DELAY }
             *
             * If you want to get statistics of the D2D networking, you should modify
             * SimLogger in a way to consider D2D_DELAY statistics.
             */
        } else {
            SimLogger.printLine("Unknown nextHopId! Terminating simulation...");
            System.exit(0);
        }

        if (delay > 0 || nextHopId == SimSettings.MOBILE_DATACENTER_ID) {

            Vm selectedVM = SimManager.getInstance().getEdgeOrchestrator().getVmToOffload(task, nextHopId);

            if (selectedVM != null) {
                //set related host id
                task.setAssociatedDatacenterId(nextHopId);

                //set related host id
                task.setAssociatedHostId(selectedVM.getHost().getId());

                //set related vm id
                task.setAssociatedVmId(selectedVM.getId());

                //bind task to related VM
                getCloudletList().add(task);
                bindCloudletToVm(task.getCloudletId(), selectedVM.getId());

                SimLogger.getInstance().taskStarted(task.getCloudletId(),task.getAssociatedHostId(),  CloudSim.clock());

                if (nextHopId != SimSettings.MOBILE_DATACENTER_ID) {
                    networkModel.uploadStarted(task.getSubmittedLocation(), nextDeviceForNetworkModel);
                    SimLogger.getInstance().setUploadDelay(task.getCloudletId(), delay, delayType);
                }

                schedule(getId(), delay, nextEvent, task);
            } else {
                //SimLogger.printLine("Task #" + task.getCloudletId() + " cannot assign to any VM");
                SimLogger.getInstance().rejectedDueToVMCapacity(task.getCloudletId(), CloudSim.clock(), vmType.ordinal());
            }
        } else {
            //SimLogger.printLine("Task #" + task.getCloudletId() + " cannot assign to any VM");
            SimLogger.getInstance().rejectedDueToBandwidth(task.getCloudletId(), CloudSim.clock(), vmType.ordinal(), delayType);

        }
    }


    /*
     * CLONE but with Task return
     */
    public Task submitTaskEnergy(TaskProperty edgeTask) {
        double delay = 0;
        int nextEvent = 0;
        int nextDeviceForNetworkModel = 0;
        VM_TYPES vmType = null;
        NETWORK_DELAY_TYPES delayType = null;

        NetworkModel networkModel = SimManager.getInstance().getNetworkModel();

        //create a task
        Task task = createTask(edgeTask);

        Location currentLocation = SimManager.getInstance().getMobilityModel().
                getLocation(task.getMobileDeviceId(), CloudSim.clock());

        //set location of the mobile device which generates this task
        task.setSubmittedLocation(currentLocation);

        //add related task to log list
        SimLogger.getInstance().addLog(task.getMobileDeviceId(),
                task.getCloudletId(),
                task.getTaskType(),
                (int) task.getCloudletLength(),
                (int) task.getCloudletFileSize(),
                (int) task.getCloudletOutputSize());

        int nextHopId = SimManager.getInstance().getEdgeOrchestrator().getDeviceToOffload(task);

        if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID) {
            delay = networkModel.getUploadDelay(task.getMobileDeviceId(), nextHopId, task);
            vmType = SimSettings.VM_TYPES.EDGE_VM;
            nextEvent = REQUEST_RECEIVED_BY_EDGE_DEVICE;
            delayType = NETWORK_DELAY_TYPES.WLAN_DELAY;
            nextDeviceForNetworkModel = SimSettings.GENERIC_EDGE_DEVICE_ID;
        } else if (nextHopId == SimSettings.MOBILE_DATACENTER_ID) {
            vmType = VM_TYPES.MOBILE_VM;
            nextEvent = REQUEST_RECEIVED_BY_MOBILE_DEVICE;

            /*
             * TODO: In this scenario device to device (D2D) communication is ignored.
             * If you want to consider D2D communication, you should calculate D2D
             * network delay here.
             *
             * You should also add D2D_DELAY to the following enum in SimSettings
             * public static enum NETWORK_DELAY_TYPES { WLAN_DELAY, MAN_DELAY, WAN_DELAY }
             *
             * If you want to get statistics of the D2D networking, you should modify
             * SimLogger in a way to consider D2D_DELAY statistics.
             */
        } else {
            SimLogger.printLine("Unknown nextHopId! Terminating simulation...");
            System.exit(0);
        }


        if (delay > 0 || nextHopId == SimSettings.MOBILE_DATACENTER_ID) {

            Vm selectedVM = SimManager.getInstance().getEdgeOrchestrator().getVmToOffload(task, nextHopId);

            if (selectedVM != null) {
            	
            	
                //set related host id
                task.setAssociatedDatacenterId(nextHopId);

                //set related host id
                task.setAssociatedHostId(selectedVM.getHost().getId());
                int idtomanage = task.getAssociatedHostId();
                
                //set related vm id
                task.setAssociatedVmId(selectedVM.getId());

                //bind task to related VM
                getCloudletList().add(task);
                bindCloudletToVm(task.getCloudletId(), selectedVM.getId());

                SimLogger.getInstance().taskStarted(task.getCloudletId(),task.getMobileDeviceId(), CloudSim.clock());

                /**
                 *
                * le prossime istruzioni ri-simulano l'energia consumata
                * questo perchè gli eventi di tipo CREATE_TASK sono gli ultimi ad essere eseguiti
                * e se ci basavamo sull energia consumata negli eventi di tipo GET_LOAD_LOG
                * i mobile host energy erano già morti
                * quindi per ogni task creato viene simulata l'energia consumata fino a quel momento
                * */
                SimManager.getInstance().getMobileServerManager().getEnergyConsumed(CloudSim.clock());
                
//                System.err.println("submitTaskEnergy: task.getmobileID: "+task.getMobileDeviceId());
                //int idtomanage = task.getMobileDeviceId() + SimSettings.getInstance().getNumOfEdgeHosts() + 1 ;
//        		MobileHostEnergy host = ((MobileHostEnergy)SimManager.getInstance().getMobileServerManager().getDatacenter().getHostList().get(task.getMobileDeviceId()));        	
//        		double energyLevel= host.getEnergyModel().getBatteryLevelWattHour();
//        		double energyLevelperc= host.getEnergyModel().getBatteryLevelPercentage();
//        		double energyMax = host.getEnergyModel().getBatteryCapacity();
                
                                
                if (deadHost.mobileHostIsDead(idtomanage)) {
                    SimLogger.getInstance().failedDueToDeviceDeath(task.getCloudletId(), CloudSim.clock());
                    return null;
                }

                if (nextHopId != SimSettings.MOBILE_DATACENTER_ID) {
                    networkModel.uploadStarted(task.getSubmittedLocation(), nextDeviceForNetworkModel);
                    SimLogger.getInstance().setUploadDelay(task.getCloudletId(), delay, delayType);
                }

                schedule(getId(), delay, nextEvent, task);
            } else {
                //SimLogger.printLine("Task #" + task.getCloudletId() + " cannot assign to any VM");
                SimLogger.getInstance().rejectedDueToVMCapacity(task.getCloudletId(), CloudSim.clock(), vmType.ordinal());
            }
        } else {
            //SimLogger.printLine("Task #" + task.getCloudletId() + " cannot assign to any VM");
            SimLogger.getInstance().rejectedDueToBandwidth(task.getCloudletId(), CloudSim.clock(), vmType.ordinal(), delayType);
//            System.err.println("RIFIUTO per banda - task:" + task.getCloudletId() + " device:" + task.getMobileDeviceId());

        }

        return task;
    }


    private void submitTaskToVm(Task task, SimSettings.VM_TYPES vmType) {
        //SimLogger.printLine(CloudSim.clock() + ": Cloudlet#" + task.getCloudletId() + " is submitted to VM#" + task.getVmId());
        schedule(getVmsToDatacentersMap().get(task.getVmId()), 0, CloudSimTags.CLOUDLET_SUBMIT, task);

        SimLogger.getInstance().taskAssigned(task.getCloudletId(),
                task.getAssociatedDatacenterId(),
                task.getAssociatedHostId(),
                task.getAssociatedVmId(),
                vmType.ordinal());
    }

    private Task createTask(TaskProperty edgeTask) {
        UtilizationModel utilizationModel = new UtilizationModelFull(); /*UtilizationModelStochastic*/
        UtilizationModel utilizationModelCPU = getCpuUtilizationModel();

        Task task = new Task(edgeTask.getMobileDeviceId(), ++taskIdCounter,
                edgeTask.getLength(), edgeTask.getPesNumber(),
                edgeTask.getInputFileSize(), edgeTask.getOutputFileSize(),
                utilizationModelCPU, utilizationModel, utilizationModel);

        //set the owner of this task
        task.setUserId(this.getId());
        task.setTaskType(edgeTask.getTaskType());

        if (utilizationModelCPU instanceof CpuUtilizationModel_Custom) {
            ((CpuUtilizationModel_Custom) utilizationModelCPU).setTask(task);
        }

        return task;
    }
}
