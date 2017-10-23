<html>
<head>
    <title>Salary Increment</title>
    <meta name="layout" content="moduleHRLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Salary Increment" SHOW_LINK_BTN="YES" linkBtnText="Confirm Salary Increment" SHOW_CREATE_BTN="YES" createButtonText="Add Increment"/>
<div class="row" id="create-form-holder" style="display: none;" >
    <div class="col-md-12">
        <section class="panel">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-10 col-md-offset-1">
                        <form class="cmxform form-horizontal" id="create-form">
                            <g:hiddenField name="id"/>
                            <div class="row">
                                <div class="form-group">
                                    <div class="col-md-3">
                                        <grailslab:select name="academicYear"  enums="true" label="Year"
                                                          from="${com.grailslab.gschoolcore.AcademicYear.schoolYears()}"></grailslab:select>
                                    </div>
                                    <div class="col-md-4">
                                        <grailslab:select name="yearMonths"  label="Month" enums="true" from="${com.grailslab.enums.YearMonths.values()}"></grailslab:select>
                                    </div>

                                </div>
                                <div class="form-group">
                                    <label for="employee" class="col-md-1 control-label">Employee</label>
                                    <div class="col-md-6">
                                        <input type="hidden" class="form-control" id="employee" name="employee" tabindex="2" />
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="basic" class="col-md-1 control-label">Basic</label>
                                    <div class="col-md-2">
                                        <input class="form-control textField-onlyAllow-number" type="text" name="basic" id="basic"
                                               tabindex="2"/>
                                    </div>
                                    <label for="houseRent" class="col-md-1 control-label">HouseRent</label>
                                    <div class="col-md-2">
                                        <input class="form-control  textField-onlyAllow-number" type="text" name="houseRent" id="houseRent"
                                               tabindex="2"/>
                                    </div>

                                    <label for="medicals" class="col-md-1 control-label"> Medical </label>
                                    <div class="col-md-2">
                                        <input class="form-control  textField-onlyAllow-number" type="text" name="medical" id="medicals"
                                               tabindex="2"/>
                                    </div>
                                </div>
                            <div class="form-group">
                                <label for="inCharge" class="col-md-1 control-label">Incharge</label>
                                <div class="col-md-2">
                                    <input class="form-control textField-onlyAllow-number" type="text" name="inCharge" id="inCharge"
                                           tabindex="2"/>
                                </div>

                                <label for="houseRent" class="col-md-1 control-label">Others</label>
                                <div class="col-md-2">
                                    <input class="form-control textField-onlyAllow-number" type="text" name="others" id="others"
                                           tabindex="2"/>
                                </div>
                                <label for="grossSalary" class="col-md-1 control-label">Gross</label>
                                <div class="col-md-2">
                                    <input class="form-control  textField-onlyAllow-number" type="text" name="grossSalary" id="grossSalary"
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

