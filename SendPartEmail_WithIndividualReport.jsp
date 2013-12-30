<%@ page import="java.sql.*,
                 java.io.*,
                 java.lang.String,
                 java.util.Vector,
                 CP_Classes.vo.votblOrganization,
                 CP_Classes.vo.votblSurvey,
                 CP_Classes.AssignTarget_Rater,
                 CP_Classes.vo.voUser"%>
<%@ page import="CP_Classes.vo.*"%>   
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                   
<jsp:useBean id="Rpt7" class="CP_Classes.Report_ListOfTarget" scope="session"/>
<jsp:useBean id="setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="db" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="email" class="CP_Classes.ReminderCheck" scope="session"/>
<jsp:useBean id="user_Jenty" class="CP_Classes.User_Jenty" scope="session"/> 
<jsp:useBean id="User" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="QR" class="CP_Classes.QuestionnaireReport" scope="session" />
<jsp:useBean id="IR" class="CP_Classes.IndividualReport" scope="session"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
</head>
<SCRIPT LANGUAGE="JavaScript">

function send(form, field){
	if(field.value == 0){
		alert("Please select a survey.");
	}
	else{
		form.action="SendPartEmail_WithIndividualReport.jsp?send="+field.value;
		form.method="post";
		form.submit();
	}
}
function proceed(form,field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?proceed="+field.value;
	form.method="post";
	form.submit();
}

function updateCboSup(form)
{
	form.action="SendPartEmail_WithIndividualReport.jsp";
	form.method="post";
	form.submit();
}

function populateDivision(form,ID)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?surveyID=" + ID;
	form.method="post";
	form.submit();
}	

function populateDept(form,field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?div="+field.value;
	form.method="post";
	form.submit();
}	

function populateGrp(form,field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?dept=" + field.value;
	form.method="post";
	form.submit();
}

function populateRater(form, field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?group=" + field.value;
	form.method="post";
	form.submit();
	}
	
function setRound(form, field){
	form.action="SendPartEmail_WithIndividualReport.jsp?round=" + field.value;
	form.method="post";
	form.submit();
}	
function selectTarget(form,field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?target=" + field.value;
	form.method="post";
	form.submit();
}

function setLanguage(form,field)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?lang=" + field.value;
	form.method="post";
	form.submit();
	}

function setFormat(form, value)
{
	form.action="SendPartEmail_WithIndividualReport.jsp?format=" + value;
	form.method="post";
	form.submit();
	}
/*
 * Change(s) :Deleted the check on the radio buttons as they do not exist any more
 * Reason(s) : The radio buttons are deleted therfore the check is obsolete.
 * Updated By: Liu Taichen
 * Updated On: 25 May 2012
 */
function preview(form, field)
{
	if(field.value != 0) {
			
			form.action="SendPartEmail_WithIndividualReport.jsp?preview=" + field.value;
			form.method="post";
			form.submit();	
	
	} else {
		alert("Please select Survey");
	}
}
</script>



<body>
<%
/*
* Liu Taichen Declaration
*/
Vector raterList = null;
Vector groupList = null;
Vector DivisionList = null;
Vector DepartmentList = null;
Vector targetList = null;


int targetID = QR.getTargetID();
int raterID = QR.getRaterID();
int divID = QR.getDivisionID();
int deptID = QR.getDepartmentID();
int groupID = QR.getGroupID();

String chkNormative="";
String chkGroupCPLine="";
String chkSplit = "";
String chkBreakCPR = "";

int t = 0;


