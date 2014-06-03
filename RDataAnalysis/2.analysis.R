
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


source("2.1.analysis-per-device.R")

source("2.2.analysis-per-device+userid.R")