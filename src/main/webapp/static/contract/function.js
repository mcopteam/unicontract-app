
// json录入格式：{"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"}

var  functionData={
//===============金融理财======================================================
//All
    "demo2":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, '{user_B, amount}')"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
        {"name":"FuncQueryUserTotalShareInPeriod(a1,a2)","text":"查询用户募集期内认购份额 FuncQueryUserTotalShareInPeriod(user_A, product_A)"},
        {"name":"FuncQueryProductTotalShareInPeriod(a1,a2)","text":"获取产品当期已募集总额 FuncQueryProductTotalShareInPeriod(product_A, user_B)"},
        {"name":"FuncAskAdminIfContinue(a1)","text":"询问管理员是否允许继续购买 FuncAskAdminIfContinue(user_B)"},
        {"name":"FuncGetAdminIfContinueReply(a1)","text":"获取管理员回复的是否继续购买信息 FuncGetAdminIfContinueReply(user_B)"},
        {"name":"FuncPurchaseExit()","text":"认购失败【达到封顶头寸 或 管理员不允许购买】FuncPurchaseExit()"},
        {"name":"FuncPurchaseSuccess()","text":"认购成功【未达到封顶头寸 或 管理员允许购买】FuncPurchaseSuccess()"},
        {"name":"FuncGetUserPrincipalAndInterest(a1)","text":"没达到募集规模：获取用户的资金及利息 FuncGetUserPrincipalAndInterest(user_A)"},
        {"name":"FuncPurchaseFailAndRefund(a1,a2)","text":"募集期外，没达到募集规模：原认购产品失败，退还资金及利息 FuncPurchaseFailAndRefund(user_A, user_B, amount)"},
        {"name":"FuncPurchaseSuccessOrgProduct(a1)","text":"募集期外，达到募集规模：产品成立，原认购产品有效 FuncPurchaseSuccessOrgProduct(user_A)"},
        {"name":"FuncSignatureProduct()","text":"签订认购协议 FuncSignatureProduct()"},
        {"name":"FuncPurchaseFailNewProduct()","text":"未达到募集规模，产品失败，不可新购产品 FuncPurchaseFailNewProduct()"},
        {"name":"FuncPurchaseSuccessNewProduct()","text":"达到募集规模，产品成立，可以新购产品，认购成功 FuncPurchaseSuccessNewProduct()"},
        {"name":"FuncQueryUserTotalShareInPeriod(a1,a2)","text":"查询用户募集期内认购份额 FuncQueryUserTotalShareInPeriod(user_A, product_A)"},
        {"name":"FuncQueryProductTotalShareInPeriod(a1,a2)","text":"获取产品当期已募集总额 FuncQueryProductTotalShareInPeriod(product_A, user_B)"},
        {"name":"FuncAskAdminIfContinue(a1)","text":"询问管理员是否允许继续购买 FuncAskAdminIfContinue(user_B)"},
        {"name":"FuncGetAdminIfContinueReply(a1)","text":"获取管理员回复的是否继续购买信息 FuncGetAdminIfContinueReply(user_B)"},
        {"name":"FuncPurchaseExit()","text":"认购失败【达到封顶头寸 或 管理员不允许购买】FuncPurchaseExit()"},
        {"name":"FuncPurchaseSuccess()","text":"认购成功【未达到封顶头寸 或 管理员允许购买】FuncPurchaseSuccess()"},
        {"name":"FuncGetUserPrincipalAndInterest(a1)","text":"没达到募集规模：获取用户的资金及利息 FuncGetUserPrincipalAndInterest(user_A)"},
        {"name":"FuncPurchaseFailAndRefund(a1,a2)","text":"募集期外，没达到募集规模：原认购产品失败，退还资金及利息 FuncPurchaseFailAndRefund(user_A, user_B, amount)"},
        {"name":"FuncPurchaseSuccessOrgProduct(a1)","text":"募集期外，达到募集规模：产品成立，原认购产品有效 FuncPurchaseSuccessOrgProduct(user_A)"},
        {"name":"FuncSignatureProduct()","text":"签订认购协议 FuncSignatureProduct()"},
        {"name":"FuncPurchaseFailNewProduct()","text":"未达到募集规模，产品失败，不可新购产品 FuncPurchaseFailNewProduct()"},
        {"name":"FuncPurchaseSuccessNewProduct()","text":"达到募集规模，产品成立，可以新购产品，认购成功 FuncPurchaseSuccessNewProduct()"},
        {"name":"FuncGetUserTotalShare(a1,a2)","text":"获取用户已购产品总金额 FuncGetUserTotalShare(product_A, user_A)"},
        {"name":"FuncGetUserHoldPeriod(a1,a2,a3)","text":"获取用户持有期 FuncGetUserHoldPeriod(period_from, user_A, product_A)"},
        {"name":"FuncRedeemFail(a1,a2)","text":"赎回失败[持有期小于7天] FuncRedeemFail(product_A, user_A)"},
        {"name":"FuncRedeemAllProcess(a1,a2)","text":"全部赎回 FuncRedeemAllProcess(product_A, user_A)"},
        {"name":"FuncCalcTotalAmount(a1,a2)","text":"计算账户赎回总额度 FuncCalcTotalAmount(product_A, user_A)"},
        {"name":"FuncRedeemLargeProcess(a1,a2)","text":"通知管理员【大额赎回】 FuncRedeemLargeProcess(product_A, user_A)"},
        {"name":"FuncGetLastRedeemLargeTime(a1,a2)","text":"获取上次大额赎回时间 FuncGetLastRedeemLargeTime(product_A, user_A)"},
        {"name":"FuncRedeemLimit(a1,a2)","text":"是连续两天大额赎回【限制操作】FuncRedeemLimit(product_A, user_A)"},
        {"name":"FuncRedeemSmallProcess(a1,a2)","text":"赎回转账【小额赎回或大额赎回不受限】FuncRedeemSmallProcess(product_A, user_A)"},
        {"name":"FuncTotalOutValueLastDay(a1,a2)","text":"获取上一工作日理财计划的净产值 FuncTotalOutValueLastDay(product_A, user_A)"},
        {"name":"FuncGetUserPurchase(a1)","text":"查询截止当前的认购金额 FuncGetUserPurchase(user_A)"},
        {"name":"FuncGetUserBalance(a1)","text":"查询截止当前的账户余额 FuncGetUserBalance(user_A)"},
        {"name":"FuncCheckLastDayInRaisePeriod(a1)","text":"判定上一工作日是否为募集期内 FuncCheckLastDayInRaisePeriod(period_from, period_to)"},
        {"name":"FuncGetDepositRate()","text":"查询人民币活期存款利率 FuncGetDepositRate()"},
        {"name":"FuncCalcAndTransferInterest(a1,a2,a3)","text":"计算活期利息转账给账户 FuncCalcAndTransferInterest(user_a, total_amount, rate)"},
        {"name":"FuncGetYearYieldRateOfLastDay()","text":"查询上一工作日实际年化收益率 FuncGetYearYieldRateOfLastDay()"},
        {"name":"FuncCalcUserRealIncome()","text":"计算用户理财实际收益并转账 FuncCalcUserRealIncome()"},
        {"name":"FuncCalcAndTransferTrusteeTee()","text":"计算理财委托托管费并转账 FuncCalcAndTransferTrusteeTee()"},
        {"name":"FuncCalcAndTransferExpectIncome()","text":"计算用户预期收益并转账 FuncCalcAndTransferExpectIncome()"},
        {"name":"FuncQueryContractState()","text":"查询用户理财合约状态 FuncQueryContractState()"},
        {"name":"FuncTerminateContract()","text":"终止理财合约，停止计息 FuncTerminateContract()"},
        {"name":"FuncStopCalcInterest()","text":"认购金额为0，停止计息 FuncStopCalcInterest()"},
        {"name":"FuncGetConditionState(a1)","text":"获取合约终止条件 FuncGetConditionState(cond_A)"},
        {"name":"FuncAbnormalEnd()","text":"理财合约终止 FuncAbnormalEnd()"},
        {"name":"FuncUserTotalRemain(a1)","text":"查询交易账户余额 FuncUserTotalRemain(user_A)"},
        {"name":"FuncPayTotalTrustFee(a1,a2)","text":"查询待付托管费用 FuncPayTotalTrustFee(user_A, user_B)"},
        {"name":"FuncPayTotalManageFee(a1,a2)","text":"查询待付管理费用 FuncPayTotalManageFee(user_A, user_C)"},
        {"name":"FuncGetProductState(a1,a2)","text":"查询理财产品状态 FuncGetProductState(user_A, product_A)"},
        {"name":"FuncBankTransfer(a1,a2,a3,a4,a5)","text":"转账到银行账户 FuncBankTransfer(user_A, bank_A, user_B, bank_B, amount)"}
    ],
