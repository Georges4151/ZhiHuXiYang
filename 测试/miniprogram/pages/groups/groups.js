const app = getApp();

Page({
  data: {
    groups: [],
    page: 1,
    size: 10,
    category: 0,
    hasMore: true,
    loading: false,
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '书法绘画' },
      { id: 2, name: '音乐舞蹈' },
      { id: 3, name: '运动健身' },
      { id: 4, name: '棋牌游戏' },
      { id: 5, name: '手工制作' }
    ]
  },

  onLoad() {
    this.loadGroups();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, groups: [] });
    this.loadGroups().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadGroups();
    }
  },

  async loadGroups() {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    try {
      const res = await wx.request({
        url: `${app.globalData.baseUrl}/api/groups/list`,
        method: 'GET',
        data: {
          category: this.data.category,
          page: this.data.page,
          size: this.data.size
        }
      });
      
      if (res.data.code === 200) {
        const groups = res.data.data || [];
        this.setData({
          groups: this.data.page === 1 ? groups : [...this.data.groups, ...groups],
          hasMore: groups.length === this.data.size,
          page: this.data.page + 1
        });
      }
    } catch (error) {
      console.error('加载小组失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  onCategoryChange(e) {
    const category = e.currentTarget.dataset.id;
    this.setData({ category, page: 1, groups: [] });
    this.loadGroups();
  },

  onGroupDetail(e) {
    const groupId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/groups/detail?id=${groupId}`
    });
  },

  onJoinGroup(e) {
    const groupId = e.currentTarget.dataset.id;
    const index = e.currentTarget.dataset.index;
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/groups/${groupId}/join`,
      method: 'POST',
      data: {
        userId: app.globalData.userInfo.id,
        userName: app.globalData.userInfo.name,
        userAvatar: app.globalData.userInfo.avatar
      },
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({ title: '加入成功', icon: 'success' });
          const groups = this.data.groups;
          groups[index].isMember = true;
          groups[index].memberCount += 1;
          this.setData({ groups });
        } else {
          wx.showToast({ title: res.data.message || '加入失败', icon: 'none' });
        }
      }
    });
  },

  onCreateGroup() {
    wx.navigateTo({
      url: '/pages/groups/create'
    });
  }
});
