<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page pageEncoding="UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Daily Schedule</title>

<jsp:useBean id="CoachSlotGroup" class="Coach.CoachSlotGroup"scope="session" />
<jsp:useBean id="CoachSlot" class="Coach.CoachSlot"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
</head>
<body>
<br>
<div align="center">
	<table align="center">
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Starting Time</font>
			</b></th>
			<th width="150" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Ending Time</font>
			</b></th>

			<%
				Vector CoachSlots = new Vector();
				int slotGroupPK=Integer.parseInt(request.getParameter("ViewSlotGroup"));
				CoachSlots = CoachSlotGroup.getSelectedSlotGroupDetails(slotGroupPK);
				voCoachSlotGroup slotGroup=CoachSlotGroup.getSelectedSlotGroup(slotGroupPK);
				%>
				<p align="center">
				<b><font color="#000080" size="2" face="Arial">Schedule Details For <%=slotGroup.getSlotGroupName()%></font></b>
				</p>
				<%
				int DisplayNo = 1;
				int pkslot=0;
				for (int i = 0; i < CoachSlots.size(); i++) {
					voCoachSlot voCoachSlot = new voCoachSlot();
					voCoachSlot = (voCoachSlot) CoachSlots.elementAt(i);

					pkslot = voCoachSlot.getPK();
					int startingTime = voCoachSlot.getStartingtime();
					int endingingTime = voCoachSlot.getEndingtime();
					//System.out.println("ending time" + endingingTime);
			%>
			<tr>
				<td align="center"><%=DisplayNo%></td>
				<td align="center"><%=startingTime%></td>
				<td align="center"><%=endingingTime%></td>
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
</div>
</body>
</html>