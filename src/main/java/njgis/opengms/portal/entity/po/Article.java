package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Article extends PortalId {
    // @Id
    // String id;
//    String oid;
    String title;
    List<String> authors;

//    int viewCount;
//    String contributor;
//    List<String> contributors = new ArrayList<>();
//    List<String> userAuthors = new ArrayList<>();
    String journal;
    String volume="";
    String pageRange="";
    String link;
    String date;
    String doi="";
    Date createDate;

    public Boolean isSame(Article article){

        if(this.title.equals(article.authors) &&
                this.authors == article.authors &&
                this.journal.equals(article.journal) &&
                this.volume.equals(article.volume) &&
                this.pageRange.equals(article.pageRange) &&
                this.link.equals(article.link) &&
                this.date.equals(article.date) &&
                this.doi.equals(article.doi)){
            return true;
        }else {
            return false;
        }

    }

}
