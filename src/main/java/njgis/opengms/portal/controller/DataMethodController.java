package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.po.DataServerTask;
import njgis.opengms.portal.service.DataMethodService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/dataApplication")
public class DataMethodController {

    @Autowired
    DataMethodService dataMethodService;


    /**
     * @Description 根据id获取method条目界面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping (value="/{id}",method = RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") String id){
        return dataMethodService.getPage(id);
    }



    /**
     * @Description 根据id拿到条目的所有信息
     * @Param [id]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value = "/getApplication/{id}",method = RequestMethod.GET)
    public JsonResult getApplicationById(@PathVariable("id") String id) throws UnsupportedEncodingException {
        return ResultUtils.success(JSONObject.toJSON(dataMethodService.getApplicationById(id)));
    }

    /**
     * 展示task页面
     */
    @LoginRequired
    @RequestMapping(value = "/task/{aid}/{sid}/{token}", method = RequestMethod.GET)
    ModelAndView getTask(@PathVariable String aid,@PathVariable String sid,@PathVariable String token){
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
    @RequestMapping(value = "/getServiceInfo/{aid}/{sid}", method = RequestMethod.GET)
    public JsonResult getServiceInfo(@PathVariable String aid,@PathVariable String sid) throws UnsupportedEncodingException {
        return dataMethodService.getServiceInfo(aid, sid);
    }


    /**
     * 根据email查询用户信息
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @RequestMapping(value = "/getContributorInfo/{email}", method = RequestMethod.GET)
    public JsonResult getContributorInfo(@PathVariable(value = "email") String email){
        return dataMethodService.getContributorInfo(email);
    }

    /**
     * 获取xml以及parameter
     * @return 所需的xml以及运行参数
     */
    @RequestMapping(value = "/getParameter/{aid}/{sid}", method = RequestMethod.GET)
    public JsonResult getParameter(@PathVariable String aid, @PathVariable String sid) throws IOException, DocumentException {
        return dataMethodService.getParameter(aid, sid);
    }

    /**
     * 通过服务id与token获取服务所绑定的数据的信息，包括文件名与url
     * @param sid 服务id
     * @param token 节点token
     * @return 服务绑定的数据信息
     */
    @RequestMapping(value = "/getRemoteDataInfo/{sid}/{token}", method = RequestMethod.GET)
    public JsonResult getRemoteDataInfo(@PathVariable(value = "sid") String sid,@PathVariable(value = "token") String token){
        return dataMethodService.getRemoteDataInfo(sid, token);
    }


    /**
     * 调用服务
     * @param dataMethodId dataMethodId
     * @param serviceId serviceId
     * @param serviceName serviceName
     * @param params 调用所需的参数
     * @param request request
     * @param selectData onlineData所选的数据，可选传
     * @param integrate 是否集成的调用，集成的调用则标识"integrate", 可选
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @throws DocumentException DocumentException
     */
    @RequestMapping(value = "/invokeMethod", method = RequestMethod.POST)
    JsonResult invokeMethod(@RequestParam(value = "dataMethodId") String dataMethodId,
                            @RequestParam(value = "serviceId") String serviceId,
                            @RequestParam(value = "serviceName") String serviceName,
                            @RequestParam(value = "params") String[] params,
                            @RequestParam(value = "selectData",  required = false) String selectData,
                            @RequestParam(value = "integrate",  required = false) String integrate,
                            HttpServletRequest request) throws IOException, DocumentException {
        return dataMethodService.invokeMethod(dataMethodId,serviceId,serviceName,params,selectData,integrate,request);
    }

}
