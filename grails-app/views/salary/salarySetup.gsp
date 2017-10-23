
<html>
<head>
    <title>Salary Setup</title>
    <meta name="layout" content="moduleHRLayout"/>
<style>
.form-group {
    margin-bottom: 5px;
}
#medical {margin-top: 0px;}

</style>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Salary Setup" SHOW_CREATE_BTN="YES"
     createButtonText="New Salary Setup"  SHOW_PRINT_BTN="YES" SHOW_LINK_BTN="YES" linkBtnText="Config"/>
<div class="row" id="create-form-holder" style="display: none;">
    <div class="col-md-12">
    <section class="panel">
        <div class="panel-body">
            <div class="row">
                <div class="col-md-10 col-md-offset-1">
                    <form class="cmxform form-horizontal" id="create-form">
                        <g:hiddenField name="id"/>
                        <g:hiddenField name="adOutStandingAmount" value="0"/>
                        <g:hiddenField name="adInstallmentAmount" value="0"/>
                        <div class="row">
                        <div class="form-group">
                            <label for="employee" class="col-md-1 control-label">Employee</label>
                            <div class="col-md-6">
                                <input type="hidden" class="form-control" id="employee" name="employee" tabindex="2" />
                            </div>
                        </div>

                          <div class="form-group">
                            <label for="basic" class="col-md-1 control-label">Basic</label>
                            <div class="col-md-2">
                                <input class="form-control grossGroup netPaygroup" type="text" name="basic" id="basic"
                                       tabindex="2"/>
                            </div>
                            <label for="houseRent" class="col-md-1 control-label">HouseRent</label>
                            <div class="col-md-2">
                                <input class="form-control grossGroup netPaygroup" type="text" name="houseRent" id="houseRent"
                                       tabindex="2"/>
                            </div>

                            <label for="medical" class="col-md-1 control-label"> Medical </label>
                            <div class="col-md-2">
                                <input class="form-control grossGroup netPaygroup" type="text" name="medical" id="medical"
                                       tabindex="2"/>
                            </div>
                              <label for="grossSalary" class="col-md-1 control-label">Gross</label>
                              <div class="col-md-2">
                                  <input class="form-control grossGroup netPaygroup" type="text" name="grossSalary" id="grossSalary"
                                         tabindex="2"/>
                              </div>
                        </div>
                        <div class="form-group">
                            <label for="inCharge" class="col-md-1 control-label">Incharge</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="inCharge" id="inCharge"
                                       tabindex="2"/>
                            </div>

                            <label for="mobileAllowance" class="col-md-1 control-label">M.Allowance</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="mobileAllowance" id="mobileAllowance"
                                       tabindex="2"/>
                            </div>
                            <label for="houseRent" class="col-md-1 control-label">others</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="others" id="others"
                                       tabindex="2"/>
                            </div>
                            <label for="area" class="col-md-1 control-label" style="display: none">Area</label>
                            <div class="col-md-2 " style="display: none">
                                <input class="form-control netPaygroup" type="text" name="area" id="area"
                                       tabindex="2"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="pfStatus" class="col-md-1 control-label">PFStatus</label>
                            <div class="col-md-2">
                                <select name="pfStatus" id="pfStatus" class="form-control">
                                    <option value="1">Yes</option>
                                    <option value="0">No</option>
                                </select>
                            </div>

                            <label for="dpsAmount" class="col-md-1 control-label">DPS</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="dpsAmount" id="dpsAmount"
                                       tabindex="2"/>
                            </div>


                            <label for="adStatus" class="col-md-1 control-label">AdStatus</label>
                            <div class="col-md-2">
                                <select name="adStatus" id="adStatus" class="form-control">
                                    <option value="1">Yes</option>
                                    <option value="0">No</option>
                                </select>
                            </div>

                            <label for="adsAmount" class="col-md-1 control-label">Advance</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="adsAmount" id="adsAmount"
                                       tabindex="2"/>
                            </div>

                        </div>

                        <div class="form-group">
                            <label for="fine" class="col-md-1 control-label">Fine</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="fine" id="fine"
                                       tabindex="2"/>
                            </div>
                            <label for="pc" class="col-md-1 control-label">PC</label>
                            <div class="col-md-2">
                                <input class="form-control netPaygroup" type="text" name="pc" id="pc"
                                       tabindex="2"/>
                            </div>

                            <label for="netPayable" class="col-md-1 control-label"> NetPayable</label>
                            <div class="col-md-2">
                                <input class="form-control" type="text" name="netPayable" id="netPayable"
                                       tabindex="2"/>
                            </div>

                        </div>

                        <div class="pull-right">
                            <button class="btn btn-primary" tabindex="7" id="form-submit-btn" type="submit">Save</button>
                            <button class="btn btn-default cancel-btn" tabindex="8"
                                    type="reset">Cancel</button>
                        </div>
                    </div>
                </div>
            </form>
            </div>
        </div>
    </section>
    </div>
