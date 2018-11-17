package io.examples.cqrs.petstore.queryobject;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Gary Cheng
 */
public interface ProductQueryObjectRepository extends JpaRepository<ProductQueryObject, String> {
}
