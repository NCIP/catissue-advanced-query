<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.query.util.global.Constants"%>
<html>
<body>
<table border="0"  height="100%" width="100%" cellpadding="1" cellspacing="3" valign="top">
	
<tr  valign="top">
	<td  valign="top" width="20%">
		<table border="0"  height="100%" width="100%" cellpadding="1" cellspacing="3" valign="top">
			<tr  class='validationMessageCss'  >
				<td width="80%" class='validationMessageCss' style="display:none">
					&nbsp;
				</td>
			</tr>
			<tr valign="top" width="100%" height="80%" align="left">
				<td valign="top" height="100%" align="left" >
					<%@ include file="/pages/content/search/querysuite/ChooseSearchCategory.jsp" %>
				</td>
			</tr>
		</table>
	</td>
	<td>
	    <table border="0"  height="100%" width="100%" cellpadding="1" cellspacing="3" valign="top">
			<tr  id="rowMsg" class='validationMessageCss'>
				<td id="validationMessagesSection"  width="80%" class='validationMessageCss'>
					<div id="validationMessagesRow"   style="overflow:auto; width:820; height:50;display:none"></div>
				</td>
			</tr>
			<tr>
				<td valign="top" width="80%" height="100%">
					<div id="queryTableTd" style="overflow:auto;height:100%;width:100%"" valign="top">
					<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
												id="DAG" width="100%" height="100%"
												codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
												<param name="movie" value="flexclient/dag/DAG.swf?view=Result"/>
												<param name="quality" value="high" />
												<param name="bgcolor" value="#869ca7" />
												
												<param name="allowScriptAccess" value="sameDomain"/>
												<embed src="flexclient/dag/DAG.swf?view=Result" quality="high" bgcolor="#869ca7"
													width="100%" height="100%" name="DAG" align="middle"
													play="true"
													loop="false"
													quality="high"
												
													allowScriptAccess="sameDomain"
													type="application/x-shockwave-flash"
													pluginspage="http://www.adobe.com/go/getflashplayer">
												</embed>

						</object>
				
					</div>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>