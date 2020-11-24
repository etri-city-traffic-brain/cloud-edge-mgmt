function chartMax(value) {
	value = value.toString();

	var aaa = parseInt(value.substring(0,1))+1;
	var bbb = "1";

	for(var i=0; i<value.length-1; i++) {
		bbb += "0";
	}

	value = aaa * bbb;

	if(value < 10)  {
		value = 10;
	}

	return value;
}

//배열 평균 구하기 함수
function average(array) {
	var sum = 0.0;

	for (var i = 0; i < array.length; i++)
		sum += array[i];

	return sum / array.length;
}

function fn_GetDataSize(P_VALUE, P_COLOR) {
	//P_VALUE = fn_unNumberFormat(P_VALUE);

	var returnValue = "";
	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE < 1024) {
		returnValue = fn_numberFormat(fn_roundXL(P_VALUE,2)) + '';//' Byte';
	}
	else if(P_VALUE < 1048576)	{
		returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1024), 2)) + ' K';
	}
	else if(P_VALUE < 1073741824) {
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1048576), 2)) + ' M';
		}
		else 	{
			returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1048576), 2)) + ' M';
			//returnValue = ' <font color="#08AF52">' + fn_numberFormat(fn_roundXL((P_VALUE / 1048576), 2)) + ' M</font>';
		}
	}
	else if(P_VALUE < 1099511627776) {
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1073741824), 2)) + ' G';
		}
		else	{
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1073741824), 2)) + ' G';
			//returnValue = ' <font color="#6495ED">' + fn_numberFormat(fn_roundXL( (P_VALUE / 1073741824), 2)) + ' G</font>';
		}
	}
	else  if(P_VALUE < 1125899906842624) 	{
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1099511627776), 2)) + ' T';
		}
		else {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1099511627776), 2)) + ' T';
			//returnValue = ' <font color="#FF6A6A">' + fn_numberFormat(fn_roundXL( (P_VALUE / 1099511627776), 2)) + ' T</font>';
		}
	}
	else 	{
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1125899906842624), 2)) + ' P';
		}
		else	{
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1125899906842624), 2)) + ' P';
			//returnValue = ' <font color="#FF6A6A">' + fn_numberFormat(fn_roundXL( (P_VALUE / 1125899906842624), 2)) + ' P</font>';
		}
	}


	return returnValue;
}

function fn_GetDataIntSize(P_VALUE, P_COLOR) {
	//P_VALUE = fn_unNumberFormat(P_VALUE);

	var returnValue = "";
	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE < 1024) {
		returnValue = fn_numberFormat(fn_roundXL(P_VALUE,0)) + '';//' Byte';
	}
	else if(P_VALUE < 1048576)	{
		returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1024), 0)) + ' K';
	}
	else if(P_VALUE < 1073741824) {
		returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1048576), 0)) + ' M';
	}
	else if(P_VALUE < 1099511627776) {
		returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1073741824), 0)) + ' G';
	}
	else  if(P_VALUE < 1125899906842624) 	{
		returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1099511627776), 0)) + ' T';
	}
	else 	{
		returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1125899906842624), 0)) + ' P';
	}


	return returnValue;
}

function formatBase1024KMGTP(y, b, fixed) {
	if(!fixed) fixed = 0;
	var abs_y = Math.abs(y);
	if (abs_y >= 1125899906842624)  { return (y / 1125899906842624).toFixed(fixed)+ "P" }
	else if (abs_y >= 1099511627776){ return (y / 1099511627776).toFixed(fixed) + "T" }
	else if (abs_y >= 1073741824)   { return (y / 1073741824).toFixed(fixed) + "G" }
	else if (abs_y >= 1048576)      { return (y / 1048576).toFixed(fixed) + "M" }
	else if (abs_y >= 1024)         { return (y / 1024).toFixed(fixed) + "K" }
	else if (abs_y < 1 && y > 0)    { return y.toFixed(fixed) }
	else if (abs_y === 0)           { return '0' }
	else                        { return y.toFixed(fixed) }
}

function formatKMBT(y, b, fixed) {
	if(!fixed) fixed = 1;
	var abs_y = Math.abs(y);
	if (abs_y >= 1000000000000)   { return (y / 1000000000000).toFixed(fixed) + "T" }
	else if (abs_y >= 1000000000) { return (y / 1000000000).toFixed(fixed) + "G" }
	else if (abs_y >= 1000000)    { return (y / 1000000).toFixed(fixed) + "M" }
	else if (abs_y >= 1000)       { return (y / 1000).toFixed(fixed) + "K" }
	else if (abs_y < 1 && y > 0)  { return y.toFixed(fixed) }
	else if (abs_y === 0)         { return '0' }
	else                      { return y.toFixed(fixed) }
}

function formatK_KMBT(y, b, fixed) {
	if(!fixed) fixed = 1;
	var abs_y = Math.abs(y);
	if (abs_y >= 1000000000) { return (y / 1000000000).toFixed(fixed) + "T" }
	else if (abs_y >= 1000000)    { return (y / 1000000).toFixed(fixed) + "G" }
	else if (abs_y >= 1000)       { return (y / 1000).toFixed(fixed) + "M" }
	else if (abs_y < 1 && y > 0)  { return y.toFixed(fixed) }
	else if (abs_y === 0)         { return '0' }
	else                      { return y.toFixed(fixed) }
}

function fn_GetDataByteInterval(P_VALUE) {
	var returnValue = "";
	var x = 1;

	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE <= 100) {
		returnValue = 20;
	}
	else if(P_VALUE <= 1024) {
		x = parseInt(P_VALUE / 5);
		returnValue = x;
	}
	else if(P_VALUE <= 1048576) {
		if(P_VALUE > 4096) {
			x = parseInt(P_VALUE / 1000 /5);
		}

		returnValue = 1024 * x;
	}
	else if(P_VALUE <= 1073741824) {
		if(P_VALUE > 4194304) {
			x = parseInt(P_VALUE / 1000000 /5);
		}
		returnValue = 1048576 * x;
	}
	else if(P_VALUE <= 1099511627776) {
		if(P_VALUE > 4294967296) {
			x = parseInt(P_VALUE / 1000000000 /5);
		}

		returnValue = 1073741824 * x;
	}
	else  if(P_VALUE <= 1125899906842624) {
		if(P_VALUE > 4398046511104) {
			x = parseInt(P_VALUE / 1000000000000 /5);
		}
		returnValue = 1099511627776 * x;
	}
	else {
		if(P_VALUE > 4503599627370496) {
			x = parseInt(P_VALUE / 1000000000000000 /5);
		}

		returnValue = 1125899906842624 * x;
	}

	return returnValue;
}

function fn_GetDataBpsInterval(P_VALUE) {
	var returnValue = "";
	var x = 1;

	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE <= 100) {
		returnValue = 20;
	}
	else if(P_VALUE <= 1000) {
		x = parseInt(P_VALUE / 5);

		returnValue = x;
	}
	else if(P_VALUE <= 1000000) {
		if(P_VALUE > 4000)
		{
			x = parseInt(P_VALUE / 1000 /5);
		}

		returnValue = 1000 * x;
	}
	else if(P_VALUE <= 100000000) {
		if(P_VALUE > 4000000) {
			x = parseInt(P_VALUE / 1000000 /5);
		}
		returnValue = 1000000 * x;
	}
	else if(P_VALUE <= 1000000000000) {
		if(P_VALUE > 4000000000) {
			x = parseInt(P_VALUE / 1000000000 /5);
		}

		returnValue = 1000000000 * x;
	}
	else  if(P_VALUE <= 1000000000000000) {
		if(P_VALUE > 4000000000000) {
			x = parseInt(P_VALUE / 1000000000000 /5);
		}
		returnValue = 1000000000000 * x;
	}
	else 	{
		if(P_VALUE > 4000000000000000) {
			x = parseInt(P_VALUE / 1000000000000000 /5);
		}

		returnValue = 1000000000000000 * x;
	}

	return returnValue;
}

function fn_GetDataSizeBps(P_VALUE, P_COLOR) {

	var returnValue = "";

	if(P_VALUE == null || P_VALUE == "") return "0 ";

	P_VALUE = parseFloat(P_VALUE);

	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE < 1000) {
		returnValue = fn_numberFormat(fn_roundXL(P_VALUE,2)) + '';
	}
	else if(P_VALUE < 1000000) {
		returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1000), 2)) + ' K';
	}
	else if(P_VALUE < 1000000000) {
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1000000), 2)) + ' M';
		}
		else 	{
			returnValue = fn_numberFormat(fn_roundXL((P_VALUE / 1000000), 2)) + ' M';
			//returnValue = ' <font color="#08AF52">' + fn_numberFormat(fn_roundXL((P_VALUE / 1000000), 2)) + ' M</font>';
		}
	}
	else if(P_VALUE < 1000000000000) {
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000), 2)) + ' G';
		}
		else 	{
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000), 2)) + ' G';
			//returnValue = ' <font color="#6495ED">' + fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000), 2)) + ' G</font>';
		}
	}
	else  if(P_VALUE < 1000000000000000) 	{
		if(P_COLOR != null && P_COLOR == false) {
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000000), 2)) + ' T';
		}
		else 	{
			returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000000), 2)) + ' T';
			//returnValue = ' <font color="#FF6A6A">' + fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000000), 2)) + ' T</font>';
		}
	}
	else 	{
		returnValue = fn_numberFormat(fn_roundXL( (P_VALUE / 1000000000000000), 2)) + ' P';
	}

	return returnValue;
}

function fn_GetDataByteToBps(P_VALUE) {
	//P_VALUE = fn_unNumberFormat(P_VALUE);

	var returnValue = "";
	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE < 1024) {
		returnValue = P_VALUE;//' Byte';
	}
	else if(P_VALUE < 1048576) {
		returnValue =P_VALUE / 1024 * 1000;
	}
	else if(P_VALUE < 1073741824) {
		returnValue = P_VALUE / 1024/ 1024 * 1000 * 1000;
	}
	else if(P_VALUE < 1099511627776) {
		returnValue = P_VALUE / 1024/ 1024/ 1024 * 1000 * 1000 * 1000;
	}
	else  if(P_VALUE < 1125899906842624) 	{
		returnValue = P_VALUE / 1024/ 1024/ 1024/1024 * 1000 * 1000 * 1000 * 1000;
	}
	else 	{
		returnValue = P_VALUE / 1024/ 1024/ 1024/1024/1024 * 1000 * 1000 * 1000 * 1000 * 1000;
	}
	return returnValue;
}

