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

            confirmLogin(){
                window.sessionStorage.setItem("history", window.location.href);
                this.$confirm('<div style=\'font-size: 18px\'>This function requires an account, <br/>please login first.</div>', 'Tip', {
                    dangerouslyUseHTMLString: true,
                    confirmButtonText: 'Log In',
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
                                    this.clsStr2 += this.treeData[j].children[k].label;
                                    if (i != this.cls.length - 1) {
                                        this.clsStr2 += ", ";
                                    }
                                    break;
                                }
                            }
                            else{
                                for(x=0;x<children.length;x++){
                                    if (this.cls[i] == children[x].oid) {
                                        ids.push(children[x].id);
                                        this.clsStr2 += children[x].label;
                                        if (i != this.cls.length - 1) {
                                            this.clsStr2 += ", ";
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