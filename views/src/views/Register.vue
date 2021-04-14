<template>
  <div class="register-main">
        <div class="register-wrapper">
            <div id="register">
                <p class="title">注册</p>
                <el-form
                        :model="ruleForm2"
                        status-icon
                        :rules="rules2"
                        ref="ruleForm2"
                        label-width="0"
                        class="demo-ruleForm"
                >
                    <el-form-item prop="username">
                        <el-input v-model="ruleForm2.username" auto-complete="off" placeholder="请输入手机号/邮箱"></el-input>
                    </el-form-item>
                    <el-form-item prop="eMailCode" class="code">
                        <el-input v-model="ruleForm2.eMailCode" placeholder="验证码"></el-input>
                        <el-button type="primary" :disabled='isDisabled' @click="sendCode">{{buttonText}}</el-button>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input type="password" v-model="ruleForm2.password" auto-complete="off"
                                  placeholder="输入密码"></el-input>
                    </el-form-item>
                    <el-form-item prop="checkPass">
                        <el-input type="password" v-model="ruleForm2.checkPass" auto-complete="off"
                                  placeholder="确认密码"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitForm('ruleForm2')" style="width:100%;">注册</el-button>
                        <p class="login" @click="gotoLogin">已有账号？立即登录</p>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>
<script>
    export default {

        name: "Register",
        data() {
            // <!--验证手机号是否合法-->
            let checkUsername = (rule, value, callback) => {
                let pattern = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
                if (value === '') {
                    callback(new Error('请输入手机号码/邮箱账号'))
                } else if (!pattern.test(value)) {
                    callback(new Error('手机号码/邮箱账号不合法'))
                } else {
                    callback()
                    console.log();
                }
            }
            //  <!--验证码是否为空-->
            let checkSmscode = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入验证码'))
                } else if (this.ruleForm2.eMailVerificationCode !== value) {
                    callback(new Error('请输入正确验证码'))
                } else {
                    callback()
                }
            }
            // <!--验证密码-->
            let validatePass = (rule, value, callback) => {
                if (value === "") {
                    callback(new Error("请输入密码"))
                } else {
                    if (this.ruleForm2.checkPass !== "") {
                        this.$refs.ruleForm2.validateField("checkPass");
                    }
                    callback()
                }
            }
            // <!--二次验证密码-->
            let validatePass2 = (rule, value, callback) => {
                if (value === "") {
                    callback(new Error("请再次输入密码"));
                } else if (value !== this.ruleForm2.password) {
                    callback(new Error("两次输入密码不一致!"));
                } else {
                    callback();
                }
            };
            return {
                ruleForm2: {
                    password: "",
                    checkPass: "",
                    username: "",
                    eMailCode: "",
                    eMailVerificationCode: "",
                },
                rules2: {
                    password: [{validator: validatePass, trigger: 'change'}],
                    checkPass: [{validator: validatePass2, trigger: 'change'}],
                    username: [{validator: checkUsername, trigger: 'change'}],
                    eMailCode: [{validator: checkSmscode, trigger: 'change'}],
                },
                buttonText: '发送验证码',
                isDisabled: false, // 是否禁止点击发送验证码按钮
                flag: true
            }
        },
        methods: {
            // <!--发送验证码-->
            sendCode() {
                let eMail = this.ruleForm2.username;
                if (this.checkMobileOrEmail(eMail)) {
                    let time = 60
                    this.buttonText = '已发送'
                    this.isDisabled = true
                    if (this.flag) {
                        this.flag = false;
                        let timer = setInterval(() => {
                            time--;
                            this.buttonText = time + ' 秒'
                            if (time === 0) {
                                clearInterval(timer);
                                this.buttonText = '重新获取'
                                this.isDisabled = false
                                this.flag = true;
                            }
                        }, 1000)
                    }
                }
                //异步请求获取验证码
                this.axios.post('/user/sendEmail', {
                    eMail: this.ruleForm2.username
                })
                    .then(response => {
                        this.ruleForm2.eMailVerificationCode = response.data.eMailCode;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
                //
                // let tel = this.ruleForm2.tel
                // if (this.checkMobile(tel)) {
                //   console.log(tel)
                //   let time = 60
                //   this.buttonText = '已发送'
                //   this.isDisabled = true
                //   if (this.flag) {
                //     this.flag = false;
                //     let timer = setInterval(() => {
                //       time--;
                //       this.buttonText = time + ' 秒'
                //       if (time === 0) {
                //         clearInterval(timer);
                //         this.buttonText = '重新获取'
                //         this.isDisabled = false
                //         this.flag = true;
                //       }
                //     }, 1000)
                //   }
                // }
            },
            // <!--提交注册-->
            submitForm(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        this.axios.post('/user', {
                            username: this.ruleForm2.username,
                            password: this.ruleForm2.password,
                        })
                            .then(response => {
                                this.$router.push('/login')
                                alert('注册成功')
                            })
                            .catch(function (error) {
                                console.log(error);
                            });
                        setTimeout(() => {
                            alert('注册成功')
                        }, 400);
                    } else {
                        console.log("error submit!!");
                        return false;
                    }
                })
            },
            // <!--进入登录页-->
            gotoLogin() {
                this.$router.push({
                    path: "/login"
                });
            },
            // 验证手机号
            checkMobileOrEmail(str) {
                let re = /^1\d{10}$/
                let pattern = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
                if (re.test(str) || pattern.test(str)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    };
</script>
<style scoped>
  .loading-wrapper {
    position: fixed;
    top: 0px;
    right: 0;
    left: 0;
    bottom: 0;
    background: #aedff8;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .register-wrapper img {
    position: absolute;
    z-index: 1;
  }
  #register {
    max-width: 440px;
    width: 400px;
    margin: 60px auto;
    background: #fff;
    padding: 20px 40px;
    border-radius: 10px;
    position: relative;
    top: 15%;
    right: -30%;
    z-index: 9;
  }
  .title {
    font-size: 26px;
    line-height: 50px;
    font-weight: bold;
    margin: 10px;
    text-align: center;
  }
  .el-form-item {
    text-align: center;
  }
  .login {
    margin-top: 10px;
    font-size: 14px;
    line-height: 22px;
    color: #1ab2ff;
    cursor: pointer;
    text-align: left;
    text-indent: 8px;
    width: 160px;
  }
  .login:hover {
    color: #2c2fd6;
  }
  .code >>> .el-form-item__content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .code button {
    margin-left: 20px;
    width: 140px;
    text-align: center;
  }
  .el-button--primary:focus {
    background: #409EFF;
    border-color: #409EFF;
    color: #fff;
  }
  .register-main{
    display: flex;
    height: 100%;
    background-image: url('../assets/images/register.svg');
    background-repeat: no-repeat;
    background-position: center;
    background-size: 100% 100%;
    background-attachment:fixed;
  }
  .register-wrapper {
    flex: 1;
  }
  svg {
    width: fit-content;
  }
</style>
