package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelRelation;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemUpdateDTO;
import njgis.opengms.portal.entity.po.Article;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.RelationTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.ModelItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ModelItemRestController
 * @Description 模型条目控制器
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/modelItem")
public class ModelItemRestController {

    @Autowired
    ModelItemService modelItemService;

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    ModelItemDao modelItemDao;

    /**
     * @Description 返回模型条目列表页面
     * @param
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 2021/7/5
     **/
    @ApiOperation(value = "返回模型条目列表页面")
    @RequestMapping(value="/repository",method = RequestMethod.GET)
    public ModelAndView modelItemListPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("model_items");

        return modelAndView;

    }

    /**
     * @Description 模型应用页面展示
     * @param
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 2021/7/5
     **/
    @ApiOperation(value = "返回模型应用页面")
    @RequestMapping(value="/application",method = RequestMethod.GET)
    public ModelAndView Application() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("modelApplication");

        return modelAndView;
    }

    /**
     * @Description 根据id获取模型条目详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    @ApiOperation(value = "根据id获取模型条目详情页面")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    ModelAndView get(@PathVariable("id") String id, HttpServletRequest request){
        PortalItem portalItem = genericService.getPortalItem(id, ItemTypeEnum.ModelItem);
        ModelAndView modelAndView = genericService.checkPrivatePageAccessPermission(portalItem, Utils.checkLoginStatus(request));
        if(modelAndView != null){
            return modelAndView;
        }else {
            return modelItemService.getPage(portalItem);
        }
    }

    /**
     * @Description 创建模型条目
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/5
     **/
    @LoginRequired
    @ApiOperation(value = "创建模型条目", notes = "@LoginRequired\n之前因为玄武盾封锁，需要以文件的形式将条目信息传入")
    @RequestMapping(value="/",method = RequestMethod.POST)
    public JsonResult addModelItem(HttpServletRequest request) throws IOException, NoSuchFieldException, IllegalAccessException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file=multipartRequest.getFile("info");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ModelItemAddDTO modelItemAddDTO=JSONObject.toJavaObject(jsonObject,ModelItemAddDTO.class);

        HttpSession session=request.getSession();

        String email=session.getAttribute("email").toString();

        ModelItem modelItem= modelItemService.insert(modelItemAddDTO,email);
        return ResultUtils.success(modelItem.getId());
    }


    /**
     * @Description 删除模型条目
     * @param id 条目Id
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/7
     **/
    @LoginRequired
    @ApiOperation(value = "删除模型条目", notes = "@LoginRequired 条目作者或管理员可以删除")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JsonResult deleteModelItem(@PathVariable(value="id") String id,
                                      HttpServletRequest request){
        HttpSession session=request.getSession();

        String email = session.getAttribute("email").toString();
        return modelItemService.delete(id,email);

    }


    /**
     * @Description 模型条目查询
     * @param modelItemFindDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/7
     **/
    @ApiOperation(value = "模型条目查询", notes = "可以查询到所有公开的模型条目")
    @RequestMapping(value = {"/items","/list"}, method = RequestMethod.POST)
    public JsonResult queryList(@RequestBody SpecificFindDTO modelItemFindDTO) {
        modelItemFindDTO.setSortField("viewCount");
        return ResultUtils.success(genericService.searchItems(modelItemFindDTO, ItemTypeEnum.ModelItem));
        // return ResultUtils.success(modelItemService.query(modelItemFindDTO, false));
    }

    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.ModelItem,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的模型条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody UserFindDTO findDTO, HttpServletRequest request) {
        // if (findDTO.getAuthorEmail() == null){
        //     HttpSession session=request.getSession();
        //     String email=session.getAttribute("email").toString();
        //     findDTO.setAuthorEmail(email);
        // }
        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.ModelItem,findDTO, true));

    }

    /**
     * @Description 模型条目更新,通过文件上传要更新的属性
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/12
     **/
    @LoginRequired
    @ApiOperation(value = "更新模型条目", notes = "@LoginRequired\n之前因为玄武盾封锁，需要以文件的形式将条目信息传入")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public JsonResult updateModelItem(HttpServletRequest request) throws IOException {

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file=multipartRequest.getFile("info");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);
        ModelItemUpdateDTO modelItemUpdateDTO=JSONObject.toJavaObject(jsonObject,ModelItemUpdateDTO.class);

        JSONObject result=modelItemService.update(modelItemUpdateDTO,email);
        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @ApiOperation(value = "获取模型条目分类信息", notes = "@LoginRequired\n")
    @RequestMapping(value = "/classifications/{id}", method = RequestMethod.GET)
    JsonResult getClass(@PathVariable("id") String id ,HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getClassifications(id));
    }

    @ApiOperation(value = "获取模型条目分类信息", notes = "@LoginRequired\n")
    @RequestMapping(value = "/localizationList/{id}", method = RequestMethod.GET)
    JsonResult getLocalizationList(@PathVariable("id") String id ,HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getLocalizationList(id));
    }

    @ApiOperation(value = "获取模型条目不同语言的详情描述")
    @RequestMapping(value = "/detailByLanguage", method = RequestMethod.GET)
    JsonResult getDetailByLanguage(@RequestParam(value="id") String id, @RequestParam(value="language") String language){

        String detail = modelItemService.getDetailByLanguage(id, language);
        if(detail==null){
            return ResultUtils.error(-1,"language does not exist in this model item.");
        }else{
            return ResultUtils.success(detail);
        }
    }

    @ApiOperation(value = "获取模型条目别名", notes = "@LoginRequired\n")
    @RequestMapping(value = "/alias/{id}", method = RequestMethod.GET)
    JsonResult getAlias(@PathVariable("id") String id ,HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getAlias(id));
    }

    @ApiOperation(value = "获取模型条目关联的其他条目", notes = "@LoginRequired\n")
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    JsonResult getRelation(@RequestParam(value = "type") String type,@RequestParam(value = "id") String id,HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }

        id = genericService.formatId(id);
        return ResultUtils.success(modelItemService.getRelation(id,type));
    }

    @ApiOperation(value = "门户所有模型的关系图页面")
    @RequestMapping(value="/modelRelationGraph",method = RequestMethod.GET)
    public ModelAndView relationGraph() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("modelRelationGraph");

        return modelAndView;

    }

    @ApiOperation(value = "刷新门户所有模型的关系图Json，并存储于服务器本地")
    @RequestMapping(value="/refreshFullRelationGraph",method = RequestMethod.POST)
    public JsonResult refreshFullRelationGraph(){
        return ResultUtils.success(modelItemService.refreshFullRelationGraph());
    }

    @ApiOperation(value = "返回指定模型的关系图Json")
    @RequestMapping(value="/relationGraph",method = RequestMethod.POST)
    public JsonResult getRelationGraph(@RequestParam(value="id") String id,@RequestParam(value="isFull") Boolean isFull){
        return ResultUtils.success(modelItemService.getRelationGraph(id,isFull));
    }

    @ApiOperation(value = "获取模型参考文献", notes = "@LoginRequired\n")
    @RequestMapping(value = "/references/{id}", method = RequestMethod.GET)
    JsonResult getReferences(@PathVariable("id") String id, HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getReferences(id));
    }

    @ApiOperation(value = "获取模型相关资源", notes = "@LoginRequired\n")
    @RequestMapping(value = "/relatedResources/{id}", method = RequestMethod.GET)
    JsonResult getRelatedResources(@PathVariable("id") String id, HttpServletRequest request){
        System.out.println("test");
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getRelatedResources(id));
    }

    @ApiOperation(value = "更新模型条目别名信息", notes = "@LoginRequired\n")
    @RequestMapping(value = "/Alias", method = RequestMethod.PUT)
    public JsonResult updateAlias(@RequestParam(value = "id") String id,
                                  @RequestParam(value = "alias[]") List<String> alias,
                                  HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        return ResultUtils.success(modelItemService.updateAlias(id,alias,email));
    }

    @ApiOperation(value = "更新模型条目分类信息", notes = "@LoginRequired\n")
    @RequestMapping(value = "/classifications", method = RequestMethod.PUT)
    public JsonResult updateClass(@RequestParam(value = "id") String id,
                                  @RequestParam(value = "class[]") List<String> classi,
                                  HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        return ResultUtils.success(modelItemService.updateClassifications(id,classi,email));
    }

    @ApiOperation(value = "更新模型条目详情描述列表", notes = "@LoginRequired\n")
    @RequestMapping(value = "/localizations", method = RequestMethod.PUT)
    public JsonResult updateLocals(@RequestParam(value = "id") String id,
                                  @RequestParam(value = "localizations") String localizations_str,
                                  HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }

        List<Localization> localizations = JSONObject.parseArray(localizations_str, Localization.class);
        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        return ResultUtils.success(modelItemService.updateLocalizations(id,localizations,email));
    }

    @ApiOperation(value = "更新模型条目关联条目,除related ModelItem", notes = "@LoginRequired\n")
    @RequestMapping(value = "/relation", method = RequestMethod.PUT)
    public JsonResult updateRelation(@RequestParam(value = "id") String id,
                                     @RequestParam(value="type") String type,
                                    @RequestParam(value = "relations[]") List<String> relations,
                                    HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }

        id = genericService.formatId(id);

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        return ResultUtils.success(modelItemService.setRelation(id,type,relations,email));
    }

    @ApiOperation(value = "更新模型条目related ModelItem", notes = "@LoginRequired\n")
    @RequestMapping(value = "/modelRelation/{id}", method = RequestMethod.PUT)
    JsonResult setModelRelation(@PathVariable("id") String id,
                                @RequestParam(value = "relations") String relations,
                                HttpServletRequest request) {
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();
        List<ModelRelation> modelRelationList = new ArrayList<>();

        //解析relations
        JSONArray relationsArr = (JSONArray) JSONArray.parse(relations);


        for (int i = 0; i < relationsArr.size(); i++) {
            JSONObject object = relationsArr.getJSONObject(i);
            // JSONObject object = (JSONObject) relations.get(i);
            ModelRelation modelRelation = new ModelRelation();
            modelRelation.setModelId(object.getString("id"));
            modelRelation.setRelation(RelationTypeEnum.getRelationTypeByText(object.getString("relation")));
            modelRelationList.add(modelRelation);
        }

        JSONObject result = modelItemService.setModelRelation(id, modelRelationList,email);

        return ResultUtils.success(result);

    }

    @ApiOperation(value = "更新模型条目参考文献", notes = "@LoginRequired\n")
    @RequestMapping(value = "/references", method = RequestMethod.PUT)
    public JsonResult updateReference(@RequestParam("id") String id,
                                      @RequestParam(value = "references") String references_str,
                                      HttpServletRequest request){

        String email = Utils.checkLoginStatus(request);
        if(StringUtils.isEmpty(email)){
            return ResultUtils.error(-1, "no login");
        }

        List<Article> references = JSONObject.parseArray(references_str, Article.class);

        return ResultUtils.success(modelItemService.updateReferences(id, references, email));
    }


    @ApiOperation(value = "根据id得到模型条目信息")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.GET)
    public JsonResult getItemById(@PathVariable String id){
        return genericService.getById(id, ItemTypeEnum.ModelItem);
    }

    @ApiOperation(value = "根据id得到模型条目贡献者信息 [/modelItem/getContributors]")
    @RequestMapping(value="/contributors",method = RequestMethod.GET)
    public JsonResult getContributors(@RequestParam(value="id") String oid){
        return ResultUtils.success(modelItemService.getContributors(oid));
    }

    @ApiOperation(value = "根据id得到模型条目每日访问量以及关联计算模型的调用量 [/modelItem/getDailyViewCount]")
    @RequestMapping (value="/dailyViewAndInvokeCount",method = RequestMethod.GET)
    public JsonResult getDailyViewCount(@RequestParam(value="id") String id) {
        return ResultUtils.success(modelItemService.getDailyViewAndInvokeCount(id));
    }

    @RequestMapping(value="/searchByDOI",method=RequestMethod.POST)
    public JsonResult searchReferenceByDOI(@RequestParam(value="doi") String DOI,
                                           @RequestParam(value="modelOid") String modelOid,
                                           HttpServletRequest httpServletRequest) throws IOException, DocumentException {

        if(StringUtils.isEmpty(Utils.checkLoginStatus(httpServletRequest))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getArticleByDOI(DOI,modelOid));
    }




}
