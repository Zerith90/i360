<%@ page import="java.sql.*,
                java.io.*,
                 java.text.DateFormat,
                 java.util.*,
                 java.util.Date,
                 java.text.*,
                 java.lang.String,
                 java.util.Vector,
				 java.util.LinkedList,
				 CP_Classes.vo.*,CP_Classes.SurveyResult"%>  

<html>
<head>
<title>Importance Individual Report (MOE)</title>

<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>

</head>

<body>
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="QR" class="CP_Classes.QuestionnaireReport" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/> 
<jsp:useBean id="User_Jenty" class="CP_Classes.User_Jenty" scope="session"/>
<jsp:useBean id="User" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="USR" class="CP_Classes.User" scope="session"/> 
<jsp:useBean id="ImptExcelIndividual" class="CP_Classes.ImptIndividualReport" scope="session"/> 
<jsp:useBean id="DG" class="CP_Classes.DevelopmentGuide" scope="session"/> 
<jsp:useBean id="Setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="AdvSetting" class="CP_Classes.AdvanceSettings" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="SR" class="CP_Classes.SurveyResult" scope="session"/>
<jsp:useBean id="ORG" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="email" class="CP_Classes.ReminderCheck" scope="session"/>
<% 	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 %>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>

<script language="javascript">

var x = parseInt(window.screen.width) / 2 - 200;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 200;
var myWindow;

function setTarget(form, field){
	form.action="ImptIndividualReport.jsp?target="+field.value;
	form.method="post";
	form.submit();
}

function setSurveyImpt(form, field){
	form.action="ImptIndividualReport.jsp?surveyImpt="+field.value;
	form.method="post";
	form.submit();
}
function send(form){
	checkField(form);
	form.action="ImptIndividualReport.jsp?send=1";
	form.method="post";
	form.submit();
}
function getID(form, ID, type)
{
	var typeSelected = "";
	
	//if(ID != 0) {
		switch(type) {
			case 1: typeSelected = "surveyID";
					  break;
			case 2: typeSelected = "groupID";
					  break;
			case 3: typeSelected = "targetID";
					  break;
		}
		var query = "ImptIndividualReport.jsp?" + typeSelected + "=" + ID;
		form.action = query;
		form.method = "post";
		form.submit();
/*	} else {
		alert("<%=trans.tslt("Please select the options")%> !");
		return false;
	}
*/	return true;	
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
function confirmOpen(form, optReport, survey, surveyImpt, target, chkDG, chkDMap, chkNormative, chkGroupCPLines, chkSplit, langType)
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
	
	if(survey.value != 0 && surveyImpt.value != 0)
	{
		if(target.value == 0)
		{
			form.btnOpen.value = "<%=trans.tslt("Processing")%> ...";
			form.btnOpen.disabled = true;
		}

		form.action = "ImptIndividualReport.jsp?open="+type+"&cancel=0&survey="+survey.value+"&surveyimpt="+surveyImpt.value;
		form.submit();
		return true;	
		
	} else {
		alert("<%=trans.tslt("Please select Survey")%>");
		return false;
	}
	
	/*
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
			myWindow=window.open('ImptIndividualReportAll.jsp','IndividualReportPopUp','scrollbars=no, width=480, height=250');
			var query = "ImptIndividualReportAll.jsp";
			myWindow.moveTo(x,y);
			myWindow.location.href = query;
	
			form.action = "ImptIndividualReport.jsp?open="+type+"&cancel=0&survey="+survey.value;
			form.submit();
			return true;	
		}
		
	} else {
		alert("<%=trans.tslt("Please select the survey name")%> !");
		return false;
	}
	*/
	}//End of checkField
}

function cancelPrint(form)
{
	form.btnOpen.value = "<%=trans.tslt("Preview")%>";
	form.action = "ImptIndividualReport.jsp?cancel=1";
	form.submit();
	return true;
}

function finishPrint(form)
{
	form.btnOpen.value = "<%=trans.tslt("Preview")%>";
	form.action = "ImptIndividualReport.jsp";
	form.submit();
	return true;
}

//function add
/*------------------------------------------------------------start: Login modification 1------------------------------------------*/
/*	choosing organization*/

function proceed(form,field)
{
	form.action="ImptIndividualReport.jsp?proceed="+field.value;
	form.method="post";
	form.submit();
}	

function populateDept(form, field)
{
	form.action="ImptIndividualReport.jsp?div="+field.value;
	form.method="post";
	form.submit();
}

function populateGrp(form, field1, field2)
{
	form.action="ImptIndividualReport.jsp?div=" + field1.value + "&dept="+ field2.value;
	form.method="post";
	form.submit();
}

</script>

