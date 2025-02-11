package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    @AutoFill(value= OperationType.INSERT)
    void insertBatch(List<DishFlavor> dishFlavors);

    @Delete("delete  from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);


    void deleteByDishIds(List<Long> ids);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> GetById(Long id);
}
