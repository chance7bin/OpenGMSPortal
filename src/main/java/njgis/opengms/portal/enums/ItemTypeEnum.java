package njgis.opengms.portal.enums;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Document
public enum ItemTypeEnum {

    DataItem(0,"data item"),

    ModelItem(1,"model item"),
    ConceptualModel(2,"conceptual model"),
    LogicalModel(3,"logical model"),
    ComputableModel(4,"computable model"),

    Concept(5,"concept"),
    SpatialReference(6,"spatial reference"),
    Template(7,"template"),
    Unit(8,"unit"),

    Theme(9,"theme"),

    DataHub(10,"data hub"),
    DataMethod(11,"data method"),

    Article(12,"article"),
    Project(13,"project"),
    Conference(14,"conference");

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
