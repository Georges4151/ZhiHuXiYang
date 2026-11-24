const API = require('../../utils/api');

Page({
  data: {
    newsList: [],
    categoryList: [
      { id: 0, name: '全部' },
      { id: 1, name: '政策法规' },
      { id: 2, name: '健康知识' },
      { id: 3, name: '社区动态' }
    ],
    currentCategory: 0,
    loading: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 10
  },

  onLoad: function() {
    this.loadNews();
  },

  onShow: function() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 2
      });
    }
  },

  loadNews: function() {
    if (this.data.loading || !this.data.hasMore) return;
    
    this.setData({ loading: true });
    
    const { currentCategory, pageNum, pageSize } = this.data;
    
    let promise;
    if (currentCategory === 0) {
      promise = API.getNewsPage(pageNum, pageSize);
    } else {
      promise = API.getNewsByCategory(currentCategory, pageNum, pageSize);
    }
    
    promise.then(data => {
      if (data && data.records) {
        const newsList = data.records.map(item => ({
          id: item.id,
          title: item.title,
          summary: item.content ? item.content.substring(0, 80) + '...' : '',
          categoryName: this.getCategoryName(item.category),
          createTime: this.formatDate(item.createTime),
          readCount: item.viewCount || 0,
          isTop: item.isTop || 0
        }));
        
        this.setData({
          newsList: pageNum === 1 ? newsList : [...this.data.newsList, ...newsList],
          loading: false,
          pageNum: pageNum + 1,
          hasMore: data.current < data.pages
        });
      } else {
        this.setData({
          loading: false,
          hasMore: false
        });
      }
    }).catch(err => {
      console.error('加载新闻失败', err);
      this.setData({
        loading: false,
        hasMore: false
      });
      wx.showToast({ title: '加载失败', icon: 'none' });
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

  onCategoryChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ 
      currentCategory: id,
      newsList: [],
      pageNum: 1,
      hasMore: true
    });
    this.loadNews();
  },

  onPullDownRefresh: function() {
    this.setData({
      newsList: [],
      pageNum: 1,
      hasMore: true
    });
    this.loadNews();
    wx.stopPullDownRefresh();
  },

  onReachBottom: function() {
    this.loadNews();
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/news/detail?id=' + id });
  }
});
