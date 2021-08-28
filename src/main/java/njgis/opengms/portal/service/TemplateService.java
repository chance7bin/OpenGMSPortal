package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.TemplateDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.template.TemplateResultDTO;
import njgis.opengms.portal.entity.po.Template;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public JsonResult getTemplate(FindDTO findDTO) {
        JsonResult result;
        Sort sort = Sort.by(findDTO.getAsc() == false ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(findDTO.getPage(), 5, sort);
        Page<TemplateResultDTO> templates = templateDao.findByNameContainsIgnoreCase(findDTO.getSearchText(), pageable);

//        List<Template> templates = templateDao.findAll();
        if (templates != null) {
            result = ResultUtils.success(templates);
        } else {
            result = ResultUtils.error();
        }
        return result;
    }


}
