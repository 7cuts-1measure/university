package simulation.model.factory;

import org.jspecify.annotations.NonNull;

public interface Creator<T> {
    @NonNull
    T newProduct(int id);
}
