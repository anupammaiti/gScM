<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="moduleLessonAndFeedbackLayout"/>
    <title>Add Feedback</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Manage Feedback" SHOW_CREATE_BTN="YES" createButtonText="Add another Feed back"/>

<g:if test="${feedBackList}">
    <div class="row" id="create-form-holder" style="display: none;">
</g:if>
<g:else>
    <div class="row" id="create-form-holder">
</g:else>

        <div class="col-sm-12">
            <div class="panel" id="selectionFilterHolder">
                <header class="panel-heading">
                    Feedback
                </header>
                <div class="panel-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <div class="col-md-2">
                                <g:select class=" form-control mesExamTypeStep" id="className" name='className'
                                          noSelection="${['': 'Select Class...']}" value="${fclassName?.id}"
                                          from='${classNameList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-2">
                                <g:select class="form-control class-name-step" id="classSection" name='classSection'
                                          noSelection="${['': 'All Section']}" value="${fsectionName?.id}"
                                          from='${sectionList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>

                            <div class="col-md-2">
                                <g:select class="form-control subject-name-step" id="subject" name='subject'
                                          noSelection="${['': 'All Subjects']}" value="${fSubjectId?.id}"
                                          from='${subjectNameList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-2">
                                <g:select class="form-control" id="weekNo" name='weekNo'
                                          noSelection="${['': 'All Weeks']}" value="${fWeekNo?.weekNumber}"
                                          from='${lessonWeekList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                            <div class="col-md-2">
                            <div class="btn-group btn-margin-left">
                                <a class="btn btn-primary next-btn" href="javascript:void(0);">
                                     Next<span class="glyphicon glyphicon-chevron-right"></span>
                                </a>
                            </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

<g:if test="${feedBackList}">
    <div class="row" id="feedback-list-holder">
        <div class="col-sm-12">
            <div class="panel">
                <header class="panel-heading">
                    <span class="panel-header-title">Class:</span><span class="panel-header-info">${fclassName?.name}</span>
                    <span class="panel-header-title">Section:</span><span class="panel-header-info">${fsectionName?.name}</span>
                    <span class="panel-header-title">Subject:</span><span class="panel-header-info">${fSubjectId?.name}</span>
                    <span class="panel-header-title">Week No:</span><span class="panel-header-info">${fWeekNo?.weekNumber}</span>
                    <span class="panel-header-title">From Date:</span><span class="panel-header-info"><g:formatDate format="dd/MM/yyyy" date="${fWeekNo?.startDate}"/></span>
                    <span class="panel-header-title">To:</span><span class="panel-header-info weekEnds"><g:formatDate format="dd/MM/yyyy" date="${fWeekNo?.endDate}"/></span>
                </header>
                <div class="panel-body">
                    <div class="row">
                        <g:if test="${lessonList}">
                            <div class="col-md-12">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped table-hover dataTable no-footer" id="list-table">
                                        <thead>
                                        <tr>
                                            <th class="col-md-2">Lesson Date</th>
                                            <th class="col-md-6">Topics Discussed</th>
                                            <th class="col-md-2">Home Work</th>
                                            <th class="col-md-2">Exam</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <g:each in="${lessonList}" var="lesson" status="i">
                                            <g:each in="${lesson.lessonDetails}" var="dataSetDetails" status="j">
                                                <g:if test="${dataSetDetails?.subject==fSubjectId}">
                                                    <tr class="even">
                                                        <td >${lesson?.lessonDate?.format('E, dd MMM yyyy')}</td>
                                                        <td>${raw(dataSetDetails.topics)}</td>
                                                        <td>${raw(dataSetDetails.homeWork)}</td>
                                                        <td>${dataSetDetails?.examDate?.format('dd-MMM-yy')}</td>
                                                    </tr>
                                                </g:if>
                                            </g:each>
                                        </g:each>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </g:if>
                        <div class="col-md-12">
                            <section class="panel">
                                <form class="cmxform form-horizontal" action="saveFeedback" method="post">
                                        <g:hiddenField name="fclassName" value="${fclassName.id}"/>
                                       <g:hiddenField name="fSectionId" value="${fsectionName.id}"/>
                                       <g:hiddenField name="fSubjectId" value="${fSubjectId.id}"/>
                                      <g:hiddenField name="fWeekNo" value="${fWeekNo?.id}"/>
                                    <div class="panel-body">
                                <div class="form-group">
                                    <label for="defaultRating" class="col-md-4 control-label" style="text-align: left">Add Rating Based on Weekly Lesson Plan: </label>
                                    <div class="col-md-5">
                                        <input  class="form-control" type="number"  name="defaultRating" id="defaultRating" placeholder="Add default feedback rating for all students"
                                                tabindex="2"/>
                                    </div>
                                    <div class="col-md-2" style="text-align: right">
                                        <div class="btn-group btn-margin-left">
                                            <a class="btn btn-info" id="programmatically-set" href="javascript:void(0);">Add for all</a>
                                        </div>
                                    </div>
                                </div>
                                        <div class="table-responsive">
                                            <table class="table table-striped table-hover table-bordered" id="list-table">
                                                <thead>
                                                <tr>
                                                    <th class="col-md-1">SL</th>
                                                    <th class="col-md-1">STD ID</th>
                                                    <th class="col-md-2">Name</th>
                                                    <th class="col-md-1">Roll No</th>
                                                    <th class="col-md-3">Feedback</th>
                                                    <th class="col-md-4">Comment</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <g:each in="${feedBackList}" var="dataSet" status="i">
                                                    <tr>
                                                        <td>${i+1}</td>
                                                        <td class="no-padding">${dataSet.stuId}</td>
                                                        <td class="no-padding">${dataSet.stuName}</td>
                                                        <td class="no-padding">${dataSet.stuRoll}</td>
                                                        <td class="no-padding">
                                                            <g:hiddenField name="studentIds" value="${dataSet.id}"/>
                                                            <input type="hidden" name="feedback${dataSet.id}"  class="rating-tooltip-manual defaultFeedback" data-filled="fa fa-star fa-1x" data-empty="fa fa-star-o fa-1x" data-fractions="1"
                                                                   data-stop="10" data-step="1" value="${dataSet.rating?:5}"/>
                                                            <span class="label label-default" style="background-color: darkseagreen">${dataSet.rating?:5}</span>
                                                        </td>
                                                        <td class="no-padding">
                                                            <textarea class="form-control" name="comment${dataSet.id}" rows="1">${dataSet.comment}</textarea>
                                                        </td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                        <div class="row">
                                            <div class="form-group">
                                                <div class="col-md-offset-8 col-md-4">
                                                    <button class="btn btn-primary" tabindex="8" type="submit">Submit</button>
                                                    <button class="btn btn-default cancel-btn" tabindex="9"
                                                            type="reset">Cancel</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </section>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</g:if>

