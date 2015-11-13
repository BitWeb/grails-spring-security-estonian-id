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
        <div class="col-sm-12 text-center">
            <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByIdBtn" href="#">Logi sisse Id-kaardiga <i class="fa fa-arrow-circle-o-right"></i></a>
        </div>
    </div>
</div>

<script>

    $('.loginByIdBtn').click(function() {
        $.ajax({
            url: '${postUrl}',
            dataType: 'json',
            error: function(xhr_data) {
                alert('login error');
            },
            success: function(xhr_data) {
                console.log(xhr_data);
                window.location = '${authSuccessUrl}';
            },
            contentType: 'application/json'
        });
        return false;
    });

</script>
</body>
</html>