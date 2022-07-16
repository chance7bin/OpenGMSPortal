package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.community.theme.ThemeDTO;
import njgis.opengms.portal.entity.po.Theme;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.ThemeService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/02
 */
@RestController
@RequestMapping(value = "/theme")
public class ThemeController {


    @Autowired
    ThemeService themeService;

    @Autowired
    GenericService genericService;


    @ApiOperation(value = "得到Maintainer [ /theme/getMaintainer/{themeOid} ]")
    @RequestMapping(value = "/maintainer/{id}", method = RequestMethod.GET)
    public JsonResult getMaintainer(@PathVariable(value = "id") String id){
        return ResultUtils.success(themeService.getMaintainer(id));
    }

    @ApiOperation(value = "得到Theme的信息 [ /theme/getInfo/{id} ]")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.GET)
    public JsonResult getInfo(@PathVariable ("id") String id) throws InvocationTargetException {
        return ResultUtils.success(themeService.getById(id));
    }

    // @ApiOperation(value = "得到modelItem的信息 [ /getModelItem ]")
    // @RequestMapping(value = "/modelItem/{id}",method = RequestMethod.GET)
    // JsonResult getModelItem(@PathVariable(value = "id") String id){
    //     return ResultUtils.success(themeService.getModelItem(id));
    // }

    @RequestMapping(value = "/getModelItem",method = RequestMethod.GET)
    JsonResult getModelItem(@RequestParam(value = "oid") String id){
        JSONObject result = themeService.getModelItem(id);
        return ResultUtils.success(result);
    }
    @RequestMapping(value = "/getDataItem",method = RequestMethod.GET)
    JsonResult getDataItem(@RequestParam(value = "oid") String id){
        JSONObject result = themeService.getDataItem(id);
        return ResultUtils.success(result);
    }
    @RequestMapping(value = "/getDataMethod",method = RequestMethod.GET)
    JsonResult getDataMethod(@RequestParam(value = "oid") String id){
        JSONObject result = themeService.getDataMethod(id);
        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "新增theme条目")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addTheme(MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ThemeDTO themeAddDTO=JSONObject.toJavaObject(jsonObject, ThemeDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();

        themeAddDTO.setCreator_name(session.getAttribute("name").toString());
        themeAddDTO.setCreator_eid(email);

        Theme theme = themeService.insertTheme(themeAddDTO, email);

        if (theme == null){
            return ResultUtils.error("update user theme resource fail");
        }

        return ResultUtils.success(theme.getId());
    }

    @LoginRequired
    @ApiOperation(value = "删除theme [ /theme/delete ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteTheme(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return themeService.deleteTheme(id,email);
    }


    @LoginRequired
    @ApiOperation(value = "更新theme [ /theme/update ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateTheme(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ThemeDTO updateDTO=JSONObject.toJavaObject(jsonObject, ThemeDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return themeService.updateTheme(updateDTO,email,id);
    }


    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Theme,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的模型条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Theme,findDTO, true));

    }

}
