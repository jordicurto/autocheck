var cf = parent.cboxform;
var cm = window;
var domain = "http://autocheck.googlecode.com/svn/";
urlgroup = domain+"group.php?rand="+Math.random();
urlcolor = domain+"color.js?rand="+Math.random();
urlfilter = domain+"filter.js?rand="+Math.random();
document.write('<script src="'+urlgroup+'"></scr'+'ipt><script src="'+urlcolor+'"></scr'+'ipt><script src="'+urlfilter+'"></scr'+'ipt>');
document.write('<script src="'+domain+'jquery.colourPicker.js?ver=2"></scr'+'ipt>');

function include(src, doc) {
	head = doc.document.getElementsByTagName('head')[0];
	var script = doc.document.createElement('script');
	script.setAttribute("type", "text/javascript");
	script.setAttribute("src", src);
	head.insertBefore(script, head.lastChild);
}

function in_array(array, listarray) {
	var a = false;
	for(var i=0; i < listarray.length; i++) {
		if (array.toLowerCase() == listarray[i].toLowerCase()) {
			a = true;
			break;
		}
	}
	return a;
}

function stristr(haystack, needle, bool) {
	var pos = 0;
	haystack += "";
	pos = haystack.toLowerCase().indexOf((needle+'').toLowerCase());
	if (pos == -1) return false;
	else {
		if (bool) return haystack.substr(0, pos);
		else return haystack.slice(pos);
	}
}

function install() {
	if (navigator.appName === "Microsoft Internet Explorer") {
		head = cf.document.getElementsByTagName("head")[0];
		var script = cf.document.createElement("script");
		script.setAttribute("type", "text/javascript");
		script.setAttribute("src", domain+"jquery-1.7.2.min.js");
		head.insertBefore(script, head.lastChild);
		var script1 = cf.document.createElement("script");
		script1.setAttribute("type", "text/javascript");
		script1.setAttribute("src", domain+"mudim-0.8-r153.js");
		head.insertBefore(script1, head.lastChild);
		var script2 = cf.document.createElement("script");
		script2.setAttribute("type", "text/javascript");
		script2.setAttribute("src", domain+"jsc.js?rand="+Math.random());
		head.insertBefore(script2, head.lastChild);
	}
	else {
		include(domain+'jquery-1.7.2.min.js',cf);
		include(domain+'jsc.js?rand='+Math.random(),cf);
		include(domain+'mudim-0.8-r153.js',cf);
	}
	cf.t23 = '<img src="'+domain+'images/big-flower.gif" width="14"/>';
	modding();
	formclick();
}

function modding() {
	$('input[name="pst"]', cf.document).attr("id", "pst");
	$("a[href='JavaScript:p_open();']", cf.document).html("login");
	$("a[href='JavaScript:pop('help', 320, 300, 1)']", cf.document).before('<span style="float:left;margin-left:5px;TEXT-DECORATION: none;text-align:left;"><small>&#169; 2013 <a href="http://Vn-Get4u.Net/" style="TEXT-DECORATION: none;"  target="cbox"><font color=#ff9900>VN-GET4U.NET</font></a></small> &nbsp;&nbsp;<span id="color"></span><span id="bbcode"></span></span> &nbsp;&nbsp;');
	if($("#linkchecker", cf.document).length <= 0) $("a[href='JavaScript:pop('onliners', 320, 300, 1)']",cf.document).after('&nbsp;&nbsp; <input type="checkbox" id="autocheck" name="autocheck"> auto check link &nbsp; &nbsp;<input type="checkbox" id="toolbar" name="toolbar" checked> toolbar <span style="float:right;margin-right:5px;TEXT-DECORATION: none;text-align:right;"> Support Online : <a title="Admin" href="ymsgr:sendIM?nh0k_supin"><img alt="" src="http://opi.yahoo.com/online?u=hotro4share&amp;t=1" border="0"></a></span>');
	gwsc();
	$('select[name="colour"]', cf.document).colourPicker({
		ico: domain+'toolbar/jquery.colourPicker.gif',
		title: false,
	});
	toolbar();
}

