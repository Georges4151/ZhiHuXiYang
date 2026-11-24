const API = require('../../utils/api');

Page({
  data: {
    posts: [],
    page: 1,
    size: 10,
    category: 0,
    hasMore: true,
    loading: false,
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '生活分享' },
      { id: 2, name: '健康养生' },
      { id: 3, name: '兴趣爱好' }
    ]
  },

  onLoad() {
    this.loadPosts();
  },

  onShow() {
    // 页面显示时刷新列表
    this.setData({ page: 1, posts: [] });
    this.loadPosts();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, posts: [] });
    this.loadPosts().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadPosts();
    }
  },

  async loadPosts() {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: API.BASE_URL + '/social/posts',
          method: 'GET',
          data: {
            category: this.data.category,
            page: this.data.page,
            size: this.data.size
          },
          success: resolve,
          fail: reject
        });
      });
      
      if (res.data.code === 200) {
        const posts = res.data.data || [];
        console.log('加载到的帖子数量:', posts.length);
        console.log('帖子数据:', posts);
        
        // 处理数据，添加分类名称和图片完整路径
        const processedPosts = posts.map(post => {
          // 处理图片路径，确保完整URL
          let images = post.images || '';
          const baseUrl = API.getBaseUrl();
          
          // 过滤空字符串
          if (images) {
            const imageArray = images.split(',').filter(img => img && img.trim() !== '');
            
            // 处理每个图片路径
            const processedImages = imageArray.map(img => {
              img = img.trim();
              if (img.startsWith('/upload/')) {
                return baseUrl + img;
              }
              return img;
            });
            
            images = processedImages.join(',');
          }
          
          // 处理用户头像路径
          let userAvatar = post.userAvatar;
          if (userAvatar && userAvatar.startsWith('/upload/')) {
            userAvatar = baseUrl + userAvatar;
          } else if (!userAvatar || userAvatar === 'null') {
            userAvatar = '/images/icon/user.png';
          }
          
          // 处理用户名
          let userName = post.userName;
          if (!userName || userName === 'null') {
            userName = '匿名用户';
          }
          
          return {
            ...post,
            images: images,
            userAvatar: userAvatar,
            userName: userName,
            categoryName: this.getCategoryName(post.category)
          };
        });
        
        this.setData({
          posts: this.data.page === 1 ? processedPosts : [...this.data.posts, ...processedPosts],
          hasMore: posts.length === this.data.size,
          page: this.data.page + 1
        });
      } else {
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('加载动态失败:', error);
      wx.showToast({
        title: '网络错误',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  getCategoryName(categoryId) {
    const names = {
      1: '生活分享',
      2: '健康养生',
      3: '兴趣爱好'
    };
    return names[categoryId] || '其他';
  },

  onCategoryChange(e) {
    const category = parseInt(e.currentTarget.dataset.id);
    this.setData({ 
      category: category, 
      page: 1, 
      posts: [],
      hasMore: true
    });
    this.loadPosts();
  },

  onLike(e) {
    const postId = e.currentTarget.dataset.id;
    const index = e.currentTarget.dataset.index;
    const post = this.data.posts[index];
    
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    wx.request({
      url: API.BASE_URL + '/social/posts/' + postId + '/like',
      method: 'POST',
      data: {
        userId: userInfo.id
      },
      success: (res) => {
        if (res.data.code === 200) {
          const posts = this.data.posts;
          posts[index].isLiked = !posts[index].isLiked;
          posts[index].likes = posts[index].isLiked ? 
            (posts[index].likes || 0) + 1 : 
            (posts[index].likes || 0) - 1;
          this.setData({ posts });
          
          wx.showToast({
            title: posts[index].isLiked ? '点赞成功' : '已取消点赞',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        });
      }
    });
  },

  onComment(e) {
    const postId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/social/detail?id=' + postId
    });
  },



  onPublish() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再发布动态',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/auth/login'
            });
          }
        }
      });
      return;
    }
    
    wx.navigateTo({
      url: '/pages/social/publish'
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

  // 分享给朋友
  onShareAppMessage(res) {
    // 判断是否是从按钮分享
    if (res.from === 'button') {
      // 获取按钮绑定的动态数据
      const postId = res.target.dataset.id;
      const content = res.target.dataset.content;
      return {
        title: content ? content.substring(0, 30) + '...' : '社区动态',
        path: '/pages/social/detail?id=' + postId,
        imageUrl: '/images/share-social.png'
      };
    }
    // 默认分享整个页面
    return {
      title: '智慧养老社区 - 老年朋友圈',
      path: '/pages/social/social',
      imageUrl: '/images/share-social.png'
    };
  },

  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '智慧养老社区 - 老年朋友圈',
      query: '',
      imageUrl: '/images/share-social.png'
    };
  }
});
