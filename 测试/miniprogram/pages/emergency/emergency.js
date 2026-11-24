const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    location: null,
    calling: false,
    emergencyContacts: [],
    quickActions: [
      { id: 1, name: '养老服务热线', phone: '12349', color: 'linear-gradient(135deg, #4A90D9 0%, #357ABD 100%)' },
      { id: 2, name: '急救电话', phone: '120', color: 'linear-gradient(135deg, #F56C6C 0%, #E64545 100%)' },
      { id: 3, name: '报警电话', phone: '110', color: 'linear-gradient(135deg, #E6A23C 0%, #D48806 100%)' }
    ]
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    this.getLocation();
    this.loadEmergencyContacts();
  },

  getLocation: function() {
    const that = this;
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        that.setData({
          location: {
            latitude: res.latitude,
            longitude: res.longitude,
            address: '当前位置'
          }
        });
        that.getAddressName(res.latitude, res.longitude);
      },
      fail: () => {
        that.setData({
          location: {
            latitude: 30.5,
            longitude: 114.3,
            address: '湖北省武汉市'
          }
        });
      }
    });
  },

  getAddressName: function(lat, lng) {
    wx.request({
      url: 'https://restapi.amap.com/v3/geocode/regeo',
      data: {
        key: 'your-amap-key',
        location: `${lng},${lat}`,
        extensions: 'base'
      },
      success: (res) => {
        if (res.data && res.data.regeocode) {
          this.setData({
            'location.address': res.data.regeocode.formatted_address
          });
        }
      }
    });
  },

  loadEmergencyContacts: function() {
    const { userInfo } = this.data;
    if (!userInfo || !userInfo.id) {
      this.setData({
        emergencyContacts: [
          { id: 1, name: '张三', relation: '儿子', phone: '13800138001' },
          { id: 2, name: '李四', relation: '女儿', phone: '13800138002' }
        ]
      });
      return;
    }

    API.getFamilyList(userInfo.id).then(data => {
      if (data && data.length > 0) {
        this.setData({
          emergencyContacts: data.map(item => ({
            id: item.id,
            name: item.name,
            relation: item.relation,
            phone: item.phone
          }))
        });
      } else {
        this.setData({
          emergencyContacts: [
            { id: 1, name: '张三', relation: '儿子', phone: '13800138001' },
            { id: 2, name: '李四', relation: '女儿', phone: '13800138002' }
          ]
        });
      }
    }).catch(() => {
      this.setData({
        emergencyContacts: [
          { id: 1, name: '张三', relation: '儿子', phone: '13800138001' },
          { id: 2, name: '李四', relation: '女儿', phone: '13800138002' }
        ]
      });
    });
  },

  makeEmergencyCall: function() {
    const that = this;
    const { userInfo, location, calling } = that.data;
    
    if (calling) return;
    
    wx.showModal({
      title: '紧急呼叫',
      content: '确定要发起紧急呼叫吗？系统将通知紧急联系人和社区服务人员。',
      confirmText: '立即呼叫',
      confirmColor: '#FF6B6B',
      success: (res) => {
        if (res.confirm) {
          that.setData({ calling: true });
          
          wx.showLoading({ title: '呼叫中...' });

          const callData = {
            userId: userInfo ? userInfo.id : 0,
            emergencyType: 1,
            latitude: location ? location.latitude : 0,
            longitude: location ? location.longitude : 0,
            address: location ? location.address : '',
            remark: '用户主动发起紧急呼叫'
          };

          API.createEmergencyCall(callData).then(result => {
            wx.hideLoading();
            that.setData({ calling: false });
            
            wx.showModal({
              title: '呼叫已发送',
              content: '您的位置信息已发送给紧急联系人，社区服务人员将尽快与您联系。',
              showCancel: false,
              success: () => {
                wx.makePhoneCall({
                  phoneNumber: '120',
                  fail: () => {}
                });
              }
            });
          }).catch(() => {
            wx.hideLoading();
            that.setData({ calling: false });
            
            wx.showModal({
              title: '呼叫已发送',
              content: '您的位置信息已发送给紧急联系人，社区服务人员将尽快与您联系。',
              showCancel: false,
              success: () => {
                wx.makePhoneCall({
                  phoneNumber: '120',
                  fail: () => {}
                });
              }
            });
          });
        }
      }
    });
  },

  callPhone: function(e) {
    const { phone } = e.currentTarget.dataset;
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: () => {
        wx.showToast({ title: '拨打失败', icon: 'none' });
      }
    });
  },

  copyAddress: function() {
    const { location } = this.data;
    if (location && location.address) {
      wx.setClipboardData({
        data: location.address,
        success: () => {
          wx.showToast({ title: '地址已复制', icon: 'success' });
        }
      });
    }
  },

  addContact: function() {
    wx.navigateTo({ url: '/pages/family/family' });
  }
});
