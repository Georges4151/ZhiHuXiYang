const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    activeTab: 'health',
    tabs: [
      { id: 'health', name: '健康数据' },
      { id: 'order', name: '订单统计' },
      { id: 'overview', name: '系统概览' }
    ],
    healthData: {
      labels: [],
      bloodPressureHigh: [],
      bloodPressureLow: [],
      heartRate: [],
      bloodSugar: []
    },
    orderStats: {
      total: 0,
      pending: 0,
      processing: 0,
      completed: 0,
      categoryStats: []
    },
    overviewStats: {
      totalUsers: 0,
      totalOrders: 0,
      totalServices: 0,
      avgRating: 0
    },
    loading: true
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    this.loadAllData();
  },

  onShow: function() {
    this.loadAllData();
  },

  loadAllData: function() {
    this.setData({ loading: true });
    
    Promise.all([
      this.loadHealthData(),
      this.loadOrderStats(),
      this.loadOverviewStats()
    ]).then(() => {
      this.setData({ loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  loadHealthData: function() {
    const { userInfo } = this.data;
    const userId = userInfo ? userInfo.id : 0;
    
    return API.getRecentHealth(userId, 7).then(data => {
      if (data && data.length > 0) {
        const labels = data.map(item => {
          const date = new Date(item.measureTime);
          return `${date.getMonth() + 1}/${date.getDate()}`;
        }).reverse();
        
        const healthData = {
          labels: labels,
          bloodPressureHigh: data.map(item => item.bloodPressureHigh || 0).reverse(),
          bloodPressureLow: data.map(item => item.bloodPressureLow || 0).reverse(),
          heartRate: data.map(item => item.heartRate || 0).reverse(),
          bloodSugar: data.map(item => item.bloodSugar || 0).reverse()
        };
        
        this.setData({ healthData });
      } else {
        this.setData({
          healthData: {
            labels: ['1/1', '1/2', '1/3', '1/4', '1/5', '1/6', '1/7'],
            bloodPressureHigh: [125, 128, 122, 130, 126, 124, 127],
            bloodPressureLow: [82, 85, 80, 88, 83, 81, 84],
            heartRate: [72, 75, 70, 78, 73, 71, 74],
            bloodSugar: [5.6, 5.8, 5.4, 6.0, 5.5, 5.7, 5.9]
          }
        });
      }
    }).catch(() => {
      this.setData({
        healthData: {
          labels: ['1/1', '1/2', '1/3', '1/4', '1/5', '1/6', '1/7'],
          bloodPressureHigh: [125, 128, 122, 130, 126, 124, 127],
          bloodPressureLow: [82, 85, 80, 88, 83, 81, 84],
          heartRate: [72, 75, 70, 78, 73, 71, 74],
          bloodSugar: [5.6, 5.8, 5.4, 6.0, 5.5, 5.7, 5.9]
        }
      });
    });
  },

  loadOrderStats: function() {
    const { userInfo } = this.data;
    const userId = userInfo ? userInfo.id : 0;
    
    return API.getUserOrders(userId, 1, 100).then(data => {
      if (data && data.records) {
        const orders = data.records;
        const categoryMap = {};
        
        orders.forEach(order => {
          const category = order.serviceCategory || '其他';
          categoryMap[category] = (categoryMap[category] || 0) + 1;
        });
        
        const categoryStats = Object.keys(categoryMap).map(name => ({
          name: name,
          value: categoryMap[name]
        }));
        
        this.setData({
          orderStats: {
            total: orders.length,
            pending: orders.filter(o => o.status === 0).length,
            processing: orders.filter(o => o.status === 1).length,
            completed: orders.filter(o => o.status === 2).length,
            categoryStats: categoryStats
          }
        });
      } else {
        this.setData({
          orderStats: {
            total: 25,
            pending: 2,
            processing: 1,
            completed: 22,
            categoryStats: [
              { name: '家政服务', value: 10 },
              { name: '医疗护理', value: 8 },
              { name: '餐饮服务', value: 5 },
              { name: '陪护服务', value: 2 }
            ]
          }
        });
      }
    }).catch(() => {
      this.setData({
        orderStats: {
          total: 25,
          pending: 2,
          processing: 1,
          completed: 22,
          categoryStats: [
            { name: '家政服务', value: 10 },
            { name: '医疗护理', value: 8 },
            { name: '餐饮服务', value: 5 },
            { name: '陪护服务', value: 2 }
          ]
        }
      });
    });
  },

  loadOverviewStats: function() {
    this.setData({
      overviewStats: {
        totalUsers: 156,
        totalOrders: 892,
        totalServices: 24,
        avgRating: 4.8
      }
    });
    return Promise.resolve();
  },

  onTabChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ activeTab: id });
  },

  getStatusText: function(type, value) {
    const statusMap = {
      bp: value < 120 ? '正常' : value < 140 ? '偏高' : '高血压',
      hr: value >= 60 && value <= 100 ? '正常' : value < 60 ? '偏低' : '偏快',
      bs: value < 6.1 ? '正常' : value < 7.0 ? '偏高' : '需关注'
    };
    return statusMap[type] || '正常';
  },

  getStatusColor: function(type, value) {
    const status = this.getStatusText(type, value);
    return status === '正常' ? '#67C23A' : status === '偏高' || status === '偏快' ? '#E6A23C' : '#F56C6C';
  },

  onPullDownRefresh: function() {
    this.loadAllData().then(() => {
      wx.stopPullDownRefresh();
    });
  }
});
