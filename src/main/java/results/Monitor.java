package results;

import chargingSite.Simulation;
import chargingSite.SimulationGUI;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import queueingSystem.QueueingSystem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.jfree.chart.ChartFactory.createXYLineChart;

public class Monitor extends Graph {
    private Statistics calc = new Statistics();
    private JFreeChart MyChart;
    private QueueingSystem source;
    private String name;
    private int confLevel;
    private List<Double> steps = new ArrayList<>();
    public List<Double> values = new ArrayList<>();

    public List<Double> means = new ArrayList<>();
    public List<Double> confidences = new ArrayList<>();
    public List<Double> stds = new ArrayList<>();
    public List<Double> maxs = new ArrayList<>();
    public List<Double> maxs90 = new ArrayList<>();
    public List<Double> mins = new ArrayList<>();
    public List<Double> mins10 = new ArrayList<>();

    public List<Double> meansSitePower = new ArrayList<>();
    public List<Double> confidencesSitePower = new ArrayList<>();
    public List<Double> stdsSitePower = new ArrayList<>();
    public List<Double> maxSitePower = new ArrayList<>();

    public List<Double> meansChargingDeviation = new ArrayList<>();
    public List<Double> confidencesChargingDeviation = new ArrayList<>();
    public List<Double> stdsChargingDeviation = new ArrayList<>();

    public void setSource(QueueingSystem source) {
        this.source = source;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Monitor() {

    }

    public Monitor(int confLevel) {
        this.confLevel = confLevel;
    }

    public void storeMean() {
        means.add(calc.getMean(source.getAmountsCharged()));
    }

    public void storeStd() {
        stds.add(calc.getStd(source.getAmountsCharged()));
    }

    public void storeConf() {
        confidences.add(calc.getConfidenceInterval(source.getAmountsCharged(), confLevel));
    }

    public void storeMax() {
        maxs.add(calc.getMax(source.getAmountsCharged()));
    }

    public void store90thQuantile() {
        maxs90.add(calc.get90thQuantile(source.getAmountsCharged()));
    }

    public void storeMin() {
        mins.add(calc.getMin(source.getAmountsCharged()));
    }

    public void store10thQuantile() {
        mins10.add(calc.get10thQuantile(source.getAmountsCharged()));
    }

    public void storeStep(double step) {
        steps.add(step);
    }

    public void storeMeanSitePower() {
        meansSitePower.add(calc.getMean(source.getSitePowers()));
    }

    public void storeStdSitePower() {
        stdsSitePower.add(calc.getStd(source.getSitePowers()));
    }

    public void storeMaxSitePower() {
        maxSitePower.add(calc.getMax(source.getSitePowers()));
    }

    public void storeConfSitePower() {
        confidencesSitePower.add(calc.getConfidenceInterval(source.getSitePowers(), confLevel));
    }

    public void storeMeanCD() {
        meansChargingDeviation.add(calc.getMean(source.getChargingDeviations()));
    }

    public void storeStdCD() {
        stdsChargingDeviation.add(calc.getStd(source.getChargingDeviations()));
    }

    public void storeConfCD() {
        confidencesChargingDeviation.add(calc.getConfidenceInterval(source.getChargingDeviations(), confLevel));
    }

    public void addGraphsEnergyCharacteristics(XYSeriesCollection dataset) {

        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        XYSeries max90Series = new XYSeries("Max90");
        XYSeries min10Series = new XYSeries("Min10");

        XYSeries meanSitePowersSeries = new XYSeries("Mean Site Power");
        XYSeries stdSitePowersSeries = new XYSeries("Std Site Power");
        XYSeries maxSitePowersSeries = new XYSeries("Maximum Site Power");

        XYSeries meanChargingDeviationSeries = new XYSeries("Mean Charging Deviation");
        XYSeries stdChargingDeviationSeries = new XYSeries("Std Charging Deviation");


        XYSeries[] confBars = new XYSeries[steps.size()];
        XYSeries[] confBarsSitePowerSeries = new XYSeries[steps.size()];
        XYSeries[] confBarsChargingDeviation = new XYSeries[steps.size()];

        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);

            double mean = means.get(i);
            double std = stds.get(i);
            double max90 = maxs90.get(i);
            double min10 = mins10.get(i);
            double conf = confidences.get(i);

            double meanSP = meansSitePower.get(i);
            double stdSP = stdsSitePower.get(i);
            double maxSP = maxSitePower.get(i);
            double confSP = confidencesSitePower.get(i);

            double meanCD = meansChargingDeviation.get(i);
            double stdCD = stdsChargingDeviation.get(i);
            double confCD = confidencesChargingDeviation.get(i);

            confBars[i] = new XYSeries("confBar" + i);
            confBarsSitePowerSeries[i] = new XYSeries("confBarSitePower" + i);
            confBarsChargingDeviation[i] = new XYSeries("confBarChargingDeviation" + i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            max90Series.add(step, max90);
            min10Series.add(step, min10);
            confBars[i].add(step, mean - conf);
            confBars[i].add(step, mean + conf);
            dataset.addSeries(confBars[i]);

            meanSitePowersSeries.add(step, meanSP / source.getNumberOfServers());
            stdSitePowersSeries.add(step, stdSP / source.getNumberOfServers());
            maxSitePowersSeries.add(step, maxSP / source.getNumberOfServers());
            confBarsSitePowerSeries[i].add(step, (meanSP - confSP) / source.getNumberOfServers());
            confBarsSitePowerSeries[i].add(step, (meanSP + confSP) / source.getNumberOfServers());
            dataset.addSeries(confBarsSitePowerSeries[i]);

            meanChargingDeviationSeries.add(step, meanCD); //WHY 0.5*?
            stdChargingDeviationSeries.add(step, stdCD);
            confBarsChargingDeviation[i].add(step, (meanCD - confCD));
            confBarsChargingDeviation[i].add(step, (meanCD + confCD));
            dataset.addSeries(confBarsChargingDeviation[i]);
        }

        dataset.addSeries(meanSeries);
        dataset.addSeries(stdSeries);
        dataset.addSeries(max90Series);
        dataset.addSeries(min10Series);

        dataset.addSeries(meanSitePowersSeries);
        dataset.addSeries(stdSitePowersSeries);
        dataset.addSeries(maxSitePowersSeries);

        dataset.addSeries(meanChargingDeviationSeries);
        dataset.addSeries(stdChargingDeviationSeries);
    }

