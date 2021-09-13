package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.SpatialReferenceDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.spatialReference.SpatialReferenceDTO;
import njgis.opengms.portal.entity.po.SpatialReference;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/03
 */
@Service
public class SpatialReferenceService {

    @Autowired
    SpatialReferenceDao spatialReferenceDao;

    @Autowired
    GenericService genericService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserService userService;

    public JsonResult getspatialReferenceById(String id) {
        return repositoryService.getRepositoryById(id,spatialReferenceDao);
    }

    public JsonResult getSpatialReferenceList(SpecificFindDTO repositoryQueryDTO){
        return ResultUtils.success(genericService.searchItems(repositoryQueryDTO, ItemTypeEnum.SpatialReference));
    }

    public JsonResult insertSpatial(SpatialReferenceDTO spatialReferenceDTO, String email) {
        SpatialReference spatialReference = new SpatialReference();

        spatialReference = (SpatialReference) repositoryService.commonInsertPart(spatialReference,spatialReferenceDTO, email, ItemTypeEnum.SpatialReference);

        try {
            userService.updateUserResourceCount(email,ItemTypeEnum.SpatialReference,"add");
        }catch (Exception e){
            return ResultUtils.error("update user spatial resource fail");
        }

        return ResultUtils.success(spatialReferenceDao.insert(spatialReference).getId());

    }

    public JsonResult updateSpatial(SpatialReferenceDTO spatialReferenceDTO, String email, String id){
        return repositoryService.commonUpdatePart(id,email,spatialReferenceDTO,ItemTypeEnum.SpatialReference);
    }


    public JsonResult deleteSpatial(String id, String email) {
        return repositoryService.commonDeletePart(id,email,ItemTypeEnum.SpatialReference);
    }
}
