var vue = new Vue({
    el: "#app",
    data: {
        htmlJSON:{
            selectModel:"Select Computable Model",
            contributedBy:"Contributed by:",
            loadData:"Load data",
            invoke:"invoke",
            introduction:"Introduction",
            contributedAt:"Contributed at ",
            isRunning:"A new task of this model is running.",
            checkinSpace:"Check in my space",
            datasetForModel:"There is a dataset for you to test this model.",
            loadTestData:"Load Test Dataset",
            ok:"OK",
            configureExecution:"Configure Execution",
            showStyle:"Show style:",
            classic:"Classic",
            semantics:"Semantics",
            workFlow:"Work flow",
            input:"Input",
            dataType:"Data Type:",
            publicExample:"Public Example",
            myTaskData:"My Task Data",
            user:"User",
            runTime:"Run Time",
            description:"Description",
            operation:"Operation",
            load:"Load",
            Parameter:"Parameter",
            Value:"Value",
            Type:"Type",
            EventName:"Event Name",
            UserDataSpace:"User Data Space",
            PublicDataCenter:"Public Data Center",
            Name:"Name :",
            Datatemplate :"Data template :",
            Storagepath:"Storage path :",
            Uploadfiles :"Upload files :",
            SelectFile:"Select File",
            file:"file",
            url:"url",
            SearchDataItem:"Search Data Item",
            nameNone:"Name",
            Contributor:"Contributor",
            CreateTime:"Create Time",
            RelatedDataItem:"Related Data Item",
            Link:"Link",
            Option:"Option",
            Cancel:"Cancel",
            Confirm:"Confirm",
            SelectFolder:"Select Folder",
            Addnewfolder:"Add new folder",

            Output:"Output",



        },
        computableId: "",
        LoadDataActiveName:"public",

        initializing:true,

        radioStyle: "Classic",
        semanticsActiveStates: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],

        tableLoading: true,
        first: true,
        activeIndex: "3-2",
        activeNameGraph: "Image",
        activeName: "Invoke",
        info: {
            dxInfo: {},
            modelInfo: {
                dataRefs:[],
                date: "",
                des: "",
                hasTest: true,
                name: "Model",
                states:[{
                    Id:'',
                    desc:'',
                    event:[{
                        data:[{
                            Id: "",
                            dataType: "",
                            desc: "",
                            nodes: undefined,
                            parentId: "",
                            text: "",
                        }],
                        eventDesc: "",
                        eventId: "",
                        eventName: "",
                        eventType: "response",
                        optional: false
                    },
                        {
                            data:[{
                                Id: "",
                                dataType: "",
                                desc: "",
                                nodes: undefined,
                                parentId: "",
                                text: "",
                            }],
                            eventDesc: "",
                            eventId: "",
                            eventName: "",
                            eventType: "noresponse",
                            optional: false
                        }]
                }

                ]
            },
            taskInfo: {},
            userInfo: {
                compute_model_userId: "",
                compute_model_user_name: "",
                userName: ""
            }
        },
        showUpload: false,
        showDataChose: false,

        activeDataSpaceName:"user",

        total: 100,
        dataFromDataContainer: [
            {
                createDate: "2016-05-02",
                name: "test",
                type: "OTHER",
                sourceStoreId: "123123"
            },
            {
                createDate: "2016-05-02",
                name: "test2",
                type: "SHAPEFILE",
                sourceStoreId: "123123"
            },
            {
                createDate: "2016-05-02",
                name: "test",
                type: "GEOTIFF",
                sourceStoreId: "123123"
            }
        ],

        relatedDataList: {},

        exampleDataListOfUser: {
            content: [
                {
                    userName: '',
                    runTime: '',
                    taskId: '',
                    description: '',
                    public: [],
                    status: '',
                    currentPage: 1,
                }
            ],
            total: 0,
        },

        exampleDataList: {
            content: [
                {
                    userName: '',
                    runTime: '',
                    description: '',
                    public: [],
                    status: '',
                    currentPage: 1,
                }
            ],
            total: 0,
        },

        inEvent: [],
        outEvent: [],
        modelId: null,

        fileList: [],

        //select data from user
        selectDataDialog: false,
        userOid: '',
        loading: false,
        userData: [],
        totalNum: '',
        page: 1,
        pageSize: 10,
        sortAsc: false,
        selectData: [],
        keyInput: '',
        modelInEvent: {},
        isFixed: false,
        introHeight: 1,

        dataChosenIndex: 1,
        detailsIndex: 1,
        managerloading: true,
        userTaskInfo: [{
            content: {},
        }],

        downloadDataSet: [],
        downloadDataSetName: [],
        packageContent: {},
        userInfo: {
            runTask: [
                {}
            ]
        },
        searchcontent: '',
        databrowser: [],

        rightMenuShow: false,


        dataItemVisible: false,
        categoryTree: [],
        classifications: [],
        dataItemSearchText: '',
        currentData: {},
        pageOption: {
            page: 0,
            pageSize: 5,
            asc: false,
            searchResult: [],
            total: 0,
        },

        relatePageOption: {
            page: 0,
            pageSize: 5,
            asc: false,
            searchResult: [],
            total: 0,
        },

        loadDataVisible: false,

        showDescriptionVisible: false,

        taskDescription: '',

        fileSpaceIndex: 1,

        myFile: [],

        myFileShown: [
            {
                children: [],
            }
        ],

        fatherIndex: '',

        pathShown: [],

        clickTimeout: 1000,

        rotatevalue: 0,

        fileSearchResult: [],

        loadjson: '',

        loadDataIndex: 1,

        uploadDialogVisible: false,
        selectFolderVisible: false,
        uploadFileList: [],
        selectedPath: [],
        uploadInPath: "",
        folderTree: [],

        multiFileDialog:false,
        outputMultiFile:[
            {
                name:'',
                url:''
            }
        ],
        downloadUrl:'',
        shareIndex:false,

        //uploadForm
        uploadName: "",
        selectLoading: false,
        options: [],
        selectValue: "",
        uploadFiles: [],
        uploadLoading: false,

        visualVisible:false,
        visualSrc:"",
        clipBoard :'',

        invokable:true,
        errorMsg:'',

        taskRunning:false,

        loadDeployedModelDialog: false,
        deployedModel:[{
            name:'',
        }],
        deployedModelCount:0,
        pageOption: {
            paginationShow:false,
            progressBar: true,
            sortAsc: false,
            currentPage: 1,
            pageSize: 10,

            total: 11,
            searchResult: [],
        },
        searchText:'',
        modelTableLoading:false,
        selectModelPage:false,
        eventChoosing:{},
        mainContainerHeight:'',

        modelLoaded:false,

        recentlyUsed:[
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-16T13:25:55.947+0000",
                lastModifyTime: "2019-05-17T13:25:55.947+0000",
                md5: "15ea8a5740fdcfa951eea30579a33c4d",
                name: "SWAT_Model",
                id: "16e31602-bd05-4da4-bd01-cb7582c21ae8",
            },
            {
                author: "Qiang Dai",
                authorId: "Qiang_Dai",
                createTime: "2019-05-16T14:43:40.834+0000",
                lastModifyTime: "2019-05-16T14:43:40.834+0000",
                md5: "387e28f188717c4745a071356839ecb0",
                name: "SWMM",
                id: "35c12565-6a5f-41e1-8e4b-5a3c7d256b03",
            },
            {
                author: "Bo Huang",
                authorId: "Bo_Huang",
                createTime: "2019-05-16T14:43:03.277+0000",
                lastModifyTime: "2019-05-16T14:43:03.277+0000",
                name: "TouchAir",
                id: "ff844894-bbb5-492c-b72f-a7e2fa651d57",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-16T14:56:38.504+0000",
                lastModifyTime: "2019-05-16T14:56:38.504+0000",
                name: "Fire Dynamics Simulator",
                id: "fe6beeac-d4fa-4685-a7fa-3fc58dfb59d3",
            },
            {
                author: "Junzhi Liu",
                authorId: "Junzhi_Liu",
                createTime: "2019-06-17T14:03:22.373+0000",
                lastModifyTime: "2019-06-17T14:03:22.373+0000",
                name: "SEIMS",
                id: "39daf1cf-acdb-4ea0-875b-9963bb80b887",
            },
            {
                author: "Xia Li",
                authorId: "Xia_Li",
                createTime: "2019-05-16T07:30:44.453+0000",
                lastModifyTime: "2019-05-16T07:30:44.453+0000",
                name: "GeoSOS_ANN_Wrap",
                id: "d8754772-86b7-4f5b-9398-60f320c161c9",
            },
            {
                author: "Min Cao",
                authorId: "Min_Cao",
                createTime: "2019-06-17T14:43:57.024+0000",
                lastModifyTime: "2019-06-17T14:43:57.024+0000",
                name: "GCAM-CA",
                id: "ebc7beaf-ff61-49da-9899-bc68e7c920e6",
            },
            {
                author: "A. Stewart Fotheringham",
                authorId: "A._Stewart_Fotheringham",
                createTime: "2019-06-17T14:07:05.185+0000",
                lastModifyTime: "2019-06-17T14:07:05.185+0000",
                name: "GWR",
                id: "fcf84557-3264-405e-93cc-0827b29fae63",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-16T13:31:58.276+0000",
                lastModifyTime: "2019-05-16T13:31:58.276+0000",
                name: "TaiHu_Fvcom",
                id: "d41dfc74-3509-4d02-8f45-5e2eeaf5eec7",
            },
            {
                author: "Dawen Yang",
                authorId: "Dawen_Yang",
                createTime: "2020-06-12T13:35:48.436+0000",
                lastModifyTime: "2020-06-12T13:35:48.436+0000",
                name: "GBEHM",
                id: "d1057c98-dcf1-4188-9bd4-49e94770901e",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-05T08:00:35.199+0000",
                lastModifyTime: "2019-05-05T08:00:35.199+0000",
                name: "Fvcom_lu_step1",
                id: "322dcfb0-6a79-48a2-9b80-bb105f1bb36d",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-05T08:01:21.060+0000",
                lastModifyTime: "2019-05-05T08:01:21.060+0000",
                name: "Fvcom_lu_step2",
                id: "32a4ea56-991b-4f13-87c2-f2f9bb02b61c",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-05T08:02:02.646+0000",
                lastModifyTime: "2019-05-05T08:02:02.646+0000",
                name: "Fvcom_lu_step3",
                id: "c7e6fb7f-5b88-4130-9522-81fd8fe87369",
            },
            {
                author: "王明",
                authorId: "王明",
                createTime: "2019-05-05T07:59:20.528+0000",
                lastModifyTime: "2019-05-05T07:59:20.528+0000",
                name: "Fvcom_lu_step4",
                id: "c9a9be25-a16b-4812-9ffd-aae6bf37098c",
            },
            {
                author: "Jinfeng Wang",
                authorId: "Jinfeng_Wang",
                createTime: "2019-05-16T12:47:36.883+0000",
                lastModifyTime: "2019-05-16T12:47:36.883+0000",
                name: "Geographical detector",
                id: "e80789bc-d29c-4bd4-9ef8-674541f40d42",
            },
            {
                author: "Xinyue Ye",
                authorId: "Xinyue_Ye",
                createTime: "2018-01-02T14:01:01.030+0000",
                lastModifyTime: "2019-05-16T14:01:01.030+0000",
                name: "Space-Time Analysis of Regional Systems",
                id: "9948998b-1daa-4dec-8d41-d74efb20431a",
            },
        ],
        inSearch:0,
        isSearchModel:false,

        taskLoading:false,
        dialogVisible: false
    },
    computed: {},
    watch: {
        // currentFile:function (file) {
        //     this.uploadToDataContainer(file);
        // }
        // eventChoosing(o, n){
        //     console.log("Watch eventChoosing")
        //     console.log(o)
        //     console.log(n)
        // }
        info(o_val, n_val){
            console.log("watch info")
            console.log(o_val)
            console.log(n_val)
        },

        htmlJSON(){
            if (this.htmlJSON.classic == '经典'){
                // console.log(1)
                this.radioStyle = '经典'
            }else {
                this.radioStyle = 'Classic'
            }

        }
    },
    methods: {
        showTaskInfo(taskId){

        },

        //可视化
        visualization(url){
            // url = "http://221.226.60.2:8082/data/8c46f99c-77ac-496a-b6c8-290b653a10cf"
            let urlArr = url.split("/");
            // 兰德访问不了门户网站的数据，要转成兰德的服务器
            url = "http://221.226.60.2:8082/data/" + urlArr[urlArr.length - 1]
            window.open('http://221.226.60.2:8082/onlinePreview?url=' + Base64.encode(url));
        },




        //翻译
        translatePage(jsonContent){
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
                        "element" : "#modelSider",
                        "popover" : {
                            "title" : "Computable Model Info",
                            "description" : "Model's brief introduction.",
                            "position" : "right-center",
                        }
                    },
                    {
                        "element": ".leftContainer",
                        "popover": {
                            "title": "Model State",
                            "description": "Model's running states.",
                            "position": "top",
                        }
                    },
                    {
                        "element": ".dataContainer",
                        "popover": {
                            "title": "Model Event",
                            "description": "Model events in different states. Before invoking model, please load necessary event data",
                            "position": "top",
                        }
                    },
                    {
                        "element" : "#showStyle",
                        "popover" : {
                            "title" : "Show Style",
                            "description" : "You can browse model states and events in different style.",
                            "position" : "bottom",
                        }
                    },
                    {
                        "element" : "#loadDataBtn",
                        "popover" : {
                            "title" : "Load Data",
                            "description" : "Click this button and check if there is prepared data set.",
                            "position" : "bottom",
                        }
                    },
                    {
                        "element" : "#invokeBtn",
                        "popover" : {
                            "title" : "Invoke",
                            "description" : "After loading necessary data, you can invoke this model on the web and get results",
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

        uploadRemove(file, fileList) {

            this.uploadFiles = fileList;
        },
        uploadChange(file, fileList) {
            console.log(fileList)
            this.uploadFiles = fileList;
        },

        uploadClose() {
            this.$refs.upload.abort();
            this.uploadDialogVisible = false;
        },

        uploadClick(){
            this.uploadSource=[];
            this.selectedPath=[];
            this.uploadFileList=[];
            this.uploadLoading=false;
            setTimeout(()=>{
                    this.uploadDialogVisible=true;
                },100

            )
        },

        submitUpload() {
            if(this.uploadName==""){
                this.$message.error(this.htmlJSON.PleaseEnterDatasetName);
                return;
            }
            if(this.selectValue==""){
                this.$message.error(this.htmlJSON.SelectDataTemplate);
                return;
            }
            if (this.selectedPath.length == 0) {
                this.$message.error(this.htmlJSON.selectDirTip);
                return;
            }
            if(this.uploadFiles.length==0){
                this.$message.error(this.htmlJSON.PleaseSelectFiles);
                return;
            }

            let formData = new FormData();

            this.uploadLoading=true;

            let configContent = "<UDXZip><Name>";
            for(let index in this.uploadFiles){
                configContent+="<add value='"+this.uploadFiles[index].name+"' />";
                formData.append("ogmsdata", this.uploadFiles[index].raw);
            }
            configContent += "</Name>";
            if(this.selectValue!=null&&this.selectValue!="none"){
                configContent+="<DataTemplate type='id'>";
                configContent+=this.selectValue;
                configContent+="</DataTemplate>"
            }
            else{
                configContent+="<DataTemplate type='none'>";
                configContent+="</DataTemplate>"
            }
            configContent+="</UDXZip>";
            // console.log(configContent)
            let configFile = new File([configContent], 'config.udxcfg', {
                type: 'text/plain',
            });
            formData.append("ogmsdata", configFile);
            formData.append("name", this.uploadName);
            formData.append("userId", this.uid);
            formData.append("serverNode", "china");
            formData.append("origination", "portal");

            $.get("/dataManager/dataContainerIpAndPort", (result) => {
                let ipAndPort = result.data;

                $.ajax({
                    url: '/dispatchRequest/uploadMutiFiles',
                    type: 'post',
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true,
                }).done((res)=> {
                    if(res.code==0){
                        let data=res.data;
                        if(this.uploadFiles.length==1){
                            data.suffix=this.uploadFiles[0].name.split(".")[1];
                        }
                        else{
                            data.suffix="zip";
                        }
                        data.label=data.file_name;
                        data.file_name+="."+data.suffix;
                        data.upload=true;
                        data.templateId=this.selectValue;
                        this.addDataToPortalBack(data,this.selectValue);

                        //reset
                        this.uploadName="";
                        this.selectValue="";
                        this.selectedPath=[];
                        this.uploadFiles=[];
                        this.remoteMethod("");
                        this.$refs.upload.clearFiles();


                    }else{
                        this.$message.error(this.htmlJSON.UploadFailed);
                    }


                    console.log(res);
                }).fail((res)=> {

                    this.uploadLoading=false;
                    this.uploadDialogVisible=false;
                    this.$message.error(this.htmlJSON.UploadFailed);
                });
            });

        },

        //获取数据模板列表用于在上传数据时选择
        remoteMethod(searchText) {

            this.selectLoading = true;
            let query = {
                page: 1,
                pageSize: 999,
                asc: 1,
                searchText: searchText
            };
            $.ajax({
                type: "POST",
                url: "/template/list",
                data: JSON.stringify(query),
                async: true,
                contentType: "application/json",
                success: (result) => {

                    this.options = [];
                    this.options.push({"name": "None", "id": "none"})
                    for (let index in result.data.list) {
                        this.options.push(result.data.list[index]);
                    }

                    this.selectLoading = false;
                }
            });
            // $.post("/repository/searchTemplate",JSON.stringify(query),(result)=>{
            //     this.selectLoading=false;
            //     this.option=result.list;
            // },"json")

        },
        selectFile() {
            if (this.selectedPath.length == 0) {
                this.$message.error(this.htmlJSON.selectDirTip);
                return;
            }
            $("#uploadFile").click()
        },

        handleSuccess(result, file, fileList) {
            console.log(result)
            let uploadSource = [];
            uploadSource.push(result.data);
            this.upload_data_dataManager(uploadSource);
        },

        upload_data_dataManager(uploadSource) {
            // console.log(this.fileNames)
            // this.fileNames.filter(res=>typeof (res)!="undefined")
            console.log(uploadSource)
            console.log($('.file-caption').val())
            if (uploadSource.length == 0) {
                alert(this.htmlJSON.UploadFileToTemplate)
            } else {
                for (let i = 0; i < uploadSource.length; i++) {
                    let dataName = uploadSource[i].file_name;
                    let dataname7suffix = dataName.split('.')
                    let fileName = dataname7suffix[0]
                    let suffix = dataname7suffix[1]
                    let dataId = uploadSource[i].source_store_id;
                    var data = {
                        author: this.userId,
                        fileName: fileName,
                        fromWhere: "PORTAL",
                        mdlId: "string",
                        sourceStoreId: dataId,
                        suffix: suffix,
                        tags: $("#managerFileTags").tagsinput('items'),
                        type: "OTHER"

                    }
                    var that = this;
                    var sucUpload
                    axios.post("/dispatchRequest/addRecordToDataContainer", data)
                        .then(res => {
                            if (res.status == 200) {

                                that.addAllData()
                                that.close()
                                sucUpload = res.status
                            }
                        });
                }
                this.addDataToPortalBack(uploadSource);


            }

        },

        addDataToPortalBack(item,templateId) {//item为undefined,则为用户上传；其他为页面已有数据的上传、修改路径

            var addItem = []
            if (item instanceof Array) {
                addItem = item;
                // for(let i=0;i<addItem.length;i++)
                //     addItem[i].file_name=this.splitFirst(addItem[i].file_name,'&')[1]
            }
            else {
                // let obj = {
                //     file_name: item.label + '.' + item.suffix,
                //     source_store_id: item.url.split('=')[1]
                // }
                addItem[0] = item
            }
            let paths = []
            if (this.uploadInPath == 1) {
                let i = this.pathShown.length - 1;
                while (i >= 0) {
                    paths.push(this.pathShown[i].id);
                    i--;
                }
                if (paths.length == 0) paths = ['0']

            } else {
                if (this.selectedPath.length == 0) {
                    alert(this.htmlJSON.selectDirTip)
                    return
                }

                let i = this.selectedPath.length - 1;//selectPath中含有all folder这个不存在的文件夹，循环索引有所区别
                while (i >= 1) {
                    paths.push(this.selectedPath[i].key);
                    i--;
                }
                if (paths.length == 0) paths = ['0']
            }
            let that = this;
            $.ajax({
                type: "POST",
                url: "/user/addFile",
                data: JSON.stringify({
                    files: addItem,
                    paths: paths
                }),

                async: true,
                traditional: true,
                contentType: "application/json",
                success: (json) => {
                    if (json.code == -1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                //window.location.href="/user/login";
                            }
                        });
                    } else {
                        let idList = json.data
                        console.log(idList)
                        if (item instanceof Array) {
                            if (this.uploadInPath == 1) {
                                for (let i = 0; i < item.length; i++) {
                                    console.log(item[i].file_name)
                                    let dataName7Suffix = item[i].file_name.split('.')
                                    let flag=false;
                                    for(let id in this.info.visualIds){
                                        if(id==templateId) flag=true;
                                    }

                                    const newChild = {
                                        id: idList[i].id,
                                        label: dataName7Suffix[0],
                                        suffix: dataName7Suffix[1],
                                        children: [],
                                        package: false,
                                        upload: true,
                                        visual: flag,
                                        father: paths[0],
                                        url: idList[i].url,
                                    };
                                    if (this.myFileShown.length === 0)
                                        this.addChild(this.myFile, paths[0], newChild)
                                    this.myFileShown.push(newChild);
                                    console.log(this.myFileShown)
                                    // this.getFilePackage();
                                    console.log(this.myFile)
                                }
                            } else {
                                this.refreshPackage(0);
                                //要写一个前台按路径查找的函数
                            }
                        } else {
                            let obj = item
                            obj.id = idList[0].id
                            obj.url = idList[0].url
                            if (this.myFileShown.length === 0)
                                this.addChild(this.myFile, paths[0], item)
                            this.myFileShown.push(item);
                        }

                        this.addFolderIndex = false;
                        //this.selectedPath=[];

                        //上传成功关闭上传窗口
                        this.selectedPath=[];
                        this.uploadName="";
                        this.selectValue="";
                        this.uploadFiles=[];
                        this.uploadLoading=false;
                        this.uploadDialogVisible=false;
                        this.$message({
                            message: this.htmlJSON.UploadSuccessfully,
                            type: 'success'
                        });

                    }

                }
            });

            // alert('Upload File successfully!')


        },

        uploadBeforeClose() {
            this.$confirm(this.htmlJSON.ConfirmClose)
                .then(_ => {
                    this.uploadDialogVisible = false;
                })
                .catch(_ => {
                });

            this.$refs.upload.clearFiles();
        },

        addFolderInTree(pageIndex, index) {
            // this.$refs.folderTree.setCurrentKey('');
            var node, data
            if (pageIndex == 'myData') {
                data = this.$refs.folderTree.getCurrentNode();
                if (data == undefined) alert(this.htmlJSON.selectDirTip)
                node = this.$refs.folderTree.getNode(data);
            }
            // else {
                //     data = this.$refs.folderTree2[index].getCurrentNode();
                //     if (data == undefined) alert('Please select a file directory')
                //     node = this.$refs.folderTree2[index].getNode(data);
                // }

            let folderExited = data.children

            console.log(node);
            let paths = [];
            while (node.key != undefined && node.key != 0) {
                paths.push(node.key);
                node = node.parent;
            }
            if (paths.length == 0) paths.push('0')
            console.log(paths)

            var newChild = {id: ""}

            this.$prompt(null, 'Enter Folder Name', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                // inputErrorMessage: '邮箱格式不正确'
            }).then(({value}) => {
                if (folderExited.some((item) => {
                    return item.label === value;
                }) == true) {
                    alert(this.htmlJSON.NameAlreadyExisting);
                    return
                }

                $.ajax({
                    type: "POST",
                    url: "/user/addFolder",
                    data: {paths: paths, name: value},
                    async: false,
                    contentType: "application/x-www-form-urlencoded",
                    success: (json) => {
                        if (json.code == -1) {
                            this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.sessionStorage.setItem("history", window.location.href);
                                    //window.location.href="/user/login";
                                }
                            });
                        }
                        else {
                            newChild = {
                                id: json.data,
                                label: value,
                                children: [],
                                father: data.id,
                                package: true,
                                suffix: '',
                                upload: false,
                                url: '',
                            };
                            if (!data.children) {
                                this.$set(data, 'children', []);
                            }
                            data.children.push(newChild);

                            if (this.myFileShown.length === 0)
                                this.addChild(this.myFile, paths[0], newChild)
                            this.myFileShown.push(newChild);

                            setTimeout(() => {
                                this.$refs.folderTree.setCurrentKey(newChild.id)
                            }, 100)
                        }

                    }

                });


            }).then(() => {

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: 'Cancel'
                });
            });


        },

        selectFolder() {

            this.selectedPath = [];

            axios.get("/user/getFolder", {})
                .then(res => {
                    let json = res.data;
                    if (json.code == -1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                //window.location.href="/user/login";
                            }
                        });
                    }
                    else {
                        this.folderTree = res.data.data;
                        this.selectFolderVisible = true;

                        this.$nextTick(()=>{
                            this.$refs.folderTree.setCurrentKey(null); //打开树之前先清空选择
                            })
                    }

                });
        },

        confirmFolder() {
            let data = this.$refs.folderTree.getCurrentNode();
            let node = this.$refs.folderTree.getNode(data);

            while (node.key != undefined && node.key != 0) {
                this.selectedPath.unshift(node);
                node = node.parent;
            }
            let allFolder = {
                key: '0',
                label: 'All Folder'
            }
            this.selectedPath.unshift(allFolder)
            // console.log(this.selectedPath)
            this.selectPathDialog = false;
            this.selectFolderVisible = false;

        },

        arraySpanMethod({row, column, rowIndex, columnIndex}) {
            if (row.children != undefined && columnIndex === 2) {
                return [1, 3];
            }
        },


        uploadToDataContainer(file, event) {
            let configContent = "<UDXZip><Name>";
            configContent+="<add value='"+file.name+"' />";
            configContent += "</Name>";
            let data=event.data[0];
            if(data.dataType=="external"){
                configContent+="<DataTemplate type='id'>";
                configContent+=data.externalId;
                configContent+="</DataTemplate>"
            }else if(data.dataType=="internal"&&data.nodes!=undefined){
                configContent+="<DataTemplate type='schema'>";
                configContent+=data.schema.trim();
                configContent+="</DataTemplate>"
            }else{
                configContent+="<DataTemplate type='none'>";
                configContent+="</DataTemplate>"
            }
            configContent+="</UDXZip>";
            let configFile = new File([configContent], 'config.udxcfg', {
                type: 'text/plain',
            });


            $.get("/dataManager/dataContainerIpAndPort", (result) => {
                let ipAndPort = result.data;
                let formData = new FormData();
                formData.append("ogmsdata", file);
                formData.append("ogmsdata", configFile);
                formData.append("name", event.eventName);
                formData.append("userId", this.uid);
                formData.append("serverNode", "china");
                formData.append("origination", "portal");
                $.ajax({
                    type: "post",
                    url: "/dispatchRequest/uploadMutiFiles",
                    data: formData,
                    async: true,
                    processData: false,
                    contentType: false,
                    success: (res) => {
                        if (res.code === 1) {
                            let data=res.data;
                            // if(this.uploadFiles.length==1){
                            //     data.suffix=this.uploadFiles[0].name.split(".")[1];
                            // }
                            // else{
                            //
                            //     data.suffix="zip";
                            // }
                            data.suffix="xml";
                            data.label=data.file_name;
                            data.file_name+="."+data.suffix;

                            let dataUrl = "https://geomodeling.njnu.edu.cn/dataTransferServer/data/" + data.source_store_id;

                            if (event == null) {
                                this.$set(this.eventChoosing, 'url', dataUrl);
                                this.$set(this.eventChoosing, 'tag', data.label)
                                this.$set(this.eventChoosing, 'suffix', data.suffix)

                                let uploadEle = $("#upload_" + this.eventChoosing.eventId);
                                uploadEle.removeAttr("disabled");
                                uploadEle.children().children().removeClass("el-icon-loading");
                                uploadEle.children().children().addClass("fa");
                                uploadEle.children().children().addClass("fa-cloud-upload");
                                $("#eventInp_" + this.eventChoosing.eventId).val(data.label + data.suffix);
                                $("#download_" + this.eventChoosing.eventId).css("display", "block");
                            }
                            else {

                                this.$set(event, 'url', dataUrl);
                                this.$set(event, 'tag', data.label)
                                this.$set(event, 'suffix', data.suffix)

                                let uploadEle = $("#upload_" + event.eventId);
                                uploadEle.removeAttr("disabled");
                                uploadEle.children().children().removeClass("el-icon-loading");
                                uploadEle.children().children().addClass("fa");
                                uploadEle.children().children().addClass("fa-cloud-upload");
                                $("#eventInp_" + event.eventId).val(data.label + data.suffix);
                                $("#download_" + event.eventId).css("display", "block");
                            }

                            $("#uploadInputData").val("");
                        }
                    }
                })
            })
        },

        createAndUploadParamFile() {
            let states = this.info.modelInfo.states;
            for (i = 0; i < states.length; i++) {
                let events = states[i].event;
                let find = false;
                for (j = 0; j < events.length; j++) {
                    let event = events[j];
                    if (event.eventType == "response" && event.children != undefined) {
                        //拼接文件内容
                        let content = "";
                        let children = event.children;
                        for (k = 0; k < children.length; k++) {
                            let child = children[k];
                            if (child.value === undefined || child.value.trim() === '') {
                                continue;
                            }
                            else {
                                content += "<XDO name=\"" + child.eventName + "\" kernelType=\"" + child.eventType.toLowerCase() + "\" value=\"" + child.value + "\" /> ";
                            }
                        }
                        if (content === "") {
                            continue;
                        }
                        else {
                            content = "<Dataset> " + content + " </Dataset>";
                        }


                        //生成文件
                        let file = new File([content], event.eventName + '.xml', {
                            type: 'text/plain',
                        });
                        //上传文件
                        console.log("content:",content);
                        this.uploadToDataContainer(file, event);

                    }
                }
            }

        },

        initModelInfo(){
            this.info.modelInfo = {
                dataRefs:[],
                date: "",
                des: "",
                hasTest: true,
                name: "Model",
                states:[{
                    Id:'',
                    desc:'',
                    event:[{
                        data:[{
                            Id: "",
                            dataType: "",
                            desc: "",
                            nodes: undefined,
                            parentId: "",
                            text: "",
                        }],
                        eventDesc: "",
                        eventId: "",
                        eventName: "",
                        eventType: "response",
                        optional: false
                    },
                        {
                            data:[{
                                Id: "",
                                dataType: "",
                                desc: "",
                                nodes: undefined,
                                parentId: "",
                                text: "",
                            }],
                            eventDesc: "",
                            eventId: "",
                            eventName: "",
                            eventType: "noresponse",
                            optional: false
                        }]
                }

                ]
            }
        },


        tableRowKey(row) {
            console.log(row)
            return row.name;
        },

        handlePageChange() {

        },
        handleView() {

        },
        selectFromDataItem(event) {
            this.eventChoosing = event;
            this.dataItemVisible = true;
            // this.relatedDataItem();
        },
        clickData(item, event) {
            console.log(item, event)
            if (this.currentData.url != item.url) {

                this.currentData = item;

                for (let parent of event.path) {
                    if (parent.id == item.url) {
                        $(".dataitemisol").removeClass("clickdataitem");
                        parent.classList.add("clickdataitem")
                        break;
                    }
                }
            }
            else {
                this.currentData = {};
                $(".dataitemisol").removeClass("clickdataitem")
            }
        },
        searchDataItem() {
            this.pageOption.classifications = this.classifications;
            this.pageOption.searchText = this.dataItemSearchText;
            axios.post("/dataItem/searchResourceByNameAndCate/", this.pageOption)
                .then((res) => {
                    console.log(res)
                    this.pageOption.searchResult = res.data.data.list;
                    this.pageOption.total = res.data.data.total;
                });
        },

        relatedDataItem() {
            let paths = window.location.href.split("/");
            this.relatePageOption.id = paths[paths.length - 1];
            axios.get("/computableModel/relatedDataByPage", {
                params: this.relatePageOption
            }).then((res) => {
                console.log(res)
                this.relatePageOption.searchResult = res.data.data.list;
                this.relatePageOption.total = res.data.data.total;
            });
        },

        chooseCate(item, e) {
            if (this.classifications[0] != item.id) {
                $(".taskDataCate").children().css("color", "black")
                e.target.style.color = 'deepskyblue';
                this.classifications.pop();
                this.classifications.push(item.id);
            }
            else {
                e.target.style.color = 'black';
                this.classifications.pop();
            }

            this.searchDataItem();

        },

        confirmData() {
            if (this.currentDataUrl != "") {
                this.dataItemVisible = false;
                console.log(this.eventChoosing, this.currentData)
                this.eventChoosing.tag = this.currentData.name;
                this.eventChoosing.url = this.currentData.url;
            }
            else {
                this.$message(this.htmlJSON.PleaseSelectDataFirst)
            }
        },
        downloadData() {
            if (this.currentDataUrl != "") {
                window.open(this.currentData.url);
            }
            else {
                this.$message(this.htmlJSON.PleaseSelectDataFirst)
            }
        },

        initSize() {
            this.$nextTick(() => {
                let scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
                let totalHeight = $('.taskMain').css('height')
                let leftbarHeight = $("#introContainer").css("height");

                if (scrollTop > 62) {
                    $('#introContainer').addClass("fixed")
                    if (parseInt(totalHeight) - parseInt(scrollTop) + 62 < parseInt(leftbarHeight)) {
                        $('#introContainer').removeClass("fixed")
                        // $('#introContainer').addClass("stop")
                    } else {
                        // $('#introContainer').removeClass("stop")
                        $('#introContainer').addClass("fixed")
                    }
                } else {
                    $('#introContainer').removeClass("fixed")
                }

                // if (parseInt(totalHeight) - parseInt(scrollTop) < 800) {
                //     $('.introContent').css('display', 'none')
                // } else {
                //     $('.introContent').css('display', 'block')
                // }


            })

        },

        generateId(key) {
            return key;
        },

        getUserTaskInfo() {
            let {code, data, msg} = fetch("/user/getFullUserInfo", {
                method: "GET",
            }).then((response) => {
                return response.json();
            }).then((data) => {
                this.userInfo = data.data;
                this.userTaskInfo = this.userInfo.runTask;
                setTimeout(() => {
                    $('.el-loading-mask').css('display', 'none');
                }, 355)

            });

        },

        share() {
            for (let i = 0; i < this.databrowser.length; i++) {
                if (this.databrowser[i].id === this.dataid) {
                    var item = this.databrowser[i];
                    break;
                }
            }


            if (item != null) {
                let url = "/dataManager/downloadRemote?&sourceStoreId=" + item.sourceStoreId;
                this.$alert("<input style='width: 100%' value=" + url + ">", {
                    dangerouslyUseHTMLString: true
                })
                // this.dataid='';

            } else {
                // console.log("从后台获取数据条目数组有误")
                this.$message(this.htmlJSON.LoginInFirst);
            }
        },

        backToPackage() {
            this.detailsIndex = 1;
        },

        dateFormat(date, format) {
            let dateObj = new Date(date);
            let fmt = format || "yyyy-MM-dd hh:mm:ss";
            //author: meizz
            var o = {
                "M+": dateObj.getMonth() + 1, //月份
                "d+": dateObj.getDate(), //日
                "h+": dateObj.getHours(), //小时
                "m+": dateObj.getMinutes(), //分
                "s+": dateObj.getSeconds(), //秒
                "q+": Math.floor((dateObj.getMonth() + 3) / 3), //季度
                S: dateObj.getMilliseconds() //毫秒
            };
            if (/(y+)/.test(fmt))
                fmt = fmt.replace(
                    RegExp.$1,
                    (dateObj.getFullYear() + "").substr(4 - RegExp.$1.length)
                );
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(
                        RegExp.$1,
                        RegExp.$1.length == 1
                            ? o[k]
                            : ("00" + o[k]).substr(("" + o[k]).length)
                    );
            return fmt;
        },
        uploadData() {
            return {
                host: this.info.dxInfo.dxIP,
                port: this.info.dxInfo.dxPort,
                type: this.info.dxInfo.dxType,
                userName: this.info.userInfo.userName
            };
        },
        // handleDataDownloadClick({sourceStoreId}) {
        //
        //     window.open("/dispatchRequest/downloadBySourceStoreId?sourceStoreId=" + sourceStoreId);
        // },
        // handleDataChooseClick({sourceStoreId, fileName, suffix}) {
        //     let url =
        //         "http://172.21.212.64:8082/dataResource/getResource?sourceStoreId=" +
        //         sourceStoreId;
        //     this.showDataChose = false;
        //     this.eventChoosing.tag = fileName + "." + suffix;
        //     this.eventChoosing.url = url;
        // },
        switchClick(i) {
            if (i == 1) {
                $(".tab1").css("display", "block");
                $(".tab2").css("display", "none");
                $(".tab3").css("display", "none");
            } else if (i == 2) {
                $(".tab1").css("display", "none");
                $(".tab2").css("display", "block");
                $(".tab3").css("display", "none");
            } else {
                $(".tab1").css("display", "none");
                $(".tab2").css("display", "none");
                $(".tab3").css("display", "block");
            }

            var btns = $(".switch-btn");

            btns.css("color", "#636363");
            btns.eq(i - 1).css("color", "#428bca");
        },
        init() {
        },
        inEventList(state) {
            return state.event.filter(value => {
                return value.eventType === "response";
            });
        },
        outEventList(state) {
            return state.event.filter(value => {
                return value.eventType === "noresponse";
            });
        },
        filterTag(value, row) {
            return row.fromWhere === value;
        },

        testDataClick(index) {
            this.loadDataIndex = index
        },

        myCalcDataClick(index) {
            this.loadDataIndex = index
        },

        publishedExampClick(index) {
            this.loadDataIndex = index
        },

        loadUserTask(val) {
            let href = window.location.href.split('/')
            let modelId = this.modelId

            axios.get("/task/tasksByModelByUser", {
                    params:
                        {
                            modelId: modelId,
                            page: val - 1
                        }
                }
            ).then((res) => {

                this.exampleDataListOfUser.content = res.data.data.content
                this.exampleDataListOfUser.total = res.data.data.total
                for (let i = 0; i < this.exampleDataListOfUser.content.length; i++) {
                    this.exampleDataListOfUser.content[i].runTime = this.dateFormat(this.exampleDataListOfUser.content[i].runTime)
                    // this.exampleDataListOfUser.content[i].status=this.exampleDataListOfUser.content[i].public[0]==='public'?'public':'private'
                }
            })
        },

        loadPublishedData(val) {
            let href = window.location.href.split('/')
            let modelId = href[href.length - 1]

            axios.get("/task/publishedTasksByModel", {
                    params:
                        {
                            modelId: modelId,
                            page: val - 1
                        }
                }
            ).then((res) => {
                this.exampleDataList.content = res.data.data.content
                console.log(this.exampleDataList.content)
                this.exampleDataList.total = res.data.data.total
                for (let i = 0; i < this.exampleDataList.content.length; i++) {
                    this.exampleDataList.content[i].runTime = this.dateFormat(this.exampleDataList.content[i].runTime)
                    // this.exampleDataList.content[i].status=this.exampleDataList.content[i].public[0]==='public'?'public':'private'
                }
            })
        },


        loadData(val) {
            this.loadDataVisible = true

            this.loadUserTask(1)
            this.loadPublishedData(1)

        },

        expandMyCalcData(el) {
            console.log(el)
            let arrow = el.currentTarget
            if (arrow.className.indexOf('transform180') == -1) {
                arrow.setAttribute("class", "fa fa-caret-square-o-down transform180")
                $('.myCalcData').collapse('show')
            } else {
                arrow.setAttribute("class", "fa fa-caret-square-o-down")
                $('.myCalcData').collapse('hide')
            }
        },

        expandPublishedData(el) {
            let arrow = el.currentTarget
            if (arrow.className.indexOf('transform180') == -1) {
                arrow.setAttribute("class", "fa fa-caret-square-o-down transform180")
                $('.publishedData').collapse('show')
            } else {
                arrow.setAttribute("class", "fa fa-caret-square-o-down")
                $('.publishedData').collapse('hide')
            }
        },

        filterPublic(value, row) {
            return row.public[0] === 'public'
        },

        handleSelectionChange() {

        },

        showDescription(item) {
            console.log(item)
            if (item.description != '') {
                this.showDescriptionVisible = true;
                this.taskDescription = item.description;
            }

        },

        shareOutput(url){
            this.shareIndex=true;
            this.downloadUrl=url;
        },

        copyLink(){
            console.log(this.clipBoard);
            let vthis = this;
            this.clipBoard.on('success', function () {
                vthis.$alert(this.htmlJSON.CopyLinkSuccessfully,{type:'success',confirmButtonText: 'comfirm',})
                this.clipBoard.destroy()
            });
            this.clipBoard.on('error', function () {
                vthis.$alert(this.htmlJSON.FailedCopyLink,{type:'error',confirmButtonText: 'comfirm',})
                this.clipBoard.destroy()
            });
            this.shareIndex=false
        },

        async loadExampleData(id) {
            console.log(id)
            let stateContainer = document.getElementsByClassName("state-container")[0]
            const loading = this.$loading({
                lock: true,
                text: "Loading",
                spinner: "el-icon-loading",
                target:stateContainer,
                background: "rgba(0, 0, 0, 0.7)"
            });

            let {data, code, msg} = await (await fetch("/task/loadPublishedData", {
                    method: "post",
                    body: id
                }
            )).json()

            if (code == -1 || code == null || code == undefined) {
                loading.close();
                this.$message.error(msg);
                return;
            }

            data.forEach(el => { //填入前端变量
                    let state = this.info.modelInfo.states.find(state => {
                        return state.name == el.state;
                    });
                    if (state == undefined) return;
                    let event = state.event.find(event => {
                        return event.eventName == el.event;
                    });
                    if (event == undefined) return;
                    this.$set(event, "tag", el.tag);
                    this.$set(event, "suffix", el.suffix);
                    this.$set(event, "url", el.url);
                    this.$set(event, "urls", el.urls);
                    this.$set(event, "multiple", el.multiple);
                    if (el.children != undefined) {
                        if (el.children.length == 1) {
                            event.children[0].value = el.children[0].value;
                        }
                        else {
                            for (i = 0; i < el.children.length; i++) {
                                let name = el.children[i].eventName
                                let eventChild = event.children.find(child => {
                                    return child.eventName == name;
                                })
                                if (eventChild != null) {
                                    eventChild.value = el.children[i].value;
                                }
                            }
                        }
                    }

                }
            )

            loading.close();
            this.loadDataVisible = false
        },

        async loadTest(type) {
            this.loadDataVisible = false
            let stateContainer = document.getElementsByClassName("state-container")[0]
            const loading = this.$loading({
                lock: true,
                text: "Loading Test Data",
                spinner: "el-icon-loading fixLoading",
                target:stateContainer,
                background: "rgba(0, 0, 0, 0.7)"
            });
            try{
                let {data, code, msg} = await (await fetch("/task/loadTestData/"+this.modelId, {
                    method: "post",
                    headers: {
                        "Content-Type": "application/json"
                    }
                })).json();

                if (code != 0 || code == null || code == undefined) {
                    loading.close();
                    if(code == -2){
                        //window.location.href="/user/login";
                        window.sessionStorage.setItem("history", window.location.href);
                    }
                    else {
                        this.$message.error(msg);
                    }
                    return;
                }
                console.log(data)
                data.forEach(el => {
                    let stateId = el.stateId;
                    let eventName = el.event;
                    let state = this.info.modelInfo.states.find(state => {
                        return state.Id == stateId;
                    });
                    if (state == undefined) return;
                    let event = state.event.find(event => {
                        return event.eventName == eventName;
                    });
                    if (event == undefined) return;
                    this.$set(event, "tag", el.tag);
                    this.$set(event, "suffix", el.suffix);
                    this.$set(event, "url", el.url);
                    this.$set(event, "visual", el.visual);
                    if (el.children.length > 0) {
                        if (el.children.length == 1) {
                            let children = event.children[0]
                            this.$set(children,'value',el.children[0].value)
                            // event.children[0].value = el.children[0].value;
                        }
                        else {
                            for (i = 0; i < el.children.length; i++) {
                                let name = el.children[i].eventName
                                let eventChild = event.children.find(child => {
                                    return child.eventName == name;
                                })
                                if (eventChild != null) {
                                    this.$set(eventChild,'value',el.children[i].value)
                                    // eventChild.value = el.children[i].value;
                                }
                            }
                        }
                    }


                })

                ;
            }catch (e){
                loading.close()
                this.$alert(this.htmlJSON.CantLoadTestData, 'Tip', {
                         type:"warning",
                         confirmButtonText: 'OK',
                         callback: ()=>{
                             return
                         }
                     }
                 );
            }

            loading.close();
            this.$forceUpdate();
            this.loadDataVisible = true
            this.loadDataVisible = false
        },

        async addDataItemData(event){
            let stateContainer = document.getElementsByClassName("state-container")[0]
            const loading = this.$loading({
                lock: true,
                text: "Loading",
                spinner: "el-icon-loading",
                target:stateContainer,
                background: "rgba(0, 0, 0, 0.7)"
            });
            let dataItemId = event.currentTarget.id;
            let body = {
                dataItemId :dataItemId,
                oid: this.modelId,
                host: this.info.dxInfo.dxIP,
                port: this.info.dxInfo.dxPort,
                type: this.info.dxInfo.dxType,
                userName: this.info.userInfo.userName
            };
            let {data, code, msg} = await (await fetch("/task/loadDataItemData/",{
                method:"post",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify(body)
                })).json();
            if (code != 0 || code == null || code == undefined) {
                loading.close();
                this.$message.error(msg);
                return;
            }
            console.log(data)
            data.forEach(el => {
                let stateId = el.stateId;
                let eventName = el.event;
                let state = this.info.modelInfo.states.find(state => {
                    return state.Id == stateId;
                });
                if (state == undefined) return;
                let event = state.event.find(event => {
                    return event.eventName == eventName;
                });
                if (event == undefined) return;
                this.$set(event, "tag", el.tag);
                this.$set(event, "suffix", el.suffix);
                this.$set(event, "url", el.url);
                this.$set(event, "visual", el.visual);
                if (el.children.length > 0) {
                    if (el.children.length == 1) {
                        event.children[0].value = el.children[0].value;
                    }
                    else {
                        for (i = 0; i < el.children.length; i++) {
                            let name = el.children[i].eventName
                            let eventChild = event.children.find(child => {
                                return child.eventName == name;
                            })
                            if (eventChild != null) {
                                eventChild.value = el.children[i].value;
                            }
                        }
                    }
                }
            });
            loading.close();
            this.loadDataVisible = false;
        },


        goPersonCenter(oid) {
            window.open("/user/" + oid);
        },

        visualize(event){
            this.visualSrc = event.url.replace("data","visual");
            this.visualVisible=true;
        },

        download(event) {
            //下载接口
            if (event.url != undefined) {
                this.eventChoosing = event;
                window.open(this.eventChoosing.url);
            }
            else {
                this.$message.error(this.htmlJSON.NoDataCanBeDownloaded);
            }
        },
        upload(event) {
            //上传接口
            this.eventChoosing = event;
            $("#uploadInputData").click();
        },
        beforeRemove(file) {
            return this.$confirm(`确定移除 ${file.name}？`);
        },
        // onSuccess({data}) {
        //     let {tag, suffix, url} = data;
        //     this.showUpload = false;
        //     this.eventChoosing.tag = tag;
        //     this.eventChoosing.suffix = suffix;
        //     this.eventChoosing.url = url;
        //     this.$refs.upload.clearFiles();
        //     $("#eventInp_" + this.eventChoosing.eventId).val(tag + "." + suffix);
        //     $("#download_" + this.eventChoosing.eventId).css("display", "block");
        // },

        myDataClick(index) {
            this.dataChosenIndex = index;
            this.pathShown = [];
            this.downloadDataSet = [];
            this.downloadDataSetName = [];
            this.getFilePackage()
        },


        async checkPersonData(event) {
            this.eventChoosing = event;//此处把页面上的event与eventChoosing绑定
            console.log(event)
            // if (this.first == true) {
            //     let d = await this.getTableData(0);
            //     this.dataFromDataContainer = d.content;
            //     this.total = d.total;
            //     this.first = false;
            // }
            this.showDataChose = true;
            this.getUserTaskInfo()
            this.$nextTick(()=>{
                this.$refs.userDataSpace.getFilePackage();
            })

        },

        cancelData(event){
            this.$set(event,'tag',undefined)
            this.$set(event,'suffix',undefined)
            this.$set(event,'url',undefined)
            this.$forceUpdate()
            // event.tag = undefined
            // this.showDataChose = true//有这两句才能触发重新渲染
            // this.showDataChose = false
            // this.eventChoosing = []
            // this.eventChoosing = event
            // this.eventChoosing.tag = undefined
            // this.eventChoosing.suffix = undefined
            // this.eventChoosing.url = undefined
            // this.eventChoosing.visual = undefined
            // $("#datainput" + this.eventChoosing.eventId).val('');
            // n_info = Object.assign({}, this.info)
            // this.$set(this, "info", {})
            // this.$set(this, "info", n_info)
        },

        getEventContent(event){
            if(event.tag!=undefined&&event.tag!=null){
                return event.tag+'.' + event.suffix
            }else{
                return ''
            }
        },

        selectDataFromPersonal() {
            if (this.currentDataUrl != "") {
                this.showDataChose = false;
                console.log(this.eventChoosing, this.downloadDataSetName)
                this.eventChoosing.tag = this.downloadDataSetName[0].name;
                this.eventChoosing.suffix = this.downloadDataSetName[0].suffix;
                this.eventChoosing.url = this.downloadDataSetName[0].address;
                this.eventChoosing.visual = this.downloadDataSetName[0].visual;

                $("#eventInp_" + this.eventChoosing.eventId).val(this.eventChoosing.tag + this.eventChoosing.suffix);
                $("#download_" + this.eventChoosing.eventId).css("display", "block");
            }
            else {
                this.$message(this.htmlJSON.PleaseSelectDataFirst)
            }

        },

        dataSpaceChange(){

        },

        selectDataspaceFile(file) {
            this.downloadDataSetName[0] = file
        },

        removeDataspaceFile(file) {
            this.downloadDataSetName[0] = {}
        },

        async handleCurrentChange(val) {
            let d = await this.getTableData(val - 1);
            this.dataFromDataContainer = d.content;
        },
        async getTableData(page) {
            this.tableLoading = true;
            let {data} = await (await fetch(
                "/dispatchRequest/getUserRelatedDataFromDataContainer/?page=" +
                page +
                "&pageSize=10&" +
                "authorName=" +
                this.info.userInfo.userName
            )).json();
            this.tableLoading = false;

            return {
                total: data.totalElements,
                content: data.content
            };
        },

        getPackageContent($event, eval, key) {
            clearTimeout(this.clickTimeout)
            if (eval.package === false)
                return
            let id = eval.id;
            this.fatherIndex = this.myFileShown[key].id;
            this.pathShown.push(this.myFileShown[key])
            if (this.myFileShown[key].children.length != 0)
                this.myFileShown = this.myFileShown[key].children;
            else
                this.myFileShown = [];

            this.renameIndex = '';
            console.log(this.myFileShown)
            // console.log(this.myFileShown.length)
            // console.log(this.fatherIndex)

        },

        getFilePackage() {
            axios.get("/user/getFolderAndFile", {})
                .then(res => {
                    let json = res.data;
                    if (json.code == -1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type:"error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.sessionStorage.setItem("history", window.location.href);
                                //window.location.href="/user/login";
                            }
                        });
                    }
                    else {
                        this.myFile = res.data.data[0].children;
                        console.log(this.myFile)
                        this.myFileShown = this.myFile;
                    }


                });
        },

        //回到上一层目录
        backToFather() {
            // if(this.myFileShown.length==0||this.fatherIndex!=0) {
            //     this.findFather(this.myFile)
            //     this.fatherIndex=this.myFileShown[0].father;
            //     console.log()
            // }else if(this.fatherIndex==0)
            //     this.myFileShown=this.myFile;
            this.pathShown.pop(this.pathShown.length - 1)
            $('.fa-arrow-left').animate({marginLeft: '-6px'}, 170)
            let allFolder = [];
            allFolder.children = this.myFile;
            this.findFather(this.myFile, allFolder)
            console.log(this.myFileShown)
            this.fatherIndex = this.myFileShown[0].father;
            $('.fa-arrow-left').animate({marginLeft: '0'}, 170)
        },

        findFather(file, father) {
            if(file) {
                if (this.fatherIndex === '0')
                    this.myFileShown = this.myFile;
                for (let i = 0; i < file.length; i++) {
                    if (file[i].id === this.fatherIndex) {
                        this.myFileShown = father.children;
                        console.log(this.myFileShown)
                        return;
                    } else {
                        this.findFather(file[i].children, file[i])
                    }
                }
            }
        },

        refreshPackage(event, index) {

            let paths = []
            if (index == 1) {
                let i = this.pathShown.length - 1;
                while (i >= 0) {
                    paths.push(this.pathShown[i].id);
                    i--;
                }
                if (paths.length == 0) paths = ['0']

            } else {
                let i = this.selectedPath.length - 1;//selectPath中含有all folder这个不存在的文件夹，循环索引有所区别
                while (i >= 1) {
                    paths.push(this.selectedPath[i].key);
                    i--;
                }
                if (paths.length == 0) paths = ['0']

                this.pathShown = []
                for (i = 1; i < this.selectedPath.length; i++) {
                    this.pathShown.push(this.selectedPath[i].data)
                }


            }

            this.rotatevalue += 180;
            console.log($('.fa-refresh'))
            $('.fa-refresh').css('transform', 'rotate(' + this.rotatevalue + 'deg)')

            $.ajax({
                type: "GET",
                url: "/user/getFileByPath",
                data: {
                    paths: paths,
                },
                async: true,
                contentType: "application/x-www-form-urlencoded",
                success: (json) => {
                    if (json.code == -1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type: "error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href = "/user/login";
                            }
                        });
                    } else {
                        this.myFileShown = json.data.data;
                        this.fatherIndex = this.myFileShown[0].father
                        this.refreshChild(this.myFile);
                        console.log(this.myFileShown)
                    }
                }

            })


        },

        refreshChild(file) {
            console.log(this.fatherIndex)
            if (file)
                for (let i = 0; i < file.length; i++) {
                    if (file[i].id === this.fatherIndex) {
                        file[i].children = this.myFileShown
                        console.log(this.myFile)
                        return;
                    } else {
                        this.refreshChild(file[i].children)
                    }
                }
        },

        singleClick($event, eval) {
            if (eval.package == true) {
                return;
            }
            if (this.rightMenuShow == true) {
                this.rightMenuShow = false;
                return
            }
            clearTimeout(this.clickTimeout)
            var target = $event.currentTarget;
            var eval = eval;
            var that = this
            this.clickTimeout = setTimeout(function () {
                that.getid(target, eval)
            }, 1)

            this.renameIndex = '';

        },

        keywordsSearch() {
            if (this.searchcontent === "") {
                this.getFilePackage()
            } else {
                axios.get('/user/keywordsSearch', {
                    params: {
                        keyword: this.searchcontent
                    }
                }).then((res) => {
                    let json = res.data;
                    if (json.code == -1) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type: "error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.sessionStorage.setItem("history", window.location.href);
                                window.location.href = "/user/login";
                            }
                        });
                    }
                    else {
                        this.fileSearchResult = json.data.data;
                        this.myFileShown = this.fileSearchResult
                    }
                })

            }


        },

        async invoke() {
            let stateContainer = document.getElementsByClassName("state-container")[0]
            let loading = this.$loading({
                lock: true,
                text: this.htmlJSON.SettingParameters,
                spinner: "el-icon-loading",
                target:stateContainer,
                background: "rgba(0, 0, 0, 0.7)"
            });
            let states = this.info.modelInfo.states;
            for (i = 0; i < states.length; i++) {
                let events = states[i].event;
                for (j = 0; j < events.length; j++) {
                    let event = events[j];
                    if (event.eventType == "response" && event.optional == false && event.children != undefined) {
                        for (k = 0; k < event.children.length; k++) {
                            let child = event.children[k];
                            if (child.value == undefined || child.value.trim() == "") {
                                loading.close();
                                this.$message.error(this.htmlJSON.SomeInputParametersAreNotSet);
                                throw new Error(this.htmlJSON.SomeInputParametersAreNotSet);
                                return;
                            }
                        }
                    }
                }
            }

            // console.log("1 this.info.modelInfo.states:",states);

            this.createAndUploadParamFile();

            // console.log("2 this.info.modelInfo.states1:",states);

            // return;

            let prepare = setInterval(() => {
                let prepared = true;

                for (i = 0; i < states.length; i++) {
                    let events = states[i].event;
                    for (j = 0; j < events.length; j++) {
                        //判断参数文件是否已经上传
                        if (events[j].eventType == "response") {

                            let children = events[j].children;
                            if (children === undefined) {
                                continue;
                            }
                            else {
                                let hasFile = false;
                                for (k = 0; k < children.length; k++) {
                                    if (children[k].value != undefined && children[k].value.trim() != "") {
                                        hasFile = true;
                                        break;
                                    }
                                }
                                if (hasFile) {
                                    if (events[j].url == undefined) {
                                        prepared = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!prepared) {
                        break;
                    }
                }

                if (prepared) {
                    clearInterval(prepare);
                    // console.log(this.modelInEvent)
                    loading.close();
                    this.taskRunning = true

                    let json = {
                        oid: this.modelId,
                        ip: this.info.taskInfo.ip,
                        port: this.info.taskInfo.port,
                        pid: this.info.taskInfo.pid,
                        inputs: []
                    };

                    try {
                        this.info.modelInfo.states.forEach(state => {
                            let statename = state.name;
                            state.event.forEach(el => {
                                let event = el.eventName;
                                let tag = el.tag;
                                let url = el.url;
                                let suffix = el.suffix;
                                let templateId = el.externalId;
                                if(templateId!=null) templateId=templateId.toLowerCase();
                                let children = el.children;
                                if (el.eventType == "response") {
                                    if (el.optional) {
                                        if (url === null || url === undefined) {

                                        } else {
                                            json.inputs.push({
                                                statename,
                                                event,
                                                url,
                                                tag,
                                                suffix,
                                                templateId,
                                                children
                                            });
                                        }
                                    } else {
                                        if (url === null || url === undefined) {
                                            this.taskRunning = false
                                            this.$message.error(this.htmlJSON.SomeInputDataAreNotProvided);
                                            throw new Error(this.htmlJSON.SomeInputDataAreNotProvided);
                                        }
                                        json.inputs.push({
                                            statename,
                                            event,
                                            url,
                                            tag,
                                            suffix,
                                            templateId,
                                            children
                                        });
                                    }
                                }
                            });
                        });
                    } catch (e) {
                        this.taskRunning = false
                        return;
                    }

                    var tid;
                    $.ajax({
                        url: "/task/invoke",
                        type: "POST",

                        contentType: "application/json",
                        data: JSON.stringify(json),
                        success: ({data, code, msg}) => {
                            let task_info = data;

                            // $(".el-loading-text").text("Model is running, you can check running state and get results in \"User Space\" -> \"Task\"")
                            this.$confirm(this.htmlJSON.TaskWillStartRunning, 'Tip', {
                                    type:"success",
                                    showCancelButton:false,
                                    confirmButtonText: this.htmlJSON.ViewInUserspace,
                                    dangerouslyUseHTMLString: true,
                                }
                            ).then(() => {
                                // window.open('/user/userSpace#/task');
                                window.location.href = '/user/userSpace#/task';
                            } ).catch(()=>{
                                // window.open('/user/userSpace#/task');
                                // window.location.href = '/user/userSpace#/task';
                                // return
                            });

                            if (code == -1) {
                                this.$alert(msg, 'Error', {
                                    type: 'error',
                                    confirmButtonText: 'OK',
                                    callback: action => {
                                        //window.location.href="/user/login";
                                    }
                                });
                                this.taskRunning = false
                                return;
                            }

                            if (code == -2) {
                                loading.close();
                                this.$alert(msg, 'Error', {
                                    type: 'error',
                                    confirmButtonText: 'OK',
                                    callback: action => {

                                    }
                                });
                                this.taskRunning = false

                                return;
                            }

                            let interval = setInterval(async () => {
                                let {code, data, msg} = await (await fetch("/task/result", {
                                    method: "post",
                                    headers: {
                                        "Content-Type": "application/json"
                                    },
                                    body: JSON.stringify(task_info)
                                })).json();
                                if (code === -1) {
                                    clearInterval(interval);
                                    this.$alert(msg, 'Error', {
                                        type: 'error',
                                        confirmButtonText: 'OK',
                                        callback: action => {

                                        }
                                    });

                                    this.taskRunning = false
                                }
                                if (data.status === -1) {
                                    clearInterval(interval);
                                    this.$alert(this.htmlJSON.ErrorWhenModelRunning, 'Error', {
                                        type: 'error',
                                        confirmButtonText: 'OK',
                                        callback: action => {

                                        }
                                    });

                                    this.taskRunning = false
                                } else if (data.status === 2) {
                                    clearInterval(interval);
                                    this.$alert(this.htmlJSON.ModelHasRunSuccessfully, 'Success', {
                                        type: 'success',
                                        confirmButtonText: 'OK',
                                        callback: action => {

                                        }
                                    });

                                    let outputs = data.outputdata;

                                    outputs.forEach(el => {
                                        let statename = el.statename;
                                        let eventName = el.event;
                                        let state = this.info.modelInfo.states.find(state => {
                                            return state.name == statename;
                                        });
                                        if (state == undefined) return;
                                        let event = state.event.find(event => {
                                            return event.eventName == eventName;
                                        });
                                        if (event == undefined) return;
                                        this.$set(event, "tag", el.tag);
                                        this.$set(event, "suffix", el.suffix);
                                        this.$set(event, "url", el.url);
                                        this.$set(event, "multiple", el.multiple);
                                    });

                                    this.taskRunning = false
                                } else {
                                }
                            }, 5000);
                        }
                    })



                }
            }, 2000)
        },


        selectFromMyData(key, modelInEvent) {
            this.selectDataDialog = true
            this.selectData = []
            this.keyInput = key

            let that = this;
            axios.get("/dataManager/list", {
                params: {
                    author: this.useroid,
                    type: "author"
                }

            })
                .then((res) => {

                    // console.log("oid datas",this.userId,res.data.data)
                    that.userData = res.data.data
                    that.totalNum = res.data.data.totalElements;
                    that.loading = false
                })


            this.modelInEvent = modelInEvent


        },
        currentPage() {

        },

        loadMore(e) {

        },
        selectUserData(item, e) {
            // console.log(e)
            this.$message(this.htmlJSON.YouHaveSelected + item.fileName + '.' + item.suffix);
            if (this.selectData.length === 0) {
                let d = {e, item}
                this.selectData.push(d)
                e.target.style.background = 'aliceblue'

            } else {
                let e2 = this.selectData.pop();

                if (e2.e != e) {

                    let d = {e, item}
                    e2.e.target.style.background = '';
                    e.target.style.background = 'aliceblue';
                    this.selectData.push(d)

                }

            }


        },

        //显示鼠标hover的title
        showtitle(ev) {
            return ev.label + "\n" + "Type: " + ev.suffix;
        },

        getImg(item) {
            let list = []
            if (item.id == 0 || item.package == true)
                return "/static/img/filebrowser/package.png"
            if (item.suffix == 'unknow')
                return "/static/img/filebrowser/unknow.svg"
            return "/static/img/filebrowser/" + item.suffix + ".svg"
        },
        generateId(key) {
            return key;
        },


        //下载
        download_data_dataManager() {

            for (let i = 0; i < this.databrowser.length; i++) {
                if (this.databrowser[i].id === this.dataid) {
                    var item = this.databrowser[i];
                    break;
                }
            }


            if (item != null) {
                let url = "/dataManager/downloadRemote?&sourceStoreId=" + item.sourceStoreId;
                let link = document.createElement('a');
                link.style.display = 'none';
                link.href = url;
                // link.setAttribute(item.fileName,'filename.'+item.suffix)

                document.body.appendChild(link)
                link.click();

            } else {
                this.$message(this.htmlJSON.SelectFileFirst);
            }


        },
        //删除
        delete_data_dataManager() {

            if (confirm(this.htmlJSON.SureToDelete)) {
                let tha = this
                axios.delete("/dataManager/delete", {
                    params: {
                        id: tha.dataid
                    }
                }).then((res) => {


                    if (res.data.msg === "成功") {
                        //删除双向绑定的数组
                        tha.rightMenuShow = false
                        tha.databrowser = []
                        tha.addAllData()
                        alert(this.htmlJSON.DeleteSuccessful)

                    }

                })
            } else {
                alert("ok")
            }


        },


        showsearchresult(data) {

            //动态创建DOM节点

            for (let i = 0; i < this.databrowser.length; i++) {
                //匹配查询字段
                if (this.databrowser[i].fileName.toLowerCase().indexOf(data.toLowerCase()) > -1) {
                    //插入查找到的card

                    //card
                    let searchresultcard = document.createElement("div");
                    searchresultcard.classList.add("el-card");
                    searchresultcard.classList.add("dataitemisol");
                    searchresultcard.classList.add("is-never-shadow");
                    searchresultcard.classList.add("sresult");


                    //cardbody
                    let secardbody = document.createElement("div");
                    secardbody.classList.add("el-card__body");
                    //card里添加cardbody
                    searchresultcard.appendChild(secardbody);

                    //el-row1
                    let cardrow1 = document.createElement("div");
                    cardrow1.classList.add("el-row");
                    secardbody.appendChild(cardrow1);

                    //3个div1
                    //div1
                    let div1 = document.createElement("div");
                    div1.classList.add("el-col");
                    div1.classList.add("el-col-6");

                    let text1 = document.createTextNode(" ");
                    div1.appendChild(text1);

                    cardrow1.appendChild(div1)

                    //div2
                    let div2 = document.createElement("div");
                    div2.classList.add("el-col");
                    div2.classList.add("el-col-12");

                    let img = document.createElement("img");
                    img.src = "/static/img/filebrowser/" + this.databrowser[i].suffix + ".svg";

                    img.style.height = '60%';
                    img.style.width = '100%';
                    img.style.marginLeft = '30%';

                    div2.appendChild(img);

                    cardrow1.appendChild(div2)

                    //div3
                    let div3 = document.createElement("div");
                    div3.classList.add("el-col");
                    div3.classList.add("el-col-6");

                    let text2 = document.createTextNode(" ");
                    div3.appendChild(text2);

                    cardrow1.appendChild(div3);


                    //el-row2
                    let cardrow2 = document.createElement("div");
                    cardrow2.classList.add("el-row");
                    secardbody.appendChild(cardrow2);

                    //3个div2
                    //div4
                    let div4 = document.createElement("div");
                    div4.classList.add("el-col");
                    div4.classList.add("el-col-2");

                    let text3 = document.createTextNode(" ");
                    div4.appendChild(text3);

                    cardrow2.appendChild(div4)

                    //div5
                    let div5 = document.createElement("div");
                    div5.classList.add("el-col");
                    div5.classList.add("el-col-20");

                    let p = document.createElement("p");
                    div5.appendChild(p);

                    let filenameandtype = document.createTextNode(this.databrowser[i].fileName + '.' + this.databrowser[i].suffix);
                    p.appendChild(filenameandtype)

                    cardrow2.appendChild(div5)

                    //div6
                    let div6 = document.createElement("div");
                    div6.classList.add("el-col");
                    div6.classList.add("el-col-20");

                    let text4 = document.createTextNode(" ");
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
                        this.dataid = this.databrowser[i].id;
                    })

                }
            }
        },

        category(data) {

            for (let j = 0; j < data.length; j++) {
                for (let i = 0; i < this.databrowser.length; i++) {
                    //匹配查询字段
                    if (this.databrowser[i].suffix.toLowerCase().indexOf(data[j].toLowerCase()) > -1) {
                        //插入查找到的card

                        //card
                        let searchresultcard = document.createElement("div");
                        searchresultcard.classList.add("el-card");
                        searchresultcard.classList.add("dataitemisol");
                        searchresultcard.classList.add("is-never-shadow");
                        searchresultcard.classList.add("sresult");


                        //cardbody
                        let secardbody = document.createElement("div");
                        secardbody.classList.add("el-card__body");
                        //card里添加cardbody
                        searchresultcard.appendChild(secardbody);

                        //el-row1
                        let cardrow1 = document.createElement("div");
                        cardrow1.classList.add("el-row");
                        secardbody.appendChild(cardrow1);

                        //3个div1
                        //div1
                        let div1 = document.createElement("div");
                        div1.classList.add("el-col");
                        div1.classList.add("el-col-6");

                        let text1 = document.createTextNode(" ");
                        div1.appendChild(text1);

                        cardrow1.appendChild(div1)

                        //div2
                        let div2 = document.createElement("div");
                        div2.classList.add("el-col");
                        div2.classList.add("el-col-12");

                        let img = document.createElement("img");
                        img.src = "/static/img/filebrowser/" + this.databrowser[i].suffix + ".svg";

                        img.style.height = '60%';
                        img.style.width = '100%';
                        img.style.marginLeft = '30%';

                        div2.appendChild(img);

                        cardrow1.appendChild(div2)

                        //div3
                        let div3 = document.createElement("div");
                        div3.classList.add("el-col");
                        div3.classList.add("el-col-6");

                        let text2 = document.createTextNode(" ");
                        div3.appendChild(text2);

                        cardrow1.appendChild(div3);


                        //el-row2
                        let cardrow2 = document.createElement("div");
                        cardrow2.classList.add("el-row");
                        secardbody.appendChild(cardrow2);

                        //3个div2
                        //div4
                        let div4 = document.createElement("div");
                        div4.classList.add("el-col");
                        div4.classList.add("el-col-2");

                        let text3 = document.createTextNode(" ");
                        div4.appendChild(text3);

                        cardrow2.appendChild(div4)

                        //div5
                        let div5 = document.createElement("div");
                        div5.classList.add("el-col");
                        div5.classList.add("el-col-20");

                        let p = document.createElement("p");
                        div5.appendChild(p);

                        let filenameandtype = document.createTextNode(this.databrowser[i].fileName + '.' + this.databrowser[i].suffix);
                        p.appendChild(filenameandtype)

                        cardrow2.appendChild(div5)

                        //div6
                        let div6 = document.createElement("div");
                        div6.classList.add("el-col");
                        div6.classList.add("el-col-20");

                        let text4 = document.createTextNode(" ");
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
                            this.dataid = this.databrowser[i].id;
                        })

                    }
                }
            }

        },

        getid($event, eval) {
            console.log(eval.id)
            this.dataid = eval.id;

            $event.closest('.el-card').className = "el-card dataitemisol clickdataitem"

            //再次点击取消选择

            if (this.downloadDataSet.indexOf(eval) > -1) {
                for (var i = 0; i < this.downloadDataSet.length; i++) {
                    if (this.downloadDataSet[i] === eval) {
                        //删除
                        this.downloadDataSet.splice(i, 1)
                        this.downloadDataSetName.splice(i, 1)
                        break
                    }
                }

            } else {
                this.downloadDataSet = []
                this.downloadDataSetName = []
                this.downloadDataSet.push(eval)
                let obj = {
                    name: eval.label,
                    suffix: eval.suffix,
                    package: eval.package,
                    url: eval.url,
                    visual: eval.visual
                }
                this.downloadDataSetName.push(obj)
            }

            if (eval.taskId != null) {
                this.detailsIndex = 2
                this.getOneOfUserTasks(eval.taskId);
            }
        },


        getOneOfUserTasks(taskId) {
            $.ajax({
                type: 'GET',
                url: "/task/taskByTaskId",
                // contentType:'application/json',

                data:
                    {
                        id: taskId,
                    },
                // JSON.stringify(obj),
                cache: false,
                async: true,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (json) => {

                    if (json.code != 0) {
                        this.$alert(this.htmlJSON.LoginInFirst, 'Error', {
                            type: "error",
                            confirmButtonText: 'OK',
                            callback: action => {
                                // window.location.href = "/user/login";
                            }
                        });
                    } else {
                        setTimeout(() => {
                            const data = json.data;
                            this.resourceLoad = false;
                            // this.researchItems = data.list;
                            this.packageContent = data;
                            console.log(this.packageContent)
                        }, 100)


                    }
                }
            })
        },

        addDataClass($event, item) {
            this.rightMenuShow = false


            if (this.downloadDataSet.indexOf(item) < 0) {
                $event.currentTarget.className = "el-card dataitemisol dataitemhover"
            }

            this.dataid = item.id


        },

        removeClass($event, item) {


            if (this.downloadDataSet.indexOf(item) > -1) {
                $event.currentTarget.className = "el-card dataitemisol clickdataitem"
            } else {
                $event.currentTarget.className = "el-card dataitemisol"
            }


        },

        //右键菜单

        rightMenu(e) {
            e.preventDefault();

            e.currentTarget.className = "el-card dataitemisol clickdataitem"


            var dom = document.getElementsByClassName("browsermenu");

            console.log(e)
            dom[0].style.top = e.pageY - 100 + "px"
            // 125 > window.innerHeight
            //     ? `${window.innerHeight - 127}px` : `${e.pageY}px`;
            dom[0].style.left = e.pageX - 200 + "px";

            this.rightMenuShow = true


        },

        openWzhRightMenu(e) {
            e.preventDefault();

            e.currentTarget.className = "el-card dataitemisol clickdataitem"
            console.log(e)

            var dom = document.getElementsByClassName("wzhRightMenu");

            dom[0].style.top = e.pageY - 250 + "px"
            dom[0].style.left = e.pageX - 230 + "px";
            // console.log(e.style)
            $('.wzhRightMenu').animate({height: '120'}, 150);
        },

        myDataClick(index) {
            this.dataChosenIndex = index;
        },

        outputDataClick(index) {
            this.dataChosenIndex = index;
        },

        checkMultiContent(output){
            this.multiFileDialog = true;
            this.outputMultiFile = [];
            let urls = output.url.substring(1, output.url.length-1).split(',')
            for(let i = 0;urls&&i<urls.length;i++){
                let obj={
                    name:output.tag+''+output.suffix,
                    url:urls[i].substring(1,urls[i].length-1),
                    visual:output.visual
                }
                this.outputMultiFile.push(obj)
            }
        },

        userDownload() {
            //todo 依据数组downloadDataSet批量下载

            let sourceId = new Array()

            for (let i = 0; i < this.downloadDataSet.length; i++) {
                sourceId.push(this.downloadDataSet[i].sourceStoreId)
            }


            if (this.downloadDataSet.length > 0) {

                const keys = sourceId.map(_ => `sourceStoreId=${_}`).join('&');
                let url = "/dataManager/downloadSomeRemote?" + keys;
                let link = document.createElement('a');
                link.style.display = 'none';
                link.href = url;
                // link.setAttribute(item.fileName,'filename.'+item.suffix)

                document.body.appendChild(link)
                link.click();

            } else {
                alert(this.htmlJSON.LoginInFirst)
            }


        },

        getTree() {
            return this.tree;
        },


        submitForm(formName) {
            //包含上传的文件信息和服务端返回的所有信息都在这个对象里
            this.$refs.upload.uploadFiles
        },


        //选择模型
        loadDeployedModelClick() {
            this.loadDeployedModelDialog = true;
            this.pageOption.currentPage = 1;
            this.searchResult = '';
            // this.loadDeployedModel();
            this.loadRecentlyUsed();
            this.isSearchModel = false
        },

        handlePageChange(val) {
            this.pageOption.currentPage = val;

            if(this.inSearch==0){
                // this.loadDeployedModel();
                this.loadRecentlyUsed();

            }
            else
                this.searchDeployedModel()
        },

        loadRecentlyUsed(){

            this.modelTableLoading = true;
            this.inSearch = 0
            let page = this.pageOption.currentPage
            let start = (page-1)*6
            let end = start+6<this.recentlyUsed.length?start+6:this.recentlyUsed.length
            this.deployedModel = this.recentlyUsed.slice(start,end)

            this.pageOption.total = this.recentlyUsed.length;
            setTimeout(()=>{
                this.modelTableLoading = false;
            },95)
        },

        loadDeployedModel(){
            this.inSearch = 0
            this.modelTableLoading = true;
            axios.get('/computableModel/loadDeployedModel',{
                params:{
                    asc:0,
                    page:this.pageOption.currentPage-1,
                    size:6,
                }
            }).then(
                (res)=>{
                    if(res.data.code==0){
                        let data = res.data.data;
                        this.deployedModel = data.content
                        this.pageOption.total = data.total;
                        setTimeout(()=>{
                            this.modelTableLoading = false;
                        },100)
                    }else{
                        this.$alert(this.htmlJSON.PleaseTryAgain,'Warning', {
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.modelTableLoading = false;
                            }
                        })

                    }
                }
            )
        },

        searchDeployedModel(page){
            this.inSearch = 1
            this.modelTableLoading = true;
            let targetPage = page==undefined?this.pageOption.currentPage:page
            this.pageOption.currentPage=targetPage

            if(this.searchText.trim()==''){
                this.loadRecentlyUsed()
                return
            }

            axios.post('/computableModel/deployedModel',{
                asc:false,
                page:targetPage,
                size:6,
                searchText: this.searchText,
            }).then(
                (res)=>{

                    this.isSearchModel = true
                    if(res.data.code==0){
                        let data = res.data.data;
                        this.deployedModel = data.content
                        this.pageOption.total = data.total;
                        setTimeout(()=>{
                            this.modelTableLoading = false;
                        },150)

                    }else{
                        this.$alert(this.htmlJSON.PleaseTryAgain,'Warning', {
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.modelTableLoading = false;
                            }
                        })

                    }
                }
            )
        },

        selectModel(oid){
            if(oid==='ed00b86d-97b8-4cab-94b1-027e5f2a907f'||
                oid==='81b1a770-2455-40b3-a992-027ec755055c'||
                oid==='02afc5c1-4634-4f4a-853d-80ec351a1f76'||
                oid==='c74f298b-646b-4f70-b95e-c1e9eeb122d7'
            ){
                window.location.href = '/task/' + oid
            }

            this.modelId = oid

            this.loadTask();
            this.loadDeployedModelDialog = false
        },

        viewUser(userId){
            window.open('/profile/'+userId)
        },

        async loadTask(){
            let id = this.modelId

            this.initModelInfo()
            this.taskLoading = true
            let res = {}
            try{
                res = await (await fetch("/task/init/" + id).catch(
                    ()=>{
                        this.taskLoading = false
                        this.$confirm(this.htmlJSON.InitializationFailure, 'Error', {
                            dangerouslyUseHTMLString: true,
                            type:"error",
                            cancelButtonText: this.htmlJSON.LoadOtherModel,
                            confirmButtonText: this.htmlJSON.TryAgain,
                        }).then(() => {
                            this.loadTask();
                        } ).catch(()=>{
                            window.location.href ='/computableModel/selecttask'
                        });
                        return
                    }
                )).json();
            }catch (e){

            }
            this.taskLoading = false
            let data = res.data
            if(res.code == -1){
                 this.$confirm(this.htmlJSON.LogInBeforeInvoke, 'Tip', {
                                         type:"warning",
                                         cancelButtonText: this.htmlJSON.Close,
                                         confirmButtonText: this.htmlJSON.LogIn,
                                         dangerouslyUseHTMLString: true,
                                     }
                                 ).then(() => {
                                     window.location.href='/user/login';
                                 }).catch(()=>{
                                     return
                                 });
                 return
            } else if (data == null || data == undefined) {
                this.$confirm(this.htmlJSON.InitializationFailure, 'Error', {
                    dangerouslyUseHTMLString: true,
                    type:"error",
                    cancelButtonText: this.htmlJSON.LoadOtherModel,
                    confirmButtonText: this.htmlJSON.TryAgain,
                }).then(() => {
                    this.loadTask();
                } ).catch(()=>{
                    window.location.href ='/computableModel/selecttask'
                });
                return
            }
            else{
                this.initializing=false;
                if(data.msg=='no service'){
                    this.invokable = false
                    this.errorMsg = 'Cannot find this model service, maybe the model container is offline or the service is hidden by the contributor.'
                    this.$confirm(this.htmlJSON.ModelContainerOffline, 'Tip', {
                            type:"error",
                            cancelButtonText: this.htmlJSON.LoadOtherModel,
                            confirmButtonText: this.htmlJSON.TryAgain,

                        }
                    ).then(() => {
                        this.loadTask();
                    } ).catch(()=>{
                        window.location.href ='/computableModel/selecttask'
                    });
                    return
                }else if(data.msg=='create failed'){
                    this.invokable = false
                    this.errorMsg = 'Cannot create this task,<br/> Please try again or <a href="mailto:opengms@njnu.edu.cn">contact us</a>.'
                    this.$confirm(this.htmlJSON.CannotCreateTask, 'Tip', {
                            type:"error",
                            cancelButtonText: this.htmlJSON.LoadOtherModel,
                            confirmButtonText: this.htmlJSON.TryAgain,
                            dangerouslyUseHTMLString: true,
                        }
                    ).then(() => {
                        window.location.reload();
                    } ).catch(()=>{
                        window.location.href ='/computableModel/selecttask'
                    });
                    return
                }
            }
            this.invokable = true
            let states = data.modelInfo.states;
            for (i = 0; i < states.length; i++) {
                let state = states[i];
                for (j = 0; j < state.event.length; j++) {
                    if (state.event[j].data != undefined && state.event[j].eventType == "response") {
                        let nodes = state.event[j].data[0].nodes;
                        let refName = state.event[j].data[0].text.toLowerCase();
                        if (state.event[j].data[0].externalId != undefined) {
                            state.event[j].externalId = state.event[j].data[0].externalId;
                        }

                        if (nodes != undefined && refName != "grid" && refName != "table" && refName != "shapes") {
                            let children = [];
                            for (k = 0; k < nodes.length; k++) {
                                let node = nodes[k];
                                let child = {};
                                child.eventId = node.text;
                                child.eventName = node.text;
                                child.eventDesc = node.desc;
                                child.eventType = node.dataType;

                                child.child = true;
                                children.push(child);
                            }
                            data.modelInfo.states[i].event[j].children = children;
                        }
                        else {
                            data.modelInfo.states[i].event[j].data[0].nodes = undefined;
                        }

                    }
                }
            }

            this.modelLoaded = true
            this.events = data.modelInfo.states[0].event;
            console.log(this.events)
            console.log(this.tableData)
            this.info = data;
            console.log(this.info);
        },

    },

    async mounted() {

        var tha = this

        this.clipBoard = new ClipboardJS(".copyLinkBtn");

        $(()=>{
            let height = document.documentElement.clientHeight;
            let mainHeight = height - 129
            this.ScreenMinHeight = (height) + "px";
            this.ScreenMaxHeight = (height) + "px";
            this.mainContainerHeight = (mainHeight) + "px";

            window.onresize = () => {
                console.log('come on ..');
                height = document.documentElement.clientHeight;
                mainHeight = height - 129
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";
                this.mainContainerHeight = (mainHeight) + "px";
            };
        })

        axios.get("/dataItem/categoryTree")
            .then(res => {
                tha.tObj = res.data.data;
                for (var e in tha.tObj) {
                    var a = {
                        key: e,
                        value: tha.tObj[e]
                    }
                    if (e != 'Data Resouces Hubs') {
                        tha.categoryTree.push(a);
                    }


                }

            })

        this.remoteMethod("");

        this.introHeight = $('.introContent').attr('height');

        console.log(this.introHeight)
        let ids = window.location.href.split("/");
        let id = ids[ids.length - 1];
        this.modelId = id;

        if(id.indexOf('selecttask')!=-1){
            this.selectModelPage=true
            this.initializing=false
        }else{
            this.loadTask()
            this.relatedDataItem();
        }


        //get login user info
        let that = this
        axios.get("/user/load")
            .then((res) => {
                if (res.data.code == 0) {
                    that.useroid = res.data.data.accessId;
                    that.uid=res.data.data.email;
                }

            })

        window.addEventListener('scroll', this.initSize);
        window.addEventListener('resize', this.initSize);

        $("#uploadInputData").change(() => {
            this.$delete(this.eventChoosing, "tag");
            this.$delete(this.eventChoosing, "suffix");
            this.$delete(this.eventChoosing, "url");
            this.$set(this.eventChoosing, "uploading", true);

            let uploadEle = $("#upload_" + this.eventChoosing.eventId);
            uploadEle.attr("disabled", true);
            uploadEle.children().children().removeClass("fa");
            uploadEle.children().children().removeClass("fa-cloud-upload");
            uploadEle.children().children().addClass("el-icon-loading");
            uploadEle.children().children().css("font-size", "12px")

            $("#eventInp_" + this.eventChoosing.eventId).val("");
            $("#download_" + this.eventChoosing.eventId).css("display", "none");

            let file = $('#uploadInputData')[0].files[0];
            this.uploadToDataContainer(file, this.eventChoosing);

        })

        /**
         * 张硕
         * 2019.11.21
         * 模型运行的图形界面
         */

        let count = 0;
        $('#workFlow').click(function () {
            $(".mxWindow").remove();
            if (count == 0) {
                count++;
                setTimeout(function () {
                    var diagram = new OGMSDiagram();
                    diagram.init($('#ogmsDiagramContainer'),
                        {
                            width: '100%',       //! Width of panel
                            // height: '100%',       //! Height of panel
                            height: 650,       //! Height of panel
                            enabled: false      //! Edit enabled
                        },
                        {
                            x: 500,            //! X postion of state information window
                            y: $(window).scrollTop() + 50,              //! Y postion of state information window
                            width: 520,         //! Width of state information window
                            height: 650         //! Height of state information window
                        },
                        {
                            x: 1000,           //! X postion of data reference information window
                            y: $(window).scrollTop() + 50,             //! Y postion of data reference information window
                            width: 300,         //! Width of data reference information window
                            height: 400         //! Height of data reference information window
                        },
                        '/static/js/mxGraph/images/modelState.png',    //! state IMG
                        '/static/js/mxGraph/images/grid.gif',          //! Grid IMG
                        '/static/js/mxGraph/images/connector.gif',     //! Connection center IMG
                        false                       //! Debug button
                    );


                    var behavior = {};
                    behavior.states = that.info.modelInfo.states;
                    behavior.dataRef = that.info.modelInfo.dataRefs;
                    behavior.transition = [];

                    that.loadjson = JSON.stringify(behavior).replace(new RegExp("\"event\":", "gm"), "\"events\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"desc\":", "gm"), "\"description\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"Id\":", "gm"), "\"id\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"eventId\":", "gm"), "\"id\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"eventName\":", "gm"), "\"name\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"eventType\":", "gm"), "\"type\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"eventDesc\":", "gm"), "\"description\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"optional\":", "gm"), "\"option\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"data\":", "gm"), "\"dataDes\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"dataType\":", "gm"), "\"type\":");
                    that.loadjson = that.loadjson.replace(new RegExp("\"text\":", "gm"), "\"name\":");
                    diagram.loadJSON(that.loadjson);
                    console.log(JSON.parse(that.loadjson))

                    diagram.onStatedbClick(function (state) {
                        diagram.showStateWin({
                            x: 1050,
                            y: 260,
                        }, {
                            width: 500,
                            height: 500
                        });

                    });
                }, 0.1);
            }
        });
        $('#classic').click(function () {
            $(".mxWindow").remove();
            count = 0;
        });
        $('#semantics').click(function () {
            $(".mxWindow").remove();
            count = 0;
        });

        $(document).on("click", ".eventBtn", function (e) {
            var event_id = e.currentTarget.id;
            var start = event_id.search('_');
            var eventId = event_id.slice(start + 1);
            var btnName = event_id.slice(0, start);

            var find = false;
            for (var i = 0; i < that.info.modelInfo.states.length; i++) {
                var state = that.info.modelInfo.states[i];
                for (var j = 0; j < state.event.length; j++) {
                    var event = state.event[j];
                    if (eventId == event.eventId) {
                        switch (btnName) {
                            case 'select':
                                that.selectFromDataItem(event);
                                break;
                            case 'upload':
                                that.upload(event);
                                break;
                            case 'check':
                                that.checkPersonData(event);
                                break;
                            case 'download':
                                that.download(event);
                                break;
                        }
                        find = true;
                        break;
                    }
                }
                if (find == true) {
                    break;
                }
            }
        }.bind(this));

        /**
         * 张硕
         * 2019.11.22
         * Event的点击事件，作用是将tab中的input文件与vue中的info属性相关联
         */
        $(document).on('click', '.eventTab', function (e) {
                var href = e.currentTarget.href;
                var start = href.search('#');
                var eventId = href.slice(start + 7);

                var find = false;
                for (var i = 0; i < that.info.modelInfo.states.length; i++) {
                    var state = that.info.modelInfo.states[i];
                    for (var j = 0; j < state.event.length; j++) {
                        var event = state.event[j];
                        if (eventId == event.eventId) {
                            if (event.eventType == "response" && event.children != undefined) {
                                for (k = 0; k < event.children.length; k++) {
                                    let child = event.children[k];
                                    $("#eventInp_" + child.eventName).val(child.value);
                                }
                            }
                            else if (event.eventType == "response" && event.tag != undefined) {
                                $("#eventInp_" + eventId).val(event.tag + "." + event.suffix);
                            } else if (event.eventType == "response") {
                                $("#download_" + eventId).css("display", "none");
                            } else if (event.eventType != "response" && event.tag != undefined) {
                                $("#eventInp_" + eventId).val(event.tag + "." + event.suffix);
                                $("#eventInp_" + eventId).css("width", "90%");
                                $("#select_" + eventId).css("display", "none");
                                $("#upload_" + eventId).css("display", "none");
                                $("#check_" + eventId).css("display", "none");
                            } else {
                                $("#inputGroup_" + eventId).css("display", "none");
                            }
                            find = true;
                            break;
                        }
                    }
                    if (find == true) {
                        break;
                    }
                }

                // that.info.modelInfo.states;
                // $("#eventInp_ + eventId").value =

            }.bind(this)
        );

        $(document).on('keyup', '.StateWindowEvent', (e) => {
            let states = this.info.modelInfo.states;
            for (i = 0; i < states.length; i++) {
                let events = states[i].event;
                for (j = 0; j < events.length; j++) {
                    let event = events[j];
                    if (event.eventId == e.target.dataset.parent) {
                        for (k = 0; k < event.children.length; k++) {
                            let child = event.children[k];
                            if (child.eventName == e.target.name) {
                                this.info.modelInfo.states[i].event[j].children[k].value = e.target.value;
                                break;
                            }
                        }
                    }
                }
            }
        })

        this.computableId = window.location.href.split("/")[4]
        console.log(this.computableId)
    },

    destory() {
        window.removeEventListener('scroll', this.initSize);
        window.removeEventListener('resize', this.initSize);
    }


});

$(function () {
    console.log('ha ha')
    $(window).resize(function () {
        let introHeaderHeight = $('.introHeader').css('width')
        console.log(introHeaderHeight)
        if (parseInt(introHeaderHeight) < 240) {
            $('.image').css('display', 'none')
        } else {
            $('.image').css('display', 'block')
        }
    })

    $('#browsercont2').click((e) => {
        $('.wzhRightMenu').animate({height: '0'}, 50);
        console.log($('#browsercont'))

    })

    $("#refreshPackageBtn").click(
        function () {
            value += 180;
            $('.fa-refresh').rotate({animateTo: value})
        }
    );


    $('document').on('onclick', '.backFatherBtn', () => {
        console.log('11')
        $('.fa-arrow-left').animate({marginLeft: '-6px'}, 170)
        $('.fa-arrow-left').animate({marginLeft: '0'}, 170)
    });


});
