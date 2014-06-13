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
#source("2.2.charts-per-device+userid.R")
source("2.3.anovas.R")


filenameprefix <- "blocks4-8"
dataMeasures <- data[data$Block > 3,]
source("2.1.charts-per-device.R")
#source("2.2.charts-per-device+userid.R")
source("2.3.anovas.R")


my.helmert = matrix(c(7/8, -1/8, -1/8, -1/8, -1/8, -1/8, -1/8, -1/8,
                       0,  6/7, -1/7, -1/7, -1/7, -1/7, -1/7, -1/7,
                       0,    0,  5/6, -1/6, -1/6, -1/6, -1/6, -1/6,
                       0,    0,    0,  4/5, -1/5, -1/5, -1/5, -1/5,
                       0,    0,    0,    0,  3/4, -1/4, -1/4, -1/4, 
                       0,    0,    0,    0,    0,  2/3, -1/3, -1/3, 
                       0,    0,    0,    0,    0,    0,  1/2, -1/2 
                     ), ncol = 7)

my.rev.helmert = matrix(c(-1/2,  1/2,    0,    0,   0,    0,   0,  0,  
                     -1/3, -1/3,  3/3,    0,   0,    0,   0,  0,
                     -1/4, -1/4, -1/4,  3/4,   0,    0,   0,  0,
                     -1/5, -1/5, -1/5, -1/5, 4/5,    0,   0,  0,
                     -1/6, -1/6, -1/6, -1/6, -1/6, 5/6,   0,  0, 
                     -1/7, -1/7, -1/7, -1/7, -1/7, -1/7, 6/7, 0,
                     -1/8, -1/8, -1/8, -1/8, -1/8, -1/8, -1/8,7/8
                     
), ncol = 7)

contrasts(dataMeasures$Block) = my.helmert
summary(lm(MovementTime~Block, data=dataMeasures[dataMeasures$Device=="LeapMotionTouchless",]))
summary(lm(MovementTime~Block, data=dataMeasures[dataMeasures$Device=="Touchpad",]))
summary(lm(MovementTime~Block, data=dataMeasures[dataMeasures$Device=="Mouse",]))
summary(lm(MovementTime~Block, data=dataMeasures))
