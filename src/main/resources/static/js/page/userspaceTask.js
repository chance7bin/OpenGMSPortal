var userTask = Vue.extend(
    {
        template:'#userTask',
        props: ["htmlJson"],
        data() {
            return {
                //页面样式控制
                dialogFormVisible: false,
                formLabelWidth: '120px',
                publicTask: {},
                taskInfo:{
                    id: '',
                    taskDateRange: '',
                    taskApplyArea: '',
                    taskDesc: ''
                },
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex: 5,
                dataCurIndex:5,

                itemIndex: 1,
                //
                userInfo: {},

                resourceLoad: false,
                dataResourceLoad: true,

                //分页控制
                page: 1,
                sortAsc: -1,//1 -1
                sortType: "default",
                pageSize: 10,// 每页数据条数
                totalPage: 0,// 总页数
                curPage: 1,// 当前页码
                pageList: [],
                totalNum: 0,

                //dataTask分页控制
                dataSortAsc: -1,//1 -1
                dataSortType: "runTime",
                dataPageSize: 10,// 每页数据条数
                dataTotalPage: 0,// 总页数
                dataCurPage: 1,// 当前页码
                dataPageList: [],
                dataTotalNum: 0,

                //integratedTask分页控制
                itdSortAsc: 1,//1 -1
                itdSortType: "default",
                itdPageSize: 10,// 每页数据条数
                itdTotalPage: 0,// 总页数
                itdCurPage: 1,// 当前页码
                itdPageList: [],
                itdTotalNum: 0,

                //用户
                userId: -1,

                //展示变量\
                itemTitle: 'Model Item',

                searchResult: [],
                dataSearchResult:[],

                modelItemResult: [],

                searchCount: 0,
                ScreenMaxHeight: "0px",
                searchText: "",
                dataSearchText:"",

                isInSearch: 0,

                //task相关
                taskStatus: 'all',
                dataTaskStatus:'all',
                itdTaskStatus:'all',

                // options: [
                //     {
                //         value: 'all',
                //         label: 'All',
                //
                //     },
                //     {
                //         value: 'inited',
                //         label: 'Inited',
                //
                //     },
                //     {
                //         value: 'calculating',
                //         label: 'Calculating',
                //
                //     },
                //     {
                //         value: 'successful',
                //         label: 'Successful',
                //
                //     },
                //
                //     {
                //         value: 'failed',
                //         label: 'Failed',
                //
                //     },
                // ],

                // itdOptions: [
                //     {
                //         value: 'all',
                //         label: 'all',
                //
                //     },
                //
                //     {
                //         value: 'builded',
                //         label: 'builded',
                //
                //     },
                //
                //     {
                //         value: 'successful',
                //         label: 'successful',
                //
                //     },
                //
                //     {
                //         value: 'calculating',
                //         label: 'calculating',
                //
                //     },
                //
                //     {
                //         value: 'failed',
                //         label: 'failed',
                //
                //     },
                // ],

                addOutputToMyDataVisible: false,
                taskSharingVisible:false,
                taskSharingActive:0,
                taskDataList: [],
                stateFilters: [],
                multipleSelection: [],
                multipleSelectionMyData: [],
                taskCollapseActiveNames: [],
                taskDataForm: {
                    name: '',
                    type: "option1",
                    contentType: "resource",
                    description: "",
                    detail: "",
                    reference: "",
                    author: "",
                    keywords: [],
                    contributers: [],
                    classifications: [],
                    displays: [],
                    authorship: [],
                    comments: [],
                    dataList: [],

                    categoryText: [],

                },

                categoryTree: [],

                folderTree: [{
                    id: 1,
                    label: 'All Folder',
                    children: [{
                        id: 4,
                        label: '二级 1-1',
                        children: [{
                            id: 9,
                            label: '三级 1-1-1'
                        }, {
                            id: 10,
                            label: '三级 1-1-2'
                        }]
                    }]
                }],

                folderTree2: [{
                    id: 1,
                    label: 'All Folder',
                    children: [{
                        id: 4,
                        label: '二级 1-1',
                        children: [{
                            id: 9,
                            label: '三级 1-1-1'
                        }, {
                            id: 10,
                            label: '三级 1-1-2'
                        }]
                    }]
                }],

                await:false,
                shareIndex:false,
                multiFileDialog : false,
                outputMultiFile : [],
                downloadUrl:'',
                clipBoard:'',

                dataTaskFindDto:{
                    page:1,
                    asc:1,
                    sort: "default",
                    pageSize: 10,
                    status:0,
                    dataSearchText:'',
                },
                dataTaskIsWrong:false,

                integratedTaskList:[],

                activeTask:'normal',

                itdSearchText:'',
            }
        },

        components: {},

        methods: {
            //公共功能
            formatDate(value, callback) {
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
                if (callback == null || callback == undefined)
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

            dataPageInit() {
                this.dataTotalPage = Math.floor((this.dataTotalNum + this.dataPageSize - 1) / this.dataPageSize);
                if (this.dataTotalPage < 1) {
                    this.dataTotalPage = 1;
                }
                this.getDataPageList();
                this.changeDataPage(this.dataCurPage);
            },

            itdPageInit() {
                this.itdTotalPage = Math.floor((this.itdTotalNum + this.itdPageSize - 1) / this.itdPageSize);
                if (this.itdTotalPage < 1) {
                    this.itdTotalPage = 1;
                }
                this.getItdPageList();
                this.changeItdPage(1);
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
            getDataPageList() {
                this.dataPageList = [];

                if (this.dataTotalPage < 5) {
                    for (let i = 0; i < this.dataTotalPage; i++) {
                        this.dataPageList.push(i + 1);
                    }
                } else if (this.dataTotalPage - this.dataCurPage < 5) {//如果总的页码数减去当前页码数小于5（到达最后5页），那么直接计算出来显示

                    this.dataPageList = [
                        this.dataTotalPage - 4,
                        this.dataTotalPage - 3,
                        this.dataTotalPage - 2,
                        this.dataTotalPage - 1,
                        this.dataTotalPage,
                    ];
                } else {
                    let cur = Math.floor((this.curPage - 1) / 5) * 5 + 1;
                    if (this.curPage % 5 === 0) {
                        cur = cur + 1;

                    }
                    this.dataPageList = [
                        cur,
                        cur + 1,
                        cur + 2,
                        cur + 3,
                        cur + 4,
                    ]
                }
            },

            getItdPageList() {
                this.itdPageList = [];

                if (this.itdTotalPage < 5) {
                    for (let i = 0; i < this.itdTotalPage; i++) {
                        this.itdPageList.push(i + 1);
                    }
                } else if (this.itdTotalPage - this.itdCurPage < 5) {//如果总的页码数减去当前页码数小于5（到达最后5页），那么直接计算出来显示

                    this.itdPageList = [
                        this.itdTotalPage - 4,
                        this.itdTotalPage - 3,
                        this.itdTotalPage - 2,
                        this.itdTotalPage - 1,
                        this.itdTotalPage,
                    ];
                } else {
                    let cur = Math.floor((this.itdCurPage - 1) / 5) * 5 + 1;
                    if (this.itdCurPage % 5 === 0) {
                        cur = cur + 1;

                    }
                    this.itdPageList = [
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
                        this.showTasksByStatus();
                    else this.searchTasks()


                }
            },

            changeDataPage(pageNo) {
                if(this.dataCurPage === pageNo) return
                this.dataCurPage = pageNo
                this.getDataTasks()
            },

            changeItdPage(pageNo) {
                if ((this.itdCurPage === 1) && (pageNo === 1)) {
                    return;
                }
                if ((this.itdCurPage === this.itdTotalPage) && (pageNo === this.itdTotalPage)) {
                    return;
                }
                if ((pageNo > 0) && (pageNo <= this.itdTotalPage)) {
                    if (this.curIndex != 1)
                        this.pageControlIndex = this.curIndex;
                    else this.pageControlIndex = 'research';

                    this.resourceLoad = true;
                    this.searchResult = [];
                    //not result scroll
                    //window.scrollTo(0, 0);
                    this.itdCurPage = pageNo;
                    this.getItdPageList();

                    this.listIntegrateTaskByStatus()


                }
            },

            // creatItem(index){
            //     window.sessionStorage.removeItem('editOid');
            //     if(index == 1) window.location.href='../model/createModelItem'
            // },

            reloadPage() {//重新装订分页诸元
                this.pageSize = 10;
                this.isInSearch = 0;
                this.page = 1;
            },
            //
            //task

            tabHandleClick(tab){
                let tabName =tab.name

                switch (tabName){
                    case 'normal':
                        this.showTasksByStatus()
                        break;
                    case 'data':
                        this.getDataTasks()
                        break;
                    case 'integrated':
                        this.listIntegrateTaskByStatus()
                        break;
                }
            },

            listIntegrateTaskByStatus(page){

                if(page!=undefined){
                    this.itdCurPage = page
                }
                this.resourceLoad = true
                axios.get("/task/pageIntegrateTaskByUserByStatus",{
                    params:{
                        status: this.itdTaskStatus,
                        page: this.itdCurPage - 1,
                        sortType: this.sortType,
                        asc: -1,
                        searchText:this.itdSearchText
                    }
                }).then((res)=>{
                    this.resourceLoad = false
                    if(res.data.code == -1){
                        window.location.href='/user/login'
                    }else{
                        let data = res.data.data
                        this.integratedTaskList = data.tasks
                        this.itdTotalNum = data.count
                        if (this.itdCurPage == 1) {
                            this.itdPageInit();
                        }
                    }
                })
            },

            checkItdTask(taskOid){
                window.localStorage.setItem('taskOid',taskOid)
                window.location.href='/computableModel/integratedModel'
            },

            getPanelClass(status){
                switch (status){
                    case -1:
                        return 'panel-danger'
                    case 0:
                        return 'panel-default'
                    case 1:
                        return 'panel-info'
                    case 2:
                        return 'panel-success'
                }
            },

            publishTask(task) {
                console.log(task)
                const h = this.$createElement;
                if (task.permission == 'private') {

                    this.$msgbox({
                        title: ' ',
                        message: this.htmlJson.SureSetPublic,
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: this.htmlJson.confirmButtonText,
                        cancelButtonText: this.htmlJson.cancelButtonText,
                        beforeClose: (action, instance, done) => {
                            let href = window.location.href.split('/')
                            let ids = href[href.length - 1]
                            let taskId = ids.split('&')[1]
                            if (action === 'confirm') {
                                instance.confirmButtonLoading = true;
                                // instance.confirmButtonText = '...';
                                setTimeout(() => {
                                    $.ajax({
                                        type: "POST",
                                        url: "/task/setPublic",
                                        data: {taskId: task.taskId},
                                        async: true,
                                        contentType: "application/x-www-form-urlencoded",
                                        success: (json) => {
                                            if (json.code == -1) {
                                                alert(this.htmlJson.LoginInFirst)
                                                window.sessionStorage.setItem("history", window.location.href);
                                                window.location.href = "/user/login"
                                            } else {
                                                // this.rightTargetItem=null;
                                                task.permission = json.data;
                                            }

                                        }
                                    });
                                    done();
                                    setTimeout(() => {
                                        instance.confirmButtonLoading = false;
                                    }, 100);
                                }, 100);
                            } else {
                                done();
                            }
                        }
                    }).then(action => {
                        this.rightMenuShow = false
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.CanVisitedPublic
                        });
                    });
                }
                else {
                    this.$msgbox({
                        title: ' ',
                        message: this.htmlJson.SureSetPrivate,
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: this.htmlJson.confirm,
                        cancelButtonText: this.htmlJson.cancel,
                        beforeClose: (action, instance, done) => {
                            let href = window.location.href.split('/')
                            let ids = href[href.length - 1]
                            let taskId = ids.split('&')[1]
                            if (action === 'confirm') {
                                instance.confirmButtonLoading = true;
                                // instance.confirmButtonText = '...';
                                setTimeout(() => {
                                    $.ajax({
                                        type: "POST",
                                        url: "/task/setPrivate",
                                        data: {taskId: task.taskId},
                                        async: true,
                                        contentType: "application/x-www-form-urlencoded",
                                        success: (json) => {
                                            if (json.code == -1) {
                                                alert(this.htmlJson.LoginInFirst)
                                                window.sessionStorage.setItem("history", window.location.href);
                                                window.location.href = "/user/login"
                                            } else {
                                                // this.rightTargetItem=null;
                                                task.permission = json.data;
                                            }

                                        }
                                    });
                                    done();
                                    setTimeout(() => {
                                        instance.confirmButtonLoading = false;
                                    }, 100);
                                }, 100);
                            } else {
                                done();
                            }
                        }
                    }).then(action => {
                        this.rightMenuShow = false
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.HasBeenSetPrivate
                        });
                    });
                }


            },

            publishDataTask(task) {
                const h = this.$createElement;
                if (task.permission == 'private') {
                    this.$msgbox({
                        title: ' ',
                        message: this.htmlJson.SureSetPublic,
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: this.htmlJson.confirm,
                        cancelButtonText: this.htmlJson.cancel,
                        beforeClose: (action, instance, done) => {
                            let href = window.location.href.split('/')
                            let ids = href[href.length - 1]
                            let taskId = ids.split('&')[1]
                            if (action === 'confirm') {
                                instance.confirmButtonLoading = true;
                                // instance.confirmButtonText = '...';
                                setTimeout(() => {
                                    $.ajax({
                                        type: "POST",
                                        url: "/task/setDataTaskPublic",
                                        data: {oid: task.oid},
                                        async: true,
                                        contentType: "application/x-www-form-urlencoded",
                                        success: (json) => {
                                            if (json.code == -1) {
                                                alert(this.htmlJson.LoginInFirst)
                                                window.sessionStorage.setItem("history", window.location.href);         // 为什么要存这个东西？
                                                window.location.href = "/user/login"
                                            } else {
                                                // this.rightTargetItem=null;
                                                task.permission = json.data;
                                            }

                                        }
                                    });
                                    done();
                                    setTimeout(() => {
                                        instance.confirmButtonLoading = false;
                                    }, 100);
                                }, 100);
                            } else {
                                done();
                            }
                        }
                    }).then(action => {
                        this.rightMenuShow = false
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.CanVisitedPublic
                        });
                    });
                } else {
                    this.$msgbox({
                        title: ' ',
                        message: h('p', null, [
                            h('span', {style: 'font-size:15px'}, 'Only you have'), h('span', {style: 'font-weight:600'}, ' permission '), h('span', 'to this task.'),
                            h('br'),
                            h('span', null, 'Are you sure to'),
                            h('span', {style: 'color: #67c23a;font-weight:600'}, ' continue'),
                            h('span', null, '?'),
                        ]),
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Confirm',
                        cancelButtonText: 'Cancel',
                        beforeClose: (action, instance, done) => {
                            let href = window.location.href.split('/')
                            let ids = href[href.length - 1]
                            let taskId = ids.split('&')[1]
                            if (action === 'confirm') {
                                instance.confirmButtonLoading = true;
                                // instance.confirmButtonText = '...';
                                setTimeout(() => {
                                    $.ajax({
                                        type: "POST",
                                        url: "/task/setDataTaskPrivate",
                                        data: {oid: task.oid},
                                        async: true,
                                        contentType: "application/x-www-form-urlencoded",
                                        success: (json) => {
                                            if (json.code == -1) {
                                                alert(this.htmlJson.LoginInFirst)
                                                window.sessionStorage.setItem("history", window.location.href);
                                                window.location.href = "/user/login"
                                            } else {
                                                // this.rightTargetItem=null;
                                                task.permission = json.data;
                                            }

                                        }
                                    });
                                    done();
                                    setTimeout(() => {
                                        instance.confirmButtonLoading = false;
                                    }, 100);
                                }, 100);
                            } else {
                                done();
                            }
                        }
                    }).then(action => {
                        this.rightMenuShow = false
                        this.$message({
                            type: 'success',
                            message: 'This task has been set private'
                        });
                    });
                }


            },

            changeTaskStatus(status) {
                this.taskStatus = status;
                if (this.taskStatus === 'successful')
                    $('.wzhSelectContainer input').css('background', '#4dc23a')
                else if (this.taskStatus === 'inited')
                    $('.wzhSelectContainer input').css('background', '#e6c13c')
                 else if (this.taskStatus === 'all')
                    $('.wzhSelectContainer input').css('background', '#00ABFF')
                else if (this.taskStatus === 'failed')
                    $('.wzhSelectContainer input').css('background', '#d74948')
                else
                    $('.wzhSelectContainer input').css('background', '#1caf9a')
                this.showTasksByStatus()
            },

            changeDataTaskStatus(status) {
                this.dataTaskStatus = status;
                this.getDataTasks()
            },

            changeIntegratedTaskStatus(status) {
                this.itdTaskStatus = status;
                if (this.itdTaskStatus === 'successful')
                    $('.wzhSelectContainer input').css('background', '#63b75d')
                else if (this.itdTaskStatus === 'all')
                    $('.wzhSelectContainer input').css('background', '#00ABFF')
                else if (this.itdTaskStatus === 'failed')
                    $('.wzhSelectContainer input').css('background', '#d74948')
                else if(this.itdTaskStatus === 'calculating')
                    $('.wzhSelectContainer input').css('background', '#1caf9a')
                else
                    $('.wzhSelectContainer input').css('background', '#939faa')
                this.listIntegrateTaskByStatus()
            },

            showTasksByStatus() {
                let name = 'tasks'
                this.resourceLoad = true
                this.isInSearch = 0;
                this.searchResult = []
                axios.post("/task/taskInfo", {
                            status: this.taskStatus,
                            page: this.page,
                            pageSize: 10,
                            sortType: this.sortType,
                            asc: -1,
                    }).then(
                    res => {

                        if (res.data.code != 0) {
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href = "/user/login";
                        } else {
                            const data = res.data.data;
                            this.resourceLoad = false;
                            this.totalNum = data.count;

                            for (var i = 0; i < data[name].length; i++) {
                                // this.searchResult.push(data[name][i]);
                                this.searchResult = data[name];
                                // console.log(data[name][i]);
                            }
                            //this.modelItemResult = data[name];
                            if (this.page == 1) {
                                this.pageInit();
                            }
                        }
                    }
                )

                this.activeIndex = '6'
                // this.curIndex = '6'
                this.defaultActive = '6';
                this.pageControlIndex = '6';
            },

            searchTasks(page) {
                let url = "/task/taskByUser";
                let name = "tasks";
                this.await = true
                this.isInSearch = 1;
                this.resourceLoad = true
                let targetPage = page==undefined?this.page:page
                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify({
                        searchText: this.searchText,
                        page: targetPage,
                        pagesize: this.pageSize,
                        sortType: this.sortType,
                        asc: this.sortAsc
                    }),
                    cache: false,
                    async: true,
                    contentType: "application/json",
                    xhrFields: {
                        withCredentials: true
                    },
                    crossDomain: true,
                    success: (json) => {
                        if (json.code != 0) {
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href = "/user/login";
                        } else {
                            data = json.data;
                            this.resourceLoad = false;
                            this.totalNum = data.count;
                            this.searchCount = Number.parseInt(data["count"]);
                            this.searchResult = data[name];
                            if (targetPage == 1) {
                                this.pageInit();
                            }
                            this.await = false
                        }

                    }
                })
            },

            searchDataTasks(){
                this.dataCurPage = 1
                this.getDataTasks()
            },

            addOutputToMyData(output,index) {
                if (output.url == undefined || output.url == ""){
                    this.$message({
                        message: this.htmlJson.noData,
                        type: 'warning'
                    });
                    return;
                }

                console.log(output)
                this.outputToMyData = output
                this.addOutputToMyDataVisible = true
                this.selectedPath = [];
                axios.get("/user/getFolder", {})
                    .then(res => {
                        let json = res.data;
                        if (json.code == -1) {
                            alert(this.htmlJson.LoginInFirst)
                            window.sessionStorage.setItem("history", window.location.href);
                            window.location.href = "/user/login"
                        } else {
                            this.folderTree = res.data.data;
                            this.selectPathDialog = true;
                            // this.$nextTick(()=>{
                            //     // this.$refs.folderTree.setCurrentKey(null); //打开树之前先清空选择
                            // })
                        }


                    });
            },

            shareOutput(url){
                this.shareIndex=true;
                this.downloadUrl=url;
            },

            copyLink(){
                console.log(this.clipBoard);
                let vthis = this;
                this.clipBoard.on('success', function () {
                    vthis.$alert('Copy link successly',{type:'success',confirmButtonText: 'OK',})
                    this.clipBoard.destroy()
                });
                this.clipBoard.on('error', function () {
                    vthis.$alert("Failed to copy link",{type:'error',confirmButtonText: 'OK',})
                    this.clipBoard.destroy()
                });
                this.shareIndex=false
            },

            addOutputToMyDataConfirm(index) {
                let data = this.$refs.folderTree2.getCurrentNode();
                let node = this.$refs.folderTree2.getNode(data);

                while (node.key != undefined && node.key != 0) {
                    this.selectedPath.unshift(node);
                    node = node.parent;
                }
                let allFder = {
                    key: '0',
                    label: 'All Folder'
                }
                this.selectedPath.unshift(allFder)
                console.log(this.selectedPath)

                this.uploadInPath = 0

                //模型容器返回的url可能带有pwd字段
                let originUrl = this.outputToMyData.url

                let dataUrl = originUrl
                if(originUrl.indexOf('pwd')!=-1){
                    dataUrl = originUrl.split('?')[0]
                }
                let obj = {
                    label: this.outputToMyData.tag,
                    suffix: this.outputToMyData.suffix,
                    url: dataUrl,
                    templateId:this.outputToMyData.url.split('/')[this.outputToMyData.url.split('/').length-1],
                }

                this.addDataToPortalBack(obj)
                this.addOutputToMyDataVisible = false
            },

            async dropPackageContent(item, index) {

                let arrow = $('.treeChildLi').eq(index);
                let father = $('ul.flexLi')
                let autoHeightFaOld = this.autoHeightFaOld;
                let targetLi = $('.packageContent').eq(index);
                let autoHeight = (this.packageContentList[index].inputs.length + this.packageContentList[index].outputs.length) * 57 + 79
                let autoHeightFa = autoHeight + autoHeightFaOld

                for (let i = 0; i < this.userTaskFullInfo.tasks.length; i++) {
                    if ((i === index)) {
                        if (!arrow.hasClass('expanded')) {
                            arrow.addClass('expanded');
                            father.animate({height: autoHeightFa}, 260, 'linear');
                            targetLi.animate({height: autoHeight}, 500, 'linear');
                            this.sharingTaskData(item, index);

                        } else if (arrow.hasClass('expanded')) {
                            father.animate({height: autoHeightFaOld}, 320)
                            $('.packageContent').eq(index).animate({height: 0}, 300);
                            $('.treeChildLi').eq(index).removeClass('expanded');
                        }
                    } else {
                        $('.treeChildLi').eq(i).removeClass('expanded');
                        $('.packageContent').eq(i).animate({height: 0}, 300);
                        // father.animate({height:autoHeightFaOld},320)
                    }

                }
            },

            getUserData(UsersInfo, prop) {

                for (i = prop.length; i > 0; i--) {
                    prop.pop();
                }
                var result = "{";
                for (index=0 ; index < UsersInfo.length; index++) {
                    //
                    if(index%4==0){
                        let value1 = UsersInfo.eq(index)[0].value.trim();
                        let value2 = UsersInfo.eq(index+1)[0].value.trim();
                        let value3 = UsersInfo.eq(index+2)[0].value.trim();
                        let value4 = UsersInfo.eq(index+3)[0].value.trim();
                        if(value1==''&&value2==''&&value3==''&&value4==''){
                            index+=4;
                            continue;
                        }
                    }

                    var Info = UsersInfo.eq(index)[0];
                    if (index % 4 == 3) {
                        if (result) {
                            result += "'" + Info.name + "':'" + Info.value + "'}"
                            prop.push(eval('(' + result + ')'));
                        }
                        result = "{";
                    }
                    else {
                        result += "'" + Info.name + "':'" + Info.value + "',";
                    }

                }
            },

            taskSharingPre() {
                let len = $(".taskSharingStep").length;
                if (this.taskSharingActive != 0)
                    this.taskSharingActive--;
                // if(this.curIndex=='3-3'){
                //     $('.dataItemShare').eq(this.taskSharingActive).animate({marginLeft:0},200)
                //     $('.dataItemShare').eq(this.taskSharingActive+1).animate({marginleft:1500},200)
                // }
            },
            taskSharingFinish() {

                this.taskSharingActive = 4;
                var selectResult = [];
                selectResult = this.multipleSelection;


                console.log(selectResult);
                for (let select of selectResult) {
                    if (select.tag) {
                        select.name = select.tag;
                        select.suffix = 'unknow';
                    } else {
                        select.name = select.fileName;
                        select.suffix = select.suffix;
                    }

                    this.taskDataForm.dataList.push(select);
                }

                this.taskDataForm.detail = tinyMCE.activeEditor.getContent();

                this.taskDataForm.keywords = $("#taskDataKeywords").val().split(",");

                this.taskDataForm.author = this.userId;

                // this.dataItemAddDTO.meta.coordinateSystem = $("#coordinateSystem").val();
                // this.dataItemAddDTO.meta.geographicProjection = $("#geographicProjection").val();
                // this.dataItemAddDTO.meta.coordinateUnits = $("#coordinateUnits").val();
                // this.dataItemAddDTO.meta.boundingRectangle=[];

                let authorship = [];
                userspace.getUserData($("#providersPanel .user-contents .form-control"), authorship);
                this.taskDataForm.authorship = authorship;
                console.log(this.taskDataForm)
                this.taskDataForm.dataType = "File";

                axios.post("/dataItem/", this.taskDataForm)
                    .then(res => {
                        console.log(res);
                        if (res.status == 200) {

                            this.openConfirmBox("create successful! Do you want to view this Data Item?", "Message", res.data.data.id);
                            this.taskSharingVisible = false;
                        }
                    })
            },
            showWaring(text) {
                this.$message({
                    showClose: true,
                    message: text,
                    type: 'warning'
                });
            },
            taskSharingNext() {

                //检查
                switch (this.taskSharingActive) {
                    case 0:
                        if (this.multipleSelection.length + this.multipleSelectionMyData.length == 0) {
                            this.showWaring(this.htmlJson.PleaseSelectDataFirst);
                            return;
                        }
                        break;
                    case 1:
                        if (this.taskDataForm.classifications.length == 0) {
                            this.showWaring(this.htmlJson.PleaseChooseCategoriesFromSidebar)
                            return;
                        }
                        if (this.taskDataForm.name.trim() == '') {
                            this.showWaring(this.htmlJson.noNameTip);
                            return;
                        }
                        if ($("#taskDataKeywords").val().split(",")[0] == '') {
                            this.showWaring(this.htmlJson.EnterKeywords);
                            return;
                        }

                        if (this.taskDataForm.description == '') {
                            this.showWaring('Please enter overview');
                            return;
                        }
                        break;
                    case 2:
                        if (tinyMCE.activeEditor.getContent().trim() == '') {
                            this.showWaring('Please enter detailed description');
                            return;
                        }
                        break;

                }


                //翻页
                let len = $(".taskSharingStep").length;
                if (this.taskSharingActive < len)
                    this.taskSharingActive++;
                if (this.taskSharingActive == 1) {
                    if ($("#taskDataShareDialog .tag-editor").length == 0) {
                        $("#taskDataKeywords").tagEditor({
                            forceLowercase: false
                        })
                    }

                    tinymce.init({
                        selector: "textarea#taskDataDetail",
                        height: 205,
                        theme: 'silver',
                        plugins: ['link', 'table', 'image', 'media'],
                        image_title: true,
                        // enable automatic uploads of images represented by blob or data URIs
                        automatic_uploads: true,
                        // URL of our upload handler (for more details check: https://www.tinymce.com/docs/configure/file-image-upload/#images_upload_url)
                        // images_upload_url: 'postAcceptor.php',
                        // here we add custom filepicker only to Image dialog
                        file_picker_types: 'image',

                        file_picker_callback: function (cb, value, meta) {
                            var input = document.createElement('input');
                            input.setAttribute('type', 'file');
                            input.setAttribute('accept', 'image/*');
                            input.onchange = function () {
                                var file = input.files[0];

                                var reader = new FileReader();
                                reader.readAsDataURL(file);
                                reader.onload = function () {
                                    var img = reader.result.toString();
                                    cb(img, {title: file.name});
                                }
                            };
                            input.click();
                        },
                        images_dataimg_filter: function (img) {
                            return img.hasAttribute('internal-blob');
                        }
                    });
                }


                // if(this.curIndex=='3-3'){
                //     console.log($('.dataItemShare').eq(this.taskSharingActive))
                //     $('.dataItemShare').eq(this.taskSharingActive-1).animate({marginLeft:-1500},220)
                //     $('.dataItemShare').eq(this.taskSharingActive).animate({marginLeft:0},220)
                // }
            },

            checkMultiContent(output){
                this.multiFileDialog = true;
                this.outputMultiFile = [];
                let urls = output.url.substring(1, output.url.length-1).split(',')
                for(let i = 0;urls&&i<urls.length;i++){
                    let obj={
                        name:output.tag+'.'+output.suffix,
                        tag:output.tag,
                        url:urls[i].substring(1,urls[i].length-1),
                        visual:output.visual,
                        event:output.event,
                        suffix:output.suffix
                    }
                    this.outputMultiFile.push(obj)
                }
            },

            handleSelectionChange(val) {
                if (val)
                    this.multipleSelection = val
                console.log(this.multipleSelection)
            },

            handleSelectionChangeMyData(val) {
                if (val)
                    this.multipleSelectionMyData = val
                console.log(this.multipleSelectionMyData)
            },

            handleSelectionChangeRow(val, row) {
                this.multipleSelection.push(row)
                // this.$refs.multipleTableOutput.toggleRowSelection(row);
                // console.log(this.$refs.multipleTableOutput[1].clearSelection())
            },
            checkSelectedFile() {
                this.checkSelectedIndex = 1;
            },
            filterType(value, row) {
                return row.type === value;
            },
            filterState(value, row) {
                return row.statename === value;
            },

            initTaskDataForm() {
                this.taskDataList = [];
                this.taskSharingActive = 0;
                this.stateFilters = [];
                this.taskCollapseActiveNames = [];
                //如果是task条目下则清空，不是则会在其他事件中清空
                if (this.curIndex == 6)
                    this.multipleSelection = [];
                this.taskDataForm = {
                    name: '',
                    type: "option1",
                    contentType: "resource",
                    description: "",
                    detail: "",
                    reference: "",
                    author: "",
                    keywords: [],
                    contributers: [],
                    classifications: [],
                    displays: [],
                    authorship: [],
                    comments: [],
                    dataList: [],

                    categoryText: [],
                };
                $(".taskDataCate").children().css("color", "black");

                if ($("#taskDataShareDialog .tag-editor").length != 0) {
                    $('#taskDataKeywords').tagEditor('destroy');
                }

                $("#taskDataKeywords").tagEditor({
                    initialTags: [''],
                    forceLowercase: false
                });

                // tinyMCE.activeEditor.setContent("");
                $(".taskDataAuthorship").remove();
                $(".user-add").click();
            },

            sharingTaskData(task, index) {

                this.initTaskDataForm();

                this.taskSharingActive = 0;
                let inputs = task.inputs;
                let outputs = task.outputs;
                for (let input of inputs) {
                    input.type = "Input";
                    this.taskDataList.push(input);

                    let exist = false;
                    for (let filter of this.stateFilters) {
                        if (filter.value == input.statename) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        let obj = {};
                        obj.text = input.statename;
                        obj.value = input.statename;
                        this.stateFilters.push(obj);
                    }
                }
                for (let output of outputs) {
                    output.type = "Output";
                    this.taskDataList.push(output);

                    let exist = false;
                    for (let filter of this.stateFilters) {
                        if (filter.value == output.statename) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        let obj = {};
                        obj.text = output.statename;
                        obj.value = output.statename;
                        this.stateFilters.push(obj);
                    }
                }

                this.taskSharingVisible = true;


            },

            handleClose(done) {
                console.log(done)
                this.$confirm(this.htmlJson.AreYouSureToClose)
                    .then(_ => {
                        done();
                    })
                    .catch(_ => {
                    });
            },

            closeAndClear() {

            },

            handleCloseandInit(done) {
                console.log(done)
                this.$confirm(this.htmlJson.AreYouSureToClose)
                    .then(_ => {
                        for (let i = 0; i < $('.treeLi').length; i++) {
                            $('.treeLi').eq(i).removeClass('expanded');
                            $('.flexLi').eq(i).animate({height: 0}, 300);
                        }
                        for(let i=0;i<$('.treeChildLi').length;i++){
                            $('.treeChildLi').eq(i).removeClass('expanded');
                            $('.packageContent').eq(i).animate({height:0},300);
                        }
                        for(let i=0;i<this.$refs.multipleTableDataSharing.length;i++)
                            this.$refs.multipleTableDataSharing[i].clearSelection();
                        this.$refs.multipleTableMyData.clearSelection();

                        done();
                    })
                    .catch(_ => {
                        done();
                    });
            },

            downloadSingle(url) {
                if (url == undefined || url == ""){
                    this.$message({
                        message: this.htmlJson.noDownloadLink,
                        type: 'warning'
                    });
                } else {
                    window.open(url);
                }
            },

            //可视化
            visualization(url){
                if (url == undefined || url == ""){
                    this.$message({
                        message: this.htmlJson.noVisualizeLink,
                        type: 'warning'
                    });
                } else {
                    // url = "http://221.226.60.2:8082/data/8c46f99c-77ac-496a-b6c8-290b653a10cf"
                    let urlArr = url.split("/");
                    // 兰德访问不了门户网站的数据，要转成兰德的服务器
                    url = "http://221.226.60.2:8082/data/" + urlArr[urlArr.length - 1]
                    window.open('http://221.226.60.2:8082/onlinePreview?url=' + Base64.encode(url));
                }

            },

            downloadAll(recordId, name, time) {
                var form = document.createElement("form");
                form.style.display = "none";

                form.setAttribute("target", "");
                form.setAttribute('method', 'get');
                form.setAttribute('action', "https://geomodeling.njnu.edu.cn/GeoModeling/DownloadAllDataServlet");

                var input1 = document.createElement("input");
                input1.setAttribute('type', 'hidden');
                input1.setAttribute('name', 'recordId');
                input1.setAttribute('value', recordId);

                var input2 = document.createElement("input");
                input2.setAttribute('type', 'hidden');
                input2.setAttribute('name', 'name');
                input2.setAttribute('value', name);

                var input3 = document.createElement("input");
                input3.setAttribute('type', 'hidden');
                input3.setAttribute('name', 'time');
                input3.setAttribute('value', time);

                form.appendChild(input1);
                form.appendChild(input2);
                form.appendChild(input3);

                document.body.appendChild(form);  //将表单放置在web中
                //将查询参数控件提交到表单上
                form.submit();
                form.remove();
            },

            chooseTaskDataCate(item, e) {
                let exist = false;
                let cls = this.taskDataForm.classifications;
                for (i = 0; i < cls.length; i++) {
                    if (cls[i] == item.id) {
                        if (e.target.type == "button") {
                            e.target.children[0].style.color = "black";
                        } else {
                            e.target.style.color = 'black';
                        }

                        cls.splice(i, 1);
                        this.taskDataForm.categoryText.splice(i, 1);
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    if (e.target.type == "button") {
                        e.target.children[0].style.color = "deepskyblue";
                    } else {
                        e.target.style.color = 'deepskyblue';
                    }

                    if (!exist) {
                        if (e.target.type == "button") {
                            e.target.children[0].style.color = "deepskyblue";
                        } else {
                            e.target.style.color = 'deepskyblue';
                        }

                        this.taskDataForm.categoryText.push(e.target.innerText);
                        this.taskDataForm.classifications.push(item.id);
                    }

                }
            },

            openConfirmBox(content, title, id) {
                this.$confirm(content, title, {
                    confirmButtonText: "Yes",
                    cancelButtonText: "No",
                    type: 'success'//'warning'
                }).then(() => {
                    window.open("/dataItem/" + id);
                }).catch(() => {

                });
            },

            openAlertBox(content, title) {
                this.$alert(content, title, {
                    confirmButtonText: 'OK',
                    callback: action => {

                    }
                });
            },

            //展开task详细数据
            expandRunInfo(index,$event){
                if(!$('.ab').eq(index).hasClass('transform180')){
                    $('.ab').eq(index).addClass('transform180')
                    $('.modelRunInfo').eq(index).collapse('show')
                }else {
                    $('.ab').eq(index).removeClass('transform180')
                    $('.modelRunInfo').eq(index).collapse('hide')
                }

            },

            expandDataTaskRunInfo(index,$event){
                if(!$('.ab2').eq(index).hasClass('transform180')){
                    $('.ab2').eq(index).addClass('transform180')
                    $('.dataModelRunInfo').eq(index).collapse('show')
                }else {
                    $('.ab2').eq(index).removeClass('transform180')
                    $('.dataModelRunInfo').eq(index).collapse('hide')
                }

            },

            //task output加入data space中
            addFolderinTree(pageIndex,index){
                let node, data

                data = this.$refs.folderTree2.getCurrentNode();
                if (data == undefined || data == null) alert(this.htmlJson.selectDirTip)
                node = this.$refs.folderTree2.getNode(data);

                let folderExited = data.children

                console.log(node);
                let paths=[];
                while(node.key!=undefined&&node.key!=0){
                    paths.push(node.key);
                    node=node.parent;
                }
                if(paths.length==0) paths.push('0')
                console.log(paths)

                var newChild={id:""}

                this.$prompt(null, 'Enter Folder Name', {
                    confirmButtonText: 'OK',
                    cancelButtonText: 'Cancel',
                    // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                    // inputErrorMessage: '邮箱格式不正确'
                }).then(({ value }) => {
                    if(folderExited.some((item)=>{
                        return  item.label===value;
                    })==true){
                        alert(this.htmlJson.ThisNameIsExistingPleaseInputNewOne);
                        return
                    }

                    $.ajax({
                        type: "POST",
                        url: "/user/addFolder",
                        data: {paths: paths, name: value},
                        async: false,
                        contentType: "application/x-www-form-urlencoded",
                        success: (json) => {
                            if (json.code == -1) {
                                alert(this.htmlJson.LoginInFirst)
                                window.sessionStorage.setItem("history", window.location.href);
                                window.location.href = "/user/login"
                            }
                            else {
                                newChild = {id: json.data, label: value, children: [], father: data.id ,package:true,suffix:'',upload:false, url:'',};
                                if (!data.children) {
                                    this.$set(data, 'children', []);
                                }
                                data.children.push(newChild);

                                setTimeout(()=>{
                                    this.$refs.folderTree2.setCurrentKey(newChild.id)
                                },100)
                            }

                        }

                    });


                }).then(()=>{

                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: 'Cancel'
                    });
                });



            },

            submitUpload() {
                if(this.uploadName==""){
                    this.$message.error(this.htmlJson.PleaseEnterTheDatasetName);
                    return;
                }
                if(this.selectValue==""){
                    this.$message.error(this.htmlJson.PleaseSelectADataTemplate);
                    return;
                }
                if (this.selectedPath.length == 0) {
                    this.$message.error(this.htmlJson.PleaseSelectAFolderFirst);
                    return;
                }
                if(this.uploadFiles.length==0){
                    this.$message.error(this.htmlJson.PleaseSelectFiles);
                    return;
                }

                let formData = new FormData();

                this.uploadLoading=true;

                let configContent = "<UDXZip><Name>";
                for(let index in this.uploadFiles){
                    configContent+="<add value='"+this.uploadFiles[index].name+"' />";
                    formData.append("ogmsdata", this.uploadFiles[index].raw);
                }
                configContent += "</Name>";
                if(this.selectValue!=null&&this.selectValue!="none"){
                    configContent+="<DataTemplate type='id'>";
                    configContent+=this.selectValue;
                    configContent+="</DataTemplate>"
                }
                else{
                    configContent+="<DataTemplate type='none'>";
                    configContent+="</DataTemplate>"
                }
                configContent+="</UDXZip>";
                // console.log(configContent)
                let configFile = new File([configContent], 'config.udxcfg', {
                    type: 'text/plain',
                });
                formData.append("ogmsdata", configFile);
                formData.append("name", this.uploadName);
                formData.append("userId", this.uid);
                formData.append("serverNode", "china");
                formData.append("origination", "portal");

                $.get("/dataManager/dataContainerIpAndPort", (result) => {
                    let ipAndPort = result.data;

                    $.ajax({
                        url: 'http://'+ipAndPort+'/data',
                        type: 'post',
                        data: formData,
                        cache: false,
                        processData: false,
                        contentType: false,
                        async: true
                    }).done((res)=> {
                        if(res.code==0){
                            let data=res.data;
                            if(this.uploadFiles.length==1){
                                data.suffix=this.uploadFiles[0].name.split(".")[1];
                            }
                            else{
                                data.suffix="zip";
                            }
                            data.label=data.file_name;
                            data.file_name+="."+data.suffix;
                            data.upload=true;
                            data.templateId=this.selectValue;
                            this.addDataToPortalBack(data,this.selectValue);

                            //reset
                            this.uploadName="";
                            this.selectValue="";
                            this.selectedPath=[];
                            this.uploadFiles=[];
                            this.remoteMethod("");
                            this.$refs.upload.clearFiles();


                        }else{
                            this.$message.error(this.htmlJson.UploadFailed);
                        }


                        console.log(res);
                    }).fail((res)=> {

                        this.uploadLoading=false;
                        this.uploadDialogVisible=false;
                        this.$message.error(this.htmlJson.UploadFailed);
                    });
                });

            },

            addDataToPortalBack(item){//item为undefined,则为用户上传；其他为页面已有数据的上传、修改路径

                var addItem=[]
                if(item instanceof Array) {
                    addItem=item;
                    // for(let i=0;i<addItem.length;i++)
                    //     addItem[i].file_name=this.splitFirst(addItem[i].file_name,'&')[1]
                }
                else{
                    let obj={
                        file_name:item.label+'.'+item.suffix,
                        label:item.label,
                        suffix:item.suffix,
                        source_store_id:item.url.split('/')[item.url.split('/').length-1],
                        templateId:item.templateId,
                        upload:'false'
                    }
                    if(item.url==='') obj.source_store_id = '';
                    addItem[0]=obj
                }
                let paths=[]
                if(this.uploadInPath==1){
                    let i=this.pathShown.length-1;
                    while (i>=0) {
                        paths.push(this.pathShown[i].id);
                        i--;
                    }
                    if(paths.length==0)paths=['0']

                }else{
                    if(this.selectedPath.length==0) {
                        alert('Please select a folder')
                        return
                    }

                    let i=this.selectedPath.length-1;//selectPath中含有all folder这个不存在的文件夹，循环索引有所区别
                    while (i>=1) {
                        paths.push(this.selectedPath[i].key);
                        i--;
                    }
                    if(paths.length==0)paths=['0']
                }
                let that = this;
                $.ajax({
                    type: "POST",
                    url: "/user/addFile",
                    data: JSON.stringify({
                        files: addItem,
                        paths: paths
                    }),

                    async: true,
                    traditional:true,
                    contentType: "application/json",
                    success: (json) => {
                        if (json.code == -1) {
                            alert(this.htmlJson.LoginInFirst)
                            window.sessionStorage.setItem("history", window.location.href);
                            window.location.href = "/user/login"
                        }else{
                            this.$message({
                                message: 'Upload successfully!',
                                type: 'success'
                            });
                        }


                    }
                });

                // alert('Upload File successfully!')


            },


            checkOutput(modelId, taskId, integrate) {
                if (integrate){
                    window.open('/computableModel/getIntegratedTask/' + taskId);
                } else{
                    window.open('/task/output/' + modelId + '&' + taskId);
                }
            },
            checkDataTaskOutput(taskId) {
                window.open('/task/dataTaskOutput/' + taskId)
            },

            initDataTaskFindDto(){
                this.dataResourceLoad = true
                this.dataSearchResult = []

                this.dataTaskFindDto.page = this.dataCurPage
                this.dataTaskFindDto.asc = this.dataSortAsc === 1? true: false
                this.dataTaskFindDto.sort = this.dataSortType
                this.dataTaskFindDto.pageSize = this.dataPageSize

                if(this.dataTaskStatus === 'all'){      // status字段： started: 1,  finished: 2,  inited: 0,   error: -1, 目前只用到了2 和 -1
                    this.dataTaskFindDto.status = 0
                    $('.wzhSelectContainer input').css('background', '#00ABFF')
                } else if (this.dataTaskStatus === 'successful'){
                    this.dataTaskFindDto.status = 2
                    $('.wzhSelectContainer input').css('background', '#63b75d')
                } else if(this.dataTaskStatus === 'calculating'){
                    this.dataTaskFindDto.status = -1
                    $('.wzhSelectContainer input').css('background', '#1caf9a')
                }else{
                    $('.wzhSelectContainer input').css('background', '#d74948')
                }
                this.dataTaskFindDto.searchText = this.dataSearchText
                this.dataTaskFindDto.sortField = this.dataSortType
            },

            getDataTasks(){     // 一些数据有问题，现在还没有解决
                this.initDataTaskFindDto()
                let that = this
                console.log(this.dataTaskFindDto)
                axios.post('/task/dataTasks', that.dataTaskFindDto)
                    .then(res=>{
                        setTimeout(()=>{
                            that.dataSearchResult = res.data.data.list
                            that.dataTotalNum = res.data.data.totalNum
                            that.dataTaskIsWrong = false
                            // try {        当时是为了处理input 和 output写的，现在已经使用inputs 和 outputs了
                            //     for(let i=0;i<that.dataSearchResult.length;++i){
                            //         console.log(that.dataSearchResult[i].input.input instanceof Array)
                            //         if(!(that.dataSearchResult[i].input.input instanceof Array)){
                            //             that.dataSearchResult[i].input.input = JSON.parse(that.dataSearchResult[i].input.input)
                            //         }
                            //         if(!(that.dataSearchResult[i].output.output instanceof Array)) {
                            //             let temp = that.dataSearchResult[i].output.output
                            //             that.dataSearchResult[i].output.output = []
                            //             that.dataSearchResult[i].output.output.push({'url': temp})
                            //         }
                            //     }
                            // } catch (err){
                            //     console.log(err)
                            // }
                            that.dataResourceLoad = false
                            that.dataPageInit()
                        })
                    })
                // input对象可能就是一个字符串，output的字符串里面可能就一个url
            },

            sendUserToParent(userId){
                this.$emit('com-senduserinfo',userId)
            },

            showTaskInfo(task){
                if (task.permission === "private"){
                    this.dialogFormVisible = true
                    this.publicTask = task
                    console.log(task)
                }
                else {
                    this.publishTask(task)
                }
            },

            taskInfoConfirm(){
                if (this.taskInfo.taskDesc === '' || this.taskInfo.taskDateRange === '' || this.taskInfo.taskApplyArea === ''){
                    this.$message({
                        message: '请完善信息',
                        type: 'warning'
                    });
                    return
                }

                this.taskInfo.id = this.publicTask.id
                let taskData = JSON.stringify({
                    id: this.taskInfo.id,
                    timeRange: this.taskInfo.taskDateRange,
                    applicationArea: this.taskInfo.taskApplyArea,
                    description: this.taskInfo.taskDesc
                })
                console.log(taskData)
                this.dialogFormVisible = false
                axios.post('/task/desc/info', taskData, {headers: {
                        'Content-Type': 'application/json'
                    }})
                    .then(res=>{
                        console.log(res)
                        this.publishTask(this.publicTask)
                    })
            }
        },

        // created() {
        //
        //
        // },
        computed:{
            options(){
                return this.htmlJson.options
            },
            itdOptions(){
                return this.htmlJson.itdOptions
            }
        },

        mounted() {


            this.showTasksByStatus();
            this.clipBoard = new ClipboardJS(".copyLinkBtn");
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
                    success: (result) => {
                        // console.log(data);

                        if (result.code !== 0) {
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href = "/user/login";
                        } else {
                            let data = result.data;
                            this.userId = data.accessId;
                            this.userName = data.name;
                            console.log(this.userId)
                            this.sendUserToParent(this.userId)
                            // this.addAllData()

                            // axios.get("/dataItem/amountofuserdata",{
                            //     params:{
                            //         userOid:this.userId
                            //     }
                            // }).then(res=>{
                            //     that.dcount=res.data
                            // });

                            $("#author").val(this.userName);

                            var index = window.sessionStorage.getItem("index");
                            //判断显示哪一个item
                            var itemIndex = window.sessionStorage.getItem("itemIndex");
                            this.itemIndex = itemIndex

                            // if (index != null && index != undefined && index != "" && index != NaN) {
                            //     this.defaultActive = index;
                            //     window.sessionStorage.removeItem("index");
                            //     // this.curIndex = index
                            //
                            //
                            // } else {
                            //     // this.changeRter(1);
                            // }

                            window.sessionStorage.removeItem("tap");
                            //this.getTasksInfo();
                            this.load = false;
                        }
                    }
                })


                //this.getModels();
            });


            var that = this

        },

    }
)
