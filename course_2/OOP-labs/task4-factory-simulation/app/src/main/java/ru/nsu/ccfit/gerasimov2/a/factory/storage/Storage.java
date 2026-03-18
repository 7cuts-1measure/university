package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;

public interface Storage {
    
    /**
     * @param product Product that matches this kind of storage
     * @throws UnsupportedProductException if specific kind of storage does not support specific type of product. 
     * For example, {@code AutoStorage} does not support {@code Motor} 
     * @throws StorageFullException if storage is full. 
     * In this case you shoud wait until storage will be free.
     */
    public void put(Product product) throws UnsupportedProductException, StorageFullException;

    public int getNumberOfProducts();
}