<p>
  <%
	//response.setHeader("Pragma", "no-cache");
	//response.setHeader("Cache-Control", "no-cache");
	//response.setDateHeader("expires", 0);

	String username=(String)session.getAttribute("username");
	
	Vector nameOfFilesCreated = new Vector<String>();
	Boolean bHasError = false;

	if (!logchk.isUsable(username)) 
	{%> 
    	<script>
			parent.location.href = "index.jsp";
		</script>
<%  } 
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

	Vector surveyList = QR.getSurvey(compID, OrgID);
	Vector surveyListImpt = QR.getSurvey(compID, OrgID); 
	Vector divisionList = null;
	Vector departmentList = null;
	Vector groupList  = null;
	Vector targetList = null;
	Vector raterList  = null;
		
	int surveyID = QR.getSurveyID();
	int surveyIDImpt = 0;
	int divisionID = QR.getDivisionID();
	int departmentID = QR.getDepartmentID();
	int groupID  = QR.getGroupID();	
	int target   = QR.getTargetID();
	//Added by Tracy 01 Sep 08
	String chkNormative="";
	//End add by Tracy**
	
	//Added by Chun Yeong 2 Jun 2011
	String chkGroupCPLine="";
	//End add by Chun Yeong**
	//Added by Chun Yeong 13 Jun 2011
	String chkSplit = "";
	//End add by Chun Yeong**
    String chkFormat ="";
	String chkDG ="";
	String chkDGOnly ="";
	//Added by Chun Yeong 1 Aug 2011
	int langType = 0;
	String template = "";
	//End add by Chun Yeong**
	
	if(surveyID != 0)
	{
		divisionList = QR.getDivision(surveyID);
		departmentList = QR.getDepartment(surveyID, divisionID);
		groupList = QR.getGroup(surveyID, QR.getDivisionID(), QR.getDepartmentID());
		targetList = QR.getTarget(surveyID, divisionID, departmentID, groupID);
		
		int count = 0;
		/*while(targetList.next())
		{
			count ++;
		}*/
		count=targetList.size();
	}

	
	if(request.getParameter("cancel") != null)
	{
		int iCancelled = Integer.parseInt(request.getParameter("cancel"));
		ImptExcelIndividual.setCancelPrint(iCancelled);		// 0=Proceed; 1=Cancelled
	} else {
		ImptExcelIndividual.setCancelPrint(0);
	}
	if(request.getParameter("surveyImpt") != null) {
		int id = Integer.parseInt(request.getParameter("surveyImpt"));
		QR.setSurveyIDImpt(id);
	}
	if(request.getParameter("target") != null) {
		int id = Integer.parseInt(request.getParameter("target"));
		QR.setTargetID(id);
	}
	
	if(request.getParameter("surveyID") != null) {
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
		groupList = QR.getGroup(ID, QR.getDivisionID(), QR.getDepartmentID());
		targetList = QR.getTarget(ID,  QR.getDivisionID(),  QR.getDepartmentID(), QR.getGroupID());
	}
	else if(request.getParameter("groupID") != null) {
		int group = Integer.parseInt(request.getParameter("groupID"));
		QR.setGroupID(group);
		QR.setRaterID(0);
		QR.setTargetID(0);			
		targetList = QR.getTarget(QR.getSurveyID(),  QR.getDivisionID(),  QR.getDepartmentID(), QR.getGroupID());
	} 
	else if(request.getParameter("targetID") != null) {
		int ID = QR.getSurveyID();
		int targetID = Integer.parseInt(request.getParameter("targetID"));
		QR.setTargetID(targetID);
		QR.setRaterID(0);
		raterList = QR.getRater(surveyID, groupID, targetID);
	}
	
	if (request.getParameter("div") != null)
	{
		QR.setDepartmentID(0);
		QR.setGroupID(0);
		int ID = QR.getSurveyID();
		int div = Integer.parseInt(request.getParameter("selDiv"));
		QR.setDivisionID(div);
		departmentList = QR.getDepartment(ID, QR.getDivisionID());
		groupList = QR.getGroup(ID, QR.getDivisionID(), QR.getDepartmentID());
		targetList = QR.getTarget(ID,  QR.getDivisionID(),  QR.getDepartmentID(), QR.getGroupID());
	}
	
	if(request.getParameter("dept") != null)
	{
		QR.setGroupID(0);
		int ID = QR.getSurveyID();
		int dept = Integer.parseInt(request.getParameter("selDept"));
		QR.setDepartmentID(dept);
		groupList = QR.getGroup(ID, QR.getDivisionID(), QR.getDepartmentID());
		targetList = QR.getTarget(ID,  QR.getDivisionID(),  QR.getDepartmentID(), QR.getGroupID());
	}
	
	if(request.getParameter("send") != null){
		
		
		String reminder = "remind";
		if (request.getParameter("chkNormative")!= null) {
			chkNormative= request.getParameter("chkNormative");
		}
		//End add by Tracy 01 Sep 08***

		//Added to get Group CP Line option by Chun Yeong 2 Jun 2011
		if (request.getParameter("chkGroupCPLine")!= null) {
			chkGroupCPLine= request.getParameter("chkGroupCPLine");
		}
		//End to get Group CP Line option by Chun Yeong 2 Jun 2011***

		//Added to get follow template option by Chun Yeong 13 Jun 2011
		if (request.getParameter("chkSplit")!=null) {
			chkSplit= request.getParameter("chkSplit");
		}
		if (request.getParameter("chkFormat")!=null) {
			chkFormat= request.getParameter("chkFormat");
		}
		if (request.getParameter("chkDG")!=null) {
			chkDG= request.getParameter("chkDG");
		}
		if (request.getParameter("template")!=null) {
			template = request.getParameter("template");
		}
		if (request.getParameter("chkDGOnly")!=null) {
			chkDGOnly = request.getParameter("chkDGOnly");
		}
		Vector options = new Vector();
		options.add(chkNormative);
		options.add(chkGroupCPLine);
		options.add(chkSplit);
		options.add(chkFormat);
		options.add(chkDG);
		options.add(template);
		options.add(chkDGOnly);
		int errorFlag = 0;
		
		
		Vector rec = QR.getTarget(QR.getSurveyID(), QR.getDivisionID(), QR.getDepartmentID(), QR.getGroupID(), QR.getRoundID());
		Vector recipients = new Vector();
		if(QR.getTargetID() != 0){
			for(Object o:rec){
				votblAssignment vo = (votblAssignment)o;
				if(vo.getTargetLoginID() == QR.getTargetID()){
					recipients.add(o);
				}
			}
		}
		else{
			recipients = rec;
		}
		 System.out.println("Division " + QR.getDivisionID());
		 System.out.println("Department  " + QR.getDepartmentID());
		 System.out.println("group " + QR.getGroupID());
		 System.out.println("target " + QR.getTargetID());
	     System.out.println("round " + QR.getRoundID());
		surveyIDImpt = QR.getSurveyIDImpt();
		
		Date timeStamp = new java.util.Date();
		Vector Target = new Vector();
		SurveyResult s = new SurveyResult();
		SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
		String temp  =  dFormat.format(timeStamp);
		timeStamp = new java.util.Date();
		dFormat = new SimpleDateFormat("ddMMyyHHmmss");
		temp  =  dFormat.format(timeStamp);

		
		Target = s.TargetID(surveyID, divisionID, departmentID, groupID);

		for (int j = 0; j < Target.size(); j++)
		{
			voUser vo = (voUser)Target.get(j);
			if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyID ,divisionID, departmentID, groupID)){						
				 if (!s.isAllRaterRated(surveyID, groupID, vo.getTargetLoginID()))                	 
	                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 1);
	             else
	                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 0);						
			}
			if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyIDImpt ,divisionID, departmentID, groupID)){						
				 if (!s.isAllRaterRated(surveyIDImpt, groupID, vo.getTargetLoginID()))                	 
	                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 1);
	             else
	                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 0);						
			}
		}	
		
		errorFlag = email.Email_With_Importance_Report(recipients, surveyID, surveyIDImpt,options,  logchk.getPKUser()); 
		String url = email.getStoreURL();
		String file_name = url.substring(url.indexOf("(") - 12);
		System.out.println(file_name);
		String output = Setting.getReport_Path() + "\\" + file_name;
		nameOfFilesCreated.add(output);
		File f = new File (output);
