var oContract = document.querySelector('.contract');
var oPage4 = document.querySelector('.page4');
var oLog = document.querySelector('.log_con');
var role = sessionStorage.role;
//			审核合约查询渲染数据
var query = document.querySelector('#queryname');
var querybtn = document.querySelector('#querynamebtn');
$('.addMenu li:eq(0)').click(function(){
	this.className='cur';
	$('.addMenu li:eq(1)').removeClass();
	$('.page3').css('display','block');
	$('.page4').css('display','none');
})
$('.addMenu li:eq(1)').click(function(){
	this.className='cur';
	$('.addMenu li:eq(0)').removeClass();
	$('.page4').css('display','block');
	$('.page3').css('display','none');
})
querybtn.onclick = function() {
	var value = query.value;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/productContract/list",
		data: {
			"token": "futurever",
			"status": -1,
			"name": value
		},
		success: function(res) {
			contractTestSuccess(res,-1);
		},
		error: function(err) {
			console.log('error');
		}
	});
}
//				审核合约渲染方法
function contractTest(Status) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/productContract/list",
		data: {
			"token": "futurever",
			"status": Status,
			"pageSize": 100
		},
		success: function(res) {
			contractTestSuccess(res, Status);
		},
		error: function(err) {
			console.log('error');
		}
	});
}
//				审核合约渲染数据success方法
function contractTestSuccess(res, Status) {
	$('.contract').empty();
	var data = res.data;
	console.log(data)
	if(res.data==null){
		var oTh = document.createElement('tr');
		oTh.innerHTML = '<th>合约名称</th><th>合约人</th><th>创建时间</th><th>执行时间</th><th>操作状态</th><th></th><th>操作</th><th></th>'
		oContract.appendChild(oTh);
	}else if(data) {
		var oTh = document.createElement('tr');
		oTh.innerHTML = '<th>合约名称</th><th>合约人</th><th>创建时间</th><th>执行时间</th><th>操作状态</th><th></th><th>操作</th><th></th>'
		oContract.appendChild(oTh);
		var contract = res.data.list;
//		用户角色
		if(role == 6) {
			for(var i = 0; i < contract.length; i++) {
				var arr = ['', '未审批', '', '审批通过', '已发布'];
				var oT = document.createElement('tr');
				switch(contract[i].status) {
					case 1:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' +( contract[i].executeTime== null ? '---' : contract[i].executeTime) + '</td><td>' + arr[contract[i].status] + '</td><td></td><td><button type="button" class="btn btn-default " data-toggle="modal"  disabled="disabled"   >签约</button></td><td></td>';
						break;
					case 3:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' + (contract[i].executeTime== null ? '---' : contract[i].executeTime) + '</td><td>' + arr[contract[i].status] + '</td><td></td><td><button type="button" class="btn btn-default " data-toggle="modal"   disabled="disabled"   >签约</button></td><td></td>';
						break;
					case 4:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' + (contract[i].executeTime== null ? '---' : contract[i].executeTime) + '</td><td>' + arr[contract[i].status] + '</td><td></td><td><button type="button" class="btn btn-default " data-toggle="modal" >签约</button></td><td></td>';
						break;
					default:
						break;
				}
				oContract.appendChild(oT);
				(function(j) {
					var btn_sign = oT.children[6];
					var id = contract[j].id;
					var contractId = contract[j].contractId;
					//									签约按钮
					btn_sign.onclick = function() {
						var jsonContract = "";
						$.ajax({
							type: "POST",
							dataType: "json",
							async: false,
							url: server_url + "/productContract/preSign",
							data: {
								"token": "futurever",
								"contractId": contractId
							},
							success: function(res) {
								var preSignData = res.data;
								jsonContract = preSignData.contract;
								jsonContract = JSON.parse(jsonContract);
								var fullfiledContract = ContractUtils.field_fullfil(jsonContract, "Contract_Signature");
								var cmpPubkey = preSignData.publishPubkey
								var cmpPrikey = preSignData.publishPrikey;
								var currentUserPubkey = preSignData.currentUserPubkey;
								var currentUserPrikey = preSignData.currentUserPrikey;
								fullfiledContract = ContractUtils.addOwners(fullfiledContract, currentUserPubkey);
								ContractUtils.sign(fullfiledContract, cmpPubkey, cmpPrikey);
								ContractUtils.sign(fullfiledContract, currentUserPubkey, currentUserPrikey);
								ContractUtils.hash(fullfiledContract);
                                console.log("----------------- kk -------------------------------");
                                console.log(JSON.stringify(fullfiledContract));
								$.ajax({
									type: "POST",
									dataType: "json",
									async: false,
									url: server_url + "/productContract/signContract",
									data: {
										"token": "futurever",
										"id": id,
										"contract": JSON.stringify(fullfiledContract)
									},
									success: function(res) {
										alert(res.msg);
										var data = res.data;
									}
								});
							}
						});
					}
					oT.onclick = function() {
						var id = contract[j].id;
						var contractId = contract[j].contractId;
						console.log(id);
						//						审核合约内容
						product_content(contractId);
						//						审核合约记录
						product_log(id);

						$(this).addClass("s1").siblings().removeClass("s1");
					}
				})(i);
			}
		} else {
//			其他角色
			for(var i = 0; i < contract.length; i++) {
				var arr = ['', '未审批', '', '审批通过', '已发布'];
				var oT = document.createElement('tr');
				switch(contract[i].status) {
					case 1:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' +( contract[i].executeTime== null ? '---' : contract[i].executeTime)  + '</td><td>' + arr[contract[i].status] + '</td><td><button type="button" class="btn btn-default "  >同意</button></td><td><button type="button" class="btn btn-default " data-toggle="modal" data-target="#myModal1121" >建议修改</button></td><td><button type="button" class="btn btn-default ">发布</button></td>';
						break;
					case 3:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' +( contract[i].executeTime== null ? '---' : contract[i].executeTime)  + '</td><td>' + arr[contract[i].status] + '</td><td><button type="button" class="btn btn-default "  disabled="disabled" >同意</button></td><td><button type="button" class="btn btn-default " data-toggle="modal" data-target="#myModal1121"  disabled="disabled" >建议修改</button></td><td><button type="button" class="btn btn-default ">发布</button></td>';
						break;
					case 4:
						oT.innerHTML = '<td>' + contract[i].name + '</td><td>' + contract[i].createUserName + '</td><td>' + contract[i].createTime + '</td><td>' +( contract[i].executeTime== null ? '---' : contract[i].executeTime)  + '</td><td>' + arr[contract[i].status] + '</td><td><button type="button" class="btn btn-default "  disabled="disabled" >同意</button></td><td><button type="button" class="btn btn-default " data-toggle="modal" data-target="#myModal1121"  disabled="disabled" >建议修改</button></td><td><button type="button" class="btn btn-default " disabled="disabled" >发布</button></td>';
						break;
					default:
						break;
				}
				oContract.appendChild(oT);
				(function(j) {
					var oAgree = oT.children[5];
					var publish = oT.children[7];
					var suggest = oT.children[6];
					var id = contract[j].id;
					var contractId = contract[j].contractId;
					oAgree.onclick = function() {
						var status = contract[j].status;
						$.ajax({
							type: "POST",
							dataType: "json",
							url: server_url + "/productContract/operate",
							data: {
								"token": "futurever",
								"id": id,
								"auditOp": 3
							},
							success: function(res) {
								contractTest(Status);
							},
							error: function(err) {
								console.log(err);
							}
						});
					};
					//									发布按钮
					publish.onclick = function() {
                        publishContract(contractId, id, Status);
					}
					suggest.onclick = function() {
						var id = contract[j].id;
						var status = contract[j].status;
						layui.use('layedit', function() {
							var layedit = layui.layedit,
								$ = layui.jquery;
							var index = layedit.build('LAY_demo1');
							//编辑器外部操作
							var active = {
								content: function() {
									alert(layedit.getContent(index)); //获取编辑器内容
								},
								text: function() {
									var value = layedit.getText(index); //获取编辑器纯文本内容
									$.ajax({
										type: "POST",
										dataType: "json",
										url: server_url + "/productContract/operate",
										data: {
											"token": "futurever",
											"id": id,
											"auditOp": 2,
											"suggestion": value
										},
										success: function(res) {
											contractTest(Status);
										},
										error: function(err) {
											console.log(err);
										}
									});
								},
								selection: function() {
									alert(layedit.getSelection(index));
								}
							};
							$('#suggest_btn').on('click', function() {
								var type = $(this).data('type');
								active[type] ? active[type].call(this) : '';
							});
						});
					}
					oT.onclick = function() {
						var contractId = contract[j].contractId;
						var id = contract[j].id;
						console.log(id);
						//						审核合约内容
						product_content(contractId);
						//						审核合约记录
						product_log(id);
//						隔行变色
						$(this).addClass("s1").siblings().removeClass("s1");
					}
				})(i);
			}
		}
	}
}
//发布合约方法
function publishContract(contractId, id, Status) {
    var oC=document.createElement('div');
    oC.innerHTML='<h1>签名</h1><div class="btn_select"><span>选择签名:</span><select id="publishSelect"></select></div><div class="btn_b"><button type="button" class="btn btn-default">取消</button><button type="button" class="btn btn-primary" id="btn_agree">同意</button></div>'
    oC.setAttribute('class','box');
    document.body.appendChild(oC)
	var oSelect=oC.querySelector('select');
    var aBtn=oC.querySelector('.btn_b');
    var oBtn1=aBtn.children[0];
    var oBtn2=aBtn.children[1];
    oBtn1.onclick=function(){
        document.body.removeChild(oC);
    }
	console.log("%c------------1. 查询并选择签约甲方数据 start -------------", 'color:red;');
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		url: server_url + "/companyUser/list",
		data: {
			"token": "futurever"
		},
		success: function(res) {
			var companyUser = res.data;
			for(var i = 0; i < companyUser.length; i++) {
				var os = document.createElement('option');
				os.innerHTML = companyUser[i].name;
				os.setAttribute('pubkey', companyUser[i].pubkey);
				os.setAttribute('prikey', companyUser[i].prikey);
                oSelect.appendChild(os);
			}
		},
		error: function(err) {
			console.log(err);
		}
	});
	oBtn2.onclick=function () {
        var pubkey = $('#publishSelect').find("option:selected").attr('pubkey');
        var value = $('#publishSelect').find("option:selected").text();
        var jsonContract = "";
        $.ajax({
            type: "POST",
            dataType: "json",
            async: false,
            url: server_url + "/productContract/queryOriginContract",
            data: {
                "token": "futurever",
                "contractId": contractId
            },
            success: function(res) {
                jsonContract = res.data;
                jsonContract = JSON.parse(jsonContract);
                console.log("-----***********************************---");
                console.log(jsonContract);
                jsonContract['ContractBody']['ContractOwners'] = null;
                var fullfiledContract = ContractUtils.field_fullfil(jsonContract, "Contract_Create");
                fullfiledContract = ContractUtils.addOwners(fullfiledContract, pubkey);
                ContractUtils.hash(fullfiledContract);
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: server_url + "/productContract/publish",
                    data: {
                        "token": "futurever",
                        "id": id,
                        "auditOp": 4,
                        "contract": JSON.stringify(fullfiledContract),
                        "publishOwner": pubkey

                    },
                    success: function(res) {
                        alert(res.msg);
                        contractTest(Status);
                    },
                    error: function(err) {
                        console.log(err);
                    }
                });
            },
            error: function(err) {
                console.log(err);
            }
        });
        document.body.removeChild(oC);
    }
}
//合约产品审核记录渲染方法
function product_log(id) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/productContract/queryAuditLog",
		data: {
			"token": "futurever",
			"id": id
		},
		success: function(res) {
			var log = res.data;
			$('.page4').empty();
			if(log) {
				console.log(log);
				for(var i = 0; i < log.length; i++) {
					var oP = document.createElement('p');
					oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>' + log[i].createTime + '，<b>' + log[i].createUserName + '</b>' + log[i].description+'</span>';
					oPage4.appendChild(oP);
					var oSp = document.createElement('span');
					if(i < log.length - 1) {
						oSp.innerHTML = '&nbsp;|';
                        oSp.style.fontSize='30px';
						oLog.appendChild(oSp);
						oPage4.appendChild(oSp);
					}
				}
			}
		},
		error: function(err) {
			console.log('error');
		}
	});
}
//合约产品内容渲染方法
function product_content(contractId) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/productContract/queryContent",
		data: {
			"token": "futurever",
			"contractId": contractId
		},
		success: function(res) {
			$('.page3 .con_des_mains').empty();
			$('.page3 .con_main  .owner').empty();
			$('.page3 .con_footer .con_footer_con').empty();
			var data = res.data;
			var data = eval('(' + data + ')');
			if(data) {
				var data = data.ContractBody;
				console.log(data);
				$('.page3 .title p').html(data.Caption);
				$('.page3 .con_main  .l p span').html(data.ContractId);
				var ContractOwners=data.ContractOwners;
				if(ContractOwners){
					for(var i=0;i<ContractOwners.length;i++){
						var oP=document.createElement('p');
						oP.innerHTML='合约主体'+(i+1)+'：'+ContractOwners[i];
						$('.page3 .con_main  .owner').append(oP)
					}
				}
				var CreateTime = laydate.now(data.CreateTime, 'YYYY-MM-DD hh:mm:ss');
				var EndTime = laydate.now(data.EndTime, 'YYYY-MM-DD hh:mm:ss');
				var StartTime = laydate.now(data.StartTime, 'YYYY-MM-DD hh:mm:ss');
				$('.page3 .con_time  .l p span').html(CreateTime);
				$('.page3 .con_time  .r .con_time_statr').html(StartTime);
				$('.page3 .con_time  .r .con_time_end').html(EndTime);
				if(data.ContractAssets){
					 var des = data.ContractAssets[0];
					 $('.page3 .con_des  .con_des_name').html(des.Caption);
					 $('.page3 .con_des  .con_des_num').html(des.Amount + des.Unit);
					 $('.page3 .con_des  .con_des_type').html(des.Description);
				}
				if(data.ContractComponents){
					for(var i = 0; i < data.ContractComponents.length; i++) {
						var oP = document.createElement('p');
						oP.innerHTML = (i + 1)+'.' + data.ContractComponents[i].Description;
						$('.page3 .con_des_mains').append(oP);
					}
				}
				var ContractSignatures=data.ContractSignatures;
				if(ContractSignatures){
					for(var i=0;i<ContractSignatures.length;i++){
						var oP=document.createElement('p');
						oP.innerHTML='签名'+(i+1)+'：'+ContractSignatures[i].Signature;
						$('.page3 .con_footer .con_footer_con').append(oP)
					}
				}
			}
		},
		error: function(err) {
			console.log(err);
		}
	});
}