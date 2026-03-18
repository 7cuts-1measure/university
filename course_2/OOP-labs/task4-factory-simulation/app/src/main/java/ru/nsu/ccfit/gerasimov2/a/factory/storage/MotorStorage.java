package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;
import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;

public class MotorStorage implements Storage {
    final int MAX_COUNT = 100;
    int count = 0;
    Motor[] products;

    @Override
    public void put(Product product) throws StorageFullException, UnsupportedProductException {
        if (product == null) throw new NullPointerException();
        if (count + 1 > MAX_COUNT) { throw new StorageFullException(); }
        if (!(product instanceof Motor)) throw new UnsupportedProductException("Supports only " + Motor.class.getSimpleName() + " but got " + product.getClass().getSimpleName());
        products[count++] = (Motor) product; 
    }

	@Override
	public int getNumberOfProducts() {
        return count;
	}
}
