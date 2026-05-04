package simulation.model;

import simulation.gui.SimulationWindow;

public class FactoryModel {
        private SimulationWindow uiWindow;

    public FactoryModel(SimulationWindow window) {
        this.uiWindow = window;
    }

    public void startSimulation() {
        // Имитация работы фабрики (например, в отдельном потоке, чтобы не блокировать UI)
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                String msg = "Станок #" + i + " начал обработку детали";
                uiWindow.appendMessage(msg);
                // Дублируем в логгер (если нужно)
                // log.info(msg);
                
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                
                if (i == 5) {
                    String errorMsg = "Ошибка: деталь бракованная!";
                    uiWindow.appendMessage(errorMsg);
                    // log.error(errorMsg);
                }
            }
            uiWindow.appendMessage("=== Симуляция завершена ===");
        }).start();
    }
}
