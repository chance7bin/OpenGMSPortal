package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.template.TemplateDTO;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.TemplateService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

    @Autowired
    GenericService genericService;




    /**
     * 获取template数据
     * @param findDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "获取template数据(全部或者根据name查询) [ /dataApplication/getTemplate ]")
    @RequestMapping(value = "/templateInfo", method = RequestMethod.POST)
    public JsonResult getTemplate(SpecificFindDTO findDTO) {
        return templateService.getTemplate(findDTO);
    }


    /**
     * 根据id获取template信息
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "根据id获取template信息 [ /repository/getTemplateInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getTemplateInfo(@PathVariable ("id") String id){
        return templateService.getTemplateById(id);
    }

    /**
     * template列表信息
     * @param repositoryQueryDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "template列表信息 [ /repository/getTemplateList ] 删除[/repository/searchTemplate]接口")
    @RequestMapping(value="/templateList",method = RequestMethod.POST)
    public JsonResult getTemplateList(@RequestBody SpecificFindDTO repositoryQueryDTO){
        return templateService.getTemplateList(repositoryQueryDTO);
    }



    /**
     * 新增template
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增template [ /repository/addTemplate ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addTemplate(MultipartFile info, HttpServletRequest request) throws IOException {
        // MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // MultipartFile file=multipartRequest.getFile("info");
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        TemplateDTO templateAddDTO=JSONObject.toJavaObject(jsonObject, TemplateDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return templateService.insertTemplate(templateAddDTO,email);
    }

    /**
     * 更新template
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新template [ /repository/updateTemplate ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateTemplate(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException{
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        TemplateDTO templateUpdateDTO=JSONObject.toJavaObject(jsonObject, TemplateDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return templateService.updateTemplate(templateUpdateDTO,email,id);
    }

    /**
     * 删除template
     * @param id
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "删除template [ /repository/deleteTemplate ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteTemplate(@PathVariable(value="id") String id, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return templateService.deleteTemplate(id,email);
    }


}
