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
            <el-button link type="primary" @click="handleTrace(row)">溯源</el-button>
            <el-button 
              link 
              type="success" 
              @click="handlePublish(row)"
              v-if="userInfo.userType === 1 && row.batchStatus === 0"
            >
              发布
            </el-button>
            <el-button 
              link 
              type="warning" 
              @click="handleInitiateConfirm(row)"
              v-if="userInfo.userType !== 1 && row.batchStatus === 0"
            >
              发起确认
            </el-button>
            <el-button 
              link 
              type="info" 
              @click="handleGenerateCode(row)"
              v-if="userInfo.userType === 4 && row.batchStatus === 2"
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
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
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

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const traceDialogVisible = ref(false)
const dialogTitle = ref('创建批号')
const formRef = ref()
const upstreamBatches = ref([])
const traceList = ref([])

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
  productVariety: '',
  certNo: ''
})

const formRules = {
  upstreamBatchId: [{ required: true, message: '请选择上游批号', trigger: 'change' }],
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
    const res = await getBatchPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      enterpriseId: userInfo.value.enterpriseId,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
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
    const res = await getAvailableUpstreamBatches(userInfo.value.userType)
    upstreamBatches.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '创建批号'
  Object.assign(form, {
    upstreamBatchId: null,
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
    if (userInfo.value.userType === 1) {
      await createBreedingBatch(data)
    } else {
      await createDownstreamBatch(data)
    }
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布该批号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishBatch(row.batchId)
    ElMessage.success('发布成功')
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
    ElMessage.success(`溯源码生成成功: ${res.data.traceCode}`)
  } catch (error) {
    console.error(error)
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

onMounted(() => {
  loadData()
})

// 组件卸载时清理所有异步资源
onUnmounted(() => {
  // 关闭所有对话框
  dialogVisible.value = false
  traceDialogVisible.value = false
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
