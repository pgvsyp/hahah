<template>
  <div>
    <el-card class="box-card mb-1">
      <p class="text-gray-400 mb-2">个人信息概览</p>
    </el-card>
    <el-card class="box-card mb-2">
      <el-form ref="form" :model="form">
        <el-form-item label="用户名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="注册时间"
                      suffix-icon="el-icon-date">
          <el-input v-model="form.name" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="上次登录时间"
                      suffix-icon="el-icon-date">
          <el-input v-model="form.name" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="账户状态">
          <el-switch v-model="form.delivery"></el-switch>
        </el-form-item>
        <el-form-item label="个人喜好">
          <el-checkbox-group v-model="form.type">
            <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>
            <el-checkbox label="地推活动" name="type"></el-checkbox>
            <el-checkbox label="线下主题活动" name="type"></el-checkbox>
            <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="特殊资源">
          <el-radio-group v-model="form.resource">
            <el-radio label="线上品牌商赞助"></el-radio>
            <el-radio label="线下场地免费"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="form.desc"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">立即修改</el-button>
          <el-button>取消修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "UesrMain",
  data() {
    return {
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      }
    }
  },
  methods: {
    onSubmit() {
      console.log('submit!');
    }
  }
}
</script>

<style scoped>

</style>