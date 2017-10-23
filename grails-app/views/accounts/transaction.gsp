<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Transaction </title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Transaction" />
<div class="row" id="create-form-holder" style="">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Add New Transaction
            </header>
            <div class="panel-body">
                <form class="form-horizontal" role="form" id="createForm">
                    <g:hiddenField name="id" id="transactionId"/>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="col-md-2 control-label">Transaction Name</label>
                                <div class="col-md-2">
                                    <input type="text" class="form-control" name="transactionName" id="transactionName" placeholder="Transaction Name">
                                </div>
                                <label class="col-md-2 control-label">Transaction type</label>
                                <div class="col-md-2">
                                    <input type="text" class="form-control" name="transactionType" id="transactionType" placeholder="Transaction Type">
                                </div>

                                <label class="col-md-2 control-label">Start Form</label>
                                <div class="col-md-2">
                                    <input type="number" class="form-control" name="startForm" id="startForm" placeholder="Start Form">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="form-group">
                            <div class="col-md-offset-9 col-lg-3">
                                <button class="btn btn-primary" tabindex="7" id="form-submit-btn" type="submit">Save</button>
                                <button class="btn btn-default cancel-btn" tabindex="5" type="reset">Cancel</button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    </div>
</div>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                 Transaction List
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th class="">SL NO</th>
                            <th>Transasction Name</th>
                            <th>Transasction Type</th>
                            <th>Start Form</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    var transactionTable;
    jQuery (function ($) {
       transactionTable = $('#list-table').DataTable({
            "bAutoWidth": true,
            "scrollX": false,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'asc'],
            "sAjaxSource": "${g.createLink(controller: 'accounts',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }

                $('td:eq(4)', nRow).html(getGridActionBtns(nRow, aData, 'editTrans,deleteTrans'));
                return nRow;
            },

            "aoColumns": [
                null,
                null,
                null,
                null,
                null
            ]
        });

        $("#createForm").submit(function () {
              jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                data: $("#createForm").serialize(),
                url: "${createLink(controller: 'accounts', action: 'transactionSave')}",
                success: function (data) {
                    showSuccessMsg(data.message);
                    $("#list-table").DataTable().draw(false);
                }
            });
        });

        $(".cancel-btn").click(function () {
            var validator = $("#createForm").validate();
            validator.resetForm();
            $("#id").val('');
            $("#create-form-holder").hide(500);
        });

        $('#list-table').on('click', 'a.editTrans-reference', function (e) {
         $("#create-form-holder").show(500);
            $("#name").focus();
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'accounts',action: 'editTransaction')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $("#form-submit-btn").html("Edit");
                        $('#transactionId').val(data.obj.id);
                        $('#transactionName').val(data.obj.name);
                        $('#transactionType').val(data.obj.type);
                        $('#startForm').val(data.obj.startForm);
                        $("#create-form-holder").show(500);
                        $("#name").focus();

                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();

        });

        $('#list-table').on('click', 'a.deleteTrans-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'accounts',action: 'deleteTransaction')}?id=" + referenceId,
                    success: function (data, textStatus) {
                    $("#list-table").DataTable().row(selectRow).remove().draw(false);
                    showSuccessMsg(data.message);
                    }
                });
            }
            e.preventDefault();
        });

    });

</script>

</body>
</html>
