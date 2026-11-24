package com.elderly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.NewsArticle;

import java.util.List;

public interface NewsArticleService extends IService<NewsArticle> {
    
    List<NewsArticle> getHotNews(Integer limit);
    
    Page<NewsArticle> getPage(Integer pageNum, Integer pageSize);
    
    Page<NewsArticle> getPageByCategoryId(Integer categoryId, Integer pageNum, Integer pageSize);
}
