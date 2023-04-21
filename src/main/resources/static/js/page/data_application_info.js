var data_application_info = new Vue({
    el: '#data_application_info',
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            activeResource: "Resource",
            lightenContributor:{},
            userId:'',      // 不能删，html页面有用
            viewCount:'',
            activeIndex:'3-3',
            activeName: 'AttributeSet',

            useroid:"",
            userImg:"",

            databrowser:[],
            dataid:'',
            searchcontent:'',
            thisciteurl:'',
            comforcom:false,
            comments:false,
            comforcomtextarea:'',
            mycommentforthedata:'',
            showkey:'',

            allcomments:[],
            thumbs:'',
            thisthumbs:'',

            dialogTableVisible: false,
            relatedResourceVisible:false,
            relateSearch: "",
            relateType: "",
            typeName: "",
            tableMaxHeight: 400,
            tableData: [{
                relation:"Connected with",
            }],
            itemInfo:{},

            dialogVisible:false,
            dialogShowClose:false,
            contentBeforeDeploy:true,
            contentDeploying:false,
            contentAfterDeploy_suc:false,
            contentAfterDeploy_fail:false,
            footerBeforeDeploy:true,
            footerAfterDeploy:false,

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

            graphVisible: 'none',
            loadjson: '',
            mDiagram: null,
            editComputableModelDialog:false,
            modelOid:'',

            methodsData:'',
            userId:'',
            dataApplicationId:'',
            addNewDialog:false,

            htmlJSON:{}
        }
    },
    methods: {
        addRelation(order){

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
                success: (result) => {
                    if (result.code !== 0) {
                        this.confirmLogin();
                    }
                    else {
                        let arr = window.location.href.split("/");
                        let id = arr[arr.length - 1].split("#")[0];
                        let targetType = arr[arr.length - 2];

                        this.dialogTableVisible = true;
                        this.$nextTick(()=>{
                            this.$refs.linkRelatedItemModule.manualInit(id,targetType,"modelItem-D");
                        })
                    }
                }
            })
        },

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
        },

        translatePage(jsonContent){
            this.htmlJSON = jsonContent
        },

        submitComment(){
            if(this.useroid==""||this.useroid==null||this.useroid==undefined){
                this.$message({
                    dangerouslyUseHTMLString: true,
                    message: '<strong>'+ this.htmlJSON.Please +' <a href="/user/login">'+this.htmlJSON.login+'</a></strong>',
                    offset: 40,
                    showClose: true,
                });
            }else if(this.commentText.trim()==""){
                this.$message({
                    message: this.htmlJSON.CommentCanNotBeEmpty,
                    offset: 40,
                    showClose: true,
                });
            }else {

                let hrefs = window.location.href.split("/");
                let id = hrefs[hrefs.length - 1].substring(0, 36);
                let typeName = hrefs[hrefs.length-2];
                let data = {
                    parentId: this.commentParentId,
                    content: this.commentText,
                    // authorId: this.useroid,
                    replyToUserId: this.replyToUserId,
                    relateItemId: id,
                    relateItemTypeName: typeName,
                };
                $.ajax({
                    url: "/comment/add",
                    async: true,
                    type: "POST",
                    contentType: 'application/json',

                    data: JSON.stringify(data),
                    success: (result) => {
                        console.log(result)
                        if(result.code==-1){
                            window.location.href="/user/login"
                        }else if (result.code == 0) {
                            this.commentText = "";
                            this.$message({
                                message: this.htmlJSON.CommentSubmittedSuccessfully,
                                type: 'success',
                                offset: 40,
                                showClose: true,
                            });
                            // this.getComments();
                        } else {
                            this.$message({
                                message: this.htmlJSON.SubmitError,
                                type: 'error',
                                offset: 40,
                                showClose: true,
                            });
                        }
                    }
                });
            }

        },
        deleteComment(oid){
            $.ajax({
                url: "/comment/delete",
                async: true,
                type: "POST",


                data: {
                    oid:oid,
                },
                success: (result) => {
                    console.log(result)
                    if(result.code==-1){
                        window.location.href="/user/login"
                    }else if (result.code == 0) {
                        this.commentText = "";
                        this.$message({
                            message: this.htmlJSON.CommentDeletedSuccessfully,
                            type: 'success',
                            offset: 40,
                            showClose: true,
                        });
                        // this.getComments();
                    } else {
                        this.$message({
                            message: this.htmlJSON.DeleteError,
                            type: 'error',
                            offset: 40,
                            showClose: true,
                        });
                    }
                }
            });
        },
        getComments(){
            let hrefs=window.location.href.split("/");
            let type=hrefs[hrefs.length-2];
            let oid=hrefs[hrefs.length-1].substring(0,36);
            let data={
                type:type,
                id:oid,
                sort:-1,
            };
            $.get("/comment/commentsByTypeAndId",data,(result)=>{
                this.commentList=result.data.commentList;
            })
        },
        replyComment(comment){
            this.commentParentId=comment.oid;
            this.replyTo=this.htmlJSON.ReplyTo+comment.author.name;
            setTimeout(function () { $("#commentTextArea").focus();}, 1);
        },
        replySubComment(comment,subComment){
            this.commentParentId=comment.oid;
            this.replyToUserId=subComment.author.oid;
            // this.commentTextAreaPlaceHolder="Reply to "+subComment.author.name;
            this.replyTo=this.htmlJSON.ReplyTo+subComment.author.name;
            setTimeout(function () { $("#commentTextArea").focus();}, 1);
        },
        tagClose(){
            this.replyTo="";
            this.replyToUserId="";
            this.commentParentId=null;
        },

        confirmLogin(){
            window.sessionStorage.setItem("history", window.location.href);
            const language = window.localStorage.getItem("language");

            if (language !== "zh-cn"){
                var loginTip = "This function requires an account, please login first."
                var login = "Log in"
                var tip = "Tip"
            }else {
                var loginTip = "该操作需要一个账户，请先登录"
                var login = "登录"
                var tip = "提示"
            }

            this.$confirm('<div style=\'font-size: 18px\'>' + loginTip + '</div>', tip, {
                dangerouslyUseHTMLString: true,
                confirmButtonText: login,
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

        addRelatedResouece(){
            this.relateType = 'concept'
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
                    if (data.code !== 0) {
                        this.confirmLogin()
                    }
                    else {
                        this.activeName_dialog = 'all'

                        this.tableData = [];

                        this.pageOption_my.currentPage = 1;
                        this.pageOption_my.searchResult = [];
                        this.pageOption_my.relateSearch = "";

                        this.pageOption_all.currentPage = 1;
                        this.pageOption_all.searchResult = [];
                        this.pageOption_all.relateSearch = "";

                        this.getRelatedResources();
                        this.search(this.activeName_dialog);
                        if(this.activeName_dialog!="all"){
                            this.search("all");
                        }
                        this.relatedResourceVisible = true;
                    }
                }
            })

        },

        edit(){
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
                success: (result) => {
                    // data = JSON.parse(data);
                    if (result.code !== 0) {
                        const language = window.localStorage.getItem("language");

                        if (language !== "zh-cn"){
                            var loginTip = "This function requires an account, please login first."
                            var login = "Log in"
                            var tip = "Tip"
                        }else {
                            var loginTip = "该操作需要一个账户，请先登录"
                            var login = "登录"
                            var tip = "提示"
                        }

                        this.$confirm('<div style=\'font-size: 18px\'>' + loginTip + '</div>', tip, {
                            dangerouslyUseHTMLString: true,
                            confirmButtonText: login,
                            cancelButtonClass: 'fontsize-15',
                            confirmButtonClass: 'fontsize-15',
                            type: 'info',
                            center: true,
                            showClose: false,
                        }).then(() => {
                            window.location.href = "/user/login";
                        }).catch(() => {

                        });
                    }
                    else {
                        let href=window.location.href;
                        let hrefs=href.split('/');
                        let oid=hrefs[hrefs.length-1].split("#")[0];
                        window.location.href = '/user/userSpace#/data/manageDataApplication/'+oid;

                    }
                }
            })
        },


        handleDownload(index,row){
            // console.log(index,row);
        },
        handleShare(index,row){

        },
        getImg(item){
            return "/static/img/filebrowser/"+item.suffix+".svg"
        },

        generateId(key){
            return key;
        },
        getid(eval){
            alert(eval);
            this.dataid=eval;


        },

        getRelatedResources() {
            //从地址栏拿到oid
            let arr = window.location.href.split("/");
            let id = arr[arr.length - 1].split("#")[0];
            let data = {
                id: id,
            };
            $.ajax({
                type: "GET",
                url: "/dataItem/relatedResources/" + id,
                // data: data,
                async: true,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        console.log(data)

                        this.tableData = data;

                    }else  if (json.code == -1) {
                        this.confirmLogin()
                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },

        search(scope) {
            let data;
            if(scope=="all"){
                // this.pageOption_all.currentPage = 1;
                data = {
                    asc: this.pageOption_all.sortAsc,
                    page: this.pageOption_all.currentPage,
                    pageSize: this.pageOption_all.pageSize,
                    searchText: this.pageOption_all.relateSearch.trim(),
                    sortField: this.pageOption_all.sortField,
                }
            }else {
                // this.pageOption_my.currentPage = 1;
                data = {
                    asc: this.pageOption_my.sortAsc,
                    page: this.pageOption_my.currentPage,
                    pageSize: this.pageOption_my.pageSize,
                    searchText: this.pageOption_my.relateSearch,
                    sortField: this.pageOption_my.sortField,
                };
            }
            let url, contentType;

            if (this.relateType == "exLink" || this.relateType == "dataSpaceFile"){
                return;
            }


            if(scope=="all") {
                url = "/" + this.relateType + "/list";
            }else{
                url = "/" + this.relateType + "/listByAuthor";
            }

            axios.post(url, data).then(res => {
                let json = res.data;
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

            });
        },


        share(){
            let item =this.databrowser[this.dataid]

            if(item!=null){
                let url ="/dataItem/downloadRemote?fileName="+item.fileName+"&sourceStoreId="+item.sourceStoreId+"&suffix="+item.suffix;
                this.$alert("<input style='width: 100%' value="+url+">",{
                    dangerouslyUseHTMLString: true
                })
                // this.dataid='';

            }else {
                // console.log("从后台获取数据条目数组有误")
                this.$message(this.htmlJSON.PleaseSelectFileFirst);
            }
        },
        //批量下载还有问题，待修改
        dall(){


            let locaurl=window.location.href;
            let url =locaurl.split("/");
            // console.log(url[url.length-1]);

            let downloadallzipurl="http://localhost:8081/dataResource/downloadAll/"+url[url.length-1];

            let link =document.createElement("a");
            link.style.display='none';
            link.href=downloadallzipurl;
            link.setAttribute("download","alldata.zip");

            document.body.appendChild(link);
            link.click();

        },
        showtitle(ev){
            return ev.fileName+"\n"+"Type:"+ev.suffix;
        },
        downloaddata(){
            let item =this.databrowser[this.dataid];

            if(item!=null){
                let url ="/dataItem/downloadRemote?fileName="+item.fileName+"&sourceStoreId="+item.sourceStoreId+"&suffix="+item.suffix;
                let link =document.createElement('a');
                link.style.display='none';
                link.href=url;
                link.setAttribute(item.fileName,'filename.'+item.suffix)

                document.body.appendChild(link)
                link.click();

            }else {
                this.$message(this.htmlJSON.PleaseSelectFileFirst);
            }


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
            for(let i=0;i<this.tableData.length;i++){
                let data = this.tableData[i];
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

        searchInit(scope){
            this.pageOption_all.currentPage = 1;
            this.pageOption_my.currentPage = 1;
            this.search(scope);
        },

        //relate search
        search(scope) {
            let data;
            if(scope=="all"){
                // this.pageOption_all.currentPage = 1;
                data = {
                    asc: this.pageOption_all.sortAsc,
                    page: this.pageOption_all.currentPage,
                    pageSize: this.pageOption_all.pageSize,
                    searchText: this.pageOption_all.relateSearch.trim(),
                    sortField: this.pageOption_all.sortField,
                }
            }else {
                // this.pageOption_my.currentPage = 1;
                data = {
                    asc: this.pageOption_my.sortAsc,
                    page: this.pageOption_my.currentPage,
                    pageSize: this.pageOption_my.pageSize,
                    searchText: this.pageOption_my.relateSearch,
                    sortField: this.pageOption_my.sortField,
                };
            }
            let url, contentType;

            if (this.relateType == "exLink" || this.relateType == "dataSpaceFile"){
                return;
            }


            if(scope=="all") {
                url = "/" + this.relateType + "/list";
            }else{
                url = "/" + this.relateType + "/listByAuthor";
            }

            axios.post(url, data).then(res => {
                let json = res.data;
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

            });
        },

        changeRelateType(activeType){
            this.relateType = activeType
            this.searchInit(this.activeName_dialog)
        },

        isCurrentItem(row){
            let urls = window.location.href.split('/');
            let oid = urls[urls.length-1].substring(0,36);
            return(oid==row.oid);
        },

        handlePageChange(val) {
            if(this.activeName_dialog=="my") {
                this.pageOption_my.currentPage = val;
            }else{
                this.pageOption_all.currentPage = val;
            }
            this.search(this.activeName_dialog);
        },

        handleEdit(index, row) {
            console.log(row);
            row.type=this.relateType
            let flag = false;
            for (i = 0; i < this.tableData.length; i++) {
                let tableRow = this.tableData[i];
                if (tableRow.id == row.id) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if(this.relateType=="modelItem"){
                    this.$set(row,"relation","Connected with");
                }
                this.tableData.push(row);
            }
        },

        addRelateResources(){
            let relationItem = {
                concepts: [],
                spatialReferences: [],
                templates: [],
                units: [],
                id: null
            };
            // let stringInfo = []
            this.tableData.forEach(function (item, index) {
                // let relateItem={}
                let relationType = item.type

                if (relationType === 'concept'){
                    relationItem.concepts.push(item.id)
                }else if (relationType === 'spatialReference'){
                    relationItem.spatialReferences.push(item.id)
                }else if (relationType === 'templates'){
                    relationItem.templates.push(item.id)
                }else if (relationType === 'units'){
                    relationItem.units.push(item.id)
                }
                // relationItem.id = item.id
                // // relationItem.type = item.type
                // if(item.type=='localFile'){
                //     formData.append("resources",item.raw);
                // }
                // if(item.type=='exLink'){
                //     relateItem.content = item.content
                //     relateItem.name = item.name
                // }
                // if(item.type=='dataSpaceFile'){
                //     // relateItem.oid = item.oid
                //     relateItem.url = item.url
                //     relateItem.name = item.name
                // }
                // stringInfo.push(relateItem);
            })

            // let file = new File([JSON.stringify(stringInfo)],'ant.txt',{
            //     type: 'text/plain',
            // });
            // formData.append("stringInfo", JSON.stringify(stringInfo))


            let arr = window.location.href.split("/");
            let id = arr[arr.length - 1].split("#")[0];
            relationItem.id = id

            // let url = '';
            let contentType = "application/json";

            $.ajax({
                type: "POST",
                url: "/dataItem/knowledge",
                data: JSON.stringify(relationItem),
                cache: false,
                // processData: false,
                contentType: contentType,
                async: true,
                success: (result) => {
                    let info = result.msg
                    if(info === 'Success'){
                        this.$alert('Success!', 'Tip', {
                            type:'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.dialogTableVisible = false;
                                window.location.reload();
                            }
                        });
                    }else if(info === 'There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.'){
                        this.$alert("There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.", 'Success', {
                            type: 'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.reload();
                            }
                        })
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

        getTypeExpress(row){
            switch (row.type){
                case "concept":
                    return 'Concept & Semantic'
                case "spatialReference":
                    return 'Spatiotemporal Reference'
                case "template":
                    return 'Data Template'
                case "unit":
                    return 'Unit & Metric'
                case "dataSpaceFile":
                    return 'File'
            }
        },

        handleDelete(index, row) {
            this.tableData.splice(index, 1);
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
                    if (data.code !== 0) {
                        this.confirmLogin()
                    }
                    else {
                        let arr = window.location.href.split("/");
                        let bindOid = arr[arr.length - 1].split("#")[0];
                        this.setSession("bindOid", bindOid);
                        switch (this.relateType) {
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
                            case "concept":
                                window.open("/user/userSpace#/community/createConcept", "_blank")
                                break;
                            case "spatialReference":
                                window.open("/user/userSpace#/community/createSpatialReference", "_blank")
                                break;
                            case "template":
                                window.open("/user/userSpace#/community/createTemplate", "_blank")
                                break;
                            case "unit":
                                window.open("/user/userSpace#/community/createUnit", "_blank")
                                break;
                        }
                        this.dialogTableVisible = false;
                    }
                }
            })
        },

        getTypeImg(row){
            switch (row.type){
                case "concept":
                    return '../../static/img/model/semantics.png'
                    break;
                case "spatialReference":
                    return '../../static/img/model/spatialreference.png'
                case "template":
                    return '../../static/img/model/template.png'
                case "unit":
                    return '../../static/img/model/unit.png'
            }

        },

        showsearchresult(data){

            //动态创建DOM节点

            for(let i=0;i<this.databrowser.length;i++){
                //匹配查询字段
                if(this.databrowser[i].fileName.toLowerCase().indexOf(data.toLowerCase())>-1){
                    //插入查找到的card

                    //card
                    let searchresultcard=document.createElement("div");
                    searchresultcard.classList.add("el-card");
                    searchresultcard.classList.add("dataitemisol");
                    searchresultcard.classList.add("is-never-shadow");
                    searchresultcard.classList.add("sresult");


                    //cardbody
                    let secardbody=document.createElement("div");
                    secardbody.classList.add("el-card__body");
                    //card里添加cardbody
                    searchresultcard.appendChild(secardbody);

                    //el-row1
                    let cardrow1=document.createElement("div");
                    cardrow1.classList.add("el-row");
                    secardbody.appendChild(cardrow1);

                    //3个div1
                    //div1
                    let div1=document.createElement("div");
                    div1.classList.add("el-col");
                    div1.classList.add("el-col-6");

                    let text1=document.createTextNode(" ");
                    div1.appendChild(text1);

                    cardrow1.appendChild(div1)

                    //div2
                    let div2=document.createElement("div");
                    div2.classList.add("el-col");
                    div2.classList.add("el-col-12");

                    let img=document.createElement("img");
                    img.src="/static/img/filebrowser/"+this.databrowser[i].suffix+".svg";

                    img.style.height='60%';
                    img.style.width='100%';
                    img.style.marginLeft='30%';

                    div2.appendChild(img);

                    cardrow1.appendChild(div2)

                    //div3
                    let div3=document.createElement("div");
                    div3.classList.add("el-col");
                    div3.classList.add("el-col-6");

                    let text2=document.createTextNode(" ");
                    div3.appendChild(text2);

                    cardrow1.appendChild(div3);


                    //el-row2
                    let cardrow2=document.createElement("div");
                    cardrow2.classList.add("el-row");
                    secardbody.appendChild(cardrow2);

                    //3个div2
                    //div4
                    let div4=document.createElement("div");
                    div4.classList.add("el-col");
                    div4.classList.add("el-col-2");

                    let text3=document.createTextNode(" ");
                    div4.appendChild(text3);

                    cardrow2.appendChild(div4)

                    //div5
                    let div5=document.createElement("div");
                    div5.classList.add("el-col");
                    div5.classList.add("el-col-20");

                    let p=document.createElement("p");
                    div5.appendChild(p);

                    let filenameandtype=document.createTextNode(this.databrowser[i].fileName+'.'+this.databrowser[i].suffix);
                    p.appendChild(filenameandtype)

                    cardrow2.appendChild(div5)

                    //div6
                    let div6=document.createElement("div");
                    div6.classList.add("el-col");
                    div6.classList.add("el-col-20");

                    let text4=document.createTextNode(" ");
                    div6.appendChild(text4);

                    cardrow2.appendChild(div6)

                    //往contents里添加card
                    document.getElementById("browsercont").appendChild(searchresultcard);

                    searchresultcard.click(function () {
                        this.dataid=i;
                    })

                }
            }
        },

        getApplication(){
            let str = window.location.href.split('/')
            let oid = str[str.length-1];
            let that = this
            axios.get('/dataMethod/method/' + oid).then((res) => {
                if(res.status === 200) {
                    that.methodsData = res.data.data.invokeServices;
                    that.viewCount = res.data.data.viewCount
                }
            }).catch(function (err) {console.log(err)})
        },
        gotoTask(event){
            let refLink=$(".invokeBtn");
            for(let i=0;i<refLink.length;i++){
                if(event.currentTarget===refLink[i]){
                    console.log(this.methodsData[i].serviceId);
                    //根据当前节点在线状态链接页面
                    if (this.methodsData[i].onlineStatus === 'offline'){
                        this.$message({
                            message: this.htmlJSON.ServiceOffline,
                            type: 'error',
                        })
                    }else {
                        window.location.href = "/dataMethod/task/" + this.dataApplicationId + '/'
                            + this.methodsData[i].serviceId + '/' + encodeURIComponent(encodeURIComponent(this.methodsData[i].token));
                    }
                }
            }
        },


        // 表格
        createService(){
            if(this.useroid==""||this.useroid==null||this.useroid==undefined){
                this.$message({
                    dangerouslyUseHTMLString: true,
                    message: '<strong>'+ this.htmlJSON.Please +' <a href="/user/login">'+this.htmlJSON.login+'</a></strong>',
                    offset: 40,
                    showClose: true,
                });
            }else {
                window.location.href = "/user/userSpace#/data/createDataApplication";
            }
        }
    },
    mounted(){
        console.log(11)
        this.lightenContributor = author
        this.$refs.mainContributorAvatar.insertAvatar(this.lightenContributor.avatar)
        this.$refs.mainContributorAvatar1.insertAvatar(this.lightenContributor.avatar)


        let str = window.location.href.split('/');
        this.dataApplicationId = str[str.length-1].split("?")[0];

        // this.setSession("history", window.location.href);
        axios.get("/user/load")
            .then((res) => {
                if (res.status == 200) {
                    if (res.data.code === 0) {
                        this.useroid = res.data.data.accessId;
                        this.userImg = res.data.data.avatar;
                    }

                }
            })
        this.getComments()
        this.getApplication()

        $(document).on('mouseover mouseout','.flexRowSpaceBetween',function(e){

            let deleteBtn=$(e.currentTarget).children().eq(1).children(".delete");
            if(deleteBtn.css("display")=="none"){
                deleteBtn.css("display","block");
            }else{
                deleteBtn.css("display","none");
            }

        });

        let qrcodes = document.getElementsByClassName("qrcode");
        for(let i=0;i<qrcodes.length;i++) {
            new QRCode(document.getElementsByClassName("qrcode")[i], {
                text: window.location.href,
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        }

        // $(".ab").click(function () {
        //
        //         if (!$(this).hasClass('transform180'))
        //             $(this).addClass('transform180')
        //         else
        //             $(this).removeClass('transform180')
        //     }
        // );

        // diagram = new OGMSDiagram();
        // diagram.init($('#mxGraphContainer'),
        //     {
        //         width: 1000,       //! Width of panel
        //         height: '100%',       //! Height of panel
        //         // height: 1000,       //! Height of panel
        //         enabled: false      //! Edit enabled
        //     },
        //     {
        //         x: 500,            //! X postion of state information window
        //         y: $("#mxGraphContainer").offset().top - $(window).scrollTop() ,              //! Y postion of state information window
        //         width: 520,         //! Width of state information window
        //         height: 650         //! Height of state information window
        //     },
        //     {
        //         x: 1000,           //! X postion of data reference information window
        //         y: $("#mxGraphContainer").offset().top - $(window).scrollTop(),              //! Y postion of data reference information window
        //         width: 300,         //! Width of data reference information window
        //         height: 400         //! Height of data reference information window
        //     },
        //     '/static/js/mxGraph/images/modelState.png',    //! state IMG
        //     '/static/js/mxGraph/images/grid.gif',          //! Grid IMG
        //     '/static/js/mxGraph/images/connector.gif',     //! Connection center IMG
        //     false                       //! Debug button
        // );
        //
        // console.log(Behavior)
        //
        // var behavior={};
        //
        // if (Behavior.StateGroup[0].States== '') {
        //     behavior.states = [];
        // }
        // else {
        //     behavior.states = Behavior.StateGroup[0].States[0].State;
        // }
        //
        // if (Behavior.StateGroup[0].StateTransitions == "") {
        //     behavior.transition = [];
        // }
        // else {
        //     behavior.transition = Behavior.StateGroup[0].StateTransitions[0].Add;
        // }
        //
        // if (Behavior.RelatedDatasets == "") {
        //     behavior.dataRef = [];
        // }
        // else {
        //     behavior.dataRef = Behavior.RelatedDatasets[0].DatasetItem;
        // }
        //
        // //console.log(behavior)
        // this.loadjson=JSON.stringify(behavior).replace(new RegExp("\"Event\":","gm"), "\"events\":");
        // console.log(JSON.parse(this.loadjson));
        // diagram.loadJSON(this.loadjson);
        //
        // diagram.onStatedbClick(function(state){
        //     diagram.showStateWin({
        //         x : 900,
        //         y : $(window).scrollTop() + 80,
        //     },{
        //         width : 520,
        //         height : 640
        //     });
        //
        // });
    }
})
