<%@ page import="com.grailslab.enums.HrKeyType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Working Day Entry list</title>
    <meta name="layout" content="adminLayout"/>
</head>
<body>

<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'previousTerm', action: 'attendance')}" firstBreadCrumbText="Attendance Entry" breadCrumbTitleText="Working Days" SHOW_CREATE_BTN="YES" createButtonText="Calculate Attendance" />

<div class="row" id="create-form-holder">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Working days
            </header>

            <div class="panel-body">
                <div class="table-responsive">
                    <g:form name="myForm" method="post" controller="previousTerm" action="workDays"
                            class="form-horizontal">
                        <table class="table table-striped table-hover table-bordered" id="list-table">
                            <thead>
                            <tr>
                                <th>SL</th>
                                <th>Class Name</th>
                                <th>Working Days Entry</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${classNameList}" var="dataSet" status="i">
                                <tr>
                                    <td>${i+1}</td>
                                    <td>${dataSet?.name}</td>
                                    <td>
                                        <input type="number" name="workingDay.${dataSet?.id}" class="checkSingle"
                                               value="${dataSet?.workingDays}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <div class="col-md-12">
                            <button class="btn btn-default" aria-hidden="true" data-dismiss="modal"
                                    type="button">Cancel</button>
                            <button id="create-yes-btn" class="btn btn-large btn-primary" type="submit">Submit</button>
                        </div>
                    </g:form>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="workingDayModal"  tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" role="form" id="createFormModal">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">Calculate working day</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="form-group" id="data_range">
                            <label for="periodStart" class="col-md-3 control-label">Exam Period</label>
                            <div class="col-md-7">
                                <div class="input-daterange input-group" id="datepicker">

                                    <span class="input-group-addon">Start</span>
                                    <g:textField class="input-sm form-control" id="periodStart" name="periodStart"
                                                 tabindex="4" placeholder="Period Start" required="required"/>
                                    <span class="input-group-addon">End</span>
                                    <g:textField class="input-sm form-control" id="periodEnd" name="periodEnd"
                                                 tabindex="5" placeholder="Period End" required="required"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer modal-footer-action-btns">
                    <button class="btn  btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="submitButton">Save changes</button>
                </div>
                <div class="modal-footer modal-refresh-processing" style="display: none;">
                    <i class="fa fa-refresh fa-spin text-center"></i>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    jQuery(function ($) {
        $('.create-new-btn').click(function (e) {
            $('#workingDayModal').modal('show');
            e.preventDefault();

        });
        $('#data_range .input-daterange').datepicker({
            keyboardNavigation: false,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $("#createFormModal").submit(function(e) {
            $('#workingDayModal .modal-footer-action-btns').hide();
            $('#workingDayModal .modal-refresh-processing').show();
            jQuery.ajax({
                type: 'POST',
                dataType:'JSON',
                data: $("#createFormModal").serialize(),
                url: "${createLink(controller: 'previousTerm', action: 'countWorkingDays')}",
                success: function (data) {
                    if(data.isError==true){
                        showErrorMsg(data.message);
                    }else {
                        showSuccessMsg(data.message);
                        window.location.href = "${g.createLink(controller: 'previousTerm',action: 'workDays')}";
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            return false; // avoid to execute the actual submit of the form.
        });
    });
</script>
</body>
</html>