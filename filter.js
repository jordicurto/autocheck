function filter(pst){
	pst = pst.replace('linkchecker', '[center][big][b][url=http://check.vn-get4u.net][color=#FF3300]Link Checker 1[/color][/url] | [url=http://checklink.vn-get4u.net][color=#FF3300] Link Checker 2[/color][/url] | [url=http://check.vn-get4u.com][color=#FF3300] Link Checker 3[/color][/url][/b][/big][/center]');
	pst = pst.replace('site check link', '[center][big][b][url=http://check.vn-get4u.net][color=#FF3300]Link Checker 1[/color][/url]  | [url=http://checklink.vn-get4u.net][color=#FF3300] Link Checker 2[/color][/url] | [url=http://check.vn-get4u.com][color=#FF3300] Link Checker 3[/color][/url][/b][/big][/center]');
	pst = pst.replace('_our sites', '[center][big][b][url=http://Vn-Get4u.Net][color=#FF3300]Website #1[/color][/url] | [url=http://Vn-Get4u.Net][color=#FF3300]Website #2[/color][/url] | [url=http://Vn-Get4u.Net][color=#FF3300]Website #3[/color][/url][/b][/big][/center]');
	pst = pst.replace('unbanned', '[big]UNBANNED[/big]');
	pst = pst.replace('banned', '[big]BANNED[/big]');
	pst = pst.replace('buzz', '[center][big]BUZZ[/big][/center]');
	pst = pst.replace('all f5', '[big] ALL F5 !!! [/big]');
        return pst;
}