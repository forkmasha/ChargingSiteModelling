package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SelectViewType extends JFrame {

    public SelectViewType() {
        initializeFrameSettings();

        JButton consoleButton = createButton("Console", new ConsoleGuiExecuter());
        JButton desktopButton = createButton("Desktop", new DesktopGuiExecuter());
        JButton webButton = createButton("Web", new WebGuiExecuter());

        setResizable(false);

        JPanel buttonPanel = createButtonPanel();
        buttonPanel.add(consoleButton);
        buttonPanel.add(desktopButton);
        buttonPanel.add(webButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void initializeFrameSettings() {
        setTitle("Open with");
        setSize(250, 200);
        centerFrameOnLargestScreen();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void centerFrameOnLargestScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsDevice largestScreen = gs[0];
        for (GraphicsDevice gd : gs) {
            if (gd.getDisplayMode().getWidth() * gd.getDisplayMode().getHeight() >
                    largestScreen.getDisplayMode().getWidth() * largestScreen.getDisplayMode().getHeight()) {
                largestScreen = gd;
            }
        }
        Rectangle bounds = largestScreen.getDefaultConfiguration().getBounds();
        setLocation(bounds.x + (bounds.width - getWidth()) / 2,
                bounds.y + (bounds.height - getHeight()) / 2);
    }

    private JPanel createButtonPanel() {
        var gridLayout = new GridLayout(3, 1, 50, 5);
        JPanel buttonPanel = new JPanel(gridLayout);
        return buttonPanel;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.addActionListener(e -> dispose());
        return button;
    }
}