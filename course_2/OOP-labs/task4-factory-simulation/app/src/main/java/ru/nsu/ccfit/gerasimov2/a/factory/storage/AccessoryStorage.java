package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.product.FactoryProduct;

public class AccessoryStorage implements Storage {
    Accessory[] products;

    @Override
    public void put(FactoryProduct product) throws UnsupportedProductException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'put'");
    }

    @Override
    public int getNumberOfProducts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumberOfProducts'");
    }


}
