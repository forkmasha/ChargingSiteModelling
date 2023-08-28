package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class SimulationGUI {
    public static void runSimulationGUI() {

        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(200, 200, 240));
        frame.setPreferredSize(new Dimension(400, 525));

        JTextField minArrivalRate = new JTextField();
        minArrivalRate.setText("0.5");

        JTextField maxArrivalRate = new JTextField();
        maxArrivalRate.setText("25.0");

        JTextField arrivalRateStep = new JTextField();
        arrivalRateStep.setText("0.5");

        SpinnerModel numberOfClientTypesModel = new SpinnerNumberModel(1, 1, 2, 1);
        JSpinner numberOfClientTypes = new JSpinner(numberOfClientTypesModel);
        SpinnerModel maxEventsModel = new SpinnerNumberModel(2500, 1, Integer.MAX_VALUE, 1);
        JSpinner maxEvents = new JSpinner(maxEventsModel);


        SpinnerModel numberOfServersMod = new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = new JSpinner(numberOfServersMod);


        SpinnerModel queueSizeMod = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = new JSpinner(queueSizeMod);

        JTextField meanServiceTime = new JTextField();
        meanServiceTime.setText("0.5");


        SpinnerModel confLevelMod = new SpinnerNumberModel(98, 95, 99, 1);
        JSpinner confLevel = new JSpinner(confLevelMod);

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
        runSimulation.setBackground(new Color(136, 186, 242));
        runSimulation.addActionListener(e -> {
            Simulation simulation = new Simulation();
            simulation.setMIN_ARRIVAL_RATE(Double.parseDouble(minArrivalRate.getText()));
            simulation.setMAX_ARRIVAL_RATE(Double.parseDouble(maxArrivalRate.getText()));
            simulation.setARRIVAL_RATE_STEP(Double.parseDouble(arrivalRateStep.getText()));
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
            simulation.setMEAN_SERVICE_TIME(Double.parseDouble(meanServiceTime.getText()));
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
            simulation.setConfLevel(Integer.parseInt(confLevel.getValue().toString()));
            simulation.runSimulation();

            frame.dispose();

        });


        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setDocumentFilterDouble(minArrivalRate);
        setDocumentFilterDouble(maxArrivalRate);
        setDocumentFilterDouble(arrivalRateStep);
        setSpinnerModelInt(numberOfClientTypes);
        setSpinnerModelInt(maxEvents);
        setSpinnerModelInt(numberOfServers);
        setSpinnerModelInt(queueSize);
        setDocumentFilterDouble(meanServiceTime);
        setSpinnerModelInt(confLevel);

        Box verticalBox = Box.createVerticalBox();

        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel toppanel = new JPanel();
        toppanel.setLayout(new GridLayout(2, 3));
        toppanel.add(new JLabel("Min Arrival Rate"));
        toppanel.add(new JLabel("Arrival Rate Step"));
        toppanel.add(new JLabel("Max Arrival Rate"));
        toppanel.add(minArrivalRate);
        toppanel.add(maxArrivalRate);
        toppanel.add(arrivalRateStep);
        toppanel.setBackground(new Color(200, 200, 240));

        verticalBox.add(toppanel);

        verticalBox.add(new JLabel("Number of Client Types"));
        verticalBox.add(numberOfClientTypes);
        verticalBox.add(new JLabel("Max Events"));
        verticalBox.add(maxEvents);
        verticalBox.add(new JLabel("Number of Servers"));
        verticalBox.add(numberOfServers);
        verticalBox.add(new JLabel("Queue Size"));
        verticalBox.add(queueSize);
        verticalBox.add(new JLabel("Queueing Type"));
        verticalBox.add(queueingType);
        verticalBox.add(new JLabel("Mean Service Time"));
        verticalBox.add(meanServiceTime);
        verticalBox.add(new JLabel("Arrival Type"));
        verticalBox.add(arrivalType);
        verticalBox.add(new JLabel("Service Type"));
        verticalBox.add(serviceType);
        verticalBox.add(new JLabel("Confidence Level"));
        verticalBox.add(confLevel);

        frame.getContentPane().add(runSimulation, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    public static void setSpinnerModelInt(JSpinner spinner) {
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

    public static void setDocumentFilterDouble(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(0, 7, 0, 0)
        ));
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (isValid(string)) super.insertString(fb, offset, string, attr);
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (isValid(text)) super.replace(fb, offset, length, text, attrs);
            }

            private boolean isValid(String text) {
                try {
                    double value = Integer.parseInt(text);
                    return value >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }
}

