// join

//article
function joinByArticle(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/article' + url + '/' + joinStr
    }
    return '/article' + url
}

//project
function joinByProject(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/project' + url + '/' + joinStr
    }
    return '/project' + url
}

//conference
function joinByConference(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/conference' + url + '/' + joinStr
    }
    return '/conference' + url
}



//get

//article
function QueryArticleListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByArticle(url, joinStr)
}

function QueryArticleListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByArticle(url, joinStr)
}

//project
function QueryProjectListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByProject(url, joinStr)
}

function QueryProjectListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByProject(url, joinStr)
}

//conference
function QueryConferenceListOfAuthor(joinStr){
    let url = '/queryListOfAuthor'
    return joinByConference(url, joinStr)
}

function QueryConferenceListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByConference(url, joinStr)
}
