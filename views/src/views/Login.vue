<template>
    <div class="register-main">
        <div class="register-wrapper">
            <div id="register">
                <p class="title">登录</p>
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
                    <el-form-item prop="password">
                        <el-input type="password" v-model="ruleForm2.password" auto-complete="off"
                                  placeholder="输入密码"></el-input>
                    </el-form-item>
                    <el-form-item prop="smscode" class="code">
                        <el-input v-model="ruleForm2.smscode" placeholder="验证码"></el-input>
                        <el-button type="primary" :disabled='isDisabled' @click="sendCode">{{buttonText}}</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitForm('ruleForm2')" style="width:100%;">登录</el-button>
                        <p class="login" @click="gotoLogin">没有账号？立即注册</p>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        name: "Login",
        data() {
            // <!--验证手机号是否合法-->
            let checkTel = (rule, value, callback) => {
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
            return {
                ruleForm2: {
                    password: "",
                    username: "",
                    smscode: "",
                    eMailVerificationCode: ""
                },
                rules2: {
                    password: [{validator: validatePass, trigger: 'change'}],
                    username: [{validator: checkTel, trigger: 'change'}],
                    smscode: [{validator: checkSmscode, trigger: 'change'}],
                },
                buttonText: '发送验证码',
                isDisabled: false, // 是否禁止点击发送验证码按钮
                flag: true,
                Auth: this.GLOBAL.Auth,
            }
        },

        created() {//此方法会导致登录后在进入注册页面,注册完后跳转到登录页面时,直接进入首页
            if (!this.GLOBAL.isFromRegister) {//故先进行判断,是否来自注册页面,用一个全局变量isFromRegister控制跳转首页否
                this.GLOBAL.isFromRegister = false
                //检测如果登录了，直接进入管理中心
                this.gotoIndex(this.getToken());
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
                this.axios.post('/admin/auth/sendEmail', {
                    eMail: this.ruleForm2.username
                })
                    .then(response => {
                        this.ruleForm2.eMailVerificationCode = response.data.eMailCode;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            // <!--提交登录-->
            submitForm(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        this.axios.post("/admin/auth/userLogin", {
                            username: this.ruleForm2.username,
                            password: this.ruleForm2.password
                        })
                            .then(response => {
                                //登录成功
                                console.log(response)

                                let userAuth = response.data.data;
                                //存用户权限信息
                                this.setAuth(userAuth);
                                //决定是否跳转
                                this.gotoIndex(userAuth.token);
                            })
                            .catch(error => console.log(error))
                        setTimeout(() => {
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
                    path: "/register"
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
            },
            //保存用户基本权限信息
            setAuth(auth) {
                if (auth) {
                    let authString = JSON.stringify(auth);
                    window.sessionStorage.setItem(this.Auth.USER_AUTH_KEY, authString);
                }
            },
            //进入首页
            gotoIndex(token) {
                if (token) {
                    this.$router.push('/')
                }
            },
            // 可以直接调用此方法获得token，一般不需要调用
            getToken() {
                let authString = window.sessionStorage.getItem(this.Auth.USER_AUTH_KEY);
                let result = "";
                if (authString) {
                    result = JSON.parse(authString);
                }
                return result["token"];
                // return result.token;
            },
            // _getVal(val){
            //     if(this.Auth.authObj == null){
            //         //console.log("加载sessionStorage中的userAuth信息......");
            //         this._getAuth();
            //     }
            //     return  this.Auth.authObj? this.Auth.authObj : null;
            // },
            // _getAuth() {
            //     let authString =  window.sessionStorage.getItem(this.Auth.USER_AUTH_KEY);
            //     if(authString) {
            //         this.Auth.authObj = JSON.parse(authString);
            //     }
            // },
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

    .register-main {
        display: flex;
        height: 100%;
        background-image: url('../assets/images/register.svg');
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100% 100%;
        background-attachment: fixed;
    }

    .register-wrapper {
        flex: 1;
    }
</style>
