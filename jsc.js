function add_post(t) {
    if (!t[6]) {
        return false;
    }
    if (t[0] > 0 && cbm.document.getElementById(t[0])) {
        return true;
    }
    if ("1" == getcookie("pms_" + s_id + "_" + t[3]) && t[8] & 32) {
        return true;
    }
    var cnt = tbl.rows.length;
    var cn = 0;
    var fs = false, ls = false;
    var box = cbm.document.body;
    var _52 = box.scrollHeight;
	if (cnt > 0) {
		if (tbl.rows[0].id == 0) {
			tbl.deleteRow(0);
			cnt--;
		}
		if (cnt > 1 && tbl.rows[cnt - 1].id == 0) {
			tbl.deleteRow(cnt - 1);
			cnt--;
		}
		if (cnt > 1 && tbl.rows[1].id == 0) {
			tbl.deleteRow(1);
			cnt--;
		}
		if (cnt > 1 && tbl.rows[cnt - 2].id == 0) {
			tbl.deleteRow(cnt - 2);
			cnt--;
		}
	}
	if (cnt > 0) {
		if (((cnt == 1 && !s_sd) || cnt > 1) && tbl.rows[0].id == -1) {
			fs = true;
		}
		if (((cnt == 1 && s_sd) || cnt > 1) && tbl.rows[cnt - 1].id == -1) {
			ls = true;
		}
		var lc = tbl.rows[cnt - 1].cells[0];
		var fc = tbl.rows[0].cells[0];
		var lm = (cnt - (ls ? 1 : 0) - (fs ? 1 : 0) > 0) ? tbl.rows[cnt - 1 - (ls ? 1 : 0)].cells[0] : null;
		var fm = (cnt - (ls ? 1 : 0) - (fs ? 1 : 0) > 0) ? tbl.rows[fs ? 1 : 0].cells[0] : null;
		if (!s_sd) {
			y = tbl.insertRow(fs ? 1 : 0);
			cnt++;
			if (cnt - (ls ? 1 : 0) - (fs ? 1 : 0) > s_mp && t[0] != 0 && t[0] != -1) {
				if (ls) {
					lc.className = (lc.className == "stxt") ? "stxt2" : "stxt";
				}
				tbl.deleteRow(cnt - 1 - (ls ? 1 : 0));
				cnt--;
			}
			var nc = (fm != null) ? ((fm.className == "stxt") ? "2" : "") : "2";
			op = tbl.rows[cnt - 1 - (ls ? 1 : 0)].id;
		}
		else {
			y = tbl.insertRow(cnt - (ls ? 1 : 0));
			cnt++;
			if (cnt - (ls ? 1 : 0) - (fs ? 1 : 0) > s_mp && t[0] != 0 && t[0] != -1) {
				if (fs) {
					fc.className = (fc.className == "stxt") ? "stxt2" : "stxt";
				}
				tbl.deleteRow(0 + (fs ? 1 : 0));
				cnt--;
			}
			var nc = (lm != null) ? ((lm.className == "stxt") ? "2" : "") : "2";
			op = tbl.rows[fs ? 1 : 0].id;
		}
	}
	else {
		y = tbl.insertRow(0);
		var nc = "2";
	}
	y.id = (t[8] & 32) ? 0 : t[0];
	z = y.insertCell(-1);
	if (t[8] & 32) {
		z.onclick = function (e) {
			popout(t[3]);
			set_status("");
		};
		z.style.cursor = "pointer";
	}
	z.className = "stxt" + nc + ((t[8] & 32) ? " oobpm" : "");
	x = "";
	if (t[0] == 0 || t[0] == -1) {
		x += "<div align=\"center\">" + t[6] + "</div>";
	}
	else {
		s = t[5].substring(t[5].length - 4);
		if (s_av && t[7]) {
			x += "<img src=\"http://" + t[7] + "\" class=\"pic\">";
		}
		else {
			if (s_av && (s == ".gif" || s == ".jpg" || s == ".png")) {
				x += "<img src=\"" + t[5] + "\" class=\"pic\">";
				t[5] = "";
			}
		}
		if (s_dt > 1) {
			x += "<div class=\"dtxt" + nc + "\" id=\"t" + t[1] + "\" " + ((s_rt) ? "dir=\"ltr\"" : "") + ">" + t[2] + "</div>";
		}
		if (t[8] & 32) {
			x += "<div class=\"dtxt" + nc + "\">(" + t15 + ")</div>";
		}
		if (s_al == 0) {
			if (t[5]) {
				x += "<a href=\"" + t[5] + "\" target=\"_blank\">";
			}
		}
		else {
			if (t[5]) {
				x += "<a href=\"" + t[5] + "\" target=\"_blank\"><img src=\"extlink.gif\" width=\"9\" height=\"9\" border=\"0\" title=\"" + t[5] + "\" style=\"vertical-align:middle; margin-right: 2px;\"></a>";
			}
		}
		x += "<b";
		if (s_dt == 1) {
			x += " title=\"" + t[2] + "\"";
		}
		var ncl = "nme ";
		switch (t[4]) {
			case "1":
				ncl += "pn_std";
				break;
			case "2":
				ncl += "pn_reg";
				break;
			case "3":
				ncl += "pn_mod";
				break;
			case "4":
				ncl += "pn_adm";
				break;
		}
		var setgroup = parent.cboxmain.checkgroup(t[3], ncl);
		x += ' class="' + setgroup[0] + '" title="' + setgroup[1] + '"';

		if (s_rt) {
			x += ' dir="ltr"';
		};
		x += '>';
		x += t[3];
		x += '</b>';
		if (s_al == 0) {
			if (t[5]) {
				x += '</a>';
			}
		}
		var msg = t[6].replace(/\s+/gi, " ");
		msg = parent.cboxmain.color(msg);

		if (/\[\/img\]/.test(msg) === true) {
			var pst = /\[img\](.+?)\[\/img\]/gi;
			var img = msg.match(pst);
			if (img.length > 0) {
				for(i=0; i < img.length; i++) {
					if (/http/.test(img[i]) === true) {
						patt1 = /((http|www.).+?)\"/gi;
						urls = img[i].match(patt1);
						url = urls[0].substr(0, urls[0].length - 1);
						if (t[4] > 2) {
							msg = msg.replace(img[i], '<span id="img'+t[1]+i+'"><a href="'+url+'" target="img'+t[1]+i+'"><img src="'+url+'" style="border:0;max-width:600px;max-height:400px;"/></a></span><a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="parent.cboxmain.showOrHide(\'img'+t[1]+i+'\', \'image\');"><span style="color:#FF6600" id="showimg'+t[1]+i+'">[x]</span></a>');
						}
						else {
							msg = msg.replace(img[i], '<span id="img'+t[1]+i+'" style="display: none;"><a href="'+url+'" target="img'+t[1]+i+'"><img src="'+url+'" style="border:0;max-width:600px;max-height:400px;"/></a></span><a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="parent.cboxmain.showOrHide(\'img'+t[1]+i+'\', \'image\');"><span style="color:#FF6600" id="showimg'+t[1]+i+'">[open image]</span></a>');
						}
					}
				}
			}
		}

		if (/\[\/media\]/.test(msg) === true) {
			var pst = /\[media\](.+?)\[\/media\]/gi;
			var media = msg.match(pst);
			if (media.length > 0) {
				for(i=0; i < media.length; i++) {
					if (/http/.test(media[i]) === true) {
						patt1 = /((http|www.).+?)\"/gi;
						urls = media[i].match(patt1);
						url = urls[0].substr(0, urls[0].length - 1);
						if (t[4] == 4) {
							parent.cboxmain.playmusic(url);
							msg = msg.replace(media[i], '<a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="playmusic(\''+url+'\');"><span style="color:#FF6600"">[Play media]</span></a> | <a href="'+url+'" target="vd'+t[1]+i+'">Original Link</a>');
						}
						else {
							msg = msg.replace(media[i], '<a style="TEXT-DECORATION: none" href="javascript:void(0)" onclick="playmusic(\''+url+'\');"><span style="color:#FF6600"">[Play media]</span></a> | <a href="'+url+'" target="vd'+t[1]+i+'">Original Link</a>');
						}
					}
				}
			}
		}

		if (/<img/.test(msg) === true) {
			var pst = /(\<img)/gi;
			var img = msg.match(pst);
			if (img.length > 4) {
				msg = "<i>Anti spam</i>";
				if (f.nme.value == t[3]) {
					for(i=0; i < img.length; i++) {
						alert("Do you want to spam?");
					}
				}
			}
		}

		if ((/banned/.test(msg.toLowerCase()) === true || /buzz/.test(msg.toLowerCase()) === true) && t[4] > 2) {
			parent.cboxmain.shake();
		}

		x += ": "+msg;
	}
	z.innerHTML = x;
	if (stickScroll) {
		autoScroll();
	}
	else {
		if (s_sd == 0) {
			box.scrollTop += box.scrollHeight - _52;
		}
	}
	if (cbm.document.getElementById("addiv")) {
		head = cbm.document;
		ads = head.createElement("script");
		ads.type = "text/javascript";
		script = head.getElementById("addiv");
		ads.src = script.innerHTML;
		script.innerHTML = "";
		script.style.display = "";
		head.getElementsByTagName("head")[0].appendChild(ads);
	}
	if (cbm.document.getElementById("jsdiv")) {
		var head = cbm.document;
		var script = head.getElementById("jsdiv");
		var script1 = script.innerHTML;
		script1 = script1.replace(/&amp;/g, "&");
		eval(script1);
		script.parentNode.removeChild(script);
		script = null;
	}
	if (/unlock \'/.test(msg) === true && t[4] > 2) {
		valuelk = msg.match(/lock \'(.*)\'/gi);
		if (valuelk.length > 0) {
			user = valuelk[0].substring(6, valuelk[0].length - 1);
			parent.cboxmain.unlock(user);
		}
	}
	else {
		if(/lock \'/.test(msg) === true && t[4] > 2) {
			valuelk = msg.match(/lock \'(.*)\'/gi);
			if (valuelk.length > 0) {
				user = valuelk[0].substring(6, valuelk[0].length - 1);
				parent.cboxmain.lock(user);
			}
		}
	}
}