package results;

import chargingSite.Simulation;
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
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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


    public void addGraphs(XYSeriesCollection dataset) {

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
            confBarsChargingDeviation[i]=new XYSeries("confBarChargingDeviation"+i);

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
            confBarsSitePowerSeries[i].add(step,  (meanSP - confSP) / source.getNumberOfServers());
            confBarsSitePowerSeries[i].add(step, (meanSP + confSP) / source.getNumberOfServers());
            dataset.addSeries(confBarsSitePowerSeries[i]);

            meanChargingDeviationSeries.add(step,  meanCD ); //WHY 0.5*?
            stdChargingDeviationSeries.add(step,  stdCD );
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

    public void drawGraph(Simulation mySim) {

        String title = "Charging Site Energy Characteristics";
        XYSeriesCollection dataset = new XYSeriesCollection();
        mySim.chargingMonitor.addGraphs(dataset);

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
        while (i < 3 * mySim.getSIM_STEPS()) {
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
        legendItems.add(new LegendItem("Power per Charging Point", Color.MAGENTA));



        LegendItemSource source = () -> legendItems;
        MyChart.getLegend().setSources(new LegendItemSource[]{source});

        ChartPanel chartPanel = new ChartPanel(MyChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
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
        });


        frame.setContentPane(chartPanel);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        frame.setLocation(screenWidth - frame.getWidth(), 0);

        frame.setVisible(true);
        chartPanel.repaint();
    }

    public void saveAsSVG(int wi, int hi, File svgFile) throws IOException {

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wi, hi));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(writer, true);
        }
    }


    public void saveSVGDialogue() {
        boolean inputValid = false;

        while (!inputValid) {
            inputValid = getUserInput() && chooseFile();

            if (inputValid) {
                try {
                    int imageWidth = Integer.parseInt(getWidthField().getText());
                    int imageHeight = Integer.parseInt(getHeightField().getText());
                    int result = JOptionPane.showConfirmDialog(null, "Do you want to save the SVG file?", "Save SVG", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        saveAsSVG(imageWidth, imageHeight, new File(getChosenFile()));
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            } else {
                // System.exit(0);
                inputValid = true;
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