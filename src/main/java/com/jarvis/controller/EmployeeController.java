package com.jarvis.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jarvis.common.R;
import com.jarvis.entity.Employee;
import com.jarvis.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author Shaun
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /*
     * 员工登录
     * */

    /**
     * 登录成功将session存到服务器
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) { //前端返回的是string类型  @RequestBody

        /**
         * 1. 将页面提交的password交给md5加密
         * 2.根据用户的username进行查询
         * 3.查询不到返回登录失败
         * 4，密码对比，密码不一致登录失败
         * 5.查看员工的状态，员工禁用则返回禁用状态
         * 6.登录成功，将用户的id存入session并放回登录成功
         *
         */
        String password = employee.getPassword();
        //md5 值加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //检查是否查到
        if (emp == null) {
            return R.error("登录失败");

        }
        //检查密码是否正确
        if (emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //判断是否已禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //登陆成功
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session 中的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     */

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes())); //md5 加密处理设置为初始密码

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //获取登录id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页
     *
     * @param page     当前页数
     * @param pageSize 当前页最多存放数据条数,就是这一页查几条数据
     * @param name     根据name查询员工的信息
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //这里之所以是返回page对象(mybatis-plus的page对象)，是因为前端需要这些分页的数据(比如当前页，总页数)
        //在编写前先测试一下前端传过来的分页数据有没有被我们接受到
        //log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器  就是page对象
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器  就是动态的封装前端传过来的过滤条件  记得加泛型
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //根据条件查询  注意这里的条件是不为空
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询  这里不用封装了mybatis-plus帮我们做好了
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * 启用员工状态更新员工操作
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now()); //更新时间
//        employee.setUpdateUser(empId); //更新人
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 通过url 地址栏进行传参
     * 修改用户时通过id使得用户回显  数据完成修改时调用上面的update 方法
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据员工查询用户信息。。");
        Employee employee =  employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查到员工信息");
    }
}