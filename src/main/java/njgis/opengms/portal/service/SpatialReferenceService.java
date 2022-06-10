package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.SpatialReferenceClassificationDao;
import njgis.opengms.portal.dao.SpatialReferenceDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.community.spatialReference.SpatialReferenceDTO;
import njgis.opengms.portal.entity.po.SpatialReference;
import njgis.opengms.portal.entity.po.SpatialReferenceClassification;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    SpatialReferenceClassificationDao spatialReferenceClassificationDao;

    @Autowired
    GenericService genericService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

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


    public JSONObject getWKT(String id) {
        SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);

        String wkt = spatialReference.getWkt();
        String wktname = spatialReference.getWkname();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wktname",wktname);
        jsonObject.put("wkt",wkt);

        return jsonObject;
    }

    public JSONObject getSpatialReference(int asc,int page,int size){
        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page, size, sort);

        List<SpatialReferenceClassification> classifications = spatialReferenceClassificationDao.findAllByParentId("58340c92-d74f-4d81-8a80-e4fcff286008");

        List<String> classificationIds = new ArrayList<String>();

        for (SpatialReferenceClassification item:classifications){
            classificationIds.add(item.getId());
        }

        Page<SpatialReference> spatialResultDTOPage = spatialReferenceDao.findAllByClassificationsIn(classificationIds,pageable);

        JSONArray j_spatialReferences = new JSONArray();

        for(SpatialReference spatialResultDTO:spatialResultDTOPage.getContent()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",spatialResultDTO.getId());
            jsonObject.put("name",spatialResultDTO.getName());
            jsonObject.put("wkt",spatialResultDTO.getWkt());
            jsonObject.put("wkname",spatialResultDTO.getWkname());

            User user = userDao.findFirstByEmail(spatialResultDTO.getAuthor());
            if(user!=null){
                jsonObject.put("author",user.getName());
            }else{
                jsonObject.put("author","unknown");
            }
            j_spatialReferences.add(jsonObject);

        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",spatialResultDTOPage.getTotalElements());
        jsonObject.put("content",j_spatialReferences);

        return jsonObject;
    }

    public JSONObject searchSpatialReference(int asc,int page,int size,String searchText){
        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page, size, sort);

        List<SpatialReferenceClassification> classifications = spatialReferenceClassificationDao.findAllByParentId("58340c92-d74f-4d81-8a80-e4fcff286008");

        List<String> classificationIds = new ArrayList<String>();

        for (SpatialReferenceClassification item:classifications){
            classificationIds.add(item.getId());
        }

        Page<SpatialReference> spatialResultDTOPage = spatialReferenceDao.findAllByNameLikeIgnoreCaseAndClassificationsIn(searchText,classificationIds,pageable);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",spatialResultDTOPage.getTotalElements());
        jsonObject.put("content",spatialResultDTOPage.getContent());

        return jsonObject;
    }
}
