package ru.nsu.ccfit.gerasimov2.a.game.view.swing;


import java.awt.Color;
import java.awt.Dimension;
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
    private Model model;
    private Position cachedSelection;
    boolean isSelecting = false;
    volatile Position selection = null;


    private void drawGrid(Graphics g, int cellSize, int offsetX, int offsetY) {
        g.setColor(Color.black);
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                g.drawRect(col * cellSize + offsetX, row * cellSize + offsetY, cellSize, cellSize);
            }
        }
    }

    public GameArea(Model model) {
        super();

        // set constructor params
        this.model = model;

        this.setBackground(GameForm.bgColor);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
        this.gridCols = model.getGemField().getCols();
        this.gridRows = model.getGemField().getRows();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        }); 
        
    }
    
    private void handleMouseClick(int x, int y) {
        System.out.printf("Get mouse event: (%d, %d)\n", x, y);
        int width = getWidth();
        int height = getHeight();

        int cellWidth = width / gridCols;
        int cellHeight = height / gridRows;

        int gridSize = Math.min(cellWidth, cellHeight);

        int offsetX = (width - (gridSize * gridCols)) / 2;
        int offsetY = (height - (gridSize * gridRows)) / 2;

        // центрируем сетку, если вся gamearea больше, чем нужно для наших
        // минимальных клеток
        int col = (x - offsetX) / gridSize;
        int row = (y - offsetY) / gridSize;

        System.out.printf("Calculated col=%d, row=%d\n", col, row);
        if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {
            selection = new Position(row, col);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        int preferredCellSize = 20; 
        return new Dimension(gridCols * preferredCellSize, gridRows * preferredCellSize); 
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
        
        int width = getWidth();
        int height = getHeight();

        // Динамический расчет размера клетки при каждой перерисовке
        int cellW = width / gridCols;
        int cellH = height / gridRows;
        int gridCellSize = Math.min(cellW, cellH);

        // Центрирование сетки внутри панели
        int totalGridWidth = gridCellSize * gridCols;
        int totalGridHeight = gridCellSize * gridRows;
        int offsetX = (width - totalGridWidth) / 2;
        int offsetY = (height - totalGridHeight) / 2;
        
        drawGrid(g, gridCellSize, offsetX, offsetY);
        drawCachedSelection(g, gridCellSize, offsetX, offsetY);
        drawGemsOnField(g, gridCellSize, offsetX, offsetY);
    }

    private void drawCachedSelection(Graphics g, int cellSize, int offsetX, int offsetY) {
        if (cachedSelection == null) { return; }

        int row = cachedSelection.getRow();
        int col = cachedSelection.getCol();

        int x = col * cellSize + offsetX;
        int y = row * cellSize + offsetY;
        
        g.setColor(new Color(188, 215, 255));
        g.fillRect(x, y, cellSize, cellSize);
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


    private void drawGemsOnField(Graphics g, int cellSize, int offsetX, int offsetY) {
        Color previous = g.getColor();

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {          
                Gem gem = model.gemAt(row, col);
                
                g.setColor(intToColor(gem.color));
                int identation = 2;
                g.fillRect(col * cellSize + identation + offsetX, 
                            row * cellSize + identation + offsetY, 
                            cellSize - 2 * identation, 
                            cellSize - 2 * identation);
                if (gem.isDestroyed()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(col * cellSize + 10 + offsetX, row * cellSize + 10 + offsetY, cellSize - 20, cellSize - 20);
                }
            } 
        }

        g.setColor(previous);
    }

    public void setSelection(Position selectionPos) {
        this.cachedSelection = selectionPos;
    }
}