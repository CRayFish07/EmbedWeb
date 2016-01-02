<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>EmbedWeb</title>
	<script type="text/javascript">
	
		var state = 1;
		function changeValue(){
			if(state%2 == 0){
				var a = document.getElementById("showColor");
				a.style.backgroundColor="#FF0000";
			}else{
				var a = document.getElementById("showColor");
				a.style.backgroundColor="#00ff00";
			}
		}
		
		//核心对象变量
		var xmlHttp = null;
		//区分浏览器创建XMLHttpRequest核心对象
		function create(){
			if (window.XMLHttpRequest){// code for Firefox, Opera, IE7, etc.
			  	xmlHttp=new XMLHttpRequest();
			}
			else if (window.ActiveXObject){// code for IE6, IE5
			  	xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		//ajax核心执行方法（此处为提交到servlet处理后,返回纯文本）
		function getState(){
		 	create();
		 	var URL = "getStat.do";
		 	xmlHttp.open("GET",URL,true);
		 	xmlHttp.onreadystatechange=callback;
		 	xmlHttp.send(null);
		}
		
		//回调函数
		function callback(){
		 	if(xmlHttp.readyState == 4){
		  		if(xmlHttp.status == 200){
		   			var v = xmlHttp.responseText;
		   			var json = eval('(' + v + ')'); 
		   			state = json.state;
		   			changeValue();
		  		}
		 	}
		}
		
		function setState(){
			if(state <= 0) {
				state = 1;
				var a = document.getElementById("showColor");
				a.style.backgroundColor="#00ff00";
			}else if (state >=1){
				state = 0;
				var a = document.getElementById("showColor");
				a.style.backgroundColor="#FF0000";
			}
		 	create();
		 	var URL = "setState.do?state="+state;
		 	xmlHttp.open("GET",URL,true);
		 	xmlHttp.onreadystatechange=null;
		 	xmlHttp.send(null);
		}
		function setDiv(){
			if(xmlHttp.readyState == 4){
		  		if(xmlHttp.status == 200){
		   			var v = xmlHttp.responseText;
		   			var json = eval('(' + v + ')'); 
		   			varb = json.state;
		   			changeValue();
		  		}
		 	}
		}
		window.onload = getState;
	</script>
</head>

    <body style="text-align:center">
        <h1 id="date"><%=new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %></h1>
        <div id="showColor" style='border: solid 3px blue;width:60px;height:60px; margin:auto ; background-color:#0F0'></div>
        <br />
        <br />
      	<input style="margin-right:40px; width:100px; height:40px" type="button" name="start" id="start" value="获取状态" onclick="getState()" />
    	<input style="margin-left:40px; width:100px; height:40px" type="button" name="change" id="change" value="改变状态" onclick="setState()" />
    </body>
</html>