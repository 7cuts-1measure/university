package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Auto;
import ru.nsu.ccfit.gerasimov2.a.factory.product.FactoryProduct;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;

public class AutoStorage implements Storage {
    private Auto[] products;
    private int count;
    private final int MAX_COUNT = 100  

    @Override
    public void put(FactoryProduct product) throws UnsupportedProductException {
        if (count + 1 > MAX_COUNT) { throw new StorageFullException("Storage is full"); }
        if (!(product instanceof Auto)) throw new UnsupportedProductException("Supports only " + Motor.class.getSimpleName() + " but got " + product.getClass().getSimpleName());
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumberOfProducts'");
    }

    
}
