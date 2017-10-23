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
    <title>Calender</title>
    <style>
        .fc-ltr .fc-basic-view .fc-day-number{text-align:center;color: #1FB5AD;font-weight: bold;font-size: 25px;}
        #public_calendar {
            margin-top: 20px;
        }
    </style>

</head>

<body>
    <section class="pageheader-default text-center">
        <div class="semitransparentbg">
            <h1 class="animated fadeInLeftBig notransition">Academic Calendar</h1>
        </div>
    </section>
    <div class="wrapsemibox">
        <section class="container animated fadeInDown notransition">
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
                maxTime: "24:00:00",
                eventRender: function(event, element) {},
                eventSources: [
                    {
                        url: "${g.createLink(controller: 'calendar',action: 'publicEvents')}",
                        data: function() { // a function that returns an object
                            return {
                                section: $('#section').val(),
                                eventType: $('#eventType').val(),
                                employee: $('#employee').val()
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
                    $('#eventCalendar').fullCalendar('updateEvent', event);
                },
                dayClick: function(date, allDay, jsEvent, view) {
                    $('#eventCalendar').fullCalendar( 'changeView', 'agendaDay' );
                    $('#eventCalendar').fullCalendar('gotoDate', date);
                },
                loading: function(bool) {
                },
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,agendaWeek,agendaDay'
                },
                defaultDate: new Date(),
                defaultView: 'month',
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
                        $('#eventCalendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                    }
                    $('#eventCalendar').fullCalendar('unselect');
                },
                editable: false,
                droppable: false
            });
        });

    </script>
</body>
</html>