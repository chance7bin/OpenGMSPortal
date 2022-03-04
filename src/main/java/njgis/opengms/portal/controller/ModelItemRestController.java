package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemFindDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemUpdateDTO;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.ModelItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    public JsonResult addModelItem(HttpServletRequest request) throws IOException {

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
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public JsonResult deleteModelItem(@ApiParam(name = "Id", value = "模型条目Id", required = true)
                                      @RequestParam(value="id") String id,
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
        return ResultUtils.success(genericService.searchItems(modelItemFindDTO, ItemTypeEnum.ModelItem));
        // return ResultUtils.success(modelItemService.query(modelItemFindDTO, false));
    }

    /**
     * @Description 某用户查询他人的模型条目
     * @param modelItemFindDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/7
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(ModelItemFindDTO modelItemFindDTO) {

        return ResultUtils.success(modelItemService.query(modelItemFindDTO, false));

    }

    /**
     * @Description 某用户查询自己的模型条目
     * @param modelItemFindDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/7
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的模型条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody ModelItemFindDTO modelItemFindDTO, HttpServletRequest request) {
        String email = Utils.checkLoginStatus(request);
        modelItemFindDTO.setAuthorEmail(email);
        return ResultUtils.success(modelItemService.query(modelItemFindDTO, true));

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
        return ResultUtils.success(modelItemService.getRelation(id,type));
    }

    @ApiOperation(value = "门户所有模型的关系图页面")
    @RequestMapping(value="/modelRelationGraph",method = RequestMethod.GET)
    public ModelAndView relationGraph() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("modelRelationGraph");

        return modelAndView;

    }

    @ApiOperation(value = "刷新门户所有模型的关系图Json，并存储于本地文件")
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
    JsonResult getReferences(@PathVariable("id") String id ,HttpServletRequest request){
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getReferences(id));
    }

    @ApiOperation(value = "获取模型相关资源", notes = "@LoginRequired\n")
    @RequestMapping(value = "/relatedResources/{id}", method = RequestMethod.GET)
    JsonResult getRelatedResources(@PathVariable("id") String id ,HttpServletRequest request){
        System.out.println("test");
        if(StringUtils.isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(modelItemService.getRelatedResources(id));
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


    /**
     * 模型详情页面RelatedData，为模型添加关联的数据
     * @param id 模型id
     * @param relatedData
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "模型详情页面RelatedData，为模型添加关联的数据 [ /modelItem/data ]")
    @RequestMapping(value = "/update/relatedData",method = RequestMethod.POST)
    JsonResult addRelatedData(@RequestParam(value = "modelId") String id,@RequestParam(value = "relatedData") List<String> relatedData){
        return modelItemService.addRelatedData(id,relatedData);
    }


    /**
     * 模型详情页面RelatedData，模型关联的3个数据
     * @param id
     * @return
     */
    @ApiOperation(value = "模型详情页面RelatedData，模型关联的3个数据 [ /modelItem/briefrelateddata ]")
    @RequestMapping(value = "/briefRelatedData",method = RequestMethod.GET)
    JsonResult getBriefRelatedData(@RequestParam(value = "id") String id){
        return ResultUtils.success(modelItemService.getRelatedData(id));
    }


    /**
     * 模型详情页面RelatedData，模型关联的所有数据
     * @param id
     * @param more
     * @return
     */
    @ApiOperation(value = "模型详情页面RelatedData，模型关联的所有数据 [ /modelItem/allrelateddata ]")
    @RequestMapping(value = "/allRelatedData",method = RequestMethod.GET)
    JsonResult getRelatedData(@RequestParam(value = "id") String id,@RequestParam(value = "more") Integer more){
        return ResultUtils.success(modelItemService.getAllRelatedData(id,more));
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
}
