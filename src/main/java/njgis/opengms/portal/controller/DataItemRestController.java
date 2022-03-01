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
import njgis.opengms.portal.entity.dto.data.dataItem.DataItemDTO;
import njgis.opengms.portal.entity.dto.dataItem.DataItemFindDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.DataItemService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.MyHttpUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.XmlTool;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${dataServerManager}")
    private String dataServerManager;

    @Value ("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;


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
    @ApiOperation(value = "更新dataItem")
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
    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
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


    @ApiOperation(value = "有关search的接口大部分都废弃了，现在用dataItem[dataHub]/items，满足不了需求的话再写新的")
    @RequestMapping(value="/tip",method = RequestMethod.GET)
    public JsonResult tip(){
        return ResultUtils.success();
    }


    /**
     * 得到用户上传的data item
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "得到用户上传的data item [ /dataItem/searchByNameAndAuthor[searchByNameByOid/searchDataByUserId] ]")
    @RequestMapping(value="/itemsByNameAndAuthor",method = RequestMethod.POST)
    public JsonResult searchByNameAndAuthor(@RequestBody SpecificFindDTO findDTO,HttpServletRequest request){
        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();
        return dataItemService.searchByNameAndAuthor(findDTO, email);
    }


    /**
     * 数据详情页面RelatedModels，为数据添加关联的模型
     * @param id 数据id
     * @param relatedModels
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "数据详情页面RelatedModels，为数据添加关联的模型 [ /dataItem/models ]")
    @RequestMapping(value = "/update/relatedModels",method = RequestMethod.POST)
    public JsonResult addRelatedModels(@RequestParam(value = "dataId") String id,@RequestParam(value = "relatedModels") List<String> relatedModels){
        return dataItemService.addRelatedModels(id,relatedModels);
    }

    /**
     * 个人中心创建数据条目
     * @param id
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "数据详情页面RelatedModels，为数据添加关联的模型 [ /dataItem/adddataitembyuser ]")
    @RequestMapping(value="/addDataItemByUser",method = RequestMethod.GET)
    public JsonResult addUserData(@RequestParam(value="id") String id) throws IOException{

        return dataItemService.addDataItemByUser(id);
    }


    /**
     * 根据token与dataId获取下载链接或者节点在线状态
     * @param token 远程数据所在节点token
     * @param dataId 远程数据id
     * @return 下载结果url或者节点不在线的状态提示
     */
    @ApiOperation(value = "根据token与dataId获取下载链接或者节点在线状态")
    @RequestMapping(value = "/downloadDisData", method = RequestMethod.POST)
    public JsonResult downloadDisData(@RequestParam(value = "token") String token,
                                      @RequestParam(value = "dataId") String dataId) throws IOException, URISyntaxException, DocumentException {
        JsonResult res = new JsonResult();
        String url = "http://"+ dataServerManager +"/fileObtain" + "?token=" + URLEncoder.encode(token) + "&id=" + dataId;
        String xml = MyHttpUtils.GET(url,"UTF-8",null);
        String dataUrl = null;
        try{
            JSONObject json = JSONObject.parseObject(xml);
            if(json.getString("code").equals("-1")){
                return  ResultUtils.error("Node offline");
            }
        }catch (Exception e){
            dataUrl = XmlTool.xml2Json(xml).getString("uid");
            res.setData(dataUrl);
            res.setCode(1);
        }
//        dataUrl = XmlTool.xml2Json(xml).getString("uid");
//        res.setData(dataUrl);
//        res.setCode(0);

        return res;
    }

    /**
     * 下载数据资源文件
     * @param sourceStoreId
     * @return
     */
    @ApiOperation(value = "下载数据资源文件")
    @RequestMapping (value = "/downloadRemote", method = RequestMethod.GET)
    ResponseEntity downloadRemote(@RequestParam ("sourceStoreId") String sourceStoreId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
            new ByteArrayHttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<>(headers);


        Map<String, String> map = new HashMap<>();
        map.put("sourceStoreId", sourceStoreId);


        ResponseEntity<byte[]> response = restTemplate.exchange(
            "http://" + dataContainerIpAndPort + "/dataResource/getResources?sourceStoreId={sourceStoreId}&",
            HttpMethod.GET, entity, byte[].class, map);

        return response;
    }

    /**
     * 数据详情页面RelatedModels，数据关联的3个模型
     * @param id
     * @return
     */
    @ApiOperation(value = "数据详情页面RelatedModels，数据关联的3个模型 [ /dataItem/briefrelatedmodels ]")
    @RequestMapping(value = "/briefRelatedModels",method = RequestMethod.GET)
    JsonResult getBriefRelatedModels(@RequestParam(value = "id") String id){
        return ResultUtils.success(dataItemService.getRelatedModels(id));
    }


    /**
     * 数据详情页面RelatedModels，数据关联的所有模型
     * @param id
     * @param more
     * @return
     */
    @ApiOperation(value = "数据详情页面RelatedModels，数据关联的所有模型 [ /dataItem/allrelatedmodels ]")
    @RequestMapping(value = "/allRelatedModels",method = RequestMethod.GET)
    JsonResult getRelatedModels(@RequestParam(value = "id") String id,@RequestParam(value = "more") Integer more){
        return ResultUtils.success(dataItemService.getAllRelatedModels(id,more));
    }

    /**
     * 模型详情页面中的RelatedData，模型关联数据搜索，搜索范围是全部的选项
     * @param dataItemFindDTO
     * @return
     */
    @ApiOperation(value = "模型详情页面中的RelatedData，模型关联数据搜索，搜索范围是全部的选项 [ /dataItem/searchFromAll ]")
    @RequestMapping(value="/allRelatedDataByFindDTO",method = RequestMethod.POST)
    JsonResult relatedModelsFromAll(@RequestBody DataItemFindDTO dataItemFindDTO){
        return ResultUtils.success(dataItemService.searchFromAllData(dataItemFindDTO));
    }

    @ApiOperation(value = "根据id得到DataItem信息")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.GET)
    public JsonResult getItemById(@PathVariable String id){
        return genericService.getById(id, ItemTypeEnum.DataItem);
    }


    @ApiOperation(value = "返回数据应用页面")
    @RequestMapping("/application")
    public ModelAndView getApplication(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("data_application");
        modelAndView.addObject("dataType","application");
        return modelAndView;
    }
}
