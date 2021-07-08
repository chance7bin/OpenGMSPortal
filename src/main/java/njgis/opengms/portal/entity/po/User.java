package njgis.opengms.portal.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import njgis.opengms.portal.entity.doo.*;
import njgis.opengms.portal.entity.doo.user.*;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.enums.UserTitleEnum;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 用户类
 * @Author kx
 * @Date 2021/6/30
 * @Version 1.0.0
 */

@Document
@Data
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class User extends PortalIdPlus {

    //用户个人信息，由用户直接填写///////////////////////////////////////
    String email;
    String password; //md5+sha256加密
    String name; //用户昵称，对应userserver的name
    UserTitleEnum title;
    String gender;
    //用户位置 County / State / Province
    String country;
    String province;
    String city;

    //用户头像
    String avatar = ""; //对应userserver的avatar
    String image = "";

    //个人介绍
    String introduction;
    String description;

    //隶属机构
    String institution;
    Affiliation affiliation;
    List<String> organizations = new ArrayList<>();

    //联系方式
    String phone;
    String weChat;
    String faceBook;
    String twitter;
    String weiBo;

    //存放用户的外部网站个人页面
    String wiki;
    String homePage;
    String personPage;//对应homepage,现为临时字段
    List<String> externalLinks = new ArrayList<>();

    //研究领域
    ArrayList<String> domain;
    List<String> subjectAreas;
    List<String> researchInterests;

    //学术信息
    List<String> articles = new ArrayList<>();//保存article的id
    List<Project> projects;
    List<Conference> conferences;

    List<AcademicService> academicServices;
    List<AwardandHonor> awardsHonors;
    List<EducationExperience> educationExperiences;

    //所属团队
    UserLab lab = new UserLab();

    ///////////////////////////////////////////////////////////////////////

    //网站运行所需的其他信息

    //    String oid; //对应userserver的userId
//    String userId;// 用来访问个人主页，与name一致，重名则加标识

    //登录IP
    String lastLoginIp;
    ArrayList<String> loginIp;
    //根据IP获得的用户位置信息
    GeoInfoMeta geoInfo;

    //运行模型记录
    List<UserTaskInfo> runTask;
    //文件
    List<FileMeta> fileContainer;

    //用户相关资源数量统计
    UserResourceCount resourceCount;

    //通知
    int noticeNum;
    List<UserNotice> noticeList;

    //时间信息
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    Date createTime;
    Date updateTime;
    Date lastSendEmailTime;

    //订阅报表
    Boolean subscribe=true;
    List<UserSubscribeItem> subscribeItemList = new ArrayList<>();

    String dataNodeToken;
    TokenInfo tokenInfo = new TokenInfo();

    UserRoleEnum userRole;
}
