export var CommentTemplate = Vue.extend({
    template: ` 
        <div>
       
                <el-table :data="commentTableData" style="width: 100%" height="75vh">
                  <el-table-column  prop="createTime"   label="日期" show-overflow-tooltip min-width="150px">    </el-table-column>
                  <el-table-column  prop="commentEmail" label="评论者邮箱" show-overflow-tooltip min-width="130px">   </el-table-column>
                  <el-table-column  prop="itemName" label="条目名称" show-overflow-tooltip min-width="200px">  </el-table-column>
                  <el-table-column prop="content" label="评论内容" show-overflow-tooltip min-width="300px">  </el-table-column>
                   <el-table-column  label="操作" min-width="100px">
                        <template slot-scope="scope">
                            <el-button type="danger"  size="mini " @click="deleteCommentById(scope.row.id)"> 删除 </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                 <el-pagination
                        background
                        @size-change="handleSizeChangeComment"
                        @current-change="handleCurrentChangeComment"
                        :current-page="currentPageComment"
                        :page-sizes="[10, 20, 30, 40]"
                        :page-size="PageSizeComment"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="totalComment"
                        class="flexRowCenter"
                >
                </el-pagination>
               
        </div>

        `,
    data() {
        return {
            commentTableData:[], //用户列表数据
            currentPageComment:1, //用户列表表格页
            PageSizeComment:10, //用户列表每页数目
            totalComment:0, //用户总数
            searchInputComment:"", //用户表搜索内容

        }
    },
    mounted(){
        this.getComment()
    },
    methods: {
        getComment(){
            axios.post('/managementSystem/commentList', {
                "asc": false,
                "page": this.currentPageComment,
                "pageSize": this.PageSizeComment,
                "searchText": this.searchInputComment,
                "sortField": "createTime"
            })
                .then(response=> {
                    this.commentTableData=response.data.data.content
                    this.totalComment=response.data.data.total
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        handleSizeChangeComment(val){
            this.PageSizeComment=val
            this.getComment()
        },
        handleCurrentChangeComment(val){
            this.currentPageComment=val
            this.getComment()
        },
        deleteCommentById(commentId){
            this.$confirm('删除该评论, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.get('/managementSystem/comment/delete/'+commentId)
                    .then(response=> {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getComment()
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