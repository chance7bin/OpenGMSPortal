var createComputableModel = Vue.extend({
    template: "#createComputableModel",
    props:['htmlJson'],
    data() {
        return {
            defaultActive: '2-4',
            curIndex: '2',

            itemInfo: {
                status:"Public",
                relateModelItemName: "",
                relateModelItem: "",
                name: "",
                image: "",
                description: "",
                contentType: "Package",
                url: "",
                modelserUrl:'',
                isAuthor: true,
                author: {
                    name: "",
                    ins: "",
                    email: "",
                },
                md5:"",
                mdl:"",
                deploy:false
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

            //文件框选择
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

            computableModel_oid:"",

            editType:'create',

            toCreate: 0,

            debounceTimeout:null,

            draft:{
                oid:'',
            },

            draftOid:'',

            startDraft:0,

            exisedServiceDialog:false,

            exisedServices:[],

            customAddMd5:false,

            publicModelContainerList: [{
                ip: '172.21.213.105',
                port:8060,
                geoInfo: {
                    city: "Nanjing",
                    countryCode: "CN",
                    countryName: "China",
                    latitude: "32.0617",
                    longitude: "118.7778",
                    region: "Jiangsu",
                },
                hardware: {
                    cpu_Core: 8,
                    diskAll: "300G",
                    diskAvailable: "280G",
                    platform: "Windows",
                    system: "Windows Server",
                    totalMemory: "8G",
                    version: "2012",
                },
                status: true
            },
                {
                    ip: '172.21.212.78',
                    port:8060,
                    geoInfo: {
                        city: "Nanjing",
                        countryCode: "CN",
                        countryName: "China",
                        latitude: "32.0617",
                        longitude: "118.7778",
                        region: "Jiangsu",
                    },
                    hardware: {
                        cpu_Core: 4,
                        diskAll: "400G",
                        diskAvailable: "387G",
                        platform: "Windows",
                        system: "Windows Server",
                        totalMemory: "8G",
                        version: "2012",
                    },
                    status: false
                },
                {
                    ip: '172.21.213.50',
                    port:8060,
                    geoInfo: {
                        city: "Nanjing",
                        countryCode: "CN",
                        countryName: "China",
                        latitude: "32.0617",
                        longitude: "118.7778",
                        region: "Jiangsu",
                    },
                    hardware: {
                        cpu_Core: 8,
                        diskAll: "120G",
                        diskAvailable: "75G",
                        platform: "Linux",
                        system: "CentOS",
                        totalMemory: "4G",
                        version: "7",
                    },
                    status: false
                },
                {
                    ip: '172.21.213.50',
                    port:8060,
                    geoInfo: {
                        city: "Nanjing",
                        countryCode: "CN",
                        countryName: "China",
                        latitude: "32.0617",
                        longitude: "118.7778",
                        region: "Jiangsu",
                    },
                    hardware: {
                        cpu_Core: 8,
                        diskAll: "120G",
                        diskAvailable: "75G",
                        platform: "Linux",
                        system: "Ubuntu",
                        totalMemory: "4G",
                        version: "7",
                    },
                    status: false
                }

            ],

            currentStep:0,

            dataDialogVisible:false,

        }
    },

    watch:{
        // 中英文切换
        htmlJson:function(newData){
            $("#subRteTitle").text("/" + newData.CreateComputableModel);
        }
    },

    methods: {
        singleFileInputBindEvent(){
            $("#file").on("change", ()=> {
                this.fileArray=[];
                this.resources=[];
                let files=$("#file")[0].files;
                let file=files[0];


                let res={};
                res.name=file.name;
                res.path="";
                let names=res.name.split('.');
                res.suffix=names[names.length-1];
                res.size=file.size;
                res.lastModified=file.lastModified;
                res.type=file.type;

                if(res.suffix!=='zip'){
                    this.$message({
                        message: 'Please select a zip file!',
                        type: 'error',
                        offset: 70,
                    });
                }else{
                    this.fileArray.push(file);
                    this.resources.push(res);
                }

                //清空
                document.getElementById("file").value='';

            });
        },
        multiFileInputBindEvent(){
            $("#file_multi").on("change", ()=> {
                let files=$("#file_multi")[0].files;
                for(i=0;i<files.length;i++){
                    let file=files[i];

                    let res={};
                    res.name=file.name;
                    res.path="";
                    let names=res.name.split('.');
                    res.suffix=names[names.length-1];
                    res.size=file.size;
                    res.lastModified=file.lastModified;
                    res.type=file.type;
                    let exist = false;
                    for(j=0;j<this.fileArray.length;j++) {
                        let fileExist = this.fileArray[j];
                        if(fileExist.name==file.name&&fileExist.lastModified == file.lastModified&&fileExist.size == file.size && fileExist.type == file.type){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        this.fileArray.push(file);
                        this.resources.push(res);
                    }
                }


                //清空
                let file=document.getElementById("file_multi");
                file.value='';
            });
        },
        contentTypeChange(type){
            this.fileArray=[];
            this.resources=[];
            if(type==="Package"){
                this.singleFileInputBindEvent();
            }else if(type==="Code"||type==="Library"){
                this.multiFileInputBindEvent();
            }

        },
        selectModelItem(index,info){
            console.log(info);
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

                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },

        changeRter(index){
            this.curIndex = index;
            var urls={
                1:'/user/userSpace',
                2:'/user/userSpace/model',
                3:'/user/userSpace/data',
                4:'/user/userSpace/server',
                5:'/user/userSpace/task',
                6:'/user/userSpace/community',
                7:'/user/userSpace/theme',
                8:'/user/userSpace/account',
                9:'/user/userSpace/feedback',
            }

            this.setSession('curIndex',index)
            window.location.href=urls[index]

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

        selectDataspaceFile(file){
            console.log(file);
        },

        handleClose(done) {
            this.$confirm('Confirm to close?')
                .then(_ => {
                    done();
                })
                .catch(_ => {
                });
        },

        addFile(){

            // this.dataDialogVisible = true;
            // this.$nextTick(()=>{
            //     this.$refs.userDataSpace.getFilePackage();
            // })

            if(this.itemInfo.contentType == "Package"){
                $("#file").click();
            }else{
                $("#file_multi").click();
            }


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
            $("#file").click();
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
        formatDate(value,callback) {
            const date = new Date(value);
            y = date.getFullYear();
            M = date.getMonth() + 1;
            d = date.getDate();
            H = date.getHours();
            m = date.getMinutes();
            s = date.getSeconds();
            if (M < 10) {
                M = '0' + M;
            }
            if (d < 10) {
                d = '0' + d;
            }
            if (H < 10) {
                H = '0' + H;
            }
            if (m < 10) {
                m = '0' + m;
            }
            if (s < 10) {
                s = '0' + s;
            }

            const t = y + '-' + M + '-' + d + ' ' + H + ':' + m + ':' + s;
            if(callback == null||callback == undefined)
                return t;
            else
                callback(t);
        },
        getServiceByMd5(){
            this.exisedServices = []
            if(this.itemInfo.md5=='')
                return
            let flag
            $.ajax({
                url:"/computableModel/findAllByMd5",
                type:"GET",
                async:false,
                data:{
                    md5:this.itemInfo.md5
                },
                success:(res) => {
                    if (res.code != 0)
                        return

                    if (res.data.length != 0){
                        this.exisedServiceDialog = true
                        this.exisedServices = res.data
                        flag = false
                    }else{
                        this.$message({
                            message: 'No existed service of this MD5',
                        });
                        flag = true
                    }
                }

            })

            return flag;
        },

        checkMd5Deployed(){
            if(this.itemInfo.md5=='')
                return
            let flag
            $.ajax({
                url:"/computableModel/checkDeployed",
                type:"GET",
                async:false,
                data:{
                    md5:this.itemInfo.md5
                },
                success:(res) => {
                    if (res.code != 0)
                        return

                    if (res.data){
                        flag = true
                        this.$message({
                            message: 'Services of this MD5 have been deployed before!',
                            type:"success"
                        });
                    }else{
                        this.$alert('No existed service of this MD5, please deploy model in the model container first!', 'Tip', {
                                 type:"warning",
                                 confirmButtonText: 'OK',
                                 callback: ()=>{
                                     return
                                 }
                             }
                         );
                        flag = false
                    }
                }

            })

            return flag;
        },

        getServiceByMd5Check(){
            // let res = this.getServiceByMd5()
            let res = this.checkMd5Deployed()
            return res
        },

        cancelMd5(){
            this.itemInfo.md5 = ''
            this.exisedServiceDialog = false
        },
        inputMd5(){
            this.customAddMd5 = false
        },

        addMd5(){
            this.exisedServiceDialog = false
            this.customAddMd5 = true
            $(".next").click()
        },

        getnoticeNum(computableModel_oid){
            this.message_num_socket = 0;//初始化消息数目
            let data = {
                type: 'computableModel',
                oid : computableModel_oid,
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
                type: 'computableModel',
                oid : computableModel_oid,
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
            if(basicInfo.resourceJson!=null)
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

            this.itemInfo.relateModelItemName = basicInfo.relatedModelItemInfoList[0].name
            this.itemInfo.relateModelItem = basicInfo.relatedModelItemInfoList[0].id
            $("#search-box").val(basicInfo.relatedModelItemInfoList[0].name)

            this.itemInfo.status=basicInfo.status;
            this.itemInfo.md5=basicInfo.md5;
            this.itemInfo.mdl=basicInfo.mdl;
            this.itemInfo.url=basicInfo.url;

            this.itemInfo.contentType = basicInfo.contentType;

            $(".providers").children(".panel").remove();

            //detail
            $("#computableModelText").html(basicInfo.localizationList[0].description);

            initTinymce('textarea#computableModelText')

            let user_num = 0;
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

            itemObj.relatedModelItems = [this.itemInfo.relateModelItem]

            itemObj.status = this.itemInfo.status
            itemObj.name = this.itemInfo.name
            itemObj.image = this.itemInfo.image
            itemObj.description = this.itemInfo.description
            itemObj.contentType = this.itemInfo.contentType
            itemObj.url = this.itemInfo.url
            itemObj.modelserUrl = this.itemInfo.modelserUrl
            itemObj.md5 = this.itemInfo.md5
            itemObj.mdl = this.itemInfo.mdl
            itemObj.pageConfig = this.itemInfo.pageConfig;

            itemObj.isAuthor=$("input[name='author_confirm']:checked").val();

            var detail = tinyMCE.activeEditor.getContent();
            itemObj.detail = detail.trim();

            itemObj.authorships=[];
            userspace.getUserData($("#providersPanel .user-contents .form-control"), itemObj.authorships);


            if(callBack){
                callBack(itemObj)
            }

            return itemObj;
        },

        selectModelServer(container){
            if(!container.status){
                this.$message({
                    message: 'Server Offline!',
                    offset: 70,
                });
                return
            }
            if(this.itemInfo.modelserUrl.split(':')[0] == container.ip){//如果选中则取消选择
                this.itemInfo.modelserUrl = ''
                this.$message({
                    message: 'Cancel select server!',
                    offset: 70,
                });
            }else{
                this.itemInfo.modelserUrl = container.ip + ':' +container.port
                this.$message({
                    message: 'Succeed to select server!',
                    type:'success',
                    offset: 70,
                });
            }
        },

        //image
        getImgContent(img){//从img-upload获得图片，该函数由组件触发
            this.itemInfo.image = img
        },

        insertImage(){
            this.$refs.imgUpload.insertImage(this.itemInfo.image)
        },

        //draft
        getDraft(){
            return this.$refs.draftBox.getDraft();
        },

        insertDraft(draftContent){
            this.insertInfo(draftContent)
        },

        onInputName(){
            console.log(1)
            if(this.toCreate==0){
                if(this.debounceTimeout!=null){

                    clearTimeout(this.debounceTimeout)
                }
                this.debounceTimeout = setTimeout(()=>{
                    this.createDraft()
                    this.toCreate = 1
                },1100)
            }


            if(this.toCreate==1){
                this.toCreate=0
                this.timeOut=setTimeout(()=>{
                    this.toCreate=1
                    this.startDraft=1
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

            let urls=window.location.href.split('/')
            let item=urls[6]
            item=item.substring(6,item.length)
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

        cancelEditClick(){
            if(this.getDraft()!=null){
                this.$refs.draftBox.cancelDraftDialog=true
            }else{
                setTimeout(() => {
                    window.location.href = "/user/userSpace#/models/computablemodel";
                }, 305)
            }
        },

        submitEdit(){
            $(".finish").click()
        },

        draftJump(){
            window.location.href = '/user/userSpace#/models/computablemodel';
        },

        ///
    },
    mounted() {
        let that = this;
        that.init();

        //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
        this.sendcurIndexToParent()

        $(() => {
            //修改页面元素尺寸
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
                success: (data) => {

                    console.log(data);

                    if (data.email == "") {
                        alert(this.htmlJson.LoginInFirst);
                        window.location.href = "/user/login";
                    } else {
                        this.userId = data.oid;
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

        this.singleFileInputBindEvent();
        this.multiFileInputBindEvent();


        $.ajax({
            type: "GET",
            url: "/user/load",
            data: {

            },
            cache: false,
            async: false,
            xhrFields:{
                withCredentials: true
            },
            crossDomain:true,
            success: (data) => {
                if (data.email == "") {
                    alert(this.htmlJson.LoginInFirst);
                    window.location.href = "/user/login";
                }
                else{
                    this.userId=data.uid;
                    this.userName=data.name;

                    var relateModelItem=this.getSession("relateModelItem");
                    this.itemInfo.relateModelItem=relateModelItem;
                    $.ajax({
                        type: "Get",
                        url: "/modelItem/getInfo/"+relateModelItem,
                        data: { },
                        cache: false,
                        async: true,
                        success: (json) => {
                            if(json.data!=null){

                                this.itemInfo.relateModelItemName=json.data.name;
                                this.clearSession();
                            }
                            else{

                            }
                        }
                    })
                }
            }
        })

        var oid = this.$route.params.editId;//取得所要edit的id
        this.draft.oid=window.localStorage.getItem('draft');//取得保存的草稿的Oid


        if ((oid === "0") || (oid === "") || (oid === null)|| (oid === undefined)) {

            $("#subRteTitle").text("/" + this.htmlJson.CreateComputableModel)

            initTinymce('textarea#computableModelText')

            if(this.draft.oid!=''&&this.draft.oid!=null&&typeof (this.draft.oid)!="undefined"){
                // this.loadDraftByOid()
                this.initDraft('create','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }else{
                this.loadMatchedCreateDraft();
            }
        }
        else {

            this.editType = 'modify';
            if(this.draft.oid==''||this.draft.oid==null||typeof (this.draft.oid)=="undefined"){
                this.initDraft('edit','/user/userSpace#/models/modelitem','item',this.$route.params.editId)
            }else{
                this.initDraft('edit','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }
            $("#subRteTitle").text("/Modify Computable Model")
            // document.title="Modify Computable Model | OpenGMS"
            if(window.localStorage.getItem('draft')==null){
                $.ajax({
                    url: "/computableModel/itemInfo/" + oid,
                    type: "get",
                    data: {},

                    success: (result) => {
                        window.sessionStorage.setItem("editComputableModel_id", "");
                        console.log(result)
                        var basicInfo = result.data;

                        this.insertInfo(basicInfo)



                    }
                })

            }
        }

        window.localStorage.removeItem('draft');

        $("#step").steps({
            onFinish: function () {
                alert('Wizard Completed');
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
                }
                else if(currentIndex === 1 && stepDirection === "forward"){
                    if(this.itemInfo.contentType=="Package"||this.itemInfo.contentType=="Code"||this.itemInfo.contentType=="Library"){
                        if(this.fileArray.length==0&&this.resources.length==0){
                            new Vue().$message({
                                message: this.htmlJson.PleaseSelectAtLeastOneFile,
                                type: 'warning',
                                offset: 70,
                            });
                            return false;
                        }else{
                            return true;
                        }
                        // if(this.itemInfo.contentType=="Package"&&this.itemInfo.modelserUrl==''){
                        //     new Vue().$message({
                        //         message: 'Please select a model server!',
                        //         type: 'warning',
                        //         offset: 70,
                        //     });
                        //     return false;
                        // }
                    }
                    if(this.itemInfo.contentType=="Service"||this.itemInfo.contentType=="Link"){
                        if(this.itemInfo.url==""){
                            new Vue().$message({
                                message: this.htmlJson.PleaseEnterURL,
                                type: 'warning',
                                offset: 70,
                            });
                            return false;
                        }else {
                            return true
                        }
                    }else if(this.itemInfo.contentType.toLowerCase()=="md5"&&newIndex==2){
                        if(this.itemInfo.md5!=''&&this.itemInfo.mdl!=''){
                            if(this.customAddMd5)
                                return true
                            return  this.getServiceByMd5Check()
                        }else if(this.itemInfo.md5==''){
                            new Vue().$message({
                                message: this.htmlJson.PleaseEnterMd5,
                                type: 'warning',
                                offset: 70,
                            });
                            return false
                        }else if(this.itemInfo.mdl==''){
                            new Vue().$message({
                                message: this.htmlJson.PleaseEnterMdl,
                                type: 'warning',
                                offset: 70,
                            });
                            return false
                        }
                    } else{
                        if(this.draft.oid!='')
                            this.createDraft();
                        return true;
                    }

                }
                else{
                    return true;
                }
            }
        });


        $(".finish").on('click',()=>{
            this.formData=new FormData();

            if(this.itemInfo.relateModelItemName==""){
                alert("Please bind a model item")
                return;
            }
            if(this.itemInfo.name.trim()==""){
                alert("Please enter name")
                return;
            }


            let loading = this.$loading({
                lock: true,
                text: this.htmlJson.Uploading,
                spinner: "el-icon-loading",
                background: "rgba(0, 0, 0, 0.7)"
            });


            // //重点在这里 如果使用 var data = {}; data.inputfile=... 这样的方式不能正常上传

            // var files=$("#resource")[0].files;
            for(i=0;i<this.fileArray.length;i++){
                this.formData.append("resources",this.fileArray[i]);
            }

            this.itemInfo = this.getItemContent();
            if ((oid === "0") || (oid === "") || (oid == null)) {

                let file = new File([JSON.stringify(this.itemInfo)],'ant.txt',{
                    type: 'text/plain',
                });
                this.formData.append("computableModel", file)


                // $("#step").css("display", "none");
                // $(".uploading").css("display", "block");

                $.ajax({
                    url: '/computableModel/',
                    type: 'post',
                    data: this.formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true
                }).done((res) => {
                    loading.close();
                    switch (res.data.code) {
                        case 1:
                            this.deleteDraft()
                            this.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.CreateConceptualModelSuccess +'</div>', this.htmlJson.Tip, {
                                dangerouslyUseHTMLString: true,
                                confirmButtonText: this.htmlJson.confirmButtonText,
                                cancelButtonText: this.htmlJson.cancelButtonText,
                                cancelButtonClass: 'fontsize-15',
                                confirmButtonClass: 'fontsize-15',
                                type: 'success',
                                center: true,
                                showClose: false,
                            }).then(() => {
                                window.location.href = "/computableModel/" + res.data.id;
                            }).catch(() => {
                                window.location.href = "/user/userSpace#/models/computablemodel";
                            });

                            break;
                        case -1:
                            this.$alert('Save files error!', 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                            break;
                        case -2:
                            this.$alert('Created failed!', 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                            break;
                    }
                }).fail((res) => {
                    window.location.href = "/user/login";
                });
            }
            else{
                this.itemInfo.id=oid;

                // for(i=0;i<this.fileArray.length;i++){
                //     this.formData.append("resources",this.fileArray[i]);
                // }

                let file = new File([JSON.stringify(this.itemInfo)],'ant.txt',{
                    type: 'text/plain',
                });
                this.formData.append("computableModel", file)

                $("#step").css("display", "none");
                $(".uploading").css("display", "block");

                $.ajax({
                    url: '/computableModel/',
                    type: 'put',
                    data: this.formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true
                }).done((res) => {
                    loading.close();
                    // $("#step").css("display", "block");
                    // $(".uploading").css("display", "none");


                    if(res.code===0) {
                        //返回data包含code表示出错
                        if(typeof(res.data.code) !== "undefined"){
                            this.$alert('failed!', 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                        }else{
                            if(res.data.method==="update") {
                                // this.deleteDraft()
                                this.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.UpdateComputableModelSuccess +'</div>', this.htmlJson.Tip, {
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
                                    window.location.href = "/computableModel/" + res.data.id;
                                }).catch(() => {
                                    window.location.href = "/user/userSpace#/models/computablemodel";
                                });
                            }else{
                                let currentUrl = window.location.href;
                                let index = currentUrl.lastIndexOf("\/");
                                that.computableModel_oid = currentUrl.substring(index + 1,currentUrl.length);
                                console.log(that.computableModel_oid);
                                //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
                                // that.getnoticeNum(that.computableModel_oid);
                                // let params = that.message_num_socket;
                                // that.send(params);
                                this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
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
                    //             let currentUrl = window.location.href;
                    //             let index = currentUrl.lastIndexOf("\/");
                    //             that.computableModel_oid = currentUrl.substring(index + 1,currentUrl.length);
                    //             console.log(that.computableModel_oid);
                    //             //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
                    //             // that.getnoticeNum(that.computableModel_oid);
                    //             // let params = that.message_num_socket;
                    //             // that.send(params);
                    //             this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
                    //                 type:"success",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //                     window.location.href = "/user/userSpace";
                    //                 }
                    //             });
                    //         case 1:
                    //             this.deleteDraft()
                    //             this.$confirm('<div style=\'font-size: 18px\'>Update computable model successfully!</div>', 'Tip', {
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
                    //                 window.location.href = "/computableModel/" + res.data.id;
                    //             }).catch(() => {
                    //                 window.location.href = "/user/userSpace#/models/computablemodel";
                    //             });
                    //             break;
                    //         case -1:
                    //             this.$alert('Save files error', 'Error', {
                    //                 type:"error",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //                     $("#step").css("display", "block");
                    //                     $(".uploading").css("display", "none");
                    //                 }
                    //             });
                    //
                    //             break;
                    //         case -2:
                    //             this.$alert('Create failed!', 'Error', {
                    //                 type:"error",
                    //                 confirmButtonText: 'OK',
                    //                 callback: action => {
                    //                     $("#step").css("display", "block");
                    //                     $(".uploading").css("display", "none");
                    //                 }
                    //             });
                    //
                    //             break;
                    //     }
                    // }
                    else{
                        this.$alert(res.msg, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                $("#step").css("display", "block");
                                $(".uploading").css("display", "none");
                            }
                        });

                    }
                }).fail((res) => {
                    this.$alert(this.htmlJson.LoginInFirst, 'Error', {
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
        //     that.computableModel_oid = currentUrl.substring(index + 1,currentUrl.length);
        //     console.log(that.computableModel_oid);
        //     //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
        //     that.getnoticeNum(that.computableModel_oid);
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

            if($(this).val()=="Package"){
                $("#resource").val("");
                $("#resource").attr("accept","application/x-zip-compressed");
                $("#resource").removeAttr("multiple");
                $("#Files").show();
                $("#URL").hide();
            }
            else if($(this).val()=="Code"||$(this).val()=="Library"){
                $("#resource").val("");
                $("#resource").removeAttr("accept");
                $("#resource").attr("multiple","multiple");
                $("#Files").show();
                $("#URL").hide();
            }
            else{
                $("#resource").val("");
                $("#Files").hide();
                $("#URL").show();
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

        var mid = window.sessionStorage.getItem("editConceptualModel_id");
        // if (mid === undefined || mid == null) {
        //     this.editorUrl = "http://127.0.0.1:8081http://127.0.0.1:8081/GeoModelingNew/modelItem/createModelItem.html";
        // } else {
        //     this.editorUrl = "http://127.0.0.1:8081http://127.0.0.1:8081/GeoModelingNew/modelItem/createModelItem.html?mid=" + mid;
        // }
    }
})