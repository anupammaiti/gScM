<%--
  Created by IntelliJ IDEA.
  User: Hasnat
  Date: 1/23/2015
  Time: 3:32 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="open-ltpl"/>
    <title>Class Routine</title>
</head>

<body>
<section class="pageheader-default text-center">
    <div class="semitransparentbg">
        <h1 class="animated fadeInLeftBig notransition">Class Routine</h1>
    </div>
</section>
<div class="wrapsemibox">
    <section class="container animated fadeInDown notransition">
        <div class="row blogindex">
            <!-- MAIN -->
            <div class="col-sm-12">
                <section>
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
        <div class="row blogindex">
            <!-- MAIN -->
            <div class="col-md-12">
                <div id='public_calendar'></div>
            </div>
        </div>
    </section>
</div>
<script>
    $(document).ready(function() {
        %{--calendar starts--}%
        $('#public_calendar').fullCalendar({
            minTime: "07:00:00",
            maxTime: "19:00:00",
            eventSources: [
                {
                    url: "${g.createLink(controller: 'calendar',action: 'routineEvents')}",
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
               alert("lession plan");
            },
            dayClick: function(date, allDay, jsEvent, view) {
                $('#public_calendar').fullCalendar( 'changeView', 'agendaDay' );
                $('#public_calendar').fullCalendar('gotoDate', date);
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
                    $('#public_calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                }
                $('#public_calendar').fullCalendar('unselect');
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
                $('#public_calendar').fullCalendar( 'refetchEvents' );
            }
        });
    });

    function loadSectionNames(){
        var academicYear =$('#academicYear').val();
        var className =$('#className').val();
        if(academicYear && className){
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'home',action: 'classSectionList')}?className="+className+"&academicYear="+academicYear,
                success: function (data, textStatus) {
                    var $select = $('#section');
                    $select.find('option:gt(0)').remove();
                    if (data.isError == false) {
                        $.each(data.sectionList,function(key, value)
                        {
                            $select.append('<option value=' + value.id + '>' + value.name + '</option>');
                        });
                    } else {
                        alert(data.message);
                    }
                    $select.trigger("change");
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        }
    }
</script>
</body>
</html>