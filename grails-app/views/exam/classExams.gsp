<%@ page import="com.grailslab.enums.ExamType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Class Exam & Schedule </title>
</head>
<body>
<grailslab:breadCrumbActions   firstBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'index')}" firstBreadCrumbText="Exam"   breadCrumbTitleText="Manage Class Schedule"/>
<div class="row" id="tc-datalist-holder">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header-title">Exam: </span> <span class="panel-header-info">${shiftExam.examName} </span>
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>SL</th>
                            <th>Class</th>
                            <th>Exam Type</th>
                            <th >Manage CT Schedule</th>
                            <th >Manage Hall Schedule</th>
                            <th >Manage Synchronize</th>
                        </tr>

                        </thead>
                        <tbody>
                        <g:each in="${classExams}" var="dataSet" status="i">
                            <tr>
                                <td>${i+1}</td>
                                <td>${dataSet.name}</td>
                                <td>${shiftExam.examTerm?.value}</td>
                                <td>
                                    <g:if test="${shiftExam.isCtExam}">
                                        <a class="btn btn-sm btn-info " title="CT Schedule" href="${g.createLink(controller: 'examSchedule', action: 'classSchedule', params: [shiftExam: shiftExam.id, className:dataSet.clasId,examType:ExamType.CLASS_TEST.key])}">CT Schedules</a>
                                        <g:if test="${!dataSet.isExamPublished}">
                                            <g:if test="${dataSet.isCtSchedulePublished}">
                                                <a class="btn btn-sm btn-primary" title="CT Published" ><span class="glyphicon glyphicon-ok"></span> Published</a>
                                            </g:if>
                                            <g:else>
                                                <g:if test="${dataSet.isCtScheduleAdded}">
                                                    <a class="btn btn-sm btn-success publish-ct-btn" title="Publish CT Schedule" shiftExamId="${shiftExam.id}" classNameId ="${dataSet.clasId}">Publish CT</a>
                                                </g:if>
                                            </g:else>
                                        </g:if>
                                    </g:if>
                                    <g:else>No Exam</g:else>
                                </td>
                                <td>
                                    <g:if test="${shiftExam.isHallExam}">
                                        <a class="btn btn-sm btn-info " title="Hall Schedule" href="${g.createLink(controller: 'examSchedule', action: 'classSchedule', params: [shiftExam: shiftExam.id, className:dataSet.clasId,examType:ExamType.HALL_TEST.key])}">Hall Schedules</a>
                                        <g:if test="${!dataSet.isExamPublished}">
                                            <g:if test="${dataSet.isHtSchedulePublished}">
                                                <a class="btn btn-sm btn-primary" title="Hall Published" ><span class="glyphicon glyphicon-ok"></span> Published</a>
                                            </g:if>
                                            <g:else>
                                                <g:if test="${dataSet.isHtScheduleAdded}">
                                                    <a class="btn btn-sm btn-success publish-hall-btn" title="Publish Hall Schedule" shiftExamId="${shiftExam.id}" classNameId ="${dataSet.clasId}">Publish Hall</a>
                                                </g:if>
                                            </g:else>
                                        </g:if>
                                    </g:if>
                                    <g:else>No Exam</g:else>
                                </td>
                                <td>
                                    <g:if test="${dataSet.isCtSchedulePublished || dataSet.isHtSchedulePublished}">
                                        <a class="btn btn-sm btn-success synchronize-class-subject" title="Synchronize with Class subject mark settings" href="#" shiftExamId="${shiftExam.id}" classNameId ="${dataSet.clasId}"><span class="red glyphicon glyphicon-refresh"></span> Synchronize</a>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<script>
    jQuery(function ($) {
        $('#list-table').dataTable({
            "sDom": "<'row'<'col-md-6'><'col-md-6'>r>t<'row'<'col-md-4'l><'col-md-4'i><'col-md-4'p>>",
            "bAutoWidth": true,
            "iDisplayLength": 25,
            "aaSorting": [0,'asc'],
            "aoColumns": [
                null,
                null,
                null,
                null,
                null,
                { "bSortable": false }
            ]
        });
        $('#list-table').on('click', 'a.publish-ct-btn', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure publish CT exam?");
            if (confirmDel == true) {
                var control = this;
                var shiftExamId = $(control).attr('shiftExamId');
                var classNameId = $(control).attr('classNameId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'publishCTSchedule')}/" + shiftExamId+"?className="+classNameId,
                    success: function (data, textStatus) {
                        hideLoading("#list-table");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'classExams', params: [id: shiftExam.id])}";
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.publish-hall-btn', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure publish Hall exam schedule?");
            if (confirmDel == true) {
                var control = this;
                var shiftExamId = $(control).attr('shiftExamId');
                var classNameId = $(control).attr('classNameId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'publishHTSchedule')}/" + shiftExamId+"?className="+classNameId,
                    success: function (data, textStatus) {
                        hideLoading("#list-table");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'classExams', params: [id: shiftExam.id])}";
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
        $('#list-table').on('click', 'a.synchronize-class-subject', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure synchronize with class subject mark settings?");
            if (confirmDel == true) {
                $(this).html("Synchronized").removeClass( "btn-success").addClass( "btn-primary");
                var control = this;
                var shiftExamId = $(control).attr('shiftExamId');
                var classNameId = $(control).attr('classNameId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'syncClassSubject')}/" + shiftExamId+"?className="+classNameId,
                    success: function (data, textStatus) {
                        hideLoading("#list-table");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
    });

</script>
</body>
</html>