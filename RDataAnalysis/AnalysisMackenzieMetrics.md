

Analisys of the Leap Motion experiment at UCP
========================================================


This document provides the analysis of the experiment conducted at UCP/CITAR comparing the movement of three devices - Mouse, Touchpad, and Leap Motion (via Touchless app) - for pointing tasks. 

We use Maczenzie's accuracy metrics to compare the devices.


```r
PRODUCE_BULK_CHARTS = FALSE
PRODUCE_BULK_HIST_CHARTS = TRUE
PRODUCE_BULK_CHARTS_DEVICE_PLUS_USERID = FALSE

#install.packages(c("ggplot2", "doBy", "psych", "car"))
require(ggplot2)
require(doBy)
require(psych)
require(car)
source("functions.R")

# load metrics data
data <- read.csv(file="data/measures-test.txt", head=TRUE, sep="")

# Users 0 and 5 did not complete the experiment
data <- data[data$UserId != 0 & data$UserId != 5,]

# convert the column to factor and name the levels
data$Device <- as.factor(data$Device)

# Set the levels 
# LeapMotion=c(0),  Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4)
# LeapMotion does not exist in the dataset from the UCP experiment
levels(data$Device) <- list( Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4))

# convert the block column to factor
data$Block <- factor(data$Block)

# In this analysis we use ErrorRateBinary, so we drop the ErrorRate column and change the column name ErrorRateBinary to ErrorRate
drops <- c("ErrorRate")
data<-data[,!(names(data) %in% drops)]
names(data)[names(data) == "ErrorRateBinary"] <- "ErrorRate"

# Compute the aggregate means for each variable.
# We agregate all values for the various circles
IVars <- c("Device", "Block", "Sequence", "UserId")
DVars <- c("Throughput", "ErrorRate", "MovementTime", "TRE", "TAC", "ODC", "MDC", "MV", "ME", "MO")
aggData <-aggregate(data[ ,DVars], data[ ,IVars], mean)
aggData$Block <- factor(aggData$Block)
```

Movement time as function of block
-------------------------------------

```r
describe.MT.aggData <- describeBy(aggData$MovementTime, aggData[,c("Block")])
describe.MT.aggData <-do.call("rbind", describe.MT.aggData)
describe.MT.aggData <- cbind(Block=rownames(describe.MT.aggData), describe.MT.aggData)
describe.MT.aggData$Block <- factor(describe.MT.aggData$Block)

p <- ggplot(describe.MT.aggData, aes(x=Block, y=mean )) + 
    geom_point(stat="identity") +
    geom_line(aes(group=vars)) + 
    ylab("Movement time (seconds) with 95% conf int") +
    #xlab("Sequence") +
    #ggtitle("Throughput") +
    geom_errorbar(aes(ymin=mean-1.96*se, ymax=mean+1.96*se), colour="Black",
                  width=.2                    # Width of the error bars
                  ) +
    theme(legend.position="none") +
    theme() 
p
```

![plot of chunk movement-time-block](figure/movement-time-block1.png) 

```r
p <- ggplot(aggData, aes_string(x="Block", y="MovementTime", group="Device", colour="Device" )) + 
            stat_summary(fun.y="mean", geom="line") + 
            stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
            theme(legend.direction = "horizontal", legend.position = "top") +
            #ylab("Percentage (%)") +
            #xlab("Sequence") +
            #ggtitle(var) +
            theme() #noop
p
```

![plot of chunk movement-time-block](figure/movement-time-block2.png) 

```r
ggsave(file = paste("charts/","all-movementtime-block-lineplot.pdf", sep=""), 
               width=20/2.54, height=16/2.54, dpi=100)
```

<!-- a comment here 

-->

Throughput  as function of block
-------------------------------------

```r
describe.T.aggData <- describeBy(aggData$Throughput, aggData[,c("Block")])
describe.T.aggData <-do.call("rbind", describe.T.aggData)
describe.T.aggData <- cbind(Block=rownames(describe.T.aggData), describe.T.aggData)
describe.T.aggData$Block <- factor(describe.T.aggData$Block)

p <- ggplot(describe.T.aggData, aes(x=Block, y=mean )) + 
    geom_point(stat="identity") +
    geom_line(aes(group=vars)) + 
    ylab("Throughput (bps) with 95% conf int") +
    #xlab("Sequence") +
    #ggtitle("Throughput") +
    geom_errorbar(aes(ymin=mean-1.96*se, ymax=mean+1.96*se), colour="Black",
                  width=.2                    # Width of the error bars
                  ) +
    theme(legend.position="none") +
    theme() 
p
```

