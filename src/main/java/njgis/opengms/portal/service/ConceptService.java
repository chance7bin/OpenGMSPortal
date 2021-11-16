package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.ConceptDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.concept.ConceptDTO;
import njgis.opengms.portal.entity.po.Concept;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/02
 */
@Service
public class ConceptService {

    @Autowired
    GenericService genericService;

    @Autowired
    ConceptDao conceptDao;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserService userService;

    public JsonResult getConceptList(SpecificFindDTO repositoryQueryDTO){
        return ResultUtils.success(genericService.searchItems(repositoryQueryDTO, ItemTypeEnum.Concept));
    }


    public JsonResult insertConcept(ConceptDTO conceptAddDTO, String email) {
        Concept concept = new Concept();

        concept = (Concept) repositoryService.commonInsertPart(concept,conceptAddDTO, email, ItemTypeEnum.Concept);

        try {
            userService.updateUserResourceCount(email,ItemTypeEnum.Concept,"add");
        }catch (Exception e){
            return ResultUtils.error("update user concept resource fail");
        }

        return ResultUtils.success(conceptDao.insert(concept).getId());

    }

    public JsonResult updateConcept(ConceptDTO conceptUpdateDTO, String email, String id) {
        // JSONObject result = new JSONObject();
        // Concept concept = conceptDao.findFirstById(id);
        // String author = concept.getAuthor();
        // if (!concept.isLock()) {
        //
        //     concept = (Concept) repositoryService.commonUpdatePart(concept,conceptUpdateDTO,ItemTypeEnum.Concept);
        //
        //     if (author.equals(email)) {
        //         conceptDao.save(concept);
        //         result.put("method", "update");
        //         result.put("id", concept.getId());
        //     } else {
        //         result.put("method", "version");
        //         // result.put("oid", templateVersion.getOid());
        //
        //     }
        //     // return result;
        // } else {
        //     result = null;
        // }
        //
        // if(result==null){
        //     return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        // }
        // else {
        //     return ResultUtils.success(result);
        // }
        return repositoryService.commonUpdatePart(id,email,conceptUpdateDTO,ItemTypeEnum.Concept);

    }


    public JsonResult deleteConcept(String id, String email) {
        return repositoryService.commonDeletePart(id,email,ItemTypeEnum.Concept);
    }


    public JsonResult getConceptById(String id) {
        return repositoryService.getRepositoryById(id,conceptDao);
    }

}
