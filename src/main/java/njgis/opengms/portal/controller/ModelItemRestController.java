package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemFindDTO;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.service.ModelItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public JsonResult queryList(ModelItemFindDTO modelItemFindDTO) {

        return ResultUtils.success(modelItemService.query(modelItemFindDTO, false));

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




}