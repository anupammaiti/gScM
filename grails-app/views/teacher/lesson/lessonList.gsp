<!DOCTYPE html>
<head>
    <title>Lesson Plan</title>
    <meta name="layout" content="moduleLessonAndFeedbackLayout"/>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'lesson',action: 'index')}" firstBreadCrumbText="Lesson" breadCrumbTitleText="Lesson" SHOW_CREATE_LINK="YES" createLinkText="Add New" createLinkUrl="${g.createLink(controller: 'lesson', action: 'create', params: [id:section?.id])}" SHOW_PRINT_BTN="YES"/>
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
                                    <th class="col-md-1">Subject</th>
                                    <th class="col-md-4">Lesson Topics</th>
                                    <th class="col-md-2">Home Work</th>
                                    <th class="col-md-1">Exam</th>
                                    <th class="col-md-2">Action</th>
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
                                                    <td>
                                                        <span class="col-md-4 no-padding"><a href="${g.createLink(controller: 'lesson',action: 'edit',id:dataSetDetails.id)}" referenceId="${dataSetDetails.id}"
                                                                                             class="edit-reference" title="Edit Lesson"><span
                                                                    class="green glyphicon glyphicon-edit"></span></a></span>
                                                        <span class="col-md-4 no-padding"><a href="" lessonId="${lesson.id}" referenceId="${dataSetDetails.id}"
                                                                                             class="delete-reference"
                                                                                             title="Delete Lesson"><span
                                                                    class="green glyphicon glyphicon-trash"></span></a></span>
                                                    </td>
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
                                                <td>
                                                    <span class="col-md-4 no-padding"><a href="${g.createLink(controller: 'lesson',action: 'edit',id:dataSetDetails.id)}" referenceId="${dataSetDetails.id}"
                                                                                         class="edit-reference" title="Edit Lesson"><span
                                                                class="green glyphicon glyphicon-edit"></span></a></span>
                                                    <span class="col-md-4 no-padding"><a href="" lessonId="${lesson.id}" referenceId="${dataSetDetails.id}"
                                                                                         class="delete-reference"
                                                                                         title="Delete Lesson"><span
                                                                class="green glyphicon glyphicon-trash"></span></a></span>
                                                </td>
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
                window.location.href = "${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section?.id])}?weekNo="+weekNo+"&subjectId="+subjectName;
            }
            e.preventDefault();
        });
        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                var lessonId = $(control).attr('lessonId');
                var weekNo = $('#weekNo').val();
                var subjectName = $('#subjectName').val();
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'lesson',action: 'delete')}?id=" +lessonId +"&lessonDetailId="+referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section?.id])}?weekNo="+weekNo+"&subjectId="+subjectName;
//                            successMsg(data.message);
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