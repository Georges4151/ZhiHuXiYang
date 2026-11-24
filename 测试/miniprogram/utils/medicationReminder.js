const API = require('./api');

const MedicationReminder = {
  lastCheckTime: 0,
  checkInterval: 60000,

  checkAndRemind: function() {
    const now = Date.now();
    if (now - this.lastCheckTime < this.checkInterval) {
      return;
    }
    this.lastCheckTime = now;

    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }

    API.getActiveMedications(userInfo.id).then(list => {
      if (!list || list.length === 0) {
        return;
      }

      const currentTime = this.getCurrentTime();
      const dueReminders = list.filter(item => {
        const reminderTime = item.reminderTime ? item.reminderTime.substring(0, 5) : '';
        return reminderTime === currentTime;
      });

      if (dueReminders.length > 0) {
        this.showReminder(dueReminders[0]);
      }
    }).catch(err => {
      console.error('检查用药提醒失败', err);
    });
  },

  getCurrentTime: function() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  },

  showReminder: function(reminder) {
    wx.vibrateLong({
      success: () => {
        console.log('震动成功');
      }
    });

    setTimeout(() => {
      wx.vibrateLong();
    }, 500);

    setTimeout(() => {
      wx.vibrateLong();
    }, 1000);

    wx.showModal({
      title: '💊 该吃药了！',
      content: `${reminder.medicationName}\n剂量：${reminder.dosage}\n提醒时间：${reminder.reminderTime ? reminder.reminderTime.substring(0, 5) : ''}`,
      confirmText: '已服药',
      cancelText: '稍后提醒',
      success: (res) => {
        if (res.confirm) {
          this.recordMedication(reminder);
        }
      }
    });
  },

  recordMedication: function(reminder) {
    const records = wx.getStorageSync('medicationRecords') || [];
    records.push({
      reminderId: reminder.id,
      medicationName: reminder.medicationName,
      dosage: reminder.dosage,
      takeTime: new Date().toISOString()
    });
    wx.setStorageSync('medicationRecords', records);
    
    wx.showToast({
      title: '已记录服药',
      icon: 'success'
    });
  },

  getTodayRecords: function() {
    const records = wx.getStorageSync('medicationRecords') || [];
    const today = new Date().toISOString().substring(0, 10);
    return records.filter(r => r.takeTime && r.takeTime.substring(0, 10) === today);
  },

  getStats: function() {
    const records = wx.getStorageSync('medicationRecords') || [];
    const today = new Date().toISOString().substring(0, 10);
    const todayRecords = records.filter(r => r.takeTime && r.takeTime.substring(0, 10) === today);
    
    return {
      total: records.length,
      today: todayRecords.length
    };
  }
};

module.exports = MedicationReminder;
