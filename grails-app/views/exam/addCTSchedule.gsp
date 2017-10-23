<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Class Test Schedule</title>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbText="Exam" firstBreadCrumbUrl="${g.createLink(controller: 'exam',action: 'index')}" secondBreadCrumbUrl="${g.createLink(controller: 'exam', action: 'sectionExams', id: exam.shiftExam?.id)}" secondBreadCrumbText="Schedule List" breadCrumbTitleText="CT Schedule"/>

<div class="row" id="create-form-holder">
    <div class="col-sm-12">
        <form class="cmxform" id="create-form">
            <section class="panel">
                <header class="panel-heading">
                    <span class="panel-header-title">Class: </span> <span class="panel-header-info">${className?.name}</span>  <span class="panel-header-title">Exam: </span> <span class="panel-header-info">${exam?.name}</span>
                </header>
                <div class="panel-body">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover table-bordered">
                            <thead>
                            <tr>
                                <th >Serial</th>
                                <th class="center">Select</th>
                                <th>Subject</th>
                                <th>CT Exam Mark</th>
                                <th> <g:select class="form-control" name="examPeriodAll" id="examPeriodAll" tabindex="5"
                                               noSelection="${['': 'Select Exam Time All...']}"
                                               from='${examPeriodList}'
                                               optionKey="id" optionValue="name"></g:select></th>
                                <th>Exam Date</th>

                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${ctSubjects}" var="classSubject" status="i">
                                <tr>
                                    <td>${i+1}</td>
                                    <td class="center"><input type="checkbox" disabled checked="checked" />
                                        <input type="hidden" value="${classSubject.subjectId}" name="subjectIds"/>
                                    </td>
                                    <td>${classSubject.subjectName}</td>
                                    <td><input type="text" value="${classSubject.ctMark}" name="examMark${classSubject.subjectId}" class="examMark form-control" readonly/>
                                    <td>
                                        <g:select class="form-control exam-period" name="examPeriod${classSubject.subjectId}" tabindex="5"
                                                  noSelection="${['': 'Select Exam Time...']}"
                                                  from='${examPeriodList}'
                                                  optionKey="id" optionValue="name"></g:select>
                                    </td>
                                    <td><input type="text" name="examDate${classSubject.subjectId}"
                                               class="examDate datepicker form-control"/></td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>

                    <div class="row">
                        <div class="form-group">
                            <div class="col-md-offset-8 col-lg-4">
                                <button class="btn btn-primary" tabindex="2" type="submit">Save</button>
                                <button class="btn btn-default cancel-btn" tabindex="3" type="reset">Cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </form>
    </div>
</div>


<script>
    var examPeriod;
    jQuery(function ($) {
        $('.datepicker').datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        });
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

        $('#examPeriodAll').on('change', function (e) {
            examPeriod = $('#examPeriodAll').val();
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
                    url: "${createLink(controller: 'examSchedule', action: 'saveCT')}?examId=" + ${exam?.id},
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        hideLoading("#create-form");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'examSchedule', action: 'ctSchedule')}/" + ${exam?.id};
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });
        $(".cancel-btn").click(function () {
            window.location.href = "${createLink(controller: 'exam', action: 'index')}";
        });
    });

</script>
</body>
</html>