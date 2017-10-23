<%@ page import="com.grailslab.enums.ExamType; com.grailslab.enums.GroupName; com.grailslab.enums.ExamTerm; com.grailslab.enums.Shift" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Exam &  Schedule </title>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'index')}" firstBreadCrumbText="Exam"  secondBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'classExams', params: [id:shiftExam?.id])}" secondBreadCrumbText="Class List"  breadCrumbTitleText="Manage Schedule" />
<div class="row" id="tc-datalist-holder">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header-title">Exam: </span> <span class="panel-header-info">${shiftExam.examName} </span><span class="panel-header-title">Class: </span> <span class="panel-header-info">${className?.name} </span>
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Class</th>
                            <th>Section</th>
                            <th >Schedule Status</th>
                            <th >Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${dataReturn}" var="dataSet">
                            <tr>
                                <td>${dataSet[0]}</td>
                                <td>${dataSet[1]}</td>
                                <td>${dataSet[2]}</td>
                                <td>
                                    <g:if test="${shiftExam.isCtExam}">
                                        <a class="btn btn-sm btn-info" href="<g:createLink controller="examSchedule" action="ctSchedule" params='[id: "${dataSet.DT_RowId}"]'/>" title="Manage CT Schedule">CT Schedule</a>
                                        <g:if test="${dataSet.ctSchedule == com.grailslab.enums.ScheduleStatus.ADDED.key}">
                                            <a class="btn btn-sm btn-info publish-ct-btn" referenceId="${dataSet.DT_RowId}" title="Publish CT Schedule" href="#">Publish CT</a>
                                        </g:if>
                                    </g:if>
                                    <g:if test="${shiftExam.isHallExam}">
                                        <a class="btn btn-sm btn-success" href="<g:createLink controller="examSchedule" action="htSchedule" params='[id: "${dataSet.DT_RowId}"]'/>" title="Manage Hall Schedule">Hall Schedule</a>
                                        <g:if test="${dataSet.hallSchedule == com.grailslab.enums.ScheduleStatus.ADDED.key}">
                                            <a class="btn btn-sm btn-success publish-hall-btn" referenceId="${dataSet.DT_RowId}" title="Publish Hall Schedule" href="#">Publish Hall</a>
                                        </g:if>
                                    </g:if>
                                    <a href="" referenceId="${dataSet.DT_RowId}" class="btn btn-sm btn-danger changeStatus-reference" title="Delete Exam">Delete Exam</a>
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
    var shift,examTerm,examType,sections,className, groupName, shiftExam, isCtExam, isHallExam;
    jQuery(function ($) {
        $('.create-new-btn').click(function (e) {
            e.preventDefault();
        });

        $('#list-table').dataTable({
            "iDisplayLength": 25
        });

        $('#list-table').on('click', 'a.changeStatus-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure delete Exam?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'exam',action: 'inactive')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'sectionExams', params: [id: shiftExam.id, className:className.id])}";
                            $("#list-table").DataTable().row(selectRow).remove().draw(false);
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
        $('#list-table').on('click', 'a.publish-ct-btn', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure publish CT exam?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#bread-action-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'publishCTSchedule')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        hideLoading("#bread-action-holder");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'sectionExams', params: [id: shiftExam.id, className:className.id])}";
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
                var referenceId = $(control).attr('referenceId');
                showLoading("#bread-action-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'publishHTSchedule')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        hideLoading("#bread-action-holder");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'sectionExams', params: [id: shiftExam.id, className:className.id])}";
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