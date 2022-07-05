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
                curQueryField:'',
                sortField: 'viewCount',
            },
            categoryName:'a24cba2b-9ce1-44de-ac68-8ec36a535d0e',
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
            ca:'',
            hubnbm:'',
            tObj:new Object(),
            categoryTree:[],
            curDefaultCate:'5f3e42070e989714e8364e9a',
            loading:false,
            useroid:'',
            dataType:"hubs",
            stretch:true,
            dataApplication: [],
            sortField: "View Count",
            sortOrder:"Desc.",
            asc:false,

            queryFields:["Name","Keyword","Content","Contributor"],
            curQueryField:"Name",
            showCategoryName:'Land regions',

            htmlJSON:{
                "ViewCount": ["View Count", "viewCount"],
                "Name": ["Name","name"],
                "CreateTime": ["Create Time","createTime"],
                "Asc": ["Asc.","Asc."],
                "Desc": ["Desc.","Desc."],
                "queryFields":[[1,"Name","Name"],[2,"Keyword","Keyword"],[3,"Content","Content"],[4,"Contributor","Contributor"]],

            }
        }
    },
    methods: {
        categoryInit(){
        // 获取url截取languages
            location_href = window.location.href
            lang = location_href.split("=")[1]

            if (lang == "zh-cn"){
                this.showCategoryName = "陆地圈"
            }else {
                this.showCategoryName = "Land Regions"
            }
        },

        translatePage(jsonContent){

            //切换列表中标签选择情况
            if(this.htmlJSON.Name[0]!=jsonContent.Name[0]) {

                if(this.sortOrder == this.htmlJSON.Asc[0]){
                    this.sortOrder = jsonContent.Asc[0];
                }else if(this.sortOrder == this.htmlJSON.Desc[0]){
                    this.sortOrder = jsonContent.Desc[0];
                }

                if(this.sortField == this.htmlJSON.ViewCount[0]){
                    this.sortField = jsonContent.ViewCount[0];
                }else if(this.sortField == this.htmlJSON.Name[0]){
                    this.sortField = jsonContent.Name[0];
                }else if(this.sortField == this.htmlJSON.CreateTime[0]){
                    this.sortField = jsonContent.CreateTime[0];
                }
            }

            this.htmlJSON = jsonContent
        },

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
        handleChange2(){

        },
        handleClick(tab, event) {
            console.log(tab, event);
        },
        startinput(){
            $('.el-collapse-item .el-button').css('color','#2b305b')
        },
        //文本检索
        search(){
            this.loading = true;
            this.progressBar = true
            this.getData()
        },
        //页码点击翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage
            this.getData()
        },
        getclasslist(val){
            this.classlist=val;
        },
        // 切换类别
        chooseCate(item, event) {
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
            return getItemById(id)
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
            let label = "";
            switch (ele){
                case this.htmlJSON.ViewCount[1]:
                    label = this.htmlJSON.ViewCount[0];
                    break;
                case this.htmlJSON.Name[1]:
                    label = this.htmlJSON.Name[0];
                    break;
                case this.htmlJSON.CreateTime[1]:
                    label = this.htmlJSON.CreateTime[0];
                    break;
            }
            this.sortField = label;
            this.getData();
        },
        changeSortOrder(ele){
            let label = "";
            switch (ele){
                case this.htmlJSON.Asc[1]:
                    label = this.htmlJSON.Asc[0];
                    break;
                case this.htmlJSON.Desc[1]:
                    label = this.htmlJSON.Desc[0];
                    break;
            }
            this.sortOrder=label;
            this.asc = (ele === 'Asc.');
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
            if(this.sortField === this.htmlJSON.CreateTime[0]){
                this.findDto.sortField = "createTime"
            } else if(this.sortField === this.htmlJSON.Name[0]){
                this.findDto.sortField = "name"
            }else{
                this.findDto.sortField = 'viewCount'
            }
        },
        getData(){
            this.setFindDto()
            let that = this;
            axios.post(getItemList(), that.findDto)
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
    }
    ,
    mounted(){
        this.categoryInit();
        let that=this;
        let u=window.location.href
        let f=u.split("/");
        this.getData()
        this.initButton();
        axios.get("/user/load")
            .then((res)=>{
                that.userName=res.data.data.name;
                that.useroid=res.data.data.accessId;
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
