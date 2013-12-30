<%@ page import="java.sql.*,
				java.util.*,
                 java.io.* "%>  

<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                    
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="SCluster" class="CP_Classes.SurveyCluster" scope="session"/>
<jsp:useBean id="SC" class="CP_Classes.SurveyCompetency" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<%@ page import="CP_Classes.vo.votblSurveyCompetency"%>
<%@ page import="CP_Classes.vo.votblSurveyCluster"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
</head>

<SCRIPT LANGUAGE=JAVASCRIPT>
var x = parseInt(window.screen.width) / 2 - 500;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 300;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up

<!-- added by Albert (16/07/2012): add a checkbox on top to choose all -->
function checkedAll(form, field, checkAll)
{	
	if(checkAll.checked == true) 
		for(var i=0; i<field.length; i++)
			field[i].checked = true;
	else 
		for(var i=0; i<field.length; i++)
			field[i].checked = false;	
}

function goNext(form){
 	form.action="SurveyCompetency.jsp?next=1";
	form.method="post";
	form.submit();
}

var myWindow;
function windowOpen() {
	myWindow=window.open('Survey_CompetencyList.jsp','windowRef','scrollbars=no, width=650, height=600');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'Survey_CompetencyList.jsp';
}

function windowOpenCluster(form){
	if(form.clusterDropList.value == -1) {
		alert("Please select a cluster");
	} else{
		myWindow=window.open('Survey_ClusterCompetencyList.jsp','windowRef','scrollbars=no, width=650, height=600');
		myWindow.moveTo(x,y);
    	myWindow.location.href = 'Survey_ClusterCompetencyList.jsp';
	}
}

function check(field){
	var isValid = 0;
	var clickedValue = 0;
	//check whether any checkbox selected
	if( field == null ) {
		isValid = 2;
	} else {
		if(isNaN(field.length) == false) {
			for (i = 0; i < field.length; i++)
				if(field[i].checked) {
					clickedValue = field[i].value;
					isValid = 1;
				}
		}else {		
			if(field.checked) {
				clickedValue = field.value;
				isValid = 1;
			}
				
		}
	}
	
	if(isValid == 1)
		return clickedValue;
	else if(isValid == 0)
		alert("<%=trans.tslt("No record selected")%>");
	else if(isValid == 2)
		alert("<%=trans.tslt("No record available")%>");
	
	isValid = 0;
}
 
function remove(form, field){
	if(check(field)){
		if(confirm("<%=trans.tslt("Remove Assigned Competency")%>?")){
			form.action="SurveyCompetency.jsp?remove=1";
			form.method="post";
			form.submit();
		}
		
	}
}

function closeAllPopout() {
	if (myWindow != null) {
		myWindow.close();
	}
}

function changeCluster(form, field){
	form.action="SurveyCompetency.jsp?clustChange="+field.value;
	form.method="post";
	form.submit();
}

function goBack() {
	var boolCluster = false;
<%
	if(SCluster.getUseCluster(CE_Survey.getSurvey_ID())!=false){
%>
		boolCluster = true;
<%		
	}
%>
	if(boolCluster==false){
		window.location = "SurveyDetail.jsp";
	}else{ 
		window.location = "SurveyCluster.jsp";
	}
}
 </SCRIPT>
 
<body onUnload="closeAllPopout()">
<%
String username=(String)session.getAttribute("username");

