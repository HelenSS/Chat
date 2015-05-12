'use strict';

var j = 0;

var message = function(name, text) {
    return {		
	user: name,
	messageText: text,
	id: (j++).toString()
    };
};

var messagesList = [];

var appState = {
    mainUrl : '/chat',
    taskList:[],
    token : 'TE11EN'
};

function run() {
    document.getElementById("available").checked = true;
    var appContainer = document.getElementsByClassName('chat')[0];
    appContainer.addEventListener('click', delegateEvent);
    restore();
}

var id;
var ind;
var ed = false;

function delegateEvent(evtObj) {
    if (evtObj.id === "send"){
	if (evtObj.value === "Edit"){
	    replaceMessage (evtObj);
	    return;
	}
    	sendMessage (evtObj);
	return;
    }

    if (evtObj.id === "delete"){
    	deleteMessage (evtObj);
	ed = false;
	return;
    }

    if (evtObj.id === "edit" && ed===true){
	editMessage (evtObj);
	ed = false;
	return;
    }

    if (+evtObj.id || evtObj.id === "0"){
 	id = evtObj.id;
	ind = evtObj.id;
	ed = true;
    }
}

function sendMessage (evtObj) {
    var name = document.getElementById ('my name').value;
	
    if (name === '')
    	return;

    var messText = document.getElementById ('my message').value;

    if (messText === '')
    	return;

    addMessage (name, messText);
    document.getElementById ('my message').value = '';
}

function addMessage (name, messText) {
    var mess = message (name, messText);
    createMessage(mess);
    messagesList.push (mess);
    post(appState.mainUrl, JSON.stringify(mess));
}

function createMessage (mess) {
    var newMess = document.createElement ("div");
    newMess.innerHTML = "<p><b>" + mess.user + "</b><p></p>" + mess.messageText + "</p>";
    newMess.setAttribute ("id", mess.id);
    newMess.setAttribute ("class", "mess");
    newMess.setAttribute ("onClick", "delegateEvent(this)");
    document.getElementById ("rect").appendChild (newMess);
    messagesList[messagesList.length] = mess;

    var block = document.getElementById("rect");
    block.scrollTop = block.scrollHeight;
}

function deleteMessage (evtObj) {
    var i;
    for (i = 0; i<messagesList.length; i++)
	if(messagesList[i].id == ind)
	    if(messagesList[i].user != document.getElementById ('my name').value)
		return;
	    else
		break;
    document.getElementById ("rect").removeChild(document.getElementById(ind));
    del(appState.mainUrl, JSON.stringify(messagesList[i]));
    messagesList.splice (ind, 1);
}

function editMessage (evtObj) {
    var i;
    for (i = 0; i<messagesList.length; i++)
	if(messagesList[i].id == ind)
	    if(messagesList[i].user != document.getElementById ('my name').value)
		return;
	    else
		break;

    ind = i;
    document.getElementById ("my message").value = messagesList[i].messageText;
    document.getElementById ("send").value = "Edit";
}

function replaceMessage (evtObj) {
    var newDiv = document.getElementById (id);
    newDiv.value = document.getElementById ("my message").value;
    newDiv.innerHTML = "<p><b>" + messagesList[ind].user + "</b><p></p>" + newDiv.value + "</p>";
    var oldDiv = document.getElementById("rect").replaceChild(newDiv, document.getElementById (id));
    document.getElementById ("send").value = "send";
    document.getElementById("my message").value = '';
    messagesList[ind].messageText = newDiv.value;
    put(appState.mainUrl, JSON.stringify(messagesList[ind]));
}

function createAllMessages(allMessages) {
    for(var i = 0; i<allMessages.length; i++)
	createMessage(allMessages[i]);
    if (allMessages.length)
        j = allMessages.length;
}

function restore(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;
    get(url, function(responseText) {
    console.assert(responseText != null);
    var response = JSON.parse(responseText);
    appState.token = response.token;
    if (response.messages)
	createAllMessages(response.messages);
    continueWith && continueWith();
    });
}

function get(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);	
}

function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);	
}

function del(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);	
}

function isError(text) {
    if(text == "")
	return false;
	
    try {
	var obj = JSON.parse(text);
    } catch(ex) {
	return true;
    }

    return !!obj.error;
}

function defaultErrorHandler(mess) {
    console.error(mess);
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
	if (xhr.readyState !== 4)
	    return;
	if(xhr.status != 200) {
	    continueWithError('Error on the server side, response ' + xhr.status);
	    return;
	}

	if(isError(xhr.responseText)) {
	    continueWithError('Error on the server side, response ' + xhr.responseText);
	    return;
	}
	if(xhr.responseText) {
	    continueWith(xhr.responseText);
	}
    };

    xhr.onimeout = function () {
        continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
	document.getElementById("available").checked = false;
	document.getElementById("unavailable").checked = true;
	var errMsg = 'Server connection error !\n'+
	'\n' +
	'Check if \n' +
	'- server is active\n'+
	'- server sends header "Access-Control-Allow-Origin:*"';

	continueWithError(errMsg);
    };

    xhr.send(data);
}