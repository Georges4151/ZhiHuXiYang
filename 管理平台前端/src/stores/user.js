import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(null)

  const loginAction = async (loginForm) => {
    const res = await login(loginForm)
    token.value = res.token
    localStorage.setItem('admin_token', res.token)
    return res
  }

  const getUserInfo = async () => {
    const res = await getInfo()
    userInfo.value = res
    return res
  }

  const logoutAction = async () => {
    await logout()
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
  }

  return {
    token,
    userInfo,
    loginAction,
    getUserInfo,
    logoutAction
  }
})
