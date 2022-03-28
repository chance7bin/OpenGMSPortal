function joinByTheme(url, joinStr){
    if (typeof(joinStr) != 'undefined'){
        return '/theme' + url + '/' + joinStr
    }
    return '/theme' + url
}

function joinByUser(url, joinStr) {
    if (typeof(joinStr) != 'undefined'){
        return '/user' + url + '/' + joinStr
    }
    return '/user' + url
}

   function getMaintainerApi(joinStr) {
        let url = '/maintainer'
        return joinByTheme(url, joinStr)
    }
    
    function addThemeApi(joinStr) {
        let url = ''
        return joinByTheme(url, joinStr)
    }
    
    function deleteThemeApi(joinStr) {
        let url = ''
        return joinByTheme(url, joinStr)
    }

    function getThemesByUserId(joinStr) {
        let url = "/themeList"
        return joinByUser(url, joinStr)

    }
    function getThemeInfoByidApi(joinStr) {
        let url = "/info"
        return joinByTheme(url, joinStr)
    }

    function updateThemeApi(joinStr) {
        let url = ''
        return joinByTheme(url, joinStr)
    }

// function joinByTheme(url, joinStr){
//     if (typeof(joinStr) != 'undefined'){
//         return '/theme' + url + '/' + joinStr
//     }
//     return '/theme' + url
// }

function QueryThemeListOfAuthorSelf(joinStr){
    let url = '/queryListOfAuthorSelf'
    return joinByTheme(url, joinStr)
}

