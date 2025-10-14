<template>
  <div class="statistics">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据统计</span>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="企业总数" :value="stats.total">
              <template #prefix>
                <el-icon style="vertical-align: middle">
                  <OfficeBuilding />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="养殖企业" :value="stats.typeStats['1'] || 0">
              <template #prefix>
                <el-icon style="vertical-align: middle">
                  <Box />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="屠宰企业" :value="stats.typeStats['2'] || 0">
              <template #prefix>
                <el-icon style="vertical-align: middle">
                  <ShoppingCart />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="批发+零售" :value="(stats.typeStats['3'] || 0) + (stats.typeStats['4'] || 0)">
              <template #prefix>
                <el-icon style="vertical-align: middle">
                  <ShoppingBag />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>企业类型分布</span>
            </template>
            <div ref="typeChart" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>省份分布（TOP 10）</span>
            </template>
            <div ref="provinceChart" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>企业注册趋势</span>
            </template>
            <div ref="trendChart" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { getEnterpriseStatistics } from '@/api/enterprise'
import * as echarts from 'echarts'

const loading = ref(false)

const stats = ref({
  total: 0,
  provinceStats: {} as any,
  monthStats: {} as any,
  typeStats: {} as any
})

const typeChart = ref()
const provinceChart = ref()
const trendChart = ref()
const chartInstances = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEnterpriseStatistics()
    // 适配 request 响应结构
    const data = res?.data || res
    // 统计总数
    stats.value = {
      total: data.typeStats ? Object.values(data.typeStats).map(Number).reduce((a, b) => a + b, 0) : 0,
      provinceStats: data.provinceStats || {},
      monthStats: data.monthStats || {},
      typeStats: data.typeStats || {}
    }
    await nextTick()
    initCharts()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  // 销毁旧图表
  chartInstances.value.forEach((chart) => chart?.dispose?.())
  chartInstances.value = []
  if (typeChart.value) {
    const chart = echarts.init(typeChart.value)
    chartInstances.value.push(chart)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '企业类型',
          type: 'pie',
          radius: '50%',
          data: [
            { value: stats.value.typeStats['1'] || 0, name: '养殖企业' },
            { value: stats.value.typeStats['2'] || 0, name: '屠宰企业' },
            { value: stats.value.typeStats['3'] || 0, name: '批发企业' },
            { value: stats.value.typeStats['4'] || 0, name: '零售企业' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  }
  if (provinceChart.value) {
    const chart = echarts.init(provinceChart.value)
    chartInstances.value.push(chart)
    const provinceData = Object.entries(stats.value.provinceStats)
      .sort((a: any, b: any) => b[1] - a[1])
      .slice(0, 10)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        data: provinceData.map(item => item[0])
      },
      series: [
        {
          name: '企业数量',
          type: 'bar',
          data: provinceData.map(item => item[1]),
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  }
  if (trendChart.value) {
    const chart = echarts.init(trendChart.value)
    chartInstances.value.push(chart)
    const monthData = Object.entries(stats.value.monthStats)
      .sort((a, b) => a[0].localeCompare(b[0]))
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: monthData.map(item => item[0])
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '注册企业数',
          type: 'line',
          stack: 'Total',
          data: monthData.map(item => item[1]),
          areaStyle: {},
          smooth: true,
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  }
}

onMounted(() => {
  loadData()
})

onUnmounted(() => {
  chartInstances.value.forEach((chart) => chart?.dispose?.())
  chartInstances.value = []
})
</script>

<style scoped lang="scss">
.statistics {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
