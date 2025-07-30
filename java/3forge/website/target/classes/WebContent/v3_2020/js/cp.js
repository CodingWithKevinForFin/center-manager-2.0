// buttons

var numTable = 0;

var addTableBtn = document.getElementById("add-table");
addTableBtn.addEventListener("click", onAddTableBtnClick);

function onAddTableBtnClick() {
	numTable++;
	var colTypesContainer = document.getElementById("cols-container");
	if (colTypesContainer.childElementCount == 0) {
		numTable = 1;	
	}
	
	var colTypeContainer = document.createElement("div");
	
	var numRowsField = document.createElement("input");
	numRowsField.setAttribute("type", "number");
	numRowsField.setAttribute("name", "numRows");
	numRowsField.setAttribute("class", "inputBox");
	numRowsField.defaultValue = 0;
	
	var numIdxsField = document.createElement("input");
	numIdxsField.setAttribute("type", "number");
	numIdxsField.setAttribute("name", "numIdxs");
	numIdxsField.setAttribute("class", "inputBox");
	numIdxsField.defaultValue = 0;
	
	var newColBtn = document.createElement("button");
	//newColBtn.setAttribute("type", "submit");
	newColBtn.setAttribute("name", "addColButton" + numTable);
	newColBtn.setAttribute("id", "addColButton" + numTable);
	newColBtn.setAttribute("class", "addColButton");
	newColBtn.appendChild(document.createTextNode("Add Column"));
	var n = numTable;
	newColBtn.addEventListener("click", function(){onAddColumnBtnClick(n)});
	
	colTypeContainer.setAttribute("class", "tableDiv");
	colTypeContainer.setAttribute("id", "tableContainer" + numTable);
	colTypeContainer.setAttribute("name", "tableContainer" + numTable);
	
	colTypeContainer.appendChild(document.createElement("div"));
	
	//Table Title Text
	var tableTitleSpan = document.createElement('span');
	tableTitleSpan.setAttribute("class", "tableTitleSpan");
	tableTitleSpan.setAttribute("id", "tableTitleSpan");
	tableTitleSpan.setAttribute("name", "tableTitleSpan");
	tableTitleSpan.appendChild(document.createTextNode("TABLE " + numTable));
	colTypeContainer.appendChild(tableTitleSpan);
	
	//Remove Table Button
	var remTableBtn = document.createElement("button");
	remTableBtn.setAttribute("class", "remTableButton");
	remTableBtn.appendChild(document.createTextNode("Remove Table"));
	remTableBtn.addEventListener("click", function(){onRemoveTableBtnClick(n)});
	colTypeContainer.appendChild(remTableBtn);
	
	//num Rows Text
	colTypeContainer.appendChild(document.createElement("div"));
	var nRowsTextSpan = document.createElement('span');
	nRowsTextSpan.setAttribute("class", "nRowsTextSpan");
	nRowsTextSpan.appendChild(document.createTextNode("Number of Rows"));
	colTypeContainer.appendChild(nRowsTextSpan);
	colTypeContainer.appendChild(numRowsField);
	
	//num Idxs Text
	colTypeContainer.appendChild(document.createElement("div"));
	var nIdxsTextSpan = document.createElement('span');
	nIdxsTextSpan.setAttribute("class", "nIdxsTextSpan");
	nIdxsTextSpan.appendChild(document.createTextNode("Number of Indices"));
	colTypeContainer.appendChild(nIdxsTextSpan);
	colTypeContainer.appendChild(numIdxsField);
	
	colTypeContainer.appendChild(document.createElement("div"));
	//colTypesContainer.appendChild(newColBtn);
	colTypesContainer.appendChild(colTypeContainer);
	colTypesContainer.appendChild(newColBtn);
}

function onRemoveColumnBtnClick() {
	event.target.parentNode.remove();
}

function onRemoveTableBtnClick(n) {
	event.target.parentNode.remove();
	var element = document.getElementById("addColButton" + n);
	element.parentNode.removeChild(element);
}

function createRemoveButton() {
	var remColBtn = document.createElement("button");
	remColBtn.setAttribute("type", "submit");
	remColBtn.setAttribute("name", "removeColButton");
	remColBtn.setAttribute("id", "removeColButton");
	remColBtn.setAttribute("class", "removeColButton");
	remColBtn.appendChild(document.createTextNode("X"));
	remColBtn.addEventListener("click", onRemoveColumnBtnClick);
	return remColBtn;
}

