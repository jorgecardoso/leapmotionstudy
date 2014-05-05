#require(RCurl) # web
require(ggplot2)
require(doBy)
#library("stringr") # for first word
#library(reshape)
#theme_set(theme_bw())


############################ data
#dataMeasuresLeap <- read.csv(file="data/leapmotion-measures.txt", head=TRUE, sep="")
#dataMeasuresTouch <- read.csv(file="data/touchpad-measures.txt", head=TRUE, sep="")
#dataMeasuresMouse <- read.csv(file="data/mouse-measures.txt", head=TRUE, sep="")

#dataMeasures <- rbind(dataMeasuresLeap, dataMeasuresTouch, dataMeasuresMouse)
dataMeasures <- read.csv(file="data/all-measures.txt", head=TRUE, sep="")


# change column name to get a nicer chart
colnames(dataMeasures)[colnames(dataMeasures)=="DeviceNumber"] <- "Device"

# convert the column to factor and name the levels
dataMeasures$Device <- as.factor(dataMeasures$Device)
levels(dataMeasures$Device) <- c("LeapMotion",  "Mouse", "Touchpad")

# print summary stats
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime~Device, 
                data=dataMeasures,
               FUN=c(mean,sd))
t(s)


# plot the movement time over block for each device
p <- ggplot(dataMeasures, aes(x=Block, y=MovementTime, group=Device, colour=Device ))
p + stat_summary(fun.y="mean", geom="line") + stat_summary(fun.y="mean", geom="point", aes(shape=Device)) 
ggsave(file = "charts/movementtime-block.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the movement offset over the TRE for each device
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device ))
p + geom_point(size=5, aes(shape=Device)) + coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
    ylab("Movement offset (pixels)") +
    xlab("Target Re-Entry") 
ggsave(file = "charts/MO-TRE.pdf", width=20/2.54, height=16/2.54, dpi=100)



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

names(dataMeasures)
boxplot(dataMeasures$MovementTime)