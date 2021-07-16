package njgis.opengms.portal.utils;

import njgis.opengms.portal.entity.doo.base.PortalIdPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 通用工具类
 * @Author kx
 * @Date 2021/7/6
 * @Version 1.0.0
 */
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

}