![plot of chunk throughput-block](figure/throughput-block1.png) 

```r
p <- ggplot(aggData, aes_string(x="Block", y="Throughput", group="Device", colour="Device" )) + 
            stat_summary(fun.y="mean", geom="line") + 
            stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
            theme(legend.direction = "horizontal", legend.position = "top") +
            #ylab("Percentage (%)") +
            #xlab("Sequence") +
            #ggtitle(var) +
            theme() #noop
p
```

![plot of chunk throughput-block](figure/throughput-block2.png) 

```r
ggsave(file = paste("charts/","all-throughput-block-lineplot.pdf", sep=""), 
               width=20/2.54, height=16/2.54, dpi=100)
```

Error rate  as function of block
-------------------------------------

```r
p <- ggplot(aggData, aes_string(x="Block", y="ErrorRate", group="Device", colour="Device" )) + 
            stat_summary(fun.y="mean", geom="line") + 
            stat_summary(fun.y="mean", geom="point", aes(shape=Device)) + 
            theme(legend.direction = "horizontal", legend.position = "top") +
            #ylab("Percentage (%)") +
            #xlab("Sequence") +
            #ggtitle(var) +
            theme() #noop
p
```

![plot of chunk ErrorRate-block](figure/ErrorRate-block.png) 

```r
ggsave(file = paste("charts/","all-throughput-block-lineplot.pdf", sep=""), 
               width=20/2.54, height=16/2.54, dpi=100)
```

Learning effect
-------------------------
To estimate the learning effect, we ran pairwise t-tests for average throughput per block (considering all devices) with a significance level of 5%:


```r
#describeBy(data$MovementTime, data$Block)

# pairwise t-tests to determine learning effect blocks
p.t.test <- pairwise.t.test(aggData$Throughput, aggData$Block, paired=T, p.adjust.method="none")
diag(p.t.test$p.value) 
```

```
## [1] 0.0001677 0.0112456 0.0215020 0.4091830 0.2499649 0.0011833 0.7604690
```

```r
diag(p.t.test$p.value) < 0.05
```

```
## [1]  TRUE  TRUE  TRUE FALSE FALSE  TRUE FALSE
```

```r
# We will consider only blocks 4 to 8 in the rest of the analysis
filenameprefix <- "blocks4-8"
aggData.noLearn <- aggData[as.numeric(aggData$Block) > 3,]

# Drop unused block levels
aggData.noLearn$Block <- factor(aggData.noLearn$Block)
```

The results indicate a clear learning effect in blocks 1 to 3, but also indicate a significant different between blocks 6 and 7, suggesting that participants were still learning after block 6. However, in our following analysis we discard only blocks 1 to 3, since those represent the most significant learning effect.





Summary statistics 
------------


```r
options(width = 200)

# describe the main variables and store in file
s<-describeBy(aggData.noLearn[, DVars], aggData.noLearn$Device, mat=FALSE, digits=2)
s <- do.call("rbind", s)
s <- cbind(Device=rownames(s), s)

write.table(s, file = paste("tables/", filenameprefix,"-measures-device-describeby.csv", sep=""), sep=",", row.names=FALSE)
s
```

