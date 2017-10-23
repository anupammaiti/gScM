import com.grailslab.bailyschool.uma.Role
import com.grailslab.bailyschool.uma.User
import com.grailslab.bailyschool.uma.UserRole
import com.grailslab.enums.CalenderMonths
import com.grailslab.enums.ExpenseHead
import com.grailslab.enums.HrKeyType
import com.grailslab.enums.MainUserType
import com.grailslab.enums.OpenContentType
import com.grailslab.enums.YearMonths
import com.grailslab.enums.NodeType
import com.grailslab.enums.AccountType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.HrCategory
import com.grailslab.hr.HrStaffCategory
import com.grailslab.open.OpenContent
import com.grailslab.accounting.ChartOfAccount
import com.grailslab.salary.SalConfiguration
import com.grailslab.settings.School
class BootStrap {
    def sequenceGeneratorService

    def init = { servletContext ->
//        addSuperAdmin()
//        initNewSchool()
//        seedPublicWebSite()
//        seedSchoolHr()
//        seedAccountData()
//        createSalary()
//        createOrganizerAndParentRole()
        sequenceGeneratorService.initSequence('libraryMembership', null, null, 1, '%05d')

        if(SalConfiguration.count()==0){new SalConfiguration(extraClassRate:250,lateFineForDays:3,pfContribution:10,pfCalField:"basic").save()}
      }
    void initNewSchool(){
        seedUserWithRole()
        seedSequence()
        seedSchoolHr()
        seedAccountData()
        seedPublicWebSite()
    }




    void createOrganizerAndParentRole(){
        Role.AvailableRoles.values().each {
            if (!Role.findByAuthority(it.value())) {
                new Role(authority: it.value()).save()
            }
        }
        def roleOrganizer = Role.findByAuthority(Role.AvailableRoles.ORGANIZER.value())
        def roleStudent = Role.findByAuthority(Role.AvailableRoles.STUDENT.value())

        User organizer = User.findByUsername('organizer') ?: new User(username: 'organizer', password: 'organizer@369', schoolId: 10000, mainUserType: MainUserType.Organizer).save(flush: true)
        if (!UserRole.findByUserAndRole(organizer, roleOrganizer)) {
            UserRole.create(organizer, roleOrganizer, true)
        }
        /*User student = User.findByUsername('student') ?: new User(username: 'student', password: 'student@369', schoolId: 10000, mainUserType: MainUserType.Student).save(flush: true)
        if (!UserRole.findByUserAndRole(student, roleStudent)) {
            UserRole.create(student, roleStudent, true)
        }*/
        sequenceGeneratorService.initSequence('bkashInvNo', null, null, 1, 'bk%06d')
        sequenceGeneratorService.initSequence('fesRegi2016', null, null, 1, '%04d')
    }

    void addSuperAdmin(){
        //Super Admin user
        def roleSwitchUser = Role.findByAuthority(Role.AvailableRoles.SWITCH_USER.value())
        def roleSuperAdmin = Role.findByAuthority(Role.AvailableRoles.SUPER_ADMIN.value())

        User suAdmin = User.findByUsername('info.grailslab@gmail.com') ?: new User(username: 'info.grailslab@gmail.com', name:'SuperAdmin', password: 'word123p@ss', schoolId: 10000, mainUserType: MainUserType.SuperAdmin).save(flush: true)
        if (!UserRole.findByUserAndRole(suAdmin, roleSwitchUser)) {
            UserRole.create(suAdmin, roleSwitchUser, true)
        }
        if (!UserRole.findByUserAndRole(suAdmin, roleSuperAdmin)) {
            UserRole.create(suAdmin, roleSuperAdmin, true)
        }
    }

