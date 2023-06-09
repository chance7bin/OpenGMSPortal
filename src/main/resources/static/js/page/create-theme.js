let id = 1000;
var createTheme = Vue.extend({
    template: "#createTheme",
    props: ["htmlJson"],
    data() {
        const themeModelData = [
            {
                id: 1,
                label: 'Default',
                children: [{
                    id: 2,
                    label: 'new model class',
                    children: [],
                    tableData: []
                }],
                tableData:[]
            }
        ];
        const themeData = [
            {
                id: 1,
                label: 'Default',
                children: [{
                    id: 2,
                    label: 'new data class',
                    children: [],
                    tableData: []
                }],
                tableData:[]
            }
        ];
        const themeDataMethodData = [
            {
                id: 1,
                label: 'Default',
                children: [{
                    id: 2,
                    label: 'new data method',
                    children: [],
                    tableData: []
                }],
                tableData:[]
            }
        ];
        return {
            editType:"",
            themeModelData: JSON.parse(JSON.stringify(themeModelData)),
            themeData: JSON.parse(JSON.stringify(themeData)),
            themeDataMethodData: JSON.parse(JSON.stringify(themeDataMethodData)),
            selectedTableData:[],
            selectedModelTableData:[],
            selecteddataMethodTableData: [],
            currentNode:2,
            currentdataMethodNode: 2,
            currentModelNode:2,
            parentNode:false,
            parentdataMethodNode:false,
            parentModelNode:false,
            childNode:true,
            childdataMethodNode:true,
            childModelNode:true,

            // remove_flag:0,
            // idflag:"",
            //mnum用来模型计数
            model_num: 0,
            data_num: 0,
            dataMethod_num:0,
            mnum: 0,
            mcnum: 0,
            dnum: 0,
            dcnum: 0,
            editableTabsValue_model: '1',
            editableTabsValue_data: '1',
            editableTabsValue_dataMethod: '1',
            editableTabsValue_applications: '1',

            tabledataflag: 0,
            tabledataflag1: 0,
            //用作改变title时的计数
            tableflag1: 0,
            tableflag2: 0,
            tableflag3: 0,


            editableTabs_model: [{
                id: "1",
                tabledata: [],
                //tabledata:[],
                title: 'Tab 1',
                name: '1',
                content: '1'
            }],
            editableTabs_data: [{
                id: "1",
                tabledata: [],
                title: 'Tab 1',
                name: '1',
                content: 'Tab 1 content'
            }],
            editableTabs_dataMethod: [{
                id: "1",
                tabledata: [],
                title: 'Tab 1',
                name: '1',
                content: 'Tab 1 content'
            }],
            editableTabs_applications: [{
                id: "1",
                title: 'Tab 1',
                name: '1',
                content: 'Tab 1 content'
            }],
            tabIndex_model: 1,
            tabIndex_data: 1,
            tabIndex_application: 1,
            //定义存储从前端获取的数据，用于与后台进行传输
            themeObj: {
                classinfo: [{
                    id: 2,
                    mcname: "new model class",
                    children:[],
                    modelsoid: [],
                }],
                dataClassInfo: [{
                    id: 2,
                    dcname: 'new data class',
                    children:[],
                    datasoid: [],
                }],
                dataMethodClassInfo:[{
                    id: 2,
                    dmcname : "new data method",
                    children :[],
                    dataMethodsoid:[]
                }],
                application: [{
                    id: "1",
                    applicationname: '',
                    applicationlink: '',
                    upload_application_image: '',
                }],
                themeModelData:'',
                themeData:'',
                themeDataMethodData:''
            },

            themeVersion:{
                modifierClass:[{
                    oid:"",
                    userName:'',
                    name:''
                }],
                subDetails:[{
                    detail:'',
                    // time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    // Class<Check> checkClass;后台处理设置
                }],
                subClassInfos:[{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    mcname:'',
                    models:[{
                        model_oid:'',
                        model_name:'',
                    }]
                    // Class<Check> checkClass;后台处理设置
                }],
                subDataInfos:[{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    dcname:'',
                    data:[{
                        data_oid:'',
                        data_name:'',
                    }]
                    // Class<Check> checkClass;后台处理设置
                }],
                subApplications:[{
                    // modify_time后台设置
                    // status: '',后台确定
                    // formatTime:'',后台设置
                    // Class<Check> checkClass;后台处理设置
                    applicationname:'',
                    applicationlink:'',
                    application_image:'',
                    upload_application_image:''
                }]
            },

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

            defaultActive: '5',
            curIndex: 7,

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

            themeImg:'',
        }
    },
    watch:{
        // 中英文切换
        htmlJson:function(newData){
            if (this.editType == 'create'){
                $("#subRteTitle").text("/" + newData.CreateTheme);
            } else {
                $("#subRteTitle").text("/" + newData.ModifyTheme);
            }

        }
    },
    methods: {
        findFirstChildObj(parent){
            let node
            if(parent.children.length==0){
                node = parent
            }
            else{
                // if(parent.children[0].children.length > 0){
                //     this.findFirstChildObj(parent.children[0])
                // }else{
                //     node = parent.children[0]
                // }
                node = this.findFirstChildObj(parent.children[0])
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
            if(modelClass.children.length ==0 && modelClass.id == this.currentModelNode){
                return modelClass.modelsoid
            }else if(modelClass.children.length > 0){
                for (let n = 0; n <modelClass.children.length; n++) {
                    var flag = this.findModelTableData(modelClass.children[n])
                    if (flag != null) {
                        return flag
                    }
                }
            }else{
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
                    var flag = this.findClass(_class.children[n])
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
        },
        changeModelClassNode(data,node) {
            if(data.children.length === 0){
                this.selectedModelTableData = data.tableData
                this.currentModelNode = data.id
                this.parentModelNode = false
                this.childModelNode = true
            }else{
                this.currentModelNode = this.findFirstChild(data)
                this.parentModelNode = true
                this.childModelNode = false
            }
            console.log(this.themeObj)
            console.log(this.selectedModelTableData)
            console.log(this.editableTabs_model)
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
            console.log(this.themeObj)
            console.log(this.selecteddataMethodTableData)
            console.log(this.editableTabs_dataMethod)
            console.log(this.themeDataMethodData)

        },
        append(data) {
            this.$prompt('Data Class Name', '提示', {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then(({ value }) => {
                const newChild = { id: id++, label: value ,children: [], tableData:[]};
                if (!data.children) {
                    this.$set(data, 'children', []);
                }
                data.children.push(newChild);
                // 找到themeobj中对应的class
                if(data.id == 1){
                    this.themeObj.dataClassInfo.push({
                        id: id-1,
                        dcname:value,
                        children:[],
                        datasoid: [],
                    })
                }else{
                    for (var n = 0; n < this.themeObj.dataClassInfo.length; n++) {
                        var modelClass = this.findClass(this.themeObj.dataClassInfo[n],data.id)
                        if(modelClass != null){
                            modelClass.children.push({
                                id: id-1,
                                dcname:value,
                                children:[],
                                datasoid: [],
                            })
                            break
                        }
                    }
                }

                //更改显示内容
                this.parentNode = true
                this.childNode = false
            }).catch(()=>{

            })
        },
        appendModel(data) {
            this.$prompt('Model Class Name', '提示', {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then(({ value }) => {
                const newChild = { id: id++, label: value ,children: [], tableData:[]};
                if (!data.children) {
                    this.$set(data, 'children', []);
                }
                data.children.push(newChild);
                // 找到themeobj中对应的class
                if(data.id == 1){
                    this.themeObj.classinfo.push({
                        id: id-1,
                        mcname:value,
                        children:[],
                        modelsoid: [],
                    })
                }else{

                    for (var n = 0; n < this.themeObj.classinfo.length; n++) {
                        var modelClass = this.findClass(this.themeObj.classinfo[n],data.id)
                        if(modelClass != null){
                            modelClass.children.push({
                                id: id-1,
                                mcname:value,
                                children:[],
                                modelsoid: [],
                            })
                            break
                        }
                    }
                }

                //更改显示内容
                this.parentModelNode = true
                this.childModelNode = false
            }).catch(()=>{

            })
        },
        appendMethod(data){
            this.$prompt("Data Method Name","提示",{
                confirmButtonText:"Yes",
                cancelButtonTextp:"No"
            }).then(({value}) => {
                const newChild = { id: id++, label: value ,children: [], tableData:[]};
                if (!data.children) {
                    this.$set(data, 'children', []);
                }
                data.children.push(newChild);
                // 找到themeobj中对应的class
                if(data.id == 1){
                    this.themeObj.dataMethodClassInfo.push({
                        id: id-1,
                        dmcname:value,
                        children:[],
                        dataMethodsoid: [],
                    })
                }else{
                    for (var n = 0; n < this.themeObj.dataMethodClassInfo.length; n++) {
                        var dataMethodClass = this.findClass(this.themeObj.dataMethodClassInfo[n],data.id)
                        if(dataMethodClass != null){
                            dataMethodClass.children.push({
                                id: id-1,
                                dmcname:value,
                                children:[],
                                dataMethodsoid: [],
                            })
                            break
                        }
                    }
                }

                //更改显示内容
                this.parentNode = true
                this.childNode = false
            })
        },
        modify(data) {
            this.$prompt('Data Class Name', 'Modify the item', {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then(({ value }) => {
                data.label = value
                // 找到themeobj中对应的class
                for (var n = 0; n < this.themeObj.dataClassInfo.length; n++) {
                    var modelClass = this.findClass(this.themeObj.dataClassInfo[n],data.id)
                    if(modelClass != null){
                        modelClass.dcname=value
                        break
                    }
                }
            }).catch(()=>{

            })
        },
        modifyModel(data) {
            this.$prompt('Model Class Name', 'Modify the item', {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then(({ value }) => {
                data.label = value
                // 找到themeobj中对应的class
                for (var n = 0; n < this.themeObj.classinfo.length; n++) {
                    var modelClass = this.findClass(this.themeObj.classinfo[n],data.id)
                    if(modelClass != null){
                        modelClass.mcname=value
                        break
                    }
                }
            }).catch(()=>{

            })
        },
        modifyMethod(data) {
            this.$prompt('Data Method Class Name', 'Modify the item', {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then(({ value }) => {
                data.label = value
                // 找到themeobj中对应的class
                for (var n = 0; n < this.themeObj.dataMethodClassInfo.length; n++) {
                    var dataMethodClass = this.findClass(this.themeObj.dataMethodClassInfo[n],data.id)
                    if(dataMethodClass != null){
                        dataMethodClass.dmcname=value
                        break
                    }
                }
            }).catch(()=>{

            })
        },
        remove(node, data) {
            this.$confirm(this.htmlJson.AreYouSureToDelete,  {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                type: 'warning',
                center: true
            }).then(() => {

                if(node.data.id=="2"){
                    this.$message({
                        type:'warning',
                        message:this.htmlJson.ThisItemForbiddenToBeDeleted,
                    });
                }
                else{
                    this.$message({
                        type: 'success',
                        message: this.htmlJson.DeleteSuccess
                    });
                    const parent = node.parent;
                    //删除themeobj中的data class
                    // 找到themeobj中对应的parent class
                    for (var n = 0; n < this.themeObj.dataClassInfo.length; n++) {
                        var modelClass = this.findClass(this.themeObj.dataClassInfo[n],parent.data.id)
                        if(modelClass != null){
                            //找到孩子节点
                            var childIndex = modelClass.children.findIndex(d => d.id === data.id);
                            modelClass.children.splice(childIndex, 1);
                            break
                        }
                    }
                    //删除树
                    const children = parent.data.children || parent.data;
                    const index = children.findIndex(d => d.id === data.id);
                    children.splice(index, 1);

                }


            }).catch(()=>{

            })
        },
        removeModel(node, data) {
            this.$confirm('Are you sure to delete this item?',  {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                type: 'warning',
                center: true
            }).then(() => {

                if(node.data.id=="2"){
                    this.$message({
                        type:'warning',
                        message:this.htmlJson.ThisItemForbiddenToBeDeleted,
                    });
                }
                else{
                    this.$message({
                        type: 'success',
                        message: this.htmlJson.DeleteSuccess
                    });
                    const parent = node.parent;
                    //删除themeobj中的model class
                    // 找到themeobj中对应的parent class
                    for (var n = 0; n < this.themeObj.classinfo.length; n++) {
                        var modelClass = this.findClass(this.themeObj.classinfo[n],parent.data.id)
                        if(modelClass != null){
                            //找到孩子节点
                            var childIndex = modelClass.children.findIndex(d => d.id === data.id);
                            modelClass.children.splice(childIndex, 1);
                            console.log(this.themeObj.classinfo)
                            break
                        }
                    }
                    //删除树
                    const children = parent.data.children || parent.data;
                    const index = children.findIndex(d => d.id === data.id);
                    children.splice(index, 1);

                }


            }).catch(()=>{

            })
        },
        removeMethod(node,data) {
            this.$confirm('Are you sure to delete this item?',  {
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                type: 'warning',
                center: true
            }).then(() => {

                if(node.data.id=="2"){
                    this.$message({
                        type:'warning',
                        message:this.htmlJson.ThisItemForbiddenToBeDeleted,
                    });
                }
                else{
                    this.$message({
                        type: 'success',
                        message: this.htmlJson.DeleteSuccess
                    });
                    const parent = node.parent;
                    //删除themeobj中的data method class
                    // 找到themeobj中对应的parent method class
                    for (var n = 0; n < this.themeObj.dataMethodClassInfo.length; n++) {
                        var dataMethodClass = this.findClass(this.themeObj.dataMethodClassInfo[n],parent.data.id)
                        if(dataMethodClass != null){
                            //找到孩子节点
                            var childIndex = dataMethodClass.children.findIndex(d => d.id === data.id);
                            dataMethodClass.children.splice(childIndex, 1);
                            break
                        }
                    }
                    //删除树
                    const children = parent.data.children || parent.data;
                    const index = children.findIndex(d => d.id === data.id);
                    children.splice(index, 1);

                }


            }).catch(()=>{

            })

        },
        // model的两个事件
        addModel(index, row) {

            // 往数组中添加新模型
            var flag = false
            for (var n = 0; n < this.selectedModelTableData.length; n++) {
                if(this.selectedModelTableData[n].oid == row.oid){
                    flag = true
                    break
                }
            }
            if(!flag){
                this.selectedModelTableData.push(row)
                // 找到当前分类的数组
                for (var n = 0; n < this.themeObj.classinfo.length; n++) {
                    var modelsoid = this.findModelTableData(this.themeObj.classinfo[n])
                    if(modelsoid != null){
                        modelsoid.push(row.oid)
                        break
                    }
                }
                // this.themeObj.classinfo[num].modelsoid.push(row.oid);
            }

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
            for (var n = 0; n < this.themeObj.classinfo.length; n++) {
                var modelsoid = this.findModelTableData(this.themeObj.classinfo[n])
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
                for (var n = 0; n < this.themeObj.dataClassInfo.length; n++) {
                    var datasoid = this.findTableData(this.themeObj.dataClassInfo[n])
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
                var datasoid = this.findModelTableData(this.themeObj.dataClassInfo[n])
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
                for (var n = 0; n < this.themeObj.dataMethodClassInfo.length; n++) {
                    var datasoid = this.findDataMethodTableData(this.themeObj.dataMethodClassInfo[n])
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
            for (var n = 0; n < this.themeObj.dataMethodClassInfo.length; n++) {
                var datasoid = this.findDataMethodTableData(this.themeObj.dataMethodClassInfo[n])
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
        // modelClass_add(){
        //     this.mcnum++;
        //     this.tableflag1++;
        //     this.tabledataflag++;
        //     $(".el-tabs__new-tab").eq(0).click();
        // },
        dataClass_add(){
            this.dcnum++;
            this.tableflag2++;
            this.tabledataflag1++;

            $(".el-tabs__new-tab").eq(0).click();
        },
        Application_add(){
            $(".el-tabs__new-tab").eq(1).click();
        },
        handleTabsEdit_model(targetName, action) {
            if (action === 'add') {
                let newTabName = ++this.tabIndex_model + '';
                this.themeObj.classinfo.push({
                    id:newTabName,
                    mcname:"",
                    modelsoid:[],
                });
                // this.idflag = newTabName;
                this.editableTabs_model.push({
                    id:newTabName,
                    tabledata:[],
                    title: 'New Tab',
                    name: newTabName,
                    content: '2'
                });
                this.editableTabsValue_model = newTabName + '';
            }
            if (action === 'remove') {
                let tabs = this.editableTabs_model;
                let activeName = this.editableTabsValue_model;
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }

                this.editableTabsValue_model = activeName;
                this.editableTabs_model = tabs.filter(tab => tab.name !== targetName);
                let num;
                for (i=0;i<this.themeObj.classinfo.length;i++) {
                    if(this.themeObj.classinfo[i].id == targetName){
                        num=i;
                        break;
                    }
                }
                this.themeObj.classinfo.splice(num,1);
            }

        },
        handleTabsEdit_data(targetName, action) {
            if (action === 'add') {
                let newTabName = ++this.tabIndex_data + '';
                // this.idflag = newTabName;
                this.themeObj.dataClassInfo.push({
                    id:newTabName,
                    // id:"",
                    dcname:"",
                    datasoid:[],
                });
                this.editableTabs_data.push({
                    id:newTabName,
                    tabledata:[],
                    title: 'New Tab',
                    name: newTabName,
                    content: 'New Tab content'
                });
                this.editableTabsValue_data = newTabName;
            }
            if (action === 'remove') {
                // this.tab_dele_num_data++;
                let tabs = this.editableTabs_data;
                let activeName = this.editableTabsValue_data;
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }

                this.editableTabsValue_data = activeName;
                this.editableTabs_data = tabs.filter(tab => tab.name !== targetName);

                let num;
                for (i=0;i<this.themeObj.dataClassInfo.length;i++) {
                    if(this.themeObj.dataClassInfo[i].id == targetName){
                        num=i;
                        break;
                    }
                }
                this.themeObj.dataClassInfo.splice(num,1);
            }
        },
        handleTabsEdit_applications(targetName, action) {
            if (action === 'add') {
                let newTabName = ++this.tabIndex_application + '';
                this.themeObj.application.push({
                    id:newTabName,
                    applicationname:'',
                    applicationlink:'',
                    upload_application_image:'',
                })

                this.editableTabs_applications.push({
                    id:newTabName,
                    title: 'New Tab',
                    name: newTabName,
                    content: 'New Tab content'
                });
                this.editableTabsValue_applications = newTabName;
            }
            if (action === 'remove') {
                let tabs = this.editableTabs_applications;
                let activeName = this.editableTabsValue_applications;
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }

                this.editableTabsValue_applications = activeName;
                this.editableTabs_applications = tabs.filter(tab => tab.name !== targetName);



                let num;
                for (i=0;i<this.themeObj.application.length;i++){
                    if(this.themeObj.application[i].id == targetName){
                        num = i;
                        break;
                    }
                }
                this.themeObj.application.splice(num,1);
            }
        },
        handleClose(done) {
            this.$confirm(this.htmlJson.AreYouSureToClose)
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
            this.search3();
        },
        search1() {
            this.relateType = "modelItem";
            if(this.pageOption1.currentPage==0){
                this.pageOption1.currentPage++;
            }
            var data = {
                asc: this.pageOption1.sortAsc,

                page: this.pageOption1.currentPage-1,
                pageSize: this.pageOption1.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
            };
            let url, contentType;

            switch (this.relateType) {
                case "dataItem":
                    url=getItemList();
                    data = {
                        page: this.pageOption1.currentPage+1,
                        pageSize: 5,
                        asc: true,
                        classifications: [],
                        category: '',
                        searchText: this.relateSearch
                    }
                    data=JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "concept":
                    url = getConceptList();
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "spatialReference":
                    url = getSpatialReferenceList()
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "template":
                    url = getTemplateList()
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "unit":
                    url = getUnitList()
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                default:
                    url = "/" + this.relateType + "/list";
                    contentType = "application/x-www-form-urlencoded";
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
                        console.log(data);

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
        search2() {
            this.relateType = "dataItem";
            if(this.pageOption2.currentPage==0){
                this.pageOption2.currentPage++;
            };
            var data = {
                asc: this.pageOption2.sortAsc,
                page: this.pageOption2.currentPage-1,
                pageSize: this.pageOption2.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
                dataType:"all",
                tabType:"all",
            };
            let url, contentType;
            switch (this.relateType) {
                case "dataItem":
                    url=getItemList();
                    data = {
                        page: this.pageOption2.currentPage,
                        pageSize: 5,
                        asc: true,
                        classifications: [],
                        category: '',
                        searchText: this.relateSearch,
                        dataType:"all",
                        tabType:"all",

                    }
                    data=JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "concept":
                    url = this.relateSearch.trim() == "" ? getConceptList() : "/repository/searchConcept";
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "spatialReference":
                    url = this.relateSearch.trim() == "" ? "/repository/getSpatialReferenceList" : "/repository/searchSpatialReference";
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "template":
                    url = this.relateSearch.trim() == "" ? "/repository/getTemplateList" : "/repository/searchTemplate";
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                case "unit":
                    url = this.relateSearch.trim() == "" ? "/repository/getUnitList" : "/repository/searchUnit";
                    data.asc = data.asc == true ? 0 : 1;
                    data = JSON.stringify(data);
                    contentType = "application/json";
                    break;
                default:
                    url = "/" + this.relateType + "/list";
                    contentType = "application/x-www-form-urlencoded";
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
                page: this.pageOption3.currentPage,
                pageSize: this.pageOption3.pageSize,
                searchText: this.relateSearch,
                sortType: "default",
                classifications: ["all"],
                dataType:"all",
            };
            let url, contentType;

            url = getMethodList();
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
                        console.log(data)

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
        handleEdit(index, row) {
            let flag = false;
            let j=0;
            let num;
            // let num;

            //找到当前选定的tab对应的数值与id对应
            for (i=0;i<this.editableTabs_model.length;i++) {
                if(this.editableTabs_model[i].id == this.editableTabsValue_model){
                    num=i;
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
            let j=0;
            let num;
            for (i=0;i<this.editableTabs_data.length;i++) {
                if(this.editableTabs_data[i].id == this.editableTabsValue_data){
                    num=i;
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
                id: oid,
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
        handleSelect(index,indexPath){
            this.setSession("index",index);
            window.location.href="/user/userSpace"
        },
        handleCheckChange(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree2.getCheckedNodes();
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
        changeOpen(n) {
            this.activeIndex = n;
        },
        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },

        sendUserToParent(userId){
            this.$emit('com-senduserinfo',userId)
        },

        deleteImg(){
            let obj = document.getElementById('imgOne')
            obj.value = ''
            this.themeImg = ''
            $('#imgShow').get(0).src = ''
        },

        imgFile() {
            $("#imgOne").click();
        },

        preImg() {
            var file = $('#imgOne').get(0).files[0];
            //创建用来读取此文件的对象
            var reader = new FileReader();
            //使用该对象读取file文件
            reader.readAsDataURL(file);
            //读取文件成功后执行的方法函数
            reader.onload =  (e) => {
                //读取成功后返回的一个参数e，整个的一个进度事件
                //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                //的base64编码格式的地址
                $('#imgShow').get(0).src = e.target.result
                this.themeImg = e.target.result
            }
        },

    },
    mounted() {
        $(() => {
            let height = document.documentElement.clientHeight;
            this.ScreenMinHeight = (height) + "px";
            this.ScreenMaxHeight = (height) + "px";

            window.onresize = () => {
                // console.log('come on ..');
                height = document.documentElement.clientHeight;
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";
            };
        })

        let that = this;

        //页面加载前先执行获取数据函数
        $(document).ready(function () {
            that.relateType = "modelItem";
            that.tableData = [];
            that.pageOption1.currentPage=0;
            that.pageOption1.searchResult = [];
            that.relateSearch = "";
            // that.getRelation();
            that.search1();


            that.relateType = "dataItem";
            that.tableData = [];
            that.pageOption2.currentPage=0;
            that.pageOption2.searchResult = [];
            that.relateSearch = "";
            // that.getRelation();
            that.search2();

            //method,application
            that.relateType = "dataMethod";
            that.tableData = [];
            that.pageOption3.currentPage = 0;
            that.pageOption3.searchResult = [];
            that.relateSearch = "";
            // that.getRelation();
            that.search3();
        });

        //input的监听值变动的事件
        $(document).on('keyup','.category_name',function ($event) {
            let category_input=$(".category_name");
            // let tab_id=$(".")
            let index=0;
            for(;index<category_input.length;index++){
                if($(this)[0]==category_input.eq(index)[0]){
                    break;
                }
            }
            that.themeObj.classinfo[index].mcname = $("#categoryname"+ index).val();
            that.editableTabs_model[index].title = $(".category_name").eq(index).val();
        });
        $(document).on('keyup','.category_name2',function ($event) {
            let category_input=$(".category_name2");
            let index=0;
            for(;index<category_input.length;index++){
                if($(this)[0]==category_input.eq(index)[0]){
                    break;
                }
            }
            that.themeObj.dataClassInfo[index].dcname = $("#categoryname2"+ index).val();
            that.editableTabs_data[index].title = $(".category_name2").eq(index).val();
        });
        $(document).on('keyup','.application_name',function ($event) {
            let name_input=$(".application_name");
            let index=0;
            for(;index<name_input.length;index++){
                if($(this)[0]==name_input.eq(index)[0]){
                    break;
                }
            }
            that.themeObj.application[index].applicationname = $("#applicationname"+ index).val();
            that.editableTabs_applications[index].title = $(".application_name").eq(index).val();
        });

        $(document).on('keyup','.application_link',function ($event) {
            let link_input=$(".application_link");
            let index = 0;
            for (;index<link_input.length;index++){
                if($(this)[0]==link_input.eq(index)[0]){
                    break;
                }
            }
            that.themeObj.application[index].applicationlink = $("#applicationlink"+ index).val();
        })



        // var mcname;


        const heights = $(window).height();
        const minH = heights-60;
        $(".infoPanel").css("min-height",minH+"px");


        window.onresize = () => {
            const height = $(window).height();
            const minH = height-60;
            $(".infoPanel").css("min-height",minH+"px");
        };
        $(".step").steps({

            onFinish: function () {
                alert(this.htmlJson.complete);
            },
            onChange: (currentIndex, newIndex, stepDirection) => {
                switch (newIndex) {
                    case 1 :
                        this.relateType = "modelItem"
                        break;
                    case 2:
                        this.relateType = "dataItem"
                        break;
                    case 3:
                        this.relateType = "dataApplication"
                        break;
                    default:
                        break;
                }

                if (currentIndex === 0 && stepDirection === "forward") {
                    if ($("#nameInput").val().length === 0) {
                        this.$alert('Please Input Theme Name!');
                        return false;
                    } else {
                        return true;
                    }
                }

                // else if (currentIndex === 1 && stepDirection === "forward") {
                //     for (let i = 0; i < this.themeObj.classinfo.length; i++) {
                //         if (this.themeObj.classinfo[i].mcname == "" || (this.findFirstChildObj(this.themeObj.classinfo[i])).modelsoid.length==0) {
                //             this.$alert("Please complete the information");
                //             return false;
                //         }
                //     }
                //     return true;
                // }
                //
                // else if (currentIndex === 2 && stepDirection === "forward") {
                //     for (let i = 0; i < this.themeObj.dataClassInfo.length; i++) {
                //         if (this.themeObj.dataClassInfo[i].dcname == "" || (this.findFirstChildObj(this.themeObj.dataClassInfo[i])).datasoid.length==0) {
                //             that.$alert("Please complete the information");
                //             return false;
                //         }
                //     }
                //     return true
                // }

                else if (currentIndex === 3 && stepDirection === "forward") {
                    return true;
                }
                else return true;


            }
        });
        const url="ModelDataDownloadServlet";
        $("#data-list").on('click','.view',function () {
            const dataID=this.getAttribute("div_id");
            console.log(dataID);
            const form = $("<form>");
            form.attr("style","display:none");
            form.attr("method","post");
            form.attr("action",url);
            $(this).append(form);//将表单放置在web中

            //在表单中添加input标签来传递参数
            //如有多个参数可添加多个input标签
            const input1=$("<input>");
            input1.attr("type","hidden");//设置为隐藏域
            input1.attr("name","dataID");//设置参数名称
            input1.attr("value",dataID);//设置参数值
            form.append(input1);//添加到表单中

            form.submit();//表单提交
            $.ajax({
                type:"get",
                url:"/test",
                dataType:'',
                success:function (data) {
                    console.log(data);
                }
            })
        });

        $(".thumbnail").click(function () {
            const dataID=this.attr("id");
            console.log(dataID);
        });

        $("." + that.classarr[0]).on("click", () => {
            that.initmodel(index,classarr,contentarr,listidarr,listidarrrow);
        });

        $("input[name='Status']").iCheck({
            //checkboxClass: 'icheckbox_square-blue',  // 注意square和blue的对应关系
            radioClass: 'iradio_flat-green',
            increaseArea: '0%' // optional
        });

        $.ajax({
            type: "GET",
            url: "/user/load",
            data: {

            },
            cache: false,
            async: false,
            success: (result) => {
                // console.log(data);
                if (result.code !== 0) {
                    that.$alert(this.htmlJson.LoginInFirst);
                    window.location.href = "/user/login";
                }
                else {
                    let data = result.data;
                    this.userId = data.accessId;
                    this.userName = data.name;
                }
            }
        })

        var oid = this.$route.params.editId;//取得所要edit的id
        var m_attr = 0;
        var d_attr = 0;

        if ((oid === "0") || (oid === "") || (oid === null)|| (oid === undefined)) {
            $("#subRteTitle").text("/"+this.htmlJson.CreateTheme);
            this.editType = "create";
            initTinymce('textarea#themeText');
        }
        else {
            // $("#title").text("Modify Theme")
            $("#subRteTitle").text("/Modify Theme")
            this.editType = "modify"
            // document.title="Modify Theme | OpenGMS"
            $.ajax({
                url: "/theme/info/" + oid,
                type: "get",
                data: {},

                success: (result) => {

                    console.log(result)
                    var basicInfo = result.data;

                    //cls
                    $(".providers").children(".panel").remove();
                    $("#nameInput").val(basicInfo.name);

                    //image
                    if (basicInfo.image != "") {
                        $("#imgShow").attr("src", basicInfo.image);
                        $('#imgShow').show();
                    }
                    //reference

                    for (i = 0; i < basicInfo.references.length; i++) {
                        var ref = basicInfo.references[i];
                        table.row.add([
                            ref.title,
                            ref.authors,
                            ref.date,
                            ref.journal,
                            ref.pageRange,
                            ref.link,
                            "<center><a href='javascript:;' class='fa fa-times refClose' style='color:red'></a></center>"]).draw();
                    }
                    if (basicInfo.references.length > 0) {
                        $("#dynamic-table").css("display", "block")
                    }


                    //detail

                    $("#myText").html(basicInfo.detail);

                    initTinymce('textarea#themeText')

                }
            });
            window.sessionStorage.setItem("edittheme_id", "");
        }

        if (this.editType == 'create'){
            $("#subRteTitle").text("/" + this.htmlJson.CreateTheme);
        } else {
            $("#subRteTitle").text("/" + this.htmlJson.ModifyTheme);
        }

        $("#step").steps({
            onFinish: function () {
                alert('Wizard Completed');
            }
        });


        $('#tagInput').tagEditor({
            forceLowercase: false
        });
        $("#refAuthor").tagEditor({
            forceLowercase: false
        })

        $("#imgChangeApp").click(function () {
            $("#imgFileApp").click();
        });
        $("#imgFileApp").change(function () {
            //获取input file的files文件数组;
            //$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
            //这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
            var file = $('#imgFileApp').get(0).files[0];
            //创建用来读取此文件的对象
            var reader = new FileReader();
            //使用该对象读取file文件
            reader.readAsDataURL(file);
            //读取文件成功后执行的方法函数
            reader.onload = function (e) {
                //读取成功后返回的一个参数e，整个的一个进度事件
                //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                //的base64编码格式的地址
                $('#imgShowApp').get(0).src = e.target.result;
                $('#imgShowApp').show();
            }
        });


        $(document).on('click','.imgChange',function ($event) {
            let num = that.editableTabsValue_applications-1;
            let img_input = $(".img_file");
            let index = 0;
            for (;index<img_input.length;index++){
                if($(this)[0].nextElementSibling==img_input.eq(index)[0]){
                    break;
                }
            }
            $('#imgFileApp'+index).click();
        });


        $(document).on('change','.img_file',function ($event) {
            // $(".img_file").change(function () {
            //匹配id，增加image
            let num;
            for (i=0;i<that.themeObj.application.length;i++){
                if(that.themeObj.application[i].id==that.editableTabsValue_applications){
                    num = i;
                    break;
                }
            }
            //获取input file的files文件数组;
            //$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
            //这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
            let file = $('#imgFileApp'+num).get(0).files[0];
            //创建用来读取此文件的对象
            let reader = new FileReader();
            //使用该对象读取file文件
            reader.readAsDataURL(file);
            //读取文件成功后执行的方法函数
            reader.onload = function (e) {
                //读取成功后返回的一个参数e，整个的一个进度事件
                //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                //的base64编码格式的地址

                $('#imgShowApp'+num).get(0).src = e.target.result;
                that.themeObj.application[num].upload_application_image = e.target.result;
                $('#imgShowApp'+num).show();
            }
            //console.log($('#imgShow1').get(0).currentSrc);
        });
        //table
        table = $('#dynamic-table').DataTable({
            //"aaSorting": [[ 0, "asc" ]],
            "paging": false,
            // "ordering":false,
            "info": false,
            "searching": false
        });
        $("#dynamic-table").css("display", "none")
        //$('#dynamic-table').dataTable().fnAddData(['111','111','111','1111','1111']);
        // $("#addref").click(function(){
        //     $("#refinfo").modal("show");
        // })
        $("#doiSearch").click(function () {
            $("#doi_searchBox").addClass("spinner")
            $.ajax({
                type: "Get",
                url: "/theme/DOISearch",
                data: {
                    doi: $("#doi_searchBox").val()
                },
                cache: false,
                async: true,
                success: (data) => {
                    data=data.data;
                    $("#doi_searchBox").removeClass("spinner")
                    if (data == "ERROR") {
                        alert(data);
                    }
                        // if(!json.doi){
                        //     alert("ERROR")
                    // }
                    else {
                        var json = eval('(' + data + ')');
                        console.log(json)
                        $("#doiTitle").val(json.title)
                        $("#doiAuthor").val(json.author)
                        $("#doiDate").val(json.month + " " + json.year)
                        $("#doiJournal").val(json.journal)
                        $("#doiPages").val(json.pages)
                        $("#doiLink").val(json.adsurl)
                        $("#doiDetails").css("display", "block");

                    }
                },
                error: (data) => {
                    $("#doi_searchBox").removeClass("spinner")
                    alert("ERROR!")
                    $("#doiDetails").css("display", "none");
                    $("#doiTitle").val("")
                }
            })


        });
        $("#modal_cancel").click(function () {
            $("#refTitle").val("")
            var tags = $('#refAuthor').tagEditor('getTags')[0].tags;
            for (i = 0; i < tags.length; i++) { $('#refAuthor').tagEditor('removeTag', tags[i]); }
            $("#refDate").val("")
            $("#refJournal").val("")
            $("#refLink").val("")
            $("#refPages").val("")

            $("#doiDetails").css("display", "none");
            $("#doiTitle").val("")
        })
        $("#modal_save").click(function () {

            if ($(".nav-tabs li").eq(0)[0].className == "active") {
                if ($("#refTitle").val().trim() == "") {
                    alert("Please Enter Title");
                }
                else {
                    table.row.add([
                        $("#refTitle").val(),
                        $("#refAuthor").val(),
                        $("#refDate").val(),
                        $("#refJournal").val(),
                        $("#refPages").val(),
                        $("#refLink").val(), "<center><a href='javascript:;' class='fa fa-times refClose' style='color:red'></a></center>"]).draw();

                    $("#dynamic-table").css("display", "block")
                    $("#refinfo").modal("hide")
                    $("#refTitle").val("")
                    var tags = $('#refAuthor').tagEditor('getTags')[0].tags;
                    for (i = 0; i < tags.length; i++) { $('#refAuthor').tagEditor('removeTag', tags[i]); }
                    $("#refDate").val("")
                    $("#refJournal").val("")
                    $("#refPages").val("")
                    $("#refLink").val("")
                }

            }
            else {
                if ($("#doiTitle").val() == "") {
                    alert("Details are empty");
                }
                else {
                    table.row.add([
                        $("#doiTitle").val(),
                        $("#doiAuthor").val(),
                        $("#doiDate").val(),
                        $("#doiJournal").val(),
                        $("#doiPages").val(),
                        $("#doiLink").val(), "<center><a href='javascript:;' class='fa fa-times refClose' style='color:red'></a></center>"]).draw();
                    $("#dynamic-table").css("display", "block")
                    $("#refinfo").modal("hide")
                    $("#doiDetails").css("display", "none");
                    $("#doiTitle").val("");
                }
            }


        })
        //table end

        $(document).on("click", ".refClose", function () {
            table.row($(this).parents("tr")).remove().draw();
            //$(this).parents("tr").eq(0).remove();
            console.log($("tbody tr"));
            if ($("tbody tr").eq(0)[0].innerText == "No data available in table") {
                $("#dynamic-table").css("display", "none")
            }
        });

        let height = document.documentElement.clientHeight;
        this.ScreenMaxHeight = (height) + "px";
        this.IframeHeight = (height - 20) + "px";

        window.onresize = () => {
            // console.log('come on ..');
            height = document.documentElement.clientHeight;
            this.ScreenMaxHeight = (height) + "px";
            this.IframeHeight = (height - 20) + "px";
        }


        // var themeObj = {};
        // that.themeObj.classinfo = new Array();
        // that.themeObj.dataClassInfo = new Array();
        // that.themeObj.application = new Array();


        $(document).on('click','#selectok',function ($event) {

            //将tabledata中的数据放到二维数组tabledata_two
            $("#selectok").html("That's ok");
            that.confirmflag = 1;
            $("#selectok").attr('id',"selectok_past");
            /*classinfo 类似于reference,将选择的model的oid放到classinfo中*/
            // themeObj.classinfo = new Array();
            // var cla = {};
            // cla.mcname = $("#categoryname").val();
            // cla.modelsoid = that.moid;
            // that.themeObj.classinfo.push(cla);
            // for(var i=0;i<that.tableData.length;i++){
            //     that.editableTabs_model[that.tabledataflag].tabledata[i] = that.tableData[i];
            // }
            // that.editableTabs_model[that.tabledataflag].tabledata.push(that.tableData);
            that.tabledataflag++;
            // that.editableTabs_model[that.tabledataflag++].tabledata.push(that.tableData);//将当前获取的tabledata追加到大数组中

            that.dialogTableVisible=false;

            $("#categoryname").attr('id','categoryname_past');//改变当前id名称
        });
        $(document).on('click','#selectok1',function ($event) {
            that.editableTabs_data[that.tableflag2++].title = $("#categoryname2").val();
            $("#selectok1").html("That's ok");
            that.confirmflag1 = 1;
            $("#selectok1").attr('id',"selectok1_past");
            /*dataclassinfo 类似于reference,将选择的model的oid放到dataclassinfo中*/
            // var dcla = {};
            // dcla.dcname = $("#categoryname2").val();
            // dcla.datasoid = that.doid;
            // that.themeObj.dataClassInfo.push(dcla);

            // that.editableTabs_data[that.tabledataflag1].tabledata.push(that.tableData);
            that.tabledataflag1++;

            that.dialogTableVisible1=false;

            $("#categoryname2").attr('id','categoryname2_past');//改变当前id名称
        });

        $(".finish").click(()=> {
            that.themeObj.themename = $("#nameInput").val();

            if(that.themeObj.themename.toString().trim()==''){
                that.$alert("Please enter a theme name")
                return;
            }

            let loading = this.$loading({
                lock: true,
                text: this.htmlJson.Uploading,
                spinner: "el-icon-loading",
                background: "rgba(0, 0, 0, 0.7)"
            });

            // if(that.themeObj.application.length===1&&that.themeObj.application[0].applicationname===""&&that.themeObj.application[0].applicationlink===""
            //     &&that.themeObj.application[0].upload_application_image===""){
            //     let loading = this.$loading({
            //         lock: true,
            //         text: "Uploading...",
            //         spinner: "el-icon-loading",
            //         background: "rgba(0, 0, 0, 0.7)"
            //     });
            // }
            //查看classinfo与dataClassInfo，如果存在一个也未输入，则删除
            if (that.themeObj.classinfo.length===1&&that.themeObj.classinfo[0].mcname===""&&that.themeObj.classinfo[0].modelsoid.length===0) {
                that.themeObj.classinfo.splice(0,1);
            }
            if (that.themeObj.dataClassInfo.length===1&&that.themeObj.dataClassInfo[0].dcname===""&&that.themeObj.dataClassInfo[0].datasoid.length===0) {
                that.themeObj.dataClassInfo.splice(0,1);
            }
            if(that.themeObj.application.length===1&&that.themeObj.application[0].applicationname===""&&that.themeObj.application[0].applicationlink===""
                &&that.themeObj.application[0].upload_application_image===""){
                that.themeObj.application.splice(0,1);
            }



            that.themeObj.image = $('#imgShow').get(0).src;

            var detail = tinyMCE.activeEditor.getContent();
            that.themeObj.detail = detail.trim();
            that.themeObj.status = "Public";
            that.themeObj.uploadImage = $('#imgShow').get(0).currentSrc;
            that.themeObj.tabledata = that.editableTabs_model;
            that.themeObj.themeData = that.themeData;
            that.themeObj.themeModelData = that.themeModelData;
            that.themeObj.themeDataMethodData = that.themeDataMethodData;
            let formData=new FormData();
            if ((oid === "0") || (oid === "") || (oid == null)) {
                let file = new File([JSON.stringify(that.themeObj)],'ant.txt',{
                    type: 'text/plain',
                });
                formData.append("info",file);

                $.ajax({
                    url: addThemeApi(),
                    type: "POST",
                    processData: false,
                    contentType: false,
                    async: true,
                    data: formData,
                    success: result => {
                        if(typeof(loading) != "undefined"){
                            loading.close();
                        }

                        if (result.code == "0") {
                            console.log("success");
                                that.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.CreateThemeSuccessfully +'</div>', 'Tip', {
                                    dangerouslyUseHTMLString: true,
                                    confirmButtonText: this.htmlJson.confirmButtonText,
                                    cancelButtonText: this.htmlJson.cancelButtonText,
                                    cancelButtonClass: 'fontsize-15',
                                    confirmButtonClass: 'fontsize-15',
                                    type: 'success',
                                    center: true,
                                    showClose: false,
                                }).then(() => {
                                    window.location.href = "/repository/theme/" + result.data;//刷新当前页面
                                }).catch(() => {
                                    window.location.href = "/user/userSpace#/userTheme";
                                });

                            // alert("Create Success");
                            // window.location.href = "/repository/theme/" + result.data;//刷新当前页面
                        }
                        else if(result.code==-1){
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href="/user/login";
                        }
                        else{
                            alert("Create failed!");
                        }
                    }
                })
            } else {

                that.themeObj["oid"] = oid;

                let file = new File([JSON.stringify(that.themeObj)],'ant.txt',{
                    type: 'text/plain',
                });
                formData.append("info",file);
                console.log(formData)
                $.ajax({
                    url: "/theme/" + oid,
                    type: "PUT",
                    processData: false,
                    contentType: false,
                    async: true,
                    data: formData,

                    success: function (result) {
                        loading.close();
                        if (result.code === 0) {
                            if(result.data.method==="update") {
                                this.$message({
                                    message: this.htmlJson.UpdateSuccess,
                                    type: 'success'
                                });
                                // alert(this.htmlJson.UpdateSuccess);
                                $("#editModal", parent.document).remove();
                                window.location.href = "/theme/" + result.data.oid;
                            }
                            else{
                                this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
                                    type:"success",
                                    confirmButtonText: 'OK',
                                    callback: action => {
                                        window.location.href = "/user/userSpace";
                                    }
                                });
                            }
                        }
                        else if(result.code==-2){
                            alert(this.htmlJson.LoginInFirst);
                            window.location.href="/user/login";
                        }
                        else if(result.code==415){
                            alert(this.htmlJson.NoModification);
                        }
                        else{
                            alert(result.msg);
                        }
                    }
                })
            }
        });

        $(document).on("click", ".author_close", function () { $(this).parents(".panel").eq(0).remove(); });

        $(document).on("keyup", ".username", function () {

            if ($(this).val()) {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html($(this).val());
            }
            else {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html("NEW");
            }
        })

    }
})
