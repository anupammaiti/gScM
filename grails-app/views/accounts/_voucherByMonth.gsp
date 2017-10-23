<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            BY MONTHLY VOUCHER
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <select id="voucherByMonthYear" class="form-control" name="voucherYear">
                            <option value="">Voucher year</option>
                            <option value="2016">2016</option>
                            <option value="2017">2017</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select id="monthName" class="form-control" name="monthName">
                            <option value="">Voucher month</option>
                            <option value="january">JANUARY</option>
                            <option value="february">FEBRUARY</option>
                            <option value="march">MARCH</option>
                            <option value="april">APRIL</option>
                            <option value="may">MAY</option>
                            <option value="june">JUNE</option>
                            <option value="july">JULY</option>
                            <option value="august">AUGUST</option>
                            <option value="september">SEPTEMBER</option>
                            <option value="october">OCTOBER</option>
                            <option value="november">NOVEMBER</option>
                            <option value="december">DECEMBER</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <g:select class="form-control" id="monthPrintType" name='printOptionType'
                                  from='${com.grailslab.enums.PrintOptionType.values()}'
                                  optionKey="key" optionValue="value"></g:select>
                    </div>
                    <button type="button" id="monthReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>

<script>
    jQuery(function ($) {
        $('#monthReportBtn').click(function (e) {
            e.preventDefault();
            var voucherYearName = $('#voucherByMonthYear').val();
            var voucherMonthName = $('#monthName').val();
            var printOptionType = $('#monthPrintType').val();
                reportParam ="${g.createLink(controller: 'accountingReport',action: 'monthlyVoucher','_blank')}?voucherYearName="+voucherYearName+"&voucherMonthName="+voucherMonthName+"&printOptionType="+printOptionType;
                window.open(reportParam);
        });
    });
</script>