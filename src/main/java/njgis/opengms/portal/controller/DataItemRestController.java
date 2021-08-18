package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.DataCategorysDao;
import njgis.opengms.portal.dao.DataHubDao;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.service.DataItemService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/04
 */
@RestController
@RequestMapping(value = "/dataItem")
@Slf4j
public class DataItemRestController {


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
    DataCategorysDao dataCategorysDao;

    @Autowired
    GenericService genericService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;


    /**
     * 通过导航栏，打开data hub repository
     * @return modelAndView
     */
    @RequestMapping("/hubs")
    public ModelAndView getHubs(){
//        System.out.println("data-items-page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("data_items_hubs");
        modelAndView.addObject("dataType","hubs");
        return modelAndView;
    }

    /**
     * 通过导航栏，打开data item repository
     * @return modelAndView
     */
    @RequestMapping("/repository")
    public ModelAndView getRepository(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("data_items_repository");
        modelAndView.addObject("dataType","repository");
        return modelAndView;
    }

    /**
     * 通过导航栏，打开data method repository
     * @return modelAndView
     */
    @RequestMapping(value = "/methods")
    public ModelAndView getMethods() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("data_application_repository");
        return modelAndView;
    }



    /**
     * @Description 获取Item Repository下的数据
     * @Author bin
     * @Param [dataItemFindDTO]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value = "/Items/getItems",method = RequestMethod.POST)
    JsonResult getItems(@RequestBody FindDTO dataItemFindDTO){
        return  ResultUtils.success(genericService.searchDataItems(dataItemFindDTO, "item"));
    }

    /**
     * @Description 获取Hub Repository下的数据
     * @Author bin
     * @Param [dataHubsFindDTO]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value = "/Items/getHubs",method = RequestMethod.POST)
    JsonResult getHubs(@RequestBody FindDTO dataHubsFindDTO){
        return  ResultUtils.success(genericService.searchDataItems(dataHubsFindDTO, "hub"));
    }

    /**
     * @Description 获取Method Repository下的数据
     * @Author bin
     * @Param [dataMethodsFindDTO]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value = "/Items/getMethods",method = RequestMethod.POST)
    JsonResult getMethods(@RequestBody FindDTO dataMethodsFindDTO){
        return  ResultUtils.success(genericService.searchDataItems(dataMethodsFindDTO, "method"));
    }



    /**
     * @Description 获取数据条目
     * @Param id dataItem的id
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
    ModelAndView getItem(@PathVariable("id") String id) throws IOException, URISyntaxException, DocumentException {
        return dataItemService.getPage(id, dataItemDao);
    }

    /**
     * @Description 获取数据中心条目
     * @Param id dataItem的id
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping (value = "/hub/{id}", method = RequestMethod.GET)
    ModelAndView getHub(@PathVariable("id") String id) throws IOException, URISyntaxException, DocumentException {
        return dataItemService.getPage(id, dataHubDao);
    }



    /**
     * 获取当前条目的远程数据信息
     * @return 成功失败或者远程数据信息
     */
    @RequestMapping(value = "/getDistributeDataInfo/{dataItemId}", method = RequestMethod.GET)
    public JsonResult getDistributeDataInfo(@PathVariable(value = "dataItemId") String dataItemId){
        JsonResult res = new JsonResult();
        List<InvokeService> invokeServices = dataItemService.getDistributeDataInfo(dataItemId);
        if(null!=invokeServices){
            res.setData(invokeServices);
            res.setCode(0);
            res.setMsg("success");
        }else {
            res.setMsg("No Distribute Data");
        }

        return res;
    }


    /**
     * @Description 获取与数据条目相关的模型
     * @Param [id] dataItem的id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value="/getRelation",method = RequestMethod.GET)
    public JsonResult getRelation(@RequestParam(value = "id") String id){

        JSONArray result=dataItemService.getRelation(id);

        return ResultUtils.success(result);

    }

    /**
     * @Description 设置与数据条目相关的模型
     * @Param id dataItem的id
     * @Param relations :增加的相关模型
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @RequestMapping(value="/setRelation",method = RequestMethod.POST)
    public JsonResult setRelation(@RequestParam(value="id") String id,
                           @RequestParam(value = "relations[]") List<String> relations){

        String result=dataItemService.setRelation(id,relations);

        return ResultUtils.success(result);

    }


}