function fn_GetDataBpsToByte(P_VALUE) {
	var returnValue = "";

	if(P_VALUE == 0) {
		returnValue = 0;
	}
	else if(P_VALUE < 1000) {
		returnValue = P_VALUE;//' Byte';
	}
	else if(P_VALUE < 1000000) {
		returnValue =P_VALUE / 1000 * 1024;
	}
	else if(P_VALUE < 1000000000) {
		returnValue = P_VALUE / 1000/ 1000 * 1024 * 1024;
	}
	else if(P_VALUE < 1000000000000) {
		returnValue = P_VALUE / 1000/ 1000/ 1000 * 1024 * 1024 * 1024;
	}
	else  if(P_VALUE < 1000000000000000) 	{
		returnValue = P_VALUE / 1000/ 1000/ 1000/1000 * 1024 * 1024 * 1024 * 1024;
	}
	else 	{
		returnValue = P_VALUE / 1000/ 1000/ 1000/1000/1000 * 1024 * 1024 * 1024 * 1024 * 1024;
	}

	return returnValue;
}

function fn_roundXL(n, digits) {
	if(n == 0) return 0;

	if (digits >= 0) return parseFloat(n.toFixed(digits)); // 소수부 반올림

	digits = Math.pow(10, digits); // 정수부 반올림
	var t = Math.round(n * digits) / digits;

	return parseFloat(t.toFixed(0));
}

//콤마찍기 
function fn_numberFormat(num) {
	var pattern = /(^[+-]?\d+)(\d{3})/;
	num += '';

	while(pattern.test(num)) {
		num = num.replace(pattern,'$1' + ',' + '$2');
	}

	return num;
}


//콤마제거 
function fn_unNumberFormat(num) {
	return (num.replace(/\,/g,""));
}

function getSpec(cpuNum, memSize, diskSize) {
	if(cpuNum === '' || memSize === '' || diskSize === '') {
		return "";
	} else {
		return cpuNum + "-" + Math.round(memSize / 1024 / 1024 / 1024) + "-" + Math.round(diskSize / 1024 / 1024);
	}
}

function specFormatter(cellval, options, row) {
	if(!cellval)
		cellval = 'UserDefined';
	return cellval + " (" +  row.cpuNum + "-" + row.memSize + "-" + row.volumeSize + ")";
}

function memSizeFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		return Math.round(cellval / 1024 / 1024 / 1024) ;
	}
}

function memSizeFormatterMark(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		return cellval + " GB" ;
	}
}

function memSizeFormatterOther(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		return Math.round(cellval / 1024 / 1024 / 1024) + " GB";
	}
}

function diskSizeFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		return Math.round((cellval / 1024 / 1024) * 100) / 100;
	}
}

function diskSizeFormatterOther(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		return Math.round((cellval / 1024 / 1024) * 100) / 100 + " GB";
	}
}

var nicCountFormatter = function(cellval, options, row) {

	if(row.nicMapInfo) {
		var nicInfo = [];
		for(var i=0; i< row.nicMapInfo.length; i++) {
			nicInfo.push(row.nicMapInfo[i].name + " (" + row.nicMapInfo[i].count + ")");
		}
		return nicInfo.join(", ");
	}
	return cellval;
};

var poolCountFormatter = function(cellval, options, row) {

	if(row.poolMapInfo) {
		var poolInfo = [];
		for(var i=0; i< row.poolMapInfo.length; i++) {
			poolInfo.push(row.poolMapInfo[i].name + " (" + row.poolMapInfo[i].count + ")");
		}
		return poolInfo.join(", ");
	}
	return cellval;
};
//anonumous.js

function storagePoolformatter(cellval, options, row) {
	if(row && row.locale == "SOS") {
		return "object storage";
	} else {
		return cellval;
	}
}


function protectionIconFormatter(cellval, options, row) {

	if(typeof row == 'object'){
		if(_.has(row, 'protection')) {
			if(cellval === 1) {
				return "<img src='/images/icon/icon_lock.png' />";
			}else if(cellval === 0){
				return "<img src='/images/icon/icon_lock_off.png' />";
			}
		}
	}
}

function stateIconFormatter(cellval, options, row) {

	if(typeof row == 'object') {
		if(_.has(row, 'state') || _.has(row, 'powerState') || _.has(row, 'status')) {
			if(options === 'state' || options.colModel.name === 'state' || options === 'powerState' || options.colModel.name === 'powerState' || options === 'status' || options.colModel.name === 'status') {
				if(!cellval)
					return "";
				if(cellval === 'active' || cellval === 'running' || cellval === 'poweredOn' || cellval === 'RUNNING') {
					// return "<img src='/images/ico_status_normal.png' alt='normal' /> " + cellval;
					return "<span class='ico_status_running'>" + cellval + "</span>";

				} else if(cellval === 'error' || cellval === 'FAILED') {
					// return "<img src='/images/ico_status_trouble.png' alt='trouble' /> " + cellval;
					return "<span class='ico_status_trouble'>" + cellval + "</span>";
				} else if(cellval === 'poweredOff' || cellval === 'shutoff' || cellval === 'stopped' || cellval === 'deallocated') {
					// return "<img src='/images/ico_status_trouble.png' alt='trouble' /> " + cellval;
					return "<span class='ico_status_disabled'>" + cellval + "</span>";
				} else {
					if(cellval == null) cellval = '';
					// return "<img src='/images/ico_status_disabled.png' alt='disabled' /> " + cellval;
					return "<span class='ico_status_normal'>" + cellval + "</span>";
				}
			}
		}

		if(_.has(row, 'enabled')) {
			if(options === 'enabled' || options.colModel.name === 'enabled') {
				if(cellval === 1) {
					return "<img src='/images/icon/icon_status_on.png' alt='Yes' /> Yes";
				} else if(cellval === 0) {
					return "<img src='/images/icon/icon_status_off.png' alt='No' /> No";
				}
			}
		}

		if(_.has(row, 'webdavEnabled')) {
			if(options === 'webdavEnabled' || options.colModel.name === 'webdavEnabled') {
				if(cellval === 1) {
					return "<img src='/images/icon/icon_status_on.png' alt='Yes' /> Yes";
				} else if(cellval === 0) {
					return "<img src='/images/icon/icon_status_off.png' alt='No' /> No";
				}
			}
		}

		if(_.has(row, 'result')) {
			if(options === 'result' || options.colModel.name === 'result') {
				if(cellval === 'SUCCESS') {
					return "<span class='ico_status_running'>" + cellval + "</span>";
					//return "<img src='/images/icon/icon_status_success.png' alt='success' /> success";
				}else if(cellval === 'FAILED') {
					return "<span class='ico_status_trouble'>" + cellval + "</span>";
					//return "<img src='/images/icon/icon_status_failed.png' alt='failed' /> failed";
				}else if(cellval != null && cellval.indexOf("ING")>0) {
					return "<span class='ico_status_normal'>" + cellval + "</span>";
					//return "<img src='/images/icon/icon_status_ing.gif' alt='" + cellval +"' />" + cellval.toLowerCase();
				}else {
					return "<span class='ico_status_disabled'>" + cellval + "</span>";
					//return "<img src='/images/icon/icon_status_ing.gif' alt='progressing' /> progressing";
				}
			}
		}

		if(_.has(row, 'icon') || _.has(row, 'os')) {
			if( options.colModel.name === 'icon' || options.colModel.name === 'os') {
				if(!cellval)
					return "";

				if(cellval.toLowerCase().indexOf('centos') != -1) {
					return '<img src="../../static/images/icon/icon_os_CentOS.png" alt="CentOS" /> CentOS';
				} else if(cellval.toLowerCase().indexOf('debian') != -1) {
					return '<img src="../../static/images/icon/icon_os_Debian.png" alt="Debian" /> Debian';
				} else if(cellval.toLowerCase().indexOf('fedora') != -1) {
					return '<img src="../../static/images/icon/icon_os_Fedora.png" alt="Fedora" /> Fedora';
				} else if(cellval.toLowerCase().indexOf('freebsd') != -1) {
					return '<img src="../../static/images/icon/icon_os_FreeBSD.png" alt="FreeBSD" /> FreeBSD';
				} else if(cellval.toLowerCase().indexOf('redhat') != -1) {
					return '<img src="../../static/images/icon/icon_os_Redhat.png" alt="Redhat" /> Redhat';
				} else if(cellval.toLowerCase().indexOf('ubuntu') != -1) {
					return '<img src="../../static/images/icon/icon_os_Ubuntu.png" alt="Ubuntu" /> Ubuntu';
				} else if(cellval.toLowerCase().indexOf('windows') != -1) {
					return '<img src="../../static/images/icon/icon_os_Windows.png" alt="Windows" /> Windows';
				}
			}
		}

		if(_.has(row, 'osType')) {
			if(options.colModel.name === 'template') {
				var osType = row.osType;
				if(!osType)
					return cellval || "";

				if(osType.toLowerCase().indexOf('centos') != -1) {
					return '<img src="../../static/images/icon/icon_os_CentOS.png" alt="CentOS" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('debian') != -1) {
					return '<img src="../../static/images/icon/icon_os_Debian.png" alt="Debian" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('fedora') != -1) {
					return '<img src="../../static/images/icon/icon_os_Fedora.png" alt="Fedora" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('freebsd') != -1) {
					return '<img src="../../static/images/icon/icon_os_FreeBSD.png" alt="FreeBSD" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('redhat') != -1) {
					return '<img src="../../static/images/icon/icon_os_Redhat.png" alt="Redhat" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('ubuntu') != -1) {
					return '<img src="../../static/images/icon/icon_os_Ubuntu.png" alt="Ubuntu" /> ' + cellval;
				} else if(osType.toLowerCase().indexOf('windows') != -1) {
					return '<img src="../../static/images/icon/icon_os_Windows.png" alt="Windows" /> ' + cellval;
				}
			}
		}

		if(_.has(row, 'template')) {
			if(options.colModel.name === 'template') {
				var templateType = row.template;

				if(options === 'template' || options.colModel.name === 'template') {
					if(templateType.toLowerCase().indexOf('centos') != -1) {
						return '<img src="../../static/images/icon/icon_os_CentOS.png" alt="CentOS" /> '+ cellval;
					}else if(templateType.toLowerCase().indexOf('debian') != -1) {
						return '<img src="../../static/images/icon/icon_os_Debian.png" alt="Debian" /> ' + cellval;
					}else if(templateType.toLowerCase().indexOf('fedora') != -1) {
						return '<img src="../../static/images/icon/icon_os_Fedora.png" alt="Fedora" /> ' + cellval;
					}else if(templateType.toLowerCase().indexOf('freebsd') != -1) {
						return '<img src="../../static/images/icon/icon_os_FreeBSD.png" alt="FreeBSD" /> ' + cellval;
					}else if(templateType.toLowerCase().indexOf('redhat') != -1) {
						return '<img src="../../static/images/icon/icon_os_Redhat.png" alt="Redhat" /> ' + cellval;
					}else if(templateType.toLowerCase().indexOf('ubuntu') != -1) {
						return '<img src="../../static/images/icon/icon_os_Ubuntu.png" alt="Ubuntu" /> ' + cellval;
					}else if(templateType.toLowerCase().indexOf('windows') != -1) {
						return '<img src="../../static/images/icon/icon_os_Windows.png" alt="Windows" /> ' + cellval;
					}


				}
			}
		}
	}
}

