//组件
var modelItem = Vue.extend({
    template: '#modelItemShow',
    props: ['searchResultRaw','htmlJson'],
    // created(){
    //     console.log("searchResultRaw:",this.searchResultRaw);
    // },
    watch:{
        searchResultRaw:{
            handler(val) {
                this.searchResultRaw = val;
            },
            deep:true,
            immediate:true
        }
    },
    data() {
        return {
            itemIndex:1,

            chartDialogVisible:false,
            chartDialogTitle:"",

            currentOid:'',
            currentName:'',

            admins:[],
            adminsDialogVisible:false,
            adminsDialogTitle:"",


        }

    },

    methods:{
        formatDateChild(val){
            let res
            this.$emit('com-format',val,a => { res = a })
            return res
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
            // this.editOid = sessionStorage.getItem('editItemOid');
        },

        seeDetailPage(oid){
            let a=this.$route.params.modelitemKind;
            let urls={
                'modelitem':'modelItem',
                'conceptualmodel':'conceptualModel',
                'logicalmodel':'logicalModel',
                'computablemodel':'computableModel'
            }
            window.open('/'+urls[a]+'/'+oid)
        },

        getType(){
            return this.$route.params.modelitemKind;
        },

        comEditItem(index,oid){
            this.$emit('com-edit',index,oid)
        },

        modelStats(){
            let chart=echarts.init(document.getElementById('chart'));
            chart.showLoading();
            $.get("/modelItem/dailyViewAndInvokeCount",{id:this.currentOid},(result)=> {
                let valueList = result.data.valueList;//[0, 0, 0, 0, 0];

                var invokeTime = ""
                var viewTime = ""
                let lang = window.localStorage.getItem('language');
                if(lang=="en-us"){
                    invokeTime = "Invoke Times"
                    viewTime = "View Times"
                }else{
                    invokeTime = "调用量"
                    viewTime = "访问量"
                }

                result.data.valueList[1][0] = viewTime
                result.data.valueList[2][0] = invokeTime
                console.log(result)
                chart.hideLoading();

                let series = [];
                for(i=1;i<valueList.length;i++){
                    series.push({type: 'line', smooth: false, seriesLayoutBy: 'row'});
                }
                let option = {
                    legend: {},
                    tooltip: {
                        trigger: 'axis',
                        showContent: true
                    },
                    dataset: {
                        source:valueList
                        //     [
                        //     ['product', '2012', '2013', '2014', '2015', '2016', '2017'],
                        //     ['Matcha Latte', 41.1, 30.4, 65.1, 53.3, 83.8, 98.7],
                        //     ['Milk Tea', 86.5, 92.1, 85.7, 83.1, 73.4, 55.1],
                        //     ['Cheese Cocoa', 24.1, 67.2, 79.5, 86.4, 65.2, 82.5],
                        //     ['Walnut Brownie', 55.2, 67.1, 69.2, 72.4, 53.9, 39.1]
                        // ]
                    },
                    xAxis: {
                        type: 'category',
                        axisTick:{
                            show:false,
                        }
                    },
                    yAxis: {gridIndex: 0},
                    grid: {top: '15%',bottom:'15%'},
                    series:series
                    //     [
                    //     {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                    //     {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                    //     {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                    //     {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                    //
                    // ]
                };

                chart.setOption(option)
            })
        },

        statistics(oid,name){
            if(this.getType()=='computablemodel') {
                window.open("/statistics/computableModel/" + oid)
            }else if (this.getType() == 'modelitem'){
                this.chartDialogVisible = true;
                this.currentOid = oid;
                this.chartDialogTitle = name + this.htmlJson.Statistics
            }

        },

        getAdmins(oid,name){
            $.get("/modelItem/getAdmins",{oid:oid},(result)=>{
                this.admins = result.data;
            });
            this.adminsDialogTitle = name + "'s admins";
            this.adminsDialogVisible = true;
            this.currentOid = oid;
        },

        addAdmin(){
            this.$prompt('Please enter user\'s email', 'Add admin', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
            }).then(({ value }) => {

                $.post("/modelItem/addAdmin",{oid:this.currentOid,email:value},(result)=>{
                    let code = result.code;
                    if(code === 0){
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.AddAdminSuccessfully
                        });
                        this.admins.push(result.data);
                    }else{
                        this.$message({
                            type: 'error',
                            message: result.msg,
                        });
                        return;
                    }

                });


            })
        },

        deleteAdmin(userName){
            $.post("/modelItem/removeAdmin",{oid:this.currentOid,userName:userName},(result)=>{
                let code = result.code;
                if(code === 0){
                    for(i=0;i<this.admins.length;i++){
                        let admin = this.admins[i];
                        if(admin.userName === userName){
                            this.admins.splice(i,1);
                        }
                    }
                    this.$message({
                        type: 'success',
                        message: this.htmlJson.DeleteAdminSuccessfully
                    });
                }
            })
        },

        comDeleteItem(index,oid){
            this.$emit('com-delete',index,oid)
        },
    },

    created(){
        // this.$parent.reloadPage()
        // this.$parent.getModels(1);
    }

})
// Vue.component('myComponent',modelItem)