System.out.println("OUTPUT = " + output );
		//set the content type(can be excel/word/powerpoint etc..)
		response.reset();
		response.setContentType ("application/xls");
		
		// set the header and also the Name by which user will be prompted to save
		// Updated filename by which user will be prompted to save to this pattern "ImptIndividualReport_<FamilyName><GivenName>.xls", Desmond 28 Oct 09
		response.addHeader ("Content-Disposition", "attachment;filename=\""+file_name+"\"");

		//get the file name
		String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
System.out.println("NAME = " + name);
		InputStream in = new FileInputStream(f);
System.out.println("INPUTSTREAM = " + in);
		ServletOutputStream outs = response.getOutputStream();
System.out.println("HERE 4");
		int bit = 256;
		int i = 0;
System.out.println("HERE 5");
		try {
			while ((bit) >= 0) {
				bit = in.read();
				outs.write(bit);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}

		outs.flush();
		outs.close();
		in.close();
		
		
		if (errorFlag == 0) {
			out.println(trans.tslt(recipients.size() + " Emails Sent") + "!");
		}
		else if (errorFlag == 1) {
%> <script>alert('You need to create a participant notification template <%=reminder %> before sending ')</script>  <%			
		}else if(errorFlag == 2){
			out.println("No Rater to send or survey not open!");
		}
        else if(errorFlag == 3){
        
				// Added error prompt for case when Survey is NOT Open, rater does not exist or rater has NO targets left to rate for the selected survey, Desmond 11 August 2010
				out.println("Survey is NOT Open, rater does not exist or rater has NO targets left to rate for this survey!");
        }
	 else if(errorFlag==100){
	%>Email sent! But error(s) occurred. Please refer to <a href='SentFailedEmail.jsp'>View Sent Failed Emails</a><%
}
		
		
		
	}
		
		if(request.getParameter("open") != null)
		{
			int type = Integer.parseInt(request.getParameter("open"));
			
			surveyID = Integer.parseInt(request.getParameter("survey"));
			surveyIDImpt = Integer.parseInt(request.getParameter("surveyimpt"));
			divisionID = QR.getDivisionID();
			departmentID = QR.getDepartmentID();
			groupID = QR.getGroupID();
			//Added getting Normative option by Tracy 01 Sep 08
		
			if (request.getParameter("chkNormative")!= null) {
				chkNormative= request.getParameter("chkNormative");
			}
			//End add by Tracy 01 Sep 08***
			
			//Added to get Group CP Line option by Chun Yeong 2 Jun 2011
			if (request.getParameter("chkGroupCPLine")!= null) {
				chkGroupCPLine= request.getParameter("chkGroupCPLine");
			}
			//End to get Group CP Line option by Chun Yeong 2 Jun 2011***

			//Added to get follow template option by Chun Yeong 13 Jun 2011
			if (request.getParameter("chkSplit")!=null) {
				chkSplit= request.getParameter("chkSplit");
			}
			//End to get follow template option by Chun Yeong 13 Jun 2011***
			
			//Added to get language option by Chun Yeong 1 Aug 2011
			if (request.getParameter("langType")!=null) {
				langType = Integer.parseInt(request.getParameter("langType"));
			}

			if (request.getParameter("template")!=null) {
				template = request.getParameter("template");
			}
			//End to get language option by Chun Yeong 1 Aug 2011***
			
			//Added checking null condition by Ha 16/05/08
			if (request.getParameter("targetName") != null)
			{
					target = Integer.parseInt(request.getParameter("targetName"));					
					
					QR.setTargetID(target);
										
					String surveyName = "";
					int iSurveyLevel = 0;	// 0=Comp, 1=KB
					votblSurvey voSurvey = SR.SurveyInfo(surveyID);
				
					surveyName = voSurvey.getSurveyName();	// Get username (for sending of email)
					iSurveyLevel = voSurvey.getLevelOfSurvey();
					
					int iNameSeq = ORG.getNameSeq(logchk.getOrg());
					String sUserEmail = User_Jenty.getUserEmail(logchk.getPKUser());	// Email of the person who gen the report
					
					String [] userInfo = USR.getUserDetail(target, iNameSeq);	// Get target's fullname (for sending of email)
					String targetName = userInfo[0] + " " + userInfo[1];
					
					boolean bDG = false;
					boolean bDMap = false;
					
					if(request.getParameter("chkDevGuide") != null)
						bDG = true;
						
					if(request.getParameter("chkDevMap") != null)
					{
						if(iSurveyLevel != 0)
							bDMap = true;
						else
							bDMap = false;				
					}
					
					boolean bMoreThanOne = false; // Flag to check whether user generate more than 1 type of report
					
					if( (type == 1 || type == 2) && bDG == true && bDMap == true )
						bMoreThanOne = true;	// Ind, DG & DMap chosen
					else if( (type == 1 || type == 2) && bDG == true )
						bMoreThanOne = true;	// Ind & DG chosen
					else if( (type == 1 || type == 2) && bDMap == true )
						bMoreThanOne = true;	// Ind & DMap chosen
					else if( bDG == true && bDMap == true )
						bMoreThanOne = true;	// DG & DMap chosen
					else
						bMoreThanOne = false;
					
					// If user doesn't specify a report to generate, prompt message
					if(type == 0 && bDG == false && bDMap == false)
					{
				%>		<script>
							alert("Please choose a report to generate");
						</script>
				<%	}
					else
					{
						if(target != 0)
						{
							Date timeStamp = new java.util.Date();
							Vector Target = new Vector();
							SurveyResult s = new SurveyResult();
							SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
							String temp  =  dFormat.format(timeStamp);
												
							if(type == 1 || type == 2)
							{
								timeStamp = new java.util.Date();
								dFormat = new SimpleDateFormat("ddMMyyHHmmss");
								temp  =  dFormat.format(timeStamp);
		
								String file_name = "Importance Report for " + targetName+ "(" + temp + ").xls";
								//Added by Ha 17/06/08 to automatically calculate
								//when generating report
								//Re-edit by Ha 04/07/08 to calculate for all target of that group
								Target = s.TargetID(surveyID, divisionID, departmentID, groupID);
			
								for (int j = 0; j < Target.size(); j++)
								{
									voUser vo = (voUser)Target.get(j);
									if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyID ,divisionID, departmentID, groupID)){						
										 if (!s.isAllRaterRated(surveyID, groupID, vo.getTargetLoginID()))                	 
							                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 1);
							             else
							                 s.CalculateStatus(vo.getTargetLoginID(),surveyID, divisionID, departmentID, groupID, 0);						
									}
									if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyIDImpt ,divisionID, departmentID, groupID)){						
										 if (!s.isAllRaterRated(surveyIDImpt, groupID, vo.getTargetLoginID()))                	 
							                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 1);
							             else
							                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 0);						
									}
								}	
								
								//Edited printing ind report with Normative option by Tract 01 Sep 08
								//Edited printing ind report with Group CP Line option by Chun Yeong 2 Jun 2011
								//Edited allowing Split option, Chun Yeong 13 Jun 2011
								//Edited allowing Language option, Chun Yeong 1 Aug 2011
								//Edited allowing Template option, Chun Yeong 1 Aug 2011
								ImptExcelIndividual.Report(surveyID,surveyIDImpt, target, pkUser, file_name, type, chkNormative, chkGroupCPLine, chkSplit, langType, template);
								
								
								/*
								*
								* Changing Report
								*
								*/
								
								
								
								
								
								//End edit by Tracy 01 Sep 08
								
								System.out.println("OKKK");
									//String displayed = "List of Competencies.xls";
					
		
					//System.out.println(logchk.getCompany() + "--" +  FKOrg + "--" +  comp + "--" +  FKJob + "--" +  origin + "--" +  logchk.getPKUser());
					//Rpt1.generateReport(logchk.getCompany(), FKOrg, comp, FKJob, origin, file_name, logchk.getPKUser(), lang);
				
								if(bMoreThanOne == false)
								{
									System.out.println("bMoreThanOne = false, Only 1 report is selected");
									String output = Setting.getReport_Path() + "\\" + file_name;
									nameOfFilesCreated.add(output);
									File f = new File (output);
						System.out.println("OUTPUT = " + output );
									//set the content type(can be excel/word/powerpoint etc..)
									response.reset();
									response.setContentType ("application/xls");
									
									// set the header and also the Name by which user will be prompted to save
									// Updated filename by which user will be prompted to save to this pattern "ImptIndividualReport_<FamilyName><GivenName>.xls", Desmond 28 Oct 09
									response.addHeader ("Content-Disposition", "attachment;filename=\""+file_name+"\"");
		
									//get the file name
									String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
						System.out.println("NAME = " + name);
									InputStream in = new FileInputStream(f);
						System.out.println("INPUTSTREAM = " + in);
									ServletOutputStream outs = response.getOutputStream();
						System.out.println("HERE 4");
									int bit = 256;
									int i = 0;
						System.out.println("HERE 5");
									try {
										while ((bit) >= 0) {
											bit = in.read();
											outs.write(bit);
										}
									} catch (IOException ioe) {
										ioe.printStackTrace(System.out);
									}
						
									outs.flush();
									outs.close();
									in.close();
								
								}	// END if(bMoreThanOne == false)
								else
								{
									// User chose to print more than 1 type of report, send through email instead
									ImptExcelIndividual.sendIndividualReport(targetName, surveyName, sUserEmail, file_name, surveyID);
								}
						
							}
							
							// Development Guide
							if(bDG == true)
							{	
								String file_name = "Development Guide" + temp + ".xls";	
								System.out.println("[ImptIndividualReport] DG. Survey = " + surveyID + ", Target = " + target);
								DG.SelTarget(surveyID, target, pkUser, file_name);
								
								if(bMoreThanOne == false)
								{					
									String output = Setting.getReport_Path() + "\\" + file_name;
									nameOfFilesCreated.add(output);
									File f = new File (output);
									
									response.reset();
									response.setContentType ("application/xls");
									response.addHeader ("Content-Disposition", "attachment;filename=\"Development Guide.xls\"");
								
									//get the file name
									String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
									
									InputStream in = new FileInputStream(f);
									ServletOutputStream outs = response.getOutputStream();
									
									int bit = 256;
									int i = 0;
						
									try {
										while ((bit) >= 0) {
											bit = in.read();
											outs.write(bit);
										}
									} catch (IOException ioe) {
										ioe.printStackTrace(System.out);
									}
					
									outs.flush();
									outs.close();
									in.close();
								}
								else
								{
									// User chose to print more than 1 type of report, send through email instead
									DG.sendDevelopmentGuide(targetName, surveyName, sUserEmail, file_name, surveyID);
								}
								
							}
							
							// Development Map
							if(bDMap == true)
							{	
								String file_name = "Development Map" + temp + ".xls";				
								boolean bBreakdown = AdvSetting.bIsBreakdown(surveyID);
								
								System.out.println("[ImptIndividualReport] DMap. Survey = " + surveyID + ", Target = " + target);
								ImptExcelIndividual.reportDevelopmentMap(surveyID, target, file_name, bBreakdown);
								
								if(bMoreThanOne == false)
								{						
									String output = Setting.getReport_Path() + "\\" + file_name;
									nameOfFilesCreated.add(output);
									File f = new File (output);
									
									response.reset();
									response.setContentType ("application/xls");
									response.addHeader ("Content-Disposition", "attachment;filename=\"Development Map.xls\"");
								
									//get the file name
									String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
									
									InputStream in = new FileInputStream(f);
									ServletOutputStream outs = response.getOutputStream();
									
									int bit = 256;
									int i = 0;
						
									try {
										while ((bit) >= 0) {
											bit = in.read();
											outs.write(bit);
										}
									} catch (IOException ioe) {
										ioe.printStackTrace(System.out);
									}
					
									outs.flush();
									outs.close();
									in.close();
								}
								else
								{
									// User chose to print more than 1 type of report, send through email instead
									ImptExcelIndividual.sendDevelopmentMap(targetName, surveyName, sUserEmail, file_name, surveyID);
								}
								
							}	
						} else {	//generate multiple reports here (rater != 0)
							
							target = Integer.parseInt(request.getParameter("targetName"));
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
							Vector allTarget = ImptExcelIndividual.AllTargets(surveyID, divisionID, departmentID, groupID, OrgID);
							int totalReports = allTarget.size();
														
							int totalGenerated = 0;
							
							for(int i=0; i<totalReports; i++) 
							{
								// for delay purpose
								if(i != 0)
									for(int j=0;j<100000;j++);
						
								if (ImptExcelIndividual.getCancelPrint() == 0)
								{
									//generate individual report.
									String [] data = (String[])(allTarget.elementAt(i));
									surveyName 	 = data[0];
									int targetID = Integer.parseInt(data[1]);
									targetName	 = data[2];					
				%>
									<script>
										window.status = "Generating Importance Individual Report for " + "<%=targetName%> (<%=total%> of <%=totalReports%>.............)";
									</script>
				<%				
									Date timeStamp = new java.util.Date();
									SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
									String temp  =  dFormat.format(timeStamp);
									
									//try {
									request.getSession().setMaxInactiveInterval(1800);
									
									if(type == 1 || type == 2)
									{
										String file_name = "Importance Report for " + targetName + " (" + temp + ").xls";
										
										nameOfFilesCreated.add(file_name);
										
										System.out.println("Generating Importance Report for " + targetName + " (" + total + " of " + totalReports + ")....");
										
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
											if (!s.checkCalculationStatusComplete(vo.getTargetLoginID(), surveyIDImpt ,divisionID, departmentID, groupID))
											{						
												 if (!s.isAllRaterRated(surveyIDImpt, groupID, vo.getTargetLoginID()))                	 
									                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 1);
									             else
									                 s.CalculateStatus(vo.getTargetLoginID(),surveyIDImpt, divisionID, departmentID, groupID, 0);						
											}
										}	
										
										//Edited printing ind report with Normative option By Tracy 01 Sep 08***
										//Edited printing ind report with Group CP Line option by Chun Yeong 2 Jun 2011
										//Edited allowing Split option, Chun Yeong 13 Jun 2011
										//Edited allowing Language option, Chun Yeong 1 Aug 2011
										//Edited allowing Template option, Chun Yeong 1 Aug 2011
										ImptExcelIndividual.Report(surveyID,surveyIDImpt, targetID, pkUser, file_name, type, chkNormative, chkGroupCPLine, chkSplit, langType, template);									
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
										ImptExcelIndividual.reportDevelopmentMap(surveyID, targetID, file_name, bBreakdown);
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
						}	// end if (target != 0)					
					} // END if(type == 0 && bDG == false && bDMap == false)
			}
		}	// end if (request.getParameter("open") != null)
		
		//if (nameOfFilesCreated.size() != 0) {
			//bHasError = ImptExcelIndividual.ZipAllFiles(nameOfFilesCreated);
		//}
