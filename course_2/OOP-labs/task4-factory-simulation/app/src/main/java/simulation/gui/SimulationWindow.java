package simulation.gui;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import static java.util.stream.IntStream.range;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.FlowLayout;
import java.awt.LayoutManager;



public class SimulationWindow {
    private final JFrame frame;
    private final LayoutManager DEFAULT_LAYOUT = new FlowLayout();
    public SimulationWindow() {
        frame = new JFrame();
        SwingUtilities.invokeLater(() -> {
            createWindowAndSetDefaultLayout();

            final int numBlocks = 10;
            createBlocks(numBlocks);
        });
        
    }

    private void createBlocks(final int numBlocks) {
        range(0, numBlocks).forEach(i -> blockName(i, numBlocks));
    }

    private void blockName(int i, final int numBlocks) {
        createBlock("block title", (i + 1) + " of " + numBlocks);
    }

    private void createBlock(String title, String message) {
        BlockComponent block = new BlockComponent(title, message);
        frame.add(block);
    }

    private void createWindowAndSetDefaultLayout() {
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(DEFAULT_LAYOUT);
        frame.setVisible(true);
    }

}
