package simulation.model.factory.product;

public class Motor extends Product {

    public Motor(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Motor";
    }
    
}
