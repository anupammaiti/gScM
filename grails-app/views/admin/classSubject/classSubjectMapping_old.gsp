<%@ page import="com.grailslab.enums.GroupName" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Holiday</title>
    <meta name="layout" content="adminLayout"/>

</head>

<body>
<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'classSubjects',action: 'index')}" firstBreadCrumbText="Class List" breadCrumbTitleText="${className.name} Subjects" SHOW_CREATE_BTN="YES" createButtonText="Add New Subject"/>

<div class="row" >
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Class Subject List
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Serial</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>CT Exam</th>
                            <th>Hall Exam</th>
                            <th>Exam Mark</th>
                            <th>Weight On Result</th>
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
                                <td>${dataSet[6]}<strong> %</strong></td>

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
<div class="modal fade" id="classSubjectModal"  tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" role="form" id="createFormModal">
                <g:hiddenField name="id" id="subjectsId"/>
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">Add Subject</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="form-group">
                            <label class="col-md-3 control-label">Class Name</label>
                            <div class="col-md-6">
                                <label class="control-label">: <strong>${className?.name}</strong></label>
                            </div>
                        </div>
                        <g:if test="${className.groupsAvailable}">
                            <div class="form-group">
                                <label for="groupName" class="col-md-3 control-label">Group</label>
                                <div class="col-md-6" id="groupNameHolder">
                                    <g:select class=" form-control" id="groupName" name='groupName' tabindex="1"
                                              noSelection="${['': 'All Group...']}"
                                              from="${GroupName.values()}"
                                              optionKey="key" optionValue="value">
                                    </g:select>
                                </div>
                            </div>
                        </g:if>
                        <div class="form-group">
                            <label for="subject" class="col-md-3 control-label">Subject</label>
                            <div class="col-md-6" id="subjectDDHolder">
                                <g:select class=" form-control" id="subject" name='subject' tabindex="1"
                                          noSelection="${['': 'Select Subject...']}"
                                          from="${subjectList}"
                                          optionKey="id" optionValue="name" required="required">
                                </g:select>
                            </div>
                        </div>
                        %{--<div class="form-group">
                            <label for="weightOnResult" class="col-md-3 control-label">Full Mark</label>
                            <div class="col-md-2">
                                <input type="number" value="100" min="0" max="100" step="1" name="fullMark" id="fullMark" class="form-control" placeholder="" required>
                            </div>
                        </div>--}%
                        <div class="form-group" id="tabu-mark-holder">
                            <label for="weightOnResult" class="col-md-3 control-label">Tabulation Mark</label>
                            <div class="col-md-2">
                                <input type="number" value="100" min="0" max="100" step="1" name="weightOnResult" id="weightOnResult" class="form-control" placeholder="" required>
                            </div>
                            <label class="col-md-6 control-label" style="text-align:left;padding-left:1px;"> % of Total Obtained Mark</label>
                        </div>
                        <div class="form-group">
                            %{--<label class="col-md-3 control-label"></label>--}%
                            <div class="col-md-11 col-md-offset-1">
                                <label class="checkbox-inline">
                                    <input type="checkbox" id="isExtracurricular" name="isExtracurricular"> Extra Curricular?
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" id="isCtExam" name="isCtExam" checked> CT Schedule?
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" id="isHallExam" name="isHallExam" checked> Hall Schedule?
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" id="isOtherActivity" name="isOtherActivity"> Other Activities?
                                </label>
                            </div>
                        </div>
                        <div class="form-group" id="ct-mark-holder">
                            <div class="row">
                                <div class="col-md-6">
                                    <label for="ctMark" class="col-md-6 control-label">CT Exam Mark</label>
                                    <div class="col-md-6">
                                        <input type="number" value="20" min="0" max="100" step="1" name="ctMark" id="ctMark" class="form-control">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="ctMark" class="col-md-7 control-label">Effective Mark (%)</label>
                                    <div class="col-md-5">
                                        <input type="number" value="100" min="0" max="100" step="1" name="ctEffMark" id="ctEffMark" class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group" id="hall-mark-holder">
                            <div class="row">
                                <div class="col-md-6">
                                    <label for="hallMark" class="col-md-6 control-label" id="hall-duration-label">Hall Mark</label>
                                    <div class="col-md-6">
                                        <input type="number" value="80" min="0" max="100" step="1" name="hallMark" id="hallMark" class="form-control">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="ctMark" class="col-md-7 control-label">Effective Mark (%)</label>
                                    <div class="col-md-5">
                                        <input type="number" value="100" min="0" max="100" step="1" name="hallEffMark" id="hallEffMark" class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn  btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="submitButton">Save changes</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    var allowOptionalSubject, subjectId, fullMark,ctEffMark,hallEffMark;
    jQuery(function ($) {


        $('#list-table').dataTable({
            "bAutoWidth": true,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0,'asc'],
            "deferLoading": ${totalCount?:0},
            "fnServerParams": function ( aoData ) {
                aoData.push( { "name": "classId", "value": ${className?.id} } );
            },
            "sAjaxSource": "${g.createLink(controller: 'classSubjects',action: 'list')}",
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
                null,
                null,
                null,
                null,
                null,
                { "bSortable": false }
            ]
        });
        $('#isOtherActivity').click(function(){
            if (this.checked) {
                $('#ctMark').val('0');
                $('#ctEffMark').val('0');
                $('#hallMark').val('0');
                $('#hallEffMark').val('0');
                $('#weightOnResult').val('0');
                $( "#isCtExam" ).prop( "checked", false);
                $( "#isHallExam" ).prop( "checked", false);
                $( "#isExtracurricular" ).prop( "checked", false);
                $('#ct-mark-holder').hide();
                $('#hall-mark-holder').hide();
                $('#tabu-mark-holder').hide();
            } else {
                $("#isHallExam").trigger("click");
            }
        });
        $('#isExtracurricular').click(function(){
            if (this.checked) {
                $('#ctMark').val('0');
                $('#ctEffMark').val('0');
                $('#hallMark').val('100');
                $('#hallEffMark').val('100');
                $('#weightOnResult').val('10');
                $( "#isCtExam" ).prop( "checked", false);
                $( "#isHallExam" ).prop( "checked", false);
                $( "#isOtherActivity" ).prop( "checked", false);
                $('#ct-mark-holder').hide();
                $('#hall-mark-holder').show();
            } else {
                $("#isHallExam").trigger("click");
            }
        });
        $('#isCtExam').click(function(){
            if (this.checked) {
                $( "#isExtracurricular" ).prop( "checked", false);
                $( "#isOtherActivity" ).prop( "checked", false);
                $( "#isHallExam" ).prop( "checked", true);
                $('#ctMark').val('20');
                $('#ctEffMark').val('100');
                $('#hallMark').val('80');
                $('#hallEffMark').val('100');
                $('#weightOnResult').val('100');
                $('#ct-mark-holder').show();
                $('#hall-mark-holder').show();
                $('#tabu-mark-holder').show();
//                $("#isHallExam").trigger("click");
            } else {
                $('#ctMark').val('0');
                $('#ctEffMark').val('0');
                $('#hallMark').val('100');
                $('#hallEffMark').val('100');
                $('#weightOnResult').val('10');
                $('#ct-mark-holder').hide();
                $('#hall-mark-holder').show();
                $('#tabu-mark-holder').show();
            }
        });
        $('#isHallExam').click(function(){
            if (this.checked) {
                $( "#isExtracurricular" ).prop( "checked", false);
                $( "#isOtherActivity" ).prop( "checked", false);
                $( "#isCtExam").trigger("click");
                $('#hallMark').val('80');
                $('#hallEffMark').val('100');
                $('#weightOnResult').val('100');
                $('#hall-mark-holder').show();
                $('#tabu-mark-holder').show();
            } else {
                $( "#isExtracurricular").trigger("click");
            }
        });
        $("#createFormModal").submit(function() {
            /*fullMark = parseInt($('#fullMark').val());
             ctEffMark = parseInt($('#ctEffMark').val());
             hallEffMark = parseInt($('#hallEffMark').val());
             totalExamMark =ctEffMark+hallEffMark;
             if(fullMark != totalExamMark){
             showErrorMsg("Exam Mark distribution is not correct. Please check again");
             return false;
             }*/
            jQuery.ajax({
                type: 'POST',
                dataType:'JSON',
                data: $("#createFormModal").serialize(),
                url: "${g.createLink(controller: 'classSubjects', action: 'saveSubject')}?className="+"${className?.id}",
                success: function (data) {
                    if(data.isError==true){
                        showErrorMsg(data.message);
                    }else {
                        subjectId =$('#subjectsId').val();
                        if(subjectId != undefined && subjectId !=''){
                            $('#classSubjectModal').modal('hide');
                        }
                        $("#subject option:selected").remove();
                        showSuccessMsg(data.message);
                        var table = $('#list-table').DataTable().ajax.reload();
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            return false; // avoid to execute the actual submit of the form.
        });



        $('.create-new-btn').click(function (e) {
            $('#classSubjectModal').modal('show');
            e.preventDefault();
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'classSubjects',action: 'editSubject')}?id=" + referenceId,
                success: function (data, textStatus) {
                    if (data.isError == false) {
                        clearForm('#createFormModal');
                        $('#subjectsId').val(data.obj.id);
                        $('#className').val(data.obj.className.id);
                        var select = $('#subject');
                        $('option', select).remove();
                        select.append('<option value="' + data.obj.subject.id + '" selected="selected">' + data.subjectName + '</option>');
//                        $('#fullMark').val(data.obj.fullMark);
                        $('#ctMark').val(data.obj.ctMark);
                        $('#ctEffMark').val(data.obj.ctEffMark);
                        $('#hallMark').val(data.obj.hallMark);
                        $('#hallEffMark').val(data.obj.hallEffMark);
                        $('#weightOnResult').val(data.obj.weightOnResult);
                        $( "#isExtracurricular" ).prop( "checked", data.obj.isExtracurricular );
                        $( "#isOtherActivity" ).prop( "checked", data.obj.isOtherActivity );
                        $( "#isCtExam" ).prop( "checked", data.obj.isCtExam );
                        $( "#isHallExam" ).prop( "checked", data.obj.isHallExam );
                        if(data.obj.isExtracurricular ===true){
                            $('#ct-mark-holder').hide();
                            $('#hall-mark-holder').show();
                        } else if(data.obj.isOtherActivity ===true){
                            $('#ct-mark-holder').hide();
                            $('#hall-mark-holder').hide();
                            $('#tabu-mark-holder').hide();
                        }else {
                            if(data.obj.isCtExam ===false){
                                $('#ct-mark-holder').hide();
                                $('#hall-mark-holder').show();
                            }
                        }
                        if(data.obj.groupName !=''){
                            $('#groupName').val(data.obj.groupName?data.obj.groupName.name:'');
                        }

                        $('#classSubjectModal').modal('show');
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
                    url: "${g.createLink(controller: 'classSubjects',action: 'inactive')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${g.createLink(controller: 'classSubjects',action: 'subjects', params: [id:className?.id])}";
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
        $('#classSubjectModal').on('hide.bs.modal', function (e) {
            window.location.href = "${g.createLink(controller: 'classSubjects',action: 'subjects', params: [id:className?.id])}";
        });
    });
    function getActionButtons(nRow, aData) {
        var actionButtons = "";
        actionButtons += '<span class="col-md-6 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="edit-reference" title="Edit">';
        actionButtons += '<span class="green glyphicon glyphicon-edit">';
        actionButtons += '</a></span>';
        actionButtons += '<span class="col-md-6 no-padding"><a href="" referenceId="' + aData.DT_RowId + '" class="delete-reference" title="Delete">';
        actionButtons += '<span class="red glyphicon glyphicon-trash"></span>';
        actionButtons += '</a></span>';
        return actionButtons;
    }

</script>
</body>
</html>


