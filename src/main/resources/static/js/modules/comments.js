Vue.component("comments",
    {
        template: '#comments',
        props: ["htmlJson"],
        data() {
            return {
                comment_userId: "",
                comment_userImg: "",
                comment_userOid: "",
                //comment
                commentText: "",
                commentParentId:null,
                commentList:[],
                replyToUserEmail:"",
                commentTextAreaPlaceHolder:"Write your comment...",
                replyTo:"",
                // htmlJSON:{},
            }
        },
        watch:{
            htmlJson:function (newval){
                console.log("newval",newval)
            }
        },
        computed: {
        },
        methods: {
            //comment
            loadUser(){
                $.get("/user/load",{},(result)=>{
                    if(result.code == 0) {
                        let data = result.data
                        if (data.email != "") {
                            this.comment_userId = data.email;
                            this.comment_userImg = data.avatar;
                            this.comment_userOid = data.email;
                        }
                    }
                })
            },
            submitComment(){
                if(this.comment_userOid==""||this.comment_userOid==null||this.comment_userOid==undefined){
                    this.$message({
                        dangerouslyUseHTMLString: true,
                        message: '<strong>Please <a href="/user/login">log in</a> first.</strong>',
                        offset: 40,
                        showClose: true,
                    });
                }else if(this.commentText.trim()==""){
                    this.$message({
                        message: this.htmlJson.CommentCanNotBeEmpty,
                        offset: 40,
                        showClose: true,
                    });
                }else {

                    let hrefs = window.location.href.split("/");
                    let id = hrefs[hrefs.length - 1].substring(0, 36);
                    let typeName = hrefs[hrefs.length-2];
                    let data = {
                        parentId: this.commentParentId,
                        content: this.commentText,
                        // authorId: this.comment_userOid,
                        replyToUserEmail: this.replyToUserEmail,
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
                            if(result.code==-1){
                                window.location.href="/user/login"
                            }else if (result.code == 0) {
                                this.commentText = "";
                                this.$message({
                                    message: this.htmlJson.CommentSubmittedSuccessfully,
                                    type: 'success',
                                    offset: 40,
                                    showClose: true,
                                });
                                this.getComments();
                            } else {
                                this.$message({
                                    message: this.htmlJson.SubmitError,
                                    type: 'error',
                                    offset: 40,
                                    showClose: true,
                                });
                            }
                        }
                    });
                }

            },
            deleteComment(id){
                this.$confirm(this.htmlJson.PermanentlyDelete, 'Warning', {
                    confirmButtonText: 'OK',
                    cancelButtonText: 'Cancel',
                    type: 'warning'
                }).then(() => {
                    $.ajax({
                        url: "/comment/delete/" + id,
                        async: true,
                        type: "GET",
                        success: (result) => {
                            console.log(result)
                            if(result.code==-1){
                                window.location.href="/user/login"
                            }else if (result.code == 0) {
                                this.commentText = "";
                                this.$message({
                                    message: this.htmlJson.CommentDeletedSuccessfully,
                                    type: 'success',
                                    offset: 40,
                                    showClose: true,
                                });
                                this.getComments();
                            } else {
                                this.$message({
                                    message: this.htmlJson.DeleteError,
                                    type: 'error',
                                    offset: 40,
                                    showClose: true,
                                });
                            }
                        }
                    });
                }).catch(() => {

                });

            },
            getComments(){
                let hrefs=window.location.href.split("/");
                let type=hrefs[hrefs.length-2];
                let oid=hrefs[hrefs.length-1].substring(0,36);
                let data={
                    type:type,
                    id:oid,
                    sort:-1,
                };
                $.get("/comment/commentsByTypeAndId",data,(result)=>{
                    this.commentList=result.data.commentList;
                    console.log("this.commentList:",this.commentList);
                })
            },
            replyComment(comment){
                this.commentParentId=comment.id;
                this.replyToUserEmail=comment.author.userId;
                this.replyTo=this.htmlJson.ReplyTo+comment.author.name;
                setTimeout(function () { $("#commentTextArea").focus();}, 1);
            },
            replySubComment(comment,subComment){
                this.commentParentId=comment.id;
                this.replyToUserEmail=subComment.author.userId;
                // this.commentTextAreaPlaceHolder="Reply to "+subComment.author.name;
                this.replyTo=this.htmlJson.ReplyTo+subComment.author.name;
                setTimeout(function () { $("#commentTextArea").focus();}, 1);
            },
            tagClose(){
                this.replyTo="";
                this.replyToUserEmail="";
                this.commentParentId=null;
            },
        },
        async created() {
            this.loadUser();
            this.getComments();
        },
        mounted() {}
    }
)