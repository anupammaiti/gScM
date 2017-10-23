%{--Left Menu for Admin login start--}%
<sec:access expression="hasRole('ROLE_ADMIN') || hasRole('ROLE_SUPER_ADMIN')">
    <!-- sidebar menu start-->
    <div class="leftside-navigation">
        <ul class="sidebar-menu" id="nav-accordion">
            <li>
                <a href="${g.createLink(controller: 'admin')}"
                   class="${params.controller == 'admin' && params.action == 'index' ? 'active' : ''}">
                    <i class="fa fa-dashboard"></i>
                    <span>Dashboard</span>
                </a>
            </li>
            <li>
                <a href="${g.createLink(controller: 'user')}"
                   class="${params.controller == 'user' && params.action == 'index' ? 'active' : ''}">
                    <i class="fa fa-users"></i>
                    <span>Manage User</span>
                </a>
            </li>
            <li class="sub-menu">
                <a href="javascript:;">
                    <i class="fa fa-book"></i>
                    <span>Academic</span>
                </a>
                <ul class="sub">
                    <li class="${params.controller == 'noticeBoard' && params.action == 'index' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'noticeBoard', action: 'index')}">Notice Board</a>
                    </li>
                    <li class="${params.controller == 'lessonWeek' && params.action == 'week' ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'lessonWeek', action: 'week')}">Lesson Week</a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
    <!-- sidebar menu end-->
</sec:access>
%{--Left Menu for Admin login end--}%

%{--##################################################################  TEACHER LOGIN START    ############################################--}%
%{--Left Menu for TEACHER login START--}%
<sec:access expression="hasRole('ROLE_TEACHER')">
    <!-- sidebar menu start-->
    <div class="leftside-navigation">
        <ul class="sidebar-menu" id="nav-accordion">
            <li>
                <a href="${g.createLink(controller: 'teacher')}"
                   class="${params.controller == 'teacher' && params.action == 'index' ? 'active' : ''}">
                    <i class="fa fa-calendar"></i>
                    <span>Dashboard</span>
                </a>
            </li>
            <li class="sub-menu">
                <a href="javascript:;">
                    <i class="fa fa-laptop"></i>
                    <span>Exam & Mark Entry</span>
                </a>
                <ul class="sub">
                    <li class="${params.controller == 'markEntry' && (params.action == 'index' || params.action == 'addCtMark' || params.action == 'addHallMark') ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'markEntry', action: 'index')}">Mark Entry</a>
                    </li>
                </ul>
            </li>


            <li class="sub-menu">
                <a href="javascript:;">
                    <i class="fa fa-reply-all"></i>
                    <span>Attendance & Previous Term </span>
                </a>
                <ul class="sub">

                    <li class="${params.controller == 'previousTerm' && (params.action == 'attendance'|| params.action == 'workDays') ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'previousTerm', action: 'attendance')}">Attendance Entry </a>
                    </li>

                    <li class="${params.controller == 'previousTerm' && (params.action == 'ctMark'|| params.action == 'ctWorkDays') ? 'active' : ''}">
                        <a href="${g.createLink(controller: 'previousTerm', action: 'ctMark')}">Previous CT Mark Entry </a>
                    </li>


                </ul>
            </li>

        </ul>
    </div>
    <!-- sidebar menu end-->

</sec:access>
%{--Left Menu for TEACHER login END--}%
