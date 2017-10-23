<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Class Routine</title>
    <meta name="layout" content="moduleStdMgmtLayout"/>
</head>

<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Class Routine" SHOW_CREATE_LINK="YES" createLinkText="Add New" createLinkUrl="${g.createLink(controller: 'classRoutine', action: 'classRoutineCreate')}"/>
<div class="row" >
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Class Routine
            </header>
            <div class="panel-body">
                <div class="row" id="class-routine-holder">
                    <form class="form-horizontal" role="form" id="create-form">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label for="academicYear" class="col-md-1 control-label">Year</label>
                                <div class="col-md-3">
                                    <g:select tabindex="1" class="form-control academic-year-step"
                                              id="academicYear" name='academicYear'
                                              noSelection="${['': 'Select Year...']}"
                                              from='${com.grailslab.gschoolcore.AcademicYear.schoolYears()}'
                                              optionKey="key" optionValue="value"></g:select>
                                </div>
                                <label for="className" class="col-md-1 control-label">Class</label>
                                <div class="col-md-3">
                                    <g:select class=" form-control class-name-step" id="className" name='className'
                                              tabindex="2"
                                              noSelection="${['': 'Select Class...']}"
                                              from='${classNameList}'
                                              optionKey="id" optionValue="name"></g:select>
                                </div>
                                <label for="section" class="col-md-1 control-label">Section</label>
                                <div class="col-md-3">
                                    <select class="form-control section-name-step" id="section" name='section'>
                                        <option  value="">Select Section</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </div>
</div>


<div class="row">
    <div class="col-md-12">
        <div class="event-calendar clearfix">
            <div class="col-md-12 calendar-block">
                <div id='user_calendar'></div>
            </div>
        </div>
    </div>
</div>

<script>
    var academicYear, className, subject, remoteUrl, section;
    jQuery(function ($) {
        $('#user_calendar').fullCalendar({
            minTime: "07:00:00",
            maxTime: "19:00:00",
            eventSources: [
                {
                    url: "${g.createLink(controller: 'remote',action: 'routineEvents')}",
                    data: function() { // a function that returns an object
                        return {
                            routineType: "classRoutine",
                            sectionId: $('#section').val()
                        };
                    },
                    error: function() {}
                }
            ],
            eventRender: function(event, element) {
                element.attr('title', event.tooltip);
                var description = event.tooltip;
                if(description != undefined){
                    if(element.className != 'syllabus-class'){
                        element.find('.fc-event-title').append("<br/><span class='tip'>" + description + "</span>");
                    }
                }
            },
            eventClick: function(event, element) {
                $('#taskAndEventModal').modal('show');
                if ( !$(this).hasClass("syllabus-personal") ) {
                    if ( !$(this).hasClass("syllabus-class") ) {
                        $('#myModal2 #clicktitle').html(event.title);
                        $('#myModal2 #clickstart').html(event.tooltip);
                        $('#myModal2').modal('show');
                    }
                }
                if (event.editable  && event.eventId!=null) {

                    jQuery.ajax({
                        type: 'POST',
                        dataType:'JSON',
                        url: "${g.createLink(controller: 'calendar',action: 'getTaskOrEvent')}?id=" + event.eventId,
                        success: function (data, textStatus) {
                            if(data.isError==false){
                                $('div#taskAndEventModal #id').val(data.objId);
                                $('div#taskAndEventModal #name').val(data.name);
                                $('div#taskAndEventModal #startDate').val(data.startDate);
                                $('div#taskAndEventModal #startTime').val(data.startTime);
                                if(data.obj!=undefined && data.obj!=""){
                                    $('div#taskAndEventModal #endDate').val(data.endDate);
                                    $('div#taskAndEventModal #endTime').val(data.endTime);
                                    $('div#taskAndEventModal #shortNote').val(data.obj.shortNote);
                                    if(data.obj.repeatType.name =='WEEKLY'){
                                        var repeatDates =data.obj.repeatDates;
                                        var thisVal;
                                        $("#weekDiv").find('input[type=checkbox]').each(function () {
                                            thisVal = this.value;
                                            if ( repeatDates.indexOf(thisVal) !== -1 ){
                                                $(this).closest('.daypick').addClass('active');
                                                $(this).attr("checked", true);
                                            }
                                        });
                                        $(".r2").trigger( "click" );
                                    }
                                    $('#collapseTwo').addClass('in');
                                }

                                %{--$('#accordion').collapse('show')--}%
                            }else{
                                showErrorMsg(data.message);
                            }
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                        }
                    });

                    return false;
                }
                $('#user_calendar').fullCalendar('updateEvent', event);
            },
            dayClick: function(date, allDay, jsEvent, view) {
                $('#user_calendar').fullCalendar( 'changeView', 'agendaDay' );
                $('#user_calendar').fullCalendar('gotoDate', date);
            },
            loading: function(bool) {
            },
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'agendaWeek,agendaDay,listWeek'
            },
            defaultDate: new Date(),
            defaultView: 'agendaWeek',
            eventLimit: true, // allow "more" link when too many events
            selectable: true,
            selectHelper: true,
            select: function(start, end) {
                var title ='';    //prompt('Event Title:');
                var eventData;
                if (title) {
                    eventData = {
                        title: title,
                        start: start,
                        end: end
                    };
                    $('#user_calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                }
                $('#user_calendar').fullCalendar('unselect');
            },
            editable: false,
            droppable: false
        });


        $('#class-routine-holder').cascadingDropdown({
            selectBoxes: [
                {
                    selector: '.academic-year-step',
                    onChange: function(value){
                        loadSectionNames();
                    }
                },
                {
                    selector: '.class-name-step',
                    requires: ['.academic-year-step'],
                    onChange: function (value) {
                        loadSectionNames();
                    }
                },
                {
                    selector: '.section-name-step',
                    requires: ['.class-name-step']
                }
            ]
        });


        $('#section').change(function () {
            var sectionId = $('#section').val();
            if (sectionId) {
                $('#user_calendar').fullCalendar( 'refetchEvents' );
            }
        });
    });

    function loadSectionNames(){
        academicYear =$('#academicYear').val();
        className =$('#className').val();
        if(academicYear && className){
            remoteUrl = "${g.createLink(controller: 'remote',action: 'classSectionList')}?className="+className+"&academicYear="+academicYear;
            loadClassSection(remoteUrl, $('#section'),"#class-routine-holder");
        }
    }
</script>
</body>
</html>


