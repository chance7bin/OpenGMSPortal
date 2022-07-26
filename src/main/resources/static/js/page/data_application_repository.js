var data_items = new Vue({
    el:"#data_applications",
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
                sortField:"viewCount",
                categoryName:'6117767e61ce444130b1a276',
                searchText:'',
                curQueryField:'',
            },
            list:new Array(),
            categoryName:'6117767e61ce444130b1a276',
            users:[],
            classlist:[],
            datacount: '',
            // classclick:false,
            // activeNames:["1"],
            // activeNames1:["11"],
            // activeNames2:["1"],
            // activeName:"first",
            // category:[],
            // ca:'All',
            // hubnbm:'',
            // tObj:new Object(),
            // categoryTree:[],
            loading:false,
            useroid:'',
            // dataType:"hubs",
            // stretch:true,
            // dataApplication: [],
            // // categoryId:"5cb83fd0ea3cba3224b6e24e",
            sortField:"View Count",
            sortOrder:"Desc.",
            asc:false,

            queryFields:["Name","Keyword","Content","Contributor"],
            curQueryField:"Name",

            htmlJSON:{
                "ViewCount": ["View Count", "viewCount"],
                "Name": ["Name","name"],
                "CreateTime": ["Create Time","createTime"],
                "Asc": ["Asc.","Asc."],
                "Desc": ["Desc.","Desc."],
                "queryFields":[[1,"Name","Name"],[2,"Keyword","Keyword"],[3,"Content","Content"],[4,"Contributor","Contributor"]],

            },
            htmlJson:{}
        }
    },
    methods: {
        // 获取缓存
        getStorage(key){
            var localStorage = window.localStorage;
            if (localStorage )
                var v = localStorage.getItem(key);
            if (!v) {
                return;
            }
            if (v.indexOf('obj-') === 0) {
                v = v.slice(4);
                return JSON.parse(v);
            } else if (v.indexOf('str-') === 0) {
                return v.slice(4);
            }
            return v;
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

        transFormCate(categoryName){
            let lang = window.localStorage.getItem("language");

            if (lang === "zh-cn"){
                if (categoryName === '6117767e61ce444130b1a276')
                    return '格式转换'
                else if (categoryName === '6117767e61ce444130b1a277')
                    return '数据处理'
                else
                    return '可视化'
            }else {
                if (categoryName === '6117767e61ce444130b1a276')
                    return 'Conversion'
                else if (categoryName === '6117767e61ce444130b1a277')
                    return 'Processing'
                else
                    return 'Visualization'
            }

        },
        //显示功能引导框
        showDriver(){
            if(true){
                this.driver = new Driver({
                    "className": "scope-class",
                    "allowClose": false,
                    "opacity" : 0.1,
                    "prevBtnText": this.htmlJson.Previous,
                    "nextBtnText": this.htmlJson.Next,
                    "closeBtnText": this.htmlJson.Close,
                    "doneBtnText": this.htmlJson.Done
                });
                this.stepsConfig = [
                    {
                        "element" : ".categoryList",
                        "popover" : {
                            "title" : this.htmlJson.MethodCategories,
                            "description" : this.htmlJson.QueryProcessByChoosingCollection,
                            "position" : "right-top",
                        }
                    },
                    {
                        "element": ".searcherInputPanel",
                        "popover": {
                            "title": this.htmlJson.Search,
                            "description": this.htmlJson.SearchProcessByModelName,
                            "position": "bottom-right",
                        }
                    },
                    {
                        "element": ".modelPanel",
                        "popover": {
                            "title": this.htmlJson.Overview,
                            "description": this.htmlJson.BrowseProcessOverview,
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#contributeBtn",
                        "popover" : {
                            "title" : this.htmlJson.Contribute,
                            "description" : this.htmlJson.ShareYourProcessOnOpenGMS,
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
            this.ca=''
        },
        //文本检索
        search(){
            // this.searchText=$('#searchBox').val();
            this.loading=true;
            var that=this
            that.progressBar=true
            this.getData()
        },
        //页码点击翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.getData()
        },
        getclasslist(val){
            this.classlist=val;
        },
        changeCateColor(){
            let eles = $('#classification').find('div')
            for(let i=0;i<eles.length;++i) {
                $(eles[i]).css('background-color','#fff')
            }
        },
        chooseCate(item, event){
            this.changeCateColor()
            $(event.target).css('background-color','#d9edf7')
            this.currentPage = 1
            this.categoryName = item;
            this.datacount=-1
            this.loading=true
            this.progressBar=true;
            this.getData()
        },
        goto(id){
            return getMethodById(id);
        },
        view(id){
            axios.get("/dataMethod/methods/viewplus",{
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
        contribute(){
            if(this.useroid==''){
                alert('Please login first!');
                window.location.href = "/user/login";
            }else{
                window.location.href="/user/userSpace#/data/createDataApplication";
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
            axios.post(getMethodList(),that.findDto)
                .then((res)=>{
                    setTimeout(()=>{
                        that.list=res.data.data.list;
                        that.progressBar=false;
                        that.datacount=res.data.data.total;
                        that.users=res.data.data.users;
                        that.loading=false;
                    },100)
                });
        }
    }
    ,
    mounted(){
        //首先到缓存中获取userSpaceAll
        this.htmlJson = this.getStorage("userSpaceAll");

        let that=this;
        let u=window.location.href
        let f=u.split("/");
        // let index = u.lastIndexOf("\/");
        // that.dataType = u.substring(index+1,u.length);
        this.getData()
        $('#conversion').click()
        axios.get("/user/load")
            .then((res)=>{
                that.userName=res.data.data.name;
                that.useroid=res.data.data.accessId;
            })

        if(document.cookie.indexOf("dataRep=1")==-1){
            this.showDriver();
            var t=new Date(new Date().getTime()+1000*60*60*24*60);
            document.cookie="dataRep=1; expires="+t.toGMTString();
        }
    }
});
