<template>
  <div class="confirmation">
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="我发起的" name="initiate">
        <el-card>
          <el-table :data="tableData" border v-loading="loading">
            <el-table-column prop="batchId" label="批号ID" width="100" />
            <el-table-column prop="confirmStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.confirmStatus)">
                  {{ getStatusName(row.confirmStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="initiateTime" label="发起时间" width="160" />
            <el-table-column prop="confirmTime" label="处理时间" width="160" />
            <el-table-column prop="rejectReason" label="拒绝原因" min-width="200" show-overflow-tooltip />
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

      <el-tab-pane label="待我确认" name="receive">
        <el-card>
          <el-table :data="tableData" border v-loading="loading">
            <el-table-column prop="batchId" label="批号ID" width="100" />
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
                <el-button 
                  link 
                  type="success" 
                  @click="handleConfirm(row)"
                  v-if="row.confirmStatus === 0"
                >
                  确认
                </el-button>
                <el-button 
                  link 
                  type="danger" 
                  @click="handleReject(row)"
                  v-if="row.confirmStatus === 0"
                >
                  拒绝
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConfirmationPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      receiveEnterpriseId: userInfo.value.enterpriseId,
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

onMounted(() => {
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