function enableFormatter(cellval, options, row){
	if(cellval === "yes" || cellval === 1){
		return "<img src='/images/icon/icon_status_on.png' alt='yes' /> yes";
	}else if(cellval === 'no' || cellval === 0) {
		return "<img src='/images/icon/icon_status_off.png' alt='no' /> no";
	}else {
		return "";
	}
}

function enableFormatterInput(cellval){
	if(cellval === "yes" || cellval === 1){
		return "<input type='image' src='/images/icon/icon_status_on.png' alt='yes' /> yes";
	}else if(cellval === 'no' || cellval === 0) {
		return "<input type='image' src='/images/icon/icon_status_off.png' alt='no' /> no";
	}else {
		return "";
	}
}

function violateFormatter(cellval){
	if(cellval === "yes"){
		return "<input type='image' src='/images/icon/icon_status_failed.png' alt='yes' /> 비정상";
	}else if(cellval === 'no') {
		return "<input type='image' src='/images/icon/icon_status_success.png' alt='no' /> 정상";
	}else {
		return "";
	}
}

function usedFormatter(cellval){
	if(cellval === "yes"){
		return "<input type='image' src='/images/icon/icon_status_inUse.png' alt='inUse' /> 사용";
	}else if(cellval === 'no') {
		return "<input type='image' src='/images/icon/icon_status_available.png' alt='available' /> 미사용";
	}else {
		return "";
	}
}

function resultFormatter(cellval) {
	if(cellval === 'SUCCESS') {
		return "<img src='/images/icon/icon_status_success.png' alt='success' /> 성공";
	}else if(cellval === 'FAIL') {
		return "<img src='/images/icon/icon_status_failed.png' alt='failed' /> 실패";
	}
}

function actionFormatter(cellval) {
	if(cellval == 'LOGIN') {
		return "로그인";
	} else if(cellval == 'CREATE_USER') {
		return "사용자 생성";
	} else if(cellval == 'UPDATE_USER') {
		return "사용자 수정";
	} else if(cellval == 'DELETE_USER') {
		return "사용자 삭제";
	} else if(cellval == 'CREATE_LICENSE') {
		return "라이센스 생성";
	} else if(cellval == 'UPDATE_LICENSE') {
		return "라이센스 수정";
	} else if(cellval == 'DELETE_LICENSE') {
		return "라이센스 삭제";
	} else if(cellval == 'CHECK_LICENSE') {
		return "라이센스 확인";
	} else if(cellval == 'LOGOUT') {
		return "로그아웃";
	}
}

function enableFormatterInput(cellval){
	if(cellval === "yes" || cellval === 1){
		return "<input type='image' src='/images/icon/icon_status_on.png' alt='yes' /> yes";
	}else if(cellval === 'no' || cellval === 0) {
		return "<input type='image' src='/images/icon/icon_status_off.png' alt='no' /> no";
	}else {
		return "";
	}
}

function kByteSizeFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	}
	var size = Number(cellval) * 1024;
	return byteSizeFormatter(size, options, row);
};

function byteSizeFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else if(row && row.type && row.type === "DIRECTORY") {
		return "&#60;dir&#62;";
	} else {
		var size = Number(cellval);
		var i = Math.floor( Math.log(size) / Math.log(1024) );
		var returnVal = ( size / Math.pow(1024, i) ).toFixed(2) * 1;
		if(isNaN(returnVal)) {
			returnVal = 0;
			i = 0;
		}

		return returnVal + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
	}
};

function schedulerFormatter(cellval, options, row){
	if(cellval == 'rr'){
		return 'Round Robin';
	}else if (cellval === 'dh'){
		return 'Destination Hash';
	}else if (cellval === 'sh'){
		return 'Source Hash';
	}else if (cellval === 'lc'){
		return 'Least Connection';
	}else {
		return '';
	}
}

function adaptiveIpFormatter(cellval, options, row){
	if(!cellval) return "<button type='button' class='btn_table'><img src='/images/icon/icon_associate.png' alt='associate' /></button>";
	return cellval + " <button type='button' class='btn_table'><img src='/images/icon/icon_disassociate.png' alt='disassociate' /></button>";
}

function ejectButtonFormatter(cellval, options, row) {
	return "<button type='button' class='btn_table'><img src='/images/icon/icon_eject.png' alt='eject' /></button>";
}

function detachButtonFormatter(cellval, options, row) {
	return "<button type='button' class='btn_table'><img src='/images/icon/icon_detach.png' alt='detach' /></button>";
}

function associateButtonFormatter(cellval, options, row) {
	return "<button type='button' class='btn_table'><img src='/images/icon/icon_associate.png' alt='associate' /></button>";
}

function disassociateButtonFormatter(cellval, options, row) {
	return "<button type='button' class='btn_table'><img src='/images/icon/icon_disassociate.png' alt='disassociate' /></button>";
}

function deleteButtonFormatter(cellval, options, row) {
	return "<button type='button' class='btn_table'><img src='/images/icon/icon_delete.png' alt='delete' /></button>";
}

var osMaps = {
	'ubuntu': '../../static/images/icon/icon_os_Ubuntu.png',
	'centos': '../../static/images/icon/icon_os_CentOS.png',
	'redhat': '../../static/images/icon/icon_os_Redhat.png',
	'debian': '../../static/images/icon/icon_os_Debian.png',
	'fedora': '../../static/images/icon/icon_os_Fedora.png',
	'freebsd': '../../static/images/icon/icon_os_FreeBSD.png',
	'windows':'../../static/images/icon/icon_os_Windows.png'
};

var ViewHelper= {

	osIconFile: function(key) {
		var name = osMaps[key];
		return name || "";
	},
	fileSizeHumanReadable: function(bytes) {
		var i = -1;
		var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
		do {
			bytes = bytes / 1024;
			i++;
		} while (bytes > 1024);

		return Math.max(bytes, 0.1).toFixed(2) + byteUnits[i];
	},

	getProtection: function(data) {
		if(data === 0){
			return 'No';
		}
		else if (data === 1){
			return 'Yes';
		}
		else {
			return '';
		}
	},

	getSpecInfoDetail:function(spec, cpuNum, memSize, volumeSize) {
		if(!spec) spec = '';
		if(cpuNum == null) cpuNum = '';
		if(memSize == null) memSize = '';
		if(volumeSize == null) volumeSize = '';
		if(spec == '' && cpuNum == '' && volumeSize == '') return '';
		if(!spec) spec = 'UserDefined';
		return spec + " (" + cpuNum +"-" + memSize + "-" + volumeSize + ")";
	},

	getSchedulerName:function(a){
		if(a == 'rr'){
			return 'Round Robin';
		}else if (a === 'dh'){
			return 'Destination Hash';
		}else if (a === 'sh'){
			return 'Source Hash';
		}else if (a === 'lc'){
			return 'Least Connection';
		}else {
			return '';
		}
	},
	getEnabled: function(data) {
		return this.getProtection(data);
	},
	getState: function(cellval) {
		if(!cellval){
			return '';
		}

		if(cellval === 'RUNNING') {
			return "<img src='/images/icon/icon_status_running.png' alt='running' /> running";
		} if(cellval === 'LEADER') {
			return "<img src='/images/icon/icon_status_running.png' alt='running' /> running (leader)";
		}else if(cellval === 'COMPLETED') {
			return "<img src='/images/icon/icon_status_completed.png' alt='completed' /> completed";
		}else if(cellval === 'AVAILABLE') {
			return "<img src='/images/icon/icon_status_available.png' alt='available' /> available";
		}else if(cellval === 'IN_USE') {
			return "<img src='/images/icon/icon_status_inUse.png' alt='in-use' /> in-use";
		}else if(cellval === 'STOPPED') {
			return "<img src='/images/icon/icon_status_stopped.png' alt='stopped' /> stopped";
		}else if(cellval === 'FAILED') {
			return "<img src='/images/icon/icon_status_failed.png' alt='failed' /> failed";
		}else if(cellval === 'UP') {
			return "<img src='/images/icon/icon_status_up.png' alt='up' /> up";
		}else if(cellval === 'DOWN') {
			return "<img src='/images/icon/icon_status_down.png' alt='down' /> down";
		}else if(cellval != null && cellval.indexOf("ING")>0) {
			return "<img src='/images/icon/icon_status_ing.gif' alt='" + cellval +"' />" + cellval.toLowerCase();
		}else {
			if(cellval == null) cellval = '';
			return "<img src='/images/icon/icon_status_off.png' alt='off' />" + cellval;
		}
	}
};

var trim = String.prototype.trim ?
	function(text) {
		return text === null ? '' : String.prototype.trim.call(text);
	} :
	function(text) {
		var trimLeft = /^\s+/,
			trimRight = /\s+$/;

		return text === null ? '' : text.toString().replace(trimLeft, '').replace(trimRight, '');
	}

