new Vue({
    el: '#manage_system',
    data:{

        pageShow:1,//控制页面切换

        //模型管理界面==============================================
        tableData: [], //表格中展示的数据
        totalModel: 0, //数据库中模型总数
        currentPage: 1,  //模型列表当前页
        PageSize: 10,  //模型列表每页数量
        searchInput:"", //模型列表搜索框内容
        sortField:"createTime", //模型列表排序字段 默认时间
        ifAsc:false, //模型请求列表升降序
        historyTableVisible:false, //历史记录弹出框
        historyList:[], //历史记录数据
        historyTotal:0, //历史记录总数
        historyNowPage:1, //历史记录当前页
        activeHistoryItem:-1, //当前展开的条目
        waitCheckModels:[], //选中的待检测模型


        //用户管理界面===============================================
        userTableData:[], //用户列表数据
        currentPageUser:1, //用户列表表格页
        PageSizeUser:10, //用户列表每页数目
        totalUser:0, //用户总数
        searchInputUser:"", //用户表搜索内容

        //条目管理界面===============================================
        geoItemMenuData:[{
            label: 'Model',
        }, {
            label: 'Data',
            children: [{
                label: 'Hub Repository'
            }, {
                label: 'Item Repository',
            }]
        }, {
            label: 'Community',
            children: [{
                label: 'Concept & Semantic',
            }, {
                label: 'Spatiotemporal Reference',
            },{
                label: 'Data Template',
            },{
                label: 'Unit & Metric',
            },]
        }], //条目页左侧菜单
        defaultPropsGeoItem: {
            children: 'children',
            label: 'label'
        },  //条目页菜单属性
        geoItemTableData:[], //条目列表数据
        currentPageGeo:1, //条目列表表格页
        PageSizeGeo:10, //条目列表每页数目
        totalGeo:0, //条目总数
        searchInputGeo:"", //条目表搜索内容
        itemType:"DataHub", //选择展示的条目类型

        //条目管理界面===============================================

        //未处理版本审核
        searchInputWaitVersion:"", //版本搜索
        waitVersionTableData:[],// 表格数据
        currentPageWaitVersion:1, //当前页
        PageSizeWaitVersion:10, //当前页大小
        totalWaitVersion:0, //总数
        dialogVersionComp:false, //版本对比对话框显示控制
        versionCompData:{}, //版本对比数据
        versionCompChangedField:{}, //版本对比数据中修改项


        //接受版本审核
        searchInputAcceptVersion:"", //版本搜索
        acceptVersionTableData:[],// 表格数据
        currentPageAcceptVersion:1, //当前页
        PageSizeAcceptVersion:10, //当前页大小
        totalAcceptVersion:0, //总数

        //接受版本审核
        searchInputRejectVersion:"", //版本搜索
        rejectVersionTableData:[],// 表格数据
        currentPageRejectVersion:1, //当前页
        PageSizeRejectVersion:10, //当前页大小
        totalRejectVersion:0, //总数

    },

    mounted() {
        //初始加载模型列表数据
        this.getModelList();
    },

    methods: {
        //页面切换
        pageChange(pageId){
            this.pageShow=pageId
            if(pageId===1){
                this.getModelList();
            }else if(pageId===2){
                this.getUserList();
            }else if(pageId===3){
                this.getGeoItemList();
            }else{
                this.getWaitVersionList();
            }
        },

        //模型管理界面================================================
        //模型列表请求模型条目数据
        getModelList(){
            axios.post('/managementSystem/deployedModel', {
                "asc": this.ifAsc,
                "page": this.currentPage,
                "pageSize": this.PageSize,
                "searchText": this.searchInput,
                "sortField": this.sortField
            })
                .then(response=> {
                    let data=response.data.data
                    this.totalModel=data.total
                    this.tableData=data.content
                    for (let i=0,len=this.waitCheckModels.length;i<len;i++){
                        this.updateCheck(this.waitCheckModels[i].modelId)
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //模型列表分页
        handleSizeChange(val) {
            this.PageSize = val;
            this.getModelList();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getModelList();

        },

        //模型搜索
        searchModel(){
            this.getModelList();
        },

        //响应模型列表选择(单选)
        handelCheckbox(selection, row){
            let tempModelItem={
                modelName:row.accessId,
                modelId:row.id
            }

            let findIndex=-1
            for(let i = 0,len=this.waitCheckModels.length; i < len; i++) {
                if(this.waitCheckModels[i].modelId===tempModelItem.modelId){
                    findIndex=i
                    break
                }
            }

            if(findIndex!==-1){
                this.waitCheckModels.splice(findIndex,1)
                this.$message({
                    showClose: true,
                    message: '从检测队列移除模型: '+tempModelItem.modelName,
                    type: 'warning',
                    duration: 2000,
                });
            }else{
                this.waitCheckModels.push(tempModelItem)
                this.$message({
                    showClose: true,
                    message: '成功添加模型: '+tempModelItem.modelName+ " 至待检测队列",
                    type: 'success',
                    duration: 2000,
                });
            }
        },

        //响应模型列表选择(全选)
        handelAllCheck(selection){
            if(selection.length!==0){  //全选中时
                for (let i=0,len=selection.length;i<len;i++){
                    let tempModelItem={
                        modelName:selection[i].accessId,
                        modelId:selection[i].id
                    }
                    let findIndex=-1
                        for(let j = 0,len1=this.waitCheckModels.length; j < len1; j++) {
                            if(this.waitCheckModels[j].modelId===tempModelItem.modelId){
                                findIndex=j
                                break
                            }
                        }

                    if(findIndex===-1){
                        this.waitCheckModels.push(tempModelItem)
                    }
                }
            }else { //全选取消
                for (let i=0,len=this.tableData.length;i<len;i++){
                    let findIndex=-1
                    for(let j = 0,len1=this.waitCheckModels.length; j < len1; j++) {
                        if(this.waitCheckModels[j].modelId===this.tableData[i].id){
                            findIndex=j
                            break
                        }
                    }
                    if(findIndex!==-1){
                        this.waitCheckModels.splice(findIndex,1)
                    }
                }
            }

        },

        //从待检测模型列表删除一项
        deleteCheckModel(modelId){
            for (let i=0,len=this.waitCheckModels.length;i<len;i++){
                if(this.waitCheckModels[i].modelId===modelId){
                    this.waitCheckModels.splice(i,1)
                    break
                }
            }
            this.updateCheck(modelId)
        },

        //输入模型id，更新模型table表中的选择状态
        updateCheck(modelId){
            this.$nextTick(function () {    //解决更换数据后this.$refs.modelTable.toggleRowSelection无效问题
                for (let i=0,len=this.tableData.length;i<len;i++){
                    if(this.tableData[i].id===modelId){
                        this.$refs.modelTable.toggleRowSelection(this.tableData[i]);
                        break
                    }
                }
            })
        },

        //检测所选模型（多选）(循环调用单次检测方法)
        checkSelectedModels() {
            this.$confirm('是否开始检测所选模型?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '开始检测!'
                });
                let selectedModelIds=this.waitCheckModels.map(item=>{
                    this.checkModel(item.modelId)
                    return item.modelId
                })
                axios.post('/managementSystem/checkList/save', {
                    "draftName": new Date().toLocaleString(),
                    "modelList": selectedModelIds,
                })
                    .then(response=> {
                        // console.log(response);
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
                this.waitCheckModels=[]
                this.$refs.modelTable.clearSelection();

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消检测！'
                });
            });
        },

        //检测模型（单个）
        checkModel(modelId){
            axios.get('/managementSystem/model/invoke/'+modelId)
                .then( response=> {
                    // console.log(response);
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //获取历史检测记录
        getHistoryList(){
            this.historyTableVisible = true
            axios.post('/managementSystem/checkList/search', {
                "asc": false,
                "page": this.historyNowPage,
                "pageSize": 5,
                "searchText": "",
                "sortField": "createTime"
            })
                .then(response=> {
                    let returnData=response.data.data
                    this.historyList=returnData.content
                    this.historyTotal=returnData.totalElements

                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //从历史记录加载所选模型
        loadCheckedModel(CheckedList){
            for (let k=0,len0=CheckedList.length;k<len0;k++){
                for (let i=0,len1=this.waitCheckModels.length;i<=len1;i++){
                    if(len1===0||i===len1){
                        let tempItem={
                            modelName:CheckedList[k].name,
                            modelId:CheckedList[k].id
                        }
                        this.waitCheckModels.push(tempItem)
                        this.updateCheck(CheckedList[k].id)
                        break
                    }
                    if(this.waitCheckModels[i].modelId===CheckedList[k].id){
                        break
                    }
                }
            }
        },

        //删除检测历史记录
        deleteHistoryItem(historyId){
            axios.delete('/managementSystem/checkList/delete/'+historyId)
                .then(response=> {
                    this.getHistoryList()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //历史记录切换页面
        handleHistoryCurrentChange(val) {
            this.historyNowPage = val;
            this.getHistoryList();
        },


        //用户管理界面================================================
        //获取用户列表信息
        getUserList(){
            axios.post('/managementSystem/user/info', {
                "asc": false,
                "page": this.currentPageUser,
                "pageSize": this.PageSizeUser,
                "searchText": this.searchInputUser,
                "sortField": "createTime"
            })
                .then(response=> {
                    this.userTableData=response.data.data.content
                    this.totalUser=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //页面切换
        handleSizeChangeUser(val){
            this.PageSizeUser=val
            this.getUserList()
        },
        handleCurrentChangeUser(val){
            this.currentPageUser=val
            this.getUserList()
        },


        //条目管理界面================================================
        //条目管理菜单
        handleNodeClickGeoItem(data){
            let treeNodeId=data.$treeNodeId
            console.log(treeNodeId)
            if(treeNodeId===1){
                this.itemType="ModelItem"
            }else if(treeNodeId===3){
                this.itemType="DataHub"
            }else if(treeNodeId===4){
                this.itemType="DataItem"
            }else if(treeNodeId===6){
                this.itemType="Concept"
            }else if(treeNodeId===7){
                this.itemType="SpatialReference"
            }else if(treeNodeId===8){
                this.itemType="Template"
            }else if(treeNodeId===9){
                this.itemType="Unit"
            }
            this.getGeoItemList()
        },

        //获取条目列表
        getGeoItemList(){
            axios.post('/managementSystem/item/info/'+this.itemType, {
                "asc": false,
                "page": this.currentPageGeo,
                "pageSize": this.PageSizeGeo,
                "searchText": this.searchInputGeo,
                "sortField": "viewCount",
                "categoryName": "", //此处不用
                "curQueryField": "name",
            })
                .then(response=> {
                    this.geoItemTableData=response.data.data.itemList
                    this.totalGeo=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //页面切换
        handleSizeChangeGeo(val){
            this.PageSizeGeo=val
            this.getGeoItemList()
        },
        handleCurrentChangeGeo(val){
            this.currentPageGeo=val
            this.getGeoItemList()
        },

        //改变条目状态
        changeStatus(val){
            console.log(val)
            let status=val.status
            if(status==="Public"){
                status="Private"
            }else{
                status="Public"
            }
            axios.post('/managementSystem/item/status/'+this.itemType+val.id+status, {
            })
                .then(response=> {
                    this.getGeoItemList()
                })
                .catch(function (error) {
                    console.log(error);
                });

        },
        setAdmin(val){
            console.log(val)
        },


        //版本审核管理界面================================================

        changeTabs(tab){
            if(tab.index==="0"){
                this.getWaitVersionList()
            }else if(tab.index==="1"){
                this.getAcceptVersionList()
            }else{
                this.getRejectVersionList()
            }
        },

        //未处理部分
        //查询未处理版本
        getWaitVersionList(){
            axios.post('/version/versionList/uncheck', {
                "asc": false,
                "page": this.currentPageWaitVersion,
                "pageSize": this.PageSizeWaitVersion,
                "searchText": this.searchInputWaitVersion,
                "sortField": "createTime",
            })
                .then(response=> {
                    console.log(response)
                    this.waitVersionTableData=response.data.data.content
                    this.totalWaitVersion=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //查看某次审核，对比
        viewWaitVersion(versionId){
            this.dialogVersionComp=true
            axios.get("/version/detail/"+versionId)
                .then(response=> {
                    this.versionCompData=response.data.data
                    this.versionCompChangedField=this.versionCompData.changedField
                    console.log(this.versionCompChangedField);
                })
                .catch(function (error) {
                    console.log(error);
                });
            console.log(versionId)

        },
        acceptVersion(){

        },
        rejectVersion(){

        },

        handleSizeChangeWaitVersion(){

        },
        handleCurrentChangeWaitVersion(){

        },

        //接受部分
        //查询接受版本
        getAcceptVersionList(){
            axios.post('/version/versionList/accepted', {
                "asc": false,
                "page": this.currentPageAcceptVersion,
                "pageSize": this.PageSizeAcceptVersion,
                "searchText": this.searchInputAcceptVersion,
                "sortField": "createTime",
            })
                .then(response=> {
                    this.acceptVersionTableData=response.data.data.content
                    this.totalAcceptVersion=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        viewAcceptVersion(){

        },

        handleSizeChangeAcceptVersion(){

        },
        handleCurrentChangeAcceptVersion(){

        },

        //拒绝部分
        //查询拒绝版本
        getRejectVersionList(){
            axios.post('/version/versionList/rejected', {
                "asc": false,
                "page": this.currentPageRejectVersion,
                "pageSize": this.PageSizeRejectVersion,
                "searchText": this.searchInputRejectVersion,
                "sortField": "createTime",
            })
                .then(response=> {
                    console.log(response)
                    this.rejectVersionTableData=response.data.data.content
                    this.totalRejectVersion=response.data.data.totalElements
                    console.log(this.rejectVersionTableData)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


        viewRejectVersion(){

        },

        handleSizeChangeRejectVersion(){

        },
        handleCurrentChangeRejectVersion(){

        },





    },
})