package com.grailslab.gschoolcore

/**
 * Created by aminul on 8/25/2015.
 */
public enum AcademicYear {
    Y2017('2017'),
    Y2016('2016'),
    Y2015('2015'),
    Y2014('2014'),

    Y2015_2016('2015-2016'),
	Y2016_2017('2016-2017'),
	Y2017_2018('2017-2018'),

    final String value

    static Collection<AcademicYear> schoolYears(){
        return [Y2017, Y2016, Y2015, Y2014]
    }
    static Collection<AcademicYear> collegeYears(){
        return [Y2015_2016, Y2016_2017, Y2017_2018]
    }
    AcademicYear(String value) {
        this.value = value
    }

    String toString() { value }
    String getKey() { name() }

    public static AcademicYear getYearByString(String year){
        for(AcademicYear e : AcademicYear.values()){
            if(year == e.value) return e;
        }
        return null;
    }
}