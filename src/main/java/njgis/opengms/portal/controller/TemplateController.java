package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/27
 */
@RestController
@RequestMapping(value = "/template")
public class TemplateController {

    @Autowired
    TemplateService templateService;

    /**
     * 获取template数据
     * @param findDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "获取template数据(全部或者根据name查询) [ /dataApplication/getTemplate ]")
    @RequestMapping(value = "/templateInfoByName", method = RequestMethod.POST)
    public JsonResult getTemplate(FindDTO findDTO) {
        return templateService.getTemplate(findDTO);
    }
}
