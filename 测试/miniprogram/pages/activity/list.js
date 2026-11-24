const API = require('../../utils/api');

Page({
  data: {
    activityList: [],
    filteredActivities: [],
    loading: true,
    currentCategory: 0,
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '健康讲座' },
      { id: 2, name: '文娱活动' },
      { id: 3, name: '志愿服务' },
      { id: 4, name: '兴趣社团' }
    ]
  },

  onLoad: function() {
    this.loadActivities();
  },

  onShow: function() {
    this.loadActivities();
  },

  loadActivities: function() {
    this.setData({ loading: true });
    
    // 调用后端API获取活动列表
    wx.request({
      url: API.ACTIVITY_LIST,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          const activities = res.data.data || [];
          // 转换数据格式以适配前端显示
          const formattedActivities = activities.map(item => ({
            id: item.id,
            title: item.title,
            description: item.description,
            coverImage: item.coverImage,
            category: item.category,
            categoryName: this.getCategoryName(item.category),
            activityTime: this.formatActivityTime(item.startTime),
            location: item.location,
            maxParticipants: item.maxParticipants,
            currentParticipants: item.currentParticipants || 0,
            status: 1,
            likes: item.likes || 0,
            comments: 0
          }));
          
          this.setData({
            activityList: formattedActivities,
            filteredActivities: formattedActivities,
            loading: false
          });
        } else {
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          });
          this.setData({ loading: false });
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
        this.setData({ loading: false });
      }
    });
  },

  getCategoryName: function(category) {
    const names = {
      1: '健康讲座',
      2: '文娱活动',
      3: '志愿服务',
      4: '兴趣社团'
    };
    return names[category] || '其他';
  },

  onCategoryChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ currentCategory: id });
    
    if (id === 0) {
      this.setData({ filteredActivities: this.data.activityList });
    } else {
      const filtered = this.data.activityList.filter(item => item.category === id);
      this.setData({ filteredActivities: filtered });
    }
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/activity/detail?id=' + id });
  },

  formatActivityTime: function(timeStr) {
    if (!timeStr) return '';
    // 处理 yyyy-MM-dd HH:mm:ss 格式
    const date = new Date(timeStr.replace(/-/g, '/'));
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${month}月${day}日 ${hour}:${minute}`;
  },

  getCategoryIcon: function(category) {
    const iconMap = {
      1: '🏥',
      2: '🎭',
      3: '❤️',
      4: '🎨'
    };
    return iconMap[category] || '📅';
  },

  onPullDownRefresh: function() {
    this.loadActivities();
    wx.stopPullDownRefresh();
  },

  onShareAppMessage: function() {
    return {
      title: '社区活动 - 智慧养老',
      path: '/pages/activity/list'
    };
  }
});
