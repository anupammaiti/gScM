<%@ page import="com.grailslab.CommonUtils; com.grailslab.enums.PayType; com.grailslab.enums.ApproveStatus; com.grailslab.enums.BankAccount; com.grailslab.enums.PaymentType; com.grailslab.enums.EmpCategoryType" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<html>
<head>
    <title>Voucher Invoice</title>
    <meta name="layout" content="adminLayout"/>
</head>
<body>

<grailslab:breadCrumbActions SHOW_CREATE_BTN="YES" createButtonText="Create Voucher"
                             controllerName="${params.controller}"/>
<grailslab:fullModal modalLabel="Save Voucher" formId="bankForm" hiddenId="bankUpdate" labelId="bankLabel"
                     modalId="bankModal">
    <input type="hidden" name="voucher" value="${params.id}"/>
    <div class="form-group">
        <label class="col-md-6 control-label" id="nameDiv">
        </label>
        <label class="col-md-6 control-label" id="idDiv">
        </label>
        <label class="col-md-6 control-label" id="dateDiv">
        </label>
        <label class="col-md-6 control-label" id="amountDiv">
        </label>
    </div>

    <div class="form-group">
        <label class="col-md-8 control-label">
            Select For Bank Payment
        </label>

        <div class="col-md-4">
            <input type="checkbox" class="form-control" name="bankPay" id="bankPay">

        </div>
    </div>

    <div id="bankInfoDiv" style="display: none">
        <grailslab:select required="true" name="bankAccount" label="Bank Account" id="bankAccountBank"
                          from="${bankAccountList}"></grailslab:select>
        <grailslab:input required="true" name="chequeNo" label="Cheque No" id="chequeNoBank"></grailslab:input>
    </div>

</grailslab:fullModal>
<div class="modal fade" data-keyboard="false" id="expenseModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-mid">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="cashLabel">Add Item</h4>
            </div>
        </div>
        <section class="panel">
            <div class="panel-body create-content">
                <form class="form-horizontal" role="form" id="expenseForm">
                    <input type="hidden" name="id" id="voucherUpdate">

                    <div class="modal-body">

                        <input type="hidden" name="voucher" value="${params.id}"/>

                        <div class="row">
                            <div class="col-md-7">
                                <grailslab:select required="true" name="expenseHead" from="${expHeadList}"
                                                  label="expense Head"></grailslab:select>
                                <grailslab:select required="true" name="expenseItem" from="${null}"
                                                  label="expense Item"></grailslab:select>
                                <grailslab:textArea name="description" label="Short Note"
                                                    id="descriptionField"></grailslab:textArea>
                            </div>

                            <div class="col-md-5">
                                <grailslab:input type="number" required="true" name="quantityField" label="Quantity"
                                                 labelSpace="col-md-6"
                                                 fieldSpace="col-md-6" value="1"></grailslab:input>
                                <grailslab:input type="number" required="true" name="unitPriceField" label="Unit Price"
                                                 labelSpace="col-md-6"
                                                 fieldSpace="col-md-6" value="0"></grailslab:input>
                                <grailslab:input type="number" required="true" name="amountField" label="Total Amount"
                                                 labelSpace="col-md-6"
                                                 fieldSpace="col-md-6" value="0"></grailslab:input>

                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-striped table-hover table-bordered" id="priceDataTable">
                                <thead>
                                <tr>
                                    <th>Expense Item</th>
                                    <th>Quantity</th>
                                    <th>Price</th>
                                    <th>Amount</th>
                                    <th>Short Note</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                            </table>
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
                    <button type="button" class="btn btn-default cancel-btn" data-dismiss="modal"
                            aria-hidden="true">Close</button>
                </div>
            </div>
        </section>
    </div>
</div>

