// join
function joinByModelItem(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/modelItem' + url + '/' + joinStr
    }
    return '/modelItem' + url
}

function joinByConceptualModel(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/conceptualModel' + url + '/' + joinStr
    }
    return '/conceptualModel' + url
}

function joinByLogicalModel(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/logicalModel' + url + '/' + joinStr
    }
    return '/logicalModel' + url
}

function joinByComputableModel(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/computableModel' + url + '/' + joinStr
    }
    return '/computableModel' + url
}



//get

// modelItemList
function getModelItemList(joinStr) {
    let url = '/items';
    return joinByModelItem(url, joinStr)
}

function getModelItemById(joinStr) {
    let url = '';//'/detail'
    return joinByModelItem(url, joinStr)
}


function QueryModelItemListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByModelItem(url, joinStr)
}

function QueryModelItemListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByModelItem(url, joinStr)
}

function QueryConceptualModelListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByConceptualModel(url, joinStr)
}

function QueryConceptualModelListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByConceptualModel(url, joinStr)
}

function QueryLogicalModelListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByLogicalModel(url, joinStr)
}

function QueryLogicalModelListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByLogicalModel(url, joinStr)
}

function QueryComputableModelListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByComputableModel(url, joinStr)
}

function QueryComputableModelListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByComputableModel(url, joinStr)
}