// join
function joinByModelItem(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/modelItem' + url + '/' + joinStr
    }
    return '/modelItem' + url
}



//get

// modelItemList
function getModelItemList(joinStr) {
    let url = '/items'
    return joinByModelItem(url, joinStr)
}

function getModelItemById(joinStr) {
    let url = '/detail'
    return joinByModelItem(url, joinStr)
}