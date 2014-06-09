


# print summary stats per Device
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime~Device, 
               data=dataMeasures,
               FUN=c(mean,sd))
s[,order(names(s))]
write.table(s[,order(names(s))], file = paste("tables/", filenameprefix,"-measures-device-mean-sd.csv", sep=""), sep=",", row.names=FALSE)

# describeby prints additional stats.
# plbut it does not work well with write.table so we sink the output
s1<-describeBy(dataMeasures[,6:22], dataMeasures$Device, mat=FALSE, digits=2)
sink(file = paste("tables/", filenameprefix, "-measures-device.csv", sep=""), append = FALSE, type = c("output"),  split = FALSE)
s1
sink()

for (var in c("MovementTime", "ErrorRate", "TRE", "TAC", "MDC", "ODC", "MV", "ME", "MO", "Throughput") ) {
  
  # plot the histograms of the movement time for each device
  p <- ggplot(dataMeasures, aes_string(var, colour="Device" )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( ~ Device, ncol=1) +
    theme(legend.position="none") +
    theme()
  p
  ggsave(file = paste("charts/hist/",filenameprefix,"-",var,"-hist.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
  
  
  
  # plot the boxplots of the movement time for each device
  p <- ggplot(dataMeasures, aes_string("Device", y=var,  colour="Device" )) + 
    geom_boxplot() + 
    coord_flip() + 
    theme(legend.position="none") +
    theme()
  p
  ggsave(file = paste("charts/boxplot/",filenameprefix,"-",var,"-boxplot.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
  
  
  
  # line plot movement time over block for each device
  p <- ggplot(dataMeasures, aes_string(x="Block", y=var, group="Device", colour="Device" )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
    theme(legend.direction = "horizontal", legend.position = "top") +
    theme() #noop
  p
  ggsave(file = paste("charts/lineplot/",filenameprefix,"-",var,"-block-lineplot.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
  
}



# plot the movement offset over the TRE for each device
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device )) +
  geom_point(size=5, aes(shape=Device)) + 
  #coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
  ylab("Movement offset (pixels)") +
  xlab("Target Re-Entry") +
  theme()
p
ggsave(file = paste("charts/",filenameprefix,"-MO-TRE.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)



# plot the throughput
p <- ggplot(dataMeasures, aes(x=Device,y=Throughput, group=Device, colour=Device, fill=Device )) + 
  stat_summary(fun.y="mean", geom="bar") + 
  stat_summary(fun.y="mean", geom="point", aes(shape=Device)) +
  theme() 
p
ggsave(file = paste("charts/",filenameprefix,"-Throughput.pdf",sep=""), width=20/2.54, height=16/2.54, dpi=100)
