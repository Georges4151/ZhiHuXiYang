const { get, post, put, del, getBaseUrl } = require('./request');

const getApiBaseUrl = () => getBaseUrl() + '/api';

const API = {
  get BASE_URL() { return getApiBaseUrl(); },
  getBaseUrl: getBaseUrl,
  
  // 活动相关API
  get ACTIVITY_LIST() { return getApiBaseUrl() + '/activity/list'; },
  getActivityList: (category) => get('/activity/list' + (category ? '?category=' + category : '')),
  getActivityDetail: (id) => get('/activity/' + id),
  joinActivity: (id) => post('/activity/join/' + id),
  cancelJoin: (id) => post('/activity/cancel/' + id),
  likeActivity: (id) => post('/activity/like/' + id),
  getActivityComments: (activityId) => get('/activity/comments/' + activityId),
  addActivityComment: (data) => post('/activity/comment', data),
  
  sendSmsCode: (data) => post('/auth/sms/send', data),
  login: (data) => post('/auth/login', data),
  register: (data) => post('/auth/register', data),
  checkPhone: (phone) => get('/auth/check-phone?phone=' + phone),
  
  getUserInfo: (id) => get('/user/info/' + id),
  getUserProfile: (id) => get('/user/profile/' + id),
  updateUser: (data) => put('/user/update', data),
  
  chat: (data) => post('/consultation/chat', data),
  getChatHistory: (userId, limit = 20) => get('/consultation/history/' + userId + '?limit=' + limit),
  
  getLatestHealth: (userId) => get('/health/latest/' + userId),
  getRecentHealth: (userId, limit = 7) => get('/health/recent/' + userId + '?limit=' + limit),
  addHealthData: (data) => post('/health/add', data),
  analyzeHealth: () => get('/health/analysis/analyze'),
  
  getServiceList: (categoryId) => get('/service/list' + (categoryId ? '?categoryId=' + categoryId : '')),
  getHotServices: (limit = 6) => get('/service/hot?limit=' + limit),
  getServiceDetail: (id) => get('/service/detail/' + id),
  createOrder: (data) => post('/service/order/create', data),
  getUserOrders: (userId, pageNum = 1, pageSize = 10) => get('/service/order/user/' + userId + '?pageNum=' + pageNum + '&pageSize=' + pageSize),
  getOrderDetail: (id) => get('/service/order/detail/' + id),
  updateOrderStatus: (orderId, status) => put('/service/order/status?orderId=' + orderId + '&status=' + status),
  
  createEmergencyCall: (data) => post('/emergency/call', data),
  getEmergencyDetail: (id) => get('/emergency/detail/' + id),
  
  getFamilyList: (userId) => get('/family/members/' + userId),
  addFamily: (data) => post('/family/bind', data),
  updateFamily: (data) => post('/family/bind', data),
  deleteFamily: (id) => post('/family/unbind/' + id),
  
  getHotNews: (limit = 5) => get('/news/hot?limit=' + limit),
  getNewsPage: (pageNum = 1, pageSize = 10) => get('/news/page?pageNum=' + pageNum + '&pageSize=' + pageSize),
  getNewsByCategory: (categoryId, pageNum = 1, pageSize = 10) => get('/news/category/' + categoryId + '?pageNum=' + pageNum + '&pageSize=' + pageSize),
  getNewsDetail: (id) => get('/news/detail/' + id),
  
  getWeatherNow: (location) => get('/weather/now' + (location ? '?location=' + location : '')),
  getWeatherForecast: (location) => get('/weather/forecast' + (location ? '?location=' + location : '')),
  getWeatherAir: (location) => get('/weather/air' + (location ? '?location=' + location : '')),
  getWeatherIndices: (location) => get('/weather/indices' + (location ? '?location=' + location : '')),
  getWeatherAll: (location) => get('/weather/all' + (location ? '?location=' + location : '')),
  
  getMedicationList: (userId) => get('/medication/list/' + userId),
  getActiveMedications: (userId) => get('/medication/active/' + userId),
  getMedicationDetail: (id) => get('/medication/detail/' + id),
  addMedication: (data) => post('/medication/add', data),
  updateMedication: (data) => post('/medication/update', data),
  deleteMedication: (id) => del('/medication/delete/' + id),
  toggleMedicationStatus: (id, status) => put('/medication/toggle/' + id + '/' + status),
  
  getAddressList: (userId) => get('/address/list/' + userId),
  getDefaultAddress: (userId) => get('/address/default/' + userId),
  getAddressDetail: (id) => get('/address/detail/' + id),
  addAddress: (data) => post('/address/add', data),
  updateAddress: (data) => post('/address/update', data),
  deleteAddress: (id) => del('/address/delete/' + id),
  setDefaultAddress: (userId, addressId) => put('/address/setDefault/' + userId + '/' + addressId),
  
  getFeedbackList: (userId) => get('/feedback/list/' + userId),
  addFeedback: (data) => post('/feedback/add', data),
  
  getNotificationList: (userId, type) => get('/notification/list/' + userId + (type !== undefined ? '?type=' + type : '')),
  getNotificationUnread: (userId) => get('/notification/unread/' + userId),
  getNotificationDetail: (id) => get('/notification/detail/' + id),
  markNotificationRead: (id) => put('/notification/read/' + id),
  markAllNotificationRead: (userId) => put('/notification/readAll/' + userId)
};

module.exports = API;
