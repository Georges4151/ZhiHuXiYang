const API = require('../../utils/api');

Page({
  data: {
    activity: null,
    comments: [],
    isLiked: false,
    isJoined: false,
    showCommentInput: false,
    commentContent: '',
    loading: true
  },

  onLoad: function(options) {
    const { id } = options;
    if (id) {
      this.loadActivityDetail(id);
      this.loadComments(id);
    }
  },

  loadActivityDetail: function(id) {
    API.getActivityDetail(id).then(activity => {
      // 处理活动详情数据
      this.setData({
        activity: {
          id: activity.id,
          title: activity.title,
          description: activity.description || activity.content || '暂无活动详情',
          coverImage: activity.coverImage || '',
          category: activity.category,
          categoryName: activity.categoryName || '其他',
          activityTime: activity.activityTime || activity.startTime,
          endTime: activity.endTime,
          location: activity.location || '待定',
          address: activity.address || '',
          maxParticipants: activity.maxParticipants || 0,
          currentParticipants: activity.currentParticipants || 0,
          status: activity.status,
          likes: activity.likes || 0,
          organizer: activity.organizer || '社区服务中心',
          contactPhone: activity.contactPhone || '',
          tips: activity.tips ? activity.tips.split('\n').filter(tip => tip.trim()) : []
        },
        loading: false
      });
    }).catch(err => {
      console.error('加载活动详情失败', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
      this.setData({ loading: false });
    });
  },

  loadComments: function(activityId) {
    API.getActivityComments(activityId).then(comments => {
      this.setData({ 
        comments: (comments || []).map(comment => ({
          id: comment.id,
          userId: comment.userId,
          userName: comment.userName || '匿名用户',
          userAvatar: comment.userAvatar || '',
          content: comment.content,
          createTime: comment.createTime,
          likes: comment.likes || 0,
          isLiked: comment.isLiked || false,
          replies: comment.replies || []
        }))
      });
    }).catch(err => {
      console.error('加载评论失败', err);
      this.setData({ comments: [] });
    });
  },

  toggleLike: function() {
    const { isLiked, activity } = this.data;
    
    API.likeActivity(activity.id).then(() => {
      this.setData({
        isLiked: !isLiked,
        'activity.likes': isLiked ? activity.likes - 1 : activity.likes + 1
      });
      
      wx.showToast({
        title: isLiked ? '已取消点赞' : '点赞成功',
        icon: 'success'
      });
    }).catch(err => {
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      });
    });
  },

  joinActivity: function() {
    const { isJoined, activity } = this.data;
    
    if (isJoined) {
      wx.showModal({
        title: '取消报名',
        content: '确定要取消报名吗？',
        success: (res) => {
          if (res.confirm) {
            API.cancelJoin(activity.id).then(() => {
              this.setData({
                isJoined: false,
                'activity.currentParticipants': activity.currentParticipants - 1
              });
              wx.showToast({ title: '已取消报名', icon: 'success' });
            }).catch(err => {
              wx.showToast({ title: '取消失败', icon: 'none' });
            });
          }
        }
      });
    } else {
      if (activity.currentParticipants >= activity.maxParticipants) {
        wx.showToast({ title: '名额已满', icon: 'none' });
        return;
      }
      
      API.joinActivity(activity.id).then(() => {
        this.setData({
          isJoined: true,
          'activity.currentParticipants': activity.currentParticipants + 1
        });
        wx.showToast({ title: '报名成功', icon: 'success' });
      }).catch(err => {
        wx.showToast({ title: err.message || '报名失败', icon: 'none' });
      });
    }
  },

  showCommentInput: function() {
    this.setData({ showCommentInput: true });
  },

  hideCommentInput: function() {
    this.setData({ showCommentInput: false, commentContent: '' });
  },

  onCommentInput: function(e) {
    this.setData({ commentContent: e.detail.value });
  },

  submitComment: function() {
    const { commentContent, activity, comments } = this.data;
    
    if (!commentContent.trim()) {
      wx.showToast({ title: '请输入评论内容', icon: 'none' });
      return;
    }

    const data = {
      activityId: activity.id,
      content: commentContent.trim()
    };

    API.addActivityComment(data).then(() => {
      // 重新加载评论列表
      this.loadComments(activity.id);
      this.setData({
        commentContent: '',
        showCommentInput: false
      });
      wx.showToast({ title: '评论成功', icon: 'success' });
    }).catch(err => {
      wx.showToast({ title: '评论失败', icon: 'none' });
    });
  },

  likeComment: function(e) {
    const { id } = e.currentTarget.dataset;
    const comments = this.data.comments.map(item => {
      if (item.id === id) {
        item.isLiked = !item.isLiked;
        item.likes = item.isLiked ? item.likes + 1 : item.likes - 1;
      }
      return item;
    });
    this.setData({ comments });
  },

  openLocation: function() {
    const { activity } = this.data;
    if (activity && activity.address) {
      wx.openLocation({
        latitude: 0,
        longitude: 0,
        name: activity.location,
        address: activity.address
      });
    }
  },

  makePhoneCall: function() {
    const { activity } = this.data;
    if (activity && activity.contactPhone) {
      wx.makePhoneCall({
        phoneNumber: activity.contactPhone
      });
    }
  },

  goToDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: '/pages/activity/detail?id=' + id
    });
  },

  onShareAppMessage: function() {
    const { activity } = this.data;
    return {
      title: activity.title,
      path: '/pages/activity/detail?id=' + activity.id
    };
  }
});
