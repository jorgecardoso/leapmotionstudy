

# analysis of variance
bartlett.test(MovementTime~Device,data=dataMeasures)
a <- aov(MovementTime~Device,data=dataMeasures)
summary(a)

a <- aov(ErrorRate~Device,data=dataMeasures)
summary(a)

a <- aov(TRE~Device,data=dataMeasures)
summary(a)

a <- aov(TAC~Device,data=dataMeasures)
summary(a)

a <- aov(MDC~Device,data=dataMeasures)
summary(a)

a <- aov(ODC~Device,data=dataMeasures)
summary(a)

a <- aov(MV~Device,data=dataMeasures)
summary(a)

a <- aov(ME~Device,data=dataMeasures)
summary(a)

a <- aov(MO~Device,data=dataMeasures)
summary(a)





















# analysis of variance
bartlett.test(MovementTime~Device,data=dataMeasures)
a <- aov(MovementTime~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(ErrorRate~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(TRE~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(TAC~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(MDC~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(ODC~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(MV~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(ME~Device+UserId,data=dataMeasures)
summary(a)

a <- aov(MO~Device+UserId,data=dataMeasures)
summary(a)