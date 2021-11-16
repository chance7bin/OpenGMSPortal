package njgis.opengms.portal.entity.doo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.enums.LanguageEnum;
import njgis.opengms.portal.utils.ImageUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Localization implements Comparable<Localization> {

    String localCode;
    String localName;
    String name;
    String description;

    @Override
    public int compareTo(Localization localization){
        return this.localName.compareTo(localization.localName);
    }

    public Localization(String code, String name, String description){
        this.localCode = code;
        this.localName = LanguageEnum.getLanguageByCode(code).getName();
        this.name = name;
        this.description = description;
    }

    /**
     * @Description 将description中的base64图片存储到本地
     * @param itemId 条目id
     * @Return void
     * @Author kx
     * @Date 21/10/13
     **/
    public void saveImage(String itemId, String resourcePath, String htmlLoadPath){
        this.description = ImageUtils.saveBase64Image(this.description,itemId,resourcePath,htmlLoadPath);
    }
}
