package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.IdGenerator;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.StorageFullException;

public class BodySupplier implements Supplier {

    private Storage<Body> storage;
    private IdGenerator idGenerator = new IdGenerator();

    public BodySupplier(Storage<Body> storage) {
        this.storage = storage;
    }

    @Override
    public void supplyStorage() throws StorageFullException {
        Body product = new Body(idGenerator.next());    
        storage.put(product);
    }    
}