if (!logchk.isUsable(username)) {
%> 
	<font size="2">
	<script>
		parent.location.href = "index.jsp";
	</script>
<%  
} else{ 
	if(request.getParameter("next") != null){
		if(CE_Survey.getCompetencyLevel() == 0){
%>
		<Script>
			window.location = "SurveyDemos.jsp";
		</Script>
<%		}else if(CE_Survey.getCompetencyLevel() == 1){%>
		<Script>
			window.location = "SurveyKeyBehaviour.jsp";
		</Script>
<%		
		}			
	}
	if(request.getParameter("remove") != null){
		int SurveyID = CE_Survey.getSurvey_ID();
		String [] CompID = request.getParameterValues("chkDel");
	
		// Edited by Eric Lu 21/5/08
		// Added new boolean to check whether competency has been removed successfully
		boolean bCompetencyRemoved = true;
 
		if(CompID != null){ 
			for(int i=0; i<CompID.length; i++){
				if(SCluster.getUseCluster(SurveyID)==false){//if not using cluster
					if (!CE_Survey.delCompetency(SurveyID,CompID[i]))
						bCompetencyRemoved = false;
				} else{//if using cluster
					if (!CE_Survey.delCompetency(SurveyID,CE_Survey.getClusterID(),CompID[i]))
						bCompetencyRemoved = false;
				}
			}
		}
	
		if (bCompetencyRemoved) {
%>
		<script>
			alert("Removed successfully, survey status has been changed to Non Commissioned, to re-open survey please go to the Survey Detail page");
		</script>
<%
		} else {
%>
		<script>
			alert("Removed unsuccessfully");
		</script>
<%
		}
	}
	if (request.getParameter("clustChange")!=null){		
	   	int id = Integer.parseInt(request.getParameter("clustChange"));
		CE_Survey.setClusterID(id);		
	}
	if(request.getParameter("sorting") != null){	 
		int type = new Integer(request.getParameter("sorting")).intValue();
		int toggle = CE_Survey.getToggle();	//0=asc, 1=desc
		CE_Survey.setSortType(type);
	
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		CE_Survey.setToggle(toggle);
	} else{
		CE_Survey.setSortType(1);
	}
%>
<form name="SurveyCompetency" action="SurveyCompetency.jsp" method="post">
<table border="0" width="81%" cellspacing="0" cellpadding="0" bordercolor="#000000" style="border-width: 0px">
	<tr>
		<td width="114" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700">
		<a href="SurveyDetail.jsp" style="cursor:pointer"><%=trans.tslt("Survey Detail")%></a></font></td>
		
		<td width="28" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="101" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color=blue><u>
		<a href="SurveyCluster.jsp" style="cursor:pointer"><%=trans.tslt("Cluster")%></a></u></font></td>
		
		<td width="28" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="101" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color="#CC0000"><%=trans.tslt("Competency")%></font></td>
		
		<td width="20" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="113" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyKeyBehaviour.jsp" style="cursor:pointer"><%=trans.tslt("Key Behaviour")%></a></font></b></td>
		
		<td width="24" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="109" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyDemos.jsp" style="cursor:pointer"><%=trans.tslt("Demographics")%></a></font></b></td>
		
		<td width="23" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="132" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyRating.jsp" style="cursor:pointer"><%=trans.tslt("Rating Task")%></a></font></b></td>
	</tr>
	<tr>
		<td width="114" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="132" style="border-style: none; border-width: medium">
		<p align="center"> <font size="2">
   
		<span style="font-weight: 700">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color=blue><u>
		<a href ="AdvanceSettings.jsp" style="cursor:pointer"><%=trans.tslt("Advanced Settings")%></a></u></font></span></td>
	</tr>
	<tr height="30">
		<td width="114" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="135" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyPrelimQ.jsp?entry=1" style="cursor:pointer"><%= trans.tslt("Preliminary Questions") %></a></font></b></td>
	</tr>
	<tr height="30">
		<td width="114" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="135" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="AdditionalQuestions.jsp?entry=1" style="cursor:pointer"><%= trans.tslt("Additional Questions") %></a></font></b></td>
	</tr>
	<tr>
		<td width="114" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="132" style="border-style: none; border-width: medium">&nbsp;
		</td>
	</tr>
</table>
<%
/***************************** THIS PART IS IF SURVEY NOT USING CLUSTER *************************************/
if(SCluster.getUseCluster(CE_Survey.getSurvey_ID())==false){ //not using cluster
%>
<div style='width:627px; height:256px; z-index:1; overflow:auto'>  
		<table border="1" width="610" bordercolor="#3399FF" bgcolor="#FFFFCC">
		<tr>
				<td colspan="4" bgcolor="#000080">
				<p align="center"><span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Competency")%></font></span></td>
			</tr>
		
		<tr>
				
					<td bgcolor="#000080" width="3%" align="center">
					<font size="2">
	  				<input type="checkbox" name="checkAll" onClick="checkedAll(this.form, this.form.chkDel,this.form.checkAll)"></font>
					</td>
		
				<td bgcolor="#000080" width="24%" align="center">
				<span style="font-weight: 700">
				<!-- Edited by Xuehai, 07 Jun 2011, Sorting by Name -->
				<a href="SurveyCompetency.jsp?sorting=1">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Name")%></font>
				</a>
				<!-- End of Edited -->
				</span></td>
				<td bgcolor="#000080" width="59%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Description")%></font></span></td>
				<td bgcolor="#000080" width="66%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Level")%></font></span></td>
			</tr>
