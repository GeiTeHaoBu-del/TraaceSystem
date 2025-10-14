<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="title">用户注册</h2>
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
        <el-form-item prop="loginCode">
          <el-input v-model="registerForm.loginCode" placeholder="请输入登录账号" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="userType">
          <el-select v-model="registerForm.userType" placeholder="请选择用户类型" size="large">
            <el-option label="系统管理员" :value="0" />
            <el-option label="养殖企业" :value="1" />
            <el-option label="屠宰企业" :value="2" />
            <el-option label="批发企业" :value="3" />
            <el-option label="零售企业" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="registerForm.userType !== 0" prop="enterpriseId">
          <el-select v-model="registerForm.enterpriseId" placeholder="请选择企业" filterable size="large">
            <el-option v-for="item in enterpriseList" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'
import { getEnterpriseList } from '@/api/enterprise'

const router = useRouter()
const loading = ref(false)
const registerForm = ref({
  loginCode: '',
  password: '',
  confirmPassword: '',
  userType: 1,
  enterpriseId: null
})
const enterpriseList = ref<any[]>([])
const registerRules = {
  loginCode: [
    { required: true, message: '请输入登录账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  enterpriseId: [
    { required: true, message: '请选择企业', trigger: 'change', validator: (_: any, value: any, cb: any) => {
      if (registerForm.value.userType !== 0 && !value) {
        cb(new Error('请选择企业'))
      } else {
        cb()
      }
    }}
  ]
}

onMounted(() => {
  getEnterpriseList().then((res) => {
    enterpriseList.value = res || []
  })
})
watch(() => registerForm.value.userType, (val) => {
  if (val !== 0) {
    getEnterpriseList().then((res) => {
      enterpriseList.value = res || []
    })
  }
})

const handleRegister = async () => {
  loading.value = true
  try {
    const { confirmPassword, ...userData } = registerForm.value
    // 仅企业用户类型提交 enterpriseId，管理员不提交
    if (userData.userType === 0) {
      userData.enterpriseId = null;
    }
    const res = await register(userData)
    if (res) {
      ElMessage.success('注册成功！')
      router.push('/login')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>   

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f7fa;
}
.register-box {
  width: 400px;
  padding: 40px 32px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.title {
  text-align: center;
  margin-bottom: 32px;
  font-size: 24px;
  font-weight: bold;
}
.register-form {
  margin-top: 16px;
}
</style>
