package results;

import chargingSite.Simulation;
import distributions.*;
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
    public List<Double> stds = new ArrayList<>();
    public List<Double> confidences = new ArrayList<>();

    public List<Double> maxs = new ArrayList<>();
    public List<Double> mins = new ArrayList<>();


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

    public void storeMin() {
        mins.add(calc.getMin(source.getAmountsCharged()));
    }

    public void storeStep(double step) {
        steps.add(step);
    }

    public void addGraphs(XYSeriesCollection dataset) {
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        XYSeries maxSeries = new XYSeries("Max");
        XYSeries minSeries = new XYSeries("Min");
        XYSeries[] confBars = new XYSeries[steps.size()];


        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double max = maxs.get(i);
            double min = mins.get(i);
            double conf = confidences.get(i);
            confBars[i] = new XYSeries("confBar" + i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            maxSeries.add(step, max);
            minSeries.add(step, min);
            confBars[i].add(step, mean - conf);
            confBars[i].add(step, mean + conf);
            dataset.addSeries(confBars[i]);

        }

        dataset.addSeries(meanSeries);
        dataset.addSeries(stdSeries);
        dataset.addSeries(maxSeries);
        dataset.addSeries(minSeries);

    }

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
    public void drawGraph(Simulation mySim) {

        String title = "Simulation Results";
        XYSeriesCollection dataset = new XYSeriesCollection();
        mySim.chargingMonitor.addGraphs(dataset);
      //  mySim.chargingMonitor.addPDFData(dataset, mySim.getMEAN_SERVICE_TIME());

        MyChart = createXYLineChart(
                title,
                "Arrival Rate [1/h]",
                "Mean and Std [kWh]",
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
        while (i < mySim.getSIM_STEPS() + 1) {
            renderer.setSeriesPaint(i, Color.BLUE);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));
        plot.setRenderer(renderer);


        while (i < 2 * (mySim.getSIM_STEPS() + 2)) {
            renderer.setSeriesPaint(i, Color.red);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        LegendItemCollection legendItems = new LegendItemCollection();
        legendItems.add(new LegendItem("Charged energy", Color.BLUE));

        LegendItemSource source = () -> legendItems;
        MyChart.getLegend().setSources(new LegendItemSource[]{source});

        ChartPanel chartPanel = new ChartPanel(MyChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add a window listener to handle the window closing event
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
                inputValid=true;
            }

        }
    }
}
