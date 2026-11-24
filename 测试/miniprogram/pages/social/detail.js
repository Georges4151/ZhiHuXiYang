const API = require('../../utils/api');

Page({
  data: {
    postId: null,
    post: {},
    comments: [],
    commentContent: '',
    loading: false,
    submitting: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ postId: options.id });
      this.loadPostDetail(options.id);
      this.loadComments(options.id);
    }
  },

  loadPostDetail(postId) {
    this.setData({ loading: true });

    wx.request({
      url: API.BASE_URL + '/social/posts/' + postId,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const post = res.data.data;
          let images = post.images || '';
          const baseUrl = API.getBaseUrl();
          if (images) {
            const imageArray = images.split(',').filter(img => img && img.trim() !== '');
            const processedImages = imageArray.map(img => {
              img = img.trim();
              if (img.startsWith('/upload/')) {
                return baseUrl + img;
              }
              return img;
            });
            images = processedImages.join(',');
          }
          let userAvatar = post.userAvatar;
          if (userAvatar && userAvatar.startsWith('/upload/')) {
            userAvatar = baseUrl + userAvatar;
          } else if (!userAvatar || userAvatar === 'null') {
            userAvatar = '/images/icon/user.png';
          }
          this.setData({
            post: {
              ...post,
              images: images,
              userAvatar: userAvatar,
              categoryName: this.getCategoryName(post.category)
            }
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },

  loadComments(postId) {
    wx.request({
      url: API.BASE_URL + '/social/posts/' + postId + '/comments',
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            comments: res.data.data || []
          });
        }
      }
    });
  },

  getCategoryName(categoryId) {
    const names = {
      1: '生活分享',
      2: '健康养生',
      3: '兴趣爱好'
    };
    return names[categoryId] || '其他';
  },

  onLike() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    const post = this.data.post;
    wx.request({
      url: API.BASE_URL + '/social/posts/' + post.id + '/like',
      method: 'POST',
      data: { userId: userInfo.id },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            'post.isLiked': !post.isLiked,
            'post.likes': post.isLiked ? (post.likes || 0) - 1 : (post.likes || 0) + 1
          });
          wx.showToast({
            title: this.data.post.isLiked ? '点赞成功' : '已取消点赞',
            icon: 'none'
          });
        }
      }
    });
  },

  onCommentInput(e) {
    this.setData({
      commentContent: e.detail.value
    });
  },

  submitComment() {
    const { commentContent, postId } = this.data;
    
    if (!commentContent.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    wx.request({
      url: API.BASE_URL + '/social/posts/' + postId + '/comments',
      method: 'POST',
      data: {
        userId: userInfo.id,
        userName: userInfo.name || userInfo.nickName || '匿名用户',
        userAvatar: userInfo.avatarUrl || '',
        content: commentContent.trim()
      },
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({
            title: '评论成功',
            icon: 'success'
          });
          this.setData({
            commentContent: ''
          });
          this.loadComments(postId);
          // 更新评论数
          this.setData({
            'post.comments': (this.data.post.comments || 0) + 1
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '评论失败',
          icon: 'none'
        });
      },
      complete: () => {
        this.setData({ submitting: false });
      }
    });
  },

  onImagePreview(e) {
    const urls = e.currentTarget.dataset.urls;
    const current = e.currentTarget.dataset.current;
    wx.previewImage({
      urls: urls,
      current: current
    });
  },

  onShareAppMessage() {
    const { post } = this.data;
    return {
      title: post.content ? post.content.substring(0, 30) + '...' : '社区动态',
      path: '/pages/social/detail?id=' + post.id
    };
  }
});
