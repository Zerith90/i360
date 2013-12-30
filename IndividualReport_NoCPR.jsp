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
<title>Individual Report NO CPR</title>

<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>

</head>

<body>
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="QR" class="CP_Classes.QuestionnaireReport" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/> 
<jsp:useBean id="User" class="CP_Classes.User_Jenty" scope="session"/>
<jsp:useBean id="USR" class="CP_Classes.User" scope="session"/> 
<jsp:useBean id="ExcelIndividual_NoCPR" class="CP_Classes.IndividualReport_NoCPR" scope="session"/> 
<jsp:useBean id="DG" class="CP_Classes.DevelopmentGuide" scope="session"/> 
<jsp:useBean id="Setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="AdvSetting" class="CP_Classes.AdvanceSettings" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="SR" class="CP_Classes.SurveyResult" scope="session"/>
<jsp:useBean id="ORG" class="CP_Classes.Organization" scope="session"/>

<script language="javascript">

var x = parseInt(window.screen.width) / 2 - 200;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 200;
var myWindow;

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
		var query = "IndividualReport_NoCPR.jsp?" + typeSelected + "=" + ID;
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
function confirmOpen(form, optReport, survey, target, chkDG, chkDMap, chkNormative)
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
	
	if(survey.value != 0)
	{
		if(target.value == 0)
		{
			form.btnOpen.value = "<%=trans.tslt("Processing")%> ...";
			form.btnOpen.disabled = true;
		}
		//Comment off by james 16-Jun 2008. 
		//myWindow=window.open('IndividualReportAll.jsp','IndividualReportPopUp','scrollbars=no, width=480, height=250');
		//var query = "IndividualReportAll.jsp";
		//myWindow.moveTo(x,y);
		//myWindow.location.href = query;

		form.action = "IndividualReport_NoCPR.jsp?open="+type+"&cancel=0&survey="+survey.value;
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
			myWindow=window.open('IndividualReportAll_NoCPR.jsp','IndividualReportPopUp','scrollbars=no, width=480, height=250');
			var query = "IndividualReportAll_NoCPR.jsp";
			myWindow.moveTo(x,y);
			myWindow.location.href = query;
	
			form.action = "IndividualReport_NoCPR.jsp?open="+type+"&cancel=0&survey="+survey.value;
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
	form.action = "IndividualReport_NoCPR.jsp?cancel=1";
	form.submit();
	return true;
}

function finishPrint(form)
{
	form.btnOpen.value = "<%=trans.tslt("Preview")%>";
	form.action = "IndividualReport_NoCPR.jsp";
	form.submit();
	return true;
}

/*------------------------------------------------------------start: Login modification 1------------------------------------------*/
/*	choosing organization*/

function proceed(form,field)
{
	form.action="IndividualReport_NoCPR.jsp?proceed="+field.value;
	form.method="post";
	form.submit();
}	

function populateDept(form, field)
{
	form.action="IndividualReport_NoCPR.jsp?div="+field.value;
	form.method="post";
	form.submit();
}

