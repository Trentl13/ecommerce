package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
