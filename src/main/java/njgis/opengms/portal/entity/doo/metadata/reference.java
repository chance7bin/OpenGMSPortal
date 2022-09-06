package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class reference {
  String title;
  String author;
  String agency;
  String press;
  Integer reel;
  Integer issue;
  String page;
  Date publishDate;
  String url;
}
