<%@ page
	import="java.sql.*,
                java.io.*,
                 java.text.DateFormat,
                 java.util.*,
                 java.util.Date,
                 java.text.*,
                 java.lang.String,
                 java.util.Vector,
				 java.util.LinkedList,
				 CP_Classes.vo.*,CP_Classes.SurveyResult"%>

<%@ page buffer="16kb" autoFlush="true" %>
<html>
<head>
<title>Individual Report</title>
<!-- CSS -->

<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.css">
<link type="text/css" rel="stylesheet" href="lib/css/bootstrap-responsive.css">
<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.min.css">
<link type="text/css" rel="stylesheet" href="lib/css/bootstrap-responsive.min.css">


<!-- jQuery -->
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.js"></script>
<script type="text/javascript" src="lib/js/jquery-1.9.1.js"></script>


<script src="lib/js/bootstrap.min.js" type="text/javascript"></script>
<script src="lib/js/bootstrap-dropdown.js"></script>

 
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%
	// by lydia Date 05/09/2008 Fix jsp file to support Thai language
%>

</head>


	<jsp:useBean id="Database" class="CP_Classes.Database" scope="session" />
	<jsp:useBean id="QR" class="CP_Classes.QuestionnaireReport"
		scope="session" />
	<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session" />
	<jsp:useBean id="User_Jenty" class="CP_Classes.User_Jenty"
		scope="session" />
	<jsp:useBean id="USR" class="CP_Classes.User" scope="session" />
	<jsp:useBean id="ExcelIndividual" class="CP_Classes.IndividualReport"
		scope="session" />
	<jsp:useBean id="DG" class="CP_Classes.DevelopmentGuide"
		scope="session" />
	<jsp:useBean id="Setting" class="CP_Classes.Setting" scope="session" />
	<jsp:useBean id="AdvSetting" class="CP_Classes.AdvanceSettings"
		scope="session" />
	<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session" />
	<jsp:useBean id="SR" class="CP_Classes.SurveyResult" scope="session" />
	<jsp:useBean id="ORG" class="CP_Classes.Organization" scope="session" />
	<%
		// added to check whether organisation is a consulting company
	// Mark Oei 09 Mar 2010
	%>
	<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey"
		scope="session" />
<style>
.modal-body {
	position: relative;
	max-height: 200;
	padding: 15px;
	overflow-x: auto;
}
.selectTarget{
background:#F0C311;
}
.newGroup{
background:#4BC23B;
}
.normalRows{
background:#ddd;
}

  </style>

<% Vector selectedUsers =new Vector();
Vector chosenOnes=new Vector();
Vector splitGroupings = new Vector();%>
<script language="javascript">

var x = parseInt(window.screen.width) / 2 - 200;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 200;
var myWindow;
var totalSelectedUsers =new Array();
var totalSelectedUsers1 =new Array();
var splitGroup = new Array();
var remainingGroups = new Array();
var newGroupName1 = new String();
var totalSelectedNames = new Array();
$(document).ready(function() {
	$('.alert').alert();
	$('.info').tooltip();
	if(totalSelectedNames.length==0 ){
		document.getElementById('userTable').innerHTML = "<tr style='font-size:12px'><td>-All Users Selected-</td></td>";
	}
});
function newGroup(target){
	
	var z="#" + target;
	if(document.getElementById(target).className =="raterCode newGroup"){
		$(z).removeClass('newGroup');
		var index=splitGroup.indexOf(target);
		splitGroup.splice(index,1);
	}else{
		$(z).addClass('newGroup');
		var isExist=new Boolean(0);
		
		
		for(var i=0;i<splitGroup.length;i++){
			
			
			if(target==splitGroup[i]){
				isExist=Boolean(1);
			}
		}
		if(isExist==false){
			splitGroup.push(target);
			
		}
	}
	
}
function groupNameConfirm(){
	$('.groups').show();
}

function addGroup(newGroupName){
	newGroupName1=newGroupName;
	addRatersToTable(newGroupName);
}
function loading(){
	$('.generateBtn').button('loading');
}
function splitPop(){		
		$('#myModalSplit').modal('show');
}
function splitUndo(){
	
	<% session.removeAttribute("raterCodes");%>
	document.IndividualReport.action= "IndividualReport.jsp";
	document.IndividualReport.submit();

}
function submitTargets(form)
{
	document.getElementById('userTable').innerHTML = "";
	if(totalSelectedNames.length==0){
		document.getElementById('userTable').innerHTML = "<tr style='font-size:12px'><td>All Users Selected-</td></td>";
	}
	addNamesToTable();

}
function removeClass(target,name)
{
	var s="#" + target ;
	
	$(s).removeClass('selectTarget');
	var index=totalSelectedUsers.indexOf(target);
	var index1=totalSelectedNames.indexOf(name);
	totalSelectedUsers.splice(index,1);
	totalSelectedNames.splice(index1,1);
}
function addNamesToTable(){
	for(var su=0;su<totalSelectedNames.length;su++){
		var name1 = "<tr style='font-size:12px'><td>" + totalSelectedNames[su] + "</td></tr>";
		
		  document.getElementById('userTable').innerHTML += name1;
	}
	    
}

function addRatersToTable(newGroupName){
	
	var selectedGroups = document.getElementsByClassName('raterCode newGroup');
	

		document.getElementById('raterGroup').innerHTML = "";
	
		for(var su=0;su<selectedGroups.length;su++){
			if( selectedGroups.length>1){
			var name1 = "<tr style='font-size:12px'><td>" + newGroupName.value + "(" + selectedGroups[su].id + ")"+ "</td></tr>";
			
			  document.getElementById('raterGroup').innerHTML += name1;
			}
		}
		if( selectedGroups.length<=1){
			document.getElementById('raterGroup').innerHTML += "<tr style='font-size:12px'><td> No Split Groups </td></tr>";
		}
	
	    
}
function chooseTarget(target,name)
{
	var s="#" + target;
	
	$(s).addClass('selectTarget');
	var isExist=new Boolean(0);
	
	
	for(var i=0;i<totalSelectedUsers.length;i++){
		
		
		if(target==totalSelectedUsers[i]){
			isExist=Boolean(1);
		}
	}
	if(isExist==false){
		totalSelectedUsers.push(target);
		totalSelectedNames.push(name);
	}
	
}

