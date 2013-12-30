<%@ page import="java.sql.*,CP_Classes.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.*"%>
<%@ page import="java.lang.String"%>
<%@ page import="java.util.Vector"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page import="CP_Classes.Calculation"%>
<%@ page import="CP_Classes.SurveyResult"%>

<%
	//by  Yiting 19/09/2009 Fix jsp files to support Thai language"
%>
<html>
<head>
<title>Group Report</title>
<style type="text/css">
<!--
.style1 {
	font-size: 10pt
}
-->
</style>

<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%
	// by lydia Date 05/09/2008 Fix jsp file to support Thai language
%>

<!-- CSS -->

<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.css">
<link type="text/css" rel="stylesheet"
	href="lib/css/bootstrap-responsive.css">
<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.min.css">
<link type="text/css" rel="stylesheet"
	href="lib/css/bootstrap-responsive.min.css">


<!-- jQuery -->
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.js"></script>
<script type="text/javascript" src="lib/js/jquery-1.9.1.js"></script>


<script src="lib/js/bootstrap.min.js" type="text/javascript"></script>




	<%boolean reportCompletion =false; %>
	<jsp:useBean id="Database" class="CP_Classes.Database" scope="session" />
	<jsp:useBean id="QR" class="CP_Classes.QuestionnaireReport" scope="session" />
	<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session" />
	<jsp:useBean id="User_Jenty" class="CP_Classes.User_Jenty"
		scope="session" />
	<jsp:useBean id="ExcelGroup" class="CP_Classes.GroupReport"
		scope="session" />

	<jsp:useBean id="Setting" class="CP_Classes.Setting" scope="session" />
	<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session" />

	<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session" />
	<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey"
		scope="session" />


	<script type="text/javascript">
var x = parseInt(window.screen.width) / 2 - 200;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 200;
 

$(document).ready(function(){
	$('.info').tooltip();
	
	
	if(<%=session.getAttribute("chosenFiles")%>!=null){
		$('#dlpage').show();
	}else{
		$('#dlpage').hide();
	}
});

