var version_compare = new Vue({
    el:"#version_compare",
    data: function (){
        return {
            id:"63551b18-f7ab-43a5-abdd-50a35b4b1e5c",
            versionData:{},
            itemInfo:{},
            relatedModelItemsPage:[],
            currentDetailLanguage:"",
            languageList:[],
            old_field:{},
            new_field:{},
            currentDetailLanguage_old:"",
            currentDetailLanguage_new:"",
            detail_old:"",
            detail_new:"",
            htmlJSON:{
                "ModelContentService":"Model Content & Service",
                "CategoryTags": "Category Tags",},
            activeName: 'Computable Model',
            activeName1: 'Model Item',
            activeName2: 'Reference',
            references_old:{},
            references_new:{},
            oldVerBackground:"",
            newVerBackground: "",
            htmlJSON: {},
            newVerBackground:"",
            //conceptual_model
            activeNameGraph: 'Image',
            graphVisible:"none",
        }
    },
    methods:{
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

        addWaterMark(text) {
            let ctx = document.createElement("canvas");
        // :style="{backgroundImage: `url(${oldVerBackground})`}"
            ctx.style.display = "none";
            let cans = ctx.getContext("2d");
            cans.rotate((-20 * Math.PI) / 180);
            cans.fillStyle = "rgba(17, 17, 17, 0.08)";
            cans.textAlign = "left";
            cans.textBaseline = "Middle";
            cans.font = "30pt Calibri";
            cans.fillText(text, 0, 100);
            cans.save();
            return ctx.toDataURL();
        },
        handleResourceTableData(data){
            let refs = data;
            // refs = refs.replaceAll(/\\/g,"").replaceAll(/\"\{/g,"{").replaceAll(/\}\"/g,"}")
            if (refs != null) {
                let jsonArray = [];
                for (i = 0; i < refs.length; i++) {
                    let json = JSON.parse(refs[i].replaceAll(/\\/g,"").replaceAll(/\"\{/g,"{").replaceAll(/\}\"/g,"}"));
                    json.authors = json.authors.join(", ");
                    jsonArray.push(json);
                }
                return jsonArray;
            }
        },
        getResourceTableData(){
            this.references_old = this.handleResourceTableData(this.references_old)
            this.references_new = this.handleResourceTableData(this.references_new)

        },
        changeDetailLanguage(command){
            if(command.type=='old'){
                this.currentDetailLanguage_old = command.command;
                let result = this.old_field.localizationList.find(item => item.localName==this.currentDetailLanguage_old)
                this.detail_old = result.description
            }
            else{
                this.currentDetailLanguage_new = command.command;
                let result = this.new_field.localizationList.find(item => item.localName==this.currentDetailLanguage_new)
                this.detail_new = result.description
            }

        },
        beforeCommand(cmd,type){
            return {
                'command':cmd,
                'type':type
            }
        },
        getDescription(){},
        translatePage(jsonContent){
            this.htmlJSON = jsonContent;
            let el_breadcrumb = $(".el-breadcrumb");

            for(i=0;i<el_breadcrumb.length;i++){
                let el_breadcrumb_items = el_breadcrumb.eq(i).children(".el-breadcrumb__item");
                let classification_path = this.model_classifications[i];
                for(j=0;j<el_breadcrumb_items.length;j++){
                    let cls = ''
                    if(jsonContent.History === "History") {//英文页面
                        cls = classification_path[j].name;
                    }else{//汉化页面
                        cls = classification_path[j].name_zh;
                    }
                    el_breadcrumb_items.eq(j).children(".el-breadcrumb__inner")[0].innerText = cls
                }

            }

        },
        getData(){
            axios.get('/version/detail/'+this.id)
                .then((res) => {
                    if(res.data.code == 0){
                        this.versionData = res.data.data
                        this.makeItemInfoData()
                    }
                })
        },
        makeItemInfoData(){
            var changedField = this.versionData.changedField


            for(let i in changedField){

                this.old_field[i]=changedField[i].original
                this.new_field[i]=changedField[i].new
            }

            //处理description
            if(this.old_field.localizationList!=null){
                this.currentDetailLanguage_old = this.old_field.localizationList[0].localName
                this.detail_old = this.old_field.localizationList[0].description
                this.currentDetailLanguage_new = this.new_field.localizationList[0].localName
                this.detail_new = this.new_field.localizationList[0].description
                for(let i in this.old_field.localizationList){
                    this.languageList.push({value:this.old_field.localizationList[i].localCode,label:this.old_field.localizationList[i].localName})
                }

            }
        },
        edit(){

        }
    },
    mounted(){
        let idStr = window.location.href.split('/')
        this.id = idStr[idStr.length-1]

        this.getData()
        if(this.references_old!={})
            this.getResourceTableData()

    //    水印背景
        this.oldVerBackground = this.addWaterMark('Old Version')
        this.newVerBackground = this.addWaterMark('New Version')

    },
    created(){
        //首先到缓存中获取userSpaceAll
        this.htmlJSON = this.getStorage("userSpaceAll");
        // this.references_old = references_old
        // this.references_new = references_new
    }
})