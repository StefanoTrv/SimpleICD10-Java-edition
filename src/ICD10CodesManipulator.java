import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ICD10CodesManipulator {

    private ArrayList<ICDNode> chapterList;
    private HashMap<String, ICDNode> codeToNode;
    private ArrayList<String> allCodesList;
    private ArrayList<String> allCodesListNoDots;
    private HashMap<String, Integer> codeToIndexDictionary;


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File( "data\\icd_10_v2019.xml" ));

        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
        System.out.println("------");

        Node ch = root.getFirstChild();

        Node childNode = root.getFirstChild();

        ArrayList<Element> chapterList = new ArrayList<Element>();
        while( childNode.getNextSibling()!=null ){
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                System.out.println(childElement.getAttribute("type"));
                chapterList.add(childElement);
            }
            childNode = childNode.getNextSibling();
        }

        System.out.println("\n\n"+chapterList.get(0).getTagName()+"\n\n");
        Node ch1child = chapterList.get(0).getFirstChild();


        while( ch1child.getNextSibling()!=null ){
            if (ch1child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) ch1child;
                System.out.println(childElement.getTagName()+" - "+childElement.getAttribute("type"));
            }
            ch1child = ch1child.getNextSibling();
        }



    }

    private class ICDNode{
        private String name;
        private String description;
        private String type;
        private ICDNode parent;
        private ArrayList<ICDNode> children;

        public ICDNode(String name, String description, String type, ICDNode parent, ArrayList<ICDNode> children){
            this.name=name;
            this.description=description;
            this.type=type;
            this.parent=parent;
            this.children=(ArrayList<ICDNode>) children.clone();
        }

        public String getName(){
            return this.name;
        }

        public String getDescription(){
            return this.description;
        }

        public String getType(){
            return this.type;
        }

        public ICDNode getParent(){
            return this.parent;
        }

        public ArrayList<ICDNode> getChildren(){
            return (ArrayList<ICDNode>) this.children.clone();
        }
    }
}
