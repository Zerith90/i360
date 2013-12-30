<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
<title>Coaching Date</title>

<jsp:useBean id="CoachDateGroup" class="Coach.CoachDateGroup"scope="session" />
<jsp:useBean id="CoachDate" class="Coach.CoachDate"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />


<script>
var x = parseInt(window.screen.width) / 2 - 240;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 115;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up


function check(field)
{
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
		alert("No record selected");
	else if(isValid == 2)
		alert("No record available");
	
	isValid = 0;

}

	function proceed(form) {
		form.action = "DateGroup.jsp?proceed=1";
		form.method = "post";
		form.submit();
	}
	function addDateGroup(form){
		var myWindow=window.open('AddDateGroup.jsp','windowRef','scrollbars=no, width=580, height=250');
		myWindow.moveTo(x,y);
	    myWindow.location.href = 'AddDateGroup.jsp';
	}
		
	function editDateGroup(form, field){
		var myWindow=window.open('EditDateGroup.jsp','windowRef','scrollbars=no, width=580, height=250');
		myWindow.moveTo(x,y);
	    myWindow.location.href = 'EditDateGroup.jsp';
	}

	function deleteDateGroup(form, field){
		if(confirm("Are you sure to delete the Period")){
		form.action="DateGroup.jsp?deleteDateGroup="+field.value;
		form.method="post";
		form.submit();
	}
	}
	function addDate(form){
		var myWindow=window.open('AddDate.jsp','windowRef','scrollbars=no, width=480, height=400');
		myWindow.moveTo(x,y);
	    myWindow.location.href = 'AddDate.jsp';
	}
		
	function editDate(form, field){
		var value = check(field);
		
		if(value)
		{						
			var myWindow=window.open('EditDate.jsp?editedDate='+ value,'windowRef','scrollbars=no, width=480, height=400');
			var query = "EditDate.jsp?editedDate=" + value;
			myWindow.moveTo(x,y);
	    	myWindow.location.href = query;
		}
		
	}


	function deleteDate(form, field) {
		var value = check(field);

		if (value) {
			if (confirm("Are you sure to delete the coaching Date")) {
				form.action = "DateGroup.jsp?deleteDate=" + value;
				form.method = "post";
				form.submit();
			}
		}
	}
