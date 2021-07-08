package njgis.opengms.portal.entity.doo;

import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
public class PortalItem extends PortalIdPlus implements Serializable {

    private static final long serialVersionUID = 1L;

    //Basic Info

    String name; //条目名称
    List<String> alias; //别名
    String image;
    String overview; //简单介绍 原为description
    List<Localization> localizationList; //多语言详细描述

    List<String> keywords; //关键字

    String author; //贡献者email
    List<AuthorInfo> authorships; //资源所有者
    List<String> admins; //管理者
    List<String> contributors; //修改者

//    @JsonFormat(pattern = "yyyy-MM-dd")//是否有用待测试
    Date createTime; //创建时间
//    @JsonFormat(pattern = "yyyy-MM-dd")
    Date lastModifyTime; //最后一次修改时间

    String status; //访问状态 Public, Discoverable or Private

    //版本控制 + contributors + lastModifyTime
    String lastModifier; //最后修改者
    List<String> versions; //历史版本
    boolean lock = false; //同一时间只能有一个待审核的版本。当有版本待审核时，lock为true，且不允许用户编辑条目

    //statistic
    int shareCount = 0;
    int viewCount = 0;
    int thumbsUpCount = 0;
    //每日访问计数
    List<DailyViewCount> dailyViewCount = new ArrayList<>();

}
