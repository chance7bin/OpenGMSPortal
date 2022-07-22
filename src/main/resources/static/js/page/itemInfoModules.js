var itemInfoModules = new Vue({
    el:"#itemInfoModules",
    data() {
        return {
            myName: "myName111"
        }
    },
    created(){
        console.log("created itemInfoModules")
        console.log(this.myName)
    },

    mounted() {


    },

    methods: {

    }
});
