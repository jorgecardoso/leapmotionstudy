#install.packages(c("ggplot2", "doBy"))
require(ggplot2)
require(doBy)
#library("stringr")
#library(reshape)



############################ data
dataMeasures <- read.csv(file="data/measures.txt", head=TRUE, sep="")


# convert the column to factor and name the levels
dataMeasures$Device <- as.factor(dataMeasures$Device)
levels(dataMeasures$Device) <- list( LeapMotion=c(0),  Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4))


source("2.1.charts-per-device.R")

source("2.2.charts-per-device+userid.R")


source("2.3.anovas.R")