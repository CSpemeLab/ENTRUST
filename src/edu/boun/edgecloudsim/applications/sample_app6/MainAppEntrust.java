/*
 * Title:        EdgeCloudSim - Main Application
 * 
 * Description:  Main application for Sample App3
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.applications.sample_app6;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import for energy values

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import edu.boun.edgecloudsim.core.ScenarioFactoryEnergy;
import edu.boun.edgecloudsim.core.SimManagerEnergy;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;

public class MainAppEntrust {
	
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		//disable console output of cloudsim library
		Log.disable();


		
		//enable console output and file output of this application
		SimLogger.enablePrintLog();
		ScenarioFactoryEnergy sampleFactory = null;
		
		int iterationNumber = 1;
		String configFile = "";
		String outputFolder = "";
		String edgeDevicesFile = "";
		String applicationsFile = "";
		if (args.length == 5){
			configFile = args[0];
			edgeDevicesFile = args[1];
			applicationsFile = args[2];
			outputFolder = args[3];
			iterationNumber = Integer.parseInt(args[4]);
		}
		else{
			SimLogger.printLine("Simulation setting file, output folder and iteration number are not provided! Using default ones...");
			configFile = "scripts/sample_app6/config/default_config.properties";
			applicationsFile = "scripts/sample_app6/config/applications.xml";
			edgeDevicesFile = "scripts/sample_app6/config/edge_devices.xml";
			outputFolder = "sim_results/ite" + iterationNumber;
		}

		//load settings from configuration file
		SimSettings SS = SimSettings.getInstance();
		if(SS.initialize(configFile, edgeDevicesFile, applicationsFile,true) == false){
			SimLogger.printLine("cannot initialize simulation settings!");
			System.exit(0);
		}
		
		if(SS.getFileLoggingEnabled()){
			SimLogger.enableFileLog();
			SimUtils.cleanOutputFolder(outputFolder);
		}
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date SimulationStartDate = Calendar.getInstance().getTime();
		String now = df.format(SimulationStartDate);
		SimLogger.printLine("Simulation started at " + now);
		SimLogger.printLine("----------------------------------------------------------------------");
		SimManagerEnergy manager = null;
		for(int j=SS.getMinNumOfMobileDev(); j<=SS.getMaxNumOfMobileDev(); j+=SS.getMobileDevCounterSize())
		{			
			SS.generateMobileConfig(j); //Random setup of mobile devices
			//End of orchestrators loop
			for(int k=0; k<SS.getSimulationScenarios().length; k++)
				for (int i = 0; i < SS.getOrchestratorPolicies().length; i++) {
					
					String simScenario = SS.getSimulationScenarios()[k];
					String orchestratorPolicy = SS.getOrchestratorPolicies()[i];
					Date ScenarioStartDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioStartDate);

					SimLogger.printLine("Scenario started at " + now);
					SimLogger.printLine("Scenario: " + simScenario + " - Policy: " + orchestratorPolicy + " - #iteration: " + iterationNumber);
					SimLogger.printLine("Duration: " + SS.getSimulationTime() / 60 + " min (warm up period: " + SS.getWarmUpPeriod() / 60 + " min) - #devices: " + j);
					SimLogger.getInstance().simStarted(outputFolder, "SIMRESULT_" + simScenario + "_" + orchestratorPolicy + "_" + j + "DEVICES");

					try {
						// First step: Initialize the CloudSim package. It should be called
						// before creating any entities.
						int num_user = 2;   // number of grid users
						Calendar calendar = Calendar.getInstance();
						boolean trace_flag = false;  // mean trace events

						// Initialize the CloudSim library
						CloudSim.init(num_user, calendar, trace_flag, 0.01);

						// Generate EdgeCloudsim Scenario Factory
						sampleFactory = new SampleScenarioFactoryEnergy(j, SS.getSimulationTime(), orchestratorPolicy, simScenario, SS.getEnergyConsumpitonMax_mobile(), SS.getEnergyConsumptionIdle_mobile());

						// Generate EdgeCloudSim Simulation Manager
//						SimManager manager = new SimManager(sampleFactory, j, simScenario, orchestratorPolicy);

						manager = new SimManagerEnergy(sampleFactory, j, simScenario, orchestratorPolicy);

						// Start simulation
						manager.startSimulation();
					} catch (Exception e) {
						SimLogger.printLine("The simulation has been terminated due to an unexpected error");
						e.printStackTrace();
						System.exit(0);
					}
					if(manager != null)
						manager.createDiagram(simScenario, orchestratorPolicy);
					Date ScenarioEndDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioEndDate);


					SimLogger.printLine("Scenario finished at " + now + ". It took " + SimUtils.getTimeDifference(ScenarioStartDate, ScenarioEndDate));
					SimLogger.printLine("----------------------------------------------------------------------");

					//	Print initial energy values
					//SimLogger.printLine("connectivity type " + SS.getCONNECTIVITY());
					Map<String,Double> energyValue = new HashMap<>();

					energyValue.put("BATTERYCAPACITY",SS.getBATTERYCAPACITY());
					energyValue.put("ENERGYCONSUMPTIONMAX_MOBILE",SS.getEnergyConsumpitonMax_mobile());
					energyValue.put("ENERGYCONSUMPTIONIDLE_MOBILE",SS.getEnergyConsumptionIdle_mobile());

					energyValue.entrySet().stream()
							.map(eValue->eValue.getKey() + " : "+eValue.getValue())
							.forEach(SimLogger::printLine);



			}//End of scenarios loop
		}//End of mobile devices loop


		Date SimulationEndDate = Calendar.getInstance().getTime();
		now = df.format(SimulationEndDate);
		SimLogger.printLine("Simulation finished at " + now +  ". It took " + SimUtils.getTimeDifference(SimulationStartDate,SimulationEndDate));
	}
}
