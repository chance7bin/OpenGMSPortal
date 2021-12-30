new Vue({
    el: '#app',
    components: {
        'avatar': VueAvatar.Avatar
    },
    data: function () {
        return {

            htmlJSON:{}
        }

    },
    methods:{
        translatePage(jsonContent){
            this.htmlJSON = jsonContent
        },

    }

})