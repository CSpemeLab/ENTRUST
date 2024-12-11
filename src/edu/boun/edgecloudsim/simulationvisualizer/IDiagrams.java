package edu.boun.edgecloudsim.simulationvisualizer;

import edu.boun.edgecloudsim.utils.Coordinates;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IDiagrams {

    default void generateEnergyCharts(List<Coordinates> coordinates, String scenarioName, String orchestretorPolicy) {

    if(chartDisable())
            return;

        Map<Integer, List<Coordinates>> coordinatesById = coordinates.stream()
                .collect(Collectors.groupingBy(Coordinates::getId));

        //todo, filter only for test, remove next line for all data
        //List<Integer> filterId = List.of(1, 2);
        //coordinatesById = coordinatesById.entrySet().stream()
        //.filter(entry -> filterId.contains(entry.getKey()))
        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        generateEnergyCharts(coordinatesById, scenarioName, orchestretorPolicy);
    }

    public void generateEnergyCharts(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy);


    default void generateMapChart(List<Coordinates> coordinates, String scenarioName, String orchestretorPolicy) {
        if(chartDisable())
            return;

        Map<Integer, List<Coordinates>> coordinatesById = coordinates.stream()
                .collect(Collectors.groupingBy(Coordinates::getId));

     //   List<Integer> filterId = List.of(1, 2, 3,4, 5,6,7,8,9);
    //    coordinatesById = coordinatesById.entrySet().stream()
        //        .filter(entry -> filterId.contains(entry.getKey()))
         //       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        generateMapChart(coordinatesById, scenarioName, orchestretorPolicy);
    }
    public void generateMapChart(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy);


    default boolean chartDisable() {
        return true;
    }


}
