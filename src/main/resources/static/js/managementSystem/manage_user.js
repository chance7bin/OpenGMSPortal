export var UserTemplate = Vue.extend({
    template: ` 
        <div >
                <!--<h2>测试</h2>-->
                <div class="table_head">
                    <div class="table_head_item1" >
                        <h2  >用户列表</h2>
                    </div>

                    <!--搜索-->
                    <div class="table_head_item2">
                        <el-input
                                placeholder="请输入邮箱"
                                prefix-icon="el-icon-search"
                                v-model="searchInputUser"
                                @keyup.enter.native="getUserList()"
                        >
                        </el-input>
                        &nbsp;&nbsp;
                        <el-button  type="success" icon="el-icon-search" @click="getUserList()" >搜索</el-button>
                    </div>

                    <!--表格头部菜单-->
                    <div class="table_head_item3" >

                    </div>

                </div>
                <el-table
                        ref="userTable"
                        :data="userTableData"
                        stripe
                        style="width: 100%"
                        height="75vh"
                        :default-sort="{ prop: 'viewCount', order: 'descending' }"
                >

                    <el-table-column sortable label="姓名"show-overflow-tooltip min-width="200px">
                        <template slot-scope="scope" >
                            <el-link :href="userPageUrl+scope.row.accessId" target="_blank">{{scope.row.name}}</el-link>
                        </template>
                    </el-table-column>

                    <el-table-column sortable prop="email" label="邮箱" show-overflow-tooltip min-width="200px">
                    </el-table-column>
                    <el-table-column prop="userRole" label="权限"  min-width="100px">
                    </el-table-column>

                    <el-table-column  label="操作" show-overflow-tooltip min-width="200px">
                        <template slot-scope="scope">
                            <el-dropdown >
                                <el-button type="primary"  size="mini">
                                    权限设置<i class="el-icon-arrow-down el-icon--right"></i>
                                </el-button>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item @click.native="handleRoleSelect('ROLE_ROOT',scope.row.id)" >ROOT</el-dropdown-item>
                                    <el-dropdown-item @click.native="handleRoleSelect('ROLE_ADMIN',scope.row.id)">ADMIN</el-dropdown-item>
                                    <el-dropdown-item @click.native="handleRoleSelect('ROLE_USER',scope.row.id)">USER</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </template>
                    </el-table-column>


                </el-table>
                <el-divider></el-divider>

                <el-pagination
                        background
                        @size-change="handleSizeChangeUser"
                        @current-change="handleCurrentChangeUser"
                        :current-page="currentPageUser"
                        :page-sizes="[10, 20, 30, 40]"
                        :page-size="PageSizeUser"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="totalUser"
                        class="flexRowCenter"
                >
                </el-pagination>
        </div>
        `,
    data() {
        return {
            userTableData:[], //用户列表数据
            currentPageUser:1, //用户列表表格页
            PageSizeUser:20, //用户列表每页数目
            totalUser:0, //用户总数
            searchInputUser:"", //用户表搜索内容
            userPageUrl:"https://geomodeling.njnu.edu.cn/profile/" // 门户个人主页
        }
    },
    mounted(){
        this.getUserList();
    },
    methods: {
//获取用户列表信息
        getUserList(){
            axios.post('/managementSystem/user/info', {
                "asc": false,
                "page": this.currentPageUser,
                "pageSize": this.PageSizeUser,
                "searchText": this.searchInputUser,
                "sortField": "createTime"
            })
                .then(response=> {
                    this.userTableData=response.data.data.content
                    this.totalUser=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //页面切换
        handleSizeChangeUser(val){
            this.PageSizeUser=val
            this.getUserList()
        },
        handleCurrentChangeUser(val){
            this.currentPageUser=val
            this.getUserList()
        },

        //用户权限切换
        handleRoleSelect(role,userId){
            axios.post('/managementSystem/user/role/'+userId+'/'+role, {
            })
                .then(response=> {
                    this.getUserList()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
    }
})