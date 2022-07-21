var createDataItem = Vue.extend({
    template: "#createDataItem",
    props:['htmlJson'],
    data() {
        return {

            defaultActive: '2-1',
            curIndex: 3,

            ScreenMaxHeight: "0px",
            IframeHeight: "0px",
            editorUrl: "",
            load: false,


            ScreenMinHeight: "0px",

            userId: "",
            userName: "",
            loginFlag: false,
            activeIndex: 2,

            userInfo: {
                //username:"",
                name: "",
                email: "",
                phone: "",
                insName: ""
            },

            treetrans: [],
            classif: [],
            active: 0,
            categoryTree: [],
            ctegorys: [],

            data_img: [],
            itemInfo: {
                image: '',
            },

            dataItemAddDTO: {
                id:"",
                name: '',
                status: 'Public',
                description: '',
                detail: '',
                author: '',
                url: '',
                dataType:"Url",//默认Url
                keywords: [],
                classifications: [],
                displays: [],
                uploadImage:"",
                authorships: [],
                meta: {
                    coordinateSystem: '',
                    geographicProjection: '',
                    coordinateUnits: '',
                    boundingRectangle: []
                },
                viewCount:0,


            },

            treeData: [{
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

            defaultProps: {
                children: 'children',
                label: 'label'
            },
            cls: [],//分类的id队列
            clsStr: '',//分类的label队列

            selectedFile:[],
            userDataList:[],
            authorDataList:[],
            dialogVisible: false,
            fileSelect:"",
            id:"",
            imageExist:false,
        }
    },

    methods: {
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

        // handleSelect(index,indexPath){
        //     this.setSession("index",index);
        //     window.location.href="/user/userSpace"
        // },
        handleCheckChange(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree2.getCheckedNodes()
            let classes = [];
            let str='';
            for (let i = 0; i < checkedNodes.length; i++) {
                // console.log(checkedNodes[i].children)
                if(checkedNodes[i].children!=undefined){
                    continue;
                }

                classes.push(checkedNodes[i].id);
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


        getDataItems() {
            this.pageSize = 10;
            this.isInSearch = 0;
            var da = {
                userOid: this.userId,
                page: this.page,
                pagesize: this.pageSize,
                asc: -1
            }

            this.loading = true
            var that = this;
            //todo 从后台拿到用户创建的data—item
            axios.get("/user/getDataItems", {
                params: da
            }).then(res => {

                this.searchResult = res.data.data.content
                this.resourceLoad = false;
                this.totalNum = res.data.data.totalElements;
                if (this.page == 1) {
                    this.pageInit();
                }
                this.data_show = true
                this.loading = false

            })


        },

        //add data item
        createDataItem() {
            for (i=0;i<this.selectedFile.length;i++){
                let fileMetaUser = {
                    id:"",
                    name:"",
                    suffix:"",
                    url:""
                };
                fileMetaUser.id = this.selectedFile[i].uid;
                fileMetaUser.name = this.selectedFile[i].name;
                // fileMetaUser.label = this.selectedFile[i].label;
                fileMetaUser.suffix = this.selectedFile[i].suffix;
                fileMetaUser.url = this.selectedFile[i].address;
                this.userDataList.push(fileMetaUser);
            }

            this.dataItemAddDTO.name = $("#dataname").val();

            this.dataItemAddDTO.overview = $("#description").val();
            // this.dataItemAddDTO.detail=$("#detail").val();
            var detail = tinyMCE.activeEditor.getContent();
            this.dataItemAddDTO.detail = detail;
            //todo 获取作者信息
            // this.dataItemAddDTO.author=$("#author").val();
            this.dataItemAddDTO.keywords = $("#keywords").tagEditor('getTags')[0].tags;

            this.dataItemAddDTO.classifications = this.cls;
            this.dataItemAddDTO.displays = this.data_img;
            this.dataItemAddDTO.uploadImage = this.itemInfo.image;
            // let subStr = $('#imgShow').get(0).src.toString().substring(0,9);
            // console.log(subStr);
            // if(subStr === "data:imag"){
            //     this.imageExist = true;
            // }
            // // if ()
            // if (this.imageExist!=false) {
            //     this.dataItemAddDTO.uploadImage = $('#imgShow').get(0).src;
            // }else {
            //     this.dataItemAddDTO.uploadImage = "";
            // }

            this.dataItemAddDTO.url = $("#ResourcesUrlText").val();


            //用户名
            // this.dataItemAddDTO.author=this.userId;
            this.dataItemAddDTO.author = this.userId;

            this.dataItemAddDTO.meta.coordinateSystem = $("#coordinateSystem").val();
            this.dataItemAddDTO.meta.geographicProjection = $("#geographicProjection").val();
            this.dataItemAddDTO.meta.coordinateUnits = $("#coordinateUnits").val();

            this.dataItemAddDTO.meta.boundingRectangle = [];
            // this.dataItemAddDTO.dataType = this.dataType;


            var authorships = [];
            var author_lenth = $(".user-attr").length;
            for (var i = 0; i < author_lenth; i++) {

                let authorInfo = {
                    name: '',
                    email: '',
                    homepage: ''
                }
                console.log($(".user-attr input"))
                let t = 3 * i
                authorInfo.name = $(".user-attr input")[t].value
                authorInfo.email = $(".user-attr input")[1 + t].value
                authorInfo.homepage = $(".user-attr input")[2 + t].value
                authorships.push(authorInfo)

            }
            this.dataItemAddDTO.authorships = authorships;
            this.dataItemAddDTO.dataList = this.userDataList;


            var thedata = this.dataItemAddDTO;

            var that = this;
             if ((this.id === "0") || (this.id === "") || (this.id == null)) {
                axios.post("/dataItem/", thedata)
                    .then(res => {
                        let result = res.data;
                        if (result.code === 0) {
                            //创建静态页面
                            // axios.get("/dataItem/adddataitembyuser", {
                            //     params: {
                            //         id: res.data.data.id
                            //     }
                            // }).then(() => {
                            //
                            // });
                            // $(".prev").click();
                            // $(".prev").click();
                            //清空
                            $("#classification").val('')
                            $("#dataname").val('');
                            $("#description").val('');
                            $("#keywords").tagEditor('removeAll');

                            $("#displays").val('');
                            $("#dataresoureurl").val("")
                            $("#coordinateSystem").val("");
                            $("#geographicProjection").val("")
                            $("#coordinateUnits").val("")
                            $("#upperleftx").val("")
                            $("#upperlefty").val("")
                            $("#bottomrightx").val("")
                            $("#bottomrighty").val("");
                            $("#imgFile").val("");
                            // var categoryAddDTO = {
                            //     id: res.data.data.id,
                            //     cate: that.cls,
                            //     dataType: that.dataItemAddDTO.dataType
                            // };
                            // axios.post('/dataItem/addcate', categoryAddDTO).then(res => {
                            //     // console.log(res)
                            // });
                            //每次创建完条目后清空category内容
                            that.ctegorys = [];
                            //清空displays内容
                            that.data_img = [];
                            this.$confirm('<div style=\'font-size: 18px\'>'+ this.htmlJson.CreateDataItemSuccessfully +'</div>', this.htmlJson.Tip, {
                                dangerouslyUseHTMLString: true,
                                confirmButtonText: this.htmlJson.confirmButtonText,
                                cancelButtonText: this.htmlJson.cancelButtonText,
                                cancelButtonClass: 'fontsize-15',
                                confirmButtonClass: 'fontsize-15',
                                type: 'success',
                                center: true,
                                showClose: false,
                            }).then(() => {
                                window.location.href = "/dataItem/" + result.data.id;
                            }).catch(() => {
                                window.location.href = "/user/userSpace#/data/dataitem";
                            });
                        }else{
                            this.$alert(this.htmlJson.CreatedFailed, 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                        }
                    })
                }else {
                    this.dataItemAddDTO.dataItemId = this.id;
                    var thedata1 = this.dataItemAddDTO;
                    axios.put("/dataItem/" + this.id,thedata1)
                        .then(result=>{
                            if (result.status ===200){
                                if (result.data.code === 0) {
                                    if(result.data.data.method==="update") {
                                        this.$message({
                                            message: this.htmlJson.UpdateSuccess,
                                            type: 'success'
                                        });
                                        // alert(this.htmlJson.UpdateSuccess);
                                        $("#editModal", parent.document).remove();
                                        window.location.href = "/dataItem/" + result.data.data.id;
                                    }
                                    else{

                                        this.$alert(this.htmlJson.ChangesHaveBeenSubmittedPleaseWaitForTheAuthorToReview, 'Success', {
                                            type:"success",
                                            confirmButtonText: 'OK',
                                            callback: action => {
                                                window.location.href = "/user/userSpace";
                                            }
                                        });
                                        //产生信号调用计数，启用websocket

                                    }
                                }
                                else if(result.data.code==-2){
                                    alert(this.htmlJson.LoginInFirst);
                                    window.location.href="/user/login";
                                }
                                else{
                                    alert(result.data.msg);
                                }
                            }
                        })
                }
        },

        next() {

        },
        change(currentIndex, newIndex, stepDirection) {
            console.log(currentIndex, newIndex, stepDirection)
        },

        sendcurIndexToParent(){
            this.$emit('com-sendcurindex',this.curIndex)
        },

        sendUserToParent(userId){
            this.$emit('com-senduserinfo',userId)
        },
        handleClose(done) {
            this.$confirm('Confirm to close?')
                .then(_ => {
                    done();
                })
                .catch(_ => {
                });
        },
        resClick(e){
            let path=e.path;
            for(i=0;i<path.length;i++){
                let obj=path[i];
                if(obj.className.indexOf("dataitemisol")!=-1){
                    $(".dataitemisol").css("border","1px solid #ebeef5");
                    this.fileSelect=obj.align;
                    obj.style.border='2px solid #60b0e8';
                    break;
                }
            }
        },

        selectDataspaceFile(file){
            if (this.selectedFile.indexOf(file) > -1) {
                for (var i = 0; i < this.selectedFile.length; i++) {
                    if (this.selectedFile[i] === file) {
                        //删除
                        this.selectedFile.splice(i, 1);
                        // this.downloadDataSetName.splice(i, 1)
                        break
                    }
                }
            } else {
                this.selectedFile.push(file);
            }
        },

        removeFile(){
            if(this.fileSelect!="") {
                $(".dataitemisol").css("border","1px solid #ebeef5");
                var file = this.selectedFile[this.fileSelect];
                this.selectDataspaceFile(file);

                this.$refs.userDataSpace.cancelSelect(file);
                // this.selectedFile.splice(Number(this.fileSelect), 1);
                this.fileSelect = "";
            }
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
                this.itemInfo.image = e.target.result
            }
        },
        deleteImg(){
            let obj = document.getElementById('imgOne')
            obj.value = ''
            this.$set(this.itemInfo,'image' , '')
            console.log(this.itemInfo.image)
        },
        openDataSpace(){
            this.dialogVisible = true;
            this.$nextTick(()=>{
                this.$refs.userDataSpace.getFilePackage();
            })
        },

        classificationShow(){
            var tha = this;
            tha.id = this.$route.params.editId;

            this.classif = [];
            $("#classification").val('');

            axios.get("/dataItem/categoryTree")
                .then(res => {
                    tha.tObj = res.data.data;
                    tree = [];
                    for (let i in Object.values(tha.tObj)){
                        let grandpa = Object.values(tha.tObj)[i];
                        // let grandpa = tha.tObj[i];
                        for (let j in Object.values(Object.values(grandpa))){
                            let father = Object.values(grandpa)[j];
                            let gChildren=[];
                            for (let k in Object.values((Object.values(father)))){
                                let son = Object.values(Object.values((father)[k]));
                                let sons = son[0];
                                let children = [];
                                for (let ii=0;ii<sons.length;ii++){
                                    let child = {
                                        label : Object.keys(sons[ii])[0],
                                        id:Object.values(sons[ii])[0]
                                    }

                                    if (treetrans[child.label] != undefined){
                                        child.label = treetrans[child.label];
                                    }

                                    // console.log('child.label',child.label)

                                    if (child.label!="all"){
                                        children.push(child);
                                    }
                                }
                                var s = {
                                    label:Object.keys(father[k])[0],
                                    children:children,
                                }

                                if (treetrans[s.label] != undefined){
                                    s.label = treetrans[s.label];
                                }

                                // console.log('s.label',s.label)

                                if(s.label!="all"){
                                    gChildren.push(s)
                                }
                            }

                            let g = {
                                label:Object.keys(grandpa)[0],
                                children:gChildren,
                            }

                            if (treetrans[g.label] != undefined){
                                g.label = treetrans[g.label];
                            }

                            // console.log('g.label',g.label)


                            ;
                            tree.push(g);
                        }
                    }
                    // console.log("treeData:",tree);
                    tha.treeData = tree;
                })
        },

        // 设置缓存
        setStorage(key, value){
            var v = value;
            //是对象转成JSON，不是直接作为值存入内存
            if (typeof v == 'object') {
                v = JSON.stringify(v);
                v = 'obj-' + v;
            } else {
                v = 'str-' + v;
            }
            var localStorage = window.localStorage;
            if (localStorage ) {
                localStorage .setItem(key, v);
            }
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
        }
    },

    created(){
        //首先到缓存中获取userSpaceAll
        this.htmlJson = this.getStorage("userSpaceAll");
        console.log("this.htmlJson:",this.htmlJson);
        if (this.htmlJson.Home == undefined) {
            //如果缓存中有userSpaceAll页面就不会报错
            if (this.getStorage("userSpaceAll") == null){

                //如果没有的话那就定时获取userSpaceAll，并放到缓存中
                var st = setTimeout(() => {
                    console.log("get userSpaceAll...");
                    this.setStorage("userSpaceAll", this.htmlJson);
                    if (this.getStorage("userSpaceAll") != null){
                        //一旦userSpaceAll获取到了，定时销毁并且刷新页面
                        // this.htmlJsonExist = true;
                        window.location.reload();
                        // clearTimeout(st);
                    }

                },100)
            }
        }
    },

    watch:{
        //     // 中英文切换
        htmlJson:{
            handler:function (val) {
                treetrans = val.tree7
                this.classificationShow()
                if (this.editType == 'create'){
                    $("#subRteTitle").text("/" + val.CreateDataItem1);
                } else {
                    $("#subRteTitle").text("/" + val.ModifyDataItem1);
                }
                }
            },
            immediate:true
    },

    mounted() {
        //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
        this.sendcurIndexToParent();

        treetrans = this.htmlJson.tree7
        this.classificationShow();

        var that = this;

        $(".step2").steps({

            onFinish: function () {
                alert(this.htmlJson.complete);
            },
            onChange: (currentIndex, newIndex, stepDirection) => {

                if (currentIndex === 0) {
                    if (stepDirection === "forward") {
                        if ($("#dataname").val().length == 0 || that.clsStr.length == 0) {
                            new Vue().$message({
                                message: this.htmlJson.CompleteDataInformation,
                                type: 'warning',
                                offset: 70,
                            });
                            return false;
                        } else {
                            return true;
                        }

                    }
                }
                if (currentIndex === 1) {
                    return true;
                }
                if (currentIndex === 2) {
                    if (stepDirection === "forward") {
                        if ($("#description").val().length == 0) {
                            new Vue().$message({
                                message: this.htmlJson.CompleteDataDescription,
                                type: 'warning',
                                offset: 70,
                            });
                            return false;
                        } else {


                            return true;
                        }

                    }
                }

            }
        });

        $(() => {
            let height = document.documentElement.clientHeight;
            this.ScreenMinHeight = (height) + "px";
            this.ScreenMaxHeight = (height) + "px";

            window.onresize = () => {
                console.log('come on ..');
                height = document.documentElement.clientHeight;
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";
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
                    // console.log(data);

                    if (result.code !== 0) {
                        alert(this.htmlJson.LoginInFirst);
                        window.location.href = "/user/login";
                    } else {
                        let data = result.data;
                        this.userId = data.accessId;
                        this.userName = data.name;
                        console.log(this.userId)

                        this.sendUserToParent(this.userId)

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
                    alert(this.htmlJson.LoginInFirst);
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

        var user_num = 0;

        if ((oid === "0") || (oid === "") || (oid === null)|| (oid === undefined)) {

            // $("#title").text("Create Model Item")
            $("#subRteTitle").text("/"+this.htmlJson.CreateDataItem)
            $("#keywords").tagEditor('destory');
            $("#keywords").tagEditor({
                forceLowercase: false,
            });

            $("#contributers").tagEditor('destory');
            $("#contributers").tagEditor({
                forceLowercase: false,
            });

            tinymce.remove("textarea#detail");//先销毁已有tinyMCE实例
            tinymce.init({
                selector: "textarea#detail",
                height: 205,
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

        }
        else {
            // $("#title").text("Modify Model Item")
            $("#subRteTitle").text("/Modify Data Item")

            document.title="Modify Data Item | OpenGMS"
            axios.get('/dataItem/itemInfo/'+oid).then(res=>{
                const resData = res.data
                if(resData.code==-1){
                    alert(this.htmlJson.LoginInFirst);
                    window.location.href = "/user/login";
                }else if(resData.data.noResult!=1){
                    let data = resData.data.result;

                    let classificationId = data.classifications;
                    this.dataItemAddDTO.dataType = data.dataType;
                    $("#ResourcesUrlText").val(data.url);//url内容填充

                    this.selectedFile = data.dataList;

                    this.$refs.tree2.setCheckedKeys(data.classifications);
                    this.clsStr=data.categories;
                    this.cls = data.classifications;
                    this.dataItemAddDTO.viewCount = data.viewCount;
                    this.itemInfo.image = data.image;
                    //清空
                    // $("#classification").val('')
                    $("#dataname").val(data.name);
                    $("#description").val(data.overview);
                    $("#keywords").tagEditor('destory');
                    $("#keywords").tagEditor({
                        initialTags: data.keywords,
                        forceLowercase: false,
                        placeholder: 'Enter keywords ...'
                    });
                    $("#contributers").tagEditor('destory');
                    $("#contributers").tagEditor({
                        initialTags: data.contributers,
                        forceLowercase: false,
                        placeholder: 'Enter keywords ...'
                    });

                    $("#detail").html(data.localizationList[0].description);
                    this.authorDataList = data.userDataList;

                    // $('#imgShow').get(0).src = data.image;
                    // $('#imgShow').show();

                    // if (data.image!=null&&data.image!="") {
                    //     $('#imgShow').attr("src", "/static" + data.image);
                    //     $('#imgShow').show();
                    //     that.imageExist = true;
                    // }else {
                    //     that.imageExist = false;
                    // }
                    // $("#displays").val('');
                    $("#dataresoureurl").val(data.reference);

                    // $("#coordinateSystem").val(data.meta.coordinateSystem);
                    // $("#geographicProjection").val(data.meta.geographicProjection)
                    // $("#coordinateUnits").val(data.meta.coordinateUnits)


                    $("#detail").html(data.detail);
                    //tinymce.remove('textarea#detail');//先销毁已有tinyMCE实例
                    initTinymce("textarea#detail");

                    let authorships = data.authorships;
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
                    $("#email").val("")
                    $("#home_page").val("")
                    $("#upperleftx").val("")
                    $("#upperlefty").val("")
                    $("#bottomrightx").val("")
                    $("#bottomrighty").val("");
                    $("#imgFile").val("");
                }
                }
            )
            // window.sessionStorage.setItem("editModelItem_id", "");
        }


        $("#step").steps({
            onFinish: function () {
                alert(this.htmlJson.WizardCompleted);
            }
        });

        $("#imgChange").click(function () {
            $("#imgFile").click();
        });
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
                data: "Get",
                url: "/modelItem/DOISearch",
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
                    alert(this.htmlJson.Error)
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
                    alert(this.htmlJson.PleaseEnterTitle);
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
                    alert(this.htmlJson.DetailsAreEmpty);
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
        $(document).on("click", ".author_close", function () { $(this).parents(".panel").eq(0).remove(); });


        //作者添加

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

        $(document).on("keyup", ".username", function () {

            if ($(this).val()) {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html($(this).val());
            }
            else {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html("NEW");
            }
        })
        //激活jQuery的icheck插件
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
        $("input:radio[name='ContentType']").on('ifChecked', function(event){

            if($(this).val()=="Resources Url"){
                that.dataType = "Url";
                $("#ResourcesUrl").show();
                $("#Resource").hide();
            }
            else{
                that.dataType = "File";
                $("#ResourcesUrl").hide();
                $("#Resource").show();
            }

        });
    }
})