</script>
</head>
<body>
<%@ include file="nav.jsp" %> 
	<%
		int logDateGroupPK = 0;
		int PKselDateGroup = CoachDateGroup.getFirtDateGroupPK();
		System.out.println(CoachDateGroup.getFirtDateGroupPK());
		Vector CoachDates = new Vector();
		if (request.getParameter("proceed") == null) {
			/* start up get the select Period detail vector */

			//System.out.println("init Period jsp:");
			if(LoginStatus.getSelectedDateGroup()==0){
				CoachDates = CoachDateGroup.getSelectedDateGroupDetails(1);
				LoginStatus.setSelectedDateGroup(PKselDateGroup);
				//logDateGroupPK = PKselDateGroup;
			}else{
				CoachDates = CoachDateGroup.getSelectedDateGroupDetails(LoginStatus.getSelectedDateGroup());
				logDateGroupPK = LoginStatus.getSelectedDateGroup();
			}
			
		}
		if (request.getParameter("proceed") != null) {
			
			if (Integer.parseInt(request.getParameter("selDateGroup"))==0) {
				%>
				<script>
				alert("Please Select Coaching Period");
				</script>
				<%
				
			}
			if (request.getParameter("selDateGroup") != null) {
				/* get the select Period detail vector */
				//System.out.println("selDateGroup"+request.getParameter("selDateGroup"));
				PKselDateGroup = Integer.parseInt(request.getParameter("selDateGroup"));
				//System.out.println("Old Page selDateGroup jsp:" + PKselDateGroup);
				CoachDates = CoachDateGroup.getSelectedDateGroupDetails(PKselDateGroup);
				LoginStatus.setSelectedDateGroup(PKselDateGroup);
				logDateGroupPK = PKselDateGroup;
			}
		}
		if(request.getParameter("deleteDateGroup")!= null){
			int PKDateGroup = new Integer(request.getParameter("deleteDateGroup")).intValue();
			//System.out.println("deleteDateGroup PK:"+PKDateGroup);
			 Boolean delete =CoachDateGroup.deleteDateGroup(PKDateGroup);
			 if(delete){
				 LoginStatus.setSelectedDateGroup(1);
				 CoachDates = CoachDateGroup.getSelectedDateGroupDetails(1);
				 %><script>
				 alert("Period deleted successfully.");
				 </script><% 
			 }
			 else{
				 %><script>
				 alert("Coaching Period used in Coaching Assgiment and cannot be deleted");
				 </script><% 
			 }
			
		}
		if(request.getParameter("deleteDate")!= null){
			//System.out.println("request:"+request.getParameter("deleteDate"));
			int PKDate = Integer.valueOf(request.getParameter("deleteDate"))  ;
			//System.out.println("deleteDate PK:"+PKDate);
			 Boolean delete =CoachDate.deleteDate(PKDate);
			 if(delete){
				 %><script>
				 alert("Coaching Date used in Coaching Assgiment and cannot be deleted");
				 </script><% 
				 CoachDates = CoachDateGroup.getSelectedDateGroupDetails(LoginStatus.getSelectedDateGroup());
				 logDateGroupPK =  LoginStatus.getSelectedDateGroup();
			 }
			 else{
				 %><script>
				 alert("An error occured while trying to delete the coaching Date.");
				 </script><% 
			 }
			
		}
	%>

	<p>	
	<br>
		<b><font color="#000080" size="3" face="Arial">Coaching Period Management</font></b>
	<br>
	</p>
	<!-- list all the Period  -->
	<%
		Vector DateGroupList = new Vector();
		DateGroupList = CoachDateGroup.getAllDateGroup();
		String dateGroupdes=CoachDateGroup.getSelectedDateGroup(logDateGroupPK).getdescription();
		String dateGroupName=CoachDateGroup.getSelectedDateGroup(logDateGroupPK).getName();
		//System.out.println("size of Period size jsp: " + DateGroupList.size());
	%>

	<!-- display Period-->
	<form>
		<table>
			<tr>
				<td width="140"><font face="Arial" size="2">Coaching Period Name:</font></td>
				<td width="23">:</td>
				<td width="500" colspan="1"><select size="1"
					name="selDateGroup" onChange="proceed(this.form)">
					<option value=0>Please select a Coaching Period Name</option>
						<%
							voCoachDateGroup voCoachDateGroup = new voCoachDateGroup();
							
							for (int i = 0; i < DateGroupList.size(); i++) {
								voCoachDateGroup = (voCoachDateGroup) DateGroupList
										.elementAt(i);
								int DateGroupPK = voCoachDateGroup.getPK();
								String DateGroupName = voCoachDateGroup.getName();
								String DateGroupDis=voCoachDateGroup.getdescription();

								if (logDateGroupPK == DateGroupPK) {
						%>
						<option value=<%=DateGroupPK%> selected><%=DateGroupName%>
							<%
								} else {
							%>
						
						<option value=<%=DateGroupPK%>><%=DateGroupName%>
							<%
								}
								}
							
							%>
						
				</select></td>
			</tr>
		</table>
		<!--  button for Period-->
		<p></p>
		<input class="btn btn-primary" type="button" name="AddDateGroup" value="Add Period"
			onclick="addDateGroup(this.form)"> 
		<input class="btn btn-primary" type="button" name="EditDateGroup" value="Edit Period"
			onclick="editDateGroup(this.form, this.form.selDateGroup)"> 
		<input class="btn btn-primary" type="button" name="DeleteDateGroup" value="Delete Period"
			onclick="deleteDateGroup(this.form, this.form.selDateGroup)">
		<br>
		<br>
		<font face="Arial" size="2">Coaching Period Description: <%=dateGroupdes%></font>
		

		<br>
		<!--Display selected Period details  -->
		<br> <br> <br> <br>
			<p>
				<b><font color="#000080" size="2" face="Arial">Coaching Period Details for <%=dateGroupName%></font></b>
			</p>
		<table>
			
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>&nbsp;</font>
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Coaching Date</font>
			</b></th>
			

				<%
				int DisplayNo = 1;
				int pkDate=0;
				
				
				for (int i = 0; i < CoachDates.size(); i++) {
					voCoachDate voCoachDate = new voCoachDate();
					voCoachDate = (voCoachDate) CoachDates.elementAt(i);
					pkDate = voCoachDate.getPK();
					String date=voCoachDate.getDate();
					String[] DateInParts=date.split(" ");
					String dateWithoutTime=DateInParts[0];
					String[] DateWithoutTimeInParts=dateWithoutTime.split("-");
					String finalDate=DateWithoutTimeInParts[2]+"-"+DateWithoutTimeInParts[1]+"-"+DateWithoutTimeInParts[0];
					
					//System.out.println("ending time" + endingingTime);
			%>
			<tr onMouseOver="this.bgColor = '#99ccff'"
				onMouseOut="this.bgColor = '#FFFFCC'">
				<td style="border-width: 1px"><font size="2"> <input type="radio" name="selDate" value=<%=pkDate%>></font></td>
				<td align="center"><%=DisplayNo%></td>
				<td align="center"><%=finalDate%></td>
				
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
		<!--  button for Date-->
		<p></p>
			<input class="btn btn-primary" type="button" name="AddDate" value="Add Coaching Date" onclick="addDate(this.form)"> 
			<input class="btn btn-primary" type="button" name="EditDate" value="Edit Coaching Date" onclick="editDate(this.form, this.form.selDate)"> 
			<input class="btn btn-primary" type="button" name="DeleteDate" value="Delete Coaching Date" onclick="deleteDate(this.form, this.form.selDate)">
		
		<p></p>
	</form>
</body>
</html>