%>
<form name="ImptIndividualReport" method="post">
<%
	if (bHasError) {
%>
<b><font face="Arial" color="#0000FF">
<%= trans.tslt("Generation process completed UnSuccessfully, ")%><br/>
<%= trans.tslt("Please see ")%><a href="ErrorLog.jsp">ErrorLog.xls</a>
<%= trans.tslt("for the details of generation failed data")%>
</font></b>
<%
	}
%>
<br/>
<table boQEr="0" width="439" cellspacing="0" cellpadding="0" font style='font-size:10.0pt;font-family:Arial'>
	<tr>
	  <td colspan="4" align="left"><b><font color="#000080" size="2" face="Arial"><%=trans.tslt("Importance Individual Report")%> </font></b></td>
    </tr>
</table>
<font size="2">&nbsp;</font><font size="2"> </font>
<table boQEr="0" width="438" cellspacing="3" cellpadding="0" style='font-size:10.0pt;font-family:Arial' border="2" bordercolor="#3399FF">
	<tr>
		<td width="100" align="right" bordercolor="#FFFFFF"><%=trans.tslt("Organisation")%> 
		:</td>
		<td width="19" bordercolor="#FFFFFF">&nbsp;</td>
		<td width="207" bordercolor="#FFFFFF">
<%
// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = ORG.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){ %>
		<select size="1" name="selOrg" onChange="proceed(this.form,this.form.selOrg)">
		<option value="0" selected><%=trans.tslt("All")%></option>
	<%
		Vector vOrg = logchk.getOrgList(logchk.getCompany());

		for(int i=0; i<vOrg.size(); i++)
		{
			votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
			int PKOrg = vo.getPKOrganization();
			String OrgName = vo.getOrganizationName();

			if(logchk.getOrg() == PKOrg)
			{ %>
				<option value=<%=PKOrg%> selected><%=OrgName%></option>
			<% } else { %>
				<option value=<%=PKOrg%>><%=OrgName%></option>
			<%	}	
		} 
	} else { %>
		<select size="1" name="selOrg" onchange="proceed(this.form,this.form.selOrg)">
		<option value=<%=logchk.getSelfOrg()%>><%=UserDetail[10]%></option>
	<% } // End of isConsulting %>
