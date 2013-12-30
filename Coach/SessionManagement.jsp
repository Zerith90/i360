<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Date"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
<title>Session Management</title>

<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<jsp:useBean id="SessionSetup" class="Coach.SessionSetup" scope="session" />


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

	function addSession(form){
		window.location.href = 'SessionName.jsp';
	}
			
	function editSession(form, field){
		var value = check(field);
		if(value)
		{						
			var myWindow=window.open('EditVenue.jsp?editedSession='+ value,'windowRef','scrollbars=no, width=480, height=250');
			var query = "EditSession.jsp?editedSession=" + value;
			myWindow.moveTo(x,y);
	    	myWindow.location.href = query;
		}
		
	}
	function setupCutOffDate(form, field){
		var value = check(field);
		if(value)
		{						
			var myWindow=window.open('EditSessionCutOffDate.jsp?editedSession='+ value,'windowRef','scrollbars=no, width=480, height=350');
			var query = "EditSessionCutOffDate.jsp?editedSession=" + value;
			myWindow.moveTo(x,y);
	    	myWindow.location.href = query;
		}
		
	}
</script>
</head>
<body>
	<%@ include file="nav.jsp" %> 

	<!-- list all the Schedule  -->

	<%	
		Vector<voCoachSession> sessions = new Vector<voCoachSession>();
		//sessions=SessionSetup.getSessionAllNames();
		sessions=SessionSetup.getAllSession();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
	%>
	<form>
	<p>	
	<br>
		<b><font color="#000080" size="3" face="Arial">Coaching Session Management</font></b>
	<br>
	</p>
		<br> <%=sessions.size() %>
		<table>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>&nbsp;</font>
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Session Name</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Session Description</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Maximum Sign-Ups</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Cut-Off Date</font>
			</b></th>

			<%
				int DisplayNo = 1;
				
				for (int i = 0; i < sessions.size(); i++) {
					voCoachSession coachSession = new voCoachSession();
					coachSession = (voCoachSession) sessions.elementAt(i);
					int coachSessionPK=coachSession.getPK();
					String coachSessionName=coachSession.getName();
					String coachSessionDes=coachSession.getDescription();
					int coachSessionMAX=coachSession.getSessionMax();
					Date closeDate=coachSession.getCloseDate();
					System.out.println("Close date: " + closeDate);
										
			%>
			<tr onMouseOver="this.bgColor = '#99ccff'"
				onMouseOut="this.bgColor = '#FFFFCC'">
				<td style="border-width: 1px"><font size="2"> <input type="radio" name="selSession" value=<%=coachSessionPK%>></font></td>
				<td align="center"><%=DisplayNo%></td>
				<!-- column for coachSessionName 1 -->
				<%
			if(coachSessionName==null||"".equalsIgnoreCase(coachSessionName)){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%=coachSessionName%></td>
				<%
			}
			 %>
				<!-- column for coachSessionName 1 -->
			 
				<!-- column for coachSessionDes 2 -->
				<%
			if(coachSessionDes==null||"".equalsIgnoreCase(coachSessionDes)){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%=coachSessionDes%></td>
				<%
			}
			 %>
				<!-- column for coachSessionDes 2 -->
			 
				<!-- column for coachSessionMAX 3 -->
				<td align="center"><%=coachSessionMAX%></td>
				<!-- column for coachSessionMAX 3-->
				<!-- column for Session closing date -->
				<%
			if(closeDate==null){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%= sdf.format(closeDate)%></td>
				<%
			}
			 %>
				<!-- column for Session closing date  -->
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
		<!--  button for slot-->
		<p></p>
			<input class="btn btn-primary" type="button" name="AddSession" value="Add Session" onclick="addSession(this.form)"> 
			<input class="btn btn-primary" type="button" name="EditSession" value="Edit Session" onclick="editSession(this.form, this.form.selSession)"> 
			<input class="btn btn-primary" type="button" name="SetUpCloseDate" value="Setup Cut-Off Date" onclick="setupCutOffDate(this.form, this.form.selSession)"> 
		<p></p>
	</form>
</body>
</html>