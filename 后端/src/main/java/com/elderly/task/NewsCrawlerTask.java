package com.elderly.task;

import com.elderly.entity.NewsArticle;
import com.elderly.service.NewsArticleService;
import com.elderly.service.NewsCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class NewsCrawlerTask {

    @Autowired
    private NewsCrawlerService newsCrawlerService;
    
    @Autowired
    private NewsArticleService newsArticleService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void crawlNewsDaily() {
        log.info("开始执行定时新闻抓取任务...");
        int count = newsCrawlerService.crawlAll();
        log.info("定时抓取完成，共新增 {} 条资讯", count);
    }
    
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOldNewsDaily() {
        log.info("开始清理超过20天的旧新闻...");
        try {
            LocalDateTime twentyDaysAgo = LocalDateTime.now().minus(20, ChronoUnit.DAYS);
            
            List<NewsArticle> oldNews = newsArticleService.lambdaQuery()
                    .lt(NewsArticle::getCreateTime, twentyDaysAgo)
                    .eq(NewsArticle::getDeleted, 0)
                    .list();
            
            int count = 0;
            for (NewsArticle article : oldNews) {
                article.setDeleted(1);
                newsArticleService.updateById(article);
                count++;
            }
            
            log.info("清理完成，共删除 {} 条超过20天的新闻", count);
        } catch (Exception e) {
            log.error("清理旧新闻失败: {}", e.getMessage());
        }
    }
}
