package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;

public interface Supplier {
    /**
     * 
     * @param storage
     * @throws UnsupportedProductException If supplier does not produce products for this type of storage
     */
    public void supplyStorage(Storage storage) throws UnsupportedProductException;
}