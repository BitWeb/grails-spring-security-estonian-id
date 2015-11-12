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

<div class="row">
    <div class="col-xs-12 text-center">
        <p><g:message code="server.home.header.loginTitle" /></p>
        <div class="col-sm-12 text-center">
            <a class="btn btn-outline-inverse btn-lg margin-top-30" href="${createLink(controller: 'customer')}"><g:message code="server.home.header.loginByIdBtn" /> <i class="fa fa-arrow-circle-o-right"></i></a>
        </div>
    </div>
</div>

<div class="col-sm-12 login-mobile-wrapper">
    <div class="login-content">
        <div class="input-group margin-top-30">
            <span class="input-group-addon">+372</span>
            <input type="text" class="form-control loginByMobiilIdPhoneNoInput" placeholder="Telefoninumber">
        </div>
        <a class="btn btn-outline-inverse btn-lg margin-top-30 mobile-login loginByMobiilIdDoLoginBtn" href="#">Sisene <i class="fa fa-arrow-circle-o-right"></i></a>
    </div>
</div>

<script>
    $('.loginByMobiilIdBtn').click(function() {

    });

    function doMobileIdPoll() {
        /*$.post('ajax/test.html', function(data) {

         setTimeout(doPoll, 5000);
         });*/
        $.ajax({
            url: '/spring-security-estonian-id/j_spring_security_estonianid_mobileid_check',
            dataType: 'json',
            error: function(xhr_data) {
                // terminate the script
            },
            success: function(xhr_data) {
                console.log(xhr_data);
                setTimeout(doMobileIdPoll, 5000);
            },
            contentType: 'application/json'
        })
    }

    $('.loginByMobiilIdDoLoginBtn').click(function() {
        var phoneNo = $('.loginByMobiilIdPhoneNoInput').val();
        $.ajax({
            url: '/spring-security-estonian-id/j_spring_security_estonianid_mobileid_check',
            dataType: 'json',
            error: function(xhr_data) {
                // terminate the script
            },
            success: function(xhr_data) {
                console.log(xhr_data);
                setTimeout(doMobileIdPoll, 20000);
            },
            data: {phoneNo: phoneNo},
            contentType: 'application/json'
        });
        return false;
    });

    $("#login-button").click(function () {
        // Set the effect type
        var effect = 'slide';

        // Set the options for the effect type chosen
        var options = { direction: "left" };

        // Set the duration (default: 400 milliseconds)
        var duration = 500;

        $('.call-to-action').animate({"margin-left": '-=100%'},600);
        // $('.login.container').show(effect, {direction: "right"}, 500);



        //$('.login.container').toggle(effect, options, duration);
    });

    $("#login-mobile-button A").click(function () {
        $('#login-mobile-button').hide();
        $('.login-mobile-wrapper').show();
    });

</script>
</body>
</html>