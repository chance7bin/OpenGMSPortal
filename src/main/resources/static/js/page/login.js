new Vue({
    el: '#app',
    data: function () {
        var validateAccount = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('Please enter email address'));
            }else {
                callback();
            }
        };
        var validatePassword = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('Please enter password'));
            } else {
                callback();
            }
        };

        return {
            //navbar
            loadActiveIndex:false,
            activeIndex: '6',
            //register form
            ruleForm: {
                account: '',
                password: '',
                password_encrypt: "",
                remember: false
            },
            rules: {
                account: [
                    {validator: validateAccount, trigger: 'blur'}
                ],
                password: [
                    {validator: validatePassword, trigger: 'blur'}
                ],

            },

            inputVisible: false,
            inputValue: '',

            reset:false,

            sendingCode:false,
            resetPassFormVisible:false,
            resetPassForm:{
                email:"",
                verifyCode:"",
                newPass:"",
            },

            guideActive:0,

            htmlJSON:{},
        }
    },
    methods: {

        translatePage(jsonContent){
            this.htmlJSON = jsonContent;
        },

        //reset cancel
        resetCancel(){
            this.resetPassFormVisible = false
            this.resetPassForm.newPass = ''
            this.resetPassForm.verifyCode = ''

        },


        //register form
        submitForm(formName) {
            if(this.loadActiveIndex==false){
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        if (!this.ruleForm.remember) {
                            // localStorage.removeItem('account');
                            localStorage.removeItem('password');
                            localStorage.setItem('remember', "no");
                        }
                        this.login();
                    } else {
                        this.$message({
                            showClose: true,
                            message: this.htmlJSON.ErrorSubmit,
                            type: 'error'
                        });
                        return false;
                    }
                });
            }
            else{
                return;
            }

        },
        resetForm(formName) {
            if(this.loadActiveIndex==false) {
                this.$refs[formName].resetFields();
            }
        },
        login() {
            this.loadActiveIndex = true;
            console.log(this.loadActiveIndex)
            this.ruleForm.password_encrypt = hex_md5(this.ruleForm.password);
            let data = {
                "account": this.ruleForm.account,
                "password": this.ruleForm.password_encrypt,
            }

            $.ajax({
                url: '/user/in',
                type: 'post',
                // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                data: data,
                // dataType : 'json',
                success: (result) => {
                    if (this.ruleForm.remember) {
                        localStorage.setItem('account', this.ruleForm.account);
                        localStorage.setItem('password', this.ruleForm.password);
                        localStorage.setItem('remember', "yes");
                    }
                    if (result.code == "0") {

                        // 重试超过限制
                        if(result.data.code == "-1"){
                            this.$notify.error({
                                title: this.htmlJSON.error,
                                message: this.htmlJSON.maxRetryPre + " " + result.data.waitMinute + " " + this.htmlJSON.maxRetrySuffix,
                                offset: 70
                            });
                            this.loadActiveIndex =false
                        }

                        else {
                            this.$notify.success({
                                title: this.htmlJSON.success,
                                message: this.htmlJSON.sucInfo,
                                offset: 70
                            });
                            setTimeout(() => {

                                if (preUrl != null && preUrl.indexOf("user/register") == -1) {
                                    window.location.href = preUrl;
                                } else {
                                    window.location.href = "/user/userSpace";
                                }
                            }, 1000)
                        }

                    } else {

                        this.$notify.error({
                            title: this.htmlJSON.error,
                            message: this.htmlJSON.errInfo,
                            offset: 70
                        });
                        this.loadActiveIndex =false

                    }
                },
                error: function (e) {
                    this.$message({
                        message: this.htmlJSON.submitErr,
                        type: 'error',
                        offset: 40,
                        showClose: true,
                    });
                    this.loadActiveIndex =false
                }
            });
        },
        sendCode() {
            if(this.loadActiveIndex==false) {
                if (this.resetPassForm.email === "") {
                    this.$message({
                        message: this.htmlJSON.emailInfo,
                        type: 'warning',
                        offset: 40,
                        showClose: true,
                    });
                } else {
                    this.sendingCode = true;
                    $.ajax({
                        url: '/user/sendResetByUserserver',
                        type: 'post',
                        // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                        data: {
                            email: this.resetPassForm.email
                        },
                        // dataType : 'json',
                        success: (result) => {
                            this.sendingCode = false;
                            if (result.data == "suc") {
                                this.reset = false;
                                this.$notify.success({
                                    title: this.htmlJSON.success,
                                    message: this.htmlJSON.codeSendSucInfo,
                                    offset: 70,
                                    duration: 0
                                });

                                this.guideActive++
                            } else if (result.data == "no user") {
                                this.$notify({
                                    title: this.htmlJSON.failed,
                                    message: this.htmlJSON.codeSendFailInfo,
                                    offset: 70,
                                    type: 'warning',
                                    duration: 0
                                });
                            } else {
                                this.$notify.error({
                                    title: this.htmlJSON.failed,
                                    message: this.htmlJSON.resetPassFailInfo,
                                    offset: 70,
                                    duration: 0
                                });
                            }
                            this.sendingCode = false
                        }, error: (e)=> {
                            this.$alert(this.htmlJSON.sendCodeErrInfo, this.htmlJSON.tip, {
                                    type: "warning",
                                    confirmButtonText: this.htmlJSON.ok,
                                    callback: () => {
                                        return
                                    }
                                }
                            );
                            this.sendingCode = false
                        }

                    })
                }
            }
        },

        resetPWDclick() {
            if(this.loadActiveIndex==false) {
                this.resetPassFormVisible = true;

                this.resetPassForm.email = this.ruleForm.account
            }
        },

        resetPassword() {
            if(this.loadActiveIndex==false) {
                $.ajax({
                    url: '/user/resetPassword',
                    type: 'post',
                    // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                    data: {
                        email: this.resetPassForm.email,
                        code: this.resetPassForm.verifyCode,
                        newPass: hex_md5(this.resetPassForm.newPass),
                    },
                    // dataType : 'json',
                    success: (result) => {
                        if (result.code == 0) {
                            this.$notify.success({
                                title: this.htmlJSON.success,
                                message: this.htmlJSON.resetPassSuc,
                                offset: 70,
                                duration: 0
                            });
                            this.resetPassFormVisible = false;
                            this.resetPassForm = {
                                email: "",
                                verifyCode: "",
                                newPass: "",
                            }
                        } else if (result.code == -1) {
                            this.$notify.error({
                                title: this.htmlJSON.error,
                                message: this.htmlJSON.userNotExist,
                                offset: 70,
                                duration: 0
                            });
                        } else {
                            this.$notify.error({
                                title: this.htmlJSON.failed,
                                message: this.htmlJSON.resetPassFail,
                                offset: 70,
                                duration: 0
                            });
                        }
                    }
                });
                // this.reset=true;
                // this.$prompt('Please enter your email:', 'Reset Password', {
                //     confirmButtonText: 'Confirm',
                //     cancelButtonText: 'Cancel',
                //     inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                //     inputErrorMessage: 'E-mail format is incorrect.'
                // }).then(({ value }) => {
                //     let info=this.$notify.info({
                //         title: 'Reseting password',
                //         message: 'Please wait for a while, new password will be sent to your email.',
                //         offset: 70,
                //         duration: 0
                //     });
                //     $.ajax({
                //         url: '/user/resetPassword',
                //         type: 'post',
                //         // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                //         data: {
                //             email:value
                //         },
                //         // dataType : 'json',
                //         success: (result) => {
                //             info.close();
                //
                //             if (result.data=="suc") {
                //                 this.reset=false;
                //                 this.$notify.success({
                //                     title: 'Success',
                //                     message: 'New password has been sent to your email. If you can not find the password, please check the spam box.',
                //                     offset: 70,
                //                     duration: 0
                //                 });
                //
                //             }
                //             else if(result.data=="no user") {
                //                 this.$notify({
                //                     title: 'Failed',
                //                     message: 'Email does not exist, please check again or register a new account.',
                //                     offset: 70,
                //                     type: 'warning',
                //                     duration: 0
                //                 });
                //             }
                //             else{
                //                 this.$notify.error({
                //                     title: 'Failed',
                //                     message: 'Reset password failed, Please try again or contact opengms@njnu.edu.cn',
                //                     offset: 70,
                //                     duration: 0
                //                 });
                //             }
                //         },
                //         error: function (e) {
                //             alert("reset password error");
                //         }
                //     });
                // }).catch(() => {
                //
                // });
            }
        },

        },
        mounted() {


            this.ruleForm.remember = false;
            const remember = localStorage.getItem('remember');
            if ((remember != undefined) && (remember === "yes")) {
                this.ruleForm.account = localStorage.getItem("account");
                this.ruleForm.password = localStorage.getItem("password");
                this.ruleForm.remember = true;
            }

            $(document).keydown((event) => {
                if (!this.reset && event.keyCode == 13) {
                    this.login();
                }
            });

        }

})