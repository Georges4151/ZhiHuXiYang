const API = require('../../utils/api');

Page({
  data: {
    id: null,
    notification: {},
    typeIcons: {
      0: '📢',
      1: '📋',
      2: '💊',
      3: '🎉'
    },
    typeNames: {
      0: '系统通知',
      1: '服务通知',
      2: '健康提醒',
      3: '活动通知'
    }
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadDetail(options.id);
    }
  },

  loadDetail: function(id) {
    const that = this;
    wx.request({
      url: API.BASE_URL + '/notification/detail/' + id,
      method: 'GET',
      success: function(res) {
        if (res.data.code === 200) {
          that.setData({
            notification: res.data.data || {}
          });
        }
      }
    });
  },

  viewOrder: function() {
    const extraData = this.data.notification.extraData;
    if (extraData) {
      try {
        const data = JSON.parse(extraData);
        if (data.orderId) {
          wx.navigateTo({
            url: '/pages/order/list'
          });
        }
      } catch (e) {
        wx.showToast({ title: '数据解析失败', icon: 'none' });
      }
    }
  }
});
