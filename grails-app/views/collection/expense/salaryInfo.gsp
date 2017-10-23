<%@ page import="com.grailslab.gschoolcore.AcademicYear; com.grailslab.enums.PaymentType; com.grailslab.enums.EmpCategoryType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Salary Info</title>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<grailslab:breadCrumbActions SHOW_CREATE_BTN="YES" createButtonText="Add New" controllerName="${params.controller}"/>
<div class="row">
    <section class="panel">
        <div class="panel-body">
            <form class="form-horizontal" role="form" id="nextForm">
                <div class="row">
                    <div class="col-md-offset-1 col-md-8">
                        <grailslab:select name="employee" required="true" label="Employee"
                                          from="${employeeList}"></grailslab:select>
                        <grailslab:select name="academicYear" required="true" enums="true" label="AcademicYear"
                                          from="${com.grailslab.gschoolcore.AcademicYear.schoolYears()}"></grailslab:select>
                        <grailslab:select name="months" required="true" label="Month" enums="true"
                                          from="${com.grailslab.enums.YearMonths.values()}"></grailslab:select>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-offset-7 col-md-2">
                        <button type="submit" id="next-btn" class="btn btn-large btn-primary">Next -></button>
                    </div>
                </div>
            </form>
        </div>
    </section>
</div>

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-mid">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="modalLabelId">Salary Info</h4>
            </div>
        </div>
        <section class="panel">
            <div class="panel-body create-content">
                <form class="form-horizontal" role="form" id="modalForm">
                    <input type="hidden" name="id" id="hiddenId">
                    <input type="hidden" name="employee" id="employeeUp" required="true">
                    <input type="hidden" name="months" id="monthsUp" required="true">
                    <input type="hidden" name="academicYear" id="academicYearUp" required="true">
                    <input type="hidden" name="voucher" id="voucher" value="${params.id}" v>

                    <div class="modal-body">

                        <div class="user-heading">
                            <section class="panel">
                                <div class="row">
                                    <div class="col-md-3" id="gross_div">0</div>
                                    <div class="col-md-3" id="house_div">0</div>
                                    <div class="col-md-3" id="pf_div">0</div>
                                    <div class="col-md-3" id="dps_div">0</div>
                                </div>

                                <div class="row">
                                    <div class="col-md-3" id="totalSalary">Total-0</div>
                                    <div class="col-md-3" id="totalExtraIncome">Total-0</div>
                                    <div class="col-md-3" id="totalDeduction">Total-0</div>
                                    <div class="col-md-3" id="netPayable">Total-0</div>
                                </div>
                            </section>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">Arear <span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="arear" id="arear" class="form-control" value="0"/>
                            </div>

                            <label class="col-md-3 control-label">In Charge Allowance<span class="required">*</span>
                            </label>

                            <div class="col-md-1">
                                <input type="checkbox" name="isInCharge" id="isInCharge" class="form-control"/>
                            </div>

                            <div class="col-md-2">
                                <input type="number" name="inChargeAllowance" id="inChargeAllowance"
                                       class="form-control" value="0"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">Medical Allowance <span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="medicalAllowance" id="medicalAllowance"
                                       class="form-control"/>
                            </div>
                            <label class="col-md-3 control-label">Mobile Allowance <span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="mobileAllowance" id="mobileAllowance" class="form-control"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">Other Allowance <span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="otherAllowance" id="otherAllowance" class="form-control"/>
                            </div>
                            <label class="col-md-3 control-label">Advance <span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="advance" id="advance" class="form-control" value="0"/>
                            </div>
                        </div>


                        <div class="form-group">
                            <label class="col-md-3 control-label">Late Fine<span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="lateDays" id="lateFineNumber" class="form-control"
                                       value="0"/>
                            </div>
                            <label class="col-md-3 control-label">Amount <span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="lateFine" id="lateFine" class="form-control" value="0"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">UnAuthorize Leave<span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="ulDays" id="ulFineNumber" class="form-control"
                                       value="0"/>
                            </div>
                            <label class="col-md-3 control-label">Amount <span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="uLFine" id="uLFine" class="form-control" value="0"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">PC Deduction<span class="required">*</span>
                            </label>

                            <div class="col-md-3">
                                <input type="number" name="pcDeduction" id="pcDeduction" class="form-control"
                                       value="0"/>
                            </div>
                            <label class="col-md-3 control-label">Other Fine<span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="otherFine" id="otherFine" class="form-control" value="0"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">Proxy Class <span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="proxyClass" id="proxyClassNumber" class="form-control"
                                       value="0"/>
                            </div>
                            <label class="col-md-3 control-label">Amount <span class="required">*</span></label>

                            <div class="col-md-3">
                                <input type="number" name="proxyAmount" id="proxyAmount" class="form-control" value="0"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label">Remarks</label>

                            <div class="col-md-9">
                                <input type="text" name="remark" id="remark" class="form-control"/>
                            </div>
                        </div>

                        <div class="modal-footer modal-footer-action-btn">
                            <button type="button" class="btn btn-default" data-dismiss="modal"
                                    aria-hidden="true">Cancel</button>
                            <button type="button" id="show-total-btn" class="btn btn-large btn-danger">Total</button>
                            <button type="submit" id="create-yes-btn" class="btn btn-large btn-primary">Submit</button>
                        </div>

                        <div class="modal-footer modal-refresh-processing" style="display: none;"><i
                                class="fa fa-refresh fa-spin text-center"></i>
                        </div>
                    </div>
                </form>
            </div>

            <div class="create-success" style="display: none;">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-2">
                            <img src="/baily-school/assets/share-modal-icon.jpg" width="60" height="60"/>
                        </div>

                        <div class="col-md-10"><p class="message-content"></p></div>
                    </div>
                </div>

                <div class="modal-footer modal-footer-action-btn">
                    <button type="button" class="btn btn-default" data-dismiss="modal" aria-hidden="true">Close</button>
                </div>
            </div>
        </section>
    </div>
