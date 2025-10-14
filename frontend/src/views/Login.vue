<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">食品溯源平台</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="loginCode">
          <el-input
            v-model="loginForm.loginCode"
            placeholder="请输入登录账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width: 48%; margin-right: 4%">登录</el-button>
          <el-button type="default" size="large" @click="goRegister" style="width: 48%">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const loginForm = ref({
  loginCode: '',
  password: ''
})

const loginRules = {
  loginCode: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const response = await login(loginForm.value)
    // response 已经是解包后的数据 (response拦截器返回了 res.data)
    // 后端返回格式: { code: 200, data: { token: '', userInfo: {} }, message: '' }
    // 响应拦截器返回了 res.data，所以 response 就是 { token: '', userInfo: {} }
    
    userStore.setToken(response.token)
    userStore.setUserInfo(response.userInfo)
    
    ElMessage.success('登录成功')
    
    // 使用 router.push 跳转到首页
    await router.push('/')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}
</style>
