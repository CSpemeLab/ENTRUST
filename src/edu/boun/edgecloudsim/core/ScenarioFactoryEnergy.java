package edu.boun.edgecloudsim.core;

import edu.boun.edgecloudsim.energy.DefaultEnergyComputingModel;

public interface ScenarioFactoryEnergy extends ScenarioFactory{
    /**
     * Metodo custom per energia
     * */
    public String getEnergyModel();

    public DefaultEnergyComputingModel getDefaultEnergyComputerModel();

}
