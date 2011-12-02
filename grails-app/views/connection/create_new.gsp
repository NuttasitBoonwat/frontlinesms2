<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
<script type="text/javascript">
	url_root = "${request.contextPath}/";
	
	function setChecked(connectionType) {
		$("#type-list input[checked=checked]").attr('checked', '');
		$("#type-list ." + connectionType).attr('checked', 'checked');
		$("#smslib-form").css('display', 'none');
		$("#email-form").css('display', 'none');
		$("#" + connectionType + "-form").css('display', 'inline');
	}
</script>
<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Choose type</a></li>
		<li><a href="#tabs-2">Enter details</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ol>

	<g:form action="save" id='newConnection'>
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>
<script>
function initializePopup() {
	$("#tabs").bind("tabsshow", function(event, ui) {
		updateConfirmationMessage();
	});
}

function updateConfirmationMessage() {
	var type = $("#type-list").find("input[checked=checked]").val();
	var name = $('#' + type + '-form #name').val();
	if(type == 'smslib') {
		$("#email-confirm").hide();
		$("#smslib-confirm").show();
		var port = $('#' + type + '-form #port').val();
		var baud = $('#' + type + '-form #baud').val();
		var pin = $('#' + type + '-form #pin').val();
		$("#confirm-name").text(name);
		$("#confirm-type").text('smslib');
		$("#confirm-port").text(port);
		$("#confirm-baud").text(baud);
		$("#confirm-pin").text(pin);
		
	} else if (type == 'email') {
		$("#smslib-confirm").hide();
		$("#email-confirm").show();
		var receiveProtocol = $('#' + type + '-form #receiveProtocol').val();
		var serverName = $('#' + type + '-form #serverName').val();
		var serverPort = $('#' + type + '-form #serverPort').val();
		var username = $('#' + type + '-form #username').val();
		var password = $('#' + type + '-form #password').val();
		$("#confirm-name").text(name);
		$("#confirm-type").text('email');
		$("#confirm-protocol").text(receiveProtocol);
		$("#confirm-server-name").text(serverName);
		$("#confirm-server-port").text(serverPort);
		$("#confirm-username").text(username);
	}
}
</script>