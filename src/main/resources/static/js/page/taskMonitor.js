new Vue({
    el:"#app",
    props:['htmlJson'],

    components:{
    },

    data(){
        return{
            currentTaskServer:{
                ip:"172.21.213.105",
                port:"27017"
            },

            taskServers:[
                {
                    ip:"172.21.213.105",
                    port:"27017"
                },
                {
                    ip:"221.226.60.2",
                    port:"27028"
                }
            ],

            serverInfoType:'task',

            curPage:0,

            tasks:[],
            containers:[],

            serverInfoType:'tasks',

            targetContainerDialog:false,
            targetContainer:{},

            pageOption: {
                paginationShow:false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 10,

                total: 11,
                searchResult: [],
            },
        }
    },

    methods:{
        formatDate(value,callback) {
            const date = new Date(value);
            y = date.getFullYear();
            M = date.getMonth() + 1;
            d = date.getDate();
            H = date.getHours();
            m = date.getMinutes();
            s = date.getSeconds();
            if (M < 10) {
                M = '0' + M;
            }
            if (d < 10) {
                d = '0' + d;
            }
            if (H < 10) {
                H = '0' + H;
            }
            if (m < 10) {
                m = '0' + m;
            }
            if (s < 10) {
                s = '0' + s;
            }

            const t = y + '-' + M + '-' + d + ' ' + H + ':' + m + ':' + s;
            if(callback == null||callback == undefined)
                return t;
            else
                callback(t);
        },

        selectTaskServer(command){

            this.currentTaskServer = command
            // this.pageOption.currentPage=1;
            this.getServerInfo()
        },

        handleClick(tab){
            let type = tab.name
            if(type == 'tasks'){
                this.getServerTasks()
            }else if(type == 'containers'){
                this.getServerContainers()
            }
        },

        getServerInfo(){
            if(this.serverInfoType=='tasks'){
                this.getServerTasks()
            }else if(this.serverInfoType=='containers'){
                this.getServerContainers()
            }
        },

        getServerTasks(){
            let taskServer = {
                ip:this.currentTaskServer.ip,
                port:this.currentTaskServer.port,
                page:this.pageOption.currentPage,
            }
            axios.post('/taskmonitor/tasks',
                taskServer
                ).then(res=>{
                    let code = res.data.code
                    if(code == -1){
                         this.$alert(this.htmlJson.FailedToGetResult, this.htmlJson.Tip, {
                                  type:"warning",
                                  confirmButtonText: 'OK',
                                  callback: ()=>{
                                      return
                                  }
                              }
                          );
                    }else{
                        this.tasks = res.data.data.list
                        this.pageOption.total = res.data.data.total
                    }
                }
            )
        },

        async getServerContainers(){
            let taskServer = {
                ip:this.currentTaskServer.ip,
                port:this.currentTaskServer.port,
                // page:this.curPage,
            }
            await axios.post('/taskmonitor/containers',
                taskServer
            ).then(res=>{
                    let code = res.data.code
                    if(code == -1){
                        this.$alert(this.htmlJson.FailedToGetResult, this.htmlJson.Tip, {
                                type:"warning",
                                confirmButtonText: 'OK',
                                callback: ()=>{
                                    return
                                }
                            }
                        );
                    }else{
                        this.containers = res.data.data
                    }
                }
            )
        },

        async checkServer(task){
            let serverId = task.t_server
            this.curTask = task

            await this.getServerContainers();
            this.targetContainer = {}
            for(let i=0;i<this.containers.length;i++){
                if(this.containers[i]._id.$oid==serverId){
                    this.targetContainer = this.containers[i]
                    this.targetContainerDialog = true
                    return
                }
            }

             this.$alert(this.htmlJson.CannotFindTheContainer, this.htmlJson.Tip, {
                      type:"warning",
                      confirmButtonText: 'OK',
                      callback: ()=>{
                          return
                      }
                  }
              );
        },
        handlePageChange(val) {
            this.pageOption.currentPage = val;

           this.getServerInfo()
        },
    },

    created(){
        this.getServerInfo()
    },

    mounted(){
    }
}
)