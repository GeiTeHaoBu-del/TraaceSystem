<template>
  <div class="trace-query-public">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <h1 class="logo">食品溯源平台 - 客户查询</h1>
      <el-button @click="goBack" type="primary" plain>返回登录</el-button>
    </div>

    <!-- 查询卡片 -->
    <div class="query-container">
      <el-card class="query-card">
        <template #header>
          <div class="card-header">
            <el-icon :size="24" color="#409EFF"><Search /></el-icon>
            <span style="margin-left: 10px;">溯源码查询</span>
          </div>
        </template>

        <!-- 搜索区域 -->
        <div class="search-container">
          <el-input
            v-model="traceCode"
            placeholder="请输入溯源码进行查询"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button 
                icon="Search" 
                @click="handleSearch"
                :disabled="countdown > 0"
              >
                {{ countdown > 0 ? `${countdown}秒后可查询` : '查询' }}
              </el-button>
            </template>
          </el-input>
          <div class="search-tip">
            <el-alert 
              type="info" 
              :closable="false"
              show-icon
            >
              <template #title>
                <span style="font-size: 12px;">为防止滥用，每次查询间隔5秒。输入产品上的溯源码即可查询完整供应链信息。</span>
              </template>
            </el-alert>
          </div>
        </div>

        <!-- 溯源结果 -->
        <div v-if="traceList.length > 0" class="trace-result">
          <el-divider content-position="left">
            <el-icon><List /></el-icon>
            <span style="margin-left: 5px;">溯源信息</span>
          </el-divider>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in traceList"
              :key="index"
              :timestamp="item.createTime"
              placement="top"
              :color="getTimelineColor(index)"
              :hollow="index === traceList.length - 1"
            >
              <el-card shadow="hover">
                <div class="trace-item">
                  <div class="trace-header">
                    <h3>{{ getEnterpriseTypeName(item) }}</h3>
                    <el-tag :type="getEnterpriseTypeTag(item)" size="large">
                      {{ getEnterpriseTypeLabel(item) }}
                    </el-tag>
                  </div>
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="批号">
                      {{ item.batchNo }}
                    </el-descriptions-item>
                    <el-descriptions-item label="产品品种">
                      {{ item.productVariety }}
                    </el-descriptions-item>
                    <el-descriptions-item label="证件编号">
                      {{ item.certNo || '无' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">
                      {{ item.createTime }}
                    </el-descriptions-item>
                    <el-descriptions-item label="批号状态" :span="2">
                      <el-tag>{{ getBatchStatusName(item.batchStatus) }}</el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 空状态 -->
        <el-empty v-else-if="searched" description="未查询到溯源信息，请检查溯源码是否正确" />
        <el-empty v-else description="请输入溯源码进行查询" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, List } from '@element-plus/icons-vue'
import { getTraceInfo } from '@/api/traceCode'

const router = useRouter()
const traceCode = ref('')
const traceList = ref<any[]>([])
const searched = ref(false)
const countdown = ref(0)
let timer: NodeJS.Timeout | null = null

const getEnterpriseTypeName = (item: any) => {
  if (!item.upstreamBatchId) return '养殖企业'
  if (item.batchNo.includes('屠宰')) return '屠宰企业'
  if (item.batchNo.includes('批发')) return '批发企业'
  return '零售企业'
}

const getEnterpriseTypeLabel = (item: any) => {
  if (!item.upstreamBatchId) return '源头'
  if (item.batchNo.includes('屠宰')) return '加工'
  if (item.batchNo.includes('批发')) return '流通'
  return '终端'
}

const getEnterpriseTypeTag = (item: any) => {
  if (!item.upstreamBatchId) return 'success'
  if (item.batchNo.includes('屠宰')) return 'warning'
  if (item.batchNo.includes('批发')) return 'info'
  return 'danger'
}

const getBatchStatusName = (status: number) => {
  const map: any = {
    0: '待发布/新建',
    1: '已发布/待确认',
    2: '已确认',
    3: '已下架'
  }
  return map[status] || '-'
}

const getTimelineColor = (index: number) => {
  const colors = ['#67C23A', '#E6A23C', '#409EFF', '#F56C6C']
  return colors[index % colors.length]
}

const startCountdown = () => {
  countdown.value = 5
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (timer) {
        clearInterval(timer)
        timer = null
      }
    }
  }, 1000)
}

const handleSearch = async () => {
  if (countdown.value > 0) {
    ElMessage.warning(`请等待 ${countdown.value} 秒后再查询`)
    return
  }

  if (!traceCode.value.trim()) {
    ElMessage.warning('请输入溯源码')
    return
  }

  searched.value = true
  try {
    const res = await getTraceInfo(traceCode.value.trim())
    const data = res?.data || res
    traceList.value = Array.isArray(data) ? data : []
    
    if (traceList.value.length === 0) {
      ElMessage.warning('未查询到溯源信息')
    } else {
      ElMessage.success('查询成功')
      // 查询成功后开始倒计时
      startCountdown()
    }
  } catch (error) {
    console.error(error)
    traceList.value = []
    ElMessage.error('查询失败，请稍后重试')
  }
}

const goBack = () => {
  router.push('/login')
}

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped lang="scss">
.trace-query-public {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;

  .top-bar {
    max-width: 1200px;
    margin: 0 auto 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .logo {
      margin: 0;
      font-size: 20px;
      color: #333;
      font-weight: 600;
    }
  }

  .query-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .query-card {
    .card-header {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }

  .search-container {
    margin-bottom: 30px;

    .search-tip {
      margin-top: 15px;
    }
  }

  .trace-result {
    margin-top: 20px;
  }

  .trace-item {
    .trace-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      h3 {
        margin: 0;
        font-size: 18px;
        color: #333;
      }
    }
  }
}
</style>
