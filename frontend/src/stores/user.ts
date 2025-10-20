import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token'))

  // 初始化时从 localStorage 恢复 userInfo
  const savedUserInfo = localStorage.getItem('userInfo')
  const userInfo = ref<any>(savedUserInfo ? JSON.parse(savedUserInfo) : null)

  // 设置用户信息
  function setUserInfo(info: any) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置 token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除用户信息
  function clearUser() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 初始化用户信息（保留此方法以兼容现有代码）
  function initUserInfo() {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
    }
  }

  return {
    token,
    userInfo,
    setUserInfo,
    setToken,
    clearUser,
    initUserInfo
  }
})