<script>
    $(function ($) {
        $('.rating-tooltip-manual').rating({
            extendSymbol: function () {
                var title;
                $(this).tooltip({
                    container: 'body',
                    placement: 'top',
                    trigger: 'manual',
                    title: function () {
                        return title;
                    }
                });
                $(this).on('rating.rateenter', function (e, rate) {
                    title = 'Fair';
                    if(rate >= 0 && rate <= 2) {
                        title = 'Bad';
                    } else if(rate > 2 && rate <= 4) {
                        title = 'Not satisfactory';
                    } else if(rate > 6 && rate <= 8) {
                        title = 'Very good';
                    } else if(rate > 8) {
                        title = 'Excellent';
                    }
                    $(this).tooltip('show');
                })
                        .on('rating.rateleave', function () {
                            $(this).tooltip('hide');
                        });
            }
        });

        $('.rating-tooltip-manual').on('change', function () {
            $(this).next('.label').html($(this).val());
        });
        $('#programmatically-set').click(function () {
            $('.defaultFeedback').rating('rate', $('#defaultRating').val());
            $('.label-default').html($('#defaultRating').val());
        });
        var  className,subject, weekNo, classSection, examName, className,sectionListUrl,  examClassUrl, classUrl,sectionWeekListUrl;
        $('#className').on('change', function (e) {
            className =$('#className').val();
            if (className) {
                classUrl = "${g.createLink(controller: 'remote',action: 'listSection')}?className="+className;
                loadClassSection(classUrl, '#classSection', "#stu-manage-report-holder")
            }
            $('#classSection').val("").trigger("change");
        });

        $('#classSection').on('change', function (e) {
            var classSection = $('#classSection').val();
            if(className!="" && classSection!=""){
                sectionListUrl = "${g.createLink(controller: 'remote',action: 'sectionSubjectList')}?id="+classSection+"&className="+className;
                sectionWeekListUrl = "${g.createLink(controller: 'remote',action: 'lessonWeekList')}?id="+classSection;
                loadSectionSubject(sectionListUrl,className,classSection, $('#subject'), "#stu-manage-report-holder");
                loadSubjectWeek(sectionWeekListUrl, $('#weekNo'), "#stu-manage-report-holder");
            }
            $('#subject').val("").trigger("change");
            $('#weekNo').val("").trigger("change");
        });

        $('.next-btn').click(function (e) {
            var reportParam
            className =$('#className').val();
            classSection = $('#classSection').val();
            subject = $('#subject').val();
            weekNo = $('#weekNo').val();
            if (classSection && subject && weekNo) {
                reportParam = '${g.createLink(controller: 'feedback',action: 'addFeedback','_blank')}?className='+className+"&section="+classSection+"&subject="+subject+"&weekNo="+weekNo;
            } else {
                alert("Please select Section, Subject and weekNo to add feedback")
            }
            window.open(reportParam);
        });
        $('.create-new-btn').click(function (e) {
            $("#create-form-holder").toggle(500);
        });
    });
</script>

</body>
</html>