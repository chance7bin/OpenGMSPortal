new Vue({
    el: '#manage_home',
    data:{

        serverNodes:[], //服务节点
        serverNodeCoords:[],//服务节点坐标

        resourceCount:[], //资源数量

        pageViewCount:[],//页面访问数量
        userViewCount:[], //用户访问数量
        itemViewCount:[], //条目访问数量

        defaultItemShow:"ModelItem",// 默认显示的条目数量

        itemIndex:0,
        itemNameEn:["ModelItem","DataItem","DataHub","DataMethod","Concept","SpatialReference","Template","Unit"],
        itemNameZh:["模型条目","数据条目","数据仓库","数据方法","概念","空间参考","数据格式","单位"],

        serviceUseType:"computableModel", //默认展示模型服务调用
        serviceUseCount:[], //服务调用数量

        userCount:0, //用户数量
    },
    mounted() {

        this.getResourceCount()

        this.getServerNodes()

        this.getPageViewCount()

        this.getUserViewCount()

        this.getItemViewCount(this.defaultItemShow)

        this.getServiceUseCount()

        this.getUserCount()

        this.autoChangeServiceCount()
        this.autoChangeItemCount()

    },
    methods: {

        //定时切换服务调用
        autoChangeServiceCount(){
            setInterval(()=>{
                if(this.serviceUseType==="computableModel"){
                    this.serviceUseType="dataMethod"
                }else {
                    this.serviceUseType="computableModel"
                }
                this.getServiceUseCount()

            },2000)
        },

        //定时切换条目访问
        autoChangeItemCount(){
            setInterval(()=>{
                this.itemIndex=(this.itemIndex+1)%8
                this.getItemViewCount(this.itemNameEn[this.itemIndex])
            },2000)
        },

        //获取服务节点数据
        getServerNodes(){
            axios.get("/managementSystem/serverNodes")
                .then(response=> {
                    this.serverNodes=response.data.data
                    let nodes=[]
                    for(let i=0,len=this.serverNodes.length;i<len;i++){
                        let cityName=this.serverNodes[i].geoJson.city
                        let j=0,len2=nodes.length
                        for(;j<len2;j++) {
                            if (cityName === nodes[j].name) {
                                break
                            }
                        }
                        if(j===len2){
                            nodes.push({
                                name:cityName,
                                count:1,
                                value:[parseFloat(this.serverNodes[i].geoJson.longitude),parseFloat(this.serverNodes[i].geoJson.latitude)]
                            })
                        }else{
                            nodes[j].count++
                        }
                    }
                    this.serverNodeCoords=nodes
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
            title: {
                text: '服务节点分布',
                    left: 'center',
                textStyle: {
                    color: '#ffffff'
                }
            },
                // backgroundColor: 'rgba(255,255,255,0)',
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
                        distance: 200,
                        autoRotate: true, // 是否开启视角绕物体的自动旋转查看
                        autoRotateSpeed: 20, //物体自转的速度,单位为角度 / 秒，默认为10 ，也就是36秒转一圈。
                        autoRotateAfterStill: 2, // 在鼠标静止操作后恢复自动旋转的时间间隔,默认 3s
                        rotateSensitivity: 3, // 旋转操作的灵敏度，值越大越灵敏.设置为0后无法旋转。[1, 0]只能横向旋转.[0, 1]只能纵向旋转
                        targetCoord: [118.7778, 32.0617], // 定位到北京
                    }
                },

                series: {
                    type: 'scatter3D',
                    coordinateSystem: 'globe',
                    // blendMode: 'lighter',
                    symbolSize: 10, // 点位大小
                    itemStyle: {
                        color: 'rgba(245,5,5,0.99)' ,// 各个点位的颜色设置
                        opacity: 1, // 透明度
                        borderWidth: 1, // 边框宽度
                        borderColor: 'rgba(20,15,2,0)' //rgba(180, 31, 107, 0.8)
                    },
                    emphasis:{
                        itemStyle:{
                            color:'green',			//鼠标移到点上的颜色变化
                            opacity:1,				//不透明度
                            borderWidth:0,			//图像描边宽度
                            borderColor:'#fff' 		//图形描边颜色
                        },
                        label:{
                            show:true,				//鼠标移动到点上是否显示标签
                            distance: 20,			//标签与点的距离
                            position:'left',      	//标签位置
                            textStyle:{
                                color:'white', 		//文字颜色
                                borderWidth:0,  	//标签上边框宽度
                                borderColor:'white',//边框颜色
                                fontFamily:'宋体',	//标签字体
                                fontSize:14,		//字体大小
                                fontWeight:'normal'	//是否加粗
                            },
                            formatter: function (val) {
                                return val.data.name+":"+val.data.count
                            },
                        }
                    },

                    data: this.serverNodeCoords
                }
            });
        },

        //获取资源数量
        getResourceCount(){

            axios.get("/managementSystem/item/count/all")
                .then(response=> {
                    this.resourceCount=response.data.data
                    this.drawResourceCount()
                })
                .catch(function (error) {
                    console.log(error);
                });

        },
        //绘制资源数量
        drawResourceCount(){
            let myChart =echarts.init( document.getElementById('resourceCount'));

            let resourceCountData=[]

            for(let i=0,len=this.resourceCount.length;i<len;i++){
                resourceCountData.push({"value":this.resourceCount[i].count,"name":this.itemNameZh[i]})
            }

            myChart.setOption(

                {
                    title: {
                        text: '平台资源数量数量',
                        left: 'center',
                        textStyle: {
                            color: '#ffffff'
                        }
                    },
                    tooltip: {
                        trigger: 'item'
                    },
                    legend: {
                        top: 'bottom',
                        // textStyle: {
                        //     fontSize: 10,
                        // },
                        itemHeight: 10,
                        left:"center",
                        formatter: function (name) {
                            return '{a|' +  name + '}'
                        },
                        textStyle: {
                            fontSize: 10,
                            backgroundColor: "transparent", // 文字块背景色，一定要加上，否则对齐不会生效
                            rich: {
                                a: {
                                    width:60
                                },
                            },
                            color: '#ffffff'
                        },
                    },
                    series: [
                        {
                            name: '资源数量',
                            type: 'pie',
                            radius: '50%',
                            minAngle: 10,  //设置扇形的最小占比
                            avoidLabelOverlap: false,
                            label: {
                                show: true,
                                fontSize: '10',
                                textStyle: {
                                    color: '#ffffff'
                                },
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    fontSize: '13',
                                    fontWeight: 'bold',
                                },
                                itemStyle: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            },
                            data: resourceCountData
                        }
                    ]
                }
            )


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


            myChart.setOption({
                title: {
                    text: '页面访问数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                grid: {
                    top:"24%",
                    bottom:'20%',
                    x:"13%"
                },

                xAxis: {
                    type: 'category',
                    data: xData,
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    // scale: true,
                    splitNumber:3,
                    axisLabel: {
                        // show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
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
                    this.userViewCount=response.data.data
                    this.drawUserViewCount()
                })
                .catch(function (error) {
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

            myChart.setOption({
                title: {
                    text: '用户访问数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                grid: {
                    top:"24%",
                    bottom:'20%',
                    x:"13%"
                },

                xAxis: {
                    type: 'category',
                    data: xData,
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                series: [
                    {
                        data: yData,
                        type: 'line',
                        smooth: true,
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'#23ec1b'
                                }
                            }
                        }
                    }
                ]
            })
        },

        //获取条目访问数量
        getItemViewCount(itemType){
            axios.post("/managementSystem/view/item/sortByViewCount/"+itemType,{
                "asc": false,
                "curQueryField": "name",
                "page": 1,
                "pageSize": 5,
                "sortField": "viewCount"
            })
                .then(response=> {
                    this.itemViewCount=response.data.data.itemList
                    this.drawItemViewCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //绘制条目访问数量
        drawItemViewCount(){
            let myChart =echarts.init( document.getElementById('itemView'));

            let xData=this.itemViewCount.map((item=>{
                return item.name
            }))
            let yData=this.itemViewCount.map((item=>{
                return item.viewCount
            }))

            // 为了柱状图每个柱子独立显示
            let mySeries=this.itemViewCount.map((item=>{
                return {
                    data: [item.viewCount],
                    name: item.name,
                    type: 'bar',
                    label: {
                        show: true,
                        fontSize: '10',
                        position: 'top',
                        textStyle: {
                            color: '#ffffff'
                        },
                    },
                }
            }))
            myChart.setOption({
                title: {
                    text: '条目访问数量-'+this.itemNameZh[this.itemIndex],
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    },
                    top:"1%",
                },
                grid: {
                    // show: true,
                    top:"15%",
                    bottom:'35%',
                    x:"13%"
                },
                legend: {
                    show: true,
                    top: 'bottom',
                    itemHeight: 10,
                    formatter: function (name) {
                        return '{a|' +  name + '}'
                    },
                    textStyle: {
                        fontSize: 10,
                        backgroundColor: "transparent", // 文字块背景色，一定要加上，否则对齐不会生效
                        rich: {
                            a: {
                                width:60
                            },
                        },
                        color: '#ffffff'
                    },
                },

                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function (value) {
                        let txt=""
                        txt+=xData[value[0].dataIndex]
                        return txt;
                    },
                    //调整提示框位置
                    position: function(point, params, dom, rect, size){
                        //其中point为当前鼠标的位置，size中有两个属性：viewSize和contentSize，分别为外层div和tooltip提示框的大小
                        let x = point[0];//
                        let y = point[1];
                        let viewWidth = size.viewSize[0];
                        let viewHeight = size.viewSize[1];
                        let boxWidth = size.contentSize[0];
                        let boxHeight = size.contentSize[1];
                        let posX = 0;//x坐标位置
                        let posY = 0;//y坐标位置

                        if(x<boxWidth){//左边放不开
                            posX = 5;
                        }else{//左边放的下
                            posX = x-boxWidth;
                        }

                        if(y<boxHeight){//上边放不开
                            posY = 5;
                        }else{//上边放得下
                            posY = y-boxHeight;
                        }

                        return [posX,posY];

                    },

                },
                xAxis: {
                    type: 'category',
                    data: [],
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                series:mySeries,

            })
        },

        //获取服务调用数量
        getServiceUseCount(){
            axios.post("/managementSystem/service/"+this.serviceUseType+"/invokeCount",{
                "asc": false,
                "curQueryField": "name",
                "page": 1,
                "pageSize": 5,
                "sortField": "invokeCount"
            })
                .then(response=> {
                    this.serviceUseCount=response.data.data.itemList
                    this.drawServiceUseCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //绘制服务调用数量
        drawServiceUseCount(){
            let myChart =echarts.init( document.getElementById('serviceUse'));
            let xData=this.serviceUseCount.map((item=>{
                return item.name
            }))
            let yData=this.serviceUseCount.map((item=>{
                return item.invokeCount
            }))

            let serverName=this.defaultItemShow==="ModelItem"?"模型服务":"数据服务"

            // 为了柱状图每个柱子独立显示
            let mySeries=this.serviceUseCount.map((item=>{
                return {
                    data: [item.invokeCount],
                    name: item.name,
                    type: 'bar',
                    label: {
                        show: true,
                        fontSize: '10',
                        position: 'top',
                        textStyle: {
                            color: '#ffffff'
                        },
                    },
                }
            }))

            myChart.setOption({

                title: {
                    text: '服务调用数量-'+serverName,
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                grid: {
                    // show: true,
                    top:"15%",
                    bottom:'35%',
                    x:"10%"
                },

                legend: {
                    show: true,
                    top: 'bottom',
                    itemHeight: 10,
                    formatter: function (name) {
                        return '{a|' +  name + '}'
                    },
                    textStyle: {
                        fontSize: 10,
                        backgroundColor: "transparent", // 文字块背景色，一定要加上，否则对齐不会生效
                        rich: {
                            a: {
                                width:60
                            },
                        },
                        color: '#ffffff'
                    },
                },

                tooltip: {
                    trigger: 'axis',
                },
                xAxis: {
                    type: 'category',
                    data: [],
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
                },
                series: mySeries,
            })
        },

        //获取用户数量
        getUserCount(){
            axios.get("/managementSystem/user/count")
                .then(response=> {
                    this.userCount=response.data.data
                    this.drawUserCount()
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        //绘制用户数量
        drawUserCount() {
            let myChart = echarts.init(document.getElementById('userCount'));
            myChart.setOption({
                title: {
                    text: '用户数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                    series: [
                        {
                            type: 'gauge',
                            min: 0,
                            max: 2000,
                            progress: {
                                show: true,
                                width: 18
                            },
                            axisLine: {
                                lineStyle: {
                                    width: 18
                                }
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                length: 10,
                                lineStyle: {
                                    width: 2,
                                    color: '#ffffff'
                                }
                            },
                            axisLabel: {
                                distance: 25,
                                color: '#999',
                                fontSize: 9,
                                textStyle: {
                                    color: '#ffffff'
                                }
                            },
                            anchor: {
                                show: true,
                                showAbove: true,
                                size: 20,
                                itemStyle: {
                                    borderWidth: 10
                                }
                            },

                            detail: {
                                valueAnimation: true,
                                fontSize: 35,
                                offsetCenter: [0, '70%'],
                                textStyle: {
                                    color: '#ffffff'
                                }
                            },
                            data: [
                                {
                                    value: this.userCount
                                }
                            ]
                        }]
                })
        }

    },
})