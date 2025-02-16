package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShopingCartService {
     void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> show();

    void deleteAll();

    void delete(ShoppingCartDTO shoppingCartDTO);
}
