package edu.boun.edgecloudsim.energy;

import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;

public abstract class EnergyComputingModel {
	protected int numberOfMobileDevices; //*from edgecloudsim model*

	protected double maxActiveConsumption; // Consumed energy when the cpu is operating at 100% in Watt
	protected double idleConsumption; // Consumed energy when idle (in Watt)
	protected double cpuEnergyConsumption = 0;
	protected double batteryCapacity;
	protected double initialBatteryLevel = 1; //FIXME useless
	protected NETWORK_DELAY_TYPES connectivity; //FIXME check if it's ok use NETWORK_DELAY_TYPES
	protected boolean isBatteryPowered = false;
//	public static final int TRANSMISSION = 0; // used to update edge devices batteries
//	public static final int RECEPTION = 1;
//	/**
//	 * An attribute that implements the Null Object Design Pattern to avoid
//	 * {@link NullPointerException} when using the NULL object instead of
//	 * attributing null to EnergyModelComputingNode variables.
//	 */
//	public static final EnergyModel NULL = EnergyModel.getInstance();
	protected double networkEnergyConsumption;
	protected double transmissionEnergyPerBits;
	protected double receptionEnergyPerBits;

	public EnergyComputingModel(int numberOfMobileDevices,double maxActiveConsumption, double idleConsumption) {

		this.numberOfMobileDevices = numberOfMobileDevices;//*from edgecloudsim model*

		this.setMaxActiveConsumption(maxActiveConsumption);
		this.setIdleConsumption(idleConsumption);
	}
	
	
	/**
	 * initializes custom energy model
	 */
	public abstract void initialize();
	
	
	/**
	 * 	Il consumo energetico totale della CPU viene aggiornato tramite il metodo updateStaticEnergyConsumption().
	 */
	public abstract void updateStaticEnergyConsumption();

	public abstract double getCpuEnergyConsumption();

	/**
	 * Energia totale consumata
	 * @return somma del consumo energetico della CPU e del consumo energetico della rete
	 */
	public abstract double getTotalEnergyConsumption();

	public abstract double getMaxActiveConsumption();

	public abstract void setMaxActiveConsumption(double maxActiveConsumption);

	public abstract double getIdleConsumption();

	public abstract void setIdleConsumption(double idleConsumption);

	public abstract double getBatteryCapacity();

	public abstract void setBatteryCapacity(double batteryCapacity);

	
	/**
	 * 
	Se il nodo è alimentato da una batteria (isBatteryPowered è true),
	viene calcolato il livello di carica della batteria in base all'energia consumata dalla CPU e dalla rete. 
	Il livello di carica della batteria è quindi restituito come percentuale della capacità totale della batteria.
	
	 * @return -1 if not battery powered else 0
	 */
	public abstract double getBatteryLevelWattHour();

	public abstract double getBatteryLevelPercentage();

	public abstract boolean isBatteryPowered();

	public abstract void setBattery(boolean battery);

	public abstract void setIntialBatteryPercentage(double batteryLevel);

	public abstract NETWORK_DELAY_TYPES getConnectivityType();

	public abstract void setConnectivityType(NETWORK_DELAY_TYPES connectivity);

	
	/**
	 * calcolo il consumo energetico wireless
	 * 
	 * @param sizeInBits quantità di dati trasmessi
	 * @param flag tipo di trasmissione (TRANSMISSION o RECEPTION )
	 */
	public abstract void updatewirelessEnergyConsumption(double sizeInBits, int flag);

	/**
	Durante l'uso attivo della CPU, viene consumata una quantità di energia proporzionale al tempo 
	di utilizzo e alla capacità di elaborazione (MIPS) della CPU.
	Questo consumo energetico viene calcolato tramite il metodo updateDynamicEnergyConsumption().
	 */
	public abstract void updateDynamicEnergyConsumption(double length, double mipsCapacity);

}
