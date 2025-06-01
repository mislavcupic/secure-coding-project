package hr.algebra.semregprojectbackend.domain.whitelisting;

import java.util.Set;

public class WhiteListed {
    public static final Set<String> WHITE_LISTED = Set.of("hr.algebra.semregprojectbackend","java.util.Set"
            ,"hr.algebra.semregprojectbackend.domain.Student","hr.algebra.semregprojectbackend.domain.Seminar","hr.algebra.semregprojectbackend.domain.Registration",
            "hr.algebra.semregprojectbackend.domain.SeminarDTO","hr.algebra.semregprojectbackend.domain.StudentDTO");

    WhiteListed(){}


}
