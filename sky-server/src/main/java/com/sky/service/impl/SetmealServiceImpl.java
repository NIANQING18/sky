package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal",key = "#setmealDTO.categoryId")
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insert(setmeal);
        //获得自增主键
        Long id = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : setmealDishes) {
            dish.setSetmealId(id);
        }
        log.info("插入套餐菜品关系");
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐分页查询
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<Setmeal> page = setmealMapper.page(setmealPageQueryDTO);
        long total = page.getTotal();
        List<Setmeal> result = page.getResult();
        return new PageResult(total,result);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        setmealMapper.delete(ids);

        setmealDishMapper.delete(ids);
    }

    /**
     * 查询套餐
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishsBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void update(SetmealDTO setmealDTO) {
        Long setmealId = setmealDTO.getId();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmeal(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();


        if(!setmealDishes.isEmpty()) {
            for (SetmealDish dish : setmealDishes) {
                dish.setSetmealId(setmealId);
            }
            log.info("更新套餐内的菜品");
            setmealDishMapper.insertBatch(setmealDishes);
        }


    }
    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    @Override
    @Cacheable(cacheNames = "setmeal" , key = "#setmeal.categoryId")
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list =  setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "setmeal_dish",key = "#id")
    public List<DishItemVO> getDishItemById(Long id) {
       List<DishItemVO> list =  setmealDishMapper.getDishItemById(id);
       return list;
    }
}
