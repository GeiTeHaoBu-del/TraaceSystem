<template>
  <div class="batch">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>批号管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">创建批号</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="批号状态">
          <el-select v-model="searchForm.batchStatus" placeholder="请选择" clearable>
            <el-option label="待发布" :value="0" v-if="userInfo.userType === 1" />
            <el-option label="已发布" :value="1" v-if="userInfo.userType === 1" />
            <el-option label="新建" :value="0" v-if="userInfo.userType !== 1" />
            <el-option label="待确认" :value="1" v-if="userInfo.userType !== 1" />
            <el-option label="已确认" :value="2" v-if="userInfo.userType !== 1" />
            <el-option label="已下架" :value="[2, 3]" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="batchNo" label="批号" width="180" />
        <el-table-column prop="productVariety" label="产品品种" width="100" />
        <el-table-column prop="certNo" label="证件编号" width="150" />
        <el-table-column prop="downstreamEnterpriseName" label="指定下游企业" width="150" v-if="userInfo.userType !== 4" />
        <el-table-column prop="batchStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getBatchStatusTag(row.batchStatus)">
              {{ getBatchStatusName(row.batchStatus, userInfo.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button 
              link 
              type="success" 
              @click="handlePublish(row)"
              v-if="userInfo.userType === 1 && row.batchStatus === 0"
            >
              发布
            </el-button>
            <el-button link type="primary" @click="handleTrace(row)">溯源</el-button>
            <el-button 
              link 
              type="info" 
              @click="handleGenerateCode(row)"
              v-if="userInfo.userType === 4"
            >
              生成溯源码
            </el-button>
            <el-button 
              link 
              type="danger" 
              @click="handleOffline(row)"
              v-if="row.batchStatus === 1 || row.batchStatus === 2"
            >
              下架
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 创建批号对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="上游批号" prop="upstreamBatchId" v-if="userInfo.userType !== 1">
          <el-select v-model="form.upstreamBatchId" placeholder="请选择上游批号" @focus="loadUpstreamBatches">
            <el-option
              v-for="item in upstreamBatches"
              :key="item.batchId"
              :label="item.batchNo"
              :value="item.batchId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指定下游企业" prop="downstreamEnterpriseId">
          <el-select v-model="form.downstreamEnterpriseId" placeholder="请选择下游企业" @focus="loadDownstreamEnterprises">
            <el-option
              v-for="item in downstreamEnterprises"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产品品种" prop="productVariety">
          <el-input v-model="form.productVariety" placeholder="请输入产品品种" />
        </el-form-item>
        <el-form-item label="证件编号" prop="certNo">
          <el-input v-model="form.certNo" placeholder="请输入证件编号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 溯源详情对话框 -->
    <el-dialog v-model="traceDialogVisible" title="溯源信息" width="800px">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in traceList"
          :key="index"
          :timestamp="item.createTime"
          placement="top"
        >
          <el-card>
            <h4>{{ getEnterpriseTypeName(item) }} - {{ item.batchNo }}</h4>
            <p>产品品种: {{ item.productVariety }}</p>
            <p>证件编号: {{ item.certNo }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getBatchPage,
  createBreedingBatch,
  createDownstreamBatch,
  publishBatch,
  offlineBatch,
  getAvailableUpstreamBatches,
  traceBatch
} from '@/api/batch'
import { initiateConfirmation } from '@/api/confirmation'
import { generateTraceCode } from '@/api/traceCode'
import { getEnterpriseList } from '@/api/enterprise'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const traceDialogVisible = ref(false)
const publishDialogVisible = ref(false)
const dialogTitle = ref('创建批号')
const formRef = ref()
const publishFormRef = ref()
interface BatchItem {
  batchId: number
  batchNo: string
  enterpriseId: number
  upstreamBatchId: number | null
  productVariety: string
  certNo: string
  batchStatus: number
  createTime: string
  updateTime: string
}

const upstreamBatches = ref<BatchItem[]>([])
const traceList = ref<BatchItem[]>([])
const downstreamEnterprises = ref([])

const searchForm = reactive({
  batchStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  upstreamBatchId: null,
  downstreamEnterpriseId: null as number | null,
  productVariety: '',
  certNo: ''
})

const formRules = {
  upstreamBatchId: [{ required: true, message: '请选择上游批号', trigger: 'change' }],
  downstreamEnterpriseId: [{ required: true, message: '请选择下游企业', trigger: 'change' }],
  productVariety: [{ required: true, message: '请输入产品品种', trigger: 'blur' }],
  certNo: [{ required: true, message: '请输入证件编号', trigger: 'blur' }]
}

const getBatchStatusName = (status: number, userType: number) => {
  if (userType === 1) {
    const map: any = { 0: '待发布', 1: '已发布', 2: '已下架' }
    return map[status] || '-'
  } else {
    const map: any = { 0: '新建', 1: '待确认', 2: '已确认', 3: '已下架' }
    return map[status] || '-'
  }
}

const getBatchStatusTag = (status: number) => {
  const map: any = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || ''
}

const getEnterpriseTypeName = (batch: any) => {
  if (!batch.upstreamBatchId) return '养殖企业'
  if (batch.batchNo.includes('屠宰')) return '屠宰企业'
  if (batch.batchNo.includes('批发')) return '批发企业'
  return '零售企业'
}

const loadData = async () => {
  loading.value = true
  try {
    console.log('加载批号数据，参数:', {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      enterpriseId: userInfo.value.enterpriseId,
      batchStatus: searchForm.batchStatus
    })
    
    const res = await getBatchPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      enterpriseId: userInfo.value.enterpriseId,
      ...searchForm
    })
    
    console.log('批号数据响应:', res)
    
    // 如果res.data存在就用res.data，否则用res本身
    const data = res.data || res
    tableData.value = data.records || []
    pagination.total = data.total || 0
    
    console.log('处理后的数据:', {
      records: tableData.value,
      total: pagination.total
    })
  } catch (error: any) {
    console.error('加载批号数据失败:', error)
    if (error.response) {
      console.error('错误响应:', error.response.data)
    }
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.batchStatus = null
  pagination.pageNum = 1
  loadData()
}

const loadUpstreamBatches = async () => {
  try {
    console.log('加载上游批号，企业类型:', userInfo.value.userType)
    const res = await getAvailableUpstreamBatches(userInfo.value.userType)
    console.log('上游批号响应:', res)
    
    // 处理响应数据
    if (Array.isArray(res)) {
      upstreamBatches.value = res
    } else if (res.data && Array.isArray(res.data)) {
      upstreamBatches.value = res.data
    } else if (res.data && Array.isArray(res.data.records)) {
      upstreamBatches.value = res.data.records
    } else {
      console.error('无效的上游批号数据:', res)
      upstreamBatches.value = []
    }
    
    console.log('处理后的上游批号:', upstreamBatches.value)
  } catch (error) {
    console.error('加载上游批号失败:', error)
    upstreamBatches.value = []
  }
}

const loadDownstreamEnterprises = async () => {
  try {
    console.log('开始加载下游企业，当前企业类型:', userInfo.value.userType)
    
    // 根据当前企业类型确定下游企业类型
    let targetType = 0
    if (userInfo.value.userType === 1) {
      targetType = 2 // 养殖企业 -> 屠宰企业
    } else if (userInfo.value.userType === 2) {
      targetType = 3 // 屠宰企业 -> 批发企业
    } else if (userInfo.value.userType === 3) {
      targetType = 4 // 批发企业 -> 零售企业
    } else {
      console.log('零售企业不需要指定下游企业')
      downstreamEnterprises.value = []
      return
    }
    
    console.log('目标下游企业类型:', targetType)
    
    // 使用 enterpriseType 参数调用 API，后端会自动过滤
    const res = await getEnterpriseList({ enterpriseType: targetType })
    console.log('企业列表API完整响应:', res)
    
    // 处理响应数据 - 统一的数据提取逻辑
    let enterprises = []
    if (res.data) {
      enterprises = res.data
    } else if (Array.isArray(res)) {
      enterprises = res
    } else {
      console.error('无法解析企业列表响应:', res)
    }
    
    console.log('解析后的企业列表:', enterprises)
    console.log('企业数量:', enterprises.length)
    
    downstreamEnterprises.value = enterprises
    
    if (downstreamEnterprises.value.length === 0) {
      ElMessage.warning(`暂无可用的下游企业（企业类型：${targetType}）`)
    }
  } catch (error) {
    console.error('加载下游企业失败:', error)
    ElMessage.error('加载下游企业列表失败')
    downstreamEnterprises.value = []
  }
}

const handleAdd = () => {
  dialogTitle.value = '创建批号'
  Object.assign(form, {
    upstreamBatchId: null,
    downstreamEnterpriseId: null,
    productVariety: '',
    certNo: ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    const data = {
      enterpriseId: userInfo.value.enterpriseId,
      ...form
    }
    
    // 养殖企业创建批号
    if (userInfo.value.userType === 1) {
      await createBreedingBatch(data)
      ElMessage.success('批号创建成功，请发布后发起确认请求')
    } else {
      // 其他企业不应该手动创建批号
      ElMessage.error('下游批号应由确认请求自动生成')
      return
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleInitiateConfirm = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发起确认请求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await initiateConfirmation({
      initiateEnterpriseId: userInfo.value.enterpriseId,
      batchId: row.batchId
    })
    ElMessage.success('已发起确认请求')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleGenerateCode = async (row: any) => {
  try {
    const res = await generateTraceCode(row.batchId)
    if (res.data && res.data.code === '00000') {
      ElMessage.success(`溯源码生成成功: ${res.data.data.traceCode}`)
      loadData() // 刷新列表
    } else {
      ElMessage.error((res.data && res.data.message) || '生成溯源码失败')
    }
  } catch (error: any) {
    console.error(error)
    ElMessage.error(error.response?.data?.message || '生成溯源码失败')
  }
}

const handleOffline = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要下架该批号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await offlineBatch(row.batchId)
    ElMessage.success('下架成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleTrace = async (row: any) => {
  try {
    const res = await traceBatch(row.batchId)
    traceList.value = res.data
    traceDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布该批号吗？发布后将自动发起确认请求', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishBatch(row.batchId)
    ElMessage.success('发布成功，已自动发起确认请求')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})

// 组件卸载时清理所有异步资源
onUnmounted(() => {
  // 关闭所有对话框
  dialogVisible.value = false
  traceDialogVisible.value = false
  publishDialogVisible.value = false
  // 停止加载状态
  loading.value = false
})
</script>

<style scoped lang="scss">
.batch {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
