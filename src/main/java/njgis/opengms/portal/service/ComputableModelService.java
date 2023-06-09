package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.doo.intergrate.Model;
import njgis.opengms.portal.entity.doo.intergrate.ModelParam;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.doo.model.Resource;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.model.ComputableModelResultDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static njgis.opengms.portal.utils.ModelServiceUtils.checkMdlJson;
import static njgis.opengms.portal.utils.Utils.convertMdl;
import static njgis.opengms.portal.utils.Utils.saveResource;

/**
 * @Description Computable Model Service
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ComputableModelService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserDao userDao;

    @Autowired
    TaskService taskService;

    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    IntegratedTaskDao integratedTaskDao;

    @Value("${htmlLoadPath}")
    String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;

    @Value(value = "Public,Discoverable")
    private List<String> itemStatusVisible;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

    @Autowired
    ModelItemService modelItemService;

    @Autowired
    RedisService redisService;

    public ModelAndView getPage(ComputableModel computableModel) {
        return getPage(computableModel, false);
    }

    public ModelAndView getPage(ComputableModel computableModel, boolean history) {
        //条目信息
        ModelAndView modelAndView = new ModelAndView();

        computableModel=(ComputableModel)genericService.recordViewCount(computableModel);
        if (!history){
            computableModelDao.save(computableModel);
        }

        //时间
        Date date = computableModel.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateResult = simpleDateFormat.format(date);

        //用户信息
        JSONObject userJson = userService.getItemUserInfoByEmail(computableModel.getAuthor());
        //资源信息
        JSONArray resourceArray = new JSONArray();
        List<Resource> resources = computableModel.getResources();

        if (resources != null) {
            for (int i = 0; i < resources.size(); i++) {

                String path = resources.get(i).getPath();

                String[] arr = path.split("\\.");
                String suffix = arr[arr.length - 1];

                arr = path.split("/");
                String name = arr[arr.length - 1].substring(14);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("suffix", suffix);
                jsonObject.put("path", resources.get(i));
                resourceArray.add(jsonObject);

            }

        }

        //排序
        List<Localization> locals = computableModel.getLocalizationList();
        Collections.sort(locals);

        String detailResult = "";
        String detailLanguage = "";
        //先找中英文描述
        for(Localization localization:locals){
            String local = localization.getLocalCode();
            if(local.equals("en")||local.equals("zh")||local.contains("en-")||local.contains("zh-")){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }
        //如果没有中英文，则使用其他语言描述
        if(detailResult.equals("")){
            for(Localization localization:locals){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }

        //语言列表
        List<String> languageList = new ArrayList<>();
        for(Localization local:locals){
            languageList.add(local.getLocalName());
        }


        String lastModifyTime = simpleDateFormat.format(computableModel.getLastModifyTime());

        //修改者信息
        String lastModifier = computableModel.getLastModifier();
        JSONObject modifierJson = null;
        if (lastModifier != null) {
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=computableModel.getAuthorships();
        if(authorshipList!=null){
            for (AuthorInfo author:authorshipList
            ) {
                if(authorshipString.equals("")){
                    authorshipString+=author.getName();
                }
                else{
                    authorshipString+=", "+author.getName();
                }

            }
        }

        //relateModelItemList
        List<String> modelItemIdList = computableModel.getRelatedModelItems();
        JSONArray modelItemInfoList = ArrayUtils.parseListToJSONArray(genericService.getRelatedModelInfoList(modelItemIdList));


        // 关联的任务运行记录
        List<Task> relateTaskRunRecord = taskService.getRelateTaskRunRecordByComputableModelId(computableModel.getId());

        modelAndView.setViewName("computable_model");

        modelAndView.addObject("itemInfo", computableModel);
//            modelAndView.addObject("classifications", classResult);
        modelAndView.addObject("date", dateResult);
        modelAndView.addObject("year", calendar.get(Calendar.YEAR));
        modelAndView.addObject("user", userJson);
        modelAndView.addObject("authorship", authorshipString);
        modelAndView.addObject("resources", resourceArray);
        modelAndView.addObject("detailLanguage",detailLanguage);
        modelAndView.addObject("languageList", languageList);
        modelAndView.addObject("relateTaskRunRecord", relateTaskRunRecord);
//        modelAndView.addObject("description",modelInfo.getOverview());
        modelAndView.addObject("detail",detailResult);
        if(computableModel.getMdl()!=null) {
            modelAndView.addObject("mdlJson", ModelServiceUtils.convertMdl(computableModel.getMdl()).getJSONObject("mdl"));
        }
        JSONObject mdlJson = (JSONObject) JSONObject.toJSON(computableModel.getMdlJson());
        if (mdlJson != null) {
            JSONObject modelClass = (JSONObject) mdlJson.getJSONArray("ModelClass").get(0);
            JSONObject behavior = (JSONObject) modelClass.getJSONArray("Behavior").get(0);
            modelAndView.addObject("behavior", behavior);
        }
        modelAndView.addObject("loadPath", htmlLoadPath);
        modelAndView.addObject("lastModifier", modifierJson);
        modelAndView.addObject("lastModifyTime", lastModifyTime);
//            modelAndView.addObject("relateModelItem", modelItemInfo);
        modelAndView.addObject("relateModelItemList",modelItemInfoList);//之前只关联一个modelitem,现在改为多个

        modelAndView.addObject("modularType", ItemTypeEnum.ComputableModel);
        return modelAndView;
    }


    /**
     * @Description 计算模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/11/11
     **/
    public ModelAndView getPage(String id) {
        ComputableModel computableModel = computableModelDao.findFirstById(id);

        return getPage(computableModel);
    }

    public ComputableModel getInfo(String id){
        ComputableModel computableModel=computableModelDao.findFirstById(id);
        ComputableModelResultDTO computableModelResultDTO=new ComputableModelResultDTO();

        BeanUtils.copyProperties(computableModel,computableModelResultDTO);

        //相关模型信息
        List<String> relatedModelItems = computableModel.getRelatedModelItems();
        computableModelResultDTO.setRelatedModelItemInfoList(genericService.getRelatedModelInfoList(relatedModelItems));

        //资源信息
        List<String> resourcePaths = new ArrayList<>();
        List<Resource> resourceList = computableModel.getResources();
        for(int i = 0;i<resourceList.size();i++){
            resourcePaths.add(resourceList.get(i).getPath());
        }
        List<SimpleFileInfo> simpleFileInfoList = genericService.getSimpleFileInfoList(resourcePaths);
        computableModelResultDTO.setResourceJson(simpleFileInfoList);

        return computableModelResultDTO;
    }

    public JSONObject insert(List<MultipartFile> files, JSONObject jsonObject, String uid) {

        JSONObject result = new JSONObject();
        ComputableModel computableModel = new ComputableModel();

        String path = resourcePath + "/computableModel/" + jsonObject.getString("contentType");

        List<Resource> resources = new ArrayList<>();
        saveResource(files, path, uid, "",resources);
        if (resources == null) {
            result.put("code", -1);
        } else {
            try {
                computableModel.setResources(resources);
                computableModel.setStatus(jsonObject.getString("status"));
                computableModel.setName(jsonObject.getString("name"));

                computableModel.setOverview(jsonObject.getString("description"));

                // computableModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorships"),AuthorInfo.class));
                // computableModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
                JSONArray re = jsonObject.getJSONArray("relatedModelItems");
                List<String> reList = re == null ? null : re.toJavaList(String.class);
                computableModel.setRelatedModelItems(reList);
                Localization localization = new Localization();
                localization.setLocalCode("en");
                localization.setLocalName("English");
                localization.setName(jsonObject.getString("name"));
                localization.setDescription(jsonObject.getString("detail"));
                List<Localization> localizations = new ArrayList<>();
                localizations.add(localization);
                computableModel.setLocalizationList(localizations);

                computableModel.setContentType(jsonObject.getString("contentType"));
                computableModel.setUrl(jsonObject.getString("url"));

                if(jsonObject.getString("image")!=null){
                    String imgPath="/computableModel/" + computableModel.getId() + "/title.jpg";
                    String image = jsonObject.getString("image");
                    String[] strs = image.split(",");
                    if(strs.length>1) {
                        String imgStr = strs[1];
                        Utils.base64StrToImage(imgStr, resourcePath + imgPath);
                        computableModel.setImage(imgPath);
                    } else {
                        computableModel.setImage("");
                    }
                }else{
                    computableModel.setImage(null);
                }

                ModelItem relateModelItem = modelItemDao.findFirstById(jsonObject.getJSONArray("relatedModelItems").getString(0));
                computableModel.setClassifications(relateModelItem.getClassifications());

                String md5 = null;
                if (jsonObject.getString("contentType").equals("Package")) {
                    String filePath = path + resources.get(0).getPath();
                    File file = new File(filePath);

                    md5 = Utils.getMd5ByFile(file);

                    String mdlPath = null;
                    String testDataDirectoryPath = null;
                    String destDirPath = path + "/unZip/" + computableModel.getId();
                    ZipUtils.unZip(new File(filePath), destDirPath);
                    File unZipDir = new File(destDirPath);
                    if (unZipDir.exists()) {
                        LinkedList<File> list = new LinkedList<File>();
                        File[] dirFiles = unZipDir.listFiles();
                        for (File file2 : dirFiles) {
                            if (file2.isDirectory()) {
                                if (file2.getName().equals("testify")) {
                                    testDataDirectoryPath = file2.getAbsolutePath();
                                } else if (file2.getName().equals("model")) {
                                    list.add(file2);
                                }
                            } else {
                                String name = file2.getName();
                                if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                    mdlPath = file2.getAbsolutePath();
                                    break;
                                }
                                // System.out.println("文件:" + file2.getAbsolutePath());
                            }
                        }
                        File temp_file;
                        while (!list.isEmpty()) {
                            temp_file = list.removeFirst();
                            dirFiles = temp_file.listFiles();
                            for (File file2 : dirFiles) {
                                if (file2.isDirectory()) {
                                    continue;
                                } else {
                                    String name = file2.getName();
                                    if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                        mdlPath = file2.getAbsolutePath();
                                        break;
                                    }
                                    // System.out.println("文件:" + file2.getAbsolutePath());
                                }
                            }
                        }
                    } else {
                        // System.out.println("文件不存在!");
                        log.warn("文件不存在!");
                    }

                    //获取测试数据，并进行存储
                    if (testDataDirectoryPath != null) {
                        String testData = ModelServiceUtils.extractTestData(resourcePath, testDataDirectoryPath, computableModel.getId());
                        computableModel.setTestDataPath(testData);
                    } else {
                        computableModel.setTestDataPath("");
                    }

                    String content = "";
                    if (mdlPath != null) {
                        try {
                            BufferedReader in = new BufferedReader(new FileReader(mdlPath));
                            String str = in.readLine();
                            if (str.contains("ModelClass")) {
                                content += str;
                            }
                            while ((str = in.readLine()) != null) {
                                content += str;
                            }
                            in.close();
                            // System.out.println(content);
                        } catch (IOException e) {
                            // System.out.println(e);
                            // e.printStackTrace();
                            log.error(e.getMessage());
                        }

                        computableModel.setMdl(content);
                        JSONObject mdlJson = XmlTool.documentToJSONObject(content);
//
//                        JSONArray HCinsert=modelClass.getJSONArray("Runtime").getJSONObject(0).getJSONArray("HardwareConfigures").getJSONObject(0).getJSONArray("INSERT");
//                        if(HCinsert!=null){
//
//                            JSONArray HCadd= new JSONArray();
//
//                            for(int j=0;j<HCinsert.size();j++){
//                                JSONObject obj=HCinsert.getJSONObject(j);
//                                if (obj.getJSONObject("key")!=null&&obj.getJSONObject("name")!=null){
//                                    HCadd.add(obj);
//                                }
//                            }
//
//                            runtime.getJSONArray("HardwareConfigures").getJSONObject(0).put("Add",HCadd);
//                        }
//
//                        JSONArray SCinsert=modelClass.getJSONArray("Runtime").getJSONObject(0).getJSONArray("SoftwareConfigures").getJSONObject(0).getJSONArray("INSERT");
//                        if(SCinsert!=null){
//
//                            JSONArray SCadd= new JSONArray();
//
//                            for(int j=0;j<HCinsert.size();j++){
//                                JSONObject obj=HCinsert.getJSONObject(j);
//                                if (obj.getJSONObject("key")!=null&&obj.getJSONObject("name")!=null){
//                                    SCadd.add(obj);
//                                }
//                            }
//
//                            runtime.getJSONArray("SoftwareConfigures").getJSONObject(0).put("Add",SCadd);
//                        }
//
//                        modelClass.getJSONArray("Runtime").remove(0);
//                        modelClass.getJSONArray("Runtime").add(runtime);
                        JSONObject modelClass = checkMdlJson(mdlJson);
                        mdlJson.getJSONArray("ModelClass").remove(0);
                        mdlJson.getJSONArray("ModelClass").add(modelClass);
                        //End
                        computableModel.setMdlJson(mdlJson);
                    } else {
                        // System.out.println("mdl文件未找到!");
                        log.warn("mdl文件未找到!");
                    }

                    Utils.deleteDirectory(destDirPath);
                }
                else if(jsonObject.getString("contentType").toLowerCase().equals("md5")){
                    String mdl = jsonObject.getString("mdl");
                    computableModel.setMdl(jsonObject.getString("mdl"));
                    md5 = jsonObject.getString("md5");
                    computableModel.setPageConfigJson(JSONArray.parseArray(jsonObject.getString("pageConfig")));
                    JSONObject mdlJson = XmlTool.documentToJSONObject(mdl);
                    JSONObject modelClass = checkMdlJson(mdlJson);
                    if(mdlJson.containsKey("ModelClass")){
                        mdlJson.getJSONArray("ModelClass").remove(0);
                        mdlJson.getJSONArray("ModelClass").add(modelClass);
                    }
                    //End
                    computableModel.setMdlJson(mdlJson);
                    computableModel.setDeploy(true);
                }

                computableModel.setMd5(md5);

                computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorships"),AuthorInfo.class));

                computableModel.setAuthor(uid);

                Date now = new Date();
                computableModel.setCreateTime(now);
                computableModel.setLastModifyTime(now);

                //联动与计算模型相关的模型关联信息
                List<String> relatedModelItems = computableModel.getRelatedModelItems();
                for(int i=0;i<relatedModelItems.size();i++) {
                    String id = relatedModelItems.get(i);
                    ModelItem modelItem = modelItemDao.findFirstById(id);
                    ModelItemRelate modelItemRelate = modelItem.getRelate();
                    modelItemRelate.getComputableModels().add(computableModel.getId());
                    modelItem.setRelate(modelItemRelate);
                    // modelItemDao.save(modelItem);
                    redisService.saveItem(modelItem,ItemTypeEnum.ModelItem);
                }

                computableModelDao.insert(computableModel);
                userService.updateUserResourceCount(uid , ItemTypeEnum.ComputableModel, "add");

                result.put("code", 1);
                result.put("id", computableModel.getId());
            } catch (Exception e) {
                logger.error("计算模型创建错误",e);
                result.put("code", -2);
            }
        }
        return result;
    }

    public JSONObject update(List<MultipartFile> files, JSONObject jsonObject, String email) {
        JSONObject result = new JSONObject();
        ComputableModel computableModel = computableModelDao.findFirstById(jsonObject.getString("id"));
        String author0 = computableModel.getAuthor();
        String contentType = jsonObject.getString("contentType");
        String originalItemName = computableModel.getName();
        List<String> versions = computableModel.getVersions();
        if (!computableModel.isLock()) {

            if (author0.equals(email)) {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(computableModel, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    computableModel.setVersions(versions);
                }
            }else{
                computableModel.setLock(true);
                // computableModelDao.save(computableModel);
                redisService.saveItem(computableModel,ItemTypeEnum.ComputableModel);
            }

            String path = resourcePath + "/computableModel/" + jsonObject.getString("contentType");
            //如果上传新文件
            if (files.size() > 0) {
                List<Resource> resources =new ArrayList<>();
                saveResource(files, path, email, "",resources);
                if (resources == null) {
                    result.put("code", -1);
                    return result;
                }
                computableModel.setResources(resources);
                computableModel.setMdlJson(null);
                try {
                    String md5 = null;
                    if (jsonObject.getString("contentType").equals("Package")) {
                        String filePath = path + resources.get(0).getPath();
                        FileInputStream file = new FileInputStream(filePath);
                        md5 = DigestUtils.md5DigestAsHex(IOUtils.readFully(file, -1, true));

                        String mdlPath = null;
                        String testDataDirectoryPath = null;
                        String destDirPath = path + "/unZip/" + computableModel.getId();
                        ZipUtils.unZip(new File(filePath), destDirPath);
                        File unZipDir = new File(destDirPath);
                        if (unZipDir.exists()) {
                            LinkedList<File> list = new LinkedList<File>();
                            File[] dirFiles = unZipDir.listFiles();
                            for (File file2 : dirFiles) {
                                if (file2.isDirectory()) {
                                    if (file2.getName().equals("testify")) {
                                        testDataDirectoryPath = file2.getAbsolutePath();
                                    } else if (file2.getName().equals("model")) {
                                        list.add(file2);
                                    }
                                } else {
                                    String name = file2.getName();
                                    if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                        mdlPath = file2.getAbsolutePath();
                                        break;
                                    }
                                    // System.out.println("文件:" + file2.getAbsolutePath());
                                }
                            }
                            File temp_file;
                            while (!list.isEmpty()) {
                                temp_file = list.removeFirst();
                                dirFiles = temp_file.listFiles();
                                for (File file2 : dirFiles) {
                                    if (file2.isDirectory()) {
                                        continue;
                                    } else {
                                        String name = file2.getName();
                                        if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                            mdlPath = file2.getAbsolutePath();
                                            break;
                                        }
                                        // System.out.println("文件:" + file2.getAbsolutePath());
                                    }
                                }
                            }
                        } else {
                            // System.out.println("文件不存在!");
                            log.warn("文件不存在!");
                        }

                        //获取测试数据，并进行存储
                        if (testDataDirectoryPath != null) {
                            String testData = ModelServiceUtils.extractTestData(resourcePath, testDataDirectoryPath, computableModel.getId());
                            computableModel.setTestDataPath(testData);
                        } else {
                            computableModel.setTestDataPath("");
                        }

                        String content = "";
                        if (mdlPath != null) {
                            try {
                                BufferedReader in = new BufferedReader(new FileReader(mdlPath));
                                String str = in.readLine();
                                if (str.contains("ModelClass")) {
                                    content += str;
                                }
                                while ((str = in.readLine()) != null) {
                                    content += str;
                                }
                                in.close();
                                // System.out.println(content);
                            } catch (IOException e) {
                                // System.out.println(e);
                                log.error(e.getMessage());
                            }

                            computableModel.setMdl(content);
                            JSONObject mdlJson = XmlTool.documentToJSONObject(content);
                            computableModel.setMdlJson(mdlJson);
                        } else {
                            // System.out.println("mdl文件未找到!");
                            log.warn("mdl文件未找到!");
                        }

                        Utils.deleteDirectory(destDirPath);
                    }
                    computableModel.setMd5(md5);

                    computableModel.setDeploy(false);

                } catch (Exception e) {
                    // e.printStackTrace();
                    log.error(e.getMessage());
                    result.put("code", -2);
                }
            }else if(contentType.toLowerCase().equals("md5")){
                String mdl = jsonObject.getString("mdl");
                computableModel.setMdl(mdl);
                String md5 = jsonObject.getString("md5");
                computableModel.setMd5(md5);
                computableModel.setPageConfigJson(JSONArray.parseArray(jsonObject.getString("pageConfig")));
                JSONObject mdlJson = XmlTool.documentToJSONObject(mdl);
                JSONObject modelClass = checkMdlJson(mdlJson);
                if(mdlJson.containsKey("ModelClass")){
                    mdlJson.getJSONArray("ModelClass").remove(0);
                    mdlJson.getJSONArray("ModelClass").add(modelClass);
                }
                //End
                computableModel.setMdlJson(mdlJson);

                computableModel.setDeploy(true);
            }

            String uploadImage = jsonObject.getString("image");
            if (uploadImage != null && !uploadImage.contains("/computableModel/") && !uploadImage.equals("")) {
                //删除旧图片
                File file = new File(resourcePath + computableModel.getImage());
                if (file.exists() && file.isFile())
                    file.delete();
                //添加新图片
                String imgPath = "/computableModel/" + computableModel.getId() + "/title.jpg";
                String imgStr = uploadImage.split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + imgPath);
                computableModel.setImage(imgPath);
            }

            computableModel.setName(jsonObject.getString("name"));
            computableModel.setStatus(jsonObject.getString("status"));
            // computableModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
            Localization localization = new Localization();
            localization.setLocalCode("en");
            localization.setLocalName("English");
            localization.setName(jsonObject.getString("name"));
            localization.setDescription(jsonObject.getString("detail"));
            List<Localization> localizations = new ArrayList<>();
            localizations.add(localization);
            computableModel.setLocalizationList(localizations);
            computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorships"),AuthorInfo.class));
            // computableModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
            JSONArray re = jsonObject.getJSONArray("relatedModelItems");
            List<String> reList = re == null ? null : re.toJavaList(String.class);
            computableModel.setRelatedModelItems(reList);
            computableModel.setOverview(jsonObject.getString("description"));
            computableModel.setContentType(jsonObject.getString("contentType"));
            computableModel.setUrl(jsonObject.getString("url"));

            ModelItem relateModelItem = modelItemDao.findFirstById(jsonObject.getJSONArray("relatedModelItems").getString(0));
            computableModel.setClassifications(relateModelItem.getClassifications());

            Date curDate = new Date();
            computableModel.setLastModifyTime(curDate);
            computableModel.setLastModifier(email);

            Version version_new = versionService.addVersion(computableModel, email, originalItemName, false);
            if (computableModel.getAuthor().equals(email)) {
                // computableModelDao.save(computableModel);
                redisService.saveItem(computableModel, ItemTypeEnum.ComputableModel);
                versions.add(version_new.getId());
                computableModel.setVersions(versions);
                result.put("method", "update");
                result.put("id", computableModel.getId());
            } else {
                // 发送通知
                noticeService.sendNoticeContainsAllAdmin(email, computableModel.getAuthor(), computableModel.getAdmins() ,ItemTypeEnum.Version,version_new, OperationEnum.Edit);

                result.put("method", "version");
                result.put("versionId", version_new.getId());
                return result;
            }
            return result;
        } else {
            return null;
        }
    }

    public JsonResult delete(String oid, String email) {
        ComputableModel computableModel = null;
        try {
            computableModel = computableModelDao.findFirstById(oid);
        } catch (Exception e) {
            return ResultUtils.error("根据id查找模型出错");
        }
        if (computableModel != null) {
            //删除资源
            String path = resourcePath + "/computableModel/" + computableModel.getContentType();
            List<Resource> resources = computableModel.getResources();
            for (int i = 0; i < resources.size(); i++) {
                Utils.delete(path + resources.get(i).getPath());
            }

            //模型条目关联删除
            List<String> relatedModelItems = computableModel.getRelatedModelItems();
            for (int i = 0; i < relatedModelItems.size(); i++) {
                String modelItemId = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(modelItemId);
                if (modelItem == null)
                    continue;

                List<String> computableModelIds = modelItem.getRelate().getComputableModels();
                for (String id : computableModelIds
                ) {
                    if (id.equals(computableModel.getId())) {
                        computableModelIds.remove(id);
                        break;
                    }
                }
                modelItem.getRelate().setComputableModels(computableModelIds);
                // modelItemDao.save(modelItem);
                redisService.saveItem(modelItem,ItemTypeEnum.ModelItem);
            }

            //计算模型删除
            // computableModelDao.delete(computableModel);
            redisService.deleteItem(computableModel, ItemTypeEnum.ComputableModel);
            userService.updateUserResourceCount(email, ItemTypeEnum.ComputableModel, "delete");

            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }

    public JSONObject searchDeployedModel(FindDTO findDTO){

        Pageable pageable = genericService.getPageable(findDTO);

        Page<ComputableModel> computableModelPage = computableModelDao.findAllByDeployAndStatusInAndNameLikeIgnoreCase(true,itemStatusVisible,findDTO.getSearchText(),pageable);
        List<ComputableModel> ComputableModelList = computableModelPage.getContent();

        // for(ComputableModel computableModel:ComputableModelList){
        //     // String author = computableModel.getAuthor();
        //     User user = userDao.findFirstByEmail(computableModel.getAuthor());
        //     computableModel.setAuthor(user.getName());
        // }



        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",computableModelPage.getTotalElements());
        jsonObject.put("content",ComputableModelList);

        return jsonObject;
    }

    public JSONObject getRelatedDataByPage(FindDTO computableModelFindDTO,String oid){
        ComputableModel computableModel = computableModelDao.findFirstById(oid);
        JSONObject jsonObject = new JSONObject();
        if(computableModel.getRelatedModelItems()!=null) {
            List<String> modelItemList = computableModel.getRelatedModelItems();
            int dataItem_total = 0;
            JSONArray jsonArray = new JSONArray();
            for(int m = 0; m<modelItemList.size(); m++) {
                ModelItem modelItem = modelItemDao.findFirstById(modelItemList.get(m));
                try {
                    List<String> relatedDataItem = modelItem.getRelate().getDataItems();
                    if (relatedDataItem != null) {
                        int page = computableModelFindDTO.getPage();
                        int pageSize = computableModelFindDTO.getPageSize();

                        for (int i = page * pageSize; i < relatedDataItem.size(); i++) {
                            DataItem dataItem = dataItemDao.findFirstById(relatedDataItem.get(i));

                            Boolean exist = false;
                            for(int j = 0;j<jsonArray.size();j++){
                                JSONObject object = jsonArray.getJSONObject(j);
                                if(object.getString("id").equals(dataItem.getId())){
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist) {
                                JSONObject obj = new JSONObject();
                                obj.put("name", dataItem.getName());
                                obj.put("id", dataItem.getId());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                obj.put("createTime", simpleDateFormat.format(dataItem.getCreateTime()));
                                obj.put("description", dataItem.getOverview());
                                obj.put("contentType", dataItem.getDataType());
                                obj.put("url", dataItem.getUrl());
                                obj.put("dataList", dataItem.getDataList());
                                JSONObject author = new JSONObject();
                                User user = userDao.findFirstByEmail(dataItem.getAuthor());
                                author.put("name", user.getName());
                                author.put("userId", user.getAccessId());
                                obj.put("author", author);

                                jsonArray.add(obj);
                                if (jsonArray.size() == pageSize)
                                    break;
                            }
                        }

                        dataItem_total += relatedDataItem.size();

                    }
                } catch (Exception e) {

                }
            }
            jsonObject.put("list", jsonArray);
            jsonObject.put("total", dataItem_total);
        }
        return jsonObject;
    }

    public void downloadResource(String id, int index, HttpServletResponse response) {

        ComputableModel computableModel = computableModelDao.findFirstById(id);

        if (computableModel == null){
            log.warn("download a resource that does not exist");
            return;
        }


        List<Resource> resources = computableModel.getResources();

        Resource resource = resources.get(index);

        String path = resourcePath + "/computableModel/" + computableModel.getContentType() + resource.getPath();

        File file = new File(path);
        if (!file.exists()){
            log.warn("download a resource that does not exist");
            return;
        }

        int downloadCount = resource.getDownloadCount();
        downloadCount++;
        resource.setDownloadCount(downloadCount);
        computableModel.setResources(resources);
        // computableModelDao.save(computableModel);
        redisService.saveItem(computableModel,ItemTypeEnum.ComputableModel);
        FileUtil.downloadFile(path, response);

    }

    public String deployToGivenServer(String id,String ip,String port) {
        ComputableModel computableModel = computableModelDao.findFirstById(id);
        //String saveFilePath = ConceptualModelService.class.getClassLoader().getResource("").getPath() + "static/upload/computableModel/Package" + computableModel.getResources().get(0);
        if (computableModel == null){
            return null;
        }
        String saveFilePath = resourcePath + "/computableModel/Package" + computableModel.getResources().get(0).getPath();
        String[] paths = computableModel.getResources().get(0).getPath().split("/");
        String fileName = paths[paths.length - 1];
        FileSystemResource deployPackage = new FileSystemResource(saveFilePath);
        String deployUrl = "http://" + ip + ":" + port + "/modelser";

        MultiValueMap<String, Object> part = new LinkedMultiValueMap<>();
        part.add("file_model", deployPackage);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String deployResultStr = restTemplate.postForObject(deployUrl, part, String.class);
            JSONObject deployResult = JSONObject.parseObject(deployResultStr);

            if (deployResult.getInteger("code") == 1) {
                if (deployResult.getString("result").equals("suc")) {
                    String mid = deployResult.getJSONObject("data").getString("_id");
                    JSONObject modelInfo = deployResult.getJSONObject("data").getJSONObject("ms_model");
                    String md5 = modelInfo.getString("p_id");

                    computableModel.setDeploy(true);
                    computableModel.setMd5(md5);
                    // computableModel.setModelserUrl("http://" + ip + ":" + port + "/modelser/" + mid);
                    computableModel.setLastModifyTime(new Date());

                    // computableModelDao.save(computableModel);
                    redisService.saveItem(computableModel,ItemTypeEnum.ComputableModel);

                    return "suc";

                }
            }

            return null;
        }catch (Exception e){
            // System.out.println(e.getMessage());
            log.error(e.getMessage());
            return null;
        }


    }

    public Page<ComputableModel> integratingList(int page, String sortType, int sortAsc){

        Sort sort = Sort.by(sortAsc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page, 8, sort);

        Page<ComputableModel> comModelList = computableModelDao.findByContentType("Package",pageable);

        for(int i = 0; i<comModelList.getContent().size(); i++){
            String mdl = comModelList.getContent().get(i).getMdl();
            if(mdl == null) continue;
            comModelList.getContent().get(i).setMdlJson(convertMdl(mdl));
        }

        return comModelList;
    }

    public ModelAndView integrate(Page<ComputableModel> computableModelList){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("integratedModeling");

//        List<ComputableModel> allComputableModel = computableModelDao.findByContentType("Package");
//        for(int i = 0; i<allComputableModel.size(); i++){
//            String mdl = allComputableModel.get(i).getMdl();
//            allComputableModel.get(i).setMdlJson(convertMdl(mdl));
//        }

        mv.addObject("computableModelList", computableModelList);
//        mv.addObject("allComputableModel", allComputableModel);
        return mv;
    }

    public ModelAndView getIntegratedTask(Page<ComputableModel> computableModelList, String xml, List<ModelParam> modelParams, List<Model> models){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("integratedModeling");
        mv.addObject("computableModelList", computableModelList);
        mv.addObject("graphXml", xml);
        mv.addObject("modelParams", modelParams);
        mv.addObject("models", models);

        return mv;
    }


    public JSONObject pageByClassi(int asc, int page, int pageSize, String sortEle, String searchText, String classification){
        Sort sort = Sort.by(asc==1?Sort.Direction.ASC:Sort.Direction.DESC,sortEle);

        Pageable pageable = PageRequest.of(page,pageSize,sort);

        Page<ComputableModel> computableModelPage = Page.empty();
        if(classification.equals("all")){
            computableModelPage = computableModelDao.findByNameContainsIgnoreCaseAndDeploy(searchText,true,pageable);
        }else{
            List<String> classifications = new ArrayList<>();
            classifications.add(classification);
            computableModelPage = computableModelDao.findAllByClassificationsInAndNameLikeIgnoreCaseAndDeploy(classifications,searchText,true,pageable);
        }

        List<ComputableModel> computableModelResult = computableModelPage.getContent();
        JSONArray j_comptblModelArray = new JSONArray();
        if(computableModelPage.getTotalElements()>0){
            for(ComputableModel computableModel:computableModelResult){
                if(computableModel.getMdl()!=null){
                    try {
                        computableModel.setMdlJson(convertMdl(computableModel.getMdl()));
                    }catch (Exception e){

                    }
                }
                String userName = computableModel.getAuthor();
                User user = userDao.findFirstByEmail(userName);
                JSONObject j_comptblModel = (JSONObject) JSONObject.toJSON(computableModel);
                j_comptblModel.put("authorName",user.getName());
                j_comptblModel.put("authorId",user.getAccessId());
                j_comptblModelArray.add(j_comptblModel);
            }
        }


        JSONObject result = new JSONObject();
        result.put("content",j_comptblModelArray);
        result.put("total",computableModelPage.getTotalElements());

        return result;

    }

    public boolean checkDeployed(String md5){
        String url ="http://" + managerServerIpAndPort + "/GeoModeling/taskNode/getServiceTask/" + md5;

        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.getForObject(url,JSONObject.class);
        if(result.getIntValue("code")==-1){
            return false;
        }
        if(result.getJSONObject("data")!=null){
            return true;
        }else {
            return false;
        }

    }

    public JSONObject getDeployedModelById(String id) {

        ComputableModel computableModel = computableModelDao.findFirstByIdAndDeploy(id,true);

        if(computableModel.getMdl()!=null){
            try {
                computableModel.setMdlJson(convertMdl(computableModel.getMdl()));
            }catch (Exception e){

            }
        }
        String userName = computableModel.getAuthor();
        User user = userDao.findFirstByEmail(userName);
        JSONObject j_comptblModel = (JSONObject) JSONObject.toJSON(computableModel);
        j_comptblModel.put("authorName",user.getName());
        j_comptblModel.put("authorId",user.getAccessId());

        return j_comptblModel;

    }

    public JSONObject searchDeployedModelByUser(String userName,int asc,int page,int size,String searchText){
        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComputableModel> computableModelPage = computableModelDao.findAllByAuthorAndDeployAndStatusInAndNameLikeIgnoreCase(userName,true,itemStatusVisible,searchText,pageable);
        List<ComputableModel> ComputableModelList = computableModelPage.getContent();
        JSONArray j_computableModelArray = new JSONArray();

        for(ComputableModel computableModel:ComputableModelList){
            String author = computableModel.getAuthor();
            User user = userService.getByEmail(computableModel.getAuthor());
            JSONObject j_computableModel = new JSONObject();
            j_computableModel.put("oid",computableModel.getId());
            j_computableModel.put("name",computableModel.getName());
            j_computableModel.put("description",computableModel.getOverview());
            j_computableModel.put("author",user.getName());
            j_computableModel.put("authorId",user.getAccessId());
            j_computableModel.put("md5",computableModel.getMd5());
            j_computableModel.put("mdl",computableModel.getMdl());
            j_computableModel.put("mdlJson",computableModel.getMdlJson());
            j_computableModel.put("createTime",computableModel.getCreateTime());
            j_computableModel.put("lastModifyTime",computableModel.getLastModifyTime());
            j_computableModelArray.add(j_computableModel);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",computableModelPage.getTotalElements());
        jsonObject.put("content",j_computableModelArray);

        return jsonObject;
    }

    public JSONObject searchDeployedModel(int asc,int page,int size,String searchText){
        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComputableModel> computableModelPage = computableModelDao.findAllByDeployAndStatusInAndNameLikeIgnoreCase(true,itemStatusVisible,searchText,pageable);
        List<ComputableModel> ComputableModelList = computableModelPage.getContent();
        JSONArray j_computableModelArray = new JSONArray();

        for(ComputableModel computableModel:ComputableModelList){
            String author = computableModel.getAuthor();
            User user = userService.getByEmail(computableModel.getAuthor());
            JSONObject j_computableModel = new JSONObject();
            j_computableModel.put("oid",computableModel.getId());
            j_computableModel.put("name",computableModel.getName());
            j_computableModel.put("description",computableModel.getOverview());
            j_computableModel.put("author",user.getName());
            j_computableModel.put("authorId",user.getAccessId());
            j_computableModel.put("md5",computableModel.getMd5());
            j_computableModel.put("mdl",computableModel.getMdl());
            j_computableModel.put("mdlJson",computableModel.getMdlJson());
            j_computableModel.put("createTime",computableModel.getCreateTime());
            j_computableModel.put("lastModifyTime",computableModel.getLastModifyTime());
            j_computableModelArray.add(j_computableModel);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",computableModelPage.getTotalElements());
        jsonObject.put("content",j_computableModelArray);

        return jsonObject;
    }

    //专门针对userserver提供的方法
    public JsonResult getInfoForUserserver(String id){

        ComputableModel computableModel=computableModelDao.findFirstById(id);
        ComputableModelResultDTO computableModelResultDTO=new ComputableModelResultDTO();
        BeanUtils.copyProperties(computableModel,computableModelResultDTO);
        computableModelResultDTO.setRelateModelItemName(
            modelItemService.getModelItemName(computableModel.getRelatedModelItems()));

        List<SimpleFileInfo> resourceArray = new ArrayList<>();
        List<Resource> resources = computableModel.getResources();

        if (resources != null) {
            for (int i = 0; i < resources.size(); i++) {

                String path = resources.get(i).getPath();

                String[] arr = path.split("\\.");
                String suffix = arr[arr.length - 1];

                arr = path.split("/");
                String name = arr[arr.length - 1].substring(14);

                // JSONObject jsonObject = new JSONObject();
                // jsonObject.put("id", i);
                // jsonObject.put("name", name);
                // jsonObject.put("suffix", suffix);
                // jsonObject.put("path",htmlLoadPath+resources.get(i));
                SimpleFileInfo simpleFileInfo = new SimpleFileInfo();
                simpleFileInfo.setId(String.valueOf(i));
                simpleFileInfo.setName(name);
                simpleFileInfo.setSuffix(suffix);
                simpleFileInfo.setPath(htmlLoadPath+resources.get(i));
                resourceArray.add(simpleFileInfo);

            }

        }

        computableModelResultDTO.setResourceJson(resourceArray);

        return ResultUtils.success(computableModelResultDTO);

    }

}
