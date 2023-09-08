package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;
import javax.swing.*;
import java.awt.*;

public class SimulationGUI {
    public static void runSimulationGUI() {

        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(200, 200, 240));
        frame.setPreferredSize(new Dimension(450, 630));
        frame.setMinimumSize(new Dimension(450, 630));

        SpinnerModel minArrivalRateModel = new SpinnerNumberModel(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner minArrivalRate = new JSpinner(minArrivalRateModel);

        SpinnerModel arrivalRateStepModel = new SpinnerNumberModel(0.5, 0.1, Double.MAX_VALUE, 0.1);
        JSpinner arrivalRateStep = new JSpinner(arrivalRateStepModel);

        SpinnerModel maxArrivalRateModel = new SpinnerNumberModel(25.0, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner maxArrivalRate = new JSpinner(maxArrivalRateModel);

        SpinnerModel numberOfClientTypesModel = new SpinnerNumberModel(1, 1, 2, 1);
        JSpinner numberOfClientTypes = new JSpinner(numberOfClientTypesModel);
        SpinnerModel maxEventsModel = new SpinnerNumberModel(2500, 1, Integer.MAX_VALUE, 1);
        JSpinner maxEvents = new JSpinner(maxEventsModel);


        SpinnerModel numberOfServersMod = new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = new JSpinner(numberOfServersMod);

        SpinnerModel queueSizeMod = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = new JSpinner(queueSizeMod);

        SpinnerModel meanServiceTimeModel = new SpinnerNumberModel(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanServiceTime = new JSpinner(meanServiceTimeModel);

        /*SpinnerModel confLevelMod = new SpinnerListModel(new Integer[]{80, 90, 95, 98, 99});
        JSpinner confLevel = new JSpinner(confLevelMod);*/

        String[] confidenceLevels = {"80", "90", "95", "98", "99"};
        JComboBox<String> confLevel = new JComboBox<>(confidenceLevels);

        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox queueingType = new JComboBox(queueingTypes);

        String[] DistributionTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC"};
        JComboBox arrivalType = new JComboBox(DistributionTypes);
        JComboBox serviceType = new JComboBox(DistributionTypes);
        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANGD");

        JButton runSimulation = new JButton("Run Simulation");
        runSimulation.setFont(new Font("Arial", Font.BOLD, 14));
        runSimulation.setForeground(Color.WHITE);

        runSimulation.addActionListener(e -> {
            Simulation simulation = new Simulation();
            simulation.setMIN_ARRIVAL_RATE(Double.parseDouble(minArrivalRate.getValue().toString()));
            simulation.setARRIVAL_RATE_STEP(Double.parseDouble(arrivalRateStep.getValue().toString()));
            simulation.setMAX_ARRIVAL_RATE(Double.parseDouble(maxArrivalRate.getValue().toString()));
            simulation.setSIM_STEPS((int) Math.ceil((simulation.getMAX_ARRIVAL_RATE() - simulation.getMIN_ARRIVAL_RATE()) / simulation.getARRIVAL_RATE_STEP()));
            simulation.setNUMBER_OF_CLIENT_TYPES(Integer.parseInt(numberOfClientTypes.getValue().toString()));
            simulation.setMAX_EVENTS((Integer) maxEvents.getValue());
            simulation.setNUMBER_OF_SERVERS(Integer.parseInt(numberOfServers.getValue().toString()));
            simulation.setQUEUE_SIZE(Integer.parseInt(queueSize.getValue().toString()));
            String queueingTypeString = (String) queueingType.getSelectedItem();
            switch (queueingTypeString) {
                case "FIFO" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.FIFO);
                case "LIFO" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.LIFO);
                case "RANDOM" -> simulation.setQUEUEING_TYPE(Queue.QueueingType.RAND);
            }
            simulation.setMEAN_SERVICE_TIME(Double.parseDouble(meanServiceTime.getValue().toString()));
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

           /* String[] confidenceLevels = {"80", "90", "95", "98", "99"};
            simulation.setConfLevel(Integer.parseInt(confLevel.getValue().toString()));*/

            int selectedConfidenceLevel = Integer.parseInt((String) confLevel.getSelectedItem());
            simulation.setConfLevel(selectedConfidenceLevel);
            simulation.runSimulation();

            // frame.dispose(); //fix the bug=)

        });

        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setSpinnerModelDouble(minArrivalRate);
        setSpinnerModelDouble(arrivalRateStep);
        setSpinnerModelDouble(maxArrivalRate);
        setSpinnerModelInt(numberOfClientTypes);
        setSpinnerModelInt(maxEvents);
        setSpinnerModelInt(numberOfServers);
        setSpinnerModelInt(queueSize);
        setSpinnerModelDouble(meanServiceTime);
       // setSpinnerModelInt(confLevel);

        Box verticalBox = Box.createVerticalBox();

        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel toppanel = new JPanel();
        toppanel.setLayout(new GridLayout(2, 3));
        toppanel.add(new JLabel("Min Arrival Rate", SwingConstants.CENTER));
        toppanel.add(new JLabel("Arrival Rate Step", SwingConstants.CENTER));
        toppanel.add(new JLabel("Max Arrival Rate", SwingConstants.CENTER));
        toppanel.add(minArrivalRate);
        toppanel.add(arrivalRateStep);
        toppanel.add(maxArrivalRate);
        toppanel.setBackground(new Color(200, 200, 240));

        verticalBox.add(toppanel);

        JPanel ProcPanel = new JPanel();
        ProcPanel.setLayout(new GridLayout(18, 1));

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
        ProcPanel.add(new JLabel("Arrival Type", SwingConstants.CENTER));
        ProcPanel.add(arrivalType);
        ProcPanel.add(new JLabel("Service Type", SwingConstants.CENTER));
        ProcPanel.add(serviceType);
        ProcPanel.add(new JLabel("Confidence Level", SwingConstants.CENTER));
        ProcPanel.add(confLevel);
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
    }

    private static void setSpinnerModelDouble(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            defaultEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    defaultEditor.getTextField().getBorder(),
                    BorderFactory.createEmptyBorder(0, 7, 0, 0)
            ));
        }
    }

    private static void setSpinnerModelInt(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            defaultEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    defaultEditor.getTextField().getBorder(),
                    BorderFactory.createEmptyBorder(0, 7, 0, 0)
            ));
        }
    }
}

