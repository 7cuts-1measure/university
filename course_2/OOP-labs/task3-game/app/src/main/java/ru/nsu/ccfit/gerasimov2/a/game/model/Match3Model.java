package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.util.List;


import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;
import ru.nsu.ccfit.gerasimov2.a.game.model.factory.GemFactory;
import ru.nsu.ccfit.gerasimov2.a.game.model.strategy.DestroyStratagy;
import ru.nsu.ccfit.gerasimov2.a.game.model.strategy.Match3DestroyStrategy;

class Move {
    public Position start;
    public Position end;

    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
}

public class Match3Model extends GameModel {
    private GemField gemField;
    private DestroyStratagy destroyAlgo;
    private AnimationState currAnimationState = AnimationState.IDLE;
    private Move move;
    private int score;
    public boolean isAnimating = false;
    private int rows;
    private int cols;
    private GemFactory gemFactory;

    @Override
    public void restart() {
        reset();
        
        gemField = new GemField(cols, rows, gemFactory);
        isAnimating = true;
        currAnimationState = AnimationState.DESTROY;
    }


    @Override
    public void reset() {
        score = 0;
        move = null;
        isAnimating = false;
        currAnimationState = AnimationState.IDLE;
    }


    public Match3Model(int rows, int cols, int maxColor) {
        this.rows   = rows;
        this.cols   = cols;
        gemFactory  = new GemFactory(maxColor);
        gemField    = new GemField(this.rows, this.cols, gemFactory);
        destroyAlgo = new Match3DestroyStrategy();

    }

    public List<Position> getPositionsToDestroy() {
        return destroyAlgo.getPositionsToDestroy(gemField);
    }

    @Override
    public Gem gemAt(int row, int col) {
        return gemField.at(row, col);
    }

    private boolean isOutOfBounds(Position p1) {
        int row = p1.getRow();
        int col = p1.getCol();

        return !(0 <= row && row <= gemField.getRows() 
            && 0 <= col && col <= gemField.getCols()); 
    }

    @Override
    public boolean checkMove(Position p1, Position p2) {
        boolean isSame = p1.getCol() == p2.getCol() && p1.getRow() == p2.getRow();

        int diffRows = Math.abs(p1.getRow() - p2.getRow());
        int diffCols = Math.abs(p1.getCol() - p2.getCol());
        boolean isNeghbours = diffRows <= 1 && diffCols <= 1 && diffCols + diffRows != 2; 
        boolean isOutOfBounds = isOutOfBounds(p1) || isOutOfBounds(p2);
        
        if (isSame || !isNeghbours || isOutOfBounds) {
            return false;
        }
        // now we sure that positions are correct and we allowed to try to swap gems 
        gemField.swap(p1, p2);  
        boolean isValidMove = destroyAlgo.isDestroyable(gemField);
        gemField.swap(p1, p2);
        return isValidMove;
        
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getCols() {
        return gemField.getCols();
    }

    @Override
    public int getRows() {
        return gemField.getRows();
    }

    @Override
    public AnimationState getAnimationState() {
        return currAnimationState;
    }

    @Override
    public void startSwapAnimation(Position p1, Position p2) {
        isAnimating = true;
        currAnimationState = AnimationState.SWAP;
        move = new Move(p1, p2);
    }

    @Override
    public void nextAnimationStep() {
        switch (currAnimationState) {
            case IDLE:
                isAnimating = false;
                return;
            case SWAP:
                doSwap();
                notifyView(); /* update the view */
                currAnimationState = AnimationState.DESTROY;
                break;
            case DESTROY:
                doDestroy();
                notifyView(); /* update the view */
                currAnimationState = AnimationState.FALLING;
                break;
            case FALLING:
                doFalling();
                notifyView(); /* update the view */
                if (destroyAlgo.isDestroyable(gemField)) {
                    currAnimationState = AnimationState.DESTROY;
                } else {
                    currAnimationState = AnimationState.IDLE; 
                    isAnimating = false; /* end animating */
                }
                break;
            default:
                throw new AnimationStateException("Unknown animation state");
        }


        
    }
    
    private void doSwap() {
        if (move == null) throw new AnimationStateException("No move was set");
        gemField.swap(move.start, move.end);
        move = null;

    }
    
    private void doDestroy() {
        List<Position> toDestroy = getPositionsToDestroy();
        score += toDestroy.size() * 10;
        toDestroy.forEach(gemPos -> gemField.destroyAt(gemPos));
    }
    
    private void doFalling() {
        gemField.refillDestroyed();
    }

    @Override
    public boolean isAnimating() {
        return isAnimating;
    }


    @Override
    public Gem gemAt(Position currPos) {
        return gemField.at(currPos);
    }
}
