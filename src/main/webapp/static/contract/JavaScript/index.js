(function() {
	//				个人设置
	var personal = document.querySelector('.keys');
	//				执行合约Page
	var oCona = document.querySelector('.con');
	//				审核合约Page
	var oCona1 = document.querySelector('.con2');
	//				oCona+oCona1 容器
	var oDiv = document.querySelector('.manage');
	//				文件管理Page
	var oFile = document.querySelector('.file_body');
	//				购电设置Page
	var oElectricity = document.querySelector('.electricity_body');
	//				资产转移Page
	var oTransfer = document.querySelector('.transfer_body');
	//				log检测Page
	var oLogView = document.querySelector('.log_view');
	//				导航列表
	var oTab = document.querySelector('.tabs');
	//				文件管理按钮
	var oTab1 = oTab.children[0];
	//				合约产品按钮
	var oTab2 = oTab.children[1];
	//				执行合约按钮
	var oTab3 = oTab.children[2];
	//				购电设置按钮
	var oTab4 = oTab.children[3];
	//				资产转移按钮
	var oTab8 = oTab.children[7];
	//				log检测按钮
	var oTab9 = oTab.children[8];
    var timer=null;
	//              角色获取
	var role = sessionStorage.role;
	if(!role) {
        location.href='login.html';
	}
	switch(role) {
		case "1":
			break;
		case "2":
			for(var i = 3; i < oTab.children.length; i++) {
				oTab.children[i].style.display = 'none';
			}
			break;
		case "3":
			break;
		case "4":
			for(var i = 3; i < oTab.children.length; i++) {
				oTab.children[i].style.display = 'none';
			}
			openPage(-1);
			break;
		case "5":
			for(var i = 0; i < oTab.children.length; i++) {
				oTab.children[i].style.display = 'none';
			}
			oTab.children[1].style.display = 'block';
			oTab.children[1].setAttribute('class', 'cur');
			oFile.style.display = 'none';
			oDiv.style.display = 'block';
			oCona1.style.display = 'block';
			oCona.style.display = 'none';
			contractTest(-1);
			break;
		case "6":
			for(var i = 0; i < oTab.children.length; i++) {
				oTab.children[i].style.display = 'none';
			}
			oTab.children[1].style.display = 'block';
			oTab.children[2].style.display = 'block';
			oTab.children[3].style.display = 'none';
			oTab.children[7].style.display = 'block';
			oTab.children[8].style.display = 'block';
			oTab.children[1].setAttribute('class', 'cur');
			oFile.style.display = 'none';
			oDiv.style.display = 'block';
			oCona1.style.display = 'block';
			oCona.style.display = 'none';
			oTransfer.style.display = 'none';
			oLogView.style.display = 'none';
			oElectricity.style.display='none';
			contractTest(-1);
			break;
		case "10":
            oTab.children[2].style.display = 'none';
            oTab.children[3].style.display = 'none';
            oTab.children[4].style.display = 'none';
            oTab.children[5].style.display = 'none';
            oTab.children[6].style.display = 'none';
            openPage(-1);
		default:
			break;
	}
	//				文件管理按钮
	oTab1.onclick = function() {
		this.className = 'cur';
		oTab2.className = '';
		oTab3.className = '';
		oTab4.className = '';
		oTab9.className = '';
		oTab8.className = '';
		oFile.style.display = 'block';
		oDiv.style.display = 'none';
		oElectricity.style.display='none';
		oLogView.style.display = 'none';
		openPage(-1);
	}
	//				合约产品按钮
	oTab2.onclick = function() {
		this.className = 'cur';
		oTab1.className = '';
		oTab3.className = '';
		oTab4.className = '';
		oTab9.className = '';
		oTab8.className = '';
		oDiv.style.display = 'block';
		oFile.style.display = 'none';
		oCona1.style.display = 'block';
		oCona.style.display = 'none';
		oLogView.style.display = 'none';
		oElectricity.style.display='none';
		contractTest(-1);
	}
	//				执行合约按钮
	oTab3.onclick = function() {
		this.className = 'cur';
		oTab1.className = '';
		oTab2.className = '';
		oTab4.className = '';
		oTab9.className = '';
		oTab8.className = '';
		oDiv.style.display = 'block';
		oFile.style.display = 'none';
		oCona.style.display = 'block';
		oCona1.style.display = 'none';
		oLogView.style.display = 'none';
		oElectricity.style.display='none';
		//                  执行合约方法
		contract_execute()

	}
	//				购电设置按钮
	oTab4.onclick = function() {
		this.className = 'cur';
		oTab1.className = '';
		oTab2.className = '';
		oTab3.className = '';
		oTab8.className = '';
		oTab9.className = '';
		oDiv.style.display = 'none';
		oFile.style.display = 'none';
		oTransfer.style.display = 'none';
		oLogView.style.display = 'none';
		oCona1.style.display = 'none';
		oCona.style.display = 'none';
		oElectricity.style.display='block';
        go();
        $.ajax({
            type: "POST",
            dataType: "json",
            async: false,
            url: server_url + "/transferController/getUserInfo",
            data: {
                "token": "futurever"
            },
            success: function (res) {
                //签名渲染数据
				$('#electricity_sign_select').empty();
				var data = res.data;
				for(var i = 0; i < data.length; i++) {
					var os = document.createElement('option');
					os.innerHTML = data[i].username;
					os.setAttribute('pubkey', data[i].pubkey);
					$('#electricity_sign_select').append(os);
				}
            }
        });
	}
	//				资产转移按钮
	oTab8.onclick = function() {
		this.className = 'cur';
		oTab1.className = '';
		oTab2.className = '';
		oTab3.className = '';
		oTab4.className = '';
		oTab9.className = '';
		oDiv.style.display = 'none';
		oFile.style.display = 'none';
		oLogView.style.display = 'none';
		oTransfer.style.display = 'block';
		oCona1.style.display = 'none';
		oCona.style.display = 'none';
		oElectricity.style.display='none';
        sendQuery();
        $.ajax({
            type: "POST",
            dataType: "json",
            async: false,
            url: server_url + "/transferController/getUserInfo",
            data: {
                "token": "futurever"
            },
            success: function (res) {
            	console.log(res);
                //收款人渲染数据
				$('#electricity_receive_select').empty();
				var data = res.data;
				for(var i = 0; i < data.length; i++) {
					var os = document.createElement('option');
					os.innerHTML = data[i].username;
					os.setAttribute('pubkey', data[i].pubkey);
					$('#electricity_receive_select').append(os);
				}
            }
        });
	}
	//				log检测按钮
	oTab9.onclick = function() {
		this.className = 'cur';
		oTab1.className = '';
		oTab2.className = '';
		oTab3.className = '';
		oTab4.className = '';
		oTab8.className = '';
		oDiv.style.display = 'none';
		oFile.style.display = 'none';
		oTransfer.style.display = 'none';
		oLogView.style.display = 'block';
		oCona1.style.display = 'none';
		oCona.style.display = 'none';
		oElectricity.style.display='none';
		logView();

	}
	//				个人设置公私钥列表Start
	function getKeys() {
		$.ajax({
			type: "POST",
			dataType: "json",
			url: server_url + "/contractUser/keyList",
			data: {
				"token": "futurever"
			},
			success: function(res) {
				$('.keys').empty()
				var oT1 = document.createElement('tr');
				oT1.innerHTML = '<th></th><th>公钥</th><th>私钥</th><th>操作</th>';
				personal.appendChild(oT1);
				var keys = res.data;
				if(keys) {
					for(var i = 0; i < keys.length; i++) {
						var oT = document.createElement('tr');
						oT.innerHTML = '<td>' + keys[i].createUserName + '</td><td>' + keys[i].pubkey + '</td><td>' + keys[i].prikey + '</td><td><a href="JavaScript:;">删除</a></td>';
						var oDel = oT.getElementsByTagName('a')[0];
						oDel.onclick = function() {
							personal.removeChild(this.parentNode.parentNode);
						}
						personal.appendChild(oT);
					}
				}
			}
		});
	}
	getKeys();
	//				个人设置公私钥列表End
	//				申请公钥Start
	$('#apply').click(function() {
		var value = $('#applyvalue').val();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: server_url + "/contractUser/applyKey",
			data: {
				"token": "futurever",
				"pubkey": value
			},
			success: function(res) {
				alert(res.msg);
			},
			error: function(error) {
				console.log("error");
				console.log(error);
			}
		})
	})
	//				申请公钥End
	//				退出Start
	$('#logout').click(function() {
		$.ajax({
			type: "GET",
			dataType: "json",
			url: server_url + "/logout",
			success: function(res) {
				if(res.code == 200) {
					alert(res.msg)
					location.href = 'login.html';
				}
			},
			error: function(err) {
				console.log(err);
				console.log('error');
			}
		});
	})
	//				退出End
	//				layui日期选择器 富文本框Start
	layui.use('laydate', function() {
		var laydate = layui.laydate;
	});
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
				var textateaValue = layedit.getText(index); //获取编辑器纯文本内容
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
	$('#transfer_data').click(function(){
		var start = {
			min: laydate.now()
			,max: '2099-06-16 23:59:59'
			,istoday: false
			,choose: function(data){
			},
			istime: true,
			format: 'YYYY-MM-DD hh:mm:ss'
		};
		start.elem = this;
		laydate(start);
	})
	$('#electricity_start_data').click(function(){
		var start = {
			// min: laydate.now()
			max: '2099-06-16 23:59:59'
			,istoday: false
			,choose: function(data){
			},
			istime: true,
			format: 'YYYY-MM-DD hh:mm:ss'
		};
		start.elem = this;
		laydate(start);
	})
	$('#electricity_end_data').click(function(){
		var start = {
			// min: laydate.now()
			max: '2099-06-16 23:59:59'
			,istoday: false
			,choose: function(data){
			},
			istime: true,
			format: 'YYYY-MM-DD hh:mm:ss'
		};
		start.elem = this;
		laydate(start);
	})
})();