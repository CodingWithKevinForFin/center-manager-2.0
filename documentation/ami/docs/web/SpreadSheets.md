## Overview
AMI supports user interactions and manipulation of spreadsheets. Users can create new spreadsheets or import existing ones for editing within the AMI environment. This section will discuss the various ways to work with spreadsheets in AMI.


**Contents:**

- Parsing XLSX & CSV files
- AMI Spreadsheet Classes
- Downloading Spreadsheets from AMI
- Example Workflows



## Parsing CSV and XLSX


**For simple use cases**, AMI allows for the parsing of both XLSX and CSV file formats. Users can import these files into the AMI environment for further manipulation and analysis.

To import an existing spreadsheet, first create an empty datamodel, then add the following AMIScript to the onProcess tab.

### Parsing CSV

!!! quote "parseCsv(String text, Boolean firstLineIsHeader)" 
    Parse the CSV file and returns a table.

    === "AmiScript"
    
        ```amiscript
        fileSystem fs = session.getFileSystem();
        string toParse = fs.readFile("/path/to/file/.../test.csv");
        table t = parseCsv(toParse,true);
        ```

    === "Output"
    
        ```amiscript
        +-----------------+
        |     Sheet1      |
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |MSFT   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ```

### Parsing XLSX

!!! quote "parseXlsx(Binary data, Boolean firstLineIsHeader, String sheetName)" 
    Parse the XLSX file and returns a table.

    === "AmiScript"
    
        ```amiscript
        fileSystem fs = session.getFileSystem();
        binary toParse = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        table t = parseXlsx(toParse,true,"Sheet1");
        ```

    === "Output"
    
        ```amiscript
        +-----------------+
        |     Sheet1      |
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |MSFT   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ```

## AMI Spreadsheet Classes

**For more complex use cases**, AMI provides a set of spreadsheet classes that facilitate various operations on spreadsheets. These classes offer methods for reading, writing, and modifying spreadsheet data. The classes include:

**SpreadSheet Builder:**

- Purpose: Used to generate a new spreadsheet within AMI web environment.
- Features: <mark style="background: #EDF0FF; color: #263054">Required for creating both flexsheets and worksheets.</mark> Provides foundational methods for building spreadsheet structures.
- Use Case: Initialize a new spreadsheet project, laying the groundwork for further customization and data manipulation.
- **Note**: Currently, combining both flexsheets and worksheets within a single spreadsheet builder is not supported.

**SpreadSheet Flexsheet:**

- Purpose: Designed to create and manage flexsheets in AMI.
- Features: Supports workflows involving Excel to AMI and back to Excel. Facilitates flexible data handling and transformation within AMI.
- Use Case: Best suited for scenarios where users need to import Excel spreadsheets into AMI, manipulate the data, and then export it back to Excel.

**Spreadsheet Worksheet:** 

- Purpose: Used to generate and manage individual worksheets in AMI.
- Features: Focuses on the workflow from AMI  to Excel, allowing detailed operations on specific worksheets.
- Use Case: Best suited for exporting data from AMI into Excel.


### SpreadSheet Builder

