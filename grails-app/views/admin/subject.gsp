<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Subject Name </title>
    <meta name="layout" content="moduleStdMgmtLayout"/>
</head>

<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Manage Subject" SHOW_CREATE_BTN="YES" createButtonText="Add New Subject" SHOW_EXTRA_BTN1="YES" extraBtn1Text="Manage Sort Order"/>
<div class="row" id="create-form-holder" style="display: none;">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Add Subject
            </header>
            <div class="panel-body">
                <form class="form-horizontal" role="form" id="create-form">
                    <g:hiddenField name="id" id="subjectId"/>

                    <div class="row">
                        <div class="form-group">
                        <label class="col-md-2 control-label">Subject Name<span class="required">*</span></label>
                        <div class="col-md-4">
                            <input class="form-control" id="name" tabindex="1" name="name" placeholder="Enter Subject Name" required>
                        </div>
                        <label class="checkbox-inline">
                            <input  type="checkbox" title="Alternative Subject" name="hasChild"
                                   id="hasChild">Has Alternative Subject
                        </label>
                    </div>
                    <div class="form-group">
                        <label class="col-md-2 control-label">Subject code</label>
                        <div class="col-md-6">
                            <g:textField class="form-control" id="code" tabindex="3" name="code" placeholder="Enter Code"/>
                        </div>
                    </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">Sort Position<span class="required">*</span></label>
                            <div class="col-md-6">
                                <input tabindex="2" type="number" min="1" max="100" step="1" name="sortPosition" id="sortPosition" placeholder="Order show in List/Tabulation" class="form-control" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">Description</label>
                            <div class="col-md-6">
                                <g:textField class="form-control" id="description" tabindex="3" name="description" placeholder="Enter Description"/>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="form-group">
                            <div class="col-md-offset-8 col-lg-4">
                                <button class="btn btn-primary btn-submit"  tabindex="4" type="submit">Save</button>
                                <button class="btn btn-default cancel-btn" tabindex="5" type="reset">Cancel</button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    </div>
</div>
<div class="row" >
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Subject List
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Serial</th>
                            <th>Name</th>
                            <th>Code</th>
                            <th>Show Order</th>
                            <th>Description</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${dataReturn}" var="dataSet" status="i">
                            <tr>
                                <td>${dataSet[0]}</td>
                                <td>${dataSet[1]}</td>
                                <td>${dataSet[2]}</td>
                                <td>${dataSet[3]}</td>
                                <td>${dataSet[4]}</td>

                                <td>
                                        <span class="col-md-4 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="edit-reference" title="Edit"><span
                                                    class="green glyphicon glyphicon-edit"></span></a></span>
                                        <span class="col-md-4 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                             class="delete-reference"
                                                                             title="Delete"><span
                                                    class="green glyphicon glyphicon-trash"></span></a></span>
                                        <g:if test="${dataSet.hasChild}">
                                            <span class="col-md-4 no-padding"><a href="" referenceId="${dataSet.DT_RowId}"
                                                                                 class="addSubject-reference"
                                                                                 title="Add Alternative Subjects"><span
                                                        class="green glyphicon glyphicon-plus"></span></a></span>
                                        </g:if>
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

<script>
    jQuery(function ($) {
        $("#create-form").submit(function() {
            jQuery.ajax({
                type: 'POST',
                dataType:'JSON',
                data: $("#create-form").serialize(),
                url: "${createLink(controller: 'subject', action: 'save')}",
                success: function (data) {
                    if (data.isError == false) {
                        clearForm("#create-form");
                        var table = $('#list-table').DataTable().ajax.reload();
                        showSuccessMsg(data.message);
                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            return false; // avoid to execute the actual submit of the form.
        });


        var oTable1 = $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0,'asc'],
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'subject',action: 'list')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(5)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },

            "aoColumns": [
                null,
                null,
                null,
                null,
                null,

                { "bSortable": false }
            ]
        });

        $('.create-new-btn').click(function (e) {
            $("#create-form-holder").toggle(1000);
            $("#name").focus();
            e.preventDefault();
        });
        $('.extra-btn-1').click(function (e) {
            window.location.href = "${createLink(controller: 'subject', action: 'sortOrder')}";
            e.preventDefault();
        });

        $(".cancel-btn").click(function(){
            clearForm("#create-form");
            $("#subjectId").val('');
            $("#create-form-holder").hide(500);
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'subject',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        clearForm('#create-form');
                        $('#subjectId').val(data.obj.id);
                        $('#name').val(data.obj.name);
                        $('#code').val(data.obj.code);
                        $('#description').val(data.obj.description);
                        $('#sortPosition').val(data.obj.sortPosition);
                        $("#hasChild").prop("checked", data.obj.hasChild);
                        $("#create-form-holder").show(500);
                        $("#name").focus();
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
                    url: "${g.createLink(controller: 'subject',action: 'inactive')}?id=" + referenceId,
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


        $('#list-table').on('click', 'a.addSubject-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var control = this;
            var referenceId = $(control).attr('referenceId');
            window.location.href = "${g.createLink(controller: 'subject',action: 'addSubject')}/"+referenceId;
            e.preventDefault();
        });
    });

    function getActionButtons(nRow, aData) {
        var actionButtons = "";
        actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="edit-reference" title="Edit">';
        actionButtons += '<span class="green glyphicon glyphicon-edit"></span>';
        actionButtons += '</a></span>';

        actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<span class="red glyphicon glyphicon-trash"></span>';
        actionButtons += '</a></span>';
        if (aData.hasChild) {
            actionButtons += '<span class="col-md-4 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="addSubject-reference" title="Add alternative subjects">';
            actionButtons += '<span class="red glyphicon glyphicon-plus"></span>';
            actionButtons += '</a></span>';
        }
        return actionButtons;
    }

</script>
</body>
</html>


