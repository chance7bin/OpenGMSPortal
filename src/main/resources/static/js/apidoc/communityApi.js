//join

//concept
function joinByConcept(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/concept' + url + '/' + joinStr
    }
    return '/concept' + url
}

//spatial
function joinBySpatialReference(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/spatialReference' + url + '/' + joinStr
    }
    return '/spatialReference' + url
}

//template
function joinByTemplate(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/template' + url + '/' + joinStr
    }
    return '/template' + url
}
//unit
function joinByUnit(url, joinStr){
    if (typeof(joinStr) != 'undefined'){ ``
        return '/unit' + url + '/' + joinStr
    }
    return '/unit' + url
}

//concept
//list
function getConceptList(joinStr) {
    let url = '/conceptList'
    return joinByConcept(url, joinStr)
}

function QueryConceptListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByConcept(url, joinStr)
}

function QueryConceptListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByConcept(url, joinStr)
}

//spatialReference
function getSpatialReferenceList(joinStr) {
    let url = '/spatialReferenceList'
    return joinBySpatialReference(url, joinStr)
}

function QuerySpatialListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinBySpatialReference(url, joinStr)
}

function QuerySpatialListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinBySpatialReference(url, joinStr)
}

//template
function getTemplateList(joinStr) {
    let url = '/templateList'
    return joinByTemplate(url, joinStr)
}

function QueryTemplateListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByTemplate(url, joinStr)
}

function QueryTemplateListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByTemplate(url, joinStr)
}

//unit
function getUnitList(joinStr) {
    let url = '/unitList'
    return joinByUnit(url, joinStr)
}

function QueryUnitListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByUnit(url, joinStr)
}

function QueryUnitListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByUnit(url, joinStr)
}