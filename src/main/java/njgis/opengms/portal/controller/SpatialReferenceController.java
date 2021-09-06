package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.spatialReference.SpatialReferenceDTO;
import njgis.opengms.portal.service.SpatialReferenceService;
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

}
