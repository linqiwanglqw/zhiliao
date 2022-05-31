package com.lin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lin.dao.mapper.SysUserMapper;
import com.lin.dao.pojo.SysUser;
import com.lin.service.LoginService;
import com.lin.service.SysUserService;
import com.lin.vo.ErrorCode;
import com.lin.vo.LoginUserVo;
import com.lin.vo.Result;
import com.lin.vo.UserVo;
import com.lin.vo.params.UserPatam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private LoginService loginService;

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("用户名不存在");
        }
        UserVo userVo  = new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
    }

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("用户名不存在");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,
                SysUser::getId,
                SysUser::getAvatar,
                SysUser::getNickname,
                SysUser::getDeleted,
                SysUser::getCreateDate,
                SysUser::getStatus);
        //limit 1可以避免全表查，遇到符合条件就终止
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {
        /**
         * 1. token合法性校验：
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 LoginUserVo
         */
        //获取token
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setStatus(sysUser.getStatus());
        return Result.success(loginUserVo);
    }

    /**
     * 注册 查询账号
     * @param account
     * @return
     */
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 注册 保存用户
     * @param sysUser
     */
    @Override
    public void save(SysUser sysUser) {
        //保存用户这 id会自动生成
        //这个地方 默认生成的id是 分布式id 雪花算法
        this.sysUserMapper.insert(sysUser);
    }

    @Override
    public void updateLastlogin(SysUser sysUser) {

        UpdateWrapper updateWrapper = new UpdateWrapper();

        updateWrapper.eq("account", sysUser.getAccount());

        updateWrapper.set("last_login", sysUser.getLastLogin());

        this.sysUserMapper.update(null, updateWrapper);
    }

    /**
     * 更新用户信息
     * @param userPatam
     * @return
     */
    @Override
    public Result updateUser(UserPatam userPatam) {
        UpdateWrapper updateWrapper = new UpdateWrapper();

        updateWrapper.eq("account", userPatam.getAccount());

        updateWrapper.set("nickname", userPatam.getNickname());

        updateWrapper.set("email", userPatam.getEmail());

        updateWrapper.set("sex", userPatam.getSex());

        updateWrapper.set("birthday",userPatam.getBirthday());

        int update = this.sysUserMapper.update(null, updateWrapper);
        if(update==1){
            return Result.success("信息修改成功");
        }else {
            return Result.fail(ErrorCode.UPDATE_ERROR.getCode(),ErrorCode.UPDATE_ERROR.getMsg());
        }
    }


}
