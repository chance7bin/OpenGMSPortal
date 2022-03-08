package njgis.opengms.portal.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.ModifiedPropertyInfo;
import njgis.opengms.portal.entity.doo.PropertyModelInfo;
import njgis.opengms.portal.entity.doo.base.PortalIdPlus;
import njgis.opengms.portal.entity.doo.model.Resource;
import njgis.opengms.portal.entity.doo.support.GeoInfoMeta;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 通用工具类
 * @Author kx
 * @Date 2021/7/6
 * @Version 1.0.0
 */
@Slf4j
public class Utils {

    // public static class Method {
    //     public static String POST = "POST";
    //     public static String GET = "GET";
    // }


    /**
     * @Description 检查用户登录状态
     * @param request
     * @Return java.lang.String 返回用户邮箱或null
     * @Author kx
     * @Date 2021/7/6
     **/
    public static String checkLoginStatus(HttpServletRequest request){

        HttpSession httpSession = request.getSession();
        Object object=httpSession.getAttribute("email");
        if(object==null){
            return null;
        }
        else{
            return object.toString();
        }

    }

    /**
     * @Description 根据条目类别生成该类别内的唯一AccessId
     * @param name accessId 依据的名字
     * @param portalIdPlusList 该类别下所有条目id的列表
     * @param isUser 是否为User，user和其他条目的accessId生成方法不一样
     * @Return java.lang.String
     * @Author kx
     * @Date 2021/7/6
     **/
    public static String generateAccessId(String name, List<PortalIdPlus> portalIdPlusList, Boolean isUser){
        if(!isUser) { //非User
            name = name.trim().replaceAll("[\\[\\]{}'‘’“”\\\\/ \\-‐,，.。<《>》=~`!@#$%^&*·！?？、|:：;；]", "_");
            name = name.replaceAll("（", "(").replaceAll("）", ")");

            //去掉字符串中（）中的内容
            name = clearBracket(name).replaceAll("[()]", "_");
            Pattern pattern = Pattern.compile("(_)\\1+");
            Matcher matcher = pattern.matcher(name);
            //sb 缓冲区作为存储替换后的结果 
            StringBuffer sb = new StringBuffer();
            //用正则表达式的find函数去判断，有没有匹配的结果集 
            while (matcher.find()) {
                //match.group(0) 是匹配的字符串，比如111234445466中的111   
                // match.group(1) 是匹配的字符串的单个字符，比如111234445466中的111中的1   
                String repeat = matcher.group(1);
                //appendReplacement用第二个参数去替换匹配成功的子串，并把结果存放在sb中，前面未匹配成功的也会放进去，后面的未匹配成功的不会放进去。例如：11123444546634 最后会替换成(1)23(4)54(6)   
                matcher.appendReplacement(sb, repeat);
            }
            //把后面未匹配成功的附加到sb上,例如：11123444546634 最后会替换成(1)23(4)54(6)34 
            matcher.appendTail(sb);
            name = sb.toString();
            if (name.endsWith("_")) {
                name = name.substring(0, name.length() - 1);
            }
        }else{ //User
            name = name.trim().replaceAll(" ","_");
        }

        //若没有重名，则直接使用
        if(portalIdPlusList.size()==0) {
            return name;
        }else { //有重名则判断是否是真正重名，并计算排序序号
            List<Integer> orders = new ArrayList<>();
            for (PortalIdPlus portalIdPlus : portalIdPlusList) {
                //userid前部是否一致
                if (portalIdPlus.getAccessId().startsWith(name)) {
                    String left = portalIdPlus.getAccessId().replace(name, "");
                    if (left.equals("")) {
                        orders.add(1);
                    }else{
                        String[] leftNameSplit = left.split("_");
                        if(leftNameSplit.length==2 && leftNameSplit[0].equals("")){
                            try {
                                int num = Integer.parseInt(leftNameSplit[1]);
                                orders.add(num);
                            }catch (Exception e){
                                continue;
                            }
                        }
                    }
                }
            }
            if(orders.size()==0) { // 不是真正重名
                return name;
            }else {
                orders.sort(Comparator.comparingInt(Integer::intValue).reversed());
                return name + "_" + (orders.get(0)+1);
            }
        }
    }

