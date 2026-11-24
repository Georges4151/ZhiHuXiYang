const API = require('../../utils/api');

Page({
  data: {
    id: null,
    article: {},
    relatedNews: []
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadArticleDetail(options.id);
    }
  },

  loadArticleDetail: function(id) {
    const that = this;
    
    API.getNewsDetail(id).then(data => {
      if (data) {
        const article = {
          id: data.id,
          title: data.title,
          content: data.content || '暂无内容',
          categoryName: that.getCategoryName(data.category),
          createTime: that.formatDate(data.createTime),
          viewCount: (data.viewCount || 0) + 1
        };
        
        that.setData({
          article: article,
          relatedNews: []
        });
      }
    }).catch(() => {
      wx.showToast({
        title: '加载文章失败',
        icon: 'none'
      });
    });
  },

  getCategoryName: function(category) {
    const categoryMap = {
      1: '政策法规',
      2: '健康知识',
      3: '社区动态'
    };
    return categoryMap[category] || '其他';
  },

  formatDate: function(dateStr) {
    if (!dateStr) return '';
    return dateStr.substring(0, 10);
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.redirectTo({ url: '/pages/news/detail?id=' + id });
  },

  onShareAppMessage: function() {
    const { article } = this.data;
    return {
      title: article.title,
      path: '/pages/news/detail?id=' + article.id
    };
  }
});
