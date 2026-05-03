package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;

public interface Creator<T extends Product> {
    T create();
}
