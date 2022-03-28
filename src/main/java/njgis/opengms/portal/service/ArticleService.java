package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ArticleDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.po.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/14
 */
@Service
public class ArticleService {

    @Autowired
    GenericService genericService;

    @Autowired
    ArticleDao articleDao;

    @Autowired
    UserDao userDao;

    // 根据查询条件查询符合条件的模型条目(主要用于根据user查询)
    public JSONObject queryByUser(UserFindDTO userFindDTO) {
        JSONObject queryResult = new JSONObject();

        //查询条件梳理
        String searchText = userFindDTO.getSearchText();
        String authorEmail = userFindDTO.getAuthorEmail();
        userFindDTO.setSortField("createDate");
        Pageable pageable = genericService.getPageable(userFindDTO);

        //根据不同的查询字段进行查询
        Page<Article> itemPage = null;

        itemPage = articleDao.findByTitleContainsIgnoreCase(searchText, pageable);

        //若未指定author，则查询全部公开的条目
        // if(authorEmail == null || authorEmail.trim().equals("")) {
        //     itemPage = articleDao.findByTitleContainsIgnoreCase(searchText, pageable);
        // }else{
        //     itemPage = articleDao.findByTitleContainsIgnoreCaseAndAuthors(searchText, authorEmail, pageable);
        // }

        //获取模型条目的创建者信息
        List<Article> items = itemPage.getContent();

        queryResult.put("list", items);
        queryResult.put("total", itemPage.getTotalElements());
        queryResult.put("pages", itemPage.getTotalPages());

        return queryResult;

    }

    public Page<Article> getArticleList(FindDTO findDTO){

        Pageable pageable = genericService.getPageable(findDTO);

        return articleDao.findAll(pageable);

    }

    public Article listById(String articleId){

        return articleDao.findFirstById(articleId);
    }

}
