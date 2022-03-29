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
        },
        data() {
            return {
                themeStyle:'lightness',
                currentLang:'en-us',
            }
        },
        computed: {},
        methods: {
            async transLateTargetPage(){//获取页面内容对应翻译文件，不包含navbar和footer
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

            loadLangContent(content){
                this.$emit('translate-page',content[this.currentLang])
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
                    window.sessionStorage.setItem("language", lang);
                    this.currentLang = lang
                    this.transLateTargetPage()
                    this.loadNavBar()
                    this.loadFooter()
                    this.loadComment()
                }
            },

            async loadNavBar(){//需要加载navbar的网页触发翻译
                let content = await this.getLangJson('navbar')
                let scopeDom = document.getElementById("navBar")

                //导航栏community英汉间距改变 --- add by wyjq
                let communityDom=document.getElementById("drop1")
                if(this.currentLang==="zh-cn"){
                    communityDom.style.width="77px"
                }else{
                    communityDom.style.width="127px"
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

        },
        created() {

            let language = window.sessionStorage.getItem("language");
            if(language != undefined && language != null){
                this.currentLang = language;
            }else {
                this.currentLang = this.initialLang
            }

            this.transLateTargetPage()
            this.loadNavBar()
            this.loadFooter()
        },
        mounted() {

        }
    }
)