    /*void createSalary() {
        ExpenseItem salary = ExpenseItem.findByName('Salary') ?: new ExpenseItem(name: 'Salary', expenseHead: ExpenseHead.SALARY, schoolId: 10000).save(flush: true)
        ExpenseDetail januarySalary = ExpenseDetail.findByName(CalenderMonths.January.key) ?: new ExpenseDetail(name: CalenderMonths.January.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail februarySalary = ExpenseDetail.findByName(CalenderMonths.February.key) ?: new ExpenseDetail(name: CalenderMonths.February.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail marchSalary = ExpenseDetail.findByName(CalenderMonths.March.key) ?: new ExpenseDetail(name: CalenderMonths.March.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail aprilSalary = ExpenseDetail.findByName(CalenderMonths.April.key) ?: new ExpenseDetail(name: CalenderMonths.April.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail maySalary = ExpenseDetail.findByName(CalenderMonths.May.key) ?: new ExpenseDetail(name: CalenderMonths.May.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail junSalary = ExpenseDetail.findByName(CalenderMonths.June.key) ?: new ExpenseDetail(name: CalenderMonths.June.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail julySalary = ExpenseDetail.findByName(CalenderMonths.July.key) ?: new ExpenseDetail(name: CalenderMonths.July.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail augustSalary = ExpenseDetail.findByName(CalenderMonths.August.key) ?: new ExpenseDetail(name: CalenderMonths.August.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail septemberSalary = ExpenseDetail.findByName(CalenderMonths.September.key) ?: new ExpenseDetail(name: CalenderMonths.September.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail octoberSalary = ExpenseDetail.findByName(CalenderMonths.October.key) ?: new ExpenseDetail(name: CalenderMonths.October.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail novemberSalary = ExpenseDetail.findByName(CalenderMonths.November.key) ?: new ExpenseDetail(name: CalenderMonths.November.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)
        ExpenseDetail decemberSalary = ExpenseDetail.findByName(CalenderMonths.December.key) ?: new ExpenseDetail(name: CalenderMonths.December.key, expenseHead: ExpenseHead.SALARY, expenseItem: salary, schoolId: 10000).save(flush: true)


    }*/

    //new school init
    void seedUserWithRole() {
        School school = School.read(10000)
        if (!school) {
            school = new School(name: 'Adarsha Shishu Government Primary School', address: '25-26, Nawab Sirajuddoullah Road, Narayanganj', email: 'narayanganjhighschool@gmail.com', contactNo: '7635133', academicYear: AcademicYear.Y2016)
            school.id = 10000
            school.save()
        }
        Role.AvailableRoles.values().each {
            if (!Role.findByAuthority(it.value())) {
                new Role(authority: it.value()).save()
            }
        }
        def roleSwitchUser = Role.findByAuthority(Role.AvailableRoles.SWITCH_USER.value())
        def roleSuperAdmin = Role.findByAuthority(Role.AvailableRoles.SUPER_ADMIN.value())
        def roleAdmin = Role.findByAuthority(Role.AvailableRoles.ADMIN.value())
        def roleHr = Role.findByAuthority(Role.AvailableRoles.HR.value())
        def roleTeacher = Role.findByAuthority(Role.AvailableRoles.TEACHER.value())
        def roleApplicant = Role.findByAuthority(Role.AvailableRoles.APPLICANT.value())
        def roleAccount = Role.findByAuthority(Role.AvailableRoles.ACCOUNTS.value())
        def roleSchoolHead = Role.findByAuthority(Role.AvailableRoles.SCHOOL_HEAD.value())
        def roleOrganizer = Role.findByAuthority(Role.AvailableRoles.ORGANIZER.value())

        //Super Admin user
        User suAdmin = User.findByUsername('info.grailslab@gmail.com') ?: new User(username: 'info.grailslab@gmail.com', password: 'word123p@ss', schoolId: 10000, mainUserType: MainUserType.SuperAdmin).save(flush: true)
        if (!UserRole.findByUserAndRole(suAdmin, roleSwitchUser)) {
            UserRole.create(suAdmin, roleSwitchUser, true)
        }
        if (!UserRole.findByUserAndRole(suAdmin, roleSuperAdmin)) {
            UserRole.create(suAdmin, roleSuperAdmin, true)
        }

        //admin user
        User admin = User.findByUsername('admin') ?: new User(username: 'admin', password: 'admin@369', schoolId: 10000, mainUserType: MainUserType.Admin).save(flush: true)
        if (!UserRole.findByUserAndRole(admin, roleSwitchUser)) {
            UserRole.create(admin, roleSwitchUser, true)
        }
        if (!UserRole.findByUserAndRole(admin, roleAdmin)) {
            UserRole.create(admin, roleAdmin, true)
        }

        User hr = User.findByUsername('hr') ?: new User(username: 'hr', password: 'hr@369', schoolId: 10000, mainUserType: MainUserType.HR).save(flush: true)
        if (!UserRole.findByUserAndRole(hr, roleHr)) {
            UserRole.create(hr, roleHr, true)
        }

        User account = User.findByUsername('account') ?: new User(username: 'account', password: 'account@369', schoolId: 10000, mainUserType: MainUserType.Accounts).save(flush: true)
        if (!UserRole.findByUserAndRole(account, roleAccount)) {
            UserRole.create(account, roleAccount, true)
        }

        User organizer = User.findByUsername('organizer') ?: new User(username: 'organizer', password: 'organizer@369', schoolId: 10000, mainUserType: MainUserType.Organizer).save(flush: true)
        if (!UserRole.findByUserAndRole(organizer, roleOrganizer)) {
            UserRole.create(organizer, roleOrganizer, true)
        }

    }

