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


            tableData: [{
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1517 弄'
            }, {
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1519 弄'
            }, {
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1516 弄'
            }]

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
                    console.log("noticeList:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        getNoticeCount(){
            axios.get("/notice/user/noticeCount")
                .then(res=> {
                    console.log("noticeCount:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        getUnReadNoticeCount(){
            axios.get("/notice/user/unreadNoticeCount")
                .then(res=> {
                    console.log("unreadNoticeCount:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        setAllNotice2Read(){
            axios.get("/notice/notice2read")
                .then(res=> {
                    console.log(res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        setOneNotice2Read(noticeId){
            axios.get("/notice/notice2read/"+noticeId)
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