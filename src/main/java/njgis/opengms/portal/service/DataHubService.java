package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.ResultDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.data.dataItem.DataItemDTO;
import njgis.opengms.portal.entity.po.DataHub;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/25
 */
@Service
public class DataHubService {

    @Autowired
    DataItemService dataItemService;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    DataCategorysDao dataCategorysDao;

    @Autowired
    GenericService genericService;

    @Autowired
    UserDao userDao;

    @Autowired
    NoticeService noticeService;

    @Autowired
    VersionService versionService;

    @Autowired
    RedisService redisService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;


    public JsonResult getHubs(SpecificFindDTO dataHubsFindDTO){
        return ResultUtils.success(genericService.searchItems(dataHubsFindDTO, ItemTypeEnum.DataHub));
    }


    /**
     * 修改hub条目
     * @param dataItemUpdateDTO
     * @param email
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject updateDataHubs(DataItemDTO dataItemUpdateDTO, String email, String id) {
        // JSONObject dao = genericService.daoFactory(ItemTypeEnum.DataHub);
        return dataItemService.updateItem(dataItemUpdateDTO,email,ItemTypeEnum.DataHub,id);
    }

    public Page<ResultDTO> getUsersUploadDataHubs(String author, Integer page, Integer pagesize, Integer asc) {

        boolean as = false;
        if (asc == 1) {
            as = true;
        }

        Sort sort = Sort.by(as ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, pagesize, sort);
        return dataHubDao.findByAuthor(pageable, author);

    }


    /**
     * 删除dataHub
     * @param id
     * @param email
     * @return int
     * @Author bin
     **/
    public JsonResult delete(String id, String email){
        DataHub data = new DataHub();
        data = (DataHub) genericService.getById(id,dataHubDao);

        if(!data.getAuthor().equals(email))
            return ResultUtils.error("Unauthorized");

        // List<String> relatedModels = data.getRelatedModels();
        // if (relatedModels!=null)
        //     for (int i = 0; i < relatedModels.size(); i++) {
        //         ModelItem modelItem = modelItemDao.findFirstByOid(relatedModels.get(i));
        //         modelItem.getRelatedData().remove(id);
        //         modelItemDao.save(modelItem);
        //     }

        try {
            // dataHubDao.deleteById(id);
            redisService.deleteItem(data, ItemTypeEnum.DataHub);
            userService.updateUserResourceCount(email, ItemTypeEnum.DataHub, "delete");
        }catch (Exception e){
            return ResultUtils.error("delete error");
        }

        return ResultUtils.success();
    }


    /**
     * 根据条目名和当前用户得到数据
     * @param findDTO
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult searchByNameAndAuthor(SpecificFindDTO findDTO,String email){

        return ResultUtils.success(genericService.searchItemsByUser(findDTO, ItemTypeEnum.DataHub, email));

    }

    public JsonResult setRelation(String id, List<String> relations, String email) {

        DataHub item = dataHubDao.findFirstById(id);

        List<String> versions = item.getVersions();
        String originalItemName = item.getName();
        JSONObject result = new JSONObject();
        if (!item.isLock()){

            String author = item.getAuthor();
            Date now = new Date();

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                dataHubDao.save(item);
            } else {
                if (versions == null || versions.size() == 0) {
                    Version version = versionService.addVersion(item, email, originalItemName);
                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            }

            List<String> oriRelatedModels = item.getRelatedModels();
            item.setRelatedModels(relations);
            item.setLastModifyTime(now);
            item.setLastModifier(email);


            Version new_version = versionService.addVersion(item, email,originalItemName);
            if (author.equals(email)){
                versions.add(new_version.getId());
                item.setVersions(versions);
                dataItemService.updateModelRelate(relations, oriRelatedModels, ItemTypeEnum.DataHub,id);
                item.setRelatedModels(relations);
                // dataHubDao.save(item);
                redisService.saveItem(item,ItemTypeEnum.DataHub);
                result.put("type","suc");

                return ResultUtils.success(result);
            }else {

                //发送通知
                List<String> recipientList = new ArrayList<>();
                recipientList.add(author);
                recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());
                recipientList = noticeService.addPortalAdmins(recipientList);
                recipientList = noticeService.addPortalRoot(recipientList);
                noticeService.sendNoticeContains(email, OperationEnum.Edit,ItemTypeEnum.Version,new_version,recipientList);
                result.put("type","version");
                return ResultUtils.success(result);
            }


        } else {
            result.put("type","version");
            return ResultUtils.success(result);
        }

    }

    public JSONArray getRelation(String id) {
        JSONArray result = new JSONArray();
        DataHub dataHub = dataHubDao.findFirstById(id);
        List<String> relatedModels = dataHub.getRelatedModels();

        for (String modelId : relatedModels) {
            ModelItem modelItem = modelItemDao.findFirstById(modelId);
            JSONObject object = new JSONObject();
            object.put("id", modelItem.getId());
            object.put("name", modelItem.getName());
            User user = userDao.findFirstByEmail(modelItem.getAuthor());
            object.put("author", user.getName());
            object.put("author_email", user.getEmail());
            result.add(object);
        }

        return result;

    }
}