</div>
<grailslab:dataTable dataSet="${dataReturn}" tableHead="Serial,Employee,Year,Month,Date,Pay"></grailslab:dataTable>
<script>
    jQuery(function ($) {
        $('#employee').select2();
        var perProxyClass = 0;
        var perLateFine = 0;
        var perUlFine = 0;
        var inChargeAllowance = 0;

        $("#proxyClassNumber").change(function () {
            if (!$.isNumeric($('#proxyClassNumber').val())) {
                alert('Insert valid Number');
                $('#proxyClassNumber').val(0);
                $('#proxyAmount').val(0);
            } else {
                $('#proxyAmount').val(perProxyClass * $('#proxyClassNumber').val());
            }
        });
        $("#lateFineNumber").change(function () {
            if (!$.isNumeric($('#lateFineNumber').val())) {
                alert('Insert valid Number');
                $('#lateFineNumber').val(0);
                $('#lateFine').val(0);
            } else {
                $('#lateFine').val(perLateFine * $('#lateFineNumber').val());
            }
        });
        $("#ulFineNumber").change(function () {
            if (!$.isNumeric($('#ulFineNumber').val())) {
                alert('Insert valid Number');
                $('#ulFineNumber').val(0);
                $('#ulFine').val(0);
            } else {
                $('#uLFine').val(perUlFine * $('#ulFineNumber').val());
            }
        });

        $('#isInCharge').click(function () {
            if ($("#isInCharge").is(':checked')) {
                $('#inChargeAllowance').val(inChargeAllowance);
            } else {
                $('#inChargeAllowance').val(0);
            }
        });

        $('#next-btn').click(function (e) {
            if ($("#employee").val() != '' && $("#academicYear").val() != '' && $("#months").val() != '') {
                $.ajax({
                    url: "${createLink(controller: 'salaryInfo', action: 'employeeInfo')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#nextForm").serialize(),
                    success: function (data) {
                        if (data.isError == false) {
                            $('#modalLabelId').html('<b>Salary Of : </b>' + data.employeeDetail);
//                            $('#basic_div').html('<b>Basic Salary : </b>' + data.obj.basic);
                            $('#gross_div').html('<b>Gross Salary : </b>' + data.obj.gross);
                            $('#dps_div').html('<b>DPS : </b>' + data.obj.dps);
                            $('#house_div').html('<b>House Allowancw : </b>' + data.obj.houseAllowance);
                            $('#pf_div').html('<b>Provident Fund : </b>' + data.obj.providentFund);
                            $('#employeeUp').val(data.obj.employee.id);
                            $('#academicYearUp').val($("#academicYear").val());
                            $('#monthsUp').val($("#months").val());
                            $('#houseAllowance').val(data.obj.houseAllowance);
                            $('#medicalAllowance').val(data.obj.medicalAllowance);
                            $('#mobileAllowance').val(data.obj.mobileAllowance);
                            $('#otherAllowance').val(data.obj.otherAllowance);
                            perProxyClass = data.addDeduct.proxyClassAllowance;
                            perLateFine = data.addDeduct.lateFine;
                            perUlFine = data.addDeduct.ulFine;
                            inChargeAllowance = data.obj.inChargeAllowance;
                            $('#myModal .create-success').hide();
                            $('#myModal .create-content').show();
                            $('#myModal').modal('show');
                        } else {
                            alert(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            } else {
                alert('All Fields Are Mandatory');
            }
            e.preventDefault();
        });


        var validator = $('#modalForm').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                arear: {
                    required: true,
                    number: true
                },
                inChargeAllowance: {
                    required: true,
                    number: true
                },
                medicalAllowance: {
                    required: true,
                    number: true
                },
                mobileAllowance: {
                    required: true,
                    number: true
                },
                otherAllowance: {
                    required: true,
                    number: true
                },
                advance: {
                    required: true,
                    number: true
                },
                lateDays: {
                    required: true,
                    number: true
                },
                lateFine: {
                    required: true,
                    number: true
                },
                ulDays: {
                    required: true,
                    number: true
                },
                uLFine: {
                    required: true,
                    number: true
                },
                pcDeduction: {
                    required: true,
                    number: true
                },
                otherFine: {
                    required: true,
                    number: true
                },
                proxyClass: {
                    required: true,
                    number: true
                },
                proxyAmount: {
                    required: true,
                    number: true
                }
            },
            errorPlacement: function (error, element) {
            },
            highlight: function (e) {
                $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
            },
            success: function (e) {
                $(e).closest('.form-group').removeClass('has-error').addClass('has-info');
                $(e).remove();

            },
            submitHandler: function (form) {
                $('#myModal .create-content .modal-footer-action-btn').hide();
                $('#myModal .create-content .modal-refresh-processing').show();
                $.ajax({
                    url: "${createLink(controller: 'salaryInfo', action: 'salarySave')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#modalForm").serialize(),
                    success: function (data) {
                        formSuccess(data);
                    },
                    failure: function (data) {
                    }
                })
            }
        });

        $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'salaryInfo',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(6)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
                null,
                null,
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false}
            ]
        });

        $('.create-new-btn').click(function (e) {
            $('#myModal .create-success').hide();
            $('#myModal .create-content').show();
            validator.resetForm();
            $("#hiddenId").val('');
            $('#myModal').modal('show');
            e.preventDefault();
        });

        $(".cancel-btn").click(function () {
            var validator = $("#modalForm").validate();
            validator.resetForm();
            $("#hiddenId").val('');
            $('#myModal').modal('hide');
        });

        $("#show-total-btn").click(function () {
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'salaryInfo',action: 'totalShow')}",
                data: $("#modalForm").serialize(),
                success: function (data) {
                    $('#totalSalary').html('<b>Total Salary : </b>' + data.totalSalary);
                    $('#totalExtraIncome').html('<b>Extra Income : </b>' + data.totalExtraIncome);
                    $('#totalDeduction').html('<b>Total Deduction : </b>' + data.totalDeduction);
                    $('#netPayable').html('<b>Net Payable : </b>' + data.netPayable);
                },
                failure: function (data) {
                }
            })
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'salarySetup',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $('#hiddenId').val(data.obj.id);
                        $('#employee').select2("destroy");
                        $('#employee').val(data.obj.employee.id);
                        $('#employee').select2().enable(true);
                        $('#basic').val(data.obj.basic);
                        $('#gross').val(data.obj.gross);
                        $('#houseAllowance').val(data.obj.houseAllowance);
                        $('#medicalAllowance').val(data.obj.medicalAllowance);
                        $('#inChargeAllowance').val(data.obj.inChargeAllowance);
                        $('#mobileAllowance').val(data.obj.mobileAllowance);
                        $('#otherAllowance').val(data.obj.otherAllowance);
                        $('#providentFund').val(data.obj.providentFund);
                        $('#dps').val(data.obj.dps);
                        $('#paymentType').val(data.obj.paymentType ? data.obj.paymentType.name : '');
                        $('#myModal .create-success').hide();
                        $('#myModal .create-content').show();
                        $('#myModal').modal('show');
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'salarySetup',action: 'delete')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            $("#list-table").DataTable().row(selectRow).remove().draw(false);
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

        $('#list-table').on('click', 'a.inactive-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'salarySetup',action: 'inactive')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            $('#list-table').DataTable().ajax.reload();
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

    function getActionButtons(nRow, aData) {
        return getActionBtn(nRow, aData);
    }

</script>
</body>
</html>