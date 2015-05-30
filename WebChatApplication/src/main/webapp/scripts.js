'use strict';

var uniqueId = function() {
    return Math.floor(Math.random() * 2147483647).toString();
};

var message = function(name, text, mess_id) {
    return {		
	user: name,
	messageText: text,
	id: mess_id
    };
};

var appState = {
    mainUrl : 'chat',
    taskList:[],
    token : 'TE0EN'
};

function run() {
    document.getElementById("available").checked = true;
    var appContainer = document.getElementsByClassName('chat')[0];
    appContainer.addEventListener('click', delegateEvent);
    setInterval("restore()",1000);
};

var id;
var ind;
var ed = false;

function delegateEvent(evtObj) {
    if (evtObj.id === "send"){
	if (evtObj.value === "edit"){
	    replaceMessage (evtObj);
	    return;
	}
       sendMessage();
	return;
    }

    if (evtObj.id === "delete"){
        var div = document.getElementById(ind);
        if (document.getElementById('my name').value === div.getAttribute("user")) {
            del(appState.mainUrl, JSON.stringify(message(div.getAttribute("user"), div.getAttribute("messageText"), ind)));
        }
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

function sendMessage () {
    var name = document.getElementById ('my name').value;
    var messText = document.getElementById ('my message').value;
    if (name === '' || messText === '')
        return;
    post(appState.mainUrl, JSON.stringify(message (name, messText, uniqueId())));
    document.getElementById ('my message').value = '';
}

function createMessage (mess) {
    var newMess = document.createElement ("div");
    newMess.innerHTML = "<p><b>" + mess.user + "</b><p></p>" + mess.messageText + "</p>";
    newMess.setAttribute("user", mess.user);
    newMess.setAttribute("messageText", mess.messageText);
    newMess.setAttribute ("id", mess.id);
    newMess.setAttribute ("class", "mess");
    newMess.setAttribute ("onClick", "delegateEvent(this)");
    document.getElementById ("rect").appendChild (newMess);

    var block = document.getElementById("rect");
    block.scrollTop = block.scrollHeight;
}

function deleteMessage (evtObj) {
    var div = document.getElementById(ind);
    if (document.getElementById('my name').value !== div.getAttribute("user"))
        return;
    var mess = message (div.getAttribute("user"), div.getAttribute("messageText"), div.getAttribute("id"));
    document.getElementById ("rect").removeChild(document.getElementById(ind));
    del(appState.mainUrl, JSON.stringify(mess));
}

function editMessage (evtObj) {
    var div = document.getElementById(ind);
    if (document.getElementById('my name').value !== div.getAttribute("user"))
        return;
    document.getElementById ("my message").value = div.getAttribute("messageText");
    document.getElementById ("send").value = "edit";
}

function replaceMessage (evtObj) {
    var newDiv = document.getElementById (id);
    newDiv.setAttribute("messageText", document.getElementById ("my message").value);
    newDiv.innerHTML = "<p><b>" + newDiv.getAttribute("user") + "</b><p></p>" + newDiv.getAttribute("messageText") + "</p>";
    var mess = message (newDiv.getAttribute("user"), newDiv.getAttribute("messageText"), newDiv.getAttribute("id"));
    document.getElementById ("send").value = "send";
    document.getElementById("my message").value = '';
    put(appState.mainUrl, JSON.stringify(mess));
}

function createAllMessages(allMessages) {
    var div;
    for(var i = 0; i<allMessages.length; i++) {
        div = document.getElementById(allMessages[i].id);
        if (!div)
            createMessage(allMessages[i]);
        else if (div.getAttribute("messageText") === allMessages[i].messageText)
            continue;
        else if (allMessages[i].messageText === '')
            document.getElementById ("rect").removeChild(document.getElementById(allMessages[i].id));
        else {
            div.setAttribute("messageText", allMessages[i].messageText);
            div.innerHTML = "<p><b>" + allMessages[i].user + "</b><p></p>" + allMessages[i].messageText + "</p>";
        }
    }
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
	if (xhr.readyState !== 4) {
        uncheked();
        return;
    }
	if(xhr.status != 200) {
	    continueWithError('Error on the server side, response ' + xhr.status);
        uncheked();
	    return;
	}

	if(isError(xhr.responseText)) {
	    continueWithError('Error on the server side, response ' + xhr.responseText);
        uncheked();
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
	uncheked();
	var errMsg = 'Server connection error !\n'+
	'\n' +
	'Check if \n' +
	'- server is active\n'+
	'- server sends header "Access-Control-Allow-Origin:*"';

	continueWithError(errMsg);
    };

    xhr.send(data);
}

function uncheked() {
    document.getElementById("available").checked = false;
    document.getElementById("unavailable").checked = true;
}