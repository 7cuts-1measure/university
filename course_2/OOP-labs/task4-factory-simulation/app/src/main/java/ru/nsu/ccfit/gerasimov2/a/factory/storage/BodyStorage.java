package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.product.FactoryProduct;

public class BodyStorage implements Storage {
    Body[] products;

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
