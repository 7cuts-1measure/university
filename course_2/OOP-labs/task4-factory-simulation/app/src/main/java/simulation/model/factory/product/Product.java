package simulation.model.factory.product;

public abstract class Product {
    private final int id;

    public int getId() {
        return id;
    }

    public Product(int id) {
        this.id = id;
    }
}
