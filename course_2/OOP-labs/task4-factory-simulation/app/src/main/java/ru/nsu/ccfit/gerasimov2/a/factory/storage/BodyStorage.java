package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;

public class BodyStorage implements Storage {
    final int MAX_COUNT = 100;
    Body[] products = new Body[MAX_COUNT];
    int count = 0;

    @Override
    public void put(Product product) throws UnsupportedProductException, StorageFullException {
        if (product == null) throw new NullPointerException();
        if (count + 1 > MAX_COUNT) { throw new StorageFullException(); }
        if (!(product instanceof Body)) throw new UnsupportedProductException("Supports only " + Body.class.getSimpleName() + " but got " + product.getClass().getSimpleName());
        products[count++] = (Body) product; 
    }

	@Override
	public int getNumberOfProducts() {
        return count;
	}
    
}
