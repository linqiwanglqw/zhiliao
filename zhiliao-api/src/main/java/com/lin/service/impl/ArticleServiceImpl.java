package com.lin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.dao.dos.Archives;
import com.lin.dao.mapper.OpenaiAnswersMapper;
import com.lin.dao.pojo.*;
import com.lin.service.*;
import com.lin.utils.SensitiveFilter;
import com.lin.utils.UserThreadLocal;
import com.lin.utils.text.StringUtils;
import com.lin.vo.*;
import com.lin.vo.params.ArticleParam;
import com.lin.vo.params.PageParams;
import com.lin.dao.mapper.ArticleBodyMapper;
import com.lin.dao.mapper.ArticleMapper;
import com.lin.dao.mapper.ArticleTagMapper;
import com.lin.vo.params.PageSearchParams;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 归档
     *
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth(),
                null);
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records, true, true));
    }

    /**
     * 搜索
     * @param pageSearchParams
     * @return
     */
    @Override
    public Result listSearchArticle(PageSearchParams pageSearchParams) {
        if ("".equals(pageSearchParams.getKeyWord())) {
            PageParams pageParams = new PageParams();
            pageParams.setPage(pageSearchParams.getPage());
            pageParams.setPageSize(pageSearchParams.getPageSize());
            pageParams.setCategoryId(pageSearchParams.getCategoryId());
            pageParams.setTagId(pageSearchParams.getTagId());
            pageParams.setYear(pageSearchParams.getYear());
            pageParams.setMonth(pageParams.getMonth());
            return listArticle(pageParams);
        } else {
            Page<Article> page = new Page<>(pageSearchParams.getPage(), pageSearchParams.getPageSize());
            IPage<Article> articleIPage = articleMapper.listArticle(
                    page,
                    pageSearchParams.getCategoryId(),
                    pageSearchParams.getTagId(),
                    pageSearchParams.getYear(),
                    pageSearchParams.getMonth(),
                    pageSearchParams.getKeyWord()
            );
            List<Article> records = articleIPage.getRecords();
            return Result.success(copyList(records, true, true));
        }
    }


    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    /**
     * 最新文章
     *
     * @param limit
     * @return
     */
    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,title from article order by create_date desc desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }


    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1. 根据id查询 文章信息
         * 2. 根据bodyId和categoryid 去做关联查询
         */
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleId);
        //状态为可用
        queryWrapper.eq(Article::getAvailable,1);
        Article article = this.articleMapper.selectOne(queryWrapper);
        ArticleVo articleVo = copy(article, true, true, true, true, true);
        //查看完文章了，新增阅读数
        //查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他的读操作，性能就会比较低
        // 更新 增加了此次接口的 耗时 如果一旦更新出问题，不能影响 查看文章的操作
        //线程池  可以把更新操作 扔到线程池中去执行，和主线程就不相关了
        threadService.updateArticleViewCount(articleMapper, article);


        //查找ai回答
        LambdaQueryWrapper<OpenaiAnswers> answersWrapper = new LambdaQueryWrapper<>();
        answersWrapper.eq(OpenaiAnswers::getArticleId,articleId);
        OpenaiAnswers openaiAnswers = openaiAnswersMapper.selectOne(answersWrapper);
        if(openaiAnswers!=null){
            articleVo.setAnswer(openaiAnswers.getAnswer());
        }

        return Result.success(articleVo);
    }

    //敏感词过滤器
    @Autowired
    SensitiveFilter sensitiveFilter;


    @Transactional
    @Override
    public Result publish(ArticleParam articleParam) throws IOException {
        //此接口 要加入到登录拦截当中
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1. 发布文章 目的 构建Article对象
         * 2. 作者id  当前的登录用户
         * 3. 标签  要将标签加入到 关联列表当中
         * 4. body 内容存储 article bodyId
         */
        Article article = new Article();
        boolean isEdit = false;
        if (articleParam.getId() != null) {
            //已存在的删除标签
            HashMap<String, Object> map = new HashMap<>();
            map.put("article_id", articleParam.getId());
            //删除标签
            articleTagMapper.deleteByMap(map);
            //删除baby内容
            articleBodyMapper.deleteByMap(map);
            //更新其他数据
            article = new Article();
            article.setId(articleParam.getId());
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
            articleMapper.updateById(article);
            isEdit = true;
        } else {
            article = new Article();
            article.setAuthorId(sysUser.getId());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCommentCounts(0);
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
            article.setLikeUserCount(0L);
            //插入之后 会生成一个文章id
            this.articleMapper.insert(article);
        }

        //tag
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(sensitiveFilter.filter(articleParam.getBody().getContent()));
        articleBody.setContentHtml(sensitiveFilter.filter(articleParam.getBody().getContentHtml()));
        //先生成boby表的id，然后赋给article表的boby_id
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String, String> map = new HashMap<>();
        map.put("id", article.getId().toString());

        if(isEdit){
            //当前文章更新了
            ArticleMessage articleMessage = new ArticleMessage();
            articleMessage.setArticleId(article.getId());
            rocketMQTemplate.convertAndSend("api-update-article:SEND_ARTICLE_MSG", articleMessage);
        }

        //保存ai回答
        openAIService.generateAnswers(articleParam.getBody().getContent(),article.getId(),isEdit);

        return Result.success(map);
    }

    @Autowired
    OpenAIService openAIService;

    @Resource
    OpenaiAnswersMapper openaiAnswersMapper;


    @Override
    public Result delAticleById(Long articleId) {
        boolean b = articleMapper.updateAvailableById(articleId);
        if (b) {
            ArticleMessage articleMessage = new ArticleMessage();
            articleMessage.setArticleId(articleId);
//            rocketMQTemplate.convertAndSend("api-update-article", articleMessage);
            return Result.success("删除成功");
        } else {
            return Result.fail(30004, "文章删除或文章不存在");
        }
    }

    @Override
    public Result searchArticle(String search) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.eq(Article::getAvailable,1);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.like(Article::getTitle, search);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    /**
     * 将需要的内容封装到vo中，然后放入list中
     *
     * @param records
     * @param isTag
     * @param isAuthor
     * @return
     */
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, false, false, false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory, false));
        }
        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;


    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory, boolean isAuthorId) {
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的接口 都需要标签 ，作者信息
        if (isTag) {
            Long articleId = article.getId();
            //查询标签
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            //查作者
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            //查作者
            articleVo.setAuthorId(String.valueOf(authorId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

}
