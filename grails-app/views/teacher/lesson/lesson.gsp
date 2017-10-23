<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleLessonAndFeedbackLayout"/>
    <title>Lesson <g:message code="top.header.brand" default="| GrailsLab"/></title>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'lesson',action: 'index')}" firstBreadCrumbText="Lesson" breadCrumbTitleText="Section List" controllerName="${params.controller}"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <table class="table table-striped table-hover table-bordered" id="list-table">
                    <thead>
                    <tr>
                        <th>SL</th>
                        <th>Class</th>
                        <th>Section</th>
                        <th>In Charge</th>
                        <th>Shift</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${sectionList}" var="section" status="i">
                        <tr>
                            <td>${i+1}</td>
                            <td>${section.className?.name}</td>
                            <td>${section.name}</td>
                            <td>${section.employee?.name}</td>
                            <td>${section.shift?.value}</td>
                            <td>
                                <sec:access controller="lesson" action="create">
                                    <span class="col-md-6 no-padding"><a href="${g.createLink(controller: 'lesson',action: 'create', params: [id:section.id])}"
                                                                         class="create-reference" title="Create Lesson Plan"><span
                                                class="green fa fa-plus"></span></a></span>
                                </sec:access>
                                <sec:access controller="lesson" action="lessonPlan">
                                    <span class="col-md-6 no-padding"><a href="${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section.id])}"
                                                                         class="view-reference" title="View Lesson Plan"><span
                                                class="green glyphicon glyphicon-tasks"></span></a></span>
                                </sec:access>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</div>
<script>
    jQuery(function ($) {
        var table = $('#list-table').dataTable({
            "sDom": "<'row'<'col-md-6'><'col-md-6'f>r>t<'row'<'col-md-4'l><'col-md-4'i><'col-md-4'p>>",
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
    });
</script>
</body>
</html>
