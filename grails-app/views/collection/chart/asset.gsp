<%@ page import="com.grailslab.enums.NodeType; com.grailslab.enums.AccountType; com.grailslab.enums.FeeAppliedType; com.grailslab.enums.PrintOptionType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>Asset</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Assets" SHOW_PRINT_BTN="YES"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table tree table-bordered table-striped table-condensed" id="list-table">
                        <thead>
                        <tr>
                            <th class="col-md-10">Asset
                            </th><th class="col-md-2">Action <a class="addAssets" href="#"><i class="fa fa-plus"></i>
                        </a></th>
                        </tr>
                        </thead>
                        <tbody>

                        <g:each in="${dataReturn}" var="dataSet">
                            <tr class="${dataSet.treeClassName}">
                                <td>${dataSet.name}</td>
                                <td>
                                    <g:if test="${dataSet.allowChild}">
                                        <span class="col-md-4 no-padding"><a href="#" refid="${dataSet.id}"
                                                                             headertext="${dataSet.name}"
                                                                             class="add-reference" title="Add"><span
                                                    class="fa fa-plus"></span></a></span>
                                    </g:if>

                                    <g:if test="${dataSet.allowEdit}">
                                        <span class="col-md-4 no-padding"><a href="#" refid="${dataSet.id}"
                                                                             headertext="${dataSet.name}"
                                                                             class="edit-reference" title="Edit"><span
                                                    class="fa fa-edit"></span></a></span>
                                    </g:if>
                                    <span class="col-md-4 no-padding"><a href="#" refid="${dataSet.id}"
                                                                         headertext="${dataSet.name}"
                                                                         refhaschield="${dataSet.hasChild}"
                                                                         class="delete-reference" title="Delete"><span
                                                class="fa fa-trash-o"></span></a></span>
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
<div class="modal fade" id="assetAccountHeadModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="assetHeadModalLabel">Add Asset</h4>
            </div>

            <div class="action-asset-head-confirm asset-head-content">
                <form class="form-horizontal" role="form" id="create-form">
                    <g:hiddenField name="id" id="accountId"/>
                    <g:hiddenField name="parentId" id="parentId"/>
                    <g:hiddenField name="accountType" id="accountType" value="${AccountType.ASSET.key}"/>
                    <div class="modal-body">
                        <div class="row">
                            <div class="row">
                                <div class="form-group">
                                    <label class="col-md-3  control-label">Name<span class="required">*</span></label>

                                    <div class="col-md-7">
                                        <g:textField class="form-control" id="name" name="name"
                                                     placeholder="Insert Name" required="required"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-3  control-label">Code</label>

                                    <div class="col-md-7">
                                        <input type="text" name="code"
                                               id="code" required="required" placeholder="Code between 100000 to 199999"
                                               class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer modal-footer-action-btns">
                        <button type="button" class="btn btn-default" data-dismiss="modal"
                                aria-hidden="true">Cancel</button>
                        <button type="submit" id="asset-account-head-yes-btn"
                                class="btn btn-large btn-primary">Submit</button>
                    </div>

                    <div class="modal-footer modal-refresh-processing" style="display: none;">
                        <i class="fa fa-refresh fa-spin text-center"></i>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="printBtnModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="printModalLabel">Print Account Heads</h4>
            </div>
            <form class="form-horizontal" role="form" id="printFormModal">
                <div class="modal-body">
                    <div class="row">
                        <div class="row">
                            <div class="form-group">
                                <label class="col-md-3  control-label">Print Type<span class="required">*</span></label>
                                <div class="col-md-7">
                                    <g:select class="form-control" id="printOptionType" name='printOptionType'
                                              from='${PrintOptionType.values()}'
                                              optionKey="key" optionValue="value" required="required"></g:select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer modal-footer-action-btns">
                    <button type="button" class="btn btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    <button type="submit" id="print-schedule-yes-btn" class="btn btn-large btn-primary">Submit</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    jQuery(function ($) {

        $('.tree').treegrid({
            expanderExpandedClass: 'glyphicon glyphicon-minus',
            expanderCollapsedClass: 'glyphicon glyphicon-plus',
            initialState: 'expanded'
        });
        var oTable1 = $('#list-table').dataTable({
            "iDisplayLength": 25,
            "aoColumns": [
                { "bSortable": false },
                { "bSortable": false }
            ]
        });

        $('.addAssets').click(function (e) {
            $("#assetHeadModalLabel").html("100000 - Asset");
            $('#assetAccountHeadModal').modal('show');
            e.preventDefault();
        });

        $('.extra-btn-1').click(function (e) {
            $('#assetAccountHeadModal').modal('show');
            e.preventDefault();
        });

        var validator = $('#create-form').validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                name: {
                    required: true,
                    maxlength: 200
                },
                code: {
                    required: true,
                    number: true,
                    minlength: 6,
                    min: 100000,
                    max: 199999
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
                    url: "${createLink(controller: 'chart', action: 'saveAccount')}",
                    type: 'post',
                    dataType: "json",
                    data: $("#create-form").serialize(),
                    success: function (data) {
                        if (data.isError == false) {
                            validator.resetForm();
                            $("#accountId").val('');
                            $("#parentId").val('');
                            $(".form-group").removeClass("has-error");
                            $('#assetAccountHeadModal').modal('hide');
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'chart',action: 'asset')}";
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    failure: function (data) {
                    }
                })
            }
        });


        $('#list-table').on('click', 'a.add-reference', function (e) {
            var control = this;
            var refid = $(control).attr('refid');
            var headertext = $(control).attr('headertext');
            validator.resetForm();
            $("#accountId").val('');
            $("#parentId").val('');
            $("#parentId").val(refid);
            $(".form-group").removeClass("has-error");
            $('#assetAccountHeadModal').modal('show');
            $("#assetHeadModalLabel").html(headertext);
            $("#name").focus();
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('refid');

            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'chart',action: 'edit')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        validator.resetForm();
                        $("#accountId").val('');
                        $("#parentId").val('');
                        $('#accountId').val(data.obj.id);
                        $('#name').val(data.obj.name);
                        $('#code').val(data.obj.code);
                        $("#assetHeadModalLabel").html("Edit Asset header");
                        $('#assetAccountHeadModal').modal('show');
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
            var control = this;
            var referenceId = $(control).attr('refid');
            var refhaschield = $(control).attr('refhaschield');
            if (refhaschield == 'false') {
                var confirmDel = confirm("Are you sure?");
                if (confirmDel == true) {
                    var control = this;
                    jQuery.ajax({
                        type: 'POST',
                        dataType: 'JSON',
                        data: {id: referenceId},
                        url: "${g.createLink(controller: 'chart',action: 'delete')}",
                        success: function (data, textStatus) {
                            if (data.isError == false) {
                                showSuccessMsg(data.message);
                                window.location.href = "${g.createLink(controller: 'chart',action: 'asset')}";
                            } else {
                                showErrorMsg(data.message);
                            }
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                        }
                    });
                }
            } else {
                alert('Delete Child First.')
            }
            e.preventDefault();
        });
        $('.print-btn').click(function (e) {
            $('#printBtnModal').modal('show');
            e.preventDefault();
        });
        $("#printFormModal").submit(function(e) {
            e.preventDefault();
            var printOptionType =$('form#printFormModal #printOptionType').find("option:selected").val();
            $('#printBtnModal').modal('hide');
            var sectionParam ="${g.createLink(controller: 'accountsReport',action: 'chart','_blank')}?accountType="+"${AccountType.ASSET.key}"+"&printOptionType="+printOptionType;
            window.open(sectionParam);
            return false; // avoid to execute the actual submit of the form.
        });
    });

</script>
</body>
</html>
