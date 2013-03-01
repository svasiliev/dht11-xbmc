(ns temperature-chart.core
	(:gen-class)
	(:use [temperature-chart.utils]))

(defn -main
	[& args]
	(save-chart (generate-chart (parse-data (read-file "external/dht11.log"))) "external/dht11.jpg"))
