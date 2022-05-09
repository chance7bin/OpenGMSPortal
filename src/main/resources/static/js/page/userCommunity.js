var userCommunity = Vue.extend(
    {
        template: "#userCommunity",
        props:["htmlJson"],
        data(){
            return{
                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex:6,

                //
                userInfo:{

                },

                countInfo:{},
            }
        },

        methods:{
            //公共功能
            setSession(name, value) {
                window.sessionStorage.setItem(name, value);
                // this.editOid = sessionStorage.getItem('editItemOid');
            },

            creatItem(index){
                var urls={
                    1:'/user/userSpace#/community/createConcept',
                    2:'/user/userSpace#/community/createSpatialReference',
                    3:'/user/userSpace#/community/createTemplate',
                    4:'/user/userSpace#/community/createUnit',
                }
                window.sessionStorage.removeItem('editOid');
                window.location.href=urls[index]
            },

            manageItem(index){
                var urls={
                    1:'/user/userSpace#/communities/concept&semantic',
                    2:'/user/userSpace#/communities/spatialReference',
                    3:'/user/userSpace#/communities/dataTemplate',
                    4:'/user/userSpace#/communities/unit&metric',
                }
                this.senditemIndexToParent(index)
                window.location.href=urls[index]

            },

            sendcurIndexToParent(){
                this.$emit('com-sendcurindex',this.curIndex)
            },

            senditemIndexToParent(index){
                this.$emit('com-senditemindex',index)
            },

            sendUserToParent(userId){
                this.$emit('com-senduserinfo',userId)
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


                //this.getModels();
            });

            //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
            this.sendcurIndexToParent()
        },

    }
)