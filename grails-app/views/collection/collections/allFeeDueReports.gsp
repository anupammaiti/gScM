<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleCollectionLayout"/>
    <title>Monthly Fee Pay/Due Report</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Fee Pay/Due Report"/>
<div class="row">
    <div class="col-md-12">
        <section class="panel">
            <header class="panel-heading">
                Monthly Fees
            </header>
            <div class="panel-body">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="allClassName" class="col-md-3 control-label">Class Name*</label>
                        <div class="col-md-8">
                            <g:select tabindex="2" class="form-control" id="allClassName"
                                      noSelection="${['': 'Select Class...']}"
                                      name='allClassName'
                                      from='${classNameList}'
                                      optionKey="id" optionValue="name"></g:select>
                        </div>
                    </div>
                    <div id="allFeeNameHolder" style="display: none;">
                        <div class="form-group">
                            <label for="allFeeItems" class="col-md-3 control-label">Fee Name*</label>
                            <div class="col-md-8">
                                <select multiple class="form-control" id="allFeeItems" name="allFeeItems" tabindex="2"></select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="uptoMonth" class="col-md-3 control-label">Up to Month</label>
                            <div class="col-md-8">
                                <g:select tabindex="2" class="form-control" id="uptoMonth" value="${formatDate(date: new Date().minus(30), format: 'MMMM').toUpperCase()}"
                                          name='uptoMonth'
                                          from='${com.grailslab.enums.YearMonths.values()}'
                                          optionKey="key" optionValue="value"></g:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="payStatus" class="col-md-3 control-label">Pay Status</label>
                            <div class="col-md-3">
                                <select name="payStatus" id="payStatus" class="form-control" tabindex="1">
                                    <option value="due">Due</option>
                                    <option value="paid">Paid</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <button class="btn btn-info" id="printMonthlyFeebtn">&nbsp;&nbsp; Print</button>
                            </div>
                            <div class="col-md-2">
                                <button class="btn btn-info" id="sendSmsMonthlyFeebtn">Send SMS</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    var className, academicYear,feeItem;
    jQuery(function ($) {
        $('#allClassName').select2().enable(true);
        $("#allFeeItems").select2({
            placeholder: "Select One or More Fee Items",
            allowClear: true
        });
        $('#allClassName').on('change', function (e) {
            className = $('#allClassName').val();
            if(className != ""){
                showLoading("#create-form-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'collections',action: 'loadClassFee')}?allFeeDue=true&className="+className,
                    success: function (data) {
                        hideLoading("#create-form-holder");
                        if (data.isError == false) {
                            var $selectMulti = $("#allFeeItems").select2();
                            $selectMulti.find('option').remove();
                            if(data.feeList){
                                $.each(data.feeList, function (key, feeItem) {
                                    $selectMulti.append('<option value=' + feeItem.id + '>' + feeItem.name + '</option>');
                                });
                                $selectMulti.val(data.selectedList).trigger("change");
                                $("#allFeeNameHolder").show();
                            }else {
                                $("#allFeeNameHolder").hide();
                            }

                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }else {
                $("#allFeeItems").find('option').remove();
                $("#allFeeNameHolder").hide();
            }
            e.preventDefault();
        });

        $('#load-student-btn').click(function (e) {
            e.preventDefault();
        });
        $('#print-btn').click(function (e) {
            var className=$('#className').val();
            var allFeeItems=$('#allFeeItems').val();
            var payStatus=$('#payStatus').val();
            var itemDetail=$('#itemDetail').val();
            var sectionParam = "${g.createLink(controller: 'accountsReport',action: 'feePayReport')}?className="+className+"&feeItems="+feeItems+"&payStatus="+payStatus+"&itemDetail="+itemDetail;
            window.open(sectionParam);
            e.preventDefault();
        });
    });
</script>
</body>
</html>