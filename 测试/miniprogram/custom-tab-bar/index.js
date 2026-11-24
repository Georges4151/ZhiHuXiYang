Component({
  data: {
    selected: 0,
    color: "#999999",
    selectedColor: "#4A90D9",
    list: [
      {
        pagePath: "/pages/index/index",
        text: "首页"
      },
      {
        pagePath: "/pages/service/service",
        text: "服务"
      },
      {
        pagePath: "/pages/news/news",
        text: "新闻"
      },
      {
        pagePath: "/pages/mine/mine",
        text: "我的"
      }
    ]
  },
  
  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset;
      const url = data.path;
      wx.switchTab({url});
      this.setData({
        selected: data.index
      });
    }
  }
});
