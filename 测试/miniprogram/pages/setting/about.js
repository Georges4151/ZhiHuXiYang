Page({
  data: {},

  copyWechat: function() {
    wx.setClipboardData({
      data: 'SmartElderlyCare',
      success: function() {
        wx.showToast({ title: '已复制', icon: 'success' });
      }
    });
  },

  copyEmail: function() {
    wx.setClipboardData({
      data: 'service@elderly.com',
      success: function() {
        wx.showToast({ title: '已复制', icon: 'success' });
      }
    });
  },

  makePhoneCall: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: function() {
        wx.showToast({ title: '拨打失败', icon: 'none' });
      }
    });
  },

  checkUpdate: function() {
    wx.showLoading({ title: '检查中...' });
    setTimeout(function() {
      wx.hideLoading();
      wx.showModal({
        title: '检查更新',
        content: '当前已是最新版本',
        showCancel: false,
        confirmText: '确定'
      });
    }, 1000);
  }
});