<%
	int counter = 1;
		//Edited by Xuehai, 07 Jun 2011, Adding 'sorting'.
		//Vector vComp = SC.getSurveyCompetency(CE_Survey.getSurvey_ID());
		Vector vComp = SC.getSurveyCompetency(CE_Survey.getSurvey_ID(), CE_Survey.getToggle(), CE_Survey.getSortType());
		
		for(int i=0; i<vComp.size(); i++)
		{
			votblSurveyCompetency vo = (votblSurveyCompetency)vComp.elementAt(i);
			int CompLevel = vo.getCompetencyLevel();
			int CompID = vo.getCompetencyID();
			String CompName = vo.getCompetencyName();
			String CompDef = vo.getCompetencyDefinition();	
						
%>		
		<tr onMouseOver = "this.bgColor = '#99ccff'"
    	onMouseOut = "this.bgColor = '#FFFFcc'">
    	
    			<td width="3%" align="left"><font face="Arial" size="2">
					<p align="center">
				<%
				if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
				{ 	%>	
					
					<input type="checkbox" name="chkDel" value=<%=CompID%>>
				<%	}	
					else
					{%>
						<%=counter%>.
				<%	}%>
				</font></td>
				<td width="24%" align="left">
				<font face="Arial" size="2">&nbsp;<%=CompName%></FONT></td>
				<td width="59%" align="left">
				<font face="Arial" size="2">&nbsp;<%=CompDef%></font></td>
				<td width="30%" align="center">
				<font face="Arial" size="2">&nbsp;<%=CompLevel%></font></td>
			</tr>
<%	counter++;	
}	%>			
		
		</table>
</div>

<table border="0" width="610" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td width="20%">
		<input type="button" value="<%=trans.tslt("Back")%>" name="btnBack" style="float: left" onClick="goBack()"></td>
		
		<%
		if(CE_Survey.getSurveyStatus() != 2)
		{
			if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
			{ 		
			%>
			<td width="26%">
			<input type="button" value="<%=trans.tslt("Add Competency")%>" name="btnAdd" style="float: right" onClick="windowOpen()">
			</td>
			<td width="24%">
			<input type="button" value="<%=trans.tslt("Remove")%>" name="btnDel" style="float: right" onClick="remove(this.form, this.form.chkDel)">
			</td>
			<%}
		}%>
		
		<td width="30%">
		<input type="button" value="<%=trans.tslt("Save")%> &amp; <%=trans.tslt("Proceed")%>" name="btnNext" style="float: right" onClick="goNext(this.form)"></td>

	</tr>
</table>
<%
}
/***************************** END OF PART NOT USING CLUSTER ********************************************/


