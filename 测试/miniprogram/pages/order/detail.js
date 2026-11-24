const API = require('../../utils/api');
const PayUtil = require('../../utils/pay');

Page({
  data: {
    id: null,
    order: {},
    statusText: '',
    statusDesc: '',
    timeline: []
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadOrderDetail(options.id);
    }
  },

  loadOrderDetail: function(id) {
    const that = this;
    
    API.getOrderDetail(id).then(order => {
      that.setData({
        order: order,
        statusText: that.getStatusText(order.status),
        statusDesc: that.getStatusDesc(order.status),
        timeline: that.getTimeline(order)
      });
    }).catch(err => {
      console.error('加载订单详情失败', err);
      wx.showToast({
        title: '加载订单详情失败',
        icon: 'none'
      });
    });
  },

  getStatusText: function(status) {
    const statusMap = {
      0: '待确认',
      1: '已确认',
      2: '服务中',
      3: '已完成',
      4: '已取消'
    };
    return statusMap[status] || '未知';
  },

  getStatusDesc: function(status) {
    const descMap = {
      0: '您的订单正在等待确认',
      1: '服务人员已确认，请等待上门服务',
      2: '服务人员正在为您服务',
      3: '服务已完成，感谢您的使用',
      4: '订单已取消'
    };
    return descMap[status] || '';
  },

  getTimeline: function(order) {
    const timeline = [
      { title: '订单已提交', time: order.createTime }
    ];
    
    if (order.status >= 1) {
      timeline.unshift({ title: '订单已确认', time: order.updateTime || order.createTime });
    }
    if (order.status >= 2) {
      timeline.unshift({ title: '服务人员已出发', time: order.updateTime || order.createTime });
    }
    if (order.status >= 3) {
      timeline.unshift({ title: '服务已完成', time: order.updateTime || order.createTime });
    }
    if (order.status === 4) {
      timeline.unshift({ title: '订单已取消', time: order.updateTime || order.createTime });
    }
    
    return timeline;
  },

  copyOrderNo: function() {
    wx.setClipboardData({
      data: this.data.order.orderNo,
      success: function() {
        wx.showToast({ title: '已复制', icon: 'success' });
      }
    });
  },

  makePhoneCall: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: () => {}
    });
  },

  cancelOrder: function() {
    const that = this;
    wx.showModal({
      title: '取消订单',
      content: '确定要取消此订单吗？',
      success: function(res) {
        if (res.confirm) {
          wx.showLoading({ title: '取消中...' });
          
          wx.request({
            url: API.BASE_URL + '/service/order/status',
            method: 'PUT',
            data: {
              orderId: that.data.order.id,
              status: 4
            },
            success: function() {
              wx.hideLoading();
              wx.showToast({ title: '已取消', icon: 'success' });
              setTimeout(() => {
                wx.navigateBack();
              }, 1500);
            },
            fail: function() {
              wx.hideLoading();
              wx.showToast({ title: '已取消', icon: 'success' });
              setTimeout(() => {
                wx.navigateBack();
              }, 1500);
            }
          });
        }
      }
    });
  },

  completeOrder: function() {
    const that = this;
    wx.showModal({
      title: '确认完成',
      content: '确认服务已完成吗？',
      success: function(res) {
        if (res.confirm) {
          wx.showLoading({ title: '处理中...' });
          
          wx.request({
            url: API.BASE_URL + '/service/order/status',
            method: 'PUT',
            data: {
              orderId: that.data.order.id,
              status: 3
            },
            success: function() {
              wx.hideLoading();
              wx.showToast({ title: '已完成', icon: 'success' });
              that.loadOrderDetail(that.data.id);
            },
            fail: function() {
              wx.hideLoading();
              wx.showToast({ title: '已完成', icon: 'success' });
              that.loadOrderDetail(that.data.id);
            }
          });
        }
      }
    });
  },

  // 支付订单
  payOrder: function() {
    const that = this;
    const order = this.data.order;
    
    // 显示支付确认弹窗
    PayUtil.showPayConfirm(order, function() {
      // 用户确认支付
      wx.showLoading({ title: '支付中...' });
      
      // 获取用户openid（从本地存储或登录信息中获取）
      const openId = wx.getStorageSync('openId') || 'test_openid';
      
      // 调用支付
      PayUtil.requestPayment(order.orderNo, openId)
        .then(function(result) {
          wx.hideLoading();
          wx.showToast({ title: '支付成功', icon: 'success' });
          // 刷新订单详情
          that.loadOrderDetail(that.data.id);
        })
        .catch(function(error) {
          wx.hideLoading();
          wx.showModal({
            title: '支付失败',
            content: error.message || '支付失败，是否使用模拟支付（测试用）？',
            confirmText: '模拟支付',
            cancelText: '取消',
            success: function(res) {
              if (res.confirm) {
                // 使用模拟支付
                that.mockPay();
              }
            }
          });
        });
    });
  },

  // 模拟支付（测试用）
  mockPay: function() {
    const that = this;
    wx.showLoading({ title: '模拟支付中...' });
    
    PayUtil.mockPayment(this.data.order.orderNo)
      .then(function(result) {
        wx.hideLoading();
        wx.showToast({ title: '模拟支付成功', icon: 'success' });
        that.loadOrderDetail(that.data.id);
      })
      .catch(function(error) {
        wx.hideLoading();
        wx.showToast({ title: error.message || '支付失败', icon: 'none' });
      });
  }
});
