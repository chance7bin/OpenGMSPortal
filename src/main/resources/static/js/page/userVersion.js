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
            activeOthersName:3

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
    },
    watch:{
        "activeName":function (val){
            console.log(val)
            this.resetPageInfo()
            let url=""
            switch(val) {
                case 0:
                    url="/version/user/versionList/edit/uncheck/All"
                    break;
                case 1:
                    url="/version/user/versionList/edit/accepted/All"
                    break;
                case 2:
                    url="/version/user/versionList/edit/rejected/All"
                    break;
                case 3:
                    url="/version/user/versionList/review/uncheck/All"
                    break;
                case 4:
                    url="/version/user/versionList/review/accepted/All"
                    break;
                case 5:
                    url="/version/user/versionList/review/rejected/All"
                    break;
                default:
                    url=""
            }

            this.getVersionList(url)
        },
        "activeOthersName":function (val){
            console.log(val)
            this.resetPageInfo()
            let url=""
            switch(val) {
                case 0:
                    url="/version/user/versionList/edit/uncheck/All"
                    break;
                case 1:
                    url="/version/user/versionList/edit/accepted/All"
                    break;
                case 2:
                    url="/version/user/versionList/edit/rejected/All"
                    break;
                case 3:
                    url="/version/user/versionList/review/uncheck/All"
                    break;
                case 4:
                    url="/version/user/versionList/review/accepted/All"
                    break;
                case 5:
                    url="/version/user/versionList/review/rejected/All"
                    break;
                default:
                    url=""
            }

            this.getVersionList(url)
        }
    },
    methods:{
        handleParentTabChange(val){
            console.log(val.label)
            // if(val.label==="My Edition"){
            //     this.activeMyName=0
            //     console.log("aaaaa")
            // }else{
            //     this.activeOthersName=3
            // }
        },

        resetPageInfo(){
            this.currentPage=1
            this.totalCount=0
            this.pageSize=10
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
                    console.log(url+" :",res)
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
                    console.log(res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        rejectVersionById(versionId){
            axios.get("/version/reject/"+versionId)
                .then(res=> {
                    console.log(res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        handleSizeChange(val){
            this.pageSize=val;
        },
        handleCurrentChange(val) {
            this.currentPage=val;
        }
    }

})