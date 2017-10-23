<!DOCTYPE html>
<head>
    <title>Lesson Plan</title>
    <meta name="layout" content="moduleLessonAndFeedbackLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Add Lesson Plan"/>
<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                <div class="form-horizontal">
                    <div class="form-group">
                        <div class="col-md-4">
                            <g:select tabindex="1" class="form-control" id="section"
                                      name='section'
                                      noSelection="${['': 'Select Section']}"
                                      from='${sectionList}'
                                      optionKey="id" optionValue="name"></g:select>
                        </div>
                        <div class="form-group">
                        <div class="col-md-4">
                        <button class="btn btn-info" id="add-lesson-btn">Add Lesson Plan</button>
                         <button class="btn btn-success" id="manage-lesson-btn">Manage Lesson Plan</button>

                        </div>
                        </div>
                    </div>
                </div>

            </header>
        </section>
    </div>
</div>

<script>
    var section;
    jQuery(function ($) {
        $('#section').select2();
        $('#add-lesson-btn').click(function (e) {
            section = $('#section').val();
            if(section != ""){
                window.location.href = "${g.createLink(controller: 'lesson',action: 'create')}/"+section;
            }
            e.preventDefault();
        });
        $('#manage-lesson-btn').click(function (e) {
            section = $('#section').val();
            if(section != ""){
                window.location.href = "${g.createLink(controller: 'lesson',action: 'lessonPlan')}/"+section;
            }
            e.preventDefault();
        });
    });

</script>
</body>
</html>