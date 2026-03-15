package ru.nsu.ccfit.gerasimov2.a.game.view.swing;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;
import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameArea extends JPanel {

    private int gridRows;
    private int gridCols;
    private int gridCellSize;
    private Model model;
    private Position cachedSelection;
    boolean isSelecting = false;
    volatile Position selection = null;


    private void drawGrid(Graphics g) {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                g.setColor(Color.black);
                g.drawRect(col * gridCellSize, row * gridCellSize, gridCellSize, gridCellSize);
            }
        }
    }

    public GameArea(Rectangle bounds, Model model) {
        super();

        // set constructor params
        this.model = model;
        this.setBounds(bounds);

        this.setBackground(GameForm.bgColor);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
        this.gridCols = model.getGemField().getCols();
        this.gridCellSize = this.getBounds().width / gridCols;
        this.gridRows = model.getGemField().getRows();
        
        if (this.getBounds().width % gridCols != 0 || this.getBounds().height % gridRows != 0) {
            throw new IllegalArgumentException("Width and height должны быть кратны количеству клеток");
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.printf("Get mouse event: (%d, %d)\n", e.getX(), e.getY());

                int col = x / gridCellSize;
                int row = y / gridCellSize;
                System.out.printf("Calculated col=%d, row=%d\n", col, row);
                if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {
                    selection = new Position(row, col);
                }
            }
        }); 
        
    }
    
    public Position getSelection() {
        Position pos = selection;
        if (selection != null) {
            selection = null;             
        }
        return pos;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCachedSelection(g);
        drawGemsOnField(g);
        drawGrid(g);
    }

    private void drawCachedSelection(Graphics g) {
        if (cachedSelection == null) { return; }

        int row = cachedSelection.getRow();
        int col = cachedSelection.getCol();

        int x = col * gridCellSize;
        int y = row * gridCellSize;
        
        g.setColor(new Color(188, 215, 255));
        g.fillRect(x, y, gridCellSize, gridCellSize);
    }

    private Color intToColor(int number) {
        switch (number) {
            case 0: return Theme.BLUE;
            case 1: return Theme.RED;
            case 2: return Theme.WHITE;
            case 3: return Theme.ORANGE;
            case 4: return Theme.GREEN;
            case 5: return Theme.GRAY;
            case 6: return Color.MAGENTA;
            default:
                return Color.DARK_GRAY;
        }
    }


    private void drawGemsOnField(Graphics g) {
        Color previous = g.getColor();

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {          
                Gem gem = model.gemAt(row, col);

                g.setColor(intToColor(gem.color));
                g.fillRect(col * gridCellSize + 2, row * gridCellSize + 2, gridCellSize - 4, gridCellSize - 4);
                if (gem.isDestroyed()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(col * gridCellSize + 10, row * gridCellSize + 10, gridCellSize - 20, gridCellSize - 20);
                }
            } 
        }

        g.setColor(previous);
    }

    public void setSelection(Position selectionPos) {
        this.cachedSelection = selectionPos;
    }
}