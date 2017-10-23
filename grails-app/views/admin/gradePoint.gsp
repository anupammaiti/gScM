<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Grade Point</title>
    <meta name="layout" content="moduleStdMgmtLayout"/>
</head>
<body>
<grailslab:breadCrumbActions  breadCrumbTitleText="Grade Point"/>
<div class="row" >
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Grade Point List
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th class="col-md-2">G.P</th>
                            <th class="col-md-3">Marks</th>
                            <th class="col-md-2">L. Grade</th>
                            <th class="col-md-5">Credentials</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${gradePoint}" var="gPoint" status="i">
                            <tr>
                                <td>${gPoint?.gPoint}</td>
                                <td>${gPoint?.fromMark+'-'+gPoint?.upToMark}</td>
                                <td>${gPoint?.lGrade}</td>
                                <td>${gPoint?.credentials}</td>

                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    jQuery(function ($) {
        $('#list-table').dataTable({"aaSorting": [0,'desc']});
    });
</script>
</body>
</html>


