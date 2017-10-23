<%@ page import="com.grailslab.enums.EmpCategoryType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Expense Detail</title>
    <meta name="layout" content="adminLayout"/>
</head>

<body>

<grailslab:breadCrumbActions SHOW_CREATE_BTN="YES" createButtonText="Add New" controllerName="${params.controller}"/>
<grailslab:fullModal modalLabel="Expense Item">
    <input type="hidden" name="expenseHead" value="${params.id}"/>
    <input type="hidden" name="expenseItem" value="${params.expenseItem}"/>
    <grailslab:input required="true" name="name" label="Name"></grailslab:input>
    <grailslab:textArea  name="description" label="Description"></grailslab:textArea>
</grailslab:fullModal>
<grailslab:dataTable dataSet="${dataReturn}" tableHead="Serial,Expense Item,Name,Description,Status" actions="fa fa-pencil-square-o, fa fa-retweet, fa fa-trash-o"></grailslab:dataTable>
<script>
    jQuery(function ($) {
        var validator = $('#modalForm').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                name: {
                    required: true,
                    maxlength: 200
                }
            },
            errorPlacement: function (error, element) {
            },
            highlight: function (e) {
                $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
            },
            success: function (e) {
                $(e).closest('.form-group').removeClass('has-error').addClass('has-info');
                $(e).remove();

            },
            submitHandler: function (form) {
                $('#myModal .create-content .modal-footer-action-btn').hide();
                $('#myModal .create-content .modal-refresh-processing').show();
                $.ajax({
                    url: "${createLink(controller: 'expenseItem', action: 'detailSave')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#modalForm").serialize(),
                    success: function (data) {
                        formSuccess(data);
                    },
                    failure: function (data) {
                    }
                })
            }
        });

        $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 50,
            "deferLoading": ${totalCount?:0},
            "sAjaxSource": "${g.createLink(controller: 'expenseItem',action: 'detailList')}?expenseItem=${params.expenseItem}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(5)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false},
                {"bSortable": false}
            ]
        });

        $('.create-new-btn').click(function (e) {
            $('#myModal .create-success').hide();
            $('#myModal .create-content').show();
            validator.resetForm();
            $("#hiddenId").val('');
            $('#myModal').modal('show');
            e.preventDefault();
        });

        $(".cancel-btn").click(function () {
            var validator = $("#modalForm").validate();
            validator.resetForm();
            $("#hiddenId").val('');
            $('#myModal').modal('hide');
        });

        $('#list-table').on('click', 'a.reference-1', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'expenseItem',action: 'detailEdit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $('#hiddenId').val(data.obj.id);
                        $('#name').val(data.obj.name);
                        $('#description').val(data.obj.description);
                        $('#myModal .create-success').hide();
                        $('#myModal .create-content').show();
                        $('#myModal').modal('show');
                    } else {
                        alert(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.reference-2', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure Changing status?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'expenseItem',action: 'detailInactive')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            $('#list-table').DataTable().ajax.reload();
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

        $('#list-table').on('click', 'a.reference-3', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'expenseItem',action: 'detailDelete')}?id=" + referenceId,
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

    });

    function getActionButtons(nRow, aData) {
        return getActionBtnCustom(nRow, aData,'fa fa-pencil-square-o, fa fa-retweet, fa fa-trash-o');
    }

</script>
</body>
</html>