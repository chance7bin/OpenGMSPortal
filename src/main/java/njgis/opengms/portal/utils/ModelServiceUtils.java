package njgis.opengms.portal.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

/**
 * @Description 与模型服务相关的工具类
 * @Author kx
 * @Date 21/11/11
 * @Version 1.0.0
 */
public class ModelServiceUtils {

    /**
     * @Description 将mdl String 转换为json
     * @param mdl
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/11/11
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
                            root.put("schema",getUdxSchema(relatedDatasets,root.getString("text")));
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
            System.out.println(mdl);
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

    /**
     * @Description 处理mdl常见错误
     * @param mdlJson
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/11/12
     **/
    public static JSONObject checkMdlJson(JSONObject mdlJson){
        try{
            //处理mdl格式错误
            JSONObject modelClass=mdlJson.getJSONArray("ModelClass").getJSONObject(0);
            //JSONObject runtime=modelClass.getJSONArray("Runtime").getJSONObject(0);

            String type=modelClass.getString("type");
            if(type!=null){
                modelClass.put("style",type);
            }
            if(modelClass.getJSONArray("Runtime").getJSONObject(0).getJSONArray("SupportiveResources")==null){
                modelClass.getJSONArray("Runtime").getJSONObject(0).put("SupportiveResources","");
            }

            return modelClass;
        }catch (Exception e){
            return new JSONObject();
        }

    }

    /**
     * @Description todo
     * @param testDataDirectory
     * @param oid
     * @Return java.lang.String
     * @Author kx
     * @Date 21/11/12
     **/
    public static String extractTestData(String resourcePath, String testDataDirectory, String oid) throws IOException {

        File testDataFile = new File(testDataDirectory);
        File[] tempTestDataDir = testDataFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (tempTestDataDir.length == 0) {
            return "";
        } else {
            File defaultTest = tempTestDataDir[0];
            String configPath = defaultTest.getAbsolutePath() + File.separator + "config.xml";
            File configFile = new File(configPath);
            if (configFile.exists()) {
                //读取config.xml文件信息，检查同级目录下方是否有相对应的数据
                boolean status = judgeConfigInformation(configFile, defaultTest.getAbsolutePath());
                if (!status) {
                    return "";
                }
                //拷贝文件
                //String destPath = ComputableModelService.class.getClassLoader().getResource("").getPath() + "static/upload/computableModel/testify/" + id;
                String destPath = resourcePath + "/computableModel/testify/" + oid;
                FileUtils.copyDirectory(defaultTest.getAbsoluteFile(), new File(destPath));
                String returnPath = oid + File.separator + "config.xml";
                return returnPath;
            } else {
                return "";
            }
        }

    }

    /**
     * @Description 检查测试数据是否完整
     * @param configFile 测试数据配置文件
     * @param parentDirectory
     * @Return boolean
     * @Author kx
     * @Date 21/11/12
     **/
    private static boolean judgeConfigInformation(File configFile, String parentDirectory) {
        org.dom4j.Document result = null;
        SAXReader reader = new SAXReader();
        try {
            result = reader.read(configFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element rootElement = result.getRootElement();
        List<Element> items = rootElement.elements();
        if (items.size() > 0) {
            for (Element item : items) {
                String fileName = item.attributeValue("File");
                String filePath = parentDirectory + File.separator + fileName;
                File tempFile = new File(filePath);
                if (tempFile.exists()) {
                    continue;
                } else {
                    return false;
                }
            }
        } else {
            System.out.println("无测试案例数据");
            return false;
        }
        return true;
    }



}