var ValidationUtil = {
	format: function() {
		var args = Array.prototype.slice.call(arguments),
			text = args.shift();
		return text.replace(/\{(\d+)\}/g, function(match, number) {
			return typeof args[number] !== 'undefined' ? args[number] : match;
		});
	},
	getError: function() {
		var result = this.validate();
		var error = [];
		for(var i in result) {
			error.push(result[i]);
		}

		if(error.length > 0 ) {
			alert(error[0]);
		}
	},
	getErrors: function() {
		var result = this.validate();
		var error = [];
		for(var i in result) {
			error.push(result[i]);
		}

		if(error.length > 0 ) {
			alert(error.join("\r\n"));
		}
	},
	invalid: function(model, error){
		if(typeof error == "object"){
			var _error = [];
			for(var i in error) {
				_error.push(error[i]);
			}

			if(_error.length > 0) {
				alert(_error[0]);
			}
		}else {
			alert(error);
		}
	},
	invalids: function(model, error){
		if(typeof error == "object"){
			var _error = [];
			for(var i in error) {
				_error.push(error[i] );
			}

			if(_error.length > 0) {
				alert(_error.join("\r\n"));
			}
		}else {
			alert(error);
		}
	},
	hasHostname: function(hostname, callback) {
		callback = callback || {};
		$.getJSON("/ace/servers?hostname="+ hostname, function(data) {
			if(data.rows.length > 0) {
				if(callback.exist)
					callback.exist();
				return true;
			}else {
				if(callback.nexist)
					callback.nexist();
				return false;
			}
		});
	},
	hasUserId: function(id, callback) {
		callback = callback || {};
		$.getJSON("/auth/users/"+ id, function(data) {
			if(data.total > 0) {
				if(callback.exist)
					callback.exist();
				return true;
			}else {
				if(callback.nexist)
					callback.nexist();
				return false;
			}
		});
	},
	getServerError: function(resp) {
		try{
			var e = $.parseJSON(resp.responseText);
// //            alert("Exception: " + e.exception + "\r\nmessage: " + e.message + "\r\nstatus: " + resp.status);
// 			alert("message: " + e.message + "\r\nstatus: " + resp.status);
			MainUI.toastMessage("message: " + e.message + "\r\nstatus: " + e.status);
		}catch(e) {
			try{
				if(resp.status != 201 && resp.status != 200) {
					MainUI.toastMessage(resp.status + "( " + resp.statusText + " )");
				} else {
					MainUI.toastMessage("Error");
				}
				// alert(resp.status + "( " + resp.statusText + " )");
			}catch(e) {
				// alert("Error");
				MainUI.toastMessage("Error");
			}
		}
	},
	trim: trim,
	hasValue: function (value) {
		return !(_.isNull(value) || _.isUndefined(value) || (_.isString(value) && trim(value) === '') || (_.isArray(value) && _.isEmpty(value)));
	},
	isNumber: function(value){
		return _.isNumber(value) || (_.isString(value) && value.match(defaultPatterns.number));
	},
	patterns: {
		// Matches any digit(s) (i.e. 0-9)
		digits: /^\d+$/,

		// Matches any number (e.g. 100.000)
		number: /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/,

		// Matches a valid email address (e.g. mail@example.com)
		email: /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i,

		// Mathes any valid url (e.g. http://www.xample.com)
		url: /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i,

		id: /^[a-z|A-Z|]+[a-z|A-Z|0-9]+$/
	}


};


var AnonModel = Backbone.Model.extend({
	initialize: function() {
		this.listenTo(this, "invalid", function(mode, error){
			if(typeof error == "object"){
				var _error = [];
				for(var i in error) {
					_error.push(error[i]);
				}

				if(_error.length > 0) {
					alert(_error[0]);
				}
			}else {
				alert(error);
			}
		});
	},
	defaults: {
		adaptiveIp: null,
		addr: '',
		alarmType: '',
		avgBps: '',
		avgBytes: '',
		bootable: '',
		conditionCount: '',
		contact: '',
		containerName: '',
		count: '',
		cpu: '',
		cpuNum: null,
		createdAt: '',
		creator: '',
		cycle: '',
		dataIp: '',
		disk: '',
		diskSize: '',
		email: '',
		enabled: '',
		expiredAt: null,
		gateway: '',
		hostName: '',
		ibps: '',
		ibytes: '',
		id: null,
		ip: '',
		isoId: null,
		lbName: null,
		mailTo: '',
		maxInstance: '',
		maxBps: '',
		maxBytes: '',
		mem: '',
		memSize: null,
		memberCount: '',
		memberPort: '',
		minBps: '',
		minBytes: '',
		name: null,
		networkName: null,
		newId: null,
		nicCount: '',
		nodeIp: null,
		objectCount: '',
		objectSize: '',
		obps: '',
		obytes: '',
		operationType: '',
		operator: '',
		os: '',
		ownership: null,
		password: null,
		password2: null,
		permissionCount:'',
		pool: null,
		poolId: null,
		poolName: null,
		port: '',
		privateIp: '',
		protection: '',
		protocol: '',
		ruleCount: '',
		scheduler: '',
		size: '',
		snapshotId: null,
		spec: null,
		specId: null,
		specName: null,
		state: '',
		status: '',
		subnetAddr: null,
		target: '',
		targetId: '',
		template: null,
		templateId: null,
		templateName: null,
		tenantId: '',
		totalBps: '',
		totalBytes: '',
		type: null,
		updatedAt: '',
		url: '',
		userName:'',
		userCount:'',
		value: '',
		version: '',
		vmId:null,
		vmName: '',
		volumeId: null,
		volumeName: null,
		volumeSize: null,
		webdavEnabled: '',
		webDavUrl: ''
	}
});

var AnonCollection = Backbone.Collection.extend({

	parse: function(request, xhr ) {
		if(request.data) {
			this.pageModel={};
			this.pageModel.page = request.page;
			this.pageModel.perPage = request.perPage;
			this.pageModel.total = request.pageSize;
			return request.data;

		}else if(request.rows) {
			this.pageModel={};
			this.pageModel.page = request.page;
			this.pageModel.total = request.total;
			return request.rows;
		}else{
			return request;
		}
	}
});


Backbone.Collection.prototype.parse = function(resp, xhr) {

	if(resp.data) {
		this.pageModel={};
		this.pageModel.page = resp.page;
		this.pageModel.perPage = resp.perPage;
		this.pageModel.total = resp.pageSize;
		return resp.data;

	}else if(resp.rows) {
		this.pageModel={};
		this.pageModel.page = resp.page;
		this.pageModel.total = resp.total;
		return resp.rows;
	}else{
		return resp;
	}

};

Backbone.View.prototype.render = function() {

	if(this.el && this.template && this.model) {
		this.$el.html( this.template(this.model.toJSON()));
	}
	return this;
};

Backbone.View.prototype.reload = function() {
	var self = this;

	if(this.model) {
		this.$el.empty();
		this.model.fetch({
			success: function(model, resp) {
				self.render();
			}
		});
	}
};

Backbone.Model.prototype.reset = function(attributes, options) {
	this.clear({silent: true});
	if(attributes) {
		this.set(attributes);
	} else {
		this.set(this.defaults);
	}

	return this;
};

var AnonListView = Backbone.View.extend({
	initialize: function(){
		this.index = 0;
		this.listenTo(this.collection, "add", this.append, this);
		this.listenTo(this.collection, "remove", this.remove, this);
		this.listenTo(this.collection, "change", this.change, this);

		if(this.frame) {
			this.setElement(this.frame);
		}
	},
	fetch: function(data){
		if(this.collection && data == undefined){
			this.collection.fetch();
		} else if(this.collection && data != undefined) {
			this.$el.empty();
			this.collection.reset();
			this.collection.fetch({data : data});

		}
	},
	refresh: function(data) {
		if(this.grid){
			$(this.grid).clearGridData();
			this.index =0;
			this.collection.reset();
			this.collection.fetch();

		}
		else if(this.el) {
			if(this.parentGrid){
				var index = $(this.parentGrid).getGridParam('selrow');

				this.$el.empty();
				if(index != null && data != undefined) {
					this.collection.reset();
					this.collection.fetch({data : data});
				}
			} else {
				this.$el.empty();
				this.collection.reset();
				this.collection.fetch();
			}
		}
	},
	blank: function() {
		if(this.grid) {
			this.$grid.clearGridData();
		}else {
			this.$el.empty();
		}
		this.collection.reset();
	},
	append: function(model) {

		if(this.grid) {
			$(this.grid).jqGrid('addRowData', model.attributes.id, model.toJSON());
		}
		else if(this.el && this.template) {
			this.$el.append( this.template(model.toJSON()));
		}
		else {
			console.log("AnonListView - Invalid function error!");
		}
	},
	remove: function(model, collections, options) {
		if(this.grid) {
			var index = $(this.grid).getGridParam('selrow');
			$(this.grid).delRowData(index);
		}
	},
	change: function(model, collections, options){
		if(this.grid){

		}else if(this.el){
			this.$el.empty();
			_.each(this.collection.models, function(model, key) {
				this.append(model);
			}, this);
		}
	}
});

var AnonDetailView = Backbone.View.extend({
	initialize: function() {
		if(this.model){
			this.dummy = this.model.clone();
		}
	},
	refresh: function() {
		if(this.parentGrid){
			var index = $(this.parentGrid).getGridParam('selrow');
			if(index != null) {
				this.render();
			} else {
				this.blank();
			}
		}
	},
	blank: function() {
		this.model = this.dummy;
		this.render();
	},
	render: function() {
		if(this.el) {
			this.$el.html( this.template(this.model.toJSON()));
		}
		return this;
	}
});

/* MinxIn */

var WindowMixin = {
	onToggle: function(params, self) {
		if(typeof params == "object") {
			if(typeof params.before == "function") {
				if(params.before()) {
					this.$el.toggle();
				}
			}
			if(this.$el.is(":visible") && typeof params.visible == "function") {
				$(document).on("keyup", this.keyHandler);
				var fn = _.bind(params.visible, self);
				fn();
			}
		}
	},
	keyHandler: function(e) {
		if(e.which == 27) this.hide();
	},
	hide: function() {
		this.$el.hide();
		$(document).off("keypup", this.keyHandler);
		if(typeof this.afterHide == "function") {
			this.afterHide();
		}
	}

};

var CollectionMixin = {
	initEvent: function() {
		this.listenTo(this.collection, "add", this.append, this);
		this.listenTo(this.collection, "reset", this.reset, this);
		this.listenTo(this.collection, "remove", this.remove, this);
	},
	refetch: function(url) {
		if(url){
			this.collection.url = url;
		}
		this.collection.reset();
		this.collection.fetch();
	},
	reload: function() {
		this.collection.reset();
		this.collection.fetch();
	}
};

var GridMixin = {
	append :function(model) {
		$(this.grid).jqGrid('addRowData', model.attributes.id, model.toJSON());
	},
	appendFirst :function(model) {
		$(this.grid).jqGrid('addRowData', model.attributes.id, model.toJSON(), "first");
	},
	addRow: function(json, key) {
		$(this.grid).jqGrid('addRowData', id, json);
	},
	addRowFrist: function(json, key) {
		$(this.grid).jqGrid('addRowData', id, json, "first");
	},
	reset: function(models) {
		$(this.grid).clearGridData();
		var grid = $(this.grid);
		models.each(function(model) {
			grid.jqGrid('addRowData', model.attributes.id, model.toJSON());
		});
	},
	remove: function(removedModel, models, options) {
		if(!!removedModel.id == false)
			throw "id is undefiend";
		$(this.grid).delRowData(removedModel.id);
	},
	removeRow: function(key) {
		$(this.grid).delRowData(key);
	}
};

