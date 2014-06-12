#install.packages("ppcor")
require(ppcor)

dataMeasures$DeviceNumber <- as.numeric(dataMeasures$Device)

View(dataMeasures)

cors <- cor(dataMeasures[,c(2,6:13, 18, 22)])
View(cors)

class(dataMeasures$Throughput)
class(dataMeasures$TRE)

pcor(dataMeasures[,c(6:13, 18, 22)])

pcor.test(dataMeasures$Throughput,dataMeasures$MovementTime,dataMeasures[,c("DeviceNumber", "UserId")])

#cors.test <- cor.test(~MO+Throughput,data=dataMeasures)
#cors.test

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



















a <- aov(Throughput~Block,data=dataMeasures[dataMeasures$Block>4 & dataMeasures$Device=="Touchpad",])
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