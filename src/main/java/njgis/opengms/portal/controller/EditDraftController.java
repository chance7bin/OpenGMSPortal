package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.draft.EditDraftDTO;
import njgis.opengms.portal.service.EditDraftService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
@RestController
@RequestMapping(value = "/draft1")  //原先的路由是draft，因为暂时不用这个功能，所以把这个路由先改掉，防止后端出错
public class EditDraftController {

    @Autowired
    EditDraftService editDraftService;

    /**
     * 根据用户以及条目类型得到草稿
     * @param itemType 条目类型
     * @param editType 操作 create/edit
     * @param httpServletRequest 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户以及条目类型得到草稿 [ /draft/getCreateDraftByUserByType ]")
    @RequestMapping(value = "/draftByUserAndType",method = RequestMethod.GET)
    public JsonResult getCreateDraftByUserByType(
        @ApiParam(name = "itemType", value = "条目类型", required = true) @RequestParam(value="itemType")String itemType,
        @ApiParam(name = "editType", value = "操作 create/edit", required = true) @RequestParam(value = "editType")String editType, 
        HttpServletRequest httpServletRequest){
        
        HttpSession session = httpServletRequest.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(editDraftService.getCreateDraftByUserByType(email,itemType,editType));
    }

    
    /**
     * 得到草稿信息
     * @param editDraftDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(
        value = "保存草稿信息 必填参数见notes( 点我! 点我! )",
        notes = "<strong>draftId:</strong>新增draft不需要，更新draft需要<strong>(必须要,不然会一直创建新的草稿)</strong>，" +
            "刚打开编辑界面的时候draftId为空，loadDraft或者在input第一次输入时就要设置draftId了</br> " +
            "<strong>itemType:</strong>条目类型</br> " +
            "<strong>user:</strong>用户email(登录才能得到用户信息)</br> " +
            "<strong>editType:</strong>标识是新建条目还是编辑create edit" +
            "<strong>itemId:</strong>如果是edit,填入条目信息" +
            "<strong>itemName:</strong>如果是edit,填入编辑的条目名" +
            "<strong>content中的description改为overview</strong>")
    @RequestMapping(value = "/init",method = RequestMethod.POST)
    public JsonResult init(@RequestBody EditDraftDTO editDraftDTO){
        return ResultUtils.success(editDraftService.init(editDraftDTO));
    }


    /**
     * 根据用户得到草稿
     * @param asc
     * @param page
     * @param itemType
     * @param size
     * @param httpServletRequest 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "根据用户得到草稿")
    @RequestMapping(value = "/pageByUser",method = RequestMethod.GET)
    public JsonResult getByUser(@RequestParam(value="asc") int asc,
                                @RequestParam(value = "page") int page,
                                @RequestParam(value = "itemType") String itemType,
                                @RequestParam(value = "size") int size,HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(editDraftService.pageByUser(asc,page,size,itemType,email));
    }


    /**
     * 删除草稿
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "删除草稿")
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public JsonResult deleteById(@PathVariable(value = "id") String id){
        return editDraftService.delete(id);
    }

    /**
     * 更新草稿
     * @param editDraftDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    @LoginRequired
    @ApiOperation(value = "更新草稿")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public JsonResult updateDraft(@RequestBody EditDraftDTO editDraftDTO){
        return ResultUtils.success(editDraftService.update(editDraftDTO));
    }


    @ApiOperation(value = "得到用户的草稿信息")
    @RequestMapping(value = "/listByUser",method = RequestMethod.GET)
    public JsonResult listByUser(HttpServletRequest httpServletRequest,@RequestParam("sort") Boolean sort){
        HttpSession session = httpServletRequest.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(editDraftService.listByUser(email,sort));
    }

}
