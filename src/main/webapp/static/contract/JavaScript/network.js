/**
 *  balanceQuery  查询用户余额请求
 */
function runTimeQuery () {
    var balanceSpan = document.querySelector('.balance_span');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/intelligentNetwork/runTimeQuery",
        data: {
            "token": "futurever",
            "contranctId":""
        },
        success: function(res) {
            console.log(res);
            $('.balance_span').empty();
            var data = res.data;
            for(var i = 0; i < data.length; i++) {
                var oT = document.createElement('span');
                if (i % 2 == 1) {
                    oT.style.backgroundColor = '#f2f2f2';
                }
                oT.innerHTML = '<span>' + data[i].amount + '</span>';
                balanceSpan.appendChild(oT);
            }
        },
        error: function(error) {
            console.log(error);
        }
    });
}

/**
 * 查询用户所有合约请求
 */
function totalTransactionQuery() {
    var contractTable1 = document.querySelector('.contract_table1');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/intelligentNetwork/transactionNumQuery",
        data: {
            "token": "futurever",
            "contranctId":""
        },
        success: function(res) {
            $('.contract_table1').empty();
            var data = res.data;
            for(var i = 0; i < data.length; i++) {
                var oT = document.createElement('tr');
                if (i % 2 == 1) {
                    oT.style.backgroundColor = '#f2f2f2';
                }
                var contract_status=['未执行','正在执行','终止'];
                switch (data[i].status){
                    case 0:
                        oT.innerHTML = '<td>' + (i+1) + '</td><td>' + data[i].name + '</td><td>' + data[i].createTime + '</td><td>' + data[i].executeTime + '</td><td>' + contract_status[data[i].status] + '</td><td>' + data[i].status + '</td><td>' + data[i].caption + '</td>';
                        break;
                    case 1:
                        oT.innerHTML = '<td>' + (i+1) + '</td><td>' + data[i].name + '</td><td>' + data[i].createTime + '</td><td>' + data[i].executeTime + '</td><td>' + contract_status[data[i].status] + '</td><td>' + data[i].status + '</td><td>' + data[i].caption + '</td>';
                        break;
                    case 2:
                        oT.innerHTML = '<td>' + (i+1) + '</td><td>' + data[i].name + '</td><td>' + data[i].createTime + '</td><td>' + data[i].executeTime + '</td><td>' + contract_status[data[i].status] + '</td><td>' + data[i].status + '</td><td>' + data[i].caption + '</td>';
                        break;
                    default:
                        break;
                }
                contractTable1.appendChild(oT);
            }
        },
        error: function(error) {
            console.log(error);
        }
    });
}

/**
 * 获取所有交易请求
 */
function transactionAllQuery() {
    var transferTable1 = document.querySelector('.transfer_table1');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: server_url + "/intelligentNetwork/transactionAllQuery",
        data: {
            "token": "futurever",
            "publicKey":""
        },
        success: function(res) {
            $('.transfer_table1').empty();
            var data = res.data;
            for(var i = 0; i < data.length; i++) {
                var oT = document.createElement('tr');
                if (i % 2 == 1) {
                    oT.style.backgroundColor = '#f2f2f2';
                }
                oT.innerHTML = '<td>' + (i+1) + '</td><td>' + data[i].owners_after + '</td><td>' + data[i].amount + '</td><td>' + data[i].timestamp + '</td>';
                transferTable1.appendChild(oT);
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
function sendQuery()
{
    transferQuery();
    balanceQuery();
    contractQuery();
}

/**
 *  合约1 转账设置
 */
function contract1(){
    var contract_example = document.querySelector('.contract_example');
    $('.contract_example').empty();
    var oT = document.createElement('div');
    oT.innerHTML = '<span>收款人:&nbsp;</span><input type="text" class="transferTo" ><br /><span>转账日期:</span><input type="text" class="transferDate" ><br /><span>转账金额:</span><input type="text" class="transferAmount" ><br />';
    contract_example.appendChild(oT);
}

/**
 *  合约2 转账设置
 *  备注：静态展示
 */
function contract2(){
    var contract_example = document.querySelector('.contract_example');
    $('.contract_example').empty();
    var oT = document.createElement('div');
    oT.innerHTML = '<span>收款人:&nbsp;</span><input type="text" class="transferTo" ><br /><span>转账金额:</span><input type="text" class="transferDate" ><br />';
    contract_example.appendChild(oT);
}

/**
 *  合约3 转账设置
 *  备注：静态展示
 */
function contract3(){
    var contract_example = document.querySelector('.contract_example');
    $('.contract_example').empty();
    var oT = document.createElement('div');
    oT.innerHTML = '<span>收款人:&nbsp;</span><input type="text" class="transferTo" ><br /><span>转账日期:</span><input type="text" class="transferDate" ><br />';
    contract_example.appendChild(oT);
}

/**
 *  转账执行
 */
function execut_function(){
    var transferTo = document.querySelector(".transferTo").value;
    //2018-07-10 10:21:12
    var transferDate = document.querySelector(".transferDate").value;
    var transferAmount = document.querySelector(".transferAmount").value;
    var contractId = "293859302396";

    contract_sign(contractId,transferTo,transferDate,transferAmount)

    /**
     * 定时器 任务方法
     * Param: 任务方法
     * Param: 定时时间
     */
    setInterval("sendQuery()","10000");
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
        url: server_url + "/intelligentNetwork/contractQuerySingle",
        data: {
            "token": "futurever",
            "contractId": contractId
        },
        success: function (res) {
            var data = res.data;
            var contractId = data.contractId;

            var preSignData = data.contractData;
            console.log(res, 'color:red;');
            jsonContract = preSignData.contract;
            jsonContract = JSON.parse(jsonContract);

            var fullfiledContract = ContractUtils.field_fullfil(jsonContract, "Contract_Signature");
            var currentUserPubkey = preSignData.currentUserPubkey;
            var currentUserPrikey = preSignData.currentUserPrikey;
            var transferTimestamp = Date.parse(new Date(transferDate));

            jsonContract.ContractBody.ContractSignatures = null;
            jsonContract.ContractBody.ContractOwners = null;
            jsonContract.ContractBody.ContractId = contractId;
            jsonContract.ContractBody.MetaAttribute.TransferTo = transferTo;
            jsonContract.ContractBody.MetaAttribute.TransferDate = transferTimestamp;
            jsonContract.ContractBody.MetaAttribute.TransferAmount = transferAmount;
            console.log(jsonContract.ContractBody.MetaAttribute.TransferTo)
            console.log(jsonContract.ContractBody.MetaAttribute.TransferDate)
            console.log(jsonContract.ContractBody.MetaAttribute.TransferAmount)

            console.log("preSignData---:\n" + preSignData);
            console.log(fullfiledContract);
            console.log("%c------------2. 附加当前用户数据 -------------", 'color:red;');
            fullfiledContract = ContractUtils.addOwners(fullfiledContract, currentUserPubkey);
            console.log("%c------------3. sign and hash -------------", 'color:red;');
            ContractUtils.sign(fullfiledContract, currentUserPubkey, currentUserPrikey);
            ContractUtils.hash(fullfiledContract);

            console.log("%c------------3. publish -------------", 'color:red;');
            console.log(fullfiledContract);
            console.log(JSON.stringify(fullfiledContract));
            $.ajax({
                type: "POST",
                dataType: "json",
                async: false,
                url: server_url + "/intelligentNetwork/paramSetting",
                data: {
                    "token": "futurever",
                    "contract":JSON.stringify(fullfiledContract)
                },
                success: function (res) {
                    alert(res.msg);
                    var data = res.data;
                }
            });
        }
    });
}