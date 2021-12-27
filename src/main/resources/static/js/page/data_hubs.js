var data_items = new Vue({
    el:"#data_items",
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            activeIndex: '3',
            searchText: '',
            progressBar: true,
            currentPage: 1,
            viewCount:-1,
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            findDto: {
                page: 1,
                pageSize: 10,
                asc: false,
                categoryName:'',
                searchContent:'',
                curQueryField:'',
            },
            list:new Array(),
            users:[],
            classlist:[],
            datacount: '',
            classclick:false,
            activeNames:["1"],
            activeNames1:["11"],
            activeNames2:["1"],
            activeName:"first",
            category:[],
            categoryName:'a24cba2b-9ce1-44de-ac68-8ec36a535d0e',
            hubnbm:'',
            tObj:new Object(),
            categoryTree:[],
            curDefaultCate:'5f3e42070e989714e8364e9a',
            loading:false,
            useroid:'',
            dataType:"hubs",
            stretch:true,
            dataApplication: [],
            // categoryId:"5cb83fd0ea3cba3224b6e24e",
            sortField:"View Count",
            sortOrder:"Desc.",
            asc:false,

            queryFields:["Name","Keyword","Content","Contributor"],
            curQueryField:"Name",
            showCategoryName:'Land regions'

        }
    },
    methods: {

        //显示功能引导框
        showDriver(){
            if(!this.driver){
                this.driver = new Driver({
                    "className": "scope-class",
                    "allowClose": false,
                    "opacity" : 0.1,
                    "prevBtnText": "Previous",
                    "nextBtnText": "Next"
                });
                this.stepsConfig = [
                    {
                        "element" : ".categoryList",
                        "popover" : {
                            "title" : "Data Categories",
                            "description" : "You can query data by choosing a category.",
                            "position" : "right-top",
                        }
                    },
                    {
                        "element": ".searcherInputPanel",
                        "popover": {
                            "title": "Search",
                            "description": "You can also search data by name.",
                            "position": "bottom-right",
                        }
                    },
                    {
                        "element": ".modelPanel",
                        "popover": {
                            "title": "Overview",
                            "description": "Here is query result, you can browse data's overview. Click data name to check detail.",
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#contributeBtn",
                        "popover" : {
                            "title" : "Contribute",
                            "description" : "You can share your data on OpenGMS, and get an OpenGMS unique identifier!",
                            "position" : "bottom",
                        }
                    }
                ];
            }

            if(document.body.clientWidth < 1000){
                this.stepsConfig[1].popover.position = "top";
            }
            this.driver.defineSteps(this.stepsConfig);
            this.driver.start();
        },

        handleChange(){

        },
        handleChange1(){

        },
        startinput(){
            $('.el-collapse-item .el-button').css('color','#2b305b')
        },

        // getData() {
        //
        // },
        //文本检索
        search(){
            this.loading = true;
            this.progressBar = true
            this.getData()
        },
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage
            this.getData()
        },
        getclasslist(val){
            this.classlist=val;
        },
        // 切换类别
        chooseCate(item, event) {
            console.log("here")
            let all_button=$('.cateButton')
            for (let i = 0; i < all_button.length; i++) {
                all_button[i].style.color="";
                all_button[i].style.fontWeight="";
                if(event.currentTarget===all_button[i]){
                    all_button[i].style.color="green";
                    all_button[i].style.fontWeight="bold";
                }
            }
            // $(event.target).style.color="green";
            // $(event.target).style.fontWeight="bold";
            this.currentPage = 1
            this.categoryName = item;
            this.showCategoryName = event.currentTarget.children[0].outerText;
            this.datacount=-1
            this.loading=true
            this.progressBar=true;
            this.getData()
        },
        defaultlist(){
            this.progressBar=true;
            this.loading=true;
            // //todo 默认第一个按钮被选中
            var this_button=$('#'+this.curDefaultCate)
            // e.target.style.color="green";
            this_button[0].style.color="green";
            this_button[0].style.fontWeight="bold";
            var all_button=$('.el-button')
            for (let i = 0; i < all_button.length; i++) {
                if(all_button[i]!=this_button[0]){
                    all_button[i].style.color="";
                    all_button[i].style.fontWeight="";
                }
            }
            this.categoryName=this_button[0].innerText
            this.getData()
        },
        goto(id){
            return getHubById(id)
            // return "/dataItem/hub/"+id;
        },

        view(id){
            axios.get("/dataItem/viewplus",{
                params:{
                    id:id
                }
            })
        },
        //格式化时间
        formatDate(date){
            var dateee=new Date(date).toJSON();
            var da = new Date(+new Date(dateee)+8*3600*1000).toISOString().replace(/T/g,' ').replace(/\.[\d]{3}Z/,'')
            return da
        },
        contribute(event){
            if(this.useroid==''){
                alert("Please login");
                window.location.href = "/user/login";
            }else{
                console.log(event.currentTarget._prevClass.split(" ")[event.currentTarget._prevClass.split(" ").length-1]);
                let type = event.currentTarget._prevClass.split(" ")[event.currentTarget._prevClass.split(" ").length-1];
                if(type === "contributeItemBtn"){
                    window.location.href="/user/userSpace#/data/createDataItem";
                }else {
                    window.location.href="/user/userSpace#/data/createDataHubs";
                }
            }
        },
        changeSortField(ele){
            this.sortField = ele;
            this.getData();
        },
        changeSortOrder(ele){
            this.sortOrder=ele;
            this.asc = (this.sortOrder === 'Asc.');
            this.getData();
        },
        setFindDto(){
            // 填入搜索条件
            this.findDto.curQueryField = this.curQueryField.toLowerCase()
            if(this.searchText.length!=0){
                if(this.findDto.curQueryField != 'contributor')
                    this.findDto.searchText = this.searchText.toLowerCase()
                else
                    this.findDto.searchText = this.searchText
            } else {
                this.findDto.searchText = ''
            }
            //把当前页码给dto
            this.findDto.page=this.currentPage;
            this.findDto.asc = this.asc;
            this.findDto.categoryName = this.categoryName==='all'?'':this.categoryName.toLowerCase()      // all 赋值为空来进行查询
            if(this.sortField === "Create Time"){
                this.findDto.sortField = "createTime"
            } else if(this.sortField === "Name"){
                this.findDto.sortField = "name"
            }else{
                this.findDto.sortField = 'viewCount'
            }
        },
        getData(){
            this.setFindDto()
            let that = this;
            console.log(11)
            axios.post(getHubList(),that.findDto)
                .then(res =>{
                    setTimeout(()=>{
                        that.list=res.data.data.list;
                        that.progressBar=false;
                        that.datacount=res.data.data.total;
                        that.users=res.data.data.users;
                        that.loading=false;
                    },100)
                });
        },
        initButton(){
            let all_button=$('.cateButton');
            all_button[0].style.color="green";
            all_button[0].style.fontWeight="bold";
        }
    },
    mounted(){
        let that=this;
        let u=window.location.href
        let f=u.split("/");
        this.getData()
        this.initButton();
        axios.get("/user/load")
            .then((res)=>{
                that.userName=res.data.name;
                that.useroid=res.data.oid;
            })
        $('.manyhub').css('display','none');
        $('.el-collapse-item .el-button').on('hover',function () {
            $(this).css('color','green')
        })
        $('.el-collapse-item .el-button').mouseout(function () {
            clicked=false;
        });
        $("#searchBox").focus(function(){
            that.startinput()
        })


        if(document.cookie.indexOf("dataRep=1")==-1){
            this.showDriver();
            var t=new Date(new Date().getTime()+1000*60*60*24*60);
            document.cookie="dataRep=1; expires="+t.toGMTString();
        }
    }
});
