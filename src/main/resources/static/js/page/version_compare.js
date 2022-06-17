var version_compare = new Vue({
    el:"#version_compare",
    data: function (){
        return {
            id:"63551b18-f7ab-43a5-abdd-50a35b4b1e5c",
            versionData:{},
            itemInfo:{},
            relatedModelItemsPage:[],
            currentDetailLanguage:"",
            languageList:[
                { value: 'af', label: 'Afrikaans' },
                { value: 'sq', label: 'Albanian' },
                { value: 'ar', label: 'Arabic' },
                { value: 'hy', label: 'Armenian' },
                { value: 'az', label: 'Azeri' },
                { value: 'eu', label: 'Basque' },
                { value: 'be', label: 'Belarusian' },
                { value: 'bg', label: 'Bulgarian' },
                { value: 'ca', label: 'Catalan' },
                { value: 'zh', label: 'Chinese' },
                { value: 'hr', label: 'Croatian' },
                { value: 'cs', label: 'Czech' },
                { value: 'da', label: 'Danish' },
                { value: 'dv', label: 'Divehi' },
                { value: 'nl', label: 'Dutch' },
                { value: 'en', label: 'English' },
                { value: 'eo', label: 'Esperanto' },
                { value: 'et', label: 'Estonian' },
                { value: 'mk', label: 'FYRO Macedonian' },
                { value: 'fo', label: 'Faroese' },
                { value: 'fa', label: 'Farsi' },
                { value: 'fi', label: 'Finnish' },
                { value: 'fr', label: 'French' },
                { value: 'gl', label: 'Galician' },
                { value: 'ka', label: 'Georgian' },
                { value: 'de', label: 'German' },
                { value: 'el', label: 'Greek' },
                { value: 'gu', label: 'Gujarati' },
                { value: 'he', label: 'Hebrew' },
                { value: 'hi', label: 'Hindi' },
                { value: 'hu', label: 'Hungarian' },
                { value: 'is', label: 'Icelandic' },
                { value: 'id', label: 'Indonesian' },
                { value: 'it', label: 'Italian' },
                { value: 'ja', label: 'Japanese' },
                { value: 'kn', label: 'Kannada' },
                { value: 'kk', label: 'Kazakh' },
                { value: 'kok', label: 'Konkani' },
                { value: 'ko', label: 'Korean' },
                { value: 'ky', label: 'Kyrgyz' },
                { value: 'lv', label: 'Latvian' },
                { value: 'lt', label: 'Lithuanian' },
                { value: 'ms', label: 'Malay' },
                { value: 'mt', label: 'Maltese' },
                { value: 'mi', label: 'Maori' },
                { value: 'mr', label: 'Marathi' },
                { value: 'mn', label: 'Mongolian' },
                { value: 'ns', label: 'Northern Sotho' },
                { value: 'nb', label: 'Norwegian' },
                { value: 'ps', label: 'Pashto' },
                { value: 'pl', label: 'Polish' },
                { value: 'pt', label: 'Portuguese' },
                { value: 'pa', label: 'Punjabi' },
                { value: 'qu', label: 'Quechua' },
                { value: 'ro', label: 'Romanian' },
                { value: 'ru', label: 'Russian' },
                { value: 'se', label: 'Sami' },
                { value: 'sa', label: 'Sanskrit' },
                { value: 'sr', label: 'Serbian' },
                { value: 'sk', label: 'Slovak' },
                { value: 'sl', label: 'Slovenian' },
                { value: 'es', label: 'Spanish' },
                { value: 'sw', label: 'Swahili' },
                { value: 'sv', label: 'Swedish' },
                { value: 'syr', label: 'Syriac' },
                { value: 'tl', label: 'Tagalog' },
                { value: 'ta', label: 'Tamil' },
                { value: 'tt', label: 'Tatar' },
                { value: 'te', label: 'Telugu' },
                { value: 'th', label: 'Thai' },
                { value: 'ts', label: 'Tsonga' },
                { value: 'tn', label: 'Tswana' },
                { value: 'tr', label: 'Turkish' },
                { value: 'uk', label: 'Ukrainian' },
                { value: 'ur', label: 'Urdu' },
                { value: 'uz', label: 'Uzbek' },
                { value: 'vi', label: 'Vietnamese' },
                { value: 'cy', label: 'Welsh' },
                { value: 'xh', label: 'Xhosa' },
                { value: 'zu', label: 'Zulu' },
            ],
        }
    },
    methods:{
        // setRelatedModelItemsPage(){
        //     this.relatedModelItemsPage = [];
        //     let relatedModelItems = this.relation.modelItems;
        //     if(relatedModelItems!=null) {
        //         for (i = 0; i < relatedModelItems.length; i++) {
        //             if (i === this.relationPageSize) break;
        //             this.relatedModelItemsPage.push(relatedModelItems[i]);
        //         }
        //     }
        // },


        //model
        changeDetailLanguage(){},
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
            var content = this.versionData.content
            this.itemInfo_old= JSON.parse(JSON.stringify(content))
        },
        edit(){

        }
    },
    mounted(){
        let idStr = window.location.href.split('/')
        this.id = idStr[idStr.length-1]

        this.getData()


    }
})