var TableMixin = {
	append :function(model) {
		$(this.templateHolder).append(this.template(model.toJSON()));
	},
	reset: function() {
		$(this.templateHolder).empty();
	},
	selectTr: function(e) {
		var id = $(e.currentTarget).attr("id");
		$(e.currentTarget).parent().children().selectOne(id, 'id');

		if(typeof this.afterSelectTr == "function") {
			if(this.collection) {
				var model = this.collection.findWhere({'id': id});
				this.afterSelectTr(model);
			}else{
				this.afterSelectTr({'id': id });
			}
		}
	}
};


_.extend(Backbone.Model.prototype, ValidationUtil);
_.extend(GridMixin, CollectionMixin);
_.extend(TableMixin, CollectionMixin);


var stompUtil = function() {

	var listeners = [];

	function connectCallback() {
		if(listeners) {
			for(var i in listeners) {
				if(typeof i == 'string' && i.indexOf('/') > -1) {
					this.subscribe(i, listeners[i]);
				}
			}
		}
	};

	function errorCallback(error){
		if(error.headers && error.headers.message){
			console.log(error.headers.message);
		}else {
			console.log(error);
		}
	};

	function connect() {
		var sock = new SockJS("/sockjs");
		this.client = Stomp.over(sock);
		this.client.connect({}, connectCallback, errorCallback);
	};

	function addListener(url, fn) {
		listeners[url] = fn;
		return this;
	};

	function getError(payload) {
		/*var msg = $(".content_north .hiddenMsg");

        if(msg.length == 0) {
            $(".searchbox").after( $("<div>", { 'class': 'hiddenMsg'}).append("<span/>").append("<p class='closeBtn'><button type='button'><span>Close</span></button></p>").hide());
            msg = $(".content_north .hiddenMsg");
        }*/
		var msgs = $(".content_north .hiddenMsg");

		var msg = $("<div>", { 'class': 'hiddenMsg'}).append("<span/>").append("<p class='closeBtn'><button type='button'><span>Close</span></button></p>").hide();

		if(msgs.length > 0) {
			msg.css("top", 6 + (38 * msgs.length));
			msg.css("zIndex", "1000");
		}

		// $(".cont_header").after(msg);

		try{
			msg.find('span').html(payload.resultDetail);
		}catch(e) {
			msg.find('span').html("Error");
			console.log(e)
		}

		msg.fadeIn(500);
		/*msg.fadeIn(500, function() {
            setTimeout(function(){
                msg.fadeOut(1000);
            }, 7000);

        });*/
	};

	return {
		connect: connect,
		addListener: addListener,
		getError: getError
	}
}();


$.jgrid.extend({
	resetSize: function(init) {
		var p = $(this).jqGrid("getGridParam");

		if(!p.autowidth) return;
		// if(init && $(this).jqGrid("getGridParam", "groupHeader")) {
		// 	var groupHeaders = $(this).jqGrid("getGridParam", "groupHeader");
		// 	$(this).jqGrid('destroyGroupHeader');
		// 	$(this).jqGrid('setGroupHeaders', groupHeaders);
		// }

		var parentDiv = $(this).parent().parent().parent().parent().parent().parent();
		// var hboxHeight = parentDiv.children().find('.ui-jqgrid-hbox').height();


        if (p.widthOrg === undefined || p.widthOrg === "auto" || p.widthOrg === "100%") {
            // $(this).jqGrid("setGridWidth", p.tblwidth + p.scrollOffset, false);
			$(this).setGridWidth( parentDiv.width(), true);
        }

		// $(this).trigger('reloadGrid');
		// $(this).setGridHeight(
		// 		isSouth == true?
		// 				(
		// 					parentDiv.height() < 571?
		// 						571 - hboxHeight : parentDiv.height() - hboxHeight
		// 				) : parentDiv.height() - hboxHeight, true);
		// $(this).setGridHeight(571 - hboxHeight, true);

		// if(!init && $(this).jqGrid("getGridParam", "groupHeader")) {
		// 	var groupHeaders = $(this).jqGrid("getGridParam", "groupHeader");
		// 	$(this).jqGrid('destroyGroupHeader');
		// 	$(this).jqGrid('setGroupHeaders', groupHeaders);
		// }
	},
	configureTable: function() {
		var colModel = $(this).jqGrid("getGridParam", "colModel");
		var colNames = $(this).jqGrid("getGridParam", "colNames");
		var rowNum = $(this).jqGrid("getGridParam", "rowNum");

		$("#pop_setting .settingList").empty();
		$("#pop_setting .select_table > option[value="+rowNum+"]").prop("selected", true);
		$.each(colModel, function(i) {

			if( colModel[i].admin == undefined || colModel[i].admin == true) {
				var inputNode = $('<input type="checkbox" id="'+colModel[i].name+'" name="" />').prop('checked', !this.hidden);
				var labelNode = $('<label for="'+colModel[i].name+'">'+$.jgrid.stripHtml((colNames[i]===""?"Protection":colNames[i]))+'</label>');
				if(i == 0) {
					inputNode.attr('disabled', 'disabled');
					labelNode.html(labelNode.html() + "  ( 첫열은 선택 불가능 합니다. )");
				}
				$('<li></li>').appendTo($("#pop_setting .settingList")).append(inputNode).append(labelNode);
			}
		});

		configureTablePopupView.initGrid(this);
	},
	setConfigureTable: function() {
		var path = window.location.pathname;
		var self = this;
		var gridId = $(this)[0].id + (path.lastIndexOf('usage') != -1? "-" + path.substring(path.lastIndexOf('/')+1, path.length) : "");
		var gridState = getObjectFromLocalStorage(gridId);
		var colModel = $(this).jqGrid("getGridParam", "colModel");

		if(gridState) {
			$.each(colModel, function(i) {
				var colName = colModel[i].name;
				var colHidden = gridState[colName];

				if(colHidden) {
					$(self).jqGrid("showCol", colName);
				} else {
					$(self).jqGrid("hideCol", colName);
				}
			});

			$(self).jqGrid('setGridParam', {rowNum: gridState["rowNum"]});
		}

		var dataType = $(this).jqGrid('getGridParam', 'datatype');
		if(dataType == 'json') {
			var colNames = $(this).jqGrid("getGridParam", "colNames");
			if($(".select_search").length > 0){
				var boxit = $(".select_search").data("selectBox-selectBoxIt");
				if(!boxit)
					return;

				var blockKeyword = ['Created', 'NIC', 'Rule', 'Member'];
				boxit.remove();
				$.each(colNames, function(i, j) {
					if(j != "" && blockKeyword.indexOf(j) == -1 ){
						if(!colModel[i].hidden) {
							if(j.indexOf("<") > -1) {
								if(!j.match(/<(.|\n)*?>/g)){
									boxit.add({ value: colModel[i].name, text: j } );
								}
							}else{
								boxit.add({ value: colModel[i].name, text: j } );
							}
						}
					}
				});
			}
		}
	},
	search: function(column, text) {

		if(($(".select_search").length == 0 || $(".input_search").length == 0) && column == null && text == null) return;

		var grid = $(this);
		var loadonce = grid.getGridParam('loadonce');
		var f = { groupOp: "AND", rules: [] };
		var searchColumn = (column != null? column : $(".select_search").val());
		var searchText = (text != null? text : $(".input_search").val());

		if(loadonce) {
			f.rules.push({field: searchColumn, op: "cn", data: searchText});

			grid[0].p.search = f.rules.length > 0;
			$.extend(grid[0].p.postData, {filters: JSON.stringify(f)});
			grid.trigger("reloadGrid", [{page: 1}]);
		} else {
			grid.setGridParam({
				page:1,
				postData: { q0: searchColumn, q1: searchText }
			}).trigger("reloadGrid");
		}
	}
});

function setRowNum(defaultRowNum, gridId) {
	var path = window.location.pathname;
	var $grid = $(gridId);
	var gridId = $grid[0].id + (path.lastIndexOf('usage') != -1? "-" + path.substring(path.lastIndexOf('/')+1, path.length) : "");
	var gridState = getObjectFromLocalStorage(gridId);

	if(gridState) {
		return gridState["rowNum"];
	} else {
		return defaultRowNum;
	}
}

/** History Grid List View */
var HistoryListView = Backbone.View.extend({
	gridOption: {
		datatype: "local",
		url:"/historys",
		jsonReader: {
			repeatitems: false,
			id: "id"
		},
		colNames : [ '아이디', '사용자 아이디', '사용자 이름', '기능', '결과', '내용', '대상', '접속주소', '요청일자'],
		colModel : [
			{ name : 'id', align : 'left', hidden:true },
			{ name : 'userId', align : 'left', width:"100px" },
			{ name : 'userName', align : 'left', width:"100px"  },
			{ name : 'action', align : 'left' , formatter:actionFormatter },
			{ name : 'result', align : 'left' , formatter:resultFormatter, width:"100px" },
			{ name : 'content', align : 'left', width:"300px" },
			{ name : 'target', align : 'left' },
			{ name : 'ip', align : 'left' },
			{ name : 'createdAt', align : 'left' }
		],
		rowNum: 10,
		sortname:"createdAt",
		sortorder:"desc",
		autowidth: true,
		gridComplete: function() { $(this).resetSize(); },
		scrollOffset:0,
		scroll: true,
		autoencode: true,
		loadtext: ""
	},
	initialize: function(){
		this.$grid = $(this.grid);
		this.$parentGrid = $(this.parentGrid);
		this.$grid.jqGrid(this.gridOption);
	},
	fetch: function(){
		var index = this.$parentGrid.getGridParam('selrow');

		if(index != null) {
			var url = "/historys";

			this.$grid.jqGrid('setGridParam', {
				datatype : 'json',
				url : url,
				postData : {
					target : index
				},
				page : 1
			}).trigger("reloadGrid");
		}
	},
	refresh: function() {
		var index = this.$parentGrid.getGridParam('selrow');

		if(index != null) {
			this.fetch();
		} else {
			this.blank();
		}
	},
	blank: function() {
		this.$grid.clearGridData();
	}
});
/* History Grid List View **/

function userNameFormatter(userId, userName, grid, rowId) {
	if(userId != "" && userId != null && userName != "" && userName != null) {
		if(grid) {
			$(grid).jqGrid('setCell',rowId,'userName', userId + ' (' + userName + ')', '');
			return "";
		} else {
			return userId + " (" + userName + ")";
		}
	} else if(userId != "" && userId != null && (userName == "" || userName == null)) {
		if(grid) {
			$(grid).jqGrid('setCell',rowId,'userName', userId, '');
			return "";
		} else {
			return userId;
		}
	} else {
		if(!grid) {
			return "";
		}
	}
}

