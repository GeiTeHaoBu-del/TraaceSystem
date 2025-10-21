<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(stat, index) in stats" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="30"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions" v-if="userInfo">
            <el-button type="primary" icon="Plus" @click="handleAction('batch')" v-if="userInfo.userType !== 0">
              创建批号
            </el-button>
            <el-button type="success" icon="Document" @click="handleAction('certificate')" v-if="userInfo.userType !== 0">
              管理证件
            </el-button>
            <el-button type="warning" icon="Search" @click="handleAction('trace')">
              溯源查询
            </el-button>
            <el-button type="info" icon="OfficeBuilding" @click="handleAction('enterprise')" v-if="userInfo.userType === 0">
              企业管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item timestamp="2025-10-14" placement="top">
              <el-card>
                <h4>欢迎使用食品溯源平台</h4>
                <p>本平台提供完整的食品溯源管理功能，包括企业管理、批号管理、溯源码生成等。</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2025-10-13" placement="top">
              <el-card>
                <h4>系统更新通知</h4>
                <p>新增批号确认流程，优化溯源查询功能。</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBatchPage } from '@/api/batch'
import { getConfirmationPage } from '@/api/confirmation'
import { getEnterpriseCertificates } from '@/api/certificate'
import { getEnterpriseStatistics } from '@/api/enterprise'
import { getTraceCodePage } from '@/api/traceCode'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const stats = ref([
  { label: '待处理批号', value: 0, icon: 'Box', color: '#409EFF' },
  { label: '待确认请求', value: 0, icon: 'Select', color: '#67C23A' },
  { label: '有效证件', value: 0, icon: 'Document', color: '#E6A23C' },
  { label: '已生成溯源码', value: 0, icon: 'Postcard', color: '#F56C6C' }
])

const handleAction = (action: string) => {
  const routes: any = {
    batch: '/batch',
    certificate: '/certificate',
    trace: '/trace-query',
    enterprise: '/enterprise'
  }
  router.push(routes[action])
}

const loadStats = async () => {
  try {
    // 检查 userInfo 是否存在
    if (!userInfo.value) {
      console.error('用户信息不存在')
      return
    }

    // 系统管理员加载全局统计数据
    if (userInfo.value.userType === 0) {
      stats.value[0]!.label = '总批号数'
      stats.value[1]!.label = '总确认请求'
      stats.value[2]!.label = '总企业数'
      stats.value[3]!.label = '总溯源码'

      try {
          const [batchRes, confirmRes, enterpriseStatsRes, traceRes] = await Promise.all([
            getBatchPage({ pageNum: 1, pageSize: 1 }),
            getConfirmationPage({ pageNum: 1, pageSize: 1 }),
            getEnterpriseStatistics(),
            getTraceCodePage({ pageNum: 1, pageSize: 1 })
          ])

          // 打印原始API返回数据
          console.log('API返回数据:', {
            batchRes,
            confirmRes,
            enterpriseStatsRes,
            traceRes
          })

          const normalizeTotal = (obj: any) => {
            console.log('normalizeTotal 处理的数据:', obj)
            
            if (!obj) {
              console.log('数据为空，返回0')
              return 0
            }
            
            // If it's an object with records array
            if (Array.isArray(obj.records)) {
              console.log('找到records数组，长度为:', obj.records.length)
              // 如果total大于0就用total，否则用records长度
              if (obj.total && obj.total > 0) {
                console.log('使用total值:', obj.total)
                return obj.total
              }
              return obj.records.length
            }
            
            // If it's a plain array
            if (Array.isArray(obj)) {
              console.log('是普通数组，长度为:', obj.length)
              return obj.length
            }
            
            // If it's an enterprise statistics object
            if (typeof obj.total === 'number' && obj.typeStats) {
              console.log('找到企业统计total:', obj.total)
              return obj.total
            }
            
            // If it's a wrapper { data: ... }
            if (obj.data) {
              console.log('找到data属性，继续处理data内容')
              return normalizeTotal(obj.data)
            }
            
            // fallback to count property
            if (typeof obj.count === 'number') {
              console.log('找到count属性:', obj.count)
              return obj.count
            }
            
            console.log('未找到任何可用的计数信息，返回0')
            return 0
          }

          console.log('开始处理各项统计数据...')
          
          const batchTotal = normalizeTotal(batchRes)
          console.log('批号总数:', batchTotal)
          
          const confirmTotal = normalizeTotal(confirmRes)
          console.log('确认请求总数:', confirmTotal)
          
          const enterpriseTotal = (enterpriseStatsRes as any)?.total || normalizeTotal(enterpriseStatsRes)
          console.log('企业总数:', enterpriseTotal)
          
          const traceTotal = normalizeTotal(traceRes)
          console.log('溯源码总数:', traceTotal)

          stats.value[0]!.value = batchTotal
          stats.value[1]!.value = confirmTotal
          stats.value[2]!.value = enterpriseTotal
          stats.value[3]!.value = traceTotal

          console.log('统计数据更新完成:', stats.value)
      } catch (e) {
        console.error('加载管理员统计失败', e)
      }

      return
    }

    // 企业用户加载自己的统计数据
    const enterpriseId = userInfo.value.enterpriseId
    if (!enterpriseId) {
      console.error('企业ID不存在')
      return
    }

    const [batchRes, confirmRes, certRes] = await Promise.all([
      getBatchPage({ pageNum: 1, pageSize: 1, enterpriseId }),
      getConfirmationPage({ pageNum: 1, pageSize: 1, receiveEnterpriseId: enterpriseId }),
      getEnterpriseCertificates(enterpriseId)
    ])
    
    console.log('企业用户统计数据:', {
      batchRes,
      confirmRes,
      certRes
    })
    
    // 处理批号统计
    const batchData = batchRes.data?.data || batchRes.data || batchRes
    console.log('处理后的批号数据:', batchData)
    stats.value[0]!.value = batchData.records?.length || 0
    
    // 处理确认请求统计
    const confirmData = confirmRes.data?.data || confirmRes.data || confirmRes
    console.log('处理后的确认请求数据:', confirmData)
    stats.value[1]!.value = confirmData.records?.length || 0
    
    // 处理证件统计
    const certData = certRes.data || certRes
    console.log('处理后的证件数据:', certData)
    stats.value[2]!.value = Array.isArray(certData) ? certData.filter((c: any) => c.status === 1).length : 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.home {
  .stat-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
    }
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 32px; /* 从 28px 调整到 32px */
    font-weight: bold;
    color: #333;
  }

  .stat-label {
    font-size: 15px; /* 从 14px 调整到 15px */
    color: #666;
    margin-top: 5px;
  }

  .quick-actions {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    
    .el-button {
      font-size: 15px; /* 按钮字体调整到 15px */
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px; /* 卡片标题字体 16px */
    font-weight: 500;
  }
  
  // 公告内容字体调整
  :deep(.el-timeline-item__timestamp) {
    font-size: 14px;
  }
  
  :deep(.el-card h4) {
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  :deep(.el-card p) {
    font-size: 15px;
    line-height: 1.6;
  }
}
</style>
