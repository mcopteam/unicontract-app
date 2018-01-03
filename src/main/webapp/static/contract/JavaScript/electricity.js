/**
 *  balanceQuery  查询用户余额请求
 */
function elebalanceQuery () {
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/electric/api",
        data: {
            "token": "futurever",
            "methodName":"queryAccountBalance"
        },
        // queryAccountBalance queryOutputNum queryRecords
        success: function(res) {
            console.log(res);
            var data = eval('(' + res.data + ')');
            console.log(data);
//          余额
            $('.electricity_contract_show_money').html(data.money);
        },
        error: function(error) {
            console.log(error);
        }
    });
}/**
 *  balanceQuery  查询电表余额请求
 */
function eleAmmeterQuery () {
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/electric/api",
        data: {
            "token": "futurever",
            "methodName":"queryAmmeterBalance"
        },
        // queryAccountBalance queryOutputNum queryRecords
        success: function(res) {
            var data = eval('(' + res.data + ')');
            console.log(data);
//          电表
            $('.electricity_contract_show_ele').html(data.money);
        },
        error: function(error) {
            console.log(error);
        }
    });
}
/**
 * 查询用户所有合约记录请求
 */
function elecontractQuery() {
    var electricityListTable = document.querySelector('.electricity_list_table');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/transferController/contractQueryAll",
        data: {
            "token": "futurever"
        },
        success: function(res) {
            if(res.data != null){
                $('.electricity_list_table').empty();
                var list = res.data;
                if(res.data==null){
                    var oTh = document.createElement('tr');
                    oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
                    electricityListTable.appendChild(oTh);
                }else if(list) {
                    var oTh = document.createElement('tr');
                    oTh.innerHTML = '<th>名称</th><th>创建时间</th><th>编号</th><th>执行时间</th><th>执行状态</th>';
                    electricityListTable.appendChild(oTh);
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
                        electricityListTable.appendChild(oT);
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
 * 获取所有交易记录请求
 */
function eletransferQuery() {
    var eleListLog = document.querySelector('.electricity_list_log');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/electric/api",
        data: {
            "token": "futurever",
            "methodName":"queryRecords"
        },
        // queryAccountBalance queryOutputNum queryRecords
        success: function(res) {
        	console.log(res)
            var data = eval('(' + res.data + ')');
            console.log(data);
        	if(data!=null){
                $('.electricity_list_log').empty();
                for(var i = 0; i < data.length; i++) {
                    var oP = document.createElement('p');
                    console.log(data)
                    oP.innerHTML = '<span class="glyphicon glyphicon-ok-circle"></span><span>转给【<b>' + data[i].To  + '</b>】<b>' + data[i].Money + '</b>元，完成</span>';
                    eleListLog.appendChild(oP);
                    var oSp = document.createElement('span');
                    if(i < data.length - 1) {
                        oSp.innerHTML = '&nbsp;|';
                        oSp.style.fontSize='30px';
                        eleListLog.appendChild(oSp);
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
 * 定时发送请求 获取余额 转账记录 合约信息
 */
function elesendQuery()
{
    elebalanceQuery();
    eleAmmeterQuery();
    elecontractQuery();
    eletransferQuery();
    console.log($('.electricity_body').css('display'));
    if($('.electricity_body').css('display')=='none'){
        clearInterval(timer);
    }

}
function go() {
    timer=setInterval(elesendQuery,5000);
}
/**
 * 转账设置
 */
function elecontract_sign(contractId,id) {
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
            var jsonContract = preSignData.contract;
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

$('#btn_low').click(function(){
	if($('.electricity_type_once').css('display')=='none'){
		$('.electricity_type_once').css('display','block');
	}else{
		$('.electricity_type_once').css('display','none');
	}
})

$('#electricity_select_btn').click(function(){
    var startDate = $('#electricity_start_data').val();
    var endData = $('#electricity_end_data').val();
    var lowMoney = $('#low_money').val();
    var autoMoney = $('#auto_money').val();
    var buySelect = $('#electricity_buy_select').val();
    var signSelect = $('#electricity_sign_select').find("option:selected").attr('pubkey');

    var contractId = "170623183817106296";
    console.log(startDate)
    console.log(endData)
    console.log(lowMoney)
    console.log(autoMoney)
    console.log(buySelect)
    console.log(signSelect)
    var contractId='170627144008249104';
    var id=133;
    elecontract_sign(contractId,id);
    elesendQuery();

})
// /**
//  * 定时器 任务方法
//  * Param: 任务方法
//  * Param: 定时时间
//  */
// setInterval("sendQuery()","10000");
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