var saveObjectInLocalStorage = function (storageItemName, object) {
		if (typeof window.localStorage !== 'undefined') {
			window.localStorage.setItem(storageItemName, JSON.stringify(object));
		}
	},
	removeObjectFromLocalStorage = function (storageItemName) {
		if (typeof window.localStorage !== 'undefined') {
			window.localStorage.removeItem(storageItemName);
		}
	},
	getObjectFromLocalStorage = function (storageItemName) {
		if (typeof window.localStorage !== 'undefined') {
			return $.parseJSON(window.localStorage.getItem(storageItemName));
		}
	};

$(window).bind('resize', function(){
	$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
});

Date.prototype.format = function(f) {
	if (!this.valueOf()) return " ";
	var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
	var d = this;
	return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
		switch ($1) {
			case "yyyy": return d.getFullYear();
			case "yy": return (d.getFullYear() % 1000).zf(2);
			case "MM": return (d.getMonth() + 1).zf(2);
			case "dd": return d.getDate().zf(2);
			case "E": return weekName[d.getDay()];
			case "HH": return d.getHours().zf(2);
			case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
			case "mm": return d.getMinutes().zf(2);
			case "ss": return d.getSeconds().zf(2);
			case "a/p": return d.getHours() < 12 ? "오전" : "오후";
			default: return $1;
		}
	});
};

String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
Number.prototype.zf = function(len){return this.toString().zf(len);};

function monthFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		var date = new Date(cellval);
		return date.format("yyyy-MM");
	}
}

function dayFormatter(cellval, options, row) {
	if(cellval === null || cellval === '') {
		return "";
	} else {
		var date = new Date(cellval);
		return date.format("yyyy-MM-dd");
	}
}


/** NODE/VM/POOL alarm 용 Formatter */
function metricFormatter(cellval) {
	if(cellval === null || cellval === '') {
		return "";
	} else if(cellval === "NODE_CPU_USE_PERCENT" || cellval ==="VM_CPU_USE_PERCENT"){
		return "CPU Usage(%)";
	} else if(cellval === "NODE_DISK_RBYTES" || cellval === "VM_DISK_RBYTES"){
		return "Disk Read(bytes)";
	} else if(cellval === "NODE_DISK_WBYTES"  || cellval === "VM_DISK_WBYTES"){
		return "Disk Write(bytes)";
	} else if(cellval === "NODE_NETWORK_INPUT"  || cellval === "VM_NETWORK_INPUT"){
		return "Network In(bps)";
	} else if(cellval === "NODE_NETWORK_OUTPUT"  || cellval === "VM_NETWORK_OUTPUT"){
		return "Network Out(bps)";
	} else if(cellval === "NODE_MEM_USE_PERCENT"){
		return "Memory Usage(%)";
	} else if(cellval === "NODE_DISK_USE_PERCENT"){
		return "Disk Usage(%)";
	} else if(cellval === "NODE_DEAD"){
		return "Node Down";
	} else if(cellval === "ACE_VIRTD_DEAD"){
		return "ACE Virtd Down";
	} else if(cellval === "ACE_NETD_DEAD"){
		return "ACE Netd Down";
	} else if(cellval === "ACE_ADMD_DEAD"){
		return "ACE Admd Down";
	} else if(cellval === "SOS_ADMD_DEAD"){
		return "SOS Admd Down";
	} else if(cellval === "SOS_MNTD_DEAD"){
		return "SOS Mntd Down";
	} else if(cellval === "SOS_VOLD_DEAD"){
		return "SOS Vold Down";
	} else if(cellval === "POOL_USE_PERCENT"){
		return "Storage Usage(%)";
	} else if(cellval === "LV_USE_PERCENT"){
		return "Storage Usage(%)";
	}
}

function statisticsFormatter(cellval) {
	if(cellval === null || cellval === '') {
		return "";
	} else if(cellval === "AVG"){
		return "Average";
	} else if(cellval === "MIN"){
		return "Minimum";
	} else if(cellval === "MAX"){
		return "Maximum";
	}
}

function operatorFormatter(cellval) {
	if(cellval === null || cellval === '') {
		return "";
	} else if(cellval === "gt"){
		return ">";
	} else if(cellval === "ge"){
		return ">=";
	} else if(cellval === "lt"){
		return "<";
	} else if(cellval === "le"){
		return "<=";
	}
}

function thresholdFormatter(cellval, options, row) {

	var metric = "";

	if(typeof row == "object") {
		metric = row.alarmType;
	}else {
		metric = options;
	}

	if(cellval === null || cellval === '') {
		return "";
	} else if(metric.indexOf("PERCENT") != -1) {
		return cellval + " %";
	} else if(metric.indexOf("DISK") != -1) {
		return cellval + " bytes";
	} else if(metric.indexOf("NETWORK") != -1) {
		return cellval + " bps";
	}
}

function resourceFormatter(cellval, options, row){
	if(typeof row == 'object') {
		if(options === 'vmCount' || options.colModel.name === 'vmCount') {
			return (row.vmLimit==0) ? 0 : Math.round(row.vmCount/row.vmLimit*100) +"% ("+ row.vmCount + "/"+ row.vmLimit +")";
		} else if (options === 'volumeCount' || options.colModel.name === 'volumeCount'){
			return (row.volumeNumLimit==0) ? 0 : Math.round(row.volumeCount/row.volumeNumLimit*100) +"% ("+ row.volumeCount + "/"+ row.volumeNumLimit +")";
		} else if (options === 'snapshotCount' || options.colModel.name === 'snapshotCount'){
			return (row.snapshotLimit==0) ? 0 : Math.round(row.snapshotCount/row.snapshotLimit*100) +"% ("+ row.snapshotCount + "/"+ row.snapshotLimit +")";
		} else if (options === 'subnetCount' || options.colModel.name === 'subnetCount'){
			return (row.subnetLimit==0) ? 0 : Math.round(row.subnetCount/row.subnetLimit*100) +"% ("+ row.subnetCount + "/"+ row.subnetLimit +")";
		} else if (options === 'templateCount' || options.colModel.name === 'templateCount'){
			return (row.templateLimit==0) ? 0 : Math.round(row.templateCount/row.templateLimit*100) +"% ("+ row.templateCount + "/"+ row.templateLimit +")";
		} else if (options === 'adaptiveIpCount' || options.colModel.name === 'adaptiveIpCount'){
			return (row.adaptiveIpLimit==0) ? 0 : Math.round(row.adaptiveIpCount/row.adaptiveIpLimit*100) +"% ("+ row.adaptiveIpCount + "/"+ row.adaptiveIpLimit +")";
		} else if (options === 'portForwardingCount' || options.colModel.name === 'portForwardingCount'){
			return (row.portForwardingLimit==0) ? 0 : Math.round(row.portForwardingCount/row.portForwardingLimit*100) +"% ("+ row.portForwardingCount + "/"+ row.portForwardingLimit +")";
		} else if (options === 'loadBalancerCount' || options.colModel.name === 'loadBalancerCount'){
			return (row.loadBalancerLimit==0) ? 0 : Math.round(row.loadBalancerCount/row.loadBalancerLimit*100) +"% ("+ row.loadBalancerCount + "/"+ row.loadBalancerLimit +")";
		} else if (options === 'autoScalerCount' || options.colModel.name === 'autoScalerCount'){
			return (row.autoScalerLimit==0) ? 0 : Math.round(row.autoScalerCount/row.autoScalerLimit*100) +"% ("+ row.autoScalerCount + "/"+ row.autoScalerLimit +")";
		} else if (options === 'containerSizeCount' || options.colModel.name === 'containerSizeCount'){
			return (row.containerSizeLimit==0) ? 0 : Math.round(row.containerSizeCount/(row.containerSizeLimit)*100) +"% ("+ row.containerSizeCount + "/"+ (row.containerSizeLimit) +"G)";
		} else if (options === 'containerNumCount' || options.colModel.name === 'containerNumCount'){
			return (row.containerNumLimit==0) ? 0 : Math.round(row.containerNumCount/row.containerNumLimit*100) +"% ("+ row.containerNumCount + "/"+ row.containerNumLimit +")";
		} else if (options === 'webdavCount' || options.colModel.name === 'webdavCount'){
			return (row.webdavLimit==0) ? 0 : Math.round(row.webdavCount/row.webdavLimit*100) +"% ("+ row.webdavCount + "/"+ row.webdavLimit +")";
		} else if (options === 'roleCount' || options.colModel.name === 'roleCount'){
			return (row.roleLimit==0) ? 0 : Math.round(row.roleCount/row.roleLimit*100) +"% ("+ row.roleCount + "/"+ row.roleLimit +")";
		} else if (options === 'userCount' || options.colModel.name === 'userCount'){
			return (row.userLimit==0) ? 0 : Math.round(row.userCount/row.userLimit*100) +"% ("+ row.userCount + "/"+ row.userLimit +")";
		} else if (options === 'tokenCount' || options.colModel.name === 'tokenCount'){
			return (row.tokenLimit==0) ? 0 : Math.round(row.tokenCount/row.tokenLimit*100) +"% ("+ row.tokenCount + "/"+ row.tokenLimit +")";
		}
	}
}

function targetFormatter(cellval, options, row){
	return row.targetName + " (" + cellval + ")";
}

function userFormatter(cellval, options, row){
	return cellval + " (" + row.id + ")";
}

/**
 캘린더 참조
 http://docs.oracle.com/javase/7/docs/api/
 **/
function scheduleTimeFormatter(cellval, options, row) {
	var day = "";
	switch(row.day) {
		case 1: day = "Sun "; break;
		case 2: day = "Mon "; break;
		case 3: day = "Tue "; break;
		case 4: day = "Wed "; break;
		case 5: day = "Thu "; break;
		case 6: day = "Fri "; break;
		case 7: day = "Sat "; break;
	}

	var min = (row.minute < 10 ? ('0' + row.minute) : row.minute);
	var hour = (row.hour < 10 ? ('0' + row.hour) : row.hour);

	if(row.day > 0) {
		if(row.type && row.type == "MONTHLY") {
			return "" + row.day + "일 " + hour + ":" + min;
		}
	}


	return  day + hour + ":" + min;
}

function dashboardUsageFormatter(cellval) {
	var value = 100;

	if(cellval > 100){
		return value;
	} else {
		return cellval;
	}
}