```
##                                                            Device vars   n     mean      sd   median  trimmed     mad       min     max   range    skew kurtosis       se
## Mouse.Throughput                                 Mouse.Throughput    1 250  4.84789 0.43153  4.80593  4.81875 0.33219   3.64434  6.3465  2.7022  0.7241  1.34634 0.027292
## Mouse.ErrorRate                                   Mouse.ErrorRate    2 250  0.01867 0.03835  0.00000  0.01100 0.00000   0.00000  0.2667  0.2667  2.5487  8.75598 0.002426
## Mouse.MovementTime                             Mouse.MovementTime    3 250  0.86772 0.09018  0.86140  0.86491 0.07067   0.64393  1.5440  0.9001  1.6794 11.73713 0.005703
## Mouse.TRE                                               Mouse.TRE    4 250  0.10347 0.08800  0.06667  0.09367 0.09884   0.00000  0.4667  0.4667  0.9524  0.74332 0.005565
## Mouse.TAC                                               Mouse.TAC    5 250  1.66453 0.37130  1.60000  1.65367 0.39536   0.80000  2.7333  1.9333  0.3287 -0.22974 0.023483
## Mouse.ODC                                               Mouse.ODC    6 250  1.19013 0.58598  1.06667  1.14133 0.59304   0.13333  3.0000  2.8667  0.7141 -0.12755 0.037061
## Mouse.MDC                                               Mouse.MDC    7 250  4.85680 0.95639  4.73333  4.80433 0.93898   2.80000  7.9333  5.1333  0.5094 -0.16697 0.060488
## Mouse.MV                                                 Mouse.MV    8 250 20.52235 6.88423 19.00302 19.97661 6.89731   9.32532 50.0686 40.7433  0.8466  0.87497 0.435397
## Mouse.ME                                                 Mouse.ME    9 250 18.40825 5.27475 18.06516 18.13646 5.25347   7.96311 38.1334 30.1703  0.6085  0.51852 0.333605
## Mouse.MO                                                 Mouse.MO   10 250 -1.67711 5.92677 -0.89555 -1.49922 4.96144 -19.17697 14.6480 33.8250 -0.3304  0.30583 0.374842
## Touchpad.Throughput                           Touchpad.Throughput    1 250  3.21480 0.42187  3.23847  3.20345 0.46182   2.23736  4.2786  2.0412  0.1599 -0.51370 0.026681
## Touchpad.ErrorRate                             Touchpad.ErrorRate    2 250  0.02133 0.04519  0.00000  0.01033 0.00000   0.00000  0.2667  0.2667  2.3632  5.86081 0.002858
## Touchpad.MovementTime                       Touchpad.MovementTime    3 250  1.31072 0.19977  1.27290  1.29773 0.21419   0.94020  2.0021  1.0619  0.6940  0.46090 0.012634
## Touchpad.TRE                                         Touchpad.TRE    4 250  0.14373 0.11746  0.13333  0.13033 0.09884   0.00000  0.6667  0.6667  1.1149  1.49464 0.007429
## Touchpad.TAC                                         Touchpad.TAC    5 250  1.25680 0.31648  1.23333  1.24600 0.34594   0.46667  2.2667  1.8000  0.3107 -0.03459 0.020016
## Touchpad.ODC                                         Touchpad.ODC    6 250  1.04293 0.55210  0.96667  1.00700 0.64246   0.06667  2.8000  2.7333  0.5422 -0.27226 0.034918
## Touchpad.MDC                                         Touchpad.MDC    7 250  4.47600 1.14940  4.33333  4.36167 0.98840   2.46667  8.2667  5.8000  0.9705  1.01525 0.072695
## Touchpad.MV                                           Touchpad.MV    8 250 20.04592 6.81060 20.21976 19.92855 6.65186   6.89674 41.9307 35.0339  0.1850 -0.26232 0.430740
## Touchpad.ME                                           Touchpad.ME    9 250 16.54774 5.24402 16.52406 16.34684 4.38401   6.24731 34.0486 27.8013  0.4458  0.48354 0.331661
## Touchpad.MO                                           Touchpad.MO   10 250 -1.58337 6.35509 -1.36884 -1.40416 6.13177 -23.41335 17.4696 40.8829 -0.2500  0.15760 0.401931
## LeapMotionTouchless.Throughput     LeapMotionTouchless.Throughput    1 250  2.16191 0.39497  2.19040  2.16564 0.48502   1.17980  3.2234  2.0436 -0.0448 -0.76611 0.024980
## LeapMotionTouchless.ErrorRate       LeapMotionTouchless.ErrorRate    2 250  0.06720 0.07651  0.06667  0.05500 0.09884   0.00000  0.4000  0.4000  1.2069  1.51238 0.004839
## LeapMotionTouchless.MovementTime LeapMotionTouchless.MovementTime    3 250  2.10709 0.51646  2.01170  2.04321 0.46786   1.26660  4.5288  3.2622  1.2835  2.25775 0.032664
## LeapMotionTouchless.TRE                   LeapMotionTouchless.TRE    4 250  0.35067 0.20844  0.33333  0.33700 0.19768   0.00000  1.2667  1.2667  0.7277  0.72858 0.013183
## LeapMotionTouchless.TAC                   LeapMotionTouchless.TAC    5 250  2.35280 0.62350  2.33333  2.32500 0.59304   1.00000  4.6000  3.6000  0.5649  0.71306 0.039433
## LeapMotionTouchless.ODC                   LeapMotionTouchless.ODC    6 250  4.49840 2.18494  4.20000  4.24967 1.87796   0.86667 12.6667 11.8000  1.1012  1.28425 0.138188
## LeapMotionTouchless.MDC                   LeapMotionTouchless.MDC    7 250  8.77707 2.43407  8.46667  8.62767 2.27332   4.00000 16.6667 12.6667  0.5679 -0.10459 0.153944
## LeapMotionTouchless.MV                     LeapMotionTouchless.MV    8 250 20.46544 6.23805 18.99970 19.72725 4.48278  10.53862 54.9318 44.3932  1.6572  4.53797 0.394529
## LeapMotionTouchless.ME                     LeapMotionTouchless.ME    9 250 16.26957 5.32712 15.19918 15.44300 3.55721   8.56724 42.5728 34.0055  2.1344  6.28763 0.336917
## LeapMotionTouchless.MO                     LeapMotionTouchless.MO   10 250 -1.49230 4.58888 -1.25696 -1.34598 3.91548 -33.56494 10.4611 44.0260 -1.4065  8.96521 0.290226
```