function loading(){
	$('.generateBtn').button('loading');
}
function getID(form, ID, type)
{
	var typeSelected = "";
	
	if(ID != 0) {
		switch(type) {
			case 1: typeSelected = "jobPost";
					  break;
			case 2: typeSelected = "surveyID";
					  break;
		}
		//\\ changed by Ha 27/05/08 to refresh the group when changing survey name
		var query = "GroupReport.jsp?groupName=0&" + typeSelected + "=" + ID;
		form.action = query;
		form.method = "post";
		form.submit();
	} else {
		alert("<%=trans.tslt("Please select the options")%>");
		return false;
	}
	return true;	
}
function closeModal()
{
	$("#myModal").modal("hide");
}
function confirmOpen(form)
{
	
	if(checkField(form)){
		//type 1=simplified;2=standard
		var type = 2; 
		if(form.chkReportType.checked)
			type = 1;
		
		//Denise 16/12/2009 exGR = 0. Exclude Group Ranking box is not checked
		var exGR = 0;	
		var wAvg =0;
		if(form.chkGR.checked)
			exGR = 1;
		if(form.weightedAverage.checked)
			wAvg=1;
		
		if(form.JobPost.value != 0 && form.surveyName.value != 0)  {
			//myWindow=window.open('GroupReportAll.jsp','GroupReportPopUp','scrollbars=no, width=1280, height=480');
			$("#myModal").modal("show");
			loading();
		//	var query = "GroupReportAll.jsp";
		//	myWindow.moveTo(x,y);
		//	myWindow.location.href =  query;
	
			//Denise 16/12/2009 To add the Exclude Group Ranking function
			var query= "GroupReport.jsp?open="+type+"&exGR="+exGR+ "&round="+ round.value;
			form.action = query;
			form.method = "post";
			form.submit();
			
			return true;	
		}else {
			alert("<%=trans.tslt("Please select the options")%>");
					return false;
				}
			}//End of checkField	

		}

		//Gwen Oh - 10/10/11: Open SubgroupReport.jsp for user to select which targets to be in the report
		function openSubgroup(form) {

			//type 1=simplified;2=standard
			var type = 2;
			if (form.chkReportType.checked)
				type = 1;

			//exGR = 0. Exclude Group Ranking box is not checked
			var exGR = 0;
			if (form.chkGR.checked)
				exGR = 1;

			if (checkField(form)) {
				var selectedSurvey = form.surveyName.options[form.surveyName.selectedIndex];
				var str = "SubgroupReport.jsp?orgName="
						+ form.selOrg.options[form.selOrg.selectedIndex].text
						+ "&surveyName=" + selectedSurvey.text + "&surveyID="
						+ selectedSurvey.value + "&divID="
						+ form.division.value + "&deptID="
						+ form.department.value + "&groupID="
						+ form.groupName.value + "&type=" + type + "&exGR="
						+ exGR;
				var myWindow = window.open(str, 'windowRef',
						'scrollbars=no, width=750, height=650');
				myWindow.moveTo(x, y);
			}
		}

		//This function is created by James. 16 Jun 2008 to make sure all field are selected.
		function checkField(form) {
			var msg = "Please select following information : \n";
			var valid = true;
			if (form.JobPost.selectedIndex == 0) {
				msg += "  - Job Position \n";
				valid = false;
			}
			if (form.surveyName.selectedIndex == 0) {
				msg += "  - Survey Name \n";
				valid = false;
			}

			if (valid) {
				return valid;
			} else {
				alert(msg);
				return false;
			}
		}

		/*------------------------------------------------------------start: LOgin modification 1------------------------------------------*/
		/*	choosing organization*/

		function proceed(form, field) {
			form.action = "GroupReport.jsp?proceed=" + field.value;
			form.method = "post";
			form.submit();
		}
		//Changed by Albert 24/05/2012 to set the bottom layer value to default when changing the upper layer value	
		//Changed by Ha 27/05/08 to set group to default when changing upper layer value
		function populateDept(form, field1, field2, field3) {
			form.action = "GroupReport.jsp?groupName=0&div=" + field1.value
					+ "&dept=0&surveyID=" + field2.value + "&jobPost="
					+ field3.value;
			form.method = "post";
			form.submit();
		}

		function populateGrp(form, field1, field2, field3, field4) {
			form.action = "GroupReport.jsp?groupName=0&div=" + field1.value
					+ "&dept=" + field2.value + "&surveyID=" + field3.value
					+ "&jobPost=" + field4.value;
			form.method = "post";
			form.submit();
		}

		function populateDesignation(form, field1, field2, field3, field4,
				field5) {
			form.action = "GroupReport.jsp?groupName=" + field5.value + "&div="
					+ field1.value + "&dept=" + field2.value + "&surveyID="
					+ field3.value + "&jobPost=" + field4.value;
			form.method = "post";
			form.submit();
		}
		//Ended changes made by Ha
	</script>


	<%
		//response.setHeader("Pragma", "no-cache");
			//response.setHeader("Cache-Control", "no-cache");
			//response.setDateHeader("expires", 0);

			String username=(String)session.getAttribute("username");

			  if (!logchk.isUsable(username)) 
			  {
	%>
	<script>
		parent.location.href = "index.jsp";
	</script>
	<%
		} else {
			if (request.getParameter("proceed") != null) {
				int PKOrg = new Integer(request.getParameter("proceed"))
						.intValue();
				logchk.setOrg(PKOrg);
				QR.setJobPostID(0);
				QR.setSurveyID(0);
				QR.setDivisionID(0);
				QR.setDepartmentID(0);
				QR.setGroupID(0);

			}

			/*-------------------------------------------------------------------end login modification 1--------------------------------------*/

					int compID = logchk.getCompany();
	
					int OrgID = logchk.getOrg();
					int pkUser = logchk.getPKUser();
					int nameSeq = User_Jenty.NameSequence(OrgID);
					int userType = logchk.getUserType(); // 1= super admin
					

					Vector jobPostList = QR.getJobPost(compID,OrgID);
					Vector surveyList = null;
					Vector groupList = null;
					Vector DivisionList = null;
					Vector DepartmentList = null;
					Vector designationList = null;
					Vector roundList =null;
					String jobPostError ="";
					String surveyIDError="";
					
					int weightedAverage = 0;
					String templateForm = " ";
					boolean clusterCompetency =false;
					boolean rankTables =false;
					boolean CGD =false;
					boolean splitCPR=false;
					boolean splitCP=false;
					 
					//Changed made by Ha 27/05/08 to set respective value to default when changing upper layer
					if (request.getParameter("jobPost") != null) {
						int id = Integer.parseInt(request.getParameter("jobPost"));

						QR.setJobPostID(id);
						QR.setSurveyID(0);
						QR.setDivisionID(0);
						QR.setDepartmentID(0);
						QR.setGroupID(0);
						QR.setDesignationName("");

						surveyList = QR.getSurvey(id);
						DivisionList = null;
						DepartmentList = null;
						groupList = null;
						designationList = null;
					}
					if (request.getParameter("surveyID") != null) {
						int id = Integer.parseInt(request.getParameter("surveyID"));
						QR.setSurveyID(id);
						QR.setDivisionID(0);
						QR.setDepartmentID(0);
						QR.setGroupID(0);
						QR.setDesignationName("");
						
						DivisionList = QR.getDivision(id);
						DepartmentList = QR.getDepartment(id,QR.getDivisionID());
						groupList = QR.getGroup(id,QR.getDivisionID(),
								QR.getDepartmentID());
						designationList = QR.getDesignation(id,QR.getDivisionID(),
								QR.getDepartmentID(),QR.getGroupID());
						roundList = QR.getRound();
					}
				
					if (request.getParameter("div") != null) {
						int id = Integer.parseInt(request.getParameter("div"));
						QR.setDivisionID(id);
						QR.setDepartmentID(0);
						QR.setGroupID(0);
						QR.setDesignationName("");

						DivisionList = QR.getDivision(QR.getSurveyID());
						DepartmentList = QR.getDepartment(QR.getSurveyID(),
								QR.getDivisionID());
						groupList = QR.getGroup(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID());
						designationList = QR.getDesignation(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID(),
								QR.getGroupID());
					}

					if (request.getParameter("dept") != null) {
						int id = Integer.parseInt(request.getParameter("dept"));
						QR.setDepartmentID(id);
						QR.setGroupID(0);
						QR.setDesignationName("");

						DivisionList = QR.getDivision(QR.getSurveyID());
						DepartmentList = QR.getDepartment(QR.getSurveyID(),
								QR.getDivisionID());
						groupList = QR.getGroup(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID());
						designationList = QR.getDesignation(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID(),
								QR.getGroupID());
					}

					if (request.getParameter("groupName") != null) {
						int id = Integer
								.parseInt(request.getParameter("groupName"));
						QR.setGroupID(id);
						QR.setDesignationName("");

						DivisionList = QR.getDivision(QR.getSurveyID());
						DepartmentList = QR.getDepartment(QR.getSurveyID(),
								QR.getDivisionID());
						groupList = QR.getGroup(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID());
						designationList = QR.getDesignation(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID(),
								QR.getGroupID());
					}
				

					if (request.getParameter("designation") != null) {
						String id = request.getParameter("designation");
						QR.setDesignationName(id);

						DivisionList = QR.getDivision(QR.getSurveyID());
						DepartmentList = QR.getDepartment(QR.getSurveyID(),
								QR.getDivisionID());
						groupList = QR.getGroup(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID());
						designationList = QR.getDesignation(QR.getSurveyID(),
								QR.getDivisionID(),QR.getDepartmentID(),
								QR.getGroupID());
					}
					if (request.getParameter("template") != null) {
						templateForm = request.getParameter("template");
						
					}
					if (request.getParameter("weightedAverage") != null) {
						weightedAverage = 1;
					} else {
						weightedAverage = 0;
					}
					if (request.getParameter("clusterCompetency") != null) {
						System.out.println("TRuE");
						clusterCompetency =true;
					
					}
					if (request.getParameter("rankingTable") != null) {
						
						rankTables =true;
					
					}
					
					if(request.getParameter("CGD") != null){
						CGD=true;
					}
					if(request.getParameter("splitCPR") != null){
						splitCPR=true;
					}
					if(request.getParameter("splitCP") != null){
						splitCP=true;
					}
					if(request.getParameter("round")!=null){
						int id= Integer.parseInt(request.getParameter("round"));
						QR.setRoundID(id);
					}

					//End of change made by Ha
					int jobPost = QR.getJobPostID();
					int surveyID = QR.getSurveyID();
					int divID = QR.getDivisionID();
					int deptID = QR.getDepartmentID();
					int groupID = QR.getGroupID();
					int roundID = QR.getRoundID();
					String designationName = QR.getDesignationName();

					if (jobPost != 0)
						surveyList = QR.getSurvey(jobPost);

					if (surveyID != 0) {
						DivisionList = QR.getDivision(surveyID);
						DepartmentList = QR.getDepartment(surveyID,
								QR.getDivisionID());
						groupList = QR.getGroup(surveyID,QR.getDivisionID(),
								QR.getDepartmentID());
						designationList = QR.getDesignation(surveyID,
								QR.getDivisionID(),QR.getDepartmentID(),
								QR.getGroupID());
						roundList=QR.getRound();
					}

					if (request.getParameter("open") != null) {
						int type = Integer.parseInt(request.getParameter("open"));
						//Denise 16/12/2009. To add the Exclude Group Ranking method
						int exGR = Integer.parseInt(request.getParameter("exGR"));
						Vector assignment = new Vector();
						SurveyResult s = new SurveyResult();
						jobPost = QR.getJobPostID();
						surveyID = QR.getSurveyID();
					
						// Added by Ha 26/05/08 adding checking null value-re-edit by Ha 06/06/08 name of parameter
						String sDivID = request.getParameter("div");
						if (sDivID != null) {
							if (!(sDivID.equals("0")))
								divID = Integer.parseInt(sDivID);
							else
								divID = 0;
						}

						String sDeptID = request.getParameter("dept");
						if (sDeptID != null) {
							if (!(sDeptID.equals("0")))
								deptID = Integer.parseInt(sDeptID);
							else
								deptID = 0;
						}
						if(request.getParameter("round")!=null){
							roundID = Integer.parseInt(request.getParameter("round"));
							QR.setRoundID(roundID);
						}
						String sGroupID = request.getParameter("groupName");
						if (sGroupID != null) {
							if (!(sGroupID.equals("0")))
								groupID = Integer.parseInt(sGroupID);
							else
								groupID = 0;
						}

						//Gwen Oh - 20/10/2011: Get selected designation (if any)
						String sDesignation = request.getParameter("designation");
						if (sDesignation != null) {
							if (!(designationName.equals("")))
								designationName = sDesignation;
						}
					
						Vector<Integer> deptIDList = new Vector<Integer>();
						Vector<Integer> groupIDList = new Vector<Integer>();

						if (divID == 0) { //division is all, deptID might be pointing more than one
							if (deptID == 0) { //department is all, group ID might be pointing more than one
								deptIDList.add(0);
								if (groupID == 0) {
									groupIDList.add(0);
								} else {
									String currentGroupName = "";
									groupList = QR.getGroup(QR.getSurveyID(),divID,
											deptID);
									//get the name of the current selected group
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getPKGroup() == groupID) {
											currentGroupName = voGrp.getGroupName();
										}
									}
									//get the groupIDs of all the groups with the same name as the current seleceted one
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getGroupName().equals(
												currentGroupName)) {
											groupIDList.add(voGrp.getPKGroup());
										}
									}
								}//end if groupID==0
							} else { //particular department, might point more than one department actually
								String currentDepartmentName = "";
								DepartmentList = QR.getDepartment(QR.getSurveyID(),
										divID);
								//get the current department name
								for (int i = 0; i < DepartmentList.size(); i++) {
									voDepartment voDept = (voDepartment) DepartmentList
											.elementAt(i);
									if (voDept.getPKDepartment() == deptID)
										currentDepartmentName = voDept
												.getDepartmentName();
								}
								//get all departmentIDs with the same name as the current department name
								for (int i = 0; i < DepartmentList.size(); i++) {
									voDepartment voDept = (voDepartment) DepartmentList
											.elementAt(i);
									if (voDept.getDepartmentName().equals(
											currentDepartmentName))
										deptIDList.add(voDept.getPKDepartment());
								}
								if (groupID == 0) {
									//get the groupIDs from all the groups
									for (int i = 0; i < deptIDList.size(); i++) {
										for (Object o : QR.getGroup(
												QR.getSurveyID(),divID,
												deptIDList.elementAt(i)))
											groupList.add(o);
									}
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										groupIDList.add(voGrp.getPKGroup());
									}
								} else {
									//get the groupIDs from all the groups
									for (int i = 0; i < deptIDList.size(); i++) {
										for (Object o : QR.getGroup(
												QR.getSurveyID(),divID,
												deptIDList.elementAt(i)))
											groupList.add(o);
									}
									String currentGroupName = "";
									//get the name of the current selected group
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getPKGroup() == groupID) {
											currentGroupName = voGrp.getGroupName();
										}
									}
									//get the groupIDs of all the groups with the same name as the current seleceted one
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getGroupName().equals(
												currentGroupName)) {
											groupIDList.add(voGrp.getPKGroup());
										}
									}
								}//end if groupID==0
							}//end if deptID==0
						} else { //divison is particular ID
							if (deptID == 0) {
								deptIDList.add(0);
								if (groupID == 0) {
									groupIDList.add(0);
								} else {
									String currentGroupName = "";
									groupList = QR.getGroup(QR.getSurveyID(),divID,
											deptID);
									//get the name of the current selected group
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getPKGroup() == groupID) {
											currentGroupName = voGrp.getGroupName();
										}
									}
									//get the groupIDs of all the groups with the same name as the current seleceted one
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList
												.elementAt(i);
										if (voGrp.getGroupName().equals(
												currentGroupName)) {
											groupIDList.add(voGrp.getPKGroup());
										}
									}
								}
							} else {
								deptIDList.add(deptID);
								groupIDList.add(groupID);
							}
						}

						Vector Target = new Vector();
						Date timeStamp = new java.util.Date();
						SimpleDateFormat dFormat = new SimpleDateFormat(
								"dd-MM-yy[HH.mm]");
						String temp = dFormat.format(timeStamp);
						String orgName = logchk.getOrgName(logchk.getOrg());
						String file_name = "GroupReport_" + orgName;
						if (divID != 0)
							file_name += "_" + QR.getDivisionName(divID);
						if (deptIDList.size() == 1 && deptIDList.get(0) != 0)
							file_name += "_"
									+ QR.getDepartmentName(deptIDList.get(0));
						if (groupIDList.size() == 1 && groupIDList.get(0) != 0)
							file_name += "_" + QR.getGroupName(groupIDList.get(0));
						file_name += "(" + temp + ").xls";
						String temp1 = "";
						System.out.println(surveyID + "--" + groupIDList + "--"
								+ deptIDList + "--" + divID + "--"
								+ designationName + "end" + file_name);

						//Changed by Ha 17/06/08 to automatically calculate if not all the raters have been calculated
						// when generating the report
						Target = s.TargetID(surveyID,divID,deptIDList,groupIDList,
								designationName);
						

						//GWEN: CONTINUE FROM HERE... DON'T HAVE TO CHANGE CALCULATESTATUS METHOD I THINK.. SINCE TARGETID IS ALREADY PASSED IN
						for (int j = 0; j < Target.size(); j++) {
							voUser vo = (voUser) Target.get(j);
							if (!s.checkCalculationStatusComplete(
									vo.getTargetLoginID(),surveyID,divID,deptID,
									groupID)) {
								if (!s.isAllRaterRated(surveyID,groupID,
										vo.getTargetLoginID()))
									s.CalculateStatus(vo.getTargetLoginID(),
											surveyID,divID,deptID,groupID,1);
								else
									s.CalculateStatus(vo.getTargetLoginID(),
											surveyID,divID,deptID,groupID,0);
							}
						}
						//Denise 16/12/2009 set the Exclude Group Ranking value 
						ExcelGroup.setExGroupRanking(exGR);

						//Gwen Oh - 17/10/2011: Set selected users to null if it's a group report (All the users in survey are required)
						ExcelGroup.setSelectedUsers(null);
					
						System.out.println("Template: " + templateForm);
						ExcelGroup.Report(surveyID,groupIDList,deptIDList,divID,pkUser,file_name,type,weightedAverage,clusterCompetency,templateForm,rankTables,roundID,CGD,splitCPR,splitCP);
						
						//session.setAttribute("Errors", errors);
						System.out.println("Redirect Start");
					
						RequestDispatcher dispatcher = request.getRequestDispatcher("GenerationComplete.jsp?Success="+ file_name);
						
						if(session.getAttribute("chosenFiles")!=null){
							Vector fileNames = (Vector)session.getAttribute("chosenFiles");
							fileNames.add(file_name);
							session.setAttribute("chosenFiles", fileNames);
						} else {
							Vector fileNames=new Vector();
							fileNames.add(file_name);
							session.setAttribute("chosenFiles", fileNames);
						}
							
						//request.setAttribute("open", null);
						//response.sendRedirect("GenerationComplete.jsp?Success="+ file_name); 
							
						 dispatcher.forward( request, response );
						
						
						 
						
					
					}
					
			%>
			</head>

	<body>
		<div class="container">
			<form name="GroupReport" method="post" action="GroupReport.jsp">

			<div class="row-fluid">
				<div class="offset1 span4">
					<h2><%=trans.tslt("Group Report")%></h2>
				</div>
				
			</div>
