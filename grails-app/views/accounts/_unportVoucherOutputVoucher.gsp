<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            Unpost Voucher/ Out of Balance Report
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <select id="unpostOutOfBalance" class="form-control" name="unpostOutOfBalance">
                            <option value="">Report type</option>
                            <option value="unpost">Unpost</option>
                            <option value="out_of_balance">Out of Balance</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="unportOutStartDate" name="startDate"
                                     tabindex="2" placeholder="From Date" required="required"/>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="unportOutToDate" name="endDate"
                                     tabindex="3" placeholder="To Date" required="required"/>
                    </div>
                <div class="col-md-2">
                    <g:select class="form-control" id="unportOutPrintType" name='unportOutPrintType'
                              from='${com.grailslab.enums.PrintOptionType.values()}'
                              optionKey="key" optionValue="value"></g:select>
                </div>
                <button type="button" id="unportOutPrintBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>
<script>
    jQuery(function ($) {
        $('#unportOutStartDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#unportOutToDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#unportOutPrintBtn').click(function (e) {
            e.preventDefault();
            var unpostOutOfBalance = $('#unpostOutOfBalance').val();
            var startDate = $('#unportOutStartDate').val();
            var endDate = $('#unportOutToDate').val();
            var printOptionType = $('#voucherPrintType').val();

            reportParam ="${g.createLink(controller: 'accountingReport',action: 'unpostOutOfBalance','_blank')}?startDate="+startDate+"&endDate="+endDate+"&unpostOutOfBalance="+unpostOutOfBalance+"&printOptionType="+printOptionType;
            window.open(reportParam);

        });
    });
</script>