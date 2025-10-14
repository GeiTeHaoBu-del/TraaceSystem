<template>
  <div class="trace-query">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>溯源查询</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="traceCode"
          placeholder="请输入溯源码进行查询"
          size="large"
          clearable
        >
          <template #append>
            <el-button icon="Search" @click="handleSearch">查询</el-button>
          </template>
        </el-input>
      </div>

      <!-- 溯源结果 -->
      <div v-if="traceList.length > 0" class="trace-result">
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in traceList"
            :key="index"
            :timestamp="item.createTime"
            placement="top"
            :color="getTimelineColor(index)"
          >
            <el-card shadow="hover">
              <div class="trace-item">
                <div class="trace-header">
                  <h3>{{ getEnterpriseTypeName(item) }}</h3>
                  <el-tag :type="getEnterpriseTypeTag(item)">
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
                    {{ item.certNo }}
                  </el-descriptions-item>
                  <el-descriptions-item label="创建时间">
                    {{ item.createTime }}
                  </el-descriptions-item>
                  <el-descriptions-item label="企业ID">
                    {{ item.enterpriseId }}
                  </el-descriptions-item>
                  <el-descriptions-item label="批号状态">
                    <el-tag>{{ getBatchStatusName(item.batchStatus) }}</el-tag>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 空状态 -->
      <el-empty v-else-if="searched" description="未查询到溯源信息" />
      <el-empty v-else description="请输入溯源码进行查询" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTraceInfo } from '@/api/traceCode'

const traceCode = ref('')
const traceList = ref<any[]>([])
const searched = ref(false)

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

const handleSearch = async () => {
  if (!traceCode.value.trim()) {
    ElMessage.warning('请输入溯源码')
    return
  }

  searched.value = true
  try {
    const res = await getTraceInfo(traceCode.value.trim())
    // 适配 request 响应结构
    const data = res?.data || res
    traceList.value = Array.isArray(data) ? data : []
    if (traceList.value.length === 0) {
      ElMessage.warning('未查询到溯源信息')
    }
  } catch (error) {
    console.error(error)
    traceList.value = []
  }
}
</script>

<style scoped lang="scss">
.trace-query {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-container {
    max-width: 600px;
    margin: 0 auto 30px;
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