function volumeSize(count, limit) {

	if(limit == 0) {
		limit = 0;
	}
	else if(limit < 1000) {
		limit = limit + "(GB)";
	}
	else if(limit < 1000000)	{
		limit = fn_roundXL(limit/1000,3) + "(TB)";
	}
	else if(limit < 1000000000) {
		limit = fn_roundXL(limit/1000000,6) + "(PB)";
	}

	if(count == 0) {
		count = 0;
	}
	else if(count < 1000) {
		count = count + "(GB)";
	}
	else if(count < 1000000)	{
		count = fn_roundXL(count/1000,3) + "(TB)";
	}
	else if(count < 1000000000) {
		count = fn_roundXL(count/1000000,6) + "(PB)";
	}

	var value = count + " / " + limit;
	return value;
}

/* NODE/VM/POOL alarm 용 Formatter */
function volumeTypeFormatter(cellval, options, row){
	if(cellval == "vda") {
		return "default";
	}

	return "add";
}

var terminationFormatter = function (cellval, options, row) {

	if(!cellval) {
		return "-";
	}
	return cellval;
};

var DateFormatter = {
	parse : function(totalSec) {
		var day = parseInt(totalSec / (60 * 60 * 24));
		var hour = parseInt((totalSec - day * 60 * 60 * 24) / (60 * 60));
		var minute = parseInt((totalSec - day * 60 * 60 * 24 - hour * 3600) / 60);
		var second = totalSec % 60;
		if(hour < 10) { hour = "0" + hour }
		if(minute < 10) { minute = "0" + minute }
		if(second < 10) { second = "0" + second }

		return day + " days, " + hour + ":" + minute + ":" + second + "  ";
	}
};

var dateFormatter = function (cellval, options, row) {

	var day = parseInt(cellval / (60 * 60 * 24));
	var hour = parseInt((cellval - day * 60 * 60 * 24) / (60 * 60));
	var minute = parseInt((cellval - day * 60 * 60 * 24 - hour * 3600) / 60);
	var second = cellval % 60;
	if(hour < 10) { hour = "0" + hour }
	if(minute < 10) { minute = "0" + minute }
	if(second < 10) { second = "0" + second }

	return day + " days, " + hour + ":" + minute + ":" + second + "  ";
};


var privateIpFormatter = function(cellval, options, row) {

	if(row.adaptiveIp != null) {
		return cellval + " (" +row.adaptiveIp + ")";
	}
	if(row.privateIp == null) {
		return "-";
	}
	return cellval;
};

var tenantInfoFormatter = function(cellValue,options,rowObject){
	return cellValue + '&nbsp;<input type="image" src="../../../../images/icon/icon_tenant.png" alt="Search" title="Tenant Info" onclick="tenantInfoPopupView.show(\''+cellValue+'\')">';
}

var storageFormatter = function(cellValue, options, rowObject) {
	if(rowObject.type != 'enterprise') {
		if(cellValue == 0) {
			return "-";
		} else {
			return cellValue;
		}
	} else {
		return cellValue;
	}
}

var getSizeToMB = function(cellval, options, row) {
	if(cellval < 1024) {
		return cellval + " MB";
	} else {
		return Math.round(cellval/1024) + " GB";
	}
}

var getSizeToGB = function(cellval, options, row) {
	return cellval + " GB";
}

var getToFixedOne = function (cellval, options, row) {
	return cellval.toFixed(1);
}

var getBooleanToYN = function(cellval, options, row) {
	if(cellval == '1' || cellval == 1 || cellval == 'true' || cellval == true) {
		// return 'Y';
		return i18n('w.yes');
	} else {
		// return 'N';
		return i18n('w.no');
	}
}
var getOpenStackPowerState = function(cellval, options, row) {
	if(cellval == 1) {
		return "Running";
	} else if(cellval == 3) {
		return "Paused";
	} else if(cellval == 4) {
		return "Shut Down";
	} else {
		return "No State";
	}
}

var getAttachmentsText = function (cellval, options, row) {
	var returnVal = "";
	if(cellval == null) return returnVal;
	for(var i=0; i<cellval.length; i++) {
		returnVal = returnVal + cellval[i]["serverName"] + ' 내의 ' + cellval[i]["device"] + '\n'
	}
	return returnVal;
}

var getIpAddressText = function(cellval, options, row) {
	var fixed = "";
	var floating = "";

	if(cellval == null) return "";

	for(var i=0; i<cellval.length; i++) {
		if(cellval[i]["type"] == "floating") {
			floating = floating + cellval[i]["addr"] + '\n';
		} else {
			fixed = fixed + cellval[i]["addr"] + '\n';
		}
	}

	if(floating.length == 0) {
		return fixed;
	}

	return fixed + "유동 IP:\n" + floating;
}

var getVisibilityZonesText = function(cellval, options, row) {
	var text = "";
	if(cellval == null) return text;
	for(var i=0; i<cellval.length; i++) {
		text = text + cellval[i] + '\n';
	}
	return text;
}

var getNeutronSubnetsText = function(cellval, options, row) {
	var text = "";
	if(cellval == null) return text;
	for(var i=0; i<cellval.length; i++) {
		text = text + cellval[i]['name'] + ' ' + cellval[i]['cidr'] + '\n';
	}
	return text;
}

var getNeutronSubnetsCidrText = function(cellval, options, row) {
	if(cellval == null) return "";
	if(cellval.length == 0) return "";

	var text = "(";
	for(var i=0; i<cellval.length; i++) {
		if(i == cellval.length - 1) {
			text = text + cellval[i]['cidr'] + ')';
		} else {
			text = text + cellval[i]['cidr'] + ',';
		}
	}

	return text;
}

var objToString = function (cellval, options, row) {
	var str = '';
	for (var p in cellval) {
		if (cellval.hasOwnProperty(p)) {
			str += p + ' : ' + cellval[p] + '\n ';
		}
	}
	return str;
}

var generateGUID = function () {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000)
			.toString(16)
			.substring(1);
	}
	return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}

