package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.StorageFullException;

public class AccessorySupplier implements Supplier {
    private int productID = 0;

    private Accessory produce() {
        return new Accessory(productID++);
    }

    @Override
    public void supplyStorage(Storage storage) throws UnsupportedProductException {
        var accessory = produce();
        try {
            storage.put(accessory);
        } catch (StorageFullException e) {
            System.out.println("Storage is full! Should wait!");
        }
    }
    
}
