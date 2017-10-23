<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            Account Ledger
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <select id="mainAccountName" class="form-control" name="mainAccount">
                            <option value="">Main account</option>
                            <option value="mainAccountNo1">Account no 1</option>
                            <option value="mainAccountNo2">Account no 2</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select id="subAccountName" class="form-control" name="subAccount">
                            <option value="">Sub account</option>
                            <option value="subAccountNo1">Sub account no 1</option>
                            <option value="subAccountNo2">Sub account no 2</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="acStartDate" name="startDate"
                                     tabindex="2" placeholder="Start Date" required="required"/>
                    </div>
                    <div class="col-md-2">
                        <g:textField class="input-sm form-control" id="acEndDate" name="endDate"
                                     tabindex="3" placeholder="End Date" required="required"/>
                    </div>
                <div class="col-md-2">
                    <g:select class="form-control" id="accountPrintType" name='printOptionType'
                              from='${com.grailslab.enums.PrintOptionType.values()}'
                              optionKey="key" optionValue="value"></g:select>
                </div>
                <button type="button" id="accountReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>
<script>
    jQuery(function ($) {

        $('#acStartDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#acEndDate').datepicker({
            keyboardNavigation: false,
            todayBtn:true,
            format: 'dd/mm/yyyy',
            forceParse: false,
            autoclose: true
        });
        $('#accountReportBtn').click(function (e) {
            e.preventDefault();
            var startDate = $('#acStartDate').val();
            var endDate = $('#acEndDate').val();
            var mainAccountName = $('#mainAccountName').val();
            var subAccountName = $('#subAccountName').val();
            var printOptionType = $('#accountPrintType').val();

                reportParam ="${g.createLink(controller: 'accountingReport',action: 'accountLedger','_blank')}?subAccountName="+subAccountName+"&mainAccountName="+mainAccountName+"&startDate="+startDate+"&endDate="+endDate+"&printOptionType="+printOptionType;
                window.open(reportParam);
        });
    });
</script>