//该组件的子组件通过router控制，每次重拿this.$route.params.modelitemKind避免回退出现混乱
var userModels = Vue.extend(
    {
        template: "#userModels",
        props: ["htmlJson"],
        data(){
            return{

                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex:2,

                itemIndex:1,

                typeRadio:"1",
                //
                userInfo:{

                },

                await: false,

                //分页控制
                page: 1,
                sortAsc: -1,//1 -1
                sortType: "default",
                pageSize: 10,// 每页数据条数
                totalPage: 0,// 总页数
                curPage: 1,// 当前页码
                pageList: [],
                totalNum: 0,

                //展示变量\
                itemTitle:'Model Item',

                searchResult: [],
                modelItemResult: [],

                searchCount: 0,
                ScreenMaxHeight: "0px",
                searchText: "",

                isInSearch:0,
            }
        },

        components: {
        },

        watch: {
            $route() {
                this.getModels()
            },
            htmlJson: function (newData) {
                let href = window.location.href;
                let arr = href.split("/");
                let currentType = arr[arr.length-1]
                this.itemTitle = newData.userModel[currentType];
            }
        },

        // router:modelRouter,

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

            manageItem(index){
                //此处跳转至统一页面，vue路由管理显示
                this.itemIndex=index;
                this.searchText = ''
                var urls={
                    1:'/user/userSpace#/models/modelitem',
                    2:'/user/userSpace#/models/conceptualmodel',
                    3:'/user/userSpace#/models/logicalmodel',
                    4:'/user/userSpace#/models/computablemodel',
                };
                window.location.href=urls[index]
                this.getModels();


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

                    switch (this.pageControlIndex) {
                        // this.computerModelsDeploy = [];
                        // this.resourceLoad = true;
                        // this.curPage = pageNo;
                        // this.getPageList();
                        // this.page = pageNo;
                        // this.getDataItems();
                        case 2:

                            if (this.isInSearch == 0)
                                this.getModels();
                            else this.searchItems();
                            break;
                        //



                    }
                    // if(this.researchIndex==1||this.researchIndex==2||this.researchIndex==3){
                    //     this.resourceLoad = true;
                    //     this.searchResult = [];
                    //     //not result scroll
                    //     //window.scrollTo(0, 0);
                    //     this.curPage = pageNo;
                    //     this.getPageList();
                    //     this.pageSize=4;
                    //     this.page = pageNo;
                    //     this.getResearchItems();
                    // }
                    //this.changeCurPage.emit(this.curPage);
                }
            },

            creatItem(index){
                let a=this.$route.params.modelitemKind
                var urls={
                    'modelitem':      '/user/userSpace#/model/createModelItem',
                    'conceptualmodel':'/user/userSpace#/model/createConceptualModel',
                    'logicalmodel':   '/user/userSpace#/model/createLogicalModel',
                    'computablemodel':'/user/userSpace#/model/createComputableModel',
                }
                window.sessionStorage.removeItem('editOid');
                window.location.href=urls[a]
            },

            reloadPage(){//重新装订分页诸元
                this.pageSize = 10;
                this.isInSearch = 0;
                this.page = 1;
            },

            getModels(index) {
                this.pageSize = 10;
                this.isInSearch = 0;
                let a=this.$route.params.modelitemKind
                if(a!=null)
                {
                    a = a.toLowerCase()
                }
                this.await = true
                //副标题切换
                let titles={
                    'modelitem':this.htmlJson.userModel.modelitem,
                    'conceptualmodel':this.htmlJson.userModel.conceptualmodel,
                    'logicalmodel':this.htmlJson.userModel.logicalmodel,
                    'computablemodel':this.htmlJson.userModel.computablemodel
                }
                this.itemTitle=titles[a]

                var url = "";
                var name = "";
                // console.log(this.searchResult);
                if (a === 'modelitem') {
                    // url = "/modelItem/queryListOfAuthorSelf";
                    url = QueryModelItemListOfAuthorSelf()
                    name = "modelItems";
                } else if (a === 'conceptualmodel') {
                    // url = "/conceptualModel/queryListOfAuthorSelf"
                    url = QueryConceptualModelListOfAuthorSelf()
                    name = "conceptualModels";
                } else if (a === 'logicalmodel') {
                    // url = "/logicalModel/queryListOfAuthorSelf"
                    url = QueryLogicalModelListOfAuthorSelf()
                    name = "logicalModels";
                } else if (a === 'computablemodel') {
                    // url = "/computableModel/queryListOfAuthorSelf";
                    url = QueryComputableModelListOfAuthorSelf()
                    name = "computableModels";
                }

                this.$forceUpdate();

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify({
                        page: this.page,
                        sortType: this.sortType,
                        asc: false,
                        authorEmail: this.userId,
                    }),
                    contentType:"application/json",
                    cache: false,
                    async: true,
                    success: (json) => {
                        if (json.code != 0) {
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href = "/user/login";
                        } else {
                            data = json.data;
                            this.resourceLoad = false;
                            this.totalNum = data.total;
                            this.$set(this,"searchResult",data.list);
                            // console.log(this.searchResult);
                            //this.modelItemResult = data[name];
                            if (this.page == 1) {
                                this.pageInit();
                            }
                            this.await = false

                        }
                    }
                })

            },


            searchItems(page) {
                this.resourceLoad = true;
                this.pageSize = 10;
                this.isInSearch = 1;
                let a=this.$route.params.modelitemKind
                this.await = true
                let urls={
                    'modelitem':      '/modelItem/queryListOfAuthorSelf',
                    'conceptualmodel':'/conceptualModel/queryListOfAuthorSelf',
                    'logicalmodel':   '/logicalModel/queryListOfAuthorSelf',
                    'computablemodel':'/computableModel/queryListOfAuthorSelf',
                }
                let names={
                    'modelitem':      'modelItems',
                    'conceptualmodel':'conceptualModels',
                    'logicalmodel':   'logicalModels',
                    'computablemodel':'computableModels',
                }
                let url=urls[a];
                let name=names[a];

                if (this.deploys_show) {
                    this.searchComputerModelsForDeploy();
                } else {
                    let targetPage = page==undefined?this.page:page
                    let reqData = {
                        searchText: this.searchText,
                        page: targetPage,
                        pagesize: this.pageSize,
                        sortType: this.sortType,
                        asc: this.sortAsc === 1,
                        authorEmail: window.localStorage.getItem("account")
                    }
                    $.ajax({
                        type: "Post",
                        url: url,
                        data: JSON.stringify(reqData),
                        cache: false,
                        async: true,
                        // dataType: "json",
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
                                let data = json.data;
                                if(data["list"].length==0&&this.page>1){
                                    this.searchItems(--this.page)
                                    return
                                }
                                this.resourceLoad = false;
                                this.totalNum = data.total;
                                this.searchCount = Number.parseInt(data["total"]);
                                this.$set(this,"searchResult",data["list"]);
                                // console.log(this.searchResult)
                                if (targetPage == 1) {
                                    this.pageInit();
                                }
                                if(page!=undefined){
                                    this.curPage = page
                                }
                                this.await = false
                            }

                        }
                    })
                }
            },

            editItem(index,oid){
                let a=this.$route.params.modelitemKind
                var urls={
                    'modelitem':      '/user/userSpace#/model/manageModelItem/'+oid,
                    'conceptualmodel':'/user/userSpace#/model/manageConceptualModel/'+oid,
                    'logicalmodel':   '/user/userSpace#/model/manageLogicalModel/'+oid,
                    'computablemodel':'/user/userSpace#/model/manageComputableModel/'+oid,
                }
                this.setSession('editId', oid)
                // window.open(urls[a])
                window.location.href = urls[a];
            },

            deleteItem(index,oid) {
                let a = this.$route.params.modelitemKind
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
                                var urls = {
                                    'modelitem': "/modelItem/",
                                    'conceptualmodel': "/conceptualModel/",
                                    'logicalmodel': "/logicalModel/",
                                    'computablemodel': "/computableModel/",
                                };


                                $.ajax({
                                    type: "DELETE",
                                    url: urls[a]+oid,
                                    cache: false,
                                    async: true,
                                    dataType: "json",
                                    xhrFields: {
                                        withCredentials: true
                                    },
                                    crossDomain: true,
                                    success: (json) => {
                                        if (json.code == -1) {
                                            alert(this.htmlJson.LoginInFirst)
                                        } else {
                                            if (json.code == 0) {
                                                this.$message({
                                                    type: 'success',
                                                    message: this.htmlJson.userModel.DeleteSuccessful
                                                });
                                                // this.$alert("delete successfully!")
                                            } else if (json.code == -2) {
                                                this.$alert(this.htmlJson.DeleteFailed)
                                            } else
                                                this.$alert(this.htmlJson.RefreshPage)
                                        }
                                        if (this.searchText.trim() != "") {
                                            this.searchItems(this.page);
                                        } else {
                                            this.getModels(index);
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
                }).then(action => {

                });
            },

            getIcon(){
                let a=this.$route.params.modelitemKind
                var srcs={
                    'modelitem':      '/static/img/model/model.png',
                    'conceptualmodel':'/static/img/model/conceptual.png',
                    'logicalmodel':   '/static/img/model/logical.png',
                    'computablemodel':'/static/img/model/calcModel.png',
                }
                return srcs[a]

            },

            sendcurIndexToParent(){
                this.$emit('com-sendcurindex',this.curIndex)
            },

            sendUserToParent(userId){
                this.$emit('com-senduserinfo',userId)
            },
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
                            let data = result.data
                            this.userId = data.email;
                            this.userName = data.name;
                            this.sendUserToParent(this.userId)

                            $("#author").val(this.userName);

                            var index = window.sessionStorage.getItem("index");
                            //判断显示哪一个item
                            var itemIndex = window.sessionStorage.getItem("itemIndex");

                            this.getModels();

                            if (index != null && index != undefined && index != "" && index != NaN) {
                                this.defaultActive = index;
                                this.handleSelect(index, null);
                                window.sessionStorage.removeItem("index");
                                this.curIndex=index


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

            //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
            this.sendcurIndexToParent()
        },

    }
)