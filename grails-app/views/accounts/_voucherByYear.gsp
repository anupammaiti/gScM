<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            BY YEARLY VOUCHER
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <select id="voucherYear" class="form-control" name="voucherYear">
                            <option value="">Voucher year</option>
                            <option value="2016">2016</option>
                            <option value="2017">2017</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <g:select class="form-control" id="yearPrintType" name='printOptionType'
                                  from='${com.grailslab.enums.PrintOptionType.values()}'
                                  optionKey="key" optionValue="value"></g:select>
                    </div>
                    <button type="button" id="yearReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>

<script>
    jQuery(function ($) {
        $('#yearReportBtn').click(function (e) {
            e.preventDefault();
            var voucherSource = $('#voucherYear').val();
            var printOptionType = $('#yearPrintType').val();
            reportParam ="${g.createLink(controller: 'accountingReport',action: 'yearlyVoucher','_blank')}?voucherSource="+voucherSource+"&printOptionType="+printOptionType;
            window.open(reportParam);
        });
    });
</script>