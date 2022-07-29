export var VersionTemplate = Vue.extend({
    template:` 
        <div class="versionHandle">
                <el-tabs tab-position="left"  type="border-card" @tab-click="changeTabs" >
<!--                            未审核页面-->
                    <el-tab-pane label="未审核版本">
                        <div class=" flexColumnStart">
                                <div class="flexRowBetween">
                                    <el-input
                                            placeholder="请输入名称"
                                            prefix-icon="el-icon-search"
                                            v-model="searchInputWaitVersion"
                                            @keyup.enter.native="getWaitVersionList()"
                                    >
                                    </el-input>
                                    &nbsp;&nbsp;
                                    <el-button  type="success" icon="el-icon-search" @click="getWaitVersionList()" >搜索</el-button>
                                </div>
                            <div>
                                <el-table
                                        ref="waitVersionTable"
                                        :data="waitVersionTableData"
                                        stripe
                                        style="width: 100% "
                                        height="70vh"
                                        :default-sort="{ prop: 'submitTime', order: 'descending' }"
                                >
                                    <el-table-column sortable prop="submitTime" label="日期"  show-overflow-tooltip min-width="240px">
                                    </el-table-column>
                                    <el-table-column sortable prop="itemName" label="名称"  show-overflow-tooltip min-width="240px">
                                    </el-table-column>

                                    <el-table-column sortable prop="type" label="类型" show-overflow-tooltip min-width="100px" >
                                    </el-table-column>
                                    <el-table-column sortable prop="editor" label="修改者"  show-overflow-tooltip min-width="180px">
                                    </el-table-column>

                                    <el-table-column  label="操作" show-overflow-tooltip min-width="250px">
                                        <template slot-scope="scope">
                                            <el-button
                                                    size="mini"
                                                    type="primary"
                                                    @click="viewVersionById(scope.row.type,scope.row.id)">查看</el-button>
                                            <el-button
                                                    size="mini"
                                                    type="success"
                                                    @click="acceptVersionById(scope.row.id)">接受</el-button>
                                            &nbsp;
                                            <el-button
                                                    size="mini"
                                                    type="danger"
                                                    @click="rejectVersionById(scope.row.id)">拒绝</el-button>
                                        </template>

                                    </el-table-column>

                                </el-table>
                                <el-divider></el-divider>

                                <el-pagination
                                        background
                                        @size-change="handleSizeChangeWaitVersion"
                                        @current-change="handleCurrentChangeWaitVersion"
                                        :current-page="currentPageWaitVersion"
                                        :page-sizes="[10, 20, 30, 40]"
                                        :page-size="PageSizeWaitVersion"
                                        layout="total, sizes, prev, pager, next, jumper"
                                        :total="totalWaitVersion"
                                        class="flexRowCenter"
                                >
                                </el-pagination>
                            </div>
                        </div>

                    </el-tab-pane>
<!--                                版本对比对话框-->
                    <el-dialog id="versionComp"  :visible.sync="dialogVersionComp" width="80%" center="true" showClose="false">
                        <div slot="title" >
                            <h1>版本审核</h1>
                            <div >
                              <span  @click="versionCompareShow"></span>
                            </div>
                          </div>

                        <div class="flexRowEvenly">
                            <h1>原始版本</h1>
                            <el-divider direction="vertical"></el-divider>
                            <h1>审核版本</h1>
                        </div>

                        <div v-if="versionCompChangedField.authorships">
                            <h1>authorships:</h1>
                            <div class="flexRowBetween">
                                <div>
                                    {{versionCompChangedField.authorships.original[0]}}
                                </div>
                                <el-divider direction="vertical"></el-divider>
                                <div>
                                    {{versionCompChangedField.authorships.new[0]}}
                                </div>
                            </div>
                            <el-divider></el-divider>
                        </div>

                        <div v-if="versionCompChangedField.localizationList">
                            <h1>localizationList:</h1>
                            <div class="flexRowBetween">
                                <div class="versionCompImgBox" v-html="versionCompChangedField.localizationList.original[0].description"></div>
                                <el-divider direction="vertical"></el-divider>
                                <div class="versionCompImgBox" v-html="versionCompChangedField.localizationList.original[0].description"></div>
                            </div>
                            <el-divider></el-divider>

                        </div>

                        <div v-if="versionCompChangedField.localizationList">
                            <h1>localizationList:</h1>
                            <div class="flexRowBetween">
                                <div class="versionCompImgBox" v-html="versionCompChangedField.localizationList.original[0].description"></div>
                                <el-divider direction="vertical"></el-divider>
                                <div class="versionCompImgBox" v-html="versionCompChangedField.localizationList.original[0].description"></div>
                            </div>
                            <el-divider></el-divider>
                        </div>
                        
                        <div class="flexRowBetween" style=" width: fit-content; position: fixed;  bottom: 5vh; right: 15vw;">
                            <el-button class="" type="success">接受</el-button>
                            <el-button class="" type="danger">拒绝</el-button>
                            <el-button class="" type="info" @click="versionCompareShow">取消</el-button>
                        </div>



                    </el-dialog>

<!--                            接受页面-->
                    <el-tab-pane label="接受版本">
                        <div class=" flexColumnStart">
                            <div class="flexRowBetween">
                                <el-input
                                        placeholder="请输入内容"
                                        prefix-icon="el-icon-search"
                                        v-model="searchInputAcceptVersion"
                                        @keyup.enter.native="getAcceptVersionList()"
                                >
                                </el-input>
                                &nbsp;&nbsp;
                                <el-button  type="success" icon="el-icon-search" @click="getAcceptVersionList()" >搜索</el-button>
                            </div>
                            <div class="">
                                <el-table
                                        ref="acceptVersionTable"
                                        :data="acceptVersionTableData"
                                        stripe
                                        style="width: 100%"
                                        height="70vh"
                                        :default-sort="{ prop: 'submitTime', order: 'descending' }"
                                >
                                    <el-table-column sortable prop="submitTime" label="日期" show-overflow-tooltip min-width="240px">
                                    </el-table-column>
                                    <el-table-column sortable prop="itemName" label="名称" show-overflow-tooltip min-width="240px">
                                    </el-table-column>

                                    <el-table-column sortable prop="type" label="类型" show-overflow-tooltip min-width="100px">
                                    </el-table-column>
                                    <el-table-column sortable prop="editor" label="修改者" show-overflow-tooltip min-width="180px">
                                    </el-table-column>

                                    <el-table-column  label="操作" show-overflow-tooltip min-width="80px">
                                        <template slot-scope="scope">
                                            <el-button
                                                    size="mini"
                                                    type="primary"
                                                    @click="viewVersionById(scope.row.type,scope.row.id)">查看</el-button>
                                        </template>

                                    </el-table-column>

                                </el-table>
                                <el-divider></el-divider>

                                <el-pagination
                                        background
                                        @size-change="handleSizeChangeAcceptVersion"
                                        @current-change="handleCurrentChangeAcceptVersion"
                                        :current-page="currentPageAcceptVersion"
                                        :page-sizes="[10, 20, 30, 40]"
                                        :page-size="PageSizeAcceptVersion"
                                        layout="total, sizes, prev, pager, next, jumper"
                                        :total="totalAcceptVersion"
                                        class="flexRowCenter"
                                >
                                </el-pagination>
                            </div>
                        </div>

                    </el-tab-pane>

<!--                            拒绝页面-->
                    <el-tab-pane label="拒接版本">
                        <div class=" flexColumnStart">
                            <div class="flexRowBetween">
                                <el-input
                                        placeholder="请输入内容"
                                        prefix-icon="el-icon-search"
                                        v-model="searchInputRejectVersion"
                                        @keyup.enter.native="getRejectVersionList()"
                                >
                                </el-input>
                                &nbsp;&nbsp;
                                <el-button  type="success" icon="el-icon-search" @click="getRejectVersionList()" >搜索</el-button>
                            </div>
                            <div>
                                <el-table
                                        ref="rejectVersionTable"
                                        :data="rejectVersionTableData"
                                        stripe
                                        style="width: 100%"
                                        height="70vh"
                                        :default-sort="{ prop: 'submitTime', order: 'descending' }"
                                >
                                    <el-table-column sortable prop="submitTime" label="日期" show-overflow-tooltip min-width="240px">
                                    </el-table-column>
                                    <el-table-column sortable prop="itemName" label="名称" show-overflow-tooltip min-width="240px">
                                    </el-table-column>

                                    <el-table-column sortable prop="type" label="类型" show-overflow-tooltip min-width="100px">
                                    </el-table-column>
                                    <el-table-column sortable prop="editor" label="修改者" show-overflow-tooltip min-width="180px">
                                    </el-table-column>

                                    <el-table-column  label="操作" show-overflow-tooltip min-width="80px">
                                        <template slot-scope="scope">
                                            <el-button
                                                    size="mini"
                                                    type="primary"
                                                    @click="viewVersionById(scope.row.type, scope.row.id)">查看</el-button>
                                        </template>

                                    </el-table-column>

                                </el-table>
                                <el-divider></el-divider>

                                <el-pagination
                                        background
                                        @size-change="handleSizeChangeRejectVersion"
                                        @current-change="handleCurrentChangeRejectVersion"
                                        :current-page="currentPageRejectVersion"
                                        :page-sizes="[10, 20, 30, 40]"
                                        :page-size="PageSizeRejectVersion"
                                        layout="total, sizes, prev, pager, next, jumper"
                                        :total="totalRejectVersion"
                                        class="flexRowCenter"
                                >
                                </el-pagination>
                            </div>
                        </div>
                    </el-tab-pane>
                </el-tabs>
        </div>
        `,
    data() {
        return {

            //未处理版本审核
            searchInputWaitVersion:"", //版本搜索
            waitVersionTableData:[],// 表格数据
            currentPageWaitVersion:1, //当前页
            PageSizeWaitVersion:20, //当前页大小
            totalWaitVersion:0, //总数
            dialogVersionComp:false, //版本对比对话框显示控制
            versionCompData:{}, //版本对比数据
            versionCompChangedField:{}, //版本对比数据中修改项


            //接受版本审核
            searchInputAcceptVersion:"", //版本搜索
            acceptVersionTableData:[],// 表格数据
            currentPageAcceptVersion:1, //当前页
            PageSizeAcceptVersion:20, //当前页大小
            totalAcceptVersion:0, //总数

            //拒绝版本审核
            searchInputRejectVersion:"", //版本搜索
            rejectVersionTableData:[],// 表格数据
            currentPageRejectVersion:1, //当前页
            PageSizeRejectVersion:20, //当前页大小
            totalRejectVersion:0, //总数
        }
    },
    mounted(){
        this.getWaitVersionList()
    },
    methods: {
        //版本审核管理界面================================================

        changeTabs(tab){
            if(tab.index==="0"){
                this.getWaitVersionList()
            }else if(tab.index==="1"){
                this.getAcceptVersionList()
            }else{
                this.getRejectVersionList()
            }
        },

        //未处理部分
        //查询未处理版本
        getWaitVersionList(){
            axios.post('/version/versionList/uncheck/All', {
                "asc": false,
                "page": this.currentPageWaitVersion,
                "pageSize": this.PageSizeWaitVersion,
                "searchText": this.searchInputWaitVersion,
                "sortField": "submitTime",
            })
                .then(response=> {
                    console.log(response)
                    this.waitVersionTableData=response.data.data.content
                    this.totalWaitVersion=response.data.data.count
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


        // //查看某次审核，对比 旧
        // viewWaitVersion(versionId){
        //     this.dialogVersionComp=true
        //     axios.get("/version/detail/"+versionId)
        //         .then(response=> {
        //             this.versionCompData=response.data.data
        //             this.versionCompChangedField=this.versionCompData.changedField
        //             console.log(this.versionCompChangedField);
        //         })
        //         .catch(function (error) {
        //             console.log(error);
        //         });
        //     console.log(versionId)
        //
        // },
        // acceptVersion(){
        //
        // },
        // rejectVersion(){
        //
        // },


        viewVersionById(type,versionId){
            let versionViewUrl="/version/versionCompare/"+versionId
            window.open(versionViewUrl, '_blank')
        },

        acceptVersionById(versionId){
            this.$confirm('接受该版本, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.get("/version/accept/"+versionId)
                    .then(res=> {
                        this.$message({
                            type: 'success',
                            message: '接受成功!'
                        });
                        this.getWaitVersionList()
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

        },

        rejectVersionById(versionId){
            this.$confirm('拒接该版本, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.get("/version/reject/"+versionId)
                    .then(res=> {
                        this.$message({
                            type: 'success',
                            message: '拒绝成功!'
                        });
                        // this.getVersionList(this.versionUrl)
                        this.getWaitVersionList()
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

        },
        versionCompareShow(){
            this.dialogVersionComp = false
        },

        handleSizeChangeWaitVersion(val){
            this.PageSizeWaitVersion=val;
            this.getWaitVersionList();
        },
        handleCurrentChangeWaitVersion(val){
            this.currentPageWaitVersion=val;
            this.getWaitVersionList();
        },

        //接受部分
        //查询接受版本
        getAcceptVersionList(){
            axios.post('/version/versionList/accepted/All', {
                "asc": false,
                "page": this.currentPageAcceptVersion,
                "pageSize": this.PageSizeAcceptVersion,
                "searchText": this.searchInputAcceptVersion,
                "sortField": "submitTime",
            })
                .then(response=> {
                    this.acceptVersionTableData=response.data.data.content
                    this.totalAcceptVersion=response.data.data.count
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        handleSizeChangeAcceptVersion(val){
            this.PageSizeAcceptVersion=val;
            this.getAcceptVersionList();
        },
        handleCurrentChangeAcceptVersion(val){
            this.currentPageAcceptVersion=val;
            this.getAcceptVersionList();

        },

        //拒绝部分
        //查询拒绝版本
        getRejectVersionList(){
            axios.post('/version/versionList/rejected/All', {
                "asc": false,
                "page": this.currentPageRejectVersion,
                "pageSize": this.PageSizeRejectVersion,
                "searchText": this.searchInputRejectVersion,
                "sortField": "submitTime",
            })
                .then(response=> {
                    console.log(response)
                    this.rejectVersionTableData=response.data.data.content
                    this.totalRejectVersion=response.data.data.count
                    console.log(this.rejectVersionTableData)
                })
                .catch(function (error) {
                    console.log(error);
                });
        },


        viewRejectVersion(){

        },

        handleSizeChangeRejectVersion(val){
            this.PageSizeRejectVersion=val;
            this.getRejectVersionList();
        },
        handleCurrentChangeRejectVersion(val){
            this.currentPageRejectVersion=val;
            this.getRejectVersionList();
        },
    }
})