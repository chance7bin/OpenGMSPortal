package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.annotation.LoginRequired;
import njgis.opengms.portal.dao.DataCategorysDao;
import njgis.opengms.portal.dao.DataHubDao;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.community.KnowledgeDTO;
import njgis.opengms.portal.entity.dto.data.dataItem.DataItemDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.*;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description hub controller
 * @Author bin
 * @Date 2021/08/25
 */
@RestController
@RequestMapping(value = "/dataHub")
@Slf4j
public class DataHubController
{

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

    @Autowired
    DataHubService dataHubService;

    @Autowired
    RepositoryService repositoryService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;


    /**
     * 新增dataHubs
     * @param dataItemAddDTO 新增hubs的数据
     * @return 成功失败标识
     */
    @LoginRequired
    @ApiOperation(value = "新增dataHubs [ /dataItem/createHubs ]")
    @PostMapping
    public JsonResult addHubs(@RequestBody DataItemDTO dataItemAddDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        // dataItemAddDTO.setAuthor(email);
        return dataItemService.insert(dataItemAddDTO, email, ItemTypeEnum.DataHub);
    }

    /**
     * 更新data hub
     * @param dataItemUpdateDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新data hub [ /dataItem/updateHubs ]")
    @PutMapping(value = "/{id}")
    public JsonResult updateDataHubs(@PathVariable String id , @RequestBody DataItemDTO dataItemUpdateDTO, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result= dataHubService.updateDataHubs(dataItemUpdateDTO,email,id);
        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }


    /**
     * 个人中心删除dataHub操作
     * @param id 待删除的dataHubId
     * @return 返回删除成功与否的标识
     */
    @LoginRequired
    @ApiOperation(value = "个人中心删除dataHub操作 [ /dataItem/delHubs ]")
    @DeleteMapping(value = "/{id}")
    public JsonResult delete(@PathVariable(value="id") String id ,HttpServletRequest request){
        HttpSession session=request.getSession();

        // if(session.getAttribute("email")==null){
        //     return ResultUtils.error(-1,"no login");
        // }

        String email=session.getAttribute("email").toString();
        return dataHubService.delete(id,email);
        // return null;
    }

    /**
     * @Description 获取Hub Repository下的数据
     * @Author bin
     * @Param [dataHubsFindDTO]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "获取Hub Repository下的数据 [ /dataItem/Items/getHubs ]")
    @RequestMapping(value = {"/items", "/list"},method = RequestMethod.POST)
    public JsonResult getHubs(@RequestBody SpecificFindDTO dataHubsFindDTO){
        return  dataHubService.getHubs(dataHubsFindDTO);
    }

    /**
     * @Description 获取数据中心条目
     * @Param id dataItem的id
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @ApiOperation(value = "根据id返回详情界面 [ /dataItem/hub/{id} ]")
    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
    public ModelAndView getHub(@PathVariable("id") String id) throws IOException, URISyntaxException, DocumentException {
        ModelAndView page = dataItemService.getPage(id, dataHubDao);
        page.addObject("modularType", ItemTypeEnum.DataHub);
        return page;
    }



    /**
     * 根据id得到dataHub条目
     * @param id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据id得到dataHub条目(修改item时调用的接口) [ /dataItem/getDataHubsByDataId ]")
    @RequestMapping(value="/itemInfo/{id}",method = RequestMethod.GET)
    public JsonResult getDataHubsByDataId(@PathVariable(value="id") String id){
        // HttpSession session = request.getSession();
        // if(session.getAttribute("email")==null){
        //     return ResultUtils.error(-1,"no login");
        // }
        // else
        //     return ResultUtils.success(dataItemService.getItemByDataId(id,"hub"));
        return ResultUtils.success(dataItemService.getItemByDataId(id,ItemTypeEnum.DataHub));
    }


    /**
     * 得到用户上传的data hub
     * @param findDTO
     * @param request
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "得到用户上传的data hub [ /dataHub/listByUserOid[searchByNameByOid] ]")
    @RequestMapping(value="/itemsByNameAndAuthor",method = RequestMethod.POST)
    public JsonResult searchByNameAndAuthor(@RequestBody SpecificFindDTO findDTO,HttpServletRequest request){
        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();
        return dataHubService.searchByNameAndAuthor(findDTO, email);
    }


    @ApiOperation(value = "根据id得到DataHub信息")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.GET)
    public JsonResult getItemById(@PathVariable String id){
        return genericService.getById(id, ItemTypeEnum.DataHub);
    }


    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.DataHub,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody UserFindDTO findDTO, HttpServletRequest request) {
        if (findDTO.getAuthorEmail() == null ||  "".equals(findDTO.getAuthorEmail())){
            String email= request.getSession().getAttribute("email").toString();
            findDTO.setAuthorEmail(email);
        }
        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.DataHub,findDTO, true));

    }


    /**
     * @Description 设置与数据条目相关的模型
     * @Param id datahub的id
     * @Param relations :增加的相关模型
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "设置与数据条目相关的模型 [ /dataItem/setRelation ]")
    @RequestMapping(value="/relation",method = RequestMethod.PUT)
    public JsonResult setRelation(@RequestParam(value="id") String id,
                                  @RequestParam(value = "relations[]", required=false) List<String> relations, HttpServletRequest request){

        id = genericService.formatId(id);
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();

        if (relations == null){
            relations = new ArrayList<>();
        }

        return dataHubService.setRelation(id,relations,email);

    }


    @ApiOperation(value = "获取与数据中心相关的模型")
    @RequestMapping(value="/relation",method = RequestMethod.GET)
    public JsonResult getRelation(@RequestParam(value = "id") String id){

        id = genericService.formatId(id);

        JSONArray result=dataHubService.getRelation(id);

        return ResultUtils.success(result);

    }

    @LoginRequired
    @ApiOperation(value = "更新knowledge")
    @PostMapping(value = "/knowledge")
    public JsonResult updateKnowledge(@RequestBody KnowledgeDTO knowledgeDTO, HttpServletRequest request) throws IOException {

        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();

        JSONObject result = repositoryService.updateKnowledge(ItemTypeEnum.DataHub ,knowledgeDTO,email);

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @ApiOperation(value = "获取模型相关资源", notes = "@LoginRequired\n")
    @RequestMapping(value = "/relatedResources/{id}", method = RequestMethod.GET)
    JsonResult getRelatedResources(@PathVariable("id") String id, HttpServletRequest request){
        // System.out.println("test");
        if(StringUtils. isEmpty(Utils.checkLoginStatus(request))){
            return ResultUtils.error(-1, "no login");
        }
        return ResultUtils.success(repositoryService.getRelatedResources(id, ItemTypeEnum.DataHub));
    }
}
