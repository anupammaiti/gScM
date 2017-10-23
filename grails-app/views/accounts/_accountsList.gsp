<div class="col-sm-12">
    <section class="panel">
        <header class="panel-heading">
            ACCOUNTS LIST
        </header>
        <div class="panel-body">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <div class="col-md-2">
                        <g:select class="form-control" id="glmstListPrintOptionType" name='glmstListPrintOptionType'
                                  from='${com.grailslab.enums.PrintOptionType.values()}'
                                  optionKey="key" optionValue="value"></g:select>
                    </div>
                    <button type="button" id="glmstListReportBtn" class="btn btn-primary">Show Report</button>
                </div>
            </form>
        </div>
    </section>
</div>
<script>
    jQuery(function ($) {
        $('#glmstListReportBtn').click(function (e) {
            e.preventDefault();
            var printType = $('#glmstListPrintOptionType').val();
                reportParam ="${g.createLink(controller: 'accountingReport',action: 'glmstListReport','_blank')}?printOptionType="+printType;
                window.open(reportParam);
        });
    });
</script>