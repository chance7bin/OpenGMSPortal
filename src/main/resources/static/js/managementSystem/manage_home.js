new Vue({
    el: '#manage_home',
    data:{
        serverNodes:[], //服务节点
        serverNodeCoods:[],//服务节点坐标

        resourceCount:[], //资源数量

        pageViewCount:[],//页面访问数量
        userViewCount:[], //用户访问数量
        itemViewCount:[], //条目访问数量

        defaultItemShow:"DataItem",// 默认显示的条目数量

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

        this.dd()

    },
    methods: {
        dd(){
            let chartDom = document.getElementById('del');
            let myChart = echarts.init(chartDom);
            myChart.setOption({
                grid: {
                    show: true,
                    y: '70%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        data: [120, 200, 150, 80, 70, 110, 130],
                        type: 'bar'
                    }
                ]
            })
        },

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
            title: {
                text: '服务节点',
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
                resourceCountData.push({"value":this.resourceCount[i].count,"name":this.resourceCount[i].type})
            }

            console.log(resourceCountData)

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

            //     {
            //     title: {
            //         text: '平台资源数量数量'
            //     },
            //     legend: {
            //         top: 'bottom'
            //     },
            //     series: [
            //         {
            //             name: 'Access From',
            //             type: 'pie',
            //             minAngle: 10,  //设置扇形的最小占比
            //             radius: ['30%', '70%'],
            //             center: ['50%', '50%'],
            //             roseType: 'area',
            //             itemStyle: {
            //                 borderRadius: 10,
            //                 borderColor: '#fff',
            //                 borderWidth: 2
            //             },
            //             label: {
            //                 show: false,
            //                 position: 'center'
            //             },
            //             emphasis: {
            //                 label: {
            //                     show: true,
            //                     fontSize: '40',
            //                     fontWeight: 'bold'
            //                 }
            //             },
            //             labelLine: {
            //                 show: true
            //             },
            //             data: resourceCountData
            //         }
            //     ]
            // }


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
            console.log(xData,yData)

            myChart.setOption({
                title: {
                    text: '页面访问数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
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
                    text: '用户访问数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
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
                        smooth: true
                    }
                ]
            })
        },

        //条目切换
        handleItemSelect(val){
            console.log(val)
            this.defaultItemShow=val
            this.getItemViewCount(this.defaultItemShow)
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
                    console.log(response)
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
            console.log(xData,yData)

            myChart.setOption({
                title: {
                    text: '条目访问数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                grid: {
                    // show: true,
                    top:"20%",
                    bottom:'20%',
                },

                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function (value) {
                        let txt=""
                        txt+=xData[value[0].dataIndex]
                        console.log(value)
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
                    data: [1,2,3,4,5],
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
                        type: 'bar',
                        label: {
                            show: true,
                            fontSize: '10',
                            position: 'top',
                            textStyle: {
                                color: '#ffffff'
                            }

                        },
                    }
                ],

            })
        },

        //服务调用切换
        handleServiceUseType(){
            this.getServiceUseCount()
        },

        //获取服务调用数量
        getServiceUseCount(){
            axios.post("/managementSystem/service/"+this.serviceUseType+"/invokeCount",{
                "asc": false,
                "curQueryField": "name",
                "page": 1,
                "pageSize": 5,
                "sortField": "viewCount"
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
                return item.viewCount
            }))
            console.log(xData,yData)

            myChart.setOption({

                title: {
                    text: '服务调用数量',
                    left: 'center',
                    textStyle: {
                        color: '#ffffff'
                    }
                },
                grid: {
                    // show: true,
                    top:"20%",
                    bottom:'10%',
                },

                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function (value) {
                        let txt=""
                        txt+=xData[value[0].dataIndex]
                        console.log(value)
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

                    }
                },
                xAxis: {
                    type: 'category',
                    data: [1,2,3,4,5],
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
                ],

            })
        },

        //获取用户数量
        getUserCount(){
            axios.get("/managementSystem/user/count")
                .then(response=> {
                    console.log(response)
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