package edu.boun.edgecloudsim.energy;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;
import edu.boun.edgecloudsim.utils.SimUtils;

public class DefaultEnergyComputingModel extends EnergyComputingModel {

	public DefaultEnergyComputingModel(int numberOfMobileDevices, double maxActiveConsumption, double idleConsumption) {
		super(numberOfMobileDevices, maxActiveConsumption, idleConsumption);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		//FIXME not used
		cpuEnergyConsumption = 0; //TEST purpose
		networkEnergyConsumption = 0; //TEST purpose
	}


	@Override
	public void updateStaticEnergyConsumption() {
		//TODO updateInterval usually set to 1 in pureedgesim
		cpuEnergyConsumption += getIdleConsumption() / 3600; //* SimSettings.getInstance().updateInterval; 
	}

	@Override
	public double getCpuEnergyConsumption() {//FIXME UNUSED
		return cpuEnergyConsumption;
	}

	@Override
	public double getTotalEnergyConsumption() {
		return cpuEnergyConsumption + networkEnergyConsumption;	
	}

	@Override
	public double getMaxActiveConsumption() {
		return maxActiveConsumption;
	}

	@Override
	public void setMaxActiveConsumption(double maxActiveConsumption) {
		this.maxActiveConsumption = maxActiveConsumption;
	}

	@Override
	public double getIdleConsumption() {
		return idleConsumption;
	}

	@Override
	public void setIdleConsumption(double idleConsumption) {
		this.idleConsumption = idleConsumption;
	}

	@Override
	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	@Override
	public void setBatteryCapacity(double batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	@Override
	public double getBatteryLevelWattHour() {
		if (!isBatteryPowered())
			return -1;
		if (getBatteryCapacity() * this.initialBatteryLevel < getTotalEnergyConsumption())
			return 0;
		return (getBatteryCapacity() * this.initialBatteryLevel) - getTotalEnergyConsumption();
	}

	@Override
	public double getBatteryLevelPercentage() {
		return getBatteryLevelWattHour() * 100 / getBatteryCapacity();
	}

	@Override
	public boolean isBatteryPowered() {
		return isBatteryPowered;
	}

	@Override
	public void setBattery(boolean battery) {
		this.isBatteryPowered = battery;
	}

	@Override
	public void setIntialBatteryPercentage(double batteryLevel) { //FIXME unused
			this.initialBatteryLevel = batteryLevel / 100.0;
	}

	@Override
	public NETWORK_DELAY_TYPES getConnectivityType() {
		return connectivity;
	}

	@Override
	public void setConnectivityType(NETWORK_DELAY_TYPES connectivity) {
		this.connectivity = connectivity;

		if (connectivity == NETWORK_DELAY_TYPES.GSM_DELAY) {
			transmissionEnergyPerBits = SimSettings.getInstance().getCellularDeviceTransmissionWattHourPerBit();
			receptionEnergyPerBits = SimSettings.getInstance().getCellularDeviceReceptionWattHourPerBit();
		} else if (connectivity == NETWORK_DELAY_TYPES.WLAN_DELAY) {
			transmissionEnergyPerBits = SimSettings.getInstance().getWifiDeviceTransmissionWattHourPerBit();
			receptionEnergyPerBits = SimSettings.getInstance().getWifiDeviceReceptionWattHourPerBit();
		}
		
//		usare NETWORK_DELAY_TYPES.WAN

		
		// Ethernet not yet handled
		/*else {
			transmissionEnergyPerBits = SimSettings.getInstance().getEthernetWattHourPerBit() / 2;
			receptionEnergyPerBits = SimSettings.getInstance().getEthernetWattHourPerBit() / 2;
		}*/
	}

	@Override
	public void updatewirelessEnergyConsumption(double sizeInBits, int flag) {
		if (flag == SimUtils.RECEPTION)
			networkEnergyConsumption += sizeInBits * transmissionEnergyPerBits;
		else
			networkEnergyConsumption += sizeInBits * receptionEnergyPerBits;
	}


	@Override
	public void updateDynamicEnergyConsumption(double length, double mipsCapacity) {
		cpuEnergyConsumption += ((getMaxActiveConsumption() - getIdleConsumption()) / 3600 * length / mipsCapacity);
	}
	
	
	/**Consumo energetico della CPU:

	Il consumo energetico della CPU è calcolato principalmente in due modi:
	Quando la CPU è inattiva, viene consumata una quantità fissa di energia specificata dall'attributo idleConsumption.
	Durante l'uso attivo della CPU, viene consumata una quantità di energia proporzionale al tempo di utilizzo e alla capacità di elaborazione (MIPS) della CPU.
	Questo consumo energetico viene calcolato tramite il metodo updateDynamicEnergyConsumption().
	
	Il consumo energetico totale della CPU viene quindi aggiornato tramite il metodo updateStaticEnergyConsumption().
	
	Consumo energetico della rete:
	Il consumo energetico della rete dipende dal tipo di connettività associata al nodo (connectivity).
	Quando vengono trasmesse o ricevuti dati attraverso la rete, viene calcolato il consumo energetico in base alla quantità di dati trasmessi (sizeInBits) e al tipo di trasmissione (flag, che può essere trasmissione o ricezione). Questo consumo energetico viene aggiunto all'attributo networkEnergyConsumption tramite il metodo updatewirelessEnergyConsumption().
	Batteria:
	Se il nodo è alimentato da una batteria (isBatteryPowered è true),
	viene calcolato il livello di carica della batteria in base all'energia consumata dalla CPU e dalla rete. 
	Il livello di carica della batteria è quindi restituito come percentuale della capacità totale della batteria.
	
	Energia totale consumata:
	L'energia totale consumata è la somma del consumo energetico della CPU e del consumo energetico della rete.
	**/

}
