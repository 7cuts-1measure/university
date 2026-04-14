package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.IdGenerator;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.StorageFullException;

public class AccessorySupplier implements Supplier {

    private Storage<Accessory> storage; 
    private IdGenerator idGenerator = new IdGenerator();
   
    public AccessorySupplier(Storage<Accessory> storage) {
        this.storage = storage;
    }
   
    @Override
    public void supplyStorage() throws StorageFullException {
        Accessory product = new Accessory(idGenerator.next());
        storage.put(product);
    }
    
}