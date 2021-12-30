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
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.ModelItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    @RequestMapping(value="/detail/{id}",method = RequestMethod.GET)
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
    @RequestMapping(value = "/items", method = RequestMethod.POST)
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
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.GET)
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
    @RequestMapping(value = "/queryListOfAuthorSelf", method = RequestMethod.GET)
    public JsonResult queryListOfAuthorSelf(ModelItemFindDTO modelItemFindDTO) {

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


    /**
     * 模型详情页面RelatedData，为模型添加关联的数据
     * @param id 模型id
     * @param relatedData
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "模型详情页面RelatedData，为模型添加关联的数据 [ /dataItem/data ]")
    @RequestMapping(value = "/update/relatedData",method = RequestMethod.POST)
    JsonResult addRelatedData(@RequestParam(value = "modelId") String id,@RequestParam(value = "relatedData") List<String> relatedData){
        return modelItemService.addRelatedData(id,relatedData);
    }


    /**
     * 模型详情页面RelatedData，模型关联的3个数据
     * @param id
     * @return
     */
    @ApiOperation(value = "模型详情页面RelatedData，模型关联的3个数据 [ /dataItem/briefrelateddata ]")
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
    @ApiOperation(value = "模型详情页面RelatedData，模型关联的所有数据 [ /dataItem/allrelateddata ]")
    @RequestMapping(value = "/allRelatedData",method = RequestMethod.GET)
    JsonResult getRelatedData(@RequestParam(value = "id") String id,@RequestParam(value = "more") Integer more){
        return ResultUtils.success(modelItemService.getAllRelatedData(id,more));
    }

    @ApiOperation(value = "根据id得到模型条目信息")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.GET)
    public JsonResult getItemById(@PathVariable String id){
        return genericService.getById(id, ItemTypeEnum.ModelItem);
    }

}
