package njgis.opengms.portal.utils;

import njgis.opengms.portal.entity.doo.base.PortalIdPlus;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.PortalIdPlus;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
                    // TODO Auto-generated catch block
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

}