<div class="row" id="confirm-form-holder" style="display: none;" >
    <div class="col-md-12">
        <section class="panel">el-body">
                <div class="row">
                    <div class="col-md-10 col-md-offset-1">
                        <form class="form-horizontal" id="confirm-increment-form">
                            <div class="row">
                                <div class="form-group">
                                    <div class="col-md-3">
                                        <grailslab:select name="academicYear" enums="true" label="Year" id="confYear"
                                                          from="${com.grailslab.gschoolcore.AcademicYear.schoolYears()}"></grailslab:select>
                                    </div>

                                    <div class="col-md-3">
                                        <grailslab:select name="yearMonths"  label="Month" enums="true" id="confMonth" from="${com.grailslab.enums.YearMonths.values()}"></grailslab:select>
                                    </div>
                                    <div class="col-md-3">
                                        <button class="btn btn-primary" tabindex="5" type="submit">Submit</button>
                                        <button class="btn btn-default cancel-btn-increment" tabindex="6"
                                                type="reset">Cancel</button>
                                    </div>

                                </div>

                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Salary Increment List
            </header>

            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th class="">SL NO</th>
                            <th>Month</th>
                            <th>Employee-ID</th>
                            <th>Name</th>
                            <th>Designation</th>
                            <th>Increment</th>
                            <th>Status</th>
                            <th>Edit | Delete </th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    var extraClassTable;
    jQuery(function ($) {
       var salaryIncrementTable = $('#list-table').DataTable({
            "sDom": "<'row'<'col-md-1 academicYear-filter-holder'><'col-md-2 month-filter-holder'><'col-md-9'f>r>t<'row'<'col-md-3'l><'col-md-3'i><'col-md-6'p>>",
            "bAutoWidth": true,
            "scrollX": false,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'asc'],
            "sAjaxSource": "${g.createLink(controller: 'salIncrement',action: 'list')}",
           "fnServerParams": function (aoData) {
               aoData.push(
                       {"name": "academicYear", "value": $('#filterAcademicYear').val()},
                       {"name": "yearMonths", "value": $('#filterYearMonths').val()}
               );
           },
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }

                $('td:eq(7)', nRow).html(getGridActionBtns(nRow, aData, 'edit,delete , '));

                return nRow;
            },
            "aoColumns": [
                {"bSortable": true,"sClass": "center"},
                null,
                null,
                null,
                null,
                null,
                null,
                {"bSortable": false,"sClass": "center"},
            ]

        });

        $('#list-table_wrapper div.academicYear-filter-holder').html('<select id="filterAcademicYear" class="form-control" name="filterAcademicYear"><g:each in="${com.grailslab.gschoolcore.AcademicYear.schoolYears()}" var="academicYear"><option value="${academicYear.key}">${academicYear.value}</option> </g:each></select>');
        $('#list-table_wrapper div.month-filter-holder').html('<select id="filterYearMonths" class="form-control" name="filterYearMonths"><option value="">All</option><g:each in="${com.grailslab.enums.YearMonths.values()}" var="yearMonths"><option value="${yearMonths.key}">${yearMonths.value}</option></g:each></select>');

        $('#filterAcademicYear').on('change', function (e) {
            showLoading("#create-form-holder");
            salaryIncrementTable.draw(false);
            hideLoading("#create-form-holder");
        });
        $('#filterYearMonths').on('change', function (e) {
            showLoading("#create-form-holder");
            salaryIncrementTable.draw(false);
            hideLoading("#create-form-holder");
        });

        $('.create-new-btn').click(function (e) {
            $("#confirm-form-holder").hide(500);
            $("#create-form-holder").toggle(500);
            $("#form-submit-btn").html("Save");
            $("#id").val("");
            $('#academicYear').attr('readonly', false);
            $("#academicYear").css("pointer-events","auto");
            $('#yearMonths').attr('readonly', false);
            $("#yearMonths").css("pointer-events","auto");
            $("#academicYear").val("");
            $("#yearMonths").val("");
            $("#select2-chosen-1").empty().append("Search for a Employee [employeeId/name/designation]");
            $("#s2id_employee").css("pointer-events","auto");
            e.preventDefault();
        });

        $(".cancel-btn").click(function () {
            $("#create-form-holder").hide(500);
        });
        $(".cancel-btn-increment").click(function () {
            $("#confirm-form-holder").hide(500);
        });

        $('.link-url-btn').click(function (e) {
            $("#confYear").val("");
            $("#confMonth").val("");
            $("#create-form-holder").hide(500);
            $("#confirm-form-holder").toggle(500);
            e.preventDefault();
        });

        $("#confirm-increment-form").submit(function(e) {
            var confirmStr = "Are you sure disburse salary? Action can't be undone.\n\nClick 'OK to confirm, or click 'Cancel' to stop this action.";
            bootbox.confirm(confirmStr, function(clickAction) {
                if(clickAction) {
                    showLoading("#confirm-form-holder");
                    $.ajax({
                        url: "${createLink(controller: 'salIncrement', action: 'confirmIncrement')}",
                        type: 'post',
                        dataType: "json",
                        data: $("#confirm-increment-form").serialize(),
                        success: function (data) {
                            hideLoading("#confirm-form-holder");
                            if (data.isError == false) {
                                $("#confirm-form-holder").hide(500);
                                showSuccessMsg(data.message);
                                salaryIncrementTable.draw(false);
                            } else {
                                showErrorMsg(data.message);
                            }
                        },
                        failure: function (data) {
                        }
                    })
                }
            });
            e.preventDefault();
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

        $("#create-form").submit(function(e) {
            e.preventDefault();
            showLoading("#create-form-holder");
            $.ajax({
                url: "${createLink(controller: 'salIncrement', action: 'save')}",
                type: 'post',
                dataType: "json",
                data: $("#create-form").serialize(),
                success: function (data) {
                    clearField();
                    salaryIncrementTable.draw(false);
                    hideLoading("#create-form-holder");
                    if (data.isError == false) {
                        showSuccessMsg(data.message);
                    } else {
                        showErrorMsg(data.message);
                    }

                },
                failure: function (data) {
                }
            })
            return false;
        });

        $('#list-table').on('click', 'a.edit-reference', function (e){
            $("#create-form-holder").show(500);
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'salIncrement',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $("#form-submit-btn").html("Update");
                        $("#id").val(data.obj.id);
                        $('#academicYear').val(data.obj.academicYear.name);
                        $('#yearMonths').val(data.obj.yearMonths.name);
                        $("#houseRent").val(data.obj.houseRent);
                        $("#basic").val(data.obj.basic)
                        $("#medicals").val(data.obj.medical);
                        $("#grossSalary").val(data.obj.grossSalary);
                        $("#inCharge").val(data.obj.inCharge);
                        $("#others").val(data.obj.others);
                        $("#employee").val(data.obj.employee.id);
                        $("#select2-chosen-1").empty().append(data.employeeName);
                        $("#s2id_employee").css("pointer-events","none");

                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();

        });

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
                        url: "${g.createLink(controller: 'salIncrement',action: 'delete')}?id=" + referenceId,
                        success: function (data, textStatus) {
                            if (data.isError == false) {
                                showSuccessMsg(data.message);
                                salaryIncrementTable.draw(false);
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
        function clearField(){
            $("#form-submit-btn").html("Save");
            $('#houseRent, #basic, #medicals, #inCharge, #others, #grossSalary, #employee').val("").attr('readonly', false).css({cursor:"text"});
            $("#select2-chosen-1").empty().append("Search for a Employee [employeeId/name/designation]");
            $("#s2id_employee").css("pointer-events","auto");
        }
        function addIncrementSalary(){
            var houseRent = ($('#houseRent').val() != "" && !isNaN($('#houseRent').val())) ? parseInt($('#houseRent').val()) : 0;
            var basic = ($('#basic').val() != "" && !isNaN($('#basic').val())) ? parseInt($('#basic').val()) : 0;
            var medicals = ($('#medicals').val() != "" && !isNaN($('#medicals').val())) ? parseInt($('#medicals').val()) : 0;
            var inCharge = ($('#inCharge').val() != "" && !isNaN($('#inCharge').val())) ? parseInt($('#inCharge').val()) : 0;
            var others = ($('#others').val() != "" && !isNaN($('#medicals').val())) ? parseInt($('#medicals').val()) : 0;
            var total = (houseRent+basic+medicals+inCharge+others)
            return total;
        }
        $("#grossSalary").keyup(function(){
            if($(this).val()>0){
                $('#houseRent, #basic, #medicals, #inCharge, #others').attr('readonly', true).css({cursor:"not-allowed"})
            }else{
                $('#houseRent, #basic, #medicals, #inCharge, #others').attr('readonly', false).css({cursor:"text"});
            }
        });
        $('#houseRent, #basic, #medicals, #inCharge, #others').keyup(function(){
            if(addIncrementSalary()>0){
                $('#grossSalary').attr('readonly', true).css({cursor:"not-allowed"}).val(addIncrementSalary());
            }else{
                $('#grossSalary').attr('readonly', false).css({cursor:"text"});
            }
        });

        $('.textField-onlyAllow-number').numeric();

    });
</script>
</body>
</html>