package edu.boun.edgecloudsim.edge_client.mobile_processing_unit;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.energy.DefaultEnergyComputingModel;
import edu.boun.edgecloudsim.utils.DeadHost;
import edu.boun.edgecloudsim.utils.SimUtils;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.List;

public class MobileHostEnergy extends MobileHost {

    private DefaultEnergyComputingModel energyModel;

    private Double batteryLevel;
    private boolean isDead;
    protected double deathTime;
    private double batteryCapacity;
    double energyAllVM = 0;
    private DeadHost deadlisthost;

    public MobileHostEnergy(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler, DefaultEnergyComputingModel _energyModel, Double _batteryCapacity) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
        energyModel = _energyModel;
//        batteryLevel = 500.0; // Math.round(Math.random() * 10000) / 100.0;
        batteryLevel = SimUtils.getRandomDoubleNumber(SimSettings.getInstance().getMIN_BATT_PERC(), SimSettings.getInstance().getMAX_BATT_PERC());
//        System.out.println("BATTERY LEVEL: "+batteryLevel);
        batteryCapacity = _batteryCapacity;
        
        energyModel.setBattery(SimSettings.getInstance().isBATTERY());
        
        energyModel.setBatteryCapacity(_batteryCapacity*batteryLevel/100);

        isDead = false;
        deadlisthost = DeadHost.getInstance();
    }

    /**
     * this method is used to update the status of the device
     * if the battery level is less than or equal to 0, the device is considered dead
     * and the death time is set to the current time
     * the static energy consumption is updated
     * if the device is dead, it cannot do anything
     * if the device is battery powered and the battery level is less than or equal to 0, the device is considered dead
     * and the death time is set to the current time
     * */
    public void updateStatus() {
        if (isDead())
            return;

        // Update the static energy consumption, the dynamic one is measure separately
        // in DefaultComputingNode.startExecution() for performance and accuracy reasons
        getEnergyModel().updateStaticEnergyConsumption();//FIXME su pure edge è usata questa funzione


        if (getEnergyModel().isBatteryPowered() && getEnergyModel().getBatteryLevelWattHour() <= 0) {
            setDeath(true, CloudSim.clock());//FIXME non so se è corretto il clock

            //  System.out.println("SONO MORTO NON POSSO FARE NULLA");
        }
    }

    protected void setDeath(Boolean dead, double time) {
        isDead = dead;
        deathTime = time;

    }


    public boolean isDead() {
        return isDead;
    }

    public DefaultEnergyComputingModel getEnergyModel() {
        return energyModel;
    }

    public void setEnergyModel(DefaultEnergyComputingModel energyModel) {
        this.energyModel = energyModel;
    }

    public Double getBatteryLevel() {//FIXME unused
        return energyModel.getBatteryLevelWattHour();
    }

    /**
     * this method is used to update the battery level of the device
     * the battery level is updated by subtracting the energy consumed by the device
     * if the battery level is less than the energy consumed by the device, the device is considered dead
     * and the battery level is set to 0
     */
    public void updateBatteryLevel() {
    	
//    	System.err.println("mobile host ["+this.getId()+"] battery"+batteryLevel);

    	Double percentageConsumed;
    	if (energyAllVM > 0) 
    	    percentageConsumed = energyAllVM / batteryCapacity;
    	else 
    	    percentageConsumed = 0.0;

    	if (batteryLevel >= percentageConsumed) 
    	    batteryLevel -= percentageConsumed;
    	else 
    	    batteryLevel = 0.0;
//        energyModel.setBatteryCapacity(batteryLevel);    
//        return batteryLevel;
    }

    /**
     * Calcola l'energia consumata dalla macchina virtuale.
     *
     * @param timePassed tempo trascorso per il calcolo dell'energia
     * @return l'energia consumata come valore double
     *
     * <p>
     * Fattori considerati per il calcolo dell'energia:
     * <ul>
     *   <li>MIPS (Million Instructions Per Second): Rappresenta la capacità di elaborazione della VM, che influisce sul consumo energetico in base alla quantità di lavoro svolto.</li>
     *   <li>Numero di PEs (Processing Elements): Indica il numero di elementi di elaborazione della VM, che può influenzare il consumo energetico in base alla parallelizzazione delle operazioni.</li>
     *   <li>RAM: La quantità di memoria RAM allocata per la VM, che influisce sul consumo energetico in base alla gestione e all'accesso alla memoria.</li>
     *   <li>Bandwidth (Larghezza di banda): La larghezza di banda disponibile per la VM, che influisce sul consumo energetico in base al trasferimento di dati in rete o tra dispositivi.</li>
     *   <li>Dimensione della VM: La dimensione della VM, che può influenzare il consumo energetico in base alla gestione dello spazio su disco o di altri dispositivi di memorizzazione.</li>
     *   <li>Stato della VM: Indica se la VM è in fase di migrazione, che può influenzare il consumo energetico in base alle operazioni necessarie per la migrazione stessa.</li>
     *   <li>Utilizzo totale della CPU: L'utilizzo totale della CPU da parte della VM, che può influenzare il consumo energetico in base alla quantità di lavoro eseguito.</li>
     * </ul>
     * </p>
     */
    public double energyConsumption(double timePassed) {
        for (Vm vm : getVmList()) {
            double energyCurrentVm = 0;
            double mipsTotali = vm.getTotalUtilizationOfCpuMips(timePassed);
            if (mipsTotali > 0)
                energyModel.updateDynamicEnergyConsumption(vm.getSize(), mipsTotali);
//            if (this.getId()==10)
//            	energyModel.initialize();
            
            energyAllVM = energyModel.getTotalEnergyConsumption();
//            double cpu = energyModel.getCpuEnergyConsumption();           
//        	System.err.println("mobile host ["+this.getId()+"] battery"+batteryLevel+" energy NET: "+ (energyAllVM - cpu) + " energy cpu: " + cpu );
            
            this.updateBatteryLevel();
            
//            if(batteryLevel.equals(0.0)){
            if(energyModel.getBatteryCapacity()<= energyAllVM){
//            	System.err.println("mobile host ["+this.getId()+"] battery"+batteryLevel+" energy consumed: "+energyAllVM);
            	
                setDeath(true, CloudSim.clock());
                //aggiungo alla lista di dispositivi morti
                deadlisthost.addMobileHost(getId());
            }
            
            
            
        }
        return energyAllVM;
    }


}
