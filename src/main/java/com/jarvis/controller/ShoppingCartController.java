package com.jarvis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jarvis.common.BaseContext;
import com.jarvis.common.CustomException;
import com.jarvis.common.R;
import com.jarvis.entity.Setmeal;
import com.jarvis.entity.ShoppingCart;
import com.jarvis.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;




    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //先设置用户id,指定当前是哪个用户的购物车数据  因为前端没有传这个id给我们,但是这个id又非常重要（数据库这个字段不能为null）,
        // 所以要想办法获取到,我们在用户登录的时候就已经保存了用户的id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品是否或者是套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            //如果已经存在,就在原来的数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在,则添加到购物车，数量默认是1
            shoppingCart.setNumber(1);
            //设置创建时间，下面购物车的实现要用
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车。。。。");
        //每个人都有自己的购物车信息，只需要查询每个人的id对应的信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
//
//        shoppingCartService.remove(queryWrapper);
//        return R.success("清空购物车成功");
        return shoppingCartService.shoopingCartclean();
    }
    /**
     * 用户减少购物车菜品
     * @return
     */
    @PostMapping("/sub")
    @Transactional
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("用户减少的菜品：{}",shoppingCart);
        //减少菜品
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if(dishId != null){
            //必须保证操作购物车都得为同一对象
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
            Integer latestNumber = shoppingCart1.getNumber();
            if (latestNumber > 0){
                //大于0，使其减一
                shoppingCartService.updateById(shoppingCart1);
            }else if (latestNumber == 0){
                // 等于0 移除该菜品
                shoppingCartService.removeById(shoppingCart1);
            }else if(latestNumber < 0){
                throw new CustomException("菜品状态异常");
            }
            return R.success(shoppingCart1);
        }
        //减少套餐
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (setmealId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            lambdaQueryWrapper.eq(ShoppingCart::getId,BaseContext.getCurrentId());
            ShoppingCart cart = shoppingCartService.getOne(lambdaQueryWrapper);
            cart.setNumber(cart.getNumber() -1);
            Integer number = cart.getNumber();
            if (number > 0) {
                shoppingCartService.updateById(cart);
            }else if(number == 0){
                shoppingCartService.removeById(cart);
            }else if(number < 0) {
                throw new CustomException("套餐状态异常");
            }
            return R.success(cart);
        }




        return R.error("操作异常");
    }
}
