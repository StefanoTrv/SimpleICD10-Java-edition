package it.trvi.simpleicd10;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An object that contains the whole ICD-10 classification and that can thus provide information about the whole classification or about single codes.
 */
public class ICD10CodesManipulator {

    private ArrayList<ICDNode> chapterList;
    private HashMap<String, ICDNode> codeToNode;
    private ArrayList<String> allCodesList;
    private ArrayList<String> allCodesListNoDots;
    private HashMap<String, Integer> codeToIndexMap;

    /**
     * The constructor that reads the data from the xml file to load all the data relative to the ICD-10 classification.
     */
    public ICD10CodesManipulator(){
        Document document;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(getClass().getResourceAsStream("icd_10_v2019.xml"));
        } catch (Exception e){
            throw new RuntimeException(e);
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

    /**
     * Private methods that takes an ICDNode and adds it and its children (their String representation) to allCodesList and allCodesListNoDots, with a depth-first pre-order visit.
     */
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

    /**
     * Private class that represents a single node of the tree of ICD-10 codes.
     */
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

    /**
     * Private method used to add the dot to a code without having to check whether the code is valid.
     */
    private String addDotToCode(String code){
        if(code.length()<4 || code.charAt(3)=='.'){
            return code;
        } else if(codeToNode.containsKey(code.substring(0,3)+"."+code.substring(3))){
            return code.substring(0,3)+"."+code.substring(3);
        } else {
            return code;
        }
    }

    /**
     * It checks whether a String is a valid chapter, block, category or subcategory in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 code, otherwise false
     */
    public boolean isValidItem(String code){
        return codeToNode.containsKey(code) || code.length()>=4 && codeToNode.containsKey(code.substring(0,3)+"."+code.substring(3));
    }

    /**
     * It checks whether a String is a valid chapter in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 chapter, otherwise false
     */
    public boolean isChapter(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("chapter");
        }else{
            return false;
        }
    }

