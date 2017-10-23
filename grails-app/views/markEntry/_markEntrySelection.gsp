<div class="row" id="markEntrySubjectSelectHolder" style="display: none">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <span class="panel-header-info">Select Subject to Mark Entry</span>
            </header>
            <div class="panel-body">
                <div class="row" id="markEntrySelectionHolder">
                    <div class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="mesExamName" class="col-md-2 control-label">Exam Name</label>
                            <div class="col-md-6">
                                <g:select class=" form-control mesExamNameStep" id="mesExamName" name='mesExamName'
                                          noSelection="${['': 'Select Exam...']}"
                                          from='${examNameList}'
                                          optionKey="id" optionValue="name"></g:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="mesClassName" class="col-md-2 control-label">Class Name</label>
                            <div class="col-md-6">
                                <select name="mesClassName"  id="mesClassName" class="form-control mesClassNameStep">
                                    <option value="">All Class</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="mesSection" class="col-md-2 control-label">Section Name</label>
                            <div class="col-md-6">
                                <select name="mesSection" id="mesSection" class="form-control mesSectionStep">
                                    <option value="">All Section</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="mesExamType" class="col-md-2 control-label">Entry For</label>
                            <div class="col-md-6">
                                <g:select class=" form-control mesExamTypeStep" id="mesExamType" name='mesExamType'
                                          noSelection="${['': 'Select Exam Term...']}"
                                          from='${com.grailslab.enums.ExamType.values()}'
                                          optionKey="key" optionValue="value"></g:select>
                            </div>
                        </div>

                        <div class="form-group" id="mesSubjectHolder" style="display: none;">
                            <label for="subject" class="col-md-2 control-label">Subject</label>
                            <div class="col-md-6">
                                <select class="form-control" id="subject" name='subject'></select>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <button type="button" id="student-mark-entry-btn"
                                        class="btn btn-default">Entry Mark</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>
<script type="application/javascript">
    var section, examTerm, examName, examType, subject, className, examClassUrl,examAsSectionListUrl;
    jQuery(function ($) {
        $('#markEntrySelectionHolder').cascadingDropdown({
            selectBoxes: [
                {
                    selector: '.mesExamNameStep',
                    onChange: function (value) {
                        mesLoadExamClasses();
                    }
                },
                {
                    selector: '.mesClassNameStep',
                    requires: ['.mesExamNameStep'],
                    onChange: function (value) {
                        mesLoadSection();
                    }
                },
                {
                    selector: '.mesSectionStep',
                    requires: ['.mesClassNameStep'],
                    onChange: function (value) {
                        mesLoadSubject();
                    }
                },
                {
                    selector: '.mesExamTypeStep',
                    requires: ['.mesSectionStep'],
                    onChange: function (value) {
                        mesLoadSubject();
                    }
                }
            ]
        });
        $('#mesExamName').on('change', function (e) {
            $('#mesClassName').val("").trigger("change");
        });
        $('#mesClassName').on('change', function (e) {
            $('#mesSection').val("").trigger("change");
        });
        $('#mesSection').on('change', function (e) {
            $('#mesExamType').val("").trigger("change");
        });
        function mesLoadExamClasses(){
            examName =$('#mesExamName').val();
            if (examName) {
                examClassUrl = "${g.createLink(controller: 'remote',action: 'examClassList')}?examName="+examName;
                loadExamClass(examClassUrl, '#mesClassName', "#markEntrySelectionHolder")
            } else {
                $('#mesSubjectHolder').hide(500);
                $('#student-mark-entry-btn').removeClass('btn-primary').addClass('btn-default');
            }

        }
        function mesLoadSection(){
            examName =$('#mesExamName').val();
            className =$('#mesClassName').val();
            if(examName!="" && className!=""){
                examAsSectionListUrl = "${g.createLink(controller: 'remote',action: 'sectionExamList')}?examType=new&examName="+examName+"&className="+className;
                loadExamAsClassSectionList(examAsSectionListUrl, $('#mesSection'),"#markEntrySelectionHolder");
            } else {
                $('#mesSubjectHolder').hide(500);
                $('#student-mark-entry-btn').removeClass('btn-primary').addClass('btn-default');
            }
        }

        function mesLoadSubject() {
            section = $('#mesSection').val();
            examName = $('#mesExamName').val();
            examType = $('#mesExamType').val();
            if (section != "" && examName != "" && examType != "") {
                showLoading("#markEntrySelectionHolder");
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'remote',action: 'examSubjectList')}/"+examName+"?exam="+section +"&examType=" + examType,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            $('#subject').select2("destroy");
                            var $select = $('#subject');
                            $select.find('option').remove();
                            $.each(data.scheduleList, function (key, value) {
                                $select.append('<option value=' + value.id + '>' + value.name + '</option>');
                            });
                            $('#subject').select2().enable(true);
                            $('#mesSubjectHolder').show(500);
                            $('#student-mark-entry-btn').removeClass('btn-default').addClass('btn-primary');
                        } else {
                            $('#mesSubjectHolder').hide(500);
                            $('#student-mark-entry-btn').removeClass('btn-primary').addClass('btn-default');
                            showErrorMsg(data.message);
                        }
                        hideLoading("#markEntrySelectionHolder");
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            } else {
                $('#mesSubjectHolder').hide(500);
                $('#student-mark-entry-btn').removeClass('btn-primary').addClass('btn-default');
            }
        }

        $('#student-mark-entry-btn').click(function (e) {
            section = $('#mesSection').val();
            examTerm = $('#examTerm').val();
            examType = $('#mesExamType').val();
            subject = $('#subject').val();
            if ((section != "") && (examTerm != "") && (examType != "") && (subject != "")) {
                if (examType == "${com.grailslab.enums.ExamType.CLASS_TEST.key}") {
                    window.location.href = '${g.createLink(controller: 'markEntry',action: 'addCtMark')}/' + subject;
                } else {
                    window.location.href = '${g.createLink(controller: 'markEntry',action: 'addHallMark')}/' + subject;
                }
            }
            e.preventDefault();
        });
    });
</script>
