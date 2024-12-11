/*
 * Title:        EdgeCloudSim - Scenario Factory
 * 
 * Description:  Sample scenario factory providing the default
 *               instances of required abstract classes
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.applications.sample_app6;

import edu.boun.edgecloudsim.cloud_server.CloudServerManager;
import edu.boun.edgecloudsim.cloud_server.DefaultCloudServerManager;
import edu.boun.edgecloudsim.core.ScenarioFactoryEnergy;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.DefaultEdgeServerManager;
import edu.boun.edgecloudsim.edge_server.EdgeServerManager;
import edu.boun.edgecloudsim.edge_client.MobileDeviceManager;
import edu.boun.edgecloudsim.edge_client.mobile_processing_unit.MobileServerManager;
import edu.boun.edgecloudsim.energy.DefaultEnergyComputingModel;
import edu.boun.edgecloudsim.mobility.MobilityModel;
import edu.boun.edgecloudsim.mobility.NomadicMobility;
import edu.boun.edgecloudsim.task_generator.IdleActiveLoadGenerator;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;
import edu.boun.edgecloudsim.network.NetworkModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SampleScenarioFactoryEnergy implements ScenarioFactoryEnergy {
	private int numOfMobileDevice;
	private double simulationTime;
	private String orchestratorPolicy;
	private String simScenario;
	private double maxActiveConsumption;
	private double idleConsumption;
	
//	SampleScenarioFactory(int _numOfMobileDevice,
//			double _simulationTime,
//			String _orchestratorPolicy,
//			String _simScenario){
//		orchestratorPolicy = _orchestratorPolicy;
//		numOfMobileDevice = _numOfMobileDevice;
//		simulationTime = _simulationTime;
//		simScenario = _simScenario;
//	}

	SampleScenarioFactoryEnergy(int _numOfMobileDevice,
								double _simulationTime,
								String _orchestratorPolicy,
								String _simScenario,
								double _maxActiveConsumption,
								double _idleConsumption
						  ){
		orchestratorPolicy = _orchestratorPolicy;
		numOfMobileDevice = _numOfMobileDevice;
		simulationTime = _simulationTime;
		simScenario = _simScenario;
		this.idleConsumption = _idleConsumption;
		this.maxActiveConsumption = _maxActiveConsumption;
	}
	
	@Override
	public LoadGeneratorModel getLoadGeneratorModel() {
		return new IdleActiveLoadGenerator(numOfMobileDevice, simulationTime, simScenario);
	}

	@Override
	public EdgeOrchestrator getEdgeOrchestrator() {
		return new SampleEdgeOrchestrator(orchestratorPolicy, simScenario);
	}

	@Override
	public MobilityModel getMobilityModel() {
		return new NomadicMobility(numOfMobileDevice,simulationTime);
	}

	@Override
	public NetworkModel getNetworkModel() {
		return new SampleNetworkModel(numOfMobileDevice, simScenario);
	}

	@Override
	public EdgeServerManager getEdgeServerManager() {
		return new DefaultEdgeServerManager();
	}
	
	@Override
	public CloudServerManager getCloudServerManager() {
		return new DefaultCloudServerManager();
	}

	@Override
	public MobileDeviceManager getMobileDeviceManager() throws Exception {
		return new SampleMobileDeviceManager();
	}

	@Override
	public MobileServerManager getMobileServerManager() {
		return new SampleMobileServerManager(numOfMobileDevice);
	}



	@Override
	public DefaultEnergyComputingModel getDefaultEnergyComputerModel() {
		//todo ramona, energy in progress
		DefaultEnergyComputingModel defaultEnergyComputingModel = new DefaultEnergyComputingModel(numOfMobileDevice, maxActiveConsumption,idleConsumption);
				//	public DefaultEnergyComputingModel(int numberOfMobileDevices, double maxActiveConsumption, double idleConsumption) {
		return defaultEnergyComputingModel;
	}

	@Override
	public String getEnergyModel() { //FIXME unused

			Double result = getDefaultEnergyComputerModel().getTotalEnergyConsumption();


			String resultMsg = String.format("For idleConsumption %.2f and maxActiveConsumption %.2f, il consumo è %.2f", idleConsumption, maxActiveConsumption, result);
			return resultMsg;

		}
	}
