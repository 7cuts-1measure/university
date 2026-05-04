package simulation.model.factory;

public interface Creator<T> {
    T newProduct(int id);
}
