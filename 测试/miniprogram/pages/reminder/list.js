const API = require('../../utils/api');
const MedicationReminder = require('../../utils/medicationReminder');

Page({
  data: {
    reminderList: [],
    loading: false,
    showAddModal: false,
    editMode: false,
    currentReminder: null,
    form: {
      medicationName: '',
      dosage: '',
      frequency: '每天一次',
      reminderTime: '08:00',
      startDate: '',
      endDate: '',
      remark: ''
    },
    frequencyOptions: ['每天一次', '每天两次', '每天三次', '每周一次'],
    frequencyIndex: 0,
    todayRecords: [],
    stats: { total: 0, today: 0 }
  },

  onLoad: function() {
    this.loadReminders();
    this.loadRecords();
  },

  onShow: function() {
    this.loadReminders();
    this.loadRecords();
  },

  loadRecords: function() {
    const todayRecords = MedicationReminder.getTodayRecords();
    const stats = MedicationReminder.getStats();
    this.setData({ todayRecords, stats });
  },

  loadReminders: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    API.getMedicationList(userInfo.id).then(list => {
      this.setData({ 
        reminderList: list || [],
        loading: false 
      });
    }).catch(err => {
      console.error('加载用药提醒失败', err);
      this.setData({ loading: false });
    });
  },

  showAddModal: function() {
    const today = this.formatDate(new Date());
    const endDate = this.formatDate(new Date(Date.now() + 30 * 24 * 60 * 60 * 1000));
    
    this.setData({
      showAddModal: true,
      editMode: false,
      currentReminder: null,
      form: {
        medicationName: '',
        dosage: '',
        frequency: '每天一次',
        reminderTime: '08:00',
        startDate: today,
        endDate: endDate,
        remark: ''
      },
      frequencyIndex: 0
    });
  },

  hideModal: function() {
    this.setData({ showAddModal: false });
  },

  onInputChange: function(e) {
    const { field } = e.currentTarget.dataset;
    const { value } = e.detail;
    this.setData({
      [`form.${field}`]: value
    });
  },

  onFrequencyChange: function(e) {
    const index = e.detail.value;
    this.setData({
      frequencyIndex: index,
      'form.frequency': this.data.frequencyOptions[index]
    });
  },

  onTimeChange: function(e) {
    this.setData({
      'form.reminderTime': e.detail.value
    });
  },

  onStartDateChange: function(e) {
    this.setData({
      'form.startDate': e.detail.value
    });
  },

  onEndDateChange: function(e) {
    this.setData({
      'form.endDate': e.detail.value
    });
  },

  submitForm: function() {
    const { form, editMode, currentReminder } = this.data;
    
    if (!form.medicationName) {
      wx.showToast({ title: '请输入药品名称', icon: 'none' });
      return;
    }
    if (!form.dosage) {
      wx.showToast({ title: '请输入用药剂量', icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    const data = {
      userId: userInfo.id,
      medicationName: form.medicationName,
      dosage: form.dosage,
      frequency: form.frequency,
      reminderTime: form.reminderTime + ':00',
      startDate: form.startDate,
      endDate: form.endDate,
      remark: form.remark
    };

    if (editMode && currentReminder) {
      data.id = currentReminder.id;
      API.updateMedication(data).then(() => {
        wx.showToast({ title: '修改成功', icon: 'success' });
        this.hideModal();
        this.loadReminders();
      }).catch(err => {
        wx.showToast({ title: '修改失败', icon: 'none' });
      });
    } else {
      API.addMedication(data).then(() => {
        wx.showToast({ title: '添加成功', icon: 'success' });
        this.hideModal();
        this.loadReminders();
      }).catch(err => {
        wx.showToast({ title: '添加失败', icon: 'none' });
      });
    }
  },

  editReminder: function(e) {
    const { item } = e.currentTarget.dataset;
    const frequencyIndex = this.data.frequencyOptions.indexOf(item.frequency);
    
    this.setData({
      showAddModal: true,
      editMode: true,
      currentReminder: item,
      form: {
        medicationName: item.medicationName,
        dosage: item.dosage,
        frequency: item.frequency,
        reminderTime: item.reminderTime ? item.reminderTime.substring(0, 5) : '08:00',
        startDate: item.startDate,
        endDate: item.endDate,
        remark: item.remark || ''
      },
      frequencyIndex: frequencyIndex >= 0 ? frequencyIndex : 0
    });
  },

  deleteReminder: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个用药提醒吗？',
      success: (res) => {
        if (res.confirm) {
          API.deleteMedication(id).then(() => {
            wx.showToast({ title: '删除成功', icon: 'success' });
            this.loadReminders();
          }).catch(err => {
            wx.showToast({ title: '删除失败', icon: 'none' });
          });
        }
      }
    });
  },

  toggleStatus: function(e) {
    const { id, status } = e.currentTarget.dataset;
    const newStatus = status === 1 ? 0 : 1;
    
    API.toggleMedicationStatus(id, newStatus).then(() => {
      this.loadReminders();
    }).catch(err => {
      wx.showToast({ title: '操作失败', icon: 'none' });
    });
  },

  formatDate: function(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  formatDisplayTime: function(timeStr) {
    if (!timeStr) return '';
    return timeStr.substring(0, 5);
  },

  getStatusText: function(status) {
    return status === 1 ? '已启用' : '已停用';
  },

  getStatusClass: function(status) {
    return status === 1 ? 'active' : 'inactive';
  }
});
