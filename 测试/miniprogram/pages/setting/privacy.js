Page({
  data: {
    locationEnabled: true,
    notificationEnabled: true,
    statsEnabled: true
  },

  onLoad: function() {
    this.loadSettings();
  },

  loadSettings: function() {
    const settings = wx.getStorageSync('privacySettings');
    if (settings) {
      this.setData(settings);
    }
  },

  saveSettings: function() {
    const { locationEnabled, notificationEnabled, statsEnabled } = this.data;
    wx.setStorageSync('privacySettings', {
      locationEnabled,
      notificationEnabled,
      statsEnabled
    });
  },

  toggleLocation: function(e) {
    this.setData({
      locationEnabled: e.detail.value
    });
    this.saveSettings();
    if (e.detail.value) {
      wx.showToast({ title: '已开启', icon: 'success' });
    }
  },

  toggleNotification: function(e) {
    this.setData({
      notificationEnabled: e.detail.value
    });
    this.saveSettings();
  },

  toggleStats: function(e) {
    this.setData({
      statsEnabled: e.detail.value
    });
    this.saveSettings();
  },

  showPrivacyPolicy: function() {
    wx.showModal({
      title: '隐私政策',
      content: '我们重视您的隐私保护。本应用仅收集必要的用户信息用于提供服务，不会向第三方泄露您的个人信息。',
      showCancel: false,
      confirmText: '我知道了'
    });
  },

  showUserAgreement: function() {
    wx.showModal({
      title: '用户协议',
      content: '欢迎使用智慧养老服务平台。使用本服务即表示您同意遵守相关法律法规，文明使用平台服务。',
      showCancel: false,
      confirmText: '我知道了'
    });
  }
});
