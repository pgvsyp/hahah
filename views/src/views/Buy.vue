<template>
  <div class="container mx-auto mt-5 p-5">
    <el-card class="box-card mb-1">
      <span class="text-xl text-gray-400 my-2">温馨提示：结算前请确认商品价格以及收货地址！</span>
    </el-card>
    <el-card class="box-card">
      <el-table
          ref="multipleTable"
          :data="tableData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="handleSelectionChange">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column
            label="添加时间"
            width="120">
          <template slot-scope="scope">{{ scope.row.date }}</template>
        </el-table-column>
        <el-table-column
            prop="address"
            label="商品名称">
        </el-table-column>
        <el-table-column
            prop="name"
            label="商品规格">
        </el-table-column>
        <el-table-column
            prop="price"
            label="价格"
            show-overflow-tooltip>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="mt-2">
      <span class="mr-2 text-gray-400">请选择收货地址：</span>
      <el-select v-model="value" placeholder="请选择">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
    </div>
    <div class="mt-2">
      <span class="mr-2 text-gray-400">总价：</span>
      <el-input
          placeholder="0.00"
          v-model="input"
          :disabled="true">
      </el-input>
    </div>
    <div style="margin-top: 20px">
      <el-button @click="toggleSelection([tableData[1], tableData[2]])">结算</el-button>
      <el-button @click="toggleSelection()">取消选择</el-button>
    </div>
  </div>

</template>

<style scoped>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>

<script>
export default {
  methods: {
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  },
  data() {
    return {
      tableData: [{
        date: '2016-05-03',
        name: '12+256 全网通 钛银黑',
        address: '小米11',
        price: 5999,
      }, {
        date: '2016-05-02',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }, {
        date: '2016-05-04',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }, {
        date: '2016-05-01',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }, {
        date: '2016-05-08',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }, {
        date: '2016-05-06',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }, {
        date: '2016-05-07',
        name: '12+256 全网通 钛银黑',
        price: 5999,
        address: '小米11'
      }],
      multipleSelection: [],
      options: [{
        value: '选项1',
        label: '收货地址'
      }, {
        value: '选项2',
        label: '收货地址'
      }, {
        value: '选项3',
        label: '收货地址'
      }, {
        value: '选项4',
        label: '收货地址'
      }, {
        value: '选项5',
        label: '收货地址'
      }],
    }
  }
}
</script>