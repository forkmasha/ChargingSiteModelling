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

        Simulation simulation = new Simulation();

        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(200, 200, 240)); // Set background color
        frame.setPreferredSize(new Dimension(400, 600));

        JTextField minArrivalRate = new JTextField();
        minArrivalRate.setText("0.5");
        JTextField maxArrivalRate = new JTextField();
        maxArrivalRate.setText("25.0");
        JTextField arrivalRateStep = new JTextField();
        arrivalRateStep.setText("0.5");
        JTextField numberOfClientTypes = new JTextField();
        numberOfClientTypes.setText("1");
        JTextField maxEvents = new JTextField();
        maxEvents.setText("2500");
        JTextField numberOfServers = new JTextField();
        numberOfServers.setText("5");
        JTextField queueSize = new JTextField();
        queueSize.setText("10");
        // JTextField simSteps = new JTextField();
        // simSteps.setText("2500");
        JTextField meanServiceTime = new JTextField();
        meanServiceTime.setText("0.5");
        JTextField confLevel = new JTextField();
        confLevel.setText("98");

        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox queueingType = new JComboBox(queueingTypes);

        String[] DistributionTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC"};
        JComboBox arrivalType = new JComboBox(DistributionTypes);
        JComboBox serviceType = new JComboBox(DistributionTypes);
        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANGD");


        JButton runSimulation = new JButton("Run Simulation");
        runSimulation.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        runSimulation.setForeground(Color.WHITE); // Set text color
        runSimulation.setBackground(new Color(136, 186, 242));// Set background color
        runSimulation.addActionListener(e -> {
            simulation.setMIN_ARRIVAL_RATE(Double.parseDouble(minArrivalRate.getText()));
            simulation.setMAX_ARRIVAL_RATE(Double.parseDouble(maxArrivalRate.getText()));
            simulation.setARRIVAL_RATE_STEP(Double.parseDouble(arrivalRateStep.getText()));
            simulation.setSIM_STEPS((int) Math.ceil((simulation.getMAX_ARRIVAL_RATE() - simulation.getMIN_ARRIVAL_RATE()) / simulation.getARRIVAL_RATE_STEP()));
            simulation.setNUMBER_OF_CLIENT_TYPES(Integer.parseInt(numberOfClientTypes.getText()));
            simulation.setMAX_EVENTS(Integer.parseInt(maxEvents.getText()));
            simulation.setNUMBER_OF_SERVERS(Integer.parseInt(numberOfServers.getText()));
            simulation.setQUEUE_SIZE(Integer.parseInt(queueSize.getText()));
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
            simulation.setConfLevel(Integer.parseInt(confLevel.getText()));
            simulation.runSimulation();
        });
        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Top, left, bottom, right

        setDocumentFilterDouble(minArrivalRate);
        setDocumentFilterDouble(maxArrivalRate);
        setDocumentFilterDouble(arrivalRateStep);
        setDocumentFilterInt(numberOfClientTypes);
        setDocumentFilterInt(maxEvents);
        setDocumentFilterInt(numberOfServers);
        setDocumentFilterInt(queueSize);
        //setDocumentFilterInt(simSteps);
        setDocumentFilterDouble(meanServiceTime);
        setDocumentFilterInt(confLevel);

        // create verticalBox to hold all the input components
        Box verticalBox = Box.createVerticalBox();

        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add empty borders

        verticalBox.add(new JLabel("Min Arrival Rate"));
        verticalBox.add(minArrivalRate);
        verticalBox.add(new JLabel("Max Arrival Rate"));
        verticalBox.add(maxArrivalRate);
        verticalBox.add(new JLabel("Arrival Rate Step"));
        verticalBox.add(arrivalRateStep);
        //verticalBox.add(new JLabel("Simulation Steps"));
        //verticalBox.add(simSteps);
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


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add empty borders
        buttonPanel.add(runSimulation);

        frame.getContentPane().add(runSimulation, BorderLayout.SOUTH);

        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    public static void setDocumentFilterDouble(JTextField textField) {

        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)
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
                if (text.charAt(0) == '0') {
                    return true;
                }
                if (text.contains(".")) {
                    return true;
                }
                try {
                    double value = Double.parseDouble(text);
                    return value > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    public static void setDocumentFilterInt(JTextField textField) {

        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)
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

