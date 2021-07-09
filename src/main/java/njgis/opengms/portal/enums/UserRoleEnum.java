package njgis.opengms.portal.enums;

/**
 * @Description 用户角色枚举类
 * @Author kx
 * @Date 2021/6/30
 * @Version 1.0.0
 */
public enum UserRoleEnum {

    ROLE_ROOT(0,"ROOT"),
    ROLE_ADMIN(1,"ADMIN"),
    ROLE_USER(2,"USER");

    private int code;
    private String role;

    UserRoleEnum(Integer code, String role) {
        this.code = code;
        this.role = role;
    }

    public Integer getCode() {
        return code;
    }

    public String getRole() {
        return role;
    }

    public static UserRoleEnum getUserRoleByRoleName(String name){
        for(UserRoleEnum userRoleEnum:UserRoleEnum.values()){
            if(userRoleEnum.name().toUpperCase().equals(name.toUpperCase())){
                return userRoleEnum;
            }
        }
        return null;
    }

    public static Boolean isAdmin(UserRoleEnum userRole){
        if(userRole == null){
            return false;
        }
        if(userRole.getCode()<2){
            return true;
        }else{
            return false;
        }
    }
}
