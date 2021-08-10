# SimpleICD10-Java-edition
 A simple Java library for ICD-10 codes

## Index
* [Release notes](#release-notes)
* [Introduction](#introduction)
* [What a code is and how it looks like](#what-a-code-is-and-how-it-looks-like)
* [Documentation](#documentation)
  * [ICD10CodesManipulator()](icd10codesmanipulator)
  * [boolean isValidItem(String code)](#boolean-isvaliditemstring-code)
  * [boolean isCategoryOrSubcategory(String code)](#boolean-iscategoryorsubcategorystring-code)
  * [boolean isChapterOrBlock(String code)](#boolean-ischapterorblockstring-code)
  * [boolean isChapter(String code)](#boolean-ischapterstring-code)
  * [boolean isBlock(String code)](#boolean-isblockstring-code)
  * [boolean isCategory(String code)](#boolean-iscategorystring-code)
  * [boolean isSubcategory(String code)](#boolean-issubcategorystring-code)
  * [String getDescription(String code)](#string-getdescriptionstring-code)
  * [String getParent(String code)](#string-getparentstring-code)
  * [ArrayList&lt;String&gt; getChildren(String code)](#arrayliststring-getchildrenstring-code)
  * [ArrayList&lt;String&gt; getAncestors(String code)](#arrayliststring-getancestorsstring-code)
  * [ArrayList&lt;String&gt; getDescendants(String code)](#arrayliststring-getdescendantsstring-code)
  * [boolean isAncestor(String a, String b)](#boolean-isancestorstring-a-string-b)
  * [boolean isDescendant(String a, String b)](#boolean-isdescendantstring-a-string-b)
  * [String getNearestCommonAncestor(String a, String b)](#string-getnearestcommonancestorstring-a-string-b)
  * [boolean isLeaf(String code)](#boolean-isleafstring-code)
  * [ArrayList&lt;String&gt; getAllCodes(boolean withDots)](#arrayliststring-getallcodesboolean-withdots)
  * [ArrayList&lt;String&gt; getAllCodes()](#arrayliststring-getallcodes)
  * [int getIndex(String code)](#int-getindexstring-code)
  * [String removeDot(String code)](#string-removedotstring-code)
  * [String addDot(String code)](#string-adddotstring-code)
* [Conclusion](#conclusion)

## Release notes
```empty```

## Introduction
The scope of this library is to provide a simple instrument for dealing with ICD-10 codes in your Java projects. It provides ways to check whether a code exists, to find its ancestors and descendants, to see its description and much more.  
The codes and their descriptions were taken from [this page](https://icd.who.int/browse10/2019/en#) in the WHO's website and are referred to the **2019 version of ICD-10**.  
If you are looking for a Python version of this project, you can check the [simple_icd_10 library](https://github.com/StefanoTrv/simple_icd_10).

In the ["data" folder](https://github.com/StefanoTrv/SimpleICD10-Java-edition/tree/master/data) you can find an XML file that contains the entire ICD-10 classification.

All the classes in this library are contained in the package "`it.trv.simpleicd10`".

## What a code is and how it looks like
We need to start by clarifying what a code is for us. The [ICD-10 instruction manual](https://icd.who.int/browse10/Content/statichtml/ICD10Volume2_en_2019.pdf) makes a distinction between **chapters**, **block of categories**, **three-character categories** and **four-character subcategories** (which from now on we'll refer to as chapters, blocks, categories and subcategories), with a few additional five-character subcategories: we will consider all these items as codes.

Generally speaking, the codes of subcategories can be written in two different ways: with a dot (for example "I13.1") and without the dot (for example "I131"). The methods in this library can receive as input codes in both these formats. The codes returned by the functions will always be in the format with the dot. You can easily change the format of a code by using the [`remove_dot`](#remove_dotcode) and [`add_dot`](#add_dotcode) functions.

## Documentation
This library is comprised of a single class, ICD10CodesManipulator. When an object of this class is instantiated, it loads all the relevant data and creates the appropriate data structures to work effectively with it. It's through these objects that the functionalities of this library can be accessed.
```Java
ICD10CodesManipulator icd = new ICD10CodesManipulator();
```
### ICD10CodesManipulator()
It creates a new ICD10CodesManipulator by loading and preparing the data of the ICD-10 classification.

Throws:  
&ensp;&ensp;`IOException` if an error occurs while reading the ICD-10 data.

```Java
ICD10CodesManipulator icd = new ICD10CodesManipulator();
```
### boolean isValidItem(String code)
This method takes a string as input and returns true if the string is a valid chapter, block, category or subcategory in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 code, otherwise false
```Java
icd.isValidItem("cat")
//false
icd.isValidItem("B99")
//true
```
### boolean isCategoryOrSubcategory(String code)
This method takes a string as input and returns true if the string is a valid category or subcategory in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 category or subcategory, otherwise false
```Java
icd.isCategoryOrSubcategory("A00-B99")
//false
icd.isCategoryOrSubcategory("B99")
//true
```
### boolean isChapterOrBlock(String code)
This method takes a string as input and returns true if the string is a valid chapter or block in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 chapter or block, otherwise false
```Java
icd.isChapterOrBlock("A00-B99")
//true
icd.isChapterOrBlock("B99")
//false
```
### boolean isChapter(String code)
This method takes a string as input and returns true if the string is a valid chapter in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 chapter, otherwise false
```Java
icd.isChapter("XII")
//true
icd.isChapter("B99")
//false
```
### boolean isBlock(String code)
This method takes a string as input and returns true if the string is a valid block in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 block, otherwise false
```Java
icd.isBlock("A00-B99")
//true
icd.isBlock("B99")
//false
```
### boolean isCategory(String code)
This method takes a string as input and returns true if the string is a valid category in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 category, otherwise false
```Java
icd.isCategory("B99")
//true
icd.isCategory("XIV")
//false
```
### boolean isSubcategory(String code)
This method takes a string as input and returns true if the string is a valid subcategory in ICD-10, false otherwise.

Parameters:  
&ensp;&ensp;`code` is the String that must be checked  
Returns:  
&ensp;&ensp;true if `code` is a valid ICD-10 subcategory, otherwise false
```Java
icd.isSubcategory("B95.1")
//true
icd.isSubcategory("B99")
//false
```
### String getDescription(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns a string with its short description, otherwise it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;the description of the code `code`  
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getDescription("XII")
//"Diseases of the skin and subcutaneous tissue"
icd.getDescription("F00")
//"Dementia in Alzheimer disease"
```
### String getParent(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns a string containing its parent, otherwise it throws an IllegalArgumentException. If the code doesn't have a parent (that is, if it's a chapter), it returns an empty string.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;the parent of the code `code`  
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getParent("C00")
//"C00-C14"
icd.getParent ("XII")
//""
```
### ArrayList&lt;String&gt; getChildren(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns an ArrayList&lt;String&gt; containing its children, otherwise it throws an IllegalArgumentException. If the code doesn't have children, it returns an empty ArrayList&lt;String&gt;.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;an ArrayList&lt;String&gt; containing the children of the code `code`  
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getChildren("XII")
//[L00-L08, L10-L14, L20-L30, L40-L45, L50-L54, L55-L59, L60-L75, L80-L99]
icd.getChildren("H60.1")
//[]
```
### ArrayList&lt;String&gt; getAncestors(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns an ArrayList&lt;String&gt; containing all its ancestors in the ICD-10 classification, otherwise it throws an IllegalArgumentException. The results are ordered from its parent to its most distant ancestor.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;an ArrayList&lt;String&gt; containing the ancestors of the code `code`  
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getAncestors("H60.1")
//[H60, H60-H62, VIII]
```
### ArrayList&lt;String&gt; getDescendants(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns an ArrayList&lt;String&gt; containing all its descendants in the ICD-10 classification, otherwise it throws an IllegalArgumentException. The returned codes are ordered as in a pre-order depth-first traversal of the tree containing the ICD-10 classification.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;an ArrayList&lt;String&gt; containing the descendants of the code `code`  
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getDescendants("C00")
//[C00.0, C00.1, C00.2, C00.3, C00.4, C00.5, C00.6, C00.8, C00.9]
```
### boolean isAncestor(String a, String b)
This method takes two strings as input. If both strings are valid ICD-10 codes, it returns true if the first code is an ancestor of the second code. If at least one of the strings is not a valid ICD-10 code, it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`a` is an ICD-10 code  
&ensp;&ensp;`b` is an ICD-10 code  
Returns:  
&ensp;&ensp;true if a is one of the ancestors of b, false otherwise
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `a` or `b` are not a valid ICD-10 code
```Java
icd.isAncestor("XVIII","R01.0")
//true
icd.isAncestor("K00-K14","M31")
//false
```
### boolean isDescendant(String a, String b)
This method takes two strings as input. If both strings are valid ICD-10 codes, it returns true if the first code is a descendant of the second code. If at least one of the strings is not a valid ICD-10 code, it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`a` is an ICD-10 code  
&ensp;&ensp;`b` is an ICD-10 code  
Returns:  
&ensp;&ensp;true if a is one of the descendants of b, false otherwise
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `a` or `b` are not a valid ICD-10 code
```Java
icd.isDescendant("R01.0","XVIII")
//true
icd.isDescendant("M31","K00-K14")
//false
```
### String getNearestCommonAncestor(String a, String b)
This method takes two strings as input. If both strings are valid ICD-10 codes, it returns their nearest common ancestor if it exists, an empty string if it doesn't exist. If at least one of the strings is not a valid ICD-10 code, it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`a` is an ICD-10 code  
&ensp;&ensp;`b` is an ICD-10 code  
Returns:  
&ensp;&ensp;the nearest common ancestor of a and b if it exists, an empty string otherwise
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `a` or `b` are not a valid ICD-10 code
```Java
icd.getNearestCommonAncestor("H28.0","H25.1")
//"H25-H28"
icd.getNearestCommonAncestor("K35","E21.0")
//""
```
### boolean isLeaf(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns true if the code is a leaf in the ICD-10 classification (that is, it has no descendants), false otherwise. If the string is not a valid ICD-10 code it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;true if `code` is a leaf in the ICD-10 classification, false otherwise
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.isLeaf("H28")
//false
icd.isLeaf("H28.0")
//true
//""
```
### ArrayList&lt;String&gt; getAllCodes(boolean withDots)
This method takes a boolean for input and returns an ArrayList&lt;String&gt; containing all the items in the ICD-10 classification. If 'withDots' is true, the subcategories in the ArrayList&lt;String&gt; will have a dot in them, if it's false the subcategories won't have a dot in them.

Parameters:  
&ensp;&ensp;withDots is a boolean that controls whether the codes in the list that is returned are in the format with or without the dot.  
Returns:  
&ensp;&ensp;the list of all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit
```Java
icd.getAllCodes(true)
//[I, A00-A09, A00, A00.0, A00.1, A00.9, A01, A01.0, ...
icd.get_all_codes(false)
//[I, A00-A09, A00, A000, A001, A009, A01, A010, ...
```
### ArrayList&lt;String&gt; getAllCodes()
This method takes a boolean for input and returns the list of all the items in the ICD-10 classification. The codes in the ArrayList&lt;String&gt; are in the format with the dot.

Returns:  
&ensp;&ensp;the list of all the codes in the ICD-10 classification, ordered as in a depth-first pre-order visit
```Java
icd.getAllCodes()
//[I, A00-A09, A00, A00.0, A00.1, A00.9, A01, A01.0, ...
```
### int getIndex(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns its index in the list returned by `getAllCodes`, otherwise it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;the index of the code `code` in the list returned by getAllCodes
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.getIndex("P00")
//7159
icd.getAllCodes(True).get(7159)
//"P00"
```
### String removeDot(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns the same code in the notation without the dot, otherwise it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;the same code in the format without the dot
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.removeDot("H60.1")
//"H601"
icd.removeDot("H601")
//"H601"
icd.removeDot("G10-G14")
//"G10-G14"
```
### String addDot(String code)
This method takes a string as input. If the string is a valid ICD-10 code, it returns the same code in the notation with the dot, otherwise it throws an IllegalArgumentException.

Parameters:  
&ensp;&ensp;`code` is an ICD-10 code  
Returns:  
&ensp;&ensp;the same code in the format with the dot
Throws:  
&ensp;&ensp;`IllegalArgumentException` if `code` is not a valid ICD-10 code
```Java
icd.addDot("H60.1")
//"H60.1"
icd.addDot("H601")
//"H60.1"
icd.addDot("G10-G14")
//"G10-G14"
```
## Conclusion
This is everything you needed to know before using the SimpleICD10 library - please contact me if you feel I missed something or there's some passage that you think should be explained better or more. Also contact me if you find any errors in the library or in the documentation.  

If you are feeling generous, consider making a donation using one of the methods listed at the end of this document.

*Stefano Travasci*

---

Paypal: [![Donate](https://www.paypalobjects.com/en_US/IT/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/donate?hosted_button_id=9HMMFAZE248VN)

Curecoin: BJdY5wCjN79bnq7jYLSowR7THzdm1CFDT9

Bitcoin: bc1qt5w3x89x546z5kwthu9hcyh5tcxr6rdf6t6uvr

&lt;sub&gt;*let me know if your favorite donation method is not in this list*&lt;/sub&gt;
