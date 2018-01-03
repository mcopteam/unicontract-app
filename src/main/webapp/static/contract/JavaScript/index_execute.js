var oAll = document.querySelector('.all_list');
//执行日志 内容+记录 显示按钮
var oExe = document.querySelector('#execute');
var oPage = document.querySelector('.page');
var oPage2 = document.querySelector('.page2');
oExe.onclick = function() {
	if(this.value == '执行日志') {
		this.value = '合约内容';
		oPage2.style.display = 'block';
		oPage.style.display = 'none';
	} else {
		this.value = '执行日志';
		oPage2.style.display = 'none';
		oPage.style.display = 'block';
	}
}
//执行合约按钮查询
var querybtn = document.querySelector('#querykeybtn');
querybtn.onclick = function() {
	var value = $('#querykey').val();
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/executeContract/queryAll",
		data: {
			"token": "futurever"
		},
		success: function(res) {
			contract_execute_success(res);
		},
		error: function(err) {
			console.log('error');
		}
	});
}

function contract_execute() {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/executeContract/queryAll",
		data: {
			"token": "futurever",
		},
		success: function(res) {
            console.log("-----------------contract_execute---------------------------")
			console.log(res)
			contract_execute_success(res)
		},
		error: function(err) {
			console.log('error');
		}
	});
}
//执行日志交互成功方法
function contract_execute_success(res) {
	$('.all_list').empty()
	var list = res.data;
	console.log("-----------------list---------------------------")
	console.log(list)
	if(res.data==null){
		var oTh = document.createElement('tr');
		oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
		oAll.appendChild(oTh);
	}else if(list) {
        console.log("-----------------list22---------------------------")
		console.log(list);
		var oTh = document.createElement('tr');
		oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
		oAll.appendChild(oTh);
		for(var i = 0; i < list.length; i++) {
			if(list[i].status == 'Contract_Unknown' || list[i].status == 'Contract_Create' || list[i].status == 'Contract_Signature') {
				status = '未执行';
			} else if(list[i].status == 'Contract_In_Process') {
				status = '正在执行';
			} else if(list[i].status == 'Contract_Completed') {
				status = '已完成';
			} else if(list[i].status == 'Contract_Discarded') {
				status = '终止';
			}
			var arr = ['未执行', '正在执行', '终止']
			var oT = document.createElement('tr');
			oT.innerHTML = '<td>' + list[i].contractName + '</td><td>' + list[i].createTime + '</td><td>' + list[i].contractId + '</td><td>' + list[i].start + '</td><td>' + status + '</td>';
			(function(j) {
				oT.onclick = function() {
					$(this).addClass("s1").siblings().removeClass("s1");
					var contractId = list[j].contractId;
					var status = list[j].status;
					//							合约内容
					execute_content(contractId);
					//							执行日志
					execute_log(contractId);
				}
			})(i);
			oAll.appendChild(oT);
		}
	}
}
//合约日志方法
function execute_log(id) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/executeContract/queryLog",
		data: {
			"token": "futurever",
			"contractId": id
		},
		success: function(res) {
			$('.dd1').empty();
			$('.log_con').empty();
			var log = res.data;
			console.log(log)
			if(log) {
				for(var i = 0; i < log.length; i++) {
					var oP = document.createElement('p');
					// switch(log[i].state) {
					// 	case "TaskState_Dormant":
					// 		oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].cname + '</b>' + log[i].description+'</span>';
					// 		break;
					// 	case "TaskState_In_Progress":
					// 		oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].cname + '</b>' + log[i].description+'</span>';
					// 		break;
					// 	case "TaskState_Completed":
					// 		oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].cname + '</b>' + log[i].description+'</span>';
					// 		break;
					// 	case "TaskState_Discard":
					// 		oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].cname + '</b>' + log[i].description+'</span>';
					// 		break;
					// 	default:
					// 		break;
					// }

                    oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].cname + '</b>' + log[i].description+'</span>';
					var oT = document.createElement('tr');
					oT.innerHTML = '<td>' + log[i].caption + '</td><td>' + log[i].createTime + '</td><td>' + log[i].cname + '</td><td>' + log[i].description + '</td>';
					$('.dd1').append(oT);
					oLog.appendChild(oP);
					var oSp = document.createElement('span');
					if(i < log.length - 1) {
						oSp.innerHTML = '&nbsp;|';
						oSp.style.fontSize='30px';
						oLog.appendChild(oSp);
					}
				}
			}
		},
		error: function(err) {
			console.log('error');
		}
	});
}
//合约内容方法
function execute_content(id) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/productContract/queryContent",
		data: {
			"token": "futurever",
			"contractId": id
		},
		success: function(res) {
			$('.page .con_des_mains').empty();
			$('.page .con_main  .owner').empty();
			$('.page .con_footer .con_footer_con').empty();
			var data = res.data;
			var data = eval('(' + data + ')');
			if(data) {
				var data = data.ContractBody;
				console.log(data);
				$('.page .title p').html(data.Caption);
				$('.page .con_main  .l p span').html(data.ContractId);
				var ContractOwners = data.ContractOwners;
				if(ContractOwners) {
					for(var i = 0; i < ContractOwners.length; i++) {
						var oP = document.createElement('p');
						oP.innerHTML='合约主体'+(i+1)+'：'+ContractOwners[i];
						$('.page .con_main  .owner').append(oP)
					}
				}
				var CreateTime = laydate.now(data.CreateTime, 'YYYY-MM-DD hh:mm:ss');
				var EndTime = laydate.now(data.EndTime, 'YYYY-MM-DD hh:mm:ss');
				var StartTime = laydate.now(data.StartTime, 'YYYY-MM-DD hh:mm:ss');
				$('.page .con_time  .l p span').html(CreateTime);
				$('.page .con_time  .r .con_time_statr').html(StartTime);
				$('.page .con_time  .r .con_time_end').html(EndTime);
				if(data.ContractAssets){
					var des = data.ContractAssets[0];
					$('.page .con_des  .con_des_name').html(des.Caption);
					$('.page .con_des  .con_des_num').html(des.Amount + des.Unit);
					$('.page .con_des  .con_des_type').html(des.Description);
				}
				if(data.ContractComponents){
					for(var i = 0; i < data.ContractComponents.length; i++) {
						var oP = document.createElement('p');
						oP.innerHTML = (i + 1)+'.' + data.ContractComponents[i].Description;
						$('.page .con_des_mains').append(oP);
					}
				}
				var ContractSignatures = data.ContractSignatures;
				if(ContractSignatures) {
					for(var i = 0; i < ContractSignatures.length; i++) {
						var oP = document.createElement('p');
						oP.innerHTML='签名'+(i+1)+'：'+ContractSignatures[i].Signature;
						$('.page .con_footer .con_footer_con').append(oP)
					}
				}
			}
		},
		error: function(err) {
			console.log(err);
		}
	});
}