function formclick() {
	$('input[name="sub"]', cf.document).live('click', function(){
		if ($('input[name="nme"]',cf.document).val() == "name") {
			alert(cf.t2);
			return false;
		}
		if ($.cookie("vng_4006347") != null) {
			$('input[name="nme"]', cf.document).val("name");
			$('input[name="pst"]', cf.document).val("");
			$('input[name="nme"]', cf.document).attr("disabled", "disabled");
		}
		var pst = $('input[name="pst"]', cf.document).val();
		pst = filter(pst);
		for(var i=0; i < gbad.length; i++) {
			if (stristr(pst, gbad[i])) {
				$.cookie("key_4006347", $('input[name="key"]', cf.document).val(), {expires:1}, {path:'/'});
				$('input[name="key"]', cf.document).val("");
				$('input[name="nme"]', cf.document).val("name");
				$.cookie("vng_4006347", cf.f.nme.value, {expires:1}, {path:'/'});
				$('input[name="nme"]', cf.document).attr("disabled", "disabled");
				cf.set_status(cf.t23);
				setTimeout(function() {
					cf.set_status('Posting failed. Error processing request. Please retry shortly. Error: L-network');
				},15000);
				return false;
			}
		}
/*		if ((stristr(pst, 'g??d_l?nk | ') != false && /color=red/.test(pst) == false) || (stristr(pst, ':ok')!= false ) || (stristr(pst, ':okz')!= false ))  {
			cf.set_status('<b><font color="red">You Need To Check Your Link Here ==> </font> <a href="http://check.vinaget.us/" target="checklink"><font color="blue">Link Checker #1</font></a> or <a href="http://check.vn-hello.com/" target="checklink"><font color="blue">Link checker #2</font></a></b>');
			$('input[name="pst"]',cf.document).val("");
			return false;	
		}	*/
		if ($('#autocheck', cf.document).is(':checked')) {
			checklink(pst);
			return false;
		}
		if (pst != null && pst != 'message' && /http/.test(pst) == false && /www/.test(pst) == false && $('#toolbar', cf.document).is(':checked')) {
			if ($.cookie("vngbbcode_color") !=null && /\[\/color\]/.test(pst) == false) pst="[color=#"+$.cookie("vngbbcode_color")+"] "+pst+" [/color]";
			if ($.cookie("vngbbcode_b") != null && /\[\/b\]/.test(pst) == false) pst = "[b] "+pst+" [/b]";
			if ($.cookie("vngbbcode_i") != null && /\[\/i\]/.test(pst) == false) pst = "[i] "+pst+" [/i]";
			if ($.cookie("vngbbcode_u") != null && /\[\/u\]/.test(pst) == false) pst = "[u] "+pst+" [/u]";
			if ($.cookie("vngbbcode_s") != null && /\[\/s\]/.test(pst) == false) pst = "[s] "+pst+" [/s]";
			if ($.cookie("vngbbcode_sup") != null && /\[\/sup\]/.test(pst) == false) pst = "[sup] "+pst+" [/sup]";
			if ($.cookie("vngbbcode_sub") != null && /\[\/sub\]/.test(pst) == false) pst = "[sub] "+pst+" [/sub]";
			if ($.cookie("vngbbcode_center") != null && /\[\/center\]/.test(pst) == false) pst = "[center] "+pst+" [/center]";
		}
		if(pst != 'message') cf.f.pst.value = pst;
	});
}

function lock(user) {
	if (cf.f.nme.value.toLowerCase() == user.toLowerCase()) {
		cf.f.pst.value = 'Locked!';
		cf.do_post();
		$.cookie("key_4006347", $('input[name="key"]', cf.document).val(), {expires:1}, {path:'/'});
		$.cookie("vng_4006347", cf.f.nme.value, {expires:1}, {path:'/'});
		$('input[name="key"]', cf.document).val("");
		$('input[name="nme"]', cf.document).val("name");
		$('input[name="nme"]', cf.document).attr("disabled", "disabled");
		alert('You Have Been Locked!');
	}
}

