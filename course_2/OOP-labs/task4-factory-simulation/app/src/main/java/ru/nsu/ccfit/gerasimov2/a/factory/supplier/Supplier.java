package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.storage.StorageFullException;

public interface Supplier {
    void supplyStorage() throws StorageFullException;
}