    /**
     * @Description 去掉字符串中（）中的内容
     * @param context
     * @Return java.lang.String
     * @Author kx
     * @Date 2021/7/6
     **/
    private static String clearBracket(String context) {
//        String bracket = context.substring(context.indexOf("（"), context.indexOf("）") + 1);
//        context = context.replace(bracket, "");
//
//        context.substring(context.lastIndexOf())
//
//        return context;

        // 修改原来的逻辑，防止右括号出现在左括号前面的位置
        int head = context.indexOf('('); // 标记第一个使用左括号的位置
        if (head == -1) {
            return context; // 如果context中不存在括号，什么也不做，直接跑到函数底端返回初值str
        } else {
            int next = head + 1; // 从head+1起检查每个字符
            int count = 1; // 记录括号情况
            do {
                if(next >= context.length()){
                    break;
                }
                if (context.charAt(next) == '(') {
                    count++;
                } else if (context.charAt(next) == ')') {
                    count--;
                }
                next++; // 更新即将读取的下一个字符的位置
                if (count == 0) { // 已经找到匹配的括号
                    String temp = context.substring(head, next);
                    context = context.replace(temp, ""); // 用空内容替换，复制给context
                    head = context.indexOf('('); // 找寻下一个左括号
                    next = head + 1; // 标记下一个左括号后的字符位置
                    count = 1; // count的值还原成1
                }
            } while (head != -1); // 如果在该段落中找不到左括号了，就终止循环
        }
        return context; // 返回更新后的context
    }

    /**
     * @Description 判断一个字符串是否存在于字符串列表中
     * @param
     * @Return java.lang.Boolean
     * @Author kx
     * @Date 2021/7/7
     **/
    public static Boolean isStrInList(String str, List<String> stringList){
        for(String s : stringList){
            if(str.equals(s)){
                return true;
            }
        }
        return false;
    }


    /**
     * @Description 打印代码所在行数()
     * 使用方法: 在类上标上注解@Slf4j
     * log.info("[定时任务第 " + counts.incrementAndGet() + "次执行]" + Utils.getLineNumber());
     * @Author bin
     * @return java.lang.String
     **/
    public static String getLineNumber(){
        return " --- lineNumber:" + Thread.currentThread().getStackTrace()[1].getLineNumber();
    }


