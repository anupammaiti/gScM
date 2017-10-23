<!DOCTYPE html>
<head>
    <title>Lesson Plan</title>
    <meta name="layout" content="moduleLessonAndFeedbackLayout"/>
    <script src="//tinymce.cachefly.net/4.1/tinymce.min.js"></script>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section?.id])}" firstBreadCrumbText="Lesson Plan" breadCrumbTitleText="${section?.className?.name?.toUpperCase()} - ${section?.name?.toUpperCase()}"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <div class="col-lg-12">
                    <form class="cmxform form-horizontal" id="create-form">
                        <div class="row">
                        <div class="form-group">
                            <label for="employee" class="col-md-2 control-label">Teacher *</label>

                            <div class="col-md-3">
                                <select class="form-control" id="employee" name="employee" tabindex="1">
                                    <option value="${workingTeacher?.id}">${workingTeacher?.empID} - ${workingTeacher?.name}</option>
                                </select>
                            </div>
                            <label for="employee" class="col-md-2 control-label">Section Name *</label>

                            <div class="col-md-3">
                                <g:select class="form-control" id="section" value="${section?.id}" name='section' tabindex="2"
                                          from='${sectionList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                        </div>
                        <div class="form-group">
                                <label for="examTerm" class="col-md-2 control-label">Exam Term *</label>

                                <div class="col-md-3">
                                    <g:select class="form-control" id="examTerm" name='examTerm' tabindex="1"
                                              from='${com.grailslab.enums.ExamTerm.values()}' value="${examTerm?.key}"
                                              optionKey="key" optionValue="value"></g:select>
                                </div>
                                <label for="subject" class="col-md-2 control-label">Subjects *</label>

                                <div class="col-md-3">
                                    <g:select class="form-control" id="subject" name='subject' value="${subject?.id}" tabindex="3"
                                              noSelection="${['': 'Select Subject...']}"
                                              from='${subjectList}'
                                              optionKey="id" optionValue="name"></g:select>
                                </div>
                        </div>
                        <div class="form-group">
                                <label for="lessonDate" class="col-md-2 control-label">Lesson Date *</label>

                                <div class="col-md-3">
                                    <input class="form-control col-md-12" type="text" name="lessonDate"
                                           id="lessonDate" value="<g:formatDate format="dd/MM/yyyy" date="${lessonDate}"/>"
                                           tabindex="4"/>
                                </div>

                                <label for="examDate" class="col-md-2 control-label">Exam Date</label>

                                <div class="col-md-3">
                                    <input class="form-control col-md-12" type="text" name="examDate"
                                           id="examDate"
                                           tabindex="5"/>
                                </div>
                        </div>

                            <div class="form-group">
                                <label for="topics" class="col-sm-2 control-label">Topics</label>

                                <div class="col-sm-8">
                                    <textarea id="topics" tabindex="6" name="topics" class="form-control add-html-content-area"></textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="homeWork" class="col-sm-2 control-label">Home Work</label>

                                <div class="col-sm-8">
                                    <textarea id="homeWork" tabindex="7" name="homeWork" class="form-control add-html-content-area"></textarea>
                                </div>
                            </div>
                            <div class="row">
                                <div class="form-group">
                                    <div class="col-md-offset-8 col-lg-4">
                                        <button class="btn btn-primary" tabindex="8" type="submit">Save</button>
                                        <button class="btn btn-default cancel-btn" tabindex="9"
                                                type="reset">Cancel</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>

                </div>
            </div>
        </section>
    </div>
</div>
<g:if test="${lessonList}">
    <div class="row">
        <div class="col-sm-12">
            <section class="panel">
                <header class="panel-heading">
                    Your Today's working Lesson(s)
                </header>
                <div class="panel-body">
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table class="table table-bordered table-striped table-hover dataTable no-footer" id="list-table">
                                <thead>
                                <tr>
                                    <th class="col-md-2">Lesson Date</th>
                                    <th class="col-md-1">Week</th>
                                    <th class="col-md-1">Subject</th>
                                    <th class="col-md-3">Lesson Topics</th>
                                    <th class="col-md-2">Home Work</th>
                                    <th class="col-md-1">Exam</th>
                                    <th class="col-md-2">Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${lessonList}" var="lesson" status="i">
                                    <g:each in="${lesson.lessonDetails}" var="dataSetDetails" status="j">
                                        <tr class="even">
                                            <g:if test="${j == 0}">
                                                <td align="center" class="selected" rowspan="${lesson.lessonDetails.size()}">${lesson?.lessonDate?.format('E, dd MMM yyyy')}</td>
                                            </g:if>
                                            <td>${lesson.weekNumber}</td>
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
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</g:if>

<script>
    tinymce.init({
        setup: function (editor) {
            editor.on('change', function () {
                tinymce.triggerSave();
            });
        },
        theme: "modern",
        mode : "specific_textareas",
        selector:'textarea.add-html-content-area',
        menubar: false,
        toolbar_items_size: 'small',
        plugins: [
            "lists hr anchor pagebreak spellchecker wordcount code fullscreen insertdatetime nonbreaking table textcolor"
        ],
        toolbar1: "bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | fontsizeselect | bullist numlist | forecolor | table"

    });

</script>
<script>
    jQuery(function ($) {
        $("#topics").focus();
        $('#lessonDate').datepicker({
            format: 'dd/mm/yyyy',
            endDate: '+90d',
            autoclose: true
        });

        $('#examDate').datepicker({
            format: 'dd/mm/yyyy',
            endDate: '+90d',
            autoclose: true
        });
        var validator = $('#create-form').validate({
            errorElement: 'span',
            focusInvalid: false,
            rules: {
                examTerm: {
                    required: true
                },
                section: {
                    required: true
                },
                lessonDate: {
                    required: true
                },
                subject: {
                    required: true
                }
            },
            highlight: function (e) {
                $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
            },
            success: function (e) {
                $(e).closest('.form-group').removeClass('has-error').addClass('has-info');
                $(e).remove();
            },
            submitHandler: function (form) {
                if(!$("#topics").val()) {
                    alert("Please provide Lesson Topic.");
                    return false;
                }
                var examTerm = $("#examTerm").val();
                var sectionName = $("#section").val();
                var subjectName = $("#subject").val();
                var lessonDate = $("#lessonDate").val();
                showLoading("#create-form-holder");
                $.ajax({
                    url: "${createLink(controller: 'lesson', action: 'save')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        hideLoading("#create-form-holder");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'lesson',action: 'create', params: [id:section?.id])}?examTerm="+examTerm+"&subject="+subjectName+"&lessonDate="+lessonDate;
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });
        $('#section').on('change', function (e) {
            e.preventDefault();
                var section = $("#section").val();
                var examTerm = $("#examTerm").val();
                var subjectName = $("#subject").val();
                var lessonDate = $("#lessonDate").val();
                window.location.href = "${g.createLink(controller: 'lesson',action: 'create')}?id="+section+"&examTerm="+examTerm+"&subject="+subjectName+"&lessonDate="+lessonDate;
        });
        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                var lessonId = $(control).attr('lessonId');
                var examTerm = $("#examTerm").val();
                var subjectName = $("#subject").val();
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'lesson',action: 'delete')}?id=" +lessonId +"&lessonDetailId="+referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'lesson',action: 'create', params: [id:section?.id])}?examTerm="+examTerm+"&subject="+subjectName;;
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

        $(".cancel-btn").click(function (e) {
            e.preventDefault();
            window.location.href = "${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section?.id])}";
        });
    });

</script>
</body>
</html>