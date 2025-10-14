<template>
  <div class="certificate">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>证件管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">添加证件</el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="certTypeId" label="证件类型" width="150">
          <template #default="{ row }">
            {{ getCertTypeName(row.certTypeId) }}
          </template>
        </el-table-column>
        <el-table-column prop="certNo" label="证件编号" width="200" />
        <el-table-column prop="validUntil" label="有效期至" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '有效' : '过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="证件类型" prop="certTypeId">
          <el-select v-model="form.certTypeId" placeholder="请选择证件类型">
            <el-option
              v-for="item in certTypeList"
              :key="item.certTypeId"
              :label="item.certTypeName"
              :value="item.certTypeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="证件编号" prop="certNo">
          <el-input v-model="form.certNo" placeholder="请输入证件编号" />
        </el-form-item>
        <el-form-item label="有效期至" prop="validUntil">
          <el-date-picker
            v-model="form.validUntil"
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getEnterpriseCertificates,
  addEnterpriseCertificate,
  updateEnterpriseCertificate,
  deleteEnterpriseCertificate,
  getCertificateTypeByEnterpriseType
} from '@/api/certificate'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加证件')
const formRef = ref()
const certTypeList = ref([])

const form = reactive({
  certId: null,
  certTypeId: null,
  certNo: '',
  validUntil: ''
})

const formRules = {
  certTypeId: [{ required: true, message: '请选择证件类型', trigger: 'change' }],
  certNo: [{ required: true, message: '请输入证件编号', trigger: 'blur' }],
  validUntil: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const getCertTypeName = (certTypeId: number) => {
  const certType: any = certTypeList.value.find((ct: any) => ct.certTypeId === certTypeId)
  return certType ? certType.certTypeName : '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const eid = userInfo.value?.enterpriseId
    if (!eid) {
      tableData.value = []
      return
    }
    const res = await getEnterpriseCertificates(eid)
    tableData.value = res.data || res
  } catch (error) {
    console.error(error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const loadCertTypes = async () => {
  try {
    const res = await getCertificateTypeByEnterpriseType(userInfo.value.userType)
    certTypeList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加证件'
  Object.assign(form, {
    certId: null,
    certTypeId: null,
    certNo: '',
    validUntil: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑证件'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    const data = {
      ...form,
      enterpriseId: userInfo.value.enterpriseId
    }
    if (form.certId) {
      await updateEnterpriseCertificate(form.certId, data)
      ElMessage.success('修改成功')
    } else {
      await addEnterpriseCertificate(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该证件吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEnterpriseCertificate(row.certId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCertTypes()
  loadData()
})

// 如果有定时器、弹窗等资源，建议在 onUnmounted 清理
onUnmounted(() => {
  dialogVisible.value = false
})
</script>

<style scoped lang="scss">
.certificate {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
