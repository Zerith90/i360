/***********************************************************************************
*	(c) Ger Versluis 2000 version 5.411 24 December 2001 (updated Jan 31st, 2003 by Dynamic Drive for Opera7)
*	For info write to menus@burmees.nl		          *
*	You may remove all comments for faster loading	          *		
***********************************************************************************/

	var NoOffFirstLineMenus=6;			// Number of first level items
	var LowBgColor='#eaebf4';			// Background color when mouse is not over
	var LowSubBgColor='#E2E6F1';			// Background color when mouse is not over on subs
	var HighBgColor='white';			// Background color when mouse is over
	var HighSubBgColor='#c0c0c0';			// Background color when mouse is over on subs
	var FontLowColor='navy';			// Font color when mouse is not over
	var FontSubLowColor='navy';			// Font color subs when mouse is not over
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
	var ChildVerticalOverlap=.2;			// vertical overlap child/ parent
	var StartTop=138;				// Menu offset x coordinate
	var StartLeft=10;				// Menu offset y coordinate
	var VerCorrect=0;				// Multiple frames y correction
	var HorCorrect=0;				// Multiple frames x correction
	var LeftPaddng=6;				// Left padding
	var TopPaddng=0;				// Top padding
	var FirstLineHorizontal=1;			// SET TO 1 FOR HORIZONTAL MENU, 0 FOR VERTICAL
	var MenuFramesVertical=0;			// Frames in cols or rows 1 or 0
	var DissapearDelay=300;			// delay before menu folds in
	var TakeOverBgColor=1;			// Menu frame takes over background color subitem frame
	var FirstLineFrame='Top';			// Frame where first level appears
	var SecLineFrame='Top';			// Frame where sub levels appear
	var DocTargetFrame='main';			// Frame where target documents appear
	var TargetLoc='';				// span id for relative positioning
	var HideTop=0;				// Hide first level when loading new document 1 or 0
	var MenuWrap=1;				// enables/ disables menu wrap 1 or 0
	var RightToLeft=0;				// enables/ disables right to left unfold 1 or 0
	var UnfoldsOnClick=0;			// Level 1 unfolds onclick/ onmouseover
	var WebMasterCheck=0;			// menu tree checking on or off 1 or 0
	var ShowArrow=1;				// Uses arrow gifs when 1
	var KeepHilite=1;				// Keep selected path highligthed
	var Arrws=['tri.gif',5,10,'',10,5,'trileft.gif',5,10];	// Arrow source, width and height


function BeforeStart(){return}
function AfterBuild(){return}
function BeforeFirstOpen(){return}
function AfterCloseAll(){return}


// Menu tree
//	MenuX=new Array(Text to show, Link, background image (optional), number of sub elements, height, width);
//	For rollover images set "Text to show" to:  "rollover:Image1.jpg:Image2.jpg"

Menu1=new Array("<img src = images/360_17.jpg>","","",5,33,156, "ff552a");

		Menu1_1=new Array("Setup","","",12,20,130);	
			Menu1_1_1=new Array("Age Range","AgeRange.jsp","",0,14,100);
			Menu1_1_2=new Array("Ethnic Group","EthnicGroup.jsp","",0);
			Menu1_1_3=new Array("Location","Location.jsp","",0);
	        Menu1_1_4=new Array("Division","Division.jsp","",0);
	        Menu1_1_5=new Array("Department","Department.jsp","",0);
	        Menu1_1_6=new Array("Group","Group.jsp","",0);
	        Menu1_1_7=new Array("Job Function","JobFunction.jsp","",0);
	        Menu1_1_8=new Array("Job Level","JobLevel.jsp","",0);
	        Menu1_1_9=new Array("Job Position","JobPosition.jsp","",0);
	        Menu1_1_10=new Array("User","UserSearch.jsp","",0);
	        Menu1_1_11=new Array("Rater Relation","RaterRelation.jsp","",0);
	        Menu1_1_12=new Array("Time Frame","TimeFrame.jsp","",0);
	        
	                        
		Menu1_2=new Array("System","","",1);
			Menu1_2_1=new Array("Change Password","ChangePassword.jsp","",0,20,120);
		
		Menu1_3=new Array("Organisation List","OrganizationList.jsp","",0,20,200);
		
		Menu1_4=new Array("Demographic Entry","DemographicEntryForAll.jsp","",0,20,200);	
		Menu1_5=new Array("Event Viewer","EventViewer.jsp","",0);
		
		

