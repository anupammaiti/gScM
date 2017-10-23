<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleCollectionLayout"/>
    <title>Discount</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Students Discount" SHOW_CREATE_BTN="YES"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Serial</th>
                            <th>Student</th>
                            <th>Section-Class</th>
                            <th>Fee Item</th>
                            <th>Discount(%)</th>
                            <th>Amount</th>
                            <th>Payable</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${dataReturn}" var="dataSet">
                            <tr>
                                <td>${dataSet[0]}</td>
                                <td>${dataSet[1]}</td>
                                <td>${dataSet[2]}</td>
                                <td>${dataSet[3]}</td>
                                <td>${dataSet[4]}</td>
                                <td>${dataSet[5]}</td>
                                <td>${dataSet[6]}</td>
                                <td>
                                        <span class="col-md-6 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="edit-reference" title="Edit"><span
                                                    class="green glyphicon glyphicon-edit"></span></a></span>
                                        <span class="col-md-6 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="delete-reference"
                                                                             title="Delete"><span
                                                    class="green glyphicon glyphicon-trash"></span></a></span>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<!-- Modal -->
<div class="modal fade" id="create-form-holder-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="create-form-holder-label">Add Student Discount</h4>
            </div>
            <form class="form-horizontal" role="form" id="create-form">
                <g:hiddenField name="id" id="discountId"/>
                <div class="modal-body">
                    <div class="row">
                        <div class="row">
                            <div class="form-group">
                                <label class="col-md-3 control-label">Student<span class="required">*</span></label>

                                <div class="col-md-8" id="student_dd-holder">
                                    <input type="hidden" class="form-control" id="student" name="student" tabindex="1" />
                                </div>
                                <div class="col-md-8" id="student-name-holder" style="display: none">
                                    <input class="form-control" id="student2" name="student2" tabindex="1" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label">Fee Item<span class="required">*</span></label>

                                <div class="col-md-8">
                                    <g:select class="form-control" id="feeItems" name='feeItems' tabindex="2"
                                              noSelection="${['': 'Select Fee...']}"
                                              from=''
                                              optionKey="id" optionValue="name"></g:select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label">Discount (%)<span class="required">*</span>
                                </label>

                                <div class="col-md-4">
                                    <input type="number" min="1" max="100" step="1" name="discountPercent"
                                           id="discountPercent" placeholder="Enter Discount (%)" class="form-control">
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
                <div class="modal-footer modal-footer-action-btns">
                    <button type="button" class="btn btn-default cancel-btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    <button type="submit" id="print-schedule-yes-btn" class="btn btn-large btn-primary btn-submit">Submit</button>
                </div>
            </form>
        </div>
    </div>
</div>


<script>
    jQuery(function ($) {
        $('#student').select2({
            placeholder: "Select Student...",
            allowClear: true,
            minimumInputLength:3,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "${g.createLink(controller: 'remote',action: 'studentTypeAheadList')}",
                dataType: 'json',
                quietMillis: 250,
                data: function (term, page) {
                    return {
                        q: term, // search term
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

        var validator = $('#create-form').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                feeItems: {
                    required: true
                },
                discountPercent: {
                    required: true,
                    number: true
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
                $.ajax({
                    url: "${createLink(controller: 'discount', action: 'save')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        if (data.isError == false) {
                            $("#discountPercent").val("");
                            showSuccessMsg(data.message);
                            var table = $('#list-table').DataTable().ajax.reload();
                            var discountId = $("#discountId").val();
                            if(discountId !=undefined && discountId !=""){
                                window.location.href = "${g.createLink(controller: 'discount',action: 'index')}";
                            }
                            $("#create-form-holder").hide(500);
                        } else {
                            showErrorMsg(data.message);
                        }

                    },
                    failure: function (data) {
                    }
                })
            }
        });
        $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0,'desc'],
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'discount',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(7)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
                null,
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false }
            ]
        });

        $('.create-new-btn').click(function (e) {
            $('#create-form-holder-modal').modal('show');
            e.preventDefault();
        });

        $(".cancel-btn").click(function () {
            var validator = $("#create-form").validate();
            validator.resetForm();
            $("#discountId").val('');
            $('#create-form-holder-modal').modal('hide');
        });

        $('#student').on('change', function (e) {
            var student = $('#student').val();
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                data: 'studentId=' + student,
                url: "${g.createLink(controller: 'discount',action: 'loadFee')}",
                success: function (data) {
                    if (data.isError == false) {
                        $('#feeItems').select2("destroy");
                        var $select = $('#feeItems');
                        $select.find('option').remove();
                        $.each(data.feeList,function(key, value)
                        {
                            $select.append('<option value=' + value.id + '>' + value.name + '</option>');
                        });
                        $('#feeItems').select2().enable(true);
                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'discount',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        clearForm('#create-form');
                        $('#discountId').val(data.obj.id);
                        $('#student_dd-holder').hide();
                        $('#student2').val(data.stdName);
                        $('#student2').prop('readonly', true);
                        $('#student-name-holder').show();
                        $('#discountPercent').val(data.obj.discountPercent);
                        $('#feeItems').select2("destroy");
                        $('#feeItems').append('<option value="' + data.feeItemsId + '" selected="selected">' + data.feeItemsName+ '</option>');
                        $('#feeItems').select2().enable(false);
                        $('#create-form-holder-modal').modal('show');
                        $("#discountPercent").focus();
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
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'discount',action: 'delete')}?id=" + referenceId,
                    success: function (data, textStatus) {
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

        $('#create-form-holder-modal').on('hide.bs.modal', function (e) {
            window.location.href = "${g.createLink(controller: 'discount',action: 'index')}";
        });
    });
    function repoFormatResult(repo) {
        var markup = '<div class="row-fluid">' +
                '<div class="span12">' + repo.name + '</div>' +
                '</div>';
        return markup;
    }

    function repoFormatSelection(repo) {
        return repo.name;
    }
    function getActionButtons(nRow, aData) {
        var actionButtons = "";
        actionButtons += '<span class="col-md-6 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="edit-reference" title="Edit">';
        actionButtons += '<span class="green glyphicon glyphicon-edit"></span>';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-6 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<span class="red glyphicon glyphicon-trash"></span>';
        actionButtons += '</a></span>';
        return actionButtons;
    }

</script>
</body>
</html>
