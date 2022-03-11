var userData = Vue.extend(
    {
        template:'#userData',
        data(){
            return{
                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex:3,

                //
                userInfo:{

                },

                categoryTree:[],
                countInfo:{

                },

            }
        },

        methods:{
            //公共功能
            setSession(name, value) {
                window.sessionStorage.setItem(name, value);
                // this.editOid = sessionStorage.getItem('editItemOid');
            },

            creatItem(index){
                window.sessionStorage.removeItem('editOid');
                if (index ===0){
                    window.location.href='/user/userSpace#/data/createDataHubs';
                }else if(index === 1) {
                    window.location.href='/user/userSpace#/data/createDataItem'
                }else if (index === 21) {
                    window.location.href='/user/userSpace#/data/createDataApplication'
                }else {
                    window.location.href='/user/userSpace#/data/createDataVisualApplication'
                }
            },

            manageItem(index){
                var urls={
                    1:'/user/userSpace#/data/myDataSpace',
                    2:'/user/userSpace#/data/distributedNode',
                    3:'/user/userSpace#/data/dataHubs',
                    4:'/user/userSpace#/data/dataitem',
                    5:'/user/userSpace#/data/processingApplication',
                    6:'/user/userSpace#/data/visualizationApplication',
                }
                window.sessionStorage.setItem('itemIndex',index)

                window.location.href=urls[index]

            },

            sendcurIndexToParent(){
                this.$emit('com-sendcurindex',this.curIndex)
            },

            sendUserToParent(userId){
                this.$emit('com-senduserinfo',userId)
            },

        },

        created() {
        },

        mounted() {

            let width = document.documentElement.clientWidth;
            if(width <= 1212){
                $(".dataLine").hide();
                if (width<=1090){
                    $(".leftData").css("width","80%");
                    $(".rightData").css("width","80%");
                }else {
                    $(".leftData").css("width","45%");
                    $(".rightData").css("width","45%");
                }
            }else {
                $(".dataLine").show();
            }
            // var tha = this
            // axios.get("/dataItem/categoryTree")
            //     .then(res => {
            //         tha.tObj = res.data;
            //         for (var e in tha.tObj) {
            //             var a = {
            //                 key: e,
            //                 value: tha.tObj[e]
            //             }
            //             if (e != 'Data Resouces Hubs') {
            //                 tha.categoryTree.push(a);
            //             }
            //
            //
            //         }
            //
            //     })

            $(() => {
                let height = document.documentElement.clientHeight;
                let width = document.documentElement.clientWidth;
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";

                window.onresize = () => {
                    // console.log('come on ..');
                    height = document.documentElement.clientHeight;
                    width = document.documentElement.clientWidth;
                    this.ScreenMinHeight = (height) + "px";
                    this.ScreenMaxHeight = (height) + "px";
                    console.log(width);
                    if(width <= 1212){
                        $(".dataLine").hide();
                        if (width<=1090){
                            $(".leftData").css("width","80%");
                            $(".rightData").css("width","80%");
                        }else {
                            $(".leftData").css("width","45%");
                            $(".rightData").css("width","45%");
                        }
                    }else {
                        $(".dataLine").show();
                        $(".leftData").css("width","45%");
                        $(".rightData").css("width","45%");
                    }

                };

                let _this= this
                $.ajax({
                    type: "GET",
                    url: "/user/load",
                    data: {},
                    cache: false,
                    async: false,
                    xhrFields: {
                        withCredentials: true
                    },
                    crossDomain: true,
                    success: function (result) {
                        if (result.code === 0) {
                            let data = result.data
                            _this.userId = data.email;
                            _this.userName = data.name;
                            _this.sendUserToParent(_this.userId)
                            $("#author").val(_this.userName);
                            var index = window.sessionStorage.getItem("index");
                            if (index != null && index != undefined && index != "" && index != NaN) {
                                _this.defaultActive = index;
                                _this.handleSelect(index, null);
                                window.sessionStorage.removeItem("index");
                                _this.curIndex = index

                            } else {
                                // this.changeRter(1);
                            }

                            window.sessionStorage.removeItem("tap");
                            //this.getTasksInfo();
                            _this.load = false;

                        } else {
                            alert("Please login");
                            window.location.href = "/user/login";
                        }
                    }
                })
                    .then(function () {
                        $.ajax({
                            type: "GET",
                            url: "/user/resourceCount",
                            data: {},
                            cache: false,
                            async: false,
                            xhrFields: {
                                withCredentials: true
                            },
                            crossDomain: true,
                            success: (result) => {
                                _this.countInfo = result.data
                                console.log(_this.countInfo)
                            }
                        })
                    })



                //this.getModels();
            });

            //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
            this.sendcurIndexToParent()
            //监听界面宽度，隐藏dataLine
            // window.onresize = function (){
            //     console.log($(window).width());
            // }
        },

    }
)
