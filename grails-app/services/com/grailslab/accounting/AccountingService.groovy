package com.grailslab.accounting

import com.grailslab.CommonUtils
import com.grailslab.accounts.GlHeader
import com.grailslab.enums.BookStockStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.library.Book
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class AccountingService {

    def allVoucherList(String sSearch){
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = GlHeader.createCriteria()

        def results = c.list() {
            and {

            }
            if (sSearch) {
                or {
                    ilike('xvoucher', sSearch)
                    ilike('xtermgl', sSearch)
                }
            }
            order("xtermgl", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { GlHeader obj ->
            dataReturns.add([id: obj.id, name: "${obj.xtermgl} - ${obj.xvoucher}"])
        }
        return dataReturns
    }
}
