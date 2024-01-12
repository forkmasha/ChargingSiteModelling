package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;

public class SimulationGUI {
    private static final Color BLUE = new Color(173, 216, 230);
    private static final Color LIGHT_BLUE = new Color(200, 200, 240);
    private static final Color DARK_BLUE = new Color(136, 186, 242);
    private static final Color ORANGE = new Color(255, 175, 128);
    private static final Color RED = new Color(255, 102, 102);

    public static void runSimulationGUI() {
        JFrame frame = createSimulationFrame();
        addWindowCloseListener(frame);
        frame.setVisible(true);
    }

    private static JFrame createSimulationFrame() {

        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(LIGHT_BLUE);
        frame.setPreferredSize(new Dimension(450, 850)); //450 775
        frame.setMinimumSize(new Dimension(450, 840));


        //JSpinner minArrivalRate = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner numberOfSteps = createSpinner(50, 0, Integer.MAX_VALUE, 1);
        JSpinner maxArrivalRate = createSpinner(25.0, 0.0, Double.MAX_VALUE, 0.1);

        JSpinner numberOfClientTypes = createSpinner(1, 1, 2, 1);
        JSpinner maxEvents = createSpinner(25000, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = createSpinner(5, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = createSpinner(10, 1, Integer.MAX_VALUE, 1);
        JSpinner meanServiceTime = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanChargingDemand = createSpinner(0.8, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity = createSpinner(60.0, 0.0, 200.0, 5);

        JSpinner maxSitePower = createSpinner(300, 1.0, Double.MAX_VALUE, 1);
        JSpinner maxPointPower = createSpinner(100, 1.0, Double.MAX_VALUE, 1);
        JSpinner maxEVPower = createSpinner(150, 1.0, Double.MAX_VALUE, 1);

        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox<String> queueingType = new JComboBox<>(queueingTypes);


        String[] distributionTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC", "LOMAX"};
        JComboBox<String> arrivalType = new JComboBox<>(distributionTypes);
        JComboBox<String> serviceType = new JComboBox<>(distributionTypes);
        JComboBox<String> demandType = new JComboBox<>(distributionTypes);
        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANG");
        demandType.setSelectedItem("BETA");

        String[] confidenceLevels = {"80", "90", "95", "98", "99"};
        JComboBox<String> confLevel = new JComboBox<>(confidenceLevels);
        confLevel.setSelectedItem("95");

        queueingType.setUI(new CustomComboBoxUI());
        arrivalType.setUI(new CustomComboBoxUI());
        serviceType.setUI(new CustomComboBoxUI());
        demandType.setUI(new CustomComboBoxUI());
        //confLevel.setUI(new CustomComboBoxUI()); //

        arrivalType.setBackground(Color.WHITE);
        serviceType.setBackground(Color.white);
        queueingType.setBackground(Color.white);
        demandType.setBackground(Color.white);
        confLevel.setBackground(Color.WHITE); //


        JButton runSimulation = new JButton("Run Simulation");
        configureButton(runSimulation);


        runSimulation.addActionListener(e -> {

            double maxSitePowerValue = getSpinnerValueAsDouble(maxSitePower);
            double maxPointPowerValue = getSpinnerValueAsDouble(maxPointPower);
            double maxEVPowerValue = getSpinnerValueAsDouble(maxEVPower);
            double meanChargingDemandValue = getSpinnerValueAsDouble(meanChargingDemand);

            Simulation simulation = new Simulation();
            //simulation.setMIN_ARRIVAL_RATE(getSpinnerValueAsDouble(minArrivalRate));
            //simulation.setARRIVAL_RATE_STEP((getSpinnerValueAsDouble(maxArrivalRate)-getSpinnerValueAsDouble(minArrivalRate))/(getSpinnerValueAsInt(numberOfSteps)-1));
            simulation.setARRIVAL_RATE_STEP((getSpinnerValueAsDouble(maxArrivalRate) / getSpinnerValueAsInt(numberOfSteps)));
            simulation.setMIN_ARRIVAL_RATE(simulation.getARRIVAL_RATE_STEP());
            simulation.setMAX_ARRIVAL_RATE(getSpinnerValueAsDouble(maxArrivalRate));
            ;
            //simulation.setSIM_STEPS((int) Math.ceil((simulation.getMAX_ARRIVAL_RATE() - simulation.getMIN_ARRIVAL_RATE()) / simulation.getARRIVAL_RATE_STEP()));
            simulation.setSIM_STEPS(getSpinnerValueAsInt(numberOfSteps));
            simulation.setNUMBER_OF_CLIENT_TYPES(getSpinnerValueAsInt(numberOfClientTypes));
            simulation.setMAX_EVENTS(getSpinnerValueAsInt(maxEvents));
            simulation.setNUMBER_OF_SERVERS(getSpinnerValueAsInt(numberOfServers));
            simulation.setQUEUE_SIZE(getSpinnerValueAsInt(queueSize));

            simulation.setMaxSitePower((int) maxSitePowerValue);     //REDO
            simulation.setMaxPointPower((int) maxPointPowerValue);
            simulation.setMaxEvPower((int) maxEVPowerValue);
            simulation.setMeanChargingDemand(meanChargingDemandValue);

            simulation.setBatteryCapacity(getSpinnerValueAsDouble(batteryCapacity));


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
                case "LOMAX" -> simulation.setARRIVAL_TYPE(DistributionType.LOMAX);
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
                case "LOMAX" -> simulation.setSERVICE_TYPE(DistributionType.LOMAX);
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
                case "LOMAX" -> simulation.setDEMAND_TYPE(DistributionType.LOMAX);
            }

            int selectedConfidenceLevel = Integer.parseInt((String) confLevel.getSelectedItem());
            simulation.setConfLevel(selectedConfidenceLevel);

            simulation.runSimulation();

            // frame.dispose(); //fix the bug=)

        });


        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        //setSpinnerModel(minArrivalRate);
        setSpinnerModel(numberOfSteps);
        setSpinnerModel(maxArrivalRate);
        setSpinnerModel(numberOfClientTypes);
        setSpinnerModel(maxEvents);
        setSpinnerModel(numberOfServers);
        setSpinnerModel(queueSize);
        setSpinnerModel(meanServiceTime);

        setSpinnerModel(maxSitePower);
        setSpinnerModel(maxPointPower);
        setSpinnerModel(maxEVPower);
        setSpinnerModel(meanChargingDemand);
        setSpinnerModel(batteryCapacity);


        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



        //JPanel ProcPanel0 = new JPanel();
        //ProcPanel0.setLayout(new GridLayout(10, 1));

        //ProcPanel0.setBackground(LIGHT_BLUE);
        //verticalBox.add(ProcPanel0);

        //JPanel toPanel = createSpinnerPanel("Min Arrival Rate", "Number of Steps", "Max Arrival Rate", minArrivalRate, numberOfSteps, maxArrivalRate);
        //JPanel toPanel = createSpinnerPanel("",);
        //verticalBox.add(toPanel);

        JPanel ProcPanel = new JPanel();
        ProcPanel.setLayout(new GridLayout(28, 1));

        ProcPanel.add(new JLabel("Number of Steps", SwingConstants.CENTER));
        ProcPanel.add(numberOfSteps);
        ProcPanel.add(new JLabel("Max Events per Step", SwingConstants.CENTER));
        ProcPanel.add(maxEvents);
        ProcPanel.add(new JLabel("Confidence Interval Level", SwingConstants.CENTER));
        ProcPanel.add(confLevel);
        ProcPanel.add(new JLabel("Arrival Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(arrivalType);
        ProcPanel.add(new JLabel("Max Mean Arrival Rate", SwingConstants.CENTER));
        ProcPanel.add(maxArrivalRate);
        ProcPanel.add(new JLabel("Number of Servers", SwingConstants.CENTER));
        ProcPanel.add(numberOfServers);
        ProcPanel.add(new JLabel("Queue Size", SwingConstants.CENTER));
        ProcPanel.add(queueSize);
        ProcPanel.add(new JLabel("Queueing Type", SwingConstants.CENTER));
        ProcPanel.add(queueingType);
        ProcPanel.add(new JLabel("Service Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(serviceType);
        ProcPanel.add(new JLabel("Mean Service Time", SwingConstants.CENTER));
        ProcPanel.add(meanServiceTime);
        ProcPanel.add(new JLabel("Number of Client Types", SwingConstants.CENTER));
        ProcPanel.add(numberOfClientTypes);
        ProcPanel.add(new JLabel("Demand Distribution Type", SwingConstants.CENTER));
        ProcPanel.add(demandType);
        ProcPanel.add(new JLabel("Mean Charging Demand", SwingConstants.CENTER));
        ProcPanel.add(meanChargingDemand);
        ProcPanel.add(new JLabel("Battery Capacity", SwingConstants.CENTER));
        ProcPanel.add(batteryCapacity);

        ProcPanel.setBackground(LIGHT_BLUE);
        verticalBox.add(ProcPanel);

        JPanel toPanel2 = createSpinnerPanel("Max Site Power", "Max Point Power", "Max EV Power", maxSitePower, maxPointPower, maxEVPower);
        verticalBox.add(toPanel2);

        //JPanel ProcPanel2 = new JPanel();
        //ProcPanel0.setLayout(new GridLayout(0, 1));

        //ProcPanel.setBackground(LIGHT_BLUE);
        //verticalBox.add(ProcPanel);

        JPanel bottomPanel = new JPanel();

        runSimulation.setForeground(Color.BLACK);
        bottomPanel.setBackground(DARK_BLUE);
        bottomPanel.setLayout(new GridLayout(1, 1));
        bottomPanel.add(runSimulation);

        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        return frame;
    }

    private static void addWindowCloseListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close all simulation results?", "Exit all confirmation", JOptionPane.YES_NO_OPTION);
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
        panel.setBackground(LIGHT_BLUE);
        return panel;
    }

    private static JPanel createSpinnerPanel(String label1, String label2, JSpinner spinner1, JSpinner spinner2) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel(label1, SwingConstants.CENTER));
        panel.add(new JLabel(label2, SwingConstants.CENTER));
        panel.add(spinner1);
        panel.add(spinner2);
        panel.setBackground(LIGHT_BLUE);
        return panel;
    }

    private static JPanel createSpinnerPanel(String label1, JSpinner spinner1) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel(label1, SwingConstants.CENTER));
        panel.add(spinner1);
        panel.setBackground(LIGHT_BLUE);
        return panel;
    }

    private static void configureButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(RED);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(RED);
            }
        });
    }


    static class CustomComboBoxUI extends BasicComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 25));
            return popup;
        }

        @Override
        public ListCellRenderer<Object> createRenderer() {
            return new BasicComboBoxRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (isSelected) {
                        comp.setBackground(BLUE);
                    } else {
                        comp.setBackground(Color.WHITE);
                    }

                    JLabel label = (JLabel) comp;
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

                    return comp;
                }
            };
        }
    }
}