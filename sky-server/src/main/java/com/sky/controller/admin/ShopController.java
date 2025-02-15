package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@ApiOperation("设置营业状态")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("营业状态设置为{}", status);
        //System.out.println(redisTemplate);
        redisTemplate.opsForValue().set("SHOP_Status", status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public  Result<Integer> getStatus() {
        Object status = redisTemplate.opsForValue().get("SHOP_Status");
        Integer statusInt= (Integer)status;
        log.info("获取到的营业状态{}", statusInt);
        return Result.success(statusInt);
    }
}
