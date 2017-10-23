<%@ page import="com.grailslab.enums.ScheduleStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleExam&ResultLayout"/>
    <title>Manage Mark Entry</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Manage Mark Entry" SHOW_PRINT_BTN="YES"/>
<div class="row" id="create-form-holder">
    <div class="col-sm-12">
        <div class="panel"  id="create-form-print" style="display: none;">
            <header class="panel-heading">
                Mark Entry Status
            </header>
            <div class="panel-body">
                <form class="form-horizontal" role="form">
                    <div class="col-md-12" id="stu-manage-report-print">
                        <div class="form-group">
                            <div class="col-md-3">
                                <g:select class=" form-control " id="printExamName" name='printExamName'
                                          tabindex="1"
                                          noSelection="${['': 'Select Exam...']}"
                                          from='${examNameList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-3">
                                <select class=" form-control " id="printClassName" name="printClassName" tabindex="2">
                                    <option value="">Select Class Name...</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <select class=" form-control " id="printSection" name="printSection" tabindex="2">
                                    <option value="">Select Section...</option>
                                </select>

                            </div>
                            <div class="btn-group btn-margin-left">
                                <a class="btn btn-primary print-btn-new" href="javascript:void(0);">
                                    <i class="fa fa fa-print"></i> View Report
                                </a>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <div class="panel">
            <header class="panel-heading">
                 Mark Entry
            </header>
            <div class="panel-body">
                <form class="form-horizontal" role="form">
                    <div class="col-md-12" id="stu-manage-report-holder">
                        <div class="form-group">
                            <div class="col-md-3">
                                <g:select class=" form-control " id="examName" name='examName'
                                          tabindex="1"
                                          noSelection="${['': 'Select Exam...']}"
                                          from='${examNameList}' value="${shiftExam?.id}"
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-3">
                                <g:select class=" form-control " id="className" name='className'
                                          tabindex="2"
                                          noSelection="${['': 'Select Class...']}"
                                          from='${classNameList}' value="${exam?.className?.id}"
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-3">
                            <g:select class=" form-control " id="section" name='section'
                                      tabindex="2"
                                      noSelection="${['': 'Select Section...']}"
                                      from='${sectionNameList}' value="${exam?.id}"
                                      optionKey="id" optionValue="name"></g:select>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<g:if test="${dataReturn}">
    <div class="row">
        <div class="col-sm-12">
            <section class="panel">
                <div class="panel-body">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover table-bordered" id="list-table">
                            <thead>
                            <tr>
                                <th class="col-md-1">SL</th>
                                <th class="col-md-2">Subject</th>
                                <th class="col-md-4">CT Status</th>
                                <th class="col-md-4">Hall Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${dataReturn}" var="dataSet" status="i">
                                <tr>
                                    <td>${i+1}</td>
                                    <td class="no-padding">${dataSet.subjectName}</td>
                                    <td class="no-padding">
                                        <g:if test="${(exam.ctSchedule==ScheduleStatus.PUBLISHED) && (dataSet.isCtExam==true)}">
                                            <a class="btn btn-sm btn-primary" title="CT Mark Input" href="${g.createLink(controller: 'markEntry', action: 'addCtMark', params: [id:dataSet.id])}">CT Input</a>
                                        </g:if>
                                        <g:if test="${dataSet.showCtComplete}">
                                            <a class="btn btn-sm btn-danger ctmark-delete-btn" referenceId="${dataSet.id}" title="Delete All CT Mark input" href="#">Delete All</a>
                                            <a class="btn btn-sm btn-info mark-ct-complete-btn" referenceId="${dataSet.id}" title="Mark CT Complete" href="#">Mark Complete</a>
                                        </g:if>
                                        <g:else>
                                            <a class="btn btn-sm btn-success" title="${dataSet.ctEntryStatus}" href="#">${dataSet.ctEntryStatus}</a>
                                        </g:else>

                                    <a class="btn btn-sm btn btn-primary ct-print-btn" referenceId="${dataSet.id}" title="Print CT Exam Mark" href="#">
                                        <i class="fa fa-print"></i> Print
                                    </a>


                                </td>
                                    %{--<td class="no-padding">${dataSet.hallMark} </td>--}%
                                    <td class="no-padding">
                                        <g:if test="${exam.hallSchedule==ScheduleStatus.PUBLISHED}">
                                            <a class="btn btn-sm btn-primary" title="Hall Mark Input" href="${g.createLink(controller: 'markEntry', action: 'addHallMark', params: [id:dataSet.id])}">Hall Input</a>
                                            <g:if test="${dataSet.showHallComplete}">
                                                <a class="btn btn-sm btn-danger hallmark-delete-btn" referenceId="${dataSet.id}" title="Delete All Hall Mark input" href="#">Delete All</a>
                                                <a class="btn btn-sm btn-info mark-hall-complete-btn" referenceId="${dataSet.id}" title="Mark Hall Complete" href="#">Mark Complete</a>
                                            </g:if>
                                            <g:else>
                                                <a class="btn btn-sm btn-success" title="${dataSet.hallEntryStatus}" href="#">${dataSet.hallEntryStatus}</a>
                                            </g:else>
                                            <a class="btn btn-sm btn btn-primary hall-print-btn" referenceId="${dataSet.id}" title="Print Hall Exam Mark" href="#">
                                                <i class="fa fa-print"></i> Print
                                            </a>
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
</g:if>


