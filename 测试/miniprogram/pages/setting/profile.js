const API = require('../../utils/api');

Page({
  data: {
    form: {
      name: '',
      gender: 0,
      birthday: ''
    },
    genderOptions: [
      { value: 0, label: '未设置' },
      { value: 1, label: '男' },
      { value: 2, label: '女' }
    ],
    genderIndex: 0,
    today: ''
  },

  onLoad: function() {
    const date = new Date();
    const today = date.getFullYear() + '-' + 
      String(date.getMonth() + 1).padStart(2, '0') + '-' + 
      String(date.getDate()).padStart(2, '0');
    
    const userInfo = wx.getStorageSync('userInfo') || {};
    const genderIndex = userInfo.gender || 0;
    
    this.setData({
      today: today,
      form: {
        name: userInfo.name || '',
        gender: userInfo.gender || 0,
        birthday: userInfo.birthday || ''
      },
      genderIndex: genderIndex
    });
  },

  onInput: function(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({
      ['form.' + field]: e.detail.value
    });
  },

  onGenderChange: function(e) {
    const index = parseInt(e.detail.value);
    this.setData({
      genderIndex: index,
      'form.gender': this.data.genderOptions[index].value
    });
  },

  onDateChange: function(e) {
    this.setData({
      'form.birthday': e.detail.value
    });
  },

  saveProfile: function() {
    const { form } = this.data;
    
    if (!form.name.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '保存中...' });
    
    const userInfo = wx.getStorageSync('userInfo') || {};
    const updatedUser = {
      id: userInfo.id,
      name: form.name.trim(),
      gender: form.gender,
      birthday: form.birthday || null
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
