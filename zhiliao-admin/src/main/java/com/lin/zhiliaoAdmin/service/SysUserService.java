package com.lin.zhiliaoAdmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.SysUserMapper;
import com.lin.zhiliaoAdmin.dao.pojo.Article;
import com.lin.zhiliaoAdmin.dao.pojo.SysUser;
import com.lin.zhiliaoAdmin.vo.ErrorCode;
import com.lin.zhiliaoAdmin.vo.PageResult;
import com.lin.zhiliaoAdmin.vo.Result;
import com.lin.zhiliaoAdmin.vo.params.PageParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserService userService;

    public Result listSysUser(PageParam pageParam) {
        Page<SysUser> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            queryWrapper.like(SysUser::getAccount, pageParam.getQueryString()).or().like(SysUser::getNickname, pageParam.getQueryString());
        }
        Page<SysUser> permissionPage = this.sysUserMapper.selectPage(page, queryWrapper);
        PageResult<SysUser> pageResult = new PageResult<>();
        for (int i = 0; i < permissionPage.getRecords().size(); i++) {
            if ("1".equals(permissionPage.getRecords().get(i).getAdmin())) {
                permissionPage.getRecords().remove(i);
                i = -1;
            } else {
                break;
            }
        }
        pageResult.setList(permissionPage.getRecords());
        pageResult.setTotal(permissionPage.getTotal());

        return Result.success(pageResult);
    }

    public Result add(SysUser sysUser) {
        if (sysUser.getAccount() == null || sysUser.getNickname() == null || sysUser.getPassword() == null || sysUser.getEmail() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        String account = sysUser.getAccount();
        String nickName = sysUser.getNickname();
        String password = sysUser.getPassword();
        String email = sysUser.getEmail();
        Result result = userService.checkInfo(account, nickName, password, email);
        if (!(result.isSuccess())) {
            return result;
        }
        System.out.println(result.isSuccess());
        String salt = UUID.randomUUID().toString().toUpperCase();
        password = getMD5Password(password, salt);
        SysUser user = new SysUser();
        user.setAvatar("/static/img/logo.b3a48c0.png");
        user.setAccount(account);
        user.setPassword(password);
        user.setNickname(nickName);
        user.setCreateDate(System.currentTimeMillis());
        user.setEmail(email);
        user.setAdmin("0");
        user.setSalt(salt);
        this.sysUserMapper.insert(user);
        return Result.success(null);
    }

    public Result update(SysUser sysUser) {

        // 首先对其输入的信息进行校验
        Result result = userService.checkSysUserInfo(sysUser.getId(), sysUser.getAccount(), sysUser.getPassword(), sysUser.getNickname(), sysUser.getEmail());
        if (!(result.isSuccess())) {
            return result;
        }
        SysUser user = userService.selectSysUserByAccount(sysUser.getAccount());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sys = new SysUser();
        if (user != null) {
            String password = user.getPassword();
            String salt = user.getSalt();
            if (!(sysUser.getPassword().equals("******"))) {
                password = getMD5Password(password, salt);
                sys.setPassword(password);
            }
        }
        sys.setAccount(sysUser.getAccount());
        sys.setNickname(sysUser.getNickname());
        sys.setEmail(sysUser.getEmail());
        queryWrapper.eq(SysUser::getId, sysUser.getId());
        this.sysUserMapper.update(sys, queryWrapper);
        return Result.success(null);
    }

    public Result delete(Long id) {
        this.sysUserMapper.deleteById(id);
        // 同时将与该用户所有与之相关联的全部删除
        List<Article> articleList = userService.selectArticleByAuthorId(id);
        userService.deleteArticleCommentArticleBody(articleList);
        return Result.success(null);
    }

    /**
     * 定义一个md5算法的加密处理
     * */
    private String getMD5Password(String password, String salt) {
        //md5加密算法方法的调用
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5Hex((salt + password + salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
    }
}
