<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Payment</title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Grails Lab Payment List" SHOW_CREATE_LINK="YES" createLinkText="Create Payment" createLinkUrl="${g.createLink(controller: 'glabPayment', action: 'createPayment')}"/>
<div class="row" id="createForm">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Grailslab Payment
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                            <tr>
                                <th>SL</th>
                                <th>#Invoice</th>
                                <th>Description</th>
                                <th>Amount</th>
                                <th>Invoice Date</th>
                                <th>Due Date</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    var bookName, bookNameBarcode,reportParam;
    jQuery(function ($) {
        var paymentTable = $('#list-table').DataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'desc'],
            "sAjaxSource": "${g.createLink(controller: 'glabPayment',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(6)', nRow).html(getActionBtnCustom(nRow, aData,'fa fa-print,fa fa-credit-card, fa fa-pencil-square-o, fa fa-trash'));
                return nRow;
            },
            "aoColumns": [
                null,
                null,
                null,
                null,
                null,
                null,
                {"bSortable": false}
            ]
        });
    });
    $('#list-table').on('click', 'a.reference-1', function (e) {
        e.preventDefault();
        var control = this;
        var referenceId = $(control).attr('referenceId');
        reportParam = "${g.createLink(controller: 'glabPayment',action: 'print','_blank')}?id=" + referenceId;
        window.open(reportParam);
    });


    $('#list-table').on('click', 'a.reference-3', function (e) {
        var control = this;
        var referenceId = $(control).attr('referenceId');
        window.location.href = "${g.createLink(controller: 'glabPayment', action: 'edit')}?id=" + referenceId;
        e.preventDefault();
    });


    $('#list-table').on('click', 'a.reference-4', function (e) {
        var control = this;
        var confirmDel = confirm("Are you sure delete voucher? This action can't be undone!");
        if (confirmDel == true) {
            var referenceId = $(control).attr('referenceId');
            showLoading("#list-table");
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'glabPayment', action: 'deletePayment')}?id=" + referenceId,
                success: function (data, textStatus) {
                    $('#list-table').DataTable().ajax.reload();
                    showInfoMsg('Delete successfully !');
                    hideLoading("#list-table");
                    if (data.isError == false) {
                        paymentTable.draw(false);
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
</script>

</body>
</html>
