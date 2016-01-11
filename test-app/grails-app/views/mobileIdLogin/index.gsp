<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ==" crossorigin="anonymous">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" integrity="sha384-aUGj/X2zp5rLCbBxumKTCw2Z50WgIr1vs/PFN4praOTvYXWlVyh2UtNUU0KAUhAX" crossorigin="anonymous">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ==" crossorigin="anonymous"></script>

    <style>
    .fa-spinner {
        -animation: spin .7s infinite linear;
        -webkit-animation: spin2 .7s infinite linear;
    }

    @-webkit-keyframes spin2 {
        from { -webkit-transform: rotate(0deg);}
        to { -webkit-transform: rotate(360deg);}
    }

    @keyframes spin {
        from { transform: scale(1) rotate(0deg);}
        to { transform: scale(1) rotate(360deg);}
    }
    .loginByMobileIdFormCtx {
        display: none;
    }
    </style>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-xs-12 text-center">
            <p><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.title" /></p>
        </div>
    </div>
    <div class="row loginByIdCardCtx">
        <div class="col-sm-offset-4 col-md-offset-4 col-lg-offset-4 col-sm-4 col-md-4 col-lg-4 text-center">
            <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByIdCardBtn" href="#" style="display: block;float: none;width: auto !important;"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.loginByIdCard" /> <i class="fa fa-arrow-circle-o-right"></i></a>
            <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByIdCardLoader" href="#" style="display: none;float: none;width: auto !important;"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.loginByIdCard" /> <i class="fa fa-spinner"></i></a>
        </div>
    </div>
    <div class="row loginByMobileIdCtx">
        <div class="col-sm-offset-4 col-md-offset-4 col-lg-offset-4 col-sm-4 col-md-4 col-lg-4 text-center">
            <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByMobileIdBtn" href="#" style="display: block;float: none;width: auto !important;"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.loginByMobileId" /> <i class="fa fa-arrow-circle-o-right"></i></a>
        </div>
    </div>
    <div class="row loginByMobileIdFormCtx">
        <div class="col-sm-12 text-center">
            <div class="row">
                <div class="col-xs-12 text-center">
                    <p><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.loginByMobileId" /></p>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-offset-4 col-md-offset-4 col-lg-offset-4 col-sm-4 col-md-4 col-lg-4 text-center" style="">
                    <div class="" style="">
                        <div class="input-group margin-top-30" style="float: none;width: auto !important;">
                            <span class="input-group-addon">+372</span>
                            <input type="text" class="form-control loginByMobiilIdPhoneNoInput" placeholder="${message(code: 'ee.bitweb.grails.springsercurity.estonianid.login.mobileIdPhoneNo')}">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-offset-4 col-md-offset-4 col-lg-offset-4 col-sm-4 col-md-4 col-lg-4 text-center">
                    <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByMobileIdLoginBtn" style="float: none;width: auto !important;" href="#"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.mobileIdBeginAuth" /> <i class="fa fa-arrow-circle-o-right"></i></a>
                    <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByMobiilIdLoader" style="display: none;float: none;width: auto !important;" href="#"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.mobileIdProcess" /> <i class="fa fa-spinner"></i></a>
                    <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByMobiilIdChallengeId" style="display: none;float: none;width: auto !important;" href="#"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.mobileIdChallengeId" />: <span class="challengeCode">1234</span> <i class="fa fa-spinner"></i></a>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-offset-4 col-md-offset-4 col-lg-offset-4 col-sm-4 col-md-4 col-lg-4 text-center">
                    <a class="btn btn-outline-inverse btn-lg margin-top-30 loginByMobileIdCancelBtn" style="float: none;width: auto !important;" href="#"><i class="fa fa-arrow-circle-o-left"></i> <g:message code="ee.bitweb.grails.springsercurity.estonianid.login.mobileIdCancel" /></a>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade idCardloginErrorModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><g:message code="ee.bitweb.grails.springsercurity.estonianid.idCard.error.title" /></h4>
            </div>
            <div class="modal-body">
                <p><g:message code="ee.bitweb.grails.springsercurity.estonianid.idCard.error.message" /></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.close" /></button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade mobileIdloginErrorModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><g:message code="ee.bitweb.grails.springsercurity.estonianid.mobileId.error.title" /></h4>
            </div>
            <div class="modal-body mobileIdloginErrorModalMessage">
                <p></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><g:message code="ee.bitweb.grails.springsercurity.estonianid.login.close" /></button>
            </div>
        </div>
    </div>
</div>