/***************************** THIS PART IS IF SURVEY USING CLUSTER *************************************/
else{ //using cluster
	Vector clusterList = null;
%>
<div style='width:627px; height:256px; z-index:1; overflow:auto'>
	<table>
		<tr><td><%=trans.tslt("Cluster")%> : </td>
		<td>
  		<select name="clusterDropList" size="1" onChange="changeCluster(this.form, this.form.clusterDropList)">
  			<option value=-1><%=trans.tslt("Please select one")%></option>
<%
			clusterList = SCluster.getSurveyCluster(CE_Survey.getSurvey_ID());
			System.out.println(clusterList.size() + "hihi");
			for(int i=0; i<clusterList.size(); i++){
				votblSurveyCluster vCluster = (votblSurveyCluster) clusterList.elementAt(i);
				int pkCluster = vCluster.getClusterID();
				String clusterName = vCluster.getClusterName();
				if(CE_Survey.getClusterID() != 0 && pkCluster == CE_Survey.getClusterID()){
%>					<option value = <%=pkCluster%> selected><%=clusterName%></option>
<% 				} else{ 
%>    				<option value = <%=pkCluster%>><%=clusterName%></option>
<%  	  
		 		}
			} //end for loop
%>  			
  		</select>
  		</td>
  		</tr>
  	</table> <br>
  		
	<table border="1" width="610" bordercolor="#3399FF" bgcolor="#FFFFCC">
		<tr>
				<td colspan="4" bgcolor="#000080">
				<p align="center"><span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Competency")%></font></span></td>
			</tr>
		
		<tr>
				
					<td bgcolor="#000080" width="3%" align="center">
					<font size="2">
	  				<input type="checkbox" name="checkAll" onClick="checkedAll(this.form, this.form.chkDel,this.form.checkAll)"></font>
					</td>
		
				<td bgcolor="#000080" width="24%" align="center">
				<span style="font-weight: 700">
				<!-- Edited by Xuehai, 07 Jun 2011, Sorting by Name -->
				<a href="SurveyCompetency.jsp?sorting=1">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Name")%></font>
				</a>
				<!-- End of Edited -->
				</span></td>
				<td bgcolor="#000080" width="59%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Description")%></font></span></td>
				<td bgcolor="#000080" width="66%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Level")%></font></span></td>
			</tr>
<%
		int counter = 1;
		Vector vComp = SC.getSurveyClusterCompetency(CE_Survey.getSurvey_ID(), CE_Survey.getClusterID(), CE_Survey.getToggle(), CE_Survey.getSortType());
		
		for(int i=0; i<vComp.size(); i++){
			votblSurveyCompetency vo = (votblSurveyCompetency)vComp.elementAt(i);
			int CompLevel = vo.getCompetencyLevel();
			int CompID = vo.getCompetencyID();
			String CompName = vo.getCompetencyName();
			String CompDef = vo.getCompetencyDefinition();	
						
%>		
		<tr onMouseOver = "this.bgColor = '#99ccff'"
    	onMouseOut = "this.bgColor = '#FFFFcc'">
    	
    			<td width="3%" align="left"><font face="Arial" size="2">
					<p align="center">
				<%
				if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
				{ 	%>	
					
					<input type="checkbox" name="chkDel" value=<%=CompID%>>
				<%	}	
					else
					{%>
						<%=counter%>.
				<%	}%>
				</font></td>
				<td width="24%" align="left">
				<font face="Arial" size="2">&nbsp;<%=CompName%></FONT></td>
				<td width="59%" align="left">
				<font face="Arial" size="2">&nbsp;<%=CompDef%></font></td>
				<td width="30%" align="center">
				<font face="Arial" size="2">&nbsp;<%=CompLevel%></font></td>
			</tr>
<%	counter++;	
}	%>			
		
		</table>
</div>

<table border="0" width="610" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td width="20%">
		<input type="button" value="<%=trans.tslt("Back")%>" name="btnBack" style="float: left" onClick="goBack()"></td>
		
		<%
		if(CE_Survey.getSurveyStatus() != 2)
		{
			if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
			{ 		
			%>
			<td width="26%">
			<input type="button" value="<%=trans.tslt("Add Competency")%>" name="btnAdd" style="float: right" onClick="windowOpenCluster(this.form)">
			</td>
			<td width="24%">
			<input type="button" value="<%=trans.tslt("Remove")%>" name="btnDel" style="float: right" onClick="remove(this.form, this.form.chkDel)">
			</td>
			<%}
		}%>
		
		<td width="30%">
		<input type="button" value="<%=trans.tslt("Save")%> &amp; <%=trans.tslt("Proceed")%>" name="btnNext" style="float: right" onClick="goNext(this.form)"></td>

	</tr>
</table>
<%
}
/***************************** END OF PART USING CLUSTER ************************************************/
%>
</form>

<%	
} //end else logchk.isUsable(username)
%>
<p></p>
<%@ include file="Footer.jsp"%>
</body>
</html>