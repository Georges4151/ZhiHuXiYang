const API = require('../../utils/api');

const DEFAULT_LOCATION = '117.33,38.37';
const DEFAULT_CITY = '黄骅市';

Page({
  data: {
    currentWeather: {
      icon: '🌤️',
      temp: '--',
      text: '加载中...',
      city: DEFAULT_CITY,
      humidity: '--',
      windDir: '--',
      windScale: '--',
      feelsLike: '--',
      visibility: '--'
    },
    forecastList: [],
    lifeIndex: [],
    airQuality: {
      aqi: '--',
      category: '--',
      level: 'good',
      pm2p5: '--',
      pm10: '--',
      o3: '--',
      no2: '--'
    },
    loading: true,
    location: DEFAULT_LOCATION
  },

  onLoad: function(options) {
    if (options.location) {
      this.setData({ location: options.location });
      if (options.city) {
        this.setData({ 'currentWeather.city': decodeURIComponent(options.city) });
      }
      this.loadWeatherData(options.location);
    } else {
      this.getLocationAndWeather();
    }
  },

  getLocationAndWeather: function() {
    const that = this;
    this.setData({ loading: true, 'currentWeather.text': '加载中...' });
    
    wx.getLocation({
      type: 'wgs84',
      success: function(res) {
        const location = res.longitude + ',' + res.latitude;
        that.setData({ location: location });
        that.loadWeatherData(location);
        that.reverseGeocode(res.latitude, res.longitude);
      },
      fail: function(err) {
        console.error('获取位置失败，使用默认位置', err);
        that.setData({
          location: DEFAULT_LOCATION,
          'currentWeather.city': DEFAULT_CITY
        });
        that.loadWeatherData(DEFAULT_LOCATION);
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
            'currentWeather.city': city
          });
        }
      },
      fail: function() {
        that.setData({
          'currentWeather.city': DEFAULT_CITY
        });
      }
    });
  },

  refreshLocation: function() {
    this.getLocationAndWeather();
  },

  onPullDownRefresh: function() {
    const location = this.data.location || DEFAULT_LOCATION;
    this.loadWeatherData(location).then(() => {
      wx.stopPullDownRefresh();
    }).catch(() => {
      wx.stopPullDownRefresh();
    });
  },

  loadWeatherData: function(location) {
    this.setData({ loading: true });
    const loc = location || this.data.location || DEFAULT_LOCATION;

    return API.getWeatherAll(loc).then(data => {
      console.log('天气数据', data);
      
      if (data) {
        if (data.now && data.now.success) {
          const now = data.now;
          this.setData({
            currentWeather: {
              icon: now.icon || '🌤️',
              temp: now.temp || '--',
              text: now.text || '--',
              city: this.data.currentWeather.city,
              humidity: now.humidity || '--',
              windDir: now.windDir || '--',
              windScale: now.windScale || '--',
              feelsLike: now.feelsLike || '--',
              visibility: now.visibility || '--'
            }
          });
        }

        if (data.forecast && data.forecast.length > 0) {
          const forecastList = data.forecast.map((item, index) => ({
            date: index === 0 ? '今天' : index === 1 ? '明天' : this.formatDate(item.fxDate),
            icon: this.getWeatherIcon(item.iconDay),
            status: item.textDay,
            high: item.tempMax,
            low: item.tempMin
          }));
          this.setData({ forecastList });
        }

        if (data.air && data.air.success) {
          const air = data.air;
          this.setData({
            airQuality: {
              aqi: air.aqi || '--',
              category: air.category || '--',
              level: this.getAqiLevel(air.aqi),
              pm2p5: air.pm2p5 || '--',
              pm10: air.pm10 || '--',
              o3: air.o3 || '--',
              no2: air.no2 || '--'
            }
          });
        }

        if (data.indices && data.indices.length > 0) {
          const lifeIndex = data.indices.map(item => ({
            id: item.type,
            icon: this.getIndexIcon(item.type),
            name: item.name,
            value: item.category,
            desc: item.text
          }));
          this.setData({ lifeIndex });
        }
      }

      this.setData({ loading: false });
    }).catch(err => {
      console.error('加载天气失败', err);
      this.setData({ 
        loading: false,
        'currentWeather.text': '加载失败'
      });
      wx.showToast({ title: '加载天气失败', icon: 'none' });
    });
  },

  formatDate: function(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    return weekDays[date.getDay()];
  },

  getWeatherIcon: function(code) {
    const iconMap = {
      '100': '☀️', '101': '🌤️', '102': '⛅', '103': '🌥️', '104': '☁️',
      '150': '☀️', '151': '🌤️',
      '300': '🌦️', '301': '🌧️', '302': '⛈️', '303': '⛈️', '304': '⛈️',
      '305': '🌧️', '306': '🌧️', '307': '🌧️', '308': '🌧️', '309': '🌧️',
      '310': '🌧️', '311': '🌧️', '312': '🌧️', '313': '🌧️', '314': '🌧️',
      '315': '🌧️', '316': '🌧️', '317': '🌧️', '318': '🌧️',
      '400': '🌨️', '401': '🌨️', '402': '🌨️', '403': '🌨️', '404': '🌨️',
      '405': '🌨️', '406': '🌨️', '407': '🌨️', '408': '🌨️', '409': '🌨️',
      '500': '🌫️', '501': '🌫️', '502': '🌫️', '503': '🌫️', '504': '🌫️',
      '507': '🌫️', '508': '🌫️', '509': '🌫️', '510': '🌫️', '511': '🌫️',
      '800': '☀️', '801': '🌤️', '802': '⛅', '803': '🌥️', '804': '☁️'
    };
    return iconMap[code] || '🌤️';
  },

  getAqiLevel: function(aqi) {
    const val = parseInt(aqi);
    if (isNaN(val)) return 'good';
    if (val <= 50) return 'excellent';
    if (val <= 100) return 'good';
    if (val <= 150) return 'moderate';
    return 'poor';
  },

  getIndexIcon: function(type) {
    const iconMap = {
      '1': '👔', '2': '🏃', '3': '🚗', '5': '☂️', '6': '😷', '8': '🌞'
    };
    return iconMap[type] || '📋';
  }
});
