const API = require('../../utils/api');
const MedicationReminder = require('../../utils/medicationReminder');

const DEFAULT_LOCATION = '117.33,38.37';
const DEFAULT_CITY = '黄骅市';

Page({
  data: {
    userInfo: null,
    bannerList: [
      { id: 1, title: '智慧养老服务平台', desc: '贴心服务，温暖相伴', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
      { id: 2, title: '关爱老人 从心开始', desc: '专业养老服务，让生活更美好', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
      { id: 3, title: '健康生活每一天', desc: '智能健康管理，守护您的健康', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
    ],
    functionList: [
      { id: 1, name: '智能咨询', url: '/pages/chatBot/chatBot', color: '#4A90D9', desc: 'AI智能问答', iconText: '智' },
      { id: 2, name: '健康管理', url: '/pages/health/health', color: '#67C23A', desc: '健康数据记录', iconText: '健' },
      { id: 3, name: '生活服务', url: '/pages/service/service', color: '#E6A23C', desc: '预约上门服务', iconText: '生' },
      { id: 4, name: '紧急呼叫', url: '/pages/emergency/emergency', color: '#F56C6C', desc: '一键呼救', iconText: '急' },
      { id: 5, name: '用药提醒', url: '/pages/reminder/list', color: '#9B59B6', desc: '定时服药提醒', iconText: '药' },
      { id: 6, name: '社区活动', url: '/pages/activity/list', color: '#E74C3C', desc: '丰富晚年生活', iconText: '社' },
      { id: 7, name: '老年朋友圈', url: '/pages/social/social', color: '#FF6B6B', desc: '分享生活点滴', iconText: '朋' },
      { id: 8, name: '使用帮助', url: '/pages/help/help', color: '#3498DB', desc: '常见问题解答', iconText: '帮' }
    ],
    hotNews: [],
    weather: {
      temp: '--',
      weather: '加载中',
      city: DEFAULT_CITY,
      humidity: '--',
      wind: '--',
      icon: '🌤️'
    },
    currentBanner: 0,
    location: DEFAULT_LOCATION
  },

  onLoad: function() {
    this.getUserInfo();
    this.loadData();
  },

  onShow: function() {
    this.getUserInfo();
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 0
      });
    }
    MedicationReminder.checkAndRemind();
  },

  getUserInfo: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({ userInfo });
    }
  },

  loadData: function() {
    this.getLocationAndWeather();
    this.loadNews();
  },

  getLocationAndWeather: function() {
    const that = this;
    
    wx.getLocation({
      type: 'wgs84',
      success: function(res) {
        const location = res.longitude + ',' + res.latitude;
        that.setData({ location: location });
        that.loadWeather(location);
        that.reverseGeocode(res.latitude, res.longitude);
      },
      fail: function(err) {
        console.error('获取位置失败，使用默认位置', err);
        that.setData({
          location: DEFAULT_LOCATION,
          'weather.city': DEFAULT_CITY
        });
        that.loadWeather(DEFAULT_LOCATION);
      }
    });
  },

  reverseGeocode: function(lat, lng) {
    const that = this;
    wx.request({
      url: 'https://apis.map.qq.com/ws/geocoder/v1/',
      data: {
        location: lat + ',' + lng,
        key: 'your-tencent-map-key'
      },
      success: function(res) {
        if (res.data && res.data.result) {
          const address = res.data.result.address_component;
          const city = address.city || address.district || DEFAULT_CITY;
          that.setData({
            'weather.city': city
          });
        }
      },
      fail: function() {
        that.setData({
          'weather.city': DEFAULT_CITY
        });
      }
    });
  },

  refreshLocation: function() {
    this.setData({
      'weather.weather': '加载中...',
      'weather.city': DEFAULT_CITY
    });
    this.getLocationAndWeather();
  },

  loadWeather: function(location) {
    const loc = location || this.data.location || DEFAULT_LOCATION;
    
    API.getWeatherNow(loc).then(data => {
      if (data && data.success) {
        this.setData({
          weather: {
            temp: data.temp || '--',
            weather: data.text || '--',
            city: this.data.weather.city,
            humidity: data.humidity || '--',
            wind: data.windDir ? data.windDir + ' ' + (data.windScale || '') + '级' : '--',
            icon: data.icon || '🌤️'
          }
        });
      } else {
        this.setData({
          'weather.weather': '获取失败'
        });
      }
    }).catch(err => {
      console.error('加载天气失败', err);
      this.setData({
        'weather.weather': '加载失败'
      });
    });
  },

  loadNews: function() {
    API.getHotNews(3).then(data => {
      this.setData({
        hotNews: (data || []).map(item => ({
          id: item.id,
          title: item.title,
          summary: item.content ? item.content.substring(0, 30) + '...' : '',
          createTime: item.createTime ? item.createTime.substring(0, 10) : '',
          readCount: item.viewCount || 0
        }))
      });
    }).catch(() => {
      this.setData({
        hotNews: []
      });
    });
  },

  onBannerChange: function(e) {
    this.setData({
      currentBanner: e.detail.current
    });
  },

  goToFunction: function(e) {
    const { url } = e.currentTarget.dataset;
    const tabBarPages = ['/pages/service/service', '/pages/news/news', '/pages/mine/mine', '/pages/index/index'];
    if (tabBarPages.includes(url)) {
      wx.switchTab({ url });
    } else {
      wx.navigateTo({ url });
    }
  },

  goToNews: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/news/detail?id=' + id });
  },

  goToEmergency: function() {
    wx.navigateTo({ url: '/pages/emergency/emergency' });
  },

  goToWeather: function() {
    const location = this.data.location || DEFAULT_LOCATION;
    wx.navigateTo({ url: '/pages/weather/weather?location=' + location + '&city=' + this.data.weather.city });
  },

  onShareAppMessage: function() {
    return {
      title: '智慧养老服务平台',
      path: '/pages/index/index'
    };
  }
});
