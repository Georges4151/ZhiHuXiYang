package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.NewsArticle;
import com.elderly.mapper.NewsArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/news")
public class AdminNewsController {

    @Autowired
    private NewsArticleMapper newsArticleMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer status) {
        
        Page<NewsArticle> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(NewsArticle::getTitle, title);
        }
        if (category != null) {
            wrapper.eq(NewsArticle::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(NewsArticle::getStatus, status);
        }
        wrapper.eq(NewsArticle::getDeleted, 0);
        wrapper.orderByDesc(NewsArticle::getCreateTime);
        
        Page<NewsArticle> result = newsArticleMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<NewsArticle> getNewsDetail(@PathVariable Long id) {
        NewsArticle article = newsArticleMapper.selectById(id);
        if (article == null) {
            return Result.error("资讯不存在");
        }
        return Result.success(article);
    }

    @PostMapping
    public Result<Boolean> addNews(@RequestBody NewsArticle article) {
        article.setViewCount(0);
        int rows = newsArticleMapper.insert(article);
        return rows > 0 ? Result.success(true) : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateNews(@PathVariable Long id, @RequestBody NewsArticle article) {
        article.setId(id);
        int rows = newsArticleMapper.updateById(article);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteNews(@PathVariable Long id) {
        LambdaUpdateWrapper<NewsArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(NewsArticle::getId, id);
        wrapper.set(NewsArticle::getDeleted, 1);
        int rows = newsArticleMapper.update(null, wrapper);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
