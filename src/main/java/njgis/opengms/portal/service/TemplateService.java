package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.TemplateDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.ResultDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.community.template.TemplateDTO;
import njgis.opengms.portal.entity.po.Template;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @Date 2021/08/27
 */
@Service
public class TemplateService {

    @Autowired
    TemplateDao templateDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    RepositoryService repositoryService;

    @Value("${resourcePath}")
    private String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;


    public JsonResult getTemplateList(SpecificFindDTO repositoryQueryDTO){
        return ResultUtils.success(genericService.searchItems(repositoryQueryDTO, ItemTypeEnum.Template));
    }

    /**
     * 在template中增加关联的method
     * @param templateId
     * @param dataMethodId
     * @return void
     * @Author bin
     **/
    public void addRelatedMethod(String templateId, String dataMethodId){
        Template template = templateDao.findFirstById(templateId);
        if(template != null){
            List<String> relatedMethods = template.getRelatedMethods();
            if(relatedMethods == null){
                relatedMethods = new ArrayList<>();
            }
            relatedMethods.add(dataMethodId);
            template.setRelatedMethods(relatedMethods);
            templateDao.save(template);
        }
    }

    /**
     * 在template中移除关联的method
     * @param templateId
     * @param dataMethodId
     * @return void
     * @Author bin
     **/
    public void removeRelatedMethod(String templateId, String dataMethodId){
        Template template = templateDao.findFirstById(templateId);
        if(template != null){
            List<String> relatedMethods = template.getRelatedMethods();
            if(relatedMethods == null){
                relatedMethods = new ArrayList<>();
            }
            relatedMethods.remove(dataMethodId);
            template.setRelatedMethods(relatedMethods);
            templateDao.save(template);
        }
    }


    /**
     * 获取template数据
     * @param findDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getTemplate(SpecificFindDTO findDTO) {
        JsonResult result;
        Sort sort = Sort.by(findDTO.getAsc() == false ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(findDTO.getPage() - 1, 5, sort);
        Page<ResultDTO> templates = templateDao.findByNameContainsIgnoreCase(findDTO.getSearchText(), pageable);

//        List<Template> templates = templateDao.findAll();
        if (templates != null) {
            result = ResultUtils.success(templates);
        } else {
            result = ResultUtils.error();
        }
        return result;
    }

    /**
     * 新增template
     * @param templateAddDTO
     * @param email
     * @return njgis.opengms.portal.entity.po.Template
     * @Author bin
     **/
    public JsonResult insertTemplate(TemplateDTO templateAddDTO, String email){
        Template template = new Template();

        template = (Template) repositoryService.commonInsertPart(template, templateAddDTO ,email, ItemTypeEnum.Template);

        try {
            userService.updateUserResourceCount(email,ItemTypeEnum.Template,"add");
        }catch (Exception e){
            return ResultUtils.error("update user template resource fail");
        }

        return ResultUtils.success(templateDao.insert(template).getId());
    }

    /**
     * 更新template
     * @param templateUpdateDTO
     * @param email
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JsonResult updateTemplate(TemplateDTO templateUpdateDTO, String email, String id) {
        // JSONObject result = new JSONObject();
        // Template template = templateDao.findFirstById(id);
        // String author = template.getAuthor();
        // if (!template.isLock()) {
        //
        //     template = (Template) repositoryService.commonUpdatePart(template,templateUpdateDTO,ItemTypeEnum.Template);
        //
        //     if (author.equals(email)) {
        //         templateDao.save(template);
        //         result.put("method", "update");
        //         result.put("id", template.getId());
        //     } else {
        //         // TODO: 2021/8/31 不是作者更新的还没做
        //         result.put("method", "version");
        //         // result.put("id", templateVersion.getId());
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
        return repositoryService.commonUpdatePart(id,email,templateUpdateDTO,ItemTypeEnum.Template);

    }


    /**
     * 删除template
     * @param id
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult deleteTemplate(String id, String email) {
        return repositoryService.commonDeletePart(id,email,ItemTypeEnum.Template);
    }


    /**
     * 根据用户查找template
     * @param email
     * @param page
     * @param sortType
     * @param asc
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getTemplatesByUserId(String email, int page, String sortType, int asc) {

        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<ResultDTO> templates = templateDao.findByAuthor(pageable, email);

        JSONObject TemplateObject = new JSONObject();
        TemplateObject.put("count", templates.getTotalElements());
        TemplateObject.put("templates", templates.getContent());

        return TemplateObject;

    }

    public JsonResult getTemplateById(String id) {
        return repositoryService.getRepositoryById(id,templateDao);
    }

}
