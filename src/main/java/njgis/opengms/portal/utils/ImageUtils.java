package njgis.opengms.portal.utils;

import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @Description 图片相关的工具类
 * @Author kx
 * @Date 2021/7/6
 * @Version 1.0.0
 */
public class ImageUtils {

    /**
     * @Description 将带有Base64的字符串中的图片存储到本地
     * @param content 带有base64的字符串
     * @param id 条目Id
     * @param resourcePath 项目静态资源路径前缀
     * @param htmlLoadPath 网络访问路径前缀
     * @Return java.lang.String
     * @Author kx
     * @Date 2021/7/6
     **/
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
            base64StrToImage(imgStr, resourcePath + imageName);

            content = content.substring(0, Start) + htmlLoadPath + imageName + content.substring(endIndex, content.length());
        }
        return content;
    }

    /**
     * @Description base64字符串转化成图片
     * @param imgStr base64字符串
     * @param path 图片存储本地路径
     * @Return boolean
     * @Author kx
     * @Date 2021/7/6
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
}