function onAddColumnBtnClick(n) {
	var tableContainer = document.getElementById("tableContainer" + n);
	var colTypeCont = document.createElement("div"); 
	//colTypeContainer.className = "col-type-container";
	
	var colTypeTextSpan = document.createElement('span');
	colTypeTextSpan.setAttribute("class", "colTypeTextSpan");
	colTypeTextSpan.appendChild(document.createTextNode("column type"));
	colTypeCont.appendChild(colTypeTextSpan);
	
	//Dropdown List with types
	var dropDown = createDropDown(); 
	colTypeCont.appendChild(dropDown);

	//Max Size Text
	var maxLenTextSpan = document.createElement('span');
	maxLenTextSpan.setAttribute("class", "maxLenTextSpan");
	maxLenTextSpan.appendChild(document.createTextNode("max size"));
	colTypeCont.appendChild(maxLenTextSpan);
	
	//Max Len Input Box
	var maxLenInput = createMaxLenInput(1);
	maxLenInput.setAttribute("name", "maxLenInputBox");
	maxLenInput.setAttribute("id", "maxLenInputBox");
	maxLenInput.setAttribute("class", "maxLenInputBox");
	colTypeCont.appendChild(maxLenInput);
	
	//Null Text
	var canBeNullTextSpan = document.createElement('span');
	canBeNullTextSpan.setAttribute("class", "canBeNullTextSpan");
	canBeNullTextSpan.appendChild(document.createTextNode("can be"));
	colTypeCont.appendChild(canBeNullTextSpan);
	var nullTextSpan = document.createElement('span');
	nullTextSpan.setAttribute("class", "nullTextSpan");
	nullTextSpan.appendChild(document.createTextNode("null"));
	colTypeCont.appendChild(nullTextSpan);
	
	//Null Checkbox
	var nullInput = createNullInput(false);
	colTypeCont.appendChild(nullInput);
	
	var remButton = createRemoveButton();
	colTypeCont.appendChild(remButton);
	tableContainer.appendChild(colTypeCont);
}

function createDropDown() {
	var dropDown = document.createElement("select");
	dropDown.setAttribute("name", "dropdown");
	dropDown.addEventListener("change", onColTypeChange);	
	var options = ["Binary OnDisk", "Big Decimal", "Big Integer", "Binary", "Boolean", "Byte", "Char", "Complex", "Double", "Enum", "Float", "Int", "Long", "Short", "String Bitmap", "String Compact Ascii", "String Compact", "String OnDisk", "String", "UTCN", "UUID", "UTC"]; // TODO: add more types.
	var selectOption = document.createElement("option");
	selectOption.disabled = true;
	selectOption.value = "";
	selectOption.text = "Select..."
	selectOption.selected = "selected";
	dropDown.appendChild(selectOption);
	for (var i = 0; i < options.length; i++) {
		var option = document.createElement("option");
		option.value = options[i];
		option.text = options[i];
		dropDown.appendChild(option);
	}
	return dropDown;
}

function onColTypeChange() {
	var dropDown = event.target;
	var container = dropDown.parentNode;
	//console.log(container.children.maxLenInputBox.value);
	if (["String Bitmap", "String Compact", "String Compact Ascii", "String"].includes(dropDown.value)) {
		container.children.maxLenInputBox.readonly = false;
		container.children.maxLenInputBox.readOnly = false;
		//console.log(container.children);
		//console.log(container.children.maxLenInputBox);
	}
	else if (["Binary", "Big Decimal", "Big Integer"].includes(dropDown.value)) {
		//container.children.maxLenInputBox.setAttribute("readonly", false);
		container.children.maxLenInputBox.readonly = false;
		container.children.maxLenInputBox.readOnly = false;
		//console.log(container.children);
		//console.log(container.children.maxLenInputBox);
	} else {
		container.children.maxLenInputBox.readonly = true;
		container.children.maxLenInputBox.readOnly = true;
		container.children.maxLenInputBox.value = 1;
		//console.log(container.children);
		//console.log(container.children.maxLenInputBox);
	}
}

function createMaxLenInput(defaultVal) {
	var maxLenInput = document.createElement("input");
	maxLenInput.setAttribute("name", "maxLenInput");
	maxLenInput.setAttribute("id", "maxLenInput");
	maxLenInput.setAttribute("class", "maxLenInput");
	maxLenInput.setAttribute("value", defaultVal);
	return maxLenInput;
}

function createNullInput(defaultVal) {
	var nullInput = document.createElement("input");
	nullInput.setAttribute("name", "nullCheckBox");
	nullInput.setAttribute("id", "nullCheckBox");
	nullInput.setAttribute("class", "nullCheckBox");
	nullInput.setAttribute("type", "checkbox");
	nullInput.checked = defaultVal;
	//nullInput.setAttribute("onclick", "return false;");
	return nullInput;
}

var calcCapBtn = document.getElementById("calc-cap");
calcCapBtn.addEventListener("click", onCalcCapBtnClick);

