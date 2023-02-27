var userVersion = Vue.extend({
    template: "#userVersion",
    props:['htmlJson'],
    data:function (){
        return{
            isActive: true,
            ScreenMinHeight: "0px",
            ScreenMaxHeight: "0px",
            name:"wang",

            versionTableData:[],
            currentPage:1,
            totalCount:0,
            pageSize:10,

            activeMyName:0,
            activeTabName:0,
            activeOthersName:3,
            activeName:0,

            versionUrl:"",

            userPageUrl:"https://geomodeling.njnu.edu.cn/profile/", // 门户个人主页
            // htmlJSON:{}
            versionClass:"edit" //edit 或者 review


        }
    },
    mounted(){
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
        });
        this.getVersionList("/version/user/versionList/edit/uncheck/All")
    },
    watch:{
        "activeName":function(val){
            this.resetPageInfo()
            switch(val) {
                case "0":
                    this.versionUrl="/version/user/versionList/edit/uncheck/All"
                    break;
                case "1":
                    this.versionUrl="/version/user/versionList/edit/accepted/All"
                    break;
                case "2":
                    this.versionUrl="/version/user/versionList/edit/rejected/All"
                    break;
                case "3":
                    this.versionUrl="/version/user/versionList/review/uncheck/All"
                    break;
                case "4":
                    this.versionUrl="/version/user/versionList/review/accepted/All"
                    break;
                case "5":
                    this.versionUrl="/version/user/versionList/review/rejected/All"
                    break;
                default:
                    this.versionUrl=""
            }
            this.getVersionList(this.versionUrl)
        }
    },
    methods:{
        tabChange(val){
            this.resetPageInfo()
            let type=""
            if(val.index==="0"){
                type="uncheck"
            }else if(val.index==="1"){
                type="accepted"
            }else {
                type="rejected"
            }
            this.currentPage=1
            this.versionUrl="/version/user/versionList/"+this.versionClass+"/"+type+"/All"
            this.getVersionList(this.versionUrl)
        },

        changeVersionClass(val){
            this.resetPageInfo()
            console.log(val)
            this.isActive=!this.isActive
            this.versionClass=val
            this.activeTabName="0"
            this.versionUrl="/version/user/versionList/"+this.versionClass+"/uncheck/All"
            this.getVersionList(this.versionUrl)
        },

        resetPageInfo(){
            this.currentPage=1
            this.totalCount=0
            this.pageSize=10
            this.versionTableData=[]
        },

        getVersionList(url){
            axios.post(url,{
                "asc": false,
                "page": this.currentPage,
                "pageSize": this.pageSize,
                "searchText": "",
                "sortField": "submitTime"
            })
                .then(res=> {
                    // console.log(url+" :",res.data)
                    this.versionTableData=res.data.data.content
                    this.totalCount=res.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        viewVersionById(type,versionId){
            let versionViewUrl="/version/versionCompare/"+versionId
            window.open(versionViewUrl, '_blank')
        },
        acceptVersionById(versionId){
            this.$confirm(this.htmlJson.acceptTip, this.htmlJson.Tip, {
                confirmButtonText: this.htmlJson.Confirm,
                cancelButtonText: this.htmlJson.Cancel,
                type: 'warning'
            }).then(() => {
                axios.get("/version/accept/"+versionId)
                    .then(res=> {
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.Success
                        });
                        this.getVersionList(this.versionUrl)
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: this.htmlJson.notOperated
                });
            });

        },
        rejectVersionById(versionId){
            this.$confirm(this.htmlJson.rejectTip, this.htmlJson.Tip, {
                confirmButtonText: this.htmlJson.Confirm,
                cancelButtonText: this.htmlJson.Cancel,
                type: 'warning'
            }).then(() => {
                axios.get("/version/reject/"+versionId)
                    .then(res=> {
                        this.$message({
                            type: 'success',
                            message: this.htmlJson.Success
                        });
                        this.getVersionList(this.versionUrl)
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: this.htmlJson.notOperated
                });
            });

        },

        handleSizeChange(val){
            this.pageSize=val;
            this.getVersionList(this.versionUrl)
        },
        handleCurrentChange(val) {
            this.currentPage=val;
            this.getVersionList(this.versionUrl)
        }
    }

})