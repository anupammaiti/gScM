<%@ page import="com.grailslab.enums.HrKeyType" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Manage Subject Sorting</title>
    <meta name="layout" content="moduleStdMgmtLayout"/>
</head>

<body>

<grailslab:breadCrumbActions firstBreadCrumbUrl="${g.createLink(controller: 'subject',action: 'index')}" firstBreadCrumbText="Subject" breadCrumbTitleText="Manage Sort Order"/>

<div class="row" id="create-form-holder">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Manage All Subject
            </header>
            <div class="panel-body">
                <div class="table-responsive">
                    <g:form name="myForm" method="post" action="subjectSortingSave"
                            class="create-form form-horizontal">
                        <table class="table table-striped table-hover table-bordered" id="list-table">
                            <thead>
                            <tr>
                                <th>SL</th>
                                <th>Name</th>
                                <th>Code</th>
                                <th>Show Order</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${subjectList}" var="dataSet" status="i">
                                <g:hiddenField name="subject" value="${dataSet.id}"/>
                                <tr>
                                    <td>${i+1}</td>
                                    <td>
                                        <input class="form-control"type="text" name="subjectName${dataSet.id}" value="${dataSet.name}"/>
                                    </td>
                                    <td><input class="form-control" type="text" name="code${dataSet.id}" value="${dataSet.code}"/></td>
                                    <td>
                                        <input class="form-control" type="number" name="sortPosition${dataSet.id}" value="${dataSet.sortPosition}"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                        <div class="col-md-12">
                            <button class="btn btn-default" aria-hidden="true" data-dismiss="modal"
                                    type="button">Cancel</button>
                            <button id="create-yes-btn" class="btn btn-large btn-primary" type="submit">Submit</button>
                        </div>
                    </g:form>
                </div>
            </div>
        </section>
    </div>
</div>

</body>
</html>