function loadTarget(form)
{
	if(form.target.selectedIndex == 1){
		var selected = document.getElementsByClassName('selectTarget');
		for(var a=0;a<selected.length;a++){
			
			totalSelectedUsers.push(selected[a].id);
		}
		$('#myModal').modal('show');
	}
}
function loadModal()
{
	var selected = document.getElementsByClassName('selectTarget');
	for(var a=0;a<selected.length;a++){
		
		totalSelectedUsers.push(selected[a].id);
	}

	$('#myModal').modal('show');
	
}
function getID(form, ID, type)
{
	var typeSelected = "";
	
	if(ID != 0) {
		switch(type) {
			case 1: typeSelected = "surveyID";
					  break;
			case 2: typeSelected = "groupID";
					  break;
			case 3: typeSelected = "targetID";
					  break;
		}
		var query = "IndividualReport.jsp?" + typeSelected + "=" + ID;
		form.action = query;
		form.method = "post";
		form.submit();
	} else {
		alert("<%=trans.tslt("Please select the options")%> !");
		return false;
	}
	return true;	
}

function delay(gap)
{ 	/* gap is in millisecs */
	var then,now; then=new Date().getTime();
	now=then;
	
	while((now-then)<gap)
	{
		now = new Date().getTime();
	}
}
//Function checkFiled is created by James 11- Jun 2008 to make sure survey name and type are selected.
function checkField(form){
	var msg = "Please select following information: \n";
	var valid = true;
	var checkType = false;
	for (i=0;i<form.optIndividual.length;i++){
	if (form.optIndividual[i].checked==true){
	 		checkType=true;
	 		break;
	 	}
	}

	if(!checkType){
		msg += " - Report Type \n";
		valid = false;
	}
	if(form.surveyName.selectedIndex == 0){
		msg += " - Survey Name \n";
		valid = false;
	}
	if(valid){
		return valid;
	}else{
		alert(msg);
	}

}
function confirmOpen(form, optReport, survey, target, chkDG, chkDMap, chkNormative, chkGroupCPLines, chkSplit, chkBreakCPR, langType)
{
   if(checkField(form)){
		var type = 0;	//type 1=simplified;2=standard
		for (i = 0; i < optReport.length; i++) 
		{
			if(optReport[i].checked)
			{
				type = optReport[i].value;
			}		
		}
		var temp = document.getElementsByClassName('raterCode newGroup');
		if(splitGroup.length==0){
			for(var y=0;y<temp.length;y++){
			splitGroup.push(temp[y].id);
			}
		}
		if(survey.value != 0)
		{
			$('#myModalLoad').modal('show');
			var selectedUsers = document.getElementsByClassName("selectTarget");
			
			query="IndividualReport.jsp?open="+type+"&cancel=0&survey=" + survey.value + "&round=" + round.value;
			if(document.IndividualReport.newGroupName.value.length>0 && splitGroup.length>0){
				query+="&";
					for(var ds=0;ds<splitGroup.length;ds++){
					query+="raterCode="+ splitGroup[ds];
					if(ds!=(splitGroup.length-1)){
						query+="&";
					}
				
				}
			}
			if(selectedUsers.length>0){
				for(var t=0;t<selectedUsers.length;t++){
					query+= ("&users=" + selectedUsers[t].id);
				}
			}
			if(document.IndividualReport.newGroupName.value!=null || !(document.IndividualReport.newGroupName.value.trim().equalsIgnoreCase(""))){
			query+="&newGroupName=" + document.IndividualReport.newGroupName.value.trim();
			}
			
			
			form.action = query;
			form.method="post";
			form.submit();
				return true;
			
		} else {
			alert("<%=trans.tslt("Please select Survey")%>");
			return false;
		}
		
	}

	
	
	var bMultiple = false;	// Check whether user want to generate more than 1 report type
	
	if(type > 0 && chkDG.checked && chkDMap.checked)
		bMultiple = true;
	else if(type > 0 && chkDG.checked)
		bMultiple = true;
	else if(type > 0 && chkDMap.checked)
		bMultiple = true;
	else if(chkDG.checked && chkDMap.checked)
		bMultiple = true;
	else
		bMultiple = false;

	if(survey.value != 0)
	{
		bGenerate = true;
		
		if(target.value == 0)
		{
			alert("target Level 0");
			if(bMultiple == true)
			{
				if(confirm("Printing all targets' reports at one go needs sufficient system resources such as memory"+
							"and network bandwith. System may hang if resources are insufficient.\n\n"+
							"Should the system hang, you may want to print reports in batches of 20 reports."))
				{
					form.btnOpen.value = "<%=trans.tslt("Processing")%> ...";
					form.btnOpen.disabled = true;
				}
				else
				{
					bGenerate = false;
					form.btnOpen.value = "<%=trans.tslt("Preview")%>";
					form.btnOpen.disabled = false;
				}
			}
			else
			{
				form.btnOpen.value = "<%=trans.tslt("Processing")%> ...";
				form.btnOpen.disabled = true;
			}
		}

		if(bGenerate == true)
		{
			myWindow=window.open('IndividualReportAll.jsp','IndividualReportPopUp','scrollbars=no, width=480, height=250');
			var query = "IndividualReportAll.jsp";
			myWindow.moveTo(x,y);
			myWindow.location.href = query;
	
			form.action = "IndividualReport.jsp?open="+type+"&cancel=0&survey="+survey.value;
			form.submit();
			return true;	
		}
		
	} else {
		alert("<%=trans.tslt("Please select the survey name")%> !");
		return false;
	}
}

function cancelPrint(form)
{
	form.btnOpen.value = "<%=trans.tslt("Preview")%>";
	form.action = "IndividualReport.jsp?cancel=1";
	form.submit();
	return true;
}

function finishPrint(form)
{
	form.btnOpen.value = "<%=trans.tslt("Preview")%>";
	form.action = "IndividualReport.jsp";
	form.submit();
	return true;
}