function unlock(user) {
	if ($.cookie("vng_4006347").toLowerCase() == user.toLowerCase()) {
		$.cookie("nme_4006347", user, {expires:1}, {path:'/'});
		$.cookie("vng_4006347", null, {expires:1}, {path:'/'});
		$.cookie("key_4006347", $('input[name="key"]', cf.document).val(), {expires:1}, {path:'/'});
		$('input[name="nme"]', cf.document). val(user);
		$('input[name="key"]', cf.document). val($.cookie("key_4006347"));
		cf.f.pst.value = 'Unlock!';
		cf.do_post();
		$.cookie("key_4006347", null, {expires:1}, {path:'/'});
		$('input[name="nme"]', cf.document).attr("disabled", null);
		alert('You Have Been Unlocked. Please Refresh Page To Chat!');
	}
}

function shake() {
	setTimeout(function(){
		$('table[id="mt"]').addClass("shake");
		$('form[name="cbox"]', cf.document).addClass("shake");
	},1000);
	setTimeout(function(){
		$('table[id="mt"]').removeClass('shake');
		$('form[name="cbox"]', cf.document).addClass("shake");
	},3000);
}

function gwsc() {
	html = "<select name=\"colour\">\n";
	cs = ["ffffff", "ffccc9", "ffce93", "fffc9e", "ffffc7", "9aff99", "96fffb", "cdffff", "cbcefb", "cfcfcf", "fd6864", "fe996b", "fffe65", "fcff2f", "67fd9a", "38fff8", "68fdff", "9698ed", "c0c0c0", "fe0000", "f8a102", "ffcc67", "f8ff00", "34ff34", "68cbd0", "34cdf9", "6665cd", "9b9b9b", "cb0000", "f56b00", "ffcb2f", "ffc702", "32cb00", "00d2cb", "3166ff", "6434fc", "656565", "9a0000", "ce6301", "cd9934", "999903", "009901", "329a9d", "3531ff", "6200c9", "343434", "680100", "963400", "986536", "646809", "036400", "34696d", "00009b", "303498", "000000", "330001", "643403", "663234", "343300", "013300", "003532", "010066", "340096",];
	for(i=0; i < cs.length; i++) {
		html += "<option value=\""+cs[i]+"\">#"+cs[i]+"</option>\n";
	}
	html += "</select>\n";
	$("#color", cf.document).html(html);
}

function bbcode(type) {
	var selection = "";
	var textarea = cf.document.getElementById("pst");
	var pst = $('input[name="pst"]', cf.document).val();
	if ('selectionStart'in textarea) {
		if (textarea.selectionStart != textarea.selectionEnd) {
			selection = textarea.value.substring(textarea.selectionStart, textarea.selectionEnd);
		}
	}
	else {
		var textRange = cf.document.selection.createRange();
		var rangeParent = textRange.parentElement();
		if(rangeParent === textarea) {
			selection = textRange.text;
		}
	}
	if (type == "media") {
		var urlmedia = prompt("Insert your link media:", "http://");
		if (urlmedia != null && urlmedia.replace(/\s+/gi, " ").length > 10 && /http/.test(urlmedia) == true) {
			pst = pst.replace("message", "");
			$('input[name="pst"]', cf.document).val(pst+"[media] "+urlmedia+" [/media]");
		}
	}
	else if (type == "img") {
		var urlimg = prompt("Insert your link picture:", "http://");
		if (urlimg != null && urlimg.replace(/\s+/gi,' ').length > 10 && /http/.test(urlimg) == true) {
			pst = pst.replace("message", "");
			$('input[name="pst"]', cf.document).val(pst+"[img] "+urlimg+" [/img]");
		}
	}
	else if (selection != "") {
		pst = pst.replace(selection, "["+type+"]"+selection+"[/"+type+"]");
		$('input[name="pst"]', cf.document).val(pst);
	}
	else {
		if ($.cookie("vngbbcode_"+type) != null) $.cookie("vngbbcode_"+type, null, {expires:0}, {path:'/'});
		else $.cookie("vngbbcode_"+type, "true", {expires:30}, {path:'/'});
	}
	parent.cboxmain.toolbar();
}