</select></td>
		<td width="212" align="left" bordercolor="#FFFFFF">&nbsp;</td>
	</tr>
	</table>


  <table width="438" boQEr="0" style='font-size:10.0pt;font-family:Arial' bgcolor="#FFFFCC" border="2" bordercolor="#3399FF" height="247" cellspacing="3">
    <tr>
      <td width="119" align="right" bordercolor="#FFFFCC"><%=trans.tslt("Survey")%> 
		:</td>
      <td width="1" bordercolor="#FFFFCC">&nbsp;</td>
      <% int t = 0; %>
	  <td width="110" bordercolor="#FFFFCC">
	  <select name="surveyName" onchange = "getID(this.form, this.form.surveyName.options[surveyName.selectedIndex].value, 1)">
	  <option value=<%=t%>><%=trans.tslt("Please select one")%>
	  <%
	  /*********************
	   * Edit By James 14-Nov 2007
	   ***********************/
	
	 // while(surveyList.next()) {
	 for(int i=0;i<surveyList.size();i++){
	  votblSurvey voS=(votblSurvey)surveyList.elementAt(i);
			int ID = voS.getSurveyID();
			String name = voS.getSurveyName();
	  		
			int selectedSurvey = QR.getSurveyID();
			
			if(selectedSurvey != 0 && ID == selectedSurvey) {
	  %>
	  	<option value = <%=selectedSurvey%> selected><%=name%>
	  <% } else {  %>
	  	<option value = <%=ID%>><%=name%>	  
	  <% }
		   } 
	  %>
      </select></td>
    </tr>
    
    <tr>
      <td width="119" align="right" bordercolor="#FFFFCC">Importance Survey:</td>
      <td width="1" bordercolor="#FFFFCC">&nbsp;</td>
	  <td width="110" bordercolor="#FFFFCC">
	  <select name="surveyNameImpt" onChange="setSurveyImpt(this.form, this.form.surveyNameImpt)">
	  <option value=<%=t%>><%=trans.tslt("Please select one")%>
	  <%
	 for(int i=0;i<surveyListImpt.size();i++){
	  votblSurvey voS=(votblSurvey)surveyListImpt.elementAt(i);
			int ID = voS.getSurveyID();
			String name = voS.getSurveyName();
			int selectedSurvey = QR.getSurveyID();
			
			if(selectedSurvey != 0 && ID == selectedSurvey) {
	  %>
	  	<option value = <%=selectedSurvey%> disabled><%=name%>
	  <% } else { if(ID == QR.getSurveyIDImpt() ) {%>
	  	<option value = <%=ID%> selected ><%=name%>	  
	  <% }else{%>
	  	<option value = <%=ID%>><%=name%>	<% 
		  }
	  }
		   } 
	  %>
      </select></td>
    </tr>
    
    <tr>
      <td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Division")%> : </td>
      <td bordercolor="#FFFFCC">&nbsp;</td>
	  <td bordercolor="#FFFFCC"><select name="selDiv" onChange="populateDept(this.form, this.form.selDiv)">
	  <option value=<%=t%>><%=trans.tslt("All")%>
	  <% 
	  if(divisionList != null) {
	  /*********************
	   * Edit By James 14-Nov 2007
	   ***********************/
		  
		  //	while(divisionList.next()) {
		  for(int j=0;j<divisionList.size();j++){
		   voDivision voD=(voDivision)divisionList.elementAt(j);
		  	
				
	  			int ID = voD.getPKDivision();
				String name = voD.getDivisionName();
				int selectedDiv = QR.getDivisionID();

				if(selectedDiv != 0 && ID == selectedDiv) {
	  %>			<option value = <%=ID%> selected><%=name%>
	  <% 		} else { 
	  				
	  %>				<option value = <%=ID%>><%=name%>	
	  <% 		}
			} 
		}
		%>
      </select></td>
    </tr>
    <tr>
      <td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Department")%> : </td>
      <td bordercolor="#FFFFCC">&nbsp;</td>
	  <td bordercolor="#FFFFCC"><select name="selDept" onChange="populateGrp(this.form, this.form.selDiv, this.form.selDept)">
	  <option value=<%=t%>><%=trans.tslt("All")%>
	  <% 
	 if(departmentList != null) { 
	 	   /*********************
			* Edit By James 14-Nov 2007
			***********************/
	
		  	//while(departmentList.next()) {
	  			for(int k=0;k<departmentList.size();k++){
				voDepartment voDepartment=(voDepartment)departmentList.elementAt(k);
				
				
				//voDepartment.getLocation();
				int ID = voDepartment.getPKDepartment();
				String name = voDepartment.getDepartmentName();
				int selectedDept = QR.getDepartmentID();
			
				if(selectedDept != 0 && ID == selectedDept) {
	  %>
	  	<option value = <%=ID%> selected><%=name%>
		<% } else 
			{ 
		%>			<option value = <%=ID%>><%=name%>	  
		<%	}
		} 
	}%>
      </select></td>
    </tr>
    <tr>
      <td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Group")%> : </td>
      <td bordercolor="#FFFFCC">&nbsp;</td>
	  <td bordercolor="#FFFFCC"><select name="groupName" onChange="getID(this.form, this.form.groupName.options[groupName.selectedIndex].value, 2)">
	  <option value=<%=t%>><%=trans.tslt("All")%>
	  <% 

	  if(groupList != null) { 
			/*********************
			* Edit By James 14-Nov 2007
			***********************/
	
		  	//while(groupList.next()) {
			for(int l=0;l<groupList.size();l++){
			voGroup voGroup=(voGroup)groupList.elementAt(l);
				
	  			int ID = voGroup.getPKGroup();
				String name = voGroup.getGroupName();
				int selectedGroup = QR.getGroupID();
			
				if(selectedGroup != 0 && ID == selectedGroup) {
	  %>
	  	<option value = <%=ID%> selected><%=name%>
		<% } else { %>
		<option value = <%=ID%>><%=name%>	  
		<% }
			} }%>
      </select></td>
    </tr>
    <tr>
      <td align="right" bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><%=trans.tslt("Target")%> 
		: </td>
      <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	  <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><select name="targetName" onChange="setTarget(this.form, this.form.targetName)">
	  <option value=<%=t%>><%=trans.tslt("All")%>
	  <% 	if(targetList != null) {
			/*********************
			* Edit By James 14-Nov 2007
			***********************/
	
	  		//	while(targetList.next()) {
			for(int m=0;m<targetList.size();m++){
	     votblAssignment voTarget=(votblAssignment)targetList.elementAt(m);
			int loginID = voTarget.getTargetLoginID();
				String name=voTarget.getFullName();
				int selectedTarget = QR.getTargetID();
		
				if(loginID == selectedTarget) {
	  %>
	  <option value = <%=loginID%> selected><%=name%>	
	 <% } else { %>
	  	<option value = <%=loginID%>><%=name%>	  
		<% }
			} 
			}%>
      </select></td>
    </tr>
    <tr>
    	<td align="right" bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><%=trans.tslt("Language")%> 
		: </td>
      <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	  <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">
      <select size="1" name="langType">
		<% 	//Added a new language drop down box, Chun Yeong 1 Aug 2011
		String[] languages = {"English", "Indonesian", "Thai"};
		for(int i=0; i<languages.length; i++) {
		%>
        	<option value = <%=i%>><%=languages[i]%></option>
		<%
			} //End for loop, language array
			//End added a new language drop down box, Chun Yeong 1 Aug 2011
		%>
			
		</select></td>
    </tr>
    <tr>
    	<td align="right" bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><%=trans.tslt("Template")%> 
		: </td>
      <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	  <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">
      <select size="1" name="template" style="width:320px">
      <%
	  		//Added display of templates of different languages, Chun Yeong 1 Aug 2011
	  		File dir = new File(Setting.getOOReportTemplatePath());
			String[] templates = dir.list();
			String defTemplate = "Individual Report Template MOE Importance Report.xls"; //default template in English
			
			templates = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("Individual Report Template") && !name.toLowerCase().contains("no_normative") && !name.toLowerCase().contains("nocpr") && !name.toLowerCase().contains("sps") && !name.toLowerCase().contains("old") && !name.toLowerCase().contains("allianz") && !name.toLowerCase().contains("combined"));
			}});
			
			String sel = "";
			
			for(int i=0; i<templates.length; i++) {
				if(templates[i].equalsIgnoreCase(defTemplate) ) { 
					sel = "selected";
				} //End if the default template is the same as the current template, Chun Yeong 1 Aug 2011
	  %>			
    			<option value = "<%=templates[i]%>" <%=sel%>><%=templates[i]%></option>
	  <%
				sel = "";
			} // End for loop, templates array
			//End of display of templates of different languages, Chun Yeong 1 Aug 2011
	  %>
      </select>
      </td>
    </tr>
	<tr>
		<td bordercolor="#FFFFCC" colspan="4">&nbsp;
        
		</td>
    	
    </tr>
    
	<tr>
		<td bordercolor="#FFFFCC" colspan="4">
		Reports to be printed:<table border="0" width="100%" id="table1">
			<tr>
				<td width="6%">
				<%//Denise 30/12/2009 change default report to individual with normative%>
				<p align="center"><input type="radio" value="2" name="optIndividual" checked></td>
				<td><font size="2">Individual Report</font></td>
				<!-- Added by Tracy 01 Sep 08 , Option to Print report with or without Normative-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkNormative" checked></td>
				<td width="30%"><font size="2">With Normative</font></td>
				<!-- End add by Tracy 01 Sep 08 -->
			</tr>
			<tr>
				<td width="6%">
				<p align="center"><input type="radio" value="1" name="optIndividual"></td>
				<td><font size="2">Simplified Individual Report (without charts)</font></td>
                <!-- Added by Chun Yeong 2 Jun 2011 , Option to Print report with or without Group CP line-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkGroupCPLine" checked></td>
				<td width="30%"><font size="2">With Group CP Line</font></td>
				<!-- End add by Chun Yeong 2 Jun 2011 -->
			</tr>
            <tr>
				<td width="6%"></td>
				<td></td>
                <!-- Added by Chun Yeong 13 Jun 2011 , Option to follow template and split peers and subordinates-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkSplit"></td>
				<td width="30%"><font size="2">Reverse Split Option</font></td>
				<!-- End add by Chun Yeong 13 Jun 2011 -->
			</tr>
			 <tr>
				<td width="6%"></td>
				<td></td>
                <!-- Added by Chun Yeong 13 Jun 2011 , Option to follow template and split peers and subordinates-->
				<td width="6%">
				<p align="center"><input type="checkbox" checked name="chkFormat"></td>
				<td width="30%"><font size="2">Use PDF instead of Excel</font></td>
				<!-- End add by Chun Yeong 13 Jun 2011 -->
			</tr>
			 <tr>
				<td width="6%"></td>
				<td></td>
                <!-- Added by Chun Yeong 13 Jun 2011 , Option to follow template and split peers and subordinates-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDG"></td>
				<td width="30%"><font size="2">Send with Development Guide</font></td>
				<!-- End add by Chun Yeong 13 Jun 2011 -->
			</tr>
			<tr>
				<td width="6%"></td>
				<td></td>
                <!-- Added by Chun Yeong 13 Jun 2011 , Option to follow template and split peers and subordinates-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDGOnly"></td>
				<td width="30%"><font size="2">Send Only Development Guide</font></td>
				<!-- End add by Chun Yeong 13 Jun 2011 -->
			</tr>
			</table>
		</td>
    	
    </tr>
    
	<tr>
		<td bordercolor="#FFFFCC" colspan="3">
		</td>
		<td width="238" bordercolor="#FFFFCC"> 
