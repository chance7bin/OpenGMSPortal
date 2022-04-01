new Vue({
    el: '#app',
    data: function () {
        return {
            htmlJSON:{},
            activeIndex:'3-2',
            activeNameGraph: 'Image',

            graphVisible:"none",

            useroid:"",
            userId:"",
            userImg:"",

            user:{},

            modelOid:'',
            editLogicalModelDialog :false,
            lightenContributor:{},

            currentDetailLanguage:"",
        }
    },
    methods: {
        translatePage(jsonContent){
            this.htmlJSON = jsonContent
        },

        changeDetailLanguage(command){
            this.currentDetailLanguage = command;
            let data = {
                "oid": this.getOid(),
                "language": this.currentDetailLanguage
            };

            if(window.location.href.indexOf("history")===-1) {
                $.get("/modelItem/getDetailByLanguage", data, (result) => {
                    this.detail = result.data;
                })
            }else{
                $.get("/version/languageDetail/modelItem", data, (result) => {
                    this.detail = result.data;
                })
            }
        },
        getDescription(){
            axios.get('/modelItem/getDescription/'+this.modelOid
            ).then(
                res => {
                    if(res.data.code==-1){
                        this.confirmLogin()
                    }else{
                        this.editDescription = true
                        let data = res.data.data
                        Vue.nextTick(()=>{
                            initTinymce('textarea#conceptText')
                            this.localizationList = data
                            let interval = setInterval(() => {
                                this.changeLocalization(this.localizationList[0])
                                clearInterval(interval);
                            }, 1000);
                        })

                    }

                }
            )
        },
        claim(){
            $.get("/user/load",{},(result)=>{
                let json = result;
                if (json.oid == "") {
                    this.confirmLogin();
                }
                else {
                    this.authorshipFormVisible = true;
                }
            })
        },
        feedBack(){
            $.get("/user/load",{},(result)=>{
                let json = result;
                if (json.oid == "") {
                    this.confirmLogin();
                }
                else {
                    window.location.href = "/user/userSpace#/feedback"
                }
            })
        },

        edit(){
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (data) => {
                    if (data.oid == "") {
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
                    }
                    else {
                        let href=window.location.href;
                        let hrefs=href.split('/');
                        let oid=hrefs[hrefs.length-1].split("#")[0];
                        this.modelOid=oid
                        window.location.href = "/user/userSpace#/model/manageLogicalModel/"+oid;
                        return

                        this.editLogicalModelDialog=true
                        window.sessionStorage.setItem("editId",oid)
                        // $.ajax({
                        //     type: "GET",
                        //     url: "/logicalModel/getUserOidByOid",
                        //     data: {
                        //         oid:oid
                        //     },
                        //     cache: false,
                        //     async: false,
                        //     xhrFields: {
                        //         withCredentials: true
                        //     },
                        //     crossDomain: true,
                        //     success: (json) => {
                        //         // if(json.data==data.oid){
                        //         window.sessionStorage.setItem("editLogicalModel_id",oid)
                        //         window.location.href="/user/createLogicalModel";
                        //         // }
                        //         // else{
                        //         //     alert("You are not the model item's author, please contact to the author to modify the model item.")
                        //         // }
                        //     }
                        // });
                    }
                }
            })
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },

        switchClick(i){

            if(i==1) {
                $(".tab1").css("display", "block");
                $(".tab2").css("display", "none");
                $(".tab3").css("display", "none");
            }
            else if(i==2) {
                $(".tab1").css("display", "none");
                $(".tab2").css("display", "block");
                $(".tab3").css("display", "none");
            }
            else{
                $(".tab1").css("display", "none");
                $(".tab2").css("display", "none");
                $(".tab3").css("display", "block");
            }


            var btns=$(".switch-btn")

            btns.css("color","#636363");
            btns.eq(i-1).css("color","#428bca");

        },

        showMxGraph(){
            this.graphVisible = "block";
            var vh=window.innerHeight;
            var h = vh - 62 +"px";
            $("#container_top").css("height",h);

            document.body.scrollTop = 0;
            document.documentElement.scrollTop = 0;
            document.body.style.overflowY="hidden";
        },

        hideMxGraph(){
            this.graphVisible = "none";

            document.body.style.overflowY="auto";
        }

    },
    mounted(){

        this.lightenContributor = author
        this.$refs.mainContributorAvatar.insertAvatar(this.lightenContributor.avatar)
        this.$refs.mainContributorAvatar1.insertAvatar(this.lightenContributor.avatar)

        this.setSession("history", window.location.href);

        axios.get("/user/load")
            .then((res) => {
                if (res.status == 200) {
                    if (res.data.oid != '') {
                        this.useroid = res.data.oid;
                        this.userId = res.data.userId;
                        this.userImg = res.data.image;
                    }

                }
            })
        // this.getComments();

        $(document).on('mouseover mouseout','.flexRowSpaceBetween',function(e){

            let deleteBtn=$(e.currentTarget).children().eq(1).children(".delete");
            if(deleteBtn.css("display")=="none"){
                deleteBtn.css("display","block");
            }else{
                deleteBtn.css("display","none");
            }

        });

        let parentWidth=$("#pane-Image").width();
        let children=$("#pane-Image img");
        for(i=0;i<children.length;i++){
            if(children.eq(i).width()>parentWidth){
                children.eq(i).css("width","100%")
            }
        }

        $(".ab").click(function () {

                if (!$(this).hasClass('transform180'))
                    $(this).addClass('transform180')
                else
                    $(this).removeClass('transform180')
            }
        );

        let qrcodes = document.getElementsByClassName("qrcode");
        for(i=0;i<qrcodes.length;i++) {
            new QRCode(document.getElementsByClassName("qrcode")[i], {
                text: window.location.href,
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        }
    }
})