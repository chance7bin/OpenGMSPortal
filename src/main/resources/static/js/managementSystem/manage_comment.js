export var CommentTemplate = Vue.extend({
    template: ` 
        <div>
                <el-table :data="commentTableData" style="width: 100%" height="75vh">
                  <el-table-column  prop="date"   label="日期" width="180">    </el-table-column>
                  <el-table-column  prop="name" label="姓名" width="180">   </el-table-column>
                  <el-table-column  prop="title" label="条目名称" width="180">  </el-table-column>
                  <el-table-column prop="comment" label="评论内容">  </el-table-column>
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
            PageSizeComment:20, //用户列表每页数目
            totalComment:0, //用户总数
            searchInputComment:"", //用户表搜索内容

        }
    },
    mounted(){

    },
    methods: {
        getComment(){

        },
        handleSizeChangeComment(){

        },
        handleCurrentChangeComment(){

        },

    }
})