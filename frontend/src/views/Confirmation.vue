<template>
  <div class="confirmation">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待我确认" name="receive">
        <el-card>
          <!-- 搜索表单 -->
          <div class="search-form" style="margin-bottom: 20px;">
            <el-form :model="searchForm" inline>
              <el-form-item label="状态">
                <el-select v-model="searchForm.confirmStatus" placeholder="请选择状态" clearable>
                  <el-option label="待确认" :value="0" />
                  <el-option label="已确认" :value="1" />
                  <el-option label="已拒绝" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadData">查询</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="tableData" border v-loading="loading">
            <el-table-column prop="batchId" label="批号ID" width="100" />
            <el-table-column prop="batchNo" label="批次编号" width="180" />
            <el-table-column prop="productVariety" label="产品名称" width="120" />
            <el-table-column prop="certNo" label="证件编号" width="150" />
            <el-table-column prop="initiateEnterpriseName" label="发起企业" width="150" />
            <el-table-column prop="confirmStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.confirmStatus)">
                  {{ getStatusName(row.confirmStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="initiateTime" label="发起时间" width="160" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button-group v-if="row.confirmStatus === 0">
                  <el-button 
                    type="success"
                    size="small"
                    @click="handleConfirm(row)"
                  >
                    确认
                  </el-button>
                  <el-button 
                    type="danger"
                    size="small"
                    @click="handleReject(row)"
                  >
                    拒绝
                  </el-button>
                </el-button-group>
                <el-button 
                  type="primary"
                  size="small"
                  @click="viewBatchDetails(row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

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
      </el-tab-pane>
    </el-tabs>

    <!-- 确认对话框 - 需要指定下游企业 -->
    <el-dialog v-model="confirmDialogVisible" title="确认批号" width="500px">
      <el-form :model="confirmForm" :rules="confirmRules" ref="confirmFormRef" label-width="120px">
        <el-form-item label="批号信息">
          <div style="color: #606266;">
            <p>批号: {{ currentBatch?.batchNo }}</p>
            <p>产品品种: {{ currentBatch?.productVariety }}</p>
            <p>发起企业: {{ currentBatch?.initiateEnterpriseName }}</p>
          </div>
        </el-form-item>
        <el-form-item label="指定下游企业" prop="downstreamEnterpriseId" v-if="!isRetailEnterprise">
          <el-select 
            v-model="confirmForm.downstreamEnterpriseId" 
            placeholder="请选择下游企业" 
            @focus="loadDownstreamEnterprises"
            style="width: 100%"
          >
            <el-option
              v-for="item in downstreamEnterprises"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-alert 
          v-if="isRetailEnterprise"
          title="您是零售企业，确认后将自动生成溯源码"
          type="info"
          :closable="false"
          style="margin-top: 10px"
        />
      </el-form>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSubmit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        rows="4"
        placeholder="请输入拒绝原因"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRejectSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getConfirmationPage, confirmRequest, rejectRequest } from '@/api/confirmation'
import { getEnterpriseList } from '@/api/enterprise'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const tableData = ref([])
const activeTab = ref('initiate')
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentConfirmId = ref<number | null>(null)

const searchForm = reactive({
  confirmStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusName = (status: number) => {
  const map: any = { 0: '待确认', 1: '已确认', 2: '已拒绝' }
  return map[status] || '-'
}

const getStatusTag = (status: number) => {
  const map: any = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || ''
}
// 重置搜索表单
const resetSearch = () => {
  searchForm.confirmStatus = null
  loadData()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    console.log('=== 开始加载确认请求数据 ===')
    console.log('当前用户信息:', userInfo.value)
    
    // 查询指定给当前企业的确认请求
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      receiveEnterpriseId: userInfo.value.enterpriseId,
      confirmStatus: searchForm.confirmStatus // 不要使用默认值，让用户选择
    }

    console.log('查询参数:', params)
    
    const res = await getConfirmationPage(params)
    console.log('API完整响应:', res)
    console.log('响应数据类型:', typeof res)
    console.log('res.data:', res.data)
    
    // 处理响应数据 - 统一的数据提取逻辑
    let pageData = null
    if (res.data && res.data.records) {
      // 标准格式：res.data.records
      pageData = res.data
    } else if (res.records) {
      // 直接格式：res.records
      pageData = res
    } else {
      console.error('无法识别的响应数据格式:', res)
      pageData = { records: [], total: 0 }
    }
    
    console.log('提取的分页数据:', pageData)
    console.log('记录数组:', pageData.records)
    console.log('记录总数:', pageData.total)
    
    // 格式化数据 - 从批号信息中提取数据
    tableData.value = (pageData.records || []).map((item: any) => {
      console.log('处理单条记录:', item)
      return {
        ...item,
        batchCode: item.batchNo || '未知',
        productName: item.productVariety || '暂无',
        quantity: item.quantity || '-',
        unit: item.unit || '-'
      }
    })
    pagination.total = pageData.total || 0
    
    console.log('最终的表格数据:', tableData.value)
    console.log('数据条数:', tableData.value.length)
    console.log('=== 数据加载完成 ===')
  } catch (error) {
    console.error('加载确认请求数据失败:', error)
    if (error.response) {
      console.error('错误响应:', error.response)
    }
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const confirmDialogVisible = ref(false)
const currentBatch = ref<any>(null)
const downstreamEnterprises = ref([])
const confirmFormRef = ref()

const confirmForm = reactive({
  downstreamEnterpriseId: null as number | null
})

const confirmRules = {
  downstreamEnterpriseId: [{ required: true, message: '请选择下游企业', trigger: 'change' }]
}

const isRetailEnterprise = computed(() => {
  return userInfo.value.userType === 4 // 零售企业
})

const loadDownstreamEnterprises = async () => {
  try {
    console.log('开始加载下游企业，当前企业类型:', userInfo.value.userType)
    
    // 根据当前企业类型确定下游企业类型
    let targetType = 0
    if (userInfo.value.userType === 2) {
      targetType = 3 // 屠宰企业 -> 批发企业
    } else if (userInfo.value.userType === 3) {
      targetType = 4 // 批发企业 -> 零售企业
    } else {
      console.log('零售企业不需要指定下游企业')
      downstreamEnterprises.value = []
      return
    }
    
    console.log('目标下游企业类型:', targetType)
    
    const res = await getEnterpriseList({ enterpriseType: targetType })
    console.log('企业列表API响应:', res)
    
    let enterprises = []
    if (res.data) {
      enterprises = res.data
    } else if (Array.isArray(res)) {
      enterprises = res
    }
    
    downstreamEnterprises.value = enterprises
    console.log('下游企业列表:', downstreamEnterprises.value)
    
    if (downstreamEnterprises.value.length === 0) {
      ElMessage.warning(`暂无可用的下游企业（企业类型：${targetType}）`)
    }
  } catch (error) {
    console.error('加载下游企业失败:', error)
    ElMessage.error('加载下游企业列表失败')
    downstreamEnterprises.value = []
  }
}

const handleConfirm = async (row: any) => {
  currentBatch.value = row
  confirmForm.downstreamEnterpriseId = null
  
  // 如果是零售企业，直接显示确认对话框，不需要选择下游企业
  if (isRetailEnterprise.value) {
    confirmDialogVisible.value = true
  } else {
    // 非零售企业需要先加载下游企业列表
    await loadDownstreamEnterprises()
    confirmDialogVisible.value = true
  }
}

const handleConfirmSubmit = async () => {
  // 如果不是零售企业，需要验证表单
  if (!isRetailEnterprise.value) {
    try {
      await confirmFormRef.value.validate()
    } catch (error) {
      return
    }
  }
  
  try {
    await ElMessageBox.confirm('确定要确认该请求吗？确认后将自动生成您的批号', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用确认API，传递下游企业ID
    await confirmRequest(currentBatch.value.confirmId, {
      downstreamEnterpriseId: confirmForm.downstreamEnterpriseId
    })
    
    ElMessage.success('确认成功，已自动生成您的批号')
    confirmDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleReject = (row: any) => {
  currentConfirmId.value = row.confirmId
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const handleRejectSubmit = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    await rejectRequest(currentConfirmId.value!, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 查看批次详情
const viewBatchDetails = (row: any) => {
  ElMessage.info('正在开发中')
  // TODO: 实现查看批次详情的功能
  console.log('查看批次详情:', row)
}

// 初始化时加载数据
onMounted(() => {
  activeTab.value = 'receive' // 固定为"待我确认"
  loadData()
})

onUnmounted(() => {
  // 组件卸载时关闭所有对话框
  rejectDialogVisible.value = false
})
</script>

<style scoped lang="scss">
.confirmation {
  .el-tabs {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
  }
}
</style>
