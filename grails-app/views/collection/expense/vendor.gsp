<%@ page import="com.grailslab.enums.EmpCategoryType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Vendor</title>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Supplier" SHOW_CREATE_BTN="YES" createButtonText="Add New Supplier"/>
<grailslab:fullModal modalLabel="Supplier">
    <grailslab:input required="true" name="name" label="Name"></grailslab:input>
    <grailslab:textArea  name="address" label="Address"></grailslab:textArea>
    <grailslab:textArea  name="description" label="Description"></grailslab:textArea>
</grailslab:fullModal>
<grailslab:dataTable dataSet="${dataReturn}" tableHead="Serial,Name,Description,Status"></grailslab:dataTable>
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
                    url: "${createLink(controller: 'expenseItem', action: 'vendorSave')}",
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
            "sAjaxSource": "${g.createLink(controller: 'expenseItem',action: 'vendorList')}",
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(4)', nRow).html(getActionButtons(nRow, aData));
                return nRow;
            },
            "aoColumns": [
                null,
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

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'expenseItem',action: 'vendorEdit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        $('#hiddenId').val(data.obj.id);
                        $('#name').val(data.obj.name);
                        $('#address').val(data.obj.address);
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

        $('#list-table').on('click', 'a.inactive-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'expenseItem',action: 'vendorInactive')}?id=" + referenceId,
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
                    url: "${g.createLink(controller: 'expenseItem',action: 'vendorDelete')}?id=" + referenceId,
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

    });

    function getActionButtons(nRow, aData) {
        return getActionBtn(nRow, aData);
    }

</script>
</body>
</html>