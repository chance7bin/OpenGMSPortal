package njgis.opengms.portal.utils;

import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.enums.ResultEnum;

/**
 * @ClassName ResultUtils
 * @Description 接口返回值工具类
 * @Author sun_liber
 * @Date 2019/2/15
 * @Version 1.0.0
 */
public class ResultUtils {
    public static JsonResult success() {
        return success(null);
    }

    public static JsonResult success(Object obj) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setMsg(ResultEnum.SUCCESS.getMsg());
        jsonResult.setCode(ResultEnum.SUCCESS.getCode());
        jsonResult.setData(obj);
        return jsonResult;
    }

    public static JsonResult error() {
        return error(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg());
    }

    public static JsonResult error(String msg) {
        return error(ResultEnum.ERROR.getCode(),msg);
    }

    public static JsonResult error(Integer code, String msg) {
        return error(code, msg, null);
    }

    public static JsonResult error(String msg, Object obj) {
        return error(ResultEnum.ERROR.getCode(), msg, obj);
    }

    public static JsonResult error(Integer code, String msg, Object obj) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setCode(code);
        jsonResult.setMsg(msg);
        jsonResult.setData(obj);
        return jsonResult;
    }

    public static JsonResult unauthorized() {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setCode(ResultEnum.UNAUTHORIZED.getCode());
        jsonResult.setMsg(ResultEnum.UNAUTHORIZED.getMsg());
        return jsonResult;
    }
}