Menu2=new Array("<img src = images/360_18.jpg>","","",7, 33, 143);
	Menu2_1=new Array("Competency","Competency.jsp","",0,20,150);	
	Menu2_2=new Array("Key Behaviour","KeyBehaviour.jsp","",0);
	Menu2_3=new Array("Job Category","JobCategory.jsp","",0);
	Menu2_4=new Array("Development Activities","DevelopmentActivities.jsp","",0);
	Menu2_5=new Array("Development Resources","DevelopmentResources.jsp","",0);
	Menu2_6=new Array("Rating Task","RatingTask.jsp","",0);
	Menu2_7=new Array("Rating Scale","RatingScale.jsp","",0);
	

Menu3=new Array("<img src = images/360_19.jpg>","","",4,33,108);
	Menu3_1=new Array("Create/Edit Survey","SurveyList_Create.jsp","",0,20,150);
	Menu3_2=new Array("Assign Target/Rater","SurveyList_AssignTR.jsp","",0);
	Menu3_3=new Array("Rater's Data Entry","RatersDataEntry.jsp","",0);
	Menu3_4=new Array("Process Result","SurveyList_Process.jsp","",0);

Menu4=new Array("<img src = images/360_20.jpg>","","",3,33,89);

	Menu4_1=new Array("System Reports","","",1,15,150);	
		Menu4_1_1=new Array("List of Competencies","Report_Competencies.jsp","",0,15,150);
	
	Menu4_2=new Array("Input Reports","","",6,15,150);
		Menu4_2_1=new Array("Competency by Survey","Report_Competencies_Survey.jsp","",0,15,150);
		Menu4_2_2=new Array("List of Surveys","Report_Survey.jsp","",0);
		Menu4_2_3=new Array("List of Rater's Status","Report_RaterStatus_Survey.jsp","",0);
		Menu4_2_4=new Array("Rater's Input for Target","Report_RaterInput_Target.jsp","",0);
		Menu4_2_5=new Array("List of Survey Targets","Report_Target_Survey.jsp","",0);
		Menu4_2_6=new Array("Nomination Status Report","Report_Nomination.jsp","",0);
	
	Menu4_3=new Array("Output Reports","","",4,15,150);
		Menu4_3_1=new Array("Questionnaire","QuestionnaireReport.jsp","",0,15,150);
		Menu4_3_2=new Array("Individual Report","IndividualReport.jsp","",0);
		Menu4_3_3=new Array("Group Report","GroupReport.jsp","",0);
		Menu4_3_4=new Array("Development Guide","Report_DevelopmentGuide.jsp","",0);
	
	//TOYOTA SETTING
	//Menu4_4=new Array("Customised Reports","","",2,15,150);
	//	Menu4_4_1=new Array("IDP Report","Report_ToyotaIDP.jsp","",0,15,150);
	//	Menu4_4_2=new Array("Result Summary Report","Report_ResultSummary.jsp","",0);	

Menu5=new Array("<img src = images/360_21.jpg>","","",5,33,87);
	Menu5_1=new Array("Email Template","ViewTemplate.jsp","",0,20,150);
	Menu5_2=new Array("Send Nomination Emails","SendNominationEmail.jsp","",0);
	Menu5_3=new Array("Send Participants Emails","SendPartEmail.jsp","",0);
	Menu5_4=new Array("View Sent Failed Emails","SentFailedEmail.jsp","",0);
	Menu5_5=new Array("Import and Export","ImportExport.jsp","",0); //,0,0); //,20,80);	
	Menu5_6=new Array("Logout","Logout.jsp","",0);
	//Menu5_3=new Array("View Total Users","ViewTotalUsersLoggingIn.jsp","",0);
	//Menu5_3=new Array("Import User","ImportUserConfirm.jsp","",0); //,0,0); //,20,80);
	//Menu5_4=new Array("Export User","ExportUser.jsp","",0);

Menu6=new Array("<img src = images/360_22.jpg>","Help.htm","",0,33,88);
