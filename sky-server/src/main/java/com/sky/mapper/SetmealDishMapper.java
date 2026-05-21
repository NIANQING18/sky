package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 查询菜品关联的套餐
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 新增菜品关联的套餐
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除菜品关联的套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id} order by dish_id")
    List<SetmealDish> getDishsBySetmealId(Long id);

    /**
     * 删除套餐关联的菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmeal(Long setmealId);

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    @Select("select sd.name,sd.copies,d.image,d.description from setmeal_dish sd left join dish d on sd.dish_id = d.id where setmeal_id = #{id}")
    List<DishItemVO> getDishItemById(Long id);
}
