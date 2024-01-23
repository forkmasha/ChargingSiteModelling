package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;

public class SimulationGUI {
    private static final Color LIGHT_PINK = new Color(255, 182, 193);
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color PEACH = new Color(255, 218, 185);
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
        //  frame.getContentPane().setBackground(LIGHT_BLUE);
        frame.setPreferredSize(new Dimension(450, 850)); //450 775
//        frame.setMinimumSize(new Dimension(450, 840));

        //JSpinner minArrivalRate = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner numberOfSteps = createSpinner(50, 0, Integer.MAX_VALUE, 1);
        JSpinner maxArrivalRate = createSpinner(25.0, 0.0, Double.MAX_VALUE, 0.1);

        JSpinner numberOfClientTypes = createSpinner(1, 1, 3, 1);

        JSpinner maxEvents = createSpinner(25000, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = createSpinner(5, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = createSpinner(10, 1, Integer.MAX_VALUE, 1);
        JSpinner meanServiceTime = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanChargingDemand = createSpinner(0.8, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity = createSpinner(60.0, 0.0, 200.0, 5);
        JSpinner maxEVPower = createSpinner(150, 1.0, Double.MAX_VALUE, 1);
        JSpinner percentageOfCars = createSpinner(100, 1, 100, 1);

        JSpinner percentageOfCars2 = createSpinner(100, 1, 100, 1);
        JSpinner meanServiceTime2 = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanChargingDemand2 = createSpinner(0.8, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity2 = createSpinner(60.0, 0.0, 200.0, 5);
        JSpinner maxEVPower2 = createSpinner(150, 1.0, Double.MAX_VALUE, 1);

        JSpinner percentageOfCars3 = createSpinner(100, 1, 100, 1);
        JSpinner meanServiceTime3 = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanChargingDemand3 = createSpinner(0.8, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity3 = createSpinner(60.0, 0.0, 200.0, 5);
        JSpinner maxEVPower3 = createSpinner(150, 1.0, Double.MAX_VALUE, 1);

        JSpinner maxSitePower = createSpinner(300, 1.0, Double.MAX_VALUE, 1);
        JSpinner maxPointPower = createSpinner(100, 1.0, Double.MAX_VALUE, 1);


        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox<String> queueingType = new JComboBox<>(queueingTypes);


        String[] distributionTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC", "LOMAX"};
        JComboBox<String> arrivalType = new JComboBox<>(distributionTypes);
        JComboBox<String> serviceType = new JComboBox<>(distributionTypes);
        JComboBox<String> demandType = new JComboBox<>(distributionTypes);

        JComboBox<String> demandType2 = new JComboBox<>(distributionTypes);
        JComboBox<String> demandType3 = new JComboBox<>(distributionTypes);

        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANG");
        demandType.setSelectedItem("BETA");

        demandType2.setSelectedItem("BETA");
        demandType3.setSelectedItem("BETA");

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

            String demandTypeString2 = (String) demandType2.getSelectedItem();
            switch (demandTypeString2) {
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

        setSpinnerModel(percentageOfCars);


        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel procPanel = new JPanel();
        procPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("General Parameters");
        procPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titledBorder));
        JScrollPane jScrollPane = new JScrollPane(procPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setMaximumSize(new Dimension(430, 550));
        numberOfClientTypes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedClientTypes = getSpinnerValueAsInt(numberOfClientTypes);
                if (isThirdCarPanelAdded(procPanel)) {
                    removeThirdCarPanel(procPanel);
                }

                if (selectedClientTypes == 1) {
                    if (isSecondCarPanelAdded(procPanel)) {
                        removeSecondCarPanel(procPanel);
                    }
                } else if (selectedClientTypes == 2) {
                    if (isThirdCarPanelAdded(procPanel)) {
                        removeThirdCarPanel(procPanel);
                    }


                    // Now add the second car panel directly to the right
                    JPanel secondCarPanel = createSecondCarPanel(percentageOfCars2, meanServiceTime2, meanChargingDemand2, batteryCapacity2, maxEVPower2);

                    GridBagConstraints secondCarGbc = new GridBagConstraints();
                    secondCarGbc.anchor = GridBagConstraints.WEST;
                    secondCarGbc.insets = new Insets(5, 5, 5, 5);
                    secondCarGbc.gridx = 0;  // Set the column index for the second car panel
                    secondCarGbc.gridy = 28;
                    procPanel.add(secondCarPanel, secondCarGbc);
                    procPanel.revalidate();
                    procPanel.repaint();

                } else if (selectedClientTypes == 3) {

                    JPanel secondCarPanel = createSecondCarPanel(percentageOfCars2, meanServiceTime2, meanChargingDemand2, batteryCapacity2, maxEVPower2);
                    GridBagConstraints secondCarGbc = new GridBagConstraints();
                    secondCarGbc.anchor = GridBagConstraints.WEST;
                    secondCarGbc.insets = new Insets(5, 5, 5, 5);
                    secondCarGbc.gridx = 0;
                    secondCarGbc.gridy = 28;
                    procPanel.add(secondCarPanel, secondCarGbc);

                    JPanel thirdCarPanel = createThirdCarPanel(percentageOfCars3, meanServiceTime3, meanChargingDemand3, batteryCapacity3, maxEVPower3);
                    GridBagConstraints thirdCarGbc = new GridBagConstraints();
                    thirdCarGbc.anchor = GridBagConstraints.WEST;
                    thirdCarGbc.insets = new Insets(5, 5, 5, 5);
                    thirdCarGbc.gridx = 0;
                    thirdCarGbc.gridy = 35; // Adjust the row index as needed
                    procPanel.add(thirdCarPanel, thirdCarGbc);

                    procPanel.revalidate();
                    procPanel.repaint();
                }
            }
        });


        addRowToPanel(procPanel, gbc, "Number of Steps", numberOfSteps);
        addRowToPanel(procPanel, gbc, "Max Events per Step", maxEvents);
        addRowToPanel(procPanel, gbc, "Confidence Interval Level", confLevel);
        addRowToPanel(procPanel, gbc, "Arrival Distribution Type", arrivalType);
        addRowToPanel(procPanel, gbc, "Max Mean Arrival Rate", maxArrivalRate);
        addRowToPanel(procPanel, gbc, "Number of Servers", numberOfServers);
        addRowToPanel(procPanel, gbc, "Queue Size", queueSize);
        addRowToPanel(procPanel, gbc, "Queueing Type", queueingType);
        addRowToPanel(procPanel, gbc, "Service Distribution Type", serviceType);
        addRowToPanel(procPanel, gbc, "Number of Client Types", numberOfClientTypes);
        addRowToPanel(procPanel, gbc, "Mean Service Time", meanServiceTime);
        addRowToPanel(procPanel, gbc, "Demand Distribution Type", demandType);
        addRowToPanel(procPanel, gbc, "Mean Charging Demand", meanChargingDemand);
        addRowToPanel(procPanel, gbc, "Battery Capacity", batteryCapacity);


        procPanel.setBackground(LIGHT_BLUE);


        procPanel.setBackground(LIGHT_BLUE);
        verticalBox.add(jScrollPane);

        JPanel toPanel2 = createSpinnerPanel("Max Site Power", "Max Point Power", "Max EV Power", maxSitePower, maxPointPower, maxEVPower);
        verticalBox.add(toPanel2);


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


    private static boolean isSecondCarPanelAdded(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private static void removeSecondCarPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                panel.remove(component);
                panel.revalidate();
                panel.repaint();
                return;
            }
        }
    }

