// 0. 如果使用模块化机制编程，导入Vue和VueRouter，要调用 Vue.use(VueRouter)

// 1. 定义 (路由) 组件。
// 可以从其他文件 import 进来
import {ModelTemplate} from "/static/js/managementSystem/manage_model.js"
import {UserTemplate} from "/static/js/managementSystem/manage_user.js"
import {ItemTemplate} from "/static/js/managementSystem/manage_item.js"
import {VersionTemplate} from "/static/js/managementSystem/manage_version.js"
import {CommentTemplate} from "/static/js/managementSystem/manage_comment.js"
import {TaskTemplate} from "/static/js/managementSystem/manage_task.js"

// 2. 定义路由
// 每个路由应该映射一个组件。 其中"component" 可以是
// 通过 Vue.extend() 创建的组件构造器，
// 或者，只是一个组件配置对象。
// 我们晚点再讨论嵌套路由。
const routes = [
    {path: '/',redirect:"/modelManage"},
    { path: '/modelManage', component: ModelTemplate,title:"模型管理",icon:"el-icon-menu" },
    { path: '/userManage', component: UserTemplate,title:"用户管理",icon:"el-icon-user-solid"},
    { path: '/itemManage', component: ItemTemplate,title:"条目管理",icon:"el-icon-s-ticket" },
    { path: '/versionManage', component: VersionTemplate ,title:"版本审核",icon:"el-icon-s-claim"},
    { path: '/commentManage', component: CommentTemplate,title:"评论管理",icon:"el-icon-s-comment" },
    { path: '/taskManage', component: TaskTemplate ,title:"任务监控",icon:"el-icon-s-order"},
]

// 3. 创建 router 实例，然后传 `routes` 配置
// 你还可以传别的配置参数, 不过先这么简单着吧。
const router = new VueRouter({
    routes // (缩写) 相当于 routes: routes
})

// 4. 创建和挂载根实例。
// 记得要通过 router 配置参数注入路由，
// 从而让整个应用都有路由功能
const app = new Vue({
    router,
    data:function (){
        return{
            leftNavMenu:routes.slice(1,7),
        }

    }
}).$mount('#manage_system_app')

// 现在，应用已经启动了！