//购买合约
	"demo3":[
		{"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
		{"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
		{"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
		{"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
		{"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, '{user_B, amount}')"},
		{"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
		{"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
		{"name":"FuncQueryUserTotalShareInPeriod(a1,a2)","text":"查询用户募集期内认购份额 FuncQueryUserTotalShareInPeriod(user_A, product_A)"},
		{"name":"FuncQueryProductTotalShareInPeriod(a1,a2)","text":"获取产品当期已募集总额 FuncQueryProductTotalShareInPeriod(product_A, user_B)"},
		{"name":"FuncAskAdminIfContinue(a1)","text":"询问管理员是否允许继续购买 FuncAskAdminIfContinue(user_B)"},
		{"name":"FuncGetAdminIfContinueReply(a1)","text":"获取管理员回复的是否继续购买信息 FuncGetAdminIfContinueReply(user_B)"},
		{"name":"FuncPurchaseExit()","text":"认购失败【达到封顶头寸 或 管理员不允许购买】FuncPurchaseExit()"},
    	{"name":"FuncPurchaseSuccess()","text":"认购成功【未达到封顶头寸 或 管理员允许购买】FuncPurchaseSuccess()"},
    	{"name":"FuncGetUserPrincipalAndInterest(a1)","text":"没达到募集规模：获取用户的资金及利息 FuncGetUserPrincipalAndInterest(user_A)"},
    	{"name":"FuncPurchaseFailAndRefund(a1,a2)","text":"募集期外，没达到募集规模：原认购产品失败，退还资金及利息 FuncPurchaseFailAndRefund(user_A, user_B, amount)"},
    	{"name":"FuncPurchaseSuccessOrgProduct(a1)","text":"募集期外，达到募集规模：产品成立，原认购产品有效 FuncPurchaseSuccessOrgProduct(user_A)"},
    	{"name":"FuncSignatureProduct()","text":"签订认购协议 FuncSignatureProduct()"},
    	{"name":"FuncPurchaseFailNewProduct()","text":"未达到募集规模，产品失败，不可新购产品 FuncPurchaseFailNewProduct()"},
    	{"name":"FuncPurchaseSuccessNewProduct()","text":"达到募集规模，产品成立，可以新购产品，认购成功 FuncPurchaseSuccessNewProduct()"}
    ],
//赎回合约
    "demo4":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, '{user_B, amount}')"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
    	{"name":"FuncGetUserTotalShare(a1,a2)","text":"获取用户已购产品总金额 FuncGetUserTotalShare(product_A, user_A)"},
		{"name":"FuncGetUserHoldPeriod(a1,a2,a3)","text":"获取用户持有期 FuncGetUserHoldPeriod(period_from, user_A, product_A)"},
		{"name":"FuncRedeemFail(a1,a2)","text":"赎回失败[持有期小于7天] FuncRedeemFail(product_A, user_A)"},
		{"name":"FuncRedeemAllProcess(a1,a2)","text":"全部赎回 FuncRedeemAllProcess(product_A, user_A)"},
		{"name":"FuncCalcTotalAmount(a1,a2)","text":"计算账户赎回总额度 FuncCalcTotalAmount(product_A, user_A)"},
		{"name":"FuncRedeemLargeProcess(a1,a2)","text":"通知管理员【大额赎回】 FuncRedeemLargeProcess(product_A, user_A)"},
		{"name":"FuncGetLastRedeemLargeTime(a1,a2)","text":"获取上次大额赎回时间 FuncGetLastRedeemLargeTime(product_A, user_A)"},
		{"name":"FuncRedeemLimit(a1,a2)","text":"是连续两天大额赎回【限制操作】FuncRedeemLimit(product_A, user_A)"},
		{"name":"FuncRedeemSmallProcess(a1,a2)","text":"赎回转账【小额赎回或大额赎回不受限】FuncRedeemSmallProcess(product_A, user_A)"},
		{"name":"FuncTotalOutValueLastDay(a1,a2)","text":"获取上一工作日理财计划的净产值 FuncTotalOutValueLastDay(product_A, user_A)"}
    ],
 //收益计算
    "demo5":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
    	{"name":"FuncGetUserPurchase(a1)","text":"查询截止当前的认购金额 FuncGetUserPurchase(user_A)"},
		{"name":"FuncGetUserBalance(a1)","text":"查询截止当前的账户余额 FuncGetUserBalance(user_A)"},
		{"name":"FuncCheckLastDayInRaisePeriod(a1)","text":"判定上一工作日是否为募集期内 FuncCheckLastDayInRaisePeriod(period_from, period_to)"},
		{"name":"FuncGetDepositRate()","text":"查询人民币活期存款利率 FuncGetDepositRate()"},
		{"name":"FuncCalcAndTransferInterest(a1,a2,a3)","text":"计算活期利息转账给账户 FuncCalcAndTransferInterest(user_a, total_amount, rate)"},
		{"name":"FuncGetYearYieldRateOfLastDay()","text":"查询上一工作日实际年化收益率 FuncGetYearYieldRateOfLastDay()"},
		{"name":"FuncCalcUserRealIncome()","text":"计算用户理财实际收益并转账 FuncCalcUserRealIncome()"},
		{"name":"FuncCalcAndTransferTrusteeTee()","text":"计算理财委托托管费并转账 FuncCalcAndTransferTrusteeTee()"},
		{"name":"FuncCalcAndTransferExpectIncome()","text":"计算用户预期收益并转账 FuncCalcAndTransferExpectIncome()"},
		{"name":"FuncQueryContractState()","text":"查询用户理财合约状态 FuncQueryContractState()"},
		{"name":"FuncTerminateContract()","text":"终止理财合约，停止计息 FuncTerminateContract()"},
		{"name":"FuncStopCalcInterest()","text":"认购金额为0，停止计息 FuncStopCalcInterest()"}
	],
 //合约终止
     "demo6":[
         {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
         {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
         {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
         {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
         {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A,user_B#amount)"},
         {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
         {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
         {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
		 {"name":"FuncGetConditionState(a1)","text":"获取合约终止条件 FuncGetConditionState(cond_A)"},
		 {"name":"FuncAbnormalEnd()","text":"理财合约终止 FuncAbnormalEnd()"}
	],
 //账务结算
	"demo7":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
		{"name":"FuncUserTotalRemain(a1)","text":"查询交易账户余额 FuncUserTotalRemain(user_A)"},
		{"name":"FuncPayTotalTrustFee(a1,a2)","text":"查询待付托管费用 FuncPayTotalTrustFee(user_A, user_B)"},
		{"name":"FuncPayTotalManageFee(a1,a2)","text":"查询待付管理费用 FuncPayTotalManageFee(user_A, user_C)"},
		{"name":"FuncGetProductState(a1,a2)","text":"查询理财产品状态 FuncGetProductState(user_A, product_A)"},
		{"name":"FuncBankTransfer(a1,a2,a3,a4,a5)","text":"转账到银行账户 FuncBankTransfer(user_A, bank_A, user_B, bank_B, amount)"}
	],
//=================房租缴纳====================================================
    "demo8":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
    	{"name":"FuncIfContinueToPayNextMonth(a1)","text":"判定下月是否需要缴纳房租 FuncIfContinueToPayNextMonth(contract_demo_2.EndTime)"},
    	{"name":"FuncContractExitForComplete()","text":"到期合约退出 FuncContractExitForComplete()"},
    	{"name":"FuncQueryUserBalance(a1)","text":"查询账户余额 FuncQueryUserBalance(User_A)"},
    	{"name":"FuncTransferMoney(a1,a2,a3)","text":"转房租给房东 FuncTransferMoney(UserA, UserB, 5000)"},
    	{"name":"FuncPrintReceipt(a1,a2,a3)","text":"打印凭条收据 FuncPrintReceipt(UserA, UserB, 5000)"},
    	{"name":"FuncRemindAccount(a1,a2,a3)","text":"提示给房东打钱 FuncRemindAccount(UserA, UserB, 5000)"},
		{"name":"FuncNoAction()","text":"空动作 FuncNoAction()"}
	],
//================微网交易=====================================================
    "demo9":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian()","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
    	{"name":"FuncQueryAmmeterBalance(a1)","text":"查询电表余额 FuncQueryAmmeterBalance(user_A)"},
		{"name":"FuncQueryAccountBalance(a1)","text":"查询用户账户余额 FuncQueryAccountBalance(user_A)"},
		{"name":"FuncNoticeDeposit(a1,a2)","text":"短信提示账户充钱 FuncNoticeDeposit(user_A, user_remainmoney)"},
		{"name":"FuncAutoPurchasingElectricity(a1,a2,a3)","text":"电表自动购电50元 FuncAutoPurchasingElectricity(user_A, user_B, amount)"},
		{"name":"FuncAutoSleeping(a1)","text":"自动休眠1小时 FuncAutoSleeping(sleeptime)"},
		{"name":"FuncGetStartEndTime(a1)","text":"获取查询起始时间 FuncGetStartEndTime(user_A)"},
		{"name":"FuncGetPowerConsumeParam(a1,a2,a3)","text":"获取电表消耗电量等信息 FuncGetPowerConsumeParam(user_A, stat_begintime, stat_endtime)"},
		{"name":"FuncGetPowerPrice()","text":"获取电价列表 FuncGetPowerPrice()"},
		{"name":"FuncCalcConsumeAmountAndMoney(a1,a2,a3,a4,a5)","text":"计算用户消耗的电费 FuncCalcConsumeAmountAndMoney(user_A, elec_amount, elec_month_tatalamount, stat_begintime, stat_endtime)"},
		{"name":"FuncTransferElecChargeToPlatform(a1,a2,a3)","text":"打印分账票据 FuncTransferElecChargeToPlatform(user_B, user_others, user_transfers)"},
		{"name":"FuncUpdateElecBalance(a1,a2)","text":"修改电表余额 FuncUpdateElecBalance(user_A, elec_amount)"},
		{"name":"FuncCalcAndSplitRatio(a1,a2,a3)","text":"计算合约分账比例 FuncCalcAndSplitRatio(user_B, stat_begintime, stat_endtime)"},
		{"name":"FuncAutoSplitAccount(a1,a2,a3)","text":"合约分账 FuncAutoSplitAccount(user_B, Split_percent, money)"},
        {"name":"FuncGetPowerPrice()","text":"获取电价 FuncGetPowerPrice()"}
	],
//===============通用方法======================================================
	"demo10":[
		{"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
	    {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
	    {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
	    {"name":"FuncTransferAsset(a1,a2, a3)","text":"资产转移 FuncTransferAsset(user_A, user_B, amount)"},
	    {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
	    {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
	    {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"}
	],
//===========自动售卖机=====================================================
	"demo11":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncTransferAsset(a1,a2)","text":"资产转移 FuncTransferAsset(user_A, user_B#amount)"},
        {"name":"FuncCreateAsset(a1,a2)","text":"资产创建 FuncCreateAsset(user_A, user_B#amount)"},
        {"name":"FuncGetContracOutputtById(a1)","text":"获取合约产出 FuncGetContracOutputtById(contract_id)"},
        {"name":"FuncIsConPutInUnichian(a1)","text":"合约产出是否入链 FuncIsConPutInUnichian(id)"},
		{"name":"FuncGetUserSelectedStyle()","text":"获取用户输入的地铁票类型 FuncGetUserSelectedStyle()"},
		{"name":"FuncGetUserSelectedCount()","text":"获取用户输入的购买数量 FuncGetUserSelectedCount()"},
		{"name":"FuncQueryRemainingCount(a1,a2)","text":"查询购买票品种余量 FuncQueryRemainingCount(style_A, count_A)"},
		{"name":"FuncExitForNoRemaining(a1,a2)","text":"退出操作，提供机器地铁票不足 FuncExitForNoRemaining(style_A, count_A)"},
		{"name":"FuncCalculatedCost(a1,a2)","text":"计算消耗总金额 FuncCalculatedCost(style_A, count_A)"},
		{"name":"FuncWaitPayMoney(a1)","text":"休眠等待用户支付 FuncWaitPayMoney(sleeptime)"},
		{"name":"FuncQueryUserPayCount(a1)","text":"查询用户支付额度 FuncQueryUserPayCount(user_A)"},
		{"name":"FuncSupplyGoods(a1,a2)","text":"售卖机出票 FuncSupplyGoods(style_A, count_A)"},
		{"name":"FuncQueryRemainingMoney(a1)","text":"查询消耗余额 FuncQueryRemainingMoney(user_A)"},
		{"name":"FuncOddChange(a1)","text":"售卖机找零 FuncOddChange(user_A)"},
		{"name":"FuncExitForSuccess(a1)","text":"退出操作，打印欢迎使用售卖机 FuncExitForSuccess(user_A)"},
		{"name":"FuncExitForTerminal(a1)","text":"购买流程结束 FuncExitForTerminal(user_A)"}
	],
//===========房产交易=====================================================
	"demo12":[
        {"name":"FuncGetNowDay()","text":"获取当前日期 FuncGetNowDay()"},
        {"name":"FuncGetNowDate()","text":"获取当前时间 FuncGetNowDate()"},
        {"name":"FuncGetNowDateTimestamp()","text":"获取当前时间戳 FuncGetNowDateTimestamp()"},
        {"name":"FuncSleepTime(a1)","text":"休眠指定时间 FuncSleepTime(sleeptime)"},
        {"name":"FuncQueryHouse()","text":"查询要购买的房产 FuncQueryHouse()"},
        {"name":"FuncExitForNoHouse()","text":"无合适房产，退出 FuncExitForNoHouse()"},
        {"name":"FuncUserBalance(a1)","text":"查询账户余额 FuncUserBalance(user_A)"},
        {"name":"FuncTransferHouseFees(a1,a2,a3)","text":"购房款转账 FuncTransferHouseFees(user_A, user_B, amount)"},
        {"name":"FuncNoticeRecharge()","text":"提示钱不足，需要充钱 FuncNoticeRecharge()"},
        {"name":"FuncQueryHouseFeesResult()","text":"查看购房款转账结果 FuncQueryHouseFeesResult()"},
        {"name":"FuncExitForTransferFail()","text":"购房款转账失败，退出 FuncExitForTransferFail()"},
        {"name":"FuncSleepTime()","text":"等待用户充钱 FuncSleepTime()"},
        {"name":"FuncTransferHouse(a1,a2,a3)","text":"房产转移 FuncTransferHouse(user_B, user_A, amount)"},
        {"name":"FuncQueryHouseResult()","text":"查询房产交易结果 FuncQueryHouseResult()"},
        {"name":"FuncExitForSuccess()","text":"购房交易成功 FuncExitForSuccess()"},
        {"name":"FuncExitForHouseTransferFail(a1)","text":"交易失败购房款回退 FuncExitForHouseTransferFail(fees_transfer_id)"}
	],
    //跨链操作
    "demo13":[
        {"name":"FuncTransferWithTransInMultiChain(a1)","text":"多链转账开始 FuncTransferWithTransInMultiChain(userParam)"},
        {"name":"FuncIsTransInMultiChain(a1)","text":"是否全部入链 FuncIsTransInMultiChain(lastRes)"},
        {"name":"FuncUnFreezeMutilChain(a1,a2)","text":"解冻成功资产 FuncUnFreezeMutilChain(userParam,lastRes)"},
        {"name":"FuncRollBackMutilTrans(a1,a2)","text":"回滚成功资产 FuncRollBackMutilTrans(userParam,lastRes)"}
    ]
}