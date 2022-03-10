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
    let url = '';//'/detail'
    return joinByDataHub(url, joinStr)
}

function getHubList(joinStr) {
    let url = '/items'
    return joinByDataHub(url, joinStr)
}

function QueryHubListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByDataHub(url, joinStr)
}

function QueryHubListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByDataHub(url, joinStr)
}

//item
function getItemList(joinStr) {
    let url = '/items'
    return joinByDataItem(url, joinStr)
}

function getItemById(joinStr) {
    let url = '';//'/detail'
    return joinByDataItem(url, joinStr)
}

function QueryItemListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByDataItem(url, joinStr)
}

function QueryItemListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByDataItem(url, joinStr)
}

//method
function getMethodList(joinStr) {
    let url = '/items'
    return joinByDataMethod(url, joinStr)
}

function getMethodById(joinStr) {
    let url = '';//'/detail'
    return joinByDataMethod(url, joinStr)
}

function QueryMethodListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByDataMethod(url, joinStr)
}

function QueryMethodListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByDataMethod(url, joinStr)
}





