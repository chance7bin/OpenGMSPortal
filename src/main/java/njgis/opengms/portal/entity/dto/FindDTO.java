package njgis.opengms.portal.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 查询条件对象
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Data
public class FindDTO implements Serializable {

    private Integer page = 1; //当前页数
    private Integer pageSize = 10; //每页数量
    private Boolean asc = false; //是否顺序，从小到大
    private String searchText; //查询内容


}
