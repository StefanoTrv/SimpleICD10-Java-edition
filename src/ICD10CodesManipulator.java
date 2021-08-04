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
        System.out.println(icd.getDescription("A15.1"));
        System.out.println(icd.getParent("A15.1"));
        System.out.println(icd.getChildren("A15"));
        System.out.println(icd.isLeaf("A15.1"));
        System.out.println(icd.isLeaf("A15"));
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

        //initializes the two lists of codes
        for(ICDNode chapter: this.chapterList){
            addTreeToList(chapter);
        }
    }

    private void addTreeToList(ICDNode node){
        String name = node.name;
        this.allCodesList.add(name);
        if(name.length()>4 && name.charAt(3)=='.'){
            allCodesListNoDots.add(name.substring(0,3)+name.substring(4));
        }else{
            allCodesListNoDots.add(name);
        }
        for(ICDNode child: node.getChildren()){
            addTreeToList(child);
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

    private String addDotToCode(String code){
        if(code.length()<4 || code.charAt(3)=='.'){
            return code;
        } else if(codeToNode.containsKey(code.substring(0,3)+"."+code.substring(3))){
            return code.substring(0,3)+"."+code.substring(3);
        } else {
            return code;
        }
    }

    public boolean isValidItem(String code){
        return codeToNode.containsKey(code) || code.length()>=4 && codeToNode.containsKey(code.substring(0,3)+"."+code.substring(3));
    }

    public boolean isChapter(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("chapter");
        }else{
            return false;
        }
    }

    public boolean isBlock(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("block");
        }else{
            return false;
        }
    }

    public boolean isCategory(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("category");
        }else{
            return false;
        }
    }

    public boolean isSubcategory(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("subcategory");
        }else{
            return false;
        }
    }

    public boolean isChapterOrBlock(String code){
        return isBlock(code)||isChapter(code);
    }

    public boolean isCategoryOrSubcategory(String code){
        return isCategory(code)||isSubcategory(code);
    }

    public String getDescription(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD 10 code.");
        }
        return codeToNode.get(addDotToCode(code)).getDescription();
    }

    public String getParent(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD 10 code.");
        }
        ICDNode parentNode = codeToNode.get(addDotToCode(code)).getParent();
        if(parentNode==null){
            return "";
        } else {
            return parentNode.name;
        }
    }

    public ArrayList<String> getChildren(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD 10 code.");
        }
        ArrayList<ICDNode> childrenNodes = codeToNode.get(addDotToCode(code)).getChildren();
        ArrayList<String> result = new ArrayList<>();
        for(ICDNode child: childrenNodes){
            result.add(child.getName());
        }
        return result;
    }

    public boolean isLeaf(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD 10 code.");
        }
        ArrayList<ICDNode> childrenNodes = codeToNode.get(addDotToCode(code)).getChildren();
        return childrenNodes.size()==0;
    }



}
