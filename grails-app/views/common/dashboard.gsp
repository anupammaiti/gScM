<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>Home</title>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Dashboard"/>
<div class="row">
    <div class="col-md-12">
        <div class="event-calendar clearfix">
            <div class="col-md-9 calendar-block">
                <div id='user_calendar'></div>
            </div>
            <div class="col-md-3 event-list-block">
                <div class="profile-nav alt">
                    <section class="panel">
                        <div class="user-heading alt clock-row terques-bg">
                            <h4 align="center"><g:formatDate format="EEE, MMM d" date="${new Date()}"/></h4>
                        </div>
                        <ul id="clock">
                            <li id="sec"></li>
                            <li id="hour"></li>
                            <li id="min"></li>
                        </ul>
                    </section>
                </div>

                %{--<ul class="event-list">
                    <li>Class 3 Bangla @ 8:30 <a href="#" class="event-close"><i class="fa fa-times"></i></a></li>
                    <li>Student feedback and progress report preparation @ 9:30 <a href="#" class="event-close"><i class="fa fa-times"></i></a></li>
                    <li>Class 4 Bangla @ 10:15 <a href="#" class="event-close"><i class="fa fa-times"></i></a></li>
                    <li>Meeting with principle @ 12:30 <a href="#" class="event-close"><i class="fa fa-times"></i></a></li>
                </ul>
                <input type="text" class="form-control evnt-input" placeholder="Add your own note">--}%
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function() {
        $('#user_calendar').fullCalendar({
            minTime: "07:00:00",
            maxTime: "19:00:00",
            eventSources: [
                {
                    url: "${g.createLink(controller: 'remote',action: 'routineEvents')}",
                    data: function() { // a function that returns an object
                        return {
                            routineType: "dashBoard"
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
               alert("Lession Plan show in popup modal")
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
                right: 'month,agendaWeek,agendaDay,listYear'
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

    });

</script>
</body>
</html>
