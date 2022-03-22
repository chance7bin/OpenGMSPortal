export var ItemTemplate = Vue.extend({
    template: `
    <div>
            <div class="flexRowBetween" >
<!--               左侧类型选择框-->
                    <el-card class="box-card" style="min-width: 260px;  height: fit-content;">
                        <div slot="header" class="clearfix">
                            <span>Data item classification</span>
                        </div>
                        <el-tree 
                            ref="itemRef"
                            :data="geoItemMenuData" 
                            :props="defaultPropsGeoItem" 
                            @node-click="handleNodeClickGeoItem" 
                            highlight-current="true"
                            node-key="id"
                            default-expand-all 
                           ></el-tree>
                    </el-card>
<!--                            右侧内容-->
                <div class="flexColumnStart" style="width: 70vw">
                    <div class="geoSubTopMenu ">
                        <div class="flexRowBetween">
                            <el-input
                                    placeholder="请输入内容"
                                    prefix-icon="el-icon-search"
                                    v-model="searchInputGeo"
                                    @keyup.enter.native="getGeoItemList()"
                            >
                            </el-input>
                            &nbsp;&nbsp;
                            <el-button  type="success" icon="el-icon-search" @click="getGeoItemList()" >搜索</el-button>
                        </div>
                    </div>
                    <div class="geoItemMainContent" >
                        <el-table
                                ref="geoItemTable"
                                :data="geoItemTableData"
                                stripe
                                style="width: 100%"
                                height="75vh"
                                :default-sort="{ prop: 'viewCount', order: 'descending' }"
                        >

                            <el-table-column sortable prop="name" label="名称" show-overflow-tooltip width="400px">
                            </el-table-column>

                            <el-table-column prop="author" label="贡献者" show-overflow-tooltip min-width="150px">
                            </el-table-column>
                            <el-table-column sortable prop="viewCount" label="查看数"  min-width="100px" >
                            </el-table-column>

                            <el-table-column  label="操作" min-width="300px">
                                <template slot-scope="scope">
                                    <el-button
                                            v-if="scope.row.status==='Public'"
                                            size="mini"
                                            type="success"
                                            icon="el-icon-unlock"
                                            @click="changeStatus(scope.row)">公开-状态切换</el-button>
                                    <el-button
                                            v-if="scope.row.status==='Private'"
                                            size="mini"
                                            type="info"
                                            icon="el-icon-lock"
                                            @click="changeStatus(scope.row)">私有-状态切换</el-button>
                                    <el-button
                                            size="mini"
                                            type="primary"
                                            icon="el-icon-user"
                                            @click="openDialog(scope.row)">管理员设置</el-button>
                                </template>

                            </el-table-column>
                        </el-table>
                        <el-divider></el-divider>
 
                        <el-pagination
                                background
                                @size-change="handleSizeChangeGeo"
                                @current-change="handleCurrentChangeGeo"
                                :current-page="currentPageGeo"
                                :page-sizes="[10, 20, 30, 40]"
                                :page-size="PageSizeGeo"
                                layout="total, sizes, prev, pager, next, jumper"
                                :total="totalGeo"
                                class="flexRowCenter"
                        >
                        </el-pagination>
                    </div>
                    
<!--                    管理员设置页面-->
                    <el-dialog title="管理员设置" :visible.sync="adminSelectDialog">
                         <div class="flexRowBetween">
                             <div style="width: 70%" >
                                 <div class="flexRowBetween">
                                    <el-input
                                        placeholder="请输入名字"
                                        prefix-icon="el-icon-search"
                                        v-model="searchUserInput"
                                        @keyup.enter.native="getUserList()"
                                    >
                                    </el-input>
                                    &nbsp;&nbsp;
                                    <el-button  type="success" icon="el-icon-search" @click="getUserList()" >搜索</el-button>
                                </div>
                                <el-table
                                    :data="userTable"
                                    stripe
                                    max-height="245"
                                    
                                    style="width: 100%; margin-top: 5px; "
                                    >
                                    <el-table-column prop="name" label="姓名" > </el-table-column>
                                    <el-table-column prop="email" label="邮箱"> </el-table-column>
                                    <el-table-column label="操作" width="80%">
                                        <template  slot-scope="scope">
                                          <el-button
                                            size="mini"
                                            type="primary"
                                            @click="addAdmin(scope.row)">添加</el-button>                                               
                                        </template>
                                    </el-table-column>
                                </el-table>
                            <el-pagination
                                layout="total,prev, pager, next"
                                :total="userTotal"
                                @current-change="handleUserCurrentChange"
                                :current-page="userNowPage"
                                :page-size="10"
                                class="flexRowCenter"
                            >   
                            </el-pagination>                                 
                            </div>
                            
                            <div style="width: 25%; ">
                                <h1>当前管理员：</h1>
                                <div style="overflow: auto;height: 245px;">
                                    <div :v-if="admins===[]">    
                                        该条目暂无管理员
                                    </div>
                                    <el-tag
                                      v-for="tag in admins"
                                      :key="tag.name"
                                      closable
                                      type=""
                                      @close="handleTagClose(tag)"
                                      style="margin: 3px"
                                      >
                                      {{tag.name}}
                                    </el-tag>    
                                </div>
                                <div>
                                   <el-button  size="mini"  type="primary" @click="setAdmin()">更新管理员</el-button>                                      
                                </div>  
                            </div>
                        </div>
                    </el-dialog>
                </div>
            </div>
    </div>
    
    `,

    data() {
        return {
            adminSelectDialog:false, //管理员选择对话框显示控制
            userTable:[],//管理员设置dialog中展示的用户
            admins:[], //当前item的管理员名字+邮箱
            currentItemId:"", //当前的item id

            userTotal:0, //用户总数
            userNowPage:1, //当前页
            searchUserInput:"",

            geoItemMenuData:[{
                id:1,
                label: 'Model',
            }, {
                id:2,
                label: 'Data',
                children: [{
                    id:3,
                    label: 'Hub Repository'
                }, {
                    id:4,
                    label: 'Item Repository',
                },{
                    id:5,
                    label:"Method Repository"
                }]
            }, {
                id:6,
                label: 'Community',
                children: [{
                    id:7,
                    label: 'Concept & Semantic',
                }, {
                    id:8,
                    label: 'Spatiotemporal Reference',
                },{
                    id:9,
                    label: 'Data Template',
                },{
                    id:10,
                    label: 'Unit & Metric',
                },]
            }], //条目页左侧菜单
            defaultPropsGeoItem: {
                children: 'children',
                label: 'label'
            },  //条目页菜单属性
            geoItemTableData:[], //条目列表数据
            currentPageGeo:1, //条目列表表格页
            PageSizeGeo:20, //条目列表每页数目
            totalGeo:0, //条目总数
            searchInputGeo:"", //条目表搜索内容
            itemType:"ModelItem", //选择展示的条目类型
        }
    },
    mounted(){
        this.getGeoItemList()
        this.$refs.itemRef.setCurrentKey("1");
    },
    methods: {
        //条目管理菜单
        handleNodeClickGeoItem(data){
            let treeNodeId=data.$treeNodeId
            if(treeNodeId===1){
                this.itemType="ModelItem"
            }else if(treeNodeId===3){
                this.itemType="DataHub"
            }else if(treeNodeId===4){
                this.itemType="DataItem"
            }else if(treeNodeId===5){
                this.itemType="DataMethod"
            }else if(treeNodeId===7){
                this.itemType="Concept"
            }else if(treeNodeId===8){
                this.itemType="SpatialReference"
            }else if(treeNodeId===9){
                this.itemType="Template"
            }else if(treeNodeId===10){
                this.itemType="Unit"
            }
            this.getGeoItemList()
        },

        //获取条目列表
        getGeoItemList(){
            axios.post('/managementSystem/item/info/'+this.itemType, {
                "asc": false,
                "page": this.currentPageGeo,
                "pageSize": this.PageSizeGeo,
                "searchText": this.searchInputGeo,
                "sortField": "viewCount",
                "categoryName": "", //此处不用
                "curQueryField": "name",
            })
                .then(response=> {
                    this.geoItemTableData=response.data.data.itemList
                    this.totalGeo=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //页面切换
        handleSizeChangeGeo(val){
            this.PageSizeGeo=val
            this.getGeoItemList()
        },
        handleCurrentChangeGeo(val){
            this.currentPageGeo=val
            this.getGeoItemList()
        },

        //改变条目状态
        changeStatus(val){
            console.log(val)
            let status=val.status
            if(status==="Public"){
                status="Private"
            }else{
                status="Public"
            }
            axios.post('/managementSystem/item/status/'+this.itemType+'/'+val.id+'/'+status, {
            })
                .then(response=> {
                    this.getGeoItemList()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //打开管理员设置的dialog
        openDialog(val){
            this.adminSelectDialog=true;
            console.log(val)
            this.admins=val.admins
            this.getUserList()
            this.currentItemId=val.id
        },
        //获取用户列表
        getUserList(){
            axios.post('/managementSystem/user/info', {
                "asc": false,
                "page": this.userNowPage,
                "pageSize": 10,
                "searchText": this.searchUserInput,
                "sortField": "createTime"
            })
                .then(response=> {
                    this.userTable=response.data.data.content
                    this.userTotal=response.data.data.totalElements
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //用户页面切换
        handleUserCurrentChange(val){
            this.userNowPage=val
            this.getUserList()
        },

        //添加管理员
        addAdmin(val){
            if(this.admins===null||this.admins===[]||(this.admins!==null&&JSON.stringify(this.admins).indexOf(val.email)===-1)){
                if(this.admins===null){
                    this.admins=[]
                }
                this.admins.push({
                    name:val.accessId,
                    email:val.email
                })
            }else{
                this.$message({
                    message: '该用户已为管理员',
                    type: 'warning'
                });
            }
        },

        //删除管理员名字
        handleTagClose(tag) {
            this.admins.splice(this.admins.indexOf(tag), 1);
        },

        //发送请求，设置管理员
        setAdmin(){
            let adminEmails=this.admins.map((item)=>{
                return item.email
            })

            axios.post('/managementSystem/item/admin/'+this.itemType+'/'+this.currentItemId,
                adminEmails
            )
                .then(response=> {
                    this.adminSelectDialog=false;
                    this.$message({
                        message: '更新设置管理员成功！',
                        type: 'success'
                    });
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
    }
})