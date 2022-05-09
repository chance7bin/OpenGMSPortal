new Vue({
    el: '#app',
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            categoryId: "a24cba2b-9ce1-44de-ac68-8ec36a535d0e",
            statistic:['Overview','Overview','Overview','Overview','Overview','Overview','Overview','Overview','Overview','Overview'],
            activeIndex: '2',
            queryType: 'normal',
            searchText: '',

            classifications_advance: [],

            pageOption: {
                paginationShow:false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 10,

                total: 264,
                searchResult: [],
            },

            defaultProps: {
                children: 'children',
                label: 'label'
            },
            driver : null,
            stepsConfig : null,

            editClassification:false,
            modelOid:"",
            cls:[],
            clsStr:"",
            cls2:[],
            clsStr2:"",
            classType:2,

            sortTypeName:"View Count",
            sortFieldName:"viewCount",
            sortOrder:"Desc.",

            advancedMode:false,

            connectors:["AND","OR","NOT"],

            curQueryField:"Name",
            queryConditions:[{
                connector:"AND",
                queryField:"Name",
                content:"",
            },{
                connector:"AND",
                queryField:"Keyword",
                content:"",
            }],

            recordHistory:true,

            htmlJSON:{
                queryFields:[[1,"Name","Name"],[2,"Keyword","Keyword"],[3,"Content","Content"],[4,"Contributor","Contributor"]],
                ViewCount: ["View Count", "viewCount"],
                Name: ["Name","name"],
                CreateTime: ["Create Time","createTime"],
                Asc: ["Asc.","Asc."],
                Desc: ["Desc.","Desc."],
                overview:"Overview",
                statistics:"Statistics",
                currentClass:"Land regions",
                contributeStr:"contributed at ",
                treeData2:[
                    {"children": [{
                            "children": [{
                                "id": 2,
                                "label": "Land regions",
                                "oid": "a24cba2b-9ce1-44de-ac68-8ec36a535d0e"
                            }, {"id": 3, "label": "Ocean regions", "oid": "75aee2b7-b39a-4cd0-9223-3b7ce755e457"}, {
                                "id": 4,
                                "label": "Frozen regions",
                                "oid": "1bf4f381-6bd8-4716-91ab-5a56e51bd2f9"
                            }, {"id": 5, "label": "Atmospheric regions", "oid": "8f4d4fca-4d09-49b4-b6f7-5021bc57d0e5"}, {
                                "id": 6,
                                "label": "Space-earth regions",
                                "oid": "d33a1ebe-b2f5-4ed3-9c76-78cfb61c23ee"
                            }, {"id": 7, "label": "Solid-earth regions", "oid": "d3ba6e0b-78ec-4fe8-9985-4d5708f28e3e"}
                            ], "id": 1, "label": "Natural-perspective", "oid": "6b2c8632-964a-4a65-a6c5-c360b2b515f0"
                        }, {
                            "children": [{
                                "id": 10,
                                "label": "Development activities",
                                "oid": "808e74a4-41c6-4558-a850-4daec1f199df"
                            }, {"id": 11, "label": "Social activities", "oid": "40534cf8-039a-4a0a-8db9-7c9bff484190"}, {
                                "id": 12,
                                "label": "Economic activities",
                                "oid": "cf9cd106-b873-4a8a-9336-dd72398fc769"
                            }],
                            "id": 9,
                            "label": "Human-perspective",
                            "oid": "77e7482c-1844-4bc3-ae37-cb09b61572da"
                        },{"id":30,
                            "label":"Integrated-perspective",
                            "oid":"396cc739-ef33-4332-8d5d-9a67c89567c7",
                            "children":[{
                                "id": 31,
                                "label": "Global scale",
                                "oid": "14130969-fda6-41ea-aa32-0af43104840b"
                            }, {
                                "id": 32,
                                "label": "Regional scale",
                                "oid": "e56c1254-70b8-4ff4-b461-b8fa3039944e"
                            }]}], "id": 24, "label": "Application-focused categories", "oid": "9f7816be-c6e3-44b6-addf-98251e3d2e19"},

                    {"children": [{
                            "children": [{
                                "id": 15,
                                "label": "Geoinformation analysis",
                                "oid": "afa99af9-4224-4fac-a81f-47a7fb663dba"
                            }, {
                                "id": 16,
                                "label": "Remote sensing analysis",
                                "oid": "f20411a5-2f55-4ee9-9590-c2ec826b8bd5"
                            }, {
                                "id": 17,
                                "label": "Geostatistical analysis",
                                "oid": "1c876281-a032-4575-8eba-f1a8fb4560d8"
                            }, {"id": 18, "label": "Intelligent computation analysis", "oid": "c6fcc899-8ca4-4269-a21e-a39d38c034a6"}],
                            "id": 14,
                            "label": "Data-perspective",
                            "oid": "4785308f-b2ef-4193-a74b-b9fe025cbc5e"
                        }, {
                            "children": [{
                                "id": 20,
                                "label": "Physical process calculation",
                                "oid": "1d564d0f-51c6-40ca-bd75-3f9489ccf1d6"
                            }, {
                                "id": 21,
                                "label": "Chemical process calculation",
                                "oid": "63266a14-d7f9-44cb-8204-c877eaddcaa1"
                            }, {
                                "id": 22,
                                "label": "Biological process calculation",
                                "oid": "6d1efa2c-830d-4546-b759-c66806c4facc"
                            }, {"id": 23, "label": "Human-activity calculation", "oid": "6952d5b2-cb0f-4ba7-96fd-5761dd566344"}],
                            "id": 19,
                            "label": "Process-perspective",
                            "oid": "746887cf-d490-4080-9754-1dc389986cf2"
                        }], "id": 25, "label": "Method-focused categories", "oid": "5f74872a-196c-4889-a7b8-9c9b04e30718"}],

            }
        }
    },
    methods: {
        translatePage(jsonContent){
            //切换当前分类名称及分类树的选中情况
            let currentTreeDataId = "";
            let newLabel = "";
            let currentTreeData = this.htmlJSON.treeData2;
            let newTreeData = jsonContent.treeData2
            for (i = 0; i < currentTreeData.length; i++) {
                let treeData1 = currentTreeData[i];
                if (treeData1.label == this.htmlJSON.currentClass) {
                    currentTreeDataId = treeData1.id;
                    break;
                }
                let find1 = false;
                for (j = 0; j < treeData1.children.length; j++) {
                    let treeData2 = treeData1.children[j];
                    if (treeData2.label == this.htmlJSON.currentClass) {
                        currentTreeDataId = treeData2.id;
                        find1 = true;
                        break;
                    }
                    let find2 = false;
                    for (k = 0; k < treeData2.children.length; k++) {
                        let treeData3 = treeData2.children[k];
                        if (treeData3.label == this.htmlJSON.currentClass) {
                            currentTreeDataId = treeData3.id;
                            find2 = true;
                            break;
                        }
                    }
                    if (find2) {
                        break;
                    }
                }
                if (find1) {
                    break;
                }
            }
            for (i = 0; i < newTreeData.length; i++) {
                let treeData1 = newTreeData[i];
                if (treeData1.id == currentTreeDataId) {
                    newLabel = treeData1.label;
                    break;
                }
                let find1 = false;
                for (j = 0; j < treeData1.children.length; j++) {
                    let treeData2 = treeData1.children[j];
                    if (treeData2.id == currentTreeDataId) {
                        newLabel = treeData2.label;
                        find1 = true;
                        break;
                    }
                    let find2 = false;
                    for (k = 0; k < treeData2.children.length; k++) {
                        let treeData3 = treeData2.children[k];
                        if (treeData3.id == currentTreeDataId) {
                            newLabel = treeData3.label;
                            find2 = true;
                            break;
                        }
                    }
                    if (find2) {
                        break;
                    }
                }
                if (find1) {
                    break;
                }
            }
            //切换列表中标签选择情况
            if(this.htmlJSON.overview!=jsonContent.overview) {

                for (i = 0; i < this.statistic.length; i++) {
                    let sta = this.statistic[i];
                    if (sta == this.htmlJSON.overview) {
                        this.statistic[i] = jsonContent.overview;
                    } else {
                        this.statistic[i] = jsonContent.statistics;
                    }
                }

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

            this.$nextTick(()=>{
                if(newLabel == ""){
                    let language = window.localStorage.getItem('language');
                    if(language=="en-us"){
                        this.htmlJSON.currentClass = "ALL";
                    }else{
                        this.htmlJSON.currentClass = "全部";
                    }
                }else {
                    this.htmlJSON.currentClass = newLabel;
                    this.$refs.treeNew.setCurrentKey(currentTreeDataId);
                }
            });
        },

        //高级搜索
        conditionAppend(){
            this.queryConditions.add({
                connector:"AND",
                queryField:"Name",
                content:"",
            });
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

        handleCheckChange12(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree12.getCheckedNodes()
            let classes = [];
            let str='';
            for (let i = 0; i < checkedNodes.length; i++) {
                // console.log(checkedNodes[i].children)
                if(checkedNodes[i].children!=undefined){
                    continue;
                }

                classes.push(checkedNodes[i].oid);
                str+=checkedNodes[i].label;
                if(i!=checkedNodes.length-1){
                    str+=", ";
                }
            }
            this.cls=classes;
            this.clsStr=str;

        },
        handleCheckChange14(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree14.getCheckedNodes()
            let classes = [];
            let str='';
            for (let i = 0; i < checkedNodes.length; i++) {
                // console.log(checkedNodes[i].children)
                if(checkedNodes[i].children!=undefined){
                    continue;
                }

                classes.push(checkedNodes[i].oid);
                str+=checkedNodes[i].label;
                if(i!=checkedNodes.length-1){
                    str+=", ";
                }
            }
            this.cls2=classes;
            this.clsStr2=str;

        },
        getClassifications(modelItem){
            this.editClassification = true;
            this.modelOid = modelItem.oid;
            $.get("/modelItem/getClassification/"+modelItem.oid,{},(result)=>{
                //cls
                this.cls = result.data.class1;

                let ids=[];
                for(i=0;i<this.cls.length;i++){
                    for(j=0;j<2;j++){
                        for(k=0;k<this.treeData[j].children.length;k++){
                            let children=this.treeData[j].children[k].children;
                            if(children==null) {
                                if (this.cls[i] == this.treeData[j].children[k].oid) {
                                    ids.push(this.treeData[j].children[k].id);
                                    this.clsStr += this.treeData[j].children[k].label;
                                    if (i != this.cls.length - 1) {
                                        this.clsStr += ", ";
                                    }
                                    break;
                                }
                            }
                            else{
                                for(x=0;x<children.length;x++){
                                    if (this.cls[i] == children[x].oid) {
                                        ids.push(children[x].id);
                                        this.clsStr += children[x].label;
                                        if (i != this.cls.length - 1) {
                                            this.clsStr += ", ";
                                        }
                                        break;
                                    }
                                }
                            }

                        }
                        if(ids.length-1==i){
                            break;
                        }
                    }
                }

                this.$refs.tree12.setCheckedKeys(ids);

                //cls
                this.cls2 = result.data.class2;
                let ids2=[];
                if(this.cls2 != null) {
                    for (i = 0; i < this.cls2.length; i++) {
                        for (j = 0; j < 2; j++) {
                            for (k = 0; k < this.htmlJSON.treeData2[j].children.length; k++) {
                                let children = this.htmlJSON.treeData2[j].children[k].children;
                                if (children == null) {
                                    if (this.cls2[i] == this.htmlJSON.treeData2[j].children[k].oid) {
                                        ids2.push(this.htmlJSON.treeData2[j].children[k].id);
                                        this.clsStr2 += this.htmlJSON.treeData2[j].children[k].label;
                                        if (i != this.cls2.length - 1) {
                                            this.clsStr2 += ", ";
                                        }
                                        break;
                                    }
                                }
                                else {
                                    for (x = 0; x < children.length; x++) {
                                        if (this.cls2[i] == children[x].oid) {
                                            ids2.push(children[x].id);
                                            this.clsStr2 += children[x].label;
                                            if (i != this.cls2.length - 1) {
                                                this.clsStr2 += ", ";
                                            }
                                            break;
                                        }
                                    }
                                }

                            }
                            if (ids2.length - 1 == i) {
                                break;
                            }
                        }
                    }
                }

                this.$refs.tree14.setCheckedKeys(ids2);
            });

        },
        submitClassifications(){
            let data = {
                oid:this.modelOid,
                class1:this.cls,
                class2:this.cls2,
            };
            $.post("/modelItem/updateClass",data,(result)=>{
                this.$alert(this.htmlJson.ChangeClassificationSuccess, 'Success', {
                    type: 'success',
                    confirmButtonText: 'OK',
                    callback: action => {
                        this.editClassification = false;
                        this.getModels(this.classType);
                    }
                });
            })
        },

        switchInit(){
            let initStr = "";
            if(this.htmlJSON.statistics=="Overview"||this.htmlJSON.statistics=="Statistics") {
                initStr = "Overview";
            }else{
                initStr = "概览";
            }
            this.statistic = [];
            for(i = 0;i<10;i++){
                this.statistic.push(initStr);
            }
        },

        switchChange(key){
            if(this.statistic[key]!='Overview'&&this.statistic[key]!="概览"){
                // var dom = document.getElementById("chart"+key);
                // var myChart = echarts.init(dom);
                // var app = {};
                //
                //     option = {
                //         legend: {},
                //         tooltip: {
                //             trigger: 'axis',
                //             showContent: false
                //         },
                //         dataset: {
                //             source: [
                //                 ['product', '2012', '2013', '2014', '2015', '2016', '2017'],
                //                 ['Matcha Latte', 41.1, 30.4, 65.1, 53.3, 83.8, 98.7],
                //                 ['Milk Tea', 86.5, 92.1, 85.7, 83.1, 73.4, 55.1],
                //                 ['Cheese Cocoa', 24.1, 67.2, 79.5, 86.4, 65.2, 82.5],
                //                 ['Walnut Brownie', 55.2, 67.1, 69.2, 72.4, 53.9, 39.1]
                //             ]
                //         },
                //         xAxis: {type: 'category'},
                //         yAxis: {gridIndex: 0},
                //         grid: {top: '5%'},
                //         series: [
                //             {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                //             {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                //             {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                //             {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                //
                //         ]
                //     };
                //
                //
                //
                //     myChart.setOption(option);

                let chart=echarts.init(document.getElementById('chart'+key));
                chart.showLoading();
                $.get("/modelItem/dailyViewAndInvokeCount",{id:this.pageOption.searchResult[key].id},(result)=> {
                    let valueList = result.data.valueList;//[0, 0, 0, 0, 0];
                    console.log(result)
                    chart.hideLoading();
                    // let option = {
                    //
                    //     // Make gradient line here
                    //     visualMap: [{
                    //         show: false,
                    //         type: 'continuous',
                    //         seriesIndex: 0,
                    //         min: 0,
                    //         max: result.data.max
                    //     }],
                    //     title: [{
                    //         left: 'center',
                    //         text: 'Daily View Count'
                    //     }],
                    //     tooltip: {
                    //         trigger: 'axis'
                    //     },
                    //     xAxis: [{
                    //         data: dateList
                    //     }],
                    //     yAxis: [{
                    //         splitLine: {show: false}
                    //     }],
                    //     grid: [{
                    //         top: '10%',
                    //         bottom: '15%'
                    //     }],
                    //     series: [{
                    //         type: 'line',
                    //         showSymbol: false,
                    //         data: valueList
                    //     }]
                    // };
                    let series = [];
                    for(i=1;i<valueList.length;i++){
                        series.push({type: 'line', smooth: false, seriesLayoutBy: 'row'});
                    }
                    option = {
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
            }
        },
        contribute(){
            $.ajax({
                url: '/user/load',
                type: 'get',
                // data对象中的属性名要和服务端控制器的参数名一致 login(name, password)
                // dataType : 'json',
                success: function (result) {
                    var json = result;
                    if (json.oid != '') {
                        window.location.href="/user/userSpace#/model/createModelItem";
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
            this.switchInit();

            this.pageOption.currentPage = 1;
            this.categoryId = "";
            if(this.queryType=='normal') {
                this.$refs.treeNew.setCurrentKey(null);
            }
            this.htmlJSON.currentClass="ALL"
            this.classType = 2;
            this.getModels(this.classType);
        },
        //页码change
        handlePageChange(val) {
            this.switchInit();
            this.pageOption.currentPage = val;

            window.scrollTo(0, 0);
            this.getModels(this.classType);
        },

        handleCurrentChange2(data) {

            this.switchInit();
            // this.pageOption.searchResult=[];
            this.pageOption.total=0;
            this.pageOption.paginationShow=false;
            this.htmlJSON.currentClass=data.label;
            this.categoryId = data.oid

            this.pageOption.currentPage=1;
            this.searchText="";
            this.classType = 2;
            this.getModels(this.classType);
        },

        getChildren(children) {
            if (children != null) {
                for (let i = 0; i < children.length; i++) {
                    let child = children[i];
                    this.classifications_old.push(child.oid);
                    this.getChildren(child.children);
                }
            }
        },
        handleCheckChange(data, checked, indeterminate) {
            this.switchInit();
            // this.pageOption.searchResult=[];
            this.pageOption.paginationShow=false;
            let checkedNodes = this.$refs.treeAdvance.getCheckedNodes()
            let classes = [];
            for (let i = 0; i < checkedNodes.length; i++) {
                classes.push(checkedNodes[i].oid);
            }
            this.classifications_advance = classes;
            console.log(this.classifications_advance);
            this.pageOption.currentPage=1;
            this.getModels();
        },
        getModels(classType) {
            if(this.recordHistory) {
                this.setUrl();
            }else{
                this.recordHistory = true;
            }
            this.pageOption.progressBar = true;
            var data = {
                sortField:this.sortFieldName,
                asc: this.pageOption.sortAsc,
                page: this.pageOption.currentPage,
                pageSize: this.pageOption.pageSize,
                curQueryField: this.curQueryField,
                categoryName:this.categoryId,
            };
            switch (this.queryType) {
                case "normal":
                    data.searchText = this.searchText.trim();

                    break;
                case "advanced":
                    data.conditions = JSON.stringify(this.queryConditions);
                    data.classifications = this.classifications_advance.length == 0 ? ["all"] : this.classifications_advance;

                    break;
            }
            console.log(data)
            this.Query(data, this.queryType);
        },

        Query(data, type) {
            let sendDate = (new Date()).getTime();
            axios.post(type == "normal" ? getModelItemList() : "/modelItem/advance",data)
                .then(res => {
                    result = res.data
                    if (result.code == 0) {
                        let data = result.data;
                        let receiveDate = (new Date()).getTime();
                        let responseTimeMs = receiveDate - sendDate;
                        let timeoutTime=0;
                        //console.log(responseTimeMs)
                        if(responseTimeMs<450){
                            timeoutTime=450-responseTimeMs;
                        }
                        setTimeout(() => {

                            this.pageOption.total = data.total;
                            this.pageOption.pages = data.pages;
                            this.pageOption.searchResult = data.list;
                            this.pageOption.users = data.users;
                            this.pageOption.progressBar = false;
                            this.pageOption.paginationShow=true;
                        }, timeoutTime);

                    }
                    else {
                        console.log("query error!")
                    }

                })
            // $.ajax({
            //     type: "POST",
            //     url:'11',
            //     data: JSON.stringify(data),
            //     async: true,
            //     success: (json) => {
            //         console.log(11)
            //
            //         if (json.code == 0) {
            //             let data = json.data;
            //             let receiveDate = (new Date()).getTime();
            //             let responseTimeMs = receiveDate - sendDate;
            //             let timeoutTime=0;
            //             //console.log(responseTimeMs)
            //             if(responseTimeMs<450){
            //                 timeoutTime=450-responseTimeMs;
            //             }
            //             setTimeout(() => {
            //
            //                 this.pageOption.total = data.total;
            //                 this.pageOption.pages = data.pages;
            //                 this.pageOption.searchResult = data.list;
            //                 this.pageOption.users = data.users;
            //                 this.pageOption.progressBar = false;
            //                 this.pageOption.paginationShow=true;
            //             }, timeoutTime);
            //
            //         }
            //         else {
            //             console.log("query error!")
            //         }
            //     }
            // })
        },
        setUrl(){
            let newUrl;
            if(this.htmlJSON.currentClass=="ALL"){
                newUrl = window.location.pathname+"?searchText="+this.searchText+"&field="+this.curQueryField;
            }else{
                newUrl = window.location.pathname+"?category=" + this.$refs.treeNew.getCurrentNode().oid;
            }

            newUrl += "&sortField=" + this.sortFieldName;
            newUrl += "&asc=" + this.pageOption.sortAsc;
            newUrl += "&page=" + this.pageOption.currentPage;
            newUrl += "&language=" + window.localStorage.getItem("language");

            var stateObject = {};
            var title = "Model Item Repository | OpenGMS";
            history.pushState(stateObject,title,newUrl);
        },
        GetQueryString(originStr,paramName) {

            var reg = new RegExp("(^|&)"+ paramName +"=([^&]*)(&|$)");

            var r = originStr.substr(1).match(reg);

            if(r!=null)return  unescape(r[2]); return null;

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
                            "title" : "Model Classifications",
                            "description" : "You can query models by choosing a classification.",
                            "position" : "right-top",
                        }
                    },
                    {
                        "element": ".searcherInputPanel",
                        "popover": {
                            "title": "Search",
                            "description": "You can also search models by model name.",
                            "position": "bottom-right",
                        }
                    },
                    {
                        "element": ".modelPanel",
                        "popover": {
                            "title": "Overview",
                            "description": "Here is query result, you can browse models' overview. Click model name to check model detail.",
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#contributeBtn",
                        "popover" : {
                            "title" : "Contribute",
                            "description" : "You can share your models on OpenGMS, and get an OpenGMS unique identifier!",
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

        urlSearch(paramStr){
            let searchText = this.GetQueryString(paramStr, "searchText");
            let field = this.GetQueryString(paramStr, "field");

            let category=this.GetQueryString(paramStr, "category");

            let sortField = this.GetQueryString(paramStr, "sortField");
            let asc = this.GetQueryString(paramStr, "asc");
            let page=this.GetQueryString(paramStr, "page");
            console.log(category,page)
            //按分类查询
            if(category!=null) {
                this.searchText = "";
                this.categoryId = category;
                for(i=0;i<this.htmlJSON.treeData2.length;i++){
                    if(category==this.htmlJSON.treeData2[i].oid){
                        this.$refs.treeNew.setCurrentKey(this.htmlJSON.treeData2[i].id);
                        this.htmlJSON.currentClass=this.htmlJSON.treeData2[i].label;
                        break;
                    }
                    else{
                        let children = this.htmlJSON.treeData2[i].children;
                        let find=false;
                        for(j=0;j<children.length;j++){
                            if(category==children[j].oid){
                                find=true;
                                this.$refs.treeNew.setCurrentKey(children[j].id);
                                this.htmlJSON.currentClass=children[j].label;
                                $(".el-tree-node__expand-icon").eq(i).click();
                                break;
                            }
                            else{
                                let childrens=children[j].children;
                                if(childrens!=undefined) {
                                    for (k = 0; k < childrens.length; k++) {
                                        if (category == childrens[k].oid) {
                                            find = true;
                                            this.$refs.treeNew.setCurrentKey(childrens[k].id);
                                            this.htmlJSON.currentClass = childrens[k].label;
                                            $(".el-tree-node__expand-icon").eq(1).click();
                                            var index=j+2;
                                            setTimeout(function(){
                                                console.log($(".el-tree-node__expand-icon"))
                                                $(".el-tree-node__expand-icon").eq(index).click();
                                            },200);

                                            break;
                                        }
                                    }
                                }

                            }
                        }
                        if(find){
                            break;
                        }
                    }

                }
            }
            // else{
            //
            //     // this.$refs.tree1.setCurrentKey(2);
            //     // //展开分类树第一层
            //     // $(".el-tree-node__expand-icon").eq(0).click();
            //     // $(".el-tree-node__expand-icon").eq(1).click();
            // }
            //按queryString查询
            else if(searchText!=null) {
                this.$refs.treeNew.setCurrentKey(null);
                this.categoryId = "";
                this.htmlJSON.currentClass="ALL";
                this.searchText = searchText;
                this.curQueryField = field;
            }else{
                this.$refs.treeNew.setCurrentKey(24);
                this.categoryId = "9f7816be-c6e3-44b6-addf-98251e3d2e19";
                this.htmlJSON.currentClass="Application-focused categories";
            }


            //设置页数
            if(page!=null){
                this.pageOption.currentPage=page;
            }else{
                this.pageOption.currentPage=1;
            }

            if(sortField!=null){
                this.sortFieldName = sortField;
                switch(sortField){
                    case "viewCount":
                        this.sortTypeName = "View Count";
                        break;
                    case "name":
                        this.sortTypeName = "Name";
                        break;
                    case "createTime":
                        this.sortTypeName = "Create Time";
                        break;
                }
            }

            if(asc!=null){
                this.pageOption.sortAsc = asc;
                if(asc){
                    this.sortOrder = this.htmlJSON.Asc[0];
                }else{
                    this.sortOrder = this.htmlJSON.Desc[0];
                }
            }

            this.recordHistory = false;
            //执行查询
            this.getModels(this.classType);
        },

        createNew(){
            axios.get("/user/load").then(
                res => {
                    if(res.data.oid==''){
                         this.$alert(this.htmlJson.LoginInFirst, 'Tip', {
                                  type:"warning",
                                  confirmButtonText: 'OK',
                                  callback: ()=>{
                                      window.location.href='/user/login'
                                  }
                              }
                          );
                    }else{
                        window.location.href=createItemUrl['modelItem']
                    }
                }
            )
        },

        feedBack(){
            window.location.href="mailto:opengms@njnu.edu.cn"
        },
    },
    mounted() {


        this.urlSearch(decodeURIComponent(window.location.search));


        //expend
        $("#expend").click(() => {
            this.pageOption.searchResult=[];
            this.pageOption.paginationShow=false;

            this.queryType = "advanced";
            $(".searcherPanel").css("display", "none");
            $(".advancedSearch").css("display", "block");
            // if($(".el-checkbox__input").eq(1).hasClass("is-checked")==true){
            //     this.getModels();
            // }
            // else {
            //     $(".el-checkbox__input").eq(1).click();
            // }
            this.getModels();
            $("#curClassBar").hide();
        })
        $("#drawback").click(() => {
            this.pageOption.searchResult=[];
            this.pageOption.paginationShow=false;

            this.queryType = "normal";
            $(".searcherPanel").css("display", "block");
            $(".advancedSearch").css("display", "none");
            this.getModels();
            $("#curClassBar").show();
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
        });

        if(document.cookie.indexOf("modelRep=1")==-1){
            this.showDriver();
            var t=new Date(new Date().getTime()+1000*60*60*24*60);
            document.cookie="modelRep=1; expires="+t.toGMTString();
        }

        window.addEventListener('popstate', (event)=> {
            this.urlSearch(event.currentTarget.location.search);
            console.log(event.currentTarget.location.search);
        })

    }
})