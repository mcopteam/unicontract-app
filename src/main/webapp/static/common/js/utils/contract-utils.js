(function (ContractUtils) {
    'use strict';

//dependency the moment.js, EncryptUtils and JsonUtils


    if (!Array.indexOf) {
        Array.prototype.indexOf = function (obj) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] == obj) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * array contains the element
     * @param {Object} array
     * @param {Object} element
     */
    var contains = function (array, element) {
        if (array instanceof Array) {
            return array.indexOf(element) >= 0;
        } else {
            throw new TypeError("array must be Array!");
        }
    };

    /**
     * generate the signature with EncryptUtils.sign
     * @param {Object} contract
     * @param {Object} publicKey
     * @param {Object} privateKey
     */
    var generateSignature = function (contract, publicKey, privateKey) {
        contract['ContractBody']['ContractSignatures'] = null;
        var contractStr = JSON.stringify(JsonUtils.sortKeys(contract)['ContractBody']);
        return {
            "OwnerPubkey": publicKey,
            "Signature": EncryptUtils.sign(privateKey, contractStr),
            "SignTimestamp": moment().format("x") //Unix Millisecond Timestamp
        }
    }

    /**
     * 验证ownerKey及签名publicKey是否合法
     * @param {Object} contract
     */
    ContractUtils.verifySignature = function (contract) {
        // 验证owner
        var owners = contract['ContractBody']['ContractOwners'];
        owners = (owners == undefined) ? [] : owners;
        if (owners.length <= 0) {
            return false;
        }

        // 验证签名中的publicKey 是否与 owner中的一致
        var contractSignatures = contract['ContractBody']['ContractSignatures'];
        contractSignatures = (contractSignatures == undefined) ? [] : contractSignatures;
        console.log(contractSignatures);
        for (var i = 0; i < contractSignatures.length; i++) {
            var contractSignature = contractSignatures[i];
            if (!contains(owners, contractSignature.OwnerPubkey)) {
                return false;
            }
        }
        //todo verify the signature is ok!
        return true;
    }

    /**
     * owner 添加
     * @param contract
     * @param owners
     * @returns {*}
     */
    ContractUtils.addOwners = function (contract, owners) {
        if (owners == undefined || owners == null) {
            return contract;
        }
        var ownersOld = contract['ContractBody']['ContractOwners'];
        ownersOld = (ownersOld == undefined) ? [] : ownersOld;
        if (owners instanceof Array) {
            for(var i=0; i< owners.length; i++){
                if(contains(ownersOld, owners[i])){
                    continue;
                }
                ownersOld.push(owners[i]);
            }
        } else {
            if(!contains(ownersOld, owners)){
                ownersOld.push(owners);
            }
        }
        contract['ContractBody']['ContractOwners'] = ownersOld;
        return contract;
    }

    ContractUtils.sign = function (contract, publicKey, privateKey) {
        if (typeof(contract) != "object") {
            throw new TypeError("contract must be json obj!");
        }

        if (publicKey == undefined || publicKey == null || publicKey == "") {
            throw new Error("publicKey is error!");
        }

        if (privateKey == undefined || privateKey == null || privateKey == "") {
            throw new Error("privateKey is error!");
        }

        var owners = contract['ContractBody']['ContractOwners'];
        owners = (owners == undefined) ? [] : owners;
        if (owners.length <= 0) {
            throw new Error("owners is blank!");
        }

        if (!contains(owners, publicKey)) {
            throw new Error("publicKey is not in the owners!");
        }

        var verifySignatureOld = ContractUtils.verifySignature(contract);
        if (!verifySignatureOld) {
            throw new Error("signatures info error!");
        }

        // index by publicKey
        var tempContractSignatures = [];
        var oldContractSignatures = contract['ContractBody']['ContractSignatures'];
        oldContractSignatures = (oldContractSignatures == undefined) ? [] : oldContractSignatures;

        //已签名的直接返回当前传入的contract
        var hasSigned = false;
        for (var i = 0; i < oldContractSignatures.length; i++) {
            var contractSignature = oldContractSignatures[i];
            if (contractSignature.OwnerPubkey === publicKey) {
                hasSigned = true;
                break;
            }
            tempContractSignatures[contractSignature.OwnerPubkey] = oldContractSignatures[i];
        }
        if (hasSigned) {
            console.log("%ccontractSignature for " + owners[i] + " has signed!", 'color:purple;', contractSignature);
            return contract;
        }
        console.log("current sign for publicKey", publicKey, "with privateKey", privateKey)
        // 按照ownerkey排序签名
        var contractSignatures = [];
        for (var i = 0; i < owners.length; i++) {
            var contractSignature = tempContractSignatures[owners[i]];
            if (contractSignature != undefined) {
                console.log("%ccontractSignature for " + owners[i] + " exist!", 'color:red;', contractSignature);
            } else if (owners[i] === publicKey) {
                contractSignature = generateSignature(contract, publicKey, privateKey);
                console.log("%ccontractSignature for " + owners[i] + " not exist and had generated!", 'color:green;', contractSignature);
            } else {
                console.log("%ccontractSignature for " + owners[i] + " not exist and wait sign!", 'color:black;', contractSignature);
                continue;
            }
            contractSignatures.push(contractSignature);
        }
        contract['ContractBody']['ContractSignatures'] = contractSignatures;
//	console.log("%c-----------------  sign ContractSignatures -------------------------", 'color:red;');
        return JsonUtils.sortKeys(contract);
    };

    /**
     * hash by sha3-256 for sortKeysContract
     * @param {Object} sortKeysContract
     */
    ContractUtils.generateHash = function (contract) {
        contract = JsonUtils.sortKeys(contract);
        var contractStr = JSON.stringify(contract['ContractBody']);
        var shaObj = new jsSHA("SHA3-256", "TEXT");
        shaObj.update(contractStr);
        return shaObj.getHash("HEX");
    }

    /**
     * generate the contract with id
     * @param contract
     * @returns {*}
     */
    ContractUtils.hash = function (contract) {
        var hashId = ContractUtils.generateHash(contract);
        contract["id"] = hashId;
        return contract;
    }

    var contract_lost_message_deal = function(componentObjArr, key, name){
        if(componentObjArr == undefined || componentObjArr == null){
            componentObjArr = null;
        }else{
            for(var j=0; j<componentObjArr.length; j++){
                var keyObj = componentObjArr[j][key];
                if(keyObj == undefined || keyObj == null){
                    componentObjArr[j][key] = null;
                }
            }
        }
        return componentObjArr;
    };

    ContractUtils.field_fullfil = function(contract, contractState){
        var contractBody = contract['ContractBody'];
        if(contractBody == undefined || contractBody == null){
            throw new Error("contractBody is blank!");
        }
        var contractComponents = contractBody['ContractComponents'];
        for(var i=0; i<contractComponents.length; i++){
            var contractComponent = contractComponents[i];
            var preConditions = contractComponent["PreCondition"];
            preConditions = contract_lost_message_deal(preConditions, "ExpressionResult", "PreCondition");
            var completeConditions  = contractComponent["CompleteCondition"];
            completeConditions = contract_lost_message_deal(completeConditions, "ExpressionResult", "CompleteCondition");
            var discardConditions  = contractComponent["DiscardCondition"];
            discardConditions = contract_lost_message_deal(discardConditions, "ExpressionResult", "DiscardCondition");
            var dataValueSetterExpressionLists  = contractComponent["DataValueSetterExpressionList"];
            dataValueSetterExpressionLists = contract_lost_message_deal(dataValueSetterExpressionLists,"ExpressionResult", "DataValueSetterExpressionList");
            var dataLists = contractComponent["DataList"];
            dataLists = contract_lost_message_deal(dataLists, "Parent", "DataList");
        }
        var contractSignatures = contract['ContractBody']['ContractSignatures'];
        if(contractSignatures == undefined || contractSignatures == null){
            contract['ContractBody']['ContractSignatures'] = null;
        }
        if(contractState != undefined && contractState != null && contractState != ""){
            contract['ContractBody']['ContractState'] = contractState;
        }
        var creatTime = contract['ContractBody']['CreateTime'];
        if(creatTime != null &&  creatTime.length!=13){
            contract['ContractBody']['CreateTime']=moment(contract['ContractBody']['CreateTime']).format('x');
        }
        var startTime = contract['ContractBody']['StartTime'];
        console.log("-------------------------- Test StartTime  -----------------------------");
        console.log(startTime);
        console.log(moment(startTime, "YY-MM-DD HH:mm:ss").isValid());
        if(startTime != null && startTime.length!=13){
            contract['ContractBody']['StartTime']=moment(contract['ContractBody']['StartTime']).format('x');
        }
        var endTime = contract['ContractBody']['EndTime'];
        console.log(endTime.length==13);
        if(endTime != null && endTime.length!=13){
            contract['ContractBody']['EndTime']=moment(contract['ContractBody']['EndTime']).format('x');
        }
        return contract;
    }

})(typeof module !== 'undefined' && module.exports ? module.exports : (self.ContractUtils = self.ContractUtils || {}));
