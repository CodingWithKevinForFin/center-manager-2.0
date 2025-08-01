// Publish project specific data
(function() {
rh = window.rh;
model = rh.model;
var defaultTopic = "Support_Landing_Page.htm";
rh._.exports(defaultTopic);
rh.consts('DEFAULT_TOPIC', encodeURI("Support_Landing_Page.htm"));
rh.consts('HOME_FILEPATH', encodeURI('index.htm'));
rh.consts('START_FILEPATH', encodeURI('index.htm'));
rh.consts('HELP_ID', '54c08094-7044-465a-b77c-5464456df04e' || 'preview');
rh.consts('LNG_SUBSTR_SEARCH', 0);

model.publish(rh.consts('KEY_LNG_NAME'), "en");
model.publish(rh.consts('KEY_DIR'), "ltr");
model.publish(rh.consts('KEY_LNG'), {"Contents":"Contents","Index":"Index","Search":"-Search-","Glossary":"Glossary","Logo/Author":"Powered By","Show":"Show","Hide":"Hide","SyncToc":"SyncToc","Prev":"Previous","Next":"Next","Disabled Prev":"<<","Disabled Next":">>","Seperate":"|","OpenLinkInNewTab":"Open in new tab","Separator":"|","SearchOptions":"Search Options","Loading":"Loading...","UnknownError":"Unknown error","Logo":"Logo","HomeButton":"Home","SearchPageTitle":"Search Results","PreviousLabel":"Previous","NextLabel":"Next","TopicsNotFound":"No results found","JS_alert_LoadXmlFailed":"Failed to load XML file","JS_alert_InitDatabaseFailed":"Failed to initialize database","JS_alert_InvalidExpression_1":"The search string you typed is not valid.","Searching":"Searching...","Cancel":"Cancel","Canceled":"Canceled","ResultsFoundText":"%1 result(s) found for %2","SearchResultsPerScreen":"Search results per page","Back":"Back","TableOfContents":"Table of Contents","IndexFilterKewords":"Filter Keywords","GlossaryFilterTerms":"Filter Terms","ShowAll":"All","HideAll":"Hide All","ShowHide":"Show/Hide","IeCompatibilityErrorMsg":"This page cannot be viewed in Internet Explorer 8 or earlier version.","NoScriptErrorMsg":"Enable JavaScript support in the browser to view this page.","EnableAndSearch":"Display results with all search words","HighlightSearchResults":"Highlight Search Results","Print":"Print","Filter":"Filter","SearchTitle":"Search","ContentFilterChanged":"Content filter is changed, search again","EndOfResults":"End of search results.","Reset":"Reset","NavTip":"Menu","ToTopTip":"Go to top","ApplyTip":"Apply","SidebarToggleTip":"Expand/Collapse","Copyright":"© Copyright 2017. All rights reserved.","FavoriteBoxTitle":"Favorites","setAsFavorites":"Add to Favorites","unsetAsFavorite":"Unset as favorite","favoritesNameLabel":"Name","favoritesLabel":"Favorites","setAsFavorite":"Set as favorite","nofavoritesFound":"You have not marked any pages as favorite.","Welcome_header":"Welcome to our Help Center","Welcome_text":"What can we help you with today?","SearchButtonTitle":"Search for...","ShowTopicInContext":"Click here to see this page in full context","TopicHiddenText":"This topic is filtered out by the selected filters.","NoTermsFound":"No terms found","NoKeywordFound":"No keyword found","SkipToMainContent":"Skip To Main Content","FilterIntro":"Please select your filter(s):"});

model.publish(rh.consts('KEY_HEADER_TITLE'), "AMI");
model.publish(rh.consts('PDF_FILE_NAME'), "");
model.publish(rh.consts('MAX_SEARCH_RESULTS'), "20");
model.publish(rh.consts('KEY_SKIN_FOLDER_NAME'), "Charcoal_Grey");
model.publish(rh.consts('CHAT_API_SESSION_TOKEN'), "");
model.publish(rh.consts('CHAT_API_PROJ_ID'), "");

model.publish(rh.consts('KEY_SUBSTR_SEARCH'), "");
})();
