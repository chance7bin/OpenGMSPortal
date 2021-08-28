package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.dataMethod.DataMethodDTO;
import njgis.opengms.portal.entity.dto.dataMethod.DataMethodUpdateDTO;
import njgis.opengms.portal.entity.dto.template.TemplateResultDTO;
import njgis.opengms.portal.entity.po.DataServerTask;
import njgis.opengms.portal.service.DataItemService;
import njgis.opengms.portal.service.DataMethodService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/16
 */
@RestController
@Slf4j
@RequestMapping(value = "/dataMethod")
public class DataMethodController {

    @Autowired
    DataMethodService dataMethodService;

    @Autowired
    DataItemService dataItemService;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    DataCategorysDao dataCategorysDao;

    @Autowired
    GenericService genericService;

    /**
     * 新增dataMethod
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增dataMethod(不支持Content Type第一个选项) [ /dataApplication/add ]")
    @PostMapping
    public JsonResult add(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("resources");
        MultipartFile file=multipartRequest.getFile("dataApplication");
        String model= IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        DataMethodDTO dataMethodDTO = JSONObject.toJavaObject(jsonObject,DataMethodDTO.class);

        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        // return ResultUtils.success();
        return dataMethodService.insert(files,dataMethodDTO,email);

    }


    /**
     * 更新dataMethod条目
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新dataMethod(只写了修改者和作者相同的情况) [ /dataApplication/update ]")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public JsonResult update(HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("resources");
        MultipartFile file = multipartRequest.getFile("dataApplication");
        String model = IOUtils.toString(file.getInputStream(), "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(model);
        DataMethodUpdateDTO dataMethodUpdateDTO = JSONObject.toJavaObject(jsonObject, DataMethodUpdateDTO.class);

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result = dataMethodService.update(files, email, dataMethodUpdateDTO);

        if (result == null) {
            return ResultUtils.error("There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        } else {
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "删除dataMethod [ /dataApplication/delete ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult deleteDataApplication(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return dataMethodService.delete(id);
    }


    /**
     * @Description 获取Method Repository下的数据
     * @Author bin
     * @Param [dataMethodsFindDTO]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "获取Method Repository下的数据 [ /dataItem/Items/getMethods (/dataApplication/methods/getApplication)]")
    @RequestMapping(value = "/items",method = RequestMethod.POST)
    public JsonResult getMethods(@RequestBody FindDTO dataMethodsFindDTO){
        return  ResultUtils.success(genericService.searchItems(dataMethodsFindDTO, "dataMethod"));
    }

    /**
     * @Description 根据id获取method条目界面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @ApiOperation(value = "根据id获取method条目界面 [ /dataApplication/{id} ]")
    @RequestMapping (value="/detail/{id}",method = RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") String id){
        return dataMethodService.getPage(id);
    }



    /**
     * @Description 根据id拿到条目的所有信息
     * @Param [id]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "根据id拿到条目的所有信息 [ /dataApplication/getApplication/{id} ]")
    @RequestMapping(value = "/method/{id}",method = RequestMethod.GET)
    public JsonResult getApplicationById(@PathVariable("id") String id) throws UnsupportedEncodingException {
        return ResultUtils.success(JSONObject.toJSON(dataMethodService.getApplicationById(id)));
    }

    /**
     * 展示task页面
     */
    @LoginRequired
    @ApiOperation(value = "展示task页面,task页面会提取url中的这三个参数 [ /dataApplication/task/{aid}/{sid}/{token} ]")
    @GetMapping(value = "/task/{aid}/{sid}/{token}")
    public ModelAndView getTask(
        @ApiParam(name = "aid", value = "数据应用id") @PathVariable String aid,
        @ApiParam(name = "aid", value = "服务id") @PathVariable String sid,
        @ApiParam(name = "aid", value = "token") @PathVariable String token){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("data_application_task");
        return modelAndView;
    }


    /**
     * 获取服务的相关信息
     * @param aid 数据应用id
     * @param sid 服务id
     * @return
     */
    @ApiOperation(value = "获取服务的相关信息 [ /dataApplication/getServiceInfo/{aid}/{sid} ]")
    @RequestMapping(value = "/serviceInfo/{aid}/{sid}", method = RequestMethod.GET)
    public JsonResult getServiceInfo(
        @ApiParam(name = "aid", value = "数据应用id") @PathVariable String aid,
        @ApiParam(name = "sid", value = "服务id") @PathVariable String sid) throws UnsupportedEncodingException {

        return dataMethodService.getServiceInfo(aid, sid);
    }


