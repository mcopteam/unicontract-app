/**
 *  balanceQuery  查询用户余额请求
 */
function balanceQuery () {
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/transferController/balanceQuery",
        data: {
            "token": "futurever"
        },
        success: function(res) {
            if(res.data != null){
                var data = res.data;
                $('.transfer_contract_show_money').html(data.amount);
            }
        },
        error: function(error) {
            console.log(error);
        }
    });
}

/**
 * 查询用户所有合约记录请求
 */
function contractQuery() {
    var transferListTable = document.querySelector('.transfer_list_table');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/transferController/contractQueryAll",
        data: {
            "token": "futurever"
        },
        success: function(res) {
            if(res.data != null){
                $('.transfer_list_table').empty();
                var list = res.data;
                if(res.data==null){
                    var oTh = document.createElement('tr');
                    oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
                    transferListTable.appendChild(oTh);
                }else if(list) {
                    var oTh = document.createElement('tr');
                    oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
                    transferListTable.appendChild(oTh);
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
                        oT.innerHTML = '<td>' + list[i].name + '</td><td>' + list[i].createTime + '</td><td>' + list[i].contractId + '</td><td>' + list[i].start + '</td><td>' + status + '</td>';
                        transferListTable.appendChild(oT);
                    }
                }
            }
        },
        error: function(error) {
            console.log(error);
        }
    });
}

/**
 * 获取所有交易转账记录请求
 */
function transferQuery() {
    var listLog = document.querySelector('.list_log');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/transferController/transferQuery",
        data: {
            "token": "futurever"
        },
        success: function(res) {
            if(res.data != null){

                $('.list_log').empty();
                var data = res.data;
                for(var i = 0; i < data.length; i++) {
                    var oP = document.createElement('p');
                    console.log(data)
                    switch(data[i].operation) {
                        case "CREATE":
                            oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>为【' + data[i].owners_after  + '】创建资产'+data[i].amount+'元</span>';
                            break;
                        case "TRANSFER":
                            if(data[i].owners_after==data[i].owner_before){
                                oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>冻结【' + data[i].owners_after  + '】' + data[i].amount + '元</span>';
                            }else{
                                oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>转给【' + data[i].owners_after  + '】' + data[i].amount + '元，完成</span>';
                            }
                            break;
                        default:
                            break;
                    }
                    listLog.appendChild(oP);
                    var oSp = document.createElement('span');
                    if(i < data.length - 1) {
                        oSp.innerHTML = '&nbsp;|';
                        oSp.style.fontSize='30px';
                        listLog.appendChild(oSp);
                    }
                }
            }

        	console.log(res)
        },
        error: function(error) {
            console.log(error);
        }
    });
}

/**
 * 定时发送请求 获取余额 转账记录 合约信息
 */
function sendQuery()
{
    transferQuery();
    balanceQuery();
    contractQuery();
}

/**
 * 转账设置
 */
function contract_sign(contractId,transferTo,transferDate,transferAmount) {
    console.log("%c------------1. 查询要签约的json数据 end -------------", 'color:red;');
    var jsonContract = "";
    $.ajax({
        type: "POST",
        dataType: "json",
        async: false,
        url: server_url + "/transferController/contractQuerySingleMysql",
        data: {
            "token": "futurever",
            "contractId": contractId
        },
        success: function (res) {
            var data = res.data;
            var contractId = data.contractId;
            var preSignData = data.contractData;
            jsonContract = preSignData.contract;
            jsonContract = JSON.parse(jsonContract);
            var fullfiledContract = ContractUtils.field_fullfil(jsonContract, "Contract_Signature");
            var currentUserPubkey = preSignData.currentUserPubkey;
            var currentUserPrikey = preSignData.currentUserPrikey;
            var transferTimestamp = Date.parse(new Date(transferDate));
            fullfiledContract.ContractBody.ContractSignatures = null;
            fullfiledContract.ContractBody.ContractOwners = null;
            fullfiledContract.ContractBody.ContractId = contractId;
            fullfiledContract.ContractBody.MetaAttribute.TransferTo = transferTo;
            fullfiledContract.ContractBody.MetaAttribute.TransferDate = transferTimestamp+"";
            fullfiledContract.ContractBody.MetaAttribute.TransferAmount = transferAmount;
            fullfiledContract = ContractUtils.addOwners(fullfiledContract, currentUserPubkey);
            ContractUtils.sign(fullfiledContract, currentUserPubkey, currentUserPrikey);
            ContractUtils.hash(fullfiledContract);
            $.ajax({
                type: "POST",
                dataType: "json",
                async: false,
                url: server_url + "/transferController/transferSetting",
                data: {
                    "token": "futurever",
                    "contract":JSON.stringify(fullfiledContract)
                },
                success: function (res) {
                    alert(res.msg);
                    var data = res.data;
                    sendQuery();
                }

            });
        }
    });
}

$('.transfer_body #btn_once').click(function(){
	if($('.transfer_type_once').css('display')=='none'){
		$('.transfer_type_once').css('display','block');
	}else{
		$('.transfer_type_once').css('display','none');
	}
})
$('#transfer_select_btn').click(function(){
    var owerValue = $('#electricity_receive_select').find("option:selected").attr('pubkey');
    var transferData = $('#transfer_data').val();
    var transferAmount = $('#transfer_amount').val();
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/transferController/queryLatelyContract",
        data: {
            "token": "futurever"
        },
        success: function(res) {
            console.log(res);
            if(res.data != null){
                var contractId = res.data.contractId;
                console.log(transferData);
                console.log(transferAmount);
                console.log(owerValue);
                contract_sign(contractId,owerValue,transferData,transferAmount);
                sendQuery();
            }
        },
        error: function(error) {
            console.log(error);
        }
    });

})
// /**
//  * 定时器 任务方法
//  * Param: 任务方法
//  * Param: 定时时间
//  */
setInterval("sendQuery()","5000");
//收款人渲染数据
//$('#receive_select').empty();
//var slelect = res.slelect;
//for(var i = 0; i < slelect.length; i++) {
//	var os = document.createElement('option');
//	os.innerHTML = slelect[i].name;
//	os.setAttribute('pubkey', slelect[i].pubkey);
//	os.setAttribute('prikey', slelect[i].prikey);
//	$('#receive_select').append(os);
//}