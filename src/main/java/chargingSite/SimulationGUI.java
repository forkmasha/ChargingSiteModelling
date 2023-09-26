package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationGUI {
    public static void runSimulationGUI() {

        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(200, 200, 240));
        frame.setPreferredSize(new Dimension(450, 700));
        frame.setMinimumSize(new Dimension(450, 700));

        JSpinner minArrivalRate = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner arrivalRateStep = createSpinner(0.5, 0.1, Double.MAX_VALUE, 0.1);
        JSpinner maxArrivalRate = createSpinner(25.0, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner numberOfClientTypes = createSpinner(1, 1, 2, 1);
        JSpinner maxEvents = createSpinner(2500, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = createSpinner(5, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = createSpinner(10, 1, Integer.MAX_VALUE, 1);
        JSpinner meanServiceTime = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner maxPower = createSpinner(50, 0, Integer.MAX_VALUE, 1);

        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox<String> queueingType = new JComboBox<>(queueingTypes);

        String[] distributionTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC"};
        JComboBox<String> arrivalType = new JComboBox<>(distributionTypes);
        JComboBox<String> serviceType = new JComboBox<>(distributionTypes);
        JComboBox<String> demandType = new JComboBox<>(distributionTypes);
        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANGD");
        demandType.setSelectedItem("BETA");

        String[] confidenceLevels = {"80", "90", "95", "98", "99"};
        JComboBox<String> confLevel = new JComboBox<>(confidenceLevels);
        confLevel.setSelectedItem("95");

        JButton runSimulation = new JButton("Run Simulation");
        runSimulation.setFont(new Font("Arial", Font.BOLD, 14));
        runSimulation.setForeground(Color.WHITE);

        runSimulation.addActionListener(e -> {
            Simulation simulation = new Simulation();
            simulation.setMIN_ARRIVAL_RATE(getSpinnerValueAsDouble(minArrivalRate));
            simulation.setARRIVAL_RATE_STEP(getSpinnerValueAsDouble(arrivalRateStep));
            simulation.setMAX_ARRIVAL_RATE(getSpinnerValueAsDouble(maxArrivalRate));
            simulation.setSIM_STEPS((int) Math.ceil((simulation.getMAX_ARRIVAL_RATE() - simulation.getMIN_ARRIVAL_RATE()) / simulation.getARRIVAL_RATE_STEP()));
            simulation.setNUMBER_OF_CLIENT_TYPES(getSpinnerValueAsInt(numberOfClientTypes));
            simulation.setMAX_EVENTS(getSpinnerValueAsInt(maxEvents));
            simulation.setNUMBER_OF_SERVERS(getSpinnerValueAsInt(numberOfServers));
            simulation.setQUEUE_SIZE(getSpinnerValueAsInt(queueSize));

            String queueingTypeString = (String) queueingType.getSelectedItem();
            switch (queueingTypeString) {
                case "FIFO" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.FIFO);
                case "LIFO" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.LIFO);
                case "RANDOM" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.RAND);
            }

            simulation.setMEAN_SERVICE_TIME(getSpinnerValueAsDouble(meanServiceTime));

            String arrivalTypeString = (String) arrivalType.getSelectedItem();
            switch (arrivalTypeString) {
                case "GEOMETRIC" -> simulation.setARRIVAL_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.setARRIVAL_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.setARRIVAL_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.setARRIVAL_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.setARRIVAL_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.setARRIVAL_TYPE(DistributionType.BETA);
                case "DETERMINISTIC" -> simulation.setARRIVAL_TYPE(DistributionType.DETERMINISTIC);
            }

            String serviceTypeString = (String) serviceType.getSelectedItem();
            switch (serviceTypeString) {
                case "GEOMETRIC" -> simulation.setSERVICE_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.setSERVICE_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.setSERVICE_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.setSERVICE_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.setSERVICE_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.setSERVICE_TYPE(DistributionType.BETA);
                case "DETERMINISTIC" -> simulation.setSERVICE_TYPE(DistributionType.DETERMINISTIC);
            }

            String demandTypeString = (String) demandType.getSelectedItem();
            switch (demandTypeString) {
                case "GEOMETRIC" -> simulation.setDEMAND_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.setDEMAND_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.setDEMAND_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.setDEMAND_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.setDEMAND_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.setDEMAND_TYPE(DistributionType.BETA);
                case "DETERMINISTIC" -> simulation.setDEMAND_TYPE(DistributionType.DETERMINISTIC);
            }

            int selectedConfidenceLevel = Integer.parseInt((String) confLevel.getSelectedItem());
            simulation.setConfLevel(selectedConfidenceLevel);
            simulation.runSimulation();

            // frame.dispose(); //fix the bug=)

        });

        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setSpinnerModel(minArrivalRate);
        setSpinnerModel(arrivalRateStep);
        setSpinnerModel(maxArrivalRate);
        setSpinnerModel(numberOfClientTypes);
        setSpinnerModel(maxEvents);
        setSpinnerModel(numberOfServers);
        setSpinnerModel(queueSize);
        setSpinnerModel(meanServiceTime);

        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toppanel = createSpinnerPanel("Min Arrival Rate", "Arrival Rate Step", "Max Arrival Rate", minArrivalRate, arrivalRateStep, maxArrivalRate);
        verticalBox.add(toppanel);

        JPanel ProcPanel = new JPanel();
        ProcPanel.setLayout(new GridLayout(22, 1));
        ProcPanel.add(new JLabel("Number of Client Types", SwingConstants.CENTER));
        ProcPanel.add(numberOfClientTypes);
        ProcPanel.add(new JLabel("Max Events", SwingConstants.CENTER));
        ProcPanel.add(maxEvents);
        ProcPanel.add(new JLabel("Number of Servers", SwingConstants.CENTER));
        ProcPanel.add(numberOfServers);
        ProcPanel.add(new JLabel("Queue Size", SwingConstants.CENTER));
        ProcPanel.add(queueSize);
        ProcPanel.add(new JLabel("Queueing Type", SwingConstants.CENTER));
        ProcPanel.add(queueingType);
        ProcPanel.add(new JLabel("Mean Service Time", SwingConstants.CENTER));
        ProcPanel.add(meanServiceTime);
        ProcPanel.add(new JLabel("Arrival Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(arrivalType);
        ProcPanel.add(new JLabel("Service Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(serviceType);
        ProcPanel.add(new JLabel("Demand Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(demandType);
        ProcPanel.add(new JLabel("Confidence Level", SwingConstants.CENTER));
        ProcPanel.add(confLevel);
        ProcPanel.add(new JLabel("Max Charging Power [kW]", SwingConstants.CENTER));
        ProcPanel.add(maxPower);

        ProcPanel.setBackground(new Color(200, 200, 240));
        verticalBox.add(ProcPanel);

        JPanel bottomPanel = new JPanel();
        runSimulation.setForeground(Color.BLACK);
        bottomPanel.setLayout(new GridLayout(1, 1));
        bottomPanel.add(runSimulation);
        bottomPanel.setBackground(new Color(136, 186, 242));

        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close the simulation results?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    private static JSpinner createSpinner(double value, double min, double max, double step) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        setSpinnerModel(spinner);
        return spinner;
    }

    private static JSpinner createSpinner(int value, int min, int max, int step) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        setSpinnerModel(spinner);
        return spinner;
    }

    private static void setSpinnerModel(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            defaultEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    defaultEditor.getTextField().getBorder(),
                    BorderFactory.createEmptyBorder(0, 3, 0, 0)
            ));
        }
    }

    private static double getSpinnerValueAsDouble(JSpinner spinner) {
        return Double.parseDouble(spinner.getValue().toString());
    }

    private static int getSpinnerValueAsInt(JSpinner spinner) {
        return Integer.parseInt(spinner.getValue().toString());
    }

    private static JPanel createSpinnerPanel(String label1, String label2, String label3, JSpinner spinner1, JSpinner spinner2, JSpinner spinner3) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3));
        panel.add(new JLabel(label1, SwingConstants.CENTER));
        panel.add(new JLabel(label2, SwingConstants.CENTER));
        panel.add(new JLabel(label3, SwingConstants.CENTER));
        panel.add(spinner1);
        panel.add(spinner2);
        panel.add(spinner3);
        panel.setBackground(new Color(200, 200, 240));
        return panel;
    }
}