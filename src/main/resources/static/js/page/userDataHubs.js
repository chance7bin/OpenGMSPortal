var userDataHubs = Vue.extend(
    {
        template: "#userDataHubs",
        props: ["htmlJson"],
        data(){
            return{
                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex:3,

                itemIndex:1,
                //
                userInfo:{

                },

                await:false,

                resourceLoad:false,

                //分页控制
                page: 1,
                sortAsc: -1,//1 -1
                sortType: "default",
                pageSize: 10,// 每页数据条数
                totalPage: 0,// 总页数
                curPage: 1,// 当前页码
                pageList: [],
                totalNum: 0,

                //用户
                userEmail:-1,

                //展示变量\
                itemTitle:'Model Item',

                searchResult: [],
                modelItemResult: [],

                searchCount: 0,
                ScreenMaxHeight: "0px",
                searchText: "",

                isInSearch:0,

                countInfo:{},

            }
        },

        components: {
        },

        methods:{
            //公共功能
            formatDate(value,callback) {
                const date = new Date(value);
                y = date.getFullYear();
                M = date.getMonth() + 1;
                d = date.getDate();
                H = date.getHours();
                m = date.getMinutes();
                s = date.getSeconds();
                if (M < 10) {
                    M = '0' + M;
                }
                if (d < 10) {
                    d = '0' + d;
                }
                if (H < 10) {
                    H = '0' + H;
                }
                if (m < 10) {
                    m = '0' + m;
                }
                if (s < 10) {
                    s = '0' + s;
                }

                const t = y + '-' + M + '-' + d + ' ' + H + ':' + m + ':' + s;
                if(callback == null||callback == undefined)
                    return t;
                else
                    callback(t);
            },

            setSession(name, value) {
                window.sessionStorage.setItem(name, value);
                // this.editOid = sessionStorage.getItem('editItemOid');
            },

            //page
            // 初始化page并显示第一页
            pageInit() {
                this.totalPage = Math.floor((this.totalNum + this.pageSize - 1) / this.pageSize);
                if (this.totalPage < 1) {
                    this.totalPage = 1;
                }
                this.getPageList();
                this.changePage(1);
            },

            getPageList() {
                this.pageList = [];

                if (this.totalPage < 5) {
                    for (let i = 0; i < this.totalPage; i++) {
                        this.pageList.push(i + 1);
                    }
                } else if (this.totalPage - this.curPage < 5) {//如果总的页码数减去当前页码数小于5（到达最后5页），那么直接计算出来显示

                    this.pageList = [
                        this.totalPage - 4,
                        this.totalPage - 3,
                        this.totalPage - 2,
                        this.totalPage - 1,
                        this.totalPage,
                    ];
                } else {
                    let cur = Math.floor((this.curPage - 1) / 5) * 5 + 1;
                    if (this.curPage % 5 === 0) {
                        cur = cur + 1;

                    }
                    this.pageList = [
                        cur,
                        cur + 1,
                        cur + 2,
                        cur + 3,
                        cur + 4,
                    ]
                }
            },

            changePage(pageNo) {
                if ((this.curPage === 1) && (pageNo === 1)) {
                    return;
                }
                if ((this.curPage === this.totalPage) && (pageNo === this.totalPage)) {
                    return;
                }
                if ((pageNo > 0) && (pageNo <= this.totalPage)) {
                    if (this.curIndex != 1)
                        this.pageControlIndex = this.curIndex;
                    else this.pageControlIndex = 'research';

                    this.resourceLoad = true;
                    this.searchResult = [];
                    //not result scroll
                    //window.scrollTo(0, 0);
                    this.curPage = pageNo;
                    this.getPageList();
                    this.page = pageNo;

                    if (this.isInSearch == 0)
                        this.getDataHubs();
                    else this.searchItems();

                }
            },

            creatItem(index){
                window.sessionStorage.removeItem('editOid');
                if(index == 1) window.location.href='/user/userSpace#/data/createDataHubs'
            },

            reloadPage(){//重新装订分页诸元
                this.pageSize = 10;
                this.isInSearch = 0;
                this.page = 1;
            },

            getDataHubs() {
                this.pageSize = 10;
                this.isInSearch = 0;
                this.await = true
                var data = {
                    authorEmail: this.userEmail,
                    page: this.page,
                    pagesize: this.pageSize,
                    asc: false,
                    searchText:this.searchText
                }
                this.loading = true
                var _this = this;
                // todo 从后台拿到用户创建的data—item
                axios.post(QueryHubListOfAuthorSelf(), data).then(res => {
                    // console.log("QueryHubListOfAuthorSelf:",res);
                    const data = res.data.data;
                    _this.searchResult = data.list
                    _this.resourceLoad = false;
                    _this.totalNum = data.total
                    if (_this.page == 1) {
                        _this.pageInit();
                    }
                    console.log("111");
                    _this.data_show = true
                    _this.loading = false
                    _this.await = false
                })


            },

            editItem(index,oid){
                var urls={
                    1:'/user/userSpace#/data/manageDataHubs/'+oid,
                }
                // this.setSession('editOid', oid)
                window.location.href=urls[1]
            },

            deleteItem(id) {
                //todo 删除category中的 id
                const h = this.$createElement;
                this.$msgbox({
                    title: ' ',
                    message: h('p', null, [
                        h('span', null, this.htmlJson.userModel.sure),
                        h('span', {style: 'font-weight:600'}, this.htmlJson.userModel.delete),
                        h('span', null, this.htmlJson.userModel.thisItem),
                    ]),
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: this.htmlJson.userModel.Confirm,
                    cancelButtonText: this.htmlJson.userModel.Cancel,
                    beforeClose: (action, instance, done) => {

                        if (action === 'confirm') {
                            instance.confirmButtonLoading = true;
                            instance.confirmButtonText = 'deleting...';
                            setTimeout(() => {

                                $.ajax({
                                    type: "delete",
                                    url: "/dataHub/"+id,
                                    cache: false,
                                    async: true,
                                    dataType: "json",
                                    xhrFields: {
                                        withCredentials: true
                                    },
                                    crossDomain: true,
                                    success: (json) => {
                                        if (json.code == -1) {
                                            alert("Please log in first!")
                                        } else {
                                            if (json.data == 1) {
                                                // this.$alert("delete successfully!")
                                            } else if (json.data == -1) {
                                                this.$alert("delete failed!")
                                            } else
                                                this.$message({
                                                    message: this.htmlJson.userModel.DeleteSuccessful,
                                                    type: 'success'
                                                });
                                        }
                                        if (this.searchText.trim() != "") {
                                            this.searchDataItem();
                                        } else {
                                            this.getDataHubs();
                                        }

                                    }
                                })
                                done();
                                setTimeout(() => {
                                    instance.confirmButtonLoading = false;
                                }, 300);
                            }, 300);
                        } else {
                            done();
                        }
                    }
                })
            },

            searchItems(page){
                this.pageSize = 10;
                this.isInSearch = 1;
                // this.searchResult = []
                this.await = true
                var that = this;
                let targetPage = page==undefined?this.page:page
                // var da = {
                //     userOid: this.userEmail,
                //     page: targetPage - 1,
                //     pageSize: this.pageSize,
                //     asc:-1,
                //     searchText: this.searchText
                // }
                let reqData = {
                    searchText: this.searchText,
                    page: targetPage,
                    pagesize: this.pageSize,
                    sortType: "createTime",
                    asc: false,
                    authorEmail: window.localStorage.getItem("account")
                }
                axios.post("/dataHub/queryListOfAuthorSelf/", reqData)
                    .then((res) => {
                        setTimeout(() => {
                            if (res.status == 200) {
                                if (res.data.data != null) {
                                    that.resourceLoad = false;
                                    that.totalNum = res.data.data.total;
                                    that.searchResult = res.data.data.list;
                                    if (targetPage == 1) {
                                        this.pageInit();
                                    }
                                    this.await = false
                                } else {
                                    alert("no result")
                                }
                            }
                        }, 1)

                        // this.list=res.data.data;

                    });
            },

            sendcurIndexToParent(){
                this.$emit('com-sendcurindex',this.curIndex)
            },

            sendUserToParent(userEmail){
                this.$emit('com-senduserinfo',userEmail)
            },

        },

        created() {


        },

        mounted() {
            //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
            this.sendcurIndexToParent()

            $(async () => {

                let height = document.documentElement.clientHeight;
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";

                window.onresize = () => {
                    console.log('come on ..');
                    height = document.documentElement.clientHeight;
                    this.ScreenMinHeight = (height) + "px";
                    this.ScreenMaxHeight = (height) + "px";
                };


                await $.ajax({
                    type: "GET",
                    url: "/user/load",
                    data: {},
                    cache: false,
                    async: true,
                    xhrFields: {
                        withCredentials: true
                    },
                    crossDomain: true,
                    success: (result) => {

                        console.log(result);

                        if (result.code !== 0) {
                            alert("Please login");
                            window.location.href = "/user/login";
                        } else {
                            let data = result.data;
                            this.userEmail = data.email;
                            this.userName = data.name;
                            this.sendUserToParent(this.userEmail)
                            $("#author").val(this.userName);

                            var index = window.sessionStorage.getItem("index");
                            //判断显示哪一个item
                            var itemIndex = window.sessionStorage.getItem("itemIndex");
                            this.itemIndex = itemIndex
                            // this.getModels(this.itemIndex);

                            this.getDataHubs();

                            if (index != null && index != undefined && index != "" && index != NaN) {
                                this.defaultActive = index;
                                this.handleSelect(index, null);
                                window.sessionStorage.removeItem("index");
                                this.curIndex = index


                            } else {
                                // this.changeRter(1);
                            }

                            window.sessionStorage.removeItem("tap");
                            //this.getTasksInfo();
                            this.load = false;
                        }
                    }
                })


                //this.getModels();
            });


        },

    }
)
