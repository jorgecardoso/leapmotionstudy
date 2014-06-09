#install.packages(c("ggplot2", "doBy"))
require(ggplot2)
require(doBy)
require(psych)
library(reshape2)


## Summarizes data.
## Gives count, mean, standard deviation, standard error of the mean, and confidence interval (default 95%).
##   data: a data frame.
##   measurevar: the name of a column that contains the variable to be summariezed
##   groupvars: a vector containing names of columns that contain grouping variables
##   na.rm: a boolean that indicates whether to ignore NA's
##   conf.interval: the percent range of the confidence interval (default is 95%)
summarySE <- function(data=NULL, measurevar, groupvars=NULL, na.rm=FALSE,
                      conf.interval=.95, .drop=TRUE) {
  require(plyr)
  
  # New version of length which can handle NA's: if na.rm==T, don't count them
  length2 <- function (x, na.rm=FALSE) {
    if (na.rm) sum(!is.na(x))
    else       length(x)
  }
  
  # This does the summary. For each group's data frame, return a vector with
  # N, mean, and sd
  datac <- ddply(data, groupvars, .drop=.drop,
                 .fun = function(xx, col) {
                   c(N    = length2(xx[[col]], na.rm=na.rm),
                     mean = mean   (xx[[col]], na.rm=na.rm),
                     sd   = sd     (xx[[col]], na.rm=na.rm)
                   )
                 },
                 measurevar
  )
  
  # Rename the "mean" column    
  datac <- rename(datac, c("mean" = measurevar))
  
  datac$se <- datac$sd / sqrt(datac$N)  # Calculate standard error of the mean
  
  # Confidence interval multiplier for standard error
  # Calculate t-statistic for confidence interval: 
  # e.g., if conf.interval is .95, use .975 (above/below), and use df=N-1
  ciMult <- qt(conf.interval/2 + .5, datac$N-1)
  datac$ci <- datac$se * ciMult
  
  return(datac)
}


data <- read.csv(file="data/Resultados.csv", head=TRUE, skip=1, sep=",")
names(data)

s <- summary(data[,c(4:9)])
s
write.table(s, file = "tables/questionnaire-demographics-summary.csv", sep=",", row.names=FALSE)


s <- summary(data[,c(11:22)])
s
write.table(s, file = "tables/questionnaire-leapmotion-summary.csv", sep=",", row.names=FALSE)


s <- summary(data[,c(24:35)])
s
write.table(s, file = "tables/questionnaire-touchpad-summary.csv", sep=",", row.names=FALSE)


s <- summary(data[,c(37:48)])
s
write.table(s, file = "tables/questionnaire-mouse-summary.csv", sep=",", row.names=FALSE)


s <- summary(data[,c(50, 51)])
s
write.table(s, file = "tables/questionnaire-devicepreference-summary.csv", sep=",", row.names=FALSE)



#####
m <- NULL
for (i in 11:22) {
  print(colnames(data)[i])
  c1 <- colnames(data)[i]
  c2 <- paste(c1,".1", sep="")
  c3 <- paste(c1,".2", sep="")
  
  m1 <- melt(data[,c("ID", c1, c2, c3)], id=c("ID"))
  colnames(m1) <- c("ID", "Device", c1) 
  levels(m1$Device) <- list( LeapMotion=c(c1),  Touchpad=c(c2), Mouse=c(c3) )
  if ( is.null(m) ) {
    m <- m1
  } else{
    m<-cbind(m, c=m1[,c(c1)] )  
    colnames(m)[colnames(m) == "c"] <- c1
  }
  
}



m<-melt(m, id=c("ID", "Device"))



ggplot(m, aes(Device, value, colour=Device)) + 
  geom_boxplot() +
  #coord_flip() +
  facet_wrap(  ~ variable,ncol=3)
ggsave(file = "charts/questionnaire/iso-boxplot.pdf", width=21/2.54, height=29/2.54, dpi=100)


describe <- describeBy(m$value, list(m$Device,m$variable), mat=TRUE)
colnames(describe)[colnames(describe)=="group1"] <- "Device"
colnames(describe)[colnames(describe)=="group2"] <- "variable"
#Reorder levels for chart
levels(describe$Device) <- list( Mouse=c("Mouse"), Touchpad=c("Touchpad"), LeapMotion=c("LeapMotion"))
    


#dfc <- summarySE(m, measurevar="value", groupvars=c("Device","variable"))

ggplot(describe, aes(Device, mean, colour=Device, fill=Device)) + 
  #stat_summary(fun.y="mean", geom="bar") + 
 geom_bar(stat="identity") +
  #coord_flip() +
  geom_errorbar(aes(ymin=mean-se, ymax=mean+se), colour="Black",
               width=.2,                    # Width of the error bars
               position=position_dodge(.9)) +
  facet_wrap(  ~ variable,ncol=3)
ggsave(file = "charts/questionnaire/iso-barplot.pdf", width=21/2.54, height=29/2.54, dpi=100)

