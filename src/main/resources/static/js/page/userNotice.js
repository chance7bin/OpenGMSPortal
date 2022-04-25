var userNotice = Vue.extend({
    template: "#userNotice",
    props: ["htmlJson"],
    data:function (){
        return{
            ScreenMinHeight: "0px",
            ScreenMaxHeight: "0px",
            name:"wang",

            noticeTableData:[],
            currentPage:1,
            noticeCount:0,
            pageSize:10,

            unReadNoticeCount:0,

            noticeDialogVisible:false,
            currentMessage:"",

            // htmlJSON:{}
        }
    },
    mounted(){

        //控制宽高
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


        this.getNoticeList()
        this.getNoticeCount()
        this.getUnReadNoticeCount()
    },
    methods:{
        //得到用户的通知列表
        getNoticeList(){
            axios.post("/notice/user/noticeList",{
                "asc": false,
                "page": this.currentPage,
                "pageSize": this.pageSize,
                "searchText": "",
                "sortField": "createTime"
            })
                .then(res=> {
                    console.log("noticeList:",res.data)
                    this.noticeTableData=res.data.data.content

                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        getNoticeCount(){
            axios.get("/notice/user/noticeCount")
                .then(res=> {
                    console.log("noticeCount:",res.data)
                    this.noticeCount=res.data.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        getUnReadNoticeCount(){
            axios.get("/notice/user/unreadNoticeCount")
                .then(res=> {
                    console.log("unreadNoticeCount:",res.data)
                    this.unReadNoticeCount=res.data.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        tableRowClassName({row, rowIndex}) {
            if(row.hasRead===false){
                return 'warning-row';
            }else{
                return ''
            }
        },
        rowClass(){
            return 'border-bottom: 1px solid ;'
        },

        setAllNotice2Read(){
            axios.get("/notice/notice2read")
                .then(res=> {
                    this.getNoticeList()
                    this.getNoticeCount()
                    this.getUnReadNoticeCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        setOneNotice2Read(noticeId){
            axios.get("/notice/notice2read/"+noticeId)
                .then(res=> {
                    this.getNoticeList()
                    this.getNoticeCount()
                    this.getUnReadNoticeCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        viewNotice(row){
            this.noticeDialogVisible=true
            this.currentMessage=row.message
            this.setOneNotice2Read(row.id)
            this.getNoticeList()
            this.getNoticeCount()
            this.getUnReadNoticeCount()
        },
        handleSizeChange(val){
            this.pageSize=val;
            this.getNoticeList()
        },
        handleCurrentChange(val) {
            this.currentPage=val;
            this.getNoticeList()

        }

    }

})