package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.time.Duration;


import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class SwingView extends View {

    private GameForm gameForm;

    public SwingView(Model model) {
        super(model);
        this.gameForm = new GameForm("tri v ryad", 640,  480, model);
    }

    void sleep() {
        try {
            Thread.sleep(Duration.ofMillis(600));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // interrupt our thread if other 
        }
    }

    @Override
    public void updateSuspended() {
        sleep();
        updateImmediatly();
        return;
    }

    @Override
    public void updateImmediatly() {
        gameForm.paintAll(gameForm.getGraphics());
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
    public Position getUserInputSelection() {
        return gameForm.getSelection();
    }

    @Override
    public void drawSelection(Position selectionPos) {
        gameForm.drawSelection(selectionPos);
    }

}