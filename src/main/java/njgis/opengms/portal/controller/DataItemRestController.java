package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.DataCategorysDao;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.data.DataCategorys;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.service.DataItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
    DataCategorysDao dataCategorysDao;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    /**
     * @Description Description
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
    ModelAndView get(@PathVariable("id") String id) throws IOException, URISyntaxException, DocumentException {
        return dataItemService.getPage(id);
    }

    /**
     * 获取当前条目的远程数据信息
     * @return 成功失败或者远程数据信息
     */
    @RequestMapping(value = "/getDistributeDataInfo/{dataItemId}", method = RequestMethod.GET)
    public JsonResult getDistributeDataInfo(@PathVariable(value = "dataItemId") String dataItemId){
        JsonResult res = new JsonResult();
        DataItem dataItem = dataItemDao.findFirstById(dataItemId);
        List<InvokeService> invokeServices = dataItem.getInvokeServices();
        if(null!=invokeServices){
            res.setData(invokeServices);
            res.setCode(0);
            res.setMsg("success");
        }else {
            res.setMsg("No Distribute Data");
        }

        return res;
    }


    @RequestMapping(value="/getRelation",method = RequestMethod.GET)
    JsonResult getRelation(@RequestParam(value = "id") String id){

        JSONArray result=dataItemService.getRelation(id);

        return ResultUtils.success(result);

    }

    @RequestMapping(value="/setRelation",method = RequestMethod.POST)
    JsonResult setRelation(@RequestParam(value="id") String id,
                           @RequestParam(value = "relations[]") List<String> relations){

        String result=dataItemService.setRelation(id,relations);

        return ResultUtils.success(result);

    }

}
