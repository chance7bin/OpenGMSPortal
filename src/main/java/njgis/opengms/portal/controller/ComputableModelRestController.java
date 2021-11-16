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
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.ComputableModelService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
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
 * @Description Computable Model Controller
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/computableModel")
public class ComputableModelRestController {

    @Autowired
    GenericService genericService;

    @Autowired
    ComputableModelService computableModelService;

    @Autowired
    UserService userService;

    /**
     * @Description 根据id获取计算模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    @ApiOperation(value = "根据id获取计算模型详情页面")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    ModelAndView get(@PathVariable("id") String id, HttpServletRequest request){
        PortalItem portalItem = genericService.getPortalItem(id, ItemTypeEnum.ComputableModel);
        ModelAndView modelAndView = genericService.checkPrivatePageAccessPermission(portalItem, Utils.checkLoginStatus(request));
        if(modelAndView != null){
            return modelAndView;
        }else {
            return computableModelService.getPage(id);
        }
    }

    /**
     * @Description 获取计算模型信息
     * @param id
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 21/10/14
     **/
    @ApiOperation(value = "根据id获取计算模型信息(修改item时调用的接口) [ /getInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    JsonResult getInfo(@PathVariable ("id") String id){
        return ResultUtils.success(computableModelService.getInfo(id));
    }

    /**
     * @Description 获取计算模型信息
     * @param
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 21/10/14
     **/
    @LoginRequired
    @ApiOperation(value = "创建computableModel [ /add ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="创建item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping (value="/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    JsonResult add(HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("resources");
        MultipartFile file=multipartRequest.getFile("computableModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        JSONObject result=computableModelService.insert(files,jsonObject,email);
        if(result.getInteger("code")==1){
            userService.ItemCountPlusOne(email, ItemTypeEnum.ComputableModel);
        }

        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "更新computableModel [ /update ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult update(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("resources");
        MultipartFile file=multipartRequest.getFile("computableModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        JSONObject result=computableModelService.update(files,jsonObject,email);

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "删除computableModel [ /delete ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult delete(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return computableModelService.delete(id,email);
    }

    @ApiOperation(value = "条目查询 ")
    @RequestMapping (value="/list",method = RequestMethod.POST)
    public JsonResult queryList(SpecificFindDTO queryDTO) {
        return ResultUtils.success(genericService.searchItems(queryDTO, ItemTypeEnum.ComputableModel));
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
            return ResultUtils.success(genericService.searchItems(specificFindDTO, ItemTypeEnum.ComputableModel));
        }else{
            return ResultUtils.unauthorized();
        }
    }

    @ApiOperation(value = "查找部署的模型 [ /searchDeployedModel ]")
    @RequestMapping(value="/deployedModel",method= RequestMethod.POST)
    public JsonResult searchDeployedModel(@RequestBody FindDTO findDTO) {
        return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    }

}