<div class="row itemList">
    <div class="col-md-12">
        <section class="panel">
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Invoice No</th>
                            <th>Name</th>
                            <th>Date</th>
                            <th>Amount</th>
                            <th>Pay Status</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>

                        <g:each in="${dataReturn}" var="dataSet">
                            <tr>
                                <td>${dataSet[0]}</td>
                                <td>${dataSet[1]}</td>
                                <td>${dataSet[2]}</td>
                                <td>${dataSet[3]}</td>
                                <td>${dataSet[4]}</td>
                                <td>

                                    <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                         title="Edit" class="reference-1">
                                        <span class="fa fa-pencil-square-o"></span></a></span>
                                    <g:if test="${dataSet.payTypeFlg == PayType.DUE.key}">
                                        <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             title="Delete" class="reference-2">
                                            <span class="fa fa-trash-o"></span></a></span>
                                    </g:if>
                                    <g:if test="${dataSet.payTypeFlg == PayType.PAID.key}">
                                        <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             title="Revert" class="reference-3">
                                            <span class="fa fa-times"></span></a></span>
                                    </g:if>

                                    <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                         title="Pay" class="reference-4">
                                        <span class="fa fa-money"></span></a></span>
                                    <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                         title="Print" class="reference-5">
                                        <span class="fa fa-file-pdf-o"></span></a></span>
                                    <span class="col-md-2 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                         title="View" class="reference-6">
                                        <span class="fa fa-eye"></span></a></span>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    jQuery(function ($) {
        var expense = $('#expenseItem');
        $('#expenseHead').select2();
        $('#expenseItem').select2();
        var rowId = 1;
        var startDate
        var endDate

        $('.create-new-btn').click(function (e) {
            table.clear().draw();
            $("#buttonEdit").attr('value', 'Save..');
            $("#buttonEdit").attr('id', 'buttonSave');
            $('#voucherUpdate').val('');
            $('#expenseModal').modal('show');
            e.preventDefault();
        });

        var table = $('#priceDataTable').DataTable({
            "sDom": "<'row'<'col-md-1 button-add-holder'><'col-md-3 input-name-holder'><'col-md-4 input-date-holder'><'col-md-4'f>r>t<'row'<'col-md-1 button-save-holder'l><'col-md-3 button-print-holder'l><'col-md-4'i><'col-md-4'p>>",
            "bPaginate": false,
            "bAutoWidth": false,
            "aoColumns": [
                {"sWidth": "30%"},
                {"sWidth": "7%"},
                {"sWidth": "7%"},
                {"sWidth": "7%"},
                {"sWidth": "30%"},
                {"sWidth": "12%"}
            ]
        });

        var voucherModalTable = $('#voucherModalTable').DataTable({
            "bPaginate": false,
            "bAutoWidth": false,
            "aoColumns": [
                {"sWidth": "15%"},
                {"sWidth": "15%"},
                {"sWidth": "30%"},
                {"sWidth": "15%"},
                {"sWidth": "10%"},
                {"sWidth": "15%"}
            ]
        });

        var voucherDetailsModalTable = $('#voucherDetailsModalTable').DataTable({
            "bPaginate": false,
            "bAutoWidth": false,
            "aoColumns": [
                {"sWidth": "25%"},
                {"sWidth": "10%"},
                {"sWidth": "10%"},
                {"sWidth": "10%"},
                {"sWidth": "25%"},
                {"sWidth": "20%"}
            ]
        });

        $('#priceDataTable_wrapper div.button-add-holder').html('<input id="buttonAdd" class="btn btn-primary form-control" type="button" value="Add">');
        $('#priceDataTable_wrapper div.button-save-holder').html('<input id="buttonSave" class="btn btn-primary form-control" type="button" value="Save">');
        $('#priceDataTable_wrapper div.button-print-holder').html('<input id="buttonPrint" class="btn btn-primary form-control" type="button" value="Print">');
        $('#priceDataTable_wrapper div.input-date-holder').html('<input id="voucherDate" name="voucherDate" class="form-control myDateField" type="text" placeholder="Insert Date">');
        $('#priceDataTable_wrapper div.input-name-holder').html('<input id="voucherName" name="name" class="form-control" type="text" placeholder="Insert Name">');

        $('#buttonAdd').on('click', function () {
            var expenseText = expense.select2('data').text;
            var expenseVal = expense.select2('data').id;
            var description = $('#descriptionField').val();
            var quantity = $('#quantityField').val();
            var unitPrice = $('#unitPriceField').val();
            var amount = $('#amountField').val();

            if (!(expenseVal == '' || quantity == '' || quantity == 0 || unitPrice == '' || unitPrice == 0 || amount == '' || amount == 0)) {
                table.row.add([
                    expenseText + '<input type="hidden" class="expenseInput" name="expense" value="' + expenseVal + '">' + '<input type="hidden" class="expenseInputText" name="expenseText" value="' + expenseText + '">',
                    quantity + '<input type="hidden" class="quantityInput" name="quantity.' + expenseVal + '"  value="' + quantity + '">',
                    unitPrice + '<input type="hidden" class="unitPriceInput" name="unitPrice.' + expenseVal + '"  value="' + unitPrice + '">',
                    amount + '<input type="hidden" class="amountInput" name="amount.' + expenseVal + '" value="' + amount + '">',
                    description + '<input type="hidden" class="descriptionInput" name="description.' + expenseVal + '" value="' + description + '">',
                    "<span class='col-md-4 no-padding'><a href='' referenceId='" + rowId + "'class='reference-delete'> <span class='fa fa-trash-o'></span></a></span>" +
                    "<span class='col-md-4 no-padding'><a href='' referenceId='" + rowId + "'class='reference-edit'> <span class='fa fa-pencil-square-o'></span></a></span>"
                ]).draw();
                $('#expenseItem').select2("destroy");
                $('#expenseItem').val('');
                $('#expenseItem').select2().enable(true);
                $('#descriptionField').val('');
                $('#quantityField').val(1);
                $('#unitPriceField').val(0);
                $('#amountField').val(0);
                rowId++
            } else {
                alert('Select Carefully');
            }
        });

        $("#quantityField").change(function () {
            var quantity = $("#quantityField").val();
            var unitPrice = $("#unitPriceField").val();
            $("#amountField").val(quantity * unitPrice);
        });

        $("#unitPriceField").change(function () {
            var quantity = $("#quantityField").val();
            var unitPrice = $("#unitPriceField").val();
            $("#amountField").val(quantity * unitPrice);
        });

        $('#buttonSave').on('click', function (e) {
            $.ajax({
                url: "${createLink(controller: 'voucher', action: 'save')}",
                type: 'post',
                dataType: "json",
                data: $("#expenseForm").serialize(),
                success: function (data) {
                    $('#expenseModal .create-content .modal-refresh-processing').hide();
                    $('#expenseModal .create-success .modal-footer-action-btn').show();
                    if (data.isError == true) {
                        $('#expenseModal .create-success p.message-content').html(data.message);
                    } else {
                        $('#expenseModal .create-success .message-content').html(data.message);
                    }
                    $('#list-table').DataTable().ajax.reload();
                    $('#expenseModal .create-content').hide(1000);
                    $('#expenseModal .create-success').show(1000);
                    $('#expenseModal .create-content .modal-footer-action-btn').show();
                },
                failure: function (data) {
                }
            })
            e.preventDefault();
        });


        $('#buttonPrint').on('click', function () {
//            $('#dateModal').modal('show');
//            $('#expenseModal').css('opacity', .5);
//            $('#expenseModal').unbind();
        });

        $('#priceDataTable').on('click', 'a.reference-delete', function (e) {
            var selectRow = $(this).parents('tr');
            var control = this;
            var referenceId = $(control).attr('referenceId');
            $("#priceDataTable").DataTable().row(selectRow).remove().draw(false);
            e.preventDefault();
        });

        $('#priceDataTable').on('click', 'a.reference-edit', function (e) {
            e.preventDefault();
            var control = this;
            var selectRow = $(this).parents('tr');
            var referenceId = $(control).attr('referenceId');
            var expenseInput = $(control).closest("tr").find('.expenseInput').val();
            var expenseInputText = $(control).closest("tr").find('.expenseInputText').val();
            var quantityInput = $(control).closest("tr").find('.quantityInput').val();
            var unitPriceInput = $(control).closest("tr").find('.unitPriceInput').val();
            var amountInput = $(control).closest("tr").find('.amountInput').val();
            var descriptionInput = $(control).closest("tr").find('.descriptionInput').val();
            $('#expenseItem').select2("destroy");
            var options = '<option value="' + expenseInput + '">' + expenseInputText + '</option>';
            $("select#expenseItem").html(options);
            $('#expenseItem').val(expenseInput);
            $('#expenseItem').select2().enable(true);
            $('#expenseHead').select2("destroy");
            $('#expenseHead').val('');
            $('#expenseHead').select2().enable(true);
            $('#descriptionField').val(descriptionInput);
            $('#quantityField').val(quantityInput);
            $('#unitPriceField').val(unitPriceInput);
            $('#amountField').val(amountInput);
            $("#priceDataTable").DataTable().row(selectRow).remove().draw(false);
        });

        $('#list-table').on('click', 'a.reference-1', function (e) {
            e.preventDefault();
            var control = this;
            var referenceId = $(control).attr('referenceId');
            table.clear().draw();
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'voucher',action: 'edit')}?voucherId=" + referenceId,
                success: function (data, textStatus) {
                    var rowId = 7;
                    if (data.isError == false) {
                        $('#voucherUpdate').val(data.obj.id);
                        $.each(data.detailObj, function (key, value) {
                            var expenseText = value.chartOfAccountsName;
                            var expenseVal = value.chartOfAccountsId;
                            var description = value.description;
                            var quantity = value.quantity;
                            var unitPrice = value.unitPrice;
                            var amount = value.amount;
                            table.row.add([
                                expenseText + '<input type="hidden" class="expenseInput" name="expense" value="' + expenseVal + '">' + '<input type="hidden" class="expenseInputText" name="expenseText" value="' + expenseText + '">',
                                quantity + '<input type="hidden" class="quantityInput" name="quantity.' + expenseVal + '"  value="' + quantity + '">',
                                unitPrice + '<input type="hidden" class="unitPriceInput" name="unitPrice.' + expenseVal + '"  value="' + unitPrice + '">',
                                amount + '<input type="hidden" class="amountInput" name="amount.' + expenseVal + '" value="' + amount + '">',
                                description + '<input type="hidden" class="descriptionInput" name="description.' + expenseVal + '" value="' + description + '">',
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + rowId + "'class='reference-delete'> <span class='fa fa-trash-o'></span></a></span>" +
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + rowId + "'class='reference-edit'> <span class='fa fa-pencil-square-o'></span></a></span>"
                            ]).draw();
                            rowId++
                        });
                        $("#buttonSave").attr('value', 'Edit..');
                        $("#buttonSave").attr('id', 'buttonEdit');
                        $('#expenseModal').modal('show');
                        console.log(data.detailObj);
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        });

        $('#expenseHead').on('change', function (e) {
            var expenseItem = $('#expenseHead').val();
            expenseHeadOnChange(expenseItem);
        });

        $('#list-table').dataTable({
            "sDom": "<'row'<'col-md-3 startDate-holder'><'col-md-3 endDate-holder'><'col-md-3 button-holder'><'col-md-3'f>r>t<'row'<'col-md-3'l><'col-md-3'i><'col-md-6'p>>",
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 50,
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'voucher',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(5)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "fnServerParams": function (aoData) {
                aoData.push({"name": "startDate", "value": startDate},
                        {"name": "endDate", "value": endDate});
            },
            "aoColumns": [
                null,
                null,
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false}
            ]
        });
        $('#list-table_wrapper div.startDate-holder').html('<input id="startDate" class="form-control myDateField" type="text" name="startDate">');
        $('#list-table_wrapper div.endDate-holder').html('<input id="endDate" class="form-control myDateField" type="text" name="endDate">');
        $('#list-table_wrapper div.button-holder').html('<input id="buttonDate" class="btn btn-primary form-control dateSearch" type="button" value="Search...">');

        $('.myDateField').datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        });

        $(".dateSearch").click(function () {
            startDate = $('#startDate').val();
            endDate = $('#endDate').val();
            $('#list-table').DataTable().ajax.reload();
        });

        $("#bankPay").change(function () {
            if (this.checked) {
                $("#bankAccountBank").attr('required', true);
                $("#chequeNoBank").attr('required', true);
                $("#bankInfoDiv").show();
            } else {
                $("#bankAccountBank").attr('required', false);
                $("#chequeNoBank").attr('required', false);
                $("#bankInfoDiv").hide();
            }
        });

        $('.reference-4').click(function (e) {
            e.preventDefault();
            var control = this;
            var referenceId = $(control).attr('referenceId');
            $('#bankModal .create-success').hide();
            $('#bankModal .create-content').show();

            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'voucher',action: 'pay')}?voucherId=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        var date = data.obj.voucherDate;
                        $('#nameDiv').html(' Invoice Name - ' + data.obj.name);
                        $('#idDiv').html('Invoice No - ' + data.obj.invoiceNo);
                        $('#dateDiv').html(' Date - ' + date);
                        $('#amountDiv').html('Total Amount - ' + data.obj.totalAmount);
                    } else {
                        $('#voucherDetailDiv').html('Error  - ' + data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            $('#bankModal').modal('show');
        });

        $('#list-table').on('click', 'a.reference-6', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            var voucherList;
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'voucher',action: 'getVoucher')}?voucherId=" + referenceId,
                success: function (data, textStatus) {
                    var rowId = 7;
                    if (data.isError == false) {
                        voucherModalTable.clear();
                        voucherList = data.obj;
                        voucherList.forEach(function (entry) {
                            voucherModalTable.row.add([
                                entry.invoice,
                                entry.voucherNo,
                                entry.chartAcc,
                                entry.voucherDate,
                                entry.totalAmount,
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-delete'> <span class='fa fa-trash-o'></span></a></span>" +
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-edit'> <span class='fa fa-pencil-square-o'></span></a></span>" +
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-show'> <span class='fa fa-eye'></span></a></span>"
                            ]).draw();
                        });
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            $('#voucherModal').modal('show');
            e.preventDefault();

        });

        $('#voucherModalTable').on('click', 'a.reference-voucher-show', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            var voucherList;
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'voucher',action: 'getVoucherDetails')}?voucherId=" + referenceId,
                success: function (data, textStatus) {
                    var rowId = 7;
                    if (data.isError == false) {
                        voucherDetailsModalTable.clear();
                        voucherList = data.obj;
                        voucherList.forEach(function (entry) {
                            voucherDetailsModalTable.row.add([
                                entry.chartOfAccount,
                                entry.quantity,
                                entry.unitPrice,
                                entry.amount,
                                entry.description,
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-delete'> <span class='fa fa-trash-o'></span></a></span>" +
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-edit'> <span class='fa fa-pencil-square-o'></span></a></span>" +
                                "<span class='col-md-4 no-padding'><a href='' referenceId='" + entry.id + "'class='reference-voucher-show'> <span class='fa fa-eye'></span></a></span>"
                            ]).draw();
                        });
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            $('#voucherModal').css('opacity', .5);
            $('#voucherDetailsModal').modal('show');
            e.preventDefault();

        });

        $('#list-table').on('click', 'a.reference-2', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'voucher',action: 'editDetails')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $('#cashUpdate').val(data.obj.id);
                        $('#unitPrice').val(data.obj.unitPrice);
                        $('#amount').val(data.obj.amount);
                        $('#quantity').val(data.obj.quantity);
                        $('#description').val(data.obj.description);
                        $('#expenseDetail').select2("destroy");
                        $('#expenseDetail').val(data.obj.expenseDetail.id);
                        $('#expenseDetail').select2().enable(true);
                        $('#cashModal .create-success').hide();
                        $('#cashModal .create-content').show();
                        $('#cashModal').modal('show');
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.reference-3', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'voucher',action: 'deleteDetails')}?id=" + referenceId,
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
        var flgApprove = aData.payTypeFlg;
        var actionButtons = "";
        actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-1" title="Edit">';
        actionButtons += '<span class="fa fa-pencil-square-o"></span>';
        actionButtons += '</a></span>';
        if (flgApprove == "${PayType.DUE.key}") {
            actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-2" title="Delete">';
            actionButtons += '<span class="fa fa-trash-o"></span>';
            actionButtons += '</a></span>';
        }
        if (flgApprove == "${PayType.PAID.key}") {
            actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-3" title="Revert">';
            actionButtons += '<span class="fa fa-times"></span>';
            actionButtons += '</a></span>';
        }
        actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-4" title="Pay">';
        actionButtons += '<span class="fa fa-money"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-5" title="Print">';
        actionButtons += '<span class="fa fa-file-pdf-o"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-2 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="reference-6" title="Print">';
        actionButtons += '<span class="fa fa-eye"></span>';
        actionButtons += '</a></span>';
        return actionButtons;
    }

    function expenseHeadOnChange(chartId) {
        jQuery.ajax({
            type: 'POST',
            dataType: 'JSON',
            url: "${g.createLink(controller: 'voucher',action: 'chartList')}?chartId=" + chartId,
            success: function (data, textStatus) {
                if (data.isError == false) {
                    var $select = $('#expenseItem');
                    $select.find('option').remove();
                    $select.append('<option value="">All Accounts</option>');
                    $.each(data.chartList, function (key, value) {
                        $select.append('<option value=' + value.id + '>' + value.name + '</option>');
                    });
                } else {
                    showErrorMsg(data.message);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    }

    $('#voucherDetailsModal').on('hidden.bs.modal', function () {
        $('#voucherModal').css('opacity', 1);
    });
    $('#expenseModal').on('hidden.bs.modal', function () {
        $('#expenseModal .create-content .modal-footer-action-btn').hide();
        $('#expenseModal .create-success .modal-footer-action-btn').hide();
        $('#expenseModal .create-content').show();
        $('#expenseModal .create-success').hide();
        $('#voucherUpdate').val('');
    });

</script>

</body>
</html>