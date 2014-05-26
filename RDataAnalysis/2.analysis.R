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
dataMeasures <- read.csv(file="data/measures.txt", head=TRUE, sep="")


# change column name to get a nicer chart
colnames(dataMeasures)[colnames(dataMeasures)=="DeviceNumber"] <- "Device"

# convert the column to factor and name the levels
dataMeasures$Device <- as.factor(dataMeasures$Device)
levels(dataMeasures$Device) <- list( LeapMotion=c(0),  Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4))

# print summary stats
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime~Device, 
                data=dataMeasures,
               FUN=c(mean,sd))
t(s)



# plot the boxplots of the movement time for each device
p <- ggplot(dataMeasures, aes(Device, y=MovementTime,  colour=Device )) + 
    geom_boxplot() + 
    coord_flip() + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/movementtime-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ErrorRate for each device
p <- ggplot(dataMeasures, aes(Device, y=ErrorRate,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/errorrate-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the TRE for each device
p <- ggplot(dataMeasures, aes(Device, y=TRE,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/tre-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the TAC for each device
p <- ggplot(dataMeasures, aes(Device, y=TAC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/tac-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MDC for each device
p <- ggplot(dataMeasures, aes(Device, y=MDC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/mdc-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the ODC for each device
p <- ggplot(dataMeasures, aes(Device, y=ODC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/odc-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)




# plot the boxplots of the MV for each device
p <- ggplot(dataMeasures, aes(Device, y=MV,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/mv-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ME for each device
p <- ggplot(dataMeasures, aes(Device, y=ME,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/me-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MO for each device
p <- ggplot(dataMeasures, aes(Device, y=MO,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/mo-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the Throughput for each device
p <- ggplot(dataMeasures, aes(Device, y=Throughput,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/throughput-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)




# plot the movement offset over the TRE for each device
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device ))
p + geom_point(size=5, aes(shape=Device)) + coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
    ylab("Movement offset (pixels)") +
    xlab("Target Re-Entry") 
ggsave(file = "charts/MO-TRE.pdf", width=20/2.54, height=16/2.54, dpi=100)



# line plot movement time over block for each device
p <- ggplot(dataMeasures, aes(x=Block, y=MovementTime, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() #noop
p
ggsave(file = "charts/movementtime-block-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the throughput
p <- ggplot(dataMeasures, aes(x=Device,y=Throughput, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="bar") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) +
    theme() 
p
ggsave(file = "charts/Throughput.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the throughput over block
p <- ggplot(dataMeasures, aes(x=Block, y=Throughput, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) +
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() 
p
ggsave(file = "charts/Throughput-block-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)





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