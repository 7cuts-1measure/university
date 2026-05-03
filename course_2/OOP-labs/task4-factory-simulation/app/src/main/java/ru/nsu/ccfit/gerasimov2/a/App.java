package ru.nsu.ccfit.gerasimov2.a;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.factory.supplier.SupplierTask;

public class App {

    public static void main(String[] args) {
        Storage<Body> bodyStorage = new Storage<>(10);
        SupplierTask<Body> supplier = new SupplierTask<>(10, bodyStorage, Body::new);
        supplier.run();
    }


}
