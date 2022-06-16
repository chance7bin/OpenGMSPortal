/**
 *组件说明：
 *  Attributes:
 *  jsonFile 标识翻译文件名称，无默认值
 *  theme 标识明暗主题，默认light
 *  initialLang 标识初始语言，默认en-us
 *  method:
 *  translate-page
 */
Vue.component("translation-bar",
    {
        template: '#translationBar',
        props: {
            theme:{
                type: String,
                default: 'light',
            },

            jsonFile: {
                type: String,
            },

            initialLang:{
                type: String,
                default: 'en-us',
            },

            withComments:{
                type: Boolean,
                default: false,
            },

            withItemInfoModules:{
                type: Boolean,
                default: false,
            }

        },
        data() {
            return {
                themeStyle:'lightness',
                currentLang:'en-us',
                langList:['en-us','zh-cn'],
            }
        },
        computed: {},
        methods: {
            async transLateTargetPage(){//获取页面内容对应翻译文件，不包含navbar和footer
                if(this.currentLang !== "zh-cn"){
                    //element-ui 切换英文，勿删！
                    if(ELEMENT!=undefined) {
                        ELEMENT.locale(ELEMENT.lang.en)
                    }
                }else{
                    if(ELEMENT!=undefined) {
                        ELEMENT.locale(ELEMENT.lang.zhCN)
                    }
                }
                let content = await this.getLangJson(this.jsonFile)
                this.loadLangContent(content)
            },

            async getLangJson(jsonFile){
                let path = "/static/translation/"+jsonFile+".json"
                let result
                $.ajax({
                    url:path,
                    type:"GET",
                    async:false,
                    dataType:'json',
                    headers:{
                        'Accept':'application/json'
                    },
                    success:((res)=>{
                            result = res
                        })
                    }

                )

                return result
            },

            // 设置缓存
            setStorage(key, value){
                var v = value;
                //是对象转成JSON，不是直接作为值存入内存
                if (typeof v == 'object') {
                    v = JSON.stringify(v);
                    v = 'obj-' + v;
                } else {
                    v = 'str-' + v;
                }
                var localStorage = window.localStorage;
                if (localStorage ) {
                    localStorage .setItem(key, v);
                }
            },

            loadLangContent(content){
                this.$emit('translate-page',content[this.currentLang])
                this.setStorage("userSpaceAll", content[this.currentLang]);
            },

            getClass(lang){
                let classStr = ''
                if(this.theme.indexOf('light')!=-1){
                    this.themeStyle = 'lightness'
                    classStr += 'lightness'
                }else if(this.theme.indexOf('dark')!=-1){
                    this.themeStyle = 'darkness'
                    classStr += 'darkness'
                }

                if(lang == this.currentLang){
                    classStr += ' checked'
                }

                return classStr
            },

            changeLang(lang){
                if(lang==this.currentLang){
                    return
                }else{
                    window.localStorage.setItem("language", lang);
                    this.currentLang = lang
                    this.changeUrl();
                    this.transLateTargetPage()
                    this.loadNavBar()
                    this.loadFooter()
                    if(this.withComments) {
                        this.loadComment()
                    }
                    if(this.withItemInfoModules) {
                        this.loadItemInfoModules();
                    }
                }
            },

            changeUrl(){
                let pathname = window.location.pathname;
                if(pathname.indexOf("userSpace")==-1) {
                    let url = decodeURIComponent(window.location.search);
                    let urlLanguage = this.GetQueryString(url, "language");
                    if (this.currentLang == urlLanguage) {
                        return;
                    } else if (urlLanguage != undefined && urlLanguage != null) { //url中存在language
                        let startIndex = url.indexOf("language=");
                        url = url.substring(0, startIndex) + "language=" + this.currentLang + url.substring(startIndex + 9 + urlLanguage.length);
                    } else { //url中不存在language
                        if (url.indexOf("?") == -1) {
                            url += "?language=" + this.currentLang;
                        } else {
                            url += "&language=" + this.currentLang;
                        }
                    }
                    history.pushState({}, document.title, url);
                }
            },

            async loadNavBar(){//需要加载navbar的网页触发翻译
                let content = await this.getLangJson('navbar');
                let scopeDom = document.getElementById("navBar");

                //导航栏community英汉间距改变 --- add by wyjq
                let communityDom=document.getElementById("drop1");
                let helpSubMenu = document.getElementById("subHelp");
                if(communityDom!==null){
                    if(this.currentLang==="zh-cn"){
                        communityDom.style.width="77px";
                        helpSubMenu.style.left="611px";
                    }else{
                        communityDom.style.width="127px";
                        helpSubMenu.style.left="661px";
                    }
                }

                this.defautTrans(content,scopeDom)
            },

            async loadFooter(){//需要加载footer的网页触发翻译
                let content = await this.getLangJson('footer')
                let scopeDom = document.getElementById("footer")
                this.defautTrans(content,scopeDom)
            },

            async loadComment(){//需要加载评论区的页面触发翻译 --- add by wyjq
                let content = await this.getLangJson('comment')
                let scopeDom = document.getElementById("comment")
                this.defautTrans(content,scopeDom)
                document.getElementById("commentTextArea").setAttribute("placeholder",content[this.currentLang]["writeYourComment"]);
            },

            async loadItemInfoModules(){
                let content = await this.getLangJson('itemInfoModules')
                let scopeDom = document.getElementById("app")
                this.defautTrans(content,scopeDom)
            },


            defautTrans(content,scopeDom){

                content = content[this.currentLang]
                for(let key in content){
                    let targets = new Set()
                    if(typeof jQuery == 'undefined'){//如果没有引入jq
                        this.getChildDomByTagByAttr(scopeDom,'all','translateId',key,targets)
                    }else{
                        let searchString = `[translateId=${key}]`
                        targets = $(searchString)

                    }
                    for(let ele of targets){
                        ele.innerText = content[key]
                    }

                }
            },

            getChildDomByTagByAttr(scopeDom,tag='all',attrName,attrValue,resultSet){
                let children = scopeDom.children
                for(let i=0;i<children.length;i++){
                    let ele = children[i]
                    if((tag=='all'||ele.nodeName.toLowerCase()==tag.toLowerCase())&&ele.getAttribute(attrName)==attrValue){
                        resultSet.add(ele)
                    }
                    this.getChildDomByTagByAttr(ele,tag='all',attrName,attrValue,resultSet)
                }

            },

            GetQueryString(originStr,paramName) {

                var reg = new RegExp("(^|&)"+ paramName +"=([^&]*)(&|$)");

                var r = originStr.substr(1).match(reg);

                if(r!=null)return  unescape(r[2]); return null;

            },

            LanguageIsValid(lang){
                for(i = 0;i<this.langList.length;i++){
                    if(lang === this.langList[i]){
                        return true;
                    }
                }
                return false;
            }

        },
        created() {

            let url = decodeURIComponent(window.location.search);
            let urlLanguage = this.GetQueryString(url, "language");
            let language = window.localStorage.getItem("language");

            if(this.LanguageIsValid(urlLanguage)){
                this.currentLang = urlLanguage;
            }else {
                if (this.LanguageIsValid(language)) {
                    this.currentLang = language;
                } else {
                    this.currentLang = this.initialLang
                }

                this.changeUrl();
            }

            if(this.currentLang !== "zh-cn"){
                //element-ui 切换英文，勿删！
                if(ELEMENT!=undefined) {
                    ELEMENT.locale(ELEMENT.lang.en)
                }
            }

            this.transLateTargetPage()
            this.loadNavBar()
            this.loadFooter()
            if(this.withComments) {
                this.loadComment()
            }
            if(this.withItemInfoModules) {
                this.loadItemInfoModules();
            }
        },
        mounted() {

        }
    }
)