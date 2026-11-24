const API = require('../../utils/api');

Page({
  data: {
    editMode: false,
    addressId: null,
    form: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      label: '家',
      isDefault: 0
    },
    labelOptions: ['家', '公司', '其他'],
    labelIndex: 0,
    region: []
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ editMode: true, addressId: options.id });
      this.loadAddress(options.id);
    }
  },

  loadAddress: function(id) {
    API.getAddressDetail(id).then(address => {
      if (address) {
        const labelIndex = this.data.labelOptions.indexOf(address.label || '家');
        this.setData({
          form: address,
          labelIndex: labelIndex >= 0 ? labelIndex : 0,
          region: [address.province, address.city, address.district]
        });
      }
    }).catch(err => {
      console.error('加载地址失败', err);
    });
  },

  onInputChange: function(e) {
    const { field } = e.currentTarget.dataset;
    const { value } = e.detail;
    this.setData({
      [`form.${field}`]: value
    });
  },

  onLabelChange: function(e) {
    const index = e.detail.value;
    this.setData({
      labelIndex: index,
      'form.label': this.data.labelOptions[index]
    });
  },

  onRegionChange: function(e) {
    const region = e.detail.value;
    this.setData({
      region: region,
      'form.province': region[0],
      'form.city': region[1],
      'form.district': region[2]
    });
  },

  onDefaultChange: function(e) {
    this.setData({
      'form.isDefault': e.detail.value ? 1 : 0
    });
  },

  chooseLocation: function() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          'form.detail': res.address + ' ' + res.name
        });
      },
      fail: () => {}
    });
  },

  submitForm: function() {
    const { form, editMode, addressId } = this.data;
    
    if (!form.name) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' });
      return;
    }
    if (!form.phone) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' });
      return;
    }
    if (!/^1[3-9]\d{9}$/.test(form.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    if (!form.detail) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    const data = {
      userId: userInfo.id,
      name: form.name,
      phone: form.phone,
      province: form.province || '',
      city: form.city || '',
      district: form.district || '',
      detail: form.detail,
      label: form.label,
      isDefault: form.isDefault
    };

    if (editMode && addressId) {
      data.id = addressId;
      API.updateAddress(data).then(() => {
        wx.showToast({ title: '修改成功', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1500);
      }).catch(err => {
        wx.showToast({ title: '修改失败', icon: 'none' });
      });
    } else {
      API.addAddress(data).then(() => {
        wx.showToast({ title: '添加成功', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1500);
      }).catch(err => {
        wx.showToast({ title: '添加失败', icon: 'none' });
      });
    }
  }
});
