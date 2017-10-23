<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Hall Test Schedule</title>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbText="Exam" firstBreadCrumbUrl="${g.createLink(controller: 'exam',action: 'index')}" secondBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'classExams', id: shiftExamId)}" secondBreadCrumbText="Class List" breadCrumbTitleText="Hall Schedule" SHOW_EXTRA_BTN1="${isEditSchedule?'YES':'NO'}" extraBtn1Text="Confirm Schedule and Publish"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header-title">Class: </span> <span class="panel-header-info">${className?.name}</span>  <span class="panel-header-title">Exam: </span> <span class="panel-header-info">${examName}</span><span class="panel-header-title">Schedule: </span> <span class="panel-header-info">Hall Exam</span>
            </header>
            <div class="panel-body">
                <g:if test='${flash.message}'>
                    <div class="errorHandler alert alert-danger">
                        <i class="fa fa-remove-sign"></i>
                        ${flash.message}
                    </div>
                </g:if>
                <g:else>
                    <form class="cmxform" id="create-form">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover table-bordered">
                                <thead>
                                <tr>
                                    <th >Serial</th>
                                    <th class="center">Select</th>
                                    <th>Subject</th>
                                    <th>Hall Exam Mark</th>
                                    <th> <g:select class="form-control" name="examPeriodAll" id="examPeriodAll" tabindex="5"
                                                   noSelection="${['': 'Select Exam Time All...']}"
                                                   from='${examPeriodList}'
                                                   optionKey="id" optionValue="name"></g:select></th>
                                    <th>Exam Date</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${classSubjects}" var="classSubject" status="i">
                                    <tr>
                                    <td>${i+1}</td>
                                    <td class="center"><input type="checkbox" name="subjectIds" value="${classSubject.subjectId}" checked="checked"/></td>
                                    <td>${classSubject.subjectName}</td>
                                    <td><input type="text" value="${classSubject.hallMark}" name="examMark${classSubject.subjectId}" class="examMark form-control" readonly/>
                                    <td><g:select class="form-control exam-period" value="${isEditSchedule ? classSubject?.htPeriod : ''}" name="examPeriod${classSubject.subjectId}" tabindex="5"
                                                  noSelection="${['': 'Select Exam Time...']}"
                                                  from='${examPeriodList}'
                                                  optionKey="id" optionValue="name"></g:select></td>
                                    <td><input type="text" name="examDate${classSubject.subjectId}" value="${isEditSchedule ? classSubject?.examDate : ''}" class="examDate datepicker form-control"/></td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>

                        <div class="row">
                            <div class="form-group">
                                <div class="col-md-offset-8 col-lg-4">
                                    <button class="btn btn-primary" tabindex="2" type="submit">${isEditSchedule ? 'Update':'Save'}</button>
                                    <button class="btn btn-default cancel-btn" tabindex="3" type="reset">Cancel</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </g:else>
            </div>
        </section>
    </div>
</div>

<script>

    jQuery.validator.addClassRules("examMark", {
        required: true,
        number: true
    });

    jQuery.validator.addClassRules("exam-period", {
        required: true
    });

    jQuery.validator.addClassRules("examDate", {
        required: true
    });

    jQuery(function ($) {
        $('.datepicker').datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        });
        $('#examPeriodAll').on('change', function (e) {
            var examPeriod = $('#examPeriodAll').val();
            if (examPeriod) {
                $('#create-form .exam-period').each(function(e) {
                    $(this).val(examPeriod);
                });
            }
        });
        var validator = $('#create-form').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            highlight: function (e) {
                $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
            },
            success: function (e) {
                $(e).closest('.form-group').removeClass('has-error').addClass('has-info');
                $(e).remove();
            },
            submitHandler: function (form) {
                showLoading("#create-form");
                $.ajax({
                    url: "${createLink(controller: 'examSchedule', action: 'saveClassHT')}?examIds=${examIds}&isNewSubject=${isNewSubject}",
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        hideLoading("#create-form");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'classExams', params: [id: shiftExamId])}";
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });
        $('.extra-btn-1').click(function (e) {
            var confirmDel = confirm("Are you sure publish CT Schedule?");
            if (confirmDel == true) {
                showLoading("#bread-action-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examSchedule',action: 'publishHTSchedule')}/${shiftExamId}?className=${className.id}",
                    success: function (data, textStatus) {
                        hideLoading("#bread-action-holder");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'exam', action: 'classExams', params: [id: shiftExamId])}";
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

        $(".cancel-btn").click(function () {
            window.location.href = "${createLink(controller: 'exam', action: 'classExams', params: [id: shiftExamId])}";
        });
    });

</script>
</body>
</html>