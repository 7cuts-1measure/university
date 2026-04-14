package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.AssemblyController;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;

public class StorageController {
    Storage<Car> storage;
    AssemblyController assembyController;
    
    public StorageController(Storage<Car> storage, AssemblyController assembyController) {
        this.storage = storage;
        this.assembyController = assembyController;
    }

    
    
}
