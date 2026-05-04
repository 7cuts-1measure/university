package simulation.model.factory.product;

public class Accessory extends Product {

    public Accessory(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Accessory";
    }

}
