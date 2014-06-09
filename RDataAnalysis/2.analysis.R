#install.packages(c("ggplot2", "doBy", "psych"))
require(ggplot2)
require(doBy)
require(psych)
#library("stringr")
#library(reshape)



############################ data
data <- read.csv(file="data/measures.txt", head=TRUE, sep="")

# convert the column to factor and name the levels
data$Device <- as.factor(data$Device)
levels(data$Device) <- list( LeapMotion=c(0),  Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4))



filenameprefix <- "all"
dataMeasures <- data
source("2.1.charts-per-device.R")
source("2.2.charts-per-device+userid.R")
source("2.3.anovas.R")


filenameprefix <- "blocks4-8"
dataMeasures <- data[data$Block > 3,]
source("2.1.charts-per-device.R")
source("2.2.charts-per-device+userid.R")
source("2.3.anovas.R")