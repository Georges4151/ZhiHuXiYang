const getBaseUrl = () => {
  const app = getApp();
  return (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
};

const request = (options) => {
  return new Promise((resolve, reject) => {
    const BASE_URL = getBaseUrl() + '/api';
    const token = wx.getStorageSync('token');
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data);
        } else {
          // 不自动显示toast，让调用方处理
          reject({
            code: res.data.code,
            message: res.data.message || '请求失败',
            data: res.data
          });
        }
      },
      fail: (err) => {
        // 网络请求失败
        reject({
          code: -1,
          message: '网络错误，请检查网络连接',
          error: err
        });
      }
    });
  });
};

const get = (url, data) => request({ url, data, method: 'GET' });
const post = (url, data) => request({ url, data, method: 'POST' });
const put = (url, data) => request({ url, data, method: 'PUT' });
const del = (url, data) => request({ url, data, method: 'DELETE' });

module.exports = {
  request,
  get,
  post,
  put,
  del,
  getBaseUrl
};
