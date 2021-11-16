new Vue({
    el: '#manage_home',
    data:{
            todos:[{
                name:"abc",
                age:"12"
            },{
                name:"def",
                age:"13"
            }],
    },
    mounted() {

        axios.get('/management/test')
            //这里使用用箭头函数，使得this指向不是windows，而是外层的vue
            .then( (response)=> {
                console.log(response);
                this.todos.push(response.data.data)
            })
            .catch(function (error) {
                console.log(error);
            });

    },
    methods: {

    },
})