package ru.nsu.ccfit.gerasimov2.a.game.controller;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class DefaultController extends Controller {
    private Position firstSelection;
    private Position secondSelection;
    private boolean isRunning = true;

    public DefaultController(Model model, View view) {
        super(model, view);
    }

    // DefaultController accept only 2 inputs!
    public void handleInput(Position userSelection) {
        if (userSelection == null) {
            return;
        }   
        if (firstSelection == null) {
            selectFirst(userSelection);
        } else if (userSelection.isSameAs(firstSelection)) { // gets the same selection
            deselectFirst();
            return;
        } 
        else if (secondSelection == null) {
            selectSecond(userSelection);

            boolean isMovable = model.setMove(firstSelection, secondSelection);
            
            if (isMovable) {
                deselectFirst();
                model.step();
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
            view.updateImmediatly();  // show it immediatly 
         
    }

    public void runGame() {
        view.updateImmediatly();    // show model for the first time
        model.step();
        while (isRunning) {
            handleInput(view.getUserInputSelection());        
        }

    }
}
