package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.NewsArticle;
import com.elderly.mapper.NewsArticleMapper;
import com.elderly.service.NewsArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsArticleServiceImpl extends ServiceImpl<NewsArticleMapper, NewsArticle> implements NewsArticleService {
    
    @Override
    public List<NewsArticle> getHotNews(Integer limit) {
        return this.list(new LambdaQueryWrapper<NewsArticle>()
                .eq(NewsArticle::getStatus, 1)
                .orderByDesc(NewsArticle::getIsTop)
                .orderByDesc(NewsArticle::getViewCount)
                .last("LIMIT " + limit));
    }
    
    @Override
    public Page<NewsArticle> getPage(Integer pageNum, Integer pageSize) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<NewsArticle>()
                .eq(NewsArticle::getStatus, 1)
                .orderByDesc(NewsArticle::getIsTop)
                .orderByDesc(NewsArticle::getCreateTime));
    }
    
    @Override
    public Page<NewsArticle> getPageByCategoryId(Integer categoryId, Integer pageNum, Integer pageSize) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<NewsArticle>()
                .eq(NewsArticle::getStatus, 1)
                .eq(NewsArticle::getCategory, categoryId)
                .orderByDesc(NewsArticle::getCreateTime));
    }
}
