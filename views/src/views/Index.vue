<template>
  <div class="main">
    <!--主容器-->
    <div class="container mx-auto p-4 content">
      <!--导航轮播功能区-->
      <div>
        <div class="flex">
          <div class="flex-initial bg-gray-500 w-3/4">
            <el-carousel height="550px">
              <el-carousel-item v-for="item in 6" :key="item"></el-carousel-item>
            </el-carousel>
          </div>
          <div class="grid place-content-center w-1/4 ml-3">
            <div class="flex items-center">

              <div class="grid grid-cols-2">
                <div class="items-center p-3">
                  <div class="bg-indigo-500 h-24 w-24 text-white text-center font-extrabold flex items-center justify-center rounded-full"></div>
                </div>

                <div class="p-3">

                  <div class="grid grid-rows-2 grid-flow-col gap-4">
                    <div class="text-gray-400">Hi~欢迎逛京东！</div>
                    <div><a href="/login" class="text-gray-400 mr-2">登录</a><a href="/register" class="text-gray-400">注册</a></div>
                  </div>

                </div>

              </div>
            </div>

            <div class="mt-2">
              <div class="grid grid-flow-col auto-cols-max overflow-hidden">
                <div class="p-4">
                  <p class="text-lg text-center text-gray-400">商城快报</p>
                  <div class="flex mt-2 text-sm" v-for="item in 5" :key="item">
                    <div class="w-1/6 text-red-400 bg-gray-300 text-center mr-3 px-1">HOT</div>
                    <div class="w-5/6 overscroll-none whitespace-nowrap text-gray-400"><a href="/">西部数据移动硬盘，办公室工作</a></div>
                  </div>
                </div>
              </div>
            </div>

            <div class="grid grid-cols-3 gap-4 mt-2 justify-items-center">
              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconjipiao"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">机票</a></div>
              </div>

              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconhuafei"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">话费</a></div>
              </div>

              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconjiudian"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">酒店</a></div>
              </div>

              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconlicai"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">理财</a></div>
              </div>

              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconyouxi"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">游戏</a></div>
              </div>

              <div class="items-center">
                <div>
                  <svg class="icon text-3xl text-center" aria-hidden="true">
                    <use xlink:href="#iconjiayouqia"></use>
                  </svg>
                </div>
                <div class="text-center text-sm text-gray-500 mt-2"><a href="/">加油卡</a></div>
              </div>

            </div>
          </div>
        </div>
      </div>
      <!--        滚动京东秒杀区-->
      <div>
        <div class="flex mt-2">
          <div class="flex-initial bg-red-500 w-1/4 h-96">
            <p class="text-4xl text-white w-full text-center mt-7">商城秒杀</p>
            <div class="text-center mt-10">
              <svg class="icon text-9xl" aria-hidden="true">
                <use xlink:href="#iconthunderbolt-fill"></use>
              </svg>
            </div>
            <p class="text-2xl text-white text-center mt-4">倒计时</p>
          </div>
          <div class="grid place-content-center w-3/4 px-2">
            <el-row :gutter="20">
              <el-col :span="6"><div class="grid-content bg-purple"><el-image :src="src" :fit="fit"></el-image></div></el-col>
              <el-col :span="6"><div class="grid-content bg-purple"><el-image :src="src" :fit="fit"></el-image></div></el-col>
              <el-col :span="6"><div class="grid-content bg-purple"><el-image :src="src" :fit="fit"></el-image></div></el-col>
              <el-col :span="6"><div class="grid-content bg-purple"><el-image :src="src" :fit="fit"></el-image></div></el-col>
            </el-row>
          </div>
        </div>
      </div>

      <div>
        <div class="flex mt-2">
          <div class="place-content-center w-full">
            <el-carousel :interval="4000" type="card">
              <el-carousel-item v-for="item in 6" :key="item">
                <el-image :src="src">
                  <div slot="placeholder" class="image-slot">
                    加载中<span class="dot">...</span>
                  </div>
                </el-image>
                <p class="text-center mt-10">安踏男鞋 运动鞋男春...</p>
              </el-carousel-item>
            </el-carousel>
          </div>
        </div>
      </div>

      <!--        特价闪购区-->
      <div>
        <div class="flex mt-2 w-100">
          <div class="flex-initial w-1/2 p-2">
            <el-card class="box-card h-96">
              <template #header>
                <div class="card-header">
                  <span class="text-2xl font-black">每日特价</span>
                  <el-button class="button text-gray-400" type="text">更多</el-button>
                </div>
              </template>
              <div class="rounded-t-xl overflow-hidden bg-gradient-to-r from-fuchsia-50 to-fuchsia-100 p-10">
                <div class="h-64 grid grid-rows-3 grid-flow-col gap-4">
                  <div class="row-span-3 rounded-md flex justify-center items-center text-gray-400  font-extrabold">
                    <div><img class="" src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt=""></div>
                  </div>
                  <div class="col-span-2 rounded-md flex justify-center items-center text-gray-400 text-2xl font-extrabold">
                    <div><img class="" src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt=""></div>
                  </div>
                  <div class="row-span-2 col-span-2 rounded-md flex justify-center items-center text-gray-400 text-2xl font-extrabold">
                    <img src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt="">
                  </div>
                </div>
              </div>
            </el-card>
          </div>
          <div class="flex-initial w-1/2 p-2">
            <el-card class="box-card h-96 text-2xl font-black">
              <template #header>
                <div class="card-header">
                  <span class="text-2xl font-black">品牌闪购</span>
                  <el-button class="button text-gray-400" type="text">更多</el-button>
                </div>
              </template>
              <div class="rounded-t-xl overflow-hidden bg-gradient-to-r from-fuchsia-50 to-fuchsia-100 p-10">
                <div class="h-64 grid grid-rows-3 grid-flow-col gap-4">
                  <div class="row-span-3 rounded-md flex justify-center items-center text-gray-400  font-extrabold">
                    <div><img class="" src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt=""></div>
                  </div>
                  <div class="col-span-2 rounded-md flex justify-center items-center text-gray-400 text-2xl font-extrabold">
                    <div><img class="" src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt=""></div>
                  </div>
                  <div class="row-span-2 col-span-2 rounded-md flex justify-center items-center text-gray-400 text-2xl font-extrabold">
                    <img src="https://img20.360buyimg.com/seckillcms/s140x140_jfs/t1/168786/36/14766/201466/605d6746E353fd3fd/b5c88fb506f55c9e.png.webp" alt="">
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>

      <!--        频道广场-->
      <div class="mt-2">
        <p class="text-2xl text-gray-400 text-center">频道广场</p>
      </div>

      <div>
        <div class="flex mt-2 justify-center items-center">
          <div class="grid grid-cols-4 gap-8">
            <div v-for="o in 8" :key="o">
              <img src="//img14.360buyimg.com/babel/s290x370_jfs/t1/123624/4/20139/166366/5fc065bfE4675d21b/40d2c7c4c506292c.jpg!cc_290x370.webp" class="lazyimg_img">
            </div>
          </div>
        </div>
      </div>

      <!--        发现好货-->

      <div class="mt-2">
        <p class="text-2xl text-gray-400 text-center">发现好货</p>
      </div>

      <div>
        <div class="flex mt-2">
          <div class="flex-initial bg-gray-600 w-1/4 h-96"></div>
          <div class="flex-initial bg-gray-300 w-3/4"></div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      value: [],
      src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
      options: [{
        value: 'zhinan',
        label: '指南',
        children: [{
          value: 'shejiyuanze',
          label: '设计原则',
          children: [{
            value: 'yizhi',
            label: '一致'
          }, {
            value: 'fankui',
            label: '反馈'
          }, {
            value: 'xiaolv',
            label: '效率'
          }, {
            value: 'kekong',
            label: '可控'
          }]
        }, {
          value: 'daohang',
          label: '导航',
          children: [{
            value: 'cexiangdaohang',
            label: '侧向导航'
          }, {
            value: 'dingbudaohang',
            label: '顶部导航'
          }]
        }]
      }, {
        value: 'zujian',
        label: '组件',
        children: [{
          value: 'basic',
          label: 'Basic',
          children: [{
            value: 'layout',
            label: 'Layout 布局'
          }, {
            value: 'color',
            label: 'Color 色彩'
          }, {
            value: 'typography',
            label: 'Typography 字体'
          }, {
            value: 'icon',
            label: 'Icon 图标'
          }, {
            value: 'button',
            label: 'Button 按钮'
          }]
        }, {
          value: 'form',
          label: 'Form',
          children: [{
            value: 'radio',
            label: 'Radio 单选框'
          }, {
            value: 'checkbox',
            label: 'Checkbox 多选框'
          }, {
            value: 'input',
            label: 'Input 输入框'
          }, {
            value: 'input-number',
            label: 'InputNumber 计数器'
          }, {
            value: 'select',
            label: 'Select 选择器'
          }, {
            value: 'cascader',
            label: 'Cascader 级联选择器'
          }, {
            value: 'switch',
            label: 'Switch 开关'
          }, {
            value: 'slider',
            label: 'Slider 滑块'
          }, {
            value: 'time-picker',
            label: 'TimePicker 时间选择器'
          }, {
            value: 'date-picker',
            label: 'DatePicker 日期选择器'
          }, {
            value: 'datetime-picker',
            label: 'DateTimePicker 日期时间选择器'
          }, {
            value: 'upload',
            label: 'Upload 上传'
          }, {
            value: 'rate',
            label: 'Rate 评分'
          }, {
            value: 'form',
            label: 'Form 表单'
          }]
        }, {
          value: 'data',
          label: 'Data',
          children: [{
            value: 'table',
            label: 'Table 表格'
          }, {
            value: 'tag',
            label: 'Tag 标签'
          }, {
            value: 'progress',
            label: 'Progress 进度条'
          }, {
            value: 'tree',
            label: 'Tree 树形控件'
          }, {
            value: 'pagination',
            label: 'Pagination 分页'
          }, {
            value: 'badge',
            label: 'Badge 标记'
          }]
        }, {
          value: 'notice',
          label: 'Notice',
          children: [{
            value: 'alert',
            label: 'Alert 警告'
          }, {
            value: 'loading',
            label: 'Loading 加载'
          }, {
            value: 'message',
            label: 'Message 消息提示'
          }, {
            value: 'message-box',
            label: 'MessageBox 弹框'
          }, {
            value: 'notification',
            label: 'Notification 通知'
          }]
        }, {
          value: 'navigation',
          label: 'Navigation',
          children: [{
            value: 'menu',
            label: 'NavMenu 导航菜单'
          }, {
            value: 'tabs',
            label: 'Tabs 标签页'
          }, {
            value: 'breadcrumb',
            label: 'Breadcrumb 面包屑'
          }, {
            value: 'dropdown',
            label: 'Dropdown 下拉菜单'
          }, {
            value: 'steps',
            label: 'Steps 步骤条'
          }]
        }, {
          value: 'others',
          label: 'Others',
          children: [{
            value: 'dialog',
            label: 'Dialog 对话框'
          }, {
            value: 'tooltip',
            label: 'Tooltip 文字提示'
          }, {
            value: 'popover',
            label: 'Popover 弹出框'
          }, {
            value: 'card',
            label: 'Card 卡片'
          }, {
            value: 'carousel',
            label: 'Carousel 走马灯'
          }, {
            value: 'collapse',
            label: 'Collapse 折叠面板'
          }]
        }]
      }, {
        value: 'ziyuan',
        label: '资源',
        children: [{
          value: 'axure',
          label: 'Axure Components'
        }, {
          value: 'sketch',
          label: 'Sketch Templates'
        }, {
          value: 'jiaohu',
          label: '组件交互文档'
        }]
      }]
    };
  },
  methods: {
    handleChange(value) {
      console.log(value);
    }
  }
}
</script>

<style scoped>
.main {
  display: flex;
  height: 100%;
}

.content {
  flex: 1;
}

.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 350px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #d3dce6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>