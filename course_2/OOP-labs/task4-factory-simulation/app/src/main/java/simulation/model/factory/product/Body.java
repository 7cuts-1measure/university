package simulation.model.factory.product;

public class Body extends Product{

    public Body(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Body";
    }
    
}
