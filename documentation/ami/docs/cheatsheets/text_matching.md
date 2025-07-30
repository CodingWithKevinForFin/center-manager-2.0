# Text Matching

For tables with large amounts of data, you may wish to filter results by some given string or numerical value. 

In AMI tables, there are search bars above each column. It is possible to refine the search results beyond the supplied tokens by passing some basic parameters to perform text-matching instead. Some common examples of ways to filter search results are listed below. 

## Lookup Table 

The following commands can be used to do simple text-matching for retrieving table entries according to some criteria. 

| Command | Finds |
|---------|-------| 
| ^X | Entries starting with "X" | 
| $X | Entries ending with "X" |
| \*X\* | Entries containing "X"| 
| X\|Y | Entries containing either "X" or "Y" |
| ? | Empty entries |
| !X | Entries except "X" |
| 0 - 10 | Numerical entries within the range 0 - 10 |
| >= 10 | Numerical entries greater than or equal to 9 |
| < -2 | Numerical entries less than -2 |

## See More 

For more detailed text matching information, see the documentation [here](../reference/ami_script.md/#simplified-text-matching).