```r
remove(s)


# descriptive stats for device*userid
s1<-describeBy(aggData.noLearn[,DVars], 
               list(aggData.noLearn$Device,aggData.noLearn$UserId), mat=FALSE, digits=2)
s1 <- do.call("rbind", s1)
s1 <- cbind(Device=rownames(s1), s1)

write.table(s1, file = paste("tables/", filenameprefix,"-measures-device+userid-describeby.csv", sep=""), sep=",", row.names=FALSE)

remove(s1)
#s1
```

Histograms
-----------------
![plot of chunk charts-bulk-hist](figure/charts-bulk-hist1.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist2.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist3.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist4.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist5.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist6.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist7.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist8.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist9.png) ![plot of chunk charts-bulk-hist](figure/charts-bulk-hist10.png) 

Boxplots
----------------



Lineplots
-------------


MO Over TRE
-----------------------

```r
s <- summaryBy(ErrorRate+TRE+TAC+MDC+ODC+MV+ME+MO+MovementTime+Throughput~Device, 
               data=aggData.noLearn,
               FUN=c(mean,sd))
# plot the movement offset over the TRE for each device
p <- ggplot(s, aes(x=TRE.mean, y=abs(MO.mean), group=Device, colour=Device )) +
    geom_point(size=5, aes(shape=Device)) + 
    #coord_cartesian(xlim = c(0, 0.4), ylim=c(0, 8)) + 
    ylab("Movement offset (pixels)") +
    xlab("Target Re-Entry") +
    theme()
p
```

![plot of chunk chart-MO-over-TRE](figure/chart-MO-over-TRE.png) 

```r
ggsave(file = paste("charts/",filenameprefix,"-MO-TRE.pdf", sep=""), width=20/2.54, height=16/2.54, dpi=100)
```

Throughput and Error rate
-----------------

```r
# plot the throughput and ErrorRate

describe <- describeBy(aggData.noLearn$Throughput, list(aggData.noLearn$Device))
describe<-do.call("rbind",describe)
describe <- cbind(Device=rownames(describe), describe)
#levels(describe$Device) <- list( Mouse=c("Mouse"), Touchpad=c("Touchpad"), LeapMotionTouchless=c("LeapMotionTouchless"))
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
#ggsave(file = paste("charts/",filenameprefix,"-Throughput.pdf",sep=""), width=20/2.54, height=16/2.54, dpi=100)

describe <- describeBy(aggData.noLearn$ErrorRate, list(aggData.noLearn$Device))
describe<-do.call("rbind",describe)
describe <- cbind(Device=rownames(describe), describe)
#levels(describe$Device) <- list( Mouse=c("Mouse"), Touchpad=c("Touchpad"), LeapMotionTouchless=c("LeapMotionTouchless"))

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

pdf(file = paste("charts/",filenameprefix,"-Throughput+ErrorRate.pdf",sep=""), width=20/2.54, height=16/2.54) 
multiplot(p1, p2, cols=2)
dev.off()
```

```
## pdf 
##   2
```

```r
p1
p2
```

