Vue.component("img-upload",
    {
        template: '#imgUpload',
        props: {
            // a: {
            //     type: String,
            //     default: 'default'
            // }
        },
        data() {
            return {
                image:'',
            }
        },
        computed: {},
        methods: {
            insertImg(img){//父组件调用该方法塞入img
                this.image = img
            },

            getImage(){//返回组件中的图片
                return this.image
            },

            deleteImg(){
                let obj = document.getElementById('imgOne')
                // obj.outerHTML=obj.outerHTML
                obj.value = ''
                this.image = ''
            },

            imgFile() {
                $("#imgOne").click();
            },

            preImg() {

                var file = $('#imgOne').get(0).files[0];
                //创建用来读取此文件的对象
                var reader = new FileReader();
                //使用该对象读取file文件
                reader.readAsDataURL(file);
                //读取文件成功后执行的方法函数
                reader.onload =  (e) => {
                    //读取成功后返回的一个参数e，整个的一个进度事件
                    //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                    //的base64编码格式的地址
                    this.image = e.target.result

                    this.postImage(this.image)
                }

            },


            postImage(img) {//抛出image给父组件方法接受
                this.$emit('get-img-content',img)
            }
        },
        created() {},
        mounted() {}
    }
)