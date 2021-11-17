package njgis.opengms.portal.enums;

/**
 * @Description 用户角色枚举类
 * @Author kx
 * @Date 2021/6/30
 * @Version 1.0.0
 */
public enum UserRoleEnum {

    ROLE_ROOT(0,"ROOT"), //最高权限
    ROLE_ADMIN(1,"ADMIN"), //管理员
    ROLE_USER(2,"USER"); //普通用户

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

    /**
     * @Description 判断是否为管理员
     * @Return java.lang.Boolean
     * @Author kx
     * @Date 21/11/12
     **/
    public Boolean isAdmin(){
        //code == 0 / 1 为管理员
        return this.getCode()<2;
    }
}
