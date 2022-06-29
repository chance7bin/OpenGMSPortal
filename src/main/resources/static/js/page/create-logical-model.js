var createLogicalModel = Vue.extend({
    template: "#createLogicalModel",
    props:['htmlJson'],
    data() {
        return {
            bindLoaing:false,
            defaultActive: '2-3',
            curIndex: '2',

            itemInfo: {
                status:"Public",
                relateModelItemName: "",
                relateModelItem: "",
                name: "",
                description: "",
                contentType: "MxGraph",
                cXml: "",
                svg: "",
                isAuthor: true,
                author: {
                    name: "",
                    ins: "",
                    email: ""
                },
                resources: [],

            },

            bindModelItemDialogVisible:false,
            pageOption: {
                paginationShow:false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,
                searchText: '',
                total: 264,
                searchResult: [],
            },

            userInfo: {},
            //文件框
            resources: [],
            fileSelect: '',
            fileArray: new Array(),
            formData: new FormData(),

            ScreenMaxHeight: "0px",
            IframeHeight: "0px",
            editorUrl: "",
            load: false,

            ScreenMinHeight: "0px",

            userId: "",
            userName: "",
            loginFlag: false,

            socket:"",

            logicalModel_oid:"",

            editType:'create',

            toCreate: 1,

            draft:{
                oid:'',
            },

            startDraft:0,

            draftOid:'',
        }
    },

    watch:{
        // 中英文切换
        htmlJson:function(newData){
            if (this.editType == 'create'){
                // console.log("create:",this.htmlJson.CreateModelItem);
                $("#subRteTitle").text("/" + newData.CreateLogicalModel);
            } else {
                // console.log("modify:",this.htmlJson.ModifyModelItem);
                $("#subRteTitle").text("/" + newData.ModifyLogicalModel);
            }
        }
    },

    methods: {
        selectModelItem(index,info){
            this.itemInfo.relateModelItemName = info.name;
            this.itemInfo.relateModelItem = info.id;
            this.bindModelItemDialogVisible = false;
        },
        handlePageChange(val) {

            this.pageOption.currentPage = val;

            this.searchModelItem();
        },
        openModelItemDialog(){
            this.pageOption.currentPage = 1;
            this.bindModelItemDialogVisible = true;
            this.searchModelItem();
        },
        searchModelItem(){
            this.bindLoaing = true
            let data = {
                asc: this.pageOption.sortAsc,
                page: this.pageOption.currentPage,
                pageSize: this.pageOption.pageSize,
                searchText: this.pageOption.searchText,
                sortType: "default",
                classifications: ["all"],
            };
            let url = getModelItemList();
            let contentType = "application/json";

            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(data),
                async: true,
                contentType: contentType,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        this.pageOption.total = data.total;
                        this.pageOption.pages = data.pages;
                        this.pageOption.searchResult = data.list;
                        this.pageOption.users = data.users;
                        this.pageOption.progressBar = false;
                        this.pageOption.paginationShow = true;
                        this.bindLoaing = true
                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },

        handleSelect(index,indexPath){
            this.setSession("index",index);
            window.location.href="/user/userSpace"
        },
        changeOpen(n) {
            this.activeIndex = n;
        },
        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },
        getSession(name){
            return window.sessionStorage.getItem(name);
        },
        clearSession(){
            window.sessionStorage.clear();
        },

        addFile(){
            $("#imgFiles").click();
        },
        removeFile(){
            if(this.fileSelect!="") {
                $(".dataitemisol").css("border","1px solid #ebeef5")
                let res=this.resources[Number(this.fileSelect)];
                for(i=0;i<this.fileArray.length;i++){
                    let file=this.fileArray[i];
                    if(file.name===res.name&&file.size===res.size&&file.lastModified===res.lastModified&&file.type===res.type){
                        this.fileArray.splice(i,1);
                        break;
                    }
                }
                this.resources.splice(Number(this.fileSelect), 1);
                this.fileSelect = "";
            }
        },
        replaceFile(){
            this.fileArray=new Array();
            $("#imgFiles").click();
        },
        resClick(e){

            let path=e.path;
            for(i=0;i<path.length;i++){
                let obj=path[i];
                if(obj.className.indexOf("dataitemisol")!=-1){
                    $(".dataitemisol").css("border","1px solid #ebeef5")
                    this.fileSelect=obj.align;
                    obj.style.border='2px solid #60b0e8';
                    break;
                }
            }
        },

        async getBindModelInfo(modelOid){
            let data = null
            await axios.get('/modelItem/searchByOid',{
                params:{
                    oid:modelOid
                }
            }).then(
                res=>{
                    if(res.data.code!=-1){
                        data = res.data.data
                    }else{

                    }
                }
            )

            return data
        },

        async insertInfo(basicInfo){

            let user_num = 0;
            this.resources=basicInfo.resourceJson;

            let modelItem = {
                name:'',
                oid:''
            }
            if(basicInfo.relateModelItem!=null&&basicInfo.relateModelItem!=undefined){
                modelItem = await this.getBindModelInfo(basicInfo.relateModelItem)
            }


            // this.itemInfo.relateModelItemName = modelItem==null?"":modelItem.name;
            // this.itemInfo.relateModelItem = modelItem==null?"":modelItem.oid;
            //
            // $("#search-box").val(basicInfo.relateModelItemName)
            //wyj 更新logical model时 选中原来选择的模型
            this.itemInfo.relateModelItemName = basicInfo.relatedModelItemInfoList[0].name
            this.itemInfo.relateModelItem = basicInfo.relatedModelItemInfoList[0].id

            $("#search-box").val(basicInfo.relatedModelItemInfoList[0].name)

            this.itemInfo.status=basicInfo.status;

            if(basicInfo.contentType=="MxGraph"){
                $("input[name='ContentType']").eq(0).iCheck('check');
                $("#MxGraph").show();
                $("#Image").hide();
            }
            else{
                $("input[name='ContentType']").eq(1).iCheck('check');
                $("#MxGraph").hide();
                $("#Image").show();
            }

            $(".providers").children(".panel").remove();

            //detail

            // $("#logicalModelText").html(basicInfo.detail);
            $("#logicalModelText").html(basicInfo.localizationList[0].description);


            initTinymce('textarea#logicalModelText')

            let authorships = basicInfo.authorships;
            if(authorships!=null) {
                for (i = 0; i < authorships.length; i++) {
                    user_num++;
                    var content_box = $(".providers");
                    var str = "<div class='panel panel-primary'> <div class='panel-heading newAuthorHeader'> <h4 class='panel-title'> <a class='accordion-toggle collapsed' style='color:white' data-toggle='collapse' data-target='#user";
                    str += user_num;
                    str += "' href='javascript:;'> NEW </a> </h4><a href='javascript:;' class='fa fa-times author_close' style='float:right;margin-top:8px;color:white'></a></div><div id='user";
                    str += user_num;
                    str += "' class='panel-collapse collapse in'><div class='panel-body user-contents'> <div class='user-attr'>\n" +
                        "                                                                                                    <div>\n" +
                        "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                        "                                                                                                               style='font-weight: bold;'>\n" +
                        "                                                                                                            Name:\n" +
                        "                                                                                                        </lable>\n" +
                        "                                                                                                        <div class='input-group col-sm-10'>\n" +
                        "                                                                                                            <input type='text'\n" +
                        "                                                                                                                   name=\"name\"\n" +
                        "                                                                                                                   class='form-control' value='" +
                        authorships[i].name +
                        "'>\n" +
                        "                                                                                                        </div>\n" +
                        "                                                                                                    </div>\n" +
                        "                                                                                                    <div style=\"margin-top:10px\">\n" +
                        "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                        "                                                                                                               style='font-weight: bold;'>\n" +
                        "                                                                                                            Affiliation:\n" +
                        "                                                                                                        </lable>\n" +
                        "                                                                                                        <div class='input-group col-sm-10'>\n" +
                        "                                                                                                            <input type='text'\n" +
                        "                                                                                                                   name=\"ins\"\n" +
                        "                                                                                                                   class='form-control' value='" +
                        authorships[i].ins +
                        "'>\n" +
                        "                                                                                                        </div>\n" +
                        "                                                                                                    </div>\n" +
                        "                                                                                                    <div style=\"margin-top:10px\">\n" +
                        "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                        "                                                                                                               style='font-weight: bold;'>\n" +
                        "                                                                                                            Email:\n" +
                        "                                                                                                        </lable>\n" +
                        "                                                                                                        <div class='input-group col-sm-10'>\n" +
                        "                                                                                                            <input type='text'\n" +
                        "                                                                                                                   name=\"email\"\n" +
                        "                                                                                                                   class='form-control' value='" +
                        authorships[i].email +
                        "'>\n" +
                        "                                                                                                        </div>\n" +
                        "                                                                                                    </div>\n" +
                        "                                                                                                    <div style=\"margin-top:10px\">\n" +
                        "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                        "                                                                                                               style='font-weight: bold;'>\n" +
                        "                                                                                                            Homepage:\n" +
                        "                                                                                                        </lable>\n" +
                        "                                                                                                        <div class='input-group col-sm-10'>\n" +
                        "                                                                                                            <input type='text'\n" +
                        "                                                                                                                   name=\"homepage\"\n" +
                        "                                                                                                                   class='form-control' value='" +
                        authorships[i].homepage +
                        "'>\n" +
                        "                                                                                                        </div>\n" +
                        "                                                                                                    </div>\n" +
                        "                                                                                                </div></div> </div> </div>"
                    content_box.append(str)
                }
            }

            this.itemInfo.name=basicInfo.name;
            this.itemInfo.description=basicInfo.overview

            // $("#nameInput").val(basicInfo.name);
            // $("#descInput").val(basicInfo.description)
        },

        getItemContent(trigger,callBack){
            let itemObj = {}

            itemObj.name=this.itemInfo.name
            itemObj.status=this.itemInfo.status
            itemObj.description=this.itemInfo.description
            itemObj.relatedModelItems=[this.itemInfo.relateModelItem]
            itemObj.contentType=$("input[name='ContentType']:checked").val();
            itemObj.isAuthor=$("input[name='author_confirm']:checked").val();
            var detail = tinyMCE.activeEditor.getContent();
            itemObj.detail = detail.trim();

            itemObj.authorships=[];
            userspace.getUserData($("#providersPanel .user-contents .form-control"), itemObj.authorships);

            /**
             * 张硕
             * 2019.11.14
             * 经过试验，logicalmodel只需要 cxml 就OK，所以暂时只存cxml，注释掉svg，xml，w，h
             * 并不OK，需要存图片，所以不能注释
             */
            let iframeWindow=$("#ModelEditor")[0].contentWindow;
            // itemObj.cXml=iframeWindow.getCxml();

            let result=iframeWindow.getXml();

            if(itemObj.contentType=="MxGraph") {
                itemObj.svg = "<svg width='" + result.w + "px' height='" + result.h + "px' xmlns='http://www.w3.org/2000/svg' xmlns:html='http://www.w3.org/1999/xhtml'>" + iframeWindow.getSvg() + "</svg>";
                itemObj.cXml=iframeWindow.getCxml();
                itemObj.xml=result.xml;
                itemObj.w=result.w;
                itemObj.h=result.h;
            }
            else{
                itemObj.svg="";
                itemObj.cXml="";
            }

            if(callBack){
                callBack(itemObj)
            }

            return itemObj
        },

        //draft
        onInputName(){
            console.log(1)
            if(this.toCreate==1){
                this.toCreate=0
                this.timeOut=setTimeout(()=>{
                    this.startDraft=1
                    this.toCreate=1
                },30000)
                setTimeout(()=>{
                    this.createDraft()
                },300)

            }
        },

        getStep(){
            let domID=$('.step-tab-panel.active')[0].id
            return parseInt(domID.substring(domID.length-1,domID.length))
        },

        createDraft(){//请求后台创建一个草稿实例,如果存在则更新

            var step = this.getStep()
            let content=this.getItemContent(step)


            //wyj url现在不是：https://geomodeling.njnu.edu.cn/user/userSpace#/model/createLogicalModel，获取不到urls[6]

            // let urls=window.location.href.split('/')
            // let item=urls[6]
            // item=item.substring(6,item.length)

            item="LogicalModel"
            let obj={
                content:content,
                editType:this.editType,
                itemType:item,
                user:this.userId,
                oid:this.draft.oid,
            }
            if(this.editType) {
                obj.itemOid=this.$route.params.editId?this.$route.params.editId:null
                obj.itemName= this.itemName;
            }

            this.$refs.draftBox.createDraft(obj)
        },

        loadMatchedCreateDraft(){
            this.$refs.draftBox.loadMatchedCreateDraft()
        },

        deleteDraft(){
            this.$refs.draftBox.deleteDraft(this.draft.oid)
        },

        initDraft(editType,backUrl,oidFrom,oid){
            this.$refs.draftBox.initDraft(editType,backUrl,oidFrom,oid)
        },

        getDraft(){
            return this.$refs.draftBox.getDraft();
        },

        insertDraft(draftContent){
            this.insertInfo(draftContent)
        },

        cancelEditClick(){
            if(this.getDraft()!=null){
                this.$refs.draftBox.cancelDraftDialog=true
            }else{
                setTimeout(() => {
                    window.location.href = "/user/userSpace#/models/createLogicalModel";
                }, 305)
            }
        },

        draftJump(){
            window.location.href = '/user/userSpace#/models/createLogicalModel';
        },
        //draft end

        sendcurIndexToParent(){
            this.$emit('com-sendcurindex',this.curIndex)
        },

        sendUserToParent(userId){
            this.$emit('com-senduserinfo',userId)
        },

        init:function () {

            // if ('WebSocket' in window) {
            //     // this.socket = new WebSocket("ws://localhost:8080/websocket");
            //     this.socket = new WebSocket(websocketAddress);
            //     // 监听socket连接
            //     this.socket.onopen = this.open;
            //     // 监听socket错误信息
            //     this.socket.onerror = this.error;
            //     // 监听socket消息
            //     this.socket.onmessage = this.getMessage;
            //
            // }
            // else {
            //     alert('当前浏览器 Not support websocket');
            //     console.log("websocket 无法连接");
            // }
        },
        open: function () {
            console.log("socket连接成功")
        },
        error: function () {
            console.log("连接错误");
        },
        getMessage: function (msg) {
            console.log(msg.data);
        },
        send: function (msg) {
            this.socket.send(msg);
        },
        close: function () {
            console.log("socket已经关闭")
        },
        getnoticeNum(logicalModel_oid){
            this.message_num_socket = 0;//初始化消息数目
            let data = {
                type: 'logicalModel',
                oid : logicalModel_oid,
            };

            //根据oid去取该作者的被编辑的条目数量
            $.ajax({
                url:"/theme/getAuthornoticeNum",
                type:"GET",
                data:data,
                async:false,
                success:(json)=>{
                    this.message_num_socket = json;
                }
            });
            let data_theme = {
                type: 'logicalModel',
                oid : logicalModel_oid,
            };
            $.ajax({
                url:"/theme/getThemenoticeNum",
                async:false,
                type:"GET",
                data:data_theme,
                success:(json)=>{
                    console.log(json);
                    for (let i=0;i<json.length;i++) {
                        for (let k = 0; k < 4; k++) {
                            let type;
                            switch (k) {
                                case 0:
                                    type = json[i].subDetails;
                                    break;
                                case 1:
                                    type = json[i].subClassInfos;
                                    break;
                                case 2:
                                    type = json[i].subDataInfos;
                                    break;
                                case 3:
                                    type = json[i].subApplications;
                                    break;
                            }
                            if (type != null && type.length > 0) {
                                for (let j = 0; j < type.length; j++) {
                                    if (k == 0) {
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    }else if (k == 1){
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    }else if (k == 2){
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    } else if (k == 3){
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        },

        submitEdit(){
            $(".finish").click()
        },


    },
    mounted() {
        //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
        let that = this;
        that.init();

        this.sendcurIndexToParent()

        $(() => {
            let height = document.documentElement.clientHeight;
            this.ScreenMinHeight = (height) + "px";
            this.ScreenMaxHeight = (height) + "px";
            this.IframeHeight = (height - 330) + "px";

            let resizeTimer = null;
            let that = this
            window.onresize = () => {
                if (resizeTimer) clearTimeout(resizeTimer);
                resizeTimer = setTimeout(function(){
                    console.log('come on ..');
                    height = document.documentElement.clientHeight;
                    that.ScreenMinHeight = (height) + "px";
                    that.ScreenMaxHeight = (height) + "px";
                    that.IframeHeight = (height - 330) + "px";
                } , 100);
            };


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

                    let data = result.data
                    console.log(data);

                    if (result.code !== 0) {
                        this.$alert(this.htmlJson.LoginInFirst, this.htmlJson.Error, {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href="/user/login";
                            }
                        });
                    } else {
                        this.userId = data.email;
                        this.userName = data.name;
                        console.log(this.userId)
                        this.sendUserToParent(this.userId)
                        // this.addAllData()

                        // axios.get("/dataItem/amountofuserdata",{
                        //     params:{
                        //         userOid:this.userId
                        //     }
                        // }).then(res=>{
                        //     that.dcount=res.data
                        // });

                        $("#author").val(this.userName);

                        var index = window.sessionStorage.getItem("index");
                        this.itemIndex=index
                        if (index != null && index != undefined && index != "" && index != NaN) {
                            this.defaultActive = index;
                            this.handleSelect(index, null);
                            window.sessionStorage.removeItem("index");
                            this.curIndex=index

                        } else {
                            // this.changeRter(1);
                        }

                        window.sessionStorage.removeItem("tap");
                        //this.getTasksInfo();
                        this.load = false;
                    }
                }
            })


            //this.getModels();
        });


        $("#imgFiles").change(()=> {

            let files=$("#imgFiles")[0].files;
            for(i=0;i<files.length;i++){
                let file=files[i];
                this.fileArray.push(file);
                let res={};
                res.name=file.name;
                res.path="";
                let names=res.name.split('.');
                res.suffix=names[names.length-1];
                res.size=file.size;
                res.lastModified=file.lastModified;
                res.type=file.type;
                this.resources.push(res);
            }


            //清空
            let file=document.getElementById("file");
            file.value='';
        });




        var oid = this.$route.params.editId;//取得所要edit的id

        this.draft.oid=window.localStorage.getItem('draft');//取得保存的草稿的Oid

        var user_num = 0;


        if ((oid === "0") || (oid === "") || (oid === null)|| (oid === undefined)) {
            this.editType = 'create';
            // $("#title").text("Create Logical Model")
            $("#subRteTitle").text("/"+this.htmlJson.CreateLogicalModel);

            $("#logicalModelText").html("");

            initTinymce('textarea#logicalModelText')

            if(this.draft.oid!=''&&this.draft.oid!=null&&typeof (this.draft.oid)!="undefined"){
                // this.loadDraftByOid()
                this.initDraft('create','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }else{
                this.loadMatchedCreateDraft();
            }
        }
        else{
            this.editType = 'modify';
            if(this.draft.oid==''||this.draft.oid==null||typeof (this.draft.oid)=="undefined"){
                this.initDraft('edit','/user/userSpace#/models/modelitem','item',this.$route.params.editId)
            }else{
                this.initDraft('edit','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }
            // $("#title").text("Modify Logical Model")
            $("#subRteTitle").text("/"+this.htmlJson.ModifyLogicalModel)
            document.title="Modify Logical Model | OpenGMS"

            if(window.localStorage.getItem('draft')==null){
                $.ajax({
                    url: "/logicalModel/itemInfo/" + oid,
                    type: "get",
                    data: {},

                    success: (result) => {
                        console.log(result)
                        var basicInfo = result.data;

                        this.insertInfo(basicInfo);





                    }
                })

            }
        }

        window.localStorage.removeItem('draft');

        $("#step").steps({
            onFinish: function () {
                // alert('Wizard Completed');
            },
            onChange: (currentIndex, newIndex, stepDirection) => {
                if (currentIndex === 0 && stepDirection === "forward") {
                    if (this.itemInfo.relateModelItem == ""||this.itemInfo.relateModelItem == null) {
                        new Vue().$message({
                            message: this.htmlJson.BindModelItemMessage,
                            type: 'warning',
                            offset: 70,
                        });
                        return false;
                    }
                    else if (this.itemInfo.name.trim() == "") {
                        new Vue().$message({
                            message: this.htmlJson.NameMessage,
                            type: 'warning',
                            offset: 70,
                        });
                        return false;
                    }else if (this.itemInfo.description.trim() == "") {
                        new Vue().$message({
                            message: this.htmlJson.overviewMessage,
                            type: 'warning',
                            offset: 70,
                        });

                        return false;
                    }
                    else {
                        if(this.draft.oid!='')
                            this.createDraft();
                        return true;
                    }
                }else{
                    return true;
                }
            }
        });


        $(".finish").click(()=>{
            this.formData=new FormData();
            let loading = this.$loading({
                lock: true,
                text: this.htmlJson.Uploading,
                spinner: "el-icon-loading",
                background: "rgba(0, 0, 0, 0.7)"
            });

            this.itemInfo = this.getItemContent('finish')


            //添加图片

            //重点在这里 如果使用 var data = {}; data.inputfile=... 这样的方式不能正常上传

            for(i=0;i<this.fileArray.length;i++){
                this.formData.append("imgFiles",this.fileArray[i]);
            }

            if ((oid === "0") || (oid === "") || (oid == null)) {
                let file = new File([JSON.stringify(this.itemInfo)],'ant.txt',{
                    type: 'text/plain',
                });
                this.formData.append("logicalModel", file)
                $.ajax({
                    url: '/logicalModel/',
                    type: 'POST',
                    data: this.formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true
                }).done((res)=> {
                    loading.close();

                    switch (res.data.code) {

                        case 1:
                            this.deleteDraft()
                            this.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.CreateLogicalModelSuccess +'</div>', this.htmlJson.Tip, {
                                dangerouslyUseHTMLString: true,
                                confirmButtonText: this.htmlJson.confirmButtonText,
                                cancelButtonText: this.htmlJson.cancelButtonText,
                                cancelButtonClass: 'fontsize-15',
                                confirmButtonClass: 'fontsize-15',
                                type: 'success',
                                center: true,
                                showClose: false,
                            }).then(() => {
                                window.location.href = "/logicalModel/" + res.data.id;
                            }).catch(() => {
                                window.location.href = "/user/userSpace#/models/logicalmodel";
                            });

                            break;
                        case -1:
                            this.$alert(this.htmlJson.SaveFilesError, this.htmlJson.Error, {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {
                                    
                                }
                            });
                            break;
                        case -2:
                            this.$alert(this.htmlJson.CreatedFailed, this.htmlJson.Error, {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                            break;
                    }
                }).fail(function (res) {
                    this.$alert(this.htmlJson.LoginInFirst, this.htmlJson.Error, {
                        type:"error",
                        confirmButtonText: 'OK',
                        callback: action => {
                            window.location.href = "/user/login";
                        }
                    });
                });
            }
            else{
                this.itemInfo.id=oid;
                this.itemInfo.resources=this.resources;

                let file = new File([JSON.stringify(this.itemInfo)],'ant.txt',{
                    type: 'text/plain',
                });

                this.formData.append("logicalModel", file)
                $.ajax({
                    url: '/logicalModel/',
                    type: 'put',
                    data: this.formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true
                }).done((res)=> {
                    loading.close();
                    console.log(res)

                    if(res.code===0) {
                        //返回包含code表示出错
                        if(typeof(res.data.code) !== "undefined"){
                            console.log("aaaaaa")
                            this.$alert(this.htmlJson.failed, this.htmlJson.Error, {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                        }else{
                            if(res.data.method==="update") {
                                this.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.UpdateLogicalModelSuccess +'</div>', this.htmlJson.Tip, {
                                    dangerouslyUseHTMLString: true,
                                    confirmButtonText: this.htmlJson.confirmButtonText,
                                    cancelButtonText: this.htmlJson.cancelButtonText,
                                    cancelButtonClass: 'fontsize-15',
                                    confirmButtonClass: 'fontsize-15',
                                    type: 'success',
                                    center: true,
                                    showClose: false,
                                }).then(() => {
                                    $("#editModal", parent.document).remove();
                                    window.location.href = "/logicalModel/" + res.data.id;
                                }).catch(() => {
                                    window.location.href = "/user/userSpace#/models/logicalmodel";
                                });
                            }else{
                                let currentUrl = window.location.href;
                                let index = currentUrl.lastIndexOf("\/");
                                that.logicalModel_oid = currentUrl.substring(index + 1,currentUrl.length);
                                console.log(that.logicalModel_oid);
                                //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
                                // that.getnoticeNum(that.logicalModel_oid);
                                // let params = that.message_num_socket;
                                // that.send(params);
                                this.$alert(this.htmlJson.ChangesHaveBeenSubmittedPleaseWaitForTheAuthorToReview, this.htmlJson.Success, {
                                    type:"success",
                                    confirmButtonText: 'OK',
                                    callback: action => {
                                        window.location.href = "/user/userSpace";
                                    }
                                });
                            }
                        }
                    }


                    // if(res.code===0) {
                    //     switch (res.data.code) {
                    //         case 0:
                    //             this.deleteDraft()
                    //             let currentUrl = window.location.href;
                    //             let index = currentUrl.lastIndexOf("\/");
                    //             that.logicalModel_oid = currentUrl.substring(index + 1,currentUrl.length);
                    //             console.log(that.logicalModel_oid);
                    //             //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
                    //             // that.getnoticeNum(that.logicalModel_oid);
                    //             // let params = that.message_num_socket;
                    //             // that.send(params);
                    //             this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
                    //                 type:"success",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //                     window.location.href = "/user/userSpace";
                    //                 }
                    //             });
                    //             break;
                    //         case 1:
                    //             this.$confirm('<div style=\'font-size: 18px\'>Update logical model successfully!</div>', 'Tip', {
                    //                 dangerouslyUseHTMLString: true,
                    //                 confirmButtonText: 'View',
                    //                 cancelButtonText: 'Go Back',
                    //                 cancelButtonClass: 'fontsize-15',
                    //                 confirmButtonClass: 'fontsize-15',
                    //                 type: 'success',
                    //                 center: true,
                    //                 showClose: false,
                    //             }).then(() => {
                    //                 $("#editModal", parent.document).remove();
                    //                 window.location.href = "/logicalModel/" + res.data.id;
                    //             }).catch(() => {
                    //                 window.location.href = "/user/userSpace#/models/logicalmodel";
                    //             });
                    //             break;
                    //         case -1:
                    //             this.$alert('Save files error', 'Error', {
                    //                 type:"error",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //
                    //                 }
                    //             });
                    //             break;
                    //         case -2:
                    //             this.$alert('Create failed!', 'Error', {
                    //                 type:"error",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //
                    //                 }
                    //             });
                    //             break;
                    //     }
                    // }
                    else{
                        this.$alert(res.msg, this.htmlJson.Error, {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {

                            }
                        });
                    }
                }).fail(function (res) {
                    this.$alert(this.htmlJson.LoginInFirst, this.htmlJson.Error, {
                        type:"error",
                        confirmButtonText: 'OK',
                        callback: action => {
                            window.location.href = "/user/login";
                        }
                    });
                });
            }
        })

        // $(".prev").click(()=>{
        //     let currentUrl = window.location.href;
        //     let index = currentUrl.lastIndexOf("\/");
        //     that.logicalModel_oid = currentUrl.substring(index + 1,currentUrl.length);
        //     console.log(that.logicalModel_oid);
        //     //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
        //     that.getnoticeNum(that.logicalModel_oid);
        //     let params = that.message_num_socket;
        //     that.send(params);
        // });


        $("input[name='ContentType']").iCheck({
            //checkboxClass: 'icheckbox_square-blue',  // 注意square和blue的对应关系
            radioClass: 'iradio_flat-green',
            increaseArea: '0%' // optional

        });
        $("input[name='author_confirm']").iCheck({
            //checkboxClass: 'icheckbox_square-blue',  // 注意square和blue的对应关系
            radioClass: 'iradio_flat-green',
            increaseArea: '0%' // optional

        });

        //content type radio
        $("input[name='ContentType']").eq(0).iCheck('check');

        $("input:radio[name='ContentType']").on('ifChecked', function(event){

            if($(this).val()=="MxGraph"){
                $("#MxGraph").show();
                $("#Image").hide();
            }
            else{
                $("#MxGraph").hide();
                $("#Image").show();
            }

        });
        //author radio
        $("input[name='author_confirm']").eq(0).iCheck('check');

        $("input:radio[name='author_confirm']").on('ifChecked', function(event){
            if($(this).val()=="true"){

                $("#author_info").hide();
            }
            else{

                $("#author_info").show();
            }

        });

        $(document).on("click", ".author_close", function () { $(this).parents(".panel").eq(0).remove(); });

        //作者添加
        var user_num = 1;
        $(".user-add").click(function () {
            user_num++;
            var content_box = $(this).parent().children('div');
            var str = "<div class='panel panel-primary'> <div class='panel-heading newAuthorHeader'> <h4 class='panel-title'> <a class='accordion-toggle collapsed' style='color:white' data-toggle='collapse' data-target='#user";
            str += user_num;
            str += "' href='javascript:;'> " + that.htmlJson.authorshipPart.NEW + " </a> </h4><a href='javascript:;' class='fa fa-times author_close' style='float:right;margin-top:8px;color:white'></a></div><div id='user";
            str += user_num;
            str += "' class='panel-collapse collapse in'><div class='panel-body user-contents'> <div class='user-attr'>\n" +
                "                                                                                                    <div>\n" +
                "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                "                                                                                                               style='font-weight: bold;'>\n" +
                that.htmlJson.authorshipPart.NEW + ":\n" +
                "                                                                                                        </lable>\n" +
                "                                                                                                        <div class='input-group col-sm-10'>\n" +
                "                                                                                                            <input type='text'\n" +
                "                                                                                                                   name=\"name\"\n" +
                "                                                                                                                   class='form-control'>\n" +
                "                                                                                                        </div>\n" +
                "                                                                                                    </div>\n" +
                "                                                                                                    <div style=\"margin-top:10px\">\n" +
                "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                "                                                                                                               style='font-weight: bold;'>\n" +
                that.htmlJson.authorshipPart.Affiliation + ":\n" +
                "                                                                                                        </lable>\n" +
                "                                                                                                        <div class='input-group col-sm-10'>\n" +
                "                                                                                                            <input type='text'\n" +
                "                                                                                                                   name=\"ins\"\n" +
                "                                                                                                                   class='form-control'>\n" +
                "                                                                                                        </div>\n" +
                "                                                                                                    </div>\n" +
                "                                                                                                    <div style=\"margin-top:10px\">\n" +
                "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                "                                                                                                               style='font-weight: bold;'>\n" +
                that.htmlJson.authorshipPart.Email + ":\n" +
                "                                                                                                        </lable>\n" +
                "                                                                                                        <div class='input-group col-sm-10'>\n" +
                "                                                                                                            <input type='text'\n" +
                "                                                                                                                   name=\"email\"\n" +
                "                                                                                                                   class='form-control'>\n" +
                "                                                                                                        </div>\n" +
                "                                                                                                    </div>\n" +
                "                                                                                                    <div style=\"margin-top:10px\">\n" +
                "                                                                                                        <lable class='control-label col-sm-2 text-center'\n" +
                "                                                                                                               style='font-weight: bold;'>\n" +
                that.htmlJson.authorshipPart.Homepage + ":\n" +
                "                                                                                                        </lable>\n" +
                "                                                                                                        <div class='input-group col-sm-10'>\n" +
                "                                                                                                            <input type='text'\n" +
                "                                                                                                                   name=\"homepage\"\n" +
                "                                                                                                                   class='form-control'>\n" +
                "                                                                                                        </div>\n" +
                "                                                                                                    </div>\n" +
                "                                                                                                </div></div> </div> </div>"
            content_box.append(str)
        })

        const timer = setInterval(()=>{
            if(this.itemName!=''&&this.startDraft==1){
                this.createDraft()
            }
        },30000)

        this.$once('hook:beforeDestroy', ()=>{
            clearInterval(timer)
            clearTimeout(this.timeOut)
        })

        // if (mid === undefined || mid == null) {
        //     this.editorUrl = "http://127.0.0.1:8081http://127.0.0.1:8081/GeoModelingNew/modelItem/createModelItem.html";
        // } else {
        //     this.editorUrl = "http://127.0.0.1:8081http://127.0.0.1:8081/GeoModelingNew/modelItem/createModelItem.html?mid=" + mid;
        // }
    }
})