    /**
     * 根据email查询用户信息
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "根据email查询用户信息 [ /dataApplication/getContributorInfo/{email} ]")
    @RequestMapping(value = "/contributorInfo/{email}", method = RequestMethod.GET)
    public JsonResult getContributorInfo(@PathVariable(value = "email") String email){
        return dataMethodService.getContributorInfo(email);
    }

    /**
     * 获取xml以及parameter
     * @return 所需的xml以及运行参数
     */
    @ApiOperation(value = "获取xml以及parameter [ /dataApplication/getParameter/{aid}/{sid} ]")
    @RequestMapping(value = "/parameter/{aid}/{sid}", method = RequestMethod.GET)
    public JsonResult getParameter(
        @ApiParam(name = "aid", value = "数据应用id") @PathVariable String aid,
        @ApiParam(name = "sid", value = "服务id") @PathVariable String sid) throws IOException, DocumentException {
        return dataMethodService.getParameter(aid, sid);
    }

    /**
     * 通过服务id与token获取服务所绑定的数据的信息，包括文件名与url
     * @param sid 服务id
     * @param token 节点token
     * @return 服务绑定的数据信息
     */
    @ApiOperation(value = "通过服务id与token获取服务所绑定的数据的信息，包括文件名与url [ /dataApplication/getRemoteDataInfo/{sid}/{token} ]")
    @RequestMapping(value = "/remoteDataInfo/{sid}/{token}", method = RequestMethod.GET)
    public JsonResult getRemoteDataInfo(
        @ApiParam(name = "sid", value = "服务id") @PathVariable(value = "sid") String sid,
        @ApiParam(name = "token", value = "节点token") @PathVariable(value = "token") String token){
        return dataMethodService.getRemoteDataInfo(sid, token);
    }


    /**
     * 调用服务
     * @param dataMethodId 方法Id
     * @param serviceId 服务Id
     * @param serviceName 服务名
     * @param params 调用所需的参数
     * @param request request
     * @param selectData onlineData所选的数据，可选传
     * @param integrate 是否集成的调用，集成的调用则标识"integrate", 可选
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @throws DocumentException DocumentException
     */
    @ApiOperation(value = "调用服务 [ /dataApplication/invokeMethod ]")
    @RequestMapping(value = "/invokeMethod", method = RequestMethod.POST)
    public JsonResult invokeMethod(
        @ApiParam(name = "dataMethodId", value = "方法Id") @RequestParam(value = "dataMethodId") String dataMethodId,
        @ApiParam(name = "serviceId", value = "服务Id") @RequestParam(value = "serviceId") String serviceId,
        @ApiParam(name = "serviceName", value = "服务名") @RequestParam(value = "serviceName") String serviceName,
        @ApiParam(name = "params", value = "调用所需的参数") @RequestParam(value = "params") String[] params,
        @ApiParam(name = "selectData", value = "onlineData所选的数据，可选传") @RequestParam(value = "selectData",  required = false) String selectData,
        @ApiParam(name = "integrate", value = "是否集成的调用，集成的调用则标识\"integrate\", 可选") @RequestParam(value = "integrate",  required = false) String integrate,
        HttpServletRequest request) throws IOException, DocumentException {

        return dataMethodService.invokeMethod(dataMethodId,serviceId,serviceName,params,selectData,integrate,request);
    }

    /**
     * 根据用户email搜索dataMethod
     * @param email
     * @param page
     * @param pagesize
     * @param asc
     * @param searchText
     * @param type
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "根据用户email搜索dataMethod [ /dataApplication/searchDataByUserId ]")
    @RequestMapping(value = "/searchDataByUserId", method = RequestMethod.GET)
    public JsonResult searchDataByUserId(
        @RequestParam(value = "email") String email,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "pageSize") int pagesize,
        @RequestParam(value = "asc") int asc,
        @RequestParam(value = "searchText") String searchText,
        @RequestParam(value = "type") String type
    ) {
        return ResultUtils.success(dataMethodService.searchDataByUserId(email, page, pagesize, asc, searchText, type));
    }


    /**
     * 根据id获取条目数据
     * @param id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @ApiOperation(value = "根据id获取条目数据 [ /dataApplication/getInfo/{oid} ]")
    @RequestMapping(value = "/itemInfo/{id}", method = RequestMethod.GET)
    public JsonResult getInfo(@PathVariable("id") String id) {
        return ResultUtils.success(dataMethodService.getInfo(id));
    }

}
