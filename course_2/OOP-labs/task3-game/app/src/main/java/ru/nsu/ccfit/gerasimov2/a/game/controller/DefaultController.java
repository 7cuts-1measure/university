package ru.nsu.ccfit.gerasimov2.a.game.controller;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class DefaultController extends Controller {
    private Position firstSelection;
    private Position secondSelection;

    public DefaultController(GameModel model, View view) {
        super(model, view);
    }

    public void handleInput(Position userSelection) {
        if (userSelection == null) {
            return;
        }   
        if (firstSelection == null) {
            selectFirst(userSelection);
        } else if (userSelection.isSameAs(firstSelection)) { // gets the same selection
            deselectFirst();
            return;
        } else if (secondSelection == null) {
            selectSecond(userSelection);

            boolean isValidMove = model.isValidMove(firstSelection, secondSelection);
            if (isValidMove) {
                model.startSwapAnimation(firstSelection, secondSelection);   /* Calling the model */
                deselectFirst();
            } else {
                view.message("Wrong move");
            }            
            deselectSecond();
        } else {
            throw new IllegalStateException("Both selections are set but no move was done");
        }
    }
    
    private void deselectFirst() {
        firstSelection = null;
        updateSelection(null);
    }
    private void selectFirst(Position selection) {
        firstSelection = selection;
        updateSelection(selection);
    }

    private void deselectSecond() {
        secondSelection = null;
    }
    private void selectSecond(Position selection) {
        secondSelection = selection;
    }

    private void updateSelection(Position pos) {
        view.drawSelection(pos); // remove selection         
    }

    @Override
    public void resetModel() {
        model.restart();
    }

    @Override
    public void changeModel(GameModel model) {
        this.model = model;
        deselectSecond();
        deselectFirst();
    }
}

