package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_mode_css_js_1 extends AbstractHttpHandler{

	public amiweb_mode_css_js_1() {
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
            "ace.define(\"ace/mode/css_highlight_rules\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/lib/lang\",\"ace/mode/text_highlight_rules\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../lib/oop\");\r\n"+
            "var lang = require(\"../lib/lang\");\r\n"+
            "var TextHighlightRules = require(\"./text_highlight_rules\").TextHighlightRules;\r\n"+
            "var supportType = exports.supportType = \"align-content|align-items|align-self|all|animation|animation-delay|animation-direction|animation-duration|animation-fill-mode|animation-iteration-count|animation-name|animation-play-state|animation-timing-function|backface-visibility|background|background-attachment|background-blend-mode|background-clip|background-color|background-image|background-origin|background-position|background-repeat|background-size|border|border-bottom|border-bottom-color|border-bottom-left-radius|border-bottom-right-radius|border-bottom-style|border-bottom-width|border-collapse|border-color|border-image|border-image-outset|border-image-repeat|border-image-slice|border-image-source|border-image-width|border-left|border-left-color|border-left-style|border-left-width|border-radius|border-right|border-right-color|border-right-style|border-right-width|border-spacing|border-style|border-top|border-top-color|border-top-left-radius|border-top-right-radius|border-top-style|border-top-width|border-width|bottom|box-shadow|box-sizing|caption-side|clear|clip|color|column-count|column-fill|column-gap|column-rule|column-rule-color|column-rule-style|column-rule-width|column-span|column-width|columns|content|counter-increment|counter-reset|cursor|direction|display|empty-cells|filter|flex|flex-basis|flex-direction|flex-flow|flex-grow|flex-shrink|flex-wrap|float|font|font-family|font-size|font-size-adjust|font-stretch|font-style|font-variant|font-weight|hanging-punctuation|height|justify-content|left|letter-spacing|line-height|list-style|list-style-image|list-style-position|list-style-type|margin|margin-bottom|margin-left|margin-right|margin-top|max-height|max-width|max-zoom|min-height|min-width|min-zoom|nav-down|nav-index|nav-left|nav-right|nav-up|opacity|order|outline|outline-color|outline-offset|outline-style|outline-width|overflow|overflow-x|overflow-y|padding|padding-bottom|padding-left|padding-right|padding-top|page-break-after|page-break-before|page-break-inside|perspective|perspective-origin|position|quotes|resize|right|tab-size|table-layout|text-align|text-align-last|text-decoration|text-decoration-color|text-decoration-line|text-decoration-style|text-indent|text-justify|text-overflow|text-shadow|text-transform|top|transform|transform-origin|transform-style|transition|transition-delay|transition-duration|transition-property|transition-timing-function|unicode-bidi|user-select|user-zoom|vertical-align|visibility|white-space|width|word-break|word-spacing|word-wrap|z-index|extends-css\";\r\n"+
            "var supportFunction = exports.supportFunction = \"rgb|rgba|url|attr|counter|counters\";\r\n"+
            "var supportConstant = exports.supportConstant = \"absolute|after-edge|after|all-scroll|all|alphabetic|always|antialiased|armenian|auto|avoid-column|avoid-page|avoid|balance|baseline|before-edge|before|below|bidi-override|block-line-height|block|bold|bolder|border-box|both|bottom|box|break-all|break-word|capitalize|caps-height|caption|center|central|char|circle|cjk-ideographic|clone|close-quote|col-resize|collapse|column|consider-shifts|contain|content-box|cover|crosshair|cubic-bezier|dashed|decimal-leading-zero|decimal|default|disabled|disc|disregard-shifts|distribute-all-lines|distribute-letter|distribute-space|distribute|dotted|double|e-resize|ease-in|ease-in-out|ease-out|ease|ellipsis|end|exclude-ruby|fill|fixed|georgian|glyphs|grid-height|groove|hand|hanging|hebrew|help|hidden|hiragana-iroha|hiragana|horizontal|icon|ideograph-alpha|ideograph-numeric|ideograph-parenthesis|ideograph-space|ideographic|inactive|include-ruby|inherit|initial|inline-block|inline-box|inline-line-height|inline-table|inline|inset|inside|inter-ideograph|inter-word|invert|italic|justify|katakana-iroha|katakana|keep-all|last|left|lighter|line-edge|line-through|line|linear|list-item|local|loose|lower-alpha|lower-greek|lower-latin|lower-roman|lowercase|lr-tb|ltr|mathematical|max-height|max-size|medium|menu|message-box|middle|move|n-resize|ne-resize|newspaper|no-change|no-close-quote|no-drop|no-open-quote|no-repeat|none|normal|not-allowed|nowrap|nw-resize|oblique|open-quote|outset|outside|overline|padding-box|page|pointer|pre-line|pre-wrap|pre|preserve-3d|progress|relative|repeat-x|repeat-y|repeat|replaced|reset-size|ridge|right|round|row-resize|rtl|s-resize|scroll|se-resize|separate|slice|small-caps|small-caption|solid|space|square|start|static|status-bar|step-end|step-start|steps|stretch|strict|sub|super|sw-resize|table-caption|table-cell|table-column-group|table-column|table-footer-group|table-header-group|table-row-group|table-row|table|tb-rl|text-after-edge|text-before-edge|text-bottom|text-size|text-top|text|thick|thin|transparent|underline|upper-alpha|upper-latin|upper-roman|uppercase|use-script|vertical-ideographic|vertical-text|visible|w-resize|wait|whitespace|z-index|zero|zoom\";\r\n"+
            "var supportConstantColor = exports.supportConstantColor = \"aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|black|blanchedalmond|blue|blueviolet|brown|burlywood|cadetblue|chartreuse|chocolate|coral|cornflowerblue|cornsilk|crimson|cyan|darkblue|darkcyan|darkgoldenrod|darkgray|darkgreen|darkgrey|darkkhaki|darkmagenta|darkolivegreen|darkorange|darkorchid|darkred|darksalmon|darkseagreen|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|floralwhite|forestgreen|fuchsia|gainsboro|ghostwhite|gold|goldenrod|gray|green|greenyellow|grey|honeydew|hotpink|indianred|indigo|ivory|khaki|lavender|lavenderblush|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightseagreen|lightskyblue|lightslategray|lightslategrey|lightsteelblue|lightyellow|lime|limegreen|linen|magenta|maroon|mediumaquamarine|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumspringgreen|mediumturquoise|mediumvioletred|midnightblue|mintcream|mistyrose|moccasin|navajowhite|navy|oldlace|olive|olivedrab|orange|orangered|orchid|palegoldenrod|palegreen|paleturquoise|palevioletred|papayawhip|peachpuff|peru|pink|plum|powderblue|purple|rebeccapurple|red|rosybrown|royalblue|saddlebrown|salmon|sandybrown|seagreen|seashell|sienna|silver|skyblue|slateblue|slategray|slategrey|snow|springgreen|steelblue|tan|teal|thistle|tomato|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen\";\r\n"+
            "var supportConstantFonts = exports.supportConstantFonts = \"arial|century|comic|courier|cursive|fantasy|garamond|georgia|helvetica|impact|lucida|symbol|system|tahoma|times|trebuchet|utopia|verdana|webdings|sans-serif|serif|monospace\";\r\n"+
            "\r\n"+
            "var numRe = exports.numRe = \"\\\\-?(?:(?:[0-9]+(?:\\\\.[0-9]+)?)|(?:\\\\.[0-9]+))\";\r\n"+
            "var pseudoElements = exports.pseudoElements = \"(\\\\:+)\\\\b(after|before|first-letter|first-line|moz-selection|selection)\\\\b\";\r\n"+
            "var pseudoClasses  = exports.pseudoClasses =  \"(:)\\\\b(active|checked|disabled|empty|enabled|first-child|first-of-type|focus|hover|indeterminate|invalid|last-child|last-of-type|link|not|nth-child|nth-last-child|nth-last-of-type|nth-of-type|only-child|only-of-type|required|root|target|valid|visited)\\\\b\";\r\n"+
            "\r\n"+
            "var CssHighlightRules = function() {\r\n"+
            "\r\n"+
            "    var keywordMapper = this.createKeywordMapper({\r\n"+
            "        \"support.function\": supportFunction,\r\n"+
            "        \"support.constant\": supportConstant,\r\n"+
            "        \"support.type\": supportType,\r\n"+
            "        \"support.constant.color\": supportConstantColor,\r\n"+
            "        \"support.constant.fonts\": supportConstantFonts\r\n"+
            "    }, \"text\", true);\r\n"+
            "\r\n"+
            "    this.$rules = {\r\n"+
            "        \"start\" : [{\r\n"+
            "            include : [\"strings\", \"url\", \"comments\"]\r\n"+
            "        }, {\r\n"+
            "            token: \"paren.lparen\",\r\n"+
            "            regex: \"\\\\{\",\r\n"+
            "            next:  \"ruleset\"\r\n"+
            "        }, {\r\n"+
            "            token: \"paren.rparen\",\r\n"+
            "            regex: \"\\\\}\"\r\n"+
            "        }, {\r\n"+
            "            token: \"string\",\r\n"+
            "            regex: \"@(?!viewport)\",\r\n"+
            "            next:  \"media\"\r\n"+
            "        }, {\r\n"+
            "            token: \"keyword\",\r\n"+
            "            regex: \"#[a-z0-9-_]+\"\r\n"+
            "        }, {\r\n"+
            "            token: \"keyword\",\r\n"+
            "            regex: \"%\"\r\n"+
            "        }, {\r\n"+
            "            token: \"variable\",\r\n"+
            "            regex: \"\\\\.[a-z0-9-_]+\"\r\n"+
            "        }, {\r\n"+
            "            token: \"string\",\r\n"+
            "            regex: \":[a-z0-9-_]+\"\r\n"+
            "        }, {\r\n"+
            "            token : \"constant.numeric\",\r\n"+
            "            regex : numRe\r\n"+
            "        }, {\r\n"+
            "            token: \"constant\",\r\n"+
            "            regex: \"[a-z0-9-_]+\"\r\n"+
            "        }, {\r\n"+
            "            caseInsensitive: true\r\n"+
            "        }],\r\n"+
            "\r\n"+
            "        \"media\": [{\r\n"+
            "            include : [\"strings\", \"url\", \"comments\"]\r\n"+
            "        }, {\r\n"+
            "            token: \"paren.lparen\",\r\n"+
            "            regex: \"\\\\{\",\r\n"+
            "            next:  \"start\"\r\n"+
            "        }, {\r\n"+
            "            token: \"paren.rparen\",\r\n"+
            "            regex: \"\\\\}\",\r\n"+
            "            next:  \"start\"\r\n"+
            "        }, {\r\n"+
            "            token: \"string\",\r\n"+
            "            regex: \";\",\r\n"+
            "            next:  \"start\"\r\n"+
            "        }, {\r\n"+
            "            token: \"keyword\",\r\n"+
            "            regex: \"(?:media|supports|document|charset|import|namespace|media|supports|document\"\r\n"+
            "                + \"|page|font|keyframes|viewport|counter-style|font-feature-values\"\r\n"+
            "                + \"|swash|ornaments|annotation|stylistic|styleset|character-variant)\"\r\n"+
            "        }],\r\n"+
            "\r\n"+
            "        \"comments\" : [{\r\n"+
            "            token: \"comment\", // multi line comment\r\n"+
            "            regex: \"\\\\/\\\\*\",\r\n"+
            "            push: [{\r\n"+
            "                token : \"comment\",\r\n"+
            "                regex : \"\\\\*\\\\/\",\r\n"+
            "                next : \"pop\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken : \"comment\"\r\n"+
            "            }]\r\n"+
            "        }],\r\n"+
            "\r\n"+
            "        \"ruleset\" : [{\r\n"+
            "            regex : \"-(webkit|ms|moz|o)-\",\r\n"+
            "            token : \"text\"\r\n"+
            "        }, {\r\n"+
            "            token : \"punctuation.operator\",\r\n"+
            "            regex : \"[:;]\"\r\n"+
            "        }, {\r\n"+
            "            token : \"paren.rparen\",\r\n"+
            "    ");
          out.print(
            "        regex : \"\\\\}\",\r\n"+
            "            next : \"start\"\r\n"+
            "        }, {\r\n"+
            "            include : [\"strings\", \"url\", \"comments\"]\r\n"+
            "        }, {\r\n"+
            "            token : [\"constant.numeric\", \"keyword\"],\r\n"+
            "            regex : \"(\" + numRe + \")(ch|cm|deg|em|ex|fr|gd|grad|Hz|in|kHz|mm|ms|pc|pt|px|rad|rem|s|turn|vh|vmax|vmin|vm|vw|%)\"\r\n"+
            "        }, {\r\n"+
            "            token : \"constant.numeric\",\r\n"+
            "            regex : numRe\r\n"+
            "        }, {\r\n"+
            "            token : \"constant.numeric\",  // hex6 color\r\n"+
            "            regex : \"#[a-f0-9]{6}\"\r\n"+
            "        }, {\r\n"+
            "            token : \"constant.numeric\", // hex3 color\r\n"+
            "            regex : \"#[a-f0-9]{3}\"\r\n"+
            "        }, {\r\n"+
            "            token : [\"punctuation\", \"entity.other.attribute-name.pseudo-element.css\"],\r\n"+
            "            regex : pseudoElements\r\n"+
            "        }, {\r\n"+
            "            token : [\"punctuation\", \"entity.other.attribute-name.pseudo-class.css\"],\r\n"+
            "            regex : pseudoClasses\r\n"+
            "        }, {\r\n"+
            "            include: \"url\"\r\n"+
            "        }, {\r\n"+
            "            token : keywordMapper,\r\n"+
            "            regex : \"\\\\-?[a-zA-Z_][a-zA-Z0-9_\\\\-]*\"\r\n"+
            "        }, {\r\n"+
            "            caseInsensitive: true\r\n"+
            "        }],\r\n"+
            "\r\n"+
            "        url: [{\r\n"+
            "            token : \"support.function\",\r\n"+
            "            regex : \"(?:url(:?-prefix)?|domain|regexp)\\\\(\",\r\n"+
            "            push: [{\r\n"+
            "                token : \"support.function\",\r\n"+
            "                regex : \"\\\\)\",\r\n"+
            "                next : \"pop\"\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string\"\r\n"+
            "            }]\r\n"+
            "        }],\r\n"+
            "\r\n"+
            "        strings: [{\r\n"+
            "            token : \"string.start\",\r\n"+
            "            regex : \"'\",\r\n"+
            "            push : [{\r\n"+
            "                token : \"string.end\",\r\n"+
            "                regex : \"'|$\",\r\n"+
            "                next: \"pop\"\r\n"+
            "            }, {\r\n"+
            "                include : \"escapes\"\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex : /\\\\$/,\r\n"+
            "                consumeLineEnd: true\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string\"\r\n"+
            "            }]\r\n"+
            "        }, {\r\n"+
            "            token : \"string.start\",\r\n"+
            "            regex : '\"',\r\n"+
            "            push : [{\r\n"+
            "                token : \"string.end\",\r\n"+
            "                regex : '\"|$',\r\n"+
            "                next: \"pop\"\r\n"+
            "            }, {\r\n"+
            "                include : \"escapes\"\r\n"+
            "            }, {\r\n"+
            "                token : \"constant.language.escape\",\r\n"+
            "                regex : /\\\\$/,\r\n"+
            "                consumeLineEnd: true\r\n"+
            "            }, {\r\n"+
            "                defaultToken: \"string\"\r\n"+
            "            }]\r\n"+
            "        }],\r\n"+
            "        escapes: [{\r\n"+
            "            token : \"constant.language.escape\",\r\n"+
            "            regex : /\\\\([a-fA-F\\d]{1,6}|[^a-fA-F\\d])/\r\n"+
            "        }]\r\n"+
            "\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.normalizeRules();\r\n"+
            "};\r\n"+
            "\r\n"+
            "oop.inherits(CssHighlightRules, TextHighlightRules);\r\n"+
            "\r\n"+
            "exports.CssHighlightRules = CssHighlightRules;\r\n"+
            "\r\n"+
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
            "ace.define(\"ace/mode/css_completions\",[\"require\",\"exports\",\"module\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var propertyMap = {\r\n"+
            "    \"background\": {\"#$0\": 1},\r\n"+
            "    \"background-color\": {\"#$0\": 1, \"transparent\": 1, \"fixed\": 1},\r\n"+
            "    \"background-image\": {\"url('/$0')\": 1},\r\n"+
            "    \"background-repeat\": {\"repeat\": 1, \"repeat-x\": 1, \"repeat-y\": 1, \"no-repeat\": 1, \"inherit\": 1},\r\n"+
            "    \"background-position\": {\"bottom\":2, \"center\":2, \"left\":2, \"right\":2, \"top\":2, \"inherit\":2},\r\n"+
            "    \"background-attachment\": {\"scroll\": 1, \"fixed\": 1},\r\n"+
            "    \"background-size\": {\"cover\": 1, \"contain\": 1},\r\n"+
            "    \"background-clip\": {\"border-box\": 1, \"padding-box\": 1, \"content-box\": 1},\r\n"+
            "    \"background-origin\": {\"border-box\": 1, \"padding-box\": 1, \"content-box\": 1},\r\n"+
            "    \"border\": {\"solid $0\": 1, \"dashed $0\": 1, \"dotted $0\": 1, \"#$0\": 1},\r\n"+
            "    \"border-color\": {\"#$0\": 1},\r\n"+
            "    \"border-style\": {\"solid\":2, \"dashed\":2, \"dotted\":2, \"double\":2, \"groove\":2, \"hidden\":2, \"inherit\":2, \"inset\":2, \"none\":2, \"outset\":2, \"ridged\":2},\r\n"+
            "    \"border-collapse\": {\"collapse\": 1, \"separate\": 1},\r\n"+
            "    \"bottom\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"clear\": {\"left\": 1, \"right\": 1, \"both\": 1, \"none\": 1},\r\n"+
            "    \"color\": {\"#$0\": 1, \"rgb(#$00,0,0)\": 1},\r\n"+
            "    \"cursor\": {\"default\": 1, \"pointer\": 1, \"move\": 1, \"text\": 1, \"wait\": 1, \"help\": 1, \"progress\": 1, \"n-resize\": 1, \"ne-resize\": 1, \"e-resize\": 1, \"se-resize\": 1, \"s-resize\": 1, \"sw-resize\": 1, \"w-resize\": 1, \"nw-resize\": 1},\r\n"+
            "    \"display\": {\"none\": 1, \"block\": 1, \"inline\": 1, \"inline-block\": 1, \"table-cell\": 1},\r\n"+
            "    \"empty-cells\": {\"show\": 1, \"hide\": 1},\r\n"+
            "    \"float\": {\"left\": 1, \"right\": 1, \"none\": 1},\r\n"+
            "    \"font-family\": {\"Arial\":2,\"Comic Sans MS\":2,\"Consolas\":2,\"Courier New\":2,\"Courier\":2,\"Georgia\":2,\"Monospace\":2,\"Sans-Serif\":2, \"Segoe UI\":2,\"Tahoma\":2,\"Times New Roman\":2,\"Trebuchet MS\":2,\"Verdana\": 1},\r\n"+
            "    \"font-size\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"font-weight\": {\"bold\": 1, \"normal\": 1},\r\n"+
            "    \"font-style\": {\"italic\": 1, \"normal\": 1},\r\n"+
            "    \"font-variant\": {\"normal\": 1, \"small-caps\": 1},\r\n"+
            "    \"height\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"left\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"letter-spacing\": {\"normal\": 1},\r\n"+
            "    \"line-height\": {\"normal\": 1},\r\n"+
            "    \"list-style-type\": {\"none\": 1, \"disc\": 1, \"circle\": 1, \"square\": 1, \"decimal\": 1, \"decimal-leading-zero\": 1, \"lower-roman\": 1, \"upper-roman\": 1, \"lower-greek\": 1, \"lower-latin\": 1, \"upper-latin\": 1, \"georgian\": 1, \"lower-alpha\": 1, \"upper-alpha\": 1},\r\n"+
            "    \"margin\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"margin-right\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"margin-left\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"margin-top\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"margin-bottom\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"max-height\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"max-width\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"min-height\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"min-width\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"overflow\": {\"hidden\": 1, \"visible\": 1, \"auto\": 1, \"scroll\": 1},\r\n"+
            "    \"overflow-x\": {\"hidden\": 1, \"visible\": 1, \"auto\": 1, \"scroll\": 1},\r\n"+
            "    \"overflow-y\": {\"hidden\": 1, \"visible\": 1, \"auto\": 1, \"scroll\": 1},\r\n"+
            "    \"padding\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"padding-top\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"padding-right\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"padding-bottom\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"padding-left\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"page-break-after\": {\"auto\": 1, \"always\": 1, \"avoid\": 1, \"left\": 1, \"right\": 1},\r\n"+
            "    \"page-break-before\": {\"auto\": 1, \"always\": 1, \"avoid\": 1, \"left\": 1, \"right\": 1},\r\n"+
            "    \"position\": {\"absolute\": 1, \"relative\": 1, \"fixed\": 1, \"static\": 1},\r\n"+
            "    \"right\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"table-layout\": {\"fixed\": 1, \"auto\": 1},\r\n"+
            "    \"text-decoration\": {\"none\": 1, \"underline\": 1, \"line-through\": 1, \"blink\": 1},\r\n"+
            "    \"text-align\": {\"left\": 1, \"right\": 1, \"center\": 1, \"justify\": 1},\r\n"+
            "    \"text-transform\": {\"capitalize\": 1, \"uppercase\": 1, \"lowercase\": 1, \"none\": 1},\r\n"+
            "    \"top\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"vertical-align\": {\"top\": 1, \"bottom\": 1},\r\n"+
            "    \"visibility\": {\"hidden\": 1, \"visible\": 1},\r\n"+
            "    \"white-space\": {\"nowrap\": 1, \"normal\": 1, \"pre\": 1, \"pre-line\": 1, \"pre-wrap\": 1},\r\n"+
            "    \"width\": {\"px\": 1, \"em\": 1, \"%\": 1},\r\n"+
            "    \"word-spacing\": {\"normal\": 1},\r\n"+
            "    \"filter\": {\"alpha(opacity=$0100)\": 1},\r\n"+
            "\r\n"+
            "    \"text-shadow\": {\"$02px 2px 2px #777\": 1},\r\n"+
            "    \"text-overflow\": {\"ellipsis-word\": 1, \"clip\": 1, \"ellipsis\": 1},\r\n"+
            "    \"-moz-border-radius\": 1,\r\n"+
            "    \"-moz-border-radius-topright\": 1,\r\n"+
            "    \"-moz-border-radius-bottomright\": 1,\r\n"+
            "    \"-moz-border-radius-topleft\": 1,\r\n"+
            "    \"-moz-border-radius-bottomleft\": 1,\r\n"+
            "    \"-webkit-border-radius\": 1,\r\n"+
            "    \"-webkit-border-top-right-radius\": 1,\r\n"+
            "    \"-webkit-border-top-left-radius\": 1,\r\n"+
            "    \"-webkit-border-bottom-right-radius\": 1,\r\n"+
            "    \"-webkit-border-bottom-left-radius\": 1,\r\n"+
            "    \"-moz-box-shadow\": 1,\r\n"+
            "    \"-webkit-box-shadow\": 1,\r\n"+
            "    \"transform\": {\"rotate($00deg)\": 1, \"skew($00deg)\": 1},\r\n"+
            "    \"-moz-transform\": {\"rotate($00deg)\": 1, \"skew($00deg)\": 1},\r\n"+
            "    \"-webkit-transform\": {\"rotate($00deg)\": 1, \"skew($00deg)\": 1 }\r\n"+
            "};\r\n"+
            "\r\n"+
            "var CssCompletions = function() {\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "\r\n"+
            "    this.completionsDefined = false;\r\n"+
            "\r\n"+
            "    this.defineCompletions = function() {\r\n"+
            "        if (document) {\r\n"+
            "            var style = document.createElement('c').style;\r\n"+
            "\r\n"+
            "            for (var i in style) {\r\n"+
            "                if (typeof style[i] !== 'string')\r\n"+
            "                    continue;\r\n"+
            "\r\n"+
            "                var name = i.replace(/[A-Z]/g, function(x) {\r\n"+
            "                    return '-' + x.toLowerCase();\r\n"+
            "                });\r\n"+
            "\r\n"+
            "                if (!propertyMap.hasOwnProperty(name))\r\n"+
            "                    propertyMap[name] = 1;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        this.completionsDefined = true;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.getCompletions = function(state, session, pos, prefix) {\r\n"+
            "        if (!this.completionsDefined) {\r\n"+
            "            this.defineCompletions();\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        if (state==='ruleset' || session.$mode.$id == \"ace/mode/scss\") {\r\n"+
            "            var line = session.getLine(pos.row).substr(0, pos.column);\r\n"+
            "            if (/:[^;]+$/.test(line)) {\r\n"+
            "                /([\\w\\-]+):[^:]*$/.test(line);\r\n"+
            "\r\n"+
            "                return this.getPropertyValueCompletions(state, session, pos, prefix);\r\n"+
            "            } else {\r\n"+
            "                return this");
          out.print(
            ".getPropertyCompletions(state, session, pos, prefix);\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        return [];\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.getPropertyCompletions = function(state, session, pos, prefix) {\r\n"+
            "        var properties = Object.keys(propertyMap);\r\n"+
            "        return properties.map(function(property){\r\n"+
            "            return {\r\n"+
            "                caption: property,\r\n"+
            "                snippet: property + ': $0;',\r\n"+
            "                meta: \"property\",\r\n"+
            "                score: 1000000\r\n"+
            "            };\r\n"+
            "        });\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.getPropertyValueCompletions = function(state, session, pos, prefix) {\r\n"+
            "        var line = session.getLine(pos.row).substr(0, pos.column);\r\n"+
            "        var property = (/([\\w\\-]+):[^:]*$/.exec(line) || {})[1];\r\n"+
            "\r\n"+
            "        if (!property)\r\n"+
            "            return [];\r\n"+
            "        var values = [];\r\n"+
            "        if (property in propertyMap && typeof propertyMap[property] === \"object\") {\r\n"+
            "            values = Object.keys(propertyMap[property]);\r\n"+
            "        }\r\n"+
            "        return values.map(function(value){\r\n"+
            "            return {\r\n"+
            "                caption: value,\r\n"+
            "                snippet: value,\r\n"+
            "                meta: \"property value\",\r\n"+
            "                score: 1000000\r\n"+
            "            };\r\n"+
            "        });\r\n"+
            "    };\r\n"+
            "\r\n"+
            "}).call(CssCompletions.prototype);\r\n"+
            "\r\n"+
            "exports.CssCompletions = CssCompletions;\r\n"+
            "});\r\n"+
            "\r\n"+
            "ace.define(\"ace/mode/behaviour/css\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/behaviour\",\"ace/mode/behaviour/cstyle\",\"ace/token_iterator\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var oop = require(\"../../lib/oop\");\r\n"+
            "var Behaviour = require(\"../behaviour\").Behaviour;\r\n"+
            "var CstyleBehaviour = require(\"./cstyle\").CstyleBehaviour;\r\n"+
            "var TokenIterator = require(\"../../token_iterator\").TokenIterator;\r\n"+
            "\r\n"+
            "var CssBehaviour = function () {\r\n"+
            "\r\n"+
            "    this.inherit(CstyleBehaviour);\r\n"+
            "\r\n"+
            "    this.add(\"colon\", \"insertion\", function (state, action, editor, session, text) {\r\n"+
            "        if (text === ':' && editor.selection.isEmpty()) {\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var iterator = new TokenIterator(session, cursor.row, cursor.column);\r\n"+
            "            var token = iterator.getCurrentToken();\r\n"+
            "            if (token && token.value.match(/\\s+/)) {\r\n"+
            "                token = iterator.stepBackward();\r\n"+
            "            }\r\n"+
            "            if (token && token.type === 'support.type') {\r\n"+
            "                var line = session.doc.getLine(cursor.row);\r\n"+
            "                var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "                if (rightChar === ':') {\r\n"+
            "                    return {\r\n"+
            "                       text: '',\r\n"+
            "                       selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "                if (/^(\\s+[^;]|\\s*$)/.test(line.substring(cursor.column))) {\r\n"+
            "                    return {\r\n"+
            "                       text: ':;',\r\n"+
            "                       selection: [1, 1]\r\n"+
            "                    };\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"colon\", \"deletion\", function (state, action, editor, session, range) {\r\n"+
            "        var selected = session.doc.getTextRange(range);\r\n"+
            "        if (!range.isMultiLine() && selected === ':') {\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var iterator = new TokenIterator(session, cursor.row, cursor.column);\r\n"+
            "            var token = iterator.getCurrentToken();\r\n"+
            "            if (token && token.value.match(/\\s+/)) {\r\n"+
            "                token = iterator.stepBackward();\r\n"+
            "            }\r\n"+
            "            if (token && token.type === 'support.type') {\r\n"+
            "                var line = session.doc.getLine(range.start.row);\r\n"+
            "                var rightChar = line.substring(range.end.column, range.end.column + 1);\r\n"+
            "                if (rightChar === ';') {\r\n"+
            "                    range.end.column ++;\r\n"+
            "                    return range;\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"semicolon\", \"insertion\", function (state, action, editor, session, text) {\r\n"+
            "        if (text === ';' && editor.selection.isEmpty()) {\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var line = session.doc.getLine(cursor.row);\r\n"+
            "            var rightChar = line.substring(cursor.column, cursor.column + 1);\r\n"+
            "            if (rightChar === ';') {\r\n"+
            "                return {\r\n"+
            "                   text: '',\r\n"+
            "                   selection: [1, 1]\r\n"+
            "                };\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.add(\"!important\", \"insertion\", function (state, action, editor, session, text) {\r\n"+
            "        if (text === '!' && editor.selection.isEmpty()) {\r\n"+
            "            var cursor = editor.getCursorPosition();\r\n"+
            "            var line = session.doc.getLine(cursor.row);\r\n"+
            "\r\n"+
            "            if (/^\\s*(;|}|$)/.test(line.substring(cursor.column))) {\r\n"+
            "                return {\r\n"+
            "                    text: '!important',\r\n"+
            "                    selection: [10, 10]\r\n"+
            "                };\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "};\r\n"+
            "oop.inherits(CssBehaviour, CstyleBehaviour);\r\n"+
            "\r\n"+
            "exports.CssBehaviour = CssBehaviour;\r\n"+
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
            "    this.foldingStartMarker = /([\\{\\[\\(])[^\\}\\]\\)]*$|^\\s*(\\/\\*)/;\r\n"+
            "    this.foldingStopMarker = /^[^\\[\\{\\(]*([\\}\\]\\)])|^[\\s\\*]*(\\*\\/)/;\r\n"+
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
            "ace.define(\"ace/mode/css\",[\"require\",\"exports\",\"module\",\"ace/lib/oop\",\"ace/mode/text\",\"ace/mode/css_highlight_rules\",\"ace/mode/matching_brace_outdent\",\"ace/worker/worker_client\",\"ace/mode/css_completions\",\"ace/mode/behaviour/css\",\"ace/mode/folding/cstyle\"], function(require, exports, module) {\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "va");
          out.print(
            "r oop = require(\"../lib/oop\");\r\n"+
            "var TextMode = require(\"./text\").Mode;\r\n"+
            "var CssHighlightRules = require(\"./css_highlight_rules\").CssHighlightRules;\r\n"+
            "var MatchingBraceOutdent = require(\"./matching_brace_outdent\").MatchingBraceOutdent;\r\n"+
            "var WorkerClient = require(\"../worker/worker_client\").WorkerClient;\r\n"+
            "var CssCompletions = require(\"./css_completions\").CssCompletions;\r\n"+
            "var CssBehaviour = require(\"./behaviour/css\").CssBehaviour;\r\n"+
            "var CStyleFoldMode = require(\"./folding/cstyle\").FoldMode;\r\n"+
            "\r\n"+
            "var Mode = function() {\r\n"+
            "    this.HighlightRules = CssHighlightRules;\r\n"+
            "    this.$outdent = new MatchingBraceOutdent();\r\n"+
            "    this.$behaviour = new CssBehaviour();\r\n"+
            "    this.$completer = new CssCompletions();\r\n"+
            "    this.foldingRules = new CStyleFoldMode();\r\n"+
            "};\r\n"+
            "oop.inherits(Mode, TextMode);\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "\r\n"+
            "    this.foldingRules = \"cStyle\";\r\n"+
            "    this.blockComment = {start: \"/*\", end: \"*/\"};\r\n"+
            "\r\n"+
            "    this.getNextLineIndent = function(state, line, tab) {\r\n"+
            "        var indent = this.$getIndent(line);\r\n"+
            "        var tokens = this.getTokenizer().getLineTokens(line, state).tokens;\r\n"+
            "        if (tokens.length && tokens[tokens.length-1].type == \"comment\") {\r\n"+
            "            return indent;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        var match = line.match(/^.*\\{\\s*$/);\r\n"+
            "        if (match) {\r\n"+
            "            indent += tab;\r\n"+
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
            "    this.getCompletions = function(state, session, pos, prefix) {\r\n"+
            "        return this.$completer.getCompletions(state, session, pos, prefix);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.createWorker = function(session) {\r\n"+
            "        var worker = new WorkerClient([\"ace\"], \"ace/mode/css_worker\", \"Worker\");\r\n"+
            "        worker.attachToDocument(session.getDocument());\r\n"+
            "\r\n"+
            "        worker.on(\"annotate\", function(e) {\r\n"+
            "            session.setAnnotations(e.data);\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        worker.on(\"terminate\", function() {\r\n"+
            "            session.clearAnnotations();\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        return worker;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.$id = \"ace/mode/css\";\r\n"+
            "    this.snippetFileId = \"ace/snippets/css\";\r\n"+
            "}).call(Mode.prototype);\r\n"+
            "\r\n"+
            "exports.Mode = Mode;\r\n"+
            "\r\n"+
            "});                (function() {\r\n"+
            "                    ace.require([\"ace/mode/css\"], function(m) {\r\n"+
            "                        if (typeof module == \"object\" && typeof exports == \"object\" && module) {\r\n"+
            "                            module.exports = m;\r\n"+
            "                        }\r\n"+
            "                    });\r\n"+
            "                })();\r\n"+
            "            ");

	}
	
}