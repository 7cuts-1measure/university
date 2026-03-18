package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Auto;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;

public class AutoStorage implements Storage {
    private Auto[] products;
    private int count;
    private final int MAX_COUNT = 100;

    @Override
    public void put(Product product) throws UnsupportedProductException, StorageFullException {
        if (product == null) throw new NullPointerException();
        if (count + 1 > MAX_COUNT) { throw new StorageFullException(); }
        if (!(product instanceof Auto)) throw new UnsupportedProductException("Supports only " + Auto.class.getSimpleName() + " but got " + product.getClass().getSimpleName());
        products[count++] = (Auto) product; 
   
    }
    public Auto getAuto() {
        count--;
        Auto tmp = products[count];
        products[count] = null;
        return tmp;
    }

    @Override
    public int getNumberOfProducts() {
        return count;
    }

    
}
