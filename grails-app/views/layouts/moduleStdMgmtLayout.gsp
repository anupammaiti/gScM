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
                        <a href="${g.createLink(controller: 'login', action: 'loginSuccess')}">
                            <i class="fa fa-dashboard"></i>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li class="sub-menu">
                        <a href="javascript:;">
                            <i class="fa fa-tasks"></i>
                            <span>Settings</span>
                        </a>
                        <ul class="sub">
                            <li class="${params.controller == 'className' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'className', action: 'index')}">Class Name</a>
                            </li>
                            <li class="${params.controller == 'classPeriod' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'classPeriod', action: 'index')}">Class Period</a>
                            </li>
                            <li class="${params.controller == 'classRoom' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'classRoom', action: 'index')}">Class Room</a>
                            </li>

                            <li class="${params.controller == 'section' && (params.action == 'index' || params.action == 'moveSection' ) ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'section', action: 'index')}">Section</a>
                            </li>
                            <li class="${params.controller == 'subject' && (params.action == 'index' || params.action == 'sortOrder' || params.action == 'addSubject') ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'subject', action: 'index')}">Subject</a>
                            </li>
                            <li class="${params.controller == 'profession' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'profession', action: 'index')}">Profession</a>
                            </li>
                            <li class="${params.controller == 'gradePoints' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'gradePoints', action: 'index')}">Grade Point</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="${g.createLink(controller: 'registration', action: 'index')}"
                           class="${params.controller == 'registration' && (params.action == 'index' || params.action == 'addDetails') ? 'active' : ''}">
                            <i class="fa fa-user"></i>
                            <span>Registration</span>
                        </a>
                    </li>
                    <li class="sub-menu">
                        <a href="javascript:;">
                            <i class="fa fa-graduation-cap"></i>
                            <span>School Branch</span>
                        </a>
                        <ul class="sub">
                            <li class="${params.controller == 'student' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'student', action: 'index')}">Manage Student</a>
                            </li>
                            <sec:ifAnyGranted roles="ROLE_SUPER_ADMIN,ROLE_ADMIN">
                                <li class="${params.controller == 'student' && params.action == 'admission' ? 'active' : ''}">
                                    <a href="${g.createLink(controller: 'student', action: 'admission')}">New Admission</a>
                                </li>
                            </sec:ifAnyGranted>
                            <li class="${params.controller == 'student' && params.action == 'roleReorder' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'student', action: 'roleReorder')}">Roll Reorder</a>
                            </li>
                            <li class="${params.controller == 'student' && params.action == 'sectionTransfer' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'student', action: 'sectionTransfer')}">Section Migration</a>
                            </li>
                            <li class="${params.controller == 'classSubjects' && (params.action == 'index' || params.action == 'subjects'|| params.action == 'editAllSubject' ) ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'classSubjects', action: 'index')}"> Class Subject </a>
                            </li>
                            <li class="${params.controller == 'classRoutine' && (params.action == 'index' || params.action == 'classRoutineCreate') ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'classRoutine', action: 'index')}"> Class Routine </a>
                            </li>
                            <li class="${params.controller == 'classRoutine' && (params.action == 'teacherRoutine' || params.action == 'teacherRoutineCreate') ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'classRoutine', action: 'teacherRoutine')}"> Teacher Routine </a>
                            </li>
                            <li class="${params.controller == 'studentSubjects' && (params.action == 'index' || params.action == 'subjects' || params.action == 'edit' || params.action == 'commonMapping') ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'studentSubjects', action: 'index')}"> Student Subject </a>
                            </li>
                            <li class="${params.controller == 'transferSubjects' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'transferSubjects', action: 'index')}">Transfer Subject (ST)</a>
                            </li>
                        </ul>
                    </li>
                    <sec:ifAnyGranted roles="ROLE_SUPER_ADMIN,ROLE_ADMIN">
                        <li class="sub-menu">
                            <a href="javascript:;">
                                <i class="glyphicon glyphicon-circle-arrow-down"></i>
                                <span>Promotion </span>
                            </a>
                            <ul class="sub">
                                <li class="${params.controller == 'student' && params.action == 'reAdmission' ? 'active' : ''}">
                                    <a href="${g.createLink(controller: 'student', action: 'reAdmission')}">Promotion</a>
                                </li>
                                <li class="${params.controller == 'student' && params.action == 'promotion' ? 'active' : ''}">
                                    <a href="${g.createLink(controller: 'student', action: 'promotion')}">Promotion(s)/Readmission</a>
                                </li>
                                <li class="${params.controller == 'student' && params.action == 'batchPromotion' ? 'active' : ''}">
                                    <a href="${g.createLink(controller: 'student', action: 'batchPromotion')}">Batch Promotion by Class</a>
                                </li>
                                %{--<li class="${params.controller == 'student' && params.action == 'batchPromotionByExam' ? 'active' : ''}">
                                    <a href="${g.createLink(controller: 'student', action: 'batchPromotionByExam')}">Batch Promotion by Exam</a>
                                </li>--}%
                            </ul>
                        </li>
                    </sec:ifAnyGranted>

                <g:if test="${(grailsApplication.config.grailslab.gschool.running=='nhs')}">
                    <li class="sub-menu">
                        <a href="javascript:;">
                            <i class="fa fa-institution"></i>
                            <span>College Branch</span>
                        </a>
                        <ul class="sub">

                            <li class="${params.controller == 'college' && params.action == 'index' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'college', action: 'index')}">Manage Student</a>
                            </li>
                            <li class="${params.controller == 'college' && params.action == 'admission' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'college', action: 'admission')}">New Admission</a>
                            </li>
                            <li class="${params.controller == 'college' && params.action == 'reAdmission' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'college', action: 'reAdmission')}">Promotion</a>
                            </li>

                            <li class="${params.controller == 'college' && params.action == 'promotion' ? 'active' : ''}">
                                <a href="${g.createLink(controller: 'college', action: 'promotion')}">Promotion(s)/Readmission</a>
                            </li>

                        </ul>
                    </li>
                </g:if>
                    <li>
                        <a href="${g.createLink(controller: 'student', action: 'tc')}"
                           class="${params.controller == 'student' && (params.action == 'tc'|| params.action == 'transferCertificate') ? 'active' : ''}">
                            <i class="fa fa-dropbox"></i>
                            <span>Tc & Dropout</span>
                        </a>
                    </li>
                    <li>
                        <a href="${g.createLink(controller: 'student', action: 'report')}"
                           class="${params.controller == 'student' && params.action == 'report' ? 'active' : ''}">
                            <i class="fa fa-file"></i>
                            <span>Reports</span>
                        </a>
                    </li>
                </ul>
            </div>
            <g:render template='/appVersion'/>
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