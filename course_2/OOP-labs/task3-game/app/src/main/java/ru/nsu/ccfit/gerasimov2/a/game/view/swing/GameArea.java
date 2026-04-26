package ru.nsu.ccfit.gerasimov2.a.game.view.swing;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;
import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.AnimationState;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameArea extends JPanel {

    private int gridRows;
    private int gridCols;
    private Controller controller;
    private ModelBox modelBox;
    private Position cachedSelection;
    boolean isSelecting = false;

    private void drawGrid(Graphics g, int cellSize, int offsetX, int offsetY) {
        g.setColor(Color.black);
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                g.drawRect(col * cellSize + offsetX, row * cellSize + offsetY, cellSize, cellSize);
            }
        }
    }

    public GameArea(ModelBox modelBox) {
        this.modelBox = modelBox;
        this.gridCols = modelBox.getModel().getCols();
        this.gridRows = modelBox.getModel().getRows();
               
        setBackground(GameForm.bgColor);
        setBorder(BorderFactory.createLineBorder(Color.black, 2));
        setMinimumSize(getMinimumSize());
        setPreferredSize(getMinimumSize());
        addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
                if (modelBox.getModel().getAnimationState() == AnimationState.IDLE) handleMouseClick(e.getX(), e.getY());
            }
        }); 
        
    }


    private void handleMouseClick(int x, int y) {
        System.out.printf("Get mouse event: (%d, %d)\n", x, y);
        
        Position selection = null;
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
            controller.handleInput(selection);
        }

    }

    @Override
    public Dimension getMinimumSize() {
        int preferredCellSize = 40;
        System.out.println("preferred size: " + gridCols * preferredCellSize + ", " + gridRows *preferredCellSize); 
        return new Dimension(gridCols * preferredCellSize, gridRows * preferredCellSize); 
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
        
        int arcWidth = cellSize / 5;
        int arcHegiht = cellSize / 5;

        g.setColor(new Color(188, 215, 255));
        g.fillRoundRect(x, y, cellSize, cellSize, arcWidth, arcHegiht);
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
        int arcWidth = cellSize / 5;
        int arcHegiht = cellSize / 5;

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {          
                Gem gem = modelBox.getModel().gemAt(row, col);
                
                g.setColor(intToColor(gem.color));
                int identation = 2;
                g.fillRoundRect(col * cellSize + identation + offsetX, 
                            row * cellSize + identation + offsetY, 
                            cellSize - 2 * identation, 
                            cellSize - 2 * identation, arcWidth, arcHegiht);
                if (gem.isDestroyed()) {
                    g.setColor(Color.BLACK);
                    int indent = 10;
                    int x = col * cellSize + indent + offsetX;
                    int y = row * cellSize + indent + offsetY;
                    g.fillRect(x, y, cellSize - 2 * indent, cellSize - 2 * indent);
                }
            } 
        }

        g.setColor(previous);
    }

    public void setSelection(Position selectionPos) {
        this.cachedSelection = selectionPos;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}