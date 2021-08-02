import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ICD10CodesManipulator {

    private ArrayList<ICDNode> chapterList;
    private HashMap<String, ICDNode> codeToNode;
    private ArrayList<String> allCodesList;
    private ArrayList<String> allCodesListNoDots;
    private HashMap<String, Integer> codeToIndexMap;


    public static void main(String[] args) throws IOException {
        ICD10CodesManipulator icd = new ICD10CodesManipulator();
    }

    public ICD10CodesManipulator() throws IOException {
        Document document;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File( "data\\icd_10_v2019.xml" ));
        } catch (Exception e){
            throw new IOException(e);
        }

        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        Node childNode = root.getFirstChild();

        ArrayList<Element> chapterElementsList = new ArrayList<Element>();
        while( childNode!=null ){
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                chapterElementsList.add(childElement);
            }
            childNode = childNode.getNextSibling();
        }

        this.chapterList=new ArrayList<>();
        this.codeToNode=new HashMap<>();
        for(Element chapter: chapterElementsList){
            chapterList.add(new ICDNode(chapter));
        }
        this.allCodesList= new ArrayList<>();
        this.allCodesListNoDots=new ArrayList<>();
        this.codeToIndexMap=new HashMap<>();

        for(ICDNode chapter: chapterList){
            printICDNode(chapter);
        }

    }

    private void printICDNode(ICDNode node){
        System.out.println(node.getName()+" - "+node.getDescription());
        for(ICDNode child: node.getChildren()){
            printICDNode(child);
        }
    }

    private class ICDNode{
        private String name;
        private String description;
        private String type;
        private ICDNode parent;
        private ArrayList<ICDNode> children;

        private ICDNode(Element element){
            this.type = element.getAttribute("type");
            this.children = new ArrayList<>();
            this.parent=null;

            //reads all the data from the XML file and creates children nodes
            String tag;
            Node child = element.getFirstChild();
            while(child!=null){
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) child;
                    tag = childElement.getTagName();
                    if(tag.equals("item")){//adds new child
                        this.children.add(new ICDNode(childElement));
                    }else if(tag.equals("name")){//sets the name
                        this.name=childElement.getTextContent();
                    }else if(tag.equals("description")){//sets the description
                        this.description=childElement.getTextContent();
                    }else{
                        System.out.println("Found unknown tag \""+tag+"\", ignoring it...");
                    }
                }
                child = child.getNextSibling();
            }
            //updates the children ICDNodes by setting their parent to this
            for(ICDNode icdChild:this.children){
                icdChild.parent=this;
            }
            //adds node to codeToNode HashMap
            codeToNode.put(this.name,this);
        }

        private String getName(){
            return this.name;
        }

        private String getDescription(){
            return this.description;
        }

        private String getType(){
            return this.type;
        }

        private ICDNode getParent(){
            return this.parent;
        }

        private ArrayList<ICDNode> getChildren(){
            return this.children;
        }
    }
}
