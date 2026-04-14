package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.IdGenerator;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.StorageFullException;

public class MotorSupplier implements Supplier {
    private Storage<Motor> storage;
    private IdGenerator idGenerator = new IdGenerator();
    
    public MotorSupplier(Storage<Motor> storage) {
        this.storage = storage;
    }

    @Override
    public void supplyStorage() throws StorageFullException {
        Motor product = new Motor(idGenerator.next());    
        storage.put(product);
    }    
}
