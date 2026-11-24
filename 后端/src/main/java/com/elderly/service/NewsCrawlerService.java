package com.elderly.service;

import com.elderly.entity.NewsArticle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class NewsCrawlerService {

    @Autowired
    private NewsArticleService newsArticleService;

    // 政府网站
    private static final String GOV_POLICY_URL = "http://www.gov.cn/zhengce/index.htm";
    private static final String GOV_NEWS_URL = "http://www.gov.cn/xinwen/index.htm";
    
    // 人民网
    private static final String PEOPLE_URL = "http://health.people.com.cn/";
    
    // 新华网
    private static final String XINHUA_URL = "http://www.xinhuanet.com/health/";
    
    // 央视网
    private static final String CCTV_URL = "https://health.cctv.com/";
    
    // 只抓取最近30天内的新闻
    private static final int MAX_DAYS_OLD = 30;

    public int crawlGovPolicy() {
        int count = 0;
        try {
            log.info("开始抓取政府养老政策...");
            
            Document doc = Jsoup.connect(GOV_POLICY_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsElements = doc.select("a[href*='content']");
            
            for (Element link : newsElements) {
                String title = link.text().trim();
                String href = link.attr("abs:href");
                
                // 检查是否是最近的新闻
                if (!isRecentNews(href)) {
                    continue;
                }
                
                if (title.length() > 5 && isElderlyRelated(title)) {
                    if (saveNewsArticle(title, href, 1, "中国政府网")) {
                        count++;
                    }
                }
            }
            
            log.info("政策抓取完成，新增 {} 条", count);
        } catch (Exception e) {
            log.error("抓取政府政策失败: {}", e.getMessage());
        }
        return count;
    }

    public int crawlGovNews() {
        int count = 0;
        try {
            log.info("开始抓取政府养老新闻...");
            
            Document doc = Jsoup.connect(GOV_NEWS_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsElements = doc.select("a[href*='content']");
            
            for (Element link : newsElements) {
                String title = link.text().trim();
                String href = link.attr("abs:href");
                
                // 检查是否是最近的新闻
                if (!isRecentNews(href)) {
                    continue;
                }
                
                if (title.length() > 5 && isElderlyRelated(title)) {
                    if (saveNewsArticle(title, href, 2, "中国政府网")) {
                        count++;
                    }
                }
            }
            
            log.info("新闻抓取完成，新增 {} 条", count);
        } catch (Exception e) {
            log.error("抓取政府新闻失败: {}", e.getMessage());
        }
        return count;
    }
    
    /**
     * 检查新闻是否是最近30天内的
     * 通过URL或访问详情页获取发布时间
     */
    private boolean isRecentNews(String url) {
        try {
            // 尝试从URL中提取日期
            // 格式1：/content/202503/10/content_123456.htm
            // 格式2：/2026-03/11/content_123456.htm
            // 格式3：/home/2023-03/29/content_5748953.htm
            
            Pattern pattern1 = Pattern.compile("/(\\d{4})(\\d{2})/(\\d{2})/");
            Matcher matcher1 = pattern1.matcher(url);
            
            if (matcher1.find()) {
                int year = Integer.parseInt(matcher1.group(1));
                int month = Integer.parseInt(matcher1.group(2));
                int day = Integer.parseInt(matcher1.group(3));
                
                LocalDate newsDate = LocalDate.of(year, month, day);
                long daysBetween = ChronoUnit.DAYS.between(newsDate, LocalDate.now());
                
                return daysBetween <= MAX_DAYS_OLD;
            }
            
            // 尝试格式：/2023-03/29/ 或 /home/2023-03/29/
            Pattern pattern2 = Pattern.compile("/(\\d{4})-(\\d{2})/(\\d{2})/");
            Matcher matcher2 = pattern2.matcher(url);
            
            if (matcher2.find()) {
                int year = Integer.parseInt(matcher2.group(1));
                int month = Integer.parseInt(matcher2.group(2));
                int day = Integer.parseInt(matcher2.group(3));
                
                LocalDate newsDate = LocalDate.of(year, month, day);
                long daysBetween = ChronoUnit.DAYS.between(newsDate, LocalDate.now());
                
                return daysBetween <= MAX_DAYS_OLD;
            }
            
            // 如果无法从URL解析，尝试访问详情页获取发布时间
            return checkDetailPageDate(url);
            
        } catch (Exception e) {
            log.warn("无法判断新闻日期，默认跳过: {}", url);
            return false;
        }
    }
    
    /**
     * 访问详情页获取发布时间
     */
    private boolean checkDetailPageDate(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();
            
            // 查找发布时间（中国政府网通常在 .pages-date 或类似选择器中）
            Elements dateElements = doc.select(".pages-date, .time, .publish-time, [class*='date'], [class*='time']");
            
            for (Element dateElem : dateElements) {
                String dateText = dateElem.text().trim();
                
                // 尝试解析日期（格式：2025-03-11 或 2025年03月11日）
                LocalDate publishDate = parseDate(dateText);
                
                if (publishDate != null) {
                    long daysBetween = ChronoUnit.DAYS.between(publishDate, LocalDate.now());
                    return daysBetween <= MAX_DAYS_OLD;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.warn("访问详情页失败: {}", url);
            return false;
        }
    }
    
    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateText) {
        try {
            // 尝试格式：2025-03-11
            if (dateText.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
                Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
                Matcher matcher = pattern.matcher(dateText);
                if (matcher.find()) {
                    return LocalDate.parse(matcher.group(1), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            }
            
            // 尝试格式：2025年03月11日
            if (dateText.matches(".*\\d{4}年\\d{2}月\\d{2}日.*")) {
                Pattern pattern = Pattern.compile("(\\d{4})年(\\d{2})月(\\d{2})日");
                Matcher matcher = pattern.matcher(dateText);
                if (matcher.find()) {
                    int year = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    int day = Integer.parseInt(matcher.group(3));
                    return LocalDate.of(year, month, day);
                }
            }
        } catch (Exception e) {
            log.debug("日期解析失败: {}", dateText);
        }
        return null;
    }

    private boolean isElderlyRelated(String title) {
        String[] keywords = {
            // 原有关键词
            "养老", "老年", "老人", "社保", "退休", "养老金", 
            "医疗", "护理", "居家", "社区服务", "康养", "医养",
            // 新增关键词
            "养老保险", "医疗保险", "医保", "社保", "养老金调整",
            "敬老院", "福利院", "疗养院", "托老所", "日间照料",
            "健康", "养生", "保健", "康复", "中医", "体检",
            "慢病", "高血压", "糖尿病", "心脑血管", "阿尔茨海默",
            "适老化", "适老改造", "无障碍", "扶手", "防滑",
            "智慧养老", "智能穿戴", "紧急呼叫", "定位", "防走失",
            "老年大学", "老年教育", "终身学习", "兴趣班",
            "老年旅游", "夕阳红", "候鸟养老", "旅居养老",
            "老年餐桌", "助餐", "送餐", "营养配餐",
            "法律援助", "老年维权", "防诈骗", "电信诈骗",
            "独生子女", "失独", "空巢老人", "留守老人",
            "银发经济", "银发产业", "老年消费", "老年用品",
            "重阳节", "敬老月", "老年节", "敬老爱老",
            "高龄津贴", "长寿金", "免费体检", "免费乘车"
        };
        for (String keyword : keywords) {
            if (title.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static final int MAX_NEWS_COUNT = 30;
    
    private boolean saveNewsArticle(String title, String url, int category, String source) {
        try {
            List<NewsArticle> existing = newsArticleService.lambdaQuery()
                    .eq(NewsArticle::getTitle, title)
                    .list();
            
            if (!existing.isEmpty()) {
                return false;
            }
            
            // 检查新闻总数是否超过上限
            long totalCount = newsArticleService.lambdaQuery()
                    .eq(NewsArticle::getDeleted, 0)
                    .count();
            
            if (totalCount >= MAX_NEWS_COUNT) {
                // 删除最旧的新闻（非置顶）
                List<NewsArticle> oldNews = newsArticleService.lambdaQuery()
                        .eq(NewsArticle::getDeleted, 0)
                        .eq(NewsArticle::getIsTop, 0)
                        .orderByAsc(NewsArticle::getCreateTime)
                        .last("LIMIT 1")
                        .list();
                
                if (!oldNews.isEmpty()) {
                    NewsArticle toDelete = oldNews.get(0);
                    toDelete.setDeleted(1);
                    newsArticleService.updateById(toDelete);
                    log.info("新闻数量达到上限，删除最旧新闻: {}", toDelete.getTitle());
                } else {
                    // 如果没有非置顶新闻可删除，跳过保存
                    log.warn("新闻数量达到上限且全部置顶，跳过保存: {}", title);
                    return false;
                }
            }
            
            NewsArticle article = new NewsArticle();
            article.setTitle(title);
            article.setContent("来源: " + source + "\n原文链接: " + url);
            article.setSummary(title.length() > 50 ? title.substring(0, 50) + "..." : title);
            article.setCategory(category);
            article.setViewCount(0);
            article.setIsTop(0);
            article.setStatus(1);
            article.setAuthor(source);
            
            newsArticleService.save(article);
            log.info("保存资讯成功: {}", title);
            return true;
        } catch (Exception e) {
            log.error("保存资讯失败: {}", e.getMessage());
            return false;
        }
    }

    public int crawlAll() {
        int total = 0;
        total += crawlGovPolicy();
        total += crawlGovNews();
        total += crawlPeopleHealth();
        total += crawlXinhuaHealth();
        total += crawlCCTVHealth();
        return total;
    }
    
    /**
     * 抓取人民网健康频道
     */
    public int crawlPeopleHealth() {
        int count = 0;
        try {
            log.info("开始抓取人民网健康频道...");
            
            Document doc = Jsoup.connect(PEOPLE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 人民网健康频道的新闻链接
            Elements newsElements = doc.select("a[href]");
            
            for (Element link : newsElements) {
                String title = link.text().trim();
                String href = link.attr("abs:href");
                
                // 过滤标题长度和关键词
                if (title.length() < 5 || title.length() > 100) {
                    continue;
                }
                
                if (!isElderlyRelated(title)) {
                    continue;
                }
                
                // 检查是否是最近的新闻
                if (!isRecentNews(href)) {
                    continue;
                }
                
                if (saveNewsArticle(title, href, 2, "人民网健康")) {
                    count++;
                }
            }
            
            log.info("人民网健康频道抓取完成，新增 {} 条", count);
        } catch (Exception e) {
            log.error("抓取人民网健康频道失败: {}", e.getMessage());
        }
        return count;
    }
    
    /**
     * 抓取新华网健康频道
     */
    public int crawlXinhuaHealth() {
        int count = 0;
        try {
            log.info("开始抓取新华网健康频道...");
            
            Document doc = Jsoup.connect(XINHUA_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsElements = doc.select("a[href]");
            
            for (Element link : newsElements) {
                String title = link.text().trim();
                String href = link.attr("abs:href");
                
                if (title.length() < 5 || title.length() > 100) {
                    continue;
                }
                
                if (!isElderlyRelated(title)) {
                    continue;
                }
                
                if (!isRecentNews(href)) {
                    continue;
                }
                
                if (saveNewsArticle(title, href, 2, "新华网健康")) {
                    count++;
                }
            }
            
            log.info("新华网健康频道抓取完成，新增 {} 条", count);
        } catch (Exception e) {
            log.error("抓取新华网健康频道失败: {}", e.getMessage());
        }
        return count;
    }
    
    /**
     * 抓取央视网健康频道
     */
    public int crawlCCTVHealth() {
        int count = 0;
        try {
            log.info("开始抓取央视网健康频道...");
            
            Document doc = Jsoup.connect(CCTV_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements newsElements = doc.select("a[href]");
            
            for (Element link : newsElements) {
                String title = link.text().trim();
                String href = link.attr("abs:href");
                
                if (title.length() < 5 || title.length() > 100) {
                    continue;
                }
                
                if (!isElderlyRelated(title)) {
                    continue;
                }
                
                if (!isRecentNews(href)) {
                    continue;
                }
                
                if (saveNewsArticle(title, href, 2, "央视网健康")) {
                    count++;
                }
            }
            
            log.info("央视网健康频道抓取完成，新增 {} 条", count);
        } catch (Exception e) {
            log.error("抓取央视网健康频道失败: {}", e.getMessage());
        }
        return count;
    }
}
