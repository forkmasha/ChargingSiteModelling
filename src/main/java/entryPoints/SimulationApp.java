package entryPoints;

import chargingSite.SimulationGUI;

public class SimulationApp {
    public static void main(String[] args) {
        if(args.length == 0)
        {
            SimulationGUI simulationGUI = new SimulationGUI();
            simulationGUI.runSimulationGUI();
        }
        else if(args.length == 2)
        {
            SimulationGUI simulationGUI = new SimulationGUI();
            simulationGUI.runSimulationConsole(args[0], args[1]);
        }
        else
        {
            System.out.println("Invalid number of arguments. Please provide either 0 or 2 arguments.");
        }
    }
}