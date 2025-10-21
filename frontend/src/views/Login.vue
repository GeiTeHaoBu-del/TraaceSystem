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
          <div class="button-group">
            <el-button type="primary" size="large" :loading="loading" @click="handleLogin">
              登录
            </el-button>
            <el-button size="large" @click="goRegister">
              注册
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 分隔线 -->
      <el-divider>或</el-divider>
      
      <!-- 客户查询入口 -->
      <div class="customer-query">
        <el-button 
          type="success" 
          size="large" 
          @click="goTraceQuery"
          plain
          style="width: 100%"
        >
          <el-icon style="margin-right: 8px"><Search /></el-icon>
          客户查询（溯源码查询）
        </el-button>
        <p class="query-tip">无需登录，直接查询产品溯源信息</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

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
    
    userStore.setToken(response.token)
    userStore.setUserInfo(response.userInfo)
    
    ElMessage.success('登录成功')
    
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

const goTraceQuery = () => {
  router.push('/trace-query')
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
  width: 420px;
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

.button-group {
  display: flex;
  gap: 12px;
  width: 100%;
  
  .el-button {
    flex: 1;
  }
}

.customer-query {
  margin-top: 20px;
  text-align: center;
  
  .query-tip {
    margin-top: 10px;
    font-size: 12px;
    color: #909399;
  }
}

.el-divider {
  margin: 20px 0;
}
</style>
