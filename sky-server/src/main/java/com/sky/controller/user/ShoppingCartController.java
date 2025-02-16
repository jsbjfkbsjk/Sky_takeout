package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags="购物车")
public class ShoppingCartController {
    @Autowired
    private ShopingCartService shopingCartService;


    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车{}", shoppingCartDTO.toString());
        shopingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("显示购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车");
        List<ShoppingCart> shoppingCarts = shopingCartService.show();
        return Result.success(shoppingCarts);
    }
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result deleteAll(){
        log.info("清空购物车");
        shopingCartService.deleteAll();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("清除指定商品")
    public Result delete(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除指定商品");
        shopingCartService.delete(shoppingCartDTO);
        return Result.success();

    }


}
