package io.examples.inventory.repository;

import io.examples.inventory.domain.Inventory;
import io.examples.inventory.repository.impl.InMemoryInventoryRepository;
import java.util.List;
import java.util.Optional;

/**
 * Inventory Repository
 *
 * @author Gary Cheng
 */
public interface InventoryRepository {

    static InventoryRepository getInstance() {
        return InMemoryInventoryRepository.getInstance();
    }

    /**
     * Return inventory list by product Id
     *
     * @param productId
     * @return
     */
    List<Inventory> getInventoryBYProductId(int productId);

    /**
     * Increase inventory
     *
     * @param increaseRequest the inventory increase request
     * @return
     */
    Inventory inscreaseInventory(Inventory increaseRequest);

    /**
     * Decrease inventory
     *
     * @param decreaseRequest
     * @return
     */
    Optional<Inventory> decreaseInventory(Inventory decreaseRequest);
}
