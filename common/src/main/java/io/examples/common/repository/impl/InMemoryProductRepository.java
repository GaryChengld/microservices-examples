package io.examples.common.repository.impl;

import io.examples.common.domain.Product;
import io.examples.common.domain.ProductUtils;
import io.examples.common.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final Map<Integer, Product> productMap = new ConcurrentHashMap<>();
    private static ProductRepository instance = new InMemoryProductRepository();

    public static ProductRepository getInstance() {
        return instance;
    }

    private InMemoryProductRepository() {
        ProductUtils.initData().forEach(product -> productMap.put(product.getId(), product));
        idGenerator.set(productMap.keySet().stream().mapToInt(Integer::intValue).max().getAsInt() + 1);
    }

    @Override
    public List<Product> getProducts() {
        return productMap.values()
                .stream()
                .sorted(Comparator.comparing(product -> product.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productMap.values()
                .stream()
                .sorted(Comparator.comparing(product -> product.getId()))
                .filter(product -> category.equals(product.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProductById(int id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public Product addProduct(Product product) {
        product.setId(idGenerator.incrementAndGet());
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public void updateProduct(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        productMap.put(product.getId(), product);
    }

    @Override
    public void deleteProduct(int id) {

    }
}
