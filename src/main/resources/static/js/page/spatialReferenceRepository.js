new Vue({
    el: '#app',
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            categoryName:"da70ad83-de57-4fc3-a85d-c1dcf4961433",
            activeIndex:'8-2',
            queryType: 'normal',
            searchText: '',
            classifications1: ["da70ad83-de57-4fc3-a85d-c1dcf4961433"],
            classifications2: ["da70ad83-de57-4fc3-a85d-c1dcf4961433"],

            currentClass: "Basic",

            pageOption: {
                paginationShow:false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 10,
                sortType: "default",
                total: 0,
                searchResult: [],
            },

            treeData: [{
                id: 1,
                nameCn: "空间参考",
                label: "Spatial Reference Repository",
                oid: '58340c92-d74f-4d81-8a80-e4fcff286008',
                children: [
                    {
                        id: 100,
                        nameCn: "基础",
                        "oid": "da70ad83-de57-4fc3-a85d-c1dcf4961433",
                        "label": "Basic"
                    },
                    {
                        id: 101,
                        nameCn: "EPSG",
                        "oid": "c4642926-e797-4f61-92d6-7933df2413d2",
                        "label": "EPSG"
                    },
                    {
                        id: 102,
                        nameCn: "ESRI",
                        "oid": "e8562394-b55f-46d7-870e-ef5ad3aaf110",
                        "label": "ESRI"
                    },
                    {
                        id: 103,
                        nameCn: "IAU",
                        "oid": "ee830613-1603-4f38-a196-5028e4e10d39",
                        "label": "IAU"
                    },
                    {
                        id: 104,
                        nameCn: "自定义",
                        "oid": "b2f2fbfd-f21a-47ac-9e1f-a96ac0218bf1",
                        "label": "Customized"
                    }]
            }, {
                id: 2,
                nameCn: "时间参考",
                label: "Temporal Reference Repository",
                oid: 'ce37e343-bf2c-4e7b-902e-46616604e184',
                children: [{
                        id: 3,
                        nameCn: "日期",
                        label: "Date",
                        oid: '295d2120-402b-4ee6-a0b5-308b67fe2c40',
                    },
                    {
                        id: 4,
                        nameCn: "时间",
                        label: "Time",
                        oid: '6883d3fb-8485-4771-9a3e-3276c759364e',
                    }]
            }],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            sortTypeName:"View Count",
            sortFieldName:"viewCount",
            sortOrder:"Desc.",

            htmlJSON:{
                queryFields:[[1,"Name","Name"],[2,"Keyword","Keyword"],[3,"Content","Content"],[4,"Contributor","Contributor"]],
                ViewCount: ["View Count", "viewCount"],
                Name: ["Name","name"],
                CreateTime: ["Create Time","createTime"],
                Asc: ["Asc.","Asc."],
                Desc: ["Desc.","Desc."],
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

            let treeData = this.treeData;


            //切换列表中标签选择情况
            if(this.htmlJSON.Name[0]!=jsonContent.Name[0]) {


                for(i = 0;i<treeData.length;i++){
                    let treeData1 = treeData[i];
                    let temp = treeData1.label;
                    treeData1.label = treeData1.nameCn;
                    treeData1.nameCn = temp;
                    for(j = 0;j<treeData1.children.length;j++){
                        let treeData2 = treeData1.children[j];
                        temp = treeData2.label;
                        treeData2.label = treeData2.nameCn;
                        treeData2.nameCn = temp;
                    }
                }
                this.treeData = treeData;

                if(this.sortOrder == this.htmlJSON.Asc[0]){
                    this.sortOrder = jsonContent.Asc[0];
                }else if(this.sortOrder == this.htmlJSON.Desc[0]){
                    this.sortOrder = jsonContent.Desc[0];
                }

                if(this.sortTypeName == this.htmlJSON.ViewCount[0]){
                    this.sortTypeName = jsonContent.ViewCount[0];
                }else if(this.sortTypeName == this.htmlJSON.Name[0]){
                    this.sortTypeName = jsonContent.Name[0];
                }else if(this.sortTypeName == this.htmlJSON.CreateTime[0]){
                    this.sortTypeName = jsonContent.CreateTime[0];
                }
            }
            this.htmlJSON = jsonContent


            //当前类别中英文切换
            let language = window.localStorage.getItem("language")
            let flag = 0;
            for(i = 0;i<treeData.length;i++){
                let treeData1 = treeData[i];
                if (treeData1.oid == this.categoryName){
                    this.currentClass = treeData1.label
                    break;
                }
                for(j = 0;j<treeData1.children.length;j++){
                    let treeData2 = treeData1.children[j];
                    if (treeData2.oid == this.categoryName){
                        this.currentClass = treeData2.label
                        flag = 1;
                        break;
                    }

                }
                if (flag == 1)
                    break;
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
                            "title" : this.htmlJson.RepositoryCollections,
                            "description" : this.htmlJson.QuerySpatialByChoosingCollection,
                            "position" : "right-top",
                        }
                    },
                    {
                        "element": ".searcherInputPanel",
                        "popover": {
                            "title": this.htmlJson.Search,
                            "description": this.htmlJson.SearchSpatialByModelName,
                            "position": "bottom-right",
                        }
                    },
                    {
                        "element": ".modelPanel",
                        "popover": {
                            "title": this.htmlJson.Overview,
                            "description": this.htmlJson.BrowseSpatialOverview,
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#contributeBtn",
                        "popover" : {
                            "title" : this.htmlJson.Contribute,
                            "description" : this.htmlJson.ShareYourSpatialOnOpenGMS,
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
            this.sortTypeName = label;
            this.sortFieldName = ele;
            this.getModels(this.classType);
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
            this.pageOption.sortAsc = ele==="Asc.";
            this.getModels(this.classType);
        },
        contribute(){
            $.ajax({
                url: '/user/load',
                type: 'get',
                // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                // dataType : 'json',
                success: function (result) {
                    var json = result;
                    if (json.code === 0) {
                        window.location.href="/user/userSpace#/community/createSpatialReference";
                    }
                    else{
                        window.location.href="/user/login";
                    }
                },
                error: function (e) {
                    alert("load user error");
                }
            });
        },
        search() {
            this.pageOption.currentPage = 1;
            this.getModels();
        },
        //页码change
        handlePageChange(val) {

            this.pageOption.currentPage = val;

            window.scrollTo(0, 0);
            this.getModels();
        },
        handleCurrentChange(data, checked, indeterminate) {
            // this.pageOption.searchResult=[];
            this.pageOption.total=0;
            this.pageOption.paginationShow=false;
            this.currentClass=data.label;
            let classes = [];
            classes.push(data.oid);
            this.classifications1 = classes;
            this.getChildren(data.children)

            this.categoryName = data.oid

            this.pageOption.currentPage=1;
            this.searchText="";
            this.getModels();
        },
        getChildren(children) {
            if (children != null) {
                for (let i = 0; i < children.length; i++) {
                    let child = children[i];
                    this.classifications1.push(child.oid);
                    this.getChildren(child.children);
                }
            }
        },
        handleCheckChange(data, checked, indeterminate) {
            // this.pageOption.searchResult=[];
            this.pageOption.paginationShow=false;
            let checkedNodes = this.$refs.tree2.getCheckedNodes()
            let classes = [];
            for (let i = 0; i < checkedNodes.length; i++) {
                classes.push(checkedNodes[i].oid);
            }
            this.classifications2 = classes;
            console.log(this.classifications2)
            this.pageOption.currentPage=1;
            this.getModels();
        },
        getModels() {
            this.pageOption.progressBar = true;
            var data = {
                asc: this.pageOption.sortAsc,
                page: this.pageOption.currentPage,
                pageSize: this.pageOption.pageSize,
                searchText : this.searchText,
                classifications : this.classifications1.length == 0 ? ["all"] : this.classifications1

            };
            this.Query(data, this.queryType);
        },
        Query(data, type) {
            let query={ };
            query.oid=data.classifications[0];
            query.page=data.page;
            query.sortField=this.sortFieldName;
            if(data.asc){
                query.asc= 1;
            }else{
                query.asc = 0;
            }
            query.searchText=data.searchText;

            let url=getSpatialReferenceList()
            // if(query.searchText.trim()==""){
            //     url=getSpatialReferenceList()
            //     query.categoryName = this.categoryName
            // }
            // else{
            //     url="/repository/searchSpatialReference";
            //     this.classifications1=[""];
            //     this.currentClass="ALL";
            //     this.$refs.tree1.setCurrentKey(null);
            // }
            query.categoryName = this.categoryName

            let sendDate = (new Date()).getTime();
            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(query),
                async: true,
                contentType: "application/json",
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        let receiveDate = (new Date()).getTime();
                        let responseTimeMs = receiveDate - sendDate;
                        let timeoutTime=0;
                        console.log(responseTimeMs)
                        if(responseTimeMs<450){
                            timeoutTime=450-responseTimeMs;
                        }
                        setTimeout(() => {
                            this.pageOption.total = data.total;
                            // this.pageOption.pages = data.pages;
                            this.pageOption.searchResult = data.list;
                            this.pageOption.users = data.users;
                            this.pageOption.progressBar = false;
                            this.pageOption.paginationShow=true;
                        }, timeoutTime);
                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },


    },
    mounted() {
        //首先到缓存中获取userSpaceAll
        this.htmlJson = this.getStorage("userSpaceAll");

        this.getModels();

        //expend
        $("#expend").click(() => {
            this.queryType = "advanced";
            $(".searcherPanel").css("display", "none");
            $(".advancedSearch").css("display", "block");
            $("#tree1").css("display", "none");
            $("#tree2").css("display", "block");
            this.getModels();
        })
        $("#drawback").click(() => {
            this.queryType = "normal";
            $(".searcherPanel").css("display", "block");
            $(".advancedSearch").css("display", "none");
            $("#tree2").css("display", "none");
            $("#tree1").css("display", "block");
            this.getModels();
        })

        //field select
        $(document).on("click", ".propName", function () {
            var downArrow = "<span class=\"caret\"></span>";
            $(this).parents(".input-group-btn").children("button").html($(this).text() + downArrow);
        })

        //add
        $(".fa-plus").click(function () {

            var field;
            var lineCount = $(".lines").children(".line").length;
            switch (lineCount) {
                case 1:
                    field = "Keyword";
                    break;
                case 2:
                    field = "Overview";
                    break;
                case 3:
                    field = "Description";
                    break;
                case 4:
                    field = "Provider";
                    break;
                case 5:
                    field = "Reference";
                    break;
            }

            var line = "<div class=\"line\">\n" +
                "                                <div class=\"input-group col-md-1 pull-left\">\n" +
                "                                    <select class=\"form-control connect\">\n" +
                "                                        <option>AND</option>\n" +
                "                                        <option>OR</option>\n" +
                "                                        <option>NOT</option>\n" +
                "                                    </select>\n" +
                "                                </div>\n" +
                "                                <div class=\"input-group col-md-5 pull-left\">\n" +
                "                                    <div class=\"input-group-btn\">\n" +
                "                                        <button type=\"button\" class=\"btn btn-default dropdown-toggle prop\"\n" +
                "                                                data-toggle=\"dropdown\">" + field + "<span class=\"caret\"></span></button>\n" +
                "                                        <ul class=\"dropdown-menu\">\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Model Name</a></li>\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Keyword</a></li>\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Overview</a></li>\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Description</a></li>\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Provider</a></li>\n" +
                "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Reference</a></li>\n" +
                "                                        </ul>\n" +
                "                                    </div>\n" +
                "                                    <input type=\"text\" class=\"form-control value\">\n" +
                "                                </div>\n" +
                "                                <div class=\"input-group col-md-1 pull-left\">\n" +
                "                                    <select class=\"form-control connect\">\n" +
                "                                        <option>AND</option>\n" +
                "                                        <option>OR</option>\n" +
                "                                        <option>NOT</option>\n" +
                "                                    </select>\n" +
                "                                </div>\n" +
                "                                <div class=\"input-group col-md-5 pull-left\">\n" +
                // "                                    <div class=\"input-group-btn\">\n" +
                // "                                        <button type=\"button\" class=\"btn btn-default dropdown-toggle prop\"\n" +
                // "                                                data-toggle=\"dropdown\">\n" +
                // "                                            "+field+"<span class=\"caret\"></span></button>\n" +
                // "                                        <ul class=\"dropdown-menu\">\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Model Name</a></li>\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Keyword</a></li>\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Overview</a></li>\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Description</a></li>\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Provider</a></li>\n" +
                // "                                            <li><a class=\"propName\" href=\"javascript:void(0);\">Reference</a></li>\n" +
                // "                                        </ul>\n" +
                // "                                    </div>\n" +
                "                                    <input type=\"text\" class=\"form-control value\">\n" +
                "                                </div>\n" +
                "                            </div>";

            if (lineCount <= 5) {
                $(".lines").append(line)
            }
        })
        //delete
        $(".fa-minus").click(function () {
            var lines = $(".lines").children(".line");
            if (lines.length > 1) {
                lines.eq(lines.length - 1).remove();
            }
        })

        if(document.cookie.indexOf("communityRep=1")==-1){
            this.showDriver();
            var t=new Date(new Date().getTime()+1000*60*60*24*60);
            document.cookie="communityRep=1; expires="+t.toGMTString();
        }
    }
})