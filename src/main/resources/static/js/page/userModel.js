var userModel = Vue.extend(
    {
        template: "#userModel",
        data() {
            return {
                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex: 2,

                //
                userInfo: {},

                countInfo: {},

            }
        },

        props: ['itemindexRaw',"htmlJson"],

        methods: {
            //公共功能

            setSession(name, value) {
                window.sessionStorage.setItem(name, value);
                // this.editOid = sessionStorage.getItem('editItemOid');
            },

            creatItem(index) {
                var urls = {
                    1: '/user/userSpace#/model/createModelItem',
                    2: '/user/userSpace#/model/createConceptualModel',
                    3: '/user/userSpace#/model/createLogicalModel',
                    4: '/user/userSpace#/model/createComputableModel',
                }
                window.sessionStorage.removeItem('editOid');
                window.location.href = urls[index]
            },

            manageItem(index) {
                //此处跳转至统一页面，vue路由管理显示
                var urls = {
                    1: '/user/userSpace#/models/modelitem',
                    2: '/user/userSpace#/models/conceptualmodel',
                    3: '/user/userSpace#/models/logicalmodel',
                    4: '/user/userSpace#/models/computablemodel',
                }

                this.senditemIndexToParent(index)
                window.location.href = urls[index]

            },

            sendcurIndexToParent() {
                this.$emit('com-sendcurindex', this.curIndex)
            },

            senditemIndexToParent(index) {
                this.$emit('com-senditemindex', index)
            },

            sendUserToParent(userId) {
                this.$emit('com-senduserinfo', userId)
            },

        },

        created() {
        },

        mounted() {

            $(() => {
                    let height = document.documentElement.clientHeight;
                    this.ScreenMinHeight = (height) + "px";
                    this.ScreenMaxHeight = (height) + "px";

                    window.onresize = () => {
                        console.log('come on ..');
                        height = document.documentElement.clientHeight;
                        this.ScreenMinHeight = (height) + "px";
                        this.ScreenMaxHeight = (height) + "px";
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
                                alert(this.htmlJson.LoginInFirst);
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




                    //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
                    this.sendcurIndexToParent()
                }
            )
        },
    }
)