package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.service.ComputableModelService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
@Slf4j
@RestController
@RequestMapping(value = "/computableModel")
public class ComputableModelRestController {

    @Autowired
    ComputableModelService computableModelService;


    @ApiOperation(value = "查找部署的模型 [ /searchDeployedModel ]")
    @RequestMapping(value="/deployedModel",method= RequestMethod.POST)
    public JsonResult searchDeployedModel(@RequestBody FindDTO findDTO) {
        return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    }

}