    public static JFrame energyCharacteristicsFrame;

    public void drawGraphEnergyCharacteristics(Simulation mySim) {

        String title = "Charging Site Energy Characteristics";
        XYSeriesCollection dataset = new XYSeriesCollection();
        mySim.chargingMonitor.addGraphsEnergyCharacteristics(dataset);

        MyChart = createXYLineChart(
                title,
                "Arrival Rate [1/h]",
                "Mean and Std [kW/server, kWh/car]",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = MyChart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        int i = 0;
        while (i < 3 * mySim.getParameters().getSIM_STEPS()) {
            renderer.setSeriesPaint(i, Color.BLUE);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
            renderer.setSeriesPaint(i, Color.MAGENTA);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
            renderer.setSeriesPaint(i, Color.RED);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.BLUE);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.BLUE);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        renderer.setSeriesPaint(i, Color.BLUE);
        renderer.setSeriesShape(i++, ShapeUtilities.createDownTriangle(2.25f));
        renderer.setSeriesPaint(i, Color.BLUE);
        renderer.setSeriesShape(i++, ShapeUtilities.createUpTriangle(2.25f));
        plot.setRenderer(renderer);


        renderer.setSeriesPaint(i, Color.MAGENTA);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.MAGENTA);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        renderer.setSeriesPaint(i, Color.MAGENTA);
        renderer.setSeriesShape(i++, ShapeUtilities.createDownTriangle(1.75f));


