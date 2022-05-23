package njgis.opengms.portal.config;

import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Author bin
 * @Date 2022/05/18
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public JsonResult handleException(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.equals("")) {
            msg = "服务器出错";
        }
        // JSONObject jsonObject = new JSONObject();
        // jsonObject.put("message", msg);
        return ResultUtils.error(msg);
    }
}