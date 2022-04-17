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
                    console.log("commentList:",res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        deleteCommentById(commentId){
            axios.post("/comment/delete",{
                id:commentId
            })
                .then(res=> {
                    console.log(res)
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }

})