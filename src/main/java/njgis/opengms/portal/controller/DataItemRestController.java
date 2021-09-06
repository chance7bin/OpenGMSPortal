package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.DataCategorysDao;
import njgis.opengms.portal.dao.DataHubDao;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.dataItem.DataItemDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.DataItemService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
     * 新增dataItem条目
     * @param dataItemAddDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "新增dataItem条目")
    @PostMapping
    public JsonResult add(@RequestBody DataItemDTO dataItemAddDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        // dataItemAddDTO.setAuthor(email);
        return dataItemService.insert(dataItemAddDTO,email, ItemTypeEnum.DataItem);
    }



    /**
     * 更新dataItem
     * @param dataItemUpdateDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新dataItem(只写了修改者和作者相同的情况)")
    @PutMapping(value = "/{id}")
    public JsonResult updateDataItem(@PathVariable String id , @RequestBody DataItemDTO dataItemUpdateDTO, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        // if(email==null){
        //     return ResultUtils.error(-2,"未登录");
        // }
        JSONObject result=dataItemService.updateDataItem(dataItemUpdateDTO,email,id);
        if(result==null){
            return ResultUtils.error("There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    /**
     * 个人中心删除dataItem操作
     * @param id 待删除的dataItemId
     * @return 返回删除成功与否的标识
     */
    @LoginRequired
    @ApiOperation(value = "个人中心删除dataItem操作 [ /dataItem/del ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult delete(@PathVariable(value="id") String id ,HttpServletRequest request){
        HttpSession session=request.getSession();

        // if(session.getAttribute("email")==null){
        //     return ResultUtils.error(-1,"no login");
        // }

        String email=session.getAttribute("email").toString();
        return dataItemService.delete(id,email);
        // return null;
    }


    /**
     * 通过导航栏，打开data hub repository
     * @return modelAndView
     */
    @ApiOperation(value = "返回data_items_hubs页面")
    @GetMapping("/hubs")
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
    @ApiOperation(value = "返回data_items_repository页面")
    @GetMapping("/repository")
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
    @ApiOperation(value = "返回data_application_repository页面 [ /dataApplication/methods ]")
    @GetMapping(value = "/methods")
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
    @ApiOperation(value = "获取Item Repository下的数据 [ /dataItem/Items/getItems ]")
    @RequestMapping(value = "/items",method = RequestMethod.POST)
    public JsonResult getItems(@RequestBody SpecificFindDTO dataItemFindDTO){
        return  dataItemService.getItems(dataItemFindDTO);
    }



    /**
     * @Description 获取数据条目,返回条目详情界面
     * @Param id dataItem的id
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @ApiOperation(value = "根据id返回详情界面")
    @RequestMapping (value = "/detail/{id}", method = RequestMethod.GET)
    public ModelAndView getItem(@ApiParam(name = "id", value = "dataItem的id", required = true)
                         @PathVariable("id") String id) throws IOException, URISyntaxException, DocumentException {
        return dataItemService.getPage(id, dataItemDao);
    }


    /**
     * 获取当前条目的远程数据信息
     * @return 成功失败或者远程数据信息
     */
    @ApiOperation(value = "获取当前条目的远程数据信息 [ /dataItem/getDistributeDataInfo/{dataItemId} ]")
    @RequestMapping(value = "/distributeDataInfo/{dataItemId}", method = RequestMethod.GET)
    public JsonResult getDistributeDataInfo(@PathVariable(value = "dataItemId") String dataItemId){
        List<InvokeService> invokeServices = dataItemService.getDistributeDataInfo(dataItemId);
        if(null!=invokeServices)
            return ResultUtils.success(invokeServices);

        return ResultUtils.error("No Distribute Data");

    }


    /**
     * @Description 获取与数据条目相关的模型
     * @Param [id] dataItem的id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "获取与数据条目相关的模型 [ /dataItem/getRelation ]")
    @RequestMapping(value="/relation",method = RequestMethod.GET)
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
    @LoginRequired
    @ApiOperation(value = "设置与数据条目相关的模型(unfinished) [ /dataItem/setRelation ]")
    @RequestMapping(value="/relation",method = RequestMethod.POST)
    public JsonResult setRelation(@RequestParam(value="id") String id,
                           @RequestParam(value = "relations[]") List<String> relations){

        return dataItemService.setRelation(id,relations);

    }






    /**
     * 得到分类树
     * @param
     * @return com.alibaba.fastjson.JSONArray
     * @Author bin
     **/
    @ApiOperation(value = "得到分类树 [ /dataItem/createTree ] ")
    @RequestMapping(value = "/categoryTree",method = RequestMethod.GET)
    public JsonResult createTree(){
//        return dataItemService.createTree();
        return ResultUtils.success(dataItemService.createTreeNew());
    }


    /**
     * 根据id得到dataItem信息
     * @param id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据id得到dataItem信息(修改item时调用的接口) [ /dataItem/getDataItemByDataId ]")
    @RequestMapping(value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getDataItemByDataId(@PathVariable(value="id") String id){
        // HttpSession session = request.getSession();
        // if(session.getAttribute("email")==null){
        //     return ResultUtils.error(-1,"no login");
        // }
        // else
        //     return ResultUtils.success(dataItemService.getItemByDataId(id,"item"));

        return ResultUtils.success(dataItemService.getItemByDataId(id,ItemTypeEnum.DataItem));
    }






}
