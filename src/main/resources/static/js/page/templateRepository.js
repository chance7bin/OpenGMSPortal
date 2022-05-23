new Vue({
    el: '#app',
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            categoryName:"f7fbecf6-9d28-405e-b7d2-07ef9d924ca6",
            activeIndex:'8-3',
            queryType: 'normal',
            searchText: '',
            classifications1: ["f7fbecf6-9d28-405e-b7d2-07ef9d924ca6"],
            classifications2: ["f7fbecf6-9d28-405e-b7d2-07ef9d924ca6"],

            currentClass: "Vector Data Format",

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
                nameCn: "数据描述模板",
                label: 'Description Templates',
                oid: 'TRJJMYDAUJTDDU5J9GPRUWAG7QJ6PHUU',
                children: [
                    {
                        id: 100,
                        nameCn: "矢量数据格式",
                        "oid": "f7fbecf6-9d28-405e-b7d2-07ef9d924ca6",
                        "label": "Vector Data Format"
                    },
                    {
                        nameCn: "栅格数据格式",
                        "oid": "9b104fd6-7949-4c3b-b277-138cd979d053",
                        "label": "Raster Data Format",
                    },
                    {
                        nameCn: "网格数据格式",
                        "oid": "316d4df0-436e-4600-a183-80abf7472a72",
                        "label": "Mesh Data Format",
                    },
                    {
                        nameCn: "图片数据格式",
                        "oid": "bc437c65-2cfe-4bde-ac31-04830f18885a",
                        "label": "Image Data Format",
                    },
                    {
                        nameCn: "视频数据格式",
                        "oid": "39c0824e-8b1a-44e5-8716-c7893afe05e8",
                        "label": "Video Data Format",
                    },
                    {
                        nameCn: "音频数据格式",
                        "oid": "82b1c2b4-4c12-441d-9d9c-09365c3c8a24",
                        "label": "Audio Data Format",
                    },
                    {
                        nameCn: "非结构化数据格式",
                        "oid": "df6d36e3-8f16-4b96-8d3f-cff24f7c0fd9",
                        "label": "Unstructural Data Format",
                    },
                    {
                        nameCn: "模型相关数据格式",
                        "oid": "26bb993b-453c-481a-a1ea-674db3e888e2",
                        "label": "Model Related Data Format",
                    },
                    {
                        nameCn: "三维模型数据格式",
                        "oid": "1d573467-f1f3-440a-a827-110ac1e820bd",
                        "label": "3D Model Data Format",
                    },
                    {
                        nameCn: "表格数据格式",
                        "oid": "8a189836-d563-440c-b5ea-c04778ac05f9",
                        "label": "Tabular Data Format",
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
            }
        }
    },
    methods: {
        translatePage(jsonContent){
            //切换列表中标签选择情况
            if(this.htmlJSON.Name[0]!=jsonContent.Name[0]) {

                let treeData = this.treeData;

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
                            "title" : "Repository Collections",
                            "description" : "You can query data templates by choosing a collection.",
                            "position" : "right",
                        }
                    },
                    {
                        "element": ".searcherInputPanel",
                        "popover": {
                            "title": "Search",
                            "description": "You can also search data templates by name.",
                            "position": "bottom-right",
                        }
                    },
                    {
                        "element": ".modelPanel",
                        "popover": {
                            "title": "Overview",
                            "description": "Here is query result, you can browse data templates' overview. Click name to check detail.",
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#contributeBtn",
                        "popover" : {
                            "title" : "Contribute",
                            "description" : "You can share data templates on OpenGMS, and get an OpenGMS unique identifier!",
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
                        window.location.href="/user/userSpace#/community/createTemplate";
                    }
                    else{
                        window.location.href="/user/login";
                    }
                },
                error: function (e) {
                    alert(this.htmlJson.loadUserError);
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

            let url=getTemplateList();
            // if(query.searchText.trim()==""){
            //     url=getTemplateList();
            //     query.categoryName = this.categoryName
            // }
            // else{
            //     url="/repository/searchTemplate";
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