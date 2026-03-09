package ru.nsu.ccfit.gerasimov2.a.game;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.controller.console.ConsoleController;

class App {
    public static void main(String[] args) {
        Controller controller = new ConsoleController();
        controller.run();
    }
}