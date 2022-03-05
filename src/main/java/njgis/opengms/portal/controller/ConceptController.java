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
import njgis.opengms.portal.entity.dto.community.concept.ConceptDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.ConceptService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.RepositoryService;
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
 * @Date 2021/09/01
 */
@RestController
@RequestMapping(value = "/concept")
public class ConceptController {


    @Autowired
    ConceptService conceptService;

    @Autowired
    GenericService genericService;

    @Autowired
    RepositoryService repositoryService;

    /**
     * concept列表信息
     * @param repositoryQueryDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "concept列表信息 [ /repository/getConceptList ] 删除[/repository/searchConcept]接口")
    @RequestMapping(value={"/conceptList", "/list"},method = RequestMethod.POST)
    public JsonResult getConceptList(@RequestBody SpecificFindDTO repositoryQueryDTO){
        return conceptService.getConceptList(repositoryQueryDTO);
    }


    /**
     * 根据id获取concept信息
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @ApiOperation(value = "根据id获取concept信息 [ /repository/getConceptInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getConceptInfo(@PathVariable ("id") String id){
        return conceptService.getConceptById(id);
    }

    
    /**
     * 新增concept
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增concept [ /repository/addConcept ] 删除[/repository/searchConcept]接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="新增item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping(headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult addConcept(MultipartFile info, HttpServletRequest request) throws IOException {
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ConceptDTO addDTO=JSONObject.toJavaObject(jsonObject, ConceptDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return conceptService.insertConcept(addDTO,email);
    }


    /**
     * 更新concept
     * @param id
     * @param info
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新concept [ /repository/updateConcept ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/{id}",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult updateConcept(@PathVariable String id, MultipartFile info, HttpServletRequest request) throws IOException{
        String model= IOUtils.toString(info.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ConceptDTO conceptUpdateDTO=JSONObject.toJavaObject(jsonObject, ConceptDTO.class);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return conceptService.updateConcept(conceptUpdateDTO,email,id);
    }

    /**
     * 删除concept
     * @param id
     * @param request 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "删除concept [ /repository/deleteConcept ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteConcept(@PathVariable(value="id") String id,  HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return conceptService.deleteConcept(id,email);
    }


    /**
     * 根据用户得到concept
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户得到concept [ /concept/listConceptsByUserOid ]")
    @RequestMapping (value = "/listByUser",method = RequestMethod.GET)
    public JsonResult listByUserOid(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByUser(findDTO, email, ItemTypeEnum.Concept));
    }


    /**
     * 根据名称和用户得到concept
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据名称和用户得到concept [ /concept/searchByNameByOid ]")
    @RequestMapping(value="/listByNameAndUser",method= RequestMethod.GET)
    public JsonResult searchByTitle(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByNameAndUser(findDTO, email, ItemTypeEnum.Concept));
    }


    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Concept,findDTO, false));

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

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.Concept,findDTO, true));

    }

}
