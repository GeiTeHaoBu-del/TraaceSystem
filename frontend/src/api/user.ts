import request from '@/utils/request'

// 登录响应数据类型
interface LoginResponse {
  token: string
  userInfo: {
    userId: number
    userType: number
    loginCode: string
    enterpriseId: number | null
    status: number
  }
}

// 用户登录
export function login(data: { loginCode: string; password: string }): Promise<LoginResponse> {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data: any) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo(userId: number) {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

// 修改密码
export function updatePassword(userId: number, data: { oldPassword: string; newPassword: string }) {
  return request({
    url: `/user/password/${userId}`,
    method: 'put',
    data
  })
}
