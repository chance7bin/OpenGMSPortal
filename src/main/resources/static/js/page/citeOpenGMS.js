var vue = new Vue({
        el: "#app",
        data() {
            return {
                ScreenMinHeight: "0px",
                ScreenWidth: 1,

                sectionTitleSpan: 7,
                sectionSpan: 17,

                sectionData: [],

                supportDoc: [],

                cites: [],

                isHelpful: '',

                commentSended: false,

                timeout1: '',

                htmlJSON:{}
            }
        },

        watch: {
            $router: {
                handler: function f(to, from) {

                }
            },
            ScreenWidth: {
                handler: function (val) {
                    if (val < 505) {
                        this.sectionTitleSpan = 24
                        this.sectionSpan = 24
                    } else {
                        this.sectionTitleSpan = 7
                        this.sectionSpan = 17
                    }
                }
            }
        },
        methods: {
            translatePage(jsonContent){
                this.htmlJSON = jsonContent;
                this.changeSectionData();
            },
            generateId(str) {
                // let reg = new RegExp(".*? ", "");
                // return str.replace(reg, "");
                return str.replaceAll(" ","_")
            },

            backClick() {
                window.location.href = '/help/support'
            },

            anchorClick(data) {
                window.location.href = '#' + data.label.replaceAll(" ","_");
                this.scrollToAnchor()
            },

            scrollToAnchor() {
                var hash = this.getHash(), // 获取url的hash值
                    anchor = this.getAnchor(hash), // 获取伪锚点的id
                    anchorDom, // 伪锚点dom对象
                    anchorScrollTop; // 伪锚点距离页面顶部的距离

                // 如果不存在伪锚点,则直接结束
                if (anchor.length < 1) {
                    return;
                }

                anchorDom = this.getDom(anchor);
                anchorScrollTop = anchorDom.offsetTop + 200;//加上navbar的高度

                this.animationToAnchor(document.body.scrollTop, anchorScrollTop);
            },

            /*
        	@function 滚动到指定位置方法
        	@param startNum {int} -- 开始位置
        	@param stopNum {int} -- 结束位置
            */
            animationToAnchor(startNum, stopNum) {
                // var nowNum = startNum + 80; // 步进为80，缓移动
                //
                // if (nowNum > stopNum) {
                //     nowNum = stopNum;
                // }
                //
                // // 缓动方法
                // window.requestAnimationFrame(function () {
                //     window.scrollTo(0, nowNum, 'smooth'); // 当前示例页面，滚动条在body，所以滚动body
                //
                //     // 滚动到预定位置则结束
                //     if (nowNum == stopNum) {
                //         return;
                //     }
                //
                //     vue.animationToAnchor(nowNum, stopNum); // 只要还符合缓动条件，则递归调用
                // });
                $('html,body').animate({scrollTop: stopNum + 'px'}, 200);
            },

            // 获取锚点id
            getAnchor(str) {
                return this.checkAnchor(str) ? str.split("d_")[1] : "";
            },

            // 通过命名找到对应的锚点
            checkAnchor(str) {
                return str.indexOf("d_") == 0 ? true : false;
            },

            // 获取hash值
            getHash() {
                return window.location.hash.substring(1);
            },

            // 获取dom对象
            getDom(id) {
                return document.getElementById(id);
            },

            unfoldSide() {
                clearTimeout(this.timeout1)
                $('.floatBlock').animate({width: 213.8}, 320, 'swing', {queue: false})
            },

            foldSide() {
                if (true) {
                    this.timeout1 = setTimeout(() => {
                        $('.floatBlock').animate({width: 0}, 320, 'swing', {queue: false})
                    }, 821)
                }
            },

            initSize(){

                var s = $(window).scrollTop();
                var w = $(window).width() + 13
                //控制右下角helpful显示隐藏
                if (w > 505) {
                    if ($('#app').height() - document.documentElement.clientHeight - s < 280) {
                        $(".floatBlock").fadeIn(500);
                    } else {
                        $(".floatBlock").fadeOut(500);
                    }
                    ;
                }

            },

            changeSectionData(){
                this.sectionData = [];

                for(let i = 0;i<this.htmlJSON.cites.length;i++){
                    let block = this.htmlJSON.cites[i];
                    let id = block["block"].replace(/ /g,"_");
                    this.htmlJSON.cites[i].id = id;
                    let obj = {
                        "id":id,
                        "label":block['block'],
                        "children":[]
                    };
                    let children = block["children"];
                    for(let j = 0;j<children.length;j++){
                        let child = children[j];
                        id = child["head"].replace(/ /g,"_");
                        this.htmlJSON.cites[i].children[j].id = id;
                        if(child["head"].trim()!="") {
                            obj["children"].push({
                                "id":child["head"].replace(/ /g,"_"),
                                "label": child['head']
                            });
                        }
                    }

                    this.sectionData.push(obj);
                }

            },

        },
        created() {

        },
        mounted() {
            // let result = $.ajax({url:"/static/json/cite.json",async:false});
            // console.log(result)
            // let blocks = JSON.parse(result.responseText);

            // this.changeSectionData();

            // this.sectionData = sectionData;
            // this.supportDoc = supportDoc;

            var vthis = this
            let height = document.documentElement.clientHeight;
            this.ScreenMinHeight = (height - 400) + "px";
            this.ScreenWidth = document.documentElement.clientWidth;
            window.addEventListener("resize", () => {
                return (() => {
                    let height = document.documentElement.clientHeight;
                    let width = document.documentElement.clientWidth;
                    this.ScreenMinHeight = (height - 400) + "px";
                    this.ScreenWidth = width;
                })()
            })

            // window.onhashchange=()=>{
            //     this.scrollToAnchor()
            // }

            $(window).scroll()

            window.addEventListener('scroll', this.initSize);
            window.addEventListener('resize', this.initSize);
        },

    }
)