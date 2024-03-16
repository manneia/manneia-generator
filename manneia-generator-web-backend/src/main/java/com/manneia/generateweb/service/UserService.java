package com.manneia.generateweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manneia.generateweb.model.dto.user.UserQueryRequest;
import com.manneia.generateweb.model.entity.User;
import com.manneia.generateweb.model.vo.LoginUserVo;
import com.manneia.generateweb.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author lkx
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 返回当前用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return 返回是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 判断是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 返回是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return 返回脱敏的登录用户信息
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return 返回脱敏后的用户信息
     */
    UserVo getUserVo(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList 请求
     * @return 返回脱敏后的用户信息列表
     */
    List<UserVo> getUserVo(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询条件
     * @return 返回封装后的查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
