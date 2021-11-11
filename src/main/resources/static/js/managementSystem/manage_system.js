new Vue({
    el: '#manage_system',
    data:{
        tableData: [], //表格中展示的数据（搜索后）
        totalData:[], //总模型数据
        totalNum: 0,
        currentPage: 1,
        PageSize: 20,
        input: "",
        todos: [{
            name: "abc",
            age: "12"
        }, {
            name: "def",
            age: "13"
        }],
        model_seen: true,
        user_seen: false,
        active_item:"2",
        searchInput:"",
        selectedModels:[]
    },

    mounted() {

        //生成假数据
        for (let i = 0, len = 120; i < len; i++) {
            let dataItem = {
                tempID: i,
                date: new Date().toLocaleString(),
                name: i.toString() + "xxx",
                count: Math.floor(Math.random() * 1000),
                status: Math.floor(Math.random()* 10)>5? "未知" : "正常",
                mmmid:"xxxxxxxxx"
            };
            this.totalData.push(dataItem);
            this.tableData=this.totalData;
        }
        this.totalNum=this.tableData.length


        axios.get('/management/test')
            //这里使用用箭头函数，使得this指向不是windows，而是外层的vue
            .then( (response)=> {
                console.log(response);
                this.todos.push(response.data.data)
            })
            .catch(function (error) {
                console.log(error);
            });

    },
    methods: {

        //分页
        handleSizeChange(val) {
            this.PageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },

        //导航菜单点击事件
        handelModelMenu(){
            this.model_seen=true
            this.user_seen=false
        },
        handelUserMenu(){
            this.model_seen=false
            this.user_seen=true
        },

        //模型搜索
        searchModel() {
            this.tableData = this.totalData.filter((item) => {
                return !this.searchInput || item.name.toLowerCase().includes(this.searchInput.toLowerCase())
            })
            this.totalNum = this.tableData.length
        },

        //监测表格选择，实时更新所选行
        handleSelectionChange(val) {
            this.selectedModels = val;
        },
        //检测所选模型（多选）
        checkSelectedModels() {
            console.log(this.selectedModels)
            let selectedModelsId = this.selectedModels.map(item => {
                return item.mmmid
            })
            console.log(selectedModelsId)
            axios.post('/checkModels', {checkModelIds:selectedModelsId})
                .then( response=> {
                    console.log(response.data.data);

                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //检测模型（单个）
        checkModel(index,item){
            console.log(index,item)
            axios.post('/checkModel', {checkModelId:item.mmmid})
                .then( response=> {
                    console.log(response.data.data);
                })
                .catch(function (error) {
                    console.log(error);
                });
        }

    },
})