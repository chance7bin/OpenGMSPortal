package njgis.opengms.portal.entity.doo.intergrate;

import lombok.Data;

/**
 * @ClassName ConditionCase.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2022/03/10/ 20:44:00
 */
@Data
public class ConditionCase {

    private String orderId;

    private String opertator;//< > = != <= >=

    private String standard;//判断标准

    private String relation;//与顺序下一个的case的关系 and or

}