$.fn.toNumberSVG = function(numbers, option) {
	option || (option = {});
	var options = {
		duration : 500,
		fixed: false,
		comma : false,
		unit: '',
		digits: 1
	};

	if(ValidationUtil.hasValue(option))$.extend(options, option);

	var numToSvg = function (num) {
		switch (num) {
			case "1":
			case 1:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M25.9,49.6H14.5V12.5L4,21.6V9.5l10.5-9.1h11.3V49.6z"/>\n'+
				'</svg>\n';
				break;
			case "2":
			case 2:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M33.4,49.6H1.8V39.4L19.6,20c1.7-1.8,2.5-3.5,2.5-5.2c0-3-1.5-4.6-4.4-4.6c-1.3,0-2.3,0.3-3,0.9c-1,0.8-1.5,2.1-1.5,3.9H1.8c0-4.7,1.6-8.4,4.7-11.1C9.4,1.3,13.2,0,17.7,0c4.6,0,8.4,1.3,11.4,4c2.9,2.7,4.4,6.3,4.4,10.8c0,2.4-0.5,4.6-1.5,6.4c-0.8,1.4-2.3,3.3-4.6,5.7L15.6,39.4h17.8V49.6z"/>\n'+
				'</svg>';
				break;
			case "3":
			case 3:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M34.1,34.9c0,4.9-1.6,8.7-4.9,11.4c-3,2.5-6.8,3.7-11.5,3.7c-4.6,0-8.3-1.2-11.3-3.5c-3.4-2.7-5.1-6.7-5.1-11.9h11.3c0,1.7,0.5,2.9,1.4,3.8c0.9,0.9,2.2,1.3,3.6,1.3c1.6,0,2.8-0.4,3.7-1.3c0.9-0.9,1.3-2.2,1.3-4c0-1.5-0.5-2.8-1.5-3.7c-1-1-2.2-1.5-3.8-1.5h-1.6v-9.8h1.6c1.6,0,2.7-0.5,3.5-1.4c0.8-0.9,1.2-2,1.2-3.2c0-1.5-0.4-2.7-1.3-3.5c-0.9-0.8-1.9-1.2-3.2-1.2c-1.2,0-2.3,0.4-3.2,1.2c-0.9,0.8-1.3,2-1.3,3.4H1.9c0-4.5,1.5-8.1,4.5-10.8c3-2.7,6.8-4,11.3-4c4.6,0,8.3,1.4,11.3,4.1c3,2.7,4.5,6.2,4.5,10.5c0,2.5-0.5,4.6-1.6,6.2c-0.8,1.3-2,2.4-3.5,3.4c1.7,1.2,3,2.5,3.9,3.8C33.5,29.9,34.1,32.2,34.1,34.9z"/>\n'+
				'</svg>';
				break;
			case "4":
			case 4:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M35,43.1h-3.7v6.5H20.5v-6.5H0.1V32.3L16.4,0.4h12.4L12.6,32.3h7.9v-6.5h10.8v6.5H35V43.1z"/>\n'+
				'</svg>';
				break;
			case "5":
			case 5:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M33.4,32.8c0,3.1-0.3,5.6-0.9,7.5c-0.6,1.9-1.8,3.7-3.6,5.5c-1.2,1.2-2.8,2.2-4.7,3c-1.9,0.8-4.2,1.2-6.7,1.2c-2.6,0-4.9-0.4-6.9-1.2c-2-0.8-3.6-1.8-4.8-3.1c-1.6-1.6-2.7-3.3-3.3-5.2c-0.6-1.9-0.9-3.8-0.9-5.9h11.3c0.2,1.7,0.6,3,1.4,3.9c0.7,0.9,1.8,1.3,3.2,1.3c1.3,0,2.4-0.4,3.3-1.3c0.9-0.9,1.3-2.8,1.3-5.6c0-2.6-0.4-4.4-1.2-5.4c-0.8-1-2-1.6-3.5-1.6c-1.5,0-2.6,0.4-3.4,1.2c-0.5,0.5-0.9,1.2-1.3,2.3H2.4V0.4h30v10.2H12.7v7.6c0.6-0.5,1.5-0.9,2.7-1.3c1.2-0.4,2.5-0.6,3.9-0.6c2,0,3.9,0.3,5.5,0.9c1.6,0.6,3,1.5,4,2.5c1.8,1.8,3.1,3.9,3.8,6.3C33.2,27.7,33.4,30,33.4,32.8z"/>\n'+
				'</svg>';
				break;
			case "6":
			case 6:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M33.9,34.2c0,5-1.6,9-4.8,11.8c-3,2.7-6.8,4-11.5,4c-4.6,0-8.4-1.3-11.5-4c-3.3-2.9-4.9-6.8-4.9-11.8c0-2.3,0.5-4.7,1.5-7.3c0.4-1.1,1.5-3.4,3.2-7l9.5-19.5h12.4L18,19.7c0.4-0.1,0.8-0.2,1.4-0.3c0.6,0,1-0.1,1.4-0.1c3.4,0,6.3,1.2,8.8,3.7C32.5,25.9,33.9,29.6,33.9,34.2z M22.6,34.2c0-1.7-0.5-3.1-1.4-4.1c-0.9-1-2.2-1.5-3.6-1.5c-1.4,0-2.6,0.5-3.6,1.5c-1,1-1.5,2.4-1.5,4.1c0,1.8,0.5,3.1,1.4,4.1c0.9,1,2.2,1.5,3.6,1.5c1.4,0,2.6-0.5,3.6-1.5C22.1,37.3,22.6,35.9,22.6,34.2z"/>\n'+
				'</svg>';
				break;
			case "7":
			case 7:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M34.5,10.6l-15.3,39H6.7l15.3-39H11.6v7.7H0.8v-18h33.7V10.6z"/>\n'+
				'</svg>';
				break;
			case "8":
			case 8:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M34.1,34.9c0,4.9-1.6,8.7-4.8,11.4c-2.9,2.5-6.8,3.7-11.7,3.7c-4.8,0-8.7-1.2-11.7-3.7c-3.2-2.7-4.8-6.5-4.8-11.4c0-2.6,0.7-4.9,2.1-7c0.8-1.1,1.9-2.3,3.5-3.6C5.2,23.2,4.1,22,3.4,21c-1.2-1.9-1.9-4-1.9-6.4c0-4.5,1.6-8.1,4.8-10.8C9.4,1.3,13.1,0,17.6,0c4.4,0,8.1,1.3,11.2,3.8c3.2,2.7,4.8,6.3,4.8,10.8c0,2.3-0.6,4.4-1.9,6.3c-0.8,1.2-1.8,2.3-3.2,3.5c1.6,1.3,2.7,2.5,3.5,3.5C33.4,29.9,34.1,32.2,34.1,34.9z M22.8,34.6c0-1.4-0.5-2.6-1.5-3.6c-1-1-2.2-1.5-3.7-1.5c-1.4,0-2.6,0.5-3.7,1.5c-1,1-1.5,2.2-1.5,3.6c0,1.4,0.5,2.6,1.5,3.6c1,1,2.2,1.5,3.7,1.5c1.4,0,2.6-0.5,3.7-1.5C22.3,37.3,22.8,36.1,22.8,34.6z M22.3,15c0-1.3-0.4-2.5-1.3-3.4c-0.9-0.9-2-1.4-3.3-1.4c-1.3,0-2.5,0.5-3.4,1.4c-0.9,0.9-1.3,2-1.3,3.4c0,1.3,0.4,2.5,1.3,3.4c0.9,0.9,2,1.4,3.4,1.4c1.3,0,2.5-0.5,3.3-1.4C21.8,17.4,22.3,16.3,22.3,15z"/>\n'+
				'</svg>';
				break;
			case "9":
			case 9:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M34.1,15.8c0,2.3-0.5,4.7-1.5,7.3c-0.4,1.1-1.5,3.4-3.2,7l-9.5,19.5H7.5l9.7-19.3c-0.4,0.1-0.8,0.2-1.4,0.3c-0.6,0-1,0.1-1.4,0.1c-3.4,0-6.3-1.2-8.8-3.7c-2.9-2.8-4.3-6.5-4.3-11.1c0-5,1.6-9,4.8-11.8c3-2.7,6.8-4,11.5-4c4.6,0,8.4,1.3,11.5,4C32.5,6.9,34.1,10.8,34.1,15.8z M22.8,15.8c0-1.7-0.5-3.1-1.4-4.1c-0.9-1-2.2-1.5-3.6-1.5c-1.4,0-2.6,0.5-3.6,1.5s-1.5,2.4-1.5,4.1c0,1.8,0.5,3.1,1.4,4.1c0.9,1,2.2,1.5,3.6,1.5c1.4,0,2.6-0.5,3.6-1.5C22.3,18.9,22.8,17.6,22.8,15.8z"/>\n'+
				'</svg>';
				break;
			case "0":
			case 0:
				return ''+
				'<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n'+
					'<path d="M33.4,34.7c0,4.8-1.6,8.7-4.8,11.5c-2.9,2.6-6.6,3.9-11,3.9c-4.4,0-8-1.3-11-3.9c-3.2-2.8-4.8-6.6-4.8-11.5V15.3c0-4.8,1.6-8.7,4.8-11.5C9.5,1.3,13.2,0,17.5,0c4.4,0,8,1.3,11,3.9c3.2,2.8,4.8,6.6,4.8,11.5V34.7z M22,34.7V15.3c0-1.7-0.4-2.9-1.2-3.8c-0.8-0.9-1.9-1.3-3.2-1.3c-1.3,0-2.4,0.4-3.2,1.3c-0.9,0.9-1.3,2.1-1.3,3.8v19.4c0,1.7,0.4,2.9,1.3,3.8c0.9,0.9,1.9,1.3,3.2,1.3c1.3,0,2.4-0.4,3.2-1.2C21.6,37.7,22,36.4,22,34.7z"/>\n'+
				'</svg>';
				break;
			case ",":
				return ''+
				'<svg class="comma" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 20 62" style="enable-background:new 0 0 20 62;" xml:space="preserve">\n'+
					'<path d="M16,53.2L3.9,62.1V37.7H16V53.2z"/>\n'+
				'</svg>';
				break;
			case ".":
				return ''+
				'<svg class="comma" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 20 62" style="enable-background:new 0 0 20 62;" xml:space="preserve">\n'+
					'<path d="M16.3,49.8H3.9V37.3h12.5V49.8z"/>\n'+
				'</svg>';
				break;
		}
	};

	var numberSvg = function (numbers, comma) {
		var numString = numbers.toString();

		if(comma) {
			numString = numString.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
		}
		var numberSvgs = [];
		for(var i=0; i<numString.length; i++) {
			numberSvgs.push(numToSvg(numString[i]));
		}
		return numberSvgs.join('');
	};

	$(this).prop('Counter',0).animate({
		Counter: numbers
	}, {
		duration: options.duration,
		easing: 'swing',
		step: function (now) {
			if(now.toString().indexOf(".") > -1 && options.fixed) {
				$(this).empty().append(numberSvg((now).toFixed(options.digits), options.comma));
			} else {
				$(this).empty().append(numberSvg(Math.ceil(now), options.comma));
			}
			if(ValidationUtil.hasValue(options.unit))$(this).append(options.unit);
		}
	});
};

var saveObjectInLocalStorage = function (storageItemName, object) {
	if (typeof window.localStorage !== 'undefined') {
		window.localStorage.setItem(storageItemName, JSON.stringify(object));
	}
},
removeObjectFromLocalStorage = function (storageItemName) {
	if (typeof window.localStorage !== 'undefined') {
		window.localStorage.removeItem(storageItemName);
	}
},
getObjectFromLocalStorage = function (storageItemName) {
	if (typeof window.localStorage !== 'undefined') {
		return $.parseJSON(window.localStorage.getItem(storageItemName));
	}
};

var saveSessionStorage = function (storageItemName, object) {
	if (typeof window.sessionStorage !== 'undefined') {
		window.sessionStorage.setItem(storageItemName, JSON.stringify(object));
	}
},
removeSessionStorage = function (storageItemName) {
	if (typeof window.sessionStorage !== 'undefined') {
		window.sessionStorage.removeItem(storageItemName);
	}
},
getSessionStorage = function (storageItemName) {
	if (typeof window.sessionStorage !== 'undefined') {
		return $.parseJSON(window.sessionStorage.getItem(storageItemName));
	}
};

//다국어
//removeObjectFromLocalStorage("message");
getMessageProperties = function (){
	if($.cookie("locale")) {
		var config = $.cookie("locale");
		if(config == locale) {
			if(getSessionStorage("message")) {
				$.i18n.map = getSessionStorage("message");
			}
			else {
				$.i18n.properties({
					name:'message',
					path:'/properties/',
					mode:'map',
					language:'',
					callback: function(){
						saveSessionStorage("message", $.i18n.map);
					}
				});

			}
		} else {
			var config = locale;
			$.cookie("locale", config, {path: '/'});

			$.i18n.properties({
				name:'message',
				path:'/properties/',
				mode:'map',
				language:'',
				callback: function(){
					saveSessionStorage("message", $.i18n.map);
				}
			});
		}
	} else {
		var config = locale;
		alert(locale);
		$.cookie("locale", config, {path: '/'});

		$.i18n.properties({
			name:'message',
			path:'/properties/',
			mode:'map',
			language:'',
			callback: function(){
				saveSessionStorage("message", $.i18n.map);
			}
		});
	}
};
getMessageProperties();

function showLoadingUI(isShow, msg, subMsg) {
	// 로딩 팝업 메세지 설정
	if(ValidationUtil.hasValue(msg)) {
		$("#loading_popup").find("span[name='msg']").html(msg);
	} else {
		$("#loading_popup").find("span[name='msg']").html(i18n('s.t.please-wait'));
	}
	if(ValidationUtil.hasValue(subMsg)) {
		$("#loading_popup").find("p[name='subMsg']").html(subMsg);
	} else {
		$("#loading_popup").find("p[name='subMsg']").html('');
	}

	// 로딩 팝업 활성화/비활성화 처리
	if(isShow) {
		$("#loading_popup").show();
	} else {
		$("#loading_popup").hide();
	}
}
function downloadTextFile(text, filename) {
	// create text file as object url
	var blob = new Blob([ text ], { "type" : "text/plain" });
	window.URL = window.URL || window.webkitURL;
	var fileUrl = window.URL.createObjectURL(blob);

	// provide text as downloaded file
	var a = $('<a style="display: none"></a>');
	a.attr("href", fileUrl);
	a.attr("download", filename);
	$(document.body).append(a);

	a[0].click();
	window.URL.revokeObjectURL(fileUrl);
	a.remove();
}

function i18n(msg) {
	var args = "\"" + msg + "\"";
	for (var i = 1; i < arguments.length; i++) {
		if(jQuery.i18n.map[arguments[i]] != null) {
			args += ", \"" + eval("jQuery.i18n.prop(" + "\"" + arguments[i] + "\")") + "\"";
		} else {
			args += ", \"" + arguments[i] + "\"";
		}
	}

	return eval("jQuery.i18n.prop(" + args + ")");
}