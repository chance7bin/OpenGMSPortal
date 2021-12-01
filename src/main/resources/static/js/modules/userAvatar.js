Vue.component("userAvatar",
    {
        template: '#userAvatar',
        props: {
            email: {
                type: String,
                default: 'default'
            }
        },
        data() {
            return {
                avatar:'',

            }
        },
        watch:{
            async email(val,oldval){
                if(typeof val === 'string'){
                    this.email = val
                    this.avatar = await this.getAvatar(this.email)
                }
            }
        },
        computed: {
        },
        methods: {
            async getAvatar(email){
                let avatar = '/static/img/icon/default.png'
                if(typeof email === 'string'){
                    if(email==='default'){
                        avatar = '/static/img/icon/default.png'
                    }else{
                        try{
                            avatar = await axios.get('/userServer/user/getAvatar/' + this.email
                            ).then(
                                res => {
                                    let msg = res.data.msg;
                                    if(msg==null){

                                        return '/static/img/icon/default.png'
                                    }else{
                                        return '/userServer' + res.data.msg
                                    }
                                }
                            ).catch(()=>{
                                    return '/static/img/icon/default.png'
                                }

                            )
                        }catch (e) {
                            avatar = '/static/img/icon/default.png'
                        }
                    }



                }
                return avatar

            },

            async updateAvatar(_email){
                let email = _email==undefined?this.email:_email

                this.avatar = await this.getAvatar(email)

                return this.avatar
            },

            insertAvatar(imageStr){
                this.avatar = imageStr===undefined?'/static/img/icon/default.png':imageStr
            }
        },
        async created() {
            if(this.email!='default'){
                this.avatar = await this.getAvatar(this.email)
            }
        },
        mounted() {}
    }
)