window.onload = function() {
	$('#login').click(function() {
		var name = $('#name').val();
		var pass = $('#password').val();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: server_url + "/login",
			data: {
				"username": name,
				"password": pass
			},
			success: function(res) {
				console.log(res);
				sessionStorage.role = res.data.role;
				sessionStorage.name = res.data.name;
				sessionStorage.pubkey = res.data.pubkey;
				sessionStorage.prikey = res.data.prikey;
				console.log(sessionStorage.role)
				console.log(res.data);
				if(res.code == 200) {
					location.href = 'index.html';
				}
			},
			error: function(error) {
				console.log(error);
			}
		});
	})
}