![plot of chunk chart-Throughput-and-ErrorRate](figure/chart-Throughput-and-ErrorRate1.png) ![plot of chunk chart-Throughput-and-ErrorRate](figure/chart-Throughput-and-ErrorRate2.png) 

Correlations
---------------

```r
round(cor(aggData.noLearn[, DVars]), 2)
```

```
##              Throughput ErrorRate MovementTime   TRE   TAC   ODC   MDC    MV    ME    MO
## Throughput         1.00     -0.34        -0.90 -0.57 -0.40 -0.65 -0.61 -0.10  0.09  0.05
## ErrorRate         -0.34      1.00         0.45  0.45  0.37  0.40  0.40  0.20  0.18 -0.07
## MovementTime      -0.90      0.45         1.00  0.66  0.58  0.82  0.77  0.18  0.03 -0.09
## TRE               -0.57      0.45         0.66  1.00  0.63  0.73  0.60  0.16  0.01 -0.02
## TAC               -0.40      0.37         0.58  0.63  1.00  0.72  0.77  0.01 -0.11  0.00
## ODC               -0.65      0.40         0.82  0.73  0.72  1.00  0.76  0.21  0.06 -0.04
## MDC               -0.61      0.40         0.77  0.60  0.77  0.76  1.00 -0.12 -0.21 -0.01
## MV                -0.10      0.20         0.18  0.16  0.01  0.21 -0.12  1.00  0.93 -0.28
## ME                 0.09      0.18         0.03  0.01 -0.11  0.06 -0.21  0.93  1.00 -0.28
## MO                 0.05     -0.07        -0.09 -0.02  0.00 -0.04 -0.01 -0.28 -0.28  1.00
```


Analysis of Variance - Differences between devices
---------------------
Analysis of variance of the throughput for the three devices showed significant differences 

```r
ggplot(aggData.noLearn, aes(x=1:nrow(aggData.noLearn), y=MO, group=Device, colour=Device)) + geom_point()
```

![plot of chunk anova](figure/anova1.png) 

```r
ggplot(aggData.noLearn, aes(x=Throughput, y=MO,  colour=Device)) + geom_point()
```

![plot of chunk anova](figure/anova2.png) 

```r
tmt<-lm(aggData.noLearn$Throughput ~ aggData.noLearn$MovementTime)
summary(tmt)
```

```
## 
## Call:
## lm(formula = aggData.noLearn$Throughput ~ aggData.noLearn$MovementTime)
## 
## Residuals:
##    Min     1Q Median     3Q    Max 
## -0.617 -0.417 -0.158  0.369  3.198 
## 
## Coefficients:
##                              Estimate Std. Error t value Pr(>|t|)    
## (Intercept)                    5.9085     0.0485     122   <2e-16 ***
## aggData.noLearn$MovementTime  -1.7503     0.0312     -56   <2e-16 ***
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
## 
## Residual standard error: 0.519 on 748 degrees of freedom
## Multiple R-squared:  0.808,	Adjusted R-squared:  0.807 
## F-statistic: 3.14e+03 on 1 and 748 DF,  p-value: <2e-16
```

```r
tmt.odc<-lm(Throughput ~ ODC, data=aggData.noLearn)
summary(tmt.odc)
```

```
## 
## Call:
## lm(formula = Throughput ~ ODC, data = aggData.noLearn)
## 
## Residuals:
##    Min     1Q Median     3Q    Max 
## -1.750 -0.743 -0.223  0.828  2.391 
## 
## Coefficients:
##             Estimate Std. Error t value Pr(>|t|)    
## (Intercept)   4.2336     0.0482    87.8   <2e-16 ***
## ODC          -0.3679     0.0157   -23.4   <2e-16 ***
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
## 
## Residual standard error: 0.899 on 748 degrees of freedom
## Multiple R-squared:  0.422,	Adjusted R-squared:  0.421 
## F-statistic:  546 on 1 and 748 DF,  p-value: <2e-16
```

```r
tmt.mdc<-lm(Throughput ~ MDC, data=aggData.noLearn)
summary(tmt.mdc)
```

