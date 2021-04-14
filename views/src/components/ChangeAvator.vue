<template>
  <div>
    <el-card class="box-card mb-1">
      <span class="text-gray-400 my-4">请在此处上传新的头像</span>
    </el-card>
    <el-card class="box-card">
      <el-upload
          class="upload-demo"
          drag
          action="https://jsonplaceholder.typicode.com/posts/"
          multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "ChangeAvator",
}
</script>

<style scoped>

</style>