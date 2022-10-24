package njgis.opengms.portal.controller;

import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.data.dataMethod.DataApplicationFindDTO;
import njgis.opengms.portal.service.DataMethodService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * 下面的接口是UserServer用到的接口
 *
 * @author 7bin
 * @date 2022/10/24
 */
@RestController
@Slf4j
@RequestMapping(value = "/dataApplication")
public class DataApplicationController {

    @Autowired
    DataMethodService dataMethodService;

    /** 下面的接口是UserServer用到的接口 */
    @RequestMapping(value = "/methods/getApplicationInvokable",method = RequestMethod.POST)
    JsonResult getApplicationInvokable(@RequestBody DataApplicationFindDTO dataApplicationFindDTO){
        return  ResultUtils.success(dataMethodService.searchApplication(dataApplicationFindDTO));
    }


    @RequestMapping(value = "/getApplication", method = RequestMethod.GET)      // 这是拿到用户上传的所有条目
    public JsonResult getUserUploadData(@RequestParam(value = "userOid", required = false) String userOid,
                                        @RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "pagesize", required = false) Integer pagesize,
                                        @RequestParam(value = "asc", required = false) Integer asc,
                                        @RequestParam(value = "type", required = false) String type
    ) {
        return ResultUtils.success(dataMethodService.getUsersUploadData(userOid, page, pagesize, asc,type));
    }

    @RequestMapping(value = "/getApplication/{oid}",method = RequestMethod.GET)     // 根据oid拿到条目的所有信息
    public JsonResult getApplicationByOid(@PathVariable("oid") String oid) throws UnsupportedEncodingException {
        return dataMethodService.getApplicationByOid(oid);
    }

}
