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
                    total: 0,
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
                    total: 0,
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
                            url = "/dataItem/list";
                        }else{
                            url = "/dataItem/listByAuthor";
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
                    let id1,id2;
                    id1 = data.id;
                    id2 = row.id;

                    if(id1==id2){
                        return true;
                    }
                }
            },

            handleDelete(index, row) {
                console.log(index, row);
                let table = new Array();
                for (i = 0; i < this.tableData_model.length; i++) {
                    let data = this.tableData_model[i];
                    let id1,id2;
                    id1 = data.id;
                    id2 = row.id;

                    if(id1!=id2) {
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
                    if (tableRow.id == row.id) {
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
                //从地址栏拿到id
                let id = this.targetItemId

                let relateArr = [];
                let url = '';
                let data;
                let contentType;

                if(this.relateItemType !== "modelItem") {
                    url = "/modelItem/setRelation";
                    this.tableData_model.forEach(function (item, index) {
                        relateArr.push(item.id);
                    })
                    data = {
                        id: id,
                        type: this.relateItemType,
                        relations: relateArr
                    };
                    contentType = "application/x-www-form-urlencoded;charset=UTF-8";
                }else{
                    url = "/modelItem/setModelRelation/"+id;
                    this.tableData_model.forEach(function (item, index) {
                        let obj = {
                            id : item.id,
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

        },
        created() {

            this.relateItemType = this.relateType
            this.targetItemType = this.targetType
            this.targetItemId = this.targetId

        },
        mounted() {}
    }
)