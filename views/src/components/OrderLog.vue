<template>
  <div>
    <el-card class="box-card mb-1">
      <span class="text-gray-400">此处展示您的订单相关信息！</span>
    </el-card>
    <el-card class="box-card">
      <el-table
          :data="tableData"
          style="width: 100%"
          :default-sort = "{prop: 'date', order: 'descending'}"
      >
        <el-table-column
            prop="date"
            label="订单日期"
            sortable
            width="180">
        </el-table-column>
        <el-table-column
            prop="name"
            label="收货人"
            sortable
            width="180">
        </el-table-column>
        <el-table-column
            prop="name"
            label="商品"
            width="180">
        </el-table-column>
        <el-table-column
            prop="address"
            label="收货地址"
            :formatter="formatter">
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "OrderLog",
  data() {
    return {
      tableData: [{
        date: '2016-05-02',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1518 弄'
      }, {
        date: '2016-05-04',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1517 弄'
      }, {
        date: '2016-05-01',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1519 弄'
      }, {
        date: '2016-05-03',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1516 弄'
      }]
    }
  },
  methods: {
    formatter(row, column) {
      return row.address;
    }
  }
}
</script>

<style scoped>

</style>