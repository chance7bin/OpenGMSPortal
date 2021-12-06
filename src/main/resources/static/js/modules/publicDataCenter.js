var DATACONTAINER = 'https://geomodeling.njnu.edu.cn/dataTransferServer'
Vue.component("publicDataCenter",
    {
        template: '#publicDataCenter',
        props: {
            singleChoice: {
                type: Boolean,
                default: false
            },
        },
        data() {
            return {
                dataList: [
                    {
                        name: "pres",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/43a521d1-0356-46e8-b060-40477f7b0d9d",
                        info: "GBHM模型 inputPresData 气压（nc文件，zip格式直接压缩）"
                    }, {
                        name: "rain",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/764f727a-7a7d-41f1-aefa-0f9fa019ad20",
                        info: "GBHM模型 inputRainData 降雨（nc文件，zip格式直接压缩）"
                    }, {
                        name: "rhum",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/0dbdfb7d-8256-4724-9ea2-63635cea5cc6",
                        info: "GBHM模型 inputRhumData 相对湿度（nc文件，zip格式直接压缩）"
                    }, {
                        name: "sun",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/6faaf200-4772-4c9f-9b7c-108091a8e5d6",
                        info: "GBHM模型 inputSunData 日照（nc文件，zip格式直接压缩）"
                    }, {
                        name: "temp",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/f1af7bfb-5f25-4f8b-8048-188e95189618",
                        info: "GBHM模型 inputTempData 气温（nc文件，zip格式直接压缩）"
                    }, {
                        name: "wind",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/e2a0b506-af73-492f-add4-3a8a5d53c308",
                        info: "GBHM模型 inputWindData  风速（nc文件，zip格式直接压缩）"
                    }, {
                        name: "lai",
                        suffix:'zip',
                        address: "http://221.226.60.2:8082/data/3dac36e9-0aa2-41ba-bf72-18cdc70c5672",
                        info: "GBHM模型 inputLaiData 植被（nc文件，zip格式直接压缩）"
                    },{
                        name: "status",
                        suffix:'nc',
                        address: "http://221.226.60.2:8082/data/12ff2dfa-4a64-4ee9-a0e9-4d090cc22abb",
                        info: "GBHM模型 inputStatusData 状态 （nc文件 不用压缩）"
                    }, {
                        name: "time_span_sam",
                        suffix:'xml',
                        address: "http://221.226.60.2:8082/data/02e71f56-3c2d-4e89-a88e-688f796b9bb5",
                        info: "GBHM模型 inputTimeSpanData 模拟时间段"
                    },
                ],

                selectedFile:{},
                dataInfoShow:'',
            }

        },
        computed: {},
        methods: {

            hoverData(data){
                this.$refs.dataInfoContainer.style.boxShadow = '0 6px 14px 0 rgba(0,0,0,.130), 0 1.22px 3.5px 0 rgba(0,0,0,.108)'
                this.dataInfoShow = data.info
            },

            leaveData(){
                this.$refs.dataInfoContainer.style.boxShadow = ''
                this.dataInfoShow = ''
                if(this.selectedFile.address!=undefined&&this.selectedFile.address!==''){
                    this.dataInfoShow = this.selectedFile.info
                }
            },

            singleClick($event, eval){
                this.$refs.dataInfoContainer.style.boxShadow = '0 6px 14px 0 rgba(0,0,0,.130), 0 1.22px 3.5px 0 rgba(0,0,0,.108)'

                this.getid(eval)
            },

            isSelected(item){
                if(this.selectedFile.address===item.address)
                    return true
                else
                    return false
            },

            getid( eval) {
                // target.className = "el-card dataitemisol clickdataitem"

                //再次点击取消选择
                if (this.isSelected(eval)) {
                    this.selectedFile = {}

                    this.removeFileFromFatherModule(eval)
                    // for (var i = 0; i < this.downloadDataSetName.length; i++) {
                    //     if (this.downloadDataSetName[i].name === eval.name&&this.downloadDataSetName[i].suffix === eval.suffix) {
                    //         //删除
                    //         this.downloadDataSetName.splice(i, 1)
                    //         console.log(this.downloadDataSetName)
                    //         break
                    //     }
                    // }

                } else {
                    if(this.singleChoice){//如果是单选，则先把其他清空
                        this.selectedFile = eval
                    }
                    // this.mutiSelect(eval)

                    this.selectFileToFatherModule(eval)
                }

            },

            selectFileToFatherModule(eval) {//将选中文件加入父组件数据中
                if(eval.address.indexOf('http')==-1){
                    eval.address = DATACONTAINER + eval.address
                }
                this.$emit('com-selectfile', eval)
            },

            removeFileFromFatherModule(eval) {
                this.$emit('com-removefile', eval)
            },



        },
        created() {},
        mounted() {}
    }
)