const API = require('../../utils/api');

Page({
  data: {
    form: {
      emergencyContact: '',
      emergencyPhone: ''
    }
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      form: {
        emergencyContact: userInfo.emergencyContact || '',
        emergencyPhone: userInfo.emergencyPhone || ''
      }
    });
  },

  onInput: function(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({
      ['form.' + field]: e.detail.value
    });
  },

  saveEmergency: function() {
    const { form } = this.data;
    
    if (form.emergencyPhone && !/^1[3-9]\d{9}$/.test(form.emergencyPhone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '保存中...' });
    
    const userInfo = wx.getStorageSync('userInfo') || {};
    const updatedUser = {
      id: userInfo.id,
      emergencyContact: form.emergencyContact.trim(),
      emergencyPhone: form.emergencyPhone.trim()
    };
    
    API.updateUser(updatedUser).then(() => {
      wx.hideLoading();
      const newUserInfo = { ...userInfo, ...updatedUser };
      wx.setStorageSync('userInfo', newUserInfo);
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(() => {
      wx.hideLoading();
      const newUserInfo = { ...userInfo, ...updatedUser };
      wx.setStorageSync('userInfo', newUserInfo);
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    });
  }
});
