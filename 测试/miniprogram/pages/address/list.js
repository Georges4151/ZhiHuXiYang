const API = require('../../utils/api');

Page({
  data: {
    addressList: [],
    loading: false
  },

  onLoad: function() {
    this.loadAddresses();
  },

  onShow: function() {
    this.loadAddresses();
  },

  loadAddresses: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    API.getAddressList(userInfo.id).then(list => {
      this.setData({ 
        addressList: list || [],
        loading: false 
      });
    }).catch(err => {
      console.error('加载地址失败', err);
      this.setData({ loading: false });
    });
  },

  goToAdd: function() {
    wx.navigateTo({ url: '/pages/address/edit' });
  },

  goToEdit: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: '/pages/address/edit?id=' + id });
  },

  setDefault: function(e) {
    const { id } = e.currentTarget.dataset;
    const userInfo = wx.getStorageSync('userInfo');
    
    API.setDefaultAddress(userInfo.id, id).then(() => {
      wx.showToast({ title: '设置成功', icon: 'success' });
      this.loadAddresses();
    }).catch(err => {
      wx.showToast({ title: '设置失败', icon: 'none' });
    });
  },

  deleteAddress: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个地址吗？',
      success: (res) => {
        if (res.confirm) {
          API.deleteAddress(id).then(() => {
            wx.showToast({ title: '删除成功', icon: 'success' });
            this.loadAddresses();
          }).catch(err => {
            wx.showToast({ title: '删除失败', icon: 'none' });
          });
        }
      }
    });
  },

  selectAddress: function(e) {
    const { item } = e.currentTarget.dataset;
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2];
    
    if (prevPage && prevPage.route === 'pages/service/service') {
      prevPage.setData({ selectedAddress: item });
      wx.navigateBack();
    }
  }
});
