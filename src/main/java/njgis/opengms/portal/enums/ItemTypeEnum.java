package njgis.opengms.portal.enums;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Document
public enum ItemTypeEnum {

    DataItem(0,"dataItem"),

    ModelItem(1,"modelItem"),
    ConceptualModel(2,"conceptualModel"),
    LogicalModel(3,"logicalModel"),
    ComputableModel(4,"computableModel"),

    Concept(5,"concept"),
    SpatialReference(6,"spatialReference"),
    Template(7,"template"),
    Unit(8,"unit"),

    Theme(9,"theme"),

    DataHub(10,"dataHub"),
    DataMethod(11,"dataMethod"),

    Article(12,"article"),
    Project(13,"project"),
    Conference(14,"conference"),

    //通知类型
    Version(15,"version");

    private int number;
    private String text;

    public String getText() {
        return text;
    }

    public static ItemTypeEnum getItemTypeByNum(int number){
        for(ItemTypeEnum itemTypeEnum:ItemTypeEnum.values()){
            if(itemTypeEnum.number==number){
                return itemTypeEnum;
            }
        }
        return null;
    }

    public static ItemTypeEnum getItemTypeByName(String name){
        for(ItemTypeEnum itemTypeEnum:ItemTypeEnum.values()){
            if(itemTypeEnum.name().toUpperCase().equals(name.toUpperCase())){
                return itemTypeEnum;
            }
        }
        return null;
    }
}
