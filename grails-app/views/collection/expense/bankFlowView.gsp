<%@ page import="com.grailslab.enums.BalanceType; com.grailslab.enums.EmpCategoryType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Expense Detail</title>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Bank Transaction" SHOW_CREATE_BTN="YES" createButtonText="Cash Withdraw" SHOW_EXTRA_BTN1="YES"
                             extraBtn1Text="Cash Deposit"/>

<grailslab:fullModal modalLabel="Bank Withdraw">
    <input type="hidden" name="balanceType" id="balanceType" value="${BalanceType.CREDIT.key}"/>
    <grailslab:input required="true" name="amount" label="Amount" type="number"></grailslab:input>
    <grailslab:select required="true" name="bankAccount" label="Bank Account"
                      from="${bankAccountList}"></grailslab:select>
    <grailslab:input required="true" name="chequeNo" label="ChequeNo"></grailslab:input>
    <grailslab:input required="true" name="flowDate" label="Date"></grailslab:input>
</grailslab:fullModal>

<grailslab:fullModal modalLabel="Update" formId="formUp" modalId="upModal" hiddenId="hiddenUp">
    <input type="hidden" id="hiddenUpId" name="upId"/>
    <grailslab:input required="true" name="amountUp" label="Amount" type="number"></grailslab:input>
    <grailslab:input required="true" id="chequeNoUp" name="chequeNoUp" label="ChequeNo"></grailslab:input>
    <grailslab:input required="true" id="flowDateUp" name="flowDateUp" label="Date"></grailslab:input>
</grailslab:fullModal>

<div class="modal fade" id="upModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="upModalLabelId">Update</h4>
            </div>
        </div>
        <section class="panel">
            <div class="panel-body create-content">
                <form class="form-horizontal" role="form" id="formUp">
                    <input type="hidden" name="id" id="upId">

                    <div class="modal-body">

                        <div class="form-group">
                            <label class="col-md-4 control-label">ChequeNo <span class="required">*</span></label>

                            <div class="col-md-8">
                                <input type="text" name="chequeNo" id="chequeNoUp" class="form-control"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label">Date <span class="required">*</span></label>

                            <div class="col-md-8">
                                <input type="text" name="flowDate" id="flowDateUp" class="form-control"/>
                            </div>
                        </div>

                        <div class="modal-footer modal-footer-action-btn">
                            <button type="button" class="btn btn-default" data-dismiss="modal"
                                    aria-hidden="true">Cancel</button>
                            <button type="submit" class="btn btn-large btn-primary">Submit</button>
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
                            <img src="${g.resource(dir: 'image', file: 'share-modal-icon.jpg')}" width="60"
                                 height="60"/>
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


<grailslab:dataTable dataSet="${dataReturn}" actions="fa fa-pencil-square-o,fa fa-trash-o"
                     tableHead="User,Bank Details,Amount,Bank Balance,Cash Balance,Balance Type"></grailslab:dataTable>
<script>

    jQuery(function ($) {
        $('#flowDate').datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        });
        $('#flowDateUp').datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        });

        var validator = $('#modalForm').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                amount: {
                    required: true,
                    number: true
                },
                bankAccount: {
                    required: true
                },
                chequeNo: {
                    required: true
                },
                flowDate: {
                    required: true
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
                    url: "${createLink(controller: 'bankFlow', action: 'save')}",
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

        var validatorUp = $('#formUp').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                amountUp: {
                    required: true,
                    number: true
                },
                chequeNoUp: {
                    required: true
                },
                flowDateUp: {
                    required: true
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
                $('#upModal .create-content .modal-footer-action-btn').hide();
                $('#upModal .create-content .modal-refresh-processing').show();
                $.ajax({
                    url: "${createLink(controller: 'bankFlow', action: 'updateSave')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#formUp").serialize(),
                    success: function (data) {
                        $('#upModal .create-content .modal-refresh-processing').hide();
                        $('#upModal .create-success .modal-footer-action-btn').show();
                        if (data.isError == true) {
                            $('#upModal .create-success p.message-content').html(data.message);
                        } else {
                            $('#upModal .create-success .message-content').html(data.message);
                        }
                        $('#list-table').DataTable().ajax.reload();
                        $('#upModal .create-content').hide(1000);
                        $('#upModal .create-success').show(1000);
                        $('#upModal .create-content .modal-footer-action-btn').show();
                    },
                    failure: function (data) {
                    }
                })
            }
        });

        $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 50,
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'bankFlow',action: 'list')}?id=${params.id}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(6)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false}
            ]
        });

        $('.create-new-btn').click(function (e) {
            $('#modalLabelId').html('Cash Withdraw');
            $('#balanceType').val('${BalanceType.CREDIT.key}');
            $('#myModal .create-success').hide();
            $('#myModal .create-content').show();
            validator.resetForm();
            $("#hiddenId").val('');
            $('#myModal').modal('show');
            e.preventDefault();
        });

        $('.extra-btn-1').click(function (e) {
            $('#modalLabelId').html('Bank Deposit');
            $('#balanceType').val('${BalanceType.DEBIT.key}');
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

        $('#list-table').on('click', 'a.reference-1', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'bankFlow',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        var flowDate = new Date(data.obj.flowDate);
                        $('#hiddenUpId').val(data.obj.id);
                        $('#amountUp').val(data.obj.amount);
                        $('#chequeNoUp').val(data.obj.chequeNo);
                        $('#flowDateUp').datepicker('setDate', flowDate);
                        $('#upModal .create-success').hide();
                        $('#upModal .create-content').show();
                        $('#upModal').modal('show');
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.reference-2', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'bankFlow',action: 'deleteFlowAndUpdate')}?id=" + referenceId,
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
    });

    function getActionButtons(nRow, aData) {
        return getActionBtnCustom(nRow, aData, 'fa fa-pencil-square-o,fa fa-trash-o');
    }

</script>
</body>
</html>