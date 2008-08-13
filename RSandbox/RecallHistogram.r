#cut values from CompareToManual.printComparisonsCSV() to make input file

file <- "recallValues.txt"
x <- t(read.csv(file))
x <- x[,]

max <- max(x)

h<-hist(x)

hist(x, breaks = 20, main="Recall per experiment", xlab="Recall", ylab="Experiment Frequency")

png("RecallHistogram.png", width=800, height = 800)
hist(x, breaks = 20, main="Recall per experiment", xlab="Recall", ylab="Experiment Frequency")
dev.off()

