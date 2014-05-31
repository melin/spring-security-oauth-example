QQ oauth2 认证流程：http://wiki.open.t.qq.com/index.php/OAuth2.0%E9%89%B4%E6%9D%83
七牛存储：http://docs.qiniutek.com/v1/api/oauth2/

http://localhost:8080/Oauth2Server/oauth/authorize?client_id=af4sdop16&response_type=code&scope=read&state=123456&redirect_uri=http://localhost:8080/rrd/api

http://localhost:8080/rrd/api?code=SICH4z&state=123456

http://localhost:8080/Oauth2Server/oauth/token?client_id=af4sdop16&client_secret=12345678&redirect_uri=http://localhost:8080/rrd/api&grant_type=authorization_code&code=REaDF0

http://localhost:8080/Oauth2Server/oauth/token?client_id=af4sdop16&client_secret=12345678&grant_type=refresh_token&refresh_token=436feaaf-2a60-41dd-acc1-41b277bd932f

