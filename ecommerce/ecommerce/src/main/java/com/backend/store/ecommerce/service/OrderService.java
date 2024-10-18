package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.repository.WebOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private WebOrderRepository webOrderRepository;

    public OrderService(WebOrderRepository webOrderRepository) {
        this.webOrderRepository = webOrderRepository;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderRepository.findByUser(user);
    }
}
