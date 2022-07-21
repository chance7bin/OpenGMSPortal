Vue.component("editClassificationModule",
    {
        template: '#editClassificationModule',
        props: {
            classificationSystem: {
                type: String,
                default: 'modelItem'
            },
            defaultExpand:{
                type:Array,
                default: [1,14,19,24]
            }
        },
        data() {
            return {
                cls:[],
                clsStr:"",

                treeData:[],
                defaultProps: {
                    children: 'children',
                    label: 'label'
                },
            }
        },
        computed: {},
        methods: {
            // 获取缓存
            getStorage(key){
                var localStorage = window.localStorage;
                if (localStorage )
                    var v = localStorage.getItem(key);
                if (!v) {
                    return;
                }
                if (v.indexOf('obj-') === 0) {
                    v = v.slice(4);
                    return JSON.parse(v);
                } else if (v.indexOf('str-') === 0) {
                    return v.slice(4);
                }
            },

            confirmLogin(){
                window.sessionStorage.setItem("history", window.location.href);
                const language = this.getStorage("language");

                if (language == "zh-cn"){
                    var loginTip = "This function requires an account, please login first."
                    var login = "Log in"
                    var tip = "Tip"
                }else {
                    var loginTip = "该操作需要一个账户，请先登录."
                    var login = "登录"
                    var tip = "提示"
                }

                this.$confirm('<div style=\'font-size: 18px\'>' + loginTip + '</div>', tip, {
                    dangerouslyUseHTMLString: true,
                    confirmButtonText: login,
                    cancelButtonClass: 'fontsize-15',
                    confirmButtonClass: 'fontsize-15',
                    type: 'info',
                    center: true,
                    showClose: false,
                }).then(() => {
                    window.location.href = "/user/login";
                }).catch(() => {

                });
            },


            insertClassifications(classifications){
                // let urls = {
                //     'modelItem':'/modelItem/getClassification/'
                // };
                this.cls = classifications;

                let ids=[];
                for(i=0;i<this.cls.length;i++){
                    for(j=0;j<2;j++){
                        for(k=0;k<this.treeData[j].children.length;k++){
                            let children=this.treeData[j].children[k].children;
                            if(children==null) {
                                if (this.cls[i] == this.treeData[j].children[k].oid) {
                                    ids.push(this.treeData[j].children[k].id);
                                    this.clsStr += this.treeData[j].children[k].label;
                                    if (i != this.cls.length - 1) {
                                        this.clsStr += ", ";
                                    }
                                    break;
                                }
                            }
                            else{
                                for(x=0;x<children.length;x++){
                                    if (this.cls[i] == children[x].oid) {
                                        ids.push(children[x].id);
                                        this.clsStr += children[x].label;
                                        if (i != this.cls.length - 1) {
                                            this.clsStr += ", ";
                                        }
                                        break;
                                    }
                                }
                            }

                        }
                        if(ids.length-1==i){
                            break;
                        }
                    }
                }

                this.$nextTick(()=>{

                    this.$refs.tree.setCheckedKeys(ids);
                })

            },

            handleCheckChange(data, checked, indeterminate) {
                let checkedNodes = this.$refs.tree.getCheckedNodes()
                let classes = [];
                let str='';
                for (let i = 0; i < checkedNodes.length; i++) {
                    // console.log(checkedNodes[i].children)
                    if(checkedNodes[i].children!=undefined){
                        continue;
                    }

                    classes.push(checkedNodes[i].oid);
                    str+=checkedNodes[i].label;
                    if(i!=checkedNodes.length-1){
                        str+=", ";
                    }
                }
                this.cls=classes;
                this.clsStr=str;

                this.$emit('select-classification',this.cls,this.clsStr)
            },

            handleCheckChange2(data, checked, indeterminate) {
                let checkedNodes = this.$refs.tree.getCheckedNodes()
                let classes = [];
                let str='';
                for (let i = 0; i < checkedNodes.length; i++) {
                    // console.log(checkedNodes[i].children)
                    if(checkedNodes[i].children!=undefined){
                        continue;
                    }

                    classes.push(checkedNodes[i].oid);
                    str+=checkedNodes[i].label;
                    if(i!=checkedNodes.length-1){
                        str+=", ";
                    }
                }
                this.cls=classes;
                this.clsStr=str;

            },

        },
        created() {
            if(this.classificationSystem=='modelItem'){
                this.treeData = window.classificationTree_modelItem
            }

        },
        mounted() {}
    }
)