package com.lin.zhiliaoAdmin.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.*;
import com.lin.zhiliaoAdmin.dao.pojo.*;
import com.lin.zhiliaoAdmin.utils.JWTUtil;
import com.lin.zhiliaoAdmin.vo.ErrorCode;
import com.lin.zhiliaoAdmin.vo.PageResult;
import com.lin.zhiliaoAdmin.vo.Result;
import com.lin.zhiliaoAdmin.vo.params.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CommentMapper commentMapper;
    @Value("${redis.token.max}")
    private Integer tokenMax;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 管理员列表
     */
    public Result listUser(PageParam pageParam) {
        Page<Admin> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(pageParam.getQueryString()), Admin::getAccount, pageParam.getQueryString());
        Page<Admin> adminPage = this.userMapper.selectPage(page, queryWrapper);
        PageResult<Admin> pageResult = new PageResult<>();
        pageResult.setList(adminPage.getRecords());
        pageResult.setTotal(adminPage.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 添加管理员同时添加相应的用户
     */
    public Result add(Admin admin) {
        String account = admin.getAccount();
        String password = admin.getPassword();
        String email = admin.getEmail();
        Result result = checkInfo(account, password, email);
        if (!(result.isSuccess())) {
            return result;
        }
        admin = new Admin();
        SysUser sysUser = new SysUser();
        // 将密码和加密盐值作为一个整体进行加密处理
        password = new BCryptPasswordEncoder().encode(password);
        // 添加管理员信息
        admin.setAccount(account);
        admin.setPassword(password);
        admin.setCreateDate(new Date());
        admin.setCreator(user());
        admin.setEmail(email);
        this.userMapper.insert(admin);
        // 同时添加到前台用户中 保证id相同 后台添加的用户均设为管理员
        admin = selectAdminByAccount(account);
        sysUser.setId(admin.getId());
        sysUser.setAccount(account);
        sysUser.setNickname(account);
        sysUser.setPassword(password);
        sysUser.setEmail(email);
        sysUser.setAdmin("1");
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setDeleted("0");
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setStatus("1");
        // 保存注册信息
        this.sysUserMapper.insert(sysUser);
        // token
        String tokenSysUser = JWTUtil.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_" + tokenSysUser, JSON.toJSONString(sysUser), tokenMax, TimeUnit.DAYS);
        return Result.success(tokenSysUser);
    }

    /**
     * 修改用户信息
     */
    public Result update(Admin admin) {
        // 首先对其输入的信息进行校验
        Result result = checkInfo(admin.getId(), admin.getAccount(), admin.getPassword(), admin.getEmail());
        if (!(result.isSuccess())) {
            return result;
        }
        String password = selectAdminByAccount(admin.getAccount()).getPassword();
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        Admin user = new Admin();
        if (!(admin.getPassword().equals("******"))) {
            password = new BCryptPasswordEncoder().encode(admin.getPassword());
            user.setPassword(password);
        }
        // 更新用户名，邮箱
        user.setAccount(admin.getAccount());
        user.setEmail(admin.getEmail());
        queryWrapper.eq(Admin::getId, admin.getId());
        this.userMapper.update(user, queryWrapper);
        // 同时修改相应的前台用户信息
        updateSysUserById(admin.getId(), admin.getAccount(), password, admin.getEmail());
        return Result.success(null);
    }

    /**
     * 修改用户信息校验
     */
    public Result checkInfo(Long id, String account, String password, String email) {
        // 校验必填项
        if (account == null || password == null || email == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }

        // 如果用户名发生改变再对其校验
        Admin admin = this.userMapper.selectById(id);
        if (!(admin.getAccount().equals(account))) {
            Result result = checkAccount(account);
            if (!(result.isSuccess())) {
                return result;
            }
        }
        // 如果邮箱发生改变再对其校验
        if (!(admin.getEmail().equals(email))) {
            Result resultEmail = checkEmail(email);
            if (!(resultEmail.isSuccess())) {
                return resultEmail;
            }
        }
        return Result.success(null);
    }

    public Result checkSysUserInfo(Long id, String account, String password, String nickName, String email) {
        Result result ;
        // 校验必填项
        if (account == null || password == null || nickName == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        // 如果用户名发生改变再对其校验
        SysUser sysUser = this.sysUserMapper.selectById(id);
        if (!(sysUser.getAccount().equals(account))) {
            result = checkSysUserAccount(account);
            if (!(result.isSuccess())) {
                return result;
            }
        }
        // 如果昵称发生改变再对其校验
        if (!(sysUser.getNickname().equals(nickName))) {
            result = checkNickName(nickName);
            if (!(result.isSuccess())) {
                return result;
            }
        }
        if (!(sysUser.getEmail().equals(email))) {
            result = checkSysUserEmail(email);
            if (!(result.isSuccess())) {
                return result;
            }
        }
        return Result.success(null);
    }

    /**
     * 添加用户信息校验
     */
    public Result checkInfo(String account, String password, String email) {
        if (account == null || password == null || email == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        Result result = checkAccountAndEmail(account, email);
        if (result.isSuccess()) {
            return result;
        }
        return Result.success(null);
    }

    public Result checkInfo(String account, String nickName, String password, String email) {
        if (account == null || password == null || email == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        // 校验用户名和邮箱
        Result result = checkSysUserAccountAndNickNameAndEmail(account, nickName, email);
        if (!(result.isSuccess())) {
            return result;
        }
        return Result.success(null);
    }

    /**
     * 校验昵称
     * */
    public Result checkNickName(String nickname) {
        SysUser sysUserNickName = selectSysUserByNickName(nickname);
        if (sysUserNickName != null) {
            if (!(sysUserNickName.getAdmin().equals("1"))) {
                return Result.fail(ErrorCode.NICKNAME_IS_EXIST.getCode(), ErrorCode.NICKNAME_IS_EXIST.getMsg());
            }
        }
        return Result.success(null);
    }

    /**
     * 返回校验用户名和邮箱的结果
     * */
    public Result checkAccountAndEmail(String account, String email) {
        // 校验用户名
        Result resultAccount = checkAccount(account);
        if (!(resultAccount.isSuccess())) {
            return resultAccount;
        }
        // 校验邮箱
        Result resultEmail = checkEmail(email);
        if (!(resultEmail.isSuccess())) {
            return resultEmail;
        }
        return Result.success(null);
    }

    /**
     * 返回校验普通用户用户名、昵称和邮箱的结果
     * */
    public Result checkSysUserAccountAndNickNameAndEmail(String account, String nickName, String email) {
        // 校验用户名
        Result resultAccount = checkSysUserAccount(account);
        if (!(resultAccount.isSuccess())) {
            return resultAccount;
        }
        // 校验昵称
        Result resultNickName = checkNickName(nickName);
        if (!(resultNickName.isSuccess())) {
            return resultNickName;
        }
        // 校验邮箱
        Result resultEmail = checkSysUserEmail(email);
        if (!(resultEmail.isSuccess())) {
            return resultEmail;
        }
        return Result.success(null);
    }

    /**
     * 校验用户名
     */
    public Result checkAccount(String account) {
        SysUser sysUserAccount = selectSysUserByAccount(account);
        SysUser sysUserNickName = selectSysUserByNickName(account);
        Admin admin = selectAdminByAccount(account);
        // 如果用户名已存在返回错误信息

        if (admin != null) {
            return Result.fail(ErrorCode.ACCOUNT_IS_EXIST.getCode(), ErrorCode.ACCOUNT_IS_EXIST.getMsg());
        }
        if (sysUserAccount != null) {
            if (sysUserAccount.getAdmin().equals("1")) {
                return Result.fail(ErrorCode.ACCOUNT_IS_EXIST.getCode(), ErrorCode.ACCOUNT_IS_EXIST.getMsg());
            }
        }
        if (sysUserNickName != null) {
            if (sysUserNickName.getAdmin().equals("1")) {
                return Result.fail(ErrorCode.ACCOUNT_IS_EXIST.getCode(), ErrorCode.ACCOUNT_IS_EXIST.getMsg());
            }
        }
        return Result.success(null);
    }

    /**
     * 校验用户名
     */
    public Result checkSysUserAccount(String account) {
        SysUser sysUserAccount = selectSysUserByAccount(account);
        // 如果用户名已存在返回错误信息
        if (sysUserAccount != null) {
            if (!("1".equals(sysUserAccount.getAdmin()))) {
                return Result.fail(ErrorCode.ACCOUNT_IS_EXIST.getCode(), ErrorCode.ACCOUNT_IS_EXIST.getMsg());
            }
        }
        return Result.success(null);
    }


    /**
     * 校验邮箱
     */
    public Result checkEmail(String email) {
        Admin adminEmail = selectAdminByEmail(email);
        SysUser sysUserEmail = selectSysUserByEmail(email);
        // 如果该邮箱已注册
        if (adminEmail != null || sysUserEmail != null) {
            return Result.fail(ErrorCode.EMAIL_EXIST.getCode(), ErrorCode.EMAIL_EXIST.getMsg());
        }
        return Result.success(null);
    }

    /**
     * 校验邮箱
     */
    public Result checkSysUserEmail(String email) {
        SysUser sysUserEmail = selectSysUserByEmail(email);
        // 如果该邮箱已注册
        if (sysUserEmail != null) {
            if (!(sysUserEmail.getAdmin().equals("1"))) {
                return Result.fail(ErrorCode.EMAIL_EXIST.getCode(), ErrorCode.EMAIL_EXIST.getMsg());
            }
        }
        return Result.success(null);
    }

    /**
     * 根据id修改用户信息
     */
    public void updateSysUserById(Long id, String account, String password, String email) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getId, id);
        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        sysUser.setPassword(password);
        sysUser.setEmail(email);
        sysUser.setNickname(account);
        this.sysUserMapper.update(sysUser, sysUserLambdaQueryWrapper);
    }

    /**
     * 根据用户名查询管理员信息
     */
    public Admin selectAdminByAccount(String account) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAccount, account);
        return this.userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邮箱查询管理员信息
     */
    public Admin selectAdminByEmail(String email) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getEmail, email);
        return this.userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户名查询用户信息
     */
    public SysUser selectSysUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据昵称查询用户信息
     */
    public SysUser selectSysUserByNickName(String nickName) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getNickname, nickName);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邮箱查询用户信息
     */
    public SysUser selectSysUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 删除用户
     */
    public Result delete(Long id) {
        this.userMapper.deleteById(id);
        this.sysUserMapper.deleteById(id);
        // 同时将与该用户所有与之相关联的全部删除
        List<Article> articleList = selectArticleByAuthorId(id);
        deleteArticleCommentArticleBody(articleList);
        return Result.success(null);
    }

    /**
     * 根据作者id查询其所有的文章
     */
    public List<Article> selectArticleByAuthorId(Long id) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getAuthorId, id);
        return this.articleMapper.selectList(queryWrapper);
    }

    /**
     * 根据文章id查询其所有的评论
     */
    public List<Comment> selectCommentsByArticleId(Long articleId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId);
        return this.commentMapper.selectList(queryWrapper);
    }

    /**
     * 根据文章id查询其文章内容
     */
    public ArticleBody selectArticleBodyByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getArticleId, articleId);
        return this.articleBodyMapper.selectOne(queryWrapper);
    }

    /**
     * 根据管理员id查询其权限
     */
    public List<Permission> findPermissionsByAdminId(Long adminId) {
        return permissionMapper.findPermissionsByAdminId(adminId);
    }

    /**
     * 获取当前登录用户信息
     */
    public String user() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof Principal) {
            return ((Principal) principal).getName();
        }
        return String.valueOf(principal);
    }

    /**
     * 删除文章与之关联的评论及其文章内容
     */
    public void deleteArticleCommentArticleBody(List<Article> articleList) {
        if (!(articleList.isEmpty())) {
            for (Article article : articleList) {
                deleteArticleCommentArticleBody(article);
                // 将该文章相关联的标签也一并删除
                List<ArticleTag> articleTagList = selectArticleTagByArticleId(article.getId());
                if (!(articleTagList.isEmpty())) {
                    for (ArticleTag articleTag : articleTagList) {
                        this.articleTagMapper.deleteById(articleTag.getId());
                    }
                }
            }
        }
    }

    /**
     * 根据文章id查询与之相关联的标签
     */
    public List<ArticleTag> selectArticleTagByArticleId(Long id) {
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        return this.articleTagMapper.selectList(queryWrapper);
    }

    /**
     * 删除文章与之关联的评论及其文章内容
     */
    public void deleteArticleCommentArticleBody(Article article) {
        if (article != null) {
            deleteCommentArticleBody(article);
        }
    }

    /**
     * 删除文章内容及其评论
     * */
    public void deleteCommentArticleBody(Article article) {
        this.articleMapper.deleteById(article.getId());
        // 删除文章的同时将其下面的所有评论一并删除
        List<Comment> commentList = selectCommentsByArticleId(article.getId());
        if (!(commentList.isEmpty())) {
            for (Comment comment : commentList) {
                this.commentMapper.deleteById(comment.getId());
            }
        }
        // 文章主题内容一并删除
        ArticleBody articleBody = selectArticleBodyByArticleId(article.getId());
        if (articleBody != null) {
            this.articleBodyMapper.deleteById(articleBody.getId());
        }
    }
}
