package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("news_article")
public class NewsArticle extends BaseEntity {
    
    private String title;
    
    private String content;
    
    private String summary;
    
    private Integer category;
    
    private String coverImage;
    
    private String author;
    
    private Integer viewCount;
    
    private Integer isTop;
    
    private Integer status;
}
