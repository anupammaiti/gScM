<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
           Balance Sheet
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">

                %{--<button type="button" id="cashOrBankReportBtn" class="btn btn-primary">Show Report</button>--}%
                </div>
            </form>
        </div>
    </section>
</div>

<script>
    jQuery(function ($) {

        $('#startDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#endDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#cashOrBankReportBtn').click(function (e) {
            e.preventDefault();
            var startDate = $('#startDate').val();
            var endDate = $('#endDate').val();
            var voucherSource = $('#cashBankBook').val();
            var printType = $('#voucherPrintType').val();
            if(voucherSource){
                reportParam ="${g.createLink(controller: 'accountingReport',action: 'cashOrBankReport','_blank')}?voucherSource="+voucherSource+"&startDate="+startDate+"&endDate="+endDate+"&sourcePrintType="+printType;
                window.open(reportParam);
            } else{
                alert("Enter Voucher Name First");
            }
        });
    });
</script>