package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.community.template.TemplateDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemFindDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.ConceptualModelService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @Description Conceptual Model Controller
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/conceptualModel")
public class ConceptualModelRestController {

    @Autowired
    GenericService genericService;

    @Autowired
    ConceptualModelService conceptualModelService;

    /**
     * @Description 根据id获取概念模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    @ApiOperation(value = "根据id获取概念模型详情页面")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    ModelAndView get(@PathVariable("id") String id, HttpServletRequest request){
        PortalItem portalItem = genericService.getPortalItem(id, ItemTypeEnum.ConceptualModel);
        ModelAndView modelAndView = genericService.checkPrivatePageAccessPermission(portalItem, Utils.checkLoginStatus(request));
        if(modelAndView != null){
            return modelAndView;
        }else {
            return conceptualModelService.getPage(id);
        }
    }

    /**
     * @Description 获取概念模型信息
     * @param id
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 21/10/14
     **/
    @ApiOperation(value = "根据id获取概念模型信息(修改item时调用的接口) [ /getInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    JsonResult getInfo(@PathVariable ("id") String id){
        return ResultUtils.success(conceptualModelService.getInfo(id));
    }

    @LoginRequired
    @ApiOperation(value = "新增conceptualModel [ /add ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addConceptalModel(MultipartFile info, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("imgFiles");
        MultipartFile file=multipartRequest.getFile("conceptualModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);

        HttpSession session=request.getSession();
        String email=session.getAttribute("uid").toString();

        JSONObject result=conceptualModelService.insert(files,jsonObject,email);

        return ResultUtils.success(result);

    }

    @LoginRequired
    @ApiOperation(value = "更新conceptualModel [ /update ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateConcept(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("imgFiles");
        MultipartFile file=multipartRequest.getFile("conceptualModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result=conceptualModelService.update(files,jsonObject,email);
        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "删除conceptualModel [ /delete ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteConcept(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return conceptualModelService.delete(id,email);
    }

    @ApiOperation(value = "条目查询 ")
    @RequestMapping (value="/list",method = RequestMethod.POST)
    public JsonResult queryList(SpecificFindDTO queryDTO) {
        return ResultUtils.success(genericService.searchItems(queryDTO, ItemTypeEnum.ConceptualModel));
    }

    @LoginRequired
    @ApiOperation(value = "查询由登录用户创建的所有条目")
    @RequestMapping (value="/listByAuthor",method = RequestMethod.POST)
    public JsonResult queryListByAuthor(FindDTO queryDTO, HttpServletRequest request) {
        String email = Utils.checkLoginStatus(request);
        if(email != null) {
            SpecificFindDTO specificFindDTO = (SpecificFindDTO) queryDTO;
            specificFindDTO.setCurQueryField("author");
            specificFindDTO.setSearchText(email);
            return ResultUtils.success(genericService.searchItems(specificFindDTO, ItemTypeEnum.ConceptualModel));
        }else{
            return ResultUtils.unauthorized();
        }
    }
}
