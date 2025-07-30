package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_mode_amiscript_js_1 extends AbstractHttpHandler{

	public amiweb_mode_amiscript_js_1() {
	}
  
	public boolean canHandle(HttpRequestResponse request){
	  return true;
	}

	public void handle(HttpRequestResponse request) throws java.io.IOException{
	  super.handle(request);
	  com.f1.utils.FastPrintStream out = request.getOutputStream();
	  HttpSession session = request.getSession(false);
	  HttpServer server = request.getHttpServer();
	  LocaleFormatter formatter = session == null ? server.getHttpSessionManager().getDefaultFormatter() : session.getFormatter();
          out.print(
            "ace.define(\"ace/mode/doc_comment_highlight_rules\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/text_highlight_rules\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../lib/oop\");\r\n"+
            "var TextHighlightRules = require(\"./text_highlight_rules\").TextHighlightRules;\r\n"+
            "\r\n"+
            "var DocCommentHighlightRules = function() {\r\n"+
            "    this.$rules = {\r\n"+
            "        \"start\" : [ {\r\n"+
            "            token : \"comment.doc.tag\",\r\n"+
            "            regex : \"@[\\\\w\\\\d_]+\" // TODO: fix email addresses\r\n"+
            "        }, \r\n"+
            "        DocCommentHighlightRules.getTagRule(),\r\n"+
            "        {\r\n"+
            "            defaultToken : \"comment.doc\",\r\n"+
            "            caseInsensitive: true\r\n"+
            "        }]\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "\r\n"+
            "oop.inherits(DocCommentHighlightRules, TextHighlightRules);\r\n"+
            "\r\n"+
            "DocCommentHighlightRules.getTagRule = function(start) {\r\n"+
            "    return {\r\n"+
            "        token : \"comment.doc.tag.storage.type\",\r\n"+
            "        regex : \"\\\\b(?:TODO|FIXME|XXX|HACK)\\\\b\"\r\n"+
            "    };\r\n"+
            "}\r\n"+
            "\r\n"+
            "DocCommentHighlightRules.getStartRule = function(start) {\r\n"+
            "    return {\r\n"+
            "        token : \"comment.doc\", // doc comment\r\n"+
            "        regex : \"\\\\/\\\\*(?=\\\\*)\",\r\n"+
            "        next  : start\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "\r\n"+
            "DocCommentHighlightRules.getEndRule = function (start) {\r\n"+
            "    return {\r\n"+
            "        token : \"comment.doc\", // closing comment\r\n"+
            "        regex : \"\\\\*\\\\/\",\r\n"+
            "        next  : start\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "exports.DocCommentHighlightRules = DocCommentHighlightRules;\r\n"+
            "\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/amiscript_highlight_rules\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/doc_comment_highlight_rules\",\"ace/mode/text_highlight_rules\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../lib/oop\");\r\n"+
            "var DocCommentHighlightRules = require(\"./doc_comment_highlight_rules\").DocCommentHighlightRules;\r\n"+
            "var TextHighlightRules = require(\"./text_highlight_rules\").TextHighlightRules;\r\n"+
            "var identifierRe = \"[a-zA-Z\\\\$_\\u00a1-\\uffff][a-zA-Z\\\\d\\\\$_\\u00a1-\\uffff]*\";\r\n"+
            "\r\n"+
            "var AmiScriptHighlightRules = function(options) {\r\n"+
            "    var keywordMapper = this.createKeywordMapper({\r\n"+
            "    	\"variable.language\":\r\n"+
            "    		\"this\"\r\n"+
            "    		,\r\n"+
            "        \"variable.language.ami\":\r\n"+
            "            \"session|tableset|layout\"\r\n"+
            "            ,\r\n"+
            "        \"keyword.control\":\r\n"+
            "            \"break|case|catch|continue|switch|do|else|finally|for|\" +\r\n"+
            "            \"if|in|instanceof|new|return|switch|throw|try|while|concurrent|extern|virtual\"\r\n"+
            "            ,\r\n"+
            "        \"keyword.operator\":\r\n"+
            "        	\"and|or|not\" \r\n"+
            "        	,\r\n"+
            "        \"keyword.sql\":\r\n"+
            "        	\"select|insert|into|update|delete|from|where|group|by|order|limit|offset|having|as|case|\"+\r\n"+
            "	        \"when|end|type|left|right|join|only|on|outer|desc|asc|union|create|table|primary|key|\" +\r\n"+
            "	        \"foreign|references|default|inner|cross|natural|database|drop|grant|values|alter|\"+\r\n"+
            "	        \"analyze|prepare|step|add|rename|modify|to|in|use|window|partition|unpack|truncate|byname\"\r\n"+
            "	        ,\r\n"+
            "	    \"keyword.class\":\r\n"+
            "            \"FormField|FormSelectField|Session|FileSystem|Panel|Relationship|Table\"+\r\n"+
            "            \"HtmlPanel|FormPanel|TablePanel|Datamodel|Row|List|Map|Set|TableSet\"\r\n"+
            "	    	,\r\n"+
            "	    \"storage.type.sql\":\r\n"+
            "	    	\"numeric|decimal|date|varchar|char|bigint|bit|binary|text|set|timestamp|number\"\r\n"+
            "	    	,\r\n"+
            "        \"storage.type\":\r\n"+
            "            \"function|\"+\r\n"+
            "            \"int|float|double|integer|long|\"+\r\n"+
            "            \"String|Integer|Double|Float|Boolean\"\r\n"+
            "            ,\r\n"+
            "        \"constant.language\":\r\n"+
            "            \"null\"\r\n"+
            "            ,\r\n"+
            "        \"constant.language.boolean\": \"true|false\"\r\n"+
            "    }, \"identifier\", true);\r\n"+
            "    var kwBeforeRe = \"case|do|else|finally|in|instanceof|return|throw|try|typeof|yield|void\";\r\n"+
            "\r\n"+
            "    var escapedRe = \"\\\\\\\\(?:x[0-9a-fA-F]{2}|\" + // hex\r\n"+
            "        \"u[0-9a-fA-F]{4}|\" + // unicode\r\n"+
            "        \"u{[0-9a-fA-F]{1,6}}|\" + // es6 unicode\r\n"+
            "        \"[0-2][0-7]{0,2}|\" + // oct\r\n"+
            "        \"3[0-7][0-7]?|\" + // oct\r\n"+
            "        \"[4-7][0-7]?|\" + //oct\r\n"+
            "        \".)\";\r\n"+
            "\r\n"+
            "    this.$rules = {\r\n"+
            "    	\"amisql_execute\":[\r\n"+
            "    	             startStringTemplate(\"ami.execute.script\", \"amisql_execute\"),\r\n"+
            "    	             {\r\n"+
            "               	    	 token:\"ami.execute.script\",\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)(\\\\\\\\.)\"\r\n"+
            "    	             },\r\n"+
            "    	             {\r\n"+
            "               	    	 token:\"ami.execute.script\",\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)$\"\r\n"+
            "    	             },\r\n"+
            "    	             {\r\n"+
            "               	    	 token:[\"ami.execute.script\",\"punctuation.operator\"],\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)(;)\",\r\n"+
            "    	            	 next:\"start\"\r\n"+
            "    	             }\r\n"+
            "    	             ],    \r\n"+
            "    	\"amisql_extern\":[\r\n"+
            "    	             startStringTemplate(\"ami.extern.script\", \"amisql_extern\"),\r\n"+
            "    	             {\r\n"+
            "               	    	 token:\"ami.extern.script\",\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)(\\\\\\\\.)\"\r\n"+
            "    	             },\r\n"+
            "    	             {\r\n"+
            "               	    	 token:\"ami.extern.script\",\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)$\"\r\n"+
            "    	             },\r\n"+
            "    	             {\r\n"+
            "               	    	 token:[\"ami.extern.script\",\"punctuation.operator\",\"punctuation.operator\"],\r\n"+
            "               	    	 regex:\"([^\\\\\\\\;]*)(}})(;)\",\r\n"+
            "    	            	 next:\"start\"\r\n"+
            "    	             }\r\n"+
            "    	                 ],\r\n"+
            "        \"no_regex\" : [\r\n"+
            "            DocCommentHighlightRules.getStartRule(\"doc-start\"),\r\n"+
            "            comments(\"no_regex\"),{\r\n"+
            "              	token: \"ami.keyword.execute\",\r\n"+
            "               	regex: /(execute)\\b/,\r\n"+
            "               	caseInsensitive:true,\r\n"+
            "               	next:\"amisql_execute\"\r\n"+
            "            }, {\r\n"+
            "              	token: [\"ami.keyword.extern\", \"ami.extern_keyword\", \"punctuation.operator\"],\r\n"+
            "               	regex: /(extern)([^{]*)({{)/,\r\n"+
            "               	caseInsensitive:true,\r\n"+
            "               	next:\"amisql_extern\"\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : \"'(?=.)\",\r\n"+
            "                next  : \"qstring\"\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : '\"(?=.)',\r\n"+
            "                next  : \"qqstring\"\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.numeric\", // hex\r\n"+
            "                regex : /0(?:[xX][0-9a-fA-F]+|[bB][01]+)\\b/\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.numeric\", // float\r\n"+
            "                regex : /[+-]?\\d[\\d_]*(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b/\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"storage.type\", \"punctuation.operator\", \"support.function\",\r\n"+
            "                    \"punctuation.operator\", \"entity.name.function\", \"text\",\"keyword.operator\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe + \")(\\\\.)(prototype)(\\\\.)(\" + identifierRe +\")(\\\\s*)(=)\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"storage.type\", \"punctuation.operator\", \"entity.name.function\", \"text\",\r\n"+
            "                    \"keyword.operator\", \"text\", \"storage.type\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe + \")(\\\\.)(\" + identifierRe +\")(\\\\s*)(=)(\\\\s*)(function)(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"entity.name.function\", \"text\", \"keyword.operator\", \"text\", \"storage.type\",\r\n"+
            "                    \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe +\")(\\\\s*)(=)(\\\\s*)(function)(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"storage.type\", \"punctuation.operator\", \"entity.name.function\", \"text\",\r\n"+
            "                    \"keyword.operator\", \"text\",\r\n"+
            "                    \"storage.type\", \"text\", \"entity.name.function\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe + \")(\\\\.)(\" + identifierRe +\")(\\\\s*)(=)(\\\\s*)(function)(\\\\s+)(\\\\w+)(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"storage.type\", \"text\", \"entity.name.function\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(function)(\\\\s+)(\" + identifierRe + \")(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"entity.name.function\", \"text\", \"punctuation.operator\",\r\n"+
            "                    \"text\", \"storage.type\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe + \")(\\\\s*)(:)(\\\\s*)(function)(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"text\", \"text\", \"storage.type\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(:)(\\\\s*)(function)(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : \"keyword\",\r\n"+
            "                regex : \"(?:\" + kwBeforeRe + \")\\\\b\",\r\n"+
            "                next : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token : [\"support.constant\"],\r\n"+
            "                regex : /that\\b/\r\n"+
            "            }, {\r\n"+
            "                token : [\"storage.type\", \"punctuation.operator\", \"support.function.firebug\"],\r\n"+
            "                regex : /(console)(\\.)(warn|info|log|error|time|trace|timeEnd|assert)\\b/\r\n"+
            "            }, {\r\n"+
            "                token : keywordMapper,\r\n"+
            "                regex : identifierRe\r\n"+
            "            }, {\r\n"+
            "                token : \"punctuation.operator\",\r\n"+
            "                regex : /[.](?![.])/,\r\n"+
            "                next  : \"property\"\r\n"+
            "            }, {\r\n"+
            "                token : \"keyword.operator\",\r\n"+
            "                regex : /--|\\+\\+|\\.{3}|===|==|=|!=|!==|<+=?|>+=?|!|&&|\\|\\||\\?\\:|[!$%&*+\\-~\\/^]=?/,\r\n"+
            "                next  : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token : \"punctuation.operator\",\r\n"+
            "                regex : /[?:,;.]/,\r\n"+
            "                next  : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token : \"paren.lparen\",\r\n"+
            "                regex : /[\\[({]/,\r\n"+
            "                next  : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token : \"paren.rparen\",\r\n"+
            "                regex : /[\\])}]/\r\n"+
            "            }, {\r\n"+
            "                token: \"comment\",\r\n"+
            "                regex: /^#!.*$/\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        property: [{\r\n"+
            "                token : \"text\",\r\n"+
            "                ");
          out.print(
            "regex : \"\\\\s+\"\r\n"+
            "            }, {\r\n"+
            "                token : [\r\n"+
            "                    \"storage.type\", \"punctuation.operator\", \"entity.name.function\", \"text\",\r\n"+
            "                    \"keyword.operator\", \"text\",\r\n"+
            "                    \"storage.type\", \"text\", \"entity.name.function\", \"text\", \"paren.lparen\"\r\n"+
            "                ],\r\n"+
            "                regex : \"(\" + identifierRe + \")(\\\\.)(\" + identifierRe +\")(\\\\s*)(=)(\\\\s*)(function)(?:(\\\\s+)(\\\\w+))?(\\\\s*)(\\\\()\",\r\n"+
            "                next: \"function_arguments\"\r\n"+
            "            }, {\r\n"+
            "                token : \"punctuation.operator\",\r\n"+
            "                regex : /[.](?![.])/\r\n"+
            "            }, {\r\n"+
            "                token : \"support.function\",\r\n"+
            "                regex : /(s(?:h(?:ift|ow(?:Mod(?:elessDialog|alDialog)|Help))|croll(?:X|By(?:Pages|Lines)?|Y|To)?|t(?:op|rike)|i(?:n|zeToContent|debar|gnText)|ort|u(?:p|b(?:str(?:ing)?)?)|pli(?:ce|t)|e(?:nd|t(?:Re(?:sizable|questHeader)|M(?:i(?:nutes|lliseconds)|onth)|Seconds|Ho(?:tKeys|urs)|Year|Cursor|Time(?:out)?|Interval|ZOptions|Date|UTC(?:M(?:i(?:nutes|lliseconds)|onth)|Seconds|Hours|Date|FullYear)|FullYear|Active)|arch)|qrt|lice|avePreferences|mall)|h(?:ome|andleEvent)|navigate|c(?:har(?:CodeAt|At)|o(?:s|n(?:cat|textual|firm)|mpile)|eil|lear(?:Timeout|Interval)?|a(?:ptureEvents|ll)|reate(?:StyleSheet|Popup|EventObject))|t(?:o(?:GMTString|S(?:tring|ource)|U(?:TCString|pperCase)|Lo(?:caleString|werCase))|est|a(?:n|int(?:Enabled)?))|i(?:s(?:NaN|Finite)|ndexOf|talics)|d(?:isableExternalCapture|ump|etachEvent)|u(?:n(?:shift|taint|escape|watch)|pdateCommands)|j(?:oin|avaEnabled)|p(?:o(?:p|w)|ush|lugins.refresh|a(?:ddings|rse(?:Int|Float)?)|r(?:int|ompt|eference))|e(?:scape|nableExternalCapture|val|lementFromPoint|x(?:p|ec(?:Script|Command)?))|valueOf|UTC|queryCommand(?:State|Indeterm|Enabled|Value)|f(?:i(?:nd|le(?:ModifiedDate|Size|CreatedDate|UpdatedDate)|xed)|o(?:nt(?:size|color)|rward)|loor|romCharCode)|watch|l(?:ink|o(?:ad|g)|astIndexOf)|a(?:sin|nchor|cos|t(?:tachEvent|ob|an(?:2)?)|pply|lert|b(?:s|ort))|r(?:ou(?:nd|teEvents)|e(?:size(?:By|To)|calc|turnValue|place|verse|l(?:oad|ease(?:Capture|Events)))|andom)|g(?:o|et(?:ResponseHeader|M(?:i(?:nutes|lliseconds)|onth)|Se(?:conds|lection)|Hours|Year|Time(?:zoneOffset)?|Da(?:y|te)|UTC(?:M(?:i(?:nutes|lliseconds)|onth)|Seconds|Hours|Da(?:y|te)|FullYear)|FullYear|A(?:ttention|llResponseHeaders)))|m(?:in|ove(?:B(?:y|elow)|To(?:Absolute)?|Above)|ergeAttributes|a(?:tch|rgins|x))|b(?:toa|ig|o(?:ld|rderWidths)|link|ack))\\b(?=\\()/\r\n"+
            "            }, {\r\n"+
            "                token : \"support.function.dom\",\r\n"+
            "                regex : /(s(?:ub(?:stringData|mit)|plitText|e(?:t(?:NamedItem|Attribute(?:Node)?)|lect))|has(?:ChildNodes|Feature)|namedItem|c(?:l(?:ick|o(?:se|neNode))|reate(?:C(?:omment|DATASection|aption)|T(?:Head|extNode|Foot)|DocumentFragment|ProcessingInstruction|E(?:ntityReference|lement)|Attribute))|tabIndex|i(?:nsert(?:Row|Before|Cell|Data)|tem)|open|delete(?:Row|C(?:ell|aption)|T(?:Head|Foot)|Data)|focus|write(?:ln)?|a(?:dd|ppend(?:Child|Data))|re(?:set|place(?:Child|Data)|move(?:NamedItem|Child|Attribute(?:Node)?)?)|get(?:NamedItem|Element(?:sBy(?:Name|TagName|ClassName)|ById)|Attribute(?:Node)?)|blur)\\b(?=\\()/\r\n"+
            "            }, {\r\n"+
            "                token :  \"support.constant\",\r\n"+
            "                regex : /(s(?:ystemLanguage|cr(?:ipts|ollbars|een(?:X|Y|Top|Left))|t(?:yle(?:Sheets)?|atus(?:Text|bar)?)|ibling(?:Below|Above)|ource|uffixes|e(?:curity(?:Policy)?|l(?:ection|f)))|h(?:istory|ost(?:name)?|as(?:h|Focus))|y|X(?:MLDocument|SLDocument)|n(?:ext|ame(?:space(?:s|URI)|Prop))|M(?:IN_VALUE|AX_VALUE)|c(?:haracterSet|o(?:n(?:structor|trollers)|okieEnabled|lorDepth|mp(?:onents|lete))|urrent|puClass|l(?:i(?:p(?:boardData)?|entInformation)|osed|asses)|alle(?:e|r)|rypto)|t(?:o(?:olbar|p)|ext(?:Transform|Indent|Decoration|Align)|ags)|SQRT(?:1_2|2)|i(?:n(?:ner(?:Height|Width)|put)|ds|gnoreCase)|zIndex|o(?:scpu|n(?:readystatechange|Line)|uter(?:Height|Width)|p(?:sProfile|ener)|ffscreenBuffering)|NEGATIVE_INFINITY|d(?:i(?:splay|alog(?:Height|Top|Width|Left|Arguments)|rectories)|e(?:scription|fault(?:Status|Ch(?:ecked|arset)|View)))|u(?:ser(?:Profile|Language|Agent)|n(?:iqueID|defined)|pdateInterval)|_content|p(?:ixelDepth|ort|ersonalbar|kcs11|l(?:ugins|atform)|a(?:thname|dding(?:Right|Bottom|Top|Left)|rent(?:Window|Layer)?|ge(?:X(?:Offset)?|Y(?:Offset)?))|r(?:o(?:to(?:col|type)|duct(?:Sub)?|mpter)|e(?:vious|fix)))|e(?:n(?:coding|abledPlugin)|x(?:ternal|pando)|mbeds)|v(?:isibility|endor(?:Sub)?|Linkcolor)|URLUnencoded|P(?:I|OSITIVE_INFINITY)|f(?:ilename|o(?:nt(?:Size|Family|Weight)|rmName)|rame(?:s|Element)|gColor)|E|whiteSpace|l(?:i(?:stStyleType|n(?:eHeight|kColor))|o(?:ca(?:tion(?:bar)?|lName)|wsrc)|e(?:ngth|ft(?:Context)?)|a(?:st(?:M(?:odified|atch)|Index|Paren)|yer(?:s|X)|nguage))|a(?:pp(?:MinorVersion|Name|Co(?:deName|re)|Version)|vail(?:Height|Top|Width|Left)|ll|r(?:ity|guments)|Linkcolor|bove)|r(?:ight(?:Context)?|e(?:sponse(?:XML|Text)|adyState))|global|x|m(?:imeTypes|ultiline|enubar|argin(?:Right|Bottom|Top|Left))|L(?:N(?:10|2)|OG(?:10E|2E))|b(?:o(?:ttom|rder(?:Width|RightWidth|BottomWidth|Style|Color|TopWidth|LeftWidth))|ufferDepth|elow|ackground(?:Color|Image)))\\b/\r\n"+
            "            }, {\r\n"+
            "                token : \"identifier\",\r\n"+
            "                regex : identifierRe\r\n"+
            "            }, {\r\n"+
            "                regex: \"\",\r\n"+
            "                token: \"empty\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"start\": [\r\n"+
            "            DocCommentHighlightRules.getStartRule(\"doc-start\"),\r\n"+
            "            comments(\"start\"),\r\n"+
            "            {\r\n"+
            "                token: \"string.regexp\",\r\n"+
            "                regex: \"\\\\/\",\r\n"+
            "                next: \"regex\"\r\n"+
            "            }, {\r\n"+
            "                token : \"text\",\r\n"+
            "                regex : \"\\\\s+|^$\",\r\n"+
            "                next : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token: \"empty\",\r\n"+
            "                regex: \"\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"regex\": [\r\n"+
            "            {\r\n"+
            "                token: \"regexp.keyword.operator\",\r\n"+
            "                regex: \"\\\\\\\\(?:u[\\\\da-fA-F]{4}|x[\\\\da-fA-F]{2}|.)\"\r\n"+
            "            }, {\r\n"+
            "                token: \"string.regexp\",\r\n"+
            "                regex: \"/[sxngimy]*\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }, {\r\n"+
            "                token : \"invalid\",\r\n"+
            "                regex: /\\{\\d+\\b,?\\d*\\}[+*]|[+*$^?][+*]|[$^][?]|\\?{3,}/\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex: /\\(\\?[:=!]|\\)|\\{\\d+\\b,?\\d*\\}|[+*]\\?|[()$^+*?.]/\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.language.delimiter\",\r\n"+
            "                regex: /\\|/\r\n"+
            "            }, {\r\n"+
            "                token: \"constant.language.escape\",\r\n"+
            "                regex: /\\[\\^?/,\r\n"+
            "                next: \"regex_character_class\"\r\n"+
            "            }, {\r\n"+
            "                token: \"empty\",\r\n"+
            "                regex: \"$\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string.regexp\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"regex_character_class\": [\r\n"+
            "            {\r\n"+
            "                token: \"regexp.charclass.keyword.operator\",\r\n"+
            "                regex: \"\\\\\\\\(?:u[\\\\da-fA-F]{4}|x[\\\\da-fA-F]{2}|.)\"\r\n"+
            "            }, {\r\n"+
            "                token: \"constant.language.escape\",\r\n"+
            "                regex: \"]\",\r\n"+
            "                next: \"regex\"\r\n"+
            "            }, {\r\n"+
            "                token: \"constant.language.escape\",\r\n"+
            "                regex: \"-\"\r\n"+
            "            }, {\r\n"+
            "                token: \"empty\",\r\n"+
            "                regex: \"$\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string.regexp.charachterclass\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"function_arguments\": [\r\n"+
            "            {\r\n"+
            "                token: \"variable.parameter\",\r\n"+
            "                regex: identifierRe\r\n"+
            "            }, {\r\n"+
            "                token: \"punctuation.operator\",\r\n"+
            "                regex: \"[, ]+\"\r\n"+
            "            }, {\r\n"+
            "                token: \"punctuation.operator\",\r\n"+
            "                regex: \"$\"\r\n"+
            "            }, {\r\n"+
            "                token: \"empty\",\r\n"+
            "                regex: \"\",\r\n"+
            "                next: \"no_regex\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"qqstring\" : [\r\n"+
            "            startStringTemplate(\"string\", \"qqstring\"),\r\n"+
            "            {\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex : escapedRe\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : \"\\\\\\\\$\",\r\n"+
            "                next  : \"qqstring\"\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : '\"|$',\r\n"+
            "                next  : \"no_regex\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string\"\r\n"+
            "            }\r\n"+
            "        ],\r\n"+
            "        \"qstring\" : [\r\n"+
            "            startStringTemplate(\"string\", \"qstring\"),\r\n"+
            "            {\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex : escapedRe\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : \"\\\\\\\\$\",\r\n"+
            "                next  : \"qstring\"\r\n"+
            "            }, {\r\n"+
            "                token : \"string\",\r\n"+
            "                regex : \"'|$\",\r\n"+
            "                next  : \"no_regex\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string\"\r\n"+
            "            }\r\n"+
            "        ]\r\n"+
            "    };\r\n"+
            "    \r\n"+
            "    \r\n"+
            "    if (!options || !options.noES6) {\r\n"+
            "        this.$rules.no_regex.unshift({\r\n"+
            "            regex: \"[{}]\", onMatch: function(val, state, stack) {\r\n"+
            "                this.next = val == \"{\" ? this.nextState : \"\";\r\n"+
            "                if (val == \"{\" && stack.length) {\r\n"+
            "                    stack.unshift(\"start\", state);\r\n"+
            "                }\r\n"+
            "                else if (val == \"}\" && stack.length) {\r\n"+
            "                    stack.shift();\r\n"+
            "                    this.next = stack.shift();\r\n"+
            "                    if (this.next.indexOf(\"string\") != -1 || this.next.indexOf(\"jsx\") != -1)\r\n"+
            "                        return \"paren.quasi.end\";\r\n"+
            "                }\r\n"+
            "                return val == \"{\" ? \"paren.lparen\" : \"paren.rparen\";\r\n"+
            "            },\r\n"+
            "            nextState: \"start\"\r\n"+
            "        }, {\r\n"+
            "            token : \"string.quasi.start\",\r\n"+
            "            regex : /`/,\r\n"+
            "            push  : [{\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex : escapedRe\r\n"+
            "            }, {\r\n"+
            "             ");
          out.print(
            "   token : \"paren.quasi.start\",\r\n"+
            "                regex : /\\${/,\r\n"+
            "                push  : \"start\"\r\n"+
            "            }, {\r\n"+
            "                token : \"string.quasi.end\",\r\n"+
            "                regex : /`/,\r\n"+
            "                next  : \"pop\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string.quasi\"\r\n"+
            "            }]\r\n"+
            "        });\r\n"+
            "        \r\n"+
            "        if (!options || !options.noJSX)\r\n"+
            "            JSX.call(this);\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    this.embedRules(DocCommentHighlightRules, \"doc-\",\r\n"+
            "        [ DocCommentHighlightRules.getEndRule(\"no_regex\") ]);\r\n"+
            "    \r\n"+
            "    this.normalizeRules();\r\n"+
            "};\r\n"+
            "\r\n"+
            "oop.inherits(AmiScriptHighlightRules, TextHighlightRules);\r\n"+
            "\r\n"+
            "function JSX() {\r\n"+
            "    var tagRegex = identifierRe.replace(\"\\\\d\", \"\\\\d\\\\-\");\r\n"+
            "    var jsxTag = {\r\n"+
            "        onMatch : function(val, state, stack) {\r\n"+
            "            var offset = val.charAt(1) == \"/\" ? 2 : 1;\r\n"+
            "            if (offset == 1) {\r\n"+
            "                if (state != this.nextState)\r\n"+
            "                    stack.unshift(this.next, this.nextState, 0);\r\n"+
            "                else\r\n"+
            "                    stack.unshift(this.next);\r\n"+
            "                stack[2]++;\r\n"+
            "            } else if (offset == 2) {\r\n"+
            "                if (state == this.nextState) {\r\n"+
            "                    stack[1]--;\r\n"+
            "                    if (!stack[1] || stack[1] < 0) {\r\n"+
            "                        stack.shift();\r\n"+
            "                        stack.shift();\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "            return [{\r\n"+
            "                type: \"meta.tag.punctuation.\" + (offset == 1 ? \"\" : \"end-\") + \"tag-open.xml\",\r\n"+
            "                value: val.slice(0, offset)\r\n"+
            "            }, {\r\n"+
            "                type: \"meta.tag.tag-name.xml\",\r\n"+
            "                value: val.substr(offset)\r\n"+
            "            }];\r\n"+
            "        },\r\n"+
            "        regex : \"</?\" + tagRegex + \"\",\r\n"+
            "        next: \"jsxAttributes\",\r\n"+
            "        nextState: \"jsx\"\r\n"+
            "    };\r\n"+
            "    this.$rules.start.unshift(jsxTag);\r\n"+
            "    var jsxJsRule = {\r\n"+
            "        regex: \"{\",\r\n"+
            "        token: \"paren.quasi.start\",\r\n"+
            "        push: \"start\"\r\n"+
            "    };\r\n"+
            "    this.$rules.jsx = [\r\n"+
            "        jsxJsRule,\r\n"+
            "        jsxTag,\r\n"+
            "        {include : \"reference\"},\r\n"+
            "        {defaultToken: \"string\"}\r\n"+
            "    ];\r\n"+
            "    this.$rules.jsxAttributes = [{\r\n"+
            "        token : \"meta.tag.punctuation.tag-close.xml\", \r\n"+
            "        regex : \"/?>\", \r\n"+
            "        onMatch : function(value, currentState, stack) {\r\n"+
            "            if (currentState == stack[0])\r\n"+
            "                stack.shift();\r\n"+
            "            if (value.length == 2) {\r\n"+
            "                if (stack[0] == this.nextState)\r\n"+
            "                    stack[1]--;\r\n"+
            "                if (!stack[1] || stack[1] < 0) {\r\n"+
            "                    stack.splice(0, 2);\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "            this.next = stack[0] || \"start\";\r\n"+
            "            return [{type: this.token, value: value}];\r\n"+
            "        },\r\n"+
            "        nextState: \"jsx\"\r\n"+
            "    }, \r\n"+
            "    jsxJsRule,\r\n"+
            "    comments(\"jsxAttributes\"),\r\n"+
            "    {\r\n"+
            "        token : \"entity.other.attribute-name.xml\",\r\n"+
            "        regex : tagRegex\r\n"+
            "    }, {\r\n"+
            "        token : \"keyword.operator.attribute-equals.xml\",\r\n"+
            "        regex : \"=\"\r\n"+
            "    }, {\r\n"+
            "        token : \"text.tag-whitespace.xml\",\r\n"+
            "        regex : \"\\\\s+\"\r\n"+
            "    }, {\r\n"+
            "        token : \"string.attribute-value.xml\",\r\n"+
            "        regex : \"'\",\r\n"+
            "        stateName : \"jsx_attr_q\",\r\n"+
            "        push : [\r\n"+
            "            {token : \"string.attribute-value.xml\", regex: \"'\", next: \"pop\"},\r\n"+
            "            {include : \"reference\"},\r\n"+
            "            {defaultToken : \"string.attribute-value.xml\"}\r\n"+
            "        ]\r\n"+
            "    }, {\r\n"+
            "        token : \"string.attribute-value.xml\",\r\n"+
            "        regex : '\"',\r\n"+
            "        stateName : \"jsx_attr_qq\",\r\n"+
            "        push : [\r\n"+
            "            {token : \"string.attribute-value.xml\", regex: '\"', next: \"pop\"},\r\n"+
            "            {include : \"reference\"},\r\n"+
            "            {defaultToken : \"string.attribute-value.xml\"}\r\n"+
            "        ]\r\n"+
            "    },\r\n"+
            "    jsxTag\r\n"+
            "    ];\r\n"+
            "    this.$rules.reference = [{\r\n"+
            "        token : \"constant.language.escape.reference.xml\",\r\n"+
            "        regex : \"(?:&#[0-9]+;)|(?:&#x[0-9a-fA-F]+;)|(?:&[a-zA-Z0-9_:\\\\.-]+;)\"\r\n"+
            "    }];\r\n"+
            "}\r\n"+
            "\r\n"+
            "//    	\"amisql_execute\":[\r\n"+
            "//    	             {\r\n"+
            "//               	    	 token:\"ami.execute.script\",\r\n"+
            "//               	    	 regex:\"([^\\\\\\\\;]*)(\\\\\\\\.)\"\r\n"+
            "//    	             },\r\n"+
            "//    	             {\r\n"+
            "//               	    	 token:\"ami.execute.script\",\r\n"+
            "//               	    	 regex:\"([^\\\\\\\\;]*)$\"\r\n"+
            "//    	             },\r\n"+
            "//    	             {\r\n"+
            "//               	    	 token:[\"ami.execute.script\",\"punctuation.operator\"],\r\n"+
            "//               	    	 regex:\"([^\\\\\\\\;]*)(;)\",\r\n"+
            "//    	            	 next:\"start\"\r\n"+
            "//    	             }\r\n"+
            "//    	             ],    \r\n"+
            "//        \"no_regex\" : [\r\n"+
            "//            DocCommentHighlightRules.getStartRule(\"doc-start\"),\r\n"+
            "//            comments(\"no_regex\"),{\r\n"+
            "//              	token: \"ami.keyword.execute\",\r\n"+
            "//               	regex: /(execute)/,\r\n"+
            "//               	caseInsensitive:true,\r\n"+
            "//               	next:\"amisql_execute\"\r\n"+
            "//            },{\r\n"+
            "function startStringTemplate(start, next){\r\n"+
            "	return [\r\n"+
            "	    {\r\n"+
            "	    	token:\"stringTemplate.paren.lparen\",\r\n"+
            "	    	regex:/\\$\\{/,\r\n"+
            "	    	next:[\r\n"+
            "					{\r\n"+
            "				    	token:\"stringTemplate.paren.rparen\",\r\n"+
            "				    	regex:/\\}/,\r\n"+
            "				    	next: next || \"pop\"\r\n"+
            "					},\r\n"+
            "					{defaultToken : \"stringTemplate.tstringTemplate\", caseInsensitive: true}\r\n"+
            "	    	      ]\r\n"+
            "		}\r\n"+
            "	];\r\n"+
            "}\r\n"+
            "function comments(next) {\r\n"+
            "    return [\r\n"+
            "        {\r\n"+
            "            token : \"comment\", // multi line comment\r\n"+
            "            regex : /\\/\\*/,\r\n"+
            "            next: [\r\n"+
            "                DocCommentHighlightRules.getTagRule(),\r\n"+
            "                {token : \"comment\", regex : \"\\\\*\\\\/\", next : next || \"pop\"},\r\n"+
            "                {defaultToken : \"comment\", caseInsensitive: true}\r\n"+
            "            ]\r\n"+
            "        }, {\r\n"+
            "            token : \"comment\",\r\n"+
            "            regex : \"\\\\/\\\\/\",\r\n"+
            "            next: [\r\n"+
            "                DocCommentHighlightRules.getTagRule(),\r\n"+
            "                {token : \"comment\", regex : \"$|^\", next : next || \"pop\"},\r\n"+
            "                {defaultToken : \"comment\", caseInsensitive: true}\r\n"+
            "            ]\r\n"+
            "        }\r\n"+
            "    ];\r\n"+
            "}\r\n"+
            "exports.AmiScriptHighlightRules = AmiScriptHighlightRules;\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/matching_brace_outdent\",[\"require\",\"exports\",\"module\",\"ace/range\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var Range = require(\"../range\").Range;\r\n"+
            "\r\n"+
            "var MatchingBraceOutdent = function() {};\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "\r\n"+
            "    this.checkOutdent = function(line, input) {\r\n"+
            "        if (! /^\\s+$/.test(line))\r\n"+
            "            return false;\r\n"+
            "\r\n"+
            "        return /^\\s*\\}/.test(input);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.autoOutdent = function(doc, row) {\r\n"+
            "        var line = doc.getLine(row);\r\n"+
            "        var match = line.match(/^(\\s*\\})/);\r\n"+
            "\r\n"+
            "        if (!match) return 0;\r\n"+
            "\r\n"+
            "        var column = match[1].length;\r\n"+
            "        var openBracePos = doc.findMatchingBracket({row: row, column: column});\r\n"+
            "\r\n"+
            "        if (!openBracePos || openBracePos.row == row) return 0;\r\n"+
            "\r\n"+
            "        var indent = this.$getIndent(doc.getLine(openBracePos.row));\r\n"+
            "        doc.replace(new Range(row, 0, row, column-1), indent);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.$getIndent = function(line) {\r\n"+
            "        return line.match(/^\\s*/)[0];\r\n"+
            "    };\r\n"+
            "\r\n"+
            "}).call(MatchingBraceOutdent.prototype);\r\n"+
            "\r\n"+
            "exports.MatchingBraceOutdent = MatchingBraceOutdent;\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/behaviour/cstyle\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/behaviour\",\"ace/token_iterator\",\"ace/lib/lang\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../../lib/oop\");\r\n"+
            "var Behaviour = require(\"../behaviour\").Behaviour;\r\n"+
            "var TokenIterator = require(\"../../token_iterator\").TokenIterator;\r\n"+
            "var lang = require(\"../../lib/lang\");\r\n"+
            "\r\n"+
            "var SAFE_INSERT_IN_TOKENS =\r\n"+
            "    [\"text\", \"paren.rparen\", \"punctuation.operator\"];\r\n"+
            "var SAFE_INSERT_BEFORE_TOKENS =\r\n"+
            "    [\"text\", \"paren.rparen\", \"punctuation.operator\", \"comment\"];\r\n"+
            "\r\n"+
            "var context;\r\n"+
            "var contextCache = {};\r\n"+
            "var initContext = function(editor) {\r\n"+
            "    var id = -1;\r\n"+
            "    if (editor.multiSelect) {\r\n"+
            "        id = editor.selection.index;\r\n"+
            "        if (contextCache.rangeCount != editor.multiSelect.rangeCount)\r\n"+
            "            contextCache = {rangeCount: editor.multiSelect.rangeCount};\r\n"+
            "    }\r\n"+
            "    if (contextCache[id])\r\n"+
            "        return context = contextCache[id];\r\n"+
            "    context = contextCache[id] = {\r\n"+
            "        autoInsertedBrackets: 0,\r\n"+
            "        autoInsertedRow: -1,\r\n"+
            "        autoInsertedLineEnd: \"\",\r\n"+
            "        maybeInsertedBrackets: 0,\r\n"+
            "        maybeInsertedRow: -1,\r\n"+
            "        maybeInsertedLineStart: \"\",\r\n"+
            "        maybeInsertedLineEnd: \"\"\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "\r\n"+
            "var getWrapped = function(selection, selected, opening, closing) {\r\n"+
            "    var rowDiff = selection.end.row - selection.start.row;\r\n"+
            "    return {\r\n"+
            "        text: opening + selected + closing,\r\n"+
            "        selection: [\r\n"+
            "                0,\r\n"+
            "                selection.start.column + 1,\r\n"+
            "                rowDiff,\r\n"+
            "                selection.end.column + (rowDiff ? 0 : 1)\r\n"+
            "            ]\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "\r\n"+
            "var CstyleBehaviour = function() {\r\n"+
            "    this.add(\"braces\", \"insertion\", function(state, action, editor, session, text) {\r\n"+
            "        var cursor = editor.getCursorPosition();\r\n"+
            "        var line = session.doc.getLine(cursor.row);\r\n"+
            "        if (text == '{') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var selection = editor.getSelectionRange();\r\n"+
            "            var selected = session.doc.getTextRange(selection);\r\n"+
            "            if (selected !== \"\" && selected !== \"{\" && editor.getWrapBehavioursEnabled()) {\r\n"+
            "                return getWrapped(selection, selected, '{', '}');\r\n"+
            "            } else if (CstyleBehaviour.isSaneInsertion(editor, session)) {\r\n"+
            "                if (/[\\]\\}\\)]/.test(line[cursor.column]) || editor.inMultiSelectMode) {\r\n"+
            "                    CstyleBehaviour.recordAutoInsert(editor, session, \"}\");\r\n"+
            "                    return {\r\n"+
            "                        text: '{}',\r\n"+
            "                        selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                } else {\r\n"+
            "                    CstyleBehaviour.recordMaybeInsert(editor, session, \"{\");\r\n"+
            "                    return {\r\n"+
            "                        text: '{',\r\n"+
            "                        selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        } else if (text == '}') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "          ");
          out.print(
            "  if (rightChar == '}') {\r\n"+
            "                var matching = session.$findOpeningBracket('}', {column: cursor.column + 1, row: cursor.row});\r\n"+
            "                if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {\r\n"+
            "                    CstyleBehaviour.popAutoInsertedClosing();\r\n"+
            "                    return {\r\n"+
            "                        text: '',\r\n"+
            "                        selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        } else if (text == \"\\n\" || text == \"\\r\\n\") {\r\n"+
            "            initContext(editor);\r\n"+
            "            var closing = \"\";\r\n"+
            "            if (CstyleBehaviour.isMaybeInsertedClosing(cursor, line)) {\r\n"+
            "                closing = lang.stringRepeat(\"}\", context.maybeInsertedBrackets);\r\n"+
            "                CstyleBehaviour.clearMaybeInsertedClosing();\r\n"+
            "            }\r\n"+
            "            var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "            if (rightChar === '}') {\r\n"+
            "                var openBracePos = session.findMatchingBracket({row: cursor.row, column: cursor.column+1}, '}');\r\n"+
            "                if (!openBracePos)\r\n"+
            "                     return null;\r\n"+
            "                var next_indent = this.$getIndent(session.getLine(openBracePos.row));\r\n"+
            "            } else if (closing) {\r\n"+
            "                var next_indent = this.$getIndent(line);\r\n"+
            "            } else {\r\n"+
            "                CstyleBehaviour.clearMaybeInsertedClosing();\r\n"+
            "                return;\r\n"+
            "            }\r\n"+
            "            var indent = next_indent + session.getTabString();\r\n"+
            "\r\n"+
            "            return {\r\n"+
            "                text: '\\n' + indent + '\\n' + next_indent + closing,\r\n"+
            "                selection: [1, indent.length, 1, indent.length]\r\n"+
            "            };\r\n"+
            "        } else {\r\n"+
            "            CstyleBehaviour.clearMaybeInsertedClosing();\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"braces\", \"deletion\", function(state, action, editor, session, range) {\r\n"+
            "        var selected = session.doc.getTextRange(range);\r\n"+
            "        if (!range.isMultiLine() && selected == '{') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var line = session.doc.getLine(range.start.row);\r\n"+
            "            var rightChar = line.substring(range.end.column, range.end.column + 1);\r\n"+
            "            if (rightChar == '}') {\r\n"+
            "                range.end.column++;\r\n"+
            "                return range;\r\n"+
            "            } else {\r\n"+
            "                context.maybeInsertedBrackets--;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"parens\", \"insertion\", function(state, action, editor, session, text) {\r\n"+
            "        if (text == '(') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var selection = editor.getSelectionRange();\r\n"+
            "            var selected = session.doc.getTextRange(selection);\r\n"+
            "            if (selected !== \"\" && editor.getWrapBehavioursEnabled()) {\r\n"+
            "                return getWrapped(selection, selected, '(', ')');\r\n"+
            "            } else if (CstyleBehaviour.isSaneInsertion(editor, session)) {\r\n"+
            "                CstyleBehaviour.recordAutoInsert(editor, session, \")\");\r\n"+
            "                return {\r\n"+
            "                    text: '()',\r\n"+
            "                    selection: [1, 1]\r\n"+
            "                };\r\n"+
            "            }\r\n"+
            "        } else if (text == ')') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var line = session.doc.getLine(cursor.row);\r\n"+
            "            var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "            if (rightChar == ')') {\r\n"+
            "                var matching = session.$findOpeningBracket(')', {column: cursor.column + 1, row: cursor.row});\r\n"+
            "                if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {\r\n"+
            "                    CstyleBehaviour.popAutoInsertedClosing();\r\n"+
            "                    return {\r\n"+
            "                        text: '',\r\n"+
            "                        selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"parens\", \"deletion\", function(state, action, editor, session, range) {\r\n"+
            "        var selected = session.doc.getTextRange(range);\r\n"+
            "        if (!range.isMultiLine() && selected == '(') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var line = session.doc.getLine(range.start.row);\r\n"+
            "            var rightChar = line.substring(range.start.column + 1, range.start.column + 2);\r\n"+
            "            if (rightChar == ')') {\r\n"+
            "                range.end.column++;\r\n"+
            "                return range;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"brackets\", \"insertion\", function(state, action, editor, session, text) {\r\n"+
            "        if (text == '[') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var selection = editor.getSelectionRange();\r\n"+
            "            var selected = session.doc.getTextRange(selection);\r\n"+
            "            if (selected !== \"\" && editor.getWrapBehavioursEnabled()) {\r\n"+
            "                return getWrapped(selection, selected, '[', ']');\r\n"+
            "            } else if (CstyleBehaviour.isSaneInsertion(editor, session)) {\r\n"+
            "                CstyleBehaviour.recordAutoInsert(editor, session, \"]\");\r\n"+
            "                return {\r\n"+
            "                    text: '[]',\r\n"+
            "                    selection: [1, 1]\r\n"+
            "                };\r\n"+
            "            }\r\n"+
            "        } else if (text == ']') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var line = session.doc.getLine(cursor.row);\r\n"+
            "            var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "            if (rightChar == ']') {\r\n"+
            "                var matching = session.$findOpeningBracket(']', {column: cursor.column + 1, row: cursor.row});\r\n"+
            "                if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {\r\n"+
            "                    CstyleBehaviour.popAutoInsertedClosing();\r\n"+
            "                    return {\r\n"+
            "                        text: '',\r\n"+
            "                        selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"brackets\", \"deletion\", function(state, action, editor, session, range) {\r\n"+
            "        var selected = session.doc.getTextRange(range);\r\n"+
            "        if (!range.isMultiLine() && selected == '[') {\r\n"+
            "            initContext(editor);\r\n"+
            "            var line = session.doc.getLine(range.start.row);\r\n"+
            "            var rightChar = line.substring(range.start.column + 1, range.start.column + 2);\r\n"+
            "            if (rightChar == ']') {\r\n"+
            "                range.end.column++;\r\n"+
            "                return range;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"string_dquotes\", \"insertion\", function(state, action, editor, session, text) {\r\n"+
            "        if (text == '\"' || text == \"'\") {\r\n"+
            "            initContext(editor);\r\n"+
            "            var quote = text;\r\n"+
            "            var selection = editor.getSelectionRange();\r\n"+
            "            var selected = session.doc.getTextRange(selection);\r\n"+
            "            if (selected !== \"\" && selected !== \"'\" && selected != '\"' && editor.getWrapBehavioursEnabled()) {\r\n"+
            "                return getWrapped(selection, selected, quote, quote);\r\n"+
            "            } else if (!selected) {\r\n"+
            "                var cursor = editor.getCursorPosition();\r\n"+
            "                var line = session.doc.getLine(cursor.row);\r\n"+
            "                var leftChar = line.substring(cursor.column-1, cursor.column);\r\n"+
            "                var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "                \r\n"+
            "                var token = session.getTokenAt(cursor.row, cursor.column);\r\n"+
            "                var rightToken = session.getTokenAt(cursor.row, cursor.column + 1);\r\n"+
            "                if (leftChar == \"\\\\\" && token && /escape/.test(token.type))\r\n"+
            "                    return null;\r\n"+
            "                \r\n"+
            "                var stringBefore = token && /string|escape/.test(token.type);\r\n"+
            "                var stringAfter = !rightToken || /string|escape/.test(rightToken.type);\r\n"+
            "                \r\n"+
            "                var pair;\r\n"+
            "                if (rightChar == quote) {\r\n"+
            "                    pair = stringBefore !== stringAfter;\r\n"+
            "                } else {\r\n"+
            "                    if (stringBefore && !stringAfter)\r\n"+
            "                        return null; // wrap string with different quote\r\n"+
            "                    if (stringBefore && stringAfter)\r\n"+
            "                        return null; // do not pair quotes inside strings\r\n"+
            "                    var wordRe = session.$mode.tokenRe;\r\n"+
            "                    wordRe.lastIndex = 0;\r\n"+
            "                    var isWordBefore = wordRe.test(leftChar);\r\n"+
            "                    wordRe.lastIndex = 0;\r\n"+
            "                    var isWordAfter = wordRe.test(leftChar);\r\n"+
            "                    if (isWordBefore || isWordAfter)\r\n"+
            "                        return null; // before or after alphanumeric\r\n"+
            "                    if (rightChar && !/[\\s;,.})\\]\\\\]/.test(rightChar))\r\n"+
            "                        return null; // there is rightChar and it isn't closing\r\n"+
            "                    pair = true;\r\n"+
            "                }\r\n"+
            "                return {\r\n"+
            "                    text: pair ? quote + quote : \"\",\r\n"+
            "                    selection: [1,1]\r\n"+
            "                };\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"string_dquotes\", \"deletion\", function(state, action, editor, session, range) {\r\n"+
            "        var selected = session.doc.getTextRange(range);\r\n"+
            "        if (!range.isMultiLine() && (selected == '\"' || selected == \"'\")) {\r\n"+
            "            initContext(editor);\r\n"+
            "            var line = session.doc.getLine(range.start.row);\r\n"+
            "            var rightChar = line.substring(range.start.column + 1, range.start.column + 2);\r\n"+
            "            if (rightChar == selected) {\r\n"+
            "                range.end.column++;\r\n"+
            "                return range;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "    \r\n"+
            "CstyleBehaviour.isSaneInsertion = function(editor, session) {\r\n"+
            "    var cursor = editor.getCursorPosition();\r\n"+
            "    var iterator = new TokenIterator(session, cursor.row, cursor.column);\r\n"+
            "    if (!this.$matchTokenType(iterator.getCurrentToken() || \"text\", SAFE_INSERT_IN_TOKENS)) {\r\n"+
            "        var iterator2 = new TokenIterator(session, cursor.row, cursor.column + 1);\r\n"+
            "        if (!this.$matchTokenType(iterator2.getCurrentToken() || \"text\", SAFE_INSERT_IN_TOKENS))\r\n"+
            "            return false;\r\n"+
            "    }\r\n"+
            "    iterator.stepForward();\r\n"+
            "    return iterator.getCurrentTokenRow() !== cursor.row ||\r\n"+
            "     ");
          out.print(
            "   this.$matchTokenType(iterator.getCurrentToken() || \"text\", SAFE_INSERT_BEFORE_TOKENS);\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.$matchTokenType = function(token, types) {\r\n"+
            "    return types.indexOf(token.type || token) > -1;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.recordAutoInsert = function(editor, session, bracket) {\r\n"+
            "    var cursor = editor.getCursorPosition();\r\n"+
            "    var line = session.doc.getLine(cursor.row);\r\n"+
            "    if (!this.isAutoInsertedClosing(cursor, line, context.autoInsertedLineEnd[0]))\r\n"+
            "        context.autoInsertedBrackets = 0;\r\n"+
            "    context.autoInsertedRow = cursor.row;\r\n"+
            "    context.autoInsertedLineEnd = bracket + line.substr(cursor.column);\r\n"+
            "    context.autoInsertedBrackets++;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.recordMaybeInsert = function(editor, session, bracket) {\r\n"+
            "    var cursor = editor.getCursorPosition();\r\n"+
            "    var line = session.doc.getLine(cursor.row);\r\n"+
            "    if (!this.isMaybeInsertedClosing(cursor, line))\r\n"+
            "        context.maybeInsertedBrackets = 0;\r\n"+
            "    context.maybeInsertedRow = cursor.row;\r\n"+
            "    context.maybeInsertedLineStart = line.substr(0, cursor.column) + bracket;\r\n"+
            "    context.maybeInsertedLineEnd = line.substr(cursor.column);\r\n"+
            "    context.maybeInsertedBrackets++;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.isAutoInsertedClosing = function(cursor, line, bracket) {\r\n"+
            "    return context.autoInsertedBrackets > 0 &&\r\n"+
            "        cursor.row === context.autoInsertedRow &&\r\n"+
            "        bracket === context.autoInsertedLineEnd[0] &&\r\n"+
            "        line.substr(cursor.column) === context.autoInsertedLineEnd;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.isMaybeInsertedClosing = function(cursor, line) {\r\n"+
            "    return context.maybeInsertedBrackets > 0 &&\r\n"+
            "        cursor.row === context.maybeInsertedRow &&\r\n"+
            "        line.substr(cursor.column) === context.maybeInsertedLineEnd &&\r\n"+
            "        line.substr(0, cursor.column) == context.maybeInsertedLineStart;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.popAutoInsertedClosing = function() {\r\n"+
            "    context.autoInsertedLineEnd = context.autoInsertedLineEnd.substr(1);\r\n"+
            "    context.autoInsertedBrackets--;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CstyleBehaviour.clearMaybeInsertedClosing = function() {\r\n"+
            "    if (context) {\r\n"+
            "        context.maybeInsertedBrackets = 0;\r\n"+
            "        context.maybeInsertedRow = -1;\r\n"+
            "    }\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "oop.inherits(CstyleBehaviour, Behaviour);\r\n"+
            "\r\n"+
            "exports.CstyleBehaviour = CstyleBehaviour;\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/folding/cstyle\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/range\",\"ace/mode/folding/fold_mode\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../../lib/oop\");\r\n"+
            "var Range = require(\"../../range\").Range;\r\n"+
            "var BaseFoldMode = require(\"./fold_mode\").FoldMode;\r\n"+
            "\r\n"+
            "var FoldMode = exports.FoldMode = function(commentRegex) {\r\n"+
            "    if (commentRegex) {\r\n"+
            "        this.foldingStartMarker = new RegExp(\r\n"+
            "            this.foldingStartMarker.source.replace(/\\|[^|]*?$/, \"|\" + commentRegex.start)\r\n"+
            "        );\r\n"+
            "        this.foldingStopMarker = new RegExp(\r\n"+
            "            this.foldingStopMarker.source.replace(/\\|[^|]*?$/, \"|\" + commentRegex.end)\r\n"+
            "        );\r\n"+
            "    }\r\n"+
            "};\r\n"+
            "oop.inherits(FoldMode, BaseFoldMode);\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "    \r\n"+
            "    this.foldingStartMarker = /(\\{|\\[)[^\\}\\]]*$|^\\s*(\\/\\*)/;\r\n"+
            "    this.foldingStopMarker = /^[^\\[\\{]*(\\}|\\])|^[\\s\\*]*(\\*\\/)/;\r\n"+
            "    this.singleLineBlockCommentRe= /^\\s*(\\/\\*).*\\*\\/\\s*$/;\r\n"+
            "    this.tripleStarBlockCommentRe = /^\\s*(\\/\\*\\*\\*).*\\*\\/\\s*$/;\r\n"+
            "    this.startRegionRe = /^\\s*(\\/\\*|\\/\\/)#?region\\b/;\r\n"+
            "    this._getFoldWidgetBase = this.getFoldWidget;\r\n"+
            "    this.getFoldWidget = function(session, foldStyle, row) {\r\n"+
            "        var line = session.getLine(row);\r\n"+
            "    \r\n"+
            "        if (this.singleLineBlockCommentRe.test(line)) {\r\n"+
            "            if (!this.startRegionRe.test(line) && !this.tripleStarBlockCommentRe.test(line))\r\n"+
            "                return \"\";\r\n"+
            "        }\r\n"+
            "    \r\n"+
            "        var fw = this._getFoldWidgetBase(session, foldStyle, row);\r\n"+
            "    \r\n"+
            "        if (!fw && this.startRegionRe.test(line))\r\n"+
            "            return \"start\"; // lineCommentRegionStart\r\n"+
            "    \r\n"+
            "        return fw;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.getFoldWidgetRange = function(session, foldStyle, row, forceMultiline) {\r\n"+
            "        var line = session.getLine(row);\r\n"+
            "        \r\n"+
            "        if (this.startRegionRe.test(line))\r\n"+
            "            return this.getCommentRegionBlock(session, line, row);\r\n"+
            "        \r\n"+
            "        var match = line.match(this.foldingStartMarker);\r\n"+
            "        if (match) {\r\n"+
            "            var i = match.index;\r\n"+
            "\r\n"+
            "            if (match[1])\r\n"+
            "                return this.openingBracketBlock(session, match[1], row, i);\r\n"+
            "                \r\n"+
            "            var range = session.getCommentFoldRange(row, i + match[0].length, 1);\r\n"+
            "            \r\n"+
            "            if (range && !range.isMultiLine()) {\r\n"+
            "                if (forceMultiline) {\r\n"+
            "                    range = this.getSectionRange(session, row);\r\n"+
            "                } else if (foldStyle != \"all\")\r\n"+
            "                    range = null;\r\n"+
            "            }\r\n"+
            "            \r\n"+
            "            return range;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        if (foldStyle === \"markbegin\")\r\n"+
            "            return;\r\n"+
            "\r\n"+
            "        var match = line.match(this.foldingStopMarker);\r\n"+
            "        if (match) {\r\n"+
            "            var i = match.index + match[0].length;\r\n"+
            "\r\n"+
            "            if (match[1])\r\n"+
            "                return this.closingBracketBlock(session, match[1], row, i);\r\n"+
            "\r\n"+
            "            return session.getCommentFoldRange(row, i, -1);\r\n"+
            "        }\r\n"+
            "    };\r\n"+
            "    \r\n"+
            "    this.getSectionRange = function(session, row) {\r\n"+
            "        var line = session.getLine(row);\r\n"+
            "        var startIndent = line.search(/\\S/);\r\n"+
            "        var startRow = row;\r\n"+
            "        var startColumn = line.length;\r\n"+
            "        row = row + 1;\r\n"+
            "        var endRow = row;\r\n"+
            "        var maxRow = session.getLength();\r\n"+
            "        while (++row < maxRow) {\r\n"+
            "            line = session.getLine(row);\r\n"+
            "            var indent = line.search(/\\S/);\r\n"+
            "            if (indent === -1)\r\n"+
            "                continue;\r\n"+
            "            if  (startIndent > indent)\r\n"+
            "                break;\r\n"+
            "            var subRange = this.getFoldWidgetRange(session, \"all\", row);\r\n"+
            "            \r\n"+
            "            if (subRange) {\r\n"+
            "                if (subRange.start.row <= startRow) {\r\n"+
            "                    break;\r\n"+
            "                } else if (subRange.isMultiLine()) {\r\n"+
            "                    row = subRange.end.row;\r\n"+
            "                } else if (startIndent == indent) {\r\n"+
            "                    break;\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "            endRow = row;\r\n"+
            "        }\r\n"+
            "        \r\n"+
            "        return new Range(startRow, startColumn, endRow, session.getLine(endRow).length);\r\n"+
            "    };\r\n"+
            "    this.getCommentRegionBlock = function(session, line, row) {\r\n"+
            "        var startColumn = line.search(/\\s*$/);\r\n"+
            "        var maxRow = session.getLength();\r\n"+
            "        var startRow = row;\r\n"+
            "        \r\n"+
            "        var re = /^\\s*(?:\\/\\*|\\/\\/|--)#?(end)?region\\b/;\r\n"+
            "        var depth = 1;\r\n"+
            "        while (++row < maxRow) {\r\n"+
            "            line = session.getLine(row);\r\n"+
            "            var m = re.exec(line);\r\n"+
            "            if (!m) continue;\r\n"+
            "            if (m[1]) depth--;\r\n"+
            "            else depth++;\r\n"+
            "\r\n"+
            "            if (!depth) break;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        var endRow = row;\r\n"+
            "        if (endRow > startRow) {\r\n"+
            "            return new Range(startRow, startColumn, endRow, line.length);\r\n"+
            "        }\r\n"+
            "    };\r\n"+
            "\r\n"+
            "}).call(FoldMode.prototype);\r\n"+
            "\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/amiscript\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/text\",\"ace/mode/amiscript_highlight_rules\",\"ace/mode/matching_brace_outdent\",\"ace/range\",\"ace/worker/worker_client\",\"ace/mode/behaviour/cstyle\",\"ace/mode/folding/cstyle\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../lib/oop\");\r\n"+
            "var TextMode = require(\"./text\").Mode;\r\n"+
            "var AmiScriptHighlightRules = require(\"./amiscript_highlight_rules\").AmiScriptHighlightRules;\r\n"+
            "var MatchingBraceOutdent = require(\"./matching_brace_outdent\").MatchingBraceOutdent;\r\n"+
            "var Range = require(\"../range\").Range;\r\n"+
            "var WorkerClient = require(\"../worker/worker_client\").WorkerClient;\r\n"+
            "var CstyleBehaviour = require(\"./behaviour/cstyle\").CstyleBehaviour;\r\n"+
            "var CStyleFoldMode = require(\"./folding/cstyle\").FoldMode;\r\n"+
            "\r\n"+
            "var Mode = function() {\r\n"+
            "    this.HighlightRules = AmiScriptHighlightRules;\r\n"+
            "    \r\n"+
            "    this.$outdent = new MatchingBraceOutdent();\r\n"+
            "    this.$behaviour = new CstyleBehaviour();\r\n"+
            "    this.foldingRules = new CStyleFoldMode();\r\n"+
            "};\r\n"+
            "oop.inherits(Mode, TextMode);\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "\r\n"+
            "    this.lineCommentStart = \"//\";\r\n"+
            "    this.blockComment = {start: \"/*\", end: \"*/\"};\r\n"+
            "\r\n"+
            "    this.getNextLineIndent = function(state, line, tab) {\r\n"+
            "        var indent = this.$getIndent(line);\r\n"+
            "\r\n"+
            "        var tokenizedLine = this.getTokenizer().getLineTokens(line, state);\r\n"+
            "        var tokens = tokenizedLine.tokens;\r\n"+
            "        var endState = tokenizedLine.state;\r\n"+
            "\r\n"+
            "        if (tokens.length && tokens[tokens.length-1].type == \"comment\") {\r\n"+
            "            return indent;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        if (state == \"start\" || state == \"no_regex\") {\r\n"+
            "            var match = line.match(/^.*(?:\\bcase\\b.*\\:|[\\{\\(\\[])\\s*$/);\r\n"+
            "            if (match) {\r\n"+
            "                indent += tab;\r\n"+
            "            }\r\n"+
            "        } else if (state == \"doc-start\") {\r\n"+
            "            if (endState == \"start\" || endState == \"no_regex\") {\r\n"+
            "                return \"\";\r\n"+
            "            }\r\n"+
            "            var match = line.match(/^\\s*(\\/?)\\*/);\r\n"+
            "            if (match) {\r\n"+
            "                if (match[1]) {\r\n"+
            "                    indent += \" \";\r\n"+
            "                }\r\n"+
            "                indent += \"* \";\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        return indent;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.checkOutdent = function(state, line, input) {\r\n"+
            "        return this.$outdent.checkOutdent(line, input);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.autoOutdent = function(state, doc, row) {\r\n"+
            "        this.$outdent.autoOutdent(doc, row);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.createWorker = function(session) {\r\n"+
            "        var worker = new WorkerClient([\"ace\"], \"ace/mode/amiscript_worker\", \"AmiScriptWorker\");\r\n"+
            "        worker.attachToDocument(session.getDocument());\r\n"+
            "\r\n"+
            "        worker.on(\"annotate\", function(results) {\r\n"+
            "            session.setAnnotations(results.data);\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        worker.on(\"terminate\", function() {\r\n"+
            "            session.clearAnnotations();\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        return worker;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.$id = \"ace/mode/amiscript\";\r\n"+
            "}).call(Mode.prototype);\r\n"+
            "\r\n"+
            "exports.Mode = Mode;\r\n"+
            "});\r\n"+
            "");

	}
	
}