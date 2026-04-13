package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;

import javax.swing.Timer;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.AnimationState;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class SwingView extends View {

    final int fallingTimerDealyMilliseconds = 700; 
    final int swapTimerDealyMilliseconds = 100; 
    final int destroyTimerDealyMilliseconds = 500; 
    private Timer fallingTimer;
    private Timer swapTimer;
    private Timer destroyTimer;
    


    private GameForm gameForm;
    private GameArea gameArea;
    private ScoreArea scoreArea;


    public SwingView(GameModel model) {
        super(model);
        gameForm = new GameForm("tri v ryad", 640,  480, model);
        gameArea = gameForm.getGameArea();
        gameArea.setVisible(false);
        scoreArea = gameForm.getScoreArea();
       
        gameForm.setVisible(false);
        initTimers();
        
    }

    private void initTimers() {
        fallingTimer = new Timer(fallingTimerDealyMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isAnimating() && model.getAnimationState() == AnimationState.FALLING) {
                    model.nextAnimationStep();
                }
            }
        });
        fallingTimer.start();


        destroyTimer = new Timer(destroyTimerDealyMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isAnimating() && model.getAnimationState() == AnimationState.DESTROY) {
                    model.nextAnimationStep();
                }
            }
        });
        destroyTimer.start();

        swapTimer = new Timer(swapTimerDealyMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isAnimating() && model.getAnimationState() == AnimationState.SWAP) {
                    model.nextAnimationStep();
                }
            }
        });
        swapTimer.start();
    }

    @Override
    public void update() {
        AnimationState state = model.getAnimationState();
        restartTimers();
        System.out.println("Current animation state: " + state);
        
        switch (state) {
            case IDLE:
                return;
            case SWAP:
                gameArea.repaint();
                break;
            case DESTROY:
                gameArea.repaint();
                scoreArea.repaint();
                break;
            case FALLING:
                gameArea.repaint();
                break;
            default:
                break;
        }
        return;
    }

    private void restartTimers() {
        swapTimer.restart();
        destroyTimer.restart();
        fallingTimer.restart();
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
        gameForm.setVisible(true);
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
        gameForm.setController(controller);
    }

}