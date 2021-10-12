package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.GenericItemDao;
import njgis.opengms.portal.dao.VersionDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/06
 */
@Service
public class VersionService {

    @Autowired
    VersionDao versionDao;

    @Autowired
    GenericService genericService;

    @Autowired
    NoticeService noticeService;


    /**
     * 添加审核版本
     * @param item 修改的条目数据
     * @param editor 修改者
     * @return njgis.opengms.portal.entity.po.Version
     * @Author bin
     **/
    public Version addVersion(PortalItem item, String editor, String originalItemName){
        Version version = new Version();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        version.setItemId(item.getId());
        version.setName(sdf.format(date) + "@" + originalItemName);
        version.setContent(item);
        version.setEditor(editor);
        version.setItemCreator(item.getAuthor());
        version.setCreateTime(date);
        Class<? extends PortalItem> aClass = item.getClass();
        String name = aClass.getName();
        String[] nameArr = name.split("\\.");
        String type = nameArr[nameArr.length - 1];
        ItemTypeEnum itemType = ItemTypeEnum.getItemTypeByName(type);
        version.setType(itemType);
        return versionDao.insert(version);

    }

    /**
     * 审核通过
     * @param versionId 审核版本id
     * @param reviewer 审核者
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult accept(String versionId, String reviewer){
        Version version = versionDao.findFirstById(versionId);
        Date date = new Date();
        version.setReviewer(reviewer);
        version.setReviewTime(date);
        version.setStatus(1);
        PortalItem content = version.getContent();
        content.setLock(false);
        //版本更新
        List<String> versions = content.getVersions();
        versions = versions == null ? new ArrayList<>() : versions;
        versions.add(version.getId());
        content.setVersions(versions);

        //贡献者更新
        List<String> contributors = content.getContributors();
        contributors = contributors == null ? new ArrayList<>() : contributors;
        if(!contributors.contains(version.getEditor())){
            contributors.add(version.getEditor());
        }
        content.setContributors(contributors);

        JSONObject factory = genericService.daoFactory(version.getType());
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        try {
            versionDao.save(version);
            itemDao.save(content);
            List<String> recipientList;
            if (version.getEditor().equals(version.getReviewer()))
                recipientList = Arrays.asList(version.getItemCreator(),version.getEditor());
            else
                recipientList = Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer());
            noticeService.sendNoticeContainRoot(reviewer, OperationEnum.Accept,version.getId(),recipientList);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
        return ResultUtils.success();
    }


    /**
     * 审核未通过
     * @param versionId 审核版本id
     * @param reviewer 审核者
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult reject(String versionId, String reviewer){
        Version version = versionDao.findFirstById(versionId);
        Date date = new Date();
        version.setReviewer(reviewer);
        version.setReviewTime(date);
        version.setStatus(-1);

        JSONObject factory = genericService.daoFactory(version.getType());
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        PortalItem content = (PortalItem) itemDao.findFirstById(version.getItemId());
        content.setLock(false);
        try {
            versionDao.save(version);
            itemDao.save(content);
            List<String> recipientList;
            if (version.getEditor().equals(version.getReviewer()))
                recipientList = Arrays.asList(version.getItemCreator(),version.getEditor());
            else
                recipientList = Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer());
            noticeService.sendNoticeContainRoot(reviewer, OperationEnum.Reject,version.getId(),recipientList);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
        return ResultUtils.success();
    }


    /**
     * 得到审核版本方法的公共代码块
     * @param all 版本列表
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersionsCommonPart(List<Version> all){
        JSONObject result = new JSONObject();
        List<Version> uncheck = new ArrayList<>();
        List<Version> accept = new ArrayList<>();
        List<Version> reject = new ArrayList<>();
        // List<Version> edit = new ArrayList<>();
        for (Version version : all) {
            switch (version.getStatus()){
                case 0:{
                    uncheck.add(version);
                    break;
                }
                case 1:{
                    accept.add(version);
                }
                case -1:{
                    reject.add(version);
                }
            }
        }
        result.put("all",all);
        result.put("uncheck",uncheck);
        result.put("accept",accept);
        result.put("reject",reject);

        return ResultUtils.success(result);
    }


    /**
     * 得到审核版本
     * @param findDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersions(FindDTO findDTO){
        try {
            List<Version> all;
            if (findDTO == null){
                all = versionDao.findAll();
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                all = versionDao.findAll(pageable).getContent();
            }
            return getVersionsCommonPart(all);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 根据状态得到审核版本
     * @param findDTO
     * @param status
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersionByConcreteStatus(FindDTO findDTO, int status) {
        try {
            if (findDTO == null){
                return ResultUtils.success(versionDao.findAllByStatus(status));
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                return ResultUtils.success(versionDao.findAllByStatus(status,pageable));
            }

        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
    }


    /**
     * 得到用户已提交审核版本的数据
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserEditVersion(String email) {

        try {
            return ResultUtils.success(versionDao.findAllByEditor(email));
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

    }

    /**
     * 得到用户待审核版本的数据
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserReviewVersion(String email) {
        try {
            if (email.equals("opengms@njnu.edu.cn")){
                return ResultUtils.success(versionDao.findAll());
            }
            return ResultUtils.success(versionDao.findAllByItemCreator(email));
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

    }

    /**
     * 根据版本状态、条目类型和操作类型得到版本
     * @param status 版本状态: 0:待审核 -1:被拒绝 1:通过 2:原始版本（或忽略）
     * @param type  条目大类: Model/Data/Community/Theme
     * @param findDTO
     * @param email
     * @param op 操作类型: edit(提交的版本,你是条目的修改者) / review(待审核的版本,你是条目的创建者)
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserVersionByStatusAndByTypeAndByOperation(int status, String type, FindDTO findDTO, String email, String op) {
        Pageable pageable = genericService.getPageable(findDTO);
        List<ItemTypeEnum> findType = new ArrayList<>();
        switch (type){
            case "Model":{
                findType.add(ItemTypeEnum.ModelItem);
                findType.add(ItemTypeEnum.ConceptualModel);
                findType.add(ItemTypeEnum.LogicalModel);
                findType.add(ItemTypeEnum.ComputableModel);
                break;
            }
            case "Data":{
                findType.add(ItemTypeEnum.DataItem);
                findType.add(ItemTypeEnum.DataHub);
                findType.add(ItemTypeEnum.DataMethod);
                break;
            }
            case "Community":{
                findType.add(ItemTypeEnum.Concept);
                findType.add(ItemTypeEnum.SpatialReference);
                findType.add(ItemTypeEnum.Template);
                findType.add(ItemTypeEnum.Unit);
                break;
            }
            case "Theme":{
                findType.add(ItemTypeEnum.Theme);
                break;
            }
            default:{
                return ResultUtils.error("invalid item type");
            }
        }

        try {
            if (op.equals("edit")){
                return ResultUtils.success(versionDao.findAllByStatusAndEditorAndTypeIn(status,email,findType,pageable));
            }
            else if (op.equals("review")) {
                // 如果登录用户是门户的话那可以得到所有对应状态的版本
                if (email.equals("opengms@njnu.edu.cn")){
                    return ResultUtils.success(versionDao.findAllByStatusAndTypeIn(status,findType,pageable));
                }
                return ResultUtils.success(versionDao.findAllByStatusAndItemCreatorAndTypeIn(status,email,findType,pageable));
            }
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

        return ResultUtils.error("invalid operation");

    }

}
