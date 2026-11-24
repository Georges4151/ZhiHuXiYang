const { get, post } = require('../../utils/request');

Page({
  data: {
    familyList: [],
    showAddModal: false,
    formData: {
      name: '',
      phone: '',
      relation: ''
    },
    relationOptions: ['配偶', '儿子', '女儿', '父亲', '母亲', '其他'],
    relationIndex: 0
  },

  onLoad() {
    this.loadFamilyList();
  },

  onShow() {
    this.loadFamilyList();
  },

  // 加载家属列表
  loadFamilyList() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }

    get(`/family/members/${userInfo.id}`)
      .then(data => {
        this.setData({
          familyList: data || []
        });
      })
      .catch(err => {
        console.error('加载家属列表失败:', err);
      });
  },

  // 显示添加弹窗
  showAddModal() {
    this.setData({
      showAddModal: true,
      formData: {
        name: '',
        phone: '',
        relation: ''
      },
      relationIndex: 0
    });
  },

  // 隐藏弹窗
  hideModal() {
    this.setData({
      showAddModal: false
    });
  },

  // 阻止弹窗内容点击事件冒泡
  onModalContentTap() {
    // 什么都不做，只是阻止事件冒泡
  },

  // 输入姓名
  onNameInput(e) {
    this.setData({
      'formData.name': e.detail.value
    });
  },

  // 输入手机号
  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    });
  },

  // 选择关系
  onRelationChange(e) {
    const index = e.detail.value;
    this.setData({
      relationIndex: index,
      'formData.relation': this.data.relationOptions[index]
    });
  },

  // 提交添加
  submitFamily() {
    const { name, phone, relation } = this.data.formData;
    
    if (!name.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' });
      return;
    }
    if (!phone.trim() || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    if (!relation) {
      wx.showToast({ title: '请选择关系', icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '添加中...' });

    // 手动添加家属
    post('/family/addManual', {
      userId: userInfo.id,
      name: name.trim(),
      phone: phone.trim(),
      relation: relation
    })
      .then(() => {
        wx.hideLoading();
        wx.showToast({ title: '添加成功', icon: 'success' });
        this.hideModal();
        this.loadFamilyList();
      })
      .catch(err => {
        wx.hideLoading();
        wx.showToast({ title: err.message || '添加失败', icon: 'none' });
        console.error('添加家属失败:', err);
      });
  },

  // 拨打电话
  callFamily(e) {
    const phone = e.currentTarget.dataset.phone;
    if (!phone) {
      wx.showToast({ title: '电话号码为空', icon: 'none' });
      return;
    }
    
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: () => {
        wx.showToast({ title: '拨打失败', icon: 'none' });
      }
    });
  },

  // 删除家属
  deleteFamily(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;

    wx.showModal({
      title: '确认删除',
      content: '确定要删除这位家属吗？',
      confirmColor: '#ff4757',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '删除中...' });
          
          post(`/family/unbind/${id}`, {})
            .then(() => {
              wx.hideLoading();
              wx.showToast({ title: '删除成功', icon: 'success' });
              this.loadFamilyList();
            })
            .catch(err => {
              wx.hideLoading();
              wx.showToast({ title: err.message || '删除失败', icon: 'none' });
              console.error('删除家属失败:', err);
            });
        }
      }
    });
  }
});
