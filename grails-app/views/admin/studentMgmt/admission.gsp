<%@ page import="com.grailslab.gschoolcore.AcademicYear" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleStdMgmtLayout"/>
    <title>New Student Admission - dropdown selection</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="New Student Admission [ Year: ${workingYear.value} ]" SHOW_CREATE_BTN="YES" createButtonText="New Admission" SHOW_PRINT_BTN="YES"/>
<div class="row" id="create-form-holder" style="display: none;">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading gschool-header-bg">
                <span class="alert-link"> New Student Admission [ Year: ${workingYear.value} ]</span>
            </header>
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-10 col-md-offset-1">
                        <form class="cmxform form-horizontal" id="create-form">
                            <g:hiddenField name="id"/>
                            <g:hiddenField name="academicYear" value="${workingYear.key}"/>
                            <g:hiddenField name="admissionMonth" value="${com.grailslab.enums.YearMonths.JANUARY.key}"/>
                            <div class="row">
                                <div class="form-group">
                                    <label class="col-md-2 control-label">Select Class</label>
                                    <div class="col-md-5">
                                        <g:select tabindex="2" class="form-control" id="formClassName" name='formClassName'
                                                  noSelection="${['': 'Select Class.....']}"
                                                  from='${classList}'
                                                  optionKey="id" optionValue="name"></g:select>
                                    </div>

                                    <div class="col-md-5">
                                        <select tabindex="3" class="form-control" id="section" name="section">
                                            <option value="">All Section</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="registration" class="col-md-2 control-label">Student</label>
                                    <div class="col-md-10">
                                            <g:select tabindex="5" class="form-control" id="registration"
                                                      name='registration'
                                                      from='${newStudents}'
                                                      optionKey="id" optionValue="name"></g:select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="rollNo" class="col-md-2 control-label">Roll</label>
                                    <div class="col-md-4">
                                        <g:textField class="form-control" id="rollNo" tabindex="6" name="rollNo"
                                                     placeholder="Enter roll"/>
                                    </div>

                                    <div class="col-md-4">
                                        <button class="btn btn-primary" tabindex="7" id="form-submit-btn" type="submit">Save</button>
                                        <button class="btn btn-default cancel-btn" tabindex="8"
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
            %{--<header class="panel-heading">
                Student List
            </header>--}%
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Serial</th>
                            <th>STD-ID</th>
                            <th>Name</th>
                            <th>Roll</th>
                            <th>Father's Name</th>
                            <th>Birth Date</th>
                            <th>Mobile</th>
                            <th>Class - Section</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<script>
    var workingYear, viewHrefUrl, academicYear, className, sectionName, printParam;
    jQuery(function ($) {
        viewHrefUrl = "${g.createLink(controller: 'registration',action: 'addDetails')}/";
        $('#registration').select2();
        var validator = $('#create-form').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                registration: {
                    required: true
                },
                section: {
                    required: true
                },
                rollNo: {
                    required: true,
                    digits: true
                }
            },
            highlight: function (e) {
                $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
            },
            success: function (e) {
                $(e).closest('.form-group').removeClass('has-error').addClass('has-info');
                $(e).remove();
            },
            submitHandler: function (form) {
                showLoading("#create-form-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    data: $("#create-form").serialize(),
                    url: "${g.createLink(controller: 'student',action: 'saveAdmission')}",
                    success: function (data, textStatus) {
                        hideLoading("#create-form-holder");
                        if (data.isError == false) {
                            $("#id").val('');
                            $("#rollNo").val('');
                            $("#form-submit-btn").html("Submit");
                            var table = $('#list-table').DataTable().ajax.reload();
                            $("#registration option:selected").remove();
                            $('#registration').select2("destroy");
                            $('#registration').select2().enable(true);
                            $("#rollNo").val(data.rollNumber);
                            $("#rollNo").focus();
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });

        var table = $('#list-table').dataTable({
            "sDom": "<'row'<'col-md-3 className-filter-holder'><'col-md-3 section-filter-holder'><'col-md-6'f>r>t<'row'<'col-md-3'l><'col-md-3'i><'col-md-6'p>>",
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [2, 'desc'],
            "sAjaxSource": "${g.createLink(controller: 'student',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(8)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "fnServerParams": function (aoData) {
                aoData.push({ "name": "sectionName", "value": $('#filterSection').val() },
                        { "name": "className", "value": $('#filterClassName').val() },
                        { "name": "academicYear", "value": $('#academicYear').val() });
            },
            "aoColumns": [
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                { "bSortable": false }
            ]
        });
        $('#list-table_wrapper div.className-filter-holder').html('<select id="filterClassName" class="form-control" name="filterClassName"><option value="">All Class</option><g:each in="${classList}" var="className"><option value="${className.id}">${className.name}</option> </g:each></select>');
        $('#list-table_wrapper div.section-filter-holder').html('<select id="filterSection" class="form-control" name="filterSection"><option value="">All Section</option></select>');

        $('#formClassName').on('change', function (e) {
            className = $('#formClassName').val();
            academicYear = $('#academicYear').val();
            if (className.length === 0) {
                $('#filterClassName').val("")
            } else {
                $('#filterClassName').val(className)
            }
            loadSectionOnClassChange(className, academicYear);
            $('#filterSection').val("").trigger("change");
        });
        $('#filterClassName').on('change', function (e) {
            className = $('#filterClassName').val();
            academicYear = $('#academicYear').val();
            if (className.length === 0) {
                $('#formClassName').val("")
            } else {
                $('#formClassName').val(className)
            }
            loadSectionOnClassChange(className, academicYear);
            $('#filterSection').val("").trigger("change");
        });

        $('#filterSection').on('change', function (e) {
            showLoading("#data-table-holder");
            $("#list-table").DataTable().draw(false);
            hideLoading("#data-table-holder");
        });

        $('#section').on('change', function (e) {
            sectionName = $('#section').val();
            academicYear = $('#academicYear').val();
            showLoading("#create-form-holder");
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                data: 'inputSection=' + sectionName + '&inputAcademicYear=' + academicYear,
                url: "${g.createLink(controller: 'student',action: 'lastRoll')}",
                success: function (data, textStatus) {
                    hideLoading("#create-form-holder");
                    if (data.isError == false) {
                        if (data.rollNumber == null) {
                            $('#rollNo').val(1);
                        } else {
                            $('#rollNo').val(data.rollNumber);
                        }
                    } else {
                        $('#rollNo').val("");
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            $('#filterSection').val(sectionName).trigger("change");
        });

        $(".cancel-btn").click(function () {
            var validator = $("#create-form").validate();
            validator.resetForm();
            $("#id").val('');
            $("#create-form-holder").hide(500);
        });

        $('.print-btn').click(function (e) {
            e.preventDefault();
            className = $('#filterClassName').find("option:selected").val();
            sectionName =$('#filterSection').find("option:selected").val();
            printParam = "${g.createLink(controller: 'studentReport',action: 'studentList','_blank')}?section="+sectionName+"&className="+className;
            window.open(printParam);
            return false;
        });

        $('.create-new-btn').click(function (e) {
            $("#create-form-holder").toggle(500);
            $("#section").focus();
            e.preventDefault();
        });


        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#data-table-holder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'student',action: 'delete')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        hideLoading("#data-table-holder");
                        if (data.isError == false) {
                            $("#list-table").DataTable().row(selectRow).remove().draw(false);
                            showSuccessMsg(data.message);
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
    });

    function getActionButtons(nRow, aData) {
        var actionButtons = "";
        actionButtons += '<span class="col-md-4 no-padding"><a href="#" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<span class="green glyphicon glyphicon-trash"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-3 no-padding"><a target="_blank" href="' + viewHrefUrl + aData.registrationId + '" class="view-reference" title="Edit Details">';
        actionButtons += '<span class="fa fa-plus-square-o"></span>';
        actionButtons += '</a></span>';
        return actionButtons;
    }
    function loadSectionOnClassChange(classNameOnchange, academicYear) {
        var $section = $('#section');
        $section.find('option').remove();
        $section.append('<option value="">All Section</option>');
        var $filterSection = $('#filterSection');
        $filterSection.find('option').remove();
        $filterSection.append('<option value="">All Section</option>');
        if (classNameOnchange.length === 0) {
            return false
        }
        jQuery.ajax({
            type: 'POST',
            dataType: 'JSON',
            url: "${g.createLink(controller: 'remote',action: 'classSectionList')}?className=" + classNameOnchange+"&academicYear="+academicYear,
            success: function (data, textStatus) {
                if (data.isError == false) {
                    $.each(data.sectionList, function (key, value) {
                        $section.append('<option value=' + value.id + '>' + value.name + '</option>');
                        $filterSection.append('<option value=' + value.id + '>' + value.name + '</option>');
                    });
                } else {
                    showErrorMsg(data.message);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    }

</script>
</body>
</html>
