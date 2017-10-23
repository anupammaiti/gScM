<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle/> <g:message code="app.header.title"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: g.message(code: 'app.school.image.folder') + '/favicon.ico')}"
          type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    <g:layoutHead/>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
        <![endif]-->
</head>
<body>
<section id="container">
    <!--header start-->
    <g:render template='/layouts/adminHeaderMenu'/>
    <!--header end-->

    <aside>

    <div id="sidebar" class="nav-collapse">
        <div class="leftside-navigation">
        <ul class="sidebar-menu" id="nav-accordion">
            <li>
                <a href="${g.createLink(controller: 'accounts')}"
                   class="${params.controller == 'accounts' && params.action == 'index' ? 'active' : ''}">
                    <i class="fa fa-tachometer"></i>
                    <span>Dashboard</span>
                </a>
            </li>
            <li class="sub-menu">
                <a class="pushActive" href="javascript:;">
                    <i class="fa fa-money"></i>
                    <span>Settings</span>
                </a>
                <ul class="sub">
                    <li class="${params.controller == 'accounts' && params.action == 'chart' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'accounts', action: 'chart')}">Chart of Account</a>
                    </li>
                    <li class="${params.controller == 'accounts' && (params.action == 'voucher' || params.action == 'createVoucher')? 'active' : ''}">
                        <a href="${g.createLink(controller: 'accounts', action: 'voucher')}">Voucher</a>
                    </li>
                    <li class="${params.controller == 'accounts' && params.action == 'transaction' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'accounts', action: 'transaction')}">Transaction</a>
                    </li>
                    <li class="${params.controller == 'accountingReport' && params.action == 'index' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'accountingReport', action: 'index')}">Accounting Reports</a>
                    </li>
                    <li class="${params.controller == 'accountingReport' && params.action == 'financialReport' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'accountingReport', action: 'financialReport')}">Financial Reports</a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="${g.createLink(controller: 'glabPayment', action: 'index')}"
                   class="${params.controller == 'glabPayment' && params.action == 'index' ? 'active' : ''}">
                    <i class="fa fa-cc-visa"></i>
                    <span>GrailsLab Payment</span>
                </a>
            </li>
        </ul>
    </div>
</div>

    </aside>
    <!--sidebar end-->
    <!--main content start-->
    <section id="main-content">
        <section class="wrapper">
            <!-- page start-->
            <g:layoutBody/>
            <!-- page end-->
        </section>
    </section>
    <!--main content end-->
</section>

</body>
</html>
<script>
    jQuery(function ($) {
        $('ul.sidebar-menu >li >ul.sub >li.active:first').parent().show();
        $('ul.sidebar-menu >li >ul.sub >li.active:first').parent().parent().children('a.pushActive').addClass('active');
    });
</script>