export var TaskTemplate = Vue.extend({
    template: ` 
 
    <div>
              <div>
                <el-radio-group v-model="servierIP">
                  <el-radio-button label="172.21.213.105">172.21.213.105</el-radio-button>
                  <el-radio-button label="221.226.60.2">221.226.60.2</el-radio-button>
                </el-radio-group>
              </div>
            <el-tabs type="border-card">
                <el-tab-pane label="Tasks">
                    <el-table
                        :data="taskData"
                        border
                        stripe
                        style="width: 100%">
                        <el-table-column
                                prop="t_msrid"
                                label="Msrid"
                                width="280">
                        </el-table-column>
                        <el-table-column
                                prop="t_user"
                                label="User"
                                width="280">
                        </el-table-column>
                        <el-table-column
                                prop="t_status"
                                sortable
                                label="Status"
                                width="100">

                        </el-table-column>
                        <el-table-column label="Date">
                            <template slot-scope="scope">
                                {{formatDate(scope.row.t_datetime)}}
                            </template>
                        </el-table-column>
                        <el-table-column
                                prop="t_server"
                                sortable
                                label="Server">
                            <template slot-scope="scope">
                                <div class="flexAlignCenter flexJustBetween">
                                    <p class="noMargin">{{scope.row.t_server}}</p>
                                    <el-button class="noMargin cursorPointer"
                                               icon="el-icon-search"
                                               type="primary"
                                               plain
                                               circle
                                               @click="checkServer(scope.row)">
                                    </el-button>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column
                                prop="t_pid"
                                sortable
                                label="Pid(md5)">
                        </el-table-column>
                    </el-table>
                    <el-pagination style="text-align: center;margin-top:20px"
                                   @current-change="handleTaskPageChange"
                                   :current-page="taskCurrentPage"
                                   layout="total, prev, pager, next"
                                   page-size=20
                                   :total="taskTotal">
                    </el-pagination>
                </el-tab-pane>
                
                <el-tab-pane label="Containers">
                    <el-table
                            :data="containerData"
                        border
                        stripe
                        style="width: 100%">
                        <el-table-column
                                prop="s_ip"
                                label="Ip">
                        </el-table-column>
                        <el-table-column
                                prop="s_port"
                                label="Port">
                        </el-table-column>
                        <el-table-column
                                label="Status">
                            <template slot-scope="scope">
                                <el-tag type="success" v-if="scope.row.s_status" >online</el-tag>
                                <el-tag type="info" v-else>offline</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column
                                label="Go Go Go">
                            <template slot-scope="scope">
                                <el-button class="noMargin cursorPointer"
                                          icon="el-icon-right"
                                           type="primary"
                                           plain
                                          circle
                                          @click="window.open('http://'+scope.row.s_ip+':'+scope.row.s_port)">
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
    </div>
        `,
    data() {
        return {
            servierIP:"172.21.213.105", //服务器地址
            taskData:[],//task的数据
            taskTotal:0,//task总数
            taskCurrentPage:1, //task当前页面

            containerData:[], //容器列表数据

        }
    },
    mounted(){

    },
    methods: {
        getTaskList(){
            axios.post('/managementSystem', {
                "asc": false,
                "page": this.taskCurrentPage,
                "pageSize": 20,
                "searchText": "",
                "sortField": ""
            })
                .then(response=> {
                    let data=response.data.data
                    this.taskTotal=data.total
                    this.taskData=data.content
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        handleTaskPageChange(val){
            this.currentPage = val;
            this.getTaskList();
        },

        getContainerList(){
            axios.post('/managementSystem', {
                "asc": false,
                "page": 1,
                "pageSize": 20,
                "searchText": "",
                "sortField": ""
            })
                .then(response=> {
                    let data=response.data.data
                    this.containerData=data.content
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


    }
})