    private static JPanel createThirdCarPanel(JSpinner percentageOfCars3, JSpinner meanServiceTime3, JSpinner meanChargingDemand3, JSpinner batteryCapacity3, JSpinner maxEVPower3) {
        JPanel thirdCarPanel = new JPanel();
        thirdCarPanel.setLayout(new GridBagLayout());
        thirdCarPanel.setBackground(LIGHT_PINK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // TitledBorder titledBorder = BorderFactory.createTitledBorder("Parameters for third car type");
        // thirdCarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titledBorder));


        addRowToPanel(thirdCarPanel, gbc, "Percentage of Cars 3", percentageOfCars3);
        addRowToPanel(thirdCarPanel, gbc, "Mean Service Time 3", meanServiceTime3);
        addRowToPanel(thirdCarPanel, gbc, "Max EV Power 3", maxEVPower3);
        addRowToPanel(thirdCarPanel, gbc, "Demand Distribution Type 3", createDemandTypeComboBox());
        addRowToPanel(thirdCarPanel, gbc, "Mean Charging Demand 3", meanChargingDemand3);
        addRowToPanel(thirdCarPanel, gbc, "Battery Capacity 3", batteryCapacity3);

        return thirdCarPanel;
    }

    private static void addRowToPanel(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.WEST;

        // Add a bit of space between label and component
        gbc.insets = new Insets(5, 5, 0, 5);

        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(200, 20)); // Adjust the width as needed for labels
        panel.add(label, gbc);

        gbc.gridy++;

        // Reset the anchor and insets for the component
        gbc.anchor = GridBagConstraints.LINE_START; // Set the anchor to LINE_START to left-align components
        gbc.insets = new Insets(5, 5, 5, 5);

        component.setPreferredSize(new Dimension(200, 20)); // Adjust the width as needed for components
        panel.add(component, gbc);
    }

    private static boolean isThirdCarPanelAdded(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private static void removeThirdCarPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                panel.remove(component);
                panel.revalidate();
                panel.repaint();
                return;
            }
        }
    }

    private static JPanel createSecondCarPanel(JSpinner percentageOfCars2, JSpinner meanServiceTime2, JSpinner maxEVPower2, JSpinner meanChargingDemand2, JSpinner batteryCapacity2) {
        JPanel secondCarPanel = new JPanel();
        secondCarPanel.setLayout(new GridBagLayout());
        secondCarPanel.setBackground(PEACH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

       // TitledBorder titledBorder = BorderFactory.createTitledBorder("Parameters for second car type");
       // secondCarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titledBorder));

        addRowToPanel(secondCarPanel, gbc, "Percentage of Cars 2", percentageOfCars2);
        addRowToPanel(secondCarPanel, gbc, "Mean Service Time 2", meanServiceTime2);
        addRowToPanel(secondCarPanel, gbc, "Max EV Power 2", maxEVPower2);
        addRowToPanel(secondCarPanel, gbc, "Demand Distribution Type 2", createDemandTypeComboBox());
        addRowToPanel(secondCarPanel, gbc, "Mean Charging Demand 2", meanChargingDemand2);
        addRowToPanel(secondCarPanel, gbc, "Battery Capacity 2", batteryCapacity2);

        return secondCarPanel;
    }

    private static JComboBox<String> createDemandTypeComboBox() {
        String[] demandTypes = {"GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "DETERMINISTIC", "LOMAX"};
        return new JComboBox<>(demandTypes);
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