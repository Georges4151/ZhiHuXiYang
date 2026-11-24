Page({
  data: {
    helpList: [
      {
        id: 1,
        title: '智能咨询',
        icon: '🤖',
        items: [
          { question: '如何使用智能咨询？', answer: '点击首页"智能咨询"按钮，进入对话页面，输入您的问题，AI助手会为您解答养老政策、健康管理、生活服务等问题。' },
          { question: '可以问哪些问题？', answer: '可以咨询养老政策、医保报销、健康养生、生活服务、紧急救助等各类问题，AI助手都会尽力为您解答。' }
        ]
      },
      {
        id: 2,
        title: '健康管理',
        icon: '💊',
        items: [
          { question: '如何记录健康数据？', answer: '点击"健康管理"，选择"添加数据"，填写血压、血糖、心率等数据后保存。系统会自动记录并生成健康趋势图。' },
          { question: '如何查看健康报告？', answer: '在健康管理页面，点击"数据统计"可以查看血压、血糖、心率等数据的趋势图表和健康建议。' }
        ]
      },
      {
        id: 3,
        title: '生活服务',
        icon: '🏠',
        items: [
          { question: '如何预约服务？', answer: '点击"生活服务"，浏览服务列表，选择需要的服务（如家政、陪护、送餐等），点击"立即预约"，填写预约信息后提交。' },
          { question: '如何查看我的订单？', answer: '点击"我的订单"，可以查看所有订单的状态，包括待确认、进行中、已完成等。' }
        ]
      },
      {
        id: 4,
        title: '紧急呼叫',
        icon: '🆘',
        items: [
          { question: '如何使用紧急呼叫？', answer: '遇到紧急情况时，点击首页红色的"一键呼救"按钮，系统会立即通知您的紧急联系人和社区服务人员。' },
          { question: '如何设置紧急联系人？', answer: '在"我的"页面，点击"紧急联系人"，添加家属或朋友的联系方式，紧急呼叫时会优先通知他们。' }
        ]
      },
      {
        id: 5,
        title: '用药提醒',
        icon: '⏰',
        items: [
          { question: '如何设置用药提醒？', answer: '点击"用药提醒"，点击"添加提醒"，输入药品名称、服用剂量、提醒时间等信息，系统会按时提醒您服药。' },
          { question: '如何修改或删除提醒？', answer: '在用药提醒列表中，点击对应的提醒项，可以选择编辑或删除。' }
        ]
      },
      {
        id: 6,
        title: '社区活动',
        icon: '🎉',
        items: [
          { question: '如何查看社区活动？', answer: '点击"社区活动"，可以浏览当前可报名的活动列表，包括健康讲座、文娱活动、志愿服务等。' },
          { question: '如何报名参加活动？', answer: '点击感兴趣的活动，查看活动详情，点击"立即报名"按钮即可参加。如需取消，点击"取消报名"即可。' }
        ]
      },
      {
        id: 7,
        title: '家属管理',
        icon: '👥',
        items: [
          { question: '如何添加家属？', answer: '点击"我的家属"，点击"添加家属"，填写家属姓名、手机号等信息，家属确认后即可绑定。' },
          { question: '家属能看到我的信息吗？', answer: '绑定后，家属可以查看您的健康数据、服务订单等信息，方便他们关心您的身体状况。' }
        ]
      },
      {
        id: 8,
        title: '数据统计',
        icon: '📊',
        items: [
          { question: '数据统计有什么用？', answer: '数据统计可以查看您的健康趋势、服务订单统计等信息，帮助您和家属了解您的健康状况和服务使用情况。' },
          { question: '如何查看数据统计？', answer: '点击"我的"，选择"数据统计"，可以查看健康数据趋势图、订单统计、系统概览等信息。' }
        ]
      }
    ],
    expandedId: null
  },

  onLoad: function() {},

  onShow: function() {},

  toggleSection: function(e) {
    const { id } = e.currentTarget.dataset;
    const { expandedId } = this.data;
    
    this.setData({
      expandedId: expandedId === id ? null : id
    });
  },

  contactService: function() {
    wx.makePhoneCall({
      phoneNumber: '12349',
      fail: () => {
        wx.showToast({ title: '拨打失败', icon: 'none' });
      }
    });
  },

  onShareAppMessage: function() {
    return {
      title: '使用帮助 - 智慧养老',
      path: '/pages/help/help'
    };
  }
});
