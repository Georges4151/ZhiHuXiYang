const API = require('../../utils/api');

Page({
  data: {
    currentTab: -1,
    messageList: [],
    unreadCount: 0,
    typeIcons: {
      0: '📢',
      1: '📋',
      2: '💊',
      3: '🎉'
    }
  },

  onLoad: function() {
    this.loadMessages();
    this.loadUnreadCount();
  },

  onShow: function() {
    this.loadMessages();
    this.loadUnreadCount();
  },

  loadMessages: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }

    const that = this;
    const type = this.data.currentTab === -1 ? '' : '?type=' + this.data.currentTab;
    
    wx.request({
      url: API.BASE_URL + '/notification/list/' + userInfo.id + type,
      method: 'GET',
      success: function(res) {
        if (res.data.code === 200) {
          that.setData({
            messageList: res.data.data || []
          });
        }
      },
      fail: function() {
        that.setData({
          messageList: []
        });
      }
    });
  },

  loadUnreadCount: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }

    const that = this;
    wx.request({
      url: API.BASE_URL + '/notification/unread/' + userInfo.id,
      method: 'GET',
      success: function(res) {
        if (res.data.code === 200) {
          that.setData({
            unreadCount: res.data.data.count || 0
          });
        }
      }
    });
  },

  switchTab: function(e) {
    const type = parseInt(e.currentTarget.dataset.type);
    this.setData({
      currentTab: type
    });
    this.loadMessages();
  },

  markAllRead: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }

    const that = this;
    wx.request({
      url: API.BASE_URL + '/notification/readAll/' + userInfo.id,
      method: 'PUT',
      success: function(res) {
        if (res.data.code === 200) {
          wx.showToast({ title: '已全部标记已读', icon: 'success' });
          that.setData({
            unreadCount: 0
          });
          that.loadMessages();
        }
      }
    });
  },

  goToDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/notification/detail?id=' + id
    });
  }
});