        renderer.setSeriesPaint(i, Color.RED);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.RED);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));


        LegendItemCollection legendItems = new LegendItemCollection();
        legendItems.add(new LegendItem("Energy per charged EV", Color.BLUE));
        legendItems.add(new LegendItem("EV Charging Deviation", Color.RED));
        legendItems.add(new LegendItem("Average Power per Charging Point", Color.MAGENTA));


        LegendItemSource source = () -> legendItems;
        MyChart.getLegend().setSources(new LegendItemSource[]{source});


        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();


        GraphicsDevice largestScreen = gs[0];
        Rectangle largestBounds = largestScreen.getDefaultConfiguration().getBounds();
        for (GraphicsDevice gd : gs) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth() * largestBounds.getHeight()) {
                largestScreen = gd;
                largestBounds = bounds;
            }
        }

        int windowWidth = (int) (largestBounds.width * 0.4);
        int windowHeight = (int) (largestBounds.height * 0.44);
        int xOffset = (int) (largestBounds.width * 0.25);


        int yOffset = (int) (largestBounds.height - windowHeight - (largestBounds.height * 0.09)); // Знизу з додатковим зсувом вгору на 10%

        ChartPanel chartPanel = new ChartPanel(MyChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(windowWidth, windowHeight));

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /* frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Do you want to save your work before exiting?", "Save before exit", JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveSVGDialogue();
                    frame.dispose();
                } else if (result == JOptionPane.NO_OPTION) {
                    frame.dispose();
                }
            }
        });*/

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnCloseEnergyCharacteristics(frame);
            }
        });

        energyCharacteristicsFrame = frame;
        frame.setContentPane(chartPanel);
        frame.pack();
       // frame.setLocation(largestBounds.x + xOffset, yOffset);
        frame.setLocation(largestBounds.x + xOffset, yOffset);
        frame.setVisible(true);
        chartPanel.repaint();
    }
    public static JFrame getEnergyCharacteristicsFrame() {
        return energyCharacteristicsFrame;
    }
    private void promptSaveOnCloseEnergyCharacteristics(JFrame frame) {
        Object[] options = {"Save", "Cancel", "Close"};
        int choice = JOptionPane.showOptionDialog(frame, "Do you want to save the chart before closing?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showSaveOptionsEnergyCharacteristics(frame);
                break;
            case JOptionPane.NO_OPTION:

                break;
            case JOptionPane.CANCEL_OPTION:
                frame.dispose();
                break;
            default:

                break;
        }
    }
    private void showSaveOptionsEnergyCharacteristics(JFrame frame) {
        Object[] saveOptions = {"CSV", "SVG", "PNG"};
        int formatChoice = JOptionPane.showOptionDialog(frame, "Choose the format to save the chart:", "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "ChargingSiteEnergyCharacteristics" + fileExtension;

        int width = 1000, height = 1000;

        if (formatChoice == 1 || formatChoice == 2) { // If not CSV
            String defaultSize = formatChoice == 1 ? "1200x730" : "2400x1560";
            String sizeInput = JOptionPane.showInputDialog(frame, "Enter dimensions (width x height):", defaultSize);
            if (sizeInput == null || sizeInput.trim().isEmpty()) {
                return;
            }
            String[] sizes = sizeInput.split("x");
            try {
                width = Integer.parseInt(sizes[0].trim());
                height = Integer.parseInt(sizes[1].trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid dimensions. Using default values.");
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory and filename to save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File(defaultFileName));

        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }

            try {
                if (fileExtension.equals(".svg")) {
                    saveEnergyCharacteristicsGraphAsSVG(MyChart, fileToSave.getAbsolutePath(), width, height);
                } else if (fileExtension.equals(".csv")) {
                    saveEnergyCharacteristicsGraphToCSV(fileToSave.getAbsolutePath());
                } else if (fileExtension.equals(".png")) {
                    saveEnergyCharacteristicsGraphToPNG(MyChart, fileToSave.getAbsolutePath(), width, height);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void saveEnergyCharacteristicsGraphAsSVG(JFreeChart chart, String filePath, int width, int height) throws IOException {
        File svgFile = new File(filePath);

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(width, height));
        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height));

        try (Writer out = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(out, true);
        } catch (IOException e) {
            throw new IOException("Problem occurred creating chart SVG: " + e.getMessage(), e);
        }
    }

    public void saveEnergyCharacteristicsGraphAsSVG(int wi, int hi, File svgFile) throws IOException {

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wi, hi));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(writer, true);
        }
    }
    public void saveEnergyCharacteristicsGraphToPNG(String filePath) {
        try {
            int width = SimulationGUI.WIDTH_OF_PNG_PICTURE;
            int height = SimulationGUI.HEIGHT_OF_PNG_PICTURE;
            File PNGFile = new File(filePath);
            ChartUtilities.saveChartAsPNG(PNGFile, MyChart, width, height);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }
    public static void saveEnergyCharacteristicsGraphToPNG(JFreeChart chart, String filePath, int width, int height) {
        try {
            File PNGFile = new File(filePath);
            ChartUtilities.saveChartAsPNG(PNGFile, chart, width, height);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart PNG: " + e.getMessage());
        }
    }

    private String formatDouble(DecimalFormat df, Double value) {
        return df.format(value);
    }
    public void saveEnergyCharacteristicsGraphToCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.####################");
        df.setDecimalSeparatorAlwaysShown(false);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Step;Mean;Std;Max90;Min10;MeanSitePower;StdSitePower;MaxSitePower;MeanChargingDeviation;StdChargingDeviation;ConfChargingDeviation\n");

            for (int i = 0; i < steps.size(); i++) {
                writer.append(formatDouble(df, steps.get(i))).append(";");
                writer.append(formatDouble(df, means.get(i))).append(";");
                writer.append(formatDouble(df, stds.get(i))).append(";");
                writer.append(formatDouble(df, maxs90.get(i))).append(";");
                writer.append(formatDouble(df, mins10.get(i))).append(";");
                writer.append(formatDouble(df, meansSitePower.get(i))).append(";");
                writer.append(formatDouble(df, stdsSitePower.get(i))).append(";");
                writer.append(formatDouble(df, maxSitePower.get(i))).append(";");
                writer.append(formatDouble(df, meansChargingDeviation.get(i))).append(";");
                writer.append(formatDouble(df, stdsChargingDeviation.get(i))).append(";");
                writer.append(formatDouble(df, confidencesChargingDeviation.get(i))).append("\n");
            }

            System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
    public void saveSVGDialogue() {
        Object[] options = {"SVG", "CSV"};
        int formatResult = JOptionPane.showOptionDialog(
                null,
                "Choose the file format to save:",
                "Choose File Format",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (formatResult == JOptionPane.YES_OPTION) {
            // Save as SVG
            boolean inputValid = getUserInput() && chooseFile();

            if (inputValid) {
                try {
                    int imageWidth = Integer.parseInt(getWidthField().getText());
                    int imageHeight = Integer.parseInt(getHeightField().getText());
                    saveEnergyCharacteristicsGraphAsSVG(imageWidth, imageHeight, new File(getChosenFile() + ".svg"));
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        } else if (formatResult == JOptionPane.NO_OPTION) {
            JFileChooser csvFileChooser = new JFileChooser();
            csvFileChooser.setDialogTitle("Choose CSV File Location");
            csvFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            csvFileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
            int csvResult = csvFileChooser.showSaveDialog(null);

            if (csvResult == JFileChooser.APPROVE_OPTION) {
                String csvFilePath = csvFileChooser.getSelectedFile().toString();
                if (!csvFilePath.endsWith(".csv")) {
                    csvFilePath += ".csv";
                }
                saveEnergyCharacteristicsGraphToCSV(csvFilePath);
            }
        }
    }
}
  /*public void saveSVGDialogue() {
        boolean inputValid = false;

        while (!inputValid) {
            JPanel panel = new JPanel(new GridLayout(3, 2));
            JLabel heightLabel = new JLabel("Height:");
            JTextField heightField = new JTextField("730");
            JLabel widthLabel = new JLabel("Width:");
            JTextField widthField = new JTextField("1200");
            JLabel fileLabel = new JLabel("File:");
            JTextField fileField = new JTextField("simulation.svg");

            panel.add(heightLabel);
            panel.add(heightField);
            panel.add(widthLabel);
            panel.add(widthField);
            panel.add(fileLabel);
            panel.add(fileField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Save as SVG",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int imageWidth = Integer.parseInt(widthField.getText());
                    int imageHeight = Integer.parseInt(heightField.getText());

                    if (imageWidth <= 0 || imageHeight <= 0) {
                        JOptionPane.showMessageDialog(null, "Width and height must be positive integers.");
                    } else {
                        inputValid = true;

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setSelectedFile(new File(fileField.getText()));

                        int fileChooserResult = fileChooser.showSaveDialog(null);

                        if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
                            File chosenFile = fileChooser.getSelectedFile();

                            try {
                                SaveAsSVG(imageWidth, imageHeight, chosenFile);
                            } catch (IOException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid width or height value. Please enter positive integers.");
                }
            } else {
                inputValid = true; // User canceled the dialog, exit the loop.
            }
        }
    }*/


    /*public void addPDFData(XYSeriesCollection dataset, double mean) {
        for (DistributionType distributionType : DistributionType.values()) {
            double[][] pdfData = getPDFDataForDistribution(distributionType, mean);
            XYSeries pdfSeries = createXYSeriesForPDF(distributionType, pdfData);
            dataset.addSeries(pdfSeries);
        }
    }

    private double[][] getPDFDataForDistribution(DistributionType distributionType, double mean) {
        switch (distributionType) {
            case GEOMETRIC:
                return GeometricDistribution.getPDF(mean, 100.0);
            case EXPONENTIAL:
                return ExponentialDistribution.getPDF(mean, 100.0);
            case BETA:
                return BetaDistribution.getPDF(mean, 1.0);
            case ERLANG:
                return ErlangDistribution.getPDF(mean, 100.0);
            case ERLANGD:
                return DiscreteErlangDistribution.getPDF(mean, 100.0);
            case UNIFORM:
                return UniformDistribution.getPDF(mean, 2 * mean);
            case DETERMINISTIC:
                return DetermanisticDistribution.getPDF(mean, 2 * mean);
            default:
                System.out.println("Distribution type is not defined!");
        }
        return null;
    }

    private XYSeries createXYSeriesForPDF(DistributionType distributionType, double[][] pdfData) {
        XYSeries pdfSeries = new XYSeries(distributionType.toString() + " PDF");
        for (int i = 0; i < pdfData.length; i++) {
            pdfSeries.add(pdfData[0][i], pdfData[1][i]);
        }
        return pdfSeries;
    }

*/