<%--
  Created by IntelliJ IDEA.
  User: ivar
  Date: 12.11.15
  Time: 20:55
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<body>

<div class="col-sm-12 login-mobile-wrapper">
    <div class="login-content">
        <div class="input-group margin-top-30">
            <span class="input-group-addon">+372</span>
            <input type="text" class="form-control loginByMobiilIdPhoneNoInput" placeholder="Telefoninumber" value="00007">
        </div>
        <a class="btn btn-outline-inverse btn-lg margin-top-30 mobile-login loginByMobiilIdDoLoginBtn" href="#">Sisene <i class="fa fa-arrow-circle-o-right"></i></a>
    </div>
    <div class="mobileIdChallenge"></div>
</div>

${postUrl}

<script>
    function handlerMobileIdResponse(xhr_data, amtTimeout) {
        console.log(xhr_data);
        if(xhr_data.errorCode == 0) {
            if(xhr_data.status == 'USER_AUTHENTICATED') {
                console.log('user authenticated');
                window.location = '${authSuccessUrl}';
            } else if(xhr_data.status == 'OUTSTANDING_TRANSACTION' || xhr_data.status == 'OK') {
                setTimeout(doMobileIdPoll, amtTimeout);
            }
        }
    }

    function doMobileIdPoll() {
        $.ajax({
            url: '${postUrl}',
            dataType: 'json',
            error: function(xhr_data) {
                // terminate the script
            },
            success: function(xhr_data) {
                handlerMobileIdResponse(xhr_data, 5000)
            },
            contentType: 'application/json'
        })
    }

    $('.loginByMobiilIdDoLoginBtn').click(function() {
        var phoneNo = $('.loginByMobiilIdPhoneNoInput').val();
        $.ajax({
            url: '${postUrl}',
            dataType: 'json',
            error: function(xhr_data) {
                // terminate the script
            },
            success: function(xhr_data) {
                $('.mobileIdChallenge').html(xhr_data.challengeId);
                handlerMobileIdResponse(xhr_data, 20000)
            },
            data: {phoneNo: '+372'+phoneNo},
            contentType: 'application/json'
        });
        return false;
    });

</script>
</body>
</html>