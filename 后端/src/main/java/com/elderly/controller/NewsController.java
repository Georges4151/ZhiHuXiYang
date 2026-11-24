package com.elderly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.NewsArticle;
import com.elderly.service.NewsArticleService;
import com.elderly.service.NewsCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    
    @Autowired
    private NewsArticleService newsArticleService;
    
    @Autowired
    private NewsCrawlerService newsCrawlerService;
    
    @GetMapping("/hot")
    public Result<List<NewsArticle>> getHotNews(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<NewsArticle> list = newsArticleService.getHotNews(limit);
        return Result.success(list);
    }
    
    @GetMapping("/page")
    public Result<Page<NewsArticle>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<NewsArticle> page = newsArticleService.getPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/category/{categoryId}")
    public Result<Page<NewsArticle>> getByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<NewsArticle> page = newsArticleService.getPageByCategoryId(categoryId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/detail/{id}")
    public Result<NewsArticle> getDetail(@PathVariable Long id) {
        NewsArticle article = newsArticleService.getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            newsArticleService.updateById(article);
        }
        return Result.success(article);
    }
    
    @GetMapping("/crawl")
    public Result<Map<String, Object>> crawlNews() {
        int count = newsCrawlerService.crawlAll();
        Map<String, Object> result = new HashMap<>();
        result.put("newCount", count);
        result.put("message", "抓取完成，新增 " + count + " 条资讯");
        return Result.success(result);
    }
}
