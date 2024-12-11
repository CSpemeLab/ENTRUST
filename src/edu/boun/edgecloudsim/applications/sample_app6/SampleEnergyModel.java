package edu.boun.edgecloudsim.applications.sample_app6;

import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;
import edu.boun.edgecloudsim.energy.EnergyComputingModel;

public class SampleEnergyModel extends EnergyComputingModel {

	public SampleEnergyModel(int numberOfMobileDevices, double maxActiveConsumption, double idleConsumption) {
		super(numberOfMobileDevices, maxActiveConsumption, idleConsumption);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStaticEnergyConsumption() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getCpuEnergyConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalEnergyConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxActiveConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxActiveConsumption(double maxActiveConsumption) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getIdleConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIdleConsumption(double idleConsumption) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBatteryCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBatteryCapacity(double batteryCapacity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBatteryLevelWattHour() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBatteryLevelPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isBatteryPowered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBattery(boolean battery) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIntialBatteryPercentage(double batteryLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NETWORK_DELAY_TYPES getConnectivityType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConnectivityType(NETWORK_DELAY_TYPES connectivity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatewirelessEnergyConsumption(double sizeInBits, int flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDynamicEnergyConsumption(double length, double mipsCapacity) {
		// TODO Auto-generated method stub
		
	}

}
