var vue = new Vue({
    el: "#app",
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {
            //2021暑期
            editActiveIndex:0, //整体编辑
            NameEditActiveIndex:0, //编辑themename
            DetailEditActiveIndex:0,//编辑themeDetail
            editThemeName:"",
            themeImage:"",
            editThemeDetail:"",
            log_detail:0,
            imgLog:1,
            //存model,data,dataMethod每一个条目的name,image,oid
            modelInfo:[],
            dataInfo:[],
            dataMethodInfo:[],
            pop:[],
            themeModelData: {},
            origin_themeModelData:{},
            themeData: {},
            origin_themeData:{},
            themeDataMethod:{},
            origin_themeDataMethod:{},
            themeApplicationData:[],
            origin_themeApplicationData:[],

            originModelNode:{},
            curModelNode:{},
            originDataNode:{},
            curDataNode:{},
            originDataMethodNode:{},
            curDataMethodNode:{},

            //2021暑期
            selectedTableData:[],
            selectedModelTableData:[],
            selecteddataMethodTableData: [],
            currentNode:2,
            currentdataMethodNode: 2,
            currentModelNode:2,
            currentModelName:'',
            currentDataName:'',
            currentDataMethodName:'',

            parentNode:false,
            parentdataMethodNode:false,
            parentModelNode:false,
            childNode:true,
            childdataMethodNode:true,
            childModelNode:true,

            //log用于计数
            log_model: 0,
            log_data: 0,
            log_application: 0,
            log_info: 0,
            log_theme:0,
            form: {
                name: '',
            },

            tabledata_get: {},
            categoryname: [],
            categorynameModel:[],
            categorynameData:[],

            theme_oid: "",

            model_num: 0,
            data_num: 0,
            mnum: 0,
            mcnum: 0,
            dnum: 0,
            dcnum: 0,
            sub_detail: '',
            editableTabsValue_model: '1',
            editableTabsValue_data: '1',
            editableTabsValue_dataMethod: '1',
            editableTabsValue_applications: 'application1',

            tabledataflag: 0,
            tabledataflag1: 0,
            //用作改变title时的计数
            tableflag1: 0,
            tableflag2: 0,
            tableflag3: 0,

            editableTabs_model: [{
                id: 1,
                label: "Default",
                children:[],
                //model的全部信息
                tableData: [],
            }],
            editableTabs_data: [{
                id: 1,
                label: "Default",
                children:[],
                //data的全部信息
                tabledata: [],
            }],
            editableTabs_dataMethod: [{
                id: 1,
                label: "Default",
                children:[],
                //data的全部信息
                tabledata: [],
            }],
            editableTabs_applications: [{
                id: "1",
                name: '1',
                content: 'Tab 1 content'
            }],
            tabIndex_model: 1,
            tabIndex_data: 1,
            tabIndex_dataMethod: 1,
            tabIndex_application: 1,
            //定义存储从前端获取的数据，用于与后台进行传输
            origin_themeObj:{
                themeName:"",
                themeImage:"",
                themeDetail:"",
                classinfo: [],
                dataClassInfo: [],
                dataMethodClassInfo: [],
                application: []
            },
            edit_themeObj: {
                themeName: "",
                themeImage: "",
                themeDetail: "",
                classinfo: [],
                dataClassInfo: [],
                dataMethodClassInfo: [],
                application: []
            },
            themeObj: {
                themeName:"",
                themeImage:"",
                themeDetail:"",
                classinfo: [],
                dataClassInfo: [],
                dataMethodClassInfo: [],
                application: []
            },

            themeVersion: {
                modifierClass: [{
                    oid: "",
                    userName: '',
                    name: ''
                }],
                subDetails: [{
                    detail: '',
                    // time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    // Class<Check> checkClass;后台处理设置
                }],
                subClassInfos: [{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    mcname: '',
                    models: [{
                        model_oid: '',
                        model_name: '',
                    }]
                    // Class<Check> checkClass;后台处理设置
                }],
                subDataInfos: [{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    dcname: '',
                    data: [{
                        data_oid: '',
                        data_name: '',
                    }]
                    // Class<Check> checkClass;后台处理设置
                }],
                subApplications: [{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    // Class<Check> checkClass;后台处理设置
                    applicationname: '',
                    applicationlink: '',
                    application_image: '',
                    upload_application_image: ''
                }]
            },
            modelClassInfos: [],
            dataClassInfos: [],
            dataMethodClassInfos: [],

            oidnumber: 0,
            numOfModelPerRow: 5,
            classarr: [],
            dialogTableVisible: false,
            dialogTableVisible1: false,
            relateTitle: "",

            tableData: [],
            tableMaxHeight: 400,
            relateSearch: "",
            pageOption1: {
                paginationShow: false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,

                total: 264,
                searchResult: [],
            },

            pageOption2: {
                paginationShow: false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,

                total: 264,
                searchResult: [],
            },

            pageOption3: {
                paginationShow: false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,

                total: 264,
                searchResult: [],

            },

            curIndex: '5',

            ScreenMaxHeight: "0px",
            IframeHeight: "0px",
            editorUrl: "",
            load: false,


            ScreenMinHeight: "0px",

            userId: "",
            userName: "",
            loginFlag: false,
            activeIndex: 5,

            userInfo: {
                //username:"",
                name: "",
                email: "",
                phone: "",
                insName: ""
            },
            defaultModelProps:{},
            defaultProps: {},
            cls: [],
            clsStr: '',
            model_num1: 1,

            defaultActive: '1',
            dialogVisible: false,
            dialogVisibleModel: false,
            dialogVisibleData: false,
            dialogVisibleDataMethod: false,
            dialogVisibleApplication: false,
            dialogVisible3: false,
            form: {
                name: '',
                region: '',
                date1: '',
                date2: '',
                delivery: false,
                type: [],
                resource: '',
                desc: ''
            },
            editThemeActive: 0,
            isCollapse: false,
            // drawer: false,
            // direction: 'rtl',
            maintainer:[],

            controlEditMark: true,
        }
    },
    methods: {
        // 2021暑期
        // 2021暑期
        // 2021暑期
        modifyModel(modelClass){
            for(let n= 0 ;n <this.edit_themeObj.classinfo.length;++n){
                let targetClass = this.findClass(this.edit_themeObj.classinfo[n],modelClass.id);
                if(targetClass!=null){
                    targetClass.mcname = modelClass.label;
                }
            }
            modelClass.editActiveIndex = 0;
        },
        modifyData(dataClass){
            for(let n= 0 ;n <this.edit_themeObj.dataClassInfo.length;++n){
                let targetClass = this.findClass(this.edit_themeObj.dataClassInfo[n],dataClass.id);
                if(targetClass!=null){
                    targetClass.dcname = dataClass.label;
                }
            }
            dataClass.editActiveIndex = 0;
        },
        modifyDataMethod(dataMethodClass){
            for(let n= 0 ;n <this.edit_themeObj.dataMethodClassInfo.length;++n){
                let targetClass = this.findClass(this.edit_themeObj.dataMethodClassInfo[n],dataMethodClass.id);
                if(targetClass!=null){
                    targetClass.dmcname = dataMethodClass.label;
                }
            }
            dataMethodClass.editActiveIndex = 0;
        },

        appendModel(modelClass){
            const newChild =
                {
                    id:++this.tabIndex_model,
                    label:'New Item',
                    children:[],
                    tableData:[],
                    editActiveIndex:0,
            };
            if(modelClass.children==undefined||modelClass.children==null){
                this.$set(modelClass,'children',[]);
            }
            modelClass.children.push(newChild);
            if(modelClass.id == 1){
            //    第一层
                this.edit_themeObj.classinfo.push({
                    id:this.tabIndex_model,
                    mcname:'New Item',
                    children:[],
                    modelsoid:[]
                })
            }
            else{
                for(let n = 0;n<this.edit_themeObj.classinfo.length;++n){
                    let targetModel = this.findClass(this.edit_themeObj.classinfo[n],modelClass.id);
                    if(targetModel!=null){
                        targetModel.children.push({
                            id:this.tabIndex_model,
                            mcname:'New Item',
                            children:[],
                            modelsoid:[]
                        })
                        break
                    }
                }
            }

        },
        appendData(dataClass){
            const newChild =
                {
                    id:++this.tabIndex_data,
                    label:'New Item',
                    children:[],
                    tableData:[],
                    editActiveIndex:0,
                };
            if(dataClass.children==undefined||dataClass.children==null){
                this.$set(dataClass,'children',[]);
            }
            dataClass.children.push(newChild);
            if(dataClass.id == 1){
                //    第一层
                this.edit_themeObj.dataClassInfo.push({
                    id:this.tabIndex_data,
                    dcname:'New Item',
                    children:[],
                    datasoid:[]
                })
            }
            else{
                for(let n = 0;n<this.edit_themeObj.dataClassInfo.length;++n){
                    let targetData = this.findClass(this.edit_themeObj.dataClassInfo[n],dataClass.id);
                    if(targetData!=null){
                        targetData.children.push({
                            id:this.tabIndex_data,
                            dcname:'New Item',
                            children:[],
                            datasoid:[]
                        })
                        break
                    }
                }
            }

        },
        appendDataMethod(dataMethodClass){
            const newChild =
                {
                    id:++this.tabIndex_dataMethod,
                    label:'New Item',
                    children:[],
                    tableData:[],
                    editActiveIndex:0,
                };
            if(dataMethodClass.children==undefined||dataMethodClass.children==null){
                this.$set(dataMethodClass,'children',[]);
            }
            dataMethodClass.children.push(newChild);
            if(dataMethodClass.id == 1){
                //    第一层
                this.edit_themeObj.dataMethodClassInfo.push({
                    id:this.tabIndex_dataMethod,
                    dmcname:'New Item',
                    children:[],
                    dataMethodsoid:[]
                })
            }
            else{
                for(let n = 0;n<this.edit_themeObj.dataMethodClassInfo.length;++n){
                    let targetData = this.findClass(this.edit_themeObj.dataMethodClassInfo[n],dataMethodClass.id);
                    if(targetData!=null){
                        targetData.children.push({
                            id:this.tabIndex_dataMethod,
                            dmcname:'New Item',
                            children:[],
                            dataMethodsoid:[]
                        })
                        break
                    }
                }
            }

        },

        removeModel(parentClass,modelClass){
            if(parentClass==undefined){
                this.$confirm('Are you sure to remove all items?', 'Remove all items', {
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'No',
                    type:'warning'
                }).then(() => {
                    this.themeModelData[0].children.splice(0,this.themeModelData[0].children.length)
                    this.edit_themeObj.classinfo.splice(0,this.edit_themeObj.classinfo.length);
                    console.log(this.themeModelData[0],this.edit_themeObj)
                    this.$message({
                        type: 'success',
                        message: 'Removed successfully!'
                    });
                }).catch(()=>{
                    this.$message({
                        type: 'info',
                        message: 'Remove cancel!'
                    });

                })
            }
            else{
                if(parentClass.id==1){
                    let index = this.themeModelData[0].children.findIndex(d => d.id === modelClass.id)
                    this.themeModelData[0].children.splice(index,1);

                    index = this.edit_themeObj.classinfo.findIndex(d => d.id === modelClass.id)
                    this.edit_themeObj.classinfo.splice(index,1);
                    console.log(this.themeModelData,this.edit_themeObj)
                }
                else{
                    //处理themeModelData
                    let targetParentIndex = this.themeModelData[0].children.findIndex(d => d.id === parentClass.id)
                    let targetIndex = this.themeModelData[0].children[targetParentIndex].children.findIndex(d=> d.id === modelClass.id)
                    this.themeModelData[0].children[targetParentIndex].children.splice(targetIndex,1);
                //    处理edit_themeObj
                    targetParentIndex = this.edit_themeObj.classinfo.findIndex(d => d.id === parentClass.id)
                    targetIndex = this.edit_themeObj.classinfo[targetParentIndex].children.findIndex(d => d.id === modelClass.id)
                    this.edit_themeObj.classinfo[targetParentIndex].children.splice(targetIndex,1)
                   }
            }
        },
        removeData(parentClass,dataClass){
            if(parentClass==undefined){
                this.$confirm('Are you sure to remove all items?', 'Remove all items', {
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'No',
                    type:'warning'
                }).then(() => {
                    this.themeData[0].children.splice(0,this.themeData[0].children.length)
                    this.edit_themeObj.dataClassInfo.splice(0,this.edit_themeObj.dataClassInfo.length);
                    console.log(this.themeData[0],this.edit_themeObj)
                    this.$message({
                        type: 'success',
                        message: 'Removed successfully!'
                    });
                }).catch(()=>{
                    this.$message({
                        type: 'info',
                        message: 'Remove cancel!'
                    });

                })
            }
            else{
                if(parentClass.id==1){
                    let index = this.themeData[0].children.findIndex(d => d.id === dataClass.id)
                    this.themeData[0].children.splice(index,1);

                    index = this.edit_themeObj.dataClassInfo.findIndex(d => d.id === dataClass.id)
                    this.edit_themeObj.dataClassInfo.splice(index,1);
                    console.log(this.themeData,this.edit_themeObj)
                }
                else{
                    //处理themeModelData
                    let targetParentIndex = this.themeData[0].children.findIndex(d => d.id === parentClass.id)
                    let targetIndex = this.themeData[0].children[targetParentIndex].children.findIndex(d=> d.id === dataClass.id)
                    this.themeData[0].children[targetParentIndex].children.splice(targetIndex,1);
                //    处理edit_themeObj
                    targetParentIndex = this.edit_themeObj.dataClassInfo.findIndex(d => d.id === parentClass.id)
                    targetIndex = this.edit_themeObj.dataClassInfo[targetParentIndex].children.findIndex(d => d.id === dataClass.id)
                    this.edit_themeObj.dataClassInfo[targetParentIndex].children.splice(targetIndex,1)
                   }
            }
        },
        removeDataMethod(parentClass,dataMethodClass){
            if(parentClass==undefined){
                this.$confirm('Are you sure to remove all items?', 'Remove all items', {
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'No',
                    type:'warning'
                }).then(() => {
                    this.themeDataMethod[0].children.splice(0,this.themeDataMethod[0].children.length)
                    this.edit_themeObj.dataMethodClassInfo.splice(0,this.edit_themeObj.dataMethodClassInfo.length);
                    console.log(this.themeDataMethod[0],this.edit_themeObj)
                    this.$message({
                        type: 'success',
                        message: 'Removed successfully!'
                    });
                }).catch(()=>{
                    this.$message({
                        type: 'info',
                        message: 'Remove cancel!'
                    });

                })
            }
            else{
                if(parentClass.id==1){
                    let index = this.themeDataMethod[0].children.findIndex(d => d.id === dataMethodClass.id)
                    this.themeDataMethod[0].children.splice(index,1);

                    index = this.edit_themeObj.dataMethodClassInfo.findIndex(d => d.id === dataMethodClass.id)
                    this.edit_themeObj.dataMethodClassInfo.splice(index,1);
                    console.log(this.themeDataMethod,this.edit_themeObj)
                }
                else{
                    //处理themeModelData
                    let targetParentIndex = this.themeDataMethod[0].children.findIndex(d => d.id === parentClass.id)
                    let targetIndex = this.themeDataMethod[0].children[targetParentIndex].children.findIndex(d=> d.id === dataMethodClass.id)
                    this.themeDataMethod[0].children[targetParentIndex].children.splice(targetIndex,1);
                //    处理edit_themeObj
                    targetParentIndex = this.edit_themeObj.dataMethodClassInfo.findIndex(d => d.id === parentClass.id)
                    targetIndex = this.edit_themeObj.dataMethodClassInfo[targetParentIndex].children.findIndex(d => d.id === dataMethodClass.id)
                    this.edit_themeObj.dataMethodClassInfo[targetParentIndex].children.splice(targetIndex,1)
                   }
            }
        },

        handleTabClick(tab,event){
            console.log(tab)
            console.log(event)
            this.editableTabsValue_applications = tab.name;
        },


        show(id, container) {

            $(".x_content").hide();
            $("#" + id).show();

            $(".infoPanel").hide();
            $("#" + container).show();
        },

        showData(id, container) {
            $(".x_content").hide();
            $("#" + id).show();

            $(".infoPanel").hide();
            $("#" + container).show();
            },

        showDataMethod(id, container) {
            $(".x_content").hide();
            $("#" + id).show();

            $(".infoPanel").hide();
            $("#" + container).show();
            },

        findInfo(type,oid){
            //1为model,2为data,3为dataMethod
            let curObj;
            switch (type) {
                case 1:
                    curObj = this.modelInfo;
                    break;
                case 2:
                    curObj = this.dataInfo;
                    break;
                case 3:
                    curObj = this.dataMethodInfo;
                    break;
            }
            for(let i = 0;i<curObj.length;++i)
            {
                if(curObj[i].oid == oid)
                    return curObj[i];
            }
            curObj.push(this.find_oid(oid,type))
            return {};
        },
        initData(){
            this.log_theme++;
            if (this.log_theme == 1) {
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
                        $.ajax({
                            url: getThemeInfoByidApi(this.themeoid),
                            type: "GET",
                            data: {},
                            success: (result) => {
                                console.log(result)

                                let basicInfo = result.data;
                                //classinfo是model,dataClassInfo是data
                                let classinfo_edit = basicInfo.classinfo;
                                // console.log(classinfo_edit);

                                this.categorynameModel = [];
                                for (let i = 0; i < classinfo_edit.length - 1; i++) {
                                    //先将classinfo中的数据存储到themeObj中
                                    this.modelClass_add();
                                }

                                //从数据库获取数据，存放到editableTans_model里，此数组在前端渲染
                                for (let i = 0; i < classinfo_edit.length; i++) {
                                    //把category name数据存储起来
                                    // this.categorynameModel.push(classinfo_edit[i].mcname);


                                    this.editableTabs_model[0].children.push({
                                        id: ++this.tabIndex_model,
                                        label: classinfo_edit[i].mcname,
                                        children: [],
                                        tableData: [],
                                        editActiveIndex:0,
                                    });
                                    this.themeObj.classinfo.push({
                                        id: this.tabIndex_model,
                                        mcname: classinfo_edit[i].mcname,
                                        children:[],
                                        modelsoid: classinfo_edit[i].modelsoid,
                                    })

                                    //处理孩子
                                    if(classinfo_edit[i].children!=null&&classinfo_edit[i].children!=undefined&&classinfo_edit[i].children.length!=0){
                                        for(let j = 0;j<classinfo_edit[i].children.length;++j)
                                        {
                                            this.editableTabs_model[0].children[i].children.push({
                                                id:++this.tabIndex_model,
                                                label : classinfo_edit[i].children[j].mcname,
                                                children:[],
                                                tableData : [],
                                                editActiveIndex:0,

                                            })
                                            this.themeObj.classinfo[i].children.push({
                                                id:this.tabIndex_model,
                                                mcname : classinfo_edit[i].children[j].mcname,
                                                children : [],
                                                modelsoid : classinfo_edit[i].children[j].modelsoid
                                            })

                                            for(let k = 0;k<classinfo_edit[i].children[j].modelsoid.length;++k){
                                                this.find_oid(classinfo_edit[i].children[j].modelsoid[k], 1);//1代表type为modelitem
                                                this.editableTabs_model[0].children[i].children[j].tableData.push(this.tabledata_get);
                                                this.modelInfo.push(this.tabledata_get);

                                            }
                                        }
                                    }

                                    for (let j = 0; j < classinfo_edit[i].modelsoid.length; j++) {
                                        this.find_oid(classinfo_edit[i].modelsoid[j], 1);//1代表type为modelitem
                                        this.editableTabs_model[0].children[i].tableData.push(this.tabledata_get);
                                        this.modelInfo.push(this.tabledata_get);
                                    }
                                }

                                this.themeModelData = JSON.parse(JSON.stringify(this.editableTabs_model));
                                this.origin_themeModelData = JSON.parse(JSON.stringify(this.editableTabs_model))


                                //data部分
                                let dataClassInfo_edit = basicInfo.dataClassInfo;
                                this.categorynameData = [];
                                for (let i = 0; i < dataClassInfo_edit.length - 1; i++) {
                                    this.dataClass_add();
                                }

                                for (let i = 0; i < dataClassInfo_edit.length; i++) {
                                    this.editableTabs_data[0].children.push({
                                        id:++this.tabIndex_data,
                                        label : dataClassInfo_edit[i].dcname,
                                        children : [],
                                        tableData : [],
                                        editActiveIndex:0,

                                    });
                                    this.themeObj.dataClassInfo.push({
                                        id : this.tabIndex_data,
                                        dcname : dataClassInfo_edit[i].dcname,
                                        children : [],
                                        datasoid : dataClassInfo_edit[i].datasoid
                                    })
                                    if(dataClassInfo_edit[i].children!=null&&dataClassInfo_edit[i].children!=undefined&&dataClassInfo_edit[i].children.length!=0){
                                        for(let j = 0;j<dataClassInfo_edit[i].children.length;++j)
                                        {
                                            this.editableTabs_data[0].children[i].children.push({
                                                id : ++this.tabIndex_data,
                                                label : dataClassInfo_edit[i].children[j].dcname,
                                                children : [],
                                                tableData : [],
                                                editActiveIndex:0,

                                            });
                                            this.themeObj.dataClassInfo[i].children.push({
                                                id : this.tabIndex_data,
                                                dcname : dataClassInfo_edit[i].children[j].dcname,
                                                children : [],
                                                datasoid : dataClassInfo_edit[i].children[j].datasoid
                                            })
                                            for (let k = 0;k<dataClassInfo_edit[i].children[j].datasoid.length;++k){
                                                this.find_oid(dataClassInfo_edit[i].children[j].datasoid[k],2);//2代表type为dataItem
                                                this.editableTabs_data[0].children[i].children[j].tableData.push(this.tabledata_get);
                                                this.dataInfo.push(this.tabledata_get);
                                            }
                                        }
                                    }

                                    for (let j = 0; j < dataClassInfo_edit[i].datasoid.length; j++) {
                                        this.find_oid(dataClassInfo_edit[i].datasoid[j], 2);
                                        this.editableTabs_data[0].children[i].tableData.push(this.tabledata_get);
                                        this.dataInfo.push(this.tabledata_get);

                                    }
                                }
                                this.themeData = JSON.parse(JSON.stringify(this.editableTabs_data));
                                this.origin_themeData = JSON.parse(JSON.stringify(this.editableTabs_data));


                                //data Method
                                let dataMethodClassInfo_edit = basicInfo.dataMethodClassInfo;
                                if(dataMethodClassInfo_edit!=null&&dataMethodClassInfo_edit!=undefined){
                                    for(let i = 0;i < dataMethodClassInfo_edit.length;++i){
                                        this.editableTabs_dataMethod[0].children.push({
                                            id:++this.tabIndex_dataMethod,
                                            label : dataMethodClassInfo_edit[i].dmcname,
                                            children : [],
                                            tableData : [],
                                            editActiveIndex:0,

                                        });
                                        this.themeObj.dataMethodClassInfo.push({
                                            id : this.tabIndex_dataMethod,
                                            dmcname : dataMethodClassInfo_edit[i].dmcname,
                                            children : [],
                                            dataMethodsoid : dataMethodClassInfo_edit[i].dataMethodsoid
                                        })
                                        if(dataMethodClassInfo_edit[i].children!=null&&dataMethodClassInfo_edit[i].children!=undefined&&dataMethodClassInfo_edit[i].children.length!=0){
                                            for(let j = 0;j<dataMethodClassInfo_edit[i].children.length;++j)
                                            {
                                                this.editableTabs_dataMethod[0].children[i].children.push({
                                                    id : ++this.tabIndex_dataMethod,
                                                    label : dataMethodClassInfo_edit[i].children[j].dmcname,
                                                    children : [],
                                                    tableData : [],
                                                    editActiveIndex:0,

                                                });
                                                this.themeObj.dataMethodClassInfo[i].children.push({
                                                    id : this.tabIndex_dataMethod,
                                                    dmcname : dataMethodClassInfo_edit[i].children[j].dmcname,
                                                    children : [],
                                                    dataMethodsoid : dataMethodClassInfo_edit[i].children[j].dataMethodsoid
                                                })
                                                for (let k = 0;k<dataMethodClassInfo_edit[i].children[j].dataMethodsoid.length;++k){
                                                    this.find_oid(dataMethodClassInfo_edit[i].children[j].dataMethodsoid[k],3);//3代表type为dataMethod
                                                    this.editableTabs_dataMethod[0].children[i].children[j].tableData.push(this.tabledata_get);
                                                    this.dataMethodInfo.push(this.tabledata_get);

                                                }
                                            }
                                        }

                                        for (let j = 0; j < dataMethodClassInfo_edit[i].dataMethodsoid.length; j++) {
                                            this.find_oid(dataMethodClassInfo_edit[i].dataMethodsoid[j], 3);
                                            this.editableTabs_dataMethod[0].children[i].tableData.push(this.tabledata_get);
                                            this.dataMethodInfo.push(this.tabledata_get);
                                        }
                                    }

                                }
                                this.themeDataMethod = JSON.parse(JSON.stringify(this.editableTabs_dataMethod));
                                this.origin_themeDataMethod = JSON.parse(JSON.stringify(this.editableTabs_dataMethod));





                                //application
                                let application_edit = basicInfo.application;
                                if(application_edit!=null&&application_edit!=undefined){
                                    for (let i = 0; i < application_edit.length; i++) {
                                        let app = {};
                                        app.applicationname = basicInfo.application[i].applicationname;
                                        app.applicationlink = basicInfo.application[i].applicationlink;
                                        app.upload_application_image = basicInfo.application[i].application_image;
                                        app.imageTag = 0;
                                        if(app.upload_application_image!=''&&app.upload_application_image!=undefined){
                                            //有图
                                            app.imageTag = 1;
                                        }

                                        this.themeApplicationData.push(app);
                                        this.themeObj.application.push(app)

                                        //用作id
                                        this.themeApplicationData[i].name = 'application'+(this.tabIndex_application++).toString();
                                    }
                                    if(this.themeApplicationData.length == 0){
                                        this.themeApplicationData.push({
                                            name:'tagDelte',
                                            applicationname:'New Tab',
                                            applicationlink:'',
                                            upload_application_image:'',
                                            imageTag:0,
                                        })
                                        this.editableTabsValue_applications = 'tagDelte'
                                    }
                                }


                                this.origin_themeApplicationData = JSON.parse(JSON.stringify(this.themeApplicationData));


                                //显示themename
                                //themename实现双向数据绑定
                                // $("#nameInput").val(basicInfo.themename);
                                this.themeName = basicInfo.themename;
                                this.editThemeName = this.themeName;
                                this.themeObj.themeName = basicInfo.themename;


                                //显示themeimg
                                //themeimg实现双向数据绑定
                                // $('#imgShow').attr("src", basicInfo.image);
                                // $('#imgShow').show();
                                if(basicInfo.image==''||basicInfo.image==null)
                                {
                                    //设为默认照片
                                    basicInfo.image = '/static/img/icon/default.png';
                                    this.imgLog = 0;
                                }
                                this.themeObj.themeImage = basicInfo.image;
                                this.themeImage = this.themeObj.themeImage;

                                //显示theme detail
                                this.themeObj.themeDetail = basicInfo.detail;
                                this.themeDetail = basicInfo.detail;


                                //值复制
                                this.edit_themeObj = JSON.parse(JSON.stringify(this.themeObj))
                                this.origin_themeObj = JSON.parse(JSON.stringify(this.themeObj))

                                console.log(this.themeObj)
                                console.log(this.themeData)
                            }
                        })
                    }
                })
            }


        },
        editNameSave(){
            if(this.editThemeName.replace(/\s+/g,"")==""){
                this.$message(
                    {
                        message:"Please enter an available name!",
                        type:"warning"
                    })
            }
            else {
                this.edit_themeObj.themeName = this.editThemeName;
                this.NameEditActiveIndex = 0;
            }

        },
        editDetailSave(){
            this.edit_themeObj.themeDetail = tinyMCE.activeEditor.getContent();
            this.DetailEditActiveIndex = 0;
        },
        editSave() {
            //查看classinfo与dataClassInfo，如果存在一个也未输入，则删除
            // if (this.themeObj.classinfo.length==1&&this.themeObj.classinfo[0].mcname==""&&this.themeObj.classinfo[0].modelsoid.length==0) {
            //     this.themeObj.classinfo.splice(0,1);
            // }
            // if (this.themeObj.dataClassInfo.length==1&&this.themeObj.dataClassInfo[0].dcname==""&&this.themeObj.dataClassInfo[0].datasoid.length==0) {
            //     this.themeObj.dataClassInfo.splice(0,1);
            // }
            // if(this.themeObj.application.length==1&&this.themeObj.application[0].applicationname==""&&this.themeObj.application[0].applicationlink==""&&this.themeObj.application[0].upload_application_image==""){
            //     this.themeObj.application.splice(0,1);
            // }


            this.themeObj = JSON.parse(JSON.stringify(this.edit_themeObj))

            //更新themename
            this.themeObj.themeName = this.edit_themeObj.themeName.trim();

            //更新detail
            var detail = this.edit_themeObj.themeDetail;
            if(typeof(detail) != 'undefined'){
                this.themeObj.detail = detail.trim();
            }
            else{
                this.themeObj.detail = ''
            }

            //更新照片
            if(this.imgLog!=0){
                //img已更新
                this.themeObj.image = $('#imgShow').get(0).src;
                this.themeObj.uploadImage = $('#imgShow').get(0).currentSrc;//
            }
            else {
                //未更新img
                this.themeObj.image='';
                this.themeObj.uploadImage='';
            }

            //更新application
            for(let i = 0; i< this.themeApplicationData.length;++i){

                if(this.themeApplicationData[i].name=='tagDelte'&&this.themeApplicationData[i].applicationname=='New Tab'&&this.themeApplicationData[i].applicationlink==''){
                    this.themeApplicationData.splice(i,1);
                    continue;
                }
                delete this.themeApplicationData[i].name;
                delete this.themeApplicationData[i].imageTag;
                if(this.themeApplicationData[i].applicationname.toString().trim()==''&&this.themeApplicationData[i].applicationlink.toString().trim()==''){
                    this.themeApplicationData.splice(i,1);
                }
            }
            this.themeObj.application = JSON.parse(JSON.stringify(this.themeApplicationData));




            //更新model，data，datamethd
            this.themeObj.tabledata = this.editableTabs_model;
            this.themeObj.themeData = this.themeData;
            this.themeObj.themeModelData = this.themeModelData

            let formData=new FormData();
            console.log(this.themeObj);


            //将数据打包传输
            this.themeObj["themeOid"] = this.themeoid;
            let _this = this;

            let file = new File([JSON.stringify(this.themeObj)],'ant.txt',{
                type: 'text/plain',
            });
            formData.append("info",file);
            let that = this;
            $.ajax({
                url: updateThemeApi(this.themeoid),
                type: "PUT",
                processData: false,
                contentType: false,
                async: true,
                data: formData,
                success: function (result) {
                    // loading.close();
                    if (result.code === 0) {
                        if(result.data.method==="update") {
                            // alert("Update Success");
                            that.$message({
                                message : 'Update Success',
                                type: 'success'});
                            $("#editModal", parent.document).remove();
                            that.dialogVisible3 = false;
                            setTimeout(function () {
                                location.reload()

                            },1000)
                            // window.location.href = "/repository/theme/" + result.data.oid;
                        }
                        else{
                            this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
                                type:"success",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                            that.dialogVisible3 = false;
                            // window.location.href = "/repository/theme/" + result.data.oid;
                            // alert("Success! Changes have been submitted, please wait for the author to review.");
                            //产生信号调用计数，启用websocket
                            // window.location.href = "/user/userSpace";
                        }
                    }
                    else if(result.code==-2){
                        alert("Please login first!");
                        // window.location.href="/user/login";
                    }
                    else{
                        alert(result.msg);
                    }
                },
                error:function(){

                    _this.$message({
                        message: 'Failure to update,Please login first!',
                        type: 'warning'
                    });
                    setTimeout(() => {
                        window.location.href = "/user/login";

                    },1500);

                }

            })
        },
        editCancel(){
             //还原名字
            //还原detail
            this.edit_themeObj = Object.assign({},this.themeObj)

             //还原图片
            $('#imgShow').attr("src",this.themeObj.themeImage);
            $('#imgShow').show();

            //还原model
            this.themeModelData = JSON.parse(JSON.stringify(this.origin_themeModelData))
            //还原data
            this.themeData = JSON.parse(JSON.stringify(this.origin_themeData));
            //还原
            this.themeDataMethod = JSON.parse(JSON.stringify(this.origin_themeDataMethod));


            this.edit_themeObj = JSON.parse(JSON.stringify(this.origin_themeObj));

            this.editActiveIndex = 0;
            this.NameEditActiveIndex =0;
            this.DetailEditActiveIndex =0;


        },
        edit_themeDetail(){

            //显示detail
            $("#themeText").html(this.themeObj.detail);
            // if(++this.log_detail==1)
            // {
                initTinymce('textarea#themeText')

                tinymce.init({
                    selector: "textarea#themeText",
                    height: 300,
                    theme: 'silver',
                    plugins: ['link', 'table', 'image', 'media'],
                    image_title: true,
                    // enable automatic uploads of images represented by blob or data URIs
                    automatic_uploads: true,
                    // URL of our upload handler (for more details check: https://www.tinymce.com/docs/configure/file-image-upload/#images_upload_url)
                    // images_upload_url: 'postAcceptor.php',
                    // here we add custom filepicker only to Image dialog
                    file_picker_types: 'image',

                    file_picker_callback: function (cb, value, meta) {
                        var input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');
                        input.onchange = function () {
                            var file = input.files[0];

                            var reader = new FileReader();
                            reader.readAsDataURL(file);
                            reader.onload = function () {
                                var img = reader.result.toString();
                                cb(img, {title: file.name});
                            }
                        };
                        input.click();
                    },
                    images_dataimg_filter: function (img) {
                        return img.hasAttribute('internal-blob');
                    }
                });

            // }
            this.DetailEditActiveIndex = 1;

        },
        edit_themeModel(){
            this.dialogVisibleModel=true;
        },
        dialogCancel(type){
            switch (type) {
                case 'Model':
                    //处理themeModelData
                    let tag = 1;
                    //    是孩子
                        for(let i = 0;i< this.themeModelData[0].children.length;++i){
                            if(this.themeModelData[0].children[i].children.length>0){
                                let targetIndex = this.themeModelData[0].children[i].children.findIndex(d => d.id === this.currentModelNode);
                                if(targetIndex!=-1){
                                    this.themeModelData[0].children[i].children[targetIndex].tableData = JSON.parse(JSON.stringify(this.originModelNode.tableData))
                                    this.selectedModelTableData =JSON.parse(JSON.stringify(this.originModelNode.tableData))
                                    tag=0;
                                    break;
                                }
                            }
                        }
                    //    不是孩子
                    if(tag){
                        for(let i = 0;i< this.themeModelData[0].children.length;++i){
                            if(this.themeModelData[0].children[i].id === this.currentModelNode){
                                this.themeModelData[0].children[i].tableData = JSON.parse(JSON.stringify(this.originModelNode.tableData))
                                this.selectedModelTableData =JSON.parse(JSON.stringify(this.originModelNode.tableData))
                                break;
                            }
                        }
                    }


                    //处理edit_themeObj
                    for(let n = 0;n<this.edit_themeObj.classinfo.length;++n){
                        let targetClass = this.findClass(this.edit_themeObj.classinfo[n],this.currentModelNode);
                        if(targetClass!=undefined&&targetClass!=null){
                            targetClass.modelsoid.splice(0,targetClass.modelsoid.length)
                            if(targetClass.modelsoid!=null){
                                for(let j = 0;j<this.selectedModelTableData.length;++j){
                                    targetClass.modelsoid.push(this.selectedModelTableData.oid)
                                }
                            }
                        break;
                        }

                    }
                    break;
                case 'Data':
                    //处理themeModelData
                    let tagD = 1;
                    //    是孩子
                    for(let i = 0;i< this.themeData[0].children.length;++i){
                        if(this.themeData[0].children[i].children.length>0){
                            let targetIndex = this.themeData[0].children[i].children.findIndex(d => d.id === this.currentNode);
                            if(targetIndex!=-1){
                                this.themeData[0].children[i].children[targetIndex].tableData = JSON.parse(JSON.stringify(this.originDataNode.tableData))
                                this.selectedTableData =JSON.parse(JSON.stringify(this.originDataNode.tableData))
                                tagD=0;
                                break;
                            }
                        }
                    }
                    //    不是孩子
                    if(tagD){
                        for(let i = 0;i< this.themeData[0].children.length;++i){
                            if(this.themeData[0].children[i].id === this.currentNode){
                                this.themeData[0].children[i].tableData = JSON.parse(JSON.stringify(this.originDataNode.tableData))
                                this.selectedTableData =JSON.parse(JSON.stringify(this.originDataNode.tableData))
                                break;
                            }
                        }
                    }


                    //处理edit_themeObj
                    for(let n = 0;n<this.edit_themeObj.dataClassInfo.length;++n){
                        let targetClass = this.findClass(this.edit_themeObj.dataClassInfo[n],this.currentNode);
                        if(targetClass!=undefined&&targetClass!=null) {
                            targetClass.datasoid.splice(0, targetClass.datasoid.length)
                            if (targetClass.datasoid != null) {
                                for (let j = 0; j < this.selectedTableData.length; ++j) {
                                    targetClass.datasoid.push(this.selectedTableData.oid)
                                }
                            }
                            break;

                        }
                    }
                    break;
                case 'DataMethod':
                    //处理themeModelData
                    let tagDM = 1;
                    //    是孩子
                    for(let i = 0;i< this.themeDataMethod[0].children.length;++i){
                        if(this.themeDataMethod[0].children[i].children.length>0){
                            let targetIndex = this.themeDataMethod[0].children[i].children.findIndex(d => d.id === this.currentdataMethodNode);
                            if(targetIndex!=-1){
                                this.themeDataMethod[0].children[i].children[targetIndex].tableData = JSON.parse(JSON.stringify(this.originDataMethodNode.tableData))
                                this.selecteddataMethodTableData =JSON.parse(JSON.stringify(this.originDataMethodNode.tableData))
                                tagDM=0;
                                break;
                            }
                        }
                    }
                    //    不是孩子
                    if(tagDM){
                        for(let i = 0;i< this.themeDataMethod[0].children.length;++i){
                            if(this.themeDataMethod[0].children[i].id === this.currentdataMethodNode){
                                this.themeDataMethod[0].children[i].tableData = JSON.parse(JSON.stringify(this.originDataMethodNode.tableData))
                                this.selecteddataMethodTableData =JSON.parse(JSON.stringify(this.originDataMethodNode.tableData))
                                break;
                            }
                        }
                    }


                    //处理edit_themeObj
                    for(let n = 0;n<this.edit_themeObj.dataMethodClassInfo.length;++n){
                        let targetClass = this.findClass(this.edit_themeObj.dataMethodClassInfo[n],this.currentdataMethodNode);
                        if(targetClass!=undefined&&targetClass!=null) {
                            targetClass.dataMethodsoid.splice(0, targetClass.dataMethodsoid.length)
                            if (targetClass.dataMethodsoid != null) {
                                for (let j = 0; j < this.selecteddataMethodTableData.length; ++j) {
                                    targetClass.dataMethodsoid.push(this.selecteddataMethodTableData.oid)
                                }
                            }
                            break;

                        }
                    }
                    break;
                case 'Application':
                    this.themeApplicationData = JSON.parse(JSON.stringify(this.origin_themeApplicationData));
                    break;
            }
            this.dialogClose(type);
        },
        dialogSave(type){
            switch (type) {
                case 'Model':
                    this.changeModelClassNode(this.curModelNode)
                    break;
                case 'Data':
                    this.changeClassNode(this.curDataNode)
                    break;
                case 'DataMethod':
                    this.changedataMethodClassNode(this.curDataMethodNode)
                    break;

            }
            this.dialogClose(type)
            console.log(this.themeObj,this.themeData)
        },
        dialogClose(type){
            switch (type) {
                case 'Model':
                    this.dialogVisibleModel = false;
                    break;
                case 'Data':
                    this.dialogVisibleData = false;
                    break;
                case 'DataMethod':
                    this.dialogVisibleDataMethod = false;
                    break;
                case 'Application':
                    this.dialogVisibleApplication = false;
                    break;
            }

        },
        // 2021暑期
        // 2021暑期
        // 2021暑期



        selectUserImg(){
            $('#editUserImg').modal('show');
            console.log($("#imgChange"))

        },
        findFirstChildObj(parent){
            let node
            if(parent.children.length==0){
                node = parent
            }
            else{
                if(parent.children[0].children.length > 0){
                    this.findFirstChildObj(parent.children[0])
                }else{
                    node = parent.children[0]
                }

            }
            return node

        },
        findFirstChild(parent){
            let nodeId
            if(parent.children[0].children.length > 0){
                this.findFirstChild(parent.children[0])
            }else{
                nodeId = parent.children[0].id
            }
            return nodeId
        },
        findModelTableData(modelClass){
            //没有孩子&&是当前节点
            if(modelClass.children.length ==0 && modelClass.id == this.currentModelNode){
                return modelClass.modelsoid
            }else if(modelClass.children.length > 0){
                //有孩子
                for (let n = 0; n <modelClass.children.length; n++) {
                    var flag = this.findModelTableData(modelClass.children[n])
                    if (flag != null) {
                        return flag
                    }
                }
            }else{
                //不是当前节点
                return null
            }

        },
        findTableData(dataClass){
            if(dataClass.children.length ==0 && dataClass.id == this.currentNode){
                return dataClass.datasoid
            }else if(dataClass.children.length > 0){
                for (let n = 0; n <dataClass.children.length; n++) {
                    var flag = this.findTableData(dataClass.children[n])
                    if (flag != null) {
                        return flag
                    }
                }
            }else{
                return null
            }

        },
        findDataMethodTableData(dataMethodClass){
            if(dataMethodClass.children.length ==0 && dataMethodClass.id == this.currentdataMethodNode){
                return dataMethodClass.dataMethodsoid;
            }else if(dataMethodClass.children.length > 0){
                for(let n = 0;n<dataMethodClass.children.length;++n){
                    var flag = this.findDataMethodTableData(dataMethodClass.children[n])
                    if(flag!=null)
                    {
                        return flag;
                    }
                }
            }else {
                return null;
            }

        },
        findClass(_class, classId){
            if(_class.id == classId){
                return _class
            }else if(_class.children.length > 0){
                for (let n = 0; n <_class.children.length; n++) {
                    var flag = this.findClass(_class.children[n],classId)
                    if (flag != null) {
                        return flag
                    }
                }
            }else{
                return null
            }
        },

        // tree的四个事件
        changeClassNode(data,node) {
            // this.selectedTableData = [];
            this.relateType = "dataItem"
            if(data.children.length === 0){
                this.selectedTableData = data.tableData
                this.currentNode = data.id
                this.parentNode = false
                this.childNode = true
            }else{
                this.currentNode = this.findFirstChild(data)
                this.parentNode = true
                this.childNode = false
            }
            this.currentDataName = data.label
            this.originDataNode = JSON.parse(JSON.stringify(data));
            this.curDataNode = data;


        },
        changeModelClassNode(data,node) {
            // this.selectedModelTableData = [];
            this.relateType = "modelItem";
            if(data.children.length === 0){
                //没有孩子
                this.selectedModelTableData = data.tableData
                this.currentModelNode = data.id
                this.parentModelNode = false
                this.childModelNode = true
            }else{
                //有孩子
                this.currentModelNode = this.findFirstChild(data)
                this.parentModelNode = true
                this.childModelNode = false
            }
            // console.log(this.themeObj)
            // console.log(this.selectedModelTableData)
            // console.log(this.editableTabs_model)
            this.currentModelName = data.label
            this.originModelNode = JSON.parse(JSON.stringify(data));
            this.curModelNode = data;


            console.log(this.themeObj,data)


        },
        changedataMethodClassNode(data,node){
            if(data.children.length === 0){
                this.selecteddataMethodTableData = data.tableData
                this.currentdataMethodNode = data.id
                this.parentdataMethodNode = false
                this.childdataMethodNode = true
            }else{
                this.currentdataMethodNode = this.findFirstChild(data)
                this.parentdataMethodNode = true
                this.childdataMethodNode = false
            }
            this.currentDataMethodNameame = data.label
            this.originDataMethodNode = JSON.parse(JSON.stringify(data));
            this.curDataMethodNode = data;



            console.log(this.themeObj)
            console.log(this.selecteddataMethodTableData)
            console.log(this.editableTabs_dataMethod)
            console.log(this.currentDataMethodNameame)

        },

        // model的两个事件
        addModel(index, row) {
            console.log(this.selectedModelTableData)
            console.log(this.currentModelNode)

            // 往数组中添加新模型
            var flag = false
            //判断已选条目是否已存在
            for (var n = 0; n < this.selectedModelTableData.length; n++) {
                if(this.selectedModelTableData[n].oid == row.oid){
                    flag = true
                    break
                }
            }
            //
            if(!flag){
                this.selectedModelTableData.push(row)

                // 找到当前分类的数组
                for (var n = 0; n < this.edit_themeObj.classinfo.length; n++) {
                    var modelsoid = this.findModelTableData(this.edit_themeObj.classinfo[n])
                    if(modelsoid != null){
                        modelsoid.push(row.oid)
                        break
                    }
                }
                // this.themeObj.classinfo[num].modelsoid.push(row.oid);
            }
            console.log(this.themeModelData)
        },
        deleteModel(index, row) {
            // 删除数组中的模型
            for (var n = 0; n < this.selectedModelTableData.length; n++) {
                if(this.selectedModelTableData[n].oid == row.oid){
                    this.selectedModelTableData.splice(n, 1);


                    break
                }
            }

            // 找到themeobj中当前分类的数组
            for (var n = 0; n < this.edit_themeObj.classinfo.length; n++) {
                var modelsoid = this.findModelTableData(this.edit_themeObj.classinfo[n])
                if(modelsoid != null){
                    for (var m = 0; m < modelsoid.length; m++) {
                        if(modelsoid[m] == row.oid){
                            modelsoid.splice(m, 1);
                            break
                        }
                    }
                    break
                }
            }

        },
        // data的两个事件
        addData(index, row) {

            // 往数组中添加新模型
            var flag = false
            for (var n = 0; n < this.selectedTableData.length; n++) {
                if(this.selectedTableData[n].oid == row.oid){
                    flag = true
                    break
                }
            }
            if(!flag){
                this.selectedTableData.push(row)
                // 找到当前分类的数组
                for (var n = 0; n < this.edit_themeObj.dataClassInfo.length; n++) {
                    var datasoid = this.findTableData(this.edit_themeObj.dataClassInfo[n])
                    if(datasoid != null){
                        datasoid.push(row.oid)
                        break
                    }
                }
                // this.themeObj.dataClassInfo[num].datasoid.push(row.oid);
            }

        },
        deleteData(index, row) {
            // 删除数组中的模型
            for (var n = 0; n < this.selectedTableData.length; n++) {
                if(this.selectedTableData[n].oid == row.oid){
                    this.selectedTableData.splice(n, 1);
                    break
                }
            }

            // 找到themeobj中当前分类的数组
            for (var n = 0; n < this.themeObj.dataClassInfo.length; n++) {
                var datasoid = this.findModelTableData(this.edit_themeObj.dataClassInfo[n])
                if(datasoid != null){
                    for (var m = 0; m < datasoid.length; m++) {
                        if(datasoid[m] == row.oid){
                            datasoid.splice(m, 1);
                            break
                        }
                    }
                    break
                }
            }
        },
        //dataMethod
        addDataMethod(index, row) {

            // 往数组中添加新模型
            var flag = false
            for (var n = 0; n < this.selecteddataMethodTableData.length; n++) {
                if(this.selecteddataMethodTableData[n].oid == row.oid){
                    flag = true
                    break
                }
            }
            if(!flag){
                this.selecteddataMethodTableData.push(row)
                // 找到当前分类的数组
                for (var n = 0; n < this.edit_themeObj.dataMethodClassInfo.length; n++) {
                    var datasoid = this.findDataMethodTableData(this.edit_themeObj.dataMethodClassInfo[n])
                    if(datasoid != null){
                        datasoid.push(row.oid)
                        break
                    }
                }
                // this.themeObj.dataClassInfo[num].datasoid.push(row.oid);
            }

        },
        deleteDataMethod(index, row) {
            // 删除数组中的模型
            for (var n = 0; n < this.selecteddataMethodTableData.length; n++) {
                if(this.selecteddataMethodTableData[n].oid == row.oid){
                    this.selecteddataMethodTableData.splice(n, 1);
                    break
                }
            }

            // 找到themeobj中当前分类的数组
            for (var n = 0; n < this.edit_themeObj.dataMethodClassInfo.length; n++) {
                var datasoid = this.findDataMethodTableData(this.edit_themeObj.dataMethodClassInfo[n])
                if(datasoid != null){
                    for (var m = 0; m < datasoid.length; m++) {
                        if(datasoid[m] == row.oid){
                            datasoid.splice(m, 1);
                            break
                        }
                    }
                    break
                }
            }

        },
        // 图片加载失败的回调
        errorHandler(){
            return true
        },

        //获取message_confirm页面
        getmessagepage() {
            console.log("ok");
            window.location.href = "/theme/getmessagepage/" + this.themeoid;
        },

        modelClass_add() {
            this.mcnum++;
            this.tableflag1++;
            this.tabledataflag++;
            // $("#categoryname").attr('id','categoryname_past');//改变当前id名称
            // $(".el-tabs__new-tab").eq(0).click();
        },
        dataClass_add() {
            // this.themeObj.dataClassInfo[this.dcnum].dcname = $("#categoryname2"+this.tableflag2).val();

            this.dcnum++;
            this.tableflag2++;
            this.tabledataflag1++;

            // $(".el-tabs__new-tab").eq(1).click();
        },
        //

        addTab(){
            this.themeApplicationData.push({
                name:'application'+(this.tabIndex_application++).toString(),
                applicationname: 'new Tab'+(this.tabIndex_application-1).toString(),
                applicationlink:'',
                upload_application_image:'',
                imageTag:0,
            })
            this.editableTabsValue_applications = 'application'+(this.tabIndex_application-1).toString();
            console.log(this.themeApplicationData)
        },

        handleTabsEdit_applications(targetName, action) {

            // if (action === 'add') {
            //     let newTabName = ++this.tabIndex_application + '';
            //     this.themeObj.application.push({
            //         id: newTabName,
            //         applicationname: '',
            //         applicationlink: '',
            //         application_image: '',
            //         upload_application_image: '',
            //     })
            //
            //     this.editableTabs_applications.push({
            //         id: newTabName,
            //         name: newTabName,
            //         content: 'New Tab content'
            //     });
            //     this.editableTabsValue_applications = newTabName;
            // }
            if (action === 'remove') {

                for(let i = 0;i<this.themeApplicationData.length;++i) {
                    if (this.themeApplicationData[i].name == targetName) {
                        this.themeApplicationData.splice(i, 1)
                        this.editableTabsValue_applications='application'+(i).toString();
                        return;
                    }
                }

            }
        },
        handleClose(done) {
            this.$confirm('Are you sure to close？')
                .then(_ => {
                    done();
                })
                .catch(_ => {
                });
        },
        handlePageChange1(val) {
            // val--;
            this.pageOption1.currentPage = val;

            this.search1();
        },
        handlePageChange2(val) {
            this.pageOption2.currentPage = val;
            this.search2();
        },
        handlePageChange3(val) {
            this.pageOption3.currentPage = val;
            this.search3()
        },
        search1() {
            this.relateType = "modelItem";
            if (this.pageOption1.currentPage == 0) {
                this.pageOption1.currentPage++;
            }
            var data = {
                asc: this.pageOption1.sortAsc,
                page: this.pageOption1.currentPage - 1,
                pageSize: this.pageOption1.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
            };
            let url, contentType;
            url = "/" + this.relateType + "/list";
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                async: true,
                contentType: contentType,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;

                        this.pageOption1.total = data.total;
                        this.pageOption1.pages = data.pages;
                        this.pageOption1.searchResult = data.list;
                        this.pageOption1.users = data.users;
                        this.pageOption1.progressBar = false;
                        this.pageOption1.paginationShow = true;

                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },
        // aaa(item){
        //     window.location.href='/profile/'+item.name
        // },
        search2() {
            this.relateType = "dataItem";
            if (this.pageOption2.currentPage == 0) {
                this.pageOption2.currentPage++;
            }
            var data = {
                asc: this.pageOption2.sortAsc,
                page: this.pageOption2.currentPage - 1,
                pageSize: this.pageOption2.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
                dataType:"all",
            };
            let url, contentType;

            url = "/dataItem/searchByName";
            data = JSON.stringify(data);
            contentType = "application/json";


            $.ajax({
                type: "POST",
                url: url,
                data: data,
                async: true,
                contentType: contentType,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;

                        this.pageOption2.total = data.total;
                        this.pageOption2.pages = data.pages;
                        this.pageOption2.searchResult = data.list;
                        this.pageOption2.users = data.users;
                        this.pageOption2.progressBar = false;
                        this.pageOption2.paginationShow = true;

                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },
        search3() {
            this.relateType = "dataApplication";
            if (this.pageOption3.currentPage == 0) {
                this.pageOption3.currentPage++;
            }
            var data = {
                asc: this.pageOption3.sortAsc,
                page: this.pageOption3.currentPage - 1,
                pageSize: this.pageOption3.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
                dataType:"all",
            };
            let url, contentType;

            url = "/dataApplication/searchByName";
            data = JSON.stringify(data);
            contentType = "application/json";


            $.ajax({
                type: "POST",
                url: url,
                data: data,
                async: true,
                contentType: contentType,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;


                        this.pageOption3.total = data.total;
                        this.pageOption3.pages = data.pages;
                        this.pageOption3.searchResult = data.list;
                        this.pageOption3.users = data.users;
                        this.pageOption3.progressBar = false;
                        this.pageOption3.paginationShow = true;

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
                    let arr = window.location.href.split("/");
                    let bindOid = arr[arr.length - 1].split("#")[0];
                    this.setSession("bindOid", bindOid);
                    switch (this.relateType) {
                        case "modelItem":
                            window.open("/user/createModelItem", "_blank")
                            break;
                        case "conceptualModel":
                            window.open("/user/createConceptualModel", "_blank")
                            break;
                        case "logicalModel":
                            window.open("/user/createLogicalModel", "_blank")
                            break;
                        case "computableModel":
                            window.open("/user/createComputableModel", "_blank")
                            break;
                        case "concept":
                            window.open("/repository/createConcept", "_blank")
                            break;
                        case "spatialReference":
                            window.open("/repository/createSpatialReference", "_blank")
                            break;
                        case "template":
                            window.open("/repository/createTemplate", "_blank")
                            break;
                        case "unit":
                            window.open("/repository/createUnit", "_blank")
                            break;
                    }

                }
            })
        },
        handleEdit(index, row) {
            let flag = false;
            let j = 0;
            let num;
            // let num;

            //找到当前选定的tab对应的数值与id对应
            for (i = 0; i < this.editableTabs_model.length; i++) {
                if (this.editableTabs_model[i].id == this.editableTabsValue_model) {
                    num = i;
                    break;
                }
            }
            for (i = 0; i < this.editableTabs_model[num].tabledata.length; i++) {
                let tableRow = this.editableTabs_model[num].tabledata[i];
                if (tableRow.oid == row.oid) {
                    flag = true;
                    break;
                }
            }
            // num=1;
            if (!flag) {
                this.editableTabs_model[num].tabledata.push(row);
                // this.themeObj.classinfo[num].mcname = $("#categoryname"+this.tableflag1).val();
                this.themeObj.classinfo[num].modelsoid.push(row.oid);
            }
        },
        handleEdit1(index, row) {
            let flag = false;
            let j = 0;
            let num;
            for (i = 0; i < this.editableTabs_data.length; i++) {
                if (this.editableTabs_data[i].id == this.editableTabsValue_data) {
                    num = i;
                    break;
                }
            }
            for (i = 0; i < this.editableTabs_data[num].tabledata.length; i++) {
                let tableRow = this.editableTabs_data[num].tabledata[i];
                if (tableRow.oid == row.oid) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                this.editableTabs_data[num].tabledata.push(row);
                // this.themeObj.dataClassInfo[num].dcname = $("#categoryname2"+this.tableflag2).val();
                this.themeObj.dataClassInfo[num].datasoid.push(row.oid);
            }
        },
        handleDelete1(index, row) {
            let tablist = $(".el-tabs__nav").eq(0);//取出model的tablist
            let tab_id = tablist.find('.is-active');//过滤出active的tab
            console.log(tab_id[0].id);
            let str = tab_id[0].id;
            let num = parseInt(str.substring(1).substring(1).substring(1).substring(1));//取出当前tab的数字
            console.log(num);
            console.log(row);
            num--;
            console.log(index, row);
            let table = new Array();
            for (i = 0; i < this.editableTabs_model[num].tabledata.length; i++) {
                table.push(this.editableTabs_model[num].tabledata[i]);
            }
            table.splice(index, 1);
            this.editableTabs_model[num].tabledata = table;

            let table1 = new Array();
            for (i = 0; i < this.themeObj.classinfo[num].modelsoid.length; i++) {
                table1.push(this.themeObj.classinfo[num].modelsoid[i]);
            }
            table1.splice(index, 1);
            this.themeObj.classinfo[num].modelsoid = table1;
        },
        handleDelete2(index, row) {
            let tablist = $(".el-tabs__nav").eq(1);//取出model的tablist
            let tab_id = tablist.find('.is-active');//过滤出active的tab
            console.log(tab_id[0].id);
            let str = tab_id[0].id;
            let num = parseInt(str.substring(1).substring(1).substring(1).substring(1));//取出当前tab的数字
            console.log(num);
            console.log(row);
            num--;
            console.log(index, row);
            let table = new Array();
            for (i = 0; i < this.editableTabs_data[num].tabledata.length; i++) {
                table.push(this.editableTabs_data[num].tabledata[i]);
            }
            table.splice(index, 1);
            this.editableTabs_data[num].tabledata = table;

            let table1 = new Array();
            for (i = 0; i < this.themeObj.dataClassInfo[num].datasoid.length; i++) {
                table1.push(this.themeObj.dataClassInfo[num].datasoid[i]);
            }
            table1.splice(index, 1);
            this.themeObj.dataClassInfo[num].datasoid = table1;
        },
        getRelation() {
            //从地址栏拿到oid
            let arr = window.location.href.split("/");
            let oid = arr[arr.length - 1].split("#")[0];
            let data = {
                oid: oid,
                type: this.relateType
            };
            $.ajax({
                type: "GET",
                url: "/modelItem/getRelation",
                data: data,
                async: true,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        console.log(data)

                        this.tableData = data;

                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },
        handleSelect(index, indexPath) {
            this.setSession("index", index);
            window.location.href = "/user/userSpace"
        },
        handleCheckChange(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree2.getCheckedNodes();
            let classes = [];
            let str = '';
            for (let i = 0; i < checkedNodes.length; i++) {
                // console.log(checkedNodes[i].children)
                if (checkedNodes[i].children != undefined) {
                    continue;
                }

                classes.push(checkedNodes[i].oid);
                str += checkedNodes[i].label;
                if (i != checkedNodes.length - 1) {
                    str += ", ";
                }
            }
            this.cls = classes;
            this.clsStr = str;

        },
        changeOpen(n) {
            this.activeIndex = n;
        },



        controlButtonSet(){        // 控制两个control_button，只让其中一个显示出来
            setTimeout(this.controlButtonSet1,250)      // 下拉菜单出现了再执行函数
        },
        controlButtonSet1(){        // 控制两个control_button，只让其中一个显示出来
            let height = $('#leftBottomBody')[0].offsetHeight
            let control_button = $('.controlEditButton')
            if(height > 700) {
                control_button[1].style.display = 'none'
                control_button[0].style.display = ''
            } else {
                control_button[0].style.display = 'none'
                control_button[1].style.display = ''
            }
        },
        controlEdit(){

            // if()
            this.editActiveIndex = 1;


            let all_button = $('.editIcon')
            let control_button = $('.controlEditButton')

            if(this.controlEditMark) {
                for(let i = 0;i<all_button.length;++i){
                    all_button[i].style.display = ''        //置为空就可以显示了
                }

                control_button[0].style.backgroundColor = '#E6A23C'     // 改变control_button样式
                control_button[0].style.borderColor = '#E6A23C'
                control_button[0].children[0].innerHTML = 'Disable editing'
                this.controlEditMark = false
            }else {
                for(let i = 0;i<all_button.length;++i){
                    all_button[i].style.display = 'none'
                }

                control_button[0].style.backgroundColor = '#409EFF'
                control_button[0].style.borderColor = '#409EFF'
                control_button[0].children[0].innerHTML = 'Enable editing'
                this.controlEditMark = true
            }


        },
        // 由oid获取条目详细数据
        find_oid(oid, num) {

            switch (num) {
                //1为type是modelitem
                case 1: {
                    let data = {
                        oid: oid,
                    };
                    $.ajax({
                        url: "/theme/getModelItem",
                        type: "get",
                        data: data,
                        async: false,
                        success: (json) => {
                            if (json.code == 0) {
                                let data = json.data;

                                this.tabledata_get = data;
                            }
                            else {
                                console.log("query error!")
                            }
                        }
                    })
                    break;
                }
                case 2: {
                    let data = {
                        oid: oid,
                    };
                    $.ajax({
                        url: "/theme/getDataItem",
                        type: "get",
                        data: data,
                        async: false,
                        success: (json) => {
                            if (json.code == 0) {
                                let data = json.data;

                                this.tabledata_get = data;
                            }
                            else {
                                console.log("query error!")
                            }
                        }
                    })
                    break;
                }
                case 3:{
                    let data = {
                        oid: oid,
                    };
                    $.ajax({
                        url: "/theme/getDataMethod",
                        type: "get",
                        data: data,
                        async: false,
                        success: (json) => {
                            if (json.code == 0) {
                                let data = json.data;

                                this.tabledata_get = data;
                            }
                            else {
                                console.log("query error!")
                            }
                        }
                    })
                    break;
                }
                default: {

                }
            }

        },
        send_message() {
            this.dialogVisible = false;
            alert("Send Message Success!")
        },
        handleClose(done) {
            this.$confirm('Confirm closing？')
                .then(_ => {
                    done();
                })
                .catch(_ => {
                });
        },
        handleCurrentChange(data, checked, indeterminate) {
            this.setUrl("/modelItem/repository?category=" + data.oid);
            this.pageOption.searchResult = [];
            this.pageOption.total = 0;
            this.pageOption.paginationShow = false;
            this.currentClass = data.label;
            let classes = [];
            classes.push(data.oid);
            this.classifications1 = classes;
            //this.getChildren(data.children)
            this.pageOption.currentPage = 1;
            this.searchText = "";
            this.getModels();
        },
        handleCheckChange(data, checked, indeterminate) {
            this.pageOption.searchResult = [];
            this.pageOption.paginationShow = false;
            let checkedNodes = this.$refs.tree2.getCheckedNodes()
            let classes = [];
            for (let i = 0; i < checkedNodes.length; i++) {
                classes.push(checkedNodes[i].oid);
            }
            this.classifications2 = classes;
            console.log(this.classifications2)
            this.pageOption.currentPage = 1;
            this.getModels();
        },
        editThemePre() {
            let len = $(".editThemeStep").length;
            if (this.editThemeActive != 0)
                this.editThemeActive--;
        },


        uploadImg(){

            $("#imgFile").click();
            $("#imgFile").change(function () {
                //获取input file的files文件数组;
                //$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
                //这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
                var file = $('#imgFile').get(0).files[0];
                //创建用来读取此文件的对象
                var reader = new FileReader();
                //使用该对象读取file文件
                reader.readAsDataURL(file);
                //读取文件成功后执行的方法函数
                reader.onload = function (e) {
                    //读取成功后返回的一个参数e，整个的一个进度事件
                    //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                    //的base64编码格式的地址
                    $('#imgShow').get(0).src = e.target.result;
                    $('#imgShow').show();
                }
            });
            //标识img已存在
            this.imgLog = 1;
        },
        uploadApplicationImg(log){
            let that = this;
            $('#imgFileApplication'+log).click();
            $('#imgFileApplication'+log).change(function () {
                //获取input file的files文件数组;
                //$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
                //这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
                var file = $('#imgFileApplication'+log).get(0).files[0];
                //创建用来读取此文件的对象
                var reader = new FileReader();
                //使用该对象读取file文件
                reader.readAsDataURL(file);
                //读取文件成功后执行的方法函数
                reader.onload = function (e) {
                    //读取成功后返回的一个参数e，整个的一个进度事件
                    //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                    //的base64编码格式的地址

                    // $('#imgShowApplication'+log).get(0).src = e.target.result;
                    that.themeApplicationData[log].upload_application_image = e.target.result;
                    that.themeApplicationData[log].imageTag = 2;


                    // $('#imgShowApplication'+log+log).hide();

                    $('#imgShowApplication'+log).show();
                    console.log(that.themeApplicationData)


                }
            });
        },
    },
    mounted() {
        let that = this;


        //设置leftContainer高度
        function resizeHeight() {
            let fixHeight = $(window).height() - 90
            $("#mainContainer").height(fixHeight)
            $("#leftContainer").height(fixHeight)
        }


        this.$nextTick(function () {
            setTimeout(()=>{
                resizeHeight()

            },100)

        })


        $(document).on('keyup', '.category_name', function ($event) {
            let category_input = $(".category_name");
            // let tab_id=$(".")
            let index = 0;
            for (; index < category_input.length; index++) {
                if ($(this)[0] == category_input.eq(index)[0]) {
                    break;
                }
            }
            that.themeObj.classinfo[index].mcname = $("#categorynameModel" + index).val();
            that.editableTabs_model[index].title = $(".category_name").eq(index).val();
        });
        $(document).on('keyup', '.category_name2', function ($event) {
            let category_input = $(".category_name2");
            let index = 0;
            for (; index < category_input.length; index++) {
                if ($(this)[0] == category_input.eq(index)[0]) {
                    break;
                }
            }
            that.themeObj.dataClassInfo[index].dcname = $("#categorynameData" + index).val();
            that.editableTabs_data[index].title = $(".category_name2").eq(index).val();
        });
        $(document).on('keyup', '.application_name', function ($event) {
            let name_input = $(".application_name");
            let index = 0;
            for (; index < name_input.length; index++) {
                if ($(this)[0] == name_input.eq(index)[0]) {
                    break;
                }
            }
            that.themeObj.application[index].applicationname = $("#applicationName" + index).val();
            that.editableTabs_applications[index].title = $(".application_name").eq(index).val();
        });

        $(document).on('keyup', '.application_link', function ($event) {
            let link_input = $(".application_link");
            let index = 0;
            for (; index < link_input.length; index++) {
                if ($(this)[0] == link_input.eq(index)[0]) {
                    break;
                }
            }
            that.themeObj.application[index].applicationlink = $("#applicationLink" + index).val();
        });
        //页面加载前先执行获取数据函数
        $(document).ready(function () {
            that.relateType = "modelItem";
            that.tableData = [];
            that.pageOption1.currentPage = 0;
            that.pageOption1.searchResult = [];
            that.relateSearch = "";
            that.getRelation();
            that.search1();

            that.relateType = "dataItem";
            that.tableData = [];
            that.pageOption2.currentPage = 0;
            that.pageOption2.searchResult = [];
            that.relateSearch = "";
            that.getRelation();
            that.search2();

            //method,application
            that.relateType = "dataMethod";
            that.tableData = [];
            that.pageOption3.currentPage = 0;
            that.pageOption3.searchResult = [];
            that.relateSearch = "";
            that.getRelation();
            that.search3();

            let winWidth = $(window).width();
            if (winWidth<750){
                that.isCollapse = true;
                $(".themeInfoImge").show();
            }else {
                that.isCollapse = false;
                $(".themeInfoImge").hide();
            }
        });
        //拿到当前页面的themeoid
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
                let href = window.location.href;
                let hrefs = href.split('/');
                that.themeoid = hrefs[hrefs.length - 1].split("#")[0];
            }
        });
        $(window).resize(function () {

            let winWidth = $(window).width();
            if (winWidth<750){
                that.isCollapse = true;
                $(".themeInfoImge").show();
            }else {
                that.isCollapse = false;
                $(".themeInfoImge").hide();
            }
            //设置leftContainer高度
            let fixHeight = $(window).height() - 90
            $("#leftContainer").height(fixHeight)
            $("#mainContainer").height(fixHeight)
            console.log($("#mainContainer"))

        });




        console.log(getMaintainerApi(this.themeoid))
        $.ajax({
            type:"GET",
            url:getMaintainerApi(this.themeoid),
            success:(data) =>{
                that.maintainer = data;
            }

        })
        this.initData();
    },
});




function initTinymce(idStr,callBack){
    tinymce.remove(idStr)
    tinymce.init({
        selector: idStr,
        height: 350,
        plugins: [
            "advcode advlist autolink codesample image imagetools ",
            " lists link media noneditable powerpaste preview",
            " searchreplace table visualblocks wordcount"
        ],
        toolbar:
            "undo redo | fontselect | fontsizeselect | bold italic underline | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image",
        file_picker_types: 'image',
        file_picker_callback: function (cb, value, meta) {
            var input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.onchange = function () {
                var file = input.files[0];

                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function () {
                    var img = reader.result.toString();
                    cb(img, {title: file.name});
                }
            };
            input.click();
        },
        images_dataimg_filter: function (img) {
            return img.hasAttribute('internal-blob');
        }
    });
}




