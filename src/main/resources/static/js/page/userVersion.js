var userVersion = Vue.extend({
    template: "#userVersion",
    props: ["htmlJson"],
    data:function (){
        return{
            ScreenMinHeight: "0px",
            ScreenMaxHeight: "0px",
            name:"wang",

            versionTableData:[],
            currentPage:1,
            totalCount:0,
            pageSize:10,

            activeMyName:0,
            activeOthersName:3,
            activeName:0,

            versionUrl:""
        }
    },
    mounted(){
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
        });
        this.getVersionList("/version/user/versionList/edit/uncheck/All")
    },
    watch:{
        "activeName":function(val){
            console.log(val)
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
            // console.log(this.versionUrl)
            this.getVersionList(this.versionUrl)
        }
    },
    methods:{
        handleParentTabChange(val){
            if(val.label==="My Edition"){
                this.activeName="0"
            }else{
                this.activeName="3"
            }
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
                "sortField": "createTime"
            })
                .then(res=> {
                    console.log(url+" :",res.data)
                    this.versionTableData=res.data.data.content
                    this.totalCount=res.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        viewVersionById(versionId){
            axios.get("/version/detail/"+versionId)
                .then(res=> {
                    console.log("version detail:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        acceptVersionById(versionId){
            axios.get("/version/accept/"+versionId)
                .then(res=> {
                    console.log("accept version:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        rejectVersionById(versionId){
            axios.get("/version/reject/"+versionId)
                .then(res=> {
                    console.log("reject version:",res)
                })
                .catch(function (error) {
                    console.log(error);
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