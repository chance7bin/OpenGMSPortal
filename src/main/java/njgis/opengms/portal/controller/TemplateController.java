package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.DataMethodDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.template.TemplateDTO;
import njgis.opengms.portal.entity.po.DataMethod;
import njgis.opengms.portal.entity.po.Template;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.RepositoryService;
import njgis.opengms.portal.service.TemplateService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    DataMethodDao dataMethodDao;

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

    /**
     * 根据用户得到template
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户得到template [ /template/listTemplatesByOid ]")
    @RequestMapping (value = "/listByUser",method = RequestMethod.GET)
    public JsonResult listByUserOid(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByUser(findDTO, email, ItemTypeEnum.Template));
    }


    /**
     * 根据名称和用户得到template
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据名称和用户得到template [ /template/searchByNameByOid ]")
    @RequestMapping(value="/listByNameAndUser",method= RequestMethod.GET)
    public JsonResult searchByTitle(FindDTO findDTO, HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(repositoryService.getRepositoryByNameAndUser(findDTO, email, ItemTypeEnum.Template));
    }



    @ApiOperation(value = "得到所有template")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    JsonResult getAllTemplate(){
        List<Template> template = templateService.searchALL();
        if(template==null){
            return ResultUtils.error("no template");
        }
        else {
            return ResultUtils.success(template);
        }
    }


    @ApiOperation(value = "根据name得到template [ /template/{name} ]")
    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
    JsonResult getTemplateByName(@PathVariable("name") String name){
        List<Template> template = templateService.searchByName(name);
        if(template==null){
            return ResultUtils.error("no template");
        }
        else {
            return ResultUtils.success(template);
        }
    }

    @ApiOperation(value = "上传文件")
    @PostMapping(value = "/uploadFiles")
    public JsonResult upload(MultipartFile uploadFile, HttpServletRequest request) {
        String realPath = "E:/data";
        File folder = new File(realPath);
        try {
            Collection<Part> parts = null;
            try {
                parts = request.getParts();
            } catch (ServletException e) {
                e.printStackTrace();
            }
            for (Part part: parts){
                String oldName = part.getSubmittedFileName();
                MultipartFile multipartFile = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(),part.getInputStream());
                part.getSize();
                multipartFile.transferTo(new File(folder, oldName));
                // System.out.println(ResultUtils.success());
                return ResultUtils.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtils.error("fail");
    }

    @ApiOperation(value = "删除文件 [ /template/fileName/{name} ]")
    @RequestMapping(value = "/fileName/{fileName}", method = RequestMethod.DELETE)
    JsonResult deleteFileByName(@PathVariable("fileName") String fileName){
        String path = "E:/data/" + fileName;
        File file = new File(path);
        try{
            if(!file.exists()){
                return ResultUtils.error("no such file");
            }
            file.delete();
            return  ResultUtils.success();
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtils.error("fail");
    }

    /**
     * 获取模板关联的数据方法信息
     * @param templateId 模板id
     * @return 关联的数据方法信息
     */
    @ApiOperation(value = "获取模板关联的数据方法信息")
    @RequestMapping(value = "/getRelatedDataMethods/{templateId}", method = RequestMethod.GET)
    public JsonResult getRelatedDataMethods(@PathVariable(value = "templateId") String templateId){
        return templateService.getRelatedDataMethods(templateId);
    }


    /**
     * 获取所有的methods数据
     * @return methods分页数据
     */
    @ApiOperation(value = "获取所有的methods数据")
    @RequestMapping(value = "/getMethods", method = RequestMethod.POST)
    JsonResult getMethods(FindDTO findDTO){
        Sort sort = Sort.by(findDTO.getAsc() == false ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(findDTO.getPage() - 1, 5, sort);
        Page<DataMethod> dataMethods =
            dataMethodDao.findByNameLike(pageable, findDTO.getSearchText());

        return ResultUtils.success(dataMethods);
    }


}
