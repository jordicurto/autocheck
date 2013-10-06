function color(pst){
	pst=pst.replace('[/mau]','</span>');
	pst=pst.replace('UNBANNED','<span class="bigred">UNBANNED</span>');
    pst=pst.replace('BANNED','<span class="bigred">BANNED !!!</span>');
	pst=pst.replace('all f5','<span class="bigred">ALL F5 !!!</span>');
	pst=pst.replace('BUZZ','<span class="bigred">BUZZ !!!</span>');
	pst=pst.replace('@','<span class="at">@</span>');
	pst=pst.replace('[do]','<SPAN class="do" title="[do] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[hong]','<SPAN class="hong" title="[hong] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[green]','<SPAN class="green" title="[green] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[xanh]','<SPAN class="xanh" title="[xanh] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[lam]','<SPAN class="lam" title="[lam] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[vang]','<SPAN class="vang" title="[vang] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[cam]','<SPAN class="cam" title="[cam] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[tim]','<SPAN class="tim" title="[tim] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[cham]','<SPAN class="cham" title="[cham] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[nau]','<SPAN class=nau" title="[nau] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[den]','<SPAN class="den" title="[den] n&#7897;i dung content [/mau]">');
	pst=pst.replace('[trang]','<SPAN class="trang" title="[trang] n&#7897;i dung content [/mau]">');
	return pst;
}