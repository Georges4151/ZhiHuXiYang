const API = require('../../utils/api');

Page({
  data: {
    content: '',
    contact: '',
    feedbackList: [],
    loading: false
  },

  onLoad: function() {
    this.loadFeedbackList();
  },

  onShow: function() {
    this.loadFeedbackList();
  },

  loadFeedbackList: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) return;

    API.getFeedbackList(userInfo.id).then(list => {
      this.setData({ feedbackList: list || [] });
    }).catch(err => {
      console.error('加载反馈列表失败', err);
    });
  },

  onContentChange: function(e) {
    this.setData({ content: e.detail.value });
  },

  onContactChange: function(e) {
    this.setData({ contact: e.detail.value });
  },

  submitFeedback: function() {
    const { content, contact } = this.data;
    
    if (!content || content.trim().length === 0) {
      wx.showToast({ title: '请输入反馈内容', icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    const data = {
      userId: userInfo.id,
      content: content.trim(),
      contact: contact.trim()
    };

    this.setData({ loading: true });
    API.addFeedback(data).then(() => {
      this.setData({ 
        content: '', 
        contact: '',
        loading: false 
      });
      wx.showToast({ title: '提交成功', icon: 'success' });
      this.loadFeedbackList();
    }).catch(err => {
      console.error('提交反馈失败', err);
      this.setData({ loading: false });
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    });
  },

  getStatusText: function(status) {
    const statusMap = { 0: '待处理', 1: '已回复' };
    return statusMap[status] || '待处理';
  }
});
