package ru.nsu.ccfit.gerasimov2.a.game.view;

import java.io.IOException;
import java.time.Duration;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.AnimationState;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;

public class ConsoleView implements View {
    Scanner scanner;    
    Position selectionPos;
    GameModel model;

    Controller controller;

    public ConsoleView(GameModel model) {
        this.model = model;
        scanner = new Scanner(System.in);
    }

    /**
     * Reading user input in foramt ROW [space] COL.
     * @return User input
     * @throws InputMismatchException  if the next token does not match the Integer regular expression, or is out of range
     * @throws NoSuchElementException  if input is exhausted
     * @throws IllegalStateException  if this scanner is closed
     */
    private Position readInputPosition() {
        while (true) {
            String rowString = scanner.next();
            String colString = scanner.next();
            try {
                Integer row = Integer.valueOf(rowString);
                Integer col = Integer.valueOf(colString);
                Position pos = new Position(row, col);

                return pos;
            } catch (NumberFormatException e) {
                popupMessage("You should type input as <ROW COL>");
            }
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void displayGemField() {
        System.out.print("    ");
        for (int i = 0; i < model.getCols(); i++) {
            System.out.printf("%d ", i);
        }
        System.out.println();
        System.out.println("---------------------");

        for (int i = 0; i < model.getRows(); i++) {
            System.out.printf("%d | ", i);
            for (int j = 0; j < model.getCols(); j++) {
                Position currPos    = new Position(i, j);
                Gem gem             = model.gemAt(currPos);
                printGem(gem, currPos, selectionPos);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("-------------------");
    }

    private void printGem(Gem gem, Position gemPos, Position selection) {
        String DestroyFmt = "\033[1;40m";
        String resetFmt = "\033[m";

        
        String gemString;
        if (gem.isDestroyed()) {
            gemString = DestroyFmt + "x" + resetFmt;
        } else {
            String colorFmt = "\033[0;3" + gem.color + "m";
            String backgrundFmt = gemPos.isSameAs(selection) ? "\033[7m" : "";
            gemString = colorFmt + backgrundFmt + gem.color + resetFmt; 
        }
        System.out.print(gemString);
    }

    @Override
    public void update() {
        displayClear();
        displayGemField();
        displayScore();
    }

    private void displayClear() {
        System.out.print("\033[2J\033[H");            
    }

    private void displayScore() {
        System.out.println("Score: " + model.getScore());
    }

    @Override
    public void message(String msg) {
        System.out.println(msg);
    }

    @Override
    public void popupMessage(String msg) {
        System.out.println(msg);
    }

    @Override
    public void drawSelection(Position selectionPos) {
        this.selectionPos = selectionPos;
        update();       
    }


    private void processFullAnimation() {
        while (model.getAnimationState() != AnimationState.IDLE) {
            model.nextAnimationStep();
            sleep(Duration.ofMillis(1000));
        }
    }

    @Override
    public void start() {
        model.restart();
        while (true) {
            processFullAnimation();
            System.out.print("-> ");  // printing prompt
            clearInputStream();
            Position userInput = readInputPosition();
            if (controller == null) {
                throw new IllegalStateException("Contoller in null. You need to call setController() to start the game");
            }
            controller.handleInput(userInput);
            model.nextAnimationStep();  // skip swap animation
        }
    }

    private void clearInputStream() {
        try {
            int skip = System.in.available();
            System.in.readNBytes(skip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}