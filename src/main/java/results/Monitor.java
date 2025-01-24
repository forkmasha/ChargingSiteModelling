package results;

import chargingSite.DefaultPictureSizes;
import chargingSite.Simulation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
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

    public void addGraphsEnergyCharacteristics(XYSeriesCollection dataset, XYSeriesCollection dataset2) {

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

            meanChargingDeviationSeries.add(step, meanCD); //WHY 0.5*?
            stdChargingDeviationSeries.add(step, stdCD);
            confBarsChargingDeviation[i].add(step, (meanCD - confCD));
            confBarsChargingDeviation[i].add(step, (meanCD + confCD));
            dataset.addSeries(confBarsChargingDeviation[i]);

            meanSitePowersSeries.add(step, meanSP); // / source.getNumberOfServers());
            stdSitePowersSeries.add(step, stdSP); // / source.getNumberOfServers());
            maxSitePowersSeries.add(step, maxSP); // / source.getNumberOfServers());
            confBarsSitePowerSeries[i].add(step, (meanSP - confSP)); // / source.getNumberOfServers());
            confBarsSitePowerSeries[i].add(step, (meanSP + confSP)); // / source.getNumberOfServers());
            dataset2.addSeries(confBarsSitePowerSeries[i]);
        }

        dataset.addSeries(meanSeries);
        dataset.addSeries(stdSeries);
        dataset.addSeries(max90Series);
        dataset.addSeries(min10Series);

        dataset.addSeries(meanChargingDeviationSeries);
        dataset.addSeries(stdChargingDeviationSeries);

        dataset2.addSeries(meanSitePowersSeries);
        dataset2.addSeries(stdSitePowersSeries);
        dataset2.addSeries(maxSitePowersSeries);
    }

    public static JFrame energyCharacteristicsFrame;
    private static final List<JFrame> openWindows = new ArrayList<>();

    public static void addWindow(JFrame frame) {
        openWindows.add(frame);
    }
    public static void closeAllWindows() {
        for (JFrame frame : openWindows) {
            frame.dispose();
        }
        openWindows.clear();
    }

    public void drawGraphEnergyCharacteristics(Simulation mySim, Boolean runWithGui) {
        String kendallName = mySim.getKendallName();
        int maxEvents = mySim.getParameters().getMaxEvents();

        String title = "Charging site Energy Characteristics \n"
                + kendallName + " Charging Site Model"
                + " (" + maxEvents + " samples per evaluation point)";

        String[] titleParts = title.split("\n");

        TextTitle textTitle = new TextTitle(titleParts[0]);
        textTitle.setFont(new Font("Arial", Font.BOLD, 24));

        TextTitle textSubtitle = new TextTitle(titleParts[1]);
        textSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        mySim.chargingMonitor.addGraphsEnergyCharacteristics(dataset,dataset2);

        MyChart = createXYLineChart(
                "",
                "Arrival Rate [1/h]",
                "Mean and Std [kWh/EV]",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        MyChart.addSubtitle(textTitle);
        MyChart.addSubtitle(textSubtitle);
        XYPlot plot = MyChart.getXYPlot();
        plot.setDataset(0, dataset);
        plot.setDataset(1, dataset2);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        plot.setRangeAxis(0, yAxis);
        NumberAxis yAxis2 = new NumberAxis("Site Power [kW]");
        yAxis2.setRange(0, 1.01 * mySim.getParameters().MAX_SITE_POWER);
        yAxis2.setLabelFont(new Font("Arial", Font.BOLD, 12));
        yAxis2.setLabelPaint(Color.magenta.darker().darker());
        yAxis2.setLabelAngle(Math.toRadians(180));
        plot.setRangeAxis(1, yAxis2);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(0,renderer);
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        plot.setRenderer(1,renderer2);

        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        int i = 0;
        while (i < 2 * mySim.getParameters().getSteps()) {
            renderer.setSeriesPaint(i, Color.BLUE);
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

        renderer.setSeriesPaint(i, Color.RED);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.RED);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        i = 0;
        Color powerColor = Color.MAGENTA.darker();
        while (i < mySim.getParameters().getSteps()) {
            renderer2.setSeriesPaint(i, powerColor);
            renderer2.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer2.setSeriesPaint(i, powerColor);
        renderer2.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer2.setSeriesPaint(i, powerColor);
        renderer2.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        renderer2.setSeriesPaint(i, powerColor);
        renderer2.setSeriesShape(i++, ShapeUtilities.createDownTriangle(1.75f));

        LegendItemCollection legendItems = new LegendItemCollection();
        legendItems.add(new LegendItem("Energy per charged EV", Color.BLUE));
        legendItems.add(new LegendItem("EV-charging deviation", Color.RED));
        legendItems.add(new LegendItem("Site Power Demand", powerColor));


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
        Monitor.addWindow(frame);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnCloseEnergyCharacteristics(frame);
            }
        });

        energyCharacteristicsFrame = frame;
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocation(largestBounds.x + xOffset, largestBounds.y + yOffset);
        frame.setVisible(runWithGui);
        chartPanel.repaint();
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
                    saveEnergyCharacteristicsGraphAsCSV(fileToSave.getAbsolutePath());
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

    public void saveEnergyCharacteristicsGraphAsSVG(int wi, int hi, File svgFile) {

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wi, hi));

        try {
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
                svgGenerator.stream(writer, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveEnergyCharacteristicsGraphAsPNG(String filePath) {
        try {
            int width = DefaultPictureSizes.PNG_WIDTH;
            int height = DefaultPictureSizes.PNG_HEIGTH;
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
    public void saveEnergyCharacteristicsGraphAsCSV(String filePath) {
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
                int imageWidth = Integer.parseInt(getWidthField().getText());
                int imageHeight = Integer.parseInt(getHeightField().getText());
                saveEnergyCharacteristicsGraphAsSVG(imageWidth, imageHeight, new File(getChosenFile() + ".svg"));
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
                saveEnergyCharacteristicsGraphAsCSV(csvFilePath);
            }
        }
    }

    public static void disposeEnergyCharacteristicsFrame() {
        if (energyCharacteristicsFrame != null) {
            energyCharacteristicsFrame.dispose();
            energyCharacteristicsFrame = null;
        }
    }
}