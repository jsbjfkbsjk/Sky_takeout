package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO){
        log.info("saveDish {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public  Result<PageResult> DishQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("DishQuery {}", dishPageQueryDTO);
        PageResult pageResult= dishService.DishQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("菜品删除")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("deleteDish {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("修改菜品回显")
    public Result<DishVO> getBYId(@PathVariable Long id){
        log.info("getBYId {}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);

    }


    @PutMapping
    @ApiOperation("更新菜品")
    public  Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("updateDish {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }



}
