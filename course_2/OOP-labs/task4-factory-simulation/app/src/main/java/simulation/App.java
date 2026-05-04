package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.gui.SimulationWindow;
import simulation.model.FactoryModel;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SetupLogger.setup();

        // 2. Создаём окно симуляции
        SimulationWindow window = new SimulationWindow();
        window.appendMessage("=== Симуляция фабрики запущена ===");

        // 3. Пример: создаём модель фабрики и передаём ей окно для отчётов
        FactoryModel model = new FactoryModel(window);
        
        // 4. Запускаем симуляцию (у вас, вероятно, какой-то цикл или таймер)
        model.startSimulation();

        // 5. Логгер продолжает работать параллельно
        log.info("Симуляция стартовала");
    }
}
