package simulation.model.factory.product;

import static java.lang.String.format;

public abstract class Product {
    private final int id;

    public int getId() {
        return id;
    }

    public Product(int id) {
        this.id = id;
    }

    public abstract String getName();

    public String getFullName() {
        return format("%s<ID: %d>", getName(), getId());
    }
}
