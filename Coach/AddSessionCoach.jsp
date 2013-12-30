<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
<title>Add Session Coach</title>

<jsp:useBean id="Coach" class="Coach.Coach"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<jsp:useBean id="SessionSetup" class="Coach.SessionSetup" scope="session" />
<script type="text/javascript">
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

function addCoach(form, field){
	var value = check(field);
	if(value)
	{	
		var myWindow=window.open('AddSessionCoach.jsp?addSessionCoach='+ value,'windowRef','scrollbars=no, width=480, height=250');
		var query = "AddSessionCoach.jsp?addSessionCoach=" + value;
		myWindow.moveTo(x,y);
    	myWindow.location.href = query;
	}
	
}

</script>
</head>
<body>

	<%
	Vector Coachs = new Vector();
	Coachs=Coach.getAllCoach();	
	
	%>
	<%
	if(request.getParameter("addSessionCoach")!=null){
			int coachPK=Integer.parseInt(request.getParameter("addSessionCoach"));
		
		

		// check whether SlotGroup name already exists in database
		 Boolean Exist = false;
		int datePK=SessionSetup.getSelectDateToEdit();
		Vector coaches=SessionSetup.getCoachBySessionIDandDateID(datePK);
		for(int i = 0; i < coaches.size(); i++){
			voCoach vo = (voCoach)coaches.elementAt(i);
			int existingCoachID=vo.getPk();
			//System.out.println("Existing Schedule Name:"+slotGroupName);
			if(existingCoachID==coachPK){
				Exist = true;
				System.out.println("Same Coach Name");
			}

		}

	
	if(!Exist) {						
		try{					
			boolean add =SessionSetup.addSessionCoach(datePK, coachPK);
			if(add){
				%>
	<script>
		opener.location.href='SelectCoach.jsp';
		opener.location.reload(true);
		window.location.href  = 'EditSessionCoach.jsp';
	</script>
	<% 
			}
			else{
				
			}
		}catch(Exception SE) {
             System.out.println(SE);
		}
		} else {			
			%>
	<script>
		alert("This coach has been selected already");
		window.location.href = 'AddSessionCoach.jsp';
	</script>
	<%			
	}
	}
	%>
	<div align="center">
	<form >
		<br>
		<p><b><font color="#000080" size="3" face="Arial">Select Coach</font></b></p>
		<table>
			
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>&nbsp;</font>
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Name</font>
			</b></th>

			<%
				int DisplayNo = 1;
				
				for (int i = 0; i < Coachs.size(); i++) {
					voCoach voCoach = new voCoach();
					voCoach = (voCoach) Coachs.elementAt(i);

					int pkCoach = voCoach.getPk();
					String name=voCoach.getCoachName();
					String link=voCoach.getLink();
			%>
			<tr onMouseOver="this.bgColor = '#99ccff'"
				onMouseOut="this.bgColor = '#FFFFCC'">
				<td style="border-width: 1px"><font size="2"> <input type="radio" name="selCoach" value=<%=pkCoach%>></font></td>
				<td align="center"><%=DisplayNo%></td>
				<td align="center"><%=name%></td>
				
				
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
		<!--  button for slot-->
		<p></p>
			<input class="btn btn-primary" type="button" name="AddSlot" value="Add Coach" onclick="addCoach(this.form,this.form.selCoach)"> 
		<p></p>
	</form>
</div>
</body>
</html>