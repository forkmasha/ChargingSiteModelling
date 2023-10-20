package results;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Graph {
    private String chosenFile;
   private final JTextField widthField;
    private final JTextField heightField;

    public Graph() {
        widthField = new JTextField("1200");
        heightField = new JTextField("730");
    }

    public JTextField getWidthField() {
        return widthField;
    }

    public JTextField getHeightField() {
        return heightField;
    }
    public String getChosenFile() {
        return chosenFile;
    }

    protected boolean getUserInput() {
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
                    return false;
                }

                chosenFile = fileField.getText();
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid width or height value. Please enter positive integers.");
                return false;
            }
        }
        return false;
    }
    protected boolean chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(chosenFile));

        int fileChooserResult = fileChooser.showSaveDialog(null);

        if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
            chosenFile = fileChooser.getSelectedFile().getAbsolutePath();
            return true;
        }
        return false;
    }

    private void configureChartPanel(ChartPanel chartPanel) {
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
    }


}
