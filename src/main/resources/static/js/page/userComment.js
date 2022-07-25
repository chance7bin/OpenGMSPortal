var userComment = Vue.extend({
    template: "#userComment",
    props: ["htmlJson"],
    data:function (){
        return{
            ScreenMinHeight: "0px",
            ScreenMaxHeight: "0px",
            name:"wang",

            commentTableData:[],


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

        this.getCommentList()
    },
    methods:{

        getCommentList(){
            axios.get("/comment/getCommentsByUser")
                .then(res=> {
                    this.commentTableData=res.data.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


        deleteCommentById(commentId){

            this.$confirm('删除评论, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.get("/comment/delete/"+commentId)
                    .then(res=> {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getCommentList()
                    })
                    .catch(function (error) {
                        console.log(error);
                    });

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });

        }
    }

})