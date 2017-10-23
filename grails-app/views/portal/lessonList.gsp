<!DOCTYPE html>
<head>
    <title>Lesson Plan</title>
    <meta name="layout" content="moduleParentsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'lesson',action: 'index')}" firstBreadCrumbText="Lesson" breadCrumbTitleText="Lesson Plan" SHOW_PRINT_BTN="YES"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <div class="form-horizontal">
                    <div class="form-group">
                        <div class="col-md-4">
                            <g:select tabindex="2" class="form-control" id="weekNo"
                                      name='weekNo' value="${loadWeek}"
                                      noSelection="${['': 'Working Week(s)...']}"
                                      from='${lessonWeekList}'
                                      optionKey="id" optionValue="name"></g:select>
                        </div>
                        <div class="col-md-5">
                            <g:select tabindex="2" class="form-control" id="subjectName"
                                      name='subjectName' value="${loadSubject?.id}"
                                      noSelection="${['': 'All Subject...']}"
                                      from='${subjectList}'
                                      optionKey="id" optionValue="name"></g:select>
                        </div>
                        <div class="col-md-3">
                            <button class="btn btn-info" id="load-btn">Load Lesson Plan</button>
                        </div>
                    </div>
                </div>
            </header>
        </section>
    </div>
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header">Class: </span><span class="panel-header-info">${section.className.name},</span><span class="panel-header">Section: </span><span class="panel-header-info">${section.name},</span><span class="panel-header">Week No: </span><span class="panel-header-info">${loadWeek}</span>
            </header>
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table class="table table-bordered table-striped table-hover dataTable no-footer" id="list-table">
                                <thead>
                                <tr>
                                    <th class="col-md-2">Lesson Date</th>
                                    <th class="col-md-2">Subject</th>
                                    <th class="col-md-4">Lesson Topics</th>
                                    <th class="col-md-2">Home Work</th>
                                    <th class="col-md-2">Exam</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${lessonList}" var="lesson" status="i">
                                    <g:if test="${loadSubject}">
                                        <g:each in="${lesson.lessonDetails}" var="dataSetDetails" status="j">
                                            <g:if test="${dataSetDetails?.subject==loadSubject}">
                                                <tr class="even">
                                                    <td >${lesson?.lessonDate?.format('E, dd MMM yyyy')}</td>
                                                    <td>${dataSetDetails.subject.name}</td>
                                                    <td>${raw(dataSetDetails.topics)}</td>
                                                    <td>${raw(dataSetDetails.homeWork)}</td>
                                                    <td>${dataSetDetails?.examDate?.format('dd-MMM-yy')}</td>
                                                </tr>
                                            </g:if>
                                        </g:each>
                                    </g:if>
                                    <g:else>
                                        <g:each in="${lesson.lessonDetails}" var="dataSetDetails" status="j">
                                            <tr class="even">
                                                <g:if test="${j == 0}">
                                                    <td align="center" class="selected" rowspan="${lesson.lessonDetails.size()}">${lesson?.lessonDate?.format('E, dd MMM yyyy')}</td>
                                                </g:if>
                                                <td>${dataSetDetails.subject.name}</td>
                                                <td>${raw(dataSetDetails.topics)}</td>
                                                <td>${raw(dataSetDetails.homeWork)}</td>
                                                <td>${dataSetDetails?.examDate?.format('dd-MMM-yy')}</td>
                                            </tr>
                                        </g:each>
                                    </g:else>
                                </g:each>
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    $('.print-btn').click(function (e) {
        e.preventDefault();
        var weekNumber = $('#weekNo').val();
        var printSubjectName = $('#subjectName').val();
        var sectionId = "${section?.id}";
        if(weekNumber != ""){
            var sectionParam ="${g.createLink(controller: 'lessonPlan',action: 'download')}?weekNumber="+weekNumber+"&subject="+printSubjectName+"&section="+sectionId;
            window.open(sectionParam);
        }
        return false; // avoid to execute the actual submit of the form.
    });

    jQuery(function ($) {
        $('#load-btn').click(function (e) {
            var weekNo = $('#weekNo').val();
            var subjectName = $('#subjectName').val();
            if((weekNo != "")||(subjectName != "")){
                showLoading("#load-week-plan-holder");
                window.location.href = "${g.createLink(controller: 'portal',action: 'lessonPlan', params: [id:section?.id])}?weekNo="+weekNo+"&subjectId="+subjectName;
            }
            e.preventDefault();
        });
    });

</script>
</body>
</html>