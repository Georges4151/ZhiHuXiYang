const API = require('../../utils/api');

Page({
  data: {
    serviceList: [],
    categoryList: [
      { id: 0, name: '全部', color: '#667eea', iconText: '全' },
      { id: 1, name: '家政服务', color: '#E6A23C', iconText: '家' },
      { id: 2, name: '餐饮服务', color: '#67C23A', iconText: '餐' },
      { id: 3, name: '医疗护理', color: '#F56C6C', iconText: '医' },
      { id: 4, name: '陪护服务', color: '#9B59B6', iconText: '陪' }
    ],
    currentCategory: 0,
    loading: false,
    userInfo: null
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    this.loadServices();
  },

  onShow: function() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 1
      });
    }
  },

  loadServices: function() {
    this.setData({ loading: true });
    
    const { currentCategory } = this.data;
    const categoryId = currentCategory === 0 ? null : currentCategory;
    
    API.getServiceList(categoryId).then(data => {
      const serviceList = (data || []).map(item => ({
        id: item.id,
        name: item.name,
        description: item.description || '',
        price: item.price || 0,
        duration: item.duration || 60,
        category: this.getCategoryName(item.category),
        sales: item.sales || 0,
        rating: item.rating || 4.8,
        iconBg: this.getCategoryColor(item.category),
        iconText: this.getCategoryIconText(item.category)
      }));
      this.setData({ serviceList, loading: false });
    }).catch(err => {
      console.error('加载服务失败', err);
      wx.showToast({
        title: '加载服务失败',
        icon: 'none'
      });
      this.setData({ 
        serviceList: [],
        loading: false 
      });
    });
  },

  getCategoryName: function(category) {
    const categoryMap = { 1: '家政服务', 2: '餐饮服务', 3: '医疗护理', 4: '陪护服务' };
    return categoryMap[category] || '其他';
  },

  getCategoryColor: function(category) {
    const colorMap = {
      1: '#E6A23C',
      2: '#67C23A',
      3: '#F56C6C',
      4: '#9B59B6'
    };
    return colorMap[category] || '#4A90D9';
  },
  
  getCategoryIconText: function(category) {
    const iconMap = {
      1: '家',
      2: '餐',
      3: '医',
      4: '陪'
    };
    return iconMap[category] || '服';
  },

  onCategoryChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ currentCategory: id });
    this.loadServices();
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/service/detail?id=' + id });
  },

  goToOrder: function(e) {
    const { id } = e.currentTarget.dataset;
    const service = this.data.serviceList.find(item => item.id === id);
    const { userInfo } = this.data;
    
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    
    if (service) {
      wx.showModal({
        title: '预约服务',
        content: `确定预约「${service.name}」服务吗？费用：¥${service.price}`,
        confirmText: '立即预约',
        success: (res) => {
          if (res.confirm) {
            wx.showLoading({ title: '预约中...' });
            
            const now = new Date();
            const formatTime = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;
            
            const orderData = {
              userId: userInfo.id,
              serviceId: service.id,
              serviceName: service.name,
              amount: service.price,
              appointmentTime: formatTime,
              serviceAddress: '待确认',
              contactName: userInfo.name || '用户',
              contactPhone: userInfo.phone || ''
            };
            
            API.createOrder(orderData).then(result => {
              wx.hideLoading();
              wx.showToast({ title: '预约成功', icon: 'success' });
              setTimeout(() => {
                wx.navigateTo({ url: '/pages/order/list' });
              }, 1500);
            }).catch((err) => {
              wx.hideLoading();
              console.error('预约失败', err);
              wx.showToast({ title: '预约失败，请重试', icon: 'none' });
            });
          }
        }
      });
    }
  },

  makePhoneCall: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: () => {}
    });
  }
});