<script>

    var shiftExam, className, section, printSection, examName, printExamName, printClassName, printOptionType,examAsSectionListUrl, printExamAsSectionListUrl, examClassUrl, printExamClassUrl, printParam;
    $('.print-btn').click(function (e) {
        $("#create-form-print").toggle(500);
        e.preventDefault();
    });
    jQuery(function ($) {
        $('#printExamName').on('change', function (e) {
            printExamName =$('#printExamName').val();
            if (printExamName) {
                printExamClassUrl = "${g.createLink(controller: 'remote',action: 'examClassList')}?examName="+printExamName;
                loadExamClass(printExamClassUrl, '#printClassName', "#stu-manage-report-print")
            }

            $('#printClassName').val("").trigger("change");
        });
        $('#printClassName').on('change', function (e) {
            printExamName =$('#printExamName').val();
            printClassName = $('#printClassName').val();
            if(printExamName!="" && printClassName!=""){
                printExamAsSectionListUrl = "${g.createLink(controller: 'remote',action: 'sectionExamList')}?examType=working&examName="+printExamName+"&className="+printClassName;
                loadExamAsClassSectionList(printExamAsSectionListUrl, $('#printSection'),"#stu-manage-report-print");
            }

            $('#printSection').val("").trigger("change");
        });

        $('#examName').on('change', function (e) {
            examName =$('#examName').val();
            if (examName) {
                examClassUrl = "${g.createLink(controller: 'remote',action: 'examClassList')}?examName="+examName;
                loadExamClass(examClassUrl, '#className', "#stu-manage-report-holder")
            }
            $('#className').val("").trigger("change");
        });
        $('#className').on('change', function (e) {
            examName =$('#examName').val();
            className = $('#className').val();
            if(examName!="" && className!=""){
                examAsSectionListUrl = "${g.createLink(controller: 'remote',action: 'sectionExamList')}?examType=working&examName="+examName+"&className="+className;
                loadExamAsClassSectionList(examAsSectionListUrl, $('#section'),"#stu-manage-report-holder");
            }
            $('#section').val("").trigger("change");
        });
        $('#section').on('change', function (e) {
            section =$('#section').val();
            if (section) {
                window.location.href = '${g.createLink(controller: 'examMark',action: 'markEntry')}/' + section;
            }
        });

        $('#list-table').dataTable({
            "iDisplayLength": 25,
            "aoColumns": [
                null,
                { "bSortable": false },
                { "bSortable": false },
                { "bSortable": false }
            ]
        });

        $('#list-table').on('click', 'a.mark-ct-complete-btn', function (e) {
            var confirmDel = confirm("Are you sure? Please confirm all student mark input done first.");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examMark',action: 'markCtComplete')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        hideLoading("#list-table");
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'examMark', action: 'markEntry')}/${exam?.id}";
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
        $('#list-table').on('click', 'a.mark-hall-complete-btn', function (e) {
            var confirmDel = confirm("Are you sure? Please confirm all student mark input done first.");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examMark',action: 'markHallComplete')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'examMark', action: 'markEntry')}/${exam?.id}";
                        } else {
                            showErrorMsg(data.message);
                        }
                        hideLoading("#list-table");
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
        $('#list-table').on('click', 'a.hallmark-delete-btn', function (e) {
            var confirmDel = confirm("Are you sure delete all hall mark input? \n\nYou need to input all hall exam mark again. Action can't be un done.");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examMark',action: 'deleteHallMark')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'examMark', action: 'markEntry')}/${exam?.id}";
                        } else {
                            showErrorMsg(data.message);
                        }
                        hideLoading("#list-table");
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
        $('#list-table').on('click', 'a.ctmark-delete-btn', function (e) {
            var confirmDel = confirm("Are you sure delete all CT mark input? \n\nYou need to input all CT exam mark again. Action can't be un done.");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                showLoading("#list-table");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'examMark',action: 'deleteCtMark')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            showSuccessMsg(data.message);
                            window.location.href = "${createLink(controller: 'examMark', action: 'markEntry')}/${exam?.id}";
                        } else {
                            showErrorMsg(data.message);
                        }
                        hideLoading("#list-table");
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });

        function loadSectionExam(examName, className, sectionCtrl, loadingCtrl){
            showLoading(loadingCtrl);
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'remote',action: 'sectionExamList')}?className="+className+"&examName="+examName,
                success: function (data, textStatus) {
                    hideLoading(loadingCtrl);
                    if (data.isError == false) {
                        var $select = $(sectionCtrl);
                        $select.find("option:gt(0)").remove();
                        $.each(data.sectionNameList,function(key, value)
                        {
                            $select.append('<option value=' + value.id + '>' + value.name + '</option>');
                        });
                    } else {
                        showErrorMsg(data.message);
                    }
                    $('#manageStumarkBtn').removeClass('btn-primary').addClass('btn-default');
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        }

        $('.print-btn-new').click(function (e) {
            e.preventDefault();
            shiftExam = $("#printExamName").val();
            printExamName = $("#printSection").val();
            printOptionType = 'PDF';
            if (printExamName) {
                printParam = "${g.createLink(controller: 'examReport',action: 'markEntryStatus','_blank')}/"+shiftExam+"?exam="+printExamName+"&printOptionType="+printOptionType;
                window.open(printParam);
            } else {
                alert("Please select section to download report");
            }

            return false;
        });

        $('#list-table').on('click', 'a.ct-print-btn', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            printOptionType = 'PDF';
            var sectionParam ="${g.createLink(controller: 'examReport',action: 'examMark','_blank')}?printOptionType="+printOptionType+"&examSchedule="+ referenceId+"&inputType=ctMark";
            window.open(sectionParam);
            return false;
        });
        $('#list-table').on('click', 'a.hall-print-btn', function (e) {
            var control = this;
            var referenceId = $(control).attr('referenceId');
            printOptionType = 'PDF';
            var sectionParam ="${g.createLink(controller: 'examReport',action: 'examMark','_blank')}?printOptionType="+printOptionType+"&examSchedule="+ referenceId+"&inputType=ctMark";
            window.open(sectionParam);
            return false;
        });
    });
</script>
</body>
</html>