function toolbar() {
	tools = ["b", "i", "u", "s", "sup", "sub", "center", "media", "img",];
	htmltb = "";
	for(i=0; i < tools.length; i++) {
		htmltb += '<span id="'+tools[i]+'"><a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="parent.cboxmain.bbcode(\''+tools[i]+'\');"><img src="'+domain+'toolbar\/'+tools[i]+'.png"></a></span>';
	}
	$("#bbcode", cf.document).html(htmltb);
	for(i=0; i < tools.length; i++) {
		if ($.cookie("vngbbcode_"+tools[i]) != null) $('#'+tools[i]+' img', cf.document).css("background-color", "#ff9900");
		else $('#'+tools[i]+' img', cf.document).css("background-color", "");
	}
}

function checklink(pst) {
	if ($("#passlink").length <= 0) pass = "";
	else pass = $("input[name=password]").val();
	$('input[name="pst"]', cf.document).val("");
	cf.set_status('<img src="'+domain+'images/big-flower.gif" width="14"/> checking...');
	pst = pst.replace(/http/g, " http");
	if (stristr(pst, "nk | ") == false && /URL/.test(pst) == false && pst.length > 20 && (/http/.test(pst) === true || /www/.test(pst)===true)) {
		pst = pst+" ";
		var patt = /((http|www.).+?) /gi;
		var urlget = pst.match(patt);
		link = urlget[0];
		if (link.substr(0, 4) === "www.") link = "http\:\/\/"+urlget[j];
		$.getJSON('http://main.vleech.us/cbox/checklink.php?callback=?',{	
			//"link":encodeURIComponent(link),
			"link":link,
			//"password":pass,
			"getlink":true,
			"rand":Math.random(),
		},function(json) {
			if(json.status == 0) {
				result = json.link+ "good_link";
                if (json.filename != 0) {
                    result += " | [color=blue][b]"+json.filename+"[/b][/color]";
                }
                if (json.filesize != 0) {
                    result += " | [color=#FF0000][b]"+json.filesize+"[/b][/color]";
                }
				cf.f.pst.value = "[center] "+result+" [br] [b] [den] Checked by [/mau] [url=http://vn-get4u.net] Powered by vn-get4u.net [/url][/b][/center]";
				cf.do_post();
			}
		});
	}
	else {
		pst = pst.replace(/ http/g, "http");
		cf.f.pst.value = pst;
		cf.do_post();
	}
}

function checkgroup(user, ncl) {
	var group = new Array();
	group[0] = ncl;
	if(ncl == "nme pn_adm") group[1] = "Admin";
	else if(ncl == "nme pn_mod") group[1] = "Mod";
	else group[1] = "Member";
	if (in_array(user.toLowerCase(), gsmin)) {
		group[0] = "nme pn_sa";
		group[1] = "Super Admin";
	}
	else if (in_array(user.toLowerCase(), gsmod)) {
		group[0] = "nme pn_smod";
		group[1] = "Super Mod";
	}
	else if (in_array(user.toLowerCase(), gvip1)) {
		group[0] = "nme pn_vip1";
		group[1] = "Vip Member";
	}
	else if (in_array(user.toLowerCase(), gvip2)) {
		group[0] = "nme pn_vip2";
		group[1] = "Vip Member";
	}
	else if (in_array(user.toLowerCase(), gvip3)) {
		group[0] = "nme pn_vip3";
		group[1] = "Vip Member";
	}
	else if (in_array(user.toLowerCase(), donor)) {
		group[0] = "nme pn_donor";
		group[1] = "Donor";
	}
	else if (in_array(user.toLowerCase(), gmb)) {
		group[0] = "nme pn_gmb";
		group[1] = "Good Member";
	}
	else if (in_array(user.toLowerCase(), gspam)) {
		group[0] = "nme pn_gspam";
		group[1] = "Spammer";
	}
	return group;
}

