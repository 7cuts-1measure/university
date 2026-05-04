package simulation.model;

/**
 * Interface that view expects from model. It contains all methods to view for draw model state
 */
public interface ViewModel {
    int getMotorSupplierPerformance();
    
    int getBodySupplierPerformance();
    
    int getAccessorySupplierPerformance();
    int getNumAccessorySuppliers();
    
    int getMotorStorageSize();
    int getMotorStorageCap();

    int getBodySorageSize();
    int getBodyStorageCap();

    int getAccessoryStorageSize();
    int getAccessoryStorageCap();

    int getCarStorageSize();
    int getCarStorageCap();

    int getNumActiveWorkers();
    int getNumTotalWorkets();
}
