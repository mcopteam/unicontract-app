var oFileLlist1 = document.querySelector('.file_list1');
//导入合约Start
var oExport = document.querySelector('#export');
oExport.onclick = function() {
	var objFile = document.getElementById("fileId");
	if(objFile.value == "") {
		alert("不能空")
	} else {
		var name = objFile.files[0].name;
		var filename = name.split('.')[0]
		if(name.split('.')[1] != 'xml') {
			alert('请选择标准的XML格式文件导入！')
		} else {
			var files = $('#fileId').prop('files'); //获取到文件列表
			if(files.length == 0) {
				alert('请选择文件');
			} else {
				var reader = new FileReader(); //新建一个FileReader
				reader.readAsText(files[0], "UTF-8"); //读取文件
				reader.onload = function(evt) { //读取完文件之后会回来这里
					var fileString = evt.target.result; // 读取文件内容
					$.ajax({
						type: "POST",
						dataType: "json",
						async: false,
						url: server_url + "/fileContract/import",
						data: {
							"token": "futurever",
							"xmlContract": fileString,
							"name": filename,
							"createFlag": true
						},
						success: function(res) {
							alert(res.msg)
							openPage(-1);
						}
					})
				}
			}
		}
	}
}
//导入合约End
//新建合约Start
var oNewopen = document.querySelector('#new_open');
var oNewFileName = document.querySelector('#newfilename');
oNewopen.onclick = function() {
	var value = oNewFileName.value;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/fileContract/create",
		data: {
			"token": "futurever",
			"name": value
		},
		success: function(res) {
			openPage(-1);
			var parameters = 'name=' + encodeURI(res.data.name) + '&id=' + res.data.id + '&contractId=' + res.data.contractId;
			location.href = 'index2.html?' + encodeURI(parameters);

		},
		error: function(err) {
			console.log('error');
		}
	});
}
// 新建合约End
//	文件管理页面渲染数据Start
function openPage(status) {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: server_url + "/fileContract/list",
		data: {
			"token": "futurever",
			"status": status,
			"pageSize": 100
		},
		success: function(res) {
			console.log(res);
			$('.file_list1').empty();
			var f = res.data;
			if(f) {
				var oTh = document.createElement('tr');
				oTh.innerHTML = '<th>序号</th><th>合约编号</th><th>合约名称</th><th>修改时间</th><th>文件状态</th><th></th><th>操作</th><th></th><th></th>';
				oFileLlist1.appendChild(oTh);
				var files = res.data.list;
				for(var i = 0; i < files.length; i++) {
					var oT = document.createElement('tr');
					if(i % 2 == 1) {
						oT.style.backgroundColor = '#e7edf7';
					}
					oT.setAttribute('data-toggle', 'modal');
					if(files[i].updateTime == null) {
						files[i].updateTime = files[i].createTime;
					}
					var arr = ['创建中', '审核中', '修改中', '等待发布', '已发布'];
					switch(files[i].status) {
						case 0:
							oT.innerHTML = '<td>' + files[i].id + '</td><td>' + files[i].contractId + '</td><td>' + files[i].name + '</td><td>' + files[i].updateTime + '</td><td>' + arr[files[i].status] + '</td><td><button type="button" class="btn btn-default " data-toggle="modal">编辑</button></td><td><button type="button" class="btn btn-default " >送审</button></td><td><button type="button" class="btn btn-default " data-toggle="modal"  disabled="disabled" >查看修改意见</button></td><td><button type="button" class="btn btn-default " data-toggle="modal">删除</button></td>';
							break;
						case 1:
							oT.innerHTML = '<td>' + files[i].id + '</td><td>' + files[i].contractId + '</td><td>' + files[i].name + '</td><td>' + files[i].updateTime + '</td><td>' + arr[files[i].status] + '</td><td><button type="button" class="btn btn-default " data-toggle="modal">编辑</button></td><td><button type="button" class="btn btn-default "  disabled="disabled">送审</button></td><td><button type="button" class="btn btn-default " data-toggle="modal"   disabled="disabled">查看修改意见</button></td><td><button type="button" class="btn btn-default " data-toggle="modal">删除</button></td>';
							break;
						case 2:
							oT.innerHTML = '<td>' + files[i].id + '</td><td>' + files[i].contractId + '</td><td>' + files[i].name + '</td><td>' + files[i].updateTime + '</td><td>' + arr[files[i].status] + '</td><td><button type="button" class="btn btn-default " data-toggle="modal">编辑</button></td><td><button type="button" class="btn btn-default "  data-target="#myModal01">送审</button></td><td><button type="button" class="btn btn-default " data-toggle="modal"   data-target="#myModal02">查看修改意见</button></td><td><button type="button" class="btn btn-default " data-toggle="modal">删除</button></td>';
							break;
						case 3:
							oT.innerHTML = '<td>' + files[i].id + '</td><td>' + files[i].contractId + '</td><td>' + files[i].name + '</td><td>' + files[i].updateTime + '</td><td>' + arr[files[i].status] + '</td><td><button type="button" class="btn btn-default " data-toggle="modal">编辑</button></td><td><button type="button" class="btn btn-default "  disabled="disabled">送审</button></td><td><button type="button" class="btn btn-default " data-toggle="modal"  disabled="disabled" >查看修改意见</button></td><td><button type="button" class="btn btn-default " data-toggle="modal">删除</button></td>';
							break;
						case 4:
							oT.innerHTML = '<td>' + files[i].id + '</td><td>' + files[i].contractId + '</td><td>' + files[i].name + '</td><td>' + files[i].updateTime + '</td><td>' + arr[files[i].status] + '</td><td><button type="button" class="btn btn-default " data-toggle="modal">编辑</button></td><td><button type="button" class="btn btn-default " disabled="disabled">送审</button></td><td><button type="button" class="btn btn-default " data-toggle="modal"   disabled="disabled">查看修改意见</button></td><td><button type="button" class="btn btn-default " data-toggle="modal">删除</button></td>';
							break;
						default:
							break;
					}
					var time = files[i].time;
					var oPen = oT.getElementsByTagName('td')[5];
					var oSong = oT.getElementsByTagName('td')[6];
					var opinion = oT.getElementsByTagName('td')[7];
					var btn_delete = oT.getElementsByTagName('td')[8];
					(function(j) {
						var id = files[j].id;
						var contractId = files[j].contractId;
						var statu = files[j].status;
						var suggestion = files[j].suggestion;
						var name = files[j].name
						//										删除按钮操作
						btn_delete.onclick = function() {
 							$.ajax({
                                type: "POST",
                                dataType: "json",
                                url: server_url + "/fileContract/delete",
                                data: {
                                    "token": "futurever",
                                    "id": id
                                },
                                success: function(res) {
                                    alert(res.msg);
                                    openPage(-1);
                                }
                            });
						}
						oSong.onclick = function() {
							var oS = document.querySelector('#songValue');
							$.ajax({
								type: "POST",
								dataType: "json",
								url: server_url + "/fileContract/sendAudit",
								data: {
									"token": "futurever",
									"id": id
								},
								success: function(res) {
									openPage(status);
								}
							})
						}
						opinion.onclick = function() {
							$('.opinion_inner').html(suggestion);
						}
						oPen.onclick = function() {
							location.href = 'index2.html?name=' + name + '&id=' + id + '&contractId=' + contractId;
						}
					})(i);
					oFileLlist1.appendChild(oT);
				}
			}
		},
		error: function(error) {
			console.log("error");
			console.log(error);
		}
	});
}
//				文件管理页面渲染数据End
