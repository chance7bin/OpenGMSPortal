//该组件有两种用法，一种是嵌入到el-dialog中，一种可以单独使用，根据父组件inDialog值判断
//在dialog中时可以通过调用closeDialog函数控制弹窗关闭，postMsg向父组件传输信息
//单独使用时也包含了confirmLogin的独立功能
//提交按钮也可以使用两种模式，一种是dialog中的自带footer按钮调用组件传值给父组件，一种是组件自带提交按钮直接提交

Vue.component("linkRelatedItemModule",
    {
        template: '#linkRelatedItemModule',
        props: {
            targetId: {
                type: String,
                default: '0'
            },
            targetType: {
                type: String,
                default: 'modelItem'
            },
            relateType: {
                type: String,
                default: 'modelItem'
            },
            ownConfirmButton:{//是否包含自带上传按钮
                type:Boolean,
                default:true
            },
            inDialog:{
                type:Boolean,
                default:false
            }
        },
        data() {
            return {
                targetItemId:'',
                targetItemType:'',
                relateItemType:'',

                pageOption_my: {
                    paginationShow: false,
                    progressBar: true,
                    sortAsc: false,
                    currentPage: 1,
                    pageSize: 5,
                    relateSearch: "",
                    sortField:"default",
                    total: 99999,
                    searchResult: [],
                },

                pageOption_all: {
                    paginationShow: false,
                    progressBar: true,
                    sortAsc: false,
                    currentPage: 1,
                    pageSize: 5,
                    relateSearch: "",
                    sortField:"viewCount",
                    total: 99999,
                    searchResult: [],
                },

                tableData_model: [],

                activeName_dialog :"my",

                tableMaxHeight: 400,

                options: [{
                    // label: 'Basic',
                    options: [{
                        value: 'Connected with'
                    }, {
                        value: 'Similar to'
                    }, {
                        value: 'Coexist in'
                    }]
                }, {
                    // label: 'Child',
                    options: [{
                        value: 'Evolved from'
                    }, {
                        value: 'Belongs to'
                    }, {
                        value: 'Integrated into'
                    }]
                }, {
                    // label: 'Parent',
                    options: [{
                        value: 'Inspires'
                    }, {
                        value: 'Contains'
                    }, {
                        value: 'Employs/Depends on'
                    }]
                }],
            }
        },
        computed: {},
        methods: {
            postMsg(msg){
                this.$emit('receive-msg',msg)
            },

            setSession(name, value) {
                window.sessionStorage.setItem(name, value);
            },

            confirmLogin(){
                window.sessionStorage.setItem("history", window.location.href);
                this.$confirm('<div style=\'font-size: 18px\'>This function requires an account, <br/>please login first.</div>', 'Tip', {
                    dangerouslyUseHTMLString: true,
                    confirmButtonText: 'Log In',
                    cancelButtonClass: 'fontsize-15',
                    confirmButtonClass: 'fontsize-15',
                    type: 'info',
                    center: true,
                    showClose: false,
                }).then(() => {
                    window.location.href = "/user/login";
                }).catch(() => {

                });
            },

            manualInit(targetItemId,targetItemType,relateItemType){
                this.targetItemId = targetItemId
                this.targetItemType = targetItemType
                this.relateItemType = relateItemType

                this.pageOption_my.currentPage = 1;
                this.pageOption_my.searchResult = [];
                this.pageOption_my.relateSearch = "";

                this.pageOption_all.currentPage = 1;
                this.pageOption_all.searchResult = [];
                this.pageOption_all.relateSearch = "";

                this.activeName_dialog = 'my'

                // this.getRelatedResources()
                this.getRelation()

                this.search(this.activeName_dialog);
                if(this.activeName_dialog!="all"){
                    this.search("all");
                }
            },

            getRelation() {
                //从地址栏拿到id
                let id = this.targetItemId;
                let data = {
                    id: id,
                    type: this.relateItemType
                };
                $.ajax({
                    type: "GET",
                    url: "/"+this.targetItemType+"/relation",
                    data: data,
                    async: true,
                    success: (json) => {
                        if (json.code == 0) {
                            let data = json.data;
                            console.log(data)

                            this.tableData_model = data;

                        }else if(json.code == -1){

                            if(this.inDialog){
                                this.postMsg(window.module_msg.no_login)
                                this.$emit('close-father-dialog')
                            }
                            this.confirmLogin()


                        }
                        else {
                            console.log("query error!")
                        }
                    }
                })
            },


            getRelatedResources() {
                //从地址栏拿到id
                let id = this.targetItemId;
                let data = {
                    id: id,
                };

                $.ajax({
                    type: "GET",
                    url: "/"+this.targetItemType+"/getRelatedResources",
                    data: data,
                    async: true,
                    success: (json) => {
                        if (json.code == 0) {
                            let data = json.data;
                            console.log(data)

                            this.tableData_model = data;

                        }else  if (json.code == -1) {
                            if(this.inDialog){
                                this.postMsg(window.module_msg.no_login)
                                this.$emit('close-father-dialog')
                            }else{
                                this.confirmLogin()
                            }
                        }
                        else {
                            console.log("query error!")
                        }
                    }
                })
            },

            checkContent(row){
                if(row.type=='file'){
                    return
                }else if(row.type=='link'){
                    window.open(row.content)
                }else {
                    window.open('/repository/'+row.type+'/'+row.oid)
                }
            },

            handlePageChange(val) {
                if(this.activeName_dialog=="my") {
                    this.pageOption_my.currentPage = val;
                }else{
                    this.pageOption_all.currentPage = val;
                }
                this.search(this.activeName_dialog);
            },

            searchInit(scope){
                this.pageOption_all.currentPage = 1;
                this.pageOption_my.currentPage = 1;
                this.search(scope);
            },
            search(scope) {
                let data;
                if(scope=="all"){
                    // this.pageOption_all.currentPage = 1;
                    data = {
                        asc: this.pageOption_all.sortAsc,
                        page: this.pageOption_all.currentPage-1,
                        pageSize: this.pageOption_all.pageSize,
                        searchText: this.pageOption_all.relateSearch.trim(),
                        sortField: this.pageOption_all.sortField,
                        classifications: ["all"],
                    }
                }else {
                    // this.pageOption_my.currentPage = 1;
                    data = {
                        asc: this.pageOption_my.sortAsc,
                        page: this.pageOption_my.currentPage-1,
                        pageSize: this.pageOption_my.pageSize,
                        searchText: this.pageOption_my.relateSearch,
                        sortField: this.pageOption_my.sortField,
                        classifications: ["all"],
                    };
                }
                let url, contentType;
                switch (this.relateItemType) {
                    case "dataItem":
                        if(scope=="all") {
                            url = "/dataItem/searchByName";
                        }else{
                            url = "/dataItem/searchByNameAndAuthor";
                        }
                        data = {
                            page: data.page+1,
                            pageSize: data.pageSize,
                            asc: true,
                            classifications: [],
                            category: '',
                            searchText: data.searchText,
                            tabType: "repository",
                            sortField: data.sortField,
                        };
                        data = JSON.stringify(data);
                        contentType = "application/json";
                        break;
                    default:
                        if(scope=="all") {
                            url = "/" + this.relateItemType + "/list";
                        }else{
                            url = "/" + this.relateItemType + "/listByAuthor";
                        }
                        contentType = "application/x-www-form-urlencoded";
                        data.classType=1;
                }
                $.ajax({
                    type: "POST",
                    url: url,
                    data: data,
                    async: true,
                    contentType: contentType,
                    success: (json) => {
                        if (json.code == 0) {
                            let data = json.data;
                            console.log(data)

                            if(scope=="all") {
                                this.pageOption_all.total = data.total;
                                this.pageOption_all.pages = data.pages;
                                this.pageOption_all.searchResult = data.list;
                                this.pageOption_all.users = data.users;
                                this.pageOption_all.progressBar = false;
                                this.pageOption_all.paginationShow = true;
                            }else{
                                this.pageOption_my.total = data.total;
                                this.pageOption_my.pages = data.pages;
                                this.pageOption_my.searchResult = data.list;
                                this.pageOption_my.users = data.users;
                                this.pageOption_my.progressBar = false;
                                this.pageOption_my.paginationShow = true;
                            }

                        }
                        else {
                            console.log("query error!")
                        }
                    }
                })
            },

            jump() {
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
                    success: (data) => {
                        if (data.oid == "") {
                            if(this.inDialog){
                                this.postMsg(window.module_msg.no_login)
                                this.$emit('close-father-dialog')
                            }else{
                                this.confirmLogin()
                            }
                        }
                        else {
                            let arr = window.location.href.split("/");
                            let bindOid = arr[arr.length - 1].split("#")[0];
                            this.setSession("bindOid", bindOid);
                            switch (this.relateItemType) {
                                case "modelItem":
                                    window.open("/user/userSpace#/model/createModelItem", "_blank")
                                    break;
                                case "conceptualModel":
                                    window.open("/user/userSpace#/model/createConceptualModel", "_blank")
                                    break;
                                case "logicalModel":
                                    window.open("/user/userSpace#/model/createLogicalModel", "_blank")
                                    break;
                                case "computableModel":
                                    window.open("/user/userSpace#/model/createComputableModel", "_blank")
                                    break;
                            }
                            this.closeFatherDialog()
                        }
                    }
                })
            },

            isCurrentItem(row){

                return(this.targetItemId==row.oid);
            },

            relationSortChange(sort){
                console.log(sort);
                let order = sort.order==="ascending";
                let field = sort.column.label.toLowerCase();
                if(this.activeName_dialog==="my"){
                    this.pageOption_my.sortAsc=order;
                    this.pageOption_my.sortField=field;
                }else{
                    this.pageOption_all.sortAsc=order;
                    this.pageOption_all.sortField=field;
                }
                this.search(this.activeName_dialog);
            },

            hasAdded(row){
                for(let i=0;i<this.tableData_model.length;i++){
                    let data = this.tableData_model[i];
                    let oid1,oid2;
                    if(data.oid!=undefined){
                        oid1 = data.oid;
                        oid2 = row.oid;
                    }else{
                        oid1 = data.id;
                        oid2 = row.id;
                    }
                    if(oid1==oid2){
                        return true;
                    }
                }
            },

            handleDelete(index, row) {
                console.log(index, row);
                let table = new Array();
                for (i = 0; i < this.tableData_model.length; i++) {
                    let data = this.tableData_model[i];
                    let oid1,oid2;
                    if(data.oid!=undefined){
                        oid1 = data.oid;
                        oid2 = row.oid;
                    }else{
                        oid1 = data.id;
                        oid2 = row.id;
                    }
                    if(oid1!=oid2) {
                        table.push(this.tableData_model[i]);
                    }
                }
                // table.splice(index, 1);
                this.tableData_model = table;

            },

            handleEdit(index, row) {
                console.log(row);
                row.type=this.relateItemType
                let flag = false;
                for (i = 0; i < this.tableData_model.length; i++) {
                    let tableRow = this.tableData_model[i];
                    if (tableRow.oid == row.oid) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    if(this.relateItemType=="modelItem"){
                        this.$set(row,"relation","Connected with");
                    }
                    this.tableData_model.push(row);
                }
            },

            closeFatherDialog(){
                this.$emit('close-father-dialog')
            },

            confirm() {
                //从地址栏拿到oid
                let oid = this.targetItemId

                let relateArr = [];
                let url = '';
                let data;
                let contentType;

                if(this.relateItemType !== "modelItem") {
                    url = "/modelItem/setRelation";
                    this.tableData_model.forEach(function (item, index) {
                        relateArr.push(item.oid);
                    })
                    data = {
                        oid: oid,
                        type: this.relateItemType,
                        relations: relateArr
                    };
                    contentType = "application/x-www-form-urlencoded;charset=UTF-8";
                }else{
                    url = "/modelItem/setModelRelation/"+oid;
                    this.tableData_model.forEach(function (item, index) {
                        let obj = {
                            oid : item.oid,
                            relation : item.relation,
                        };
                        relateArr.push(obj);
                    });
                    data = {
                        relations: relateArr,
                    };
                    data = JSON.stringify(data);
                    contentType = "application/json;charset=UTF-8";
                }

                $.ajax({
                    type: "POST",
                    url: url,
                    data: data,
                    contentType:contentType,
                    async: true,
                    success: (result) => {
                        if(result.code == -1){
                            if(this.inDialog){
                                this.postMsg(window.module_msg.no_login)
                                this.$emit('close-father-dialog')
                            }else{
                                this.confirmLogin()
                            }
                        }else{
                            let info = result.data.type;
                            if(info === 'suc'){
                                this.$alert('Success!', 'Tip', {
                                    type:'success',
                                    confirmButtonText: 'OK',
                                    callback: action => {

                                        if(this.relateItemType === "modelItem"){
                                            this.relatedModelItems = result.data.data;
                                            this.setRelatedModelItemsPage();
                                            if(this.modelRelationGraphShow){
                                                this.generateModelRelationGraph();
                                            }

                                        }else {
                                            this.postMsg(window.module_msg.suc)
                                        }
                                    }
                                });
                            }else if(info==='version'){
                                this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                                    type: 'success',
                                    confirmButtonText: 'OK',
                                    callback: action => {
                                        window.location.reload();
                                        if(this.inDialog){
                                            this.postMsg(window.module_msg.pending)
                                        }
                                    }
                                })
                            }
                        }

                    },
                    error: (json) => {
                        this.$alert('Submitted failed!', 'Error', {
                            type:'error',
                            confirmButtonText: 'OK',
                            callback: action => {

                            }
                        });
                    }
                })
            },

            setRelatedModelItemsPage(){
                this.relatedModelItemsPage = [];
                for(i=0;i<this.relatedModelItems.length;i++){
                    if(i===this.relationPageSize) break;
                    this.relatedModelItemsPage.push(this.relatedModelItems[i]);

                }
            },

            generateModelRelationGraph(){
                this.modelRelationGraphShow = true;

                let nodes = [];
                let links = [];

                $.post("/modelItem/getRelationGraph",{"oid":this.modelInfo.oid},(result)=>{
                    console.log(result);
                    nodes = result.data.nodes;
                    links = result.data.links;

                    setTimeout(()=>{

                        let object = document.getElementById('modelRelationGraph');
                        let modelRelationGraph = echarts.init(object,'light');
                        modelRelationGraph.showLoading();

                        modelRelationGraph.on("click",(param)=>{
                            if(param.value !== undefined){
                                this.curRelation=param.value;
                                this.modelRelationGraphSideBarShow = true;
                            }
                            console.log(param)
                        });

                        let graph_nodes = [];
                        let graph_links = [];

                        let radius = 200;
                        let title = this.modelInfo.name;
                        graph_nodes.push({
                            name: title,
                            x: 500,
                            y: 300,
                            value: {
                                style: "node",
                                type: "model",
                                name: nodes[0].name,
                                oid: nodes[0].oid,
                                img: nodes[0].img,
                                overview: nodes[0].overview,
                            },
                            itemStyle:{
                                color: 'green',
                            },
                            label: {
                                formatter: title.length > 9 ? title.substring(0,7)+"..." : title,
                            },
                            tooltip:{
                                formatter:"{b}",
                            },
                        });

                        let dtAngle = 360 / nodes.length;
                        let curAngle = 0;
                        //加入节点
                        for(i = 1;i<nodes.length;i++){
                            let node = nodes[i];

                            curAngle = curAngle + dtAngle;
                            let radian = curAngle * 2 * Math.PI / 360;
                            let dx = Math.cos(radian) * radius;
                            let dy = Math.sin(radian) * radius;

                            let name = node.name;

                            //加入节点
                            if(node.type === "ref"){
                                let formatter = name.length > 9 ? name.substring(0,7)+"..." : name;
                                graph_nodes.push({
                                    name: node.name,
                                    value: {
                                        style: "node",
                                        type: "ref",
                                        name: node.name,
                                        author: node.author,
                                        journal: node.journal,
                                        link: node.link,
                                    },
                                    x: graph_nodes[0].x + dx,
                                    y: graph_nodes[0].y + dy,
                                    symbolSize: 8,
                                    itemStyle:{
                                        color: 'skyblue',
                                    },
                                    label: {
                                        show: false,
                                        formatter: formatter,
                                    },
                                    tooltip: {
                                        formatter: "Reference: {b}",
                                    },
                                });
                            }else {
                                let name = node.name;
                                let start = name.indexOf("(");
                                let end = name.indexOf(")");
                                if(name.length>0&&start!=-1&&end!=-1) {
                                    let part1 = name.substring(0, start).trim();
                                    if(end + 1 == name.length){
                                        name = part1;
                                    }else {
                                        let part2 = name.substring(end + 1, name.length - 1);
                                        name = part1 + " " + part2;
                                    }
                                }
                                let formatter = name.length > 9 ? name.substring(0, 7) + "..." : name;
                                graph_nodes.push({
                                    name: name,
                                    value: {
                                        style: "node",
                                        type: "model",
                                        name: node.name,
                                        oid: node.oid,
                                        img: node.img,
                                        overview: node.overview,
                                    },
                                    x: graph_nodes[0].x + dx,
                                    y: graph_nodes[0].y + dy,
                                    // symbolSize: 10,
                                    itemStyle:{
                                        color: 'orange',
                                    },
                                    label: {
                                        formatter: formatter,
                                    },
                                    tooltip: {
                                        formatter: "{b}",
                                    },
                                });
                            }
                        }

                        //加入连线
                        for(i = 0;i<links.length;i++) {
                            let link = links[i];
                            if(link.type === "ref") {
                                graph_links.push({
                                    source: link.ori,
                                    target: link.tar,
                                    // symbolSize: [5, 10],
                                    label: {
                                        show: false,
                                        formatter: link.relation,
                                        fontSize: 12,
                                    },
                                    lineStyle: {
                                        width: 2,
                                        curveness: 0
                                    },
                                    symbol: ['none', 'none'],
                                    tooltip: {
                                        show: false,
                                        position: 'bottom',
                                        formatter: nodes[link.ori].name + " " + link.relation + " " + nodes[link.tar].name,
                                    },

                                });
                            }else{
                                graph_links.push({
                                    source: link.ori,
                                    target: link.tar,
                                    symbolSize: [5, 10],
                                    label: {
                                        show: true,
                                        formatter: link.relation,
                                        fontSize: 12,
                                    },
                                    lineStyle: {
                                        width: 2,
                                        curveness: 0
                                    },
                                    tooltip: {
                                        position: 'bottom',
                                        formatter: nodes[link.ori].name + " " + link.relation + " " + nodes[link.tar].name,
                                    },

                                });
                            }
                        }

                        let option = {
                            title: {
                                //text: 'Graph 简单示例'
                            },
                            tooltip: {},
                            toolbox: {
                                right:10,
                                feature: {

                                    myFull: {
                                        show: true,
                                        title: 'Full Screen',
                                        icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                        onclick: (e)=>{
                                            let opts = e.getOption();
                                            opts.toolbox[0].feature.myFull={};//.show=false;
                                            // opts.toolbox[0].feature.myFullExit.show=true;
                                            this.graphFullScreen = true;
                                            setTimeout(()=>{
                                                let object = document.getElementById('fullScreenGraph');
                                                let graph = echarts.init(object,'light');
                                                graph.setOption(opts);

                                                graph.on("click",(param)=>{
                                                    if(param.value !== undefined){
                                                        this.curRelation=param.value;
                                                        this.modelRelationGraphSideBarShow = true;
                                                    }

                                                    console.log(param)
                                                });

                                                // opts.toolbox[0].feature.myFull.show=false
                                                // //window.top表示最顶层iframe  如果在当页面全屏打开 删去window.top即可
                                                // window.top.layer.open({
                                                //     title:false,
                                                //     type:1,
                                                //     content:'<div class="fullChart" style="height:100%;width:100%;padding:30px 0px"></div>',
                                                //     success:function(){
                                                //         var fullchart = echarts.init(window.top.document.getElementById('fullChart'))
                                                //         fullchart.setOption(opts)
                                                //     }
                                                // })
                                            },300);

                                        }
                                    },
                                    saveAsImage: {},
                                    restore: {},
                                    // myFullExit: {
                                    //     show: false,
                                    //     title: 'Exit',
                                    //     icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                    //     onclick: (e)=>{
                                    //         this.graphFullScreen = false;
                                    //
                                    //     }
                                    // },
                                }
                            },
                            animation: false,
                            // animationDurationUpdate: 500,
                            // animationEasingUpdate: 'quinticInOut',
                            series: [
                                {
                                    type: 'graph',
                                    layout: 'force',
                                    draggable: false,
                                    focusNodeAdjacency:true,
                                    symbolSize: 25,
                                    zoom:4,
                                    roam: true,
                                    force:{
                                        repulsion:100,
                                        // edgeLength:[150,200],
                                        layoutAnimation:false,
                                    },
                                    label: {
                                        show: true,
                                    },
                                    edgeSymbol: ['circle', 'arrow'],
                                    edgeSymbolSize: [4, 10],
                                    edgeLabel: {
                                        fontSize: 20
                                    },
                                    data: [{
                                        name: '节点1',
                                        x: 500,
                                        y: 300,
                                        symbolSize:50,
                                        itemStyle:{
                                            color: 'blue',
                                        },
                                    }, {
                                        name: '节点2',
                                        x: 800,
                                        y: 300
                                    }, {
                                        name: '节点3',
                                        x: 550,
                                        y: 100
                                    }, {
                                        name: '节点4',
                                        x: 550,
                                        y: 500
                                    }],
                                    // links: [],
                                    links: [{
                                        source: 0,
                                        target: 1,
                                        symbolSize: [5, 20],
                                        label: {
                                            show: true,
                                            formatter:"1234",
                                        },
                                        lineStyle: {
                                            width: 5,
                                            curveness: 0
                                        }
                                    }, {
                                        source: '节点2',
                                        target: '节点1',
                                        label: {
                                            show: true
                                        },
                                        lineStyle: {
                                            curveness: 0.2
                                        }
                                    }, {
                                        source: '节点1',
                                        target: '节点3'
                                    }, {
                                        source: '节点2',
                                        target: '节点3'
                                    }, {
                                        source: '节点2',
                                        target: '节点4'
                                    }, {
                                        source: '节点1',
                                        target: '节点4'
                                    }],
                                    lineStyle: {
                                        opacity: 0.9,
                                        width: 2,
                                        curveness: 0
                                    }
                                }
                            ]
                        };

                        option.series[0].data = graph_nodes;
                        option.series[0].links = graph_links;
                        console.log(option);
                        modelRelationGraph.setOption(option);
                        modelRelationGraph.hideLoading();
                    },300)

                });



            },
        },
        created() {

            this.relateItemType = this.relateType
            this.targetItemType = this.targetType
            this.targetItemId = this.targetId

        },
        mounted() {}
    }
)