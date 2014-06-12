source("functions.R")


# print summary stats per Device
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime+Throughput~Device, 
               data=dataMeasures,
               FUN=c(mean,sd))
s[,order(names(s))]
write.table(s[,order(names(s))], file = paste("tables/", filenameprefix,"-measures-device-summary.csv", sep=""), sep=",", row.names=FALSE)

# describeby prints additional stats.
# but it does not work well with write.table so we sink the output
s1<-describeBy(dataMeasures[,c("ErrorRate","TRE","TAC","MDC","ODC","MV","ME","MO","MovementTime","Throughput")], dataMeasures$Device, mat=FALSE, digits=2)
s1 <- do.call("rbind", s1)
s1 <- cbind(Device=rownames(s1), s1)
write.table(s1, file = paste("tables/", filenameprefix,"-measures-device-describeby.csv", sep=""), sep=",", row.names=FALSE)



for (var in c("MovementTime", "ErrorRate", "TRE", "TAC", "MDC", "ODC", "MV", "ME", "MO", "Throughput") ) {
  
  # plot the histograms of the for each device
  p <- ggplot(dataMeasures, aes_string(x=var, colour="Device" )) + 
    geom_histogram() + 
    #coord_flip() + 
    facet_wrap( ~ Device, ncol=1) +
    theme(legend.position="none") +
    #ylab("Percentage (%)") +
    #xlab("Sequence") +
    #ggtitle(var) +
    theme()
  p
  ggsave(file = paste("charts/hist/",filenameprefix,"-",var,"-hist.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
  
  
  
  # plot the boxplots of the movement time for each device
  p <- ggplot(dataMeasures, aes_string(x="Device", y=var,  colour="Device" )) + 
    geom_boxplot() + 
    coord_flip() + 
    theme(legend.position="none") +
    #ylab("Percentage (%)") +
    #xlab("Sequence") +
    #ggtitle(var) +
    theme()
  p
  ggsave(file = paste("charts/boxplot/",filenameprefix,"-",var,"-boxplot.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
  
  
  
  # line plot movement time over block for each device
  p <- ggplot(dataMeasures, aes_string(x="Block", y=var, group="Device", colour="Device" )) + 
    stat_summary(fun.y="mean", geom="line") + 
    stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
    theme(legend.direction = "horizontal", legend.position = "top") +
    #ylab("Percentage (%)") +
    #xlab("Sequence") +
    #ggtitle(var) +
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



# plot the throughput and ErrorRate

describe <- describeBy(dataMeasures$Throughput, list(dataMeasures$Device))
describe<-do.call("rbind",describe)
describe <- cbind(Device=rownames(describe), describe)
levels(describe$Device) <- list( Mouse=c("Mouse"), Touchpad=c("Touchpad"), LeapMotionTouchless=c("LeapMotionTouchless"))
p1 <- ggplot(describe, aes(x=Device, y=mean, colour=Device, fill=Device )) + 
  geom_bar(stat="identity") +
  ylab("BPS") +
  #xlab("Sequence") +
  ggtitle("Throughput") +
  geom_errorbar(aes(ymin=mean-1.96*se, ymax=mean+1.96*se), colour="Black",
                width=.2,                    # Width of the error bars
                position=position_dodge(.9)) +
    theme(legend.position="none") +
  theme() 
p1
#ggsave(file = paste("charts/",filenameprefix,"-Throughput.pdf",sep=""), width=20/2.54, height=16/2.54, dpi=100)

describe <- describeBy(dataMeasures$ErrorRate, list(dataMeasures$Device))
describe<-do.call("rbind",describe)
describe <- cbind(Device=rownames(describe), describe)
levels(describe$Device) <- list( Mouse=c("Mouse"), Touchpad=c("Touchpad"), LeapMotionTouchless=c("LeapMotionTouchless"))

p2 <- ggplot(describe, aes(x=Device, y=mean*100, colour=Device, fill=Device )) + 
  geom_bar(stat="identity") +
  ylab("Percentage (%)") +
  #xlab("Sequence") +
  ggtitle("ErrorRate") +
  geom_errorbar(aes(ymin=100*(mean-1.96*se), ymax=100*(mean+1.96*se)), colour="Black",
                width=.2,                    # Width of the error bars
                position=position_dodge(.9)) +
  
    theme(legend.position=c(0,0.9), legend.direction="horizontal") +
  theme() 
p2

pdf(file = paste("charts/",filenameprefix,"-Throughput+ErrorRate.pdf",sep=""), width=20/2.54, height=16/2.54) 
multiplot(p1, p2, cols=2)
dev.off()
