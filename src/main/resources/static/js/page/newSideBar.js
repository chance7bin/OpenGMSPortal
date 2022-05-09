Vue.component('headSideBar', {
    template: '#headSideBar',
    props: ['userInfoinParent','curindexParent',"htmlJson"],

    data() {
        return {
            tableData: [{
                info:[],
                model:[],
                data:[],
                application:[]
            }],
            // g:0,
            curIndex:1,
            itemIndex: 1,

            //
            userInfo:{

            },
            noticeNum:0,

            htmlJSON:{}

        }
    },

    watch: {
        curindexParent: {
            handler() {
                this.curIndex = this.curindexParent
            },
            immediate: true
        },

        userInfoinParent: {
            handler() {
                this.userInfo = this.userInfoinParent
                // this.getMessageInfo();
            },
            immediate: true
        },


        $route:{
            handler(to, from) {
                //通过路由判断条目高亮显示,
                console.log(to.path)
                let path = to.path.split('/')[1]
                if(path === '')
                    this.curIndex = 1
                else if(path.indexOf('model') != -1)
                    this.curIndex = 2
                else if(path.indexOf('data') != -1&&path.indexOf('dataTemplate') == -1)
                    this.curIndex = 3
                else if(path.indexOf('server') != -1)
                    this.curIndex = 4
                else if(path.indexOf('task') != -1)
                    this.curIndex = 5
                else if(path.indexOf('community') != -1||path.indexOf('communities') != -1)
                    this.curIndex = 6
                else if(path.indexOf('Theme') != -1)
                    this.curIndex = 7
                else if(path.indexOf('account') != -1)
                    this.curIndex = 8
                else if(path.indexOf('feedback') != -1)
                    this.curIndex = 9
                else if(path.indexOf('notice') != -1)
                    this.curIndex = 10
                else if(path.indexOf('draft') != -1)
                    this.curIndex = 11
                else if(path.indexOf('version') != -1)
                    this.curIndex = 12
                else if(path.indexOf('comment') != -1)
                    this.curIndex = 13
            },
            immediate:true
        }
    },

    methods:{
        changeRter(index){
            this.curIndex = index;
            var urls={
                1:'/user/userSpace#/',
                2:'/user/userSpace#/model',
                3:'/user/userSpace#/data',
                4:'/user/userSpace#/server',
                5:'/user/userSpace#/task',
                6:'/user/userSpace#/community',
                7:'/user/userSpace#/userTheme',
                8:'/user/userSpace#/account',
                10:'/user/userSpace#/notice',
                9:'/user/userSpace#/feedback',
                11:'/user/userSpace#/draftBox',
                12:'/user/userSpace#/version',
                13:'/user/userSpace#/comment'
            }

            this.setSession('curIndex',index)
            window.location.href=urls[index]

            //此处改完还要修改监听中的$route以保证回退时能够正确高亮显示所在条目

        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
            // this.editOid = sessionStorage.getItem('editItemOid');
        },

        getUserInfo() {
            axios.get('/user/getFullUserInfo').then(
                res => {
                    if(res.data.code==0){
                        this.userInfo = res.data.data//userserver多包一层data
                        this.subscribe = this.userInfo.subscribe;
                        let orgs = this.userInfo.organizations;

                        if (orgs.length != 0) {
                            this.userInfo.orgStr = orgs[0];
                            for (i = 1; i < orgs.length; i++) {
                                this.userInfo.orgStr += ", " + orgs[i];
                            }
                        }

                        let sas = this.userInfo.domain;
                        this.userInfo.saStr = '';
                        if (sas != null && sas.length != 0) {
                            this.userInfo.saStr = sas[0];
                            for (i = 1; i < sas.length; i++) {
                                this.userInfo.saStr += ", " + sas[i];
                            }
                        }

                        this.$refs.userAvatar.updateAvatar();//触发头像组件更新

                        this.load = false;
                    }else if (res.data.code == -1) {
                        this.$alert(this.htmlJson.LoginInFirst)
                        window.sessionStorage.setItem("history", window.location.href);
                        window.location.href = "/user/login"
                    }else if(res.data.code == -2){
                        let resmsg = res.data.msg
                        let msg
                        if(resmsg=="no user"){
                            msg = "User of this email does not exist!"
                        }else if(resmsg=="err"){
                            msg = "Network error, cannot find the user."
                        }
                        this.$alert(msg, 'Error', {
                                  type:"warning",
                                  confirmButtonText: 'OK',
                                  callback: ()=>{
                                      window.location.href = "/home"
                                      return
                                  }
                              }
                          );
                    }

                }
            )
        },

        subMenuDropDpwn(target,timerDrop,timerFold){
            let childrenCount=target.children('ul').children('li').length;
            // console.log(childrenCount);
            let timeLength=childrenCount*60;
            let height=childrenCount*45+5;
            // target.animate({height:height},timeLength,'swing');
            target.children('ul').animate({height:height},timeLength,'swing');
            // target.children('ul').animate({width:180},timeLength,'swing');
            // target.children('ul').children().css('display','block')
            let li=target.children('ul').children('li');
            clearTimeout(timerFold);

        },

        subMenuFoldUp(target,timerDrop,timerFold){
            let childrenCount=target.children('ul').children('li').length;
            let timeLength=childrenCount*40;

            // target.animate({height:0},timeLength,'linear');
            target.children('ul').animate({height:0},timeLength,'linear');
            // target.children('ul').animate({width:0},timeLength,'swing');
            let li=target.children('ul').children('li');
            // target.children('ul').children().css('display','none')
            clearTimeout(timerDrop);
            target.children('ul').children('#phoneLogin').css('height','0')
        },
        //
        getnoticeNum(){
            $.updatePortalUser({
                url:"/theme/getnoticeNum",
                type:"GET",
                success:(data)=>{
                    this.noticeNum = data;
                }
            })
        }
    },

    created(){
        this.getUserInfo()


    },

    mounted(){

        let that = this;
        //let that= this;
        //用于判断用户是否收到消息
        // that.getMessageInfo();
        // this.getnoticeNum();

        $('#dropmu').click((e)=>{
            // clearTimeout(tFoldLmu);
            let target=$('#submenu');
            let height=target.children('ul').css('height');
            if (height=='0px'){
                target.css('display','block')
                this.subMenuDropDpwn(target);
            }

            else
                this.subMenuFoldUp(target);
            if(e.stopPropagation){
                e.stopPropagation();
            }else{
                e.cancelBubble = true;
            }
        })

        $('html').click((e)=>{
            if($(e.target).closest("#leftUl").length == 0){
                // clearTimeout(tFoldLmu);
                let target=$('#submenu');
                this.subMenuFoldUp(target);
            }
        })
    },

})