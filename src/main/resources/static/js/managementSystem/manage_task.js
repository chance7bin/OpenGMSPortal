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
                        height="65vh">
                        <el-table-column
                                width="230px"
                                prop="msrid"
                                label="Msrid">
                        </el-table-column>
                        <el-table-column
                                prop="user"
                                label="用户">
                        </el-table-column>
                        <el-table-column
                                prop="status"
                                label="状态">
                        </el-table-column>
                        <el-table-column prop="date" label="时间">
<!--                            <template slot-scope="scope">-->
<!--                                {{formatDate(scope.row.t_datetime)}}-->
<!--                            </template>-->
                        </el-table-column>
                        <el-table-column label="运行的模型容器">
                            <template slot-scope="scope">
                                <el-link type="primary" :href="'http://'+scope.row.runServer+'/modelserrun/'+scope.row.msrid" target="_blank">{{scope.row.runServer}}</el-link>
                            </template>
                        </el-table-column>
                        <el-table-column label="模型名称" >
                              <template slot-scope="scope">
                                  <el-popover
                                    placement="bottom"
                                    title=""
                                    width="255"
                                    trigger="hover">
                                    <h5 style="margin: 0">名称:</h5>
                                    <p style="margin: 0">{{scope.row.computableModelName}}</p>
                                    <h5 style="margin: 0">md5:</h5>
                                    <p style="margin: 0">{{scope.row.md5}}</p>
                                    <h5 style="margin: 0">部署的模型容器:</h5>
                                    <el-link v-for="(item,index) in scope.row.deployedServer" type="primary" :href="'http://'+item+'/modelser/all'" target="_blank">{{item}}</el-link>
                                    
                                    <div slot="reference" style="width=100%;overflow: hidden; text-overflow: ellipsis;white-space: nowrap;color: darkorange; ">
                                        {{scope.row.computableModelName}}
                                    <div/>
                                  </el-popover>
                                  </template>
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
            servierIP:"172.21.213.105", //服务器地址
            taskData:[],//task的数据
            taskTotal:0,//task总数
            taskCurrentPage:1, //task当前页面

            containerData:[], //容器列表数据

        }
    },
    mounted(){
        this.getTaskList()
        this.getContainerList()
    },
    methods: {
        getTaskList(){
            axios.post('/managementSystem/taskList', {
                "asc": false,
                "page": this.taskCurrentPage,
                "pageSize": 20,
                "searchText": "",
                "sortField": "createTime"
            })
                .then(response=> {
                    console.log("1111")
                    let data=response.data.data
                    this.taskTotal=data.total
                    this.taskData=data.list
                    console.log(data)
                    console.log("1111")

                })
                .catch(function (error) {
                    console.log("1111")

                    console.log(error);
                });
        },

        handleTaskPageChange(val){
            this.taskCurrentPage = val;
            this.getTaskList();
        },

        getContainerList(){
            axios.get('/managementSystem/mscList')
                .then(response=> {
                    this.containerData=response.data.data
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


    }
})