package njgis.opengms.portal.component;

import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ExceptionControllerAdvice
 * @Description 自定义错误
 * @Author sun_liber
 * @Date 2019/2/15
 * @Version 1.0.0
 */
@ControllerAdvice
@ResponseBody
@Slf4j
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


    // @ExceptionHandler(value = Exception.class)
    // public ModelAndView defaultErrorHandler(Exception e) {
    //     ModelAndView mv = new ModelAndView();
    //     // mv.setViewName("error/500");
    //
    //     //自定义的异常
    //     if (e instanceof MyException) {
    //         MyException myException = (MyException) e;
    //         if (myException.getCode() == ResultEnum.UNAUTHORIZED.getCode()){
    //             mv.setViewName("error/404");
    //             return mv;
    //         }
    //     }
    //     mv.setViewName("error/500");
    //     return mv;
    // }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public JsonResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        // log.error("请求地址[ {} ], 发生业务异常.", requestURI);
        Integer code = e.getCode();
        return code != null ? ResultUtils.error(code, e.getMessage()) : ResultUtils.error(e.getMessage());
    }
}
