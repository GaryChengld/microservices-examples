package io.examples.inventory.repository.impl;

import io.examples.inventory.domain.Inventory;
import io.examples.inventory.repository.InventoryRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * Implement Inventory repository in memory
 *
 * @author Gary Cheng
 */
public class InMemoryInventoryRepository implements InventoryRepository {
    private static InventoryRepository instance = new InMemoryInventoryRepository();
    private static final Comparator<Inventory> COMPARATOR = Comparator.comparing(Inventory::getProductId).thenComparing(Inventory::getLocation);
    private final Set<Inventory> inventorySet = new ConcurrentSkipListSet<>(COMPARATOR);


    public static InventoryRepository getInstance() {
        return instance;
    }

    @Override
    public List<Inventory> getInventoryBYProductId(int productId) {
        return inventorySet.stream()
                .filter(inventory -> inventory.getProductId() == productId)
                .sorted(COMPARATOR)
                .collect(Collectors.toList());
    }

    @Override
    public Inventory inscreaseInventory(Inventory increaseRequest) {
        Optional<Inventory> inventoryOptional = inventorySet.stream().filter(inventory -> inventory.equals(increaseRequest)).findFirst();
        Inventory inventory;
        if (inventoryOptional.isPresent()) {
            inventory = inventoryOptional.get();
            inventory.setQuantity(inventory.getQuantity() + increaseRequest.getQuantity());
        } else {
            inventory = increaseRequest;
            inventorySet.add(inventory);
        }
        return inventory;
    }

    @Override
    public Optional<Inventory> decreaseInventory(Inventory decreaseRequest) {
        Optional<Inventory> inventoryOptional = inventorySet.stream().filter(inventory -> inventory.equals(decreaseRequest)).findFirst();
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setQuantity(inventory.getQuantity() - decreaseRequest.getQuantity());
            return Optional.of(inventory);
        } else {
            return Optional.empty();
        }
    }
}