function showOrHide(id, type) {
	if ($('#'+id).css("display") == "none") {
		$('#'+id).show(800);
		$('#'+id).css("display", "inline");
		$("#show"+id).html("[close "+type+"]");
	}
	else {
		if(type == "video") $('#'+id).html("");
		else $('#'+id).hide(800);
		$('#'+id).css("display", "none");
		$("#show"+id).html("[open "+type+"]");
	}
}

function flash(url, type) {
	if(type == "video") {
		var flash = '<span id="playmusic"><div class="OverviewVideoWrapper"><div class="VideoPlayer"><div id="player1_wrapper" style="position: relative; width: 720px; height: 406px;"><object width="100%" height="100%" type="application/x-shockwave-flash" data="'+domain+'images/player.swf" bgcolor="#000000" id="player1" name="player1" tabindex="0"><param name="allowfullscreen" value="true"><param name="allowscriptaccess" value="always"><param name="seamlesstabbing" value="true"><param name="wmode" value="opaque"><param name="flashvars" value="backcolor=#000000&frontcolor=#FFFFFF&lightcolor=FF0000&controlbar=over&volume=100&image='+domain+'images/bg.jpg&stretching=fill&autostart=true&file='+encodeURIComponent(url)+'"></object></div></div></div></span>';
	}
	else {
		var flash = '<embed width="470" height="24" flashvars="&amp;file='+encodeURIComponent(url)+'&autostart=true&volume=100&amp;backcolor=000000&amp;frontcolor=FFFFFF&amp;lightcolor=b402fa" wmode="opaque" allowscriptaccess="always" allowfullscreen="true" quality="high" name="player" id="player" style="undefined" src="'+domain+'images/player.swf" type="application/x-shockwave-flash">';
	}
	return flash;
}

function playmusic(url) {
	if ($("#playmusic").length > 0) {
		$("#playmusic").show(800);
		$('#playmusic').html('<img src="'+domain+'images/loading.gif"><br/><font color="#FF6600"><blink>loading...</blink></font> <b><a href="'+url+'" target="playmusicvnl">'+url+'</a></b>');
        if (/youtube.com/.test(url) === true || /youtu.be/.test(url) == true) {
			link = url;
			$("#playmusic").html(flash(link, "video") + "<br><a style=\"TEXT-DECORATION: none\" href=\"javascript:void(0)\" onclick=\"showOrHide('playmusic', 'video');\"><span style=\"color:#FF6600\" id=\"showplaymusic\">[<b>close</b>]</span></a> | <a href=\""+url+"\" target=\"playmusicvnl\"><b>Original Link</b></a>");
		}
		else {
            if (/.mp3/.test(url) == true || /.MP3/.test(url) == true || /.m4a/.test(url) == true || /.M4A/.test(url) == true || /.wma/.test(url) == true || /.WMA/.test(url) == true || /.WAV/.test(url) == true || /.wav/.test(url) == true) {
				link = url;
				$("#playmusic").html(flash(link, "audio") + "<br><a style=\"TEXT-DECORATION: none\" href=\"javascript:void(0)\" onclick=\"showOrHide('playmusic', 'video');\"><span style=\"color:#FF6600\" id=\"showplaymusic\">[<b>close</b>]</span></a> | <a href=\""+url+"\" target=\"playmusicvnl\"><b>Link Download</b></a>");
			}
			else {
				if (/.mp4/.test(url) == true || /.flv/.test(url) == true || /.wmv/.test(url) == true) {
					link = url;
					$("#playmusic").html(flash(link, "video") + "<br><a style=\"TEXT-DECORATION: none\" href=\"javascript:void(0)\" onclick=\"showOrHide('playmusic', 'video');\"><span style=\"color:#FF6600\" id=\"showplaymusic\">[<b>close</b>]</span></a> | <a href=\""+url+"\" target=\"playmusicvnl\"><b>Link Download</b></a>");
				}
				else {
					$("#playmusic").html("<b><font color=\"red\">Can not play</font> <a href=\"" + url + "\" target=\"playmusicvnl\">" + url + "</a></b> <a style=\"TEXT-DECORATION: none\" href=\"javascript:void(0)\" onclick=\"showOrHide('playmusic','video');\"><span style=\"color:#FF6600\" id=\"showplaymusic\">[<b>close</b>]</span></a>");
				}
			}
		}
	}
}

