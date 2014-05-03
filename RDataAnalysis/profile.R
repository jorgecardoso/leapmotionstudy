Rprof("profile1.out", line.profiling=TRUE, interval = 0.01)
eval(parse(file = "analysis.R", keep.source=TRUE))
Rprof(NULL)

summaryRprof("profile1.out", lines = "show")

#require(profr)
#require(ggplot2)
#x = profr(source("analysis.R"))
#ggplot(x)