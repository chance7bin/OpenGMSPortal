package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.dao.ThemeDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.theme.Application;
import njgis.opengms.portal.entity.doo.theme.Maintainer;
import njgis.opengms.portal.entity.dto.community.theme.ThemeDTO;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.Theme;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/02
 */
@Service
public class ThemeService {

    @Autowired
    ThemeDao themeDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserService userService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    GenericService genericService;

    @Autowired
    VersionService versionService;

    @Value("${resourcePath}")
    private String resourcePath;


    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    public List<Maintainer> getMaintainer(String id){
        Theme theme = themeDao.findFirstById(id);
        List<Maintainer> maintainers = theme.getMaintainer();
        User user;
        for(Maintainer maintainer:maintainers){
            user = userDao.findFirstByEmail(maintainer.getId());
            if(maintainer.getImage()==null||maintainer.getImage().equals("")){
                maintainer.setImage(user.getAvatar());
            }
            if(maintainer.getEmail()==null||maintainer.getEmail().equals("")){
                maintainer.setEmail(user.getEmail());
            }
            if(maintainer.getUserId()==null||maintainer.getUserId().equals("")){
                maintainer.setUserId(user.getAccessId());
            }
            maintainer.setName(user.getName());
        }
        theme.setMaintainer(maintainers);
        themeDao.save(theme);
        return maintainers;
    }


    public Theme getById(String id) {

        try {
            Theme theme = themeDao.findFirstById(id);

            //详情页面
            String detailResult;
            String theme_detailDesc = "";
            if(theme.getLocalizationList()!=null){
                theme_detailDesc=theme.getLocalizationList().get(0).getDescription();
            }
            int num=theme_detailDesc.indexOf("/upload/document/");
            if(num==-1||num>20){
                detailResult=theme_detailDesc;
            }
            else {
                if(theme_detailDesc.indexOf("/")==0){
                    theme_detailDesc.substring(1);
                }
                //model_detailDesc = model_detailDesc.length() > 0 ? model_detailDesc.substring(1) : model_detailDesc;
                String filePath = resourcePath.substring(0,resourcePath.length()-7) +"/" + theme_detailDesc;
                try {
                    filePath = java.net.URLDecoder.decode(filePath, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (theme_detailDesc.length() > 0) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        StringBuilder detail = new StringBuilder();
                        try {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                            BufferedReader br = new BufferedReader(inputStreamReader);
                            String line;
                            while ((line = br.readLine()) != null) {
                                line = line.replaceAll("<h1", "<h1 style='font-size:16px;margin-top:0'");
                                line = line.replaceAll("<h2", "<h2 style='font-size:16px;margin-top:0'");
                                line = line.replaceAll("<h3", "<h3 style='font-size:16px;margin-top:0'");
                                line = line.replaceAll("<p", "<p style='font-size:14px;text-indent:2em'");
                                detail.append(line);
                            }
                            br.close();
                            inputStreamReader.close();
                            fileInputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        detailResult = detail.toString();
                    } else {
                        detailResult = theme_detailDesc;
                    }
                } else {
                    detailResult = theme_detailDesc;
                }
            }
            theme.getLocalizationList().get(0).setDescription(detailResult);

            theme.setImage(theme.getImage().equals("")?"":htmlLoadPath+theme.getImage());
            return theme;
        } catch (Exception e) {
            System.out.println("有人乱查数据库！！该ID不存在Model Item对象");
            throw new MyException(ResultEnum.NO_OBJECT);
        }


    }

    public JSONObject getModelItem(String id){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        JSONObject item = new JSONObject();
        item.put("oid", modelItem.getId());
        item.put("name", modelItem.getName());
        item.put("image", modelItem.getImage());
        item.put("author",userService.getByEmail(modelItem.getAuthor()).getName());

        return item;
    }

    public Theme insertTheme(ThemeDTO themeAddDTO, String email){



        List<Maintainer> maintainers = new ArrayList<>();
        Maintainer maintainer = new Maintainer();
        maintainer.setName(themeAddDTO.getCreator_name());
        User user = userDao.findFirstByEmail(email);
        maintainer.setId(email);
        maintainer.setEmail(user.getEmail());
        maintainer.setName(user.getName());
        maintainer.setImage(user.getAvatar());
        maintainer.setUserId(user.getAccessId());

        maintainers.add(maintainer);
        themeAddDTO.setMaintainer(maintainers);

        Theme theme = new Theme();

        theme = (Theme) repositoryService.commonInsertPart(theme,themeAddDTO, email, ItemTypeEnum.Theme);
        theme.setName(themeAddDTO.getThemename());
        if (themeAddDTO.getDetail() != null)
            theme.setLocalizationList(genericService.detail2localization(theme.getThemename(),themeAddDTO.getDetail()));


        try {
            userService.updateUserResourceCount(email,ItemTypeEnum.Theme,"add");
        }catch (Exception e){
            return null;
        }


        return themeDao.insert(theme);
    }

    public JsonResult deleteTheme(String id, String email) {
        return repositoryService.commonDeletePart(id,email,ItemTypeEnum.Theme);
    }


    public JsonResult updateTheme(ThemeDTO themeDTO, String email, String id) {

        JSONObject result = new JSONObject();

        Theme item = themeDao.findFirstById(id);
        String author = item.getAuthor();
        List<String> versions = item.getVersions();
        String originalItemName = item.getThemename();
        if (!item.isLock()) {

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                themeDao.save(item);
            } else {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(item, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            }

            String detail = Utils.saveBase64Image(themeDTO.getDetail(),id,resourcePath,htmlLoadPath);
            themeDTO.setLocalizationList(genericService.detail2localization(themeDTO.getThemename(),detail));
            themeDTO.setCreator_name(item.getCreator_name());
            themeDTO.setCreator_eid(item.getCreator_eid());
            themeDTO.setMaintainer(item.getMaintainer());
            item = (Theme) repositoryService.updatePart(item,themeDTO,ItemTypeEnum.Theme,email);

            List<Application> applications = themeDTO.getApplication();
            for(int i = 0;i<applications.size();i++){
                String path1 = "/repository/theme/" + UUID.randomUUID().toString() + ".jpg";
                Application application = applications.get(i);
                String[] strs1 = application.getUpload_application_image().split(",");
                if(strs1.length>1){
                    String imgStr = application.getUpload_application_image().split(",")[1];
                    Utils.base64StrToImage(imgStr, resourcePath+path1);
                    application.setApplication_image(path1);
                    //因为upload_application_image为base64，存入数据库非常占内存，故在此处将此属性转为空存入
                    application.setUpload_application_image("");
                } else {
                    application.setApplication_image(application.getUpload_application_image());
                    application.setUpload_application_image("");
                }
            }
            item.setApplication(applications);
            Version new_version = versionService.addVersion(item, email,originalItemName);
            if (author.equals(email)) {
                versions.add(new_version.getId());
                item.setVersions(versions);
                themeDao.save(item);
                result.put("method", "update");
                result.put("id", item.getId());
            } else {


                //发送通知
                List<String> recipientList = Arrays.asList(author);
                recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());
                recipientList = noticeService.addPortalAdmins(recipientList);
                recipientList = noticeService.addPortalRoot(recipientList);
                noticeService.sendNoticeContains(email, OperationEnum.Edit,ItemTypeEnum.Version,new_version.getId(),recipientList);

                result.put("method", "version");
                result.put("versionId", new_version.getId());

            }
            // return result;
        } else {
            result = null;
        }

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }

    }

}
