const API = require('../../utils/api');

Page({
  data: {
    healthTags: [
      { name: '高血压', selected: false },
      { name: '糖尿病', selected: false },
      { name: '心脏病', selected: false },
      { name: '关节炎', selected: false },
      { name: '骨质疏松', selected: false },
      { name: '白内障', selected: false },
      { name: '听力下降', selected: false },
      { name: '行动不便', selected: false },
      { name: '失眠', selected: false },
      { name: '慢性支气管炎', selected: false },
      { name: '胃病', selected: false },
      { name: '其他', selected: false }
    ],
    otherCondition: ''
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const healthCondition = userInfo.healthCondition || '';
    
    if (healthCondition) {
      const conditions = healthCondition.split('、').filter(c => c.trim());
      const healthTags = this.data.healthTags.map(tag => {
        if (conditions.includes(tag.name)) {
          return { ...tag, selected: true };
        }
        return tag;
      });
      
      const predefinedTags = healthTags.map(t => t.name);
      const otherConditions = conditions.filter(c => !predefinedTags.includes(c));
      
      this.setData({
        healthTags: healthTags,
        otherCondition: otherConditions.join('、')
      });
    }
  },

  toggleTag: function(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      ['healthTags[' + index + '].selected']: !this.data.healthTags[index].selected
    });
  },

  onOtherInput: function(e) {
    this.setData({
      otherCondition: e.detail.value
    });
  },

  saveHealth: function() {
    const { healthTags, otherCondition } = this.data;
    
    const selectedTags = healthTags.filter(t => t.selected).map(t => t.name);
    const allConditions = [...selectedTags];
    
    if (otherCondition.trim()) {
      allConditions.push(otherCondition.trim());
    }
    
    const healthCondition = allConditions.join('、');
    
    wx.showLoading({ title: '保存中...' });
    
    const userInfo = wx.getStorageSync('userInfo') || {};
    const updatedUser = {
      id: userInfo.id,
      healthCondition: healthCondition
    };
    
    API.updateUser(updatedUser).then(() => {
      wx.hideLoading();
      const newUserInfo = { ...userInfo, ...updatedUser };
      wx.setStorageSync('userInfo', newUserInfo);
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(() => {
      wx.hideLoading();
      const newUserInfo = { ...userInfo, ...updatedUser };
      wx.setStorageSync('userInfo', newUserInfo);
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    });
  }
});
