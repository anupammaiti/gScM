<%@ page import="com.grailslab.enums.ExamTerm" %>
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
                        <g:hiddenField name="lessonId" id="lessonId" value="${lessonObj?.id}"/>
                        <g:hiddenField name="lessonDetailId" id="lessonDetailId" value="${lessonDetails.id}"/>
                        <div class="row">
                        <div class="form-group">
                            <label for="employee" class="col-md-2 control-label">Teacher *</label>

                            <div class="col-md-8">
                                <select class="form-control" id="employee" name="employee" tabindex="1">
                                    <option value="${lessonDetails?.employee?.id}">${lessonDetails?.employee?.empID} - ${lessonDetails?.employee?.name}</option>
                                </select>
                            </div>
                        </div>
                            <div class="form-group">
                                <label for="examTerm" class="col-md-2 control-label">Exam Term *</label>

                                <div class="col-md-8">
                                    <g:select class="form-control" id="examTerm" name='examTerm' tabindex="1"
                                              noSelection="${['': 'Select Term...']}"
                                              from='${ExamTerm.values()}'
                                              optionKey="key" optionValue="value" value="${lessonObj?.examTerm.key}"></g:select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="subject" class="col-md-2 control-label">Subjects *</label>

                                <div class="col-md-8">
                                    <g:select class="form-control" id="subject" name='subject' tabindex="2"
                                              noSelection="${['': 'Select Subject...']}"
                                              from='${subjectList}'
                                              optionKey="id" optionValue="name" value="${lessonDetails?.subject?.id}"></g:select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="lessonDate" class="col-md-2 control-label">Lesson Date *</label>

                                <div class="col-md-3">
                                    <input class="form-control col-md-12" type="text" name="lessonDate" id="lessonDate" value="<g:formatDate format="dd/MM/yyyy" date="${lessonObj?.lessonDate}"/>"
                                           tabindex="6"/>
                                </div>

                                <label for="examDate" class="col-md-2 control-label">Exam Date</label>

                                <div class="col-md-3">
                                    <input class="form-control col-md-12" type="text" name="examDate" id="examDate" value="<g:formatDate format="dd/MM/yyyy" date="${lessonDetails?.examDate}"/>" tabindex="6"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="topics" class="col-sm-2 control-label">Topics</label>

                                <div class="col-sm-8">
                                    <textarea id="topics" name="topics" class="form-control add-html-content-area">${raw(lessonDetails.topics)}</textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="homeWork" class="col-sm-2 control-label">Home Work</label>

                                <div class="col-sm-8">
                                    <textarea id="homeWork" name="homeWork" class="form-control add-html-content-area">${raw(lessonDetails.homeWork)}</textarea>
                                </div>
                            </div>

                            <br>

                            <div class="row">
                                <div class="form-group">
                                    <div class="col-md-offset-8 col-lg-4">
                                        <button class="btn btn-primary" tabindex="2" type="submit">Update</button>
                                        <button class="btn btn-default cancel-btn" tabindex="3">Cancel</button>
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

    jQuery(function ($) {
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
                showLoading("#create-form-holder");
                $.ajax({
                    url: "${createLink(controller: 'lesson', action: 'update')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        hideLoading("#create-form-holder");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'lesson',action: 'create', params: [id:section?.id])}?examTerm="+examTerm;
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });
        $(".cancel-btn").click(function (e) {
            e.preventDefault();
            window.location.href = "${g.createLink(controller: 'lesson',action: 'lessonPlan', params: [id:section?.id])}";
        });
    });

</script>
</body>
</html>