<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            Cash Book/ Bank Book Report
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <select id="cashBankBook" class="form-control" name="cashBankBook">
                            <option value="">Report type</option>
                                <option value="cash">Cash Book</option>
                                <option value="bank">Bank Book</option>
                            </select>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="cashBankBookStartDate" name="startDate"
                                     tabindex="2" placeholder="From Date" required="required"/>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="cashBankBookOutToDate" name="endDate"
                                     tabindex="3" placeholder="To Date" required="required"/>
                    </div>
                <div class="col-md-2">
                    <g:select class="form-control" id="cashOrBankPrintType" name='cashOrBankPrintType'
                              from='${com.grailslab.enums.PrintOptionType.values()}'
                              optionKey="key" optionValue="value"></g:select>
                </div>
                <button type="button" id="cashOrBankReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>

<script>
    jQuery(function ($) {
        $('#cashBankBookStartDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#cashBankBookOutToDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });

        $('#cashOrBankReportBtn').click(function (e) {
            e.preventDefault();
            var startDate = $('#cashBankBookStartDate').val();
            var endDate = $('#cashBankBookOutToDate').val();
            var voucherSource = $('#cashBankBook').val();
            var printType = $('#cashOrBankPrintType').val();

                reportParam ="${g.createLink(controller: 'accountingReport',action: 'cashOrBankReport','_blank')}?voucherSource="+voucherSource+"&startDate="+startDate+"&endDate="+endDate+"&printOptionType="+printType;
                window.open(reportParam);

        });
    });
</script>