    <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" role="form" id="createFormModal">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">Load Employee Attendance</h4>
                </div>
                <div class="modal-body">
                    <div class="row">

                        <div class="form-group">
                            <label for="academicYear" class="col-md-4 control-label">Year</label>
                            <div class="col-md-4">
                                <g:select class="form-control" id="academicYear" name='academicYear' tabindex="3"
                                          noSelection="${['': 'Select Year...']}"
                                          from='${com.grailslab.gschoolcore.AcademicYear.schoolYears()}'
                                          optionKey="key" optionValue="value"></g:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="yearMonths" class="col-md-4 control-label">Month</label>
                            <div class="col-md-4">
                                <g:select class="form-control" id="yearMonths" name='yearMonths' tabindex="3"
                                          noSelection="${['': 'Select Month...']}"
                                          from='${com.grailslab.enums.YearMonths.values()}'
                                          optionKey="key" optionValue="value"></g:select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn  btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    <button type="button" class="btn btn-primary" id="yearmonthBtn">Load</button>
                </div>

            </form>
        </div>
    </div>
