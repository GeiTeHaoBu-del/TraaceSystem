<template>
  <div class="enterprise">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增企业</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="企业类型">
          <el-select v-model="searchForm.enterpriseType" placeholder="请选择" clearable>
            <el-option label="养殖企业" :value="1" />
            <el-option label="屠宰企业" :value="2" />
            <el-option label="批发企业" :value="3" />
            <el-option label="零售企业" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="searchForm.province" placeholder="请输入省份" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="enterpriseName" label="企业名称" min-width="150" />
        <el-table-column prop="enterpriseType" label="企业类型" width="100">
          <template #default="{ row }">
            {{ getEnterpriseTypeName(row.enterpriseType) }}
          </template>
        </el-table-column>
        <el-table-column prop="province" label="省份" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="registerTime" label="注册时间" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              link 
              :type="row.status === 1 ? 'warning' : 'success'" 
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="form.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="form.enterpriseType" placeholder="请选择企业类型">
            <el-option label="养殖企业" :value="1" />
            <el-option label="屠宰企业" :value="2" />
            <el-option label="批发企业" :value="3" />
            <el-option label="零售企业" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" type="textarea" rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="注册时间" prop="registerTime">
          <el-date-picker
            v-model="form.registerTime"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getEnterprisePage,
  createEnterprise,
  updateEnterprise,
  deleteEnterprise,
  updateEnterpriseStatus
} from '@/api/enterprise'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增企业')
const formRef = ref()

// 搜索区域表单字段调整
const searchForm = reactive({
  enterpriseType: null,
  province: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  enterpriseId: null,
  enterpriseName: '',
  enterpriseType: null,
  province: '',
  contactPhone: '',
  address: '',
  registerTime: ''
})

const formRules = {
  enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  enterpriseType: [{ required: true, message: '请选择企业类型', trigger: 'change' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  registerTime: [{ required: true, message: '请选择注册时间', trigger: 'change' }]
}

const getEnterpriseTypeName = (type: number) => {
  const map: any = { 1: '养殖企业', 2: '屠宰企业', 3: '批发企业', 4: '零售企业' }
  return map[type] || '-'
}

const loadData = async () => {
  loading.value = true
  try {
    // 只传递有值的查询参数，企业类型和省份
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (searchForm.enterpriseType) params.enterpriseType = searchForm.enterpriseType
    if (searchForm.province) params.province = searchForm.province
    const res = await getEnterprisePage(params)
    const data = res?.data || res
    tableData.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.enterpriseType = null
  searchForm.province = ''
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增企业'
  Object.assign(form, {
    enterpriseId: null,
    enterpriseName: '',
    enterpriseType: null,
    province: '',
    contactPhone: '',
    address: '',
    registerTime: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑企业'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.enterpriseId) {
      await updateEnterprise(form.enterpriseId, form)
      ElMessage.success('修改成功')
    } else {
      await createEnterprise(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleStatusChange = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${action}该企业吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateEnterpriseStatus(row.enterpriseId, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该企业吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEnterprise(row.enterpriseId)
    ElMessage.success('删除成功')
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
  dialogVisible.value = false
})
</script>

<style scoped lang="scss">
.enterprise {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
