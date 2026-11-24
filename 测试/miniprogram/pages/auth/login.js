const API = require('../../utils/api');

Page({
  data: {
    phone: '',
    password: '',
    showPassword: false,
    loading: false
  },

  onPhoneInput: function(e) {
    this.setData({ phone: e.detail.value });
  },

  onPasswordInput: function(e) {
    this.setData({ password: e.detail.value });
  },

  togglePassword: function() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  handleLogin: async function() {
    const { phone, password, loading } = this.data;
    
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
    
    this.setData({ loading: true });
    
    try {
      const res = await API.login({ phone, password });
      
      // 保存用户信息
      const user = res.user || res;
      wx.setStorageSync('userInfo', user);
      
      // 生成临时 token（后端未返回 token 时的临时方案）
      let token = res.token;
      if (!token && user.id) {
        token = 'user_' + user.id + '_' + Date.now();
      }
      if (token) {
        wx.setStorageSync('token', token);
      }
      
      wx.showToast({ title: '登录成功', icon: 'success' });
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/mine/mine' });
      }, 1000);
    } catch (e) {
      wx.showToast({ title: e.message || '登录失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  goToRegister: function() {
    wx.navigateTo({ url: '/pages/auth/register' });
  },

  onAgreement: function() {
    wx.showToast({ title: '用户协议', icon: 'none' });
  },

  onPrivacy: function() {
    wx.showToast({ title: '隐私政策', icon: 'none' });
  }
});
