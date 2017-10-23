<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Chart of Accounts</title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Voucher List" SHOW_CREATE_LINK="YES" createLinkText="Create Voucher" createLinkUrl="${g.createLink(controller: 'accounts', action: 'createVoucher')}"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Voucher
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                            <tr>
                                <th>SL</th>
                                <th># Voucher</th>
                                <th>Date</th>
                                <th>Narration</th>
                                <th>Status</th>
                                <th>Posted</th>
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
    var bookName, bookNameBarcode;
    jQuery(function ($) {
        var voucherTable = $('#list-table').DataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'desc'],
            "sAjaxSource": "${g.createLink(controller: 'accounts',action: 'voucherList')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(6)', nRow).html(getActionBtnCustom(nRow, aData,'fa fa-pencil-square-o, fa fa-trash'));
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
        var control = this;
        var referenceId = $(control).attr('referenceId');
        window.location.href = "${g.createLink(controller: 'accounts',action: 'createVoucher')}?id=" + referenceId;
        e.preventDefault();
    });
    
    $('#list-table').on('click', 'a.reference-2', function (e) {
        var control = this;
        var confirmDel = confirm("Are you sure delete voucher? This action can't be undone!");
        if (confirmDel == true) {
            var referenceId = $(control).attr('referenceId');
            showLoading("#list-table");
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'accounts',action: 'deleteVoucher')}?id=" + referenceId,
                success: function (data, textStatus) {
                    hideLoading("#list-table");
                    if (data.isError == false) {
                        listTable.draw(false);
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
