# Managing Styles

## Copying Styles

To copy a dashboard style from one dashboard to another we can use the Import/Export Style function in Style Manager. Let's take a pre-styled dashboard as such:

![](../resources/legacy_mediawiki/PreStyledDashboard.png "PreStyledDashboard.png")

To copy this style, enter Development mode and under *Dashboard* select *Style Manager*. Select the style to copy, right click to *Export Style* and copy:

![](../resources/legacy_mediawiki/ExportStyle.png "ExportStyle.png")

Next, open the dashboard in which you would like to import this style. Under *Style Manager* select *Import Style*. Paste the copied text - you can rename the *id* and *lb* to your preferred names (here we have named both to **CopiedStyle**):

![](../resources/legacy_mediawiki/ImportStyle.png "ImportStyle.png")

Finally, under **Layout Default**, select CopiedStyle to inherit from. This will update the style of the entire dashboard to this style.

![](../resources/legacy_mediawiki/InheritedStyle.png "InheritedStyle.png")

## Readonly Styles

AMI has several layout styles you could choose from. These layout styles are encoded in JSON format files and are placed in amione/data/styles directory. In this example, we have a stylesheet called **DARKMATTER.amistyle.json** and show how to include it in AMI.

1. Navigate to the AMI installation path and place the attached JSON file inside the styles directory (amione/data/styles)  

	![](../resources/legacy_mediawiki/Dir1.jpg "Dir1.jpg")

1. Navigate to /amione/config and add the following inside local.properties file
	
	```
    ami.style.files=data/styles/*amistyle.json
	```

	![](../resources/legacy_mediawiki/L2.jpg "L2.jpg") ![](../resources/legacy_mediawiki/L3.png "L3.png")

1. Restart AMI shut down the current AMI instance and restart  

1. Log in and open Dashboard \> Style Manager  

	![](../resources/legacy_mediawiki/L31.jpg "L31.jpg")  

5. Select Layout Default from tree and Dark Matter from the dropdown list Your dashboard should now pick up the styles from the Dark Matter stylesheet.  

	![](../resources/legacy_mediawiki/Layout.png "Layout.png") ![](../resources/legacy_mediawiki/Like.jpg "Like.jpg")

