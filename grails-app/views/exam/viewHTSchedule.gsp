<%@ page import="com.grailslab.enums.ExamType; com.grailslab.enums.ScheduleStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Hall Schedule</title>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbText="Exam" firstBreadCrumbUrl="${g.createLink(controller: 'exam',action: 'index')}" secondBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'classExams', id: shiftExamId)}" secondBreadCrumbText="Class List" breadCrumbTitleText="Hall Schedules" SHOW_LINK_BTN="${isExamPublished?'NO':'YES'}"  linkBtnText="Edit Schedule"/>

<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header-title">Exam: </span> <span class="panel-header-info">${examName}</span><span class="panel-header-title">Class: </span> <span class="panel-header-info">${className?.name}</span>
            </header>
        </section>
    </div>
</div>

<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <g:if test="${classSubjects}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover table-bordered" id="list-table">
                            <thead>
                            <tr>
                                <th class="center">SL</th>
                                <th class="center">Exam Date</th>
                                <th class="center">Subject</th>
                                <th class="center">Full Mark</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${classSubjects}" var="dataSet" status="i">
                                <tr>
                                    <td class="center">${i+1}</td>
                                    <td class="center">${dataSet.examDate}</td>
                                    <td class="center">${dataSet.subjectName}</td>
                                    <td class="center">${dataSet.hallMark}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </g:if>
                <g:else>
                    <g:if test='${flash.message}'>
                        <div class="errorHandler alert alert-danger">
                            <i class="fa fa-remove-sign"></i>
                            ${flash.message}
                        </div>

                    </g:if>
                </g:else>
            </div>
        </section>
    </div>
</div>

<script>
    jQuery(function ($) {
        $('#list-table').dataTable({"iDisplayLength": 25});
        $('.link-url-btn').click(function (e) {
            var confirmDel = confirm("Are you sure Edit HT Schedule?");
            if (confirmDel == true) {
                var shiftExam = "${shiftExamId}";
                var className = "${className.id}";
                var examType = "${ExamType.HALL_TEST.key}";
                window.location.href = "${createLink(controller: 'examSchedule', action: 'classSchedule')}?shiftExam="+shiftExam+"&className="+className+"&examType="+examType+"&forceEdit=true";
            }
            e.preventDefault();
        });
    });
</script>
</body>
</html>