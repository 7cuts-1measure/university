package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.time.Duration;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class SwingView extends View {

    private GameForm gameForm;
    private GameArea gameArea;

    public SwingView(GameModel model) {
        super(model);
        this.gameForm = new GameForm("tri v ryad", 640,  480, model);
        this.gameArea = gameForm.getGameArea();
    }

    void sleep() {
        try {
            Thread.sleep(Duration.ofMillis(600));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // interrupt our thread if other 
        }
    }

    @Override
    public void update() {
        updateImmediatly();
        return;
    }

    @Override
    public void updateImmediatly() {
        //gameForm.paint(gameForm.getGraphics());
        gameForm.repaint(100);
        
    }

    @Override
    public void message(String string) {
        System.err.println("message");
    }

    @Override
    public void popupMessage(String string) {
        System.err.println("display  msg");
    }

    @Override
    public void drawSelection(Position selectionPos) {
        gameForm.drawSelection(selectionPos);
    }

    @Override
    public void start() {
        model.step();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
        gameForm.setController(controller);
    }

}