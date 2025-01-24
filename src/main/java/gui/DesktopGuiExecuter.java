package gui;

import chargingSite.SimulationGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopGuiExecuter implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        SimulationGUI simulationGUI = new SimulationGUI();
        simulationGUI.runSimulationGUI();
    }
}
