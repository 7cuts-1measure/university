package simulation;

/**
 * Interface that view expects from model. It contains all methods to view for draw model state
 */
public interface ViewModel {
    int getMotorSupplierPerformance();
    
    int getBodySupplierPerformance();
    
    int getAccessorySupplierPerformance();
    int getNumAccessorySuppliers();
    
    int getMotorStorageSize() throws InterruptedException;
    int getMotorStorageCap();

    int getBodySorageSize() throws InterruptedException;
    int getBodyStorageCap();

    int getAccessoryStorageSize() throws InterruptedException;
    int getAccessoryStorageCap();

    int getCarStorageSize() throws InterruptedException;
    int getCarStorageCap();

    int getNumPendingTasks();
    int getNumTotalCarsAssembled();

    void shutdown();
}