    /**
     * It checks whether a String is a valid block in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 block, otherwise false
     */
    public boolean isBlock(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("block");
        }else{
            return false;
        }
    }

    /**
     * It checks whether a String is a valid category in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 category, otherwise false
     */
    public boolean isCategory(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("category");
        }else{
            return false;
        }
    }

    /**
     * It checks whether a String is a valid subcategory in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 subcategory, otherwise false
     */
    public boolean isSubcategory(String code){
        code = addDotToCode(code);
        if (codeToNode.containsKey(code)){
            return codeToNode.get(code).getType().equals("subcategory");
        }else{
            return false;
        }
    }

    /**
     * It checks whether a String is a valid chapter or block in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 chapter or block, otherwise false
     */
    public boolean isChapterOrBlock(String code){
        return isBlock(code)||isChapter(code);
    }

    /**
     * It checks whether a String is a valid category or subcategory in ICD-10.
     *
     * @param code is the String that must be checked
     * @return true if code is a valid ICD-10 category or subcategory, otherwise false
     */
    public boolean isCategoryOrSubcategory(String code){
        return isCategory(code)||isSubcategory(code);
    }

    /**
     * Given a String that contains an ICD-10 code, it returns the description of said code.
     *
     * @param code is the ICD-10 code
     * @return the description of code
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public String getDescription(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        return codeToNode.get(addDotToCode(code)).getDescription();
    }

    /**
     * Given a String that contains an ICD-10 code, it returns its parent in the ICD-10 classification (or an empty string if it doesn't have a parent).
     *
     * @param code is the ICD-10 code
     * @return a String containing the parent of code (an empty string if code has no parent)
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public String getParent(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        ICDNode parentNode = codeToNode.get(addDotToCode(code)).getParent();
        if(parentNode==null){
            return "";
        } else {
            return parentNode.name;
        }
    }

    /**
     * Given a String that contains an ICD-10 code, it returns the list of the children of said code in the ICD-10 classification.
     *
     * @param code is the ICD-10 code
     * @return an ArrayList&lt;String&gt; containing all the children of code
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public ArrayList<String> getChildren(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        ArrayList<ICDNode> childrenNodes = codeToNode.get(addDotToCode(code)).getChildren();
        ArrayList<String> result = new ArrayList<>();
        for(ICDNode child: childrenNodes){
            result.add(child.getName());
        }
        return result;
    }

    /**
     * Given a String that contains an ICD-10 code, it checks whether that code is a leaf in the ICD-10 classification.
     *
     * @param code is the ICD-10 code
     * @return true if code is a leaf in the ICD-10 classification (that is, if it has no children), false otherwise
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public boolean isLeaf(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        ArrayList<ICDNode> childrenNodes = codeToNode.get(addDotToCode(code)).getChildren();
        return childrenNodes.size()==0;
    }

    /**
     * Given a String that contains an ICD-10 code, it returns the list of the ancestors of said code in the ICD-10 classification.
     *
     * @param code is the ICD-10 code
     * @return an ArrayList&lt;String&gt; containing all the ancestor of code, ordered from the closer to the furthest
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public ArrayList<String> getAncestors(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        ICDNode node = codeToNode.get(addDotToCode(code));
        ArrayList<String> result = new ArrayList<>();
        while (node.getParent()!=null){
            result.add(node.getParent().getName());
            node = node.getParent();
        }
        return result;
    }

    /**
     * Given a String that contains an ICD-10 code, it returns the list of the descendants of said code in the ICD-10 classification.
     *
     * @param code is the ICD-10 code
     * @return an ArrayList&lt;String&gt; containing all the descendants of code, ordered as in a depth-first pre-order visit
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public ArrayList<String> getDescendants(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        ICDNode node = codeToNode.get(addDotToCode(code));
        ArrayList<String> result = new ArrayList<>();
        addChildrenToList(node,result);
        return result;
    }

    /**
     * Private method that adds an ICDNode and its children (their String representations) to a list.
     */
    private void addChildrenToList(ICDNode node, ArrayList<String> list){
        for(ICDNode child: node.getChildren()){
            list.add(child.name);
            addChildrenToList(child,list);
        }
    }

    /**
     * It checks whether a code (a) is one of the ancestors of another code (b). A code is never an ancestor of itself.
     *
     * @param a is the code that may or may not be an ancestor of b
     * @param b is the code that whose ancestors could include a
     * @return true if a is one of the ancestors of b, false otherwise
     * @throws IllegalArgumentException if a or b are not a valid ICD-10 code
     */
    public boolean isAncestor(String a, String b) throws IllegalArgumentException{
        if(!isValidItem(a)){
            throw new IllegalArgumentException("\""+a+"\" is not a valid ICD-10 code.");
        }
        ICDNode node = codeToNode.get(addDotToCode(a));
        return getAncestors(b).contains(a) && !a.equals(b);
    }

    /**
     * It checks whether a code (a) is one of the descendants of another code (b). A code is never a descendant of itself.
     *
     * @param a is the code that may or may not be a descendant of b
     * @param b is the code that whose descendants could include a
     * @return true if a is one of the descendants of b, false otherwise
     * @throws IllegalArgumentException if a or b are not a valid ICD-10 code
     */
    public boolean isDescendant(String a, String b) throws IllegalArgumentException{
        return isAncestor(b,a);
    }

    /**
     * Given two ICD-10 codes a and b, it returns their nearest common ancestor in the ICD-10 classification (or an empty string if they don't have a nearest common ancestor).
     *
     * @param a is an ICD-10 code
     * @param b is an ICD-10 code
     * @return the nearest common ancestor of a and b if it exists, an empty string otherwise
     * @throws IllegalArgumentException if a or b are not a valid ICD-10 code
     */
    public String getNearestCommonAncestor(String a, String b) throws IllegalArgumentException{
        ArrayList<String> ancestorsA = getAncestors(a);
        ancestorsA.add(0,addDotToCode(a));
        ArrayList<String> ancestorsB = getAncestors(b);
        ancestorsB.add(0,addDotToCode(b));
        if(ancestorsB.size()>ancestorsA.size()){
            ArrayList<String> temp = ancestorsA;
            ancestorsA = ancestorsB;
            ancestorsB = temp;
        }
        for(String ancestor: ancestorsA){
            if (ancestorsB.contains(ancestor)){
                return ancestor;
            }
        }
        return "";
    }

    /**
     * It returns an ArrayList&lt;String&gt; that contains all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit.
     *
     * @param withDots is a boolean that controls whether the codes in the list that is returned are in the format with or without the dot.
     * @return the list of all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit, in the format with the dot if withDots is true, in the format without the dot otherwise
     */
    public ArrayList<String> getAllCodes(boolean withDots){
        if (withDots){
            return (ArrayList<String>) allCodesList.clone();
        } else {
            return (ArrayList<String>) allCodesListNoDots.clone();
        }
    }

    /**
     * It returns an ArrayList&lt;String&gt; that contains all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit.
     *
     * @return the list of all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit, in the format with the dot
         */
    public ArrayList<String> getAllCodes(){
        return getAllCodes(true);
    }

    /**
     * It returns the index of a particular code in the list returned by getAllCodes.
     *
     * @param code is the code whose index we want to find
     * @return the index of code in the list returned by getAllCodes
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public int getIndex(String code) throws IllegalArgumentException{
        if(!isValidItem(code)){
            throw new IllegalArgumentException("\""+code+"\" is not a valid ICD-10 code.");
        }
        code = addDotToCode(code);
        if (codeToIndexMap.containsKey(code)){
            return codeToIndexMap.get(code);
        } else {
            int i = allCodesList.indexOf(code);
            codeToIndexMap.put(code,i);
            return i;
        }
    }

    /**
     * Given an ICD-10 code, it returns the same code in the format without the dot.
     *
     * @param code is an ICD-10 code
     * @return the same code in the format without the dot
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public String removeDot(String code) throws IllegalArgumentException{
        return allCodesListNoDots.get(getIndex(code));
    }

    /**
     * Given an ICD-10 code, it returns the same code in the format with the dot.
     *
     * @param code is an ICD-10 code
     * @return the same code in the format with the dot
     * @throws IllegalArgumentException if code is not a valid ICD-10 code
     */
    public String addDot(String code) throws IllegalArgumentException{
        return allCodesList.get(getIndex(code));
    }

}
