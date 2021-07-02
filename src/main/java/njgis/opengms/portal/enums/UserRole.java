package njgis.opengms.portal.enums;

/**
 * @Description 用户角色枚举类
 * @Author kx
 * @Date 2021/6/30
 * @Version 1.0.0
 */
public enum UserRole {

    ROLE_ADMIN(1,"ADMIN"),
    ROLE_MONITOR(2,"MONITOR"),
    ROLE_USER(3,"USER");

    private int code;
    private String role;

    UserRole(Integer code, String role) {
        this.code = code;
        this.role = role;
    }

    public Integer getCode() {
        return code;
    }

    public String getRole() {
        return role;
    }
}
