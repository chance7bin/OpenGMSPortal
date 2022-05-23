package njgis.opengms.portal.enums;

/**
 * @EnumName ResultEnum
 * @Description 接口返回值枚举
 * @Author sun_liber
 * @Date 2019/2/15
 * @Version 1.0.0
 */

public enum ResultEnum {
    SUCCESS(0, "Success"),
    NO_OBJECT(-1, "No object"),
    ERROR(-2,"Error"),
    UNAUTHORIZED(-1,"Unauthorized");


    private int code;

    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