```
## 
## Call:
## lm(formula = Throughput ~ MDC, data = aggData.noLearn)
## 
## Residuals:
##    Min     1Q Median     3Q    Max 
## -1.841 -0.737 -0.168  0.788  2.589 
## 
## Coefficients:
##             Estimate Std. Error t value Pr(>|t|)    
## (Intercept)   5.1143     0.0880    58.1   <2e-16 ***
## MDC          -0.2826     0.0134   -21.0   <2e-16 ***
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
## 
## Residual standard error: 0.937 on 748 degrees of freedom
## Multiple R-squared:  0.372,	Adjusted R-squared:  0.371 
## F-statistic:  443 on 1 and 748 DF,  p-value: <2e-16
```

```r
tmt.mdc.tre<-lm(Throughput ~ MDC+TRE, data=aggData.noLearn)
summary(tmt.mdc.tre)
```

```
## 
## Call:
## lm(formula = Throughput ~ MDC + TRE, data = aggData.noLearn)
## 
## Residuals:
##    Min     1Q Median     3Q    Max 
## -2.014 -0.705 -0.163  0.738  2.425 
## 
## Coefficients:
##             Estimate Std. Error t value Pr(>|t|)    
## (Intercept)   4.9894     0.0844   59.09   <2e-16 ***
## MDC          -0.1937     0.0159  -12.17   <2e-16 ***
## TRE          -2.0668     0.2221   -9.31   <2e-16 ***
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
## 
## Residual standard error: 0.888 on 747 degrees of freedom
## Multiple R-squared:  0.437,	Adjusted R-squared:  0.436 
## F-statistic:  290 on 2 and 747 DF,  p-value: <2e-16
```

```r
an<-anova(tmt, tmt.odc)
```

```
## Warning: models with response '"Throughput"' removed because response differs from model 1
```

```r
summary(an)
```

```
##        Df          Sum Sq       Mean Sq         F value         Pr(>F) 
##  Min.   :  1   Min.   :201   Min.   :  0.3   Min.   :3140   Min.   :0  
##  1st Qu.:188   1st Qu.:362   1st Qu.:211.3   1st Qu.:3140   1st Qu.:0  
##  Median :374   Median :523   Median :422.2   Median :3140   Median :0  
##  Mean   :374   Mean   :523   Mean   :422.2   Mean   :3140   Mean   :0  
##  3rd Qu.:561   3rd Qu.:684   3rd Qu.:633.2   3rd Qu.:3140   3rd Qu.:0  
##  Max.   :748   Max.   :844   Max.   :844.2   Max.   :3140   Max.   :0  
##                                              NA's   :1      NA's   :1
```

```r
plot(aggData.noLearn$Throughput, aggData.noLearn$MO)
```

![plot of chunk anova](figure/anova3.png) 

```r
leveneTest(Throughput ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)
## group   2    0.85   0.43
##       747
```

```r
leveneTest(ErrorRate ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)    
## group   2    50.4 <2e-16 ***
##       747                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(TAC ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)    
## group   2    48.8 <2e-16 ***
##       747                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(TRE ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)    
## group   2    80.8 <2e-16 ***
##       747                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(MDC ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)    
## group   2     112 <2e-16 ***
##       747                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(ODC ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)    
## group   2     159 <2e-16 ***
##       747                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(ME ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)
## group   2    1.65   0.19
##       747
```

```r
leveneTest(MV ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value Pr(>F)   
## group   2    4.85  0.008 **
##       747                  
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
leveneTest(MO ~ Device,data=aggData.noLearn, center="mean")
```

```
## Levene's Test for Homogeneity of Variance (center = "mean")
##        Df F value  Pr(>F)    
## group   2    17.6 3.3e-08 ***
##       747                    
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
bartlett.test(MO ~ Device,data=aggData.noLearn)
```

```
## 
## 	Bartlett test of homogeneity of variances
## 
## data:  MO by Device
## Bartlett's K-squared = 27.41, df = 2, p-value = 1.117e-06
```

```r
var(aggData.noLearn$MO)
```

```
## [1] 32.11
```

```r
var(aggData.noLearn$MO[aggData.noLearn$Device=="Mouse"])
```

```
## [1] 35.13
```

```r
var(aggData.noLearn$MO[aggData.noLearn$Device=="Touchpad"])
```

```
## [1] 40.39
```

```r
var(aggData.noLearn$MO[aggData.noLearn$Device=="LeapMotionTouchless"])
```

```
## [1] 21.06
```

```r
# Throughput: Homogeneity of variance
hov1 <- bartlett.test(Throughput ~ Device,data=aggData.noLearn)
hov1
```

