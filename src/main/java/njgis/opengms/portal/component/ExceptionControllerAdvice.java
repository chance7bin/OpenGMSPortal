package njgis.opengms.portal.component;

import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ExceptionControllerAdvice
 * @Description 自定义错误
 * @Author sun_liber
 * @Date 2019/2/15
 * @Version 1.0.0
 */
@ControllerAdvice
@ResponseBody
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<JsonResult> defaultErrorHandler(Exception e) {
        //自定义的异常
        if (e instanceof MyException) {
            MyException myException = (MyException) e;
            return ResponseEntity.status(HttpStatus.OK).body(ResultUtils.error(myException.getCode(), myException.getMessage()));
        } else {//未定义的其他 服务器内部的异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultUtils.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
