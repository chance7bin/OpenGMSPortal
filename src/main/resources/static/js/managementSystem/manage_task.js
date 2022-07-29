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
                        style="width: 100%"
                        height="70vh">
                        <el-table-column width="230px" prop="msrid" label="Msrid" show-overflow-tooltip min-width="250px"> </el-table-column>
                        <el-table-column label="模型名称" show-overflow-tooltip min-width="150px" >
                              <template slot-scope="scope" >
                                  <el-popover
                                    placement="bottom"
                                    title=""
                                    width="270"
                                    trigger="hover"
                                    >
                                    <h4 style="margin: 0">名称:</h4>
                                    <p style="margin: 0">{{scope.row.computableModelName}}</p>
                                    <h4 style="margin: 0">md5:</h4>
                                    <p style="margin: 0">{{scope.row.md5}}</p>
                                    <h4 style="margin: 0">部署的模型容器:</h4>
                                    <el-link v-for="(item,index) in scope.row.deployedServer" 
                                            type="primary" 
                                            :href="'http://'+item+'/modelser/all'" 
                                            target="_blank"
                                            style="display: flex; flex-direction: column;align-items: flex-start;"
                                            >{{item}}</el-link>
                                    
                                    <div slot="reference" style="width=100%;overflow: hidden; text-overflow: ellipsis;white-space: nowrap;color: #1077e1; ">
                                        {{scope.row.computableModelName}}
                                    <div/>
                                  </el-popover>
                              </template>
                        </el-table-column>
                        <el-table-column prop="user" label="用户" show-overflow-tooltip min-width="200px"> </el-table-column>
                        <el-table-column prop="status" label="状态" show-overflow-tooltip min-width="100px"> </el-table-column>
                        <el-table-column prop="date" label="时间" show-overflow-tooltip min-width="190px"> </el-table-column>
                        <el-table-column label="运行的模型容器" show-overflow-tooltip min-width="190px">
                            <template slot-scope="scope">
                                <el-link type="primary" :href="'http://'+scope.row.runServer+'/modelserrun/'+scope.row.msrid" target="_blank">{{scope.row.runServer}}</el-link>
                            </template>
                        </el-table-column>
                        
                    </el-table>
                    <el-pagination 
                                   background
                                   style="text-align: center;margin-top:20px"
                                    @size-change="handleTaskSizeChange"
                                   @current-change="handleTaskPageChange"
                                   :current-page="taskCurrentPage"
                                   layout="total, sizes, prev, pager, next, jumper"
                                   :page-size=taskPageSize
                                   :total="taskTotal">
                    </el-pagination>
                </el-tab-pane>
                
                <el-tab-pane label="Containers">
                    <el-table
                            :data="containerData"
                        border
                        stripe
                        style="width: 100%">
                        <el-table-column prop="ip" label="Ip"></el-table-column>
                        <el-table-column prop="port" label="Port"></el-table-column>
                        <el-table-column prop="type" label="Type"></el-table-column>
                        <el-table-column label="Status">
                            <template slot-scope="scope">
                                <el-tag type="success" v-if="scope.row.status" >online</el-tag>
                                <el-tag type="danger" v-else>offline</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <templat slot-scope="scope">
                                 <el-link :href="'http://'+scope.row.ip+':'+scope.row.port" target="_blank">前往模型容器</el-link>
                            </templat>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
    </div>
        `,
    data() {
        return {
            servierIP: "172.21.213.105", //服务器地址
            taskData: [],//task的数据
            taskTotal: 0,//task总数
            taskCurrentPage: 1, //task当前页面
            taskPageSize:20,

            containerData: [], //容器列表数据

        }
    },
    mounted() {
        this.getTaskList()
        this.getContainerList()
    },
    methods: {
        getTaskList() {
            axios.post('/managementSystem/taskList', {
                "asc": false,
                "page": this.taskCurrentPage,
                "pageSize": this.taskPageSize,
                "searchText": "",
                "sortField": "createTime"
            })
                .then(response => {
                    let data = response.data.data
                    this.taskTotal = data.total
                    this.taskData = data.list
                    console.log(data)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        handleTaskPageChange(val) {
            this.taskCurrentPage = val;
            this.getTaskList();
        },

        handleTaskSizeChange(val){
          this.taskPageSize=val;
          this.getTaskList();
        },



        getContainerList() {
            axios.get('/managementSystem/mscList')
                .then(response => {
                    this.containerData = response.data.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


    }
})