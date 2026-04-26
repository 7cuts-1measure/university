package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.Timer;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.AnimationState;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class SwingView implements View {

    final int fallingTimerDealyMilliseconds = 700; 
    final int swapTimerDealyMilliseconds = 100; 
    final int destroyTimerDealyMilliseconds = 500; 
    
    private Timer fallingTimer;
    private Timer swapTimer;
    private Timer destroyTimer;
    

    private GameForm gameForm;
    private GameArea gameArea;
    private ScoreArea scoreArea;
    
    public ScoreArea getScoreArea() {
        return scoreArea;
    }

    private ModelBox modelBox;
    private Controller controller;

    public SwingView(GameModel model) {
        this.modelBox = new ModelBox(model);
        gameForm = new GameForm("tri v ryad", 640,  480, modelBox, this);
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
                if (modelBox.getModel().getAnimationState() == AnimationState.FALLING) {
                    modelBox.getModel().nextAnimationStep();
                }
            }
        });
        fallingTimer.start();


        destroyTimer = new Timer(destroyTimerDealyMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modelBox.getModel().getAnimationState() == AnimationState.DESTROY) {
                    modelBox.getModel().nextAnimationStep();
                }
            }
        });
        destroyTimer.start();

        swapTimer = new Timer(swapTimerDealyMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modelBox.getModel().getAnimationState() == AnimationState.SWAP) {
                    modelBox.getModel().nextAnimationStep();
                }
            }
        });
        swapTimer.start();
    }

    @Override
    public void update() {
        AnimationState state = modelBox.getModel().getAnimationState();
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
        System.err.println("message: " + string);
    }

    @Override
    public void popupMessage(String string) {
        Dialogs.showWarning(string);
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

    public GameArea getGameArea() {
        return gameArea;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}