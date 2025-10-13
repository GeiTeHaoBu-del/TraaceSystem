<template>
  <div class="trace-code">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>溯源码管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true">
        <el-form-item>
          <el-button type="primary" icon="Plus" @click="handleBatchGenerate">批量生成溯源码</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="batchNo" label="批号" width="180" />
        <el-table-column prop="traceCode" label="溯源码" min-width="200" />
        <el-table-column prop="generateTime" label="生成时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleShowQRCode(row)">查看二维码</el-button>
            <el-button link type="success" @click="handleDownloadQRCode(row)">下载</el-button>
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

    <!-- 二维码对话框 -->
    <el-dialog v-model="qrDialogVisible" title="溯源码二维码" width="400px">
      <div class="qr-container">
        <canvas ref="qrCanvas"></canvas>
        <p class="trace-code-text">{{ currentTraceCode }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getTraceCodePage, generateTraceCode } from '@/api/traceCode'
import { getBatchPage } from '@/api/batch'
import * as QRCode from 'qrcode'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const tableData = ref([])
const qrDialogVisible = ref(false)
const qrCanvas = ref<HTMLCanvasElement>()
const currentTraceCode = ref('')
const selectedBatches = ref([])
const batchList = ref([])
const selectedBatch = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTraceCodePage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.records.map((item: any) => ({
      ...item,
      qrCodeUrl: ''
    }))
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadBatches = async () => {
  try {
    const res = await getBatchPage({
      pageNum: 1,
      pageSize: 100,
      enterpriseId: userInfo.value.enterpriseId,
      batchStatus: 2
    })
    batchList.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleSelectionChange = (val: any) => {
  selectedBatches.value = val
}

const handleBatchGenerate = async () => {
  if (selectedBatches.value.length === 0) {
    ElMessage.warning('请先选择批号')
    return
  }

  loading.value = true
  try {
    for (const batch of selectedBatches.value as any[]) {
      await generateTraceCode(batch.batchId)
    }
    ElMessage.success('批量生成成功')
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleShowQRCode = async (row: any) => {
  if (!row.traceCode || row.traceCode === '-') {
    try {
      const res = await generateTraceCode(row.batchId)
      currentTraceCode.value = res.data.traceCode
    } catch (error) {
      console.error(error)
      return
    }
  } else {
    currentTraceCode.value = row.traceCode
  }

  qrDialogVisible.value = true
  
  // 等待 DOM 更新后生成二维码
  setTimeout(() => {
    if (qrCanvas.value) {
      QRCode.toCanvas(qrCanvas.value, currentTraceCode.value, {
        width: 300,
        margin: 2
      })
    }
  }, 100)
}

const handleDownloadQRCode = async (row: any) => {
  if (!row.traceCode || row.traceCode === '-') {
    ElMessage.warning('请先生成溯源码')
    return
  }

  try {
    const canvas = document.createElement('canvas')
    await QRCode.toCanvas(canvas, row.traceCode, {
      width: 300,
      margin: 2
    })
    
    const link = document.createElement('a')
    link.download = `溯源码_${row.batchNo}.png`
    link.href = canvas.toDataURL()
    link.click()
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  loadData()
  loadBatches()
})
</script>

<style scoped lang="scss">
.trace-code {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .qr-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;

    canvas {
      margin-bottom: 20px;
    }

    .trace-code-text {
      font-size: 14px;
      color: #666;
      word-break: break-all;
      text-align: center;
    }
  }
}
</style>