//function add
/*------------------------------------------------------------start: Login modification 1------------------------------------------*/
/*	choosing organization*/

function proceed(form,field)
{
	form.action="IndividualReport.jsp?proceed="+field.value;
	form.method="post";
	form.submit();

}	

function populateDept(form, field)
{
	form.action="IndividualReport.jsp?div="+field.value;
	form.method="post";
	form.submit();
}

function populateGrp(form, field1, field2)
{
	form.action="IndividualReport.jsp?div=" + field1.value + "&dept="+ field2.value;
	form.method="post";
	form.submit();
}

</script>
<body>
	<p>
		<%
			//response.setHeader("Pragma", "no-cache");
			//response.setHeader("Cache-Control", "no-cache");
			//response.setDateHeader("expires", 0);

			String username=(String)session.getAttribute("username");
			
			
			Vector nameOfFilesCreated = new Vector<String>();
			Boolean bHasError = false;

			if (!logchk.isUsable(username)) 
			{
		%>
		<script>
			parent.location.href = "index.jsp";
		</script>
		<%
			} 
			else 
			{
			    // Changed by Ha 23/05/08: set value to default when button show is clicked
				if(request.getParameter("proceed") != null)
				{ 
					int PKOrg = new Integer(request.getParameter("proceed")).intValue();
				 	logchk.setOrg(PKOrg);
				 	QR.setSurveyID(0);
				 	QR.setDivisionID(0);
				 	QR.setDepartmentID(0);
				 	QR.setGroupID(0);
				 	QR.setTargetID(0);
				}

				/*-----------------------------------end login modification 1--------------------------------------*/
				
				int compID = logchk.getCompany();
				
			
				int OrgID = logchk.getOrg();	
				
				int pkUser = logchk.getPKUser();
				int userType = logchk.getUserType();	// 1= super admin
				int nameSeq = User_Jenty.NameSequence(OrgID);
				String newName=" ";
				Vector surveyList = QR.getSurvey(compID, OrgID);
				Vector divisionList = null;
				Vector departmentList = null;
				Vector groupList  = null;
				Vector targetList = null;
				Vector raterList = null;
				Vector raterCodeList =null;
				Vector roundList = null;
				Vector raters = null;
				boolean surveyOverview =false;
				int surveyID = QR.getSurveyID();
				int divisionID = QR.getDivisionID();
				int departmentID = QR.getDepartmentID();
				int groupID = QR.getGroupID();
				int target = QR.getTargetID();
				int roundID = QR.getRoundID();
				String newGroupName="";
				//Added by Tracy 01 Sep 08
				String chkNormative = "";
				//End add by Tracy**

				//Added by Chun Yeong 2 Jun 2011
				String chkGroupCPLine = "";
				//End add by Chun Yeong**
				//Added by Chun Yeong 13 Jun 2011
				String chkSplit = "";
				//End add by Chun Yeong**

				//Added by Albert 9 July 2012
				String chkBreakCPR = "";

				//Added by Chun Yeong 1 Aug 2011
				int langType = 0;
				String template = "";
				//End add by Chun Yeong**
				boolean weightedAverage = false;
				/*
				 *Handle for target list inputs
				 */

				if (request.getParameterValues("weightedAverage") != null) {
					weightedAverage = true;
				}
				if (request.getParameterValues("users") != null) {
					String[] users = (String[]) request
							.getParameterValues("users");

					for (int a = 0; a < users.length; a++) {
						System.out.println(users[a]);

						int selectedID = Integer.parseInt(users[a]);

						String name = ExcelIndividual.getName(selectedID);

						String[] vadar = new String[2];
						vadar[0] = users[a];
						vadar[1] = name;
						chosenOnes.add(vadar);

					}
					session.setAttribute("theOnes",chosenOnes);

				}

				if (request.getParameterValues("raterCode") != null) {
					String[] raterCodes = (String[]) request
							.getParameterValues("raterCode");
					System.out.println("kik" + raterCodes.length);
					//raters.clear();
					splitGroupings=new Vector();
					for (int a = 0; a < raterCodes.length; a++) {
						System.out.println(raterCodes[a]);
						splitGroupings.add(raterCodes[a]);
					}
					
					
					session.setAttribute("raterCodes",splitGroupings);
					
				}
				if(request.getParameter("newGroupName")!=null){
					
					newName = (String)request.getParameter("newGroupName");
					session.setAttribute("newGroupName",newName);
					System.out.println((String)session.getAttribute("newGroupName"));
				}
				if (surveyID != 0) {
					divisionList = QR.getDivision(surveyID);
					departmentList = QR.getDepartment(surveyID,divisionID);
					groupList = QR.getGroup(surveyID,QR.getDivisionID(),
							QR.getDepartmentID());
					targetList = QR.getTarget(surveyID,divisionID,departmentID,
							groupID);
					roundList = QR.getRound();
				
					raters = ExcelIndividual.getSplitGroups(QR
								.getSurveyID());
					
					int count = 0;
					/*while(targetList.next())
					{
					count ++;
					}*/
					count = targetList.size();
				}

				if (request.getParameter("cancel") != null) {
					int iCancelled = Integer.parseInt(request
							.getParameter("cancel"));
					ExcelIndividual.setCancelPrint(iCancelled); // 0=Proceed; 1=Cancelled
				} else {
					ExcelIndividual.setCancelPrint(0);
				}
				if(request.getParameter("surveyOverview")!=null) {
					surveyOverview =true;
				}
				if (request.getParameter("surveyID") != null) {
					int ID = Integer.parseInt(request.getParameter("surveyID"));
					QR.setSurveyID(ID);
					QR.setDivisionID(0);
					QR.setDepartmentID(0);
					QR.setGroupID(0);
					QR.setRaterID(0);
					QR.setTargetID(0);
					QR.setPageLoad(1);

					divisionList = QR.getDivision(ID);
					departmentList = QR.getDepartment(ID);
					groupList = QR.getGroup(ID,QR.getDivisionID(),
							QR.getDepartmentID());
					targetList = QR.getTarget(ID,QR.getDivisionID(),
							QR.getDepartmentID(),QR.getGroupID());
					roundList = QR.getRound();
					raters = ExcelIndividual.getSplitGroups(QR
								.getSurveyID());
					newName=(String)session.getAttribute("newName");
				} else if (request.getParameter("groupID") != null) {
					int group = Integer.parseInt(request
							.getParameter("groupID"));
					QR.setGroupID(group);
					QR.setRaterID(0);
					QR.setTargetID(0);
					targetList = QR.getTarget(QR.getSurveyID(),
							QR.getDivisionID(),QR.getDepartmentID(),
							QR.getGroupID());
				} else if (request.getParameter("targetID") != null) {
					int ID = QR.getSurveyID();
					int targetID = Integer.parseInt(request
							.getParameter("targetID"));
					QR.setTargetID(targetID);
					QR.setRaterID(0);
					raterList = QR.getRater(surveyID,groupID,targetID);
				}

				if (request.getParameter("round") != null) {
					roundID = Integer.parseInt(request.getParameter("round"));
					QR.setRoundID(roundID);
				}
				if (request.getParameter("div") != null) {
					QR.setDepartmentID(0);
					QR.setGroupID(0);
					int ID = QR.getSurveyID();
					int div = Integer.parseInt(request.getParameter("selDiv"));
					QR.setDivisionID(div);
					departmentList = QR.getDepartment(ID,QR.getDivisionID());
					groupList = QR.getGroup(ID,QR.getDivisionID(),
							QR.getDepartmentID());
					targetList = QR.getTarget(ID,QR.getDivisionID(),
							QR.getDepartmentID(),QR.getGroupID());
				}

				if (request.getParameter("dept") != null) {
					QR.setGroupID(0);
					int ID = QR.getSurveyID();
					int dept = Integer
							.parseInt(request.getParameter("selDept"));
					QR.setDepartmentID(dept);
					groupList = QR.getGroup(ID,QR.getDivisionID(),
							QR.getDepartmentID());
					targetList = QR.getTarget(ID,QR.getDivisionID(),
							QR.getDepartmentID(),QR.getGroupID());
				}

				if (request.getParameter("open") != null) {
					int type = Integer.parseInt(request.getParameter("open"));

					surveyID = Integer.parseInt(request.getParameter("survey"));
					divisionID = QR.getDivisionID();
					departmentID = QR.getDepartmentID();
					groupID = QR.getGroupID();
					//Added getting Normative option by Tracy 01 Sep 08
					if(session.getAttribute("raterCodes")!=null){
						raterCodeList = (Vector) session.getAttribute("raterCodes");
					}
					if(session.getAttribute("newGroupName")!=null){
						newGroupName = (String)session.getAttribute("newGroupName");
					}
					if (request.getParameter("chkNormative") != null) {
						chkNormative = request.getParameter("chkNormative");
					}
					//End add by Tracy 01 Sep 08***

					//Added to get Group CP Line option by Chun Yeong 2 Jun 2011
					if (request.getParameter("chkGroupCPLine") != null) {
						chkGroupCPLine = request.getParameter("chkGroupCPLine");
					}
					//End to get Group CP Line option by Chun Yeong 2 Jun 2011***

					//Added to get follow template option by Chun Yeong 13 Jun 2011
					if (request.getParameter("chkSplit") != null) {
						chkSplit = request.getParameter("chkSplit");
					}
					//End to get follow template option by Chun Yeong 13 Jun 2011***

					//Added to get the breakCPR option by Albert 09 July 2012
					if (request.getParameter("chkBreakCPR") != null) {
						chkBreakCPR = request.getParameter("chkBreakCPR");
					}
					//end to get breakCPR option

					//Added to get language option by Chun Yeong 1 Aug 2011
					if (request.getParameter("langType") != null) {
						langType = Integer.parseInt(request.getParameter("langType"));
					}

					if (request.getParameter("template") != null) {
						template = request.getParameter("template");
					}
					//End to get language option by Chun Yeong 1 Aug 2011***

					//Added checking null condition by Ha 16/05/08
					
					if (request.getParameter("target") != null) {
						target = Integer.parseInt(request.getParameter("target"));

						QR.setTargetID(target);

						String surveyName = "";
						int iSurveyLevel = 0; // 0=Comp, 1=KB
						votblSurvey voSurvey = SR.SurveyInfo(surveyID);

						surveyName = voSurvey.getSurveyName(); // Get username (for sending of email)
						iSurveyLevel = voSurvey.getLevelOfSurvey();

						int iNameSeq = ORG.getNameSeq(logchk.getOrg());
						String sUserEmail = User_Jenty.getUserEmail(logchk.getPKUser()); // Email of the person who gen the report

						String [] userInfo = USR.getUserDetail(target, iNameSeq);	// Get target's fullname (for sending of email)
						String targetName = userInfo[0] + " " + userInfo[1];
						
						boolean bDG = false;
						boolean bDMap = false;

						if (request.getParameter("chkDevGuide") != null)
							bDG = true;

						if (request.getParameter("chkDevMap") != null) {
							if (iSurveyLevel != 0)
								bDMap = true;
							else
								bDMap = false;
						}

						boolean bMoreThanOne = false; // Flag to check whether user generate more than 1 type of report

						if ((type == 1 || type == 2) && bDG == true
								&& bDMap == true)
							bMoreThanOne = true; // Ind, DG & DMap chosen
						else if ((type == 1 || type == 2) && bDG == true)
							bMoreThanOne = true; // Ind & DG chosen
						else if ((type == 1 || type == 2) && bDMap == true)
							bMoreThanOne = true; // Ind & DMap chosen
						else if (bDG == true && bDMap == true)
							bMoreThanOne = true; // DG & DMap chosen
						else
							bMoreThanOne = false;

						// If user doesn't specify a report to generate, prompt message
						if (type == 0 && bDG == false && bDMap == false) {
						%>
						<script>
							alert("Please choose a report to generate");
						</script>
				<%
				}else{
					if(target != 0)
					{							
						chosenOnes = (Vector)session.getAttribute("theOnes");
						for(int a=0;a<chosenOnes.size();a++){
							String[] theOne=(String[])(chosenOnes.elementAt(a));
							int theOneID = Integer.parseInt(theOne[0]);
							String theOneName = theOne[1];
							Date timeStamp = new java.util.Date();
							Vector Target = new Vector();
							SurveyResult s = new SurveyResult();
							SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
							String temp  =  dFormat.format(timeStamp);
												
							if(type == 1 || type == 2)
							{
								timeStamp = new java.util.Date();
								dFormat = new SimpleDateFormat("dd-MM-yy[HH.mm.ss]");
								temp  =  dFormat.format(timeStamp);
				
								String file_name = "Individual Report for " + theOneName+ "(" + temp + ").xls";
								//Added by Ha 17/06/08 to automatically calculate
								//when generating report
									//Re-edit by Ha 04/07/08 to calculate for all target of that group
									Target = s.TargetID(surveyID, divisionID, departmentID, groupID);
				
									for (int j = 0; j < Target.size(); j++)
									{
										voUser vo = (voUser)Target.get(j);
										if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyID ,divisionID, departmentID, groupID))
										{						
											 if (!s.isAllRaterRated(surveyID, groupID, vo.getTargetLoginID()))                	 
								                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 1);
								             else
								                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 0);						
										}
									}	
									
									
									Vector errors = ExcelIndividual.Report(surveyID,divisionID,departmentID,groupID,theOneID, pkUser, file_name, type, chkNormative, chkGroupCPLine, chkSplit, chkBreakCPR, langType, template,weightedAverage,roundID,raterCodeList,newGroupName,surveyOverview);
									//End edit by Tracy 01 Sep 08

									
									if(session.getAttribute("IndividualReport")!=null){
										Vector fileNames = (Vector)session.getAttribute("IndividualReport");
										fileNames.add(file_name);
										session.setAttribute("IndividualReport", fileNames);
									} else {
										Vector fileNames=new Vector();
										fileNames.add(file_name);
										session.setAttribute("IndividualReport", fileNames);
									}
		
								} 
							
							}
						RequestDispatcher dispatcher = request.getRequestDispatcher("GenerationComplete.jsp?Success=1");
						
						dispatcher.forward(request,response);	
 									
								
								
							} else {	//generate multiple reports here (rater != 0)
								
								target = Integer.parseInt(request.getParameter("target"));
								QR.setTargetID(target);
								divisionID = Integer.parseInt(request.getParameter("selDiv"));
								departmentID = Integer.parseInt(request.getParameter("selDept"));
								groupID = Integer.parseInt(request.getParameter("groupName"));
								QR.setDivisionID(divisionID);
								QR.setDepartmentID(departmentID);
								QR.setGroupID(groupID);
												
								int total = 1; //total report generated
								String path = "C:/tomcat/webapps/i360/Report/";
								/*
								System.out.println("Surv = " + surveyID);
								System.out.println("Div = " + divisionID);
								System.out.println("Dept = " + departmentID);
								System.out.println("Grp = " + groupID);
								*/
								// extract all targets under this group
								Vector allTarget = ExcelIndividual.AllTargets(surveyID, divisionID, departmentID, groupID, OrgID);
								int totalReports = allTarget.size();
															
								int totalGenerated = 0;
								
								for(int i=0; i<totalReports; i++) 
								{
									// for delay purpose
									if(i != 0)
										for(int j=0;j<100000;j++);
							
									if (ExcelIndividual.getCancelPrint() == 0)
									{
										//generate individual report.
										String [] data = (String[])(allTarget.elementAt(i));
										surveyName 	 = data[0];
										int targetID = Integer.parseInt(data[1]);
										targetName	 = data[2];		%>
										<script>
											window.status = "Generating Individual Report for " + "<%=targetName%> (<%=total%> of <%=totalReports%>.............)";
										</script>
			<%
										Date timeStamp = new java.util.Date();
										SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
										String temp  =  dFormat.format(timeStamp);
										
										//try {
										request.getSession().setMaxInactiveInterval(1800);
										
										if(type == 1 || type == 2)
										{
											String file_name = "Individual Report for " + targetName + " (" + temp + ").xls";
											
											nameOfFilesCreated.add(file_name);
											
											System.out.println("Generating Individual Report for " + targetName + " (" + total + " of " + totalReports + ")....");
											
											//Added by Ha 19/06/08 to automatically calculate if user choose to generate report which has not been calculated yet
											//Re-edit by Ha 04/07/08 to calculate for all target of that group
											Vector Target = new Vector();
											SurveyResult s = new SurveyResult();								
												
											Target = s.TargetID(surveyID, divisionID, departmentID, groupID);
											
											for (int j = 0; j < Target.size(); j++)
											{
												voUser vo = (voUser)Target.get(j);
												if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyID ,divisionID, departmentID, groupID))
												{						
													 if (!s.isAllRaterRated(surveyID, groupID, vo.getTargetLoginID()))                	 
										                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 1);
										             else
										                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 0);						
												}
											}	
											
											//Edited printing ind report with Normative option By Tracy 01 Sep 08***
											//Edited printing ind report with Group CP Line option by Chun Yeong 2 Jun 2011
											//Edited allowing Split option, Chun Yeong 13 Jun 2011
											//Edited allowing Language option, Chun Yeong 1 Aug 2011
											//Edited allowing Template option, Chun Yeong 1 Aug 2011
											ExcelIndividual.Report(surveyID,divisionID,departmentID,groupID,targetID, pkUser, file_name, type, chkNormative, chkGroupCPLine, chkSplit, chkBreakCPR, langType, template,weightedAverage,roundID,raterCodeList,newGroupName,surveyOverview);									
											// End edit by Tracy****
											System.out.println("Completed Individual Report for " + targetName + " (" + total + " of " + totalReports + ")....");
											totalGenerated++;
										}
										
										if(bDG == true)
										{
											String file_name = "Development Guide for " + targetName + " (" + temp + ").xls";
											
											nameOfFilesCreated.add(file_name);
											
											System.out.println("Generating Development Guide for " + targetName + " (" + total + " of " + totalReports + ")....");
											DG.SelTarget(surveyID, targetID, pkUser, file_name);
											System.out.println("Completed Development Guide for " + targetName + " (" + total + " of " + totalReports + ")....");
											totalGenerated++;
										}
										
										if(bDMap == true)
										{
											String file_name = "Development Map for " + targetName + " (" + temp + ").xls";
											boolean bBreakdown = AdvSetting.bIsBreakdown(surveyID);
											
											nameOfFilesCreated.add(file_name);
											
											System.out.println("Generating Development Map for " + targetName + " (" + total + " of " + totalReports + ")....");
											ExcelIndividual.reportDevelopmentMap(surveyID, targetID, file_name, bBreakdown);
											System.out.println("Completed Development Map for " + targetName + " (" + total + " of " + totalReports + ")....");
											totalGenerated++;
										}
										
										//} catch (Exception E) { System.out.println(E.getMessage());}
			%>
			<script>
											window.status = "Completed for "+ "<%=targetName%> (<%=total%> of <%=totalReports%>)";
										</script>
			<%
				total++;
									} else {
										System.out.println("Report generation has been cancelled.\nThe generated reports are stored in " + path) ;
										i = totalReports;	// Stop generating, break the for loop
									}
									
								}	// end for
			%>
			<script>
									alert("<%=totalGenerated%> Report(s) Generated Successfully.\nThe generated reports are stored in <%=path%>");
			</script>
			<%
				} // end if (target != 0)					
							} // END if(type == 0 && bDG == false && bDMap == false)
						}
					} // end if (request.getParameter("open") != null)

				//if (nameOfFilesCreated.size() != 0) {
				//bHasError = ExcelIndividual.ZipAllFiles(nameOfFilesCreated);
				//}
		%>
	
	<div class="container">

		<form name="IndividualReport" method="post">
			<%
				if (bHasError) {
			%>
			<b><font face="Arial" color="#0000FF"> <%=trans
							.tslt("Generation process completed UnSuccessfully, ")%><br />
					<%=trans.tslt("Please see ")%><a href="ErrorLog.jsp">ErrorLog.xls</a>
					<%=trans
							.tslt("for the details of generation failed data")%>
			</font></b>
			<%
				}
			%>
			<br />
		
			<br>
			<div class="row-fluid">

				<div class="offset1 span4">
					<h2><%=trans.tslt("Individual Report")%></h2>
				</div>

			</div>
 
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Organisation")%>:
				</div>
				<div class="span5">
					<%
						// Added to check whether organisation is also a consulting company
							// if yes, will display a dropdown list of organisation managed by this company
							// else, it will display the current organisation only
							// Mark Oei 09 Mar 2010
							String[] UserDetail = new String[14];
							UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
							boolean isConsulting = true;
							isConsulting = ORG.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
							if (isConsulting) {
					%>
					<select size="1" name="selOrg"
						onChange="proceed(this.form,this.form.selOrg)">
						<option value="0" selected><%=trans.tslt("All")%></option>
						<%
							Vector vOrg = logchk.getOrgList(logchk.getCompany());

									for (int i = 0; i < vOrg.size(); i++) {
										votblOrganization vo = (votblOrganization) vOrg
												.elementAt(i);
										int PKOrg = vo.getPKOrganization();
										String OrgName = vo.getOrganizationName();

										if (logchk.getOrg() == PKOrg) {
						%>
						<option value=<%=PKOrg%> selected><%=OrgName%></option>
						<%
							} else {
						%>
						<option value=<%=PKOrg%>><%=OrgName%></option>
						<%
							}
									}
								} else {
						%>
						<select size="1" name="selOrg"
						onchange="proceed(this.form,this.form.selOrg)">
							<option value=<%=logchk.getSelfOrg()%>><%=UserDetail[10]%></option>
					</select>
						<%
							} // End of isConsulting
						%>
					</select>
				</div>
			</div>


			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Survey")%>:
				</div>
				<div class="span5">
					<%
						int t = 0;
					%>
					<select name="surveyName"
						onchange="getID(this.form, this.form.surveyName.options[surveyName.selectedIndex].value, 1)">
						<option value=<%=t%>><%=trans.tslt("Please select one")%>
							<%
								/*********************
									 * Edit By James 14-Nov 2007
									 ***********************/

									// while(surveyList.next()) {
									for (int i = 0; i < surveyList.size(); i++) {
										votblSurvey voS = (votblSurvey) surveyList.elementAt(i);
										int ID = voS.getSurveyID();
										String name = voS.getSurveyName();

										int selectedSurvey = QR.getSurveyID();

										if (selectedSurvey != 0 && ID == selectedSurvey) {
							%>
						
						<option value=<%=selectedSurvey%> selected><%=name%>
							<%
								} else {
							%>
						
						<option value=<%=ID%>><%=name%>
							<%
								}
									}
							%>
						
					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Division")%>
					:
				</div>
				<div class="span5">
					<select name="selDiv"
						onChange="populateDept(this.form, this.form.selDiv)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								if (divisionList != null) {
										/*********************
										 * Edit By James 14-Nov 2007
										 ***********************/

										//	while(divisionList.next()) {
										for (int j = 0; j < divisionList.size(); j++) {
											voDivision voD = (voDivision) divisionList.elementAt(j);

											int ID = voD.getPKDivision();
											String name = voD.getDivisionName();
											int selectedDiv = QR.getDivisionID();

											if (selectedDiv != 0 && ID == selectedDiv) {
							%>
						
						<option value=<%=ID%> selected><%=name%>
							<%
								} else {
							%>
						
						<option value=<%=ID%>><%=name%>
							<%
								}
										}
									}
							%>
						
					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Department")%>
					:
				</div>
				<div class="span5">
					<select name="selDept"
						onChange="populateGrp(this.form, this.form.selDiv, this.form.selDept)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								if (departmentList != null) {
										/*********************
										 * Edit By James 14-Nov 2007
										 ***********************/

										//while(departmentList.next()) {
										for (int k = 0; k < departmentList.size(); k++) {
											voDepartment voDepartment = (voDepartment) departmentList
													.elementAt(k);

											//voDepartment.getLocation();
											int ID = voDepartment.getPKDepartment();
											String name = voDepartment.getDepartmentName();
											int selectedDept = QR.getDepartmentID();

											if (selectedDept != 0 && ID == selectedDept) {
							%>
						
						<option value=<%=ID%> selected><%=name%>
							<%
								} else {
							%>
						
						<option value=<%=ID%>><%=name%>
							<%
								}
										}
									}
							%>
						
					</select><span class="info" data-toggle="tooltip" data-placement="right" title="" data-original-title="Sort by departments for the ranking tables to be sorted by departments"><i class="icon-info-sign"></i></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Group")%>
					:
				</div>
				<div class="span5">
					<select name="groupName"
						onChange="getID(this.form, this.form.groupName.options[groupName.selectedIndex].value, 2)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								if (groupList != null) {
										/*********************
										 * Edit By James 14-Nov 2007
										 ***********************/

										//while(groupList.next()) {
										for (int l = 0; l < groupList.size(); l++) {
											voGroup voGroup = (voGroup) groupList.elementAt(l);

											int ID = voGroup.getPKGroup();
											String name = voGroup.getGroupName();
											int selectedGroup = QR.getGroupID();

											if (selectedGroup != 0 && ID == selectedGroup) {
							%>
						
						<option value=<%=ID%> selected><%=name%>
							<%
								} else {
							%>
						
						<option value=<%=ID%>><%=name%>
							<%
								}
										}
									}
							%>
						
					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Target")%>
					:
				</div>
				<div class="span5">
					<select name="target" id="target" onchange="javascript: loadTarget(this.form)">
					<%
					if(chosenOnes.size()==0){%>
						<option value=<%=t%> selected><%=trans.tslt("All")%></option>
						<option value="1">-Select Target-</option>
					<%}else{%>
						<option value=<%=t%>><%=trans.tslt("All")%></option>
						<option value="1" selected>-Select Target-</option>	
					<%}%>
					</select>
			
				</div>
			</div>
				<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" width="300">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h3 id="myModalLabel">List of Targets</h3>
					</div>
					
					<div class="modal-body" max>
					
					<table class="table">
						
							<%	if (targetList != null) {
										/*********************
										 * Edit By James 14-Nov 2007
										 ***********************/

										//	while(targetList.next()) {
											
										for (int m = 0; m < targetList.size(); m++) {
											votblAssignment voTarget = (votblAssignment) targetList
													.elementAt(m);
											int loginID = voTarget.getTargetLoginID();
											String name = voTarget.getFullName();
											int selectedTarget = QR.getTargetID();
											boolean isExist= false;
											for(int n=0;n<chosenOnes.size();n++){
												String[] harry = (String[]) chosenOnes.elementAt(n);
												int id=Integer.parseInt(harry[0]);
												if(id==loginID){
													isExist=true;
												}
											}
											if(isExist==true){
											%>	<tr id="<%=loginID%>" class="selectTarget">
													<td>
														<label id="<%=name%>" onClick="javascript:chooseTarget('<%=loginID%>','<%=name%>');">
															
																<%=name%></label>
													</td>
													<td>
														<button type="button" class="close" onclick="javascript: removeClass('<%=loginID%>','<%=name%>');" >×</button>
													</td>
												</tr><%
												}else{
													%>
													<tr id="<%=loginID%>">
													<td>
														<label id="<%=name%>"  onClick="javascript:chooseTarget('<%=loginID%>','<%=name%>');">
															
																<%=name%></label>
													</td>
													<td>
														<button type="button" class="close" onclick="javascript: removeClass('<%=loginID%>','<%=name%>');" >×</button>
													</td>
												</tr>
												<%}
											}
											
										
										}else{
											%>Please choose a survey before proceeding.<%
										}
									
							%>
							</table>
					</div>
					<div class="modal-footer">
						<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
						<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="submitTargets(this.form)"> Save</button>
						<!-- <button class="btn btn-primary" onclick="return submitTargets(this.form)">Save changes</button>-->
						
					</div>
				
				</div>
		<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Round")%>
					:
				</div>
				<div class="span5">
					<select name="round" id="round">
					<%
					if(roundList!=null){
						for(int i=0;i<roundList.size();i++){
							int roundNo =(Integer) roundList.elementAt(i);%>
						<option value=<%=roundNo%>><%=roundNo%></option>
						
					<%}
					}else {%>
						<option class="disabled" value="0">No rounds available</option>
					<%} %></select>
			
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					Language:
				</div>
				<div class="span5">
					<select size="1" name="langType">
						<%
							//Added a new language drop down box, Chun Yeong 1 Aug 2011
								String[] languages = {"English", "Indonesian", "Thai"};
								for (int i = 0; i < languages.length; i++) {
						%>
						<option value=<%=i%>><%=languages[i]%></option>
						<%
							} //End for loop, language array
									//End added a new language drop down box, Chun Yeong 1 Aug 2011
						%>

					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Template")%>
					:
				</div>

				<div class="span5">
					<select size="1" name="template" style="width: 320px">
						<%
							//Added display of templates of different languages, Chun Yeong 1 Aug 2011
								File dir = new File(Setting.getOOReportTemplatePath());
								String[] templates = dir.list();
								String defTemplate = "Individual Report Template.xls"; //default template in English
								String hidTemplate = "Individual Report Template.xls"; //template not used anymore in Individual Report but used in MOE Importance

								templates = dir.list(new FilenameFilter() {
									public boolean accept(File dir, String name) {
										return (name.startsWith("Individual Report Template")
												&& !name.toLowerCase().contains("no_normative")
												&& !name.toLowerCase().contains("nocpr")
												&& !name.toLowerCase().contains("sps")
												&& !name.toLowerCase().contains("old")
												&& !name.toLowerCase().contains("allianz") && !name
												.toLowerCase().contains("combined"));
									}
								});

								String sel = "";

								for (int i = 0; i < templates.length; i++) {
									if (templates[i].equalsIgnoreCase(defTemplate)) {
										sel = "selected";
									} //End if the default template is the same as the current template, Chun Yeong 1 Aug 2011
									if (templates[i].equalsIgnoreCase(hidTemplate)) {
										continue;
									} //End if the default template is the same as the current template, Chun Yeong 1 Aug 2011
						%>
						<option value="<%=templates[i]%>" <%=sel%>><%=templates[i]%></option>
						<%
							sel = "";
								} // End for loop, templates array
									//End of display of templates of different languages, Chun Yeong 1 Aug 2011
						%>
					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<table class="table" id="theOneTable">
					
						<thead>
						<tr>
							<td>Name:</td>
							<td><button type="button" onclick="javascript: loadModal();" class="btn"><i class="icon-pencil"></i>Edit</button></td>
						</tr>
						</thead>
						
						<tbody id="userTable">
						
						</tbody>
					</table>
				</div>
				<div class="span2">
					<table class="table" id="splitTable">
					
						<thead>
						<tr>
							<td>Rater Groups:</td>
						
						</tr>
						</thead>
						
						<tbody id="raterGroup" >
						
						</tbody>
					</table>
				</div>
				
				<div class="span2">
					<label onclick="splitPop()"><i class=icon-th-large></i>Split options</label>
					<label onclick="splitUndo()"><i class=icon-refresh></i>Undo Split</label>
				</div>
			</div>
			<div id="myModalSplit" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  			<div class="modal-header">
	   			 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	   			 <h3 id="myModalLabel">Split Options</h3>
	 		 </div>
	 		 <div class="modal-body">
	  
			
					<% if(newName!=null){%>
					Group Name: <input onchange="checkEmpty(newGroupName)" type="text" value = "<%=newName%>" name="newGroupName">
					<%}else{ %>
					Group Name: <input onchange="checkEmpty(newGroupName)" type="text"  name="newGroupName">
				 	<%} %>
				
					 <div class="groups">
					 	<table class="table ">
						 	<tbody>
						    <%
						    if(raters!=null && raters.size()!=0){
						    	for(int f=0;f<raters.size();f++){
						    		String raterCode = (String)raters.elementAt(f);
						    		String raterCode1 = "";
						    		if(raterCode.equalsIgnoreCase("SUP")){
						    			raterCode1="Superior";
						    		}else if(raterCode.equalsIgnoreCase("DIR")){
						    			raterCode1="Direct";
						    		}else if(raterCode.equalsIgnoreCase("IDR")){
						    			raterCode1="Indirect";
						    		}else{
						    			raterCode1=raterCode;
						    		}
						    		
						    		%>
						    			<tr class="raterCode" id="<%=raterCode%>" onclick="newGroup('<%=raterCode%>')"><td><label><%=raterCode1%></label></td></tr>
						    	<%}
						    }
						    	%>
						    </tbody>
					    </table>
					  </div>
					
					  </div>
	  <div class="modal-footer">
	    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	    <button class="btn btn-primary save" onClick = "addGroup(newGroupName)" data-dismiss="modal" aria-hidden="true" >Merge</button>
	    
	  </div>
	</div>
			
		
			<div class="row-fluid">
				<div class="span5">
					<h3>
						<b>Report Customisation:</b>
					</h3>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="chkGroupCPLine" checked>
						With Group CP Line</label>
				</div>
				<div class="span3">
					<label><input type="radio" value="2" name="optIndividual"
						checked> Individual Report</label>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="chkNormative" checked>
						With Normative</label>
				</div>
				<div class="span4">
					<label><input type="radio" value="1" name="optIndividual">
						Simplified Individual Report (without charts)</label>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="chkSplit"> With
						Reverse Split Option</label>
				</div>
			</div>	
			<div class="row-fluid">
				<div class="span3">
					<%
						if (CE_Survey.getBreakCPR(QR.getSurveyID()) == 1) {
					%>
					<label><input type="checkbox" name="chkBreakCPR" checked>
						<%
							} else {
						%> <label><input type="checkbox" name="chkBreakCPR">
							<%
								}
							%> Breakdown CPR</label>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="chkDevGuide">
						Development Guide</label>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="chkDevMap">
						Development Map</label>
				</div>
			</div>
				<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="weightedAverage">
						Weighted Average</label>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3">
					<label><input type="checkbox" name="surveyOverview">
						Survey Overview</label>
				</div>
			</div>
			<div class="row-fluid">
				<%
					if (!logchk.getCompanyName().equalsIgnoreCase("demo")
								|| userType == 1) {
				%>
				<!-- Edited by Tracy 01 Sep 08 -->
				<!-- Edited by Chun Yeong 2 Jun 2011 -->
				<div class="row-fluid">
					<div class="offset1 span2">
						<button type="button" class="btn btn-primary generateBtn" name="btnOpen"
							 data-loading-text="Generating... "value="<%=trans.tslt("Generate")%>"
							onclick="confirmOpen(this.form, this.form.optIndividual, this.form.surveyName, this.form.targetName, this.form.chkDevGuide, this.form.chkDevMap, this.form.chkNormative, this.form.chkGroupCPLine, this.form.chkSplit, this.form.langType, this.form.template);">Generate</button>
					</div>
					<!-- End edit by Chun Yeong 2 Jun 2011 -->
					<!-- End edit by Tracy 01 Sep 08 -->
					
						<a type="button" id="dlpage" class ="btn" href="GenerationComplete.jsp" >Download Page</a></div>
					
					<%
						} else {
					%>
					<input type="button" name="btnOpen"
						value="<%=trans.tslt("Preview")%>" disabled> <input
						type="button" name="btnCancel" value="<%=trans.tslt("Cancel")%>"
						disabled>
					<%
						} // end if(compID != 2 || userType == 1)
					%>


					<%
						}
					%>
				</div>
				
			</div>
		</form>
			<div id="myModalLoad" class="modal hide fade" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">×</button>
				<h3 id="myModalLabel">Report Generation</h3>
			</div>
			<div class="modal-body">
				<p>This process may take awhile. Please do not close the main window.</p>
			</div>
			
	
    
	</div>
			
	
				<%@ include file="Footer.jsp"%>
</body>
</html>