<%	
	if(!logchk.getCompanyName().equalsIgnoreCase("demo") || userType == 1)
	{
%>		<input type="button" name="btnCancel" value="<%=trans.tslt("Send Via Email")%>" onclick = "send(this.form)">
		<!-- Edited by Tracy 01 Sep 08 -->
  		<!-- Edited by Chun Yeong 2 Jun 2011 -->
		<input type="button" name="btnOpen" value="<%=trans.tslt("Preview")%>" onclick = "return confirmOpen(this.form, this.form.optIndividual, this.form.surveyName, this.form.surveyNameImpt, this.form.targetName, this.form.chkDevGuide, this.form.chkDevMap, this.form.chkNormative, this.form.chkGroupCPLine, this.form.chkSplit, this.form.langType, this.form.template)">
        <!-- End edit by Chun Yeong 2 Jun 2011 -->
		<!-- End edit by Tracy 01 Sep 08 -->
		<input type="button" name="btnCancel" value="<%=trans.tslt("Cancel")%>" onclick = "return cancelPrint(this.form)">
<%		
		
	} else { 
	

%>		<input type="button" name="btnOpen" value="<%=trans.tslt("Preview")%>" disabled>
		<input type="button" name="btnCancel" value="<%=trans.tslt("Cancel")%>" disabled>
<%	
	} // end if(compID != 2 || userType == 1) %>
    	</td>   
    	
    </tr>
    
  </table>
  
<% }
%>
</form>

<table border="0" width="610" height="26">
	<tr>
   
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
   
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
   
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>   
	<%@ include file="Footer.jsp"%>
	</tr>
</table>
</body>
</html>

<%--This part is not needed since those files have been downloaded seperately by Ha 27/05
if (!bHasError && nameOfFilesCreated.size() > 1) {
		String output = Setting.getReport_Path() + "\\" + "outFile.zip";
		File f = new File (output);
	
		//set the content type(can be excel/word/powerpoint etc..)
		response.reset();
		response.setContentType ("application/xls");
		//set the header and also the Name by which user will be prompted to save
		response.addHeader ("Content-Disposition", "attachment;filename=\"AllReports.zip\"");
			
		//get the file name
		String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
		//OPen an input stream to the file and post the file contents thru the 
		//servlet output stream to the client m/c
		
		InputStream in = new FileInputStream(f);
		ServletOutputStream outs = response.getOutputStream();
		
		
		int bit = 256;
		int i = 0;


		try {
			while ((bit) >= 0) {
				bit = in.read();
				outs.write(bit);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}

		outs.flush();
		outs.close();
		in.close();
}
--%>