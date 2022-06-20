package njgis.opengms.portal.entity.doo.intergrate;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ControlCondition.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2022/03/10/ 20:43:00
 */
@Data
public class ControlCondition {
    private String id;

    private String value;//待比较值

    private String format;

    private String expression;//表达式

    private String link;//连接的输出id

    private String trueAction;//actionId

    private String falseAction;//actionId

    private List<ConditionCase> cases;

    private int status = 0;//1标识已经判断过
}
