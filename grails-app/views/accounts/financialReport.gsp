<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleAccountsLayout"/>
    <title>Accounts Reports</title>
</head>

<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Voucher Reports"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Trial Balance
            </header>
            <div class="panel-body">

            </div>
        </section>
    </div>
    <g:render template='/accounts/incomeStatement'/>
    <g:render template='/accounts/financialStatement'/>
    <g:render template='/accounts/balanceSheet'/>
</div>


<script>

    var reportParam, startDate,endDate,academicYear,className, studentStatus ,contact,gender,religion,section,student,printOptionType,discount,reportSortType,reportType;
    jQuery(function ($) {
        $('#voucherNo').select2({
            placeholder: "Search voucher",
            allowClear: true,
            minimumInputLength:3,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "${g.createLink(controller: 'remote',action: 'voucherList')}",
                dataType: 'json',
                quietMillis: 250,
                data: function (term, page) {
                    return {
                        q: term
                    };
                },
                results: function (data, page) { // parse the results into the format expected by Select2.
                    // since we are using custom formatting functions we do not need to alter the remote JSON data
                    return { results: data.items };
                },
                cache: true
            },
            formatResult: repoFormatResult, // omitted for brevity, see the source of this page
            formatSelection: repoFormatSelection,  // omitted for brevity, see the source of this page
            dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
            escapeMarkup: function (m) { return m; } // we do not want to escape markup since we are displaying html in results
        });

        $('#voucherReportBtn').click(function (e) {
            e.preventDefault();
            var voucher = $('#voucherNo').val();
            var printType = $('#voucherPrintType').val();
            if(voucher){
            reportParam ="${g.createLink(controller: 'accountingReport',action: 'voucherReport','_blank')}?voucherNo="+voucher+"&voucherPrintType="+printType;
            window.open(reportParam);
            } else{
                alert("Enter Voucher First");
            }
        });

    });

</script>

</body>
</html>