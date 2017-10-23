<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            By Date Voucher
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="byDateVoucherStartDate" name="startDate"
                                     tabindex="2" placeholder="Start Date" required="required"/>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="byDateVoucherEndDate" name="endDate"
                                     tabindex="3" placeholder="End Date" required="required"/>
                    </div>
                <div class="col-md-2">
                    <g:select class="form-control" id="byDatePrintType" name='printOptionType'
                              from='${com.grailslab.enums.PrintOptionType.values()}'
                              optionKey="key" optionValue="value"></g:select>
                </div>
                <button type="button" id="byDatePrintTypeReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>
<script>
    jQuery(function ($) {
        $('#byDateVoucherStartDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#byDateVoucherEndDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#byDatePrintTypeReportBtn').click(function (e) {
            e.preventDefault();
            var startDate = $('#byDateVoucherStartDate').val();
            var endDate = $('#byDateVoucherEndDate').val();
            var printOptionType = $('#byDatePrintType').val();
                reportParam ="${g.createLink(controller: 'accountingReport',action: 'voucherByDate','_blank')}?startDate="+startDate+"&endDate="+endDate+"&printOptionType="+printOptionType;
                window.open(reportParam);
        });
    });
</script>