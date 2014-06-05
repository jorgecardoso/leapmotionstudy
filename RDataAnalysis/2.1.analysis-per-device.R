
# print summary stats per Device
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime~Device, 
               data=dataMeasures,
               FUN=c(mean,sd))
s[,order(names(s))]
write.table(s[,order(names(s))], file = "tables/measures-device-mean-sd.csv", sep=",", row.names=FALSE)


# plot the histograms of the movement time for each device
p <- ggplot(dataMeasures, aes(MovementTime, colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/movementtime-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the movement time for each device
p <- ggplot(dataMeasures, aes(Device, y=MovementTime,  colour=Device )) + 
    geom_boxplot() + 
    coord_flip() + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/boxplot/movementtime-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the ErrorRate  for each device
p <- ggplot(dataMeasures, aes(ErrorRate,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/errorrate-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ErrorRate for each device
p <- ggplot(dataMeasures, aes(Device, y=ErrorRate,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/errorrate-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the histograms of the TRE  for each device
p <- ggplot(dataMeasures, aes(TRE,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/tre-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the TRE for each device
p <- ggplot(dataMeasures, aes(Device, y=TRE,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/tre-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the histograms of the TAC  for each device
p <- ggplot(dataMeasures, aes(TAC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/tac-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the TAC for each device
p <- ggplot(dataMeasures, aes(Device, y=TAC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/tac-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the MDC  for each device
p <- ggplot(dataMeasures, aes(MDC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mdc-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MDC for each device
p <- ggplot(dataMeasures, aes(Device, y=MDC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mdc-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the ODC  for each device
p <- ggplot(dataMeasures, aes(ODC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/odc-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ODC for each device
p <- ggplot(dataMeasures, aes(Device, y=ODC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/odc-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the histograms of the MV  for each device
p <- ggplot(dataMeasures, aes(MV,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mv-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MV for each device
p <- ggplot(dataMeasures, aes(Device, y=MV,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mv-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the histograms of the ME  for each device
p <- ggplot(dataMeasures, aes(ME,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/me-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the ME for each device
p <- ggplot(dataMeasures, aes(Device, y=ME,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/me-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the MO  for each device
p <- ggplot(dataMeasures, aes(MO,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mo-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the MO for each device
p <- ggplot(dataMeasures, aes(Device, y=MO,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mo-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the Throughput  for each device
p <- ggplot(dataMeasures, aes(Throughput,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(  ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/Throughput-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the Throughput for each device
p <- ggplot(dataMeasures, aes(Device, y=Throughput,  colour=Device)) + 
    geom_boxplot() + #outlier.shape = NA) + 
    coord_flip() + 
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/throughput-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)




# plot the movement offset over the TRE for each device
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device )) +
    geom_point(size=5, aes(shape=Device)) + 
    #coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
    ylab("Movement offset (pixels)") +
    xlab("Target Re-Entry") +
    theme()
p
ggsave(file = "charts/MO-TRE.pdf", width=20/2.54, height=16/2.54, dpi=100)



# line plot movement time over block for each device
p <- ggplot(dataMeasures, aes(x=Block, y=MovementTime, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() #noop
p
ggsave(file = "charts/lineplot/movementtime-block-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the throughput
p <- ggplot(dataMeasures, aes(x=Device,y=Throughput, group=Device, colour=Device, fill=Device )) + 
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
ggsave(file = "charts/lineplot/Throughput-block-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)




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