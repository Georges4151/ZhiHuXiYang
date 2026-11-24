const API = require('../../utils/api');

Page({
  data: {
    content: '',
    images: [],
    category: 1,
    categories: [
      { id: 1, name: '生活分享' },
      { id: 2, name: '健康养生' },
      { id: 3, name: '兴趣爱好' }
    ],
    submitting: false,
    maxLength: 500
  },

  onLoad() {
    // 检查登录状态
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再发布动态',
        confirmText: '去登录',
        showCancel: false,
        success: () => {
          wx.redirectTo({
            url: '/pages/auth/login'
          });
        }
      });
    }
  },

  // 输入内容
  onContentInput(e) {
    const content = e.detail.value;
    if (content.length > this.data.maxLength) {
      wx.showToast({
        title: '内容不能超过' + this.data.maxLength + '字',
        icon: 'none'
      });
      return;
    }
    this.setData({ content });
  },

  // 选择分类
  onCategoryChange(e) {
    const category = parseInt(e.currentTarget.dataset.id);
    this.setData({ category });
  },

  // 选择图片
  chooseImage() {
    const remainingCount = 9 - this.data.images.length;
    if (remainingCount <= 0) {
      wx.showToast({
        title: '最多选择9张图片',
        icon: 'none'
      });
      return;
    }

    wx.chooseImage({
      count: remainingCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = [...this.data.images, ...res.tempFilePaths];
        this.setData({
          images: newImages.slice(0, 9)
        });
      }
    });
  },

  // 预览图片
  previewImage(e) {
    const current = e.currentTarget.dataset.src;
    wx.previewImage({
      current: current,
      urls: this.data.images
    });
  },

  // 删除图片
  deleteImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images.filter((_, i) => i !== index);
    this.setData({ images });
  },

  // 提交发布
  submitPublish() {
    const { content, category, images } = this.data;
    
    // 验证内容
    if (!content.trim() && images.length === 0) {
      wx.showToast({
        title: '请输入内容或选择图片',
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
    wx.showLoading({ title: '发布中...' });

    // 如果有图片，先上传图片
    if (images.length > 0) {
      this.uploadImages(images).then(imageUrls => {
        this.createPost(content.trim(), category, imageUrls, userInfo);
      }).catch(() => {
        wx.hideLoading();
        this.setData({ submitting: false });
        wx.showToast({
          title: '图片上传失败',
          icon: 'none'
        });
      });
    } else {
      this.createPost(content.trim(), category, [], userInfo);
    }
  },

  // 上传图片
  uploadImages(images) {
    return new Promise((resolve, reject) => {
      const uploadTasks = images.map(imagePath => {
        return new Promise((resolve, reject) => {
          wx.uploadFile({
            url: API.BASE_URL + '/upload/image',
            filePath: imagePath,
            name: 'file',
            success: (res) => {
              try {
                const data = JSON.parse(res.data);
                if (data.code === 200) {
                  resolve(data.data);
                } else {
                  reject(new Error(data.message));
                }
              } catch (e) {
                reject(e);
              }
            },
            fail: reject
          });
        });
      });

      Promise.all(uploadTasks)
        .then(urls => resolve(urls))
        .catch(err => reject(err));
    });
  },

  // 创建动态
  createPost(content, category, imageUrls, userInfo) {
    console.log('发布动态 - userInfo:', userInfo);
    console.log('发布动态 - userAvatar:', userInfo.avatar);
    console.log('发布动态 - userName:', userInfo.name);
    
    const postData = {
      userId: userInfo.id,
      userName: userInfo.name || '匿名用户',
      userAvatar: userInfo.avatar || '',
      content: content,
      category: category,
      images: imageUrls.join(',')
    };
    
    console.log('发布动态 - postData:', postData);

    wx.request({
      url: API.BASE_URL + '/social/posts',
      method: 'POST',
      data: postData,
      success: (res) => {
        wx.hideLoading();
        this.setData({ submitting: false });
        
        if (res.data.code === 200) {
          wx.showToast({
            title: '发布成功',
            icon: 'success',
            duration: 1500
          });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          wx.showToast({
            title: res.data.message || '发布失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.hideLoading();
        this.setData({ submitting: false });
        wx.showToast({
          title: '发布失败，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 取消发布
  cancelPublish() {
    const { content, images } = this.data;
    if (content.trim() || images.length > 0) {
      wx.showModal({
        title: '提示',
        content: '确定要放弃发布吗？',
        confirmText: '放弃',
        confirmColor: '#999',
        cancelText: '继续编辑',
        success: (res) => {
          if (res.confirm) {
            wx.navigateBack();
          }
        }
      });
    } else {
      wx.navigateBack();
    }
  }
});
