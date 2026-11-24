const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    currentTab: 0,
    tabs: [
      { id: 0, name: '全部' },
      { id: 1, name: '待服务' },
      { id: 2, name: '服务中' },
      { id: 3, name: '已完成' },
      { id: 4, name: '已取消' }
    ],
    orderList: [],
    loading: false,
    pageNum: 1,
    pageSize: 10
  },

  onLoad: function(options) {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    
    if (options.status) {
      this.setData({ currentTab: parseInt(options.status) });
    }
    
    this.loadOrders();
  },

  onShow: function() {
    this.loadOrders();
  },

  loadOrders: function() {
    const { userInfo, currentTab } = this.data;
    
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      this.setData({
        orderList: []
      });
      return;
    }
    
    this.setData({ loading: true });
    
    API.getUserOrders(userInfo.id, 1, 20).then(data => {
      let orderList = (data && data.records ? data.records : []).map(item => ({
        id: item.id,
        orderNo: item.orderNo,
        serviceName: item.serviceName,
        price: item.amount,
        status: item.orderStatus,
        statusText: this.getStatusText(item.orderStatus),
        createTime: this.formatDate(item.createTime),
        appointmentTime: this.formatDate(item.appointmentTime)
      }));
      
      if (currentTab > 0) {
        if (currentTab === 4) {
          orderList = orderList.filter(item => item.status === 3 || item.status === 4);
        } else {
          orderList = orderList.filter(item => item.status === currentTab - 1);
        }
      }
      
      this.setData({ 
        orderList: orderList, 
        loading: false 
      });
    }).catch(err => {
      console.error('加载订单失败', err);
      wx.showToast({
        title: '加载订单失败',
        icon: 'none'
      });
      this.setData({
        orderList: [],
        loading: false
      });
    });
  },

  getStatusText: function(status) {
    const statusMap = { 0: '待服务', 1: '服务中', 2: '已完成', 3: '已取消', 4: '已取消' };
    return statusMap[status] || '未知';
  },

  formatDate: function(dateStr) {
    if (!dateStr) return '';
    return dateStr.substring(0, 16).replace('T', ' ');
  },

  onTabChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ currentTab: id });
    this.loadOrders();
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/order/detail?id=' + id });
  },

  cancelOrder: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '取消订单',
      content: '确定要取消此订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '取消中...' });
          API.updateOrderStatus(id, 4).then(() => {
            wx.hideLoading();
            wx.showToast({ title: '已取消', icon: 'success' });
            this.loadOrders();
          }).catch((err) => {
            wx.hideLoading();
            console.error('取消订单失败', err);
            wx.showToast({ title: '取消失败，请重试', icon: 'none' });
          });
        }
      }
    });
  },

  goToPay: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '支付确认',
      content: '确定要支付此订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '支付中...' });
          API.updateOrderStatus(id, 1).then(() => {
            wx.hideLoading();
            wx.showToast({ title: '支付成功', icon: 'success' });
            this.loadOrders();
          }).catch((err) => {
            wx.hideLoading();
            console.error('支付失败', err);
            wx.showToast({ title: '支付失败，请重试', icon: 'none' });
          });
        }
      }
    });
  },

  completeOrder: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '确认完成',
      content: '确定服务已完成吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '处理中...' });
          API.updateOrderStatus(id, 2).then(() => {
            wx.hideLoading();
            wx.showToast({ title: '已确认完成', icon: 'success' });
            this.loadOrders();
          }).catch((err) => {
            wx.hideLoading();
            console.error('确认完成失败', err);
            wx.showToast({ title: '操作失败，请重试', icon: 'none' });
          });
        }
      }
    });
  },

  getStatusColor: function(status) {
    const colors = {
      0: '#E6A23C',
      1: '#409EFF',
      2: '#67C23A',
      3: '#909399',
      4: '#909399'
    };
    return colors[status] || '#999';
  }
});
