package entryPoints;

import chargingSite.SimulationGUI;

public class MainToRunFromConsole {
    public static void main(String[] args) {
        SimulationGUI simulationGUI = new SimulationGUI();
        simulationGUI.runSimulationConsole("SimulationParameters.xml", "");
    }
}