const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    healthData: null,
    recentData: [],
    showAddModal: false,
    activeTab: 'overview',
    tabs: [
      { id: 'overview', name: '概览' },
      { id: 'history', name: '历史' }
    ],
    formData: {
      bloodPressureHigh: '',
      bloodPressureLow: '',
      heartRate: '',
      bloodSugar: '',
      temperature: '',
      weight: ''
    },
    healthTips: [
      '建议每天测量血压2次，早晚各一次',
      '空腹血糖正常值：3.9-6.1mmol/L',
      '正常心率范围：60-100次/分钟'
    ]
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    this.loadHealthData();
  },

  onShow: function() {
    this.loadHealthData();
  },

  loadHealthData: function() {
    const { userInfo } = this.data;
    if (!userInfo || !userInfo.id) {
      this.setData({
        healthData: {
          bloodPressureHigh: 125,
          bloodPressureLow: 82,
          heartRate: 72,
          bloodSugar: 5.6,
          temperature: 36.5,
          weight: 65.5,
          measureTime: '2024-01-15 08:30'
        },
        recentData: [
          { id: 1, measureTime: '2024-01-15', bloodPressureHigh: 125, bloodPressureLow: 82, heartRate: 72, bloodSugar: 5.6 },
          { id: 2, measureTime: '2024-01-14', bloodPressureHigh: 128, bloodPressureLow: 85, heartRate: 75, bloodSugar: 5.8 },
          { id: 3, measureTime: '2024-01-13', bloodPressureHigh: 122, bloodPressureLow: 80, heartRate: 70, bloodSugar: 5.4 },
          { id: 4, measureTime: '2024-01-12', bloodPressureHigh: 130, bloodPressureLow: 88, heartRate: 78, bloodSugar: 6.0 },
          { id: 5, measureTime: '2024-01-11', bloodPressureHigh: 126, bloodPressureLow: 83, heartRate: 73, bloodSugar: 5.5 }
        ]
      });
      return;
    }

    wx.showLoading({ title: '加载中...' });
    
    Promise.all([
      API.getLatestHealth(userInfo.id),
      API.getRecentHealth(userInfo.id, 10)
    ]).then(([latestData, recentData]) => {
      wx.hideLoading();
      if (latestData) {
        this.setData({ 
          healthData: {
            ...latestData,
            measureTime: this.formatTime(latestData.measureTime)
          }
        });
      }
      if (recentData && recentData.length > 0) {
        this.setData({
          recentData: recentData.map(item => ({
            ...item,
            measureTime: this.formatDate(item.measureTime)
          }))
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('加载健康数据失败', err);
      this.setData({
        healthData: {
          bloodPressureHigh: 125,
          bloodPressureLow: 82,
          heartRate: 72,
          bloodSugar: 5.6,
          temperature: 36.5,
          weight: 65.5,
          measureTime: '2024-01-15 08:30'
        },
        recentData: [
          { id: 1, measureTime: '2024-01-15', bloodPressureHigh: 125, bloodPressureLow: 82, heartRate: 72, bloodSugar: 5.6 },
          { id: 2, measureTime: '2024-01-14', bloodPressureHigh: 128, bloodPressureLow: 85, heartRate: 75, bloodSugar: 5.8 },
          { id: 3, measureTime: '2024-01-13', bloodPressureHigh: 122, bloodPressureLow: 80, heartRate: 70, bloodSugar: 5.4 }
        ]
      });
    });
  },

  onTabChange: function(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ activeTab: id });
  },

  showAddModal: function() {
    this.setData({ showAddModal: true });
  },

  hideAddModal: function() {
    this.setData({ showAddModal: false });
  },

  onFormInput: function(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`formData.${field}`]: e.detail.value
    });
  },

  submitHealthData: function() {
    const { formData, userInfo } = this.data;
    
    if (!formData.bloodPressureHigh && !formData.heartRate && !formData.bloodSugar) {
      wx.showToast({ title: '请至少填写一项数据', icon: 'none' });
      return;
    }

    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    const now = new Date();
    const measureTimeStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;

    const submitData = {
      userId: userInfo.id,
      bloodPressureHigh: formData.bloodPressureHigh ? parseFloat(formData.bloodPressureHigh) : null,
      bloodPressureLow: formData.bloodPressureLow ? parseFloat(formData.bloodPressureLow) : null,
      heartRate: formData.heartRate ? parseFloat(formData.heartRate) : null,
      bloodSugar: formData.bloodSugar ? parseFloat(formData.bloodSugar) : null,
      temperature: formData.temperature ? parseFloat(formData.temperature) : null,
      weight: formData.weight ? parseFloat(formData.weight) : null,
      measureTime: measureTimeStr
    };

    API.addHealthData(submitData).then(data => {
      wx.hideLoading();
      wx.showToast({ title: '添加成功', icon: 'success' });
      this.setData({
        showAddModal: false,
        formData: {
          bloodPressureHigh: '',
          bloodPressureLow: '',
          heartRate: '',
          bloodSugar: '',
          temperature: '',
          weight: ''
        }
      });
      this.loadHealthData();
    }).catch(err => {
      wx.hideLoading();
      console.error('提交失败', err);
      wx.showToast({ title: '网络错误，请重试', icon: 'none' });
    });
  },

  formatTime: function(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
  },

  formatDate: function(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  getHealthStatus: function(type, value) {
    const statusMap = {
      bp: value < 120 ? '正常' : value < 140 ? '偏高' : '高血压',
      hr: value >= 60 && value <= 100 ? '正常' : value < 60 ? '偏低' : '偏快',
      bs: value < 6.1 ? '正常' : value < 7.0 ? '偏高' : '需关注'
    };
    return statusMap[type] || '正常';
  },

  getStatusColor: function(type, value) {
    const status = this.getHealthStatus(type, value);
    return status === '正常' ? '#67C23A' : status === '偏高' || status === '偏快' ? '#E6A23C' : '#F56C6C';
  },

  onAnalyzeHealth: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    wx.showLoading({ title: 'AI 分析中...' });

    API.analyzeHealth().then(result => {
      wx.hideLoading();
      if (result && result.success) {
        this.showAnalysisResult(result);
      } else {
        wx.showToast({ title: result.message || '分析失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('AI分析失败', err);
      wx.showToast({ title: '网络错误，请重试', icon: 'none' });
    });
  },

  showAnalysisResult: function(data) {
    if (!data) return;

    let analysisContent = data.analysis || '暂无分析结果';

    analysisContent = analysisContent
      .replace(/### /g, '\n🏥 ')
      .replace(/## /g, '\n📋 ')
      .replace(/\*\*(.*?)\*\*/g, '$1')
      .replace(/\*(.*?)\*/g, '$1')
      .replace(/1\./g, '\n1.')
      .replace(/2\./g, '\n2.')
      .replace(/3\./g, '\n3.')
      .replace(/4\./g, '\n4.')
      .trim();

    wx.showModal({
      title: '🤖 AI 健康分析报告',
      content: analysisContent,
      showCancel: true,
      cancelText: '关闭',
      confirmText: '知道了',
      confirmColor: '#67C23A',
      success: (res) => {
        if (res.confirm) {
          console.log('用户查看分析结果');
        }
      }
    });
  }
});