<%-- 			<div class="row-fluid">
				<div class="span5">
				<%if(session.getAttribute("errors")!=null){
					Vector reportError = (Vector)session.getAttribute("errors");
				
					for(int i=0;i<reportError.size();i++){ %>
				<span class="label label-important">Error: <%=(String)reportError.elementAt(i) %></span>
				<%}
				}%>
				</div></div> --%>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Organisation")%>	:
				</div>
				
				<%
					// Added to check whether organisation is also a consulting company
						// if yes, will display a dropdown list of organisation managed by this company
						// else, it will display the current organisation only
						// Mark Oei 09 Mar 2010
						String[] UserDetail = new String[14];
						UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
						boolean isConsulting = true;
						isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
						if (isConsulting) {
				%>
				<div class="span5">
					<select size="1" name="selOrg"
						onchange="proceed(this.form,this.form.selOrg)">
						<option value="0" selected><%=trans.tslt("Please select one")%></option>
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
					<%=trans.tslt("Job Position")%>:
				</div>

				<%
					int t = 0;
				%>
				<div class="span5">
					<select name="JobPost"
						onchange="getID(this.form, this.form.JobPost.options[JobPost.selectedIndex].value, 1)">
						<option value=<%=t%>><%=trans.tslt("Please select one")%>
							<%
								/*********************
									 * Edit By James 14-Nov 2007
									 ***********************/

									// while(jobPostList.next()) {
									for (int i = 0; i < jobPostList.size(); i++) {
										votblJobPosition voJob = (votblJobPosition) jobPostList
												.elementAt(i);
										int ID = voJob.getJobPositionID();
										String name = voJob.getJobPosition();

										jobPost = QR.getJobPostID();

										if (jobPost != 0 && ID == jobPost) {
							%>
						
						<option value=<%=ID%> selected><%=name%>
							<%
								} else {
							%>
						
						<option value=<%=ID%>><%=name%>
							<%
								}
									}
							%>
						
					</select><span class="label label-important"><%=jobPostError %></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Survey")%>
					:
				</div>
				<div class="span5">
					<select name="surveyName"
						onchange="getID(this.form, this.form.surveyName.options[surveyName.selectedIndex].value, 2)">
						<option value=<%=t%>><%=trans.tslt("Please select one")%>
							<%
								if (surveyList != null) {
										for (int j = 0; j < surveyList.size(); j++) {
											votblSurvey voSurvey = (votblSurvey) surveyList
													.elementAt(j);

											int ID = voSurvey.getSurveyID();
											String name = voSurvey.getSurveyName();
											surveyID = QR.getSurveyID();

											if (surveyID != 0 && ID == surveyID) {
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
						
					</select><span class="label label-important"><%=surveyIDError %></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Division")%>
					:
				</div>
				<div class="span5">
					<select name="division"
						onChange="populateDept(this.form, this.form.division, this.form.surveyName, this.form.JobPost)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								if (DivisionList != null) {
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
					<select name="department"
						onChange="populateGrp(this.form, this.form.division, this.form.department, this.form.surveyName, this.form.JobPost)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								DepartmentList = QR.getDepartment(QR.getSurveyID(),
											QR.getDivisionID());
									//delete duplicate entries with same department name but different IDs
									Vector<voDepartment> uniqueDeptList = new Vector<voDepartment>();
									for (int i = 0; i < DepartmentList.size(); i++) {
										voDepartment voDept = (voDepartment) DepartmentList
												.elementAt(i);
										Boolean duplicated = false;
										for (voDepartment vo : uniqueDeptList) {
											if (voDept.getDepartmentName().equals(
													vo.getDepartmentName())) {
												duplicated = true;
											}
										}
										if (!duplicated) {
											uniqueDeptList.add(voDept);
										}
									}
									if (uniqueDeptList != null) {
										for (int l = 0; l < uniqueDeptList.size(); l++) {
											voDepartment voDep = (voDepartment) uniqueDeptList
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

						</option>
					</select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Group")%>
					:
				</div>
				<div class="span5">
					<select name="groupName"
						onChange="populateDesignation(this.form, this.form.division, this.form.department, this.form.surveyName, this.form.JobPost, this.form.groupName)">
						<option value=<%=t%>><%=trans.tslt("All")%>
							<%
								surveyID = QR.getSurveyID();
									divID = QR.getDivisionID();
									deptID = QR.getDepartmentID();
									groupID = QR.getGroupID();

									if (deptID == 0) {// deptID="All" is selected
										groupList = QR.getGroup(surveyID,divID,deptID);
									} else {// particular deptID
										Vector sameNameDepartment = new Vector();
										String currentDepartmentName = "";
										//get the current department name
										for (int i = 0; i < DepartmentList.size(); i++) {
											voDepartment voDept = (voDepartment) DepartmentList
													.elementAt(i);
											if (voDept.getPKDepartment() == QR.getDepartmentID())
												currentDepartmentName = voDept.getDepartmentName();
										}
										//get all departmentIDs with the same name as the current department name
										for (int i = 0; i < DepartmentList.size(); i++) {
											voDepartment voDept = (voDepartment) DepartmentList
													.elementAt(i);
											if (voDept.getDepartmentName().equals(
													currentDepartmentName))
												sameNameDepartment.add(voDept);
										}
										//get the groups from different departments
										groupList = new Vector();
										for (int i = 0; i < sameNameDepartment.size(); i++) {
											voDepartment voDept = (voDepartment) sameNameDepartment
													.elementAt(i);
											for (Object o : QR.getGroup(surveyID,divID,
													voDept.getPKDepartment()))
												groupList.add(o);
										}
									}
									//delete duplicate entries with same group name but different IDs
									Vector<voGroup> uniqueGroupList = new Vector<voGroup>();
									for (int i = 0; i < groupList.size(); i++) {
										voGroup voGrp = (voGroup) groupList.elementAt(i);
										Boolean duplicated = false;
										for (voGroup vo : uniqueGroupList) {
											if (voGrp.getGroupName().equals(vo.getGroupName())) {
												duplicated = true;
											}
										}
										if (!duplicated) {
											uniqueGroupList.add(voGrp);
										}
									}
									if (uniqueGroupList != null) {
										for (int l = 0; l < uniqueGroupList.size(); l++) {
											voGroup voGrp = (voGroup) uniqueGroupList.elementAt(l);
											int ID = voGrp.getPKGroup();
											String name = voGrp.getGroupName();
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
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<%=trans.tslt("Designation")%>
					:
				</div>
				<div class="span5">
					<select name="designation">
						<option value=''><%=trans.tslt("All")%>
							<%
								if (designationList != null) {

										for (int m = 0; m < designationList.size(); m++) {

											String name = (String) designationList.elementAt(m);
											String selectedName = QR.getDesignationName();
											if (!name.startsWith("")
													&& name.startsWith(selectedName)) {
							%>
						
						<option value='<%=name%>' selected><%=name%>
							<%
								} else {
							%>
						
						<option value='<%=name%>'><%=name%>
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
					}%></select>
				
			
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
				Template:</div>
				<div class="span6">
				 <select size="1" name="template" style="width:320px">
      <%
	  		//Added display of templates of different languages, Chun Yeong 1 Aug 2011
	  		File dir = new File(Setting.getOOReportTemplatePath());
			String[] templates = dir.list();
			String defTemplate = "Group Report Template_General.xls"; //default template in English
			String hidTemplate = "Group Report Template.xls"; //template not used anymore in Individual Report but used in MOE Importance
			
			templates = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("Group Report Template") && !name.toLowerCase().contains("no_normative") && !name.toLowerCase().contains("nocpr") && !name.toLowerCase().contains("sps") && !name.toLowerCase().contains("old") && !name.toLowerCase().contains("allianz") && !name.toLowerCase().contains("combined"));
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
      </select></div>
			</div>
			<div class="row-fluid" style="padding-top: 20px">
				<h4>Report Customisation</h4>
			</div>
			<div class="row-fluid">
				<label><input type="checkbox"   class ="roundedTwo" name="chkReportType" value="ON">
				<span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option to omit the Competency charts.">Simplified Report
</label>
			</div>
			<div class="row-fluid">
<label> <input type="checkbox"  class ="roundedTwo"  name="chkGR" value="chkGR"> <span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option to omit the group ranking table.">Exclude
				Group Ranking
			</label></div>
			<div class="row-fluid">
<label>
				<input  class ="roundedTwo" type="checkbox" name="weightedAverage"
					value="weightedAverage"><span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option for the Current Proficiency(All)'s scores to be calculated based on the average of the different groups of raters' scores" > Weighted Average</span>
					</label>
			</div>
			
			<div class="row-fluid">
<label>
				<input  class ="roundedTwo" type="checkbox" name="CGD"
					value="CGD"><span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option for the rating's distribution to be shown in graphs" > Ratings (Graph)</span>
					</label>
			</div>
			<div class="row-fluid">
<label>
				<input  class ="roundedTwo" type="checkbox" name="splitCPR"
					value="splitCPR"><span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option to split the CPR scores" > Split CPR Scores</span>
					</label>
			</div><div class="row-fluid">
<label>
				<input  class ="roundedTwo" type="checkbox" name="splitCP"
					value="splitCPR"><span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option to split the CPR scores" > Split CP Scores</span>
					</label>
			</div>
			
			<div class="row-fluid">
<label>
				<input type="checkbox"  class ="roundedTwo"  name="clusterCompetency"
					value="clusterCompetency"/> <span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option for the competencies to be grouped under clusters.">Cluster Competency</span>
			</label></div>
			<div class="row-fluid">
<label> <input type="checkbox"  class ="roundedTwo"  name="rankingTable" value="rankingTable"> <span class="info" data-toggle="tooltip" title="" data-placement="right" data-original-title="Check this option to include the ranking table of the participants in the survey.">
				Ranking Tables
			</label></div>
					
			<div class="row-fluid"></div>
			<%
				if (compID != 2 || userType == 1) {
			%>
			<!-- <input type="button" name="btnSubgroup" value="<%=trans.tslt("Preview Subgroup")%>" onclick = "return openSubgroup(this.form)">
-->
			<div class="row-fluid" style="padding-top: 50px">
				<div class="span7">
					<button type="button" class="btn btn-primary generateBtn" name="btnOpen"
						value="<%=trans.tslt("Generate")%>"
						onclick="return confirmOpen(this.form)" data-loading-text="Generating...">Generate</button>
						<a type="button"class ="btn" href="ResetCalculation.jsp" >Reset Calculation</a>
					<a type="button" id="dlpage" class ="btn" href="GenerationComplete.jsp" >Download Page</a>
				</div>
			</div>
				<%
					} else {
				%>
				<!--<input type="button" name="btnSubgroup" value="<%=trans.tslt("Preview Subgroup")%>" onclick = "return openSubgroup(this.form)" disabled>
-->
				<div class="row-fluid">
					<div class="offset2 span2">
						<input type="button" name="btnOpen"
							value="<%=trans.tslt("Preview")%>"
							onclick="return confirmOpen(this.form)" disabled>
					</div></div>
					<%
						}
					%>



					<%
						}
					%>
				
		</form>
		

		<!-- Modal -->
		<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog"
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
		</div>

	</div>
	<script src="lib/js/bootstrap-tooltip.js"></script>
	<%@ include file="Footer.jsp"%>

</body>
</html>