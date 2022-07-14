export var ModelTemplate = Vue.extend({
    template: ` 
<div class="model_list_container">
                    <div class="model_list_container">
<!--                        表格上部菜单-->
                        <div class="table_head">
                            <div class="table_head_item1" >
                                <h2  >模型列表</h2>
                            </div>

<!--                            搜索-->
                            <div class="table_head_item2">
                                <el-input
                                        placeholder="请输入内容"
                                        prefix-icon="el-icon-search"
                                        v-model="searchInput"
                                        @keyup.enter.native="getModelList()"
                                >
                                </el-input>
                                &nbsp;&nbsp;
                                <el-button  type="success" icon="el-icon-search" @click="searchModel()" >搜索</el-button>
                            </div>

<!--                            表格头部菜单-->
                            <div class="table_head_item3" >
                                    <el-button type="primary" @click="getHistoryList()">检测历史</el-button>

                                <el-popover
                                        placement="bottom"
                                        width="345"
                                        trigger="hover"
                                        style="margin-left: 10px">
                                    <el-table :data="waitCheckModels" :show-header="false" max-height="345" stripe>
                                        <el-table-column type="index" width="40"></el-table-column>
                                        <el-table-column width="255" property="modelName" label="名称">

                                        </el-table-column>
                                        <el-table-column  label="操作" width="50">
                                            <template slot-scope="scope" >
                                                <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteCheckModel(scope.row.modelId)" circle></el-button>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                    <el-button  slot="reference"   type="success" >待检测模型</el-button>
                                    <div class="flexRowBetween">
                                        <div class="flexColCenter">
                                            <span>（此处批量检测模型将计入检测历史）</span>
                                        </div>
                                        <el-button  size="medium" type="success" @click="checkSelectedModels()" >批量检测</el-button>
                                    </div>
                                </el-popover>
                            </div>
                            <div >
                            </div>
                        </div>

                        <!-- 历史记录弹出框 -->
                        <el-dialog title="检测历史记录" :visible.sync="historyTableVisible">

                            <el-collapse v-model="activeHistoryItem" class="historyDiv" accordion>
                                <el-collapse-item v-for="(item, index) in historyList"  :name="index">
                                    <template slot="title">
                                        <h4>{{item.draftName}}</h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                        <el-button
                                                size="mini"
                                                type="primary"
                                                @click.stop="checkHistoryModels(item.historyList)">Check</el-button>
                                        <el-button
                                                size="mini"
                                                type="success"
                                                @click.stop="loadCheckedModel(item.historyList)">Load</el-button>
                                        <el-button
                                                size="mini"
                                                type="warning"
                                                @click.stop="deleteHistoryItem(item.id)">Delete</el-button>
                                    </template>
                                    <el-table :data="item.historyList" :show-header="false">
                                        <el-table-column  property="modelName" width="400px" show-overflow-tooltip></el-table-column>          
                                        <el-table-column  >
                                            <template slot-scope="scope">
                                                <el-tag
                                                        v-if="(scope.row.status===-1 ||scope.row.hasInvokeSuccess===false||scope.row.hasTest===false)&&scope.row.msg!=='未找到测试数据'"
                                                        type='danger'
                                                        disable-transitions>{{scope.row.msg}}</el-tag>
                                                <el-tag
                                                        v-if="(scope.row.status===-1 ||scope.row.hasInvokeSuccess===false||scope.row.hasTest===false)&&scope.row.msg==='未找到测试数据' "
                                                        color="rgb(200 255 247)"
                                                        disable-transitions>未找到测试数据</el-tag>
                                                <el-tag
                                                        v-if="scope.row.hasInvokeSuccess===true&& scope.row.status===0 "
                                                        type='warning'
                                                        disable-transitions>正在排队检测</el-tag>
                                                <el-tag
                                                        v-if="scope.row.hasInvokeSuccess===true&& scope.row.status===1 "
                                                        type='primary'
                                                        disable-transitions>正在检测</el-tag>
                                                <el-tag
                                                        v-if="scope.row.hasInvokeSuccess===true&& scope.row.status===2 "
                                                        type='success'
                                                        disable-transitions>正常</el-tag> 
                                            </template>
                                        </el-table-column>  
                                        <el-table-column >
                                            <template slot-scope="scope">
                                                <el-tag type='info' disable-transitions>
                                                    <el-link v-if="scope.row.msrid!==null && scope.row.msrid!==''" :href="'http://'+scope.row.msrAddress+'/modelserrun/'+scope.row.msrid" target="_blank">{{scope.row.msrAddress}}</el-link>
                                                    <div v-else>未分配模型容器</div>
                                                </el-tag>
                                            </template>
                                        </el-table-column>  
                                    </el-table>
                                </el-collapse-item>
                            </el-collapse>
                            
                            <el-pagination
                                    layout="total,prev, pager, next"
                                    :total="historyTotal"
                                    @current-change="handleHistoryCurrentChange"
                                    :current-page="historyNowPage"
                                    :page-size="10"
                                    class="flexRowCenter"
                                >
                            </el-pagination>
                        </el-dialog>

<!--                        模型列表-->
                        <span>tips：点击检测进行单个模型检测，或勾选多个模型进行批量检测</span>
                        <el-table
                                ref="modelTable"
                                @select="handelCheckbox"
                                @select-all="handelAllCheck"
                                :data="tableData"
                                stripe
                                style="width: 100%"
                                height="75vh"
                                :default-sort="{ prop: 'viewCount', order: 'descending' }"
                        >
                            <el-table-column type="selection">
                            </el-table-column>
                            <el-table-column sortable label="名称" show-overflow-tooltip min-width="200px">
                                <template slot-scope="scope" >
                                    <el-link :href="computableModelUrl+scope.row.id" target="_blank" >{{scope.row.name}}</el-link>
                                </template>
                            </el-table-column>

                            <el-table-column sortable prop="md5" label="md5" show-overflow-tooltip min-width="180px">
                            </el-table-column>

                            <el-table-column sortable prop="author" label="作者" show-overflow-tooltip min-width="100px">
                            </el-table-column>

                            <el-table-column sortable prop="viewCount" label="访问量" min-width="80px"></el-table-column>
                            <el-table-column sortable label="模型容器IP" min-width="120px">
                                <template slot-scope="scope" >
                                  <el-popover
                                    placement="bottom"
                                    title="服务器地址(点击前往):"
                                    width="200"
                                    trigger="hover">
                                        <el-link type="primary" v-for="(item,index) in scope.row.deployedMSR" :href="'http://'+item+'/modelser/all'" target="_blank">{{item}}</el-link>
                                        <el-button size="mini" slot="reference" >查看服务器IP</el-button>
                                  </el-popover>
                                </template>
                            </el-table-column>

                            <el-table-column  label="操作" >
                                <template slot-scope="scope">
                                    <el-button
                                            size="mini"
                                            type="primary"
                                            @click="checkModel(scope.row.id)"
                                            title="单个模型检测不计入检测历史">检测</el-button>
                                </template>
                            </el-table-column>

                            <el-table-column  prop="status" label="状态" min-width="150px">
                                <template slot-scope="scope">
                                    <el-tag
                                            v-if="scope.row.checkedModel ===null"
                                            type='info'
                                            disable-transitions>未检测</el-tag>
                                    <el-tag
                                            v-if="scope.row.checkedModel !==null && (scope.row.checkedModel.status===-1 ||scope.row.checkedModel.hasInvokeSuccess===false||scope.row.checkedModel.hasTest===false)&&scope.row.checkedModel.msg!=='未找到测试数据'"
                                            type='danger'
                                            disable-transitions>{{scope.row.checkedModel.msg}}</el-tag>
                                    <el-tag
                                            v-if="scope.row.checkedModel !==null && (scope.row.checkedModel.status===-1 ||scope.row.checkedModel.hasInvokeSuccess===false||scope.row.checkedModel.hasTest===false)&&scope.row.checkedModel.msg==='未找到测试数据' "
                                            color="rgb(200 255 247)"
                                            disable-transitions>未找到测试数据</el-tag>
                                    <el-tag
                                            v-if="scope.row.checkedModel !==null &&scope.row.checkedModel.hasInvokeSuccess===true&& scope.row.checkedModel.status===0 "
                                            type='warning'
                                            disable-transitions>正在排队检测</el-tag>
                                    <el-tag
                                            v-if="scope.row.checkedModel !==null &&scope.row.checkedModel.hasInvokeSuccess===true&& scope.row.checkedModel.status===1 "
                                            type='primary'
                                            disable-transitions>正在检测</el-tag>
                                    <el-tag
                                            v-if="scope.row.checkedModel !==null &&scope.row.checkedModel.hasInvokeSuccess===true&& scope.row.checkedModel.status===2 "
                                            type='success'
                                            disable-transitions>正常</el-tag>
                                </template>
                            </el-table-column>

                        </el-table>
                        <el-divider></el-divider>
<!--                            模型列表分页-->
                            <el-pagination
                                    background
                                    @size-change="handleSizeChange"
                                    @current-change="handleCurrentChange"
                                    :current-page="currentPage"
                                    :page-sizes="[10, 20, 30, 40]"
                                    :page-size="PageSize"
                                    layout="total, sizes, prev, pager, next, jumper"
                                    :total="totalModel"
                                    class="flexRowCenter"
                            >
                            </el-pagination>
                    </div>
                </div>
        `,
    data() {
        return {
            tableData: [], //表格中展示的数据
            totalModel: 0, //数据库中模型总数
            currentPage: 1,  //模型列表当前页
            PageSize: 20,  //模型列表每页数量
            searchInput:"", //模型列表搜索框内容
            sortField:"createTime", //模型列表排序字段 默认时间
            ifAsc:false, //模型请求列表升降序
            historyTableVisible:false, //历史记录弹出框
            historyList:[], //历史记录数据
            historyTotal:0, //历史记录总数
            historyNowPage:1, //历史记录当前页
            activeHistoryItem:-1, //当前展开的条目
            waitCheckModels:[], //选中的待检测模型


            // computableModelUrl:"https://geomodeling.njnu.edu.cn/computableModel/", //门户计算模型的网址前缀
            computableModelUrl:"/computableModel/", //门户计算模型的网址前缀
            mscUrl:"http://172.21.212.85:8060/modelserrun/61b88bfffb1ee50774d2683d",

        }
    },
    mounted() {
        //初始加载模型列表数据
        this.getModelList();
    },
    methods: {
        //模型列表请求模型条目数据
        getModelList(){
            axios.post('/managementSystem/deployedModel', {
                "asc": this.ifAsc,
                "page": this.currentPage,
                "pageSize": this.PageSize,
                "searchText": this.searchInput,
                "sortField": this.sortField
            })
                .then(response=> {
                    let data=response.data.data
                    this.totalModel=data.total
                    this.tableData=data.content
                    for (let i=0,len=this.waitCheckModels.length;i<len;i++){
                        this.updateCheck(this.waitCheckModels[i].modelId)
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //模型列表分页
        handleSizeChange(val) {
            this.PageSize = val;
            this.getModelList();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getModelList();
        },

        //模型搜索
        searchModel(){
            this.getModelList();
        },

        //响应模型列表选择(单选)
        handelCheckbox(selection, row){
            let tempModelItem={
                modelName:row.name,
                modelId:row.id
            }

            let findIndex=-1
            for(let i = 0,len=this.waitCheckModels.length; i < len; i++) {
                if(this.waitCheckModels[i].modelId===tempModelItem.modelId){
                    findIndex=i
                    break
                }
            }

            if(findIndex!==-1){
                this.waitCheckModels.splice(findIndex,1)
                this.$message({
                    showClose: true,
                    message: '从检测队列移除模型: '+tempModelItem.modelName,
                    type: 'warning',
                    duration: 2000,
                });
            }else{
                this.waitCheckModels.push(tempModelItem)
                this.$message({
                    showClose: true,
                    message: '成功添加模型: '+tempModelItem.modelName+ " 至待检测队列",
                    type: 'success',
                    duration: 2000,
                });
            }
        },

        //响应模型列表选择(全选)
        handelAllCheck(selection){
            if(selection.length!==0){  //全选中时
                for (let i=0,len=selection.length;i<len;i++){
                    let tempModelItem={
                        modelName:selection[i].accessId,
                        modelId:selection[i].id
                    }
                    let findIndex=-1
                    for(let j = 0,len1=this.waitCheckModels.length; j < len1; j++) {
                        if(this.waitCheckModels[j].modelId===tempModelItem.modelId){
                            findIndex=j
                            break
                        }
                    }

                    if(findIndex===-1){
                        this.waitCheckModels.push(tempModelItem)
                    }
                }
            }else { //全选取消
                for (let i=0,len=this.tableData.length;i<len;i++){
                    let findIndex=-1
                    for(let j = 0,len1=this.waitCheckModels.length; j < len1; j++) {
                        if(this.waitCheckModels[j].modelId===this.tableData[i].id){
                            findIndex=j
                            break
                        }
                    }
                    if(findIndex!==-1){
                        this.waitCheckModels.splice(findIndex,1)
                    }
                }
            }

        },

        //从待检测模型列表删除一项
        deleteCheckModel(modelId){
            for (let i=0,len=this.waitCheckModels.length;i<len;i++){
                if(this.waitCheckModels[i].modelId===modelId){
                    this.$message({
                        showClose: true,
                        message: '从检测队列移除模型: '+this.waitCheckModels[i].modelName,
                        type: 'warning',
                        duration: 2000,
                    });
                    this.waitCheckModels.splice(i,1)

                    break
                }
            }
            this.updateCheck(modelId)
        },

        //输入模型id，更新模型table表中的选择状态
        updateCheck(modelId){
            this.$nextTick(function () {    //解决更换数据后this.$refs.modelTable.toggleRowSelection无效问题
                for (let i=0,len=this.tableData.length;i<len;i++){
                    if(this.tableData[i].id===modelId){
                        this.$refs.modelTable.toggleRowSelection(this.tableData[i]);
                        break
                    }
                }
            })
        },

        //检测历史模型
        checkHistoryModels(historyList){
            this.$confirm('是否开始检测所选模型?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '开始检测!!!'
                });
                let selectedModelIds=historyList.map(item=>{
                    return item.modelId
                })
                axios.post('/managementSystem/model/invoke/batch',
                    selectedModelIds
                ).then(response=>{
                    this.getModelList()
                }).catch(function (error) {
                    console.log(error);
                })
                this.waitCheckModels=[]
                this.$refs.modelTable.clearSelection();

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消检测！！！'
                });
            });
        },


        //检测所选模型（多选）
        checkSelectedModels() {
            this.$confirm('是否开始检测所选全部模型?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '开始检测!!!'
                });
                let selectedModelIds=this.waitCheckModels.map(item=>{
                    return item.modelId
                })
                axios.post('/managementSystem/model/invoke/batch',
                    selectedModelIds
                ).then(response=>{
                    this.getModelList()
                }).catch(function (error) {
                    console.log(error);
                })
                this.waitCheckModels=[]
                this.$refs.modelTable.clearSelection();

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消检测！！！'
                });
            });
        },

        //检测模型（单个，用于响应单次检测按钮）
        checkModel(modelId){
            this.$confirm('是否开始检测所选模型?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '开始检测!'
                });
                axios.get('/managementSystem/model/invoke/'+modelId)
                    .then( response=> {
                        // console.log(response);
                        this.getModelList()
                    })
                    .catch(function (error) {
                        console.log(error);
                    });

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消检测！'
                });
            });


        },

        //获取历史检测记录
        getHistoryList(){
            this.historyTableVisible = true
            axios.post('/managementSystem/checkList/search', {
                "asc": false,
                "page": this.historyNowPage,
                "pageSize": 10,
                "searchText": "",
                "sortField": "createTime"
            })
                .then(response=> {
                    console.log(response)
                    let returnData=response.data.data
                    this.historyList=returnData.content
                    this.historyTotal=returnData.total

                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //从历史记录加载所选模型
        loadCheckedModel(historyList){
            for (let k=0,len0=historyList.length;k<len0;k++){
                for (let i=0,len1=this.waitCheckModels.length;i<=len1;i++){
                    if(len1===0||i===len1){
                        let tempItem={
                            modelName:historyList[k].modelName,
                            modelId:historyList[k].modelId
                        }
                        this.waitCheckModels.push(tempItem)
                        this.updateCheck(historyList[k].modelId)
                        break
                    }
                    if(this.waitCheckModels[i].modelId===historyList[k].modelId){
                        break
                    }
                }
            }
            this.$message('添加成功');
        },

        //删除检测历史记录
        deleteHistoryItem(historyId){
            axios.delete('/managementSystem/checkList/delete/'+historyId)
                .then(response=> {
                    this.getHistoryList()
                    this.$message('删除成功');
                })
                .catch(function (error) {
                    this.$message.error('删除失败');
                    console.log(error);
                });
        },

        //历史记录切换页面
        handleHistoryCurrentChange(val) {
            this.historyNowPage = val;
            this.getHistoryList();
        },
    }
})
