new Vue({
    el: "#app",
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: {
        //unitconversion
        lightenContributor:{},
        oid_cvt: window.oidcvt,
        unitname: window.infoname,
        unitdata:{},
        unitPrearr:[],
        ipt1value:"",
        unitSlct:[],
        slct1Value:"",
        slct2Value:"",
        activeIndex: '8-1',
        useroid: "",
        userId:"",
        userImg: "",

        addLocalVisible:false,
        localization:{
            id:"",
            language:"",
            name:"",
            desc:"",
        },
        languageList:[
            { value: 'af', label: 'Afrikaans' },
            { value: 'sq', label: 'Albanian' },
            { value: 'ar', label: 'Arabic' },
            { value: 'hy', label: 'Armenian' },
            { value: 'az', label: 'Azeri' },
            { value: 'eu', label: 'Basque' },
            { value: 'be', label: 'Belarusian' },
            { value: 'bg', label: 'Bulgarian' },
            { value: 'ca', label: 'Catalan' },
            { value: 'zh', label: 'Chinese' },
            { value: 'hr', label: 'Croatian' },
            { value: 'cs', label: 'Czech' },
            { value: 'da', label: 'Danish' },
            { value: 'dv', label: 'Divehi' },
            { value: 'nl', label: 'Dutch' },
            { value: 'en', label: 'English' },
            { value: 'eo', label: 'Esperanto' },
            { value: 'et', label: 'Estonian' },
            { value: 'mk', label: 'FYRO Macedonian' },
            { value: 'fo', label: 'Faroese' },
            { value: 'fa', label: 'Farsi' },
            { value: 'fi', label: 'Finnish' },
            { value: 'fr', label: 'French' },
            { value: 'gl', label: 'Galician' },
            { value: 'ka', label: 'Georgian' },
            { value: 'de', label: 'German' },
            { value: 'el', label: 'Greek' },
            { value: 'gu', label: 'Gujarati' },
            { value: 'he', label: 'Hebrew' },
            { value: 'hi', label: 'Hindi' },
            { value: 'hu', label: 'Hungarian' },
            { value: 'is', label: 'Icelandic' },
            { value: 'id', label: 'Indonesian' },
            { value: 'it', label: 'Italian' },
            { value: 'ja', label: 'Japanese' },
            { value: 'kn', label: 'Kannada' },
            { value: 'kk', label: 'Kazakh' },
            { value: 'kok', label: 'Konkani' },
            { value: 'ko', label: 'Korean' },
            { value: 'ky', label: 'Kyrgyz' },
            { value: 'lv', label: 'Latvian' },
            { value: 'lt', label: 'Lithuanian' },
            { value: 'ms', label: 'Malay' },
            { value: 'mt', label: 'Maltese' },
            { value: 'mi', label: 'Maori' },
            { value: 'mr', label: 'Marathi' },
            { value: 'mn', label: 'Mongolian' },
            { value: 'ns', label: 'Northern Sotho' },
            { value: 'nb', label: 'Norwegian' },
            { value: 'ps', label: 'Pashto' },
            { value: 'pl', label: 'Polish' },
            { value: 'pt', label: 'Portuguese' },
            { value: 'pa', label: 'Punjabi' },
            { value: 'qu', label: 'Quechua' },
            { value: 'ro', label: 'Romanian' },
            { value: 'ru', label: 'Russian' },
            { value: 'se', label: 'Sami' },
            { value: 'sa', label: 'Sanskrit' },
            { value: 'sr', label: 'Serbian' },
            { value: 'sk', label: 'Slovak' },
            { value: 'sl', label: 'Slovenian' },
            { value: 'es', label: 'Spanish' },
            { value: 'sw', label: 'Swahili' },
            { value: 'sv', label: 'Swedish' },
            { value: 'syr', label: 'Syriac' },
            { value: 'tl', label: 'Tagalog' },
            { value: 'ta', label: 'Tamil' },
            { value: 'tt', label: 'Tatar' },
            { value: 'te', label: 'Telugu' },
            { value: 'th', label: 'Thai' },
            { value: 'ts', label: 'Tsonga' },
            { value: 'tn', label: 'Tswana' },
            { value: 'tr', label: 'Turkish' },
            { value: 'uk', label: 'Ukrainian' },
            { value: 'ur', label: 'Urdu' },
            { value: 'uz', label: 'Uzbek' },
            { value: 'vi', label: 'Vietnamese' },
            { value: 'cy', label: 'Welsh' },
            { value: 'xh', label: 'Xhosa' },
            { value: 'zu', label: 'Zulu' },
        ],

        coordianate:{wkname:''},

        loading:false,

        loadSpatialDialog:false,

        transformVisible:false,
        inputCoordinate:{
            geogcs:{
                unit:{
                    key:''
                }
            }
        },
        inputX:'',
        inputY:'',
        inputLong:'',
        inputLat:'',
        outputCoordinate:{
            geogcs:{
                unit:{
                    key:''
                }
            }
        },
        outputX:'',
        outputY:'',
        outputLong:'',
        outputLat:'',

        pageOption: {
            paginationShow:false,
            progressBar: true,
            sortAsc: false,
            currentPage: 1,
            pageSize: 10,

            total: 11,
        },
        pageOption1: {
            paginationShow:false,
            progressBar: true,
            sortAsc: false,
            currentPage: 1,
            pageSize: 5,
            searchText: '',
            total: 264,
            searchResult: [],
        },
        searchText:'',

        isInSearch:0,

        loadStatus:0,

        searchResult:[{
            name:'',
        }],

        batchSpatialInputDialog:false,
        batchSpatialLoading:false,

        batchSpatialOutput:'',
        batchSpatialOutputDialog:false,


        csvFile:[],

        folderTree: [{
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

        addOutputToMyDataVisible:false,

        selectedPath:[],
        selectPathDialog:false,
        relatedDataMethods:[{

        }],
        templateId:'',
        bindNewMethodDialog: false,
        searchResult: [],
    //    Date转换
        PERSIAN_EPOCH : 1948320.5,
        GREGORIAN_EPOCH : 1721425.5,
        ISLAMIC_EPOCH : 1948439.5,
        inCalen:"",
        inYear:"",
        inMonth:"",
        inDay:"",
        outCalen:"",
        outYear:"",
        outMonth:"",
        outDay:"",
        inMonthOptions:[],
        outMonthOptions:[],
        CalendarValue : [
            {
                id : 1,
                label : "Gregorian Calendar"
            },
            {
                id : 2,
                label : "Mayan Calendar"
            },
            {
                id : 3,
                label : "Ethiopian Calendar"
            },
            {
                id : 4,
                label : "Jewish Calendar"
            },
            {
                id : 5,
                label : "Julian Calendar"
            },
            {
                id : 6,
                label : "Buddhist Calendar"
            },
            {
                id : 7,
                label : "Chinese Calendar"
            },
            {
                id : 8,
                label : "Revised Julian Calendar"
            },
            {
                id : 8,
                label : "Coptic Calendar"
            },
            {
                id : 9,
                label : "Hindu Calendar"
            },
            {
                id : 10,
                label : "Islamic Calendar"
            },
            {
                id : 11,
                label : "Roman Calendar"
            },
            {
                id : 12,
                label : "Persian Solar Hijri Calendar"
            }
        ],
        GregorianMonth : [
            {
                id : 1,
                label : "January"
            },
            {
                id : 2,
                label : "February"
            },
            {
                id : 3,
                label : "March"
            },
            {
                id : 4,
                label : "April"
            },
            {
                id : 5,
                label : "May"
            },
            {
                id : 6,
                label : "June"
            },
            {
                id : 7,
                label : "July"
            },
            {
                id : 8,
                label : "August"
            },
            {
                id : 9,
                label : "September"
            },
            {
                id : 10,
                label : "October"
            },
            {
                id : 11,
                label : "November"
            },
            {
                id : 12,
                label : "December"
            },
        ],
        IslamicMonth : [
            {
                id : 1,
                label : "Muharram"
            },
            {
                id : 2,
                label : "Saphar"
            },
            {
                id : 3,
                label : "Rabia Al Awwel"
            },
            {
                id : 4,
                label : "Rabia Al Thani"
            },
            {
                id : 5,
                label : "Jomada Al Awwel"
            },
            {
                id : 6,
                label : "Jomada Al Thani"
            },
            {
                id : 7,
                label : "Rajab"
            },
            {
                id : 8,
                label : "Sha'ban"
            },
            {
                id : 9,
                label : "Ramadan"
            },
            {
                id : 10,
                label : "Shawwal"
            },
            {
                id : 11,
                label : "Dul Qa'dah"
            },
            {
                id : 12,
                label : "Dul Hijjah"
            }
        ],
        PesianMonth : [
            {
                id : 1,
                label : "Farvardin"
            },
            {
                id : 2,
                label : "Ordibehesht"
            },
            {
                id : 3,
                label : "Khordad"
            },
            {
                id : 4,
                label : "Tir"
            },
            {
                id : 5,
                label : "Mordad"
            },
            {
                id : 6,
                label : "Shahrivar"
            },
            {
                id : 7,
                label : "Mehr"
            },
            {
                id : 8,
                label : "Aban"
            },
            {
                id : 9,
                label : "Azar"
            },
            {
                id : 10,
                label : "Dey"
            },
            {
                id : 11,
                label : "Bahman"
            },
            {
                id : 12,
                label : "Esfand"
            },
        ],
    //    Time转换
        TimeSystemOptions : [
            {
                id : 1,
                label : "UTC (Coordinated Universal Time)"
            },
            {
                id : 2,
                label : "Universal Time (UT)"
            },
            {
                id : 3,
                label : "Geocentric Coordinate Time (TCG)"
            },
            {
                id : 4,
                label : "International Atomic Time (TAI)"
            },
            {
                id : 5,
                label : "Barycentric Dynamical Time (TDB)"
            },
            {
                id : 6,
                label : "Terrestrial Time (TT)"
            },
            {
                id : 7,
                label : "Sidereal Time"
            },
            {
                id : 8,
                label : "Barycentric Coordinate Time (TCB)"
            },
        ],
        inTime:"",
        inHour:"1",
        inMin:"1",
        inSec:"1",
        outTime:"",
        outHour:"",
        outMin:"",
        outSec:"",
        intime:"01:01:01",
        outtime:"",

        htmlJSON:{}
    },
    methods: {
        translatePage(jsonContent){
            this.htmlJSON = jsonContent
        },

        select_prebase(value, loc, x, tag) {
            //选择转换前后的单位
            if (tag == 1)
                return this.pre_base(value, loc, x)
            else {
                //alert(loc)
                return this.base_pre(value, loc, x)
            }
        },

        base_pre(value, loc, x) {
            //转换前缀to base unit
            var pre = value.substring(0, loc)
            switch (pre) {
                case "Nano":
                    return x / 1e-9
                case "Micro":
                    return x / 1e-6
                case "Milli":
                    return x / 1e-3
                case "Centi":
                    return x / 1e-2
                case "Deci":
                    return x / 1e-1
                case "Hecto":
                    return x / 1e2
                case "Kilo":
                    return x / 1e3
                case "Mega":
                    return x / 1e6
                case "Yocto":
                    return x / 1e-24
                case "Zepto":
                    return x / 1e-21
                case "Atto":
                    return x / 1e-18
                case "Femto":
                    return x / 1e-15
                case "Pico":
                    return x / 1e-12
                case "Deca":
                    return x / 1e1
                case "Giga":
                    return x / 1e9
                case "Tera":
                    return x / 1e12
                case "Peta":
                    return x / 1e15
                case "Exa":
                    return x / 1e18
                case "Zetta":
                    return x / 1e21
                case "Yotta":
                    return x / 1e24

            }

        },

        pre_base(value, loc, x) {
            //转换unit to 前缀
            var pre = value.substring(0, loc)
            switch (pre) {
                case "Nano":
                    return x * 1e-9
                case "Micro":
                    return x * 1e-6
                case "Milli":
                    return x * 1e-3
                case "Centi":
                    return x * 1e-2
                case "Deci":
                    return x * 1e-1
                case "Hecto":
                    return x * 1e2
                case "Kilo":
                    return x * 1e3
                case "Mega":
                    return x * 1e6
                case "Yocto":
                    return x * 1e-24
                case "Zepto":
                    return x * 1e-21
                case "Atto":
                    return x * 1e-18
                case "Femto":
                    return x * 1e-15
                case "Pico":
                    return x * 1e-12
                case "Deca":
                    return x * 1e1
                case "Giga":
                    return x * 1e9
                case "Tera":
                    return x * 1e12
                case "Peta":
                    return x * 1e15
                case "Exa":
                    return x * 1e18
                case "Zetta":
                    return x * 1e21
                case "Yotta":
                    return x * 1e24
            }
        },

        // 处理带有前缀的单位
        prefunc(value, x, tag) {
            var loc;
            var field;
            for (field in this.unitPrearr) {
                loc = value.search(this.unitPrearr[field]);
                if (loc != -1 && loc != 0) {
                    return this.select_prebase(value, loc, x, tag);
                }
            }
            return x;
        },
        async cvtclick(){

            var slct1_value = this.slct1Value
            var slct2_value = this.slct2Value
            var cvt_value = this.ipt1value

            //用户未选择单位类型
            if (slct1_value == 'select the unit' || slct2_value == 'select the unit')
                this.$alert("Please select the unit you want to convert!");

            else if(""===cvt_value)
                this.$alert("Please input a correct value!");
            else {
                //转换单位类型相同
                if (slct1_value == slct2_value)
                    document.getElementById("ipt2").value = cvt_value
                else {

                    var x = cvt_value
                    var field


                    //处理带有前缀的单位
                    x = this.prefunc(slct1_value, x, 1)
                    //unit to base
                    for (field in this.unitdata.Units) {
                        if (this.unitdata.Units[field].SingularName == slct1_value) {
                            x = eval(this.unitdata.Units[field].FromUnitToBaseFunc)
                            break
                        }
                    }

                    //处理带有前缀的单位
                    x = this.prefunc(slct2_value, x, 2)


                    //base to result
                    for (field in this.unitdata.Units) {
                        if (this.unitdata.Units[field].SingularName == slct2_value) {
                            x = eval(this.unitdata.Units[field].FromBaseToUnitFunc)
                            break
                        }
                    }
                    document.getElementById("ipt2").value = x
                }
            } //else

        },

        getLanguageList(){
            $.get("/static/languageList.json").success((content)=>{
                this.languageList = content;
            })
        },
        submitLocalization(){
            let hrefs = window.location.href.split('/');
            let type = hrefs[hrefs.length - 2];
            let oid = hrefs[hrefs.length-1].substring(0,36);

            this.localization.id=oid;
            if(this.localization.language==""){
                this.$message({
                    message: 'Please select language',
                    type: 'warning'
                });
                return;
            }
            if(this.localization.name==""){
                this.$message({
                    message: 'Please enter name',
                    type: 'warning'
                });
                return;
            }
            if(this.localization.desc==""){
                this.$message({
                    message: 'Please enter description',
                    type: 'warning'
                });
                return;
            }

            // this.localization.language=this.localization.language.label;

            let url;
            switch (type){
                case "concept":
                    url="/repository/addConceptLocalization";
                    break;
                case "spatialReference":
                    url="/repository/addSpatialReferenceLocalization";
                    break;
                case "unit":
                    url="/repository/addUnitLocalization";
                    break;
            }
            $.ajax({
                url:url,
                data:this.localization,
                type:"post",
                success:(result)=> {
                    if(result.data==="ok"){
                        this.$alert("Submitted successfully, please wait for review.", 'Tip', {
                            type: 'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.addLocalVisible = false
                            }
                        });
                    }
                }
            })
        },
        submitComment() {
            if (this.useroid == "" || this.useroid == null || this.useroid == undefined) {
                this.$message({
                    dangerouslyUseHTMLString: true,
                    message: '<strong>Please <a href="/user/login">log in</a> first.</strong>',
                    offset: 40,
                    showClose: true,
                });
            } else if (this.commentText.trim() == "") {
                this.$message({
                    message: 'Comment can not be empty!',
                    offset: 40,
                    showClose: true,
                });
            } else {

                let hrefs = window.location.href.split("/");
                let id = hrefs[hrefs.length - 1].substring(0, 36);
                let typeName = hrefs[hrefs.length - 2];
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
                        if (result.code == -1) {
                            window.location.href = "/user/login"
                        } else if (result.code == 0) {
                            this.commentText = "";
                            this.$message({
                                message: 'Comment submitted successfully!',
                                type: 'success',
                                offset: 40,
                                showClose: true,
                            });
                            // this.getComments();
                        } else {
                            this.$message({
                                message: 'Submit Error!',
                                type: 'error',
                                offset: 40,
                                showClose: true,
                            });
                        }
                    }
                });
            }

        },
        deleteComment(oid) {
            $.ajax({
                url: "/comment/delete",
                async: true,
                type: "POST",


                data: {
                    oid: oid,
                },
                success: (result) => {
                    console.log(result)
                    if (result.code == -1) {
                        window.location.href = "/user/login"
                    } else if (result.code == 0) {
                        this.commentText = "";
                        this.$message({
                            message: 'Comment deleted successfully!',
                            type: 'success',
                            offset: 40,
                            showClose: true,
                        });
                        // this.getComments();
                    } else {
                        this.$message({
                            message: 'Delete Error!',
                            type: 'error',
                            offset: 40,
                            showClose: true,
                        });
                    }
                }
            });
        },
        getComments() {
            let hrefs = window.location.href.split("/");
            let type = hrefs[hrefs.length - 2];
            let oid = hrefs[hrefs.length - 1].substring(0, 36);
            let data = {
                type: type,
                id: oid,
                sort: -1,
            };
            $.get("/comment/commentsByTypeAndId", data, (result) => {
                this.commentList = result.data.commentList;
            })
        },
        replyComment(comment) {
            this.commentParentId = comment.oid;
            this.replyTo = "Reply to " + comment.author.name;
            setTimeout(function () {
                $("#commentTextArea").focus();
            }, 1);
        },
        replySubComment(comment, subComment) {
            this.commentParentId = comment.oid;
            this.replyToUserId = subComment.author.oid;
            // this.commentTextAreaPlaceHolder="Reply to "+subComment.author.name;
            this.replyTo = "Reply to " + subComment.author.name;
            setTimeout(function () {
                $("#commentTextArea").focus();
            }, 1);
        },
        tagClose() {
            this.replyTo = "";
            this.replyToUserId = "";
            this.commentParentId = null;
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
                success: (data) => {
                    if (data.oid === "") {
                        this.$confirm('<div style=\'font-size: 18px\'>This function requires an account, <br/>please login first.</div>', 'Tip', {
                            dangerouslyUseHTMLString: true,
                            confirmButtonText: 'Log In',
                            cancelButtonClass: 'fontsize-15',
                            confirmButtonClass: 'fontsize-15',
                            type: 'info',
                            center: true,
                            showClose: false,
                        }).then(() => {
                            this.setSession("history",window.location.href);
                            window.location.href = "/user/login";
                        }).catch(() => {

                        });
                    }
                    else {
                        let href = window.location.href;
                        let hrefs = href.split('/');
                        let type = hrefs[hrefs.length - 2];
                        let oid = hrefs[hrefs.length - 1].split("#")[0];
                        let url = "", sessionName = "", location = "";

                        switch (type) {
                            case "concept":
                                url = "/repository/getConceptUserOidByOid";
                                sessionName = "editConcept_id";
                                location = "/repository/createConcept";
                                break;
                            case "spatialReference":
                                url = "/repository/getSpatialReferenceUserOidByOid";
                                sessionName = "editSpatial_id";
                                location = "/repository/createSpatialReference";
                                break;
                            case "template":
                                url = "/repository/getTemplateUserOidByOid";
                                sessionName = "editTemplate_id";
                                location = "/repository/createTemplate";
                                break;
                            case "unit":
                                url = "/repository/getUnitUserOidByOid";
                                sessionName = "editUnit_id";
                                location = "/repository/createUnit";
                                break;
                        }
                        var urls = {
                            'concept': '/user/userSpace#/community/manageConcept/' + oid,
                            'spatialReference': '/user/userSpace#/community/manageSpatialReference/' + oid,
                            'template': '/user/userSpace#/community/manageTemplate/' + oid,
                            'unit': '/user/userSpace#/community/manageUnit/' + oid,
                        }

                        window.location.href = urls[type];
                        // $.ajax({
                        //     type: "GET",
                        //     url: url,
                        //     data: {
                        //         oid:oid
                        //     },
                        //     cache: false,
                        //     async: false,
                        //     xhrFields: {
                        //         withCredentials: true
                        //     },
                        //     crossDomain: true,
                        //     success: (json) => {
                        //         // if(json.data==data.oid){
                        //         window.sessionStorage.setItem(sessionName,oid)
                        //         window.location.href=location;
                        //         // }
                        //         // else{
                        //         //     alert("You are not the model item's author, please contact to the author to modify the model item.")
                        //         // }
                        //     }
                        // });
                    }
                }
            })
        },

        getQuateMarkContent(str){//获得引号之间的内容
            if(str.indexOf('"')!=-1){
                let regex = /"([^"]*)"/g;
                let currentResult=regex.exec(str);
                return currentResult
            }
            else return ['"'+str+'"',str]
        },

        getSqBracketContent(str){//获得方括号之间内容
            if(str.indexOf("[")!=-1){
                let regex=/\[([^\]]*)]/g
                let currentResult=regex.exec(str);
                return currentResult
            }
            else return str
        },

        bracketMatch(str,num){//匹配第num个左括号的右括号，返回下标
            const leftArr=[]
            let top=0,i;
            for(i=0;i<str.length;i++){
                if(str[i]==='('||str[i]==='['||str[i]==='{'){
                    leftArr.push(i)
                    top++
                }
                if(str[i]===')'||str[i]==']'||str[i]==='}'){
                    leftArr.pop()
                    if(top==num)
                        break
                    top--
                }

            }
            return i
        },

        //以下是解析wkt各部分的字段
        initCompd(coordianate,wkt){
            let index=wkt.indexOf('COMPD_CS')
            let regex = /"([^"]*)"/g;
            let currentResult=regex.exec(wkt);
            coordianate.coName=currentResult[1]
        },

        initProj(coordianate,wkt){
            let index=wkt.indexOf('PROJCS')
            let str=wkt.substring(index,wkt.length)
            let regex = /"([^"]*)"/g;
            let currentResult=regex.exec(str);
            if (coordianate.compd==0) {
                coordianate.coName=currentResult[1]
            }
            let obj={}
            obj.name=currentResult[1]
            let a = wkt.indexOf('GEOGCS')+6
            let eleStart = this.bracketMatch(wkt.substring(a),1)+a+1
            let eleEnd=wkt.indexOf('VERT_CS')==-1?wkt.length:wkt.indexOf('VERT_CS')
            obj.parameters=this.initEle("PARAMETER",wkt.substring(eleStart,eleEnd))
            obj.unit=this.initEle("UNIT",wkt.substring(eleStart,eleEnd))[0]
            obj.axis=this.initEle("AXIS",wkt.substring(eleStart,eleEnd))
            obj.authority=this.initEle("AUTHORITY",wkt.substring(eleStart,eleEnd))[0]
            return obj
        },

        initGeog(wktStr){
            let obj={}

            let dIndex=wktStr.indexOf('DATUM')
            let pIndex=wktStr.indexOf('PRIMEM')
            let datumEnd=this.bracketMatch(wktStr.substring(dIndex),1)
            let end=this.bracketMatch(wktStr.substring(pIndex),1)
            obj.name=this.getQuateMarkContent(wktStr)[1]
            let eleStart = datumEnd>end?datumEnd:end
            let eleEnd=wktStr.length
            obj.unit=this.initEle("UNIT",wktStr.substring(eleStart,eleEnd))[0]
            obj.axis=this.initEle("AXIS",wktStr.substring(eleStart,eleEnd))
            obj.authority=this.initEle("AUTHORITY",wktStr.substring(eleStart,eleEnd))[0]
            return obj
        },

        initDatum(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.authority=this.initEle("AUTHORITY",wktStr)[0]


            return obj
        },

        initPrime(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.num=wktStr.split(',')[1]
            obj.authority=this.initEle("AUTHORITY",wktStr)[0]
            return obj
        },

        initProjection(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.authority=this.initEle("AUTHORITY",wktStr)[0]
            return obj
        },

        initVert(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.unit=this.initEle("UNIT",wktStr)[0]
            obj.axis=this.initEle("AXIS",wktStr)
            obj.authority=this.initEle("AUTHORITY",wktStr)[0]
            return obj
        },

        initVDatum(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.year=wktStr.split(',')[1]
            obj.authority=this.initEle("AUTHORITY",wktStr.substring(eleStart,eleEnd))[0]

            return obj
        },

        initSpheoid(wktStr){
            let obj={}
            obj.name=this.getQuateMarkContent(wktStr)[1]
            obj.authority=this.initEle("AUTHORITY",wktStr)[0]
            let eles=wktStr.split(',')
            obj.semimajorAxis=eles[1]
            if(parseInt(eles[2])<6000000){
                obj.inversFlat=eles[2]
            }else{
                obj.inversFlat=eles[3]
                obj.semiminorAxis=eles[2]
            }
            return obj
        },

        initEle(eleName,wktStr){
            let i=0,content,kvs,result=[]
            while (i<wktStr.length){
                let index=wktStr.indexOf(eleName,i)
                if(index!=-1){
                    content =this.getSqBracketContent(wktStr.substring(index))[1]
                    kvs=content.split(',')
                    let key=kvs[0]
                    let obj={
                        'key':this.getQuateMarkContent(kvs[0])[1],
                        'value':this.getQuateMarkContent(kvs[1])[1]
                    }
                    result.push(obj)
                    i=index+eleName.length
                }else{
                    break
                }

            }
            return result

        },

        //解析wkt,分别对每个字段节点解析
        wktTransfer(coordinate,wkt){
            let obj={};
            // if(wkt.indexOf('GEOGCS')==-1) return
            // let subStrIndex=this.findFirstCoupe(wkt)
            if(wkt.indexOf('COMPD_CS')!=-1){
                coordinate.compd=1
                this.initCompd(coordinate,wkt)

            }
            else{
                coordinate.compd=0
                // this.initGeog(wkt)
            }
            if(wkt.indexOf('PROJCS')!=-1){
                coordinate.projcs=this.initProj(coordinate,wkt)
            }

            if(wkt.indexOf('GEOGCS')!=-1){
                let start = wkt.indexOf('GEOGCS')+6
                let end = this.bracketMatch(wkt.substring(start),1)
                coordinate.geogcs=this.initGeog(wkt.substring(start+1,end+start))
            }

            if(wkt.indexOf('DATUM')!=-1) {
                let start = wkt.indexOf('DATUM') + 5
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.datum = this.initDatum(wkt.substring(start + 1, end+start))
            }

            if(wkt.indexOf('SPHEROID')!=-1) {
                let start = wkt.indexOf('SPHEROID') + 8
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.spheroid = this.initSpheoid(wkt.substring(start + 1, end+start))
            }

            if(wkt.indexOf('PRIMEM')!=-1) {
                let start = wkt.indexOf('PRIMEM') + 6
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.prime = this.initPrime(wkt.substring(start + 1, end+start))
            }

            if(wkt.indexOf('PROJECTION')!=-1) {
                let start = wkt.indexOf('PROJECTION') + 10
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.projection = this.initProjection(wkt.substring(start + 1, end+start))
            }

            if(wkt.indexOf('VERT_CS')!=-1) {
                let start = wkt.indexOf('VERT_CS') + 7
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.vertcs = this.initGeog(wkt.substring(start + 1, end+start))
            }

            if(wkt.indexOf('VERT_DATUM')!=-1) {
                let start = wkt.indexOf('VERT_DATUM') + 10
                let end = this.bracketMatch(wkt.substring(start), 1)
                coordinate.vDatum = this.initGeog(wkt.substring(start + 1, end+start))
            }

        },

        findFirstCoupe(str){
            let firstL=str.indexOf('[')
            let firstR
            if(firstL!=-1)
                firstR=str.indexOf(']')
            return {firstL:firstL,firstR:fristR}
        },

        loadCSDetails(){
            let href = window.location.href;
            let hrefs = href.split('/');
            let item = hrefs[hrefs.length - 2].split("#")[0];
            let oid = hrefs[hrefs.length - 1].split("#")[0];
            if(item=='spatialReference'){
                axios.get('/spatial/getWKT',{
                        params:{
                            oid:oid,
                        }
                    }

                ).then(res=>{
                    if(res.data.code==0){
                        let data = res.data.data;
                        if(data.wktname!=null||data.wktname!=''){
                            this.coordianate.wkname=data.wktname;
                        }
                        if(data.wkt!=null||data.wkt!=''){
                            this.wktTransfer(this.coordianate,data.wkt);
                            console.log(this.coordianate)
                        }

                    }
                })
            }

        },

        isNum(val){
            var regPos = /^\d+(\.\d+)?$/; //非负浮点数
            var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
            if(regPos.test(val) || regNeg.test(val)) {
                return true;
            } else {
                return false;
            }
        },

        loadSpatialReferenceClick(status) {
            this.loadSpatialDialog = true;
            this.loadStatus=status
            this.pageOption.currentPage = 1;
            this.searchResult = '';
            this.loadSpatialReference();

        },

        handlePageChange(val) {
            this.pageOption.currentPage = val;

            if(this.inSearch==0)
                this.loadSpatialReference();
            else
                this.searchSpatialReference()
        },
        loadSpatialReference(){
            this.inSearch = 0
            this.loading = true;
            axios.get('/spatial/getSpatialReference',{
                params:{
                    asc:0,
                    page:this.pageOption.currentPage-1,
                    size:6,
                }
            }).then(
                (res)=>{
                    if(res.data.code==0){
                        let data = res.data.data;
                        this.searchResult = data.content
                        this.pageOption.total = data.total;
                        setTimeout(()=>{
                            this.loading = false;
                        },100)
                    }else{
                        this.$alert('Please try again','Warning', {
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.loading = false;
                            }
                        })

                    }
                }
            )
        },

        searchSpatialReference(page){
            this.inSearch = 1
            this.loading = true;
            let targetPage = page==undefined?this.pageOption.currentPage:page
            this.pageOption.currentPage=targetPage
            axios.get('/spatial/searchSpatialReference',{
                params:{
                    asc:0,
                    page:targetPage-1,
                    size:6,
                    searchText: this.searchText,
                }
            }).then(
                (res)=>{
                    if(res.data.code==0){
                        let data = res.data.data;
                        this.searchResult = data.content
                        this.pageOption.total = data.total;
                        setTimeout(()=>{
                            this.loading = false;
                        },150)

                    }else{
                        this.$alert('Please try again','Warning', {
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.loading = false;
                            }
                        })

                    }
                }
            )
        },

        loadCoordinate(item){
            this.inputX=this.inputY=this.inputLat=this.inputLong=this.outputX=this.outputY=this.outputLat=this.outputLong=''
            this.loadSpatialDialog=false
            if(this.loadStatus==0){
                this.inputCoordinate= item
                this.inputCoordinate.name = item.name
                this.inputCoordinate.wkt = item.wkt
                this.wktTransfer(this.inputCoordinate,this.inputCoordinate.wkt)
            }else{
                this.outputCoordinate = item
                this.outputCoordinate.name = item.name
                this.outputCoordinate.wkt = item.wkt
                this.wktTransfer(this.outputCoordinate,this.outputCoordinate.wkt)
            }

        },

        //判断坐标系单位
        judgeUnit(coordinate){
            if(coordinate.geogcs==undefined) return 'metre'
            if (coordinate.projcs!=undefined&&(coordinate.projcs.unit.key.toLowerCase().indexOf('metre') != -1||coordinate.projcs.unit.key.toLowerCase().indexOf('m') != -1)) {
                return 'metre'
            } else {
                return 'degree'
            }
            // if(coordinate.geogcs.unit.key.toLowerCase().indexOf('metre')==-1) {
            //     if (coordinate.projcs!=undefined&&coordinate.projcs.unit.key.toLowerCase().indexOf('metre') != -1) {
            //         return 'metre'
            //     } else {
            //         return 'degree'
            //     }
            // }else{
            //     if (coordinate.projcs!=undefined&&coordinate.projcs.unit.key.toLowerCase().indexOf('metre') != -1) {
            //         return 'metre'
            //     } else {
            //         return 'degree'
            //     }
            // }

        },

        async transformClick(){
            if(this.judgeUnit(this.inputCoordinate)==='metre'){
                if(this.inputCoordinate.name==''){
                    this.$alert('Please select the input coordinate system')
                    return
                }
                if(this.inputX==''||this.inputY==''||!this.isNum(this.inputX)||!this.isNum(this.inputY)){
                    this.$alert('Please input valid value')
                    return
                }

            }else{
                if(this.inputCoordinate.name==''){
                    this.$alert('Please select the input coordinate system')
                    return
                }
                if(this.inputLong==''||this.inputLat==''||!this.isNum(this.inputLong)||!this.isNum(this.inputLat)){
                    this.$alert('Please input valid value')
                    return
                }
            }

            if(this.outputCoordinate.name==''){
                this.$alert('Please select the output coordinate system')
                return
            }

            // this.transFormByGDAL().then(res =>{
            //     let gdalResult = res
            //     if(gdalResult == null||gdalResult == undefined||gdalResult.toString() == '0,0,0'){
            //         this.transformXY()
            //     }else {
            //         if(this.judgeUnit(this.outputCoordinate)==='metre') {
            //             this.outputX = gdalResult[0].toFixed(5)
            //             this.outputY = gdalResult[1].toFixed(5)
            //         }else{
            //             this.outputLong = gdalResult[0].toFixed(5)
            //             this.outputLat = gdalResult[1].toFixed(5)
            //         }
            //     }
            // })
            this.transformXY()



        },

        transformXY(){
            let inX = parseFloat(this.judgeUnit(this.inputCoordinate)==='metre'?this.inputX:this.inputLong)
            let inY = parseFloat(this.judgeUnit(this.inputCoordinate)==='metre'?this.inputY:this.inputLat)

            var firstProjection = this.inputCoordinate.wkt;
            var secondProjection = this.outputCoordinate.wkt;

            if(firstProjection.indexOf('GEOGCS')==-1||secondProjection.indexOf('GEOGCS')==-1){
                this.$alert('The selected coordinates are not supported to transform.')
                return
            }



            let result
            try{
                result=proj4(firstProjection,secondProjection,[inX,inY]);
                if(this.judgeUnit(this.outputCoordinate)==='metre') {
                    this.outputX = result[0].toFixed(5)
                    this.outputY = result[1].toFixed(5)
                }else{
                    this.outputLong = result[0].toFixed(5)
                    this.outputLat = result[1].toFixed(5)
                }
            }catch (e) {
                this.$alert('The selected coordinates are not supported to transform.')
            }


            // // let inCoorNum=this.inputCoordinate.substring(this.inputCoordinate.indexOf('.')+1)
            // let inCoorNum=4326
            // // let outCoorNum=this.outputCoordinate.substring(this.outputCoordinate.indexOf('.')+1)
            // let outCoorNum=3587
            //
            // let obj={
            //     inCoordinate:inCoorNum,
            //     inX:inX,
            //     inY:inY,
            //     outCoordinate:outCoorNum,
            // }
            // var vthis=this
            // $.ajax({
            //     url: 'http://epsg.io/trans',
            //     dataType: "jsonp",
            //     data: {
            //         x:inX,
            //         y:inY,
            //         s_srs:inCoorNum,
            //         t_srs:outCoorNum
            //     },
            //     success: (data) => {
            //         if(data!=null){
            //             this.outputX=data.x
            //             this.outputY=data.y
            //
            //         }else{
            //             this.$alert('Connection timed out, please try again')
            //         }
            //     }
            // });
        },

        async transFormByGDAL(){
            let refInfo={
                inputRefName:this.inputCoordinate.name,
                inputRefWkt:this.inputCoordinate.wkt,
                inputRefUnit:this.judgeUnit(this.inputCoordinate),
                inputRefX:this.judgeUnit(this.inputCoordinate)==='metre'?this.inputX:this.inputLong,
                inputRefY:this.judgeUnit(this.inputCoordinate)==='metre'?this.inputY:this.inputLat,
                outputRefName:this.outputCoordinate.name,
                outputRefWkt:this.outputCoordinate.wkt,
                outputRefUnit:this.judgeUnit(this.outputCoordinate),
            }

            let data

            await axios.post('/GDAL/transformSpactialRef',refInfo).then(
                res=>{
                    data = res.data
                }
            )

            return data
        },

        exchangeIO(){
            let a = this.inputCoordinate
            this.inputCoordinate = this.outputCoordinate
            this.outputCoordinate = a

            if(this.judgeUnit(this.inputCoordinate)==='metre'){
                this.inputX=this.outputX
                this.outputX=''

                this.inputY=this.outputY
                this.outputY=''

            }else{
                this.inputLong=this.outputLong
                this.outputLong=''

                this.inputLat=this.outputLat
                this.outputLat=''
            }

            if(this.judgeUnit(this.inputCoordinate)==='metre'){
                if(this.inputX!=''&&this.inputY!=''&&this.isNum(this.inputX)&&this.isNum(this.inputY)){
                    this.transformClick();
                }

            }else{
                if(this.inputLong!=''&&this.inputLat!=''&&this.isNum(this.inputLong)&&this.isNum(this.inputLat)){
                    this.transformClick();
                }
            }
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },

        batchInput(){
            this.batchSpatialInputDialog = true
        },

        uploadRemove(file, fileList) {
            this.csvFile = [];
        },

        uploadChange(file, fileList) {
            this.csvFile.splice(0,1);
            let fileMeta = {
                name:file.name,
                oid:file.uid,
                raw:file.raw,
                type:'localFile'
            }
            this.csvFile.push(fileMeta)
        },

        batchinputTransform(){
            this.batchSpatialInputDialog=false
            this.batchSpatialLoading=true
            let formData = new FormData()

            let spatialInfo = {
                inputRefName:this.inputCoordinate.name,
                inputRefWkt:this.inputCoordinate.wkt,
                outputRefName:this.outputCoordinate.name,
                outputRefWkt:this.outputCoordinate.wkt,
            }
            formData.append('spatialInfo',JSON.stringify(spatialInfo))

            formData.append('csv',this.csvFile[0].raw)

            this.csvFile = [];

            $.ajax(
                {
                    url:'/GDAL/transformSpatialByBatch',
                    type:'post',
                    data:formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true,
                    success:(res)=>{
                        if(res.code != -1){
                            this.batchSpatialOutput = res.data.csv
                            this.batchSpatialOutputDialog = true
                            this.useroid = res.data.user

                        }else{
                            this.$message('Fail to transform.');
                        }
                        this.batchSpatialLoading = false
                    },
                    error:(res)=>{
                        this.batchSpatialLoading = false
                    }
                }
            )
        },

        downloadSpatialOutput(){
            window.open("/static"+this.batchSpatialOutput)
        },

        copySpatialOutput(){
            let input = document.createElement('input');
            input.style.opacity  = 0

            document.body.appendChild(input);
            input.value = "http://geomodeling.njnu.edu.cn/static"+this.batchSpatialOutput;  // 这里表示想要复制的内容
            input.focus();
            input.select();
            if (document.execCommand('copy')) {
                document.execCommand('copy');
                this.$message({message:'The url has been copied to clipboard.', type: 'success'});
            }else{
                 this.$alert('<p>Your brouser does not support copying to clipboard,please copy it manually:<br><strong>'+input.value+'</strong></p>',  'Tip', {
                          type:"warning",
                          dangerouslyUseHTMLString: true,
                          confirmButtonText: 'OK',
                          callback: ()=>{
                              return
                          }
                      }
                  );
            }
            document.body.removeChild(input);
        },

        //task output加入data space中
        addFolderinTree(pageIndex,index){
            let node, data

            data = this.$refs.folderTree2.getCurrentNode();
            if (data == undefined || data == null) alert('Please select a file directory')
            node = this.$refs.folderTree2.getNode(data);

            let folderExited = data.children

            console.log(node);
            let paths=[];
            while(node.key!=undefined&&node.key!=0){
                paths.push(node.key);
                node=node.parent;
            }
            if(paths.length==0) paths.push('0')
            console.log(paths)

            var newChild={id:""}

            this.$prompt(null, 'Enter Folder Name', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                // inputErrorMessage: '邮箱格式不正确'
            }).then(({ value }) => {
                if(folderExited.some((item)=>{
                    return  item.label===value;
                })==true){
                    alert('this name is existing in this path, please input a new one');
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
                            alert("Please login first!")
                            window.sessionStorage.setItem("history", window.location.href);
                            window.location.href = "/user/login"
                        }
                        else {
                            newChild = {id: json.data, label: value, children: [], father: data.id ,package:true,suffix:'',upload:false, url:'',};
                            if (!data.children) {
                                this.$set(data, 'children', []);
                            }
                            data.children.push(newChild);

                            setTimeout(()=>{
                                this.$refs.folderTree2.setCurrentKey(newChild.id)
                            },100)
                        }

                    }

                });


            }).then(()=>{

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: 'Cancel'
                });
            });



        },

        addSpatialOutputToDataSpace(){
            this.addOutputToMyDataVisible = true
            this.selectedPath = [];
            axios.get("/user/getFolder", {})
                .then(res => {
                    let json = res.data;
                    if (json.code == -1) {
                        alert("Please login first!")
                        window.sessionStorage.setItem("history", window.location.href);
                        window.location.href = "/user/login"
                    } else {
                        this.folderTree = res.data.data;
                        this.selectPathDialog = true;
                    //
                    }


                });
        },

        addOutputToDataSpace(dataUrl){
            let data = this.$refs.folderTree2.getCurrentNode();
            let node = this.$refs.folderTree2.getNode(data);

            while (node.key != undefined && node.key != 0) {
                this.selectedPath.unshift(node);
                node = node.parent;
            }
            let allFder = {
                key: '0',
                label: 'All Folder'
            }
            this.selectedPath.unshift(allFder)
            console.log(this.selectedPath)

            this.uploadInPath = 0

            let obj = {
                label: this.outputToMyData.tag,
                suffix: this.outputToMyData.suffix,
                url: dataUrl,
                templateId:this.outputToMyData.url.split('/')[this.outputToMyData.url.split('/').length-1],
            }

            this.addDataToPortalBack(obj)
            this.addOutputToMyDataVisible = false
        },

        generatePath(){
            let data = this.$refs.folderTree2.getCurrentNode();
            let node = this.$refs.folderTree2.getNode(data);

            while (node.key != undefined && node.key != 0) {
                this.selectedPath.unshift(node);
                node = node.parent;
            }
            let allFder = {
                key: '0',
                label: 'All Folder'
            }
            this.selectedPath.unshift(allFder)
            console.log(this.selectedPath)
        },

        addDataToPortalBack(item){//item为undefined,则为用户上传；其他为页面已有数据的上传、修改路径

            var addItem=[]
            if(item instanceof Array) {
                addItem=item;
                // for(let i=0;i<addItem.length;i++)
                //     addItem[i].file_name=this.splitFirst(addItem[i].file_name,'&')[1]
            }
            else{
                let obj={
                    file_name:item.label+'.'+item.suffix,
                    label:item.label,
                    suffix:item.suffix,
                    source_store_id:item.source_store_id,
                    templateId:item.templateId,
                    upload:'false'
                }
                addItem[0]=obj
            }
            let paths=[]
            if(this.uploadInPath==1){
                let i=this.pathShown.length-1;
                while (i>=0) {
                    paths.push(this.pathShown[i].id);
                    i--;
                }
                if(paths.length==0)paths=['0']

            }else{
                if(this.selectedPath.length==0) {
                    alert('Please select a folder')
                    return
                }

                let i=this.selectedPath.length-1;//selectPath中含有all folder这个不存在的文件夹，循环索引有所区别
                while (i>=1) {
                    paths.push(this.selectedPath[i].key);
                    i--;
                }
                if(paths.length==0)paths=['0']
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
                traditional:true,
                contentType: "application/json",
                success: (json) => {
                    if (json.code == -1) {
                        alert("Please login first!")
                        window.sessionStorage.setItem("history", window.location.href);
                        window.location.href = "/user/login"
                    }else{
                        this.$message({
                            message: 'Upload successfully!',
                            type: 'success'
                        });
                    }


                }
            });

            // alert('Upload File successfully!')


        },

        addOutputToDataServer7DataSpace(){
            let file = []
            file.push(this.batchSpatialOutput)
            //先在后台上传到数据容器
            $.ajax({
                type: "POST",
                url: "/repository/addOutputToDataServer",
                data: {
                    files: file,
                    user: this.useroid,
                    template: null,
                },
                async: true,
                traditional:true,
                success:(res)=>{
                    let data = res.data.data
                    let file = {
                        file_name:data.file_name,
                        source_store_id:data.id,
                        templateId:'none',

                    }

                    let index = data.file_name.lastIndexOf(".")
                    file.label = data.file_name.substring(0,index + 1);
                    file.suffix = data.file_name.substring(index+1,data.file_name.length);
                    this.generatePath()//先组合上传的文件路径
                    this.addDataToPortalBack(file)
                    this.addOutputToMyDataVisible = false
                }
                }
            )
        },

        getRelatedDataMethods(){
            let url = window.location.href;
            let index = url.lastIndexOf('/');
            this.templateId = url.slice(index+1, url.length);
            console.log(this.templateId);
            $.ajax({
                url:'/template/getRelatedDataMethods/' + this.templateId,
                type:'GET',
                success: (json)=>{
                    console.log(json.data);
                    if(json.data != null){
                        let arr = [];
                        console.log(typeof json.data);
                        this.relatedDataMethods = json.data;
                    }
                }
            })
        },
        ViewMethod(event){
            let refLink=$(".viewMethod");
            for(let i=0;i<refLink.length;i++){
                if(event.currentTarget===refLink[i]){
                    window.location.href = '/dataMethod/' + this.relatedDataMethods[i].oid;
                    break;
                }
            }
        },
        searchMethod(){
            //获取所有的template
            let pageData = {
                asc: this.pageOption1.sortAsc,
                page: this.pageOption1.currentPage-1,
                pageSize: this.pageOption1.pageSize,
                searchText: this.pageOption1.searchText,
                sortElement: "default",
                // classifications: ["all"],
            };
            let contentType = "application/x-www-form-urlencoded";
            $.ajax({
                type:'POST',
                url:'/template/getMethods',
                data:pageData,
                async: true,
                contentType: contentType,
                success:(json)=>{
                    console.log(json);
                    // that.dataApplication.bindTemplates = result.data;

                    if (json.code === 0) {
                        let data = json.data;
                        console.log(data)

                        this.pageOption1.total = data.totalElements;
                        this.pageOption1.pages = data.totalPages;
                        this.pageOption1.searchResult = data.content;
                        // this.pageOption.users = data.users;
                        this.pageOption1.progressBar = false;
                        this.pageOption1.paginationShow = true;

                    }
                    else {
                        console.log("query error!")
                    }
                }

            })
        },
        openBindMethodDialog(){
            this.pageOption1.currentPage = 1;
            this.pageOption1.sortAsc = false;
            this.bindNewMethodDialog = true;
            this.searchMethod();
        },
        selectMethod(index, info){
            console.log(info);

        },

    //    date转换方法
        CalenChange(inOrout) {
            if (inOrout == 1) {
                switch (this.inCalen) {
                    case "Gregorian Calendar" :
                    case "Julian Calendar" :
                        this.inMonthOptions = this.GregorianMonth
                        break;
                    case "Islamic Calendar" :
                        this.inMonthOptions = this.IslamicMonth
                        break;
                    case "Persian Solar Hijri Calendar" :
                        this.inMonthOptions = this.PesianMonth
                        break;
                    default:
                        break;
                }

            }
            else{
                switch (this.outCalen) {
                    case "Gregorian Calendar" :
                        this.outMonthOptions = this.GregorianMonth
                        break;
                    case "Islamic Calendar" :
                        this.outMonthOptions = this.IslamicMonth
                        break;
                    case "Persian Solar Hijri Calendar" :
                        this.outMonthOptions = this.PesianMonth
                        break;
                    default:
                        break;
                }


            }

        },
        CalendarTransform(){

            //判断输入是否合法
            if(this.inCalen==""){
                this.$message({
                    message: 'Please select the calendar you want to convert',
                    type: 'warning'
                });
            }
            else if(this.inYear==""||typeof (parseInt(this.inYear))!="number")
            {
                this.$message({
                    message: 'Please enter the correct year',
                    type: 'warning'
                });
            }
            else if(this.inMonth==""){
                this.$message({
                    message: 'Please select the month you want to convert',
                    type: 'warning'
                });
            }
            else if(this.inDay==""||typeof (parseInt(this.inDay))!="number")
            {
                this.$message({
                    message: 'Please enter the correct day',
                    type: 'warning'
                });
            }
            else if(this.outCalen==""){
                this.$message({
                    message: 'Please select the calendar you want to convert',
                    type: 'warning'
                });
            }
            else {
                var year, mon, day, hour, min, sec, flag,result;
                year = new Number(this.inYear);
                mon = new Number(this.inMonth);
                day = new Number(this.inDay);
                hour = min = sec = 0;
                flag = true;
                var middle;
                switch (this.inCalen){
                    case "Gregorian Calendar" :
                        middle = this.gregorian_to_jd(year, mon, day)

                        break;
                    case "Islamic Calendar" :
                        middle = this.islamic_to_jd(year,
                            mon,day );
                        break;
                    case "Persian Solar Hijri Calendar" :
                        middle = this.islamic_to_jd(year,mon,day)
                        break;
                    case "Julian Calendar" :
                        middle = this.j_to_jd(year,mon,day)
                        break;
                    default:
                        flag = false;
                        break;
                }
                if(flag){
                    switch (this.outCalen) {
                        case "Gregorian Calendar" :
                            result = this.jd_to_gregorian(middle)
                            break;
                        case "Islamic Calendar" :
                            result = this.jd_to_islamic(middle);
                            break;
                        case "Persian Solar Hijri Calendar" :
                            result = this.jd_to_persian(middle)
                            break;
                        case "Julian Calendar" :
                            result = this.jd_to_j(middle)
                            break;
                        default:
                            flag = false;
                            break;
                    }
                }
                if(flag){
                    this.outYear = result[0];
                    this.outMonth = result[1];
                    this.outDay = result[2];
                }
                else{
                    this.outYear = "";
                    this.outMonth = "";
                    this.outDay = "";
                }

            }



        },
        gregorian_to_jd(year,month,day){
            return (this.GREGORIAN_EPOCH - 1) +
                (365 * (year - 1)) +
                Math.floor((year - 1) / 4) +
                (-Math.floor((year - 1) / 100)) +
                Math.floor((year - 1) / 400) +
                Math.floor((((367 * month) - 362) / 12) +
                    ((month <= 2) ? 0 :
                            (this.leap_gregorian(year) ? -1 : -2)
                    ) +
                    day);
        },
        persian_to_jd(year, month, day) {
            var epbase, epyear;

            epbase = year - ((year >= 0) ? 474 : 473);
            epyear = 474 + this.mod(epbase, 2820);

            return day +
                ((month <= 7) ?
                        ((month - 1) * 31) :
                        (((month - 1) * 30) + 6)
                ) +
                Math.floor(((epyear * 682) - 110) / 2816) +
                (epyear - 1) * 365 +
                Math.floor(epbase / 2820) * 1029983 +
                (this.PERSIAN_EPOCH - 1);
        },
        islamic_to_jd(year, month, day) {
            return (day +
                Math.ceil(29.5 * (month - 1)) +
                (year - 1) * 354 +
                Math.floor((3 + (11 * year)) / 30) +
                this.ISLAMIC_EPOCH) - 1;
        },
        j_to_jd(year,month,day) {
            return parseInt(365.25*(year+4716))+parseInt(30.6001*(month+1)+day-1524.5);
            // return parseInt(365.25*(year+4712))+parseInt(30.61*(month+1))+day-1524.5;

        },
        jd_to_j(jd){
          // var year,month,day,muyd;
          // year = parseInt(jd/365.25)-4712;
          // muyd = jd-parseInt(365.25*(year+4712))-1
          //   if(muyd>=59){
          //       month = (parseInt((muyd + 1 +63)/30.61) - 1);
          //       month = month>12?month-12:month;
          //   }
          //   else{
          //       year = year - 1
          //       muyd = jd - parseInt(365.25*(year+4712)) - 1;
          //       month = (parseInt((muyd + 1 +63)/30.61) - 1)
          //       month = month>12 ? month-12 : month     //得到月，小m后面还有别的用处
          //   }
          //   day = jd - parseInt((year + 4712) * 365.25)  - parseInt(30.61*(month + 1)) + 63 - 1 + 1
            var year,month,day,muyd;
            year = parseInt(jd/365.25)-4716;
            muyd = jd-parseInt(365.25*(year+4716))-1
            if(muyd>=59){
                month = (parseInt((muyd + 1 +63)/30.6001) - 1);
                month = month>12?month-12:month;
            }
            else{
                year = year - 1
                muyd = jd - parseInt(365.25*(year+4716)) - 1;
                month = (parseInt((muyd + 1 +63)/30.6001) - 1)
                month = month>12 ? month-12 : month     //得到月，小m后面还有别的用处
            }
            day = jd - parseInt((year + 4716) * 365.25)  - parseInt(30.6001*(month + 1)) + 63 - 1 + 1
            return new Array(year,month,day);
        },
        jd_to_gregorian(jd) {
            var wjd, depoch, quadricent, dqc, cent, dcent, quad, dquad,
                yindex, dyindex, year, yearday, leapadj;

            wjd = Math.floor(jd - 0.5) + 0.5;
            depoch = wjd - this.GREGORIAN_EPOCH;
            quadricent = Math.floor(depoch / 146097);
            dqc = this.mod(depoch, 146097);
            cent = Math.floor(dqc / 36524);
            dcent = this.mod(dqc, 36524);
            quad = Math.floor(dcent / 1461);
            dquad = this.mod(dcent, 1461);
            yindex = Math.floor(dquad / 365);
            year = (quadricent * 400) + (cent * 100) + (quad * 4) + yindex;
            if (!((cent == 4) || (yindex == 4))) {
                year++;
            }
            yearday = wjd - this.gregorian_to_jd(year, 1, 1);
            leapadj = ((wjd < this.gregorian_to_jd(year, 3, 1)) ? 0
                    :
                    (this.leap_gregorian(year) ? 1 : 2)
            );
            let month = Math.floor((((yearday + leapadj) * 12) + 373) / 367);
            let day = (wjd - this.gregorian_to_jd(year, month, 1)) + 1;

            return new Array(year, month, day);
        },
        jd_to_persian(jd) {
            var year, month, day, depoch, cycle, cyear, ycycle,
                aux1, aux2, yday;


            jd = Math.floor(jd) + 0.5;

            depoch = jd - this.persian_to_jd(475, 1, 1);
            cycle = Math.floor(depoch / 1029983);
            cyear = this.mod(depoch, 1029983);
            if (cyear == 1029982) {
                ycycle = 2820;
            } else {
                aux1 = Math.floor(cyear / 366);
                aux2 = this.mod(cyear, 366);
                ycycle = Math.floor(((2134 * aux1) + (2816 * aux2) + 2815) / 1028522) +
                    aux1 + 1;
            }
            year = ycycle + (2820 * cycle) + 474;
            if (year <= 0) {
                year--;
            }
            yday = (jd - this.persian_to_jd(year, 1, 1)) + 1;
            month = (yday <= 186) ? Math.ceil(yday / 31) : Math.ceil((yday - 6) / 30);
            day = (jd - this.persian_to_jd(year, month, 1)) + 1;
            return new Array(year, month, day);
        },
        jd_to_islamic(jd){
            var year, month, day;

            jd = Math.floor(jd) + 0.5;
            year = Math.floor(((30 * (jd - this.ISLAMIC_EPOCH)) + 10646) / 10631);
            month = Math.min(12,
                Math.ceil((jd - (29 + this.islamic_to_jd(year, 1, 1))) / 29.5) + 1);
            day = (jd - this.islamic_to_jd(year, month, 1)) + 1;
            return new Array(year, month, day);
        },
        leap_gregorian(year){
            return ((year % 4) == 0) &&
                (!(((year % 100) == 0) && ((year % 400) != 0)));
        },
        updateFromGregorian() {
            var j, year, mon, mday, hour, min, sec,
                weekday, julcal, hebcal, islcal, hmindex, utime, isoweek,
                may_countcal, mayhaabcal, maytzolkincal, bahcal, frrcal,
                indcal, isoday, xgregcal;

            year = new Number(this.inYear);
            mon = this.inMonth;
            mday = new Number(this.inDay);
            hour = min = sec = 0;
            // hour = new Number(document.gregorian.hour.value);
            // min = new Number(document.gregorian.min.value);
            // sec = new Number(document.gregorian.sec.value);

            //  Update Julian day


            j = this.gregorian_to_jd(year, mon + 1, mday) +
                ((sec + 60 * (min + 60 * hour)) / 86400.0);


            weekday = this.jwday(j);


            islcal = this.jd_to_islamic(j);
            document.islamic.year.value = islcal[0];
            document.islamic.month.selectedIndex = islcal[1] - 1;
            document.islamic.day.value = islcal[2];
            document.islamic.wday.value = "yawm " + ISLAMIC_WEEKDAYS[weekday];
            document.islamic.leap.value = NormLeap[leap_islamic(islcal[0]) ? 1 : 0];

            //  Update Persian Calendar

            perscal = this.jd_to_persian(j);
            document.persian.year.value = perscal[0];
            document.persian.month.selectedIndex = perscal[1] - 1;
            document.persian.day.value = perscal[2];
            document.persian.wday.value = PERSIAN_WEEKDAYS[weekday];
            document.persian.leap.value = NormLeap[leap_persian(perscal[0]) ? 1 : 0];
        },
        islamic_to_jd(year, month, day) {
            return (day +
                Math.ceil(29.5 * (month - 1)) +
                (year - 1) * 354 +
                Math.floor((3 + (11 * year)) / 30) +
                this.ISLAMIC_EPOCH) - 1;},
        jwday(j)
        {
            return this.mod(Math.floor((j + 1.5)), 7);
        },
        mod(a, b)
        {
            return a - (b * Math.floor(a / b));
        },
        //        Time转换方法
        intimeChange(){
            this.intime = this.createTime(this.inHour,this.inMin,this.inSec);
        },
        TimeTransform(){
            //判断输入是否合法
            if(this.inTime==""){
                this.$message({
                    message: 'Please select the time you want to convert',
                    type: 'warning'
                });
            }
            else if(this.inHour==""||typeof (parseInt(this.inHour))!="number")
            {
                this.$message({
                    message: 'Please enter the correct hour',
                    type: 'warning'
                });
            }
            else if(this.inMin==""){
                this.$message({
                    message: 'Please enter the correct minute',
                    type: 'warning'
                });
            }
            else if(this.inSec==""||typeof (parseInt(this.inSec))!="number")
            {
                this.$message({
                    message: 'Please enter the correct second',
                    type: 'warning'
                });
            }
            else if(this.outTime==""){
                this.$message({
                    message: 'Please select the time you want to convert',
                    type: 'warning'
                });
            }
            else {
                var inSecs = this.time_to_sec(this.createTime(this.inHour,this.inMin,this.inSec));
                var middleSecs,resultSecs;
                var flag=true;
                if(this.inTime!=this.outTime){
                    switch(this.inTime){
                        case "UTC (Coordinated Universal Time)":
                            middleSecs = this.utc_to_tai(inSecs);
                            break;
                        case "Terrestrial Time (TT)":
                            middleSecs = this.tt_to_tai(inSecs);
                            break;
                        case "International Atomic Time (TAI)":
                            middleSecs = inSecs;
                            break;
                        default:
                            flag=!flag;
                            break;
                    }
                    if(flag){
                        switch(this.outTime){
                            case "UTC (Coordinated Universal Time)":
                                resultSecs = this.tai_to_utc(middleSecs);
                                break;
                            case "Terrestrial Time (TT)":
                                resultSecs = this.tai_to_tt(middleSecs);
                                break;
                            case "International Atomic Time (TAI)":
                                resultSecs = middleSecs;
                                break;
                            default:
                                flag=!flag;
                                break;
                        }
                        if(flag){
                            this.updateOutTime(resultSecs);
                        }
                    }
                }
                else{
                    this.updateOutTime(inSecs)
                }
            }
        },
        tt_to_tai(inSecs){
            return  inSecs-32.184;
        },
        utc_to_tai(inSecs){
            return inSecs+37;
        },
        tdb_to_tai(inSecs){

        },
        tai_to_tt(inSecs){
            return inSecs+32.184;
        },
        tai_to_utc(inSecs){
            return inSecs-37;
        },
        updateOutTime(resultSecs){
            var result = this.sec_to_timeArr(resultSecs);
            this.outHour = result[0];
            this.outMin = result[1];
            this.outSec = result[2];
            this.outtime = this.createTime(result[0],result[1],result[2]);
        },
        time_to_sec(time){
                var s = '';
                var hour = time.split(':')[0];
                var min = time.split(':')[1];
                var sec = time.split(':')[2];
                sec = Number(hour*3600) + Number(min*60) + Number(sec);
                return sec;

        },
        sec_to_timeArr(s){
            var t;
            if(s > -1){
                var hour = Math.floor(s/3600);
                var min = Math.floor(s/60) % 60;
                var sec = s % 60;
                t = this.createTimeArr(hour,min,sec);
            }
            return t;

        },
        createTime(hour,minute,second){
            var time;
            if(hour < 10) {
                time = '0'+ hour + ":";
            } else {
                time = hour + ":";
            }
            if(minute < 10){time += "0";}
            time += minute + ":";
            if(second < 10){time += "0";}
            time += second.toFixed(2);
            return time;
        },
        createTimeArr(hour,minute,second){
            return new Array(hour,minute,second);
        }
    },
    mounted() {

        this.lightenContributor = author
        this.$refs.mainContributorAvatar.insertAvatar(this.lightenContributor.avatar)
        this.$refs.mainContributorAvatar1.insertAvatar(this.lightenContributor.avatar)

        let that=this;
        this.getRelatedDataMethods();

        var prearr=new Array();
        if(this.oid_cvt!=null){
            $("#transform").show();
        }
        if(this.oid_cvt!="") {

            $.get("/repository/getUnitConvertInfo/" + this.oid_cvt, function (result) {
                that.unitdata = result;
                adddata(that.unitdata);
            });

            if(this.unitname!=null&&this.unitname.indexOf('_')!=-1)
            {
                this.unitname=this.unitname.replace(/_/,'');
            }
        }

        function adddata(data) {
            var item;
            $.each(data.Units, function (i, field) {
                //加入singularname
                if (typeof (field['Prefixes']) == "object")
                    prearr.push(field.SingularName);

                item={value:field.SingularName,
                          label:field.SingularName}
                that.unitSlct.push(item)

                //加入有前缀的singularname
                if (field['Prefixes']) {
                    $.each(field['Prefixes'], function (tmp, field1) {
                        item={value:field1+field.SingularName,
                            label:field1+field.SingularName};
                        that.unitSlct.push(item)
                    })
                }//if
            })//each
            that.unitPrearr=prearr;
            that.slct1Value=that.unitname
        }

        this.setSession("history", window.location.href);
        $.get("/user/load", {}, (result) => {
            let res=result;

                if (res.oid != '') {
                    this.useroid = res.oid;
                    this.userId = res.userId;
                    this.userImg = res.image;
                }

        });
        // this.getComments();

        $(document).on('mouseover mouseout','.flexRowSpaceBetween',function(e){

            let deleteBtn=$(e.currentTarget).children().eq(1).children(".delete");
            if(deleteBtn.css("display")=="none"){
                deleteBtn.css("display","block");
            }else{
                deleteBtn.css("display","none");
            }

        });

        this.loadCSDetails()

        this.inputCoordinate.name = window.spatialRfInPage.name
        this.inputCoordinate.wkt = window.spatialRfInPage.wkt
        this.wktTransfer(this.inputCoordinate,this.inputCoordinate.wkt)
        this.outputCoordinate.name = window.spatialRfInPage.name
        this.outputCoordinate.wkt = window.spatialRfInPage.wkt
        this.wktTransfer(this.outputCoordinate,this.outputCoordinate.wkt)


    }



});

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

