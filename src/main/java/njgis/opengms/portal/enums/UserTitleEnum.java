package njgis.opengms.portal.enums;

import lombok.Getter;

@Getter
public enum UserTitleEnum {
    Professor("Professor"), Dr("Dr"), Mr("Mr"), Ms("Ms"), Miss("Miss"), Mrs("Mrs"), Mx("Mx"), Unfilled("") ;

    private String desc;

    UserTitleEnum(String desc) {
        this.desc = desc;
    }
}
