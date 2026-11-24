// pages/chatBot/chatBot.js
Page({
  data: {
    chatMode: "model",
    showBotAvatar: true,
    showVoice: true,
    
    modelConfig: {
      modelProvider: "deepseek",
      quickResponseModel: "deepseek-v3",
      welcomeMsg: "您好！我是智慧养老助手，可以为您解答养老政策、健康养生、生活服务等方面的问题，请问有什么可以帮您的？",
    },
    
    agentConfig: {
      allowVoice: true,
      showBotName: true,
    }
  },
  onLoad(options) {},
  onReady() {},
  onShow() {},
  onHide() {},
  onUnload() {},
  onPullDownRefresh() {},
  onReachBottom() {},
  onShareAppMessage() {},
});