| Method                                                  | Return Type    | Description                                                     |
| ------------------------------------------------------- | -------------- | --------------------------------------------------------------- |
| [`addSheet(TablePanel tablePanel, String sheetName, Boolean onlySelectedRows, Boolean shouldFormat)`](#spreadsheetbuilder-addsheet)                 | Boolean      | Adds a new worksheet to the spreadsheet builder.                                           |
| [`addFlexSheet(String name)`](#spreadsheetbuilder-addflexsheet)             | Boolean         | Adds a new flexsheet to the spreadsheet builder.            |
| [`deleteSheet(String sheetName)`](#spreadsheetbuilder-deletesheet) | Boolean     | Deletes the specified spreadsheet from the spreadsheet builder.          |
| [`getExcelPosition(Integer position)`](#spreadsheetbuilder-getexcelposition)                         | String         | Returns the alphabetical representation of the zero-based position.                   |
| [`loadExistingSheets(Binary data)`](#spreadsheetbuilder-loadexistingsheets)                         | Boolean         | Loads the binary data of an XLSX file into individual flex sheets.                   |
| [`getSheetNames()`](#spreadsheetbuilder-getsheetname)                         | Set         | Returns a set containing the names of the sheets added, including hidden sheets.                   |
| [`getWorksheet(String worksheetName)`](#spreadsheetbuilder-getworksheet-flexsheet)                         | Object         | Returns the spreadsheet in the *SpreadSheetFlexsheet* class.                   |
| [`getWorksheet(String worksheetName)`](#spreadsheetbuilder-getworksheet-worksheet)                         | Object         | Returns the spreadsheet in the *SpreadSheetWorksheet* class.                   |
| [`hideSheet(String sheetName)`](#spreadsheetbuilder-hidesheet)                         | Boolean         | Hides the specified spreadsheet.  |
| [`showSheet(String sheetName)`](#spreadsheetbuilder-showsheet)                         | Boolean         | Shows the specified hidden spreadsheet.  |
| [`renameSheet(String sheetName, String newSheetName)`](#spreadsheetbuilder-renamesheet)                         | Boolean         | Rename the specified spreadsheet.  |
| [`build()`](#spreadsheetbuilder-build)                         | Binary       | Builds and returns a binary containing the spreadsheet.  |

<a id="spreadsheetbuilder-addsheet"></a>
!!! quote "addSheet(TablePanel tablePanel, String sheetName, Boolean onlySelectedRows, Boolean shouldFormat)" 
    Adds a new *worksheet* to the spreadsheet builder.

    === "AmiScript"
    
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        tablepanel tp = layout.getPanel("datamodel");
        builder.addSheet(tp, "Sheet2", false, false);
        ```
    === "Output"
    
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-addflexsheet"></a>
!!! quote "addFlexSheet(String name)" 
    Adds a new flexsheet to the spreadsheet builder.

    === "AmiScript"
    
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        builder.addFlexSheet("Sheet2");
        ```
    === "Output"
    
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-deletesheet"></a>
!!! quote "deleteSheet(String sheetName)" 
    Deletes the specified spreadsheet from the spreadsheet builder.

    === "AmiScript"
    
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        builder.deleteSheet("Sheet1");
        ```
    === "Output"
    
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-getexcelposition"></a>
!!! quote "getExcelPosition(Integer position)" 
    Returns the alphabetical representation of the zero-based position.

    === "AmiScript"
    
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        builder.getExcelPosition(2);
        ```
    === "Output"
    
        ```amiscript
        C
        ```
<br>


<a id="spreadsheetbuilder-loadexistingsheets"></a>
!!! quote "loadExistingSheets(Binary data)" 
    Loads the binary data of an XLSX file into individual flex sheets.

    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-getsheetnames"></a>
!!! quote "getSheetNames()" 
    Returns a set containing the names of the sheets added, including hidden sheets.

    === "AmiScript"
        ```amiscript
        builder.getSheetNames();
        ```
    === "Output"
        ```amiscript
        ["Sheet1", "Sheet2", "Sheet3"]
        ```
<br>

<a id="spreadsheetbuilder-getworksheet-flexsheet"></a>
!!! quote "getWorksheet(String worksheetName)" 
    Returns the spreadsheet in the *SpreadSheetFlexsheet* class.

    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        ```
    === "Output"
        ```amiscript
        spreadsheet.AmiWebSpreadSheetFlexsheet@7145684a
        ```
<br>

<a id="spreadsheetbuilder-getworksheet-worksheet"></a>
!!! quote "getWorksheet(String worksheetName)" 
    Returns the spreadsheet in the *SpreadSheetWorksheet* class.

    === "AmiScript"
        ```amiscript
        spreadSheetWorksheet ws = builder.getWorksheet("Sheet1");
        ```
    === "Output"
        ```amiscript
        spreadsheet.AmiWebSpreadSheetWorksheet@41a89499
        ```

<br>

<a id="spreadsheetbuilder-hidesheet"></a>
!!! quote "hideSheet(String sheetName)" 
    Hides the specified spreadsheet.
    === "AmiScript"
        ```amiscript
        builder.hideSheet("Sheet1");
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-showsheet"></a>
!!! quote "showSheet(String sheetName)" 
    Shows the specified hidden spreadsheet.
    === "AmiScript"
        ```amiscript
        builder.showSheet("Sheet1");
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-renamesheet"></a>
!!! quote "renameSheet(String sheetName, String newSheetName)" 
    Rename the specified spreadsheet.
    === "AmiScript"
        ```amiscript
        builder.renameSheet("Sheet1", "New1");
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetbuilder-build"></a>
!!! quote "build()" 
    Builds and returns a binary containing the spreadsheet.
    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        binary out = builder.build();
        fs.writeBinaryFile("/path/to/save/.../test1.xlsx", out, false);
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>


### SpreadSheet Flexsheet

| Method                                                  | Return Type    | Description                                                     |
| ------------------------------------------------------- | -------------- | --------------------------------------------------------------- |
| [`getValue(String dimension)`](#spreadsheetflexsheet-getvalue)                 | Boolean      | Gets the value of the specified cell.                                           |
| [`getValues(String dimension, Boolean hasHeader, List classes)`](#spreadsheetflexsheet-getvalues)        | Boolean     | Returns the values of the specified range of cells as a table.            |
| [`getValuesNamedRange(String nameRange, Boolean hasHeader, List classes)`](#spreadsheetflexsheet-getvaluesnamedrange) | Boolean     | Returns the values of the specified name range as a table.          |
| [`getTitle()`](#spreadsheetflexsheet-gettitle)                         | String         | Gets the name of the sheet.                   |
| [`getStyle(String dimension)`](#spreadsheetflexsheet-getstyle)                         | Boolean         | Gets the style id associated with the specified cell.                   |
| [`setValue(String dimension, Object value)`](#spreadsheetflexsheet-setvalue)                         | Set         | Sets the value for the specified range of cells.                   |
| [`setValue(String dimension, Table value, Boolean useHeader)`](#spreadsheetflexsheet-setvalue-headers)                         | Object         | Sets the value for the specified range of cells.                   |
| [`setValueNamedRange(String nameRange, Object value)`](#spreadsheetflexsheet-setvaluenamedrange)                         | Object         | Sets the values for the specified named range of cells.                   |
| [`setTitle(String title)`](#spreadsheetflexsheet-settitle)                         | Boolean         | Sets the name of the sheet.  |
| [`setStyle(String dimension)`](#spreadsheetflexsheet-setstyle)                         | Boolean         | Sets the style id for the specified range of cells.  |


<a id="spreadsheetflexsheet-getvalue"></a>
!!! quote "getValue(String dimension)" 
    Gets the value of the specified cell.
    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        fileSystem fs = session.getFileSystem();
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");
        builder.loadExistingSheets(b);
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        flex.getValue("A2");
        ```
    === "Output"
        ```amiscript
        100
        ```
<br>

<a id="spreadsheetflexsheet-getvalues"></a>
!!! quote "getValues(String dimension, Boolean hasHeader, List classes)" 
    Returns the values of the specified range of cells as a table. 
    <br>*If the values are of a date type in Excel, they are returned as strings.*
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        list classes = new list("string", "integer");
        flex.getValues("A1:B4", true, classes);
        ```
    === "Output"
        ```amiscript
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |MSFT   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ``` 
    To manage large Excel file with many columns, use the following approach: 

    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        list classes = new list();
        int columnscount = 100; 
        for (int i = 0; i < columnscount; i++) {classes.add("string");}
        table t = flex.getValues("A1:CV1001", true, classes);
        ```
    === "Output"
        ```amiscript
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |MSFT   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ```
    **Note**: In this example, all columns are set to string type. You can modify the column types using [`Table::alterColumn(currentColumnName, newColumnName, newTypeOrNull)`](https://doc.3forge.com/javadoc/classes/Table/?h=altercolum#altercolumncurrentcolumnnamenewcolumnnamenewtypeornull).
<br>

<a id="spreadsheetflexsheet-getvaluesnamedrange"></a>
!!! quote "getValuesNamedRange(String nameRange, Boolean hasHeader, List classes)" 
    Returns the values of the specified name range as a table. <br>
    *namedRange* refers to the *Names* defined in the *Name Manager* in Excel. 
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        list classes = new list("string", "integer");
        flex.getValuesNamedRange("Sample", true, classes);
        ```
    === "Output"
        ```amiscript
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |MSFT   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ```
<br>

<a id="spreadsheetflexsheet-gettitle"></a>
!!! quote "getTitle()" 
    Gets the value of the specified cell. 
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        flex.getTitle();
        ```
    === "Output"
        ```amiscript
        Sheet1
        ```
<br>


<a id="spreadsheetflexsheet-getstyle"></a>
!!! quote "getStyle(String dimension)" 
    Gets the style id associated with the specified cell. 
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        flex.getStyle("A2");
        ```
    === "Output"
        ```amiscript
        1
        ```
<br>

<a id="spreadsheetflexsheet-setvalue"></a>
!!! quote "setValue(String dimension, Object value)" 
    Sets the value for the specified range of cells.
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        list classes = new list("string", "integer");
        flex.setValue("A2", "NVDA");
        flex.getValues("A1:B4", true, classes);
        ```
    === "Output"
        ```amiscript
        +-------+---------+
        |Symbol |Quantity |
        |String |Integer  |
        +-------+---------+
        |NVDA   |100      |
        |IBM    |200      |
        |AAPL   |400      |
        +-------+---------+
        ```
<br>

<a id="spreadsheetflexsheet-setvalue-headers"></a>
!!! quote "setValue(String dimension, Table value, Boolean useHeader)" 
    Sets the style id associated with the specified cell. 
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        create table fruits (name string, quantity integer, price double);
        insert into fruits values ("apple", 3, 2.0), ("mango", 5, 4.5), ("pear", 2, 3.5);
        table fruits = select * from fruits;
        flex.setValue("A1", fruits, true);
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetflexsheet-setvaluenamedrange"></a>
!!! quote "setValueNamedRange(String nameRange, Object value)" 
    Sets the values for the specified named range of cells. <br>
    *namedRange* refers to the *Names* defined in the *Name Manager* in Excel.  
    === "AmiScript"
        ```amiscript
          spreadSheetFlexsheet fs = builder.getWorksheet("Sheet1");
          flex.setValueNamedRange("Sample", fruits);
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetflexsheet-settitle"></a>
!!! quote "setTitle(String title)" 
    Sets the name of the sheet.  
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        flex.setTitle("Renamed");
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetflexsheet-setstyle"></a>
!!! quote "setStyle(String dimension)" 
    Sets the style id for the specified range of cells.  
    === "AmiScript"
        ```amiscript
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        int styleid = flex.getStyle("A2");
        flex.setStyle("A3", styleid);
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>


### Spreadsheet Worksheet

| Method                                                  | Return Type    | Description                                                     |
| ------------------------------------------------------- | -------------- | --------------------------------------------------------------- |
| [`getTable()`](#spreadsheetworksheet-gettable)                 | Table      | Returns the table.                                           |
| [`getTitle()`](#spreadsheetworksheet-gettitle)        | String     | Gets the name of the sheet.            |
| [`setTitle(String title)`](#spreadsheetworksheet-settitle)        | String     | Sets the name of the sheet.            |
| [`addColWithFormula(Integer position, String colName, String formula)`](#spreadsheetworksheet-addcolwithformula)        | Boolean     | Creates a new formula based column.            |

<a id="spreadsheetworksheet-gettable"></a>
!!! quote "getTable()" 
    Returns the table.  
    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        tablepanel tp = layout.getPanel("datamodel");
        builder.addSheet(tp, "Sheet2",false, false);
        
        spreadSheetworksheet ws = builder.getWorksheet("Sheet2");   
        ws.getTable();
        ```
    === "Output"
        ```amiscript
        +---------------+
        |    Sheet2     |
        +------+--------+
        |Symbol|Quantity|
        |String|String  |
        +------+--------+
        |MSFT  |100     |
        |IBM   |200     |
        |AAPL  |400     |
        +------+--------+
        ```
<br>

<a id="spreadsheetworksheet-gettitle"></a>
!!! quote "getTitle()" 
    Gets the name of the sheet.  
    === "AmiScript"
        ```amiscript
        spreadSheetworksheet ws = builder.getWorksheet("Sheet2");   
        ws.getTitle();
        ```
    === "Output"
        ```amiscript
        Sheet2
        ```
<br>


<a id="spreadsheetworksheet-settitle"></a>
!!! quote "setTitle(String title)" 
    Sets the name of the sheet. 
    === "AmiScript"
        ```amiscript
        spreadSheetworksheet ws = builder.getWorksheet("Sheet2");   
        ws.setTitle("Renamed");
        ```
    === "Output"
        ```amiscript
        true
        ```
<br>

<a id="spreadsheetworksheet-addcolwithformula"></a>
!!! quote "addColWithFormula(Integer position, String colName, String formula)" 
    Creates a new formula based column. 
    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();
        tablepanel tp = layout.getPanel("datamodel2");
        builder.addSheet(tp, "fruits", false, false);

        spreadSheetworksheet ws = builder.getWorksheet("fruits");
        ws.getTable();
        +----------------------+
        |        fruits        |
        +------+--------+------+
        |Name  |Quantity|Price |
        |String|String  |String|
        +------+--------+------+
        |apple |3       |2.0   |
        |mango |5       |4.5   |
        |pear  |2       |3.5   |
        +------+--------+------+

        ws.addColWithFormula(3, "total", "`Price`*`Quantity`");   
        session.downloadToBrowser("fruits.xlsx",builder.build());
        ```
    === "Output"
        ![](../resources/legacy_mediawiki/Spreadsheet(12).png "Spreadsheet(12).png")
<br>


## Downloading of SpreadSheets

Users can download their modified spreadsheets from AMI. This feature ensures that any changes made within the AMI environment can be saved and utilized outside of it. 

### From AMI Tables

To download an existing table in AMI as a spreadsheet, click on the `Download as Spread Sheet` option.

![](../resources/legacy_mediawiki/Spreadsheet(1).png "Spreadsheet(1).png")

### From Datamodels

To download from Datamodels, use either of the following AMIScripts:

!!! quote "Session.downloadToBrowser(String fileName, String fileData)" 

    === "AmiScript"
        ```amiscript
        spreadSheetBuilder builder = new SpreadSheetBuilder();	
        fileSystem fs = session.getFileSystem();							
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");	
        builder.loadExistingSheets(b);	
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");
        binary out = builder.build();	
        session.downloadToBrowser("test.xlsx",builder.build());
        ```


!!! quote "FileSystem.writeFile(String file, String data, Boolean shouldAppend)" 

    === "AmiScript"
        ```amiscript   
        spreadSheetBuilder builder = new SpreadSheetBuilder();	
        fileSystem fs = session.getFileSystem();							
        binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");	
        builder.loadExistingSheets(b);	
        spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");	
        binary out = builder.build();		
        fs.writeBinaryFile("/path/to/file/.../test2.xlsx", out, false);	
        ```


## Example Workflows

This section provides example workflows that demonstrate how to use AMI's spreadsheet features.

### Modify contents of a single cell

=== "AmiScript"
    ``` amiscript
      // Create a new SpreadSheetBuilder
      SpreadSheetBuilder builder = new SpreadSheetBuilder();	
      
      FileSystem fs = session.getFileSystem();					
      
      // Grabs the Excel file and saves the Binary			
      Binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");	
      
      if(b ==null){
        session.alert("file doesnt exist");
      }
      
      // Loads the Excel file using the Binary
      builder.loadExistingSheets(b);	
      
      // Adds a new FlexSheet to enable cell edits
      SpreadSheetFlexsheet  fs2 = builder.getWorksheet("Sheet1");	
      
      // Adds "LOAD EDIT CELL" to cell E5
      fs2.setValue("E5:E5", "LOAD EDIT CELL");	
      
      // Builds a Binary from the edited SpreadSheet
      Binary out = builder.build();		
      
      // Exports the edited SpreadSheet from the built Binary
      fs.writeBinaryFile("/path/to/file/.../test1.xlsx", out,false);	
    ```

=== "Original Spreadsheet"
    ![](../resources/legacy_mediawiki/Spreadsheet(7).png "Spreadsheet(7).png")

=== "Modified Spreadsheet"
    ![](../resources/legacy_mediawiki/Spreadsheet(8).png "Spreadsheet(8).png")

### Modify column content with conditions

=== "AmiScript"
    ``` amiscript

      spreadSheetBuilder builder = new SpreadSheetBuilder();	
      fileSystem fs = session.getFileSystem();							
      binary b = fs.readBinaryFile("/path/to/file/.../test.xlsx");	
      builder.loadExistingSheets(b);	
      spreadSheetFlexsheet flex = builder.getWorksheet("Sheet1");	

      // Sets the column header
      flex.setValue("C1", ">300 ?"); 

      // Sets the column content
      for (int i=2; i<5; i++){
        int qty = flex.getValue("B"+{i});
        if( qty > 300 ) {
          flex.setValue("C"+{i}, "Yes");
        } else 
          flex.setValue("C"+{i}, "No");
      }

      binary out = builder.build();		
      fs.writeBinaryFile("/path/to/file/.../test2.xlsx", out, false);	
    ```

=== "Original Spreadsheet"
    ![](../resources/legacy_mediawiki/Spreadsheet(10).png "Spreadsheet(10).png")

=== "Modified Spreadsheet"
    ![](../resources/legacy_mediawiki/Spreadsheet(11).png "Spreadsheet(11).png")











