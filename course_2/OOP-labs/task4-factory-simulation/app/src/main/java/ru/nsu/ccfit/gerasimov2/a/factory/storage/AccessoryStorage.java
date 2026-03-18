package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.UnsupportedProductException;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;

public class AccessoryStorage implements Storage {
    Accessory[] products;

    @Override
    public void put(Product product) throws UnsupportedProductException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'put'");
    }

    @Override
    public int getNumberOfProducts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumberOfProducts'");
    }


}
