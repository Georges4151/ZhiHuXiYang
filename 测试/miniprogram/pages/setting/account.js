const API = require('../../utils/api');

Page({
  data: {
    userInfo: {},
    genderText: '未设置',
    baseUrl: ''
  },

  onLoad: function() {
    this.setData({
      baseUrl: API.BASE_URL
    });
    this.loadUserInfo();
  },

  onShow: function() {
    this.loadUserInfo();
  },

  loadUserInfo: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      let genderText = '未设置';
      if (userInfo.gender === 1) {
        genderText = '男';
      } else if (userInfo.gender === 2) {
        genderText = '女';
      }
      
      this.setData({
        userInfo: userInfo,
        genderText: genderText
      });
    }
  },

  chooseAvatar: function() {
    const that = this;
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        that.uploadAvatar(tempFilePath);
      }
    });
  },

  uploadAvatar: function(filePath) {
    const that = this;
    const userInfo = this.data.userInfo;
    
    wx.showLoading({ title: '上传中...' });
    
    wx.uploadFile({
      url: API.BASE_URL + '/user/avatar',
      filePath: filePath,
      name: 'file',
      formData: {
        userId: userInfo.id
      },
      success: function(res) {
        wx.hideLoading();
        const data = JSON.parse(res.data);
        if (data.code === 200) {
          const avatarUrl = data.data.url;
          const newUserInfo = { ...userInfo, avatar: avatarUrl };
          wx.setStorageSync('userInfo', newUserInfo);
          that.setData({ userInfo: newUserInfo });
          wx.showToast({ title: '头像已更新', icon: 'success' });
        } else {
          wx.showToast({ title: data.message || '上传失败', icon: 'none' });
        }
      },
      fail: function() {
        wx.hideLoading();
        wx.showToast({ title: '上传失败', icon: 'none' });
      }
    });
  },

  goToEdit: function(e) {
    const type = e.currentTarget.dataset.type;
    const urlMap = {
      'profile': '/pages/setting/profile',
      'password': '/pages/setting/password',
      'emergency': '/pages/setting/emergency',
      'health': '/pages/setting/health'
    };
    
    if (urlMap[type]) {
      wx.navigateTo({ url: urlMap[type] });
    }
  },

  logout: function() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: function(res) {
        if (res.confirm) {
          wx.removeStorageSync('userInfo');
          wx.showToast({ title: '已退出', icon: 'success' });
          setTimeout(function() {
            wx.navigateTo({ url: '/pages/auth/login' });
          }, 1500);
        }
      }
    });
  }
});
