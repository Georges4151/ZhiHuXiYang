const API = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    inputValue: '',
    messages: [],
    isTyping: false,
    scrollTop: 0
  },

  onLoad: function() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
    this.initWelcome();
  },

  initWelcome: function() {
    const welcomeMsg = {
      id: Date.now(),
      role: 'assistant',
      content: '您好！我是智慧养老助手，可以为您解答养老政策、健康知识、生活服务等方面的问题。请问有什么可以帮您的？',
      time: this.formatTime(new Date())
    };
    this.setData({
      messages: [welcomeMsg]
    });
  },

  onInputChange: function(e) {
    this.setData({
      inputValue: e.detail.value
    });
  },

  sendMessage: async function() {
    const { inputValue, messages, userInfo, isTyping } = this.data;
    
    if (!inputValue.trim() || isTyping) {
      return;
    }

    const userMsg = {
      id: Date.now(),
      role: 'user',
      content: inputValue,
      time: this.formatTime(new Date())
    };

    this.setData({
      messages: [...messages, userMsg],
      inputValue: '',
      isTyping: true
    });

    this.scrollToBottom();

    try {
      const data = await API.chat({
        userId: userInfo.id || 1,
        question: inputValue
      });

      const assistantMsg = {
        id: Date.now() + 1,
        role: 'assistant',
        content: data.answer,
        time: this.formatTime(new Date())
      };

      this.setData({
        messages: [...this.data.messages, assistantMsg],
        isTyping: false
      });

      this.scrollToBottom();
    } catch (e) {
      console.error('发送消息失败', e);
      const errorMsg = {
        id: Date.now() + 1,
        role: 'assistant',
        content: '抱歉，网络出现问题，请稍后再试。',
        time: this.formatTime(new Date())
      };
      this.setData({
        messages: [...this.data.messages, errorMsg],
        isTyping: false
      });
    }
  },

  scrollToBottom: function() {
    wx.createSelectorQuery()
      .select('.message-list')
      .boundingClientRect((rect) => {
        this.setData({
          scrollTop: rect.height + 1000
        });
      })
      .exec();
  },

  formatTime: function(date) {
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    return `${hour}:${minute}`;
  },

  clearChat: function() {
    wx.showModal({
      title: '提示',
      content: '确定要清空聊天记录吗？',
      success: (res) => {
        if (res.confirm) {
          this.initWelcome();
        }
      }
    });
  },

  onQuickQuestion: function(e) {
    const { question } = e.currentTarget.dataset;
    this.setData({ inputValue: question });
    this.sendMessage();
  }
});