function populateGrp(form, field1, field2)
{
	form.action="IndividualReport_NoCPR.jsp?div=" + field1.value + "&dept="+ field2.value;
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
	int nameSeq = User.NameSequence(OrgID);

	Vector surveyList = QR.getSurvey(compID, OrgID);
	Vector divisionList = null;
	Vector departmentList = null;
	Vector groupList  = null;
	Vector targetList = null;
	Vector raterList  = null;
		
	int surveyID = QR.getSurveyID();
	int divisionID = QR.getDivisionID();
	int departmentID = QR.getDepartmentID();
	int groupID  = QR.getGroupID();	
	int target   = QR.getTargetID();
	//Added by Tracy 01 Sep 08
	String chkNormative="";
	//End add by Tracy**
	
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
		ExcelIndividual_NoCPR.setCancelPrint(iCancelled);		// 0=Proceed; 1=Cancelled
	} else {
		ExcelIndividual_NoCPR.setCancelPrint(0);
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
		
		if(request.getParameter("open") != null)
		{
			int type = Integer.parseInt(request.getParameter("open"));
			
			surveyID = Integer.parseInt(request.getParameter("survey"));
			divisionID = QR.getDivisionID();
			departmentID = QR.getDepartmentID();
			groupID = QR.getGroupID();
			//Added getting Normative option by Tracy 01 Sep 08
		
			if (request.getParameter("chkNormative")!= null) {
				chkNormative= request.getParameter("chkNormative");
			}
			//End add by Tracy 01 Sep 08***
			
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
					String sUserEmail = User.getUserEmail(logchk.getPKUser());	// Email of the person who gen the report
					
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
		
								String file_name = "IndividualReport" + temp + ".xls";
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
								
								//Edited printing ind report with Normative option by Tract 01 Sep 08
								ExcelIndividual_NoCPR.Report(surveyID, target, pkUser, file_name, type, chkNormative);
								//End edit by Tracy 01 Sep 08
								
								//System.out.println("OKKK");
									//String displayed = "List of Competencies.xls";
					
		
					//System.out.println(logchk.getCompany() + "--" +  FKOrg + "--" +  comp + "--" +  FKJob + "--" +  origin + "--" +  logchk.getPKUser());
					//Rpt1.generateReport(logchk.getCompany(), FKOrg, comp, FKJob, origin, file_name, logchk.getPKUser(), lang);
				
								if(bMoreThanOne == false)
								{
									//System.out.println("HEREEEEE");
									String output = Setting.getReport_Path() + "\\" + file_name;
									nameOfFilesCreated.add(output);
									File f = new File (output);
						//System.out.println("OUTPUT = " + output );
									//set the content type(can be excel/word/powerpoint etc..)
									response.reset();
									response.setContentType ("application/xls");
									//set the header and also the Name by which user will be prompted to save
									response.addHeader ("Content-Disposition", "attachment;filename=\"IndividualReport.xls\"");
		
									//get the file name
									String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
					//	System.out.println("NAME = " + name);
									InputStream in = new FileInputStream(f);
						//System.out.println("INPUTSTREAM = " + in);
									ServletOutputStream outs = response.getOutputStream();
						//System.out.println("HERE 4");
									int bit = 256;
									int i = 0;
						//System.out.println("HERE 5");
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
									ExcelIndividual_NoCPR.sendIndividualReport(targetName, surveyName, sUserEmail, file_name, surveyID);
								}
						
							}
							
							if(bDG == true)
							{	
								String file_name = "Development Guide" + temp + ".xls";	
								System.out.println("[IndividualReport] DG. Survey = " + surveyID + ", Target = " + target);
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
							
							if(bDMap == true)
							{	
								String file_name = "Development Map" + temp + ".xls";				
								boolean bBreakdown = AdvSetting.bIsBreakdown(surveyID);
								
								System.out.println("[IndividualReport] DMap. Survey = " + surveyID + ", Target = " + target);
								ExcelIndividual_NoCPR.reportDevelopmentMap(surveyID, target, file_name, bBreakdown);
								
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
									ExcelIndividual_NoCPR.sendDevelopmentMap(targetName, surveyName, sUserEmail, file_name, surveyID);
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
							Vector allTarget = ExcelIndividual_NoCPR.AllTargets(surveyID, divisionID, departmentID, groupID, OrgID);
							int totalReports = allTarget.size();
														
							int totalGenerated = 0;
							
							for(int i=0; i<totalReports; i++) 
							{
								// for delay purpose
								if(i != 0)
									for(int j=0;j<100000;j++);
						
								if (ExcelIndividual_NoCPR.getCancelPrint() == 0)
								{
									//generate individual report.
									String [] data = (String[])(allTarget.elementAt(i));
									surveyName 	 = data[0];
									int targetID = Integer.parseInt(data[1]);
									targetName	 = data[2];					
				%>
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
										String file_name = "Individual Report for " + targetName + ".xls";
										
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
										
										//Edited printing ind report with Normative option
										// By Tracy 01 Sep 08***
										ExcelIndividual_NoCPR.Report(surveyID, targetID, pkUser, file_name, type, chkNormative);									
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
										ExcelIndividual_NoCPR.reportDevelopmentMap(surveyID, targetID, file_name, bBreakdown);
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
			//bHasError = ExcelIndividual_NoCPR.ZipAllFiles(nameOfFilesCreated);
		//}
%>
<form name="IndividualReport" method="post">
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
<font face="Arial" size="2">For generation of more than 1 type of reports, they 
will be sent to the Administrator's email.
<br>
&nbsp;</font><table boQEr="0" width="439" cellspacing="0" cellpadding="0" font style='font-size:10.0pt;font-family:Arial'>
	<tr>
	  <td colspan="4" align="left"><b><font color="#000080" size="2" face="Arial"><%=trans.tslt("Individual Report NO CPR")%> </font></b></td>
    </tr>

</table>
<font size="2">&nbsp;</font><font size="2"> </font>
<table boQEr="0" width="438" cellspacing="3" cellpadding="0" style='font-size:10.0pt;font-family:Arial' border="2" bordercolor="#3399FF">
	<tr>
		<td width="100" align="right" bordercolor="#FFFFFF"><%=trans.tslt("Organisation")%> 
		:</td>
		<td width="19" bordercolor="#FFFFFF">&nbsp;</td>
		<td width="207" bordercolor="#FFFFFF"><select size="1" name="selOrg" onChange="proceed(this.form,this.form.selOrg)">
			<option value="0" selected><%=trans.tslt("All")%>
<%
	Vector vOrg = logchk.getOrgList(logchk.getCompany());
	
	for(int i=0; i<vOrg.size(); i++)
	{
		votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
		int PKOrg = vo.getPKOrganization();
		String OrgName = vo.getOrganizationName();
	
		if(logchk.getOrg() == PKOrg)
		{
%>
	<option value=<%=PKOrg%> selected><%=OrgName%></option>

<%		}
		else	
		{
%>
	<option value=<%=PKOrg%>><%=OrgName%></option>
<%		}	
	}
%>
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
	  <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><select name="targetName">
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
		<td bordercolor="#FFFFCC" colspan="4">&nbsp;
		</td>
    	
    </tr>
    
	<tr>
		<td bordercolor="#FFFFCC" colspan="4">
		Reports to be printed:<table border="0" width="100%" id="table1">
			<tr>
				<td width="6%">
				<p align="center"><input type="radio" value="2" name="optIndividual"></td>
				<td><font size="2">Individual Report</font></td>
				<!-- Added by Tracy 01 Sep 08 , Option to Print report with or without Normative-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkNormative"></td>
				<td width="30%"><font size="2">With Normative</font></td>
				<!-- End add by Tracy 01 Sep 08 -->
			</tr>
			<tr>
				<td width="6%">
				<p align="center"><input type="radio" value="1" name="optIndividual"></td>
				<td><font size="2">Simplified Individual Report (without charts)</font></td>
			</tr>
			<tr>
				<td colspan="2"><font size="2">with</font></td>
			</tr>
			<tr>
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDevGuide"></td>
				<td><font size="2">Development Guide</font></td>
			</tr>
			<tr>
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDevMap"></td>
				<td><font size="2">Development Map (Only available for Key 
				Behaviour level surveys)</font></td>
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
%>		
		<!-- Edited by Tracy 01 Sep 08 -->
		<input type="button" name="btnOpen" value="<%=trans.tslt("Preview")%>" onclick = "return confirmOpen(this.form, this.form.optIndividual, this.form.surveyName, this.form.targetName, this.form.chkDevGuide, this.form.chkDevMap, this.form.chkNormative)">
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