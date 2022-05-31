package com.lin.service;

import com.lin.vo.Result;
import com.lin.vo.UserVo;
import com.lin.dao.pojo.SysUser;
import com.lin.vo.params.UserPatam;

public interface SysUserService {

    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);

    /**
     * 根据账户查找用户
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);

    /**
     * 保存用户
     * @param sysUser
     */
    void save(SysUser sysUser);

    void updateLastlogin(SysUser sysUser);

    Result updateUser(UserPatam userPatam);
}