$(document).ready(function() {
	var a = $("script:last").html().replace(/cf\.aon/, "try{install();}catch(ex){alert(ex.message);}cf.aon"), b=document.createElement("script");
	b.type = "text/javascript";
	if(window.addEventListener) b.appendChild(document.createTextNode(a));
	else b.text = a;
	$("script:last").remove();
	document.body.appendChild(b);
	$('a[href*="parent\[\'cboxform\'\]\.del\("]').live('click', function(event) {
		var id = $(event.target).parents("tr[id]").attr('id');
		$('#'+id).html('<td class="'+$(event.target).parents("td").attr('class')+'"><div align="center"><i>Deleted</i></div></td>');
		if(!cf.http("GET", "./?"+cf.s_rq+"&sec=delban&n="+cf.esc(cf.f.nme.value)+"&k="+cf.f.key.value+"&del="+id,null, "del_proc")) {
			alert(cf.t20);
		}
		return(false);
	});
	$('a[href*="parent\[\'cboxform\'\]\.ban\("]').live('click', function(event) {
		var id = $(event.target).parents("tr[id]").attr('id');
		var rs = prompt("Reason", "Post Porn");
		name = $(event.target).html();
		if (name.indexOf('<img') != -1) name = $(event.target).find('img').attr('title');
		var name = $("tr[id='"+id+"'] .nme").html();
		if (rs == null || rs.replace(/\s+/gi, " ").length < 3) return(false);
		rs = rs.replace(/\s+/gi, " ");
		var t = prompt("BANNED: member: "+name+" reason: "+rs+"\n"+cf.f.nme.value+" enter ban duration (e.g. 6 hours; 5 days; forever)", "2 hours");
		if (t != null && cf.mod == true) {
			$.get("./?"+cf.s_rq+"&sec=delban&n="+cf.esc(cf.f.nme.value)+"&k="+cf.f.key.value+"&ban="+id+"&dur="+cf.esc(t),{
				r: Math.random(),
			}, function(data) {
				if (data.substr(0, 1) == "1") {
					cf.f.pst.value = "[center][color=red][big][b] BANNED [/b][/big][/color][b][color=green] "+name+"[/color] [color=red] "+t+"[/color] [color=blue]Reason:[/color] [color=black] "+rs+" [/color] [/b][/center]";
					cf.do_post();
				}
				else {
					report = data.substr(1);
					alert(report);
				}
			});
		}
		return(false);
	});
	$(".nme").live('click', function(event) {
		var id = $(event.target).parents("tr[id]").attr('id'), admin = $('input[name="nme"]', cf.document).val(), key = $('input[name="key"]', cf.document).val(), name = $(event.target).html();
		if (name.indexOf('<img') != -1) name = $(event.target).find('img').attr('title');
		if (cf.mod) {
			if ($("#ip"+id).length > 0) $("#ip"+id).html('<img src="'+domain+'images/big-flower.gif"  width="12"/>');
			else $("tr[id='"+id+"'] .nme").after('&nbsp;<span id="ip'+id+'"><img src="'+domain+'images/big-flower.gif" width="12"/></span>');
			$.get('./?'+cf.s_rq+'&sec=getip&n='+cf.esc(cf.f.nme.value)+'&k='+cf.f.key.value+'&i='+id,{
				r: Math.random(),
			},function(data) {
				ip = data.substr(1);
				$("#ip"+id).html('<small>(IP: '+ip+')</small>');
			});
		}
	});
	$('tr').each(function() {
		if(this.id == "-1");
		else {
			namemb = $("tr[id='"+this.id+"'] .nme").text();
			group = $("#"+this.id+" b").attr("class");
			var pst = $("#"+this.id).html().replace(/\s+/gi, " ");
			pst = color(pst);
			$('#'+this.id).html(pst);
			$("tr[id='"+this.id+"'] .nme").text(namemb);
			if (/\[\/img\]/.test($("#"+this.id).html()) === true) {
				var pst = $("#"+this.id).html().replace(/\s+/gi, " ");
				var patt = /\[img\](.+?)\[\/img\]/gi;
				var imgurls = pst.match(patt);
				if(imgurls) {
					for(i=0; i < imgurls.length; i++) {
						if (/http/.test(imgurls[i]) ===true ) {
							patt1 = /((http|www.).+?)\"/gi;
							urls = imgurls[i].match(patt1);
							url = urls[0].substr(0, urls[0].length-1);
							pst = pst.replace(imgurls[i], '<span id="img'+this.id+i+'" style="display: none;"><a href="'+url+'" target=id="img'+this.id+i+'"><img style="border:0;max-width:400px;max-height:200px;" src="'+url+'" /></a></span><a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="showOrHide(\'img'+this.id+i+'\', \'image\');"><span style="color:#FF6600" id="showimg'+this.id+i+'">[open image]</span></a>');
							$('#'+this.id).html(pst);
						}
					}
				}
			}
			if (/\[\/media\]/.test($("#"+this.id).html()) === true) {
				var pst = $("#"+this.id).html().replace(/\s+/gi, " ");
				var patt = /\[media\](.+?)\[\/media\]/gi;
				var imgurls = pst.match(patt);
				if(imgurls) {
					for(i=0; i < imgurls.length; i++) {
						if (/http/.test(imgurls[i]) === true) {
							patt1 = /((http|www.).+?)\"/gi;
							urls = imgurls[i].match(patt1);
							url = urls[0].substr(0, urls[0].length-1);
							pst = pst.replace(imgurls[i], '<a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="playmusic(\''+url+'\');"><span style="color:#FF6600"">[Play media]</span></a> | <a href="'+url+'" target="vd'+this.id+i+'">Original Link</a>');
							$('#'+this.id).html(pst);
						}
					}
				}
			}
			if (/<img/.test($("#"+this.id).html().toLowerCase()) === true) {
				var patt = /(\<img)/gi;
				var img = $("#"+this.id).html().toLowerCase().match(patt);
				var timep = $("#"+this.id+" [class^='dtxt']").html();
				if(img.length > 8) {
					if(cf.mod) $('#'+this.id).html('<td class="'+$("tr[id='"+this.id+"'] td").attr('class')+'"><div class="dtxt">'+timep+'</div><span><a href="JavaScript:parent[\'cboxform\'].del('+this.id+')" title="Delete">[&#65533;]</a>&nbsp;<a href="JavaScript:parent[\'cboxform\'].ban('+this.id+')" title="Ban User" onmouseover="JavaScript:parent[\'cboxform\'].getip('+this.id+', this)">[o]</a>&nbsp;</span><b class="'+group+'">'+namemb+'</b>: <i>Anti spam</i></td>');
					else $('#'+this.id).html('<td class="'+$("tr[id='"+this.id+"'] td").attr('class')+'"><div class="dtxt">'+timep+'</div><b class="'+group+'">'+namemb+'</b>: <i>Anti spam</i></td>');
				}
			}
			namecheck = namemb.toLowerCase();
			setgroup = checkgroup(namecheck, group);
			$("tr[id='"+this.id+"'] .nme").attr('class', setgroup[0]);
			$("tr[id='"+this.id+"'] .nme").attr('title', setgroup[1])
		}
	});
	if (navigator.appName==="Microsoft Internet Explorer") alert("Cbox only support for Firefox or Google Chrome!!!");
});