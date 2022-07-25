package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.annotation.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.community.unit.UnitDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.RepositoryService;
import njgis.opengms.portal.service.UnitService;
import njgis.opengms.portal.utils.ResultUtils;
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
 * @Date 2021/09/03
 */
@RestController
@RequestMapping(value = "/unit")
public class UnitController {

    @Autowired
    UnitService unitService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    GenericService genericService;

    /**
     * unit列表信息
     * @param repositoryQueryDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "unit列表信息 [ /repository/getUnitList ] 删除[/repository/searchUnit]接口")
    @RequestMapping(value={"/unitList", "/list"},method = RequestMethod.POST)
    public JsonResult getUnitList(@RequestBody SpecificFindDTO repositoryQueryDTO){
        return unitService.getUnitList(repositoryQueryDTO);
    }

    /**
     * 根据id获取unit信息
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "根据id获取unit信息 [ /repository/getUnitInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getUnitInfo(@PathVariable ("id") String id){
        return unitService.getUnitById(id);
    }

    
    /**
     * 新增unit
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增unit [ /repository/addUnit ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addUnit(MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        UnitDTO addDTO=JSONObject.toJavaObject(jsonObject, UnitDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return unitService.insertUnit(addDTO,email);
    }


    /**
     * 更新unit
     * @param id
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新unit [ /repository/updateUnit ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateUnit(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        UnitDTO updateDTO=JSONObject.toJavaObject(jsonObject, UnitDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return unitService.updateUnit(updateDTO,email,id);
    }

    /**
     * 删除unit
     * @param id
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "删除unit [ /repository/deleteUnit ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteUnit(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return unitService.deleteUnit(id,email);
    }


    /**
     * 根据用户得到unit
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户得到unit [ /unit/listUnitsByOid ]")
    @RequestMapping (value = "/listByUser",method = RequestMethod.POST)
    public JsonResult listByUserOid(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByUser(findDTO, email, ItemTypeEnum.Unit));
    }


    /**
     * 根据名称和用户得到unit
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据名称和用户得到unit [ /unit/searchByNameByOid ]")
    @RequestMapping(value="/listByNameAndUser",method= RequestMethod.GET)
    public JsonResult searchByTitle(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByNameAndUser(findDTO, email, ItemTypeEnum.Unit));
    }

    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Unit,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的模型条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody UserFindDTO findDTO, HttpServletRequest request) {
        if ("".equals(findDTO.getAuthorEmail()) || findDTO.getAuthorEmail() == null){
            String email = request.getSession().getAttribute("email").toString();
            findDTO.setAuthorEmail(email);
        }
        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Unit,findDTO, true));

    }

}
