//join

//concept
function joinByConcept(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/concept' + url + '/' + joinStr
    }
    return '/concept' + url
}

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

//spatialReference
function getspatialReferenceList(joinStr) {
    let url = '/spatialReferenceList'
    return joinBySpatialReference(url, joinStr)
}

//template
function getTemplateList(joinStr) {
    let url = '/templateList'
    return joinByTemplate(url, joinStr)
}

//unit
function getUnitList(joinStr) {
    let url = '/unitList'
    return joinByUnit(url, joinStr)
}