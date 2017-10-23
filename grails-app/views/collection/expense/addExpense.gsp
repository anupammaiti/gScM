<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>Add Expense</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Expense" SHOW_CREATE_BTN="YES"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Invoice Number</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Date</th>
                            <th>Paid Status</th>
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
                                        <span class="col-md-4 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="edit-reference" title="Edit"><span
                                                    class="green glyphicon glyphicon-edit"></span></a></span>
                                        <span class="col-md-4 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="delete-reference"
                                                                             title="Delete"><span
                                                    class="green glyphicon glyphicon-trash"></span></a></span>
                                        <span class="col-md-4 no-padding"><a
                                                href="<g:createLink controller="expense" action="view"
                                                                    id="${dataSet.DT_RowId}"/>" target="_blank"
                                                referenceId="${dataSet.DT_RowId}"
                                                class="view-reference"
                                                title="View"><i class="icon-external-link"></i></a></span>
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
<div class="modal fade bs-example-modal-lg" id="mainModal" tabindex="-1" role="dialog"
     aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="mainModalLabel">Expense</h4>
            </div>

            <div class="action-print-confirm print-content">
                <form class="form-horizontal" role="form" id="create-form">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">

                                    <div class="col-md-4">
                                        <g:select class="form-control" id="account" name='account'
                                                  tabindex="3"
                                                  noSelection="${['': 'Select One...']}"
                                                  from='${accountList}'
                                                  optionKey="id" optionValue="nameWithCode"></g:select>
                                    </div>

                                    <div class="col-md-2">
                                        <input type="text" class="form-control" id="unitePrice" name="unitePrice"
                                               placeholder="Unite Price" value=""/>
                                    </div>

                                    <div class="col-md-2">
                                        <input type="text" class="form-control" id="quantity" name="quantity"
                                               placeholder="Quantity" value=""/>
                                    </div>

                                    <div class="col-md-2">
                                        <button class="btn btn-success" id="btn-price" type="button"><span
                                                class="ui-button-text">0</span></button>
                                    </div>

                                    <div class="col-md-1">
                                        <button id="add-btn"
                                                class="btn btn-large btn-primary">Add Data</button>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-4 col-md-offset-8">
                                        <button class="btn btn-success" id="btn-price-total" type="button">Total- <span
                                                class="ui-button-text">0</span></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <table class="table table-striped table-hover table-bordered" id="add-table">
                        <thead>
                        <tr>
                            <th>Account</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                    </table>

                    <div class="modal-footer modal-footer-action-btns">
                        <button type="button" id="cancel-btn" class="btn btn-default" data-dismiss="modal"
                                aria-hidden="true">Cancel</button>
                        <button type="submit" id="print-schedule-yes-btn"
                                class="btn btn-large btn-primary">Submit</button>
                    </div>

                    <div class="modal-footer modal-refresh-processing" style="display: none;">
                        <i class="fa fa-refresh fa-spin text-center"></i>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editModal" tabindex="-1" role="dialog"
     aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="editModalLabel">Expense</h4>
            </div>

            <div class="action-print-confirm print-content">
                <form class="form-horizontal" role="form" id="edit-form">
                    <input type="hidden" id="invoiceId" name="invoiceId">

                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">

                                    <div class="col-md-3">
                                        <input type="text" class="form-control " id="invoiceDateEdit"
                                               name="invoiceDateEdit"
                                               placeholder="Insert Date" value=""/>
                                    </div>


                                    <div class="col-md-3 radio">
                                        <label class="checkbox-inline">
                                            <input id="verified" type="radio" checked name="verified "
                                                   value="${Boolean.TRUE}">
                                            Varefide
                                        </label>
                                        <label class="checkbox-inline">
                                            <input id="notVerified" type="radio" name="verified "
                                                   value="${Boolean.FALSE}">
                                            Not Verified
                                        </label>
                                    </div>

                                    <div class="col-md-3 radio">
                                        <label class="checkbox-inline">
                                            <input id="paid" type="radio" checked name="verified "
                                                   value="${Boolean.TRUE}">
                                            Paid
                                        </label>
                                        <label class="checkbox-inline">
                                            <input id="unPaid" type="radio" name="verified "
                                                   value="${Boolean.FALSE}">
                                            Unpaid
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-4 col-md-offset-8">
                                    <button class="btn btn-success" id="btn-update" type="submit">Total- <span
                                            class="ui-button-text">0</span></button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>

    $('#invoiceDateEdit').datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    });

    jQuery(function ($) {

        var rowNumber = 0;
        var rowId = 0;
        var totalPrice = 0;
        var price = 0;
        var unitePrice = 0;
        var quantity = 0;
        $('.create-new-btn').click(function (e) {
            $('#mainModal').modal('show');
            e.preventDefault();
        });

        $('#unitePrice').keyup(function () {

            if (!isNaN($('#unitePrice').val())) {
                unitePrice = $('#unitePrice').val();
                quantity = $('#quantity').val();
                price = unitePrice * quantity;
                $('#price').val(price);
                $("#btn-price span").text(price);
            } else {
                alert('Insert Number Please')
                $('#unitePrice').val('');
            }
        });

        $('#quantity').keyup(function () {
            if (!isNaN($('#quantity').val())) {
                quantity = $('#quantity').val();
                unitePrice = $('#unitePrice').val();
                price = unitePrice * quantity;
                $("#btn-price span").text(price);
                $('#price').val(price);
            } else {
                alert('Insert Number Please')
                $('#quantity').val('');
            }
        });

        $('#add-btn').click(function (e) {

            if ($('#account').val() != '' && $('#unitePrice').val() != '' && $('#quantity').val() != '') {
                rowId = rowId + 1
                rowNumber = rowNumber + 1
                var account = $('#account').val();
                var accountText = $("#account option:selected").text();
                var unitePrice = $('#unitePrice').val();
                var quantity = $('#quantity').val();
                price = unitePrice * quantity;
                totalPrice = totalPrice + price
                $('#quantity').val('');
                $('#unitePrice').val('');
                $("#btn-price span").text(0);
                $("#btn-price-total span").text(totalPrice);
                $('#add-table').append(
                        '<tr>' +
                        '<td class="account">' + accountText + rowId + '<input type="hidden" id="account' + rowId + '" name="accountVal.' + rowId + '" value="' + account + '"/></td>' +
                        '<td class="unitePrice">' + unitePrice + '<input type="hidden" id="unitePrice' + rowId + '" name="unitePriceVal.' + rowId + '" value="' + unitePrice + '"/></td>' +
                        '<td class="quantity">' + quantity + '<input type="hidden" id="quantity' + rowId + '" name="quantityVal.' + rowId + '" value="' + quantity + '"/></td>' +
                        '<td><input type="hidden" id="price' + rowId + '" name="price.' + rowId + '" value="' + price + '"/>' + price + '</td>' +
                        '<td>' +
                        '<span class="col-md-6 no-padding"><a href="" referenceId="' + rowId + '" class="edit-reference" title="Edit"><span class="green glyphicon glyphicon-edit"></span></a></span>' +
                        '<span class="col-md-6 no-padding"><a href="" referenceId="' + rowId + '" class="delete-reference" title="Delete"><span class="green glyphicon glyphicon-trash"></span></a></span>' +
                        '</td>' +
                        '</tr>'
                );
            } else {
                alert('All Fields Are Mandatory')
            }
            e.preventDefault();
        });

        $('#add-table').on('click', 'a.edit-reference', function (e) {
            var referenceId = $(this).attr('referenceId');
            var priceEdit = $('#price' + referenceId).val();
            var unitePriceEdit = $('#unitePrice' + referenceId).val();
            var quantityEdit = $('#quantity' + referenceId).val();
            var account = $('#account' + referenceId).val();
            $('#unitePrice').val(unitePriceEdit);
            $('#quantity').val(quantityEdit);
            $("#btn-price span").text(priceEdit);
            totalPrice = totalPrice - priceEdit;
            $("#btn-price-total span").text(totalPrice);
            $('#account').val(account);
            $(this).parent().parent().parent().remove();
            e.preventDefault();
        });

        $('#add-table').on('click', 'a.delete-reference', function (e) {
            var referenceId = $(this).attr('referenceId');
            var priceEdit = $('#price' + referenceId).val();
            totalPrice = totalPrice - priceEdit;
            $("#btn-price-total span").text(totalPrice);
            $(this).parent().parent().parent().remove();
            e.preventDefault();
        });

        $('#cancel-btn').click(function (e) {
            window.location.href = "${g.createLink(controller: 'expense',action: 'index')}";
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
                $.ajax({
                    url: "${createLink(controller: 'expense', action: 'save')}?totalPrice=" + totalPrice,
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'expense',action: 'index')}";
                        } else {
                            showErrorMsg(data.message);
                        }
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
            "sAjaxSource": "${g.createLink(controller: 'expense',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(5)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false }
            ]
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'expense',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        var invoiceDate = data.obj.invoiceDate;
                        clearForm('#edit-form');
                        $('#invoiceId').val(data.obj.id);
                        $('#invoiceDateEdit').datepicker('setDate', new Date(invoiceDate));
                        if (data.obj.verified == true) {
                            $('#verified').prop('checked', true);
                        } else {
                            $('#notVerified').prop('checked', true);
                        }

                        if (data.obj.paid == true) {
                            $('#paid').prop('checked', true);
                        } else {
                            $('#unPaid').prop('checked', true);
                        }

                        $('#editModal').modal('show');
                    } else {
                        showErrorMsg(data.message);
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
                    url: "${g.createLink(controller: 'expense',action: 'changeStatus')}?id=" + referenceId,
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
        var actionButtons = "";
        actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="edit-reference" title="Edit">';
        actionButtons += '<span class="green glyphicon glyphicon-edit"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<span class="red glyphicon glyphicon-trash"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<i class="icon-external-link"></i>';
        actionButtons += '</a></span>';
        return actionButtons;
    }

</script>
</body>
</html>
