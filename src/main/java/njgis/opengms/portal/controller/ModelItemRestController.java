package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.ModelItemService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

}
