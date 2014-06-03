

# print summary stats per Device + UserId
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime~Device+UserId, 
               data=dataMeasures,
               FUN=c(mean,sd))
s[,order(names(s))]
write.table(s[,order(names(s))], file = "tables/measures-device+userid-mean-sd.csv", sep=",", row.names=FALSE)


# plot the histograms of the movement time for each device + userid
p <- ggplot(dataMeasures, aes(MovementTime, colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(Device ~ UserId, nrow=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/movementtime-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the boxplots of the movement time for each device + userid
p <- ggplot(dataMeasures, aes(Device, y=MovementTime,  colour=Device )) + 
    geom_boxplot() + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/boxplot/movementtime-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the ErrorRate  for each device+userid
p <- ggplot(dataMeasures, aes(ErrorRate,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap(UserId  ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/errorrate-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ErrorRate for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=ErrorRate,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/errorrate-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the TRE  for each device+userid
p <- ggplot(dataMeasures, aes(TRE,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/tre-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the TRE for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=TRE,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/tre-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the TAC  for each device+userid
p <- ggplot(dataMeasures, aes(TAC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/tac-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the boxplots of the TAC for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=TAC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/tac-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the histograms of the MDC  for each device+userid
p <- ggplot(dataMeasures, aes(MDC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) + 
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mdc-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MDC for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=MDC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mdc-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the ODC  for each device+userid
p <- ggplot(dataMeasures, aes(ODC,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/odc-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ODC for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=ODC,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/odc-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the MV  for each device+userid
p <- ggplot(dataMeasures, aes(MV,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mv-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MV for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=MV,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mv-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the ME  for each device+userid
p <- ggplot(dataMeasures, aes(ME,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/me-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the ME for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=ME,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/me-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the MO  for each device+userid
p <- ggplot(dataMeasures, aes(MO,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/mo-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the boxplots of the MO for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=MO,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/mo-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the histograms of the Throughput  for each device+userid
p <- ggplot(dataMeasures, aes(Throughput,  colour=Device )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( UserId ~ Device, ncol=3) +
    theme(legend.position="none") +
    theme()
p
ggsave(file = "charts/hist/Throughput-device+userid-hist.pdf", width=20/2.54, height=16/2.54, dpi=100)

# plot the boxplots of the Throughput for each device+userid
p <- ggplot(dataMeasures, aes(Device, y=Throughput,  colour=Device )) + 
    geom_boxplot() + #outlier.shape = NA) + 
    #coord_flip() + 
    facet_wrap( ~ UserId, nrow=1) +
    theme(legend.position="none") +
    #scale_y_continuous(limits = c(0, 0.2)) +
    theme() #noop
p
ggsave(file = "charts/boxplot/throughput-device+userid-boxplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the movement offset over the TRE for each device+userid
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device )) +
    geom_point(size=5, aes(shape=Device)) + 
    #coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
    facet_wrap( ~ UserId, nrow=3) +
    ylab("abs(Movement offset) (pixels)") +
    xlab("Target Re-Entry") +
    theme()
p
ggsave(file = "charts/MO-TRE-device+userid.pdf", width=20/2.54, height=16/2.54, dpi=100)



# line plot movement time over block for each device+userid
p <- ggplot(dataMeasures, aes(x=Block, y=MovementTime, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
    facet_wrap( ~ UserId, nrow=3) +
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() #noop
p
ggsave(file = "charts/lineplot/movementtime-block-device+userid-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)


# plot the throughput+userid
p <- ggplot(dataMeasures, aes(x=Device,y=Throughput, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="bar") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) +
    facet_wrap( ~ UserId, nrow=3) +
    theme() 
p
ggsave(file = "charts/Throughput-device+userid.pdf", width=20/2.54, height=16/2.54, dpi=100)



# plot the throughput over block+userid
p <- ggplot(dataMeasures, aes(x=Block, y=Throughput, group=Device, colour=Device )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) +
    facet_wrap( ~ UserId, nrow=3) +
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() 
p
ggsave(file = "charts/lineplot/Throughput-block-device+userid-lineplot.pdf", width=20/2.54, height=16/2.54, dpi=100)










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
