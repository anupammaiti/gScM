package com.grailslab.admin

import com.grailslab.settings.GradePoint

class GradePointsController {

    def index() {
        def gradePoint = GradePoint.list()
        render(view: '/admin/gradePoint', model: [gradePoint: gradePoint])
    }
}
