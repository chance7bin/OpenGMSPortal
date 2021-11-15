new Vue({
    el: '#manage_system',
    data:{
        tableData: [], //表格中展示的数据
        totalModel: 0, //数据库中模型总数
        currentPage: 1,  //模型列表当前页
        PageSize: 20,  //模型列表每页数量
        input: "",

        model_seen: true,
        user_seen: false,
        active_item:"2",
        searchInput:"", //模型列表搜索框内容
        sortField:"createTime", //模型列表排序字段 默认时间
        ifasc:false, //模型请求列表升降序
        selectedModels:[],
        historyTableVisible:false, //历史记录弹出框
    },

    mounted() {
        //初始加载模型列表数据
        this.getModelList();
    },

    methods: {

        //导航菜单点击切换模型/用户管理页面
        handelModelMenu(){
            this.model_seen=true
            this.user_seen=false
        },
        handelUserMenu(){
            this.model_seen=false
            this.user_seen=true
        },

        //模型列表请求模型数据
        getModelList(){
            axios.post('/managementSystem/deployedModel', {
                "asc": this.ifasc,
                "page": this.currentPage,
                "pageSize": this.PageSize,
                "searchText": this.searchInput,
                "sortField": this.sortField
            })
                .then(response=> {
                    console.log(response);
                    let data=response.data.data
                    this.totalModel=data.total
                    this.tableData=data.content
                })
                .catch(function (error) {
                    console.log(error);
                });

        },

        //分页
        handleSizeChange(val) {
            this.PageSize = val;
            this.getModelList();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getModelList();
        },

        //监测表格选择，实时更新所选行
        handleSelectionChange(val) {
            this.selectedModels = val;
        },

        //检测所选模型（多选）
        checkSelectedModels() {
            console.log(this.selectedModels)
            let selectedModelsId = this.selectedModels.map(item => {
                this.checkModel(item)
            })
        },

        //检测模型（单个）
        checkModel(modelItem){
            axios.get('/managementSystem/model/invoke/'+modelItem.id)
                .then( response=> {
                    console.log(response);
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        UpdateStatus(){
            axios.post('/managementSystem/update/modelStatus', {
                "asc": this.ifasc,
                "page": this.currentPage,
                "pageSize": this.PageSize,
                "sortType": "runtime",
                "status": "all"
            })
                .then(response=> {
                    console.log(response);
                    // let data=response.data.data
                    // this.totalModel=data.total
                    // this.tableData=data.content
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        testGetApi(){
            axios.get('/managementSystem/test')
                //这里使用用箭头函数，使得this指向不是windows，而是外层的vue
                .then( (response)=> {
                    console.log(response);
                    this.todos.push(response.data.data)
                })
                .catch(function (error) {
                    console.log(error);
                });
        }

    },
})