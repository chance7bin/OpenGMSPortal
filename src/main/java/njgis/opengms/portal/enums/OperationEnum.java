package njgis.opengms.portal.enums;

import lombok.AllArgsConstructor;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/09
 */
@AllArgsConstructor
public enum OperationEnum {

    //条目的编辑 接受 拒绝
    Edit(0,"edit",ItemTypeEnum.Version),
    Accept(1,"accept",ItemTypeEnum.Version),
    Reject(2,"reject",ItemTypeEnum.Version),

    //对条目操作的通知
    Inform(3,"inform",ItemTypeEnum.Information);

    private int number;
    private String text;
    private ItemTypeEnum type; //通知类型，包括版本审核、评论等

    public String getText() {
        return text;
    }

    public ItemTypeEnum getType() {
        return type;
    }
}
