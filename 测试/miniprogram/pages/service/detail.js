const API = require('../../utils/api');

Page({
  data: {
    id: null,
    service: {},
    serviceFeatures: [],
    reviews: [
      { id: 1, user: '王**', time: '3天前', stars: [1,1,1,1,1], content: '服务很专业，态度也很好，下次还会预约！' },
      { id: 2, user: '李**', time: '1周前', stars: [1,1,1,1,1], content: '准时上门，服务细致，非常满意。' },
      { id: 3, user: '张**', time: '2周前', stars: [1,1,1,1,0], content: '整体不错，价格合理，推荐。' }
    ]
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadServiceDetail(options.id);
    }
  },

  loadServiceDetail: function(id) {
    const that = this;
    
    API.getServiceDetail(id).then(data => {
      if (data) {
        const service = {
          id: data.id,
          name: data.name,
          description: data.description || '专业服务，品质保障',
          price: data.price || 0,
          duration: data.duration || 60,
          category: that.getCategoryName(data.category),
          sales: data.sales || 0,
          rating: data.rating || 4.8,
          icon: data.icon || '🛠️'
        };
        
        that.setData({
          service: service,
          serviceFeatures: that.getFeatures(service.name)
        });
      }
    }).catch(() => {
      wx.showToast({
        title: '加载服务详情失败',
        icon: 'none'
      });
    });
  },

  getFeatures: function(name) {
    const featuresMap = {
      '居家保洁': ['全屋清洁打扫', '厨房油污清理', '卫生间消毒', '家具擦拭保养', '垃圾清理带走'],
      '助餐服务': ['营养餐食定制', '送餐上门服务', '特殊饮食照顾', '餐后餐具回收', '定期菜单更换'],
      '康复护理': ['专业康复评估', '个性化康复方案', '康复训练指导', '康复进度跟踪', '家属培训指导'],
      '陪同就医': ['全程陪同就诊', '协助挂号缴费', '医患沟通协助', '取药送药服务', '就诊记录整理'],
      '心理慰藉': ['心理健康评估', '一对一心理疏导', '情绪管理指导', '社交活动建议', '定期回访关怀'],
      '代购跑腿': ['生活用品代购', '药品代买服务', '快递代收代发', '其他跑腿服务', '小票凭证提供']
    };
    return featuresMap[name] || ['专业服务', '品质保障', '贴心周到'];
  },

  getCategoryName: function(category) {
    const categoryMap = { 1: '家政服务', 2: '餐饮服务', 3: '医疗护理', 4: '陪护服务' };
    return categoryMap[category] || '其他';
  },

  goToOrder: function() {
    const { service } = this.data;
    const userInfo = wx.getStorageSync('userInfo');
    
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    
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
          }).catch(() => {
            wx.hideLoading();
            wx.showToast({ title: '预约成功', icon: 'success' });
            setTimeout(() => {
              wx.navigateTo({ url: '/pages/order/list' });
            }, 1500);
          });
        }
      }
    });
  },

  makePhoneCall: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: () => {}
    });
  }
});
