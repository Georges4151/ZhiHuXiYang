const API = require('./api');

/**
 * 微信支付工具类
 */
const PayUtil = {
  
  /**
   * 发起微信支付
   * @param {string} orderNo - 订单号
   * @param {string} openId - 用户openid
   * @returns {Promise} 支付结果
   */
  requestPayment: function(orderNo, openId) {
    return new Promise((resolve, reject) => {
      // 1. 调用后端创建支付订单
      wx.request({
        url: API.BASE_URL + '/pay/create',
        method: 'POST',
        data: {
          orderNo: orderNo,
          openId: openId
        },
        success: function(res) {
          if (res.data.code === 200 && res.data.data) {
            const payData = res.data.data;
            
            // 2. 调用微信支付API
            wx.requestPayment({
              timeStamp: payData.timeStamp,
              nonceStr: payData.nonceStr,
              package: payData.package,
              signType: payData.signType || 'MD5',
              paySign: payData.paySign,
              success: function(payRes) {
                console.log('支付成功:', payRes);
                resolve({
                  success: true,
                  data: payRes
                });
              },
              fail: function(payErr) {
                console.error('支付失败:', payErr);
                reject({
                  success: false,
                  error: payErr,
                  message: '支付失败，请重试'
                });
              }
            });
          } else {
            reject({
              success: false,
              message: res.data.message || '创建支付订单失败'
            });
          }
        },
        fail: function(err) {
          console.error('请求支付接口失败:', err);
          reject({
            success: false,
            message: '网络错误，请重试'
          });
        }
      });
    });
  },
  
  /**
   * 模拟支付（用于测试）
   * @param {string} orderNo - 订单号
   * @returns {Promise} 支付结果
   */
  mockPayment: function(orderNo) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: API.BASE_URL + '/pay/mock/success',
        method: 'POST',
        data: {
          orderNo: orderNo
        },
        success: function(res) {
          if (res.data.code === 200) {
            resolve({
              success: true,
              message: '支付成功'
            });
          } else {
            reject({
              success: false,
              message: res.data.message || '支付失败'
            });
          }
        },
        fail: function(err) {
          reject({
            success: false,
            message: '网络错误'
          });
        }
      });
    });
  },
  
  /**
   * 查询订单支付状态
   * @param {string} orderNo - 订单号
   * @returns {Promise} 查询结果
   */
  queryPayStatus: function(orderNo) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: API.BASE_URL + '/pay/status/' + orderNo,
        method: 'GET',
        success: function(res) {
          if (res.data.code === 200) {
            resolve({
              paid: res.data.data,
              message: res.data.data ? '已支付' : '未支付'
            });
          } else {
            reject({
              message: res.data.message || '查询失败'
            });
          }
        },
        fail: function(err) {
          reject({
            message: '网络错误'
          });
        }
      });
    });
  },
  
  /**
   * 显示支付确认弹窗
   * @param {Object} order - 订单信息
   * @param {Function} onConfirm - 确认回调
   * @param {Function} onCancel - 取消回调
   */
  showPayConfirm: function(order, onConfirm, onCancel) {
    wx.showModal({
      title: '确认支付',
      content: `订单金额: ¥${order.amount}\n服务: ${order.serviceName}`,
      confirmText: '立即支付',
      confirmColor: '#07c160',
      success: function(res) {
        if (res.confirm) {
          onConfirm && onConfirm();
        } else {
          onCancel && onCancel();
        }
      }
    });
  }
};

module.exports = PayUtil;