<g:javascript>
    var fDoMobileIdPoll = false;
    var mobileIdTimeoutHandle = null;

    var mobileIdStatusMessages = {
        status: {
            notValid: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.notValid')}',
            expiredTransaction: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.expiredTransaction')}',
            userCancel: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.userCancel')}',
            midNotReady: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.midNotReady')}',
            phoneAbsent: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.phoneAbsent')}',
            sendingError: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.sendingError')}',
            simError: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.simError')}',
            internalError: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.mobileId.status.internalError')}'
        },
        errorCode: {
            code0: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.digidocservice.error.code0')}',
            code300: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.digidocservice.error.code300')}',
            code301: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.digidocservice.error.code301')}',
            code303: '${message(code: 'ee.bitweb.grails.springsercurity.estonianid.digidocservice.error.code303')}'
        }
    };

    function startMobileIdPolling(phoneNo) {
        fDoMobileIdPoll = true;
        doMobileIdPoll(phoneNo);
    }

    function cancelMobileIdPolling() {
        fDoMobileIdPoll = false;
        if(mobileIdTimeoutHandle) {
            clearTimeout(mobileIdTimeoutHandle);
        }
        $('.loginByMobileIdLoginBtn').show();
        $('.loginByMobiilIdLoader').hide();
        $('.loginByMobiilIdChallengeId').hide();
    }

    function idCardLoginError() {
        $('.loginByIdCardLoader').hide();
        $('.loginByIdCardBtn').show();
        $('.loginByMobileIdCtx').show();
        $('.idCardloginErrorModal').modal('show');
    }

    function mobileIdLoginError(errorCode, status) {
        cancelMobileIdPolling();
        if(errorCode != 0) {
            if(errorCode == '300') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.errorCode['code300']);
            } else if(errorCode == '301') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.errorCode['code301']);
            } else if(errorCode == '303') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.errorCode['code303']);
            } else {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.errorCode['code0']);
            }
        } else {
            if(status == 'NOT_VALID') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['notValid']);
            } else if(status == 'EXPIRED_TRANSACTION') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['expiredTransaction']);
            } else if(status == 'USER_CANCEL') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['userCancel']);
            } else if(status == 'MID_NOT_READY') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['midNotReady']);
            } else if(status == 'PHONE_ABSENT') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['phoneAbsent']);
            } else if(status == 'SENDING_ERROR') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['sendingError']);
            } else if(status == 'SIM_ERROR') {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['simError']);
            } else {
                $('.mobileIdloginErrorModalMessage p').html(mobileIdStatusMessages.status['internalError']);
            }
        }
        $('.mobileIdloginErrorModal').modal('show');
        //alert('mobile id login error ' + errorCode + ' ' + status);
    }

    function doMobileIdPoll(phoneNo, amtTimeout) {
        if(fDoMobileIdPoll) {
            if (!amtTimeout) amtTimeout = 20000;
            $.ajax({
                url: '${authMobileIdUrl}',
                dataType: 'json',
                error: function (xhr_data) {
                    mobileIdLoginError(-1, '');
                },
                success: function (xhr_data) {
                    //console.log(xhr_data);
                    if (xhr_data.errorCode == 0) {
                        if (xhr_data.status == 'USER_AUTHENTICATED') {
                            window.location = '${authSuccessUrl}';
                        } else if (xhr_data.status == 'OUTSTANDING_TRANSACTION' || xhr_data.status == 'OK') {
                            $('.loginByMobiilIdLoader').hide();
                            $('.loginByMobiilIdChallengeId .challengeCode').html(xhr_data.challengeId);
                            $('.loginByMobiilIdChallengeId').show();
                            mobileIdTimeoutHandle = setTimeout(function () {
                                doMobileIdPoll(phoneNo, 5000);
                            }, amtTimeout);
                        } else {
                            mobileIdLoginError(xhr_data.errorCode, xhr_data.status);
                        }
                    } else {
                        mobileIdLoginError(xhr_data.errorCode, xhr_data.status);
                    }
                },
                data: {phoneNo: phoneNo},
                contentType: 'application/json'
            });
        }
    }

	$('.loginByIdCardBtn').click(function() {
        $('.loginByMobileIdCtx').hide();
        $('.loginByIdCardBtn').hide();
        $('.loginByIdCardLoader').show();
		$.ajax({
			url: '${authIdCardUrl}',
			dataType: 'json',
			error: function(xhr_data) {
                idCardLoginError();
			},
			success: function(xhr_data) {
				window.location = '${authSuccessUrl}';
			},
			contentType: 'application/json'
		});
        return false;
	});

    $('.loginByMobileIdBtn').click(function () {
        $('.loginByIdCardCtx').hide();
        $('.loginByMobileIdCtx').hide();
        $('.loginByMobileIdFormCtx').show();
        return false;
    });

    $('.loginByMobileIdCancelBtn').click(function () {
        cancelMobileIdPolling();
        $('.loginByMobileIdFormCtx').hide();
        $('.loginByMobileIdCtx').show();
        $('.loginByIdCardCtx').show();
        return false;
    });

    $('.loginByMobileIdLoginBtn').click(function() {
        var phoneNo = '+372'+$('.loginByMobiilIdPhoneNoInput').val();
        $('.loginByMobileIdLoginBtn').hide();
        $('.loginByMobiilIdLoader').show();
        startMobileIdPolling(phoneNo);
        return false;
    });

</g:javascript>

</body>
</html>
