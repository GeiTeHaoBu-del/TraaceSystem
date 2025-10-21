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
            <el-table-column prop="batchCode" label="批次编号" width="120" />
            <el-table-column prop="productName" label="产品名称" width="120" />
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="unit" label="单位" width="80" />
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
    console.log('加载待确认的请求数据')
    
    // 只查询待确认的请求
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      receiveEnterpriseId: userInfo.value.enterpriseId,
      confirmStatus: 0 // 只显示待确认的
    }

    console.log('查询参数:', params)
    
    const res = await getConfirmationPage(params)
    console.log('查询结果:', res)
    
    // 格式化数据
    tableData.value = (res.data?.records || []).map((item: any) => ({
      ...item,
      // 添加其他需要的字段处理
      productName: item.productName || '暂无',
      quantity: item.quantity || 0,
      unit: item.unit || '个',
      batchCode: item.batchCode || '未知'
    }))
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('加载确认请求数据失败:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleConfirm = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要确认该请求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await confirmRequest(row.confirmId)
    ElMessage.success('确认成功')
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