    /**
     * base64字符串转化成图片
     * @param imgStr
     * @param path
     * @return boolean
     * @Author bin
     **/
    public static boolean base64StrToImage(String imgStr, String path) {
        if (imgStr == null)
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            //文件夹不存在则自动创建
            File tempFile = new File(path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(tempFile);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 保存文件
     * @param files
     * @param path
     * @param uid
     * @param suffix
     * @param result
     * @return java.util.List<java.lang.String>
     **/
    public static List<Resource> saveResource(List<MultipartFile> files, String path, String uid, String suffix,List<Resource> result) {
        new File(path).mkdirs();


        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            fileName = "/" + uid + "/" + new Date().getTime() + "_" + fileName;
            Resource resource = new Resource();
            resource.setPath(suffix + fileName);
            result.add(resource);
            int size = (int) file.getSize();
            log.info(fileName + "-->" + size);

            if (file.isEmpty()) {
                continue;
            } else {
                File dest = new File(path + fileName);
                if (!dest.getParentFile().exists()) { // 判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return result;
    }

    public static List<String> saveFiles(List<MultipartFile> files, String path, String uid, String suffix,List<String> result) {
        new File(path).mkdirs();


        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            fileName = "/" + uid + "/" + new Date().getTime() + "_" + fileName;
            result.add(suffix + fileName);
            int size = (int) file.getSize();
            log.info(fileName + "-->" + size);

            if (file.isEmpty()) {
                continue;
            } else {
                File dest = new File(path + fileName);
                if (!dest.getParentFile().exists()) { // 判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return result;
    }


    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean delete(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 差集(基于API解法) 适用于小数据量
     * 求List1中有的但是List2中没有的元素
     * 时间复杂度 O(list1.size() * list2.size())
     */
    public static List<String> subList(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<>(list1);
        list.removeAll(list2);
        return list;
    }


    /**
     * 比较两个对象属性值是否相同,如果不同返回修改过的属性信息集合,包括：字段名,原始数据值，新值
     *
     * @param oldObj       原始对象
     * @param newObj       新对象
     * @param ignoreFields 忽略比较字段,如果忽略比较字段和记录比较字段中都有相同字段，则忽略比较字段优先级较高
     * @param recordFields 记录比较字段
     * @return 变化后的数据集合
     */
    public static <T> List<ModifiedPropertyInfo> compareProperty(
        @NonNull T oldObj,
        @NonNull T newObj,
        List<String> ignoreFields,
        List<String> recordFields
    ) {
        // 通过反射获取原始对象的属性名称、getter返回值类型、属性值等信息
        List<PropertyModelInfo> oldObjectPropertyValue = getObjectPropertyValue(oldObj, ignoreFields, recordFields);
        // 通过反射获取新对象的属性名称、getter返回值类型、属性值等信息
        List<PropertyModelInfo> newObjectPropertyValue = getObjectPropertyValue(newObj, ignoreFields, recordFields);

        // 定义修改属性值接收器集合,包括：字段名,原始数据值，新值
        List<ModifiedPropertyInfo> modifiedPropertyInfos = new ArrayList<>(oldObjectPropertyValue.size());

        // 获取新对象所有属性、属性值
        Map<String, Object> newObjectInfoMap = getObjectPropertyAndValue(newObjectPropertyValue);
        // 获取原始对象所有属性、属性值
        Map<String, Object> oldObjectInfoMap = getObjectPropertyAndValue(oldObjectPropertyValue);

        // 处理原始对象数据内容并和新对象的属性、属性值比较
        for (Map.Entry<String, Object> oldInfoMap : oldObjectInfoMap.entrySet()) {
            String oldKey = oldInfoMap.getKey();
            Object oldValue = oldInfoMap.getValue();

            // 比较原始对象和新对象之间的属性和属性值差异
            if (newObjectInfoMap.containsKey(oldKey)) {
                ModifiedPropertyInfo modifiedPropertyInfo = new ModifiedPropertyInfo();
                Object newValue = newObjectInfoMap.get(oldKey);

                if (oldValue != null && newValue != null) {
                    // 初始值和新值不为空比较值
                    if (!oldValue.equals(newValue)) {
                        modifiedPropertyInfo.setPropertyName(oldKey);
                        modifiedPropertyInfo.setOldValue(oldValue);
                        modifiedPropertyInfo.setNewValue(newValue);
                        modifiedPropertyInfos.add(modifiedPropertyInfo);
                    }
                } else if (oldValue == null && newValue == null) {
                    // 如果初始值和新值全部为空不做处理
                } else {
                    // 如果初始值和新值有一个为空
                    modifiedPropertyInfo.setPropertyName(oldKey);
                    modifiedPropertyInfo.setOldValue(oldValue);
                    modifiedPropertyInfo.setNewValue(newValue);
                    modifiedPropertyInfos.add(modifiedPropertyInfo);
                }
            }
        }
        return modifiedPropertyInfos;
    }

    /**
     * 组建对象属性和属性值信息数据
     *
     * @param objectPropertyValue 对象属性和属性值信息
     * @return 对象属性和属性值信息
     */
    private static Map<String, Object> getObjectPropertyAndValue(List<PropertyModelInfo> objectPropertyValue) {
        Map<String, Object> objectInfoMap = new HashMap<>(16);
        for (PropertyModelInfo propertyModelInfo : objectPropertyValue) {
            // 对象属性名称
            String propertyName = propertyModelInfo.getPropertyName();
            // 对象属性值
            Object value = propertyModelInfo.getValue();
            objectInfoMap.put(propertyName, value);
        }
        return objectInfoMap;
    }

    /**
     * 通过反射获取对象的属性名称、getter返回值类型、属性值等信息
     *
     * @param obj          对象
     * @param ignoreFields 忽略字段属性集合
     * @param recordFields 比较字段属性集合
     * @return 对象的信息
     */
    private static <T> List<PropertyModelInfo> getObjectPropertyValue(
        @NonNull T obj,
        List<String> ignoreFields,
        List<String> recordFields
    ) {
        Class<?> objClass = obj.getClass();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(objClass);
        List<PropertyModelInfo> modelInfos = new ArrayList<>(propertyDescriptors.length);

        // 遍历对象属性信息
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Method readMethod = propertyDescriptor.getReadMethod();
            // 该字段属性是否被过滤
            boolean isIgnore = ignoreFields == null || !ignoreFields.contains(name);
            // 该字段属性是否必须被比较
            boolean isRecord = recordFields == null || recordFields.contains(name);
            if (readMethod != null && isIgnore && isRecord) {
                try {
                    // 获取数据类型
                    Class<?> returnType = readMethod.getReturnType();
                    // 获取数据值
                    Object invoke = readMethod.invoke(obj);
                    modelInfos.add(new PropertyModelInfo(name, invoke, returnType));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("反射获取类:" + objClass.getName() + "方法异常:", e);
                }
            }
        }
        return modelInfos;
    }

    /**
     * @Description 计算文件md5
     * @param file
     * @Return java.lang.String
     * @Author kx
     * @Date 21/11/12
     **/
    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    // public static class Method {
    //     public static String POST = "POST";
    //     public static String GET = "GET";
    // }
    public static JSONObject connentURL(String method, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(3000);
            connection.connect();
            // 取得输入流，并使用Reader读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));//设置编码,否则中文乱码
            String lines = "";
            String strResponse = "";
            while ((lines = reader.readLine()) != null) {
                strResponse += lines;
            }
            JSONObject jsonResponse = JSONObject.parseObject(strResponse);

            reader.close();

            connection.disconnect();

            return jsonResponse;


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static JSONObject postJSON(String urlStr, JSONObject jsonParam) {
        try {

            //System.out.println(obj);
            // 创建url资源
            URL url = new URL(urlStr);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);

            conn.setDoInput(true);

            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] data = (jsonParam.toJSONString()).getBytes();

            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));

            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");


            // 开始连接请求
            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write(data);
            out.flush();
            out.close();

            // System.out.println(conn.getResponseCode());
            // System.out.println(conn.getResponseMessage());

            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                // System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();
                String a = null;
                try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    // 转成字符串
                    a = new String(data1);
                    // System.out.println(a);
                    return JSONObject.parseObject(a);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return null;
                }
            } else {
                // System.out.println("no++");
                return null;
            }

        } catch (Exception e) {
            // System.out.println(e);
            return null;
        }

    }

    /**
     * mdl2json
     * @param mdl
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public static JSONObject convertMdl(String mdl) {
        JSONObject mdlObj = new JSONObject();
        try {
            Document mdlDoc = DocumentHelper.parseText(mdl);
            Element rootElement = mdlDoc.getRootElement();
            mdlObj.put("name", rootElement.attributeValue("name"));


            Element AttributeSet = rootElement.element("AttributeSet");
            Element Behavior = rootElement.element("Behavior");

            //基本属性开始
            Element Category = AttributeSet.element("Categories").element("Category");
            mdlObj.put("principle", Category.attributeValue("principle"));
            mdlObj.put("path", Category.attributeValue("path"));

            List<Element> LocalAttributes = AttributeSet.element("LocalAttributes").elements();
            if (LocalAttributes.size() > 0) {
                for (Element LocalAttribute : LocalAttributes) {
                    JSONObject local = new JSONObject();
                    local.put("localName", LocalAttribute.attributeValue("localName"));
                    Element Keywords = LocalAttribute.element("Keywords");
                    local.put("keywords", Keywords.getText());
                    Element Abstract = LocalAttribute.element("Abstract");
                    local.put("abstract", Abstract.getText());
                    if (LocalAttribute.attributeValue("local").equals("EN_US")) {
                        mdlObj.put("enAttr", local);
                    } else {
                        mdlObj.put("cnAttr", local);
                    }
                }
            }
            //基本属性结束

            //相关数据开始


            Element RelatedDatasets = Behavior.element("RelatedDatasets");
            if (RelatedDatasets == null) {
                RelatedDatasets = Behavior.element("DatasetDeclarations");
            }
            List<Element> DatasetItems = RelatedDatasets.elements();
            if (DatasetItems.size() > 0) {
                String relatedDatasets = mdl.substring(mdl.indexOf("<RelatedDatasets>") + 17, mdl.indexOf("</RelatedDatasets>"));
                JSONArray DatasetItemArray = new JSONArray();
                for (Element DatasetDeclaration : DatasetItems) {
                    JSONArray dataset = new JSONArray();
                    JSONObject root = new JSONObject();
                    root.put("text", DatasetDeclaration.attributeValue("name"));
                    if (DatasetDeclaration.attribute("description") != null) {
                        root.put("desc", DatasetDeclaration.attributeValue("description"));
                    } else {
                        root.put("desc", "");
                    }
                    root.put("dataType", DatasetDeclaration.attributeValue("type"));
                    if (DatasetDeclaration.attributeValue("type").equals("external")) {
                        String external = "";
                        if (DatasetDeclaration.attribute("externalId") != null) {
                            external = DatasetDeclaration.attributeValue("externalId");
                        } else if (DatasetDeclaration.attribute("external") != null) {
                            external = DatasetDeclaration.attributeValue("external");
                        }
                        root.put("externalId", external.toLowerCase());

                        root.put("parentId", "null");
                        dataset.add(root);
                    } else {
                        Element UDXDeclaration;
                        if (DatasetDeclaration.element("UdxDeclaration") != null) {
                            UDXDeclaration = DatasetDeclaration.element("UdxDeclaration");
                        } else {
                            UDXDeclaration = DatasetDeclaration.element("UDXDeclaration");
                        }
                        String rootId = "";
                        if (UDXDeclaration.attribute("id") != null) {
                            rootId = "root" + UDXDeclaration.attributeValue("id");
                        } else {
                            rootId = "root" + UUID.randomUUID().toString();
                        }
                        root.put("Id", rootId);
                        root.put("parentId", "null");

                        Element udxNode;
                        if (UDXDeclaration.element("UDXNode") != null) {
                            udxNode = UDXDeclaration.element("UDXNode");
                        } else {
                            udxNode = UDXDeclaration.element("UdxNode");
                        }
                        List<Element> UdxNodes = udxNode.elements();
                        if (UdxNodes.size() > 0) {
                            root.put("schema",Utils.getUdxSchema(relatedDatasets,root.getString("text")));
                            root.put("nodes", new JSONArray());
                            convertData(UdxNodes, root);
                        }
                        dataset.add(root);
                    }
                    DatasetItemArray.add(dataset);
                }
                mdlObj.put("DataItems", DatasetItemArray);
            }
            //相关数据结束

            //State开始
            Element States = Behavior.element("StateGroup").element("States");
            List<Element> StateList = States.elements();
            JSONArray states = new JSONArray();
            if (StateList.size() > 0) {
                for (Element State : StateList) {
                    JSONObject stateObj = new JSONObject();
                    stateObj.put("name", State.attributeValue("name"));
                    stateObj.put("type", State.attributeValue("type"));
                    stateObj.put("desc", State.attributeValue("description"));
                    stateObj.put("Id", State.attributeValue("id"));
                    List<Element> EventList = State.elements();
                    JSONArray event = new JSONArray();
                    for (Element Event : EventList) {
                        JSONObject eventObj = new JSONObject();
                        eventObj.put("eventId", UUID.randomUUID().toString());
                        eventObj.put("eventName", Event.attributeValue("name"));
                        eventObj.put("eventType", Event.attributeValue("type"));
                        eventObj.put("eventDesc", Event.attributeValue("description"));
                        Element Parameter = null;
                        if (Event.attributeValue("type").equals("response")) {
                            Parameter = Event.element("ResponseParameter");
                            if (Event.attribute("optional") != null) {
                                if (Event.attributeValue("optional").equalsIgnoreCase("True")) {
                                    if (Event.element("ControlParameter") != null) {
                                        Parameter = Event.element("ControlParameter");
                                    }
                                    eventObj.put("optional", true);
                                } else {
                                    eventObj.put("optional", false);
                                }
                            }
                        } else {
                            Parameter = Event.element("DispatchParameter");
                            if (Event.attribute("optional") != null) {
                                if (Event.attributeValue("optional").equalsIgnoreCase("True")) {
                                    if (Event.element("ControlParameter") != null) {
                                        Parameter = Event.element("ControlParameter");
                                    }
                                    eventObj.put("optional", true);
                                } else {
                                    eventObj.put("optional", false);
                                }
                            }
                            if(Event.attribute("multiple") != null){//判断是否可以多输出
                                if(Event.attributeValue("multiple").equalsIgnoreCase("True")){
                                    eventObj.put("multiple", true);

                                }else{
                                    eventObj.put("multiple", false);
                                }
                            }
                        }

                        for (int i = 0; i < mdlObj.getJSONArray("DataItems").size(); i++) {
                            JSONArray currentDataSet = mdlObj.getJSONArray("DataItems").getJSONArray(i);
                            JSONObject rootData = currentDataSet.getJSONObject(0);
                            if (Parameter == null) {
                                break;
                            }
                            if (rootData.getString("text").equals(Parameter.attributeValue("datasetReference"))) {
                                eventObj.put("data", currentDataSet);
                            }
                        }
                        event.add(eventObj);
                    }
                    stateObj.put("event", event);
                    states.add(stateObj);
                }
            }
            mdlObj.put("states", states);
            //State结束
        } catch (DocumentException e) {
            // System.out.println(mdl);
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();
        result.put("mdl", mdlObj);
        return result;
    }


    public static String getUdxSchema(String text,String name){
        int findIndex=text.indexOf(name);
        int startIndex=text.indexOf(">",findIndex+name.length())+1;
        int endIndex=text.indexOf("</DatasetItem>",startIndex);
        return text.substring(startIndex,endIndex);
    }

    public static void convertData(List<Element> udxNodes, JSONObject root) {
        if (udxNodes.size() > 0) {
            for (Element udxNode : udxNodes) {
                JSONObject node = new JSONObject();
                node.put("text", udxNode.attributeValue("name"));
                String dataType=udxNode.attributeValue("type");
                String dataType_result="";
//                switch (dataType) {
//                    case "DTKT_INT | DTKT_LIST":
//                        dataType_result = "int_array";
//                        break;
//                    default:
//                        String[] strings=dataType.split("_");
//                        for(int i=0;i<strings.length;i++){
//                            if(!strings[i].equals("DTKT")){
//                                dataType_result+=strings[i];
//                                if(i!=strings.length-1){
//                                    dataType_result+="_";
//                                }
//                            }
//                        }
//                }
                String[] dataTypes=dataType.split("\\|");
                if(dataTypes.length>1){
                    for(int j=0;j<dataTypes.length;j++){
                        String[] strings=dataTypes[j].trim().split("_");
                        if(strings[1].equals("LIST")){
                            strings[1]="ARRAY";
                        }
                        dataType_result+=strings[1];
                        if(j!=dataTypes.length-1){
                            dataType_result+="_";
                        }
                    }
                }
                else{
                    String[] strings=dataType.split("_");
                    dataType_result=strings[1];
                }

                node.put("dataType", dataType_result);
                node.put("desc", udxNode.attributeValue("description"));
                if (udxNode.attributeValue("type").equals("external")) {
                    node.put("externalId", udxNode.attributeValue("externalId").toLowerCase());
                }
                List<Element> nodeChildren = udxNode.elements();
                if (nodeChildren.size() > 0) {
                    node.put("nodes", new JSONArray());
                    convertData(nodeChildren, node);
                }
                JSONArray nodes = root.getJSONArray("nodes");
                nodes.add(node);
            }
        } else {
            return;
        }
    }


    public static GeoInfoMeta getGeoInfoMeta(String host) throws Exception {
        IP2Location location = new IP2Location();
        location.IPDatabasePath = ClassUtils.getDefaultClassLoader().getResource("static/").getPath()+"IP2LOCATION-LITE-DB5.BIN";
        GeoInfoMeta geoInfoMeta = new GeoInfoMeta();
        try {
            IPResult rec = location.IPQuery(host);
            if ("OK".equals(rec.getStatus())) {
                if(!rec.getCountryLong().equals("-")) {
                    geoInfoMeta.setCity(rec.getCity());
                    geoInfoMeta.setRegion(rec.getRegion());
                    geoInfoMeta.setCountryCode(rec.getCountryShort());
                    String countryName = rec.getCountryLong();

                    switch (countryName.trim()){
                        case "United States of America":
                            countryName="United States";
                            break;
                        case "United Kingdom of Great Britain and Northern Ireland":
                            countryName="United Kingdom";
                            break;
                        case "Hong Kong":
                            countryName="Hong Kong, China";
                            break;
                        case "Macao":
                            countryName="Macao, China";
                            break;
                        case "Taiwan (Province of China)":
                            countryName="Taiwan, China";
                            break;
                        case "Congo (Democratic Republic of the)":
                            countryName="Dem. Rep. Congo";
                            break;
                        case "Russian Federation":
                            countryName="Russia";
                            break;
                        case "Korea (Republic of)":
                            countryName="Korea";
                            break;
                        case "Iran (Islamic Republic of)":
                            countryName="Iran";
                            break;
                        case "Bolivia (Plurinational State of)":
                            countryName="Bolivia";
                            break;
                        case "Micronesia (Federated States of)":
                            countryName="Micronesia";
                            break;
                        case "Venezuela (Bolivarian Republic of)":
                            countryName="Venezuela";
                            break;
                        case "Moldova (Republic of)":
                            countryName="Moldova";
                            break;
                        case "Dominican Republic":
                            countryName="Dominican Rep.";
                            break;
                        case "Palestine, State of":
                            countryName="Palestine, State of";
                            break;
                        case "Tanzania, United Republic of":
                            countryName="Tanzania";
                            break;

                        default:
                            break;
                    }

                    geoInfoMeta.setCountryName(countryName);
                    geoInfoMeta.setLatitude(String.valueOf(rec.getLatitude()));
                    geoInfoMeta.setLongitude(String.valueOf(rec.getLongitude()));
                }else{
                    geoInfoMeta.setCity("Nanjing");
                    geoInfoMeta.setRegion("Jiangsu");
                    geoInfoMeta.setCountryCode("CN");
                    geoInfoMeta.setCountryName("China");
                    geoInfoMeta.setLatitude("32.0617");
                    geoInfoMeta.setLongitude("118.7778");

                }
            } else if ("EMPTY_IP_ADDRESS".equals(rec.getStatus())) {
                System.out.println("IP address cannot be blank.");
            } else if ("INVALID_IP_ADDRESS".equals(rec.getStatus())) {
                System.out.println("Invalid IP address.");
            } else if ("MISSING_FILE".equals(rec.getStatus())) {
                System.out.println("Invalid database path.");
            } else if ("IPV6_NOT_SUPPORTED".equals(rec.getStatus())) {
                System.out.println("This BIN does not contain IPv6 data.");
            } else {
                System.out.println("Unknown error." + rec.getStatus());
            }
//            if (rec.getDelay() == true) {
//                System.out.println("The last query was delayed for 5 seconds because this is an evaluation copy.");
//            }
//            System.out.println("Java Component: " + rec.getVersion());
        }catch (Exception e){
            System.out.println(e.fillInStackTrace());
            geoInfoMeta.setCity("Nanjing");
            geoInfoMeta.setRegion("Jiangsu");
            geoInfoMeta.setCountryCode("CN");
            geoInfoMeta.setCountryName("China");
            geoInfoMeta.setLatitude("32.0617");
            geoInfoMeta.setLongitude("118.7778");
        }

        return geoInfoMeta;
    }


    public static String saveBase64Image(String content,String id,String resourcePath,String htmlLoadPath){
        if(content==null){
            return null;
        }
        int startIndex = 0, endIndex = 0, index = 0;
        while (content!=null&&content.indexOf("src=\"data:im", startIndex) != -1) {
            int Start = content.indexOf("src=\"data:im", startIndex) + 5;
            int typeStart = content.indexOf("/", Start) + 1;
            int typeEnd = content.indexOf(";", typeStart);
            String type = content.substring(typeStart, typeEnd);
            startIndex = typeEnd + 8;
            endIndex = content.indexOf("\"", startIndex);
            String imgStr = content.substring(startIndex, endIndex);

            String imageName = "/detailImage/" + id + "/" + id + "_" + (index++) + "." + type;
            Utils.base64StrToImage(imgStr, resourcePath + imageName);

            content = content.substring(0, Start) + htmlLoadPath + imageName + content.substring(endIndex, content.length());
        }
        return content;
    }

    /**
     * 获取全部字段为null的属性名
     * 用于BeanUtils.copyProperties()拷贝属性时，忽略空值
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
