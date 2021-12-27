//join
function joinByDataHub(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/dataHub' + url + '/' + joinStr
    }
    return '/dataHub' + url
}

function joinByDataItem(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/dataItem' + url + '/' + joinStr
    }
    return '/dataItem' + url
}

function joinByDataMethod(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/dataMethod' + url + '/' + joinStr
    }
    return '/dataMethod' + url
}


//get

//hub
function getHubById(joinStr) {
    let url = '/detail'
    return joinByDataHub(url, joinStr)
}

function getHubList(joinStr) {
    let url = '/items'
    return joinByDataHub(url, joinStr)
}

//item
function getItemList(joinStr) {
    let url = '/items'
    return joinByDataItem(url, joinStr)
}

//method
function getMethodList(joinStr) {
    let url = '/items'
    return joinByDataMethod(url, joinStr)
}

function getMethodById(joinStr) {
    let url = '/detail'
    return joinByDataMethod(url, joinStr)
}

