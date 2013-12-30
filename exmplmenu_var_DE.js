/***********************************************************************************
*	(c) Ger Versluis 2000 version 5.411 24 December 2001 (updated Jan 31st, 2003 by Dynamic Drive for Opera7)
*	For info write to menus@burmees.nl		          *
*	You may remove all comments for faster loading	          *		
***********************************************************************************/

	var NoOffFirstLineMenus=3;			// Number of first level items
	var LowBgColor='#DEE7EF';			// Background color when mouse is not over
	var LowSubBgColor='#647BBB';			// Background color when mouse is not over on subs
	var HighBgColor='#DEE7EF';			// Background color when mouse is over
	var HighSubBgColor='#c0c0c0';			// Background color when mouse is over on subs
	var FontLowColor='navy';			// Font color when mouse is not over
	var FontSubLowColor='white';			// Font color subs when mouse is not over
	var FontHighColor='white';			// Font color when mouse is over
	var FontSubHighColor='navy';			// Font color subs when mouse is over
	var BorderColor='white';			// Border color
	var BorderSubColor='#FFFFCC';			// Border color for subs
	var BorderWidth=0;				// Border width
	var BorderBtwnElmnts=0;			// Border between elements 1 or 0
	var FontFamily="arial,technical"	// Font family menu items
	var FontSize=8;				// Font size menu items
	var FontBold=1;				// Bold menu items 1 or 0
	var FontItalic=0;				// Italic menu items 1 or 0
	var MenuTextCentered='left';			// Item text position 'left', 'center' or 'right'
	var MenuCentered='left';			// Menu horizontal position 'left', 'center' or 'right'
	var MenuVerticalCentered='top';		// Menu vertical position 'top', 'middle','bottom' or static
	var ChildOverlap=0;				// horizontal overlap child/ parent
	var ChildVerticalOverlap=.5;			// vertical overlap child/ parent
	var StartTop=77;				// Menu offset x coordinate
	var StartLeft=10;				// Menu offset y coordinate
	var VerCorrect=0;				// Multiple frames y correction
	var HorCorrect=0;				// Multiple frames x correction
	var LeftPaddng=6;				// Left padding
	var TopPaddng=0;				// Top padding
	var FirstLineHorizontal=0;			// SET TO 1 FOR HORIZONTAL MENU, 0 FOR VERTICAL
	var MenuFramesVertical=1;			// Frames in cols or rows 1 or 0
	var DissapearDelay=200;			// delay before menu folds in
	var TakeOverBgColor=1;			// Menu frame takes over background color subitem frame
	var FirstLineFrame='Side';			// Frame where first level appears
	var SecLineFrame='Side';			// Frame where sub levels appear
	var DocTargetFrame='main';			// Frame where target documents appear
	var TargetLoc='Side';				// span id for relative positioning
	var HideTop=0;				// Hide first level when loading new document 1 or 0
	var MenuWrap=1;				// enables/ disables menu wrap 1 or 0
	var RightToLeft=0;				// enables/ disables right to left unfold 1 or 0
	var UnfoldsOnClick=0;			// Level 1 unfolds onclick/ onmouseover
	var WebMasterCheck=0;			// menu tree checking on or off 1 or 0
	var ShowArrow=0;				// Uses arrow gifs when 1
	var KeepHilite=1;				// Keep selected path highligthed
	//Edited by Xuehai, 25 May 2011.
	//Change width&height to be 0 when img.src=''. To avoid Chrome&Firefox display blank square.
	//var Arrws=['tri.gif',5,10,'',10,5,'trileft.gif',5,10];	// Arrow source, width and height
	var Arrws=['tri.gif',5,10,'',0,0,'trileft.gif',5,10];	// Arrow source, width and height


function BeforeStart(){return}
function AfterBuild(){return}
function BeforeFirstOpen(){return}
function AfterCloseAll(){return}


// Menu tree
//	MenuX=new Array(Text to show, Link, background image (optional), number of sub elements, height, width);
//	For rollover images set "Text to show" to:  "rollover:Image1.jpg:Image2.jpg"

Menu1=new Array("<img src = images/reports.bmp>","","",11,51,127);
	Menu1_1=new Array("List of Competencies","Report_Competencies.jsp","",0,17,150);
	Menu1_2=new Array("Competency Listing","Report_Competencies_Survey.jsp","",0);
	Menu1_3=new Array("List of Surveys","Report_Survey.jsp","",0);
	Menu1_4=new Array("List of Rater's Status","Report_RaterStatus_Survey.jsp","",0);
	Menu1_5=new Array("Rater's Input for Target","Report_RaterInput_Target.jsp","",0);
	Menu1_6=new Array("Rater's Input for Group","Report_RaterInput_Group.jsp","",0);
	Menu1_7=new Array("List of Survey Targets","Report_Target_Survey.jsp","",0);
	Menu1_8=new Array("Questionnaire","QuestionnaireReport.jsp","",0);
	Menu1_9=new Array("Individual Report","IndividualReport.jsp","",0);
	Menu1_10=new Array("Group Report","GroupReport.jsp","",0);
	Menu1_11=new Array("Development Guide","Report_DevelopmentGuide.jsp","",0);

Menu2=new Array("<img src = images/tools.bmp>","","",2,51,127);
	Menu2_1=new Array("Change Password","ChangePassword.jsp","",0,20,150);
	Menu2_2=new Array("Logout","Logout.jsp","",0,20,120);

	//Fix the link to the correct file By Hemilda Date 11/08/2008
Menu3=new Array("<img src = images/help.bmp>","Guides/DataEntry.htm","",0,51,127);