function onCalcCapBtnClick() {
	var resultContainer = document.getElementById("cp-result");
	resultContainer.innerHTML = "";
	var dataContainer = document.getElementById("cols-container");
	var tablesSizeMB = 0;
	var tablesSuggestedCapacityMB = 0;
	
	for (var n = 0; n < dataContainer.children.length; n += 2) {
		var tableSizeMB = 0;
		var tableSuggestedCapacityMB = 0;
		var tableContainer = dataContainer.children[n];
		console.log(tableContainer);
		var rowCnt = tableContainer.children.numRows.value;
		var idxCnt = tableContainer.children.numIdxs.value;
		var nullColCnt = 0;
		var dataSize = 0.0;
		
		var columns = tableContainer.children;
		for (var c = 10; c < columns.length; c++) {
			var column = columns[c].children;
			var type = column.dropdown.value;
			var size = column.maxLenInputBox.value;
			var allowNull = column.nullCheckBox.checked ? 1 : 0;
			nullColCnt += allowNull;
			if (["UTC", "UTCN", "Double", "Long", "String OnDisk",  "Binary OnDisk"].includes(type)) {
				dataSize += 8.0;
			}
			if (["Int", "Float", "Enum"].includes(type)) {
				dataSize += 4.0;
			}
			else if (["Char", "Short"].includes(type)) {
				dataSize += 2.0;
			}
			else if (["Byte"].includes(type)) {
				dataSize += 1.0;
			}
			else if (["Boolean"].includes(type)) {
				dataSize += 0.125;
			}
			else if (["String Bitmap"].includes(type)) {
				dataSize += size;
			}
			else if (["String Compact"].includes(type)) {
				dataSize += (2.0 * size + 6.0);
			}
			else if (["String Compact Ascii"].includes(type)) {
				dataSize += (1.0 * size + 5.0);
			}
			else if (["String"].includes(type)) {
				dataSize += (2.0 * size + 64.0);
			}
			else if (["Binary"].includes(type)) {
				dataSize += (size + 56.0);
			}
			else if (["Big Decimal"].includes(type)) {
				dataSize += (size + 68.0);
			}
			else if (["Big Integer"].includes(type)) {
				dataSize += (size + 64.0);
			}
			else if (["Complex"].includes(type)) {
				dataSize += 68.0;
			}
			else if (["UUID"].includes(type)) {
				dataSize += 64.0;
			}
			else {
				dataSize += 0.0;
			}
		}
		tableSizeMB = rowCnt * (128.0 + dataSize + idxCnt * 100.0 + nullColCnt * 0.125) / 1048576.0;
		tableSuggestedCapacityMB = tableSizeMB * 2.0;
		tablesSizeMB += tableSizeMB;
		tablesSuggestedCapacityMB += tableSuggestedCapacityMB;
		
		var tableNameTextSpan = document.createElement('span');
		tableNameTextSpan.setAttribute("class", "tableNameTextSpan");
		tableNameTextSpan.appendChild(document.createTextNode(tableContainer.children.tableTitleSpan.innerHTML));
		resultContainer.appendChild(tableNameTextSpan);

		var tableResultTextSpan = document.createElement('span');
		tableResultTextSpan.setAttribute("class", "tableResultTextSpan");
		tableResultTextSpan.appendChild(document.createTextNode("Estimated Size: " + Math.ceil(tableSizeMB).toString() + " MB"));
		resultContainer.appendChild(tableResultTextSpan);
		var tableSuggestionTextSpan = document.createElement('span');
		tableSuggestionTextSpan.setAttribute("class", "tableSuggestionTextSpan");
		tableSuggestionTextSpan.appendChild(document.createTextNode("Suggested Capacity: " + Math.ceil(tableSuggestedCapacityMB).toString() + " MB"));
		resultContainer.appendChild(tableSuggestionTextSpan);
		resultContainer.appendChild(document.createElement("div"));
	}
	
	var tablesResultTextSpan = document.createElement('span');
	tablesResultTextSpan.setAttribute("class", "tablesResultTextSpan");
	tablesResultTextSpan.appendChild(document.createTextNode("Total Estimated Size: " + Math.ceil(tablesSizeMB).toString() + " MB"));
	resultContainer.appendChild(tablesResultTextSpan);
	resultContainer.appendChild(document.createElement("div"));
	var tablesSuggestionTextSpan = document.createElement('span');
	tablesSuggestionTextSpan.setAttribute("class", "tablesSuggestionTextSpan");
	tablesSuggestionTextSpan.appendChild(document.createTextNode("Total Suggested Capacity: " +  Math.ceil(tablesSuggestedCapacityMB).toString() + " MB"))
	resultContainer.appendChild(tablesSuggestionTextSpan);
	resultContainer.appendChild(document.createElement("div"));
}
