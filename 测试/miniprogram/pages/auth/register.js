const API = require('../../utils/api');

Page({
  data: {
    phone: '',
    password: '',
    confirmPassword: '',
    name: '',
    gender: 1,
    emergencyContact: '',
    emergencyPhone: '',
    showPassword: false,
    showConfirmPassword: false,
    loading: false,
    genderOptions: [
      { value: 1, label: '男' },
      { value: 2, label: '女' }
    ]
  },

  onPhoneInput: function(e) {
    this.setData({ phone: e.detail.value });
  },

  onPasswordInput: function(e) {
    this.setData({ password: e.detail.value });
  },

  onConfirmPasswordInput: function(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  onNameInput: function(e) {
    this.setData({ name: e.detail.value });
  },

  onGenderChange: function(e) {
    this.setData({ gender: parseInt(e.detail.value) });
  },

  onEmergencyContactInput: function(e) {
    this.setData({ emergencyContact: e.detail.value });
  },

  onEmergencyPhoneInput: function(e) {
    this.setData({ emergencyPhone: e.detail.value });
  },

  togglePassword: function() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  toggleConfirmPassword: function() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword });
  },

  handleRegister: async function() {
    const { phone, password, confirmPassword, name, gender, emergencyContact, emergencyPhone, loading } = this.data;
    
    if (loading) return;
    
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' });
      return;
    }
    
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    
    if (password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return;
    }
    
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }
    
    if (!name) {
      wx.showToast({ title: '请输入姓名', icon: 'none' });
      return;
    }
    
    this.setData({ loading: true });
    
    try {
      const user = await API.register({
        phone,
        password,
        name,
        gender,
        emergencyContact,
        emergencyPhone
      });
      
      wx.setStorageSync('userInfo', user);
      wx.showToast({ title: '注册成功', icon: 'success' });
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/mine/mine' });
      }, 1000);
    } catch (e) {
      wx.showToast({ title: e.message || '注册失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  goToLogin: function() {
    wx.navigateBack();
  }
});
