# Super Filter

## Overview

The Super Filter layout provides a simple method for filtering common column values across multiple tables.

## Setup

1. In the layout which you want to use the super filter layout, include the **SuperFilter.layout** file through the Included Layouts panel.  
1. Open the **SuperFilter.layout** file, and use the **SuperFilter** window to pass in a mapping json (see [JSON Config](#json-config)) and its corresponding mapping id.  
1. Open your layout and attach a new data model with the following script:  

	``` java
	 spr_fltr_initTable("PANELNAME", "MAPPINGID", null); 
	```
	
	Where **PANELNAME** is the name of the panel containing the super filter and **MAPPINGID** is the earlier created id. (4). Create a new panel in your layout with the name **PANELNAME**, and attach a table visualization on top of the previously created data model. If everything has been setup correctly, you should see a table **\`SEARCH FIELDS\`** containing your column names.

1. Finish creating the panel and open the **AMISCRIPT callbacks**, on the **onFilterChanging()** tab, add the following line:  

	``` java
	spr_fltr_tableFilterSearch(this,null); 
	```

1. Submit your changes and the filters on the super filter should affect the target tables.

## JSON Config  

``` json
{
    "DmMapping": {
        //Table name should correspond to the data model name
        "TABLE_A": {
            "ColMapping": {
                //Column name on the Super Filter
                "A": {
                    //Column name on the table
                    "ColName": "TABLE_A_A",
                    //Supported types are: string, long, int, integer (non case sensitive)
                    "ColType": "String"
                },
                "B": {
                    //Full name on the table if using a JOIN
                    "ColName": "JOINED_TABLE.B",  
                    "ColType": "String",                    
                }
        },
        "TABLE_B": {
            "ColMapping": {
                "A": {
                    "ColName": "TABLE_B_A",
                    "ColType": "String"
                }
        },
        "TABLE_C": {
            "ColMapping": {
                "B": {
                    "ColName": "TABLE_C_B",
                    "ColType": "String"
                }
        }
    },
    "FormFilterBys": [
        {
            //Super Filter column
            "FltrDisplay": "A"
        },
        {
            "FltrDisplay": "B"

        	//Use Java SimpleDateFormat to parse string into long values
            "FormatDate": "dd/MM/yyyy HH:mm:ss",
            //Optionally specify timezone, otherwise session default will be used
            "FormatTimezone": "UTC"
        }
    ]
}
```

## Supported Usage

On all column types, you can use any of the following syntax for searches  

| Input   | Check                              |
|---------|------------------------------------|
| 100     | Equality check                     |
| word    | Equality check                     |
| \>100   | \>=100 numerical check             |
| \>=100  | \>=100 numerical check             |
| \<100   | \<=100 numerical check             |
| \<=100  | \<=100 numerical check             |
| 100-200 | \>= 100 && \<= 200 numerical check |
| a\|b\|c | string in "a" "b", or "c" check    |