</div>

<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Salary Setup List
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr style="white-space: nowrap">
                            <th>Emp-ID</th>
                            <th >Emp- Name</th>
                            <th >Gross</th>
                            <th >Basic</th>
                            <th >HR</th>
                            <th >Medical</th>
                            <th >In charge</th>
                            <th >M.Allowance</th>
                            <th >Others</th>
                            <th >PFStatus</th>
                            <th >DPS</th>
                            <th >ADStatus</th>
                            <th >Advance</th>
                            <th>Fine</th>
                            <th>PC</th>
                            <th >Net Payable</th>
                            <th >Delete</th>

                        </tr>
                        </thead>
                        <tbody style="white-space: nowrap;">
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<script>
    jQuery(function ($) {
        var salarySetupTable = $('#list-table').DataTable({
            "sDom": "<'row'<'col-md-1 academicYear-filter-holder'><'col-md-2 month-filter-holder'><'col-md-9'f>r>t<'row'<'col-md-3'l><'col-md-3'i><'col-md-6'p>>",
            "bAutoWidth": true,
            "scrollX": false,
            "bServerSide": true,
            "scrollX": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'asc'],
            "sAjaxSource": "${g.createLink(controller: 'salarySetup',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:not(:last-child)',nRow).click(function() {editData(aData)});
                $('td:eq(16)', nRow).html(getGridActionBtns(nRow, aData, 'delete'));
                return nRow;
            },

            "aoColumns": [
                {"bSortable": true,"sClass": "center"},
                {"bSortable": true},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false,"sClass": "center"},
                {"bSortable": false}
            ],
        });

        $("#create-form").submit(function(e) {
            e.preventDefault();
            if($("#netPayable").val().length >0){
                $("#netPayable").css("border", "1px solid #e2e2e4");
                var pfStatusFlag=true;
                if($("#pfStatus").val()> 0){
                    if($("#dpsAmount").val().length>0 && parseInt($("#dpsAmount").val())>0){
                        pfStatusFlag=true;
                    }else {pfStatusFlag=false;}
                 }

                if(pfStatusFlag){
                    $("#dpsAmount").css("border", "1px solid #e2e2e4");
                    $.ajax({
                        url: "${createLink(controller: 'salarySetup', action: 'save')}",
                        type: 'post',
                        dataType: "json",
                        data: $("#create-form").serialize(),
                        success: function (data) {
                            if (data.isError == false) {
                                clearForm($("#create-form"));
                                salarySetupTable.draw(false);
                                showSuccessMsg(data.message);
                                $("#create-form-holder").hide(500);
                            } else {
                                showErrorMsg(data.message);
                            }

                        },
                        failure: function (data) {
                        }
                    })
                }else {
                    $("#dpsAmount").css("border", "red solid 1px");
                    //bootbox.alert("please put DPS Amount")
                }
            }else{
                $("#netPayable").css("border", "red solid 1px");
            }
            return false;
        });
        $('#employee').select2({
            placeholder: "Search for a Employee [employeeId/name/designation]",
            allowClear: true,
            minimumInputLength:3,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "${g.createLink(controller: 'remote',action: 'employeeWithDesignationList')}",
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


        $("#employee").on("change", function(e) {
            if($("#employee").val().length>0){
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'salarySetup', action: 'empPreSetupData')}?id="+$("#employee").val(),
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            $("#adOutStandingAmount").val(data.adOutStandingAmount)
                            if(data.adOutStandingAmount>0){
                                $("#adStatus").val("1")
                                $("#adsAmount").val(data.adOutStandingAmount<data.adInstallmentAmount?data.adOutStandingAmount:data.adInstallmentAmount)
                                $("#adsAmount").prop("readonly", true);
                            }else{
                                $("#adStatus").val("0")
                                $("#adsAmount").val('')
                                $("#adsAmount").prop("readonly", true);
                            }

                            //showSuccessMsg(data.message);
//
                        } else {
                            //  showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }

        });
        var  editData=function(adata){
            $("#create-form-holder").show(500);
            $("html, body").animate({ scrollTop: 0 }, 600);
            var referenceId = adata.DT_RowId
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'salarySetup',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $("#netPayable").prop("readonly", true);
                        $("#netPayable").css("border", "1px solid #e2e2e4");
                        $("#dpsAmount").css("border", "1px solid #e2e2e4");
                        $("#adStatus").css("pointer-events","none");
                        $("#form-submit-btn").html("Update");
                        $("#id").val(data.obj.id)
                        $("#employee").val(data.obj.employee.id)
                        $("#grossSalary").val(data.obj.grossSalary)
                        $("#basic").val(data.obj.basic)
                        $("#houseRent").val(data.obj.houseRent)
                        $("#medical").val(data.obj.medical)
                        $("#inCharge").val(data.obj.inCharge)
                        $("#mobileAllowance").val(data.obj.mobileAllowance)

                        $("#others").val(data.obj.others)
                        $("#area").val(data.obj.area)

                        $("#pfStatus").val(data.obj.pfStatus?"1":"0")
                        $("#dpsAmount").val(data.obj.dpsAmount)
                        if($("#pfStatus").val()>0){
                            $("#dpsAmount").prop("readonly", false);
                        }else{
                            $("#dpsAmount").prop("readonly", true);
                        }

                        $("#adOutStandingAmount").val(data.obj.adsAmount)
                        $("#adInstallmentAmount").val(data.obj.adsAmount)


                        $("#adStatus").val(data.obj.adStatus?"1":"0")
                        $("#adsAmount").val(data.obj.adsAmount)

                        if(data.obj.adStatus){
                            $("#adsAmount").prop("readonly", false);
                        }else{
                            $("#adsAmount").prop("readonly", true);
                        }

                        $("#fine").val(data.obj.fine)
                        $("#pc").val(data.obj.pc)
                        $("#netPayable").val(data.obj.netPayable)

                        $("#select2-chosen-1").empty().append(data.employeeName);
                        $("#s2id_employee").css("pointer-events","none");

                        $("#grossSalary").prop("readonly", false);
                        if ($("#basic").val().length == 0 && $("#houseRent").val().length == 0 && $("#medical").val().length == 0) {
                            $("#basic").prop("readonly", true);
                            $("#houseRent").prop("readonly", true);
                            $("#medical").prop("readonly", true);
                        }else{
                            $("#basic").prop("readonly", false);
                            $("#houseRent").prop("readonly", false);
                            $("#medical").prop("readonly", false);
                        }
                        if(data.obj.pfStatus){$("#pfStatus").prop("readonly", false);}else{$("#pfStatus").prop("readonly", true);}
                        if(data.obj.adStatus){$("#adStatus").prop("readonly", false);}else{$("#adStatus").prop("readonly", true);}
                        $("#create-form-holder").show(500);
                        $("#name").focus();

                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        };
        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var confirmStr = "Are you want to delete this  ." +
                    "  \n\nClick 'OK to confirm, or click 'Cancel' to stop this action.";
            var selectRow = $(this).parents('tr');
            var control = this;
            var referenceId = $(control).attr('referenceId');
            bootbox.confirm(confirmStr, function(clickAction) {
                if(clickAction) {
                    jQuery.ajax({
                        type: 'POST',
                        dataType: 'JSON',
                        url: "${g.createLink(controller: 'salarySetup',action: 'delete')}?id=" + referenceId,
                        success: function (data, textStatus) {
                            if (data.isError == false) {
                                showSuccessMsg(data.message);
                                salarySetupTable.draw(false);
                            } else {
                                showErrorMsg(data.message);
                            }
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                        }
                    });
                }
            });
            e.preventDefault();
        });
        $('.create-new-btn').click(function (e) {
            $("#create-form-holder").toggle(500);
            $("#form-submit-btn").html("Save");
            $("#netPayable").prop("readonly", true);
            $("#id").val("");
            $("#select2-chosen-1").empty().append("Search for a Employee [employeeId/name/designation]");
            $("#s2id_employee").css("pointer-events","auto");
            $("#employee").val("");
            $("#grossSalary").prop("readonly", false);
            $("#basic").prop("readonly", false);
            $("#houseRent").prop("readonly", false);
            $("#medical").prop("readonly", false);
            $("#netPayable").css("border", "1px solid #e2e2e4");
            $("#dpsAmount").css("border", "1px solid #e2e2e4");
            $("#adStatus").css("pointer-events","none");
            $("#name").focus();
            e.preventDefault();
        });
        $(".cancel-btn").click(function () {
            var validator = $("#create-form").validate();
            validator.resetForm();
            $("#id").val('');
            $("#create-form-holder").hide(500);
        });

        var arr = [ "grossSalary","basic","houseRent","medical","inCharge","mobileAllowance","others","area","dpsAmount","adsAmount","fine","pc","netPayable"];
        $.each( arr, function( i, val ) {
            var ids= "#"+val
            $(ids).numeric({decimalPlaces: 2 });
        });
        $('.grossGroup').change(function(){
            if($("#basic").val().length> 0 || $("#houseRent").val().length> 0 || $("#medical").val().length> 0){
                $("#grossSalary").prop("readonly", true);
                $("#grossSalary").val(math.chain($("#basic").val()).add($("#houseRent").val()).add($("#medical").val()).done());
            }else{
                //  $("#grossSalary").val(math.chain($("#basic").val()).add($("#houseRent").val()).add($("#medical").val()).done());
                $("#grossSalary").prop("readonly", false);
            }
        });

        $("#grossSalary").change(function(){
            if($("#grossSalary").val().length>0) {
                if ($("#basic").val().length == 0 && $("#houseRent").val().length == 0 && $("#medical").val().length == 0) {
                    $("#basic").prop("readonly", true);
                    $("#houseRent").prop("readonly", true);
                    $("#medical").prop("readonly", true);
                }
            }else{
                $("#basic").prop("readonly", false);
                $("#houseRent").prop("readonly", false);
                $("#medical").prop("readonly", false);
            }
        });
        $("#adsAmount").blur(function(){
            var totalAmount= $("#adOutStandingAmount").val()
            var adInstallmentAmount=$("#adInstallmentAmount").val()
            var paymentAmount=$("#adsAmount").val()
           if(parseInt(paymentAmount)>parseInt(totalAmount)){
                $("#adsAmount").val(parseInt(totalAmount)>parseInt(adInstallmentAmount)?parseInt(adInstallmentAmount):parseInt(totalAmount))
               $("#netPayable").val(
                                math.chain($("#grossSalary").val())
                               .add($("#inCharge").val())
                               .add($("#mobileAllowance").val())
                               .add($("#others").val())
                               .add($("#area").val())
                               .subtract($("#dpsAmount").val())
                               .subtract($("#adsAmount").val())
                               .subtract($("#fine").val())
                               .subtract ($("#pc").val())
                               .done())
                bootbox.alert("it is not allow more than Outstading Amount " +totalAmount)

            }
        })
        $('.netPaygroup').change(function(){
            $("#netPayable").val(
                    math.chain($("#grossSalary").val())
                            .add($("#inCharge").val())
                            .add($("#mobileAllowance").val())
                            .add($("#others").val())
                            .add($("#area").val())
                            .subtract($("#dpsAmount").val())
                            .subtract($("#adsAmount").val())
                            .subtract($("#fine").val())
                            .subtract ($("#pc").val())
                            .done())
        });

        $("#pfStatus").change(function(){
            if($("#pfStatus").val()==0){
                $("#dpsAmount").val(0)
                $("#dpsAmount").prop("readonly", true);
            }else {
                $("#dpsAmount").prop("readonly", false);
            }
            math.chain($("#netPayable").val()).subtract($("#dpsAmount").val())

        });
        $("#adStatus").change(function(){
            if($("#adStatus").val()==0){
                $("#adsAmount").val(0)
                $("#adsAmount").prop("readonly", true);
            }else {
                $("#adsAmount").prop("readonly", false);
            }

            math.chain($("#netPayable").val()).subtract($("#adsAmount").val())

        });

        $('.print-btn').click(function (e) {
            e.preventDefault();
            var printParam ="${g.createLink(controller: 'salaryReport',action: 'setUp','_blank')}";
            window.open(printParam);
            return false;
        });


        var $salaryConfig
        $(".link-url-btn").click(function(){
        $.post("${createLink(controller: 'salarySetup',action: 'salConfigData')}", function(data,status) {
          if(status==="success"){
              if (data.isError == false) {
               glModal('${g.createLink(controller: 'remote',action: 'loadModal', params: [modalName:"/modals/SalaryConfig"])}',function($modal){
                   $salaryConfig=$modal
                   $modal.find("#id").val(data.obj.id)
                   $modal.find("#extraClassRate").val(data.obj.extraClassRate)
                   $modal.find("#lateFineForDays").val(data.obj.lateFineForDays)
                   $modal.find("#pfContribution").val(data.obj.pfContribution)
                   $modal.find("#pfCalField").val(data.obj.pfCalField)

                  });
              } else {
              showErrorMsg(data.message);
              }
          }
          if(status==="error"){}

            },"json");

        })

        $(document).on('click',"#configbtn",function(){
            $.ajax({
                url: "${createLink(controller: 'salarySetup',action: 'saveConfig')}",
                type: 'post',
                dataType: "json",
                data: $("#createFormModal").serialize(),
                success: function (data) {
                    if (data.isError == false) {
                          showSuccessMsg(data.message);
                          $salaryConfig.modal("hide")

                    } else {
                  showErrorMsg(data.message);
                    }

                },
                failure: function (data) {
                }
            })
        })
    });
</script>
</body>
</html>