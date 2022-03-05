package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.community.spatialReference.SpatialReferenceDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.RepositoryService;
import njgis.opengms.portal.service.SpatialReferenceService;
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
@RequestMapping(value = "/spatialReference")
public class SpatialReferenceController {

    @Autowired
    SpatialReferenceService spatialReferenceService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    GenericService genericService;


    /**
     * 根据id获取spatialReference信息
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "根据id获取spatialReference信息 [ /repository/getSpatialInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getSpatialInfo(@PathVariable("id") String id){
       return spatialReferenceService.getspatialReferenceById(id);
    }

    /**
     * spatialReference列表信息
     * @param repositoryQueryDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "spatialReference列表信息 [ /repository/getSpatialReferenceList ] 删除[/repository/searchSpatialReference]接口")
    @RequestMapping(value="/spatialReferenceList",method = RequestMethod.POST)
    public JsonResult getSpatialReferenceList(@RequestBody SpecificFindDTO repositoryQueryDTO){
        return spatialReferenceService.getSpatialReferenceList(repositoryQueryDTO);
    }

    /**
     * 新增spatialReference
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增spatialReference [ /repository/addSpatialReference ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addSpatialReference(MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        SpatialReferenceDTO addDTO=JSONObject.toJavaObject(jsonObject, SpatialReferenceDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return spatialReferenceService.insertSpatial(addDTO,email);
    }

    /**
     * 更新spatialReference
     * @param id
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新spatialReference [ /repository/updateSpatialReference ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateSpatialReference(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException{
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        SpatialReferenceDTO spatialReferenceDTO=JSONObject.toJavaObject(jsonObject, SpatialReferenceDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return spatialReferenceService.updateSpatial(spatialReferenceDTO,email,id);
    }

    /**
     * 删除spatialReference
     * @param id
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "删除spatialReference [ /repository/deleteSpatialReference ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteSpatialReference(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return spatialReferenceService.deleteSpatial(id,email);
    }


    /**
     * 根据用户得到spatialReference
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户得到spatialReference [ /spatial/listSpatialsByOid ]")
    @RequestMapping (value = "/listByUser",method = RequestMethod.POST)
    public JsonResult listByUserOid(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByUser(findDTO, email, ItemTypeEnum.SpatialReference));
    }


    /**
     * 根据名称和用户得到concept
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据名称和用户得到spatialReference [ /spatial/searchByNameByOid ]")
    @RequestMapping(value="/listByNameAndUser",method= RequestMethod.GET)
    public JsonResult searchByTitle(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByNameAndUser(findDTO, email, ItemTypeEnum.SpatialReference));
    }

    @ApiOperation(value = "getWKT [ /spatial/listSpatialsByOid ]")
    @RequestMapping(value="/getWKT",method= RequestMethod.GET)
    JsonResult searchByTitle(@RequestParam(value = "id")String id){
        return ResultUtils.success(spatialReferenceService.getWKT(id));
    }

    @ApiOperation(value = "getSpatialReference [ /spatial/listSpatialsByOid ]")
    @RequestMapping(value="/getSpatialReference",method= RequestMethod.GET)
    JsonResult getSpatialReference(@RequestParam(value="asc") int asc,
                                   @RequestParam(value = "page") int page,
                                   @RequestParam(value = "size") int size)
    {
        return ResultUtils.success(spatialReferenceService.getSpatialReference(asc,page,size));
    }


    @ApiOperation(value = "searchSpatialReference [ /spatial/listSpatialsByOid ]")
    @RequestMapping(value="/searchSpatialReference",method= RequestMethod.GET)
    JsonResult searchSpatialReference(@RequestParam(value="asc") int asc,
                                      @RequestParam(value = "page") int page,
                                      @RequestParam(value = "size") int size,
                                      @RequestParam(value = "searchText") String searchText)
    {
        return ResultUtils.success(spatialReferenceService.searchSpatialReference(asc,page,size,searchText));
    }


    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.SpatialReference,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的模型条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.SpatialReference,findDTO, true));

    }

}