```
## 
## 	Bartlett test of homogeneity of variances
## 
## data:  Throughput by Device
## Bartlett's K-squared = 2.074, df = 2, p-value = 0.3545
```

```r
# Anova
a <- aov(Throughput~Device, data=aggData.noLearn)
a
```

```
## Call:
##    aov(formula = Throughput ~ Device, data = aggData.noLearn)
## 
## Terms:
##                 Device Residuals
## Sum of Squares   915.8     129.5
## Deg. of Freedom      2       747
## 
## Residual standard error: 0.4164
## Estimated effects may be unbalanced
```

```r
summary(a)
```

```
##              Df Sum Sq Mean Sq F value Pr(>F)    
## Device        2    916     458    2641 <2e-16 ***
## Residuals   747    130       0                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
# pairwise comparisons, adjusting p-value
# all devices are significantly different from each other, in terms of throughput
TukeyHSD(a)
```

```
##   Tukey multiple comparisons of means
##     95% family-wise confidence level
## 
## Fit: aov(formula = Throughput ~ Device, data = aggData.noLearn)
## 
## $Device
##                                diff    lwr     upr p adj
## Touchpad-Mouse               -1.633 -1.721 -1.5456     0
## LeapMotionTouchless-Mouse    -2.686 -2.773 -2.5985     0
## LeapMotionTouchless-Touchpad -1.053 -1.140 -0.9654     0
```

```r
# TRE: Homogeneity of variance
hov1 <- bartlett.test(TRE ~ Device,data=aggData.noLearn)
hov1
```

```
## 
## 	Bartlett test of homogeneity of variances
## 
## data:  TRE by Device
## Bartlett's K-squared = 194.9, df = 2, p-value < 2.2e-16
```

```r
# Anova
a <- aov(Throughput~Device, data=aggData.noLearn)
a
```

```
## Call:
##    aov(formula = Throughput ~ Device, data = aggData.noLearn)
## 
## Terms:
##                 Device Residuals
## Sum of Squares   915.8     129.5
## Deg. of Freedom      2       747
## 
## Residual standard error: 0.4164
## Estimated effects may be unbalanced
```

```r
summary(a)
```

```
##              Df Sum Sq Mean Sq F value Pr(>F)    
## Device        2    916     458    2641 <2e-16 ***
## Residuals   747    130       0                   
## ---
## Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
```

```r
# pairwise comparisons, adjusting p-value
# all devices are significantly different from each other, in terms of throughput
TukeyHSD(a)
```

```
##   Tukey multiple comparisons of means
##     95% family-wise confidence level
## 
## Fit: aov(formula = Throughput ~ Device, data = aggData.noLearn)
## 
## $Device
##                                diff    lwr     upr p adj
## Touchpad-Mouse               -1.633 -1.721 -1.5456     0
## LeapMotionTouchless-Mouse    -2.686 -2.773 -2.5985     0
## LeapMotionTouchless-Touchpad -1.053 -1.140 -0.9654     0
```

```r
# ErrorRate: Homogeneity of variance
hov1 <- bartlett.test(ErrorRate ~ Device,data=aggData.noLearn)
hov1
```

```
## 
## 	Bartlett test of homogeneity of variances
## 
## data:  ErrorRate by Device
## Bartlett's K-squared = 136.4, df = 2, p-value < 2.2e-16
```

```r
# ER is non-normal....
# Anova





a <- aov(Throughput~Block,data=aggData.noLearn)
a
```

```
## Call:
##    aov(formula = Throughput ~ Block, data = aggData.noLearn)
## 
## Terms:
##                  Block Residuals
## Sum of Squares     2.9    1042.5
## Deg. of Freedom      4       745
## 
## Residual standard error: 1.183
## Estimated effects may be unbalanced
```

```r
summary(a)
```

```
##              Df Sum Sq Mean Sq F value Pr(>F)
## Block         4      3    0.72    0.51   0.72
## Residuals   745   1042    1.40
```

```r
throughput.p.t.test <- pairwise.t.test(aggData.noLearn$Throughput, aggData.noLearn$Device, paired=T)
throughput.p.t.test
```

```
## 
## 	Pairwise comparisons using paired t tests 
## 
## data:  aggData.noLearn$Throughput and aggData.noLearn$Device 
## 
##                     Mouse  Touchpad
## Touchpad            <2e-16 -       
## LeapMotionTouchless <2e-16 <2e-16  
## 
## P value adjustment method: holm
```
