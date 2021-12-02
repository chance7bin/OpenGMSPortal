new Vue({
    el: '#manage_home',
    data:{
        serverNodes:[], //服务节点
        serverNodeCoods:[],//服务节点坐标

        resourceCount:[], //资源数量

        pageViewCount:[],//页面访问数量
        userViewCount:[], //用户访问数量

    },
    mounted() {
        this.getServerNodes()

        this.getPageViewCount()

        this.getUserViewCount()
    },
    methods: {
        //获取服务节点数据
        getServerNodes(){
            axios.get("/managementSystem/serverNodes")
                .then(response=> {
                    this.serverNodes=response.data.data.data
                    for(let i=0,len=this.serverNodes.length;i<len;i++){
                        this.serverNodeCoods.push([parseFloat(this.serverNodes[i].geoJson.longitude),parseFloat(this.serverNodes[i].geoJson.latitude)])
                    }
                    this.loadEarth()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //绘制地球
        loadEarth(){
            let myChart = echarts.init(document.getElementById('earthNodes'));
            let ROOT_PATH ='https://cdn.jsdelivr.net/gh/apache/echarts-website@asf-site/examples';

            myChart.setOption({
                backgroundColor: '#081f57',
                globe: {
                    baseTexture: ROOT_PATH + '/data-gl/asset/world.topo.bathy.200401.jpg',
                    heightTexture:
                        ROOT_PATH + '/data-gl/asset/bathymetry_bw_composite_4k.jpg',
                    shading: 'lambert',
                    light: {
                        ambient: {
                            intensity: 1
                        },
                        main: {
                            intensity: 1
                        }
                    },
                    viewControl: {
                        autoRotate: true, // 是否开启视角绕物体的自动旋转查看
                        autoRotateSpeed: 20, //物体自转的速度,单位为角度 / 秒，默认为10 ，也就是36秒转一圈。
                        autoRotateAfterStill: 2, // 在鼠标静止操作后恢复自动旋转的时间间隔,默认 3s
                        rotateSensitivity: 3, // 旋转操作的灵敏度，值越大越灵敏.设置为0后无法旋转。[1, 0]只能横向旋转.[0, 1]只能纵向旋转
                        targetCoord: [118.7778, 32.0617], // 定位到北京
                        // maxDistance: 100,
                        // minDistance: 300
                    }
                },
                series: {
                    type: 'scatter3D',
                    coordinateSystem: 'globe',
                    blendMode: 'lighter',
                    symbolSize: 8, // 点位大小
                    itemStyle: {
                        color: '#f00' ,// 各个点位的颜色设置
                        opacity: 1, // 透明度
                        borderWidth: 1, // 边框宽度
                        borderColor: 'rgba(20,15,2,0.8)' //rgba(180, 31, 107, 0.8)
                    },
                    data: this.serverNodeCoods
                }
            });
        },

        //获取资源数量
        getResourceCount(){
            let itemTypes=["DataItem", "ModelItem", "ConceptualModel", "LogicalModel", "ComputableModel", "Concept", "SpatialReference", "Template", "Unit", "Theme", "DataHub", "DataMethod", "Article", "Project", "Conference", "Version", "Information"]
            itemTypes.map((itemType)=>{
                axios.get("/managementSystem/item/count/"+itemType)
                    .then(response=> {
                        this.resourceCount.push({itemType:response.data.data})
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            })
        },

        //获取页面访问数量
        getPageViewCount(){
            axios.get("/managementSystem/view/page/count")
                .then(response=> {
                    this.pageViewCount=response.data.data
                    this.drawPageViewCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //绘制页面访问图
        drawPageViewCount(){
            let myChart =echarts.init( document.getElementById('pageView'));
            let xData=this.pageViewCount.map((item=>{
                return item.date
            }))
            let yData=this.pageViewCount.map((item=>{
                return item.count
            }))
            console.log(xData,yData)

            myChart.setOption({
                title: {
                    text: '页面访问数量'
                },
                xAxis: {
                    type: 'category',
                    data: xData
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        data: yData,
                        type: 'line',
                        smooth: true
                    }
                ]
            })

        },

        //获取用户访问数量
        getUserViewCount(){
            axios.get("/managementSystem/view/user/count")
                .then(response=> {
                    console.log(response)
                    this.userViewCount=response.data.data
                    this.drawUserViewCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        //绘制用户访问数量
        drawUserViewCount(){
            let myChart =echarts.init( document.getElementById('userView'));
            let xData=this.userViewCount.map((item=>{
                return item.date
            }))
            let yData=this.userViewCount.map((item=>{
                return item.count
            }))
            console.log(xData,yData)

            myChart.setOption({
                title: {
                    text: '用户访问数量'
                },
                xAxis: {
                    type: 'category',
                    data: xData
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        data: yData,
                        type: 'line',
                        smooth: true
                    }
                ]
            })
        },


    },
})