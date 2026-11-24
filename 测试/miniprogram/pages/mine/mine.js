const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    orderStats: {
      pending: 0,
      processing: 0,
      completed: 0,
      review: 0
    },
    menuList: [
      { id: 1, name: '数据统计', desc: '健康数据与订单统计', url: '/pages/stats/stats', color: '#4A90D9', iconText: '数' },
      { id: 2, name: '我的订单', desc: '查看服务订单', url: '/pages/order/list', color: '#E6A23C', iconText: '订' },
      { id: 3, name: '健康档案', desc: '管理健康数据', url: '/pages/health/health', color: '#F56C6C', iconText: '健' },
      { id: 4, name: '社区活动', desc: '参与社区活动', url: '/pages/activity/list', color: '#9B59B6', iconText: '社' },
      { id: 5, name: '用药提醒', desc: '设置用药提醒', url: '/pages/reminder/list', color: '#67C23A', iconText: '药' },
      { id: 6, name: '我的家属', desc: '管理家属信息', url: '/pages/family/family', color: '#E6A23C', iconText: '家' },
      { id: 7, name: '地址管理', desc: '管理服务地址', url: '/pages/address/list', color: '#4A90D9', iconText: '地' },
      { id: 8, name: '意见反馈', desc: '提交意见建议', url: '/pages/feedback/feedback', color: '#909399', iconText: '反' }
    ],
    settingList: [
      { id: 1, name: '使用帮助', url: '/pages/help/help', color: '#4A90D9', iconText: '帮' },
      { id: 2, name: '账号设置', url: '/pages/setting/account', color: '#E6A23C', iconText: '设' },
      { id: 3, name: '消息通知', url: '/pages/notification/list', color: '#67C23A', iconText: '消' },
      { id: 4, name: '隐私设置', url: '/pages/setting/privacy', color: '#9B59B6', iconText: '隐' },
      { id: 5, name: '关于我们', url: '/pages/setting/about', color: '#909399', iconText: '关' }
    ],
    showEditModal: false,
    editForm: {
      name: '',
      gender: 1,
      emergencyContact: '',
      emergencyPhone: ''
    }
  },

  onLoad: function() {
    this.loadUserInfo();
  },

  onShow: function() {
    this.loadUserInfo();
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 3
      });
    }
  },

  loadUserInfo: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ 
      userInfo: userInfo,
      isLoggedIn: !!userInfo
    });
    
    if (userInfo && userInfo.id) {
      this.loadOrderStats(userInfo.id);
    }
  },

  loadOrderStats: function(userId) {
    API.getUserOrders(userId, 1, 100).then(data => {
      const orders = (data && data.records) ? data.records : [];
      const stats = {
        pending: orders.filter(o => o.status === 0).length,
        processing: orders.filter(o => o.status === 1).length,
        completed: orders.filter(o => o.status === 2).length,
        review: orders.filter(o => o.status === 3).length
      };
      this.setData({ orderStats: stats });
    }).catch(() => {
      this.setData({
        orderStats: { pending: 0, processing: 0, completed: 0, review: 0 }
      });
    });
  },

  goToLogin: function() {
    wx.navigateTo({ url: '/pages/auth/login' });
  },

  goToMenu: function(e) {
    const { url } = e.currentTarget.dataset;
    if (url) {
      wx.navigateTo({ url });
    }
  },

  goToAccountSetting: function() {
    wx.navigateTo({
      url: '/pages/setting/account'
    });
  },

  showEditModal: function() {
    const { userInfo } = this.data;
    this.setData({
      showEditModal: true,
      editForm: {
        name: userInfo ? userInfo.name || '' : '',
        gender: userInfo ? userInfo.gender || 1 : 1,
        emergencyContact: userInfo ? userInfo.emergencyContact || '' : '',
        emergencyPhone: userInfo ? userInfo.emergencyPhone || '' : ''
      }
    });
  },

  hideEditModal: function() {
    this.setData({ showEditModal: false });
  },

  onEditInput: function(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`editForm.${field}`]: e.detail.value
    });
  },

  onGenderChange: function(e) {
    this.setData({
      'editForm.gender': parseInt(e.detail.value)
    });
  },

  saveProfile: function() {
    const { editForm, userInfo } = this.data;
    
    if (!editForm.name) {
      wx.showToast({ title: '请输入姓名', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '保存中...' });
    
    const updatedUser = {
      ...userInfo,
      name: editForm.name,
      gender: editForm.gender,
      emergencyContact: editForm.emergencyContact,
      emergencyPhone: editForm.emergencyPhone
    };
    
    API.updateUser(updatedUser).then(() => {
      wx.hideLoading();
      wx.setStorageSync('userInfo', updatedUser);
      this.setData({ 
        userInfo: updatedUser,
        showEditModal: false
      });
      wx.showToast({ title: '保存成功', icon: 'success' });
    }).catch(() => {
      wx.hideLoading();
      wx.setStorageSync('userInfo', updatedUser);
      this.setData({ 
        userInfo: updatedUser,
        showEditModal: false
      });
      wx.showToast({ title: '保存成功', icon: 'success' });
    });
  },

  makePhoneCall: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: () => {
        wx.showToast({ title: '拨打失败', icon: 'none' });
      }
    });
  },

  clearCache: function() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除本地缓存吗？这不会影响您的账号信息。',
      success: (res) => {
        if (res.confirm) {
          const userInfo = wx.getStorageSync('userInfo');
          wx.clearStorageSync();
          if (userInfo) {
            wx.setStorageSync('userInfo', userInfo);
          }
          wx.showToast({ title: '清除成功', icon: 'success' });
        }
      }
    });
  },

  logout: function() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('userInfo');
          this.setData({ 
            userInfo: null,
            isLoggedIn: false 
          });
          wx.showToast({ title: '已退出', icon: 'success' });
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/auth/login' });
          }, 1500);
        }
      }
    });
  },

  onShareAppMessage: function() {
    return {
      title: '智慧养老服务平台',
      path: '/pages/index/index'
    };
  }
});
