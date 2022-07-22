

Vue.component("itemInfoModules",
    {
        template: '#itemInfoModules',
        data() {
            return {
                myName: "myName111"
            }
        },
        computed:{
            myName(){
                console.log("computed itemInfoModules");
                return "myName111";
            }
        },
        created(){
            console.log("created itemInfoModules")
            console.log(this.myName)
        },

        mounted() {
            console.log("mounted itemInfoModules")
            console.log(this.myName)

        },

        methods: {

        },
        activated () {

            console.log('实例被激活时使用，用于重复激活一个实例的时候')

        },

        deactivated () {

            console.log('实例没有被激活时')

        }
    }
);
