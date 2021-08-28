package njgis.opengms.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @EnumName ResultEnum
 * @Description 接口返回值枚举
 * @Author sun_liber
 * @Date 2019/2/15
 * @Version 1.0.0
 */

public enum ResultEnum {
    SUCCESS(1, "Success"),
    NO_OBJECT(0, "No object"),
    ERROR(-1,"Error"),
    UNAUTHORIZED(-2,"Unauthorized");


    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