    void seedSequence(){
        //new Sequence
        sequenceGeneratorService.initSequence('InvoiceNo', null, null, 1, 'INV-%06d')
        sequenceGeneratorService.initSequence('bkashInvNo', null, null, 1, 'bk%06d')
        sequenceGeneratorService.initSequence('studentNo', null, null, 1, '%06d')
        sequenceGeneratorService.initSequence('libraryMembership', null, null, 1, '%05d')
    }
    void seedSchoolHr(){
        HrCategory.findByHrKeyType(HrKeyType.HM) ?: new HrCategory(hrKeyType: HrKeyType.HM, name: 'School Head', sortOrder: 3).save(flush: true)
        HrCategory.findByHrKeyType(HrKeyType.AHM) ?: new HrCategory(hrKeyType: HrKeyType.AHM, name: 'Assistant Head', sortOrder: 2).save(flush: true)
        HrCategory.findByHrKeyType(HrKeyType.TEACHER) ?: new HrCategory(hrKeyType: HrKeyType.TEACHER, name: 'Teacher', sortOrder: 1).save(flush: true)
        HrCategory.findByHrKeyType(HrKeyType.OSTAFF) ?: new HrCategory(hrKeyType: HrKeyType.OSTAFF, name: 'Office Staff', sortOrder: 4).save(flush: true)
        HrCategory.findByHrKeyType(HrKeyType.SSTAFF) ?: new HrCategory(hrKeyType: HrKeyType.SSTAFF, name: 'Support Staff', sortOrder: 5).save(flush: true)

        //Staff Category for showing staff list in public website
//        HrStaffCategory.findByKeyName('general') ?: new HrStaffCategory(keyName: 'general', name: 'General Section', description: 'School Start journey on 1965. Mr. Rahman was the founding Headmaster of the school. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:1).save(flush: true)
//        HrStaffCategory.findByKeyName('vocational') ?: new HrStaffCategory(keyName: 'vocational', name: 'Vocational Section', description: 'Vocational Section start journey on 1997. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:2).save(flush: true)

        HrStaffCategory.findByKeyName('morning') ?: new HrStaffCategory(keyName: 'morning', name: 'Morning Shift', description: 'Morning Shift start on 2001. Usual Session from 7:30 AM to 10:30 AM. Classes hold from Sunday to Thrusday. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:1).save(flush: true)
        HrStaffCategory.findByKeyName('day') ?: new HrStaffCategory(keyName: 'day', name: 'Day Shift', description: 'Day Shift start on 2001. Usual Session from 10:30 AM to 2:30 PM. Classes hold from Sunday to Thrusday. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:2).save(flush: true)

        HrStaffCategory.findByKeyName('primary') ?: new HrStaffCategory(keyName: 'primary', name: 'Primary Section', description: 'Day Shift start on 2001. Usual Session from 10:30 AM to 2:30 PM. Classes hold from Sunday to Thrusday. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:5).save(flush: true)
        HrStaffCategory.findByKeyName('school') ?: new HrStaffCategory(keyName: 'school', name: 'School Section', description: 'Day Shift start on 2001. Usual Session from 10:30 AM to 2:30 PM. Classes hold from Sunday to Thrusday. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:6).save(flush: true)
//        HrStaffCategory.findByKeyName('college') ?: new HrStaffCategory(keyName: 'college', name: 'College Section', description: 'Day Shift start on 2001. Usual Session from 10:30 AM to 2:30 PM. Classes hold from Sunday to Thrusday. Mr. ABC was the key person to introduce this sction. Many talented teachers teaching in this school. Now there is 25 full time teacher, 5 staff and 3 support staff in General Section', sortOrder:7).save(flush: true)
    }
    void seedAccountData() {
        ChartOfAccount.findByCode(100000)?:new ChartOfAccount(name:"Asset", code:100000,nodeType:NodeType.ROOT, accountType:AccountType.ASSET).save()
        ChartOfAccount.findByCode(200000)?:new ChartOfAccount(name:"Income", code:200000,nodeType:NodeType.ROOT, accountType:AccountType.INCOME).save()
        ChartOfAccount.findByCode(300000)?:new ChartOfAccount(name:"Expense", code:300000,nodeType:NodeType.ROOT, accountType:AccountType.EXPENSE).save()
        ChartOfAccount.findByCode(400000)?:new ChartOfAccount(name:"Stationary", code:400000,nodeType:NodeType.ROOT, accountType:AccountType.STATIONARY).save()
        ChartOfAccount.findByCode(500000)?:new ChartOfAccount(name:"Cafeteria", code:500000,nodeType:NodeType.ROOT, accountType:AccountType.CAFETERIA).save()
    }
    void seedPublicWebSite() {
        OpenContent.findByTitle("SCHOOL FACULTIES")?:new OpenContent(openContentType:OpenContentType.Feature_Content,
                title:'SCHOOL FACULTIES',iconClass:'icon-book',description:'Our prime goal is to establish such a school which meets the demand of the city and so her quality, philosophy & standard are always being tried to keep up to the level of the age. We collect materials for imparting education from source, ponder over them, do reflect upon learner�s',
                sortOrder:1).save()
        OpenContent.findByTitle("EDUCATION SYSTEMS")?:new OpenContent(openContentType:OpenContentType.Feature_Content,
                title:'EDUCATION SYSTEMS',iconClass:'icon-book',description:'The Baily school an educational framework that today includes pre-nursery level to junior high school. The framework includes lots of children, the majority of whom travel daily from more than several communities within a radius of 30 kilometers. Children in the framework.......',
                sortOrder:2).save()
        OpenContent.findByTitle("OTHER FACULTIES")?:new OpenContent(openContentType:OpenContentType.Feature_Content,
                title:'OTHER FACULTIES',iconClass:'icon-book',description:'The Rowland Institute at Harvard is located in a 110000 square-foot building located on the Charles River in Cambridge, Massachusetts,in the midst of a ...',
                sortOrder:3).save()
        OpenContent.findByTitle("MISSION AND VISSION")?:new OpenContent(openContentType:OpenContentType.Feature_Content,
                title:'MISSION AND VISSION',iconClass:'icon-book',description:'It is a global standard school with a difference under the available combined education programmed of NCTBB and University of London, UK with an aristocratic and luxurious perspective.It was founded in 2006 establishing location at the city heart and prime point in Narayanganj.....',
                sortOrder:4).save()
    }


    def destroy = {
    }
}
