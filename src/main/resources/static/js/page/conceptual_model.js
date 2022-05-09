new Vue({
    el: '#app',
    props:['htmlJson'],
    data: function () {
        return {
            htmlJSON:{},
            useroid:"",
            userId:"",
            userImg:"",

            modelItemFormVisible:false,
            activeIndex:'3-1',
            activeNameGraph: 'Image',
            activeName: 'Conceptual Model',
            lightenContributor:{},

            form:{
                name:"",
            },
            graphVisible:"none",
            editConceptualModelDialog:false,
            modelOid:'',
            mainContributor:{},

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

        confirmLogin(){
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
                        this.confirmLogin();

                    }
                    else {
                        let href=window.location.href;
                        let hrefs=href.split('/');
                        let oid=hrefs[hrefs.length-1].split("#")[0];
                        window.open("/user/userSpace#/model/manageConceptualModel/"+oid);
                        return

                        this.editDialogOpen()
                        window.sessionStorage.setItem("editId",oid)
                        // $.ajax({
                        //     type: "GET",
                        //     url: "/conceptualModel/getUserOidByOid",
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
                        //         window.sessionStorage.setItem("editConceptualModel_id",oid)
                        //         window.location.href="/user/createConceptualModel";
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

        editDialogOpen(){
            let href = window.location.href;
            let hrefs = href.split('/');
            let oid = hrefs[hrefs.length - 1].split("#")[0];
            this.modelOid = oid;
            this.editConceptualModelDialog = true
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },

        switchClick(i){

            if(i==2) {
                $(".tab1").css("display", "none");
                $(".tab2").css("display", "block");
            }
            else {
                $(".tab2").css("display", "none");
                $(".tab1").css("display", "block");
            }

            var btns=$(".switch-btn")
            btns.eq(i%2).css("color","#636363");
            //btns.eq(i%2).attr("href","javascript:void(0)");
            btns.eq((i+1)%2).css("color","#428bca");
            //btns.eq((i+1)%2).attr("href","");
        },

        formateTime(val){
            var date = new Date(val);
            return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
        },

        bindModelItem(){
            let urls=window.location.href.split("/");
            $.ajax({
                type: "GET",
                url: "/modelItem/bindModel/",
                data: {
                    type:1,
                    name:this.form.name,
                    oid:urls[urls.length-1]
                },
                async: false,
                success: (json) => {
                    if(json.code==-1){
                        this.$alert(this.htmlJson.ModelItemIsNotExistPleaseCheckTheName, this.htmlJson.Error, {
                            type:'error',
                            confirmButtonText: 'OK',
                            callback: action => {

                            }
                        });
                    }
                    else{
                        this.$alert(this.htmlJson.BindSuccessfully, this.htmlJson.Success, {
                            type:'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.href=window.location.href.substring(0,window.location.href.length-36)+json.data;
                            }
                        });
                    }
                }
            })
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

        $("[data-toggle='tooltip']").tooltip();

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
let _this = this;
            _this.mainContributor = author;
            _this.$refs.mainContributorAvatar.insertAvatar(_this.mainContributor.avatar)
            _this.$refs.mainContributorAvatar1.insertAvatar(_this.mainContributor.avatar)



    }
})