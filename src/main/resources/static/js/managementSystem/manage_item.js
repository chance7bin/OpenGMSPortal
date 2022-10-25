export var ItemTemplate = Vue.extend({
    template: `
    <div>
            <div class="flexRowBetween" >
<!--               左侧类型选择框-->
                    <el-card class="box-card" style="min-width: 260px;  height: fit-content;">
                        <div slot="header" class="clearfix">
                            <span>条目分类</span>
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
                                    placeholder="请输入条目名称"
                                    prefix-icon="el-icon-search"
                                    v-model="searchInputGeo"
                                    @keyup.enter.native="searchItem()"
                            >
                            </el-input>
                            &nbsp;&nbsp;
                            <el-button  type="success" icon="el-icon-search" @click="searchItem()" >搜索</el-button>
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
                                    <el-dropdown split-button type="success" size="mini" @command="changeStatus"">
                                      {{scope.row.status}}
                                      <el-dropdown-menu slot="dropdown">
                                        <el-dropdown-item :command="['Public',scope.row]">公开</el-dropdown-item>
                                        <el-dropdown-item :command="['Private',scope.row]">私有</el-dropdown-item>
                                        <el-dropdown-item :command="['Discoverable',scope.row]">可发现</el-dropdown-item>
                                      </el-dropdown-menu>
                                    </el-dropdown>
                                
<!--                                    <el-button-->
<!--                                            v-if="scope.row.status==='Public'"-->
<!--                                            size="mini"-->
<!--                                            type="success"-->
<!--                                            icon="el-icon-unlock"-->
<!--                                            @click="changeStatus(scope.row)">公开-状态切换</el-button>-->
<!--                                    <el-button-->
<!--                                            v-if="scope.row.status==='Private'"-->
<!--                                            size="mini"-->
<!--                                            type="info"-->
<!--                                            icon="el-icon-lock"-->
<!--                                            @click="changeStatus(scope.row)">私有-状态切换</el-button>-->
                                    <el-button
                                            size="mini"
                                            type="primary"
                                            icon="el-icon-user"
                                            @click="openDialog(scope.row)">管理员设置</el-button>
                                    <el-button
                                            size="mini"
                                            type="danger"
                                            icon="el-icon-delete"
                                            @click="deleteItem(scope.row)">删除</el-button>
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
                                        placeholder="请输入邮箱"
                                        prefix-icon="el-icon-search"
                                        v-model="searchUserInput"
                                        @keyup.enter.native="searchUser()"
                                    >
                                    </el-input>
                                    &nbsp;&nbsp;
                                    <el-button  type="success" icon="el-icon-search" @click="searchUser()" >搜索</el-button>
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
                children: [{
                    id:2,
                    label: 'Model Item'
                }, {
                    id:3,
                    label: 'Conceptual Model'
                }, {
                    id:4,
                    label: 'Logical Model',
                },{
                    id:5,
                    label:"Computable Model"
                }]
            }, {
                id:6,
                label: 'Data',
                children: [{
                    id:7,
                    label: 'Hub Repository'
                }, {
                    id:8,
                    label: 'Item Repository',
                },{
                    id:9,
                    label:"Method Repository"
                }]
            }, {
                id:10,
                label: 'Community',
                children: [{
                    id:11,
                    label: 'Concept & Semantic',
                }, {
                    id:12,
                    label: 'Spatiotemporal Reference',
                },{
                    id:13,
                    label: 'Data Template',
                },{
                    id:14,
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
            let treeNodeId=data.id
            if(treeNodeId===2){
                this.itemType="ModelItem"
            }else if(treeNodeId===3){
                this.itemType="ConceptualModel"
            }else if(treeNodeId===4){
                this.itemType="LogicalModel"
            }else if(treeNodeId===5){
                this.itemType="ComputableModel"
            }else if(treeNodeId===7){
                this.itemType="DataHub"
            }else if(treeNodeId===8){
                this.itemType="DataItem"
            }else if(treeNodeId===9){
                this.itemType="DataMethod"
            }else if(treeNodeId===11){
                this.itemType="Concept"
            }else if(treeNodeId===12){
                this.itemType="SpatialReference"
            }else if(treeNodeId===13){
                this.itemType="Template"
            }else if(treeNodeId===14){
                this.itemType="Unit"
            }
            this.currentPageGeo=1
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

        searchItem(){
          this.currentPageGeo=1;
          this.searchGeoItemList();
        },

        //页面切换
        handleSizeChangeGeo(val){
            this.PageSizeGeo=val
            this.currentPageGeo=1;
            this.getGeoItemList()
        },
        handleCurrentChangeGeo(val){
            this.currentPageGeo=val
            this.getGeoItemList()
        },

        //搜索条目
        searchGeoItemList(){
            this.currentPageGeo=1
            this.getGeoItemList()
        },

        //改变条目状态
        changeStatus(command){
            console.log("command",command)
            let status=command[0]
            let id=command[1]["id"]
            axios.post('/managementSystem/item/status/'+this.itemType+'/'+id+'/'+status, {
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
        deleteItem(val){
            console.log(val)
            console.log(this.itemType)
            let type=""
            if(this.itemType=="ModelItem"){
                type="modelItem"
            }else if(this.itemType=="ConceptualModel"){
                type="conceptualModel"
            }else if(this.itemType=="LogicalModel"){
                type="logicalModel"
            }else if(this.itemType=="ComputableModel"){
                type="computableModel"
            }else if(this.itemType=="DataHub"){
                type="dataHub"
            }else if(this.itemType=="DataItem"){
                type="dataItem"
            }else if(this.itemType=="DataMethod"){
                type="dataMethod"
            }else if(this.itemType=="Concept"){
                type="concept"
            }else if(this.itemType=="SpatialReference"){
                type="spatialReference"
            }else if(this.itemType=="Template"){
                type="template"
            }else if(this.itemType=="Unit"){
                type="unit"
            }


            this.$confirm('删除该条目, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.delete("/"+type+'/'+val.id)
                    .then( response=> {
                        console.log(response);
                        this.getGeoItemList()
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
        searchUser(){
          this.userNowPage=1;
          this.getUserList();
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