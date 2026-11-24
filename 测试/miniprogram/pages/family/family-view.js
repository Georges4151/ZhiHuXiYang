const API = require('../../utils/api');

Page({
  data: {
    bindUsers: [],
    selectedUser: null,
    healthData: [],
    serviceOrders: [],
    notifications: [],
    unreadCount: 0,
    activeTab: 'health',
    tabs: [
      { id: 'health', name: '健康数据' },
      { id: 'service', name: '服务进度' },
      { id: 'notification', name: '消息通知' }
    ]
  },

  onLoad() {
    this.loadBindUsers();
  },

  onShow() {
    if (this.data.selectedUser) {
      this.loadData();
    }
  },

  async loadBindUsers() {
    try {
      const userInfo = wx.getStorageSync('userInfo');
      if (!userInfo || !userInfo.id) {
        console.error('用户未登录');
        return;
      }
      
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: API.BASE_URL + '/family/bind-users/' + userInfo.id,
          method: 'GET',
          success: resolve,
          fail: reject
        });
      });
      
      if (res.data.code === 200) {
        const bindUsers = res.data.data || [];
        this.setData({ 
          bindUsers,
          selectedUser: bindUsers.length > 0 ? bindUsers[0] : null
        });
        
        if (bindUsers.length > 0) {
          this.loadData();
        }
      }
    } catch (error) {
      console.error('加载绑定用户失败:', error);
    }
  },

  async loadData() {
    if (!this.data.selectedUser) return;
    
    const userId = this.data.selectedUser.userId;
    const userInfo = wx.getStorageSync('userInfo');
    const familyUserId = userInfo ? userInfo.id : null;
    
    if (!familyUserId) {
      console.error('用户未登录');
      return;
    }
    
    if (this.data.activeTab === 'health') {
      this.loadHealthData(userId, familyUserId);
    } else if (this.data.activeTab === 'service') {
      this.loadServiceOrders(userId, familyUserId);
    } else if (this.data.activeTab === 'notification') {
      this.loadNotifications(familyUserId);
    }
  },

  async loadHealthData(userId, familyUserId) {
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: API.BASE_URL + '/family/health-data/' + userId,
          method: 'GET',
          data: { familyUserId, days: 7 },
          success: resolve,
          fail: reject
        });
      });
      
      if (res.data.code === 200) {
        this.setData({ healthData: res.data.data || [] });
      }
    } catch (error) {
      console.error('加载健康数据失败:', error);
    }
  },

  async loadServiceOrders(userId, familyUserId) {
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: API.BASE_URL + '/family/service-orders/' + userId,
          method: 'GET',
          data: { familyUserId },
          success: resolve,
          fail: reject
        });
      });
      
      if (res.data.code === 200) {
        this.setData({ serviceOrders: res.data.data || [] });
      }
    } catch (error) {
      console.error('加载服务订单失败:', error);
    }
  },

  async loadNotifications(familyUserId) {
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: API.BASE_URL + '/family/notifications/' + familyUserId,
          method: 'GET',
          success: resolve,
          fail: reject
        });
      });
      
      if (res.data.code === 200) {
        const notifications = res.data.data || [];
        const unreadCount = notifications.filter(n => n.isRead === 0).length;
        this.setData({ notifications, unreadCount });
      }
    } catch (error) {
      console.error('加载通知失败:', error);
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.id;
    this.setData({ activeTab: tab });
    this.loadData();
  },

  onUserSelect(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ 
      selectedUser: this.data.bindUsers[index],
      page: 1
    });
    this.loadData();
  },

  onMarkRead(e) {
    const notificationId = e.currentTarget.dataset.id;
    const userInfo = wx.getStorageSync('userInfo');
    
    wx.request({
      url: API.BASE_URL + '/family/notifications/' + notificationId + '/read',
      method: 'POST',
      success: (res) => {
        if (res.data.code === 200) {
          this.loadNotifications(userInfo.id);
        }
      }
    });
  },

  onMarkAllRead() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      console.error('用户未登录');
      return;
    }
    
    wx.request({
      url: API.BASE_URL + '/family/notifications/' + userInfo.id + '/read-all',
      method: 'POST',
      success: (res) => {
        if (res.data.code === 200) {
          this.loadNotifications(userInfo.id);
          wx.showToast({ title: '已全部标记为已读', icon: 'success' });
        }
      }
    });
  },

  onOrderDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order/detail?id=${orderId}`
    });
  },

  getStatusText(status) {
    const statusMap = {
      0: '待服务',
      1: '服务中',
      2: '已完成',
      3: '已取消'
    };
    return statusMap[status] || '未知';
  },

  getStatusColor(status) {
    const colorMap = {
      0: '#faad14',
      1: '#1890ff',
      2: '#52c41a',
      3: '#ff4d4f'
    };
    return colorMap[status] || '#999';
  }
});
