

var  data_item_info = new Vue({
    el: '#data_item_info',
    // props:['htmlJSON'],
    components: {
        'avatar': VueAvatar.Avatar
    },
    data:function () {
        return{
            activeResource: "Resource",
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

            itemInfo:{},

            lightenContributor:{},
            activeIndex:'2-2',
            activeName: 'Conceptual Model',

            databrowser:[],
            dataid:'',
            searchcontent:'',
            thisciteurl:'',
            comforcom:false,
            comments:false,
            comforcomtextarea:'',
            mycommentforthedata:'',
            showkey:'',
            loading:false,

            userImg:"",

            allcomments:[],
            thumbs:'',
            thisthumbs:'',
            userName:'',
            useroid:'',
            userUid:'',
            userId:"",
            dataCategory:[],
            viewCount:'',
            related3Models:[],
            currentDataId:'',

            value1:'1',
            relatedModelNotNull:false,
            relatedModelIsNull:false,
            searchRelatedModelsDialogVisible:false,
            addRelatedModelsDialogVisible:false,
            allRelatedModels:[],
            dataNums:5,
            timer:false,
            nomore:"",
            nomoreflag:false,
            itemType: '',

            relatedModelsSearchText:'',
            addModelsSearchText:'',
            searchAddRelatedModels:[],
            searchAddModelPage:0,

            selectedModels:[],
            selectedModelsOid:[],

            //Relation 弹出框
            dialogTableVisible:false,
            relateSearch:"",
            relateType:"",
            relateTitle:"",
            tableMaxHeight:400,
            tableData: [],

            pageOption: {
                paginationShow:false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,

                total: 0,
                searchResult: [],
            },

            currentData:"",

            forkDialogVisible:false,
            id:1000,
            folderTree : [{
                id: 1,
                label: 'All Folder',
                children: [{
                    id: 4,
                    label: '二级 1-1',
                    children: [{
                        id: 9,
                        label: '三级 1-1-1'
                    }, {
                        id: 10,
                        label: '三级 1-1-2'
                    }]
                }]
            }],
            activeNameProcessing: 'Related Processing',
            activeNameVisual: 'Related Visualization',
            input:'',
            dialogVisible: false,
            processingId:"",
            xml:"",
            parameters:[],
            loading: false,
            loading2:false,
            loading3:false,
            processingType:"",
            dataItemId:"",
            objProcess:{
                msg:"",
                dataId:"",
                pcsId:"",
                params:[],
                name:"",
                token:"",
                reqUsrOid:"",
            },
            objDistributed:{},

            authorshipFormVisible:false,
            invokeServices:[],

            htmlJSON:{},
            currentLng:"nameEn" //用户控制分类的中英文切换 nameCn/nameEn
        }

    } ,
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
        },

        translatePage(jsonContent){
            this.htmlJSON = jsonContent;
            this.changeCLSLng();
        },

        //修改分类的中英文展示标识
        changeCLSLng(){
            let language = window.localStorage.getItem("language");
            if (language == "zh-cn"){
                this.currentLng = "nameCn";
            } else {
                this.currentLng = "nameEn";
            }
        },

        feedBack(){
            $.get("/user/load",{},(result)=>{

                if (result.code !== 0) {
                    this.confirmLogin();
                }
                else {
                    window.location.href = "/user/userSpace#/feedback"
                }
            })
        },

        confirmLogin(){
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

        forkData(){
            if(dataSelection.length!=0){
                axios.get("/user/getFolder",{})
                    .then(res=> {
                        let json=res.data;
                        if(json.code==-1){
                            this.confirmLogin();
                        }
                        else {
                            this.folderTree=res.data.data;
                            this.forkDialogVisible=true;
                        }


                    });

            }
            else{
                alert(this.htmlJSON.PleaseSelectDataFirst)
            }
        },
        addFolder(){
            let data=this.$refs.folderTree.getCurrentNode();
            let node=this.$refs.folderTree.getNode(data);
            console.log(node);
            let paths=[];
            while(node.key!=undefined&&node.key!=0){
                paths.push(node.key);
                node=node.parent;
            }
            console.log(paths)

            this.$prompt(null, this.htmlJSON.EnterFolderName, {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                // inputErrorMessage: '邮箱格式不正确'
            }).then(({ value }) => {

                $.ajax({
                    type: "POST",
                    url: "/user/addFolder",
                    data: {paths: paths, name: value},
                    async: true,
                    contentType: "application/x-www-form-urlencoded",
                    success: (json) => {
                        if (json.code == -1) {
                            this.confirmLogin();
                        }
                        else {
                            const newChild = {id: json.data, label: value, children: []};
                            if (!data.children) {
                                this.$set(data, 'children', []);
                            }
                            data.children.push(newChild);
                        }

                    }
                });


            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: this.htmlJSON.Cancel
                });
            });
        },
        confirmFolder(){
            let data=this.$refs.folderTree.getCurrentNode();
            let node=this.$refs.folderTree.getNode(data);

            let paths=[];
            while(node.key!=undefined&&node.key!=0){
                paths.push(node.key);
                node=node.parent;
            }
            paths=paths.length==0?["root"]:paths;

            let urls=window.location.href.split("/");
            let itemId=urls[urls.length-1].trim();

            console.log(itemId)
            $.ajax({
                type: "POST",
                url: "/user/forkData",
                data: {paths: paths, itemId:itemId, dataIds:dataSelection},
                async: true,
                contentType: "application/x-www-form-urlencoded",
                success: (json) => {
                    if (json.code == -1) {
                        this.confirmLogin();
                    }
                    else {
                        alert(this.htmlJSON.ForkSuccessfully);
                        this.forkDialogVisible=false;
                    }

                }
            });
        },
        append(data) {

            this.$prompt(null, this.htmlJSON.EnterFolderName, {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                // inputErrorMessage: '邮箱格式不正确'
            }).then(({ value }) => {
                const newChild = { id: this.id++, label: value, children: [] };
                if (!data.children) {
                    this.$set(data, 'children', []);
                }
                data.children.push(newChild);
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: this.htmlJSON.Cancel
                });
            });

        },
        remove(node, data) {
            const parent = node.parent;
            const children = parent.data.children || parent.data;
            const index = children.findIndex(d => d.id === data.id);
            children.splice(index, 1);
        },
        getUrlParam(url,name) {

            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");

            var r = window.location.search.substr(1).match(reg);

            if(r!=null)return  unescape(r[2]); return null;

        },
        //Relation 相关
        getRelation(){
            //从地址栏拿到oid
            let arr=window.location.href.split("/");
            let id=arr[arr.length-1].split("#")[0];
            let data = {
                id:id,
            };
            $.ajax({
                type: "GET",
                url: `/${this.itemType}/relation`,
                data: data,
                async: true,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        console.log(data)

                        this.tableData=data;

                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },
        handlePageChange(val) {

            this.pageOption.currentPage = val;

            this.search();
        },
        handleDelete(index,row){
            console.log(index,row);
            let table=new Array();
            for(i=0;i<this.tableData.length;i++){
                table.push(this.tableData[i]);
            }
            table.splice(index,1);
            this.tableData=table;

        },
        handleEdit(index,row){
            console.log(row);
            let flag=false;
            for(i=0;i<this.tableData.length;i++){
                let tableRow=this.tableData[i];
                if(tableRow.oid==row.oid){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                this.tableData.push(row);
            }
        },
        confirm(){
            //从地址栏拿到oid
            let arr=window.location.href.split("/");
            let id=arr[arr.length-1].split("#")[0];

            let relateArr=[];
            this.tableData.forEach(function(item,index){
                relateArr.push(item.oid);
            })
            if(relateArr.length==0){
                relateArr.push(null);
            }

            var data = {
                id:id,
                relations:relateArr
            };

            $.ajax({
                type: "POST",
                url: "/dataItem/setRelation",
                data: data,
                async: true,
                success: (json) => {
                    this.$alert(this.htmlJSON.Success, 'Tip', {
                    type:'success',
                    confirmButtonText: 'OK',
                    callback: action => {
                        this.dialogTableVisible = false;
                        window.location.reload();
                    }
                });
                },
                error:(json)=>{
                    this.$alert(this.htmlJSON.failed, 'Error', {
                    type:'error',
                        confirmButtonText: 'OK',
                        callback: action => {

                    }
                });
                }
            })
        },
        handleClose(done) {
            this.$confirm(this.htmlJSON.AreYouSureToClose)
                .then(_ => {
                    done();
                })
                .catch(_ => {});
        },
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
        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },
        jump(){
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields:{
                    withCredentials: true
                },
                crossDomain:true,
                success: (result) => {
                    if (result.code !== 0) {
                        this.confirmLogin();
                    }
                    else{
                        // let arr=window.location.href.split("/");
                        // let bindOid=arr[arr.length-1].split("#")[0];
                        // this.setSession("bindOid",bindOid);

                        window.open("/user/userSpace", "_blank")

                    }
                }
            })
        },
        search(){
            var data = {
                asc: this.pageOption.sortAsc,
                page: this.pageOption.currentPage,
                pageSize: this.pageOption.pageSize,
                searchText: this.relateSearch,
                sortType:"default",
            };
            let url;

            url="/modelItem/listByAuthor";

            axios.post(url, data).then(res => {
                let json = res.data;
                if (json.code == 0) {
                    let data = json.data;
                    console.log(data)

                    this.pageOption.total = data.total;
                    this.pageOption.pages = data.pages;
                    this.pageOption.searchResult = data.list;
                    this.pageOption.users = data.users;
                    this.pageOption.progressBar = false;
                    this.pageOption.paginationShow=true;

                }
                else {
                    console.log("query error!")
                }
            })
        },
        //end relation
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
        getid(event){
            console.log(eval)
            this.dataid=eval;
        },
        share(){


            if(currentData!=''){
                let url ="/dispatchRequest/download?url=" + currentData;
                this.$alert("<input style='width: 100%' value="+'https://geomodeling.njnu.edu.cn'+url+">",{
                    dangerouslyUseHTMLString: true
                })
                // this.dataid='';

            }else {
                // console.log("从后台获取数据条目数组有误")
                this.$message(this.htmlJSON.PleaseSelectFileFirst);
            }
        },
        dall(){

            let downloadAllZipUrl="/dataManager/downloadSomeRemote"

            let data=$(".dataitemisol");
            console.log($(".dataitemisol").get(1));
            for(i=0;i<data.length;i++){
                let url=data.eq(0).attr('id');
                let id=this.getUrlParam(url,"sourceStoreId");

                downloadAllZipUrl+=i==0?'?':'&';
                downloadAllZipUrl+=id;

            }


            let link =document.createElement("a");
            link.style.display='none';
            link.href=downloadAllZipUrl;
            link.setAttribute("download","allData.zip");

            document.body.appendChild(link);
            link.click();

        },
        showtitle(ev){
            return ev.fileName+"\n"+"Type:"+ev.suffix;
        },
        downloaddata(){
            if(currentData!='') {

                window.open("/dispatchRequest/download?url=" + currentData);
            }
            else{
                this.$message(this.htmlJSON.PleaseSelectFileFirst);
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

                    //DOM2级事件绑定

                    // searchresultcard.addEventListener('click',()=>{
                    //    //点击赋值id
                    //     this.dataid=i;
                    // });
                    searchresultcard.click(function () {
                        this.dataid=i;
                    })

                }
            }
        },
        showmycomment(key){
            if(this.comforcom===key){
                this.comforcom=''
                this.comforcomtextarea=''
            }else {
                this.comforcom=key
            }

        },
        showcomment(key){
            if(this.comments===key){
                this.comments=''
            }else {


                this.comments=key;
            }



        },


        //对评论的评论
        reply(id){

            if(this.useroid==''){
                alert(this.htmlJSON.LoginInFirst);
                window.location.href = "/user/login";
            }else{
                var curid=window.location.href;
                var theid=curid.split("/");
                var replycomment={
                    author:this.userName,
                    comment:this.comforcomtextarea,
                    date:new Date()

                }

                var commentsAddDTO={
                    "id":theid[theid.length-1],
                    "commentid":id,
                    "commentsForComment":replycomment
                }

                var that=this;
                axios.post("/dataItem/reply",commentsAddDTO)
                    .then(res=>{
                        if(res.status == 200){
                            that.$notify({
                                title: '评论成功',
                                message: '成功对此条评论发表了自己的看法',
                                type: 'success'
                            });
                            // console.log(that.allcomments)
                            var currenturl=window.location.href;
                            var dataitemid=currenturl.split("/");
                            axios.get("/dataItem/getcomment/"+dataitemid[dataitemid.length-1])
                                .then(res=>{
                                    // console.log("red"+res)
                                    that.allcomments=res.data.data.comments;

                                    // console.log("after"+that.allcomments)

                                })
                        }
                    });

            }

        },
        //对数据条目的评论
        putcomment(){
            if(this.useroid==''){
                alert(this.htmlJSON.LoginInFirst);
                window.location.href = "/user/login";
            }else{
                var curid=window.location.href;
                var theid=curid.split("/");
                var nowDate=new Date();
                if(this.allcomments)
                    var commentsAddDTO={
                        "id":theid[theid.length-1],
                        "myComment":{
                            "id":this.allcomments.length,
                            "comment":this.mycommentforthedata,
                            "thumbsUpNumber":0,
                            "commentDate":nowDate,
                            "author":this.userName,
                        }
                    };
                var that=this;
                axios.post("/dataItem/putcomment",commentsAddDTO)
                    .then(res=>{
                        if(res.status == 200){
                            that.$notify({
                                title: '评论成功',
                                message: '成功对本数据项进行了评论',
                                type: 'success'
                            });
                            // console.log(that.allcomments)
                            var currenturl=window.location.href;
                            var dataitemid=currenturl.split("/");
                            axios.get("/dataItem/getcomment/"+dataitemid[dataitemid.length-1])
                                .then(res=>{
                                    // console.log("red"+res)
                                    that.allcomments=res.data.data.comments;

                                    // console.log("after"+that.allcomments)

                                })
                        }
                    });
            }



        },
        //点赞
        thumbsup(key){
            this.thisthumbs=key;

            var curid=window.location.href;
            var theid=curid.split("/");
            var commentsUpdateDTO={
                "dataId":theid[theid.length-1],
                "commentId":key
            }

            var that=this;
            axios.post("/dataItem/thumbsup",commentsUpdateDTO)
                .then(res=>{
                    if(res.status == 200){
                        that.$notify({
                            title: '感谢点赞',
                            message: '成功对本数据项进行了评论',
                            type: 'success'
                        });
                        // console.log(res)
                        that.thumbs=res.data;

                    }
                });




        },
        //格式化评论时间
        formatDate(date){
            var dateee=new Date(date).toJSON();
            var da = new Date(+new Date(dateee)+8*3600*1000).toISOString().replace(/T/g,' ').replace(/\.[\d]{3}Z/,'')
            return da
        },
        getcommentlength(arr){
            if(arr==null){
                return 0
            }else {
                return arr.length;
            }
        },
        getCategory(){
            var that=this;
            this.dataCategory=[];
            let curentId=document.location.href.split("/");
            // axios.get("/dataItem/category/",{
            //     params:{
            //         id:curentId[curentId.length-1]
            //     }
            // })
            //     .then(res=>{
            //        that.dataCategory=res.data.data;
            //     })
        },
        clickDataItemInfo(id){
            //todo jump to the dataitems,and choose the id category

            // console.log(dataItems.$data)

            // window.history.back(-1)
            // data_items.$options.methods.chooseCate(id)

        },
        checkRelatedModels(item){
            let curentId=document.location.href.split("/");
            return curentId[0]+"//"+curentId[2]+"/modelItem/"+item.oid;
        },
        handleClose(done) {
            this.$confirm('Are you sure to close this dialog？')
                .then(_ => {
                    done();
                })
                .catch(_ => {});
        },
        //add related models
        addRelatedModel(){

            if(this.useroid==''){
                alert(this.htmlJSON.LoginInFirst);
                window.location.href = "/user/login";
            }else{
                this.searchAddModelPage=0
                this.searchAddRelatedModels=[]
                this.addModelsSearchText=""
                this.selectedModels=[]
                this.selectedModelsOid=[]


                this.addRelatedModelsDialogVisible=true






            }



        },
        searchRelatedModels(){



            this.nomoreflag=false
            if(this.value1==='1'){

                this.addSearchFromUser()
            }else if(this.value1==='2'){

                this.addSearchFromAll()
            }
        },
        clearSearchResult(){
            this.searchAddRelatedModels=[]
        },
        loadAddMore(e){

            let that=this
            if ( e.target.scrollHeight - e.target.clientHeight-e.target.scrollTop <10) { //到达底部100px时,加载新内容

                clearTimeout(this.timer);

                this.timer=setTimeout(()=>{
                        that.searchAddModelPage+=1// 这里加载数据..



                        if(this.value1==='1'){
                            that.addSearchFromUser()
                        }else if(this.value1==='2'){
                            that.addSearchFromAll()
                        }


                    },
                    500)

            }


        },
        addSearchFromUser() {

            let data={
                searchText:this.addModelsSearchText,
                page:this.searchAddModelPage,
                sortType:"default",
                asc:1
            }
            let that=this
            this.loading=true
            axios.get("/modelItem/searchModelItemsByUserId",{
                params:data
            })
                .then((res)=>{

                    if(res.status===200){
                        that.loading=false
                        that.searchAddRelatedModels=that.searchAddRelatedModels.concat(res.data.data.modelItems)
                    }



                })

        },
        addSearchFromAll(){
            let data=new FormData()
            data.append('searchText',this.addModelsSearchText)
            data.append('page',this.searchAddModelPage)
            data.append('sortType','viewCount')
            data.append('asc',false)
            data.append('pageSize',10)
            data.append('classifications[]','all')



            let that=this
            this.loading=true
            axios.get("/modelItem/items",data)
                .then((res)=>{

                    if(res.status===200){
                        that.loading=false
                        that.searchAddRelatedModels=that.searchAddRelatedModels.concat(res.data.data.list)
                    }



                })

        },
        selectRelatedModel(item,e){

            if(this.selectedModels.indexOf(item.name)>-1){
                e.currentTarget.className="is-hover-shadow models_margin_style"

                this.getRidOf(item.name,this.selectedModels)
                this.getRidOf(item.oid,this.selectedModelsOid)
            }else{
                e.currentTarget.className="is-hover-shadow models_margin_style selectedModels"

                this.selectedModels.push(item.name)
                this.selectedModelsOid.push(item.oid)
            }



        },
        getRidOf(e,arr){
            arr.splice(arr.indexOf(e),1)
        },
        relatedToCurrentData(){

            if(this.selectedModelsOid.length===0){
                alert(this.htmlJSON.PleaseSelectModelFirst)
            }else{

                let curentId=document.location.href.split("/");

                let dataItemFindDTO={
                    dataId:curentId[curentId.length-1],
                    relatedModels:this.selectedModelsOid
                }

                axios.post("/dataItem/models",dataItemFindDTO)


                    .then((res)=>{
                        if(res.status===200){
                            alert(this.htmlJSON.RelatedModelsSuccessfully)

                        }

                    })



            }

        },
        showRelatedModels(){
            this.dataNums=5
            this.searchAddRelatedModels=[]
            this.searchRelatedModelsDialogVisible=true
            relatedModelsSearchText=""
            this.RelatedModels(this.dataNums)

        },
        searchFromRelatedModels(){
            //todo search from show related models
        },
        //函数节流防抖
        loadMore(e){

            if(!this.nomoreflag){
                if ( e.target.scrollHeight - e.target.clientHeight-e.target.scrollTop <10) { //到达底部100px时,加载新内容

                    clearTimeout(this.timer);

                    this.timer=setTimeout(()=>{
                            this.dataNums+=5// 这里加载数据..
                            this.RelatedModels(this.dataNums)
                        },
                        500)

                }
            }

        },
        RelatedModels(more){
            let curentId=document.location.href.split("/");
            let that=this
            this.loading=true
            this.nomore=false
            axios.get("/dataItem/allrelatedmodels",{
                params:{
                    id:curentId[curentId.length-1],
                    more:more
                }
            })
                .then((res)=>{
                    if(res.status==200){
                        that.loading=false
                        //todo 传回来数组为空时
                        if(res.data.data[0].all==="all"){
                            that.nomore="no more"
                            that.nomore=true
                            that.loading=false

                        }else{
                            that.allRelatedModels=that.allRelatedModels.concat(res.data.data)
                            that.loading=false
                        }

                    }

                })

        },
        getUrlParam(url, name) {

            let urls = url.split('?')
            return urls[1];

        },
        shareData(){
            let downloadAllZipUrl="/dataManager/downloadSomeRemote";
            if(dataSelection.length!=0) {
                for(i=0;i<dataSelection.length;i++){

                    let id=this.getUrlParam(dataSelection[i]);

                    downloadAllZipUrl+=i==0?'?':'&';
                    downloadAllZipUrl+=id;

                }
                this.$alert("<textarea style='width: 100%;height:auto'>https://geomodeling.njnu.edu.cn"+downloadAllZipUrl+"</textarea>","Sharing link",{
                    dangerouslyUseHTMLString: true
                })
            }
            else{
                alert(this.htmlJSON.PleaseSelectFileFirst);
            }
        },
        edit() {
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
                    let data = result.data
                    if (result.code !== 0) {
                        this.confirmLogin()

                    }
                    else {
                        let href = window.location.href;
                        let hrefs = href.split('/');
                        let oid = hrefs[hrefs.length - 1].split("#")[0];
                        //正则匹配hub
                        let result = href.match("Hub");
                        if (result!=null){
                            window.location.href = '/user/userSpace#/data/manageDataHubs/'+oid
                        }else {
                            window.location.href = '/user/userSpace#/data/manageDataItem/' + oid
                        }
                    }
                }
            })
        },
        downLoadDistributedNodeData(){
            this.loading = true;

            // $(".connect").attr('style','display: inline-block;color: mediumblue;font-size: larger;font-weight: 600;');

            // let obj;
            let data = {
                dataOid: this.dataItemId,
            };
            // console.log('sd');
            // var time = new Date();
            let that=this
            $.ajax({
                url:"/dataItem/getDistributedObj",
                type:"GET",
                data:data,
                async:false,
                success:(json)=>{
                    if (json.code == "0") {
                        that.objDistributed = json.data;
                        // that.linkWebsocket("distributed");
                        that.mseeageByWebSocket("distributed")

                    }else if(json.code==-1){
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href="/user/login";
                            }
                        });
                    }else if (json.code = "1"){
                        that.loading = false;
                        // $(".connect").hide();
                        let dataUrl = json.data;
                        window.location.href=dataUrl;
                    }
                }
            })
            // if (obj!=null) {
            //     ws.send(JSON.stringify(obj));
            // }
        },
        invokeProcessing($event){
            this.processingType = "process";
            this.loading2 = false;
            let processingInvokes = $(".processingInvoke");
            for(i=0;i<processingInvokes.length;i++){
                if(event.currentTarget===processingInvokes[i]){
                    this.processingId = processingInvokes[i].id;
                }
            }
            // var url = window.location.href;
            // var index = url.lastIndexOf("\/");
            // let dataItemId = url.substring(index+1,url.length);
            // this.dataItemId = dataItemId;
            let formData = new FormData();
            formData.append("dataItemId", this.dataItemId);
            formData.append("processingId",this.processingId);
            formData.append("type",this.processingType);
            $.ajax({
                url:"/dataItem/getParemeter",
                type:"POST",
                data:formData,
                cache: false,
                processData: false,
                contentType: false,
                success: (json) => {
                    if (json.code == 0) {
                        this.xml = json.xml;
                        this.parameters = json.parameters
                    }else if(json.code==-1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href="/user/login";
                            }
                        });
                    }
                }

            })
        },
        invokeProcessingVisual($event){
            this.processingType = "visual";
            this.loading2 = false;
            let processingInvokes = $(".processingInvokeVisual");
            for(i=0;i<processingInvokes.length;i++){
                if(event.currentTarget===processingInvokes[i]){
                    this.processingId = processingInvokes[i].id;
                }
            }
            // var url = window.location.href;
            // var index = url.lastIndexOf("\/");
            // let dataItemId = url.substring(index+1,url.length);
            let formData = new FormData();
            formData.append("dataItemId", this.dataItemId);
            formData.append("processingId",this.processingId);
            formData.append("type",this.processingType);
            $.ajax({
                url:"/dataItem/getParemeter",
                type:"POST",
                data:formData,
                cache: false,
                processData: false,
                contentType: false,
                success: (json) => {
                    if (json.code == 0) {
                        this.xml = json.xml;
                        this.parameters = json.parameters
                    }else if(json.code==-1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href="/user/login";
                            }
                        });
                    }
                }
            })
        },
        invokeProcessingNow(){
            //this.loading = true;
            this.loading3 = true;
            this.objProcess.msg = 'reqPcs';//固定值，区分消息类型
            let formData = new FormData();
            formData.append("dataItemId", this.dataItemId);
            formData.append("processingId",this.processingId);
            formData.append("type",this.processingType);
            // obj.dataId = dataItemId;
            this.objProcess.pcsId = this.processingId;
            let params= [];
            for (let i = 0;i<this.parameters.length;i++){
                params.push($('#'+this.parameters[i]).val());
            }
            params=params.join(",");
            this.objProcess.params = params;
            $.ajax({
                url:"/dataItem/getProcessingObj",
                type:"POST",
                data:formData,
                cache:false,
                processData: false,
                contentType: false,
                success:(json)=>{
                    this.objProcess.name = json.dataItemName;
                    this.objProcess.token = json.token;
                    this.objProcess.reqUsrOid = json.userId;
                    this.objProcess.dataId = json.dataId;
                    this.objProcess.wsId = this.dataItemId;

                    this.mseeageByWebSocket("process");
                }
            })
        },
        mseeageByWebSocket(type){


            let objSend;
            if (type=="process"){
                objSend = this.objProcess;
            } else if (type == "distributed") {
                objSend = this.objDistributed;
            }

            let _ws=this.$root.$el._myws,that=this
            _ws.send(JSON.stringify(objSend))
            this.$root.$el._myws.onmessage=function (e) {

                //没有对应数据
                if (e.data === 'no data in service node!') {
                    if (type=="process"){
                        that.loading2 = false;
                        that.loading3 = false;
                    }else {
                        that.loading = false;
                    }
                    // loading = false;
                    alert(this.htmlJSON.NoDataInServiceNode)
                } else
                    //没有权限
                if (e.data === 'no authority') {
                    if (type=="process"){
                        that.loading2 = false; that.loading3 = false;
                    }else {
                        that.loading = false;
                    }
                    alert(this.htmlJSON.noAuthority)
                } else
                    //服务结点离线
                if (e.data == 'node offline') {
                    if (type=="process"){
                        that.loading2 = false; that.loading3 = false;
                    }else {
                        that.loading = false;
                    }
                    alert(this.htmlJSON.ServiceNodeOffline)
                } else
                    //注册门户到中转服务器成功
                if (e.data === 'success') {
                    // alert("连接成功")
                } else {
                    //心跳检测
                    if (e.data === 'beat') {
                        _ws.send('online')

                    } else {
                    }
                    let r = JSON.parse(e.data)
                    if (r.msg == 'insitudata') {
                        // alert(e.data)
                        let data = {
                            dataOid: that.dataItemId,
                            rId: r.id
                        }
                        $.ajax({
                            url:"/dataItem/saveUrl",
                            type:"POST",
                            data:data,
                            success:(json)=>{
                                console.log(json);
                                console.log("success");

                            }
                        })
                        if (type=="process"){
                            that.loading2 = false; that.loading3 = false;
                        }else {
                            that.loading = false;
                        }

                        window.location.href = "http://111.229.14.128:8899/data/"+ r.id;

                        return
                    }
                }
                // if (objSend!=null&&e.data!="node offline") {
                //     _ws.send(JSON.stringify(objSend));
                // }


            }


        },
        getDistributeDataInfo(){
            let that = this;
          $.ajax({
              url:"/dataItem/distributeDataInfo/" + that.dataItemId,
              type:"get",
          }).done((res)=> {
              if (res.code == 0) {
                    console.log(res.data);
                  that.invokeServices = res.data;
              }
          })
        },
        downloadDisData(event){
            this.loading = true;
            let that = this;
            let refLink=$(".downloadBtn");
            for(i=0;i<refLink.length;i++) {
                if (event.currentTarget === refLink[i]) {
                    let token = this.invokeServices[i].token;
                    let dataId = this.invokeServices[i].serviceId;
                    let formData = new FormData();
                    formData.append("token", token);
                    formData.append("dataId", dataId);
                    $.ajax({
                        url:'/dataItem/downloadDisData',
                        type:'POST',
                        data:formData,
                        cache: false,
                        processData: false,
                        contentType: false,
                    }).done((res)=>{
                        if(res.code == 0){
                            console.log("test");
                            that.loading = false;
                            window.location.href = res.data;
                        }else if(res.code == -1){
                            that.loading = false;
                            this.$message({
                                type: 'error',
                                message: this.htmlJSON.DistributeNodeOffline
                            });
                        }
                    })
                }
            }
        },
        receiveModuleMsg(msg){
            let m_code = msg.m_code
            let m_msg = msg.m_msg

            if(m_code==-1){
                if(m_msg.toLowerCase()==='nologin'&&this.dialogTableVisible){
                    this.confirmLogin()
                    this.dialogTableVisible = false
                }
            }else if(m_code==0){
                if(m_msg.toLowerCase()==='pending'&&this.dialogTableVisible){
                    this.dialogTableVisible = false
                    window.location.reload();
                }
            }else if(m_code==1){
                if(m_msg.toLowerCase()==='suc'&&this.dialogTableVisible){
                    this.dialogTableVisible = false
                    window.location.reload();
                }
            }
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

        getRelatedResources() {
            //从地址栏拿到oid
            let arr = window.location.href.split("/");
            let id = arr[arr.length - 1].split("#")[0];
            let data = {
                id: id,
            };
            $.ajax({
                type: "GET",
                url: `/${this.itemType}/relatedResources/` + id,
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
                url: `/${this.itemType}/knowledge`,
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

        changeRelateType(activeType){
            this.relateType = activeType
            this.searchInit(this.activeName_dialog)
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

    },

    mounted(){

        this.changeCLSLng();

        this.itemType = window.location.pathname.split('/')
        this.itemType = this.itemType[this.itemType.length-2]
        // console.log(this.itemType)

        this.lightenContributor = author
        this.$refs.mainContributorAvatar.insertAvatar(this.lightenContributor.avatar)
        this.$refs.mainContributorAvatar1.insertAvatar(this.lightenContributor.avatar)
        // let that=_this

        //  1===0 表示不执行该方法，执行的话把这个去掉
        if( 1===0 && window.WebSocket) {
            this.$root.$el._myws = new WebSocket('ws://111.229.14.128:1708');
            this.$root.$el._myws.onopen = function (e) {
                let obj = {
                    msg: 'regist',
                    token: that.dataItemId,
                }
                that.$root.$el._myws.send(JSON.stringify(obj));
            }
            this.$root.$el._myws.onclose = function (e) {
                console.log("服务器关闭");
            }
            this.$root.$el._myws.onerror = function () {
                console.log("连接出错");
            }

            this.$root.$el._myws.onmessage=function (e) {

                if (e.data === 'success') {
                    console.log("websocket set up")
                }

            }

        }
        let that = this;
        //获取当前数据条目的id
        var url = window.location.href;
        var index = url.lastIndexOf("\/");
        let dataItemId = url.substring(index+1,url.length);
        that.dataItemId = dataItemId;
        axios.get("/user/load")
            .then((res) => {
                if (res.data.code == 0) {

                    this.useroid = res.data.data.accessId;
                    this.userUid = res.data.data.email;
                    this.userId = res.data.data.accessId;
                    this.userImg = res.data.data.avatar;


                }
            })
        // this.getComments();

        $(document).on('mouseover mouseout','.flexRowSpaceBetween',function(e){

            let deleteBtn=$(e.currentTarget).children().eq(1).children(".delete");
            if(deleteBtn.css("display")=="none"){
                deleteBtn.css("display","block");
            }else{
                deleteBtn.css("display","none");
            }

        });

        this.getCategory();

        var currenturl=window.location.href;
        var url=currenturl.split("/")
        this.currentDataId=url[url.length-1]
        var dataitemid=currenturl.split("/");


        var alldata=new Array();

        let qrcodes = document.getElementsByClassName("qrcode");
        for(i=0;i<qrcodes.length;i++) {
            new QRCode(document.getElementsByClassName("qrcode")[i], {
                text: window.location.href,
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        }

        // $('html, body').animate({scrollTop:0}, 'slow');


        let descHeight=$("#description .block_content").height();
        if(descHeight>300){
            $("#description .block_content").css("overflow","hidden")
            $("#description .block_content").css("height","250px")

            $(".fullPaper").removeClass("hide");
        }

        $("#fullPaper").click(function(){
            $("#description .block_content").css("overflow","inherit");
            $("#description .block_content").css("height","auto");
            $(".fullPaper").remove();
        })

        this.getDistributeDataInfo();

    },

});
var currentData='';
var dataSelection=[];
//JQuery
$(function () {

    //数据项点击样式事件
    $(".filecontent .el-card").on('click',function (e) {
        console.log(e.currentTarget.id)
        let exist=false;
        for(i=0;i<dataSelection.length;i++)
        {
            let data=dataSelection[i];
            if(data==e.currentTarget.id){
                exist=true;
                dataSelection.splice(i,1);
                $(this).removeClass("clickdataitem");
                break;
            }
        }

        if(!exist){
            dataSelection.push(e.currentTarget.id);
            $(this).addClass("clickdataitem");
        }

    });


    //contents白板右键点击菜单事件，是否添加有待进一步思考
    $(".filecontent").contextmenu(function (e) {
        e.preventDefault();
        // $(".browser").css({
        //     "left":e.pageX,
        //     "top":e.pageY
        // }).show();

    });



    //下载全部按钮为所有数据项添加样式事件
    $(".downloadAll").click(function () {
        $(".dataitemisol").addClass("clickdataitem")

        let downloadAllZipUrl="/dataManager/downloadSomeRemote"

        let data=$(".dataitemisol");
        console.log($(".dataitemisol").get(1));
        for(i=0;i<data.length;i++){
            let url=data.eq(i).attr('id');
            let id=getUrlParam(url);

            downloadAllZipUrl+=i==0?'?':'&';
            downloadAllZipUrl+=id;

        }

        // let downloadallzipurl="http://172.21.212.64:8082/dataResource/getResourcesRelatedDataItem/"+url[url.length-1];
        //
        let link =document.createElement("a");
        link.style.display='none';
        link.href=downloadAllZipUrl;
        link.setAttribute("download","allData.zip");

        document.body.appendChild(link);
        link.click();

    });

    $(".dwload").click(function () {
        let downloadAllZipUrl="/dataManager/downloadSomeRemote";
        if(dataSelection.length!=0) {
            for(i=0;i<dataSelection.length;i++){

                let id=getUrlParam(dataSelection[i]);

                downloadAllZipUrl+=i==0?'?':'&';
                downloadAllZipUrl+=id;

            }
            let link =document.createElement("a");
            link.style.display='none';
            link.href=downloadAllZipUrl;
            link.setAttribute("download","allData.zip");

            document.body.appendChild(link);
            link.click();
        }
        else{
            alert(this.htmlJSON.PleaseSelectFileFirst);
        }


    });


    //搜索结果样式效果和菜单事件
    $("#browsercont").on('click',function (e) {

        $(".el-card.dataitemisol.is-never-shadow.sresult").click(function () {
            $(this).addClass("clickdataitem");

            $(this).siblings().removeClass("clickdataitem");

        });


        $(".el-card.dataitemisol.is-never-shadow.sresult").contextmenu(function () {

            $(".browsermenu").css({
                "left":e.pageX,
                "top":e.pageY
            }).show();

        })


    });

    //contents白板点击隐藏数据项菜单事件
    $(".filecontent").click(function () {
        $(".browsermenu").hide();
        //$(".dataitemisol").removeClass("clickdataitem")

    });

    //光标移入输入框隐藏数据项右键菜单
    $("#searchinput").on("mouseenter",function () {
        $(".browsermenu").hide();
    });


    function getUrlParam(url,name) {

        let urls=url.split('?')
        return urls[1];

    }

    var value=0;

    $(".ab").click(
        function () {
            if(this.className.indexOf('transform180')==-1)
                $(this).addClass('transform180')
            else
                $(this).removeClass('transform180')
        }
    );



});


//todo 文件管理器已经阉割 CUT