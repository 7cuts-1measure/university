package simulation.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Model;

public class SimulationWindow {
    private JFrame frame;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Model model;

    private final BlockComponent motorBlock;
    private final BlockComponent bodyBlock;
    private final BlockComponent accessoryBlock;
    private final BlockComponent carBlock;

    private JLabel pendingTasksLabel;
    private JLabel totalCarsLabel;

    private JSlider motorSlider;
    private JSlider bodySlider;
    private JSlider accessorySlider;
    private final ScheduledExecutorService updater = Executors.newSingleThreadScheduledExecutor();

    private final int MIN_SLIDER_VALUE = 0;
    private final int MAX_SLIDER_VALUE = 30;
    private final int SLIDER_MAJOR_STEP = 5;
    private final int SLIDER_MINOR_STEP = 1;

    public SimulationWindow(Model model) {
        this.model = model;

        motorBlock = new BlockComponent("Motor Storage", "");
        bodyBlock = new BlockComponent("Body Storage", "");
        accessoryBlock = new BlockComponent("Accessory Storage", "");
        carBlock = new BlockComponent("Car Storage", "");

        SwingUtilities.invokeLater(() -> {
            createWindow();
            startUpdater();
        });
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {frame.setVisible(true);});
    }

    private void createWindow() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Factory Simulation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.shutdown();
                updater.shutdownNow();
            }
        });

        JPanel infoPanel = createInfoPanel();
        frame.add(infoPanel, BorderLayout.NORTH);

        JPanel storagePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        storagePanel.add(motorBlock);
        storagePanel.add(bodyBlock);
        storagePanel.add(accessoryBlock);
        storagePanel.add(carBlock);
        frame.add(storagePanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        log.info("Simulation window created with sliders");
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        pendingTasksLabel = new JLabel("Pending tasks: ");
        totalCarsLabel = new JLabel("Total cars assembled: ");

        pendingTasksLabel.setPreferredSize(new Dimension(200, 25));
        totalCarsLabel.setPreferredSize(new Dimension(200, 25));

        panel.add(pendingTasksLabel);
        panel.add(totalCarsLabel);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Supplier's performance"));

        motorSlider = createSlider(
            "Motors",
            model.getMotorSupplierPerformance(),
            MIN_SLIDER_VALUE, MAX_SLIDER_VALUE, SLIDER_MINOR_STEP, SLIDER_MAJOR_STEP
        );
        motorSlider.addChangeListener(this::onMotorSliderChanged);
        panel.add(createSliderRow("Motors", motorSlider));

        bodySlider = createSlider(
            "Bodys",
            model.getBodySupplierPerformance(),
            MIN_SLIDER_VALUE, MAX_SLIDER_VALUE, SLIDER_MINOR_STEP, SLIDER_MAJOR_STEP
        );
        bodySlider.addChangeListener(this::onBodySliderChanged);
        panel.add(createSliderRow("Bodys", bodySlider));

        accessorySlider = createSlider(
            "Accessories",
            model.getAccessorySupplierPerformance(),
            MIN_SLIDER_VALUE, MAX_SLIDER_VALUE, SLIDER_MINOR_STEP, SLIDER_MAJOR_STEP
        );
        accessorySlider.addChangeListener(this::onAccessorySliderChanged);
        panel.add(createSliderRow("Accessories", accessorySlider));

        return panel;
    }

    private JSlider createSlider(String name, int initialValue, int min, int max, int minorTick, int majorTick) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, initialValue);
        slider.setMinorTickSpacing(minorTick);
        slider.setMajorTickSpacing(majorTick);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = min; i <= max; i += majorTick) {
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        slider.setLabelTable(labelTable);
        return slider;
    }

    private JPanel createSliderRow(String labelText, JSlider slider) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        JLabel label = new JLabel(labelText + ":");
        label.setPreferredSize(new Dimension(80, 20));
        row.add(label, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        return row;
    }

    private void onMotorSliderChanged(ChangeEvent e) {
        if (!motorSlider.getValueIsAdjusting()) {
            int value = motorSlider.getValue();
            model.setMotorSupplierPerformance(value);
            log.debug("Motor performance set to {}", value);
        }
    }

    private void onBodySliderChanged(ChangeEvent e) {
        if (!bodySlider.getValueIsAdjusting()) {
            int value = bodySlider.getValue();
            model.setBodySupplierPerformance(value);
            log.debug("Body performance set to {}", value);
        }
    }

    private void onAccessorySliderChanged(ChangeEvent e) {
        if (!accessorySlider.getValueIsAdjusting()) {
            int value = accessorySlider.getValue();
            model.setAccessorySupplierPerformance(value);
            log.debug("Accessory performance set to {}", value);
        }
    }

    private void startUpdater() {
        updater.scheduleAtFixedRate(() -> {
            try {
                final int motorSize = model.getMotorStorageSize();
                final int motorCap = model.getMotorStorageCap();
                final String motorMsg = storageMessage(motorSize, motorCap);

                final int bodySize = model.getBodySorageSize();
                final int bodyCap = model.getBodyStorageCap();
                final String bodyMsg = storageMessage(bodySize, bodyCap);

                final int accSize = model.getAccessoryStorageSize();
                final int accCap = model.getAccessoryStorageCap();
                final String accMsg = storageMessage(accSize, accCap);

                final int carSize = model.getCarStorageSize();
                final int carCap = model.getCarStorageCap();
                final String carMsg = storageMessage(carSize, carCap);

                final int pendingTasks = model.getNumPendingTasks();
                final int totalCars = model.getNumTotalCarsAssembled();

                SwingUtilities.invokeLater(() -> {
                    motorBlock.setMessage(motorMsg);
                    bodyBlock.setMessage(bodyMsg);
                    accessoryBlock.setMessage(accMsg);
                    carBlock.setMessage(carMsg);

                    pendingTasksLabel.setText("Pending tasks: " + pendingTasks);
                    totalCarsLabel.setText("Total cars assembled: " + totalCars);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private String storageMessage(int size, int cap) {
        return size + " / " + cap;
    }
}