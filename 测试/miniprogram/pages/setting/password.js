const API = require('../../utils/api');

Page({
  data: {
    hasPassword: false,
    form: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      hasPassword: !!(userInfo.password && userInfo.password.length > 0)
    });
  },

  onInput: function(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({
      ['form.' + field]: e.detail.value
    });
  },

  savePassword: function() {
    const { hasPassword, form } = this.data;
    
    if (hasPassword && !form.oldPassword) {
      wx.showToast({ title: '请输入原密码', icon: 'none' });
      return;
    }
    
    if (!form.newPassword) {
      wx.showToast({ title: '请输入新密码', icon: 'none' });
      return;
    }
    
    if (form.newPassword.length < 6 || form.newPassword.length > 20) {
      wx.showToast({ title: '密码长度需6-20位', icon: 'none' });
      return;
    }
    
    if (form.newPassword !== form.confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '保存中...' });
    
    const userInfo = wx.getStorageSync('userInfo') || {};
    
    wx.request({
      url: API.BASE_URL + '/user/password',
      method: 'PUT',
      data: {
        userId: userInfo.id,
        oldPassword: form.oldPassword,
        newPassword: form.newPassword
      },
      success: (res) => {
        wx.hideLoading();
        if (res.data.code === 200) {
          wx.showToast({ title: '修改成功', icon: 'success' });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          wx.showToast({ title: res.data.message || '修改失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({ title: '网络错误', icon: 'none' });
      }
    });
  }
});
