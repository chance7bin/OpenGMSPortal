
var communityItem = Vue.extend({
    template: '#communityShow',
    props: ['searchResultRaw','htmlJson'],
    created(){
        console.log(this.searchResultRaw);
    },
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
            let a=this.$route.params.communityKind
            let urls={
                'concept&semantic':'repository/concept',
                'spatialReference':'repository/spatialReference',
                'dataTemplate':    'repository/template',
                'unit&metric':     'repository/unit'
            }
            window.location.href='/'+urls[a]+'/'+oid
        },

        comEditItem(index,oid){
            this.$emit('com-edit',index,oid)
        },

        comDeleteItem(index,oid){
            this.$emit('com-delete',index,oid)
        },

        sendUserToParent(userId){
            this.$emit('com-senduserinfo',userId)
        },
    },

    created(){
        console.log('aaa')
        // this.$parent.reloadPage()
        // this.$parent.getCommunity(1);
    }

})
// Vue.component('myComponent',modelItem)

var userCommunities = Vue.extend(
    {
        template: "#userCommunities",
        props: ["htmlJson"],
        data(){
            return{
                //页面样式控制
                loading: 'false',
                load: true,
                ScreenMinHeight: "0px",
                ScreenMaxHeight: "0px",

                //显示控制
                curIndex:6,

                itemIndex:1,
                //
                userInfo:{

                },

                await:false,

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
                userEmail:""
            }
        },

        props:['itemindexRaw', 'htmlJson'],

        components: {
        },

        watch: {
            $route() {
                this.getCommunity()
            },
            htmlJson: function (newData) {
                let href = window.location.href;
                let arr = href.split("/");
                let currentType = arr[arr.length-1]
                this.itemTitle = newData.userModel[currentType];
            }
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

            manageItem(index){
                //此处跳转至统一页面，vue路由管理显示
                this.itemIndex=index;
                this.searchText = ''
                var urls={
                    1:'/user/userSpace#/communities/concept&semantic',
                    2:'/user/userSpace#/communities/spatialReference',
                    3:'/user/userSpace#/communities/dataTemplate',
                    4:'/user/userSpace#/communities/unit&metric',
                }
                window.sessionStorage.setItem('itemIndex',index)
                this.reloadPage();
                window.location.href=urls[index]
                this.getCommunity();


            },

            getIcon(){
                let a=this.$route.params.communityKind
                var srcs={
                    'concept&semantic':'/static/img/model/semantics.png',
                    'spatialReference':'/static/img/model/spatialreference.png',
                    'dataTemplate':    '/static/img/model/template.png',
                    'unit&metric':     '/static/img/model/unit.png',
                }
                return srcs[a]

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
                            else this.searchModels();
                            break;
                        //
                        case 3:

                            if (this.isInSearch == 0)
                                this.getDataItems();
                            else this.searchDataItem();
                            break;

                        case 4:

                            if (this.isInSearch == 0)
                                this.getConcepts();
                            else this.searchConcepts();
                            break;
                        case 5:

                            if (this.isInSearch == 0)
                                this.getSpatials();
                            else this.searchSpatials()
                            break;
                        case 6:

                            if (this.isInSearch == 0)
                                this.getCommunity();
                            else this.searchCommunity();
                            break;
                        case 7:

                            if (this.isInSearch == 0)
                                this.getUnits();
                            else this.searchUnits();
                            break;

                        case 8:
                            if (this.isInSearch == 0)
                                this.getTheme();
                            else {}
                            break;

                        case 9:

                            if (this.isInSearch == 0){
                                if(this.taskStatus!=10)
                                    this.showTasksByStatus(this.taskStatus)
                                else
                                    this.getModels();
                            }

                            else this.searchModels();
                            break;

                        case 'research':

                            switch (this.researchIndex) {
                                case 1:
                                    this.getArticleResult();
                                    console.log('article')
                                    break;
                                case 2:
                                    this.getProjectResult();
                                    break;
                                case 3:
                                    this.getConferenceResult();
                                    break;
                            }
                            break;


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
                let a=this.$route.params.communityKind
                var urls={
                    'concept&semantic':'/user/userSpace#/community/createConcept',
                    'spatialReference':'/user/userSpace#/community/createSpatialReference',
                    'dataTemplate':    '/user/userSpace#/community/createTemplate',
                    'unit&metric':     '/user/userSpace#/community/createUnit',
                }
                window.sessionStorage.removeItem('editOid');
                window.location.href=urls[a]
            },

            reloadPage(){//重新装订分页诸元
                this.pageSize = 10;
                this.isInSearch = 0;
                this.page = 1;
            },

            getCommunity(index) {
                this.pageSize = 10;
                this.isInSearch = 0;
                this.await = true
                let a=this.$route.params.communityKind

                // if(index!=null&&index!=undefined)
                //     this.itemIndex = index;userCommunity


                let titles={
                    'concept&semantic':this.htmlJson.ConceptSemantic,
                    'spatialReference':this.htmlJson.SpatialReference,
                    'dataTemplate':this.htmlJson.DataTemplate,
                    'unit&metric':this.htmlJson.UnitMetric
                }
                this.itemTitle=titles[a]

                var url = "";
                var name = "";
                console.log(this.searchResult);
                if (a === 'concept&semantic') {
                    // url = "/repository/getConceptsByUserId";
                    url = QueryConceptListOfAuthorSelf();
                    name = "concepts";
                } else if (a === 'spatialReference') {
                    // url = "/repository/getSpatialsByUserId";
                    url = QuerySpatialListOfAuthorSelf();
                    name = "spatials";
                } else if (a === 'dataTemplate') {
                    // url = "/repository/getTemplatesByUserId";
                    url = QueryTemplateListOfAuthorSelf();
                   name = "templates";
                } else if (a === 'unit&metric') {
                    // url = "/repository/getUnitsByUserId";
                    url = QueryUnitListOfAuthorSelf();
                   name = "units";
                }


                this.$forceUpdate();

                let data = {
                    page: this.page,
                    asc: false,
                    authorEmail:this.userEmail

                };

                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(data),
                    cache: false,
                    async: true,
                    contentType:"application/json",
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
                            this.totalNum = data.total;
                            // this.searchCount = Number.parseInt(data["count"]);
                            //this.searchResult = data[name];
                            // for (var i = 0; i < data[name].length; i++) {
                            //     // this.searchResult.push(data[name][i]);
                            //     this.searchResult.splice(i, 0, data[name][i]);
                            //     console.log(data[name][i]);
                            // }
                            this.$set(this,"searchResult",data.list);
                            console.log(this.searchResult);
                            //this.modelItemResult = data[name];
                            if (this.page == 1) {
                                this.pageInit();
                            }
                            this.await = false
                        }
                    }
                })

            },

            searchCommunity(page) {
                this.resourceLoad = true;
                this.pageSize = 10;
                this.isInSearch = 1;
                this.await = true
                let a=this.$route.params.communityKind
                let urls={
                    'concept&semantic':'/concept/queryListOfAuthorSelf',
                    'spatialReference':'/spatialReference/queryListOfAuthorSelf',
                    'dataTemplate':    '/template/queryListOfAuthorSelf',
                    'unit&metric':     '/unit/queryListOfAuthorSelf',
                }
                let names = {
                    'concept&semantic': 'concepts',
                    'spatialReference': 'spatials',
                    'dataTemplate': 'templates',
                    'unit&metric': 'units',
                }
                let url=urls[a];
                let name=names[a];
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
                    // data: {
                    //     searchText: this.searchText,
                    //     page: targetPage - 1,
                    //     pagesize: this.pageSize,
                    //     sortType: this.sortType,
                    //     asc: this.sortAsc
                    // },
                    cache: false,
                    async: true,
                    // dataType: "json",
                    data: JSON.stringify(reqData),
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
                            this.totalNum = data.total;
                            this.searchCount = Number.parseInt(data["total"]);
                            this.$set(this, "searchResult", data["list"]);

                            if (targetPage == 1) {
                                this.pageInit();
                            }
                            this.await = false
                        }

                    }
                })

            },

            editItem(index,oid){
                let a=this.$route.params.communityKind
                var urls={
                     'concept&semantic':'/user/userSpace#/community/manageConcept/'+oid,
                     'spatialReference':'/user/userSpace#/community/manageSpatialReference/'+oid,
                     'dataTemplate':    '/user/userSpace#/community/manageTemplate/'+oid,
                     'unit&metric':     '/user/userSpace#/community/manageUnit/'+oid,
                }
                this.setSession('editOid', oid)
                window.location.href=urls[a]
            },

            deleteItem(index,oid) {
                let a=this.$route.params.communityKind
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
                            instance.confirmButtonText = this.htmlJson.Deleting;
                            setTimeout(() => {
                                var urls = {
                                    'concept&semantic':"/concept/",
                                    'spatialReference':"/spatialReference/",
                                    'dataTemplate':    "/template/",
                                    'unit&metric':     "/unit/",
                                };


                                $.ajax({
                                    type: "DELETE",
                                    url: urls[a]+oid,
                                    // data: {
                                    //     oid: oid
                                    // },
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
                                                // this.$alert("delete successfully!")
                                            } else if (json.code == -2) {
                                                this.$alert(this.htmlJSon.DeleteFailed)
                                            } else
                                                this.$alert(this.htmlJson.RefreshPage)
                                        }
                                        if (this.searchText.trim() != "") {
                                            this.searchCommunity();
                                        } else {
                                            this.getCommunity(index);
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
                    this.$message({
                        type: 'success',
                        message: this.htmlJson.DeleteSuccess
                    });
                });
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
                            let data = result;
                            this.userId = data.data.accessId;
                            this.userEmail=data.data.email
                            this.userName = data.data.name;
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

                            this.getCommunity();

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