boolean enableBtnPreview = false;//Added by ping yang on 30/7/08 to disable 'Preview" button when no supervisor
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {%> <font size="2">
   
	<script>
	parent.location.href = "index.jsp";
	</script>
<%  } 
  else 
  { 

if(request.getParameter("proceed") != null)
{ 
	int var2 = new Integer(request.getParameter("selOrg")).intValue();
	CE_Survey.set_survOrg(var2);
	//Added to change the display organization name
	//Mark Oei 16 April 2010
 	logchk.setOrg(var2);
}

/*
*Liu Taichen
*/

if (request.getParameter("surveyID") != null){
	
	int id = Integer.parseInt(request.getParameter("surveyID"));
	
	QR.setSurveyID(id);
	QR.setDivisionID(0);
	QR.setDepartmentID(0);
	QR.setGroupID(0);
	QR.setRaterID(0);
	
	DivisionList = QR.getDivision(id);
	
	DepartmentList = QR.getDepartment(id, QR.getDivisionID());
	groupList = QR.getGroup(id, QR.getDivisionID(), QR.getDepartmentID());
	raterList = QR.getRater(QR.getSurveyID());
}

if (request.getParameter("div")!=null)
{		
    int id = Integer.parseInt(request.getParameter("div"));
	QR.setDivisionID(id);
	QR.setDepartmentID(0);
	QR.setGroupID(0);

	
	DivisionList = QR.getDivision(QR.getSurveyID());
	DepartmentList = QR.getDepartment(QR.getSurveyID(), QR.getDivisionID());
	groupList = QR.getGroup(QR.getSurveyID(), QR.getDivisionID(), QR.getDepartmentID());

}

if (request.getParameter("dept")!=null)
{		
    int id = Integer.parseInt(request.getParameter("dept"));
	QR.setDepartmentID(id);
	QR.setGroupID(0);

	
	DivisionList = QR.getDivision(QR.getSurveyID());
	DepartmentList = QR.getDepartment(QR.getSurveyID(),  QR.getDivisionID());
	groupList = QR.getGroup(QR.getSurveyID(), QR.getDivisionID(), QR.getDepartmentID());
	
}

if (request.getParameter("group")!=null)
{
	int id = Integer.parseInt(request.getParameter("group"));
	QR.setGroupID(id);

}

if (request.getParameter("round")!=null)
{
	int round = Integer.parseInt(request.getParameter("round"));
	QR.setRoundID(round);
}

if (request.getParameter("target")!=null)
{
	int id = Integer.parseInt(request.getParameter("target"));
	QR.setTargetID(id);
	
}

if (request.getParameter("format")!=null)
{
	int id = Integer.parseInt(request.getParameter("format"));
	IR.setFormat(id);
	
}

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

//Added to get the breakCPR option by Albert 09 July 2012
if (request.getParameter("chkBreakCPR")!=null) {
	chkBreakCPR= request.getParameter("chkBreakCPR");
}


/*
* Change(s) : Modified codes in the preview IF condition. Added capturing of input variable chkAttachment and modifying the the calling method Email_Survey_Open_Participant_Option
* Reason(s) : To include 2 more parameter value, pkUser and isAttachment for facilitating in the option of including excel questionnaires as attachment when sending reminder or participant emails.
* Updated By: Sebastian
* Updated On: 27 July 2010
*/
if(request.getParameter("send") != null)
{   System.out.println(request.getParameter("send"));
	int SurveyID = Integer.parseInt(request.getParameter("send"));
	
	String reminder = "remind";
	//Added new variable to determine if user check to include Excel Questionnaires as attachments, Sebastian 27 July 2010
	String DevGuide = "";
	
	if (request.getParameter("chkDevGuide") != null)
	{
		DevGuide = request.getParameter("chkDevGuide");
	}
	System.out.println(chkNormative);
	System.out.println(chkGroupCPLine);
	System.out.println(chkSplit);
	System.out.println(chkBreakCPR);
	System.out.println(DevGuide);
	
	Vector options = new Vector();
	options.add(chkNormative);
	options.add(chkGroupCPLine);
	options.add(chkSplit);
	options.add(chkBreakCPR);
	options.add(DevGuide);
	
		
	//System.out.println("chkReminder = " + iReminder);
	//System.out.println("optSend = " + request.getParameter("optSend"));
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
	
	
			//email.Sup_Nominate(SurveyID,iReminder);
			//Modified calling of method to include passing of 2 additional variable values, int pkUser and boolean isAttachment to facilitate in the functionality of including Excel Questionnaire as Attachment, Sebastian 27 July 2010
			errorFlag = email.Email_With_Individual_Report(recipients, SurveyID,IR.getFormat(), options,  logchk.getPKUser()); // (int SurveyID, String date, int RaterID, int isReminder)
			
			if (errorFlag == 0) {
				out.println(trans.tslt("Emails Sent") + "!");
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
	}

%>
<form name="SendPartEmail_WithIndividualReport" action="SendPartEmail_WithIndividualReport.jsp" method="post">
<table border="0" width="483" cellspacing="0" cellpadding="0">
	<tr>
		<td><b>
		<font face="Arial" size="2" color="#000080">Send Emails With Individual Report</font></b></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table border="2" width="483" cellspacing="0" cellpadding="0" bgcolor="#FFFFCC" bordercolor="#3399FF">
		<tr>
		<td width="117" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp;</td>
		<td width="228" style="border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="154" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp; </td>
	</tr>
		<tr>
		<td style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium ; align="right" bordercolor="#FFFFCC"><%=trans.tslt("Organization")%>: </td>
		  <td bordercolor="#FFFFCC">&nbsp;</td>
		<td width="228" style="border-style: none; border-width: medium">
		<p align="left">			

<%
	/*************************************************
	*The following was edited by clement at 9-jan-2007
	*************************************************/

// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){ %>
		<select size="1" name="selOrg" onchange="proceed(this.form,this.form.selOrg)">
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
</select><font size="2"> </font>
</td>
		<td width="154" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> </td>
	</tr>
	<tr>
		<td align="right" bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
	</tr>
	<tr>
		
		 <td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Survey")%>: </td>
		    <td bordercolor="#FFFFCC">&nbsp;</td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> <font size="2">
   
    	<select size="1" name="selSurvey" onChange="populateDivision(this.form,this.form.selSurvey.options[selSurvey.selectedIndex].value)">
    	<option selected value=0>Please select a survey</option>
<%
	int iSurveyID= QR.getSurveyID();
	
	
	boolean anyRecord=false;
	

	
	Vector v = CE_Survey.getSurveys(logchk.getCompany(), CE_Survey.get_survOrg());
	
	for(int i=0; i<v.size(); i++)
	{
		votblSurvey vo = (votblSurvey)v.get(i);

		anyRecord=true;
		int Surv_ID = vo.getSurveyID();
		String Surv_Name = vo.getSurveyName();
		
			if(iSurveyID!= 0 && iSurveyID== Surv_ID)
			{%>
				<option value=<%=Surv_ID%> selected><%=Surv_Name%></option>	
		<%	}else
			{%>
				<option value=<%=Surv_ID%>><%=Surv_Name%></option>
			<%	}
	}%>
</select></td>
	</tr>
	<tr>
					<td align="right" bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
				</tr>
				<tr>
					<td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Division")%>
						:</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC"><select name="division"
						onChange="populateDept(this.form, this.form.division)">
						
							<option value=<%=t%>><%=trans.tslt("All")%>
								<%  
								    
								    DivisionList = QR.getDivision(QR.getSurveyID());
									if (DivisionList != null) {
											/*********************
											 * Edit By James 14-Nov 2007
											 ***********************/

											//while(DivisionList.next()) {
											for (int k = 0; k < DivisionList.size(); k++) {
												voDivision voD = (voDivision) DivisionList.elementAt(k);

												int ID = voD.getPKDivision();
												String name = voD.getDivisionName();

												divID = QR.getDivisionID();
												if (divID != 0 && ID == divID) {
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
											else {
								
							} %>
					</select>
					</td>
				</tr>
				<tr>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
				</tr>
				<tr>
					<td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Department")%>
						:</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC"><select name="department"
						onChange="populateGrp(this.form,this.form.department)">
							<option value=<%=t%>><%=trans.tslt("All")%>
								<%
								
								DepartmentList = QR.getDepartment(QR.getSurveyID(), QR.getDivisionID());
								
							
									if (DepartmentList != null) {

											/*********************
											 * Edit By James 14-Nov 2007
											 ***********************/

											//while(DepartmentList.next()) {
											for (int l = 0; l < DepartmentList.size(); l++) {
												voDepartment voDep = (voDepartment) DepartmentList
														.elementAt(l);

												int ID = voDep.getPKDepartment();
												String name = voDep.getDepartmentName();

												deptID = QR.getDepartmentID();
												if (deptID != 0 && ID == deptID) {
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
					</td>
				</tr>
				<tr>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Group")%>
						:</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC"><select name="groupName"
						onChange="populateRater(this.form, this.form.groupName)">
							<option value=<%=t%>><%=trans.tslt("All")%>
								<%
								groupList = QR.getGroup(QR.getSurveyID(), QR.getDivisionID(), QR.getDepartmentID());
								
					/*
					*Liu Taichen 24 May 2012
					*remove the duplicated group names in the dropbox
					*/
					Vector<voGroup> shortList = new Vector<voGroup>();
					for(int i = 0; i < groupList.size(); i ++){
						voGroup voG = (voGroup) groupList.elementAt(i);
						Boolean duplicated = false;
						for(voGroup vo : shortList){
						if(voG.getGroupName().equals(vo.getGroupName())){
							duplicated = true;
						}
						}
						if(!duplicated){
							shortList.add(voG);
						}
					}
									if (groupList != null) {
											/*********************
											 * Edit By James 14-Nov 2007
											 ***********************/

											//while(groupList.next()) {
											for (int m = 0; m < shortList.size(); m++) {
												voGroup voG = (voGroup) shortList.elementAt(m);
												int ID = voG.getPKGroup();
												String name = voG.getGroupName();

												groupID = QR.getGroupID();
												if (groupID != 0 && ID == groupID) {
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
					</td>
				</tr>
				<tr>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
				</tr>
				
				<%
				/*
				 *Change(s): added a dropbox to select the round
				 *Reason(s): to enbale filtering of email recipients by round
				 *Updated By: Liu Taichen
				 *Updated On: 6/July/2012
				 */
				   %>
				
				<tr>
					<td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Round")%>
						:</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC"><select name="round"
						onChange="setRound(this.form, this.form.round)">
							<option value=<%=t%>><%=trans.tslt("All")%>
						<%
								int	surveyID = QR.getSurveyID();
								divID = QR.getDivisionID();
								int	departmentID = QR.getDepartmentID();
							    groupID = QR.getGroupID();
							    int roundID = QR.getRoundID();
							    Vector vRound = QR.getRound();//User.getRoundList(surveyID);
							    for(Object o : vRound){
							    	int round = (Integer)o;
							    	if(round == roundID){
							    		%>
							    		<option value=<%=round%> selected>  <%=round%>							    		
							    		<%
							    	}
							    	else{
							    		%>
							    		<option value=<%=round%>><%=round%>
							    		<%
							    	}
							    }
						
							%>
					</select>
					</td>
				</tr>
				<tr>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
				</tr>
			<tr>
					<td align="right" bordercolor="#FFFFCC"><%=trans.tslt("Target")%>
						:</td>
					<td bordercolor="#FFFFCC">&nbsp;</td>
					<td bordercolor="#FFFFCC"><select name="target"
						onChange="selectTarget(this.form,this.form.target)">
							<option value=<%=0%>><%=trans.tslt("All")%>
											         
				         <%
				            divID = QR.getDivisionID();							
						    groupID = QR.getGroupID();	
						    //for all departments
						        if (departmentID == 0){							    	
						    	if (groupID ==0){targetList = QR.getTarget(surveyID, divID, departmentID, groupID, 0);}
						    	else{	
						        Vector sameNameGroup = new Vector();
						        String currentGroupName = "";
						    //get the name of the current selected group
						    	for(int i = 0; i < groupList.size(); i ++){
									voGroup voG = (voGroup) groupList.elementAt(i);
								   if(voG.getPKGroup() == QR.getGroupID()){
									   currentGroupName = voG.getGroupName();
								   }
						    	} 
						    //get the groupIDs of all the groups with the same name as the current seleceted one
								for(int i = 0; i < groupList.size(); i ++){
									voGroup voG = (voGroup) groupList.elementAt(i);
									if(voG.getGroupName().equals(currentGroupName)){
										sameNameGroup.add(voG);
									}
								}
							//get the raters from the different groups
							targetList = new Vector();
							   for(int i = 0; i < sameNameGroup.size(); i++){
								   voGroup voG = (voGroup) sameNameGroup.elementAt(i);
								   for( Object o : QR.getTarget(surveyID, divID, departmentID, voG.getPKGroup(), 0)){
									   targetList.add(o);
								   }								  
							   }							 									
						    	} 
						    	}
						    //for single departments
						    else{
						    	
						    	targetList = QR.getTarget(surveyID, divID, departmentID, groupID, 0);
						    }
						    
						    //add in filtering for round
						    Vector vTarget = new Vector();
						    		for(Object o : targetList){
						    			vTarget.add(o);
						    		}
						    targetList = new Vector();
						    		if(vTarget != null){
						    			
						    			for (int l = 0; l < vTarget.size(); l++) {
											votblAssignment voTarget = (votblAssignment)vTarget.elementAt(l);                                               
											int ID = voTarget.getTargetLoginID();
											String[] sD = User.getUserDetailWithRound(ID, 0);
											int targetRound = Integer.parseInt(sD[14]);
											if(targetRound == QR.getRoundID() || QR.getRoundID() == 0){
												targetList.add(voTarget);
											}
						    		}		
						    		}
						    
					
								if (targetList  != null) {

										for (int l = 0; l < targetList.size(); l++) {
											votblAssignment voTarget = (votblAssignment)targetList.elementAt(l);     
											int ID = voTarget.getTargetLoginID();
											//String[] sD = User.getUserDetailWithRound(ID, 0);
											//int targetRound = Integer.parseInt(sD[14]);
											
											String name = voTarget.getFullName();											
											targetID = QR.getTargetID();
				
											if (targetID != 0 && ID == targetID) {
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
					</td>
				</tr>
				
		<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp; </td>
		</tr>
		    <tr>
    	<td align="right" bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium"><%=trans.tslt("Language")%> 
		: </td>
      <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	  <td bordercolor="#FFFFCC" style="border-bottom-style: none; border-bottom-width: medium">
      <select size="1" name="langType" onchange="setLanguage(this.form, this.form.langType)">
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
	  		File dir = new File(setting.getOOReportTemplatePath());
			String[] templates = dir.list();
			String defTemplate = "Individual Report Template_Cluster.xls"; //default template in English
			String hidTemplate = "Individual Report Template.xls"; //template not used anymore in Individual Report but used in MOE Importance
			
			templates = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("Individual Report Template") && !name.toLowerCase().contains("no_normative") && !name.toLowerCase().contains("nocpr") && !name.toLowerCase().contains("sps") && !name.toLowerCase().contains("old") && !name.toLowerCase().contains("allianz") && !name.toLowerCase().contains("combined"));
			}});
			
			String sel = "";
			
			for(int i=0; i<templates.length; i++) {
				if(templates[i].equalsIgnoreCase(defTemplate) ) { 
					sel = "selected";
				} //End if the default template is the same as the current template, Chun Yeong 1 Aug 2011
				if(templates[i].equalsIgnoreCase(hidTemplate) ) { 
					continue;
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
		Format of Report:<table border="0" width="100%" id="table1">
			<tr>
				<td width="6%">
				<%//Denise 30/12/2009 change default report to individual with normative
				
				if(IR.getFormat() == 0){%>
				<p align="center"><input type="radio" value="0" name="optIndividual" checked onChange="setFormat(this.form, 0)")></td>
				<td><font size="2">Excel</font></td>
				<%}else { %>
				<p align="center"><input type="radio" value="0" name="optIndividual"  onChange="setFormat(this.form, 0)")></td>
				<td><font size="2">Excel</font></td>
				<%} %>
				<!-- Added by Tracy 01 Sep 08 , Option to Print report with or without Normative-->
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkNormative" checked></td>
				<td width="30%"><font size="2">With Normative</font></td>
				<!-- End add by Tracy 01 Sep 08 -->
			</tr>
			<tr>
				<td width="6%">
				<% if(IR.getFormat() == 1){%>
				<p align="center"><input type="radio" value="1" name="optIndividual" checked onChange="setFormat(this.form, 1)"></td>
				<td><font size="2">PDF</font></td>
				<%} else{ %>
				<p align="center"><input type="radio" value="1" name="optIndividual" onChange="setFormat(this.form, 1)"></td>
				<td><font size="2">PDF</font></td>
				<%} %>
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
				<td colspan="2"><font size="2">with</font></td>
				
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkBreakCPR"></td>
				<td width="30%"><font size="2">Break Down CPR</font></td>
			</tr>
			<tr>
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDevGuide"></td>
				<td><font size="2">Development Guide</font></td>
			</tr>
			<!-- <tr>
				<td width="6%">
				<p align="center"><input type="checkbox" name="chkDevMap"></td>
				<td><font size="2">Development Map (Only available for Key 
				Behaviour level surveys)</font></td>
			</tr> -->
			</table>
		</td>
    	
    </tr>
    
	<tr>
		<td bordercolor="#FFFFCC" colspan="3">
		</td>
		<td width="238" bordercolor="#FFFFCC"> 
<%	
    int userType = logchk.getUserType();
	if(!logchk.getCompanyName().equalsIgnoreCase("demo") || userType == 1)
	{
%>		
		<!-- Edited by Tracy 01 Sep 08 -->
  		<!-- Edited by Chun Yeong 2 Jun 2011 -->
		<input type="button" name="btnOpen" value="<%=trans.tslt("Send")%>" onclick = "return send(this.form, this.form.selSurvey)">
        <!-- End edit by Chun Yeong 2 Jun 2011 -->
		<!-- End edit by Tracy 01 Sep 08 -->
		<input type="button" name="btnCancel" value="<%=trans.tslt("Cancel")%>" onclick = "return cancelPrint(this.form)">
<%		
		
	} else { 
	

%>		<input type="button" name="btnOpen" value="<%=trans.tslt("Send")%>" disabled>
		<input type="button" name="btnCancel" value="<%=trans.tslt("Cancel")%>" disabled>
<%	
	} // end if(compID != 2 || userType == 1) %>
    	</td>   
    	
    </tr>

	<tr>
		<td width="116" align="center" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
		<td width="228" align="center" style="border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
		<td width="155" align="center" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
	</tr>
</table>
</form>
<%		%>

<%@ include file="Footer.jsp"%>

</body>
</html>