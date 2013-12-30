<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
<title>Venue</title>

<jsp:useBean id="Venue" class="Coach.CoachVenue"scope="session" />
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

	function addVenue(form){
		var myWindow=window.open('AddVenue.jsp','windowRef','scrollbars=no, width=480, height=250');
		myWindow.moveTo(x,y);
	    myWindow.location.href = 'AddVenue.jsp';
	}
			
	function editVenue(form, field){
		var value = check(field);
		if(value)
		{						
			var myWindow=window.open('EditVenue.jsp?editedVenue='+ value,'windowRef','scrollbars=no, width=480, height=250');
			var query = "EditVenue.jsp?editedVenue=" + value;
			myWindow.moveTo(x,y);
	    	myWindow.location.href = query;
		}
		
	}

	function deleteVenue(form, field) {
		var value = check(field);
		if (value) {
			if (confirm("Are you sure to delete the Venue slot")) {
				form.action = "Venue.jsp?deleteVenue=" + value;
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
		Vector Venues = new Vector();
		if(request.getParameter("deleteVenue")!= null){
			int PKVenue = Integer.valueOf(request.getParameter("deleteVenue"))  ;
			 Boolean delete =Venue.deleteCoachVenue(PKVenue);
			 if(delete){
				 %><script>
				 alert("Venue deleted successfully.");
				 </script><% 
				 Venues=Venue.getAllCoachVenue();
			 }
			 else{
				 %><script>
				 alert("Venue used in Coaching Assgiment and cannot be delete");
				 </script><% 
			 }
			
		}
	%>

	<p>
	<br>
		<b><font color="#000080" size="3" face="Arial">Venue Management</font></b>
	</p>

	<!-- list all the Schedule  -->
	

	<%
	Venues=Venue.getAllCoachVenue();
	%>
	<form>
		<br> 
		<table>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>&nbsp;</font>
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Address Line 1</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Address Line 2</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Address Line 3</font>
			</b></th>

			<%
				int DisplayNo = 1;
				
				for (int i = 0; i < Venues.size(); i++) {
					voCoachVenue voVenue = new voCoachVenue();
					voVenue = (voCoachVenue) Venues.elementAt(i);

					int pkVenue = voVenue.getVenuePK();
					String venue1=voVenue.getVenue1();
					String venue2=voVenue.getVenue2();
					String venue3=voVenue.getVenue3();
					
			%>
			<tr onMouseOver="this.bgColor = '#99ccff'"
				onMouseOut="this.bgColor = '#FFFFCC'">
				<td style="border-width: 1px"><font size="2"> <input type="radio" name="selVenue" value=<%=pkVenue%>></font></td>
				<td align="center"><%=DisplayNo%></td>
				<!-- column for venue 1 -->
				<%
			if(venue1==null||"".equalsIgnoreCase(venue1)){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%=venue1%></td>
				<%
			}
			 %>
				<!-- column for venue 1 -->
			 
				<!-- column for venue 2 -->
				<%
			if(venue2==null||"".equalsIgnoreCase(venue2)){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%=venue2%></td>
				<%
			}
			 %>
				<!-- column for venue 2 -->
			 
				<!-- column for venue 3 -->
				<%
			if(venue3==null||"".equalsIgnoreCase(venue3)){
			%><td align="center">&nbsp;</td><%
			}else{
							%>
				<td align="center"><%=venue3%></td>
				<%
			}
			 %>
				<!-- column for venue 3-->
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
		<!--  button for slot-->
		<p></p>
			<input class="btn btn-primary" type="button" name="AddVenue" value="Add Venue" onclick="addVenue(this.form)"> 
			<input class="btn btn-primary" type="button" name="EditVenue" value="Edit Venue" onclick="editVenue(this.form, this.form.selVenue)"> 
			<input class="btn btn-primary" type="button" name="DeleteVenue" value="Delete Venue" onclick="deleteVenue(this.form, this.form.selVenue)">
		
		<p></p